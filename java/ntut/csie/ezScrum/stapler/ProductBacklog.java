package ntut.csie.ezScrum.stapler;

import java.util.List;

import ntut.csie.ezScrum.plugin.util.PluginConfigManager;
import ntut.csie.protocal.PluginConfig;
import ntut.csie.ui.protocol.EzScrumUI;
import ntut.csie.ui.protocol.ProductBacklogUI;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class ProductBacklog {
	private String projectName;

	public ProductBacklog(String projectName) {
		this.projectName = projectName;
	}

	// it is a stapler sample ->rest url http://localhost:8080/ezScrum/project/productBacklog/someOperation
	public void doSomeOperation(StaplerRequest request, StaplerResponse response) {

	}

	// get data by jsp
	public String getToolbarPluginStringList() {
		// get plugin info from pluginConfig.conf in project folder
		
		StringBuilder sb = new StringBuilder();
		for (EzScrumUI ezScrumUI : EzScrumRoot.getLastEzScrumUIList()) {
			if (ezScrumUI instanceof ProductBacklogUI) {
				PluginConfig projectPluginConfig = new PluginConfigManager(this.projectName).getAvailablePluginConfigByPluginId(ezScrumUI.getPluginUI().getPluginID());
				PluginConfig ezScrumPluginConfig = new PluginConfigManager().getAvailablePluginConfigByPluginId(ezScrumUI.getPluginUI().getPluginID());
				if (projectPluginConfig != null && ezScrumPluginConfig != null) {
					/** todo */
					List<String> pluginIDList = ((ProductBacklogUI) ezScrumUI).getToolbarPluginIDList();
					for (String pluginID : pluginIDList) {
						if (sb.toString().isEmpty()) {
							sb.append("'" + pluginID + "'");
						} else {
							sb.append(",'" + pluginID + "'");
						}
					}
				}
			}
		}
		return sb.toString();

	}
}
