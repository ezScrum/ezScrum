package ntut.csie.ezScrum.web.export;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.logic.ProjectLogic;

public class EzScrumFile {
	private ArrayList<String> mProjectsName = new ArrayList<String>();

	public EzScrumFile(ArrayList<String> projectsName) {
		mProjectsName = projectsName;
	}

	public String getJsonData() throws JSONException {
		ArrayList<ProjectObject> projects = getFilteredProjects(mProjectsName);
		JSONObject resultJsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for (ProjectObject project : projects) {
			JSONObject jsonObject = JsonTranslater.translateProject(project);
			jsonArray.put(jsonObject);
		}
		resultJsonObject.put("projects", jsonArray);
		return resultJsonObject.toString();
	}
	
	private ArrayList<ProjectObject> getFilteredProjects(ArrayList<String> projectsName) {
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
