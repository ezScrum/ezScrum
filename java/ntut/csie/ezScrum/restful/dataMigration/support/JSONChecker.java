package ntut.csie.ezScrum.restful.dataMigration.support;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ProjectJSONEnum;

public class JSONChecker {
	public static String checkProjectJSON(String projectJSONString) {
		JSONObject projectJSON = null;
		String checkedJSONString = null;
		try {
			projectJSON = new JSONObject(projectJSONString);

			if (projectJSON.isNull(ProjectJSONEnum.NAME)) {
				projectJSON.put(ProjectJSONEnum.NAME, "");
			} else if (projectJSON.isNull(ProjectJSONEnum.DISPLAY_NAME)) {
				projectJSON.put(ProjectJSONEnum.DISPLAY_NAME, "");
			} else if (projectJSON.isNull(ProjectJSONEnum.COMMENT)) {
				projectJSON.put(ProjectJSONEnum.COMMENT, "");
			} else if (projectJSON.isNull(ProjectJSONEnum.PRODUCT_OWNER)) {
				projectJSON.put(ProjectJSONEnum.PRODUCT_OWNER, "");
			} else if (projectJSON.isNull(ProjectJSONEnum.CREATE_TIME)) {
				projectJSON.put(ProjectJSONEnum.CREATE_TIME, System.currentTimeMillis());
			}
			checkedJSONString = projectJSON.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return checkedJSONString;
	}
}
