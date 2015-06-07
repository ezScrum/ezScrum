package ntut.csie.ezScrum.web.importer;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonParser {
	// project key set
	public static String PROJECT_CREATE_TIME = "createTime";
	public static String PROJECT_ATTACH_MAX_SIZE = "attachMaxSize";
	public static String PROJECT_PRODUCT_OWNER = "productOwner";
	public static String PROJECT_NAME = "name";
	public static String PROJECT_COMMENT = "comment";
	public static String PROJECT_DISPLAY_NAME = "displayName";
	
	public static ProjectObject parseProject(JSONObject projectJson) {
		ProjectObject project = new ProjectObject((String) projectJson.get(PROJECT_NAME));
		project.setCreateTime((long) projectJson.get(PROJECT_CREATE_TIME))
				.setAttachFileSize((long) projectJson.get(PROJECT_ATTACH_MAX_SIZE))
				.setManager((String) projectJson.get(PROJECT_PRODUCT_OWNER))
				.setComment((String) projectJson.get(PROJECT_COMMENT))
				.setDisplayName((String) projectJson.get(PROJECT_DISPLAY_NAME));
		return project;
	}
	
	public static ArrayList<ProjectObject> parseProjects(JSONArray projectJsonArray) {
		ArrayList<ProjectObject> projects = new ArrayList<>();
		for (int i = 0; i < projectJsonArray.size(); i++) {
			JSONObject projectJson = (JSONObject) projectJsonArray.get(i);
			ProjectObject project = parseProject(projectJson);
			projects.add(project);
		}
		return projects;
	}
}
