package ntut.csie.ezScrum.restful.dataMigration;

import static org.junit.Assert.assertTrue;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.AccountJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.AttachFileJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ExportJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.HistoryJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ProjectJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ReleaseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ScrumRoleJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.SprintJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.StoryJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TagJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TaskJSONEnum;

public class IntegratedRESTfulApiTest {
	@Test
	public void testImportProjectsJSON() throws JSONException {
		// Test Data
		JSONObject entityJSON = new JSONObject();
		//// Accounts
		JSONArray accountJSONArray = new JSONArray();
		JSONObject accountJSON1 = new JSONObject();
		accountJSON1.put(AccountJSONEnum.USERNAME, "TEST_ACCOUNT_1");
		accountJSON1.put(AccountJSONEnum.NICK_NAME, "TEST_ACCOUNT_1_NICKNAME");
		accountJSON1.put(AccountJSONEnum.PASSWORD, "TEST_ACCOUNT_1_PASSWORD");
		accountJSON1.put(AccountJSONEnum.EMAIL, "TEST_ACCOUNT_1@gmail.com");
		accountJSON1.put(AccountJSONEnum.ENABLE, true);
		JSONObject accountJSON2 = new JSONObject();
		accountJSON2.put(AccountJSONEnum.USERNAME, "TEST_ACCOUNT_2");
		accountJSON2.put(AccountJSONEnum.NICK_NAME, "TEST_ACCOUNT_2_NICKNAME");
		accountJSON2.put(AccountJSONEnum.PASSWORD, "TEST_ACCOUNT_2_PASSWORD");
		accountJSON2.put(AccountJSONEnum.EMAIL, "TEST_ACCOUNT_2@gmail.com");
		accountJSON2.put(AccountJSONEnum.ENABLE, true);
		accountJSONArray.put(accountJSON1)
		                .put(accountJSON2);
		entityJSON.put(ExportJSONEnum.ACCOUNTS, accountJSONArray);
		
		//// Projects
		JSONArray projectJSONArray = new JSONArray();
		JSONObject projectJSON = new JSONObject();
		projectJSON.put(ProjectJSONEnum.NAME, "TEST_PROJECT_1");
		projectJSON.put(ProjectJSONEnum.DISPLAY_NAME, "TEST_PROJECT_1_DISPLAY_NAME");
		projectJSON.put(ProjectJSONEnum.COMMENT, "TEST_PROJECT_1_COMMENT");
		projectJSON.put(ProjectJSONEnum.PRODUCT_OWNER, "TEST_PROJECT_1_PRODUCT_OWNER");
		projectJSON.put(ProjectJSONEnum.ATTATCH_MAX_SIZE, 2);
		projectJSON.put(ProjectJSONEnum.CREATE_TIME, System.currentTimeMillis());
		projectJSONArray.put(projectJSON);
		entityJSON.put(ExportJSONEnum.PROJECTS, projectJSONArray);
		// ScrumRole
		JSONObject scrumRoleJSON = new JSONObject();
		JSONObject productOwnerJSON = new JSONObject();
		productOwnerJSON.put(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG, true);
		productOwnerJSON.put(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN, true);
		productOwnerJSON.put(ScrumRoleJSONEnum.ACCESS_TASKBOARD, true);
		productOwnerJSON.put(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG, true);
		productOwnerJSON.put(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN, true);
		productOwnerJSON.put(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE, true);
		productOwnerJSON.put(ScrumRoleJSONEnum.ACCESS_UNPLAN, true);
		productOwnerJSON.put(ScrumRoleJSONEnum.ACCESS_REPORT, true);
		productOwnerJSON.put(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT, true);
		scrumRoleJSON.put(ScrumRoleJSONEnum.PRODUCT_OWNER, productOwnerJSON);
		
		JSONObject scrumMasterJSON = new JSONObject();
		scrumMasterJSON.put(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG, true);
		scrumMasterJSON.put(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN, true);
		scrumMasterJSON.put(ScrumRoleJSONEnum.ACCESS_TASKBOARD, true);
		scrumMasterJSON.put(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG, true);
		scrumMasterJSON.put(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN, true);
		scrumMasterJSON.put(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE, true);
		scrumMasterJSON.put(ScrumRoleJSONEnum.ACCESS_UNPLAN, true);
		scrumMasterJSON.put(ScrumRoleJSONEnum.ACCESS_REPORT, true);
		scrumMasterJSON.put(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT, false);
		scrumRoleJSON.put(ScrumRoleJSONEnum.SCRUM_MASTER, scrumMasterJSON);
		
		JSONObject scrumTeamJSON = new JSONObject();
		scrumTeamJSON.put(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG, true);
		scrumTeamJSON.put(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN, true);
		scrumTeamJSON.put(ScrumRoleJSONEnum.ACCESS_TASKBOARD, true);
		scrumTeamJSON.put(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG, true);
		scrumTeamJSON.put(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN, true);
		scrumTeamJSON.put(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE, true);
		scrumTeamJSON.put(ScrumRoleJSONEnum.ACCESS_UNPLAN, true);
		scrumTeamJSON.put(ScrumRoleJSONEnum.ACCESS_REPORT, false);
		scrumTeamJSON.put(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT, false);
		scrumRoleJSON.put(ScrumRoleJSONEnum.SCRUM_MASTER, scrumTeamJSON);
		
		JSONObject StakeholderJSON = new JSONObject();
		StakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG, true);
		StakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN, false);
		StakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_TASKBOARD, false);
		StakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG, true);
		StakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN, false);
		StakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE, false);
		StakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_UNPLAN, false);
		StakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_REPORT, true);
		StakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT, false);
		scrumRoleJSON.put(ScrumRoleJSONEnum.SCRUM_MASTER, StakeholderJSON);
		
		JSONObject guestJSON = new JSONObject();
		guestJSON.put(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG, true);
		guestJSON.put(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN, false);
		guestJSON.put(ScrumRoleJSONEnum.ACCESS_TASKBOARD, false);
		guestJSON.put(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG, true);
		guestJSON.put(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN, false);
		guestJSON.put(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE, false);
		guestJSON.put(ScrumRoleJSONEnum.ACCESS_UNPLAN, false);
		guestJSON.put(ScrumRoleJSONEnum.ACCESS_REPORT, false);
		guestJSON.put(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT, false);
		scrumRoleJSON.put(ScrumRoleJSONEnum.SCRUM_MASTER, guestJSON);
		projectJSON.put(ProjectJSONEnum.SCRUM_ROLES, scrumRoleJSON);
		
		// Project Role
		JSONArray projectRoleJSONArray = new JSONArray();
		JSONObject accountJSON = new JSONObject();
		accountJSON.put(AccountJSONEnum.USERNAME, "TEST_ACCOUNT_1");
		accountJSON.put(ScrumRoleJSONEnum.ROLE, "ScrumTeam");
		projectRoleJSONArray.put(accountJSON);
		
		// Tags
		JSONArray tagJSONArray = new JSONArray();
		JSONObject tagJSON1 = new JSONObject();
		tagJSON1.put(TagJSONEnum.NAME, "TEST_TAG_1");
		JSONObject tagJSON2 = new JSONObject();
		tagJSON2.put(TagJSONEnum.NAME, "TEST_TAG_2");
		tagJSONArray.put(tagJSON1);
		tagJSONArray.put(tagJSON2);
		projectJSON.put(ProjectJSONEnum.TAGS, tagJSONArray);
		
		// Release
		JSONArray releaseJSONArray = new JSONArray();
		JSONObject releaseJSON = new JSONObject();
		releaseJSON.put(ReleaseJSONEnum.NAME, "TEST_RELEASE_1");
		releaseJSON.put(ReleaseJSONEnum.DESCRIPTION, "TEST_RELEASE_1_DESCRIPTION");
		releaseJSON.put(ReleaseJSONEnum.START_DATE, "2015/11/24");
		releaseJSON.put(ReleaseJSONEnum.DUE_DATE, "2015/12/21");
		releaseJSONArray.put(releaseJSON);
		projectJSON.put(ProjectJSONEnum.RELEASES, releaseJSONArray);
		
		// Sprint
		JSONArray sprintJSONArray = new JSONArray();
		JSONObject sprintJSON = new JSONObject();
		sprintJSON.put(SprintJSONEnum.GOAL, "TEST_SPRINT_GOAL");
		sprintJSON.put(SprintJSONEnum.INTERVAL, 2);
		sprintJSON.put(SprintJSONEnum.TEAM_SIZE, 4);
		sprintJSON.put(SprintJSONEnum.AVAILABLE_HOURS, 120);
		sprintJSON.put(SprintJSONEnum.FOCUS_FACTOR, 80);
		sprintJSON.put(SprintJSONEnum.START_DATE, "2015/11/24");
		sprintJSON.put(SprintJSONEnum.DUE_DATE, "2015/12/07");
		sprintJSON.put(SprintJSONEnum.DEMO_DATE, "2015/12/07");
		sprintJSON.put(SprintJSONEnum.DEMO_PLACE, "TEST_SPRINT_DEMO_PLACE");
		sprintJSON.put(SprintJSONEnum.DAILY_INFO, "TEST_SPRINT_DAILY_INFO");
		projectJSON.put(ProjectJSONEnum.SPRINTS, sprintJSONArray);
		
		// Story
		JSONArray storyJSONArray = new JSONArray();
		JSONObject storyJSON = new JSONObject();
		storyJSON.put(StoryJSONEnum.ID, 1);
		storyJSON.put(StoryJSONEnum.NAME, "TEST_STORY_1");
		storyJSON.put(StoryJSONEnum.STATUS, "new");
		storyJSON.put(StoryJSONEnum.ESTIMATE, 3);
		storyJSON.put(StoryJSONEnum.IMPORTANCE, 90);
		storyJSON.put(StoryJSONEnum.VALUE, 5);
		storyJSON.put(StoryJSONEnum.NOTES, "TEST_STORY_1_NOTES");
		storyJSON.put(StoryJSONEnum.HOW_TO_DEMO, "TEST_STORY_1_HOWTODEMO");
		sprintJSON.put(SprintJSONEnum.STORIES, storyJSONArray);
		// Tag in Story
		JSONArray tagInStoryJSONArray = new JSONArray();
		JSONObject tagJSON = new JSONObject();
		tagJSON.put(TagJSONEnum.NAME, "TEST_TAG_1");
		tagInStoryJSONArray.put(tagJSON);
		storyJSON.put(StoryJSONEnum.TAGS, tagInStoryJSONArray);
		// History in Story
		JSONArray historyInStoryJSONArray = new JSONArray();
		JSONObject historyJSON = new JSONObject();
		historyJSON.put(HistoryJSONEnum.HISTORY_TYPE, "CREATE");
		historyJSON.put(HistoryJSONEnum.OLD_VALUE, "");
		historyJSON.put(HistoryJSONEnum.NEW_VALUE, "");
		historyJSON.put(HistoryJSONEnum.CREATE_TIME, System.currentTimeMillis());
		historyInStoryJSONArray.put(historyJSON);
		storyJSON.put(StoryJSONEnum.HISTORIES, historyInStoryJSONArray);
		// AttachFiles in Story
		JSONArray attachfileInStoryJSONArray = new JSONArray();
		JSONObject attachfileJSON = new JSONObject();
		attachfileJSON.put(AttachFileJSONEnum.NAME, "Story01.txt");
		attachfileJSON.put(AttachFileJSONEnum.CONTENT_TYPE, "application/octet-stream");
		attachfileJSON.put(AttachFileJSONEnum.BINARY, "U3RvcnkwMQ==");
		attachfileInStoryJSONArray.put(attachfileJSON);
		storyJSON.put(StoryJSONEnum.ATTACH_FILES, attachfileInStoryJSONArray);
		// Tasks
		JSONArray taskInStoryJSONArray = new JSONArray();
		JSONObject taskJSON = new JSONObject();
		taskJSON.put(TaskJSONEnum.ID, 3);
		taskJSON.put(TaskJSONEnum.NAME, "TEST_TASK_1");
		taskJSON.put(TaskJSONEnum.HANDLER, "TEST_ACCOUNT_2");
		taskJSON.put(TaskJSONEnum.ESTIMATE, 8);
		taskJSON.put(TaskJSONEnum.REMAIN, 0);
		taskJSON.put(TaskJSONEnum.ACTUAL, 0);
		taskJSON.put(TaskJSONEnum.NOTES, "TEST_TASK_NOTES");
		taskJSON.put(TaskJSONEnum.STATUS, "closed");
		taskInStoryJSONArray.put(taskJSON);
		storyJSON.put(StoryJSONEnum.TASKS, taskInStoryJSONArray);
		// Partners in Task
		JSONArray partnerInTaskJSONArray = new JSONArray();
		JSONObject partnerJSON = new JSONObject();
		partnerJSON.put(AccountJSONEnum.USERNAME, "TEST_ACCOUNT_1");
		partnerInTaskJSONArray.put(partnerJSON);
		taskJSON.put(TaskJSONEnum.PARTNERS, partnerInTaskJSONArray);
		// History in Task
		JSONArray historyInTaskJSONArray = new JSONArray();
		JSONObject historyInTaskJSON = new JSONObject();
		historyInTaskJSON.put(HistoryJSONEnum.HISTORY_TYPE, "CREATE");
		historyInTaskJSON.put(HistoryJSONEnum.OLD_VALUE, "");
		historyInTaskJSON.put(HistoryJSONEnum.NEW_VALUE, "");
		historyInTaskJSON.put(HistoryJSONEnum.CREATE_TIME, System.currentTimeMillis());
		historyInTaskJSONArray.put(historyInTaskJSON);
		taskJSON.put(TaskJSONEnum.HISTORIES, historyInTaskJSONArray);
		// AttachFile in Task
		JSONArray attachfileInTaskJSONArray = new JSONArray();
		JSONObject attachfileInTaskJSON = new JSONObject();
		attachfileInTaskJSON.put(AttachFileJSONEnum.NAME, "Task01.txt");
		attachfileInTaskJSON.put(AttachFileJSONEnum.CONTENT_TYPE, "application/octet-stream");
		attachfileInTaskJSON.put(AttachFileJSONEnum.BINARY, "VGFzazAx");
		attachfileInTaskJSONArray.put(attachfileInTaskJSON);
		taskJSON.put(StoryJSONEnum.ATTACH_FILES, attachfileInTaskJSONArray);
	}
}
