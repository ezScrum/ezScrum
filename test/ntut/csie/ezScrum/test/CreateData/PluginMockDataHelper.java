package ntut.csie.ezScrum.test.CreateData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class PluginMockDataHelper {
	public static boolean isMockFileExisted(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = isMockFileExisted( new File(dir, children[i]) );
                if (!success) {
                    return false;
                }
            }
        }
        
        // The directory is now empty so delete it
        return dir.delete();
    }
	
	public static String createPluginConfigFile() {
		final String workspacePath = "./WebContent/Workspace";
		final String projectName = "/PluginForTest";
		final String metadataFolderName = "/_metadata";
		final String configFileName = "/pluginConfig.conf";
		final String pluginID = "plugin";
		
		String projectFolderPath = workspacePath + projectName + metadataFolderName;
		String configFileNamePath = projectFolderPath + configFileName;
		String fileContent = "";
		try {
			File folder = new File( projectFolderPath );
			folder.mkdirs();
			
			JSONArray pluginConfigJSONArr = new JSONArray();
			
			JSONObject pluginProperty1 = new JSONObject();
			pluginProperty1.put("id", pluginID+"1");
			pluginProperty1.put("available", "true");
			pluginConfigJSONArr.put( pluginProperty1 );
			
			JSONObject pluginProperty2 = new JSONObject();
			pluginProperty2.put("id", pluginID+"2");
			pluginProperty2.put("available", "false");
			pluginConfigJSONArr.put( pluginProperty2 );
			
			fileContent = pluginConfigJSONArr.toString();
			
			FileWriter configFile = new FileWriter( configFileNamePath );
			BufferedWriter out = new BufferedWriter( configFile );
			out.write( fileContent );
			out.close();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return fileContent;
	}
	
	public static void createResourcePropertyFile() {
		String resourcePropertyFilePath = "./resource.property";
		File resourcePropertyFile = new File(resourcePropertyFilePath);
		try {
			resourcePropertyFile.createNewFile();
			
			FileWriter configFile = new FileWriter( resourcePropertyFilePath );
			BufferedWriter out = new BufferedWriter( configFile );
			String resourceProperty = "ntut.csie.jcis.resource.relatedWorkspaceRoot=.\\\\WebContent\\\\Workspace";
			out.write( resourceProperty );
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteResourcePropertyFile() {
		String resourcePropertyFilePath = "./resource.property";
		File resourcePropertyFile = new File(resourcePropertyFilePath);
		if( resourcePropertyFile.exists() ){
			resourcePropertyFile.delete();
		}
	}
}
