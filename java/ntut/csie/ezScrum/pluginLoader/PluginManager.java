package ntut.csie.ezScrum.pluginLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.plugin.util.PluginConfigManager;
import ntut.csie.protocal.Action;
import ntut.csie.protocal.PluginConfig;
import ntut.csie.ui.protocol.EzScrumUI;
import ntut.csie.ui.protocol.PluginUI;
import ntut.csie.ui.protocol.UIConfig;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PluginManager {
	private List<PluginWrapper> pluginWrapperList;

	public PluginManager() {
		PluginWrapperHelper pluginWrapperHelper = new PluginWrapperHelper();
		this.pluginWrapperList = pluginWrapperHelper.generatePluginWrappers();
	}

	public List<PluginWrapper> getPluginWrapperList() {
		return this.pluginWrapperList;
	}

	/**
	 * add plugin
	 * */
	public boolean addPlugin(String outsidePluginPath) {
		File outsidePluginFile = new File(outsidePluginPath);

		String pluginName = FilenameUtils.removeExtension(outsidePluginFile.getName());
		String unzipDestinationPath = "./WebContent/pluginWorkspace/" + pluginName;
		File unzipDestinationDir = new File(unzipDestinationPath);

		AntZip antZip = new AntZip();
		antZip.unZip(outsidePluginFile.getAbsolutePath(), unzipDestinationDir.getAbsolutePath());
		
		// 新增全域的pluginConfig
		new PluginConfigManager().addConfigUI(pluginName);
		return true;
	}

	/**
	 * remove plugin
	 * */
	public boolean removePlugin(String insidePluginPath) {
		// remove configuration in pluginConfig.conf in every project which has available this plugin
		final String workspaceString = "./WebContent/Workspace/";
		File workspace = new File(workspaceString);
		String pluginUIId = this.getPluginUIIdByPluginDirPath(insidePluginPath);
		for (File file : workspace.listFiles()) {
			if (!file.isHidden() && !file.getName().equals("_metadata")) {
				PluginConfigManager pluginConfigManager = new PluginConfigManager(file.getName());
				pluginConfigManager.removeConfigUIByPluginUIId(pluginUIId);
				pluginConfigManager = null;
			} else  if (file.getName().equals("_metadata")) {
				// 移除全域的pluginConfig
				PluginConfigManager pluginConfigManager = new PluginConfigManager();
				pluginConfigManager.removeConfigUIByPluginUIId(pluginUIId);
			}
		}
		try {
			FileUtils.deleteDirectory(new File(insidePluginPath));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}
	
	public boolean setPluginEnable(String pluginName, String enable) {
		PluginConfigManager configManager = new PluginConfigManager();
		Gson gson = new Gson();
		List<PluginConfig> pluginConfigList = gson.fromJson(configManager.readFileContent(), new TypeToken<List<PluginConfig>>() {}.getType());
		for (PluginConfig pluginConfig : pluginConfigList) {
			if (pluginConfig.getId().contentEquals(pluginName)) {
				pluginConfig.setAvailable(Boolean.parseBoolean(enable));
				break;
			}
		}
		String content = gson.toJson(pluginConfigList);
		configManager.replaceFileContent(content);
		return true;
	}
	
	public boolean getPluginEnable(String pluginName) {
		PluginConfigManager configManager = new PluginConfigManager();
		Gson gson = new Gson();
		List<PluginConfig> pluginConfigList = gson.fromJson(configManager.readFileContent(), new TypeToken<List<PluginConfig>>() {}.getType());
		for (PluginConfig pluginConfig : pluginConfigList) {
			if (pluginConfig.getId().contentEquals(pluginName)) {
				return pluginConfig.isAvailable();
			}
		}
		return false;
	}

	public List<UIConfig> getUIConfigList() {
		List<UIConfig> configPluginList = new ArrayList<UIConfig>();
		for (PluginWrapper pluginWrapper : this.pluginWrapperList) {
			UIConfig uiConfig;
			try {
				uiConfig = (UIConfig) pluginWrapper.getPlugin("UIConfig");
				configPluginList.add(uiConfig);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return configPluginList;
	}

	public List<Action> getActionList() {
		List<Action> actionList = new ArrayList<Action>();
		for (PluginWrapper pluginWrapper : this.pluginWrapperList) {
			Action action;
			try {
				action = (Action) pluginWrapper.getPlugin("Action");
				actionList.add(action);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return actionList;
	}

	private String getPluginUIIdByPluginDirPath(String pluginDirPath) {
		// in order to get formal file path to compare, we use getAbsolutePath() api in File
		File pluginDir = new File(pluginDirPath);// according to pluginDir to find pluginId	
		for (PluginWrapper pluginWrapper : this.pluginWrapperList) {
			if (pluginWrapper.getPlugnDirPath().equals(pluginDir.getAbsolutePath())) {

				UIConfig uiConfig = null;
				try {
					uiConfig = (UIConfig) pluginWrapper.getPlugin("UIConfig");
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				List<EzScrumUI> ezScrumUIList = new ArrayList<EzScrumUI>();
				uiConfig.setEzScrumUIList(ezScrumUIList);

				for (EzScrumUI ezScrumUI : ezScrumUIList) {
					if (ezScrumUI instanceof PluginUI) {
						return ((PluginUI) ezScrumUI).getPluginID();
					}
				}
			}
		}
		return "";
	}

}
