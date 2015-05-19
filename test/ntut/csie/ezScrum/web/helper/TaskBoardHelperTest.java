package ntut.csie.ezScrum.web.helper;

import static org.junit.Assert.assertEquals;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaskBoardHelperTest {
	private Configuration mConfig = null;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
	private static final int PROJECT_COUNT = 1;
	private static final int SPRINT_COUNT = 1;
	private static final int STORY_COUNT = 3;
	private static final int STORY_ESTIMATE = 5;
	private static final int TASK_COUNT = 3;
	private static final int TASK_ESTIMATE = 8;
	private static final String CREATE_PRODUCTBACKLOG_TYPE = "EST";
	private TaskBoardHelper mTaskBoardHelper = null; 
	
	@Before
	public void setUp() throws Exception {
		// initialize Configuration
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		ini = null;

		mCP = new CreateProject(PROJECT_COUNT);
		mCP.exeCreate();

		mCS = new CreateSprint(SPRINT_COUNT, mCP);
		mCS.exe();

		mASTS = new AddStoryToSprint(STORY_COUNT, STORY_ESTIMATE, mCS, mCP,
				CREATE_PRODUCTBACKLOG_TYPE);
		mASTS.exe();

		mATTS = new AddTaskToStory(TASK_COUNT, TASK_ESTIMATE, mASTS, mCP);
		mATTS.exe();

		long sprintId = mCS.getSprintsId().get(0);
		mTaskBoardHelper = new TaskBoardHelper(mCP.getAllProjects().get(0), sprintId);
	}
	
	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		ini = null;
		mCP = null;
		mCS = null;
		mASTS = null;
		mATTS = null;
	}
	
	/**
	 * Get all stories and tasks, no filter
	 * 
	 * @throws JSONException
	 */
	@Test
	public void testGetTaskBoardStoryTaskListText_ALL() throws JSONException {
		String response = mTaskBoardHelper.getTaskBoardStoryTaskListText("ALL").toString();
		JSONObject json = new JSONObject(response);

		JSONArray stories = json.getJSONArray("Stories");
		assertEquals(3, stories.length());
		for (int i = 0; i < stories.length(); i++) {
			String storyIndex = String.valueOf(i + 1);
			
			JSONObject story = stories.getJSONObject(i);
			assertEquals(storyIndex, story.getString("Id"));
			assertEquals("TEST_STORY_" + storyIndex, story.getString("Name"));
			assertEquals("TEST_STORY_NOTE_" + storyIndex, story.getString("Notes"));
			assertEquals("TEST_STORY_DEMO_" + storyIndex, story.getString("HowToDemo"));
			assertEquals("50", story.getString("Value"));
			assertEquals("5", story.getString("Estimate"));
			assertEquals("100", story.getString("Importance"));
			assertEquals("1", story.getString("Sprint"));
			assertEquals(0, story.getJSONArray("AttachFileList").length());

			JSONArray tasks = story.getJSONArray("Tasks");
			for (int j = 0; j < tasks.length(); j++) {
				String taskIndex = String.valueOf(i * 3 + j + 1);
				JSONObject task = tasks.getJSONObject(j);
				assertEquals(taskIndex, task.getString("Id"));
				assertEquals("TEST_TASK_" + String.valueOf(j + 1), task.getString("Name"));
				assertEquals("TEST_TASK_NOTES_" + String.valueOf(j + 1), task.getString("Notes"));
				assertEquals("", task.getString("HandlerUserName"));
				assertEquals("", task.getString("Partners"));
				assertEquals("8", task.getString("Estimate"));
				assertEquals("0", task.getString("Actual"));
				assertEquals("8", task.getString("RemainHours"));
				assertEquals("new", task.getString("Status"));
				assertEquals(0, task.getJSONArray("AttachFileList").length());
			}
		}
	}
	
	/**
	 * Use username to filter stories and tasks
	 * 
	 * @throws JSONException
	 */
	@Test
	public void testGetTaskBoardStoryTaskListText_Filter() throws JSONException {
		String response = mTaskBoardHelper.getTaskBoardStoryTaskListText("ALL").toString();
		JSONObject json = new JSONObject(response);

		JSONArray stories = json.getJSONArray("Stories");
		assertEquals(3, stories.length());
		for (int i = 0; i < stories.length(); i++) {
			String storyIndex = String.valueOf(i + 1);
			
			JSONObject story = stories.getJSONObject(i);
			assertEquals(storyIndex, story.getString("Id"));
			assertEquals("TEST_STORY_" + storyIndex, story.getString("Name"));
			assertEquals("TEST_STORY_NOTE_" + storyIndex, story.getString("Notes"));
			assertEquals("TEST_STORY_DEMO_" + storyIndex, story.getString("HowToDemo"));
			assertEquals("50", story.getString("Value"));
			assertEquals("5", story.getString("Estimate"));
			assertEquals("100", story.getString("Importance"));
			assertEquals("1", story.getString("Sprint"));
			assertEquals(0, story.getJSONArray("AttachFileList").length());

			JSONArray tasks = story.getJSONArray("Tasks");
			for (int j = 0; j < tasks.length(); j++) {
				String taskIndex = String.valueOf(i * 3 + j + 1);
				JSONObject task = tasks.getJSONObject(j);
				assertEquals(taskIndex, task.getString("Id"));
				assertEquals("TEST_TASK_" + String.valueOf(j + 1), task.getString("Name"));
				assertEquals("TEST_TASK_NOTES_" + String.valueOf(j + 1), task.getString("Notes"));
				assertEquals("", task.getString("HandlerUserName"));
				assertEquals("", task.getString("Partners"));
				assertEquals("8", task.getString("Estimate"));
				assertEquals("0", task.getString("Actual"));
				assertEquals("8", task.getString("RemainHours"));
				assertEquals("new", task.getString("Status"));
				assertEquals(0, task.getJSONArray("AttachFileList").length());
			}
		}
		
		// set task #1 handler to admin(1)
		mATTS.getTasks().get(0).setHandlerId(1).save();
		
		response = mTaskBoardHelper.getTaskBoardStoryTaskListText("admin").toString();
		json = new JSONObject(response);
		stories = json.getJSONArray("Stories");
		assertEquals(1, stories.length());
		
		JSONObject story = stories.getJSONObject(0);
		String storyIndex  = "1";
		assertEquals(storyIndex, story.getString("Id"));
		assertEquals("TEST_STORY_" + storyIndex, story.getString("Name"));
		assertEquals("TEST_STORY_NOTE_" + storyIndex, story.getString("Notes"));
		assertEquals("TEST_STORY_DEMO_" + storyIndex, story.getString("HowToDemo"));
		assertEquals("50", story.getString("Value"));
		assertEquals("5", story.getString("Estimate"));
		assertEquals("100", story.getString("Importance"));
		assertEquals("1", story.getString("Sprint"));
		assertEquals(0, story.getJSONArray("AttachFileList").length());
		
		
		assertEquals(1, story.getJSONArray("Tasks").length());
		JSONObject task = story.getJSONArray("Tasks").getJSONObject(0);
		String taskIndex = "1";
		assertEquals(taskIndex, task.getString("Id"));
		assertEquals("TEST_TASK_1", task.getString("Name"));
		assertEquals("TEST_TASK_NOTES_1", task.getString("Notes"));
		assertEquals("admin", task.getString("HandlerUserName"));
		assertEquals("", task.getString("Partners"));
		assertEquals("8", task.getString("Estimate"));
		assertEquals("0", task.getString("Actual"));
		assertEquals("8", task.getString("RemainHours"));
		assertEquals("new", task.getString("Status"));
		assertEquals(0, task.getJSONArray("AttachFileList").length());
	}
	
	@Test
	public void testGetSprintInfoForTaskBoardText() throws JSONException {
		String response = mTaskBoardHelper.getSprintInfoForTaskBoardText().toString();
		JSONObject json = new JSONObject(response);
		assertEquals(15, json.getInt("CurrentStoryPoint"));
		assertEquals(72, json.getInt("CurrentTaskPoint"));
		assertEquals("TEST_SPRINTGOAL_1", json.getString("SprintGoal"));
		assertEquals("Release #0", json.getString("ReleaseID"));
		
		// close a story and tasks belong this story
		mASTS.getStories().get(0).setStatus(StoryObject.STATUS_DONE).save();
		mATTS.getTasks().get(0).setStatus(TaskObject.STATUS_DONE).setRemains(0).save();
		mATTS.getTasks().get(1).setStatus(TaskObject.STATUS_DONE).setRemains(0).save();
		mATTS.getTasks().get(2).setStatus(TaskObject.STATUS_DONE).setRemains(0).save();
		
		response = mTaskBoardHelper.getSprintInfoForTaskBoardText().toString();
		json = new JSONObject(response);
		assertEquals(10, json.getInt("CurrentStoryPoint"));
		assertEquals(48, json.getInt("CurrentTaskPoint"));
		assertEquals("TEST_SPRINTGOAL_1", json.getString("SprintGoal"));
		assertEquals("Release #0", json.getString("ReleaseID"));
	}
}
