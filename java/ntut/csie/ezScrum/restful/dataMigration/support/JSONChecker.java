package ntut.csie.ezScrum.restful.dataMigration.support;

import java.util.Iterator;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.AccountJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ProjectJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ScrumRoleJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TagJSONEnum;

public class JSONChecker {
	public static String checkProjectJSON(String projectJSONString) {
		String message = "";
		try {
			JSONObject projectJSON = new JSONObject(projectJSONString);
			projectJSON.getString(ProjectJSONEnum.NAME);
			projectJSON.getString(ProjectJSONEnum.DISPLAY_NAME);
			projectJSON.getString(ProjectJSONEnum.COMMENT);
			projectJSON.getString(ProjectJSONEnum.PRODUCT_OWNER);
			projectJSON.getLong(ProjectJSONEnum.CREATE_TIME);
		} catch (JSONException e) {
			message = e.getMessage();
		}
		return message;
	}

	public static String checkScrumRolesJSON(String scrumRolesJSONString) {
		String message = "";
		try {
			JSONObject scrumRolesJSON = new JSONObject(scrumRolesJSONString);
			Iterator<?> iterator = scrumRolesJSON.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				JSONObject roleJSON = scrumRolesJSON.getJSONObject(key);
				roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG);
				roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN);
				roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE);
				roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG);
				roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN);
				roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD);
				roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLAN);
				roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT);
				roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT);
			}
		} catch (JSONException e) {
			message = e.getMessage();
		}
		return message;
	}

	public static String checkProjectRoleJSON(String projectRoleJSONString) {
		String message = "";
		try {
			JSONObject projectRoleJSON = new JSONObject(projectRoleJSONString);
			projectRoleJSON.getString(AccountJSONEnum.USERNAME);
			projectRoleJSON.getString(ScrumRoleJSONEnum.ROLE);
		} catch (JSONException e) {
			message = e.getMessage();
		}
		return message;
	}

	public static String checkTagJSON(String tagJSONString) {
		String message = "";
		try {
			JSONObject tagJSON = new JSONObject(tagJSONString);
			tagJSON.getString(TagJSONEnum.NAME);
		} catch (JSONException e) {
			message = e.getMessage();
		}
		return message;
	}
}
