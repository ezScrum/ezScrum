package ntut.csie.ezScrum.web.export;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonTranslater {
	public static JSONObject translateProject(ProjectObject project) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", project.getName());
		jsonObject.put("displayName", project.getDisplayName());
		jsonObject.put("comment", project.getComment());
		jsonObject.put("productOwner", project.getManager());
		jsonObject.put("attachMaxSize", project.getAttachFileSize());
		jsonObject.put("createTime", project.getCreateDate());
		return jsonObject;
	}
}
