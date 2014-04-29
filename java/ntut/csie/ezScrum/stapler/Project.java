package ntut.csie.ezScrum.stapler;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;

import ntut.csie.ezScrum.plugin.util.PluginConfigManager;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;
import ntut.csie.protocal.PluginConfig;
import ntut.csie.ui.protocol.EzScrumUI;
import ntut.csie.ui.protocol.ProjectUI;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class Project {
	private String mProjectName;

	public Project(String projectName) {
		this.mProjectName = projectName;
	}

	public void doSetProjectPluginConfig(StaplerRequest request, StaplerResponse response) {
		//get plugin config page string
		String pluginConfigJSONArrayString = request.getParameter("pluginConfigJSONArrayString");
		PluginConfigManager pluginConfigModifier = new PluginConfigManager(this.mProjectName);
		pluginConfigModifier.replaceFileContent(pluginConfigJSONArrayString);
		try {
			response.getWriter().write(this.mProjectName);
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void doGetProjectPluginConfig(StaplerRequest request, StaplerResponse response) {
		PluginConfigManager pluginConfigModifier = new PluginConfigManager(this.mProjectName);
		if (!pluginConfigModifier.readFileContent().equals("")) {
			JsonParser parser = new JsonParser();
			JsonArray pluginJsonArray = parser.parse(pluginConfigModifier.readFileContent()).getAsJsonArray();
			Gson gson = new Gson();
			StringBuilder sb = new StringBuilder();
			sb.append("{success: true, data: { pluginConfigArray: [");
			for (int i = 0; i < pluginJsonArray.size(); i++) {
				PluginConfig pluginConfig = gson.fromJson(pluginJsonArray.get(i), PluginConfig.class);
				if (i != 0) {
					sb.append(",");
				}
				sb.append(gson.toJson(pluginConfig));
			}
			sb.append("]} }");
			try {
				response.getWriter().write(sb.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Object getDynamic(String token, StaplerRequest request, StaplerResponse response) {
		HttpSession session = request.getSession();

		IProject project = (IProject) SessionManager.getProject(request);
		if (session == null || project == null) {
			return this;
		}

		String projectName = project.getProjectDesc().getName();

		if (token.equals("productBacklog")) { //when user enter to project
			return new ProductBacklog(projectName);
		} else if (token.equals("taskBoard")) {
			return new TaskBoard(projectName);
		}

		return this;
	}

	public String getProjectLeftTreePluginString() {
		// get plugin info from pluginConfig.conf in project folder
		PluginConfigManager pluginConfigManager = new PluginConfigManager(mProjectName);

		StringBuilder pluginStringList = new StringBuilder();
		for (EzScrumUI ezScrumUI : EzScrumRoot.getLastEzScrumUIList()) {
			if (ezScrumUI instanceof ProjectUI) {
				PluginConfig pluginConfig = pluginConfigManager.getAvailablePluginConfigByPluginId(ezScrumUI.getPluginUI().getPluginID());
				if (pluginConfig != null) {
					List<String> pluginIDList = ((ProjectUI) ezScrumUI).getProjectLeftTreeIDList();
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
	
	public String getProjectPagePluginString() {
		// get plugin info from pluginConfig.conf in project folder
		PluginConfigManager pluginConfigManager = new PluginConfigManager(mProjectName);

		StringBuilder pluginStringList = new StringBuilder();
		for (EzScrumUI ezScrumUI : EzScrumRoot.getLastEzScrumUIList()) {
			if (ezScrumUI instanceof ProjectUI) {
				PluginConfig pluginConfig = pluginConfigManager.getAvailablePluginConfigByPluginId(ezScrumUI.getPluginUI().getPluginID());
				if (pluginConfig != null) {
					List<String> pluginIDList = ((ProjectUI) ezScrumUI).getProjectPageIDList();
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
