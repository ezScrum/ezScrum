package ntut.csie.ezScrum.restful.dataMigration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.HttpServer;

import ntut.csie.ezScrum.dao.AccountDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.AccountJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.AttachFileJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ExportJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.HistoryJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ProjectJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ReleaseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.RetrospectiveJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ScrumRoleJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.SprintJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.StoryJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TagJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TaskJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.UnplanJSONEnum;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.databaseEnum.RoleEnum;

public class IntegratedRESTfulApiTest extends JerseyTest {
	private Configuration mConfig;
	private ResourceConfig mResourceConfig;
	private Client mClient;
	private HttpServer mHttpServer;
	private static String BASE_URL = "http://localhost:8080/ezScrum/resource/";
	private URI mBaseUri = URI.create(BASE_URL);
	
	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(AccountRESTfulApi.class, ProjectRESTfulApi.class, SprintRESTfulApi.class, StoryRESTfulApi.class,
		        TaskRESTfulApi.class, DroppedStoryRESTfulApi.class, DroppedTaskRESTfulApi.class, ReleaseRESTfulApi.class,
		        RetrospectiveRESTfulApi.class, UnplanRESTfulApi.class, IntegratedRESTfulApi.class);
		return mResourceConfig;
	}

	@Before
	public void setUp() throws Exception {
		// Set Test Mode
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// Start Server
		mHttpServer = JdkHttpServerFactory.createHttpServer(mBaseUri, mResourceConfig, true);

		// Create Client
		mClient = ClientBuilder.newClient();
	}

	@After
	public void tearDown() {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		// Stop Server
		mHttpServer.stop(0);

		// ============= release ==============
		ini = null;
		mHttpServer = null;
		mClient = null;
	}
	
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
		projectJSON.put(ProjectJSONEnum.PROJECT_ROLES, projectRoleJSONArray);
		
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
		sprintJSONArray.put(sprintJSON);
		projectJSON.put(ProjectJSONEnum.SPRINTS, sprintJSONArray);
		
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
		JSONObject tagInStoryJSON = new JSONObject();
		tagInStoryJSON.put(TagJSONEnum.NAME, "TEST_TAG_1");
		tagInStoryJSONArray.put(tagInStoryJSON);
		storyJSON.put(StoryJSONEnum.TAGS, tagInStoryJSONArray);
		// History in Story
		JSONArray historyInStoryJSONArray = new JSONArray();
		JSONObject historyInStoryJSON = new JSONObject();
		historyInStoryJSON.put(HistoryJSONEnum.HISTORY_TYPE, "CREATE");
		historyInStoryJSON.put(HistoryJSONEnum.OLD_VALUE, "");
		historyInStoryJSON.put(HistoryJSONEnum.NEW_VALUE, "");
		long storyCreateTime = System.currentTimeMillis();
		historyInStoryJSON.put(HistoryJSONEnum.CREATE_TIME, storyCreateTime);
		historyInStoryJSONArray.put(historyInStoryJSON);
		storyJSON.put(StoryJSONEnum.HISTORIES, historyInStoryJSONArray);
		// AttachFiles in Story
		JSONArray attachfileInStoryJSONArray = new JSONArray();
		JSONObject attachfileInStoryJSON = new JSONObject();
		attachfileInStoryJSON.put(AttachFileJSONEnum.NAME, "Story01.txt");
		attachfileInStoryJSON.put(AttachFileJSONEnum.CONTENT_TYPE, "application/octet-stream");
		attachfileInStoryJSON.put(AttachFileJSONEnum.BINARY, "U3RvcnkwMQ==");
		attachfileInStoryJSONArray.put(attachfileInStoryJSON);
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
		JSONObject partnerInTaskJSON = new JSONObject();
		partnerInTaskJSON.put(AccountJSONEnum.USERNAME, "TEST_ACCOUNT_1");
		partnerInTaskJSONArray.put(partnerInTaskJSON);
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
		unplanJSONArray.put(unplanJSON);
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
		droppedStoryJSON.put(StoryJSONEnum.ATTACH_FILES, attachfileInDroppedStoryJSONArray);
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
		
		// Call '/dataMigration/projects' API
		Response response = mClient.target(BASE_URL)
		        .path("dataMigration/projects")
		        .request()
		        .post(Entity.text(entityJSON.toString()));
		
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		String responseMessage = responseJSON.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);
		String responseContent = responseJSON.getString(ResponseJSONEnum.JSON_KEY_CONTENT);
		assertEquals(ResponseJSONEnum.SUCCESS_MEESSAGE, responseMessage);
		assertEquals(new JSONObject().toString(), responseContent);
		
		// Assert Accounts
		ArrayList<AccountObject> allAccounts = AccountObject.getAllAccounts();
		ArrayList<AccountObject> accounts = new ArrayList<AccountObject>();
		
		// filter accounts are not admin
		for (AccountObject account : allAccounts) {
			if (!account.isAdmin()) {
				accounts.add(account);
			}
		}
		
		assertEquals(2, accounts.size());
		assertEquals(accountJSON1.getString(AccountJSONEnum.USERNAME), accounts.get(0).getUsername());
		assertEquals(accountJSON1.getString(AccountJSONEnum.NICK_NAME), accounts.get(0).getNickName());
		assertEquals(AccountDAO.getMd5(accountJSON1.getString(AccountJSONEnum.PASSWORD)), accounts.get(0).getPassword());
		assertEquals(accountJSON1.getString(AccountJSONEnum.EMAIL), accounts.get(0).getEmail());
		assertEquals(accountJSON1.getBoolean(AccountJSONEnum.ENABLE), accounts.get(0).getEnable());
		
		assertEquals(accountJSON2.getString(AccountJSONEnum.USERNAME), accounts.get(1).getUsername());
		assertEquals(accountJSON2.getString(AccountJSONEnum.NICK_NAME), accounts.get(1).getNickName());
		assertEquals(AccountDAO.getMd5(accountJSON2.getString(AccountJSONEnum.PASSWORD)), accounts.get(1).getPassword());
		assertEquals(accountJSON2.getString(AccountJSONEnum.EMAIL), accounts.get(1).getEmail());
		assertEquals(accountJSON2.getBoolean(AccountJSONEnum.ENABLE), accounts.get(1).getEnable());
		
		// Assert Projects
		ArrayList<ProjectObject> projects = ProjectObject.getAllProjects();
		assertEquals(1, projects.size());
		ProjectObject project = projects.get(0);
		assertEquals(projectJSON.getString(ProjectJSONEnum.NAME), project.getName());
		assertEquals(projectJSON.getString(ProjectJSONEnum.DISPLAY_NAME), project.getDisplayName());
		assertEquals(projectJSON.getString(ProjectJSONEnum.COMMENT), project.getComment());
		assertEquals(projectJSON.getString(ProjectJSONEnum.PRODUCT_OWNER), project.getManager());
		assertEquals(projectJSON.getInt(ProjectJSONEnum.ATTATCH_MAX_SIZE), project.getAttachFileSize());
		assertEquals(projectJSON.getLong(ProjectJSONEnum.CREATE_TIME), project.getCreateTime());
		
		// Assert ScrumRoles in Project
		// Product Owner
		ScrumRole productOwnerInProject = project.getScrumRole(RoleEnum.ProductOwner);
		assertEquals(productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG), productOwnerInProject.getAccessProductBacklog());
		assertEquals(productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN), productOwnerInProject.getAccessSprintPlan());
		assertEquals(productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD), productOwnerInProject.getAccessTaskBoard());
		assertEquals(productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG), productOwnerInProject.getAccessSprintBacklog());
		assertEquals(productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN), productOwnerInProject.getAccessReleasePlan());
		assertEquals(productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE), productOwnerInProject.getAccessRetrospective());
		assertEquals(productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLAN), productOwnerInProject.getAccessUnplanItem());
		assertEquals(productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT), productOwnerInProject.getAccessReport());
		assertEquals(productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT), productOwnerInProject.getAccessEditProject());
	
		// Scrum Master
		ScrumRole scrumMasterInProject = project.getScrumRole(RoleEnum.ScrumMaster);
		assertEquals(scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG), scrumMasterInProject.getAccessProductBacklog());
		assertEquals(scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN), scrumMasterInProject.getAccessSprintPlan());
		assertEquals(scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD), scrumMasterInProject.getAccessTaskBoard());
		assertEquals(scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG), scrumMasterInProject.getAccessSprintBacklog());
		assertEquals(scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN), scrumMasterInProject.getAccessReleasePlan());
		assertEquals(scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE), scrumMasterInProject.getAccessRetrospective());
		assertEquals(scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLAN), scrumMasterInProject.getAccessUnplanItem());
		assertEquals(scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT), scrumMasterInProject.getAccessReport());
		assertEquals(scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT), scrumMasterInProject.getAccessEditProject());
		
		// Scrum Team
		ScrumRole scrumTeamInProject = project.getScrumRole(RoleEnum.ScrumTeam);
		assertEquals(scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG), scrumTeamInProject.getAccessProductBacklog());
		assertEquals(scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN), scrumTeamInProject.getAccessSprintPlan());
		assertEquals(scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD), scrumTeamInProject.getAccessTaskBoard());
		assertEquals(scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG), scrumTeamInProject.getAccessSprintBacklog());
		assertEquals(scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN), scrumTeamInProject.getAccessReleasePlan());
		assertEquals(scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE), scrumTeamInProject.getAccessRetrospective());
		assertEquals(scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLAN), scrumTeamInProject.getAccessUnplanItem());
		assertEquals(scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT), scrumTeamInProject.getAccessReport());
		assertEquals(scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT), scrumTeamInProject.getAccessEditProject());
		
		// Stakeholder
		ScrumRole stakeholderInProject = project.getScrumRole(RoleEnum.Stakeholder);
		assertEquals(stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG), stakeholderInProject.getAccessProductBacklog());
		assertEquals(stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN), stakeholderInProject.getAccessSprintPlan());
		assertEquals(stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD), stakeholderInProject.getAccessTaskBoard());
		assertEquals(stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG), stakeholderInProject.getAccessSprintBacklog());
		assertEquals(stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN), stakeholderInProject.getAccessReleasePlan());
		assertEquals(stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE), stakeholderInProject.getAccessRetrospective());
		assertEquals(stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLAN), stakeholderInProject.getAccessUnplanItem());
		assertEquals(stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT), stakeholderInProject.getAccessReport());
		assertEquals(stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT), stakeholderInProject.getAccessEditProject());
		
		// Guest
		ScrumRole guestInProject = project.getScrumRole(RoleEnum.Guest);
		assertEquals(guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG), guestInProject.getAccessProductBacklog());
		assertEquals(guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN), guestInProject.getAccessSprintPlan());
		assertEquals(guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD), guestInProject.getAccessTaskBoard());
		assertEquals(guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG), guestInProject.getAccessSprintBacklog());
		assertEquals(guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN), guestInProject.getAccessReleasePlan());
		assertEquals(guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE), guestInProject.getAccessRetrospective());
		assertEquals(guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLAN), guestInProject.getAccessUnplanItem());
		assertEquals(guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT), guestInProject.getAccessReport());
		assertEquals(guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT), guestInProject.getAccessEditProject());
		
		// Assert ProjectRoles in Project
		ArrayList<AccountObject> projectMembers = project.getProjectMembers();
		assertEquals(1, projectMembers.size());
		assertEquals(projectRoleJSONArray.getJSONObject(0).getString(AccountJSONEnum.USERNAME), projectMembers.get(0).getUsername());
		assertEquals(projectRoleJSONArray.getJSONObject(0).getString(ScrumRoleJSONEnum.ROLE), projectMembers.get(0).getRoles().get(project.getName()).getScrumRole().getRoleName());
	
		// Assert Tags in Project
		ArrayList<TagObject> tagsInProject = project.getTags();
		assertEquals(2, tagsInProject.size());
		assertEquals(projectTagJSON1.getString(TagJSONEnum.NAME), tagsInProject.get(0).getName());
		assertEquals(projectTagJSON2.getString(TagJSONEnum.NAME), tagsInProject.get(1).getName());
		
		// Assert Releases in Project
		ArrayList<ReleaseObject> releasesInProject = project.getReleases();
		assertEquals(1, releasesInProject.size());
		assertEquals(releaseJSON.getString(ReleaseJSONEnum.NAME), releasesInProject.get(0).getName());
		assertEquals(releaseJSON.getString(ReleaseJSONEnum.DESCRIPTION), releasesInProject.get(0).getDescription());
		assertEquals(releaseJSON.getString(ReleaseJSONEnum.START_DATE), releasesInProject.get(0).getStartDateString());
		assertEquals(releaseJSON.getString(ReleaseJSONEnum.DUE_DATE), releasesInProject.get(0).getDueDateString());
		
		// Assert Sprints in Project
		ArrayList<SprintObject> sprintsInProject = project.getSprints();
		assertEquals(1, sprintsInProject.size());
		SprintObject sprint = sprintsInProject.get(0);
		assertEquals(sprintJSON.getString(SprintJSONEnum.GOAL), sprint.getGoal());
		assertEquals(sprintJSON.getInt(SprintJSONEnum.INTERVAL), sprint.getInterval());
		assertEquals(sprintJSON.getInt(SprintJSONEnum.TEAM_SIZE), sprint.getTeamSize());
		assertEquals(sprintJSON.getInt(SprintJSONEnum.AVAILABLE_HOURS), sprint.getAvailableHours());
		assertEquals(sprintJSON.getInt(SprintJSONEnum.FOCUS_FACTOR), sprint.getFocusFactor());
		assertEquals(sprintJSON.getString(SprintJSONEnum.START_DATE), sprint.getStartDateString());
		assertEquals(sprintJSON.getString(SprintJSONEnum.DUE_DATE), sprint.getDueDateString());
		assertEquals(sprintJSON.getString(SprintJSONEnum.DEMO_DATE), sprint.getDemoDateString());
		assertEquals(sprintJSON.getString(SprintJSONEnum.DEMO_PLACE), sprint.getDemoPlace());
		assertEquals(sprintJSON.getString(SprintJSONEnum.DAILY_INFO), sprint.getDailyInfo());
		
		// Assert Stories in Sprint
		ArrayList<StoryObject> storiesInSprint = sprint.getStories();
		assertEquals(1, storiesInSprint.size());
		StoryObject story = storiesInSprint.get(0);
		assertEquals(storyJSON.getString(StoryJSONEnum.NAME), story.getName());
		assertEquals(storyJSON.getString(StoryJSONEnum.STATUS), story.getStatusString());
		assertEquals(storyJSON.getInt(StoryJSONEnum.ESTIMATE), story.getEstimate());
		assertEquals(storyJSON.getInt(StoryJSONEnum.IMPORTANCE), story.getImportance());
		assertEquals(storyJSON.getInt(StoryJSONEnum.VALUE), story.getValue());
		assertEquals(storyJSON.getString(StoryJSONEnum.NOTES), story.getNotes());
		assertEquals(storyJSON.getString(StoryJSONEnum.HOW_TO_DEMO), story.getHowToDemo());
		
		// Assert Tags in Story
		ArrayList<TagObject> tagsInStory = story.getTags();
		assertEquals(1, tagsInStory.size());
		assertEquals(tagInStoryJSON.getString(TagJSONEnum.NAME), tagsInStory.get(0).getName());
		
		// Assert Histories in Story
		ArrayList<HistoryObject> historiesInStory = story.getHistories();
		assertEquals(1, historiesInStory.size());
		assertEquals(HistoryObject.TYPE_CREATE, historiesInStory.get(0).getHistoryType());
		assertEquals(historyInStoryJSON.getString(HistoryJSONEnum.OLD_VALUE), historiesInStory.get(0).getOldValue());
		assertEquals(historyInStoryJSON.getString(HistoryJSONEnum.NEW_VALUE), historiesInStory.get(0).getNewValue());
		assertEquals(historyInStoryJSON.getLong(HistoryJSONEnum.CREATE_TIME), historiesInStory.get(0).getCreateTime());
		
		// Assert Attach Files in Story
		ArrayList<AttachFileObject> attachFilesInStory = story.getAttachFiles();
		assertEquals(1, attachFilesInStory.size());
		assertEquals(attachfileInStoryJSON.getString(AttachFileJSONEnum.NAME), attachFilesInStory.get(0).getName());
		assertEquals(attachfileInStoryJSON.getString(AttachFileJSONEnum.CONTENT_TYPE), attachFilesInStory.get(0).getContentType());
		assertEquals(story.getId(), attachFilesInStory.get(0).getIssueId());
		assertEquals(IssueTypeEnum.TYPE_STORY, attachFilesInStory.get(0).getIssueType());
		
		// Assert Tasks in Story
		ArrayList<TaskObject> tasksInStory = story.getTasks();
		assertEquals(1, tasksInStory.size());
		TaskObject task = tasksInStory.get(0);
		assertEquals(taskJSON.getString(TaskJSONEnum.NAME), task.getName());
		assertEquals(taskJSON.getString(TaskJSONEnum.HANDLER), task.getHandler().getUsername());
		assertEquals(taskJSON.getInt(TaskJSONEnum.ESTIMATE), task.getEstimate());
		assertEquals(taskJSON.getInt(TaskJSONEnum.REMAIN), task.getRemains());
		assertEquals(taskJSON.getInt(TaskJSONEnum.ACTUAL), task.getActual());
		assertEquals(taskJSON.getString(TaskJSONEnum.NOTES), task.getNotes());
		assertEquals(taskJSON.getString(TaskJSONEnum.STATUS), task.getStatusString());
		
		// Assert Partners in Task
		ArrayList<AccountObject> partnersInTask = task.getPartners();
		assertEquals(1, partnersInTask.size());
		assertEquals(partnerInTaskJSON.getString(AccountJSONEnum.USERNAME), partnersInTask.get(0).getUsername());
		
		// Assert Histories in Task
		ArrayList<HistoryObject> historiesInTask = task.getHistories();
		assertEquals(1, historiesInTask.size());
		assertEquals(HistoryObject.TYPE_CREATE, historiesInTask.get(0).getHistoryType());
		assertEquals(historyInTaskJSON.getString(HistoryJSONEnum.OLD_VALUE), historiesInTask.get(0).getOldValue());
		assertEquals(historyInTaskJSON.getString(HistoryJSONEnum.NEW_VALUE), historiesInTask.get(0).getNewValue());
		assertEquals(historyInTaskJSON.getLong(HistoryJSONEnum.CREATE_TIME), historiesInTask.get(0).getCreateTime());
		
		// Assert Attach Files in Task
		ArrayList<AttachFileObject> attachFilesInTask = task.getAttachFiles();
		assertEquals(1, attachFilesInTask.size());
		assertEquals(attachfileInTaskJSON.getString(AttachFileJSONEnum.NAME), attachFilesInTask.get(0).getName());
		assertEquals(attachfileInTaskJSON.getString(AttachFileJSONEnum.CONTENT_TYPE), attachFilesInTask.get(0).getContentType());
		assertEquals(task.getId(), attachFilesInTask.get(0).getIssueId());
		assertEquals(IssueTypeEnum.TYPE_TASK, attachFilesInTask.get(0).getIssueType());
		
		// Assert Retrospectives in Sprint
		ArrayList<RetrospectiveObject> goodsInSprint = sprint.getGoods();
		assertEquals(1, goodsInSprint.size());
		assertEquals(goodJSON.getString(RetrospectiveJSONEnum.NAME), goodsInSprint.get(0).getName());
		assertEquals(goodJSON.getString(RetrospectiveJSONEnum.DESCRIPTION), goodsInSprint.get(0).getDescription());
		assertEquals(goodJSON.getString(RetrospectiveJSONEnum.TYPE), goodsInSprint.get(0).getType());
		assertEquals(goodJSON.getString(RetrospectiveJSONEnum.STATUS), goodsInSprint.get(0).getStatus());
		ArrayList<RetrospectiveObject> improvementsInSprint = sprint.getImprovements();
		assertEquals(1, improvementsInSprint.size());
		assertEquals(improvementJSON.getString(RetrospectiveJSONEnum.NAME), improvementsInSprint.get(0).getName());
		assertEquals(improvementJSON.getString(RetrospectiveJSONEnum.DESCRIPTION), improvementsInSprint.get(0).getDescription());
		assertEquals(improvementJSON.getString(RetrospectiveJSONEnum.TYPE), improvementsInSprint.get(0).getType());
		assertEquals(improvementJSON.getString(RetrospectiveJSONEnum.STATUS), improvementsInSprint.get(0).getStatus());
		
		// Assert Unplans in Sprint
		ArrayList<UnplanObject> unplansInSprint = sprint.getUnplans();
		assertEquals(1, unplansInSprint.size());
		UnplanObject unplan = unplansInSprint.get(0);
		assertEquals(unplanJSON.getString(UnplanJSONEnum.NAME), unplan.getName());
		assertEquals(unplanJSON.getString(UnplanJSONEnum.HANDLER), unplan.getHandler().getUsername());
		assertEquals(unplanJSON.getInt(UnplanJSONEnum.ESTIMATE), unplan.getEstimate());
		assertEquals(unplanJSON.getInt(UnplanJSONEnum.ACTUAL), unplan.getActual());
		assertEquals(unplanJSON.getString(UnplanJSONEnum.NOTES), unplan.getNotes());
		assertEquals(unplanJSON.getString(UnplanJSONEnum.STATUS), unplan.getStatusString());
		
		// Assert Partners in Unplan
		ArrayList<AccountObject> partnersInUnplan = unplan.getPartners();
		assertEquals(0, partnersInUnplan.size());
		
		// Assert Histories in Unplan
		ArrayList<HistoryObject> historiesInUnplan = unplan.getHistories();
		assertEquals(1, historiesInUnplan.size());
		assertEquals(HistoryObject.TYPE_CREATE, historiesInUnplan.get(0).getHistoryType());
		assertEquals(historyInUnplanJSON.getString(HistoryJSONEnum.OLD_VALUE), historiesInUnplan.get(0).getOldValue());
		assertEquals(historyInUnplanJSON.getString(HistoryJSONEnum.NEW_VALUE), historiesInUnplan.get(0).getNewValue());
		assertEquals(historyInUnplanJSON.getLong(HistoryJSONEnum.CREATE_TIME), historiesInUnplan.get(0).getCreateTime());
		
		// Assert Dropped Stories in Project
		ArrayList<StoryObject> droppedStoriesInProject = project.getDroppedStories();
		assertEquals(1, droppedStoriesInProject.size());
		StoryObject droppedStory = droppedStoriesInProject.get(0);
		assertEquals(droppedStoryJSON.getString(StoryJSONEnum.NAME), droppedStory.getName());
		assertEquals(droppedStoryJSON.getString(StoryJSONEnum.STATUS), droppedStory.getStatusString());
		assertEquals(droppedStoryJSON.getInt(StoryJSONEnum.ESTIMATE), droppedStory.getEstimate());
		assertEquals(droppedStoryJSON.getInt(StoryJSONEnum.IMPORTANCE), droppedStory.getImportance());
		assertEquals(droppedStoryJSON.getInt(StoryJSONEnum.VALUE), droppedStory.getValue());
		assertEquals(droppedStoryJSON.getString(StoryJSONEnum.NOTES), droppedStory.getNotes());
		assertEquals(droppedStoryJSON.getString(StoryJSONEnum.HOW_TO_DEMO), droppedStory.getHowToDemo());
		
		// Assert Tags in Dropped Story
		ArrayList<TagObject> tagsInDroppedStory = droppedStory.getTags();
		assertEquals(1, tagsInDroppedStory.size());
		assertEquals(tagInDroppedStoryJSON.getString(TagJSONEnum.NAME), tagsInDroppedStory.get(0).getName());
		
		// Assert Histories Dropped Story
		ArrayList<HistoryObject> historiesInDroppedStory = droppedStory.getHistories();
		assertEquals(1, historiesInDroppedStory.size());
		assertEquals(HistoryObject.TYPE_CREATE, historiesInDroppedStory.get(0).getHistoryType());
		assertEquals(historyInDroppedStoryJSON.getString(HistoryJSONEnum.OLD_VALUE), historiesInDroppedStory.get(0).getOldValue());
		assertEquals(historyInDroppedStoryJSON.getString(HistoryJSONEnum.NEW_VALUE), historiesInDroppedStory.get(0).getNewValue());
		assertEquals(historyInDroppedStoryJSON.getLong(HistoryJSONEnum.CREATE_TIME), historiesInDroppedStory.get(0).getCreateTime());
		
		// Assert Attach Files in Dropped Story
		ArrayList<AttachFileObject> attachFilesInDroppedStory = droppedStory.getAttachFiles();
		assertEquals(1, attachFilesInDroppedStory.size());
		assertEquals(attachfileInDroppedStoryJSON.getString(AttachFileJSONEnum.NAME), attachFilesInDroppedStory.get(0).getName());
		assertEquals(attachfileInDroppedStoryJSON.getString(AttachFileJSONEnum.CONTENT_TYPE), attachFilesInDroppedStory.get(0).getContentType());
		assertEquals(droppedStory.getId(), attachFilesInDroppedStory.get(0).getIssueId());
		assertEquals(IssueTypeEnum.TYPE_STORY, attachFilesInDroppedStory.get(0).getIssueType());
		
		// Assert Tasks in Dropped Story
		ArrayList<TaskObject> tasksInDroppedStory = droppedStory.getTasks();
		assertEquals(1, tasksInDroppedStory.size());
		TaskObject taskInDroppedStory = tasksInDroppedStory.get(0);
		assertEquals(taskInDroppedStoryJSON.getString(TaskJSONEnum.NAME), taskInDroppedStory.getName());
		assertEquals(taskInDroppedStoryJSON.getString(TaskJSONEnum.HANDLER), taskInDroppedStory.getHandler().getUsername());
		assertEquals(taskInDroppedStoryJSON.getInt(TaskJSONEnum.ESTIMATE), taskInDroppedStory.getEstimate());
		assertEquals(taskInDroppedStoryJSON.getInt(TaskJSONEnum.REMAIN), taskInDroppedStory.getRemains());
		assertEquals(taskInDroppedStoryJSON.getInt(TaskJSONEnum.ACTUAL), taskInDroppedStory.getActual());
		assertEquals(taskInDroppedStoryJSON.getString(TaskJSONEnum.NOTES), taskInDroppedStory.getNotes());
		assertEquals(taskInDroppedStoryJSON.getString(TaskJSONEnum.STATUS), taskInDroppedStory.getStatusString());
		
		// Assert Partners in Task in Dropped Story
		ArrayList<AccountObject> partnersInTaskInDroppedStory = taskInDroppedStory.getPartners();
		assertEquals(0, partnersInTaskInDroppedStory.size());
		
		// Assert Histories in Task in Dropped Story
		ArrayList<HistoryObject> historiesInTaskInDroppedStory = taskInDroppedStory.getHistories();
		assertEquals(1, historiesInTaskInDroppedStory.size());
		assertEquals(HistoryObject.TYPE_CREATE, historiesInTaskInDroppedStory.get(0).getHistoryType());
		assertEquals(historyInTaskInDroppedStoryJSON.getString(HistoryJSONEnum.OLD_VALUE), historiesInTaskInDroppedStory.get(0).getOldValue());
		assertEquals(historyInTaskInDroppedStoryJSON.getString(HistoryJSONEnum.NEW_VALUE), historiesInTaskInDroppedStory.get(0).getNewValue());
		assertEquals(historyInTaskInDroppedStoryJSON.getLong(HistoryJSONEnum.CREATE_TIME), historiesInTaskInDroppedStory.get(0).getCreateTime());
		
		// Assert Attach Files in Task in Dropped Story
		ArrayList<AttachFileObject> attachFilesInTaskInDroppedStory = taskInDroppedStory.getAttachFiles();
		assertEquals(1, attachFilesInTaskInDroppedStory.size());
		assertEquals(attachfileInTaskInDroppedStoryJSON.getString(AttachFileJSONEnum.NAME), attachFilesInTaskInDroppedStory.get(0).getName());
		assertEquals(attachfileInTaskInDroppedStoryJSON.getString(AttachFileJSONEnum.CONTENT_TYPE), attachFilesInTaskInDroppedStory.get(0).getContentType());
		assertEquals(taskInDroppedStory.getId(), attachFilesInTaskInDroppedStory.get(0).getIssueId());
		assertEquals(IssueTypeEnum.TYPE_TASK, attachFilesInTaskInDroppedStory.get(0).getIssueType());
		
		// Assert Dropped Tasks in Project
		ArrayList<TaskObject> droppedTasksInProject = project.getDroppedTasks();
		assertEquals(1, droppedTasksInProject.size());
		TaskObject droppedTask = droppedTasksInProject.get(0);
		assertEquals(droppedTaskJSON.getString(TaskJSONEnum.NAME), droppedTask.getName());
		assertNull(droppedTask.getHandler());
		assertEquals(droppedTaskJSON.getInt(TaskJSONEnum.ESTIMATE), droppedTask.getEstimate());
		assertEquals(droppedTaskJSON.getInt(TaskJSONEnum.REMAIN), droppedTask.getRemains());
		assertEquals(droppedTaskJSON.getInt(TaskJSONEnum.ACTUAL), droppedTask.getActual());
		assertEquals(droppedTaskJSON.getString(TaskJSONEnum.NOTES), droppedTask.getNotes());
		assertEquals(droppedTaskJSON.getString(TaskJSONEnum.STATUS), droppedTask.getStatusString());
		
		// Assert Partners in Dropped Task
		ArrayList<AccountObject> partnersInDroppedTask = droppedTask.getPartners();
		assertEquals(0, partnersInDroppedTask.size());
		
		// Assert Histories in Dropped Task
		ArrayList<HistoryObject> historiesInDroppedTask = droppedTask.getHistories();
		assertEquals(1, historiesInDroppedTask.size());
		assertEquals(HistoryObject.TYPE_CREATE, historiesInDroppedTask.get(0).getHistoryType());
		assertEquals(historyInDroppedTaskJSON.getString(HistoryJSONEnum.OLD_VALUE), historiesInDroppedTask.get(0).getOldValue());
		assertEquals(historyInDroppedTaskJSON.getString(HistoryJSONEnum.NEW_VALUE), historiesInDroppedTask.get(0).getNewValue());
		assertEquals(historyInDroppedTaskJSON.getLong(HistoryJSONEnum.CREATE_TIME), historiesInDroppedTask.get(0).getCreateTime());
		
		// Assert Attach Files in Dropped Task
		ArrayList<AttachFileObject> attachFilesInDroppedTask = droppedTask.getAttachFiles();
		assertEquals(1, attachFilesInDroppedTask.size());
		assertEquals(attachfileInDroppedTaskJSON.getString(AttachFileJSONEnum.NAME), attachFilesInDroppedTask.get(0).getName());
		assertEquals(attachfileInDroppedTaskJSON.getString(AttachFileJSONEnum.CONTENT_TYPE), attachFilesInDroppedTask.get(0).getContentType());
		assertEquals(droppedTask.getId(), attachFilesInDroppedTask.get(0).getIssueId());
		assertEquals(IssueTypeEnum.TYPE_TASK, attachFilesInDroppedTask.get(0).getIssueType());
	}
	
	public void testImportExistedProjectJSON() throws JSONException {
		// Test Data
		JSONObject entityJSON = new JSONObject();
		//// Accounts
		JSONArray accountJSONArray = new JSONArray();
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
		projectJSON.put(ProjectJSONEnum.PROJECT_ROLES, projectRoleJSONArray);
		
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
		sprintJSONArray.put(sprintJSON);
		projectJSON.put(ProjectJSONEnum.SPRINTS, sprintJSONArray);
		
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
		JSONObject tagInStoryJSON = new JSONObject();
		tagInStoryJSON.put(TagJSONEnum.NAME, "TEST_TAG_1");
		tagInStoryJSONArray.put(tagInStoryJSON);
		storyJSON.put(StoryJSONEnum.TAGS, tagInStoryJSONArray);
		// History in Story
		JSONArray historyInStoryJSONArray = new JSONArray();
		JSONObject historyInStoryJSON = new JSONObject();
		historyInStoryJSON.put(HistoryJSONEnum.HISTORY_TYPE, "CREATE");
		historyInStoryJSON.put(HistoryJSONEnum.OLD_VALUE, "");
		historyInStoryJSON.put(HistoryJSONEnum.NEW_VALUE, "");
		long storyCreateTime = System.currentTimeMillis();
		historyInStoryJSON.put(HistoryJSONEnum.CREATE_TIME, storyCreateTime);
		historyInStoryJSONArray.put(historyInStoryJSON);
		storyJSON.put(StoryJSONEnum.HISTORIES, historyInStoryJSONArray);
		// AttachFiles in Story
		JSONArray attachfileInStoryJSONArray = new JSONArray();
		JSONObject attachfileInStoryJSON = new JSONObject();
		attachfileInStoryJSON.put(AttachFileJSONEnum.NAME, "Story01.txt");
		attachfileInStoryJSON.put(AttachFileJSONEnum.CONTENT_TYPE, "application/octet-stream");
		attachfileInStoryJSON.put(AttachFileJSONEnum.BINARY, "U3RvcnkwMQ==");
		attachfileInStoryJSONArray.put(attachfileInStoryJSON);
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
		JSONObject partnerInTaskJSON = new JSONObject();
		partnerInTaskJSON.put(AccountJSONEnum.USERNAME, "TEST_ACCOUNT_1");
		partnerInTaskJSONArray.put(partnerInTaskJSON);
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
		unplanJSONArray.put(unplanJSON);
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
		droppedStoryJSON.put(StoryJSONEnum.ATTACH_FILES, attachfileInDroppedStoryJSONArray);
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
		
		// Call '/dataMigration/projects' API
		Response response = mClient.target(BASE_URL)
		        .path("dataMigration/projects")
		        .request()
		        .post(Entity.text(entityJSON.toString()));
		
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		String responseMessage = responseJSON.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);
		String responseContent = responseJSON.getString(ResponseJSONEnum.JSON_KEY_CONTENT);
		assertEquals(ResponseJSONEnum.SUCCESS_MEESSAGE, responseMessage);
		assertEquals(new JSONObject().toString(), responseContent);
		
		// Import existed project
		// Call '/dataMigration/projects' API
		response = mClient.target(BASE_URL)
		        .path("dataMigration/projects")
		        .request()
		        .post(Entity.text(entityJSON.toString()));
		
		// Assert
		ProjectObject latestProject = ProjectObject.getAllProjects().get(1);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertTrue(latestProject != null);
		assertTrue(!latestProject.getName().equals("TEST_PROJECT_1"));
		assertTrue(!latestProject.getDisplayName().equals("TEST_PROJECT_1"));
	}
}
