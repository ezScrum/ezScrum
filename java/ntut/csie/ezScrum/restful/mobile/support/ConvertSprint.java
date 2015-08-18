package ntut.csie.ezScrum.restful.mobile.support;

import java.util.ArrayList;

import ntut.csie.ezScrum.restful.mobile.util.SprintUtil;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ConvertSprint {

	public static String getAllSprint(ArrayList<SprintObject> sprints,
			long currentSprintID) throws JSONException {
		JSONObject sprintList = new JSONObject();
		JSONArray sprintArray = new JSONArray();
		sprintList.put(SprintUtil.TAG_CURRENT_SPRINTID, currentSprintID);
		sprintList.put(SprintUtil.TAG_SPRINTS, sprintArray);
		for (SprintObject sprint : sprints) {
			JSONObject sprintPlan = new JSONObject();
			JSONObject sprintProperties = new JSONObject();
			sprintProperties.put(SprintUtil.TAG_ID, sprint.getId())
							.put(SprintUtil.TAG_PROJECT_ID, sprint.getProjectId())
							.put(SprintUtil.TAG_START_DATE, sprint.getStartDateString())
							.put(SprintUtil.TAG_INTERVAL, sprint.getInterval())
							.put(SprintUtil.TAG_MEMBERS, sprint.getMembersAmount())
							.put(SprintUtil.TAG_SERIAL_ID, sprint.getSerialId())
							.put(SprintUtil.TAG_SPRINT_GOAL, sprint.getSprintGoal())
							.put(SprintUtil.TAG_HOURS_CAN_COMMIT, sprint.getHoursCanCommit())
							.put(SprintUtil.TAG_FOCUS_FACTOR, sprint.getFocusFactor())
							.put(SprintUtil.TAG_DEMO_DATE, sprint.getDemoDateString())
							.put(SprintUtil.TAG_DEMO_PLACE, sprint.getDemoPlace())
							.put(SprintUtil.TAG_DAILY_MEETING, sprint.getDailyInfo());
			sprintPlan.put(SprintUtil.TAG_SPRINT, sprintProperties);
			sprintArray.put(sprintPlan);
		}
		System.out.println("ConvertSprint : getAllSprint : "
				+ sprintList.toString());
		return sprintList.toString();
	}

	public static String convertSprintToJsonString(SprintObject sprint) throws JSONException {
		JSONObject sprintJson = new JSONObject();
		sprintJson.put(SprintUtil.TAG_ID, sprint.getId())
		        .put(SprintUtil.TAG_PROJECT_ID, sprint.getProjectId())
		        .put(SprintUtil.TAG_START_DATE, sprint.getStartDateString())
		        .put(SprintUtil.TAG_INTERVAL, sprint.getInterval())
		        .put(SprintUtil.TAG_MEMBERS, sprint.getMembersAmount())
		        .put(SprintUtil.TAG_SERIAL_ID, sprint.getSerialId())
		        .put(SprintUtil.TAG_SPRINT_GOAL, sprint.getSprintGoal())
		        .put(SprintUtil.TAG_HOURS_CAN_COMMIT, sprint.getHoursCanCommit())
		        .put(SprintUtil.TAG_FOCUS_FACTOR, sprint.getFocusFactor())
		        .put(SprintUtil.TAG_DEMO_DATE, sprint.getDemoDateString())
		        .put(SprintUtil.TAG_DEMO_PLACE, sprint.getDemoPlace())
		        .put(SprintUtil.TAG_DAILY_MEETING, sprint.getDailyInfo())
				.put(SprintUtil.TAG_DUE_DATE, sprint.getDueDateString());
		
		JSONArray storiesArray = new JSONArray();
		for(StoryObject story : sprint.getStories()){
			storiesArray.put(story.toJSON());
		}
		
		sprintJson.put(SprintUtil.TAG_STORIES, storiesArray);
		return sprintJson.toString();
	}

	// 將 sprint list 轉成 json 字串
	public static String convertSprintsToJsonString(
			ArrayList<SprintObject> sprints) throws JSONException {
		JSONArray jsonSprints = new JSONArray();
		for (SprintObject sprint : sprints) {
			JSONObject jsonSprint = new JSONObject();
			jsonSprint.put(SprintUtil.TAG_ID, sprint.getId())
					.put(SprintUtil.TAG_PROJECT_ID, sprint.getProjectId())
					.put(SprintUtil.TAG_START_DATE, sprint.getStartDateString())
					.put(SprintUtil.TAG_INTERVAL, sprint.getInterval())
					.put(SprintUtil.TAG_MEMBERS, sprint.getMembersAmount())
					.put(SprintUtil.TAG_SERIAL_ID, sprint.getSerialId())
					.put(SprintUtil.TAG_SPRINT_GOAL, sprint.getSprintGoal())
					.put(SprintUtil.TAG_HOURS_CAN_COMMIT, sprint.getHoursCanCommit())
					.put(SprintUtil.TAG_FOCUS_FACTOR, sprint.getFocusFactor())
					.put(SprintUtil.TAG_DEMO_DATE, sprint.getDemoDateString())
					.put(SprintUtil.TAG_DEMO_PLACE, sprint.getDemoPlace())
					.put(SprintUtil.TAG_DAILY_MEETING, sprint.getDailyInfo());
			jsonSprints.put(jsonSprint);
		}
		return jsonSprints.toString();
	}
}
