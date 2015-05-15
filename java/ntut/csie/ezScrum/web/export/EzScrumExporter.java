package ntut.csie.ezScrum.web.export;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.logic.ProjectLogic;

public class EzScrumExporter {
	private ArrayList<String> mProjectsName = new ArrayList<String>();

	public EzScrumExporter(ArrayList<String> projectsName) {
		mProjectsName = projectsName;
	}

	@SuppressWarnings("unchecked")
	public String getJsonData() {
		ArrayList<ProjectObject> projects = getFilteredProjects(mProjectsName);
		JSONObject resultJsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for (ProjectObject project : projects) {
			JSONObject jsonObject = JsonTranslater.translateProject(project);
			jsonArray.add(jsonObject);
		}
		resultJsonObject.put("projects", jsonArray);
		return resultJsonObject.toString();
	}

	private ArrayList<ProjectObject> getFilteredProjects(
			ArrayList<String> projectsName) {
		ArrayList<ProjectObject> projects = new ArrayList<ProjectObject>();
		// get all projects
		ProjectLogic projectLogic = new ProjectLogic();
		List<ProjectObject> allProjects = projectLogic.getAllProjectsForDb();
		for (ProjectObject project : allProjects) {
			if (projectsName.contains(project.getName())) {
				projects.add(project);
			}
		}
		return projects;
	}
}