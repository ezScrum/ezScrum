package ntut.csie.ezScrum.restful.mobilel.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.restful.mobile.service.StoryWebService;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.databasEnum.StoryEnum;
import ntut.csie.ezScrum.web.databasEnum.TaskEnum;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StoryWebServiceTest {
	private int mProjectCount = 1;
	private int mSprintCount = 1;
	private int mStoryCount = 5;
	private int mTaskCount = 3;
	private StoryWebService mStoryWebService;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
	private Configuration mConfig;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		// 新增Sprint
		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();

		// 新增Story
		mASTS = new AddStoryToSprint(mStoryCount, 1, mCS, mCP,
				CreateProductBacklog.COLUMN_TYPE_EST);
		mASTS.exe();
		
		mATTS = new AddTaskToStory(mTaskCount, 8, mASTS, mCP);
		mATTS.exe();

		// create account
		String TEST_ACCOUNT_NAME = "TEST_ACCOUNT_1";
		String TEST_ACCOUNT_NICKNAME = "TEST_ACCOUNT_NICKNAME_1";
		String TEST_ACCOUNT_PASSWORD = "TEST_ACCOUNT_PASSWORD_1";
		String TEST_ACCOUNT_EMAIL = "TEST_ACCOUNT_EMAIL_1";

		AccountObject account = new AccountObject(TEST_ACCOUNT_NAME);
		account.setNickName(TEST_ACCOUNT_NICKNAME);
		account.setPassword(TEST_ACCOUNT_PASSWORD);
		account.setEmail(TEST_ACCOUNT_EMAIL);
		account.setEnable(true);
		account.save();
		account.reload();

		mStoryWebService = new StoryWebService(account, mCP.getAllProjects()
				.get(0).getName());
	}

	@After
	public void tearDown() {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		mConfig.setTestMode(false);
		mConfig.save();

		// release
		mStoryWebService = null;
		mCP = null;
		mCS = null;
		mASTS = null;
		mATTS = null;
		mConfig = null;
	}

	@Test
	public void testCreateStory() throws JSONException {
		JSONObject storyJson = new JSONObject();
		storyJson.put("name", "TEST_NAME").put("notes", "TEST_NOTES")
				.put("how_to_demo", "TEST_HOW_TO_DEMO").put("importance", 99)
				.put("value", 15).put("estimate", 21).put("status", 0)
				.put("sprint_id", -1).put("tags", "");

		String response = mStoryWebService.createStory(storyJson.toString());

		JSONObject responseJson = new JSONObject(response);
		assertEquals("SUCCESS", responseJson.getString("status"));
		assertNotSame("NULL", responseJson.getString("storyId"));
	}

	@Test
	public void testUpdateStory() throws JSONException {
		StoryObject story = mASTS.getStories().get(0);

		JSONObject storyJson = new JSONObject();
		storyJson.put("id", story.getId()).put("name", "NAME")
				.put("notes", "NOTES").put("how_to_demo", "HOW_TO_DEMO")
				.put("importance", 99).put("value", 15).put("estimate", 21)
				.put("status", StoryObject.STATUS_DONE).put("sprint_id", 1L).put("tags", "");
		
		String response = mStoryWebService.updateStory(storyJson.toString());
		JSONObject responseJson = new JSONObject(response);
		assertEquals(story.getId(), responseJson.getLong(StoryEnum.ID));
		assertEquals("NAME", responseJson.getString(StoryEnum.NAME));
		assertEquals("NOTES", responseJson.getString(StoryEnum.NOTES));
		assertEquals("HOW_TO_DEMO", responseJson.getString(StoryEnum.HOW_TO_DEMO));
		assertEquals(21, responseJson.getInt(StoryEnum.ESTIMATE));
		assertEquals(99, responseJson.getInt(StoryEnum.IMPORTANCE));
		assertEquals(15, responseJson.getInt(StoryEnum.VALUE));
		assertEquals(StoryObject.STATUS_DONE, responseJson.getInt(StoryEnum.STATUS));
		assertEquals(1L, responseJson.getLong(StoryEnum.SPRINT_ID));
	}

	@Test
	public void testGetTasksInStory() throws JSONException {
		StoryObject story = mASTS.getStories().get(0);
		ArrayList<TaskObject> tasks = story.getTasks();
		
		String response = mStoryWebService.getTasksInStory(story.getId());
		JSONObject responseJson = new JSONObject(response);
		JSONArray tasksJson = responseJson.getJSONArray("tasks");
		for (int i = 0; i < tasksJson.length(); i++) {
			JSONObject taskJson = tasksJson.getJSONObject(i);
			TaskObject task = tasks.get(i);
			assertEquals(task.getId(), taskJson.getLong(TaskEnum.ID));
			assertEquals(task.getName(), taskJson.getString(TaskEnum.NAME));
			assertEquals(task.getNotes(), taskJson.getString(TaskEnum.NOTES));
			assertEquals(task.getEstimate(), taskJson.getInt(TaskEnum.ESTIMATE));
			assertEquals(task.getActual(), taskJson.getInt(TaskEnum.ACTUAL));
			assertEquals(task.getRemains(), taskJson.getInt(TaskEnum.REMAIN));
			assertEquals(task.getStatus(), taskJson.getInt(TaskEnum.STATUS));
			assertEquals(task.getSerialId(), taskJson.getInt(TaskEnum.SERIAL_ID));
			assertEquals(task.getProjectId(), taskJson.getInt(TaskEnum.PROJECT_ID));
			assertEquals("{}", taskJson.getJSONObject(TaskEnum.HANDLER).toString());
		}
	}

	@Test
	public void testAddExistedTask() {
		long projectId = mCP.getAllProjects().get(0).getId();
		StoryObject story = mASTS.getStories().get(0);
		assertEquals(3, story.getTasks().size());
		
		TaskObject task1 = new TaskObject(projectId);
		task1.setName("TASK_NAME1").save();
		
		TaskObject task2 = new TaskObject(projectId);
		task2.setName("TASK_NAME2").save();
		
		String taskIdJsonString = String.format("[%s, %s]", task1.getId(), task2.getId());
		mStoryWebService.addExistedTask(story.getId(), taskIdJsonString);
		
		story.reload();
		assertEquals(5, story.getTasks().size());
		
		TaskObject actualTask1 = story.getTasks().get(3);
		assertEquals(task1.getId(), actualTask1.getId());
		assertEquals(task1.getName(), actualTask1.getName());
		
		TaskObject actualTask2 = story.getTasks().get(4);
		assertEquals(task2.getId(), actualTask2.getId());
		assertEquals(task2.getName(), actualTask2.getName());
	}
}
