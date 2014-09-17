package ntut.csie.ezScrum.pluginLoader;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.pluginLoader.PluginWrapper;

public class PluginWrapperHelper {
	private final String pluginWorkspace = "./WebContent/pluginWorkspace/";

	public List<PluginWrapper> generatePluginWrappers() {
		List<PluginWrapper> mapList = new ArrayList<PluginWrapper>();
		File pluginWorkspaceDir = new File(pluginWorkspace);
		for (File file : pluginWorkspaceDir.listFiles()) {
			Map<String, String> map = new HashMap<String, String>();
			try {
				if (file.isDirectory() && !file.isHidden() && !file.getName().equals("tempCompressedPluginFileRepository")) {// avoid .svn dir and temp repository
					String configFilePath = file.getAbsolutePath() + "//config.conf";

					FileInputStream fileInpuStream = new FileInputStream(configFilePath);
					DataInputStream dataInputStream = new DataInputStream(fileInpuStream);
					BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
					String strLine;
					//Read File Line By Line
					while ((strLine = bufferReader.readLine()) != null) {
						addToMap(strLine, map);
					}
					//Close resource
					bufferReader.close();
					dataInputStream.close();
					fileInpuStream.close();
					PluginWrapper pluginWrapper = new PluginWrapper(file.getAbsolutePath(), map);
					mapList.add(pluginWrapper);
				}
			} catch (Exception e) {//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		}
		return mapList;
	}

	private void addToMap(String strline, Map<String, String> map) {
		String[] result = strline.split(":");
		map.put(result[0], result[1]);
	}
}