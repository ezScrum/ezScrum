package ntut.csie.ezScrum.restful.mobilel.service;

import static org.junit.Assert.assertEquals;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.restful.mobile.service.SprintBacklogWebService;
import ntut.csie.ezScrum.restful.mobile.support.ConvertSprintBacklog;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.jcis.account.core.LogonException;

public class SprintBacklogWebServiceTest {
	private int mProjectCount = 1;
	private int mReleaseCount = 1;
	private int mSprintCount = 3;
	private int mStoryCount = 3;
	private int mStoryEstimation = 5;
	private CreateProject mCP;
	private CreateRelease mCR;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private ProjectObject mProject;
	private SprintBacklogWebService mSprintBacklogWebService;
	private Configuration mConfig;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		mConfig = new Configuration(new UserSession(AccountObject.get("admin")));
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		mCP = new CreateProject(mProjectCount);
		mCP.exeCreateForDb(); // 新增一測試專案

		mCR = new CreateRelease(mReleaseCount, mCP);
		mCR.exe();
		
		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();
		
		mASTS = new AddStoryToSprint(mStoryCount, mStoryEstimation, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mASTS.exe();

		mProject = mCP.getAllProjects().get(0);
	}

	@After
	public void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mConfig.setTestMode(false);
		mConfig.save();

		// release
		mCP = null;
		mCR = null;
		mCS = null;
		mASTS = null;
		mConfig = null;
	}
	
	@Test
	public void testGetStoriesIdJsonStringInSprint() throws LogonException, JSONException {
		// get sprint
		SprintObject sprint = mCS.getSprints().get(0);
		// create account
		AccountObject account = new AccountObject("TEST_ACCOUNT");
		account.setPassword("TEST_ACCOUNT")
		        .setEmail("ezscrum@scrum.tw")
		        .setNickName("TEST_NICK_NAME")
		        .setEnable(true)
		        .save();

		// get Expected String
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(mProject);
		String expectedString = ConvertSprintBacklog.getStoriesIdJsonStringInSprint(sprintBacklogHelper.getStoriesSortedByIdInSprint());
		// get Actual String
		mSprintBacklogWebService = new SprintBacklogWebService(account.getUsername(), account.getPassword(), mProject.getName(), sprint.getId());
		String actualString = mSprintBacklogWebService.getStoriesIdJsonStringInSprint();
		// assert
		assertEquals(expectedString, actualString);
	}

	@Test
	public void testGetTasksIdJsonStringInStory() throws LogonException, JSONException {
		// get sprint
		SprintObject sprint = mCS.getSprints().get(0);
		// get story
		StoryObject story = sprint.getStories().get(0);
		// create account
		AccountObject account = new AccountObject("TEST_ACCOUNT");
		account.setPassword("TEST_ACCOUNT")
		        .setEmail("ezscrum@scrum.tw")
		        .setNickName("TEST_NICK_NAME")
		        .setEnable(true)
		        .save();

		// get Actual String
		mSprintBacklogWebService = new SprintBacklogWebService(account.getUsername(), account.getPassword(), mProject.getName(), sprint.getId());
		String actualString = mSprintBacklogWebService.getTasksIdJsonStringInStory(story.getId());
		// get Expected String
		String expectedString = ConvertSprintBacklog.getTasksIdJsonStringInStory(story.getId(), story.getTasks());
		// assert
		assertEquals(expectedString, actualString);
	}

	@Test
	public void testGetTaskHsitoryJsonString() throws Exception {
		// add task to story
		AddTaskToStory ATTS = new AddTaskToStory(5, 13, mASTS, mCP);
		ATTS.exe();
		
		// get sprint
		SprintObject sprint = mCS.getSprints().get(0);
		// get story
		StoryObject story = sprint.getStories().get(0);
		// get task
		TaskObject task = story.getTasks().get(0);
		// create account
		AccountObject account = new AccountObject("TEST_ACCOUNT");
		account.setPassword("TEST_ACCOUNT")
		        .setEmail("ezscrum@scrum.tw")
		        .setNickName("TEST_NICK_NAME")
		        .setEnable(true)
		        .save();
		
		// get Actual String
		mSprintBacklogWebService = new SprintBacklogWebService(account.getUsername(), account.getPassword(), mProject.getName(), sprint.getId());
		String actualString = mSprintBacklogWebService.getTaskHsitoryJsonString(task.getId());
		// get Expected String
		String expectedString = ConvertSprintBacklog.getTaskHistoriesJsonString(task.getHistories());
		// assert
		assertEquals(expectedString, actualString);
	}

	@Test
	public void testGetTaskJsonString() throws Exception {
		// add task to story
		AddTaskToStory ATTS = new AddTaskToStory(5, 13, mASTS, mCP);
		ATTS.exe();

		// get sprint
		SprintObject sprint = mCS.getSprints().get(0);
		// get story
		StoryObject story = sprint.getStories().get(0);
		// get task
		TaskObject task = story.getTasks().get(0);
		// create account
		AccountObject account = new AccountObject("TEST_ACCOUNT");
		account.setPassword("TEST_ACCOUNT")
		        .setEmail("ezscrum@scrum.tw")
		        .setNickName("TEST_NICK_NAME")
		        .setEnable(true)
		        .save();

		// get Actual String
		mSprintBacklogWebService = new SprintBacklogWebService(account.getUsername(), account.getPassword(), mProject.getName(), sprint.getId());
		String actualString = mSprintBacklogWebService.getTaskJsonString(task.getId());
		// get Expected String
		String expectedString = ConvertSprintBacklog.getTaskJsonString(task);
		// assert
		assertEquals(expectedString, actualString);
	}

	@Test
	public void testGetSprintBacklogJsonString() throws Exception {
		// add task to story
		AddTaskToStory ATTS = new AddTaskToStory(5, 13, mASTS, mCP);
		ATTS.exe();

		// get sprint
		SprintObject sprint = mCS.getSprints().get(0);
		// create account
		AccountObject account = new AccountObject("TEST_ACCOUNT");
		account.setPassword("TEST_ACCOUNT")
		        .setEmail("ezscrum@scrum.tw")
		        .setNickName("TEST_NICK_NAME")
		        .setEnable(true)
		        .save();

		// get Actual String
		mSprintBacklogWebService = new SprintBacklogWebService(account.getUsername(), account.getPassword(), mProject.getName(), sprint.getId());
		String actualString = mSprintBacklogWebService.getSprintBacklogJsonString();
		// get Expected String
		String expectedString = ConvertSprintBacklog.getSprintBacklogJsonString(sprint);
		// assert
		assertEquals(expectedString, actualString);
	}

	@Test
	public void testGetTaskboardJsonString() throws Exception {
		// add task to story
		AddTaskToStory ATTS = new AddTaskToStory(5, 13, mASTS, mCP);
		ATTS.exe();

		// get sprint
		SprintObject sprint = mCS.getSprints().get(0);
		// create account
		AccountObject account = new AccountObject("TEST_ACCOUNT");
		account.setPassword("TEST_ACCOUNT")
		        .setEmail("ezscrum@scrum.tw")
		        .setNickName("TEST_NICK_NAME")
		        .setEnable(true)
		        .save();

		// get Actual String
		mSprintBacklogWebService = new SprintBacklogWebService(account.getUsername(), account.getPassword(), mProject.getName(), sprint.getId());
		String actualString = mSprintBacklogWebService.getTaskboardJsonString();
		// get Expected String
		String expectedString = new ConvertSprintBacklog().getTaskboardJsonString(sprint);
		// assert
		assertEquals(expectedString, actualString);
	}
}
