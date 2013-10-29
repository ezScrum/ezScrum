package ntut.csie.ezScrum.stapler;

import java.io.IOException;

import ntut.csie.ezScrum.plugin.util.PluginConfigManager;
import ntut.csie.protocal.PluginConfig;
import ntut.csie.ui.protocol.EzScrumUI;
import ntut.csie.ui.protocol.TaskBoardUI;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class TaskBoard {
	private String projectName;
	public TaskBoard( String projectName ){
		this.projectName = projectName;
	}
	
	public String getBoardPlugin(){
    	//get plugin info from pluginConfig.conf in project folder
    	PluginConfigManager pluginConfigManager = new PluginConfigManager( this.projectName );

    	String pluginID = "";
		for(EzScrumUI ezScrumUI : EzScrumRoot.getLastEzScrumUIList() ){
			if( ezScrumUI instanceof TaskBoardUI ){
				PluginConfig pluginConfig = pluginConfigManager.getAvailablePluginConfigByPluginId(ezScrumUI.getPluginUI().getPluginID());
				if( pluginConfig != null ){
    				pluginID = ((TaskBoardUI) ezScrumUI).getBaordPlugin();

	    			// Task Board 應該只會有一個 plug-in, break for "for loop"
	    			break;
				}
			}
		}
		return pluginID;
    }
}
