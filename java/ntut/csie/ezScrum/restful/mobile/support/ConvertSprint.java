package ntut.csie.ezScrum.restful.mobile.support;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.iternal.SprintPlanDesc;
import ntut.csie.ezScrum.restful.mobile.util.SprintUtil;
import ntut.csie.ezScrum.web.dataObject.SprintObject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;

public class ConvertSprint {

	public static String getAllSprint(List<SprintObject> sprintObject, int currentSprintID) throws JSONException {
		Gson gson = new Gson();
		JSONObject sprintList = new JSONObject();
		JSONArray sprintArray = new JSONArray();
		sprintList.put(SprintUtil.TAG_CURRENTSPRINTID, currentSprintID);
		sprintList.put(SprintUtil.TAG_SPRINTLIST, sprintArray);
		for(SprintObject sprint : sprintObject){
			JSONObject sprintPlan = new JSONObject();
			JSONObject sprintProperties = new JSONObject(gson.toJson(sprint));
			sprintPlan.put(SprintUtil.TAG_SPRINT, sprintProperties);
			sprintArray.put(sprintPlan);
		}
		System.out.println("ConvertSprint : getAllSprint : " + sprintList.toString());
		return sprintList.toString();
	}
	
	// 將 sprint list 轉成 json 字串
	public static String convertSprintListToJsonString(List<SprintObject> sprintList) {
		Gson gson = new Gson();
		return gson.toJson(sprintList);
	}
	
	// 將 sprint object 轉成 sprint plan desc
	public static ISprintPlanDesc convertSprintObjectToDesc(SprintObject object) {
		ISprintPlanDesc sprint = new SprintPlanDesc();
		sprint.setID(object.id);
		sprint.setStartDate(object.startDate);
		sprint.setInterval(object.interval);
		sprint.setMemberNumber(object.members);
		sprint.setFocusFactor(object.focusFactor);
		sprint.setGoal(object.sprintGoal);
		sprint.setAvailableDays(object.hoursCanCommit);
		sprint.setDemoDate(object.demoDate);
		sprint.setDemoPlace(object.demoPlace);
		return sprint;
	}

	public static List<SprintObject> convertSprintDescToObject(List<ISprintPlanDesc> decsList) {
		List<SprintObject> objectList = new ArrayList<SprintObject>();
		for (ISprintPlanDesc decs : decsList)
			objectList.add(new SprintObject(decs));
		return objectList;
	}
}
