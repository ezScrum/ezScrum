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
			JSONObject sprintProperties = new JSONObject();
			sprintProperties.put(SprintUtil.TAG_ID, sprint.getId())
			        .put(SprintUtil.TAG_PROJECT_ID, sprint.getProjectId())
			        .put(SprintUtil.TAG_STARTDATE, sprint.getStartDate())
			        .put(SprintUtil.TAG_INTERVAL, sprint.getInterval())
			        .put(SprintUtil.TAG_MEMBERS, sprint.getMembersNumber())
			        .put(SprintUtil.TAG_SERIAL_ID, sprint.getSerialId())
			        .put(SprintUtil.TAG_SPRINTGOAL, sprint.getSprintGoal())
			        .put(SprintUtil.TAG_HOURSCANCOMMIT, sprint.getHoursCanCommit())
			        .put(SprintUtil.TAG_FOCUSFACTOR, sprint.getFocusFactor())
			        .put(SprintUtil.TAG_DEMODATE, sprint.getDemoDate())
			        .put(SprintUtil.TAG_DEMOPLACE, sprint.getDemoPlace())
			        .put(SprintUtil.TAG_DAILYMEETING, sprint.getDailyInfo());
			sprintPlan.put(SprintUtil.TAG_SPRINT, sprintProperties);
			sprintArray.put(sprintPlan);
		}
		System.out.println("ConvertSprint : getAllSprint : " + sprintList.toString());
		return sprintList.toString();
	}
	
	public static String convertSprintToJsonString(SprintObject sprint) throws JSONException {
		JSONObject sprintJson = new JSONObject();
		sprintJson.put(SprintUtil.TAG_ID, sprint.getId())
		        .put(SprintUtil.TAG_PROJECT_ID, sprint.getProjectId())
		        .put(SprintUtil.TAG_STARTDATE, sprint.getStartDate())
		        .put(SprintUtil.TAG_INTERVAL, sprint.getInterval())
		        .put(SprintUtil.TAG_MEMBERS, sprint.getMembersNumber())
		        .put(SprintUtil.TAG_SERIAL_ID, sprint.getSerialId())
		        .put(SprintUtil.TAG_SPRINTGOAL, sprint.getSprintGoal())
		        .put(SprintUtil.TAG_HOURSCANCOMMIT, sprint.getHoursCanCommit())
		        .put(SprintUtil.TAG_FOCUSFACTOR, sprint.getFocusFactor())
		        .put(SprintUtil.TAG_DEMODATE, sprint.getDemoDate())
		        .put(SprintUtil.TAG_DEMOPLACE, sprint.getDemoPlace())
		        .put(SprintUtil.TAG_DAILYMEETING, sprint.getDailyInfo())
		        .put("stories", sprint.getStories());
		return sprintJson.toString();
	}
	
	// 將 sprint list 轉成 json 字串
	public static String convertSprintListToJsonString(ArrayList<SprintObject> sprints) throws JSONException {
		JSONArray jsonSprints = new JSONArray();
		for (SprintObject sprint : sprints) {
			JSONObject jsonSprint = new JSONObject();
			jsonSprint.put(SprintUtil.TAG_ID, sprint.getId())
			        .put(SprintUtil.TAG_PROJECT_ID, sprint.getProjectId())
			        .put(SprintUtil.TAG_STARTDATE, sprint.getStartDate())
			        .put(SprintUtil.TAG_INTERVAL, sprint.getInterval())
			        .put(SprintUtil.TAG_MEMBERS, sprint.getMembersNumber())
			        .put(SprintUtil.TAG_SERIAL_ID, sprint.getSerialId())
			        .put(SprintUtil.TAG_SPRINTGOAL, sprint.getSprintGoal())
			        .put(SprintUtil.TAG_HOURSCANCOMMIT, sprint.getHoursCanCommit())
			        .put(SprintUtil.TAG_FOCUSFACTOR, sprint.getFocusFactor())
			        .put(SprintUtil.TAG_DEMODATE, sprint.getDemoDate())
			        .put(SprintUtil.TAG_DEMOPLACE, sprint.getDemoPlace())
			        .put(SprintUtil.TAG_DAILYMEETING, sprint.getDailyInfo());
			jsonSprints.put(jsonSprint);
		}
		return jsonSprints.toString();
	}
}
