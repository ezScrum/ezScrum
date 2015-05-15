package ntut.csie.ezScrum.web.export;

import org.json.simple.JSONObject;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;

public class JsonTranslater {
	@SuppressWarnings("unchecked")
	public static JSONObject translateProject(ProjectObject project) {
		JSONObject object = new JSONObject();
		object.put("name", project.getName());
		object.put("displayName", project.getDisplayName());
		object.put("comment", project.getComment());
		object.put("productOwner", project.getManager());
		object.put("attachMaxSize", project.getAttachFileSize());
		object.put("createTime", project.getCreateDate());
		return object;
	}
}
