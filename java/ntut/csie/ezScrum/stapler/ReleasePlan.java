package ntut.csie.ezScrum.stapler;

import java.util.List;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import ntut.csie.ezScrum.plugin.util.PluginConfigManager;
import ntut.csie.protocal.PluginConfig;
import ntut.csie.ui.protocol.EzScrumUI;
import ntut.csie.ui.protocol.ReleasePlanUI;

public class ReleasePlan {
	public ReleasePlan() {
	}

	// it is a stapler sample ->rest url http://localhost:8080/ezScrum/ReleasePlan/someOperation
	public void doSomeOperation(StaplerRequest request, StaplerResponse response) {}

	// get data by jsp
	public String getToolbarPluginStringList() {
		// get plugin info from pluginConfig.conf in project folder
		PluginConfigManager pluginConfigManager = new PluginConfigManager();

		StringBuilder pluginStringList = new StringBuilder();
		for (EzScrumUI ezScrumUI : EzScrumRoot.getLastEzScrumUIList()) {
			if (ezScrumUI instanceof ReleasePlanUI) {
				PluginConfig pluginConfig = pluginConfigManager.getAvailablePluginConfigByPluginId(ezScrumUI.getPluginUI().getPluginID());
				if (pluginConfig != null) {
					List<String> pluginIDList = ((ReleasePlanUI) ezScrumUI).getToolbarPluginIDList();
					for (String pluginID : pluginIDList) {
						if (pluginStringList.toString().isEmpty()) {
							pluginStringList.append("'").append(pluginID).append("'");
						} else {
							pluginStringList.append(",'").append(pluginID).append("'");
						}
					}
				}
			}
		}
		return pluginStringList.toString();
	}
}
