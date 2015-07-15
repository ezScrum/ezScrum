package ntut.csie.ezScrum.restful.mobile.support;

import java.util.ArrayList;

import ntut.csie.ezScrum.restful.mobile.util.SprintUtil;
import ntut.csie.ezScrum.web.dataObject.SprintObject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ConvertSprint {

	public static String getAllSprint(ArrayList<SprintObject> sprints, long currentSprintID) throws JSONException {
		JSONObject sprintList = new JSONObject();
		JSONArray sprintArray = new JSONArray();
		sprintList.put(SprintUtil.TAG_CURRENTSPRINTID, currentSprintID);
		sprintList.put(SprintUtil.TAG_SPRINTLIST, sprintArray);
		for(SprintObject sprint : sprints){
			JSONObject sprintPlan = new JSONObject();
			JSONObject sprintProperties = new JSONObject(sprint.toString());
			sprintPlan.put(SprintUtil.TAG_SPRINT, sprintProperties);
			sprintArray.put(sprintPlan);
		}
		System.out.println("ConvertSprint : getAllSprint : " + sprintList.toString());
		return sprintList.toString();
	}
	
	// 將 sprint list 轉成 json 字串
	public static String convertSprintListToJsonString(ArrayList<SprintObject> sprints) throws JSONException {
		JSONArray jsonSprints = new JSONArray();
		for (SprintObject sprint : sprints) {
			JSONObject jsonSprint = new JSONObject(sprint.toString());
			jsonSprints.put(jsonSprint);
		}
		return jsonSprints.toString();
	}
}
