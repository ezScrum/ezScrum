package ntut.csie.ezScrum.restful.dataMigration.support;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

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
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
import ntut.csie.ezScrum.web.databaseEnum.RoleEnum;

public class JSONCheckerTest {
	@Test
	public void testCheckProjectJSON() throws JSONException {
		JSONObject projectJSON = new JSONObject();
		assertFalse(JSONChecker.checkProjectJSON(projectJSON.toString()).isEmpty());
		projectJSON.put(ProjectJSONEnum.NAME, "name");
		assertFalse(JSONChecker.checkProjectJSON(projectJSON.toString()).isEmpty());
		projectJSON.put(ProjectJSONEnum.DISPLAY_NAME, "display name");
		assertFalse(JSONChecker.checkProjectJSON(projectJSON.toString()).isEmpty());
		projectJSON.put(ProjectJSONEnum.COMMENT, "comment");
		assertFalse(JSONChecker.checkProjectJSON(projectJSON.toString()).isEmpty());
		projectJSON.put(ProjectJSONEnum.PRODUCT_OWNER, "PO");
		assertFalse(JSONChecker.checkProjectJSON(projectJSON.toString()).isEmpty());
		projectJSON.put(ProjectJSONEnum.CREATE_TIME, System.currentTimeMillis());
		assertTrue(JSONChecker.checkProjectJSON(projectJSON.toString()).isEmpty());
	}
	
	@Test
	public void testCheckReleaseJSON() throws JSONException {
		JSONObject releaseJSON = new JSONObject();
		assertFalse(JSONChecker.checkReleaseJSON(releaseJSON.toString()).isEmpty());
		releaseJSON.put(ReleaseJSONEnum.NAME, "name");
		assertFalse(JSONChecker.checkReleaseJSON(releaseJSON.toString()).isEmpty());
		releaseJSON.put(ReleaseJSONEnum.DESCRIPTION, "description");
		assertFalse(JSONChecker.checkReleaseJSON(releaseJSON.toString()).isEmpty());
		releaseJSON.put(ReleaseJSONEnum.START_DATE, "2015/12/6");
		assertFalse(JSONChecker.checkReleaseJSON(releaseJSON.toString()).isEmpty());
		releaseJSON.put(ReleaseJSONEnum.END_DATE, "2015/12/31");
		assertTrue(JSONChecker.checkReleaseJSON(releaseJSON.toString()).isEmpty());
	}
	
	@Test
	public void testCheckScrumRolesJSON() throws JSONException {
		JSONObject scrumRolesJSON = new JSONObject();
		JSONObject productOwnerJSON = new JSONObject();
		JSONObject scrumMasterJSON = new JSONObject();
		JSONObject scrumTeamJSON = new JSONObject();
		JSONObject stakeholderJSON = new JSONObject();
		JSONObject guestJSON = new JSONObject();
		scrumRolesJSON.put(ScrumRoleJSONEnum.PRODUCT_OWNER, productOwnerJSON);
		scrumRolesJSON.put(ScrumRoleJSONEnum.SCRUM_MASTER, scrumMasterJSON);
		scrumRolesJSON.put(ScrumRoleJSONEnum.SCRUM_TEAM, scrumTeamJSON);
		scrumRolesJSON.put(ScrumRoleJSONEnum.STAKEHOLDER, stakeholderJSON);
		scrumRolesJSON.put(ScrumRoleJSONEnum.GUEST, guestJSON);
		Iterator<?> iterator = scrumRolesJSON.keys();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			JSONObject roleJSON = scrumRolesJSON.getJSONObject(key);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG, true);
		}
		assertFalse(JSONChecker.checkScrumRolesJSON(scrumRolesJSON.toString()).isEmpty());
		iterator = scrumRolesJSON.keys();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			JSONObject roleJSON = scrumRolesJSON.getJSONObject(key);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN, true);
		}
		assertFalse(JSONChecker.checkScrumRolesJSON(scrumRolesJSON.toString()).isEmpty());
		iterator = scrumRolesJSON.keys();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			JSONObject roleJSON = scrumRolesJSON.getJSONObject(key);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE, true);
		}
		assertFalse(JSONChecker.checkScrumRolesJSON(scrumRolesJSON.toString()).isEmpty());
		iterator = scrumRolesJSON.keys();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			JSONObject roleJSON = scrumRolesJSON.getJSONObject(key);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG, true);
		}
		assertFalse(JSONChecker.checkScrumRolesJSON(scrumRolesJSON.toString()).isEmpty());
		iterator = scrumRolesJSON.keys();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			JSONObject roleJSON = scrumRolesJSON.getJSONObject(key);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN, true);
		}
		assertFalse(JSONChecker.checkScrumRolesJSON(scrumRolesJSON.toString()).isEmpty());
		iterator = scrumRolesJSON.keys();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			JSONObject roleJSON = scrumRolesJSON.getJSONObject(key);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_TASKBOARD, true);
		}
		assertFalse(JSONChecker.checkScrumRolesJSON(scrumRolesJSON.toString()).isEmpty());
		iterator = scrumRolesJSON.keys();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			JSONObject roleJSON = scrumRolesJSON.getJSONObject(key);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_UNPLAN, true);
		}
		assertFalse(JSONChecker.checkScrumRolesJSON(scrumRolesJSON.toString()).isEmpty());
		iterator = scrumRolesJSON.keys();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			JSONObject roleJSON = scrumRolesJSON.getJSONObject(key);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT, true);
		}
		assertFalse(JSONChecker.checkScrumRolesJSON(scrumRolesJSON.toString()).isEmpty());
		iterator = scrumRolesJSON.keys();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			JSONObject roleJSON = scrumRolesJSON.getJSONObject(key);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_REPORT, true);
		}
		assertTrue(JSONChecker.checkScrumRolesJSON(scrumRolesJSON.toString()).isEmpty());
	}
	
	@Test
	public void testCheckProjectRoleJSON() throws JSONException {
		JSONObject projectRoleJSON = new JSONObject();
		assertFalse(JSONChecker.checkProjectRoleJSON(projectRoleJSON.toString()).isEmpty());
		projectRoleJSON.put(AccountJSONEnum.USERNAME, "username");
		assertFalse(JSONChecker.checkProjectRoleJSON(projectRoleJSON.toString()).isEmpty());
		projectRoleJSON.put(ScrumRoleJSONEnum.ROLE, RoleEnum.ProductOwner.name());
		assertTrue(JSONChecker.checkProjectRoleJSON(projectRoleJSON.toString()).isEmpty());
	}
	
	@Test
	public void testCheckTagJSON() throws JSONException {
		JSONObject tagJSON = new JSONObject();
		assertFalse(JSONChecker.checkTagJSON(tagJSON.toString()).isEmpty());
		tagJSON.put(TagJSONEnum.NAME, "tag01");
		assertTrue(JSONChecker.checkTagJSON(tagJSON.toString()).isEmpty());
	}
	
	@Test
	public void testCheckAccountJSON() throws JSONException {
		JSONObject accountJSON = new JSONObject();
		assertFalse(JSONChecker.checkAccountJSON(accountJSON.toString()).isEmpty());
		accountJSON.put(AccountJSONEnum.USERNAME, "username");
		assertFalse(JSONChecker.checkAccountJSON(accountJSON.toString()).isEmpty());
		accountJSON.put(AccountJSONEnum.NICK_NAME, "nick+ name");
		assertFalse(JSONChecker.checkAccountJSON(accountJSON.toString()).isEmpty());
		accountJSON.put(AccountJSONEnum.PASSWORD, "password");
		assertFalse(JSONChecker.checkAccountJSON(accountJSON.toString()).isEmpty());
		accountJSON.put(AccountJSONEnum.EMAIL, "email@gmail.com");
		assertFalse(JSONChecker.checkAccountJSON(accountJSON.toString()).isEmpty());
		accountJSON.put(AccountJSONEnum.ENABLE, true);
		assertTrue(JSONChecker.checkAccountJSON(accountJSON.toString()).isEmpty());
	}
	
	@Test
	public void testCheckSprintJSON() throws JSONException {
		JSONObject sprintJSON = new JSONObject();
		assertFalse(JSONChecker.checkSprintJSON(sprintJSON.toString()).isEmpty());
		sprintJSON.put(SprintJSONEnum.GOAL, "sprint goal");
		assertFalse(JSONChecker.checkSprintJSON(sprintJSON.toString()).isEmpty());
		sprintJSON.put(SprintJSONEnum.INTERVAL, 2);
		assertFalse(JSONChecker.checkSprintJSON(sprintJSON.toString()).isEmpty());
		sprintJSON.put(SprintJSONEnum.TEAM_SIZE, 5);
		assertFalse(JSONChecker.checkSprintJSON(sprintJSON.toString()).isEmpty());
		sprintJSON.put(SprintJSONEnum.AVAILABLE_HOURS, 100);
		assertFalse(JSONChecker.checkSprintJSON(sprintJSON.toString()).isEmpty());
		sprintJSON.put(SprintJSONEnum.FOCUS_FACTOR, 70);
		assertFalse(JSONChecker.checkSprintJSON(sprintJSON.toString()).isEmpty());
		sprintJSON.put(SprintJSONEnum.START_DATE, "2015/11/27");
		assertFalse(JSONChecker.checkSprintJSON(sprintJSON.toString()).isEmpty());
		sprintJSON.put(SprintJSONEnum.END_DATE, "2015/12/10");
		assertFalse(JSONChecker.checkSprintJSON(sprintJSON.toString()).isEmpty());
		sprintJSON.put(SprintJSONEnum.DEMO_DATE, "2015/12/10");
		assertFalse(JSONChecker.checkSprintJSON(sprintJSON.toString()).isEmpty());
		sprintJSON.put(SprintJSONEnum.DEMO_PLACE, "Lab1321");
		assertFalse(JSONChecker.checkSprintJSON(sprintJSON.toString()).isEmpty());
		sprintJSON.put(SprintJSONEnum.DAILY_INFO, "12:50@Lab1321");
		assertTrue(JSONChecker.checkSprintJSON(sprintJSON.toString()).isEmpty());
	}
	
	@Test
	public void testCheckStoryJSON() throws JSONException {
		JSONObject storyJSON = new JSONObject();
		storyJSON.put(StoryJSONEnum.NAME, "name");
		assertFalse(JSONChecker.checkStoryJSON(storyJSON.toString()).isEmpty());
		storyJSON.put(StoryJSONEnum.STATUS, "new");
		assertFalse(JSONChecker.checkStoryJSON(storyJSON.toString()).isEmpty());
		storyJSON.put(StoryJSONEnum.ESTIMATE, 1);
		assertFalse(JSONChecker.checkStoryJSON(storyJSON.toString()).isEmpty());
		storyJSON.put(StoryJSONEnum.IMPORTANCE, 2);
		assertFalse(JSONChecker.checkStoryJSON(storyJSON.toString()).isEmpty());
		storyJSON.put(StoryJSONEnum.VALUE, 3);
		assertFalse(JSONChecker.checkStoryJSON(storyJSON.toString()).isEmpty());
		storyJSON.put(StoryJSONEnum.NOTES, "notes");
		assertFalse(JSONChecker.checkStoryJSON(storyJSON.toString()).isEmpty());
		storyJSON.put(StoryJSONEnum.HOW_TO_DEMO, "how to demo");
		assertTrue(JSONChecker.checkStoryJSON(storyJSON.toString()).isEmpty());
	}
	
	@Test
	public void testCheckTaskJSON() throws JSONException {
		JSONObject taskJSON = new JSONObject();
		taskJSON.put(TaskJSONEnum.NAME, "name");
		assertFalse(JSONChecker.checkTaskJSON(taskJSON.toString()).isEmpty());
		taskJSON.put(TaskJSONEnum.HANDLER, "account1");
		assertFalse(JSONChecker.checkTaskJSON(taskJSON.toString()).isEmpty());
		taskJSON.put(TaskJSONEnum.ESTIMATE, 1);
		assertFalse(JSONChecker.checkTaskJSON(taskJSON.toString()).isEmpty());
		taskJSON.put(TaskJSONEnum.REMAIN, 2);
		assertFalse(JSONChecker.checkTaskJSON(taskJSON.toString()).isEmpty());
		//taskJSON.put(TaskJSONEnum.ACTUAL, 3);
		assertFalse(JSONChecker.checkTaskJSON(taskJSON.toString()).isEmpty());
		taskJSON.put(TaskJSONEnum.NOTES, "notes");
		assertFalse(JSONChecker.checkTaskJSON(taskJSON.toString()).isEmpty());
		taskJSON.put(TaskJSONEnum.STATUS, "assigned");
		assertFalse(JSONChecker.checkTaskJSON(taskJSON.toString()).isEmpty());
		JSONArray partnerJSONArray = new JSONArray();
		taskJSON.put(TaskJSONEnum.PARTNERS, partnerJSONArray);
		assertTrue(JSONChecker.checkTaskJSON(taskJSON.toString()).isEmpty());
	}
	
	@Test
	public void testCheckUnplanJSON() throws JSONException {
		JSONObject unplanJSON = new JSONObject();
		unplanJSON.put(UnplanJSONEnum.NAME, "name");
		assertFalse(JSONChecker.checkUnplanJSON(unplanJSON.toString()).isEmpty());
		unplanJSON.put(UnplanJSONEnum.HANDLER, "account1");
		assertFalse(JSONChecker.checkUnplanJSON(unplanJSON.toString()).isEmpty());
		unplanJSON.put(UnplanJSONEnum.ESTIMATE, 1);
		assertFalse(JSONChecker.checkUnplanJSON(unplanJSON.toString()).isEmpty());
		unplanJSON.put(UnplanJSONEnum.ACTUAL, 3);
		assertFalse(JSONChecker.checkUnplanJSON(unplanJSON.toString()).isEmpty());
		unplanJSON.put(UnplanJSONEnum.NOTES, "notes");
		assertFalse(JSONChecker.checkUnplanJSON(unplanJSON.toString()).isEmpty());
		unplanJSON.put(UnplanJSONEnum.STATUS, "assigned");
		assertFalse(JSONChecker.checkUnplanJSON(unplanJSON.toString()).isEmpty());
		JSONArray partnerJSONArray = new JSONArray();
		unplanJSON.put(UnplanJSONEnum.PARTNERS, partnerJSONArray);
		assertTrue(JSONChecker.checkUnplanJSON(unplanJSON.toString()).isEmpty());
	}
	
	@Test
	public void testCheckRetrospectiveJSON() throws JSONException {
		JSONObject retrospectiveJSON = new JSONObject();
		retrospectiveJSON.put(RetrospectiveJSONEnum.NAME, "name");
		assertFalse(JSONChecker.checkRetrospectiveJSON(retrospectiveJSON.toString()).isEmpty());
		retrospectiveJSON.put(RetrospectiveJSONEnum.DESCRIPTION, "description");
		assertFalse(JSONChecker.checkRetrospectiveJSON(retrospectiveJSON.toString()).isEmpty());
		retrospectiveJSON.put(RetrospectiveJSONEnum.TYPE, RetrospectiveObject.TYPE_GOOD);
		assertFalse(JSONChecker.checkRetrospectiveJSON(retrospectiveJSON.toString()).isEmpty());
		retrospectiveJSON.put(RetrospectiveJSONEnum.STATUS, RetrospectiveObject.STATUS_RESOLVED);
		assertTrue(JSONChecker.checkRetrospectiveJSON(retrospectiveJSON.toString()).isEmpty());
	}
	
	@Test
	public void testCheckHistoryJSON() throws JSONException {
		JSONObject historyJSON = new JSONObject();
		historyJSON.put(HistoryJSONEnum.HISTORY_TYPE, "NAME");
		assertFalse(JSONChecker.checkHistoryJSON(historyJSON.toString()).isEmpty());
		historyJSON.put(HistoryJSONEnum.OLD_VALUE, "old name");
		assertFalse(JSONChecker.checkHistoryJSON(historyJSON.toString()).isEmpty());
		historyJSON.put(HistoryJSONEnum.NEW_VALUE, "new name");
		assertFalse(JSONChecker.checkHistoryJSON(historyJSON.toString()).isEmpty());
		historyJSON.put(HistoryJSONEnum.CREATE_TIME, System.currentTimeMillis());
		assertTrue(JSONChecker.checkHistoryJSON(historyJSON.toString()).isEmpty());
	}
	
	@Test
	public void testCheckAttachFileJSON() throws JSONException {
		JSONObject attachFileJSON = new JSONObject();
		attachFileJSON.put(AttachFileJSONEnum.NAME, "NAME");
		assertFalse(JSONChecker.checkAttachFileJSON(attachFileJSON.toString()).isEmpty());
		attachFileJSON.put(AttachFileJSONEnum.CONTENT_TYPE, "application/octet-stream");
		assertFalse(JSONChecker.checkAttachFileJSON(attachFileJSON.toString()).isEmpty());
		attachFileJSON.put(AttachFileJSONEnum.BINARY, "U3RvcnkwMQ==");
		assertTrue(JSONChecker.checkAttachFileJSON(attachFileJSON.toString()).isEmpty());
	}
}
