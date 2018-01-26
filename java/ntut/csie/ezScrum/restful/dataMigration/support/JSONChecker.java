package ntut.csie.ezScrum.restful.dataMigration.support;

import java.util.Iterator;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.AccountJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.AttachFileJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.HistoryJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ProjectJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ReleaseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.RetrospectiveJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ScrumRoleJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.SprintJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.StoryJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TagJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TaskJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.UnplanJSONEnum;

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
	
	public static String checkReleaseJSON(String releaseJSONString) {
		String message = "";
		try {
			JSONObject releaseJSON = new JSONObject(releaseJSONString);
			releaseJSON.getString(ReleaseJSONEnum.NAME);
			releaseJSON.getString(ReleaseJSONEnum.DESCRIPTION);
			releaseJSON.getString(ReleaseJSONEnum.START_DATE);
			releaseJSON.getString(ReleaseJSONEnum.END_DATE);
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

	public static String checkAccountJSON(String accountJSONString) {
		String message = "";
		try {
			JSONObject accountJSON = new JSONObject(accountJSONString);
			accountJSON.getString(AccountJSONEnum.USERNAME);
			accountJSON.getString(AccountJSONEnum.NICK_NAME);
			accountJSON.getString(AccountJSONEnum.PASSWORD);
			accountJSON.getString(AccountJSONEnum.EMAIL);
			accountJSON.getBoolean(AccountJSONEnum.ENABLE);
		} catch (JSONException e) {
			message = e.getMessage();
		}
		return message;
	}
	
	public static String checkSprintJSON(String sprintJSONString) {
		String message = "";
		try {
			JSONObject sprintJSON = new JSONObject(sprintJSONString);
			sprintJSON.getString(SprintJSONEnum.GOAL);
			sprintJSON.getInt(SprintJSONEnum.INTERVAL);
			sprintJSON.getInt(SprintJSONEnum.TEAM_SIZE);
			sprintJSON.getInt(SprintJSONEnum.AVAILABLE_HOURS);
			sprintJSON.getInt(SprintJSONEnum.FOCUS_FACTOR);
			sprintJSON.getString(SprintJSONEnum.START_DATE);
			sprintJSON.getString(SprintJSONEnum.END_DATE);
			sprintJSON.getString(SprintJSONEnum.DEMO_DATE);
			sprintJSON.getString(SprintJSONEnum.DEMO_PLACE);
			sprintJSON.getString(SprintJSONEnum.DAILY_INFO);
		} catch (JSONException e) {
			message = e.getMessage();
		}
		return message;
	}
	
	public static String checkStoryJSON(String storyJSONString) {
		String message = "";
		try {
			JSONObject storyJSON = new JSONObject(storyJSONString);
			storyJSON.getString(StoryJSONEnum.NAME);
			storyJSON.getString(StoryJSONEnum.STATUS);
			storyJSON.getInt(StoryJSONEnum.ESTIMATE);
			storyJSON.getInt(StoryJSONEnum.IMPORTANCE);
			storyJSON.getInt(StoryJSONEnum.VALUE);
			storyJSON.getString(StoryJSONEnum.NOTES);
			storyJSON.getString(StoryJSONEnum.HOW_TO_DEMO);
		} catch (JSONException e) {
			message = e.getMessage();
		}
		return message;
	}
	
	public static String checkTaskJSON(String taskJSONString) {
		String message = "";
		try {
			JSONObject taskJSON = new JSONObject(taskJSONString);
			taskJSON.getString(TaskJSONEnum.NAME);
			taskJSON.getString(TaskJSONEnum.HANDLER);
			taskJSON.getInt(TaskJSONEnum.ESTIMATE);
			taskJSON.getInt(TaskJSONEnum.REMAIN);
			//taskJSON.getInt(TaskJSONEnum.ACTUAL);
			taskJSON.getString(TaskJSONEnum.NOTES);
			taskJSON.getString(TaskJSONEnum.STATUS);
			JSONArray partnerJSONArray = taskJSON.getJSONArray(TaskJSONEnum.PARTNERS);
			for (int i = 0; i < partnerJSONArray.length(); i++) {
				partnerJSONArray.getJSONObject(i).getString(AccountJSONEnum.USERNAME);
			}
		} catch (JSONException e) {
			message = e.getMessage();
		}
		return message;
	}
	
	public static String checkUnplanJSON(String unplanJSONString) {
		String message = "";
		try {
			JSONObject unplanJSON = new JSONObject(unplanJSONString);
			unplanJSON.getString(UnplanJSONEnum.NAME);
			unplanJSON.getString(UnplanJSONEnum.HANDLER);
			unplanJSON.getInt(UnplanJSONEnum.ESTIMATE);
			//unplanJSON.getInt(UnplanJSONEnum.ACTUAL);
			unplanJSON.getString(UnplanJSONEnum.NOTES);
			unplanJSON.getString(UnplanJSONEnum.STATUS);
			JSONArray partnerJSONArray = unplanJSON.getJSONArray(UnplanJSONEnum.PARTNERS);
			for (int i = 0; i < partnerJSONArray.length(); i++) {
				partnerJSONArray.getJSONObject(i).getString(AccountJSONEnum.USERNAME);
			}
		} catch (JSONException e) {
			message = e.getMessage();
		}
		return message;
	}
	
	public static String checkRetrospectiveJSON(String retrospectiveJSONString) {
		String message = "";
		try {
			JSONObject retrospectiveJSON = new JSONObject(retrospectiveJSONString);
			retrospectiveJSON.getString(RetrospectiveJSONEnum.NAME);
			retrospectiveJSON.getString(RetrospectiveJSONEnum.DESCRIPTION);
			retrospectiveJSON.getString(RetrospectiveJSONEnum.TYPE);
			retrospectiveJSON.getString(RetrospectiveJSONEnum.STATUS);
		} catch (JSONException e) {
			message = e.getMessage();
		}
		return message;
	}
	
	public static String checkHistoryJSON(String historyJSONString) {
		String message = "";
		try {
			JSONObject historyJSON = new JSONObject(historyJSONString);
			historyJSON.getString(HistoryJSONEnum.HISTORY_TYPE);
			historyJSON.getString(HistoryJSONEnum.OLD_VALUE);
			historyJSON.getString(HistoryJSONEnum.NEW_VALUE);
			historyJSON.getLong(HistoryJSONEnum.CREATE_TIME);
		} catch (JSONException e) {
			message = e.getMessage();
		}
		return message;
	}
	
	public static String checkAttachFileJSON(String attachFileJSONString) {
		String message = "";
		try {
			JSONObject attachFileJSON = new JSONObject(attachFileJSONString);
			attachFileJSON.getString(AttachFileJSONEnum.NAME);
			attachFileJSON.getString(AttachFileJSONEnum.CONTENT_TYPE);
			attachFileJSON.getString(AttachFileJSONEnum.BINARY);
		} catch (JSONException e) {
			message = e.getMessage();
		}
		return message;
	}
}
