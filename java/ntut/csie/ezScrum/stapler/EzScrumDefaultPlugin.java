package ntut.csie.ezScrum.stapler;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import ntut.csie.ezScrum.plugin.util.PluginModifier;
import org.apache.commons.io.FilenameUtils;

public class EzScrumDefaultPlugin {
	private String pluginWorkspacePath = "./WebContent/pluginWorkspace/";
	private String filePath = "./WebContent/Pages/ImportPluginList.jsp";
	private Map<String, Boolean> pluginPathMap;
	
	public EzScrumDefaultPlugin(){
		handleDefaultPlugin();
		modifyFileContent();
	}
	
	/**
	 * 處理default plugin是否要加入ezScrum
	 */
	private void handleDefaultPlugin(){
		File pluginWorkspaceFile = new File( pluginWorkspacePath );
		this.pluginPathMap = readFileContent();
		for( String pluginPath:pluginWorkspaceFile.list() ){
			if( !pluginPath.equals(".svn") ){
				if( !isPluginExisted(pluginPath) ){
					addDefaultPluginToEzScrum(pluginPath);
				}
			}
		}
	}
	
	/**
	 * 修改ImportPluginList.jsp
	 */
	private void modifyFileContent(){
        for( Map.Entry<String, Boolean> entry : pluginPathMap.entrySet() ) {
        	if( !entry.getValue() ){
        		removeDefaultPluginFromEzScrum( entry.getKey() );
        	}
        } 
	}
	
	/**
	 * 新增default plugin 的import.jsp in ImportPluginList.jsp
	 * @param pluginName
	 */
	private void addDefaultPluginToEzScrum( String pluginName ){
		String pluginPath = "<jsp:include page=\"../pluginWorkspace/"+pluginName+"/import.jsp\"/>";
		PluginModifier pluginModifier = new PluginModifier();
		try {
			pluginPathMap.put(pluginPath, true);
			pluginModifier.addPluginImportPath(FilenameUtils.removeExtension(pluginName)); // file name without extension
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 判斷 default plugin 是否有被移除
	 * @param pluginName
	 * @return
	 */
	private boolean isPluginExisted( String pluginName ){
		boolean isPluginExisted = false;
        for( Map.Entry<String, Boolean> entry : pluginPathMap.entrySet() ) {
        	String pluginPath = entry.getKey();
        	if( pluginPath.contains(pluginName) ){
        		isPluginExisted = true;
        		entry.setValue(true);
        		break;
        	}else{
        		isPluginExisted = false;
        	}
        } 
		return isPluginExisted;
	}
	
	/**
	 * 移除default plugin 的import.lst in ImportPluginList.jsp
	 * @param pluginPath
	 */
	private void removeDefaultPluginFromEzScrum( String pluginPath ){
		String pluginName = pluginPath.split("/")[2];
		PluginModifier pluginModifier = new PluginModifier();
		try {
			pluginModifier.removePluginImportPath(pluginName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 讀出檔案有關於default plugin 的import.jsp in ImportPluginList.jsp
	 * @return
	 */
	private Map<String, Boolean> readFileContent(){//讀出檔案內容
		Map<String, Boolean> pluginPathMap = new HashMap<String, Boolean>();
		
		try{
			//將舊的內容讀出
			FileInputStream fileInpuStream = new FileInputStream( filePath );
			DataInputStream dataInputStream = new DataInputStream(fileInpuStream);
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
			String strLine;
			//Read File Line By Line
			while ((strLine = bufferReader.readLine()) != null) {
				if( strLine.contains("<jsp:include page=\"../pluginWorkspace/") ){
					pluginPathMap.put(strLine, false);
				}
			}
			//	Close resource
			bufferReader.close();
			dataInputStream.close();
			fileInpuStream.close();
			
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return pluginPathMap;
	}
}
