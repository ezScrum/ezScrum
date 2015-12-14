package ntut.csie.ezScrum.restful.dataMigration;

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
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.RetrospectiveJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ScrumRoleJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.SprintJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.StoryJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TagJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TaskJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.UnplanJSONEnum;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;

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
		long project1CreateTime = System.currentTimeMillis();
		projectJSON.put(ProjectJSONEnum.CREATE_TIME, project1CreateTime);
		projectJSONArray.put(projectJSON);
		entityJSON.put(ExportJSONEnum.PROJECTS, projectJSONArray);
		// ScrumRole
		JSONObject scrumRolesJSON = new JSONObject();
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
		scrumRolesJSON.put(ScrumRoleJSONEnum.PRODUCT_OWNER, productOwnerJSON);
		
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
		scrumRolesJSON.put(ScrumRoleJSONEnum.SCRUM_MASTER, scrumMasterJSON);
		
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
		scrumRolesJSON.put(ScrumRoleJSONEnum.SCRUM_TEAM, scrumTeamJSON);
		
		JSONObject stakeholderJSON = new JSONObject();
		stakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG, true);
		stakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN, false);
		stakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_TASKBOARD, false);
		stakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG, true);
		stakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN, false);
		stakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE, false);
		stakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_UNPLAN, false);
		stakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_REPORT, true);
		stakeholderJSON.put(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT, false);
		scrumRolesJSON.put(ScrumRoleJSONEnum.STAKEHOLDER, stakeholderJSON);
		
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
		scrumRolesJSON.put(ScrumRoleJSONEnum.GUEST, guestJSON);
		projectJSON.put(ProjectJSONEnum.SCRUM_ROLES, scrumRolesJSON);
		
		// Project Role
		JSONArray projectRoleJSONArray = new JSONArray();
		JSONObject accountJSON = new JSONObject();
		accountJSON.put(AccountJSONEnum.USERNAME, "TEST_ACCOUNT_1");
		accountJSON.put(ScrumRoleJSONEnum.ROLE, "ScrumTeam");
		projectRoleJSONArray.put(accountJSON);
		
		// Tags
		JSONArray tagJSONArray = new JSONArray();
		JSONObject projectTagJSON1 = new JSONObject();
		projectTagJSON1.put(TagJSONEnum.NAME, "TEST_TAG_1");
		JSONObject projectTagJSON2 = new JSONObject();
		projectTagJSON2.put(TagJSONEnum.NAME, "TEST_TAG_2");
		tagJSONArray.put(projectTagJSON1);
		tagJSONArray.put(projectTagJSON2);
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
		sprintJSONArray.put(sprintJSON);
		
		// Story
		JSONArray storyJSONArray = new JSONArray();
		JSONObject storyJSON = new JSONObject();
		storyJSON.put(StoryJSONEnum.NAME, "TEST_STORY_1");
		storyJSON.put(StoryJSONEnum.STATUS, "new");
		storyJSON.put(StoryJSONEnum.ESTIMATE, 3);
		storyJSON.put(StoryJSONEnum.IMPORTANCE, 90);
		storyJSON.put(StoryJSONEnum.VALUE, 5);
		storyJSON.put(StoryJSONEnum.NOTES, "TEST_STORY_1_NOTES");
		storyJSON.put(StoryJSONEnum.HOW_TO_DEMO, "TEST_STORY_1_HOWTODEMO");
		storyJSONArray.put(storyJSON);
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
		long storyCreateTime = System.currentTimeMillis();
		historyJSON.put(HistoryJSONEnum.CREATE_TIME, storyCreateTime);
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
		long taskCreateTime = System.currentTimeMillis();
		historyInTaskJSON.put(HistoryJSONEnum.CREATE_TIME, taskCreateTime);
		historyInTaskJSONArray.put(historyInTaskJSON);
		taskJSON.put(TaskJSONEnum.HISTORIES, historyInTaskJSONArray);
		// AttachFile in Task
		JSONArray attachfileInTaskJSONArray = new JSONArray();
		JSONObject attachfileInTaskJSON = new JSONObject();
		attachfileInTaskJSON.put(AttachFileJSONEnum.NAME, "Task01.txt");
		attachfileInTaskJSON.put(AttachFileJSONEnum.CONTENT_TYPE, "application/octet-stream");
		attachfileInTaskJSON.put(AttachFileJSONEnum.BINARY, "VGFzazAx");
		attachfileInTaskJSONArray.put(attachfileInTaskJSON);
		taskJSON.put(TaskJSONEnum.ATTACH_FILES, attachfileInTaskJSONArray);
		// Retrospectives in Sprint
		JSONArray retrospectiveJSONArray = new JSONArray();
		JSONObject goodJSON = new JSONObject();
		goodJSON.put(RetrospectiveJSONEnum.NAME, "TEST_RETROSPECTIVE_GOOD");
		goodJSON.put(RetrospectiveJSONEnum.DESCRIPTION, "TEST_RETROSPECTIVE_GOOD_DESCRIPTION");
		goodJSON.put(RetrospectiveJSONEnum.TYPE, RetrospectiveObject.TYPE_GOOD);
		goodJSON.put(RetrospectiveJSONEnum.STATUS, "new");
		retrospectiveJSONArray.put(goodJSON);
		JSONObject improvementJSON = new JSONObject();
		improvementJSON.put(RetrospectiveJSONEnum.NAME, "TEST_RETROSPECTIVE_IMPROVEMENT");
		improvementJSON.put(RetrospectiveJSONEnum.DESCRIPTION, "TEST_RETROSPECTIVE_IMPROVEMENT_DESCRIPTION");
		improvementJSON.put(RetrospectiveJSONEnum.TYPE, RetrospectiveObject.TYPE_IMPROVEMENT);
		improvementJSON.put(RetrospectiveJSONEnum.STATUS, "closed");
		retrospectiveJSONArray.put(improvementJSON);
		sprintJSON.put(SprintJSONEnum.RETROSPECTIVES, retrospectiveJSONArray);
		// Unplans in Sprint
		JSONArray unplanJSONArray = new JSONArray();
		JSONObject unplanJSON = new JSONObject();
		unplanJSON.put(UnplanJSONEnum.NAME, "TEST_UNPLAN_NAME");
		unplanJSON.put(UnplanJSONEnum.HANDLER, "TEST_ACCOUNT_1");
		unplanJSON.put(UnplanJSONEnum.ESTIMATE, 3);
		unplanJSON.put(UnplanJSONEnum.ACTUAL, 0);
		unplanJSON.put(UnplanJSONEnum.NOTES, "TEST_UNPLAN_NOTES");
		unplanJSON.put(UnplanJSONEnum.STATUS, "assigned");
		unplanJSON.put(UnplanJSONEnum.PARTNERS, new JSONArray());
		JSONArray unplanHistoryJSONArray = new JSONArray();
		JSONObject historyInUnplanJSON = new JSONObject();
		historyInUnplanJSON.put(HistoryJSONEnum.HISTORY_TYPE, "CREATE");
		historyInUnplanJSON.put(HistoryJSONEnum.OLD_VALUE, "");
		historyInUnplanJSON.put(HistoryJSONEnum.NEW_VALUE, "");
		long historyInUnplanCreateTime = System.currentTimeMillis();
		historyInUnplanJSON.put(HistoryJSONEnum.CREATE_TIME, historyInUnplanCreateTime);
		unplanHistoryJSONArray.put(historyInUnplanJSON);
		unplanJSON.put(UnplanJSONEnum.HISTORIES, unplanHistoryJSONArray);
		sprintJSON.put(SprintJSONEnum.UNPLANS, unplanJSONArray);
		// Dropped Stories in Project
		JSONArray droppedStoryJSONArray = new JSONArray();
		JSONObject droppedStoryJSON = new JSONObject();
		droppedStoryJSON.put(StoryJSONEnum.NAME, "TEST_DROPPED_STORY");
		droppedStoryJSON.put(StoryJSONEnum.STATUS, "new");
		droppedStoryJSON.put(StoryJSONEnum.ESTIMATE, 8);
		droppedStoryJSON.put(StoryJSONEnum.IMPORTANCE, 85);
		droppedStoryJSON.put(StoryJSONEnum.VALUE, 13);
		droppedStoryJSON.put(StoryJSONEnum.NOTES, "TEST_DROPPED_STORY_NOTES");
		droppedStoryJSON.put(StoryJSONEnum.HOW_TO_DEMO, "TEST_DROPPED_STORY_HOWTODEMO");
		droppedStoryJSONArray.put(droppedStoryJSON);
		projectJSON.put(ProjectJSONEnum.DROPPED_STORIES, droppedStoryJSONArray);
		// Tag in Dropped Story
		JSONArray tagInDroppedStoryJSONArray = new JSONArray();
		JSONObject tagInDroppedStoryJSON = new JSONObject();
		tagInDroppedStoryJSON.put(TagJSONEnum.NAME, "TEST_TAG_2");
		tagInDroppedStoryJSONArray.put(tagInDroppedStoryJSON);
		droppedStoryJSON.put(StoryJSONEnum.TAGS, tagInDroppedStoryJSONArray);
		// History in Story
		JSONArray historyInDroppedStoryJSONArray = new JSONArray();
		JSONObject historyInDroppedStoryJSON = new JSONObject();
		historyInDroppedStoryJSON.put(HistoryJSONEnum.HISTORY_TYPE, "CREATE");
		historyInDroppedStoryJSON.put(HistoryJSONEnum.OLD_VALUE, "");
		historyInDroppedStoryJSON.put(HistoryJSONEnum.NEW_VALUE, "");
		long droppedStoryCreateTime = System.currentTimeMillis();
		historyInDroppedStoryJSON.put(HistoryJSONEnum.CREATE_TIME, droppedStoryCreateTime);
		historyInDroppedStoryJSONArray.put(historyInDroppedStoryJSON);
		droppedStoryJSON.put(StoryJSONEnum.HISTORIES, historyInDroppedStoryJSONArray);
		// AttachFiles in Story
		JSONArray attachfileInDroppedStoryJSONArray = new JSONArray();
		JSONObject attachfileInDroppedStoryJSON = new JSONObject();
		attachfileInDroppedStoryJSON.put(AttachFileJSONEnum.NAME, "DroppedStory01.txt");
		attachfileInDroppedStoryJSON.put(AttachFileJSONEnum.CONTENT_TYPE, "application/octet-stream");
		attachfileInDroppedStoryJSON.put(AttachFileJSONEnum.BINARY, "RHJvcHBlZFN0b3J5MDE=");
		attachfileInDroppedStoryJSONArray.put(attachfileInDroppedStoryJSON);
		storyJSON.put(StoryJSONEnum.ATTACH_FILES, attachfileInDroppedStoryJSONArray);
		// Tasks
		JSONArray taskInDroppedStoryJSONArray = new JSONArray();
		JSONObject taskInDroppedStoryJSON = new JSONObject();
		taskInDroppedStoryJSON.put(TaskJSONEnum.NAME, "TEST_TASK_IN_DROPPED_STORY");
		taskInDroppedStoryJSON.put(TaskJSONEnum.HANDLER, "TEST_ACCOUNT_1");
		taskInDroppedStoryJSON.put(TaskJSONEnum.ESTIMATE, 5);
		taskInDroppedStoryJSON.put(TaskJSONEnum.REMAIN, 3);
		taskInDroppedStoryJSON.put(TaskJSONEnum.ACTUAL, 0);
		taskInDroppedStoryJSON.put(TaskJSONEnum.NOTES, "TEST_TASK_IN_DROPPED_STORY_NOTES");
		taskInDroppedStoryJSON.put(TaskJSONEnum.STATUS, "assigned");
		taskInDroppedStoryJSONArray.put(taskInDroppedStoryJSON);
		droppedStoryJSON.put(StoryJSONEnum.TASKS, taskInDroppedStoryJSONArray);
		// Partners in Task
		taskInDroppedStoryJSON.put(TaskJSONEnum.PARTNERS, new JSONArray());
		// History in Task
		JSONArray historyInTaskInDroppedStoryJSONArray = new JSONArray();
		JSONObject historyInTaskInDroppedStoryJSON = new JSONObject();
		historyInTaskInDroppedStoryJSON.put(HistoryJSONEnum.HISTORY_TYPE, "CREATE");
		historyInTaskInDroppedStoryJSON.put(HistoryJSONEnum.OLD_VALUE, "");
		historyInTaskInDroppedStoryJSON.put(HistoryJSONEnum.NEW_VALUE, "");
		long taskInDroppedStoryCreateTime = System.currentTimeMillis();
		historyInTaskInDroppedStoryJSON.put(HistoryJSONEnum.CREATE_TIME, taskInDroppedStoryCreateTime);
		historyInTaskInDroppedStoryJSONArray.put(historyInTaskInDroppedStoryJSON);
		taskInDroppedStoryJSON.put(TaskJSONEnum.HISTORIES, historyInTaskInDroppedStoryJSONArray);
		// AttachFile in Task in Dropped Story
		JSONArray attachfileInTaskInDroppedStoryJSONArray = new JSONArray();
		JSONObject attachfileInTaskInDroppedStoryJSON = new JSONObject();
		attachfileInTaskInDroppedStoryJSON.put(AttachFileJSONEnum.NAME, "TaskInDroppedStory01.txt");
		attachfileInTaskInDroppedStoryJSON.put(AttachFileJSONEnum.CONTENT_TYPE, "application/octet-stream");
		attachfileInTaskInDroppedStoryJSON.put(AttachFileJSONEnum.BINARY, "VGFza0luRHJvcHBlZFN0b3J5MDE=");
		attachfileInTaskInDroppedStoryJSONArray.put(attachfileInTaskInDroppedStoryJSON);
		taskInDroppedStoryJSON.put(StoryJSONEnum.ATTACH_FILES, attachfileInTaskInDroppedStoryJSONArray);
		// Dropped Tasks in Project
		JSONArray droppedTaskJSONArray = new JSONArray();
		JSONObject droppedTaskJSON = new JSONObject();
		droppedTaskJSON.put(TaskJSONEnum.NAME, "TEST_DROPPED_TASK");
		droppedTaskJSON.put(TaskJSONEnum.HANDLER, "");
		droppedTaskJSON.put(TaskJSONEnum.ESTIMATE, 8);
		droppedTaskJSON.put(TaskJSONEnum.REMAIN, 8);
		droppedTaskJSON.put(TaskJSONEnum.ACTUAL, 0);
		droppedTaskJSON.put(TaskJSONEnum.NOTES, "TEST_DROPPED_TASK_NOTES");
		droppedTaskJSON.put(TaskJSONEnum.STATUS, "new");
		// Partners in Task
		droppedTaskJSON.put(TaskJSONEnum.PARTNERS, new JSONArray());
		// History in Task
		JSONArray historyInDroppedTaskJSONArray = new JSONArray();
		JSONObject historyInDroppedTaskJSON = new JSONObject();
		historyInDroppedTaskJSON.put(HistoryJSONEnum.HISTORY_TYPE, "CREATE");
		historyInDroppedTaskJSON.put(HistoryJSONEnum.OLD_VALUE, "");
		historyInDroppedTaskJSON.put(HistoryJSONEnum.NEW_VALUE, "");
		long droppedTaskCreateTime = System.currentTimeMillis();
		historyInDroppedTaskJSON.put(HistoryJSONEnum.CREATE_TIME, droppedTaskCreateTime);
		historyInDroppedTaskJSONArray.put(historyInDroppedTaskJSON);
		droppedTaskJSON.put(TaskJSONEnum.HISTORIES, historyInDroppedTaskJSONArray);
		// AttachFile in Task
		JSONArray attachfileInDroppedTaskJSONArray = new JSONArray();
		JSONObject attachfileInDroppedTaskJSON = new JSONObject();
		attachfileInDroppedTaskJSON.put(AttachFileJSONEnum.NAME, "DroppedTask01.txt");
		attachfileInDroppedTaskJSON.put(AttachFileJSONEnum.CONTENT_TYPE, "application/octet-stream");
		attachfileInDroppedTaskJSON.put(AttachFileJSONEnum.BINARY, "RHJvcHBlZFRhc2swMQ==");
		attachfileInDroppedTaskJSONArray.put(attachfileInDroppedTaskJSON);
		droppedTaskJSON.put(TaskJSONEnum.ATTACH_FILES, attachfileInDroppedTaskJSONArray);
		droppedTaskJSONArray.put(droppedTaskJSON);
		projectJSON.put(ProjectJSONEnum.DROPPED_TASKS, droppedTaskJSONArray);
	}
}
