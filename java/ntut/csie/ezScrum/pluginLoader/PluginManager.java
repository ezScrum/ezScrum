package ntut.csie.ezScrum.pluginLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ui.protocol.EzScrumUI;
import ntut.csie.ui.protocol.PluginUI;
import ntut.csie.ui.protocol.UIConfig;
import ntut.csie.ezScrum.plugin.util.PluginConfigManager;
import ntut.csie.protocal.Action;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class PluginManager {
	private List<PluginWrapper> pluginWrapperList;
	
	public PluginManager(){
		PluginWrapperHelper pluginWrapperHelper = new PluginWrapperHelper();
		this.pluginWrapperList = pluginWrapperHelper.generatePluginWrappers();
	}
	
	public List<PluginWrapper> getPluginWrapperList(){
		return this.pluginWrapperList;
	}
	
	/**todo add plugin*/
	public boolean addPlugin( String outsidePluginPath ){
		File outsidePluginFile = new File( outsidePluginPath );
		
		String pluginName = FilenameUtils.removeExtension(outsidePluginFile.getName());
		String unzipDestinationPath = "./WebContent/pluginWorkspace" + "/" +pluginName;
		File unzipDestinationDir = new File( unzipDestinationPath );
		
		AntZip antZip = new AntZip();
		antZip.unZip(outsidePluginFile.getAbsolutePath(), unzipDestinationDir.getAbsolutePath());
		
		return true;
	}
	
	/**todo remove plugin*/
	public boolean removePlugin(  String insidePluginPath ){    	
    	//remove configuration in pluginConfig.conf in every project which has available this plugin
    	final String workspaceString = "./WebContent/Workspace/";
    	File workspace = new File( workspaceString );
    	String pluginUIId = this.getPluginUIIdByPluginDirPath( insidePluginPath );
    	for( File file : workspace.listFiles() ){
    		if( !file.isHidden() && !file.getName().equals("_metadata")){
    			PluginConfigManager pluginConfigManager = new PluginConfigManager( file.getName() );
    			pluginConfigManager.removeConfigUIByPluginUIId( pluginUIId );
    			pluginConfigManager = null;
    		}
    	}
    	try {
        	FileUtils.deleteDirectory(new File( insidePluginPath ));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return true;
	}
	
	public List<UIConfig> getUIConfigList() {
		List<UIConfig> configPluginList = new ArrayList<UIConfig>();
		for( PluginWrapper pluginWrapper : this.pluginWrapperList ){
			UIConfig uiConfig;
			try {
				uiConfig = (UIConfig)pluginWrapper.getPlugin("UIConfig");
				configPluginList.add( uiConfig );
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return configPluginList;		
	}
	
	public List<Action> getActionList() {
		List<Action> actionList = new ArrayList<Action>();
		for( PluginWrapper pluginWrapper : this.pluginWrapperList ){
			Action action;
			try {
				action = (Action)pluginWrapper.getPlugin("Action");
				actionList.add( action );
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return actionList;		
	}
	
	private String getPluginUIIdByPluginDirPath( String pluginDirPath ){
		// in order to get formal file path to compare, we use getAbsolutePath() api in File
		File pluginDir = new File( pluginDirPath );// according to pluginDir to find pluginId	
    	for(PluginWrapper pluginWrapper : this.pluginWrapperList){
    		if( pluginWrapper.getPlugnDirPath().equals( pluginDir.getAbsolutePath() ) ){
    			
    			UIConfig uiConfig = null;
    			try {
    				uiConfig = (UIConfig)pluginWrapper.getPlugin("UIConfig");
    			} catch (InstantiationException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IllegalAccessException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			List<EzScrumUI> ezScrumUIList = new ArrayList<EzScrumUI>();
    			uiConfig.setEzScrumUIList( ezScrumUIList );
    			
    			for( EzScrumUI ezScrumUI : ezScrumUIList ){
    				if( ezScrumUI instanceof PluginUI ){
    					return ((PluginUI) ezScrumUI).getPluginID();
    				}
    			}
    		}
    	}
		return "";
	}
	
}
