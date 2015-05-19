package ntut.csie.ezScrum.restful.mobile.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import ntut.csie.ezScrum.restful.mobile.util.CommonUtil;
import ntut.csie.ezScrum.restful.mobile.util.ProjectUtil;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ConvertProject {
	private JSONObject mProjectList;
	private JSONArray mProjectArray;

	public ConvertProject() throws JSONException {
		mProjectList = new JSONObject();
		mProjectArray = new JSONArray();
		mProjectList.put(ProjectUtil.TAG_PROJECTS, mProjectArray);
	}

	public void addProject(ProjectObject project) throws JSONException {
		JSONObject object = new JSONObject();
		JSONObject projectJson = new JSONObject();
		// createDate
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String createTime = dateFormat.format(project.getCreateTime());

		// TODO: 此 ID 為 PID，因前端還未修改所以先不動 (待改!!!)
		projectJson.put(CommonUtil.TAG_ID, project.getName());
		projectJson.put(CommonUtil.TAG_NAME, project.getDisplayName());
		projectJson.put(ProjectUtil.TAG_COMMENT, project.getComment());
		projectJson.put(ProjectUtil.TAG_MANAGER, project.getManager());
		projectJson.put(ProjectUtil.TAG_CREATEDATE, createTime);

		// demoDate
		SprintPlanHelper SPhelper = new SprintPlanHelper(project);
		String demoDate = SPhelper.getNextDemoDate();
		if(demoDate == null) {
			projectJson.put(ProjectUtil.TAG_DEMODATE, "No Plan!");
		} else {
			projectJson.put(ProjectUtil.TAG_DEMODATE, demoDate);
		}
		object.put(ProjectUtil.TAG_PROJECT, projectJson);
		mProjectArray.put(object);
	}

	public String getJSONString() {
		return mProjectList.toString();
	}
}
