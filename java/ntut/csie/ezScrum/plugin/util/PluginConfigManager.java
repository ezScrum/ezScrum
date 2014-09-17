package ntut.csie.ezScrum.plugin.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import ntut.csie.jcis.resource.core.ResourceFacade;
import ntut.csie.protocal.PluginConfig;

public class PluginConfigManager {

	private final String workspacePath = ResourceFacade.getWorkspace().getRoot().getFullPath().getPathString();
	private String filePath;
	private final String configFileName = "/pluginConfig.conf";

	public PluginConfigManager() {
		this.filePath = this.workspacePath + "/_metadata/" + this.configFileName;
	}
	
	public PluginConfigManager(String projectName) {
		this.filePath = this.workspacePath + "//" + projectName + "/_metadata/" + this.configFileName;
	}

	public PluginConfig getAvailablePluginConfigByPluginId(String pluginId) {
		for (PluginConfig pluginConfig : this.getPluginConfigList()) {
			if (pluginConfig.getId().equals(pluginId) && pluginConfig.isAvailable()) {
				return pluginConfig;
			}
		}
		return null;
	}

	public void removeConfigUIByPluginUIId(String pluginUIId) {
		List<PluginConfig> pluginConfigList = this.getPluginConfigList();
		for (PluginConfig pluginConfig : pluginConfigList) {
			if (pluginConfig.getId().equals(pluginUIId)) {
				pluginConfigList.remove(pluginConfig);
				break;
			}
		}
		Gson gson = new Gson();
		String jsonString = gson.toJson(pluginConfigList);
		this.replaceFileContent(jsonString);
	}

	private List<PluginConfig> getPluginConfigList() {
		List<PluginConfig> pluginConfigList = new ArrayList<PluginConfig>();
		if (!this.readFileContent().equals("")) {
			JsonParser parser = new JsonParser();
			JsonArray pluginJsonArray = parser.parse(this.readFileContent()).getAsJsonArray();
			Gson gson = new Gson();
			for (int i = 0; i < pluginJsonArray.size(); i++) {
				PluginConfig pluginConfig = gson.fromJson(pluginJsonArray.get(i), PluginConfig.class);
				pluginConfigList.add(pluginConfig);
			}
		}
		return pluginConfigList;
	}

	public void replaceFileContent(String content) {
		File pluginConfigFile = new File(this.filePath);
		if (!pluginConfigFile.exists()) {
			try {
				pluginConfigFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.cleanFileContent();
		this.writeFileContent(content);
	}

	private void cleanFileContent() {
		try {
			BufferedWriter erasor = new BufferedWriter(new FileWriter(filePath));
			erasor.write("");
			erasor.flush();
			erasor.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeFileContent(String content) {// 寫入檔案內容
		try {
			// Create file
			FileWriter fireWriter = new FileWriter(filePath);
			BufferedWriter bufferWriter = new BufferedWriter(fireWriter);
			bufferWriter.write(content);
			// Close resource
			bufferWriter.close();
			bufferWriter.close();
			fireWriter.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public String readFileContent() {// 讀出檔案內容
		String result = "";
		try {
			// 將舊的內容讀出
			File file = new File(filePath);
			if (!file.exists()) {// file is not existed, means first read file
				return "";
			}
			FileInputStream fileInpuStream = new FileInputStream(filePath);
			DataInputStream dataInputStream = new DataInputStream(fileInpuStream);
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
			String strLine;
			String newLine = System.getProperty("line.separator");
			// Read File Line By Line
			while ((strLine = bufferReader.readLine()) != null) {
				// Print the content on the console
				if (!result.equals("")) {
					result += newLine + strLine;
				} else {
					result += strLine;
				}
			}
			// Close resource
			bufferReader.close();
			dataInputStream.close();
			fileInpuStream.close();

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return result;
	}

	public void addConfigUI(String pluginName) {
		List<PluginConfig> configList = getPluginConfigList();
		PluginConfig config = new PluginConfig();
		config.setId(pluginName);
		config.setAvailable(true);
		configList.add(config);
		Gson gson = new Gson();
		replaceFileContent(gson.toJson(configList));
    }
}
