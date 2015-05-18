package ntut.csie.ezScrum.stapler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.plugin.util.PluginModifier;
import ntut.csie.ezScrum.pluginLoader.PluginManager;
import ntut.csie.ezScrum.pluginLoader.PluginWrapper;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ui.protocol.EzScrumUI;
import ntut.csie.ui.protocol.PluginUI;
import ntut.csie.ui.protocol.UIConfig;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class EzScrumRoot {
	public static EzScrumRoot ezScrumRoot = new EzScrumRoot();
	final String tempUploadFilePath = "./WebContent/pluginWorkspace/tempCompressedPluginFileRepository/";

	public EzScrumRoot() {
		EzScrumDefaultPlugin ezScrumDefaultPlugin = new EzScrumDefaultPlugin();
	}

	// get installed pluginList
	public void doGetInstalledPluginList(StaplerRequest request, StaplerResponse response) throws IOException {
		// write projects to XML formatoikk
		StringBuilder sb = new StringBuilder();

		PluginManager pluginManager = new PluginManager();
		List<PluginWrapper> pluginWrapperList = pluginManager.getPluginWrapperList();
		sb.append("<Plugins>");
		for (PluginWrapper pluginWrapper : pluginWrapperList) {
			String pluginName = pluginWrapper.getPluginName();
			sb.append("<Plugin>");
			sb.append("<Name>" + pluginName + "</Name>");
			sb.append("<Enable>" + pluginManager.getPluginEnable(pluginName) + "</Enable>");
			sb.append("</Plugin>");
		}
		sb.append("</Plugins>");
		try {
			response.getWriter().write(sb.toString());
			response.getWriter().close();
		} catch (IOException e) {
			throw e;
		}
	}

	public void doAddPlugin(StaplerRequest request, StaplerResponse response) throws Exception {
		File uploadedPluginFile = this.uploadPluginFile(request);	// upload plugin
		PluginModifier pluginModifier = new PluginModifier();		// 管理plugin將web資源註冊到系統
		PluginManager pluginManager = new PluginManager();			// 管理安裝 反安裝插件

		try {
			// uncompress plugin
			boolean isPluginAdded = pluginManager.addPlugin(uploadedPluginFile.getAbsolutePath());

			// add import.jsp which in plugin to host
			if (isPluginAdded) {
				// delete compressed plugin file
				uploadedPluginFile.delete();

				new File(tempUploadFilePath).delete();
				pluginModifier.addPluginImportPath(FilenameUtils.removeExtension(uploadedPluginFile.getName()));//file name without extension
			}

		} catch (Exception e) {
			throw e;
		}

		try {
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().write("{\"success\":true}");
			response.getWriter().close();
		} catch (IOException e) {
			throw e;
		}
	}

	public void doRemovePlugin(StaplerRequest request, StaplerResponse response) throws Exception {
		String pluginName = request.getParameter("pluginName");// it is unique

		PluginModifier pluginModifier = new PluginModifier();
		try {
			// remove import.jsp which in plugin to host
			pluginModifier.removePluginImportPath(FilenameUtils.removeExtension(pluginName));
		} catch (Exception e) {
			throw e;
		}
		final String pluginPath = "./WebContent/pluginWorkspace/" + pluginName;

		PluginManager pluginManager = new PluginManager();
		// uninstall plugin 
		pluginManager.removePlugin(pluginPath);
	}

	public void doSetPluginEnable(StaplerRequest request, StaplerResponse response) {
		String pluginName = request.getParameter("pluginName");
		String pluginEnable = request.getParameter("pluginEnable");
		PluginManager pluginManager = new PluginManager();
		pluginManager.setPluginEnable(pluginName, pluginEnable);
	}

	public void doGetConfigPluginList(StaplerRequest request, StaplerResponse response) throws Exception {
		try {
			StringBuilder sb = new StringBuilder();
			for (EzScrumUI ezScrumUI : EzScrumRoot.getLastEzScrumUIList()) {
				if (ezScrumUI instanceof PluginUI) {
					String pluginID = ((PluginUI) ezScrumUI).getPluginID();
					if (sb.toString().isEmpty()) {
						sb.append("{\"name\":\"" + pluginID + "\"}");
					} else {
						sb.append(",{\"name\":\"" + pluginID + "\"}");
					}
				}
			}
			response.getWriter().write("{success: true, data: { plugin: [" + sb.toString() + "]} }");
			response.getWriter().close();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	public Object getDynamic(String token, StaplerRequest request, StaplerResponse response) {
		ProjectObject project = SessionManager.getProjectObject(request);

		if (token.equals("project")) {
			return project;
		}

		if (token.equals("plugin")) {//enter to plugin
			return new Plugin();
		}

		return this;
	}

	public static List<EzScrumUI> getLastEzScrumUIList() {
		List<EzScrumUI> ezScrumUIList = new ArrayList<EzScrumUI>();
		PluginManager pluginManager = new PluginManager();
		List<UIConfig> uiConfigList = pluginManager.getUIConfigList();
		for (UIConfig uiConfig : uiConfigList) {
			uiConfig.setEzScrumUIList(ezScrumUIList);
		}
		return ezScrumUIList;
	}

	private File uploadPluginFile(StaplerRequest request) throws Exception {
		File tempCompressedPluginFileDir = new File(tempUploadFilePath);
		tempCompressedPluginFileDir.mkdir();//create temp dir to contain temp file
		File uploadPluginFile = null;
		try {
			DiskFileUpload fu = new DiskFileUpload();
			//most document size 40MB
			fu.setSizeMax(41943040);
			//buffer size 4KB
			fu.setSizeThreshold(4096);
			fu.setRepositoryPath(tempUploadFilePath);
			List fileItems = fu.parseRequest(request);

			FileItem fileItem = (FileItem) fileItems.get(0);
			String fileName = fileItem.getName();
			if (!fileName.equals("")) {
				uploadPluginFile = new File(tempUploadFilePath + fileName);
				fileItem.write(uploadPluginFile);
			}
		} catch (Exception e) {
			throw e;
		}
		return uploadPluginFile;
	}
}