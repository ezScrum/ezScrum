package ntut.csie.ezScrum.web.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SprintBacklogHelperTest {
	private SprintBacklogHelper mSprintBacklogHelper;
	private Configuration mConfig = null;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;

	@Before
	public void setUp() throws Exception {
		// initialize database
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		ini = null;

		// create test data
		int PROJECT_COUNT = 1;
		int SPRINT_COUNT = 1;
		int STORY_COUNT = 3;
		int STORY_ESTIMATE = 5;
		int TASK_COUNT = 3;
		int TASK_ESTIMATE = 8;
		String CREATE_PRODUCTBACKLOG_TYPE = "EST";

		mCP = new CreateProject(PROJECT_COUNT);
		mCP.exeCreate();

		mCS = new CreateSprint(SPRINT_COUNT, mCP);
		mCS.exe();

		mASTS = new AddStoryToSprint(STORY_COUNT, STORY_ESTIMATE, mCS, mCP,
				CREATE_PRODUCTBACKLOG_TYPE);
		mASTS.exe();

		mATTS = new AddTaskToStory(TASK_COUNT, TASK_ESTIMATE, mASTS, mCP);
		mATTS.exe();

		ProjectObject project = mCP.getAllProjects().get(0);
		long sprintId = 1;
		mSprintBacklogHelper = new SprintBacklogHelper(project, sprintId);
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
		mSprintBacklogHelper = null;
	}

	@Test
	public void testDropTask() {
		long taskId = 1;
		// get task one
		TaskObject task = TaskObject.get(taskId);
		task.setHandlerId(2);
		task.save();
		// check task status before test
		assertEquals(1, task.getStoryId());
		assertEquals(2, task.getHandlerId());
		// remove task
		mSprintBacklogHelper.dropTask(1);
		// get task again
		task = TaskObject.get(1);
		// check task status
		assertEquals(TaskObject.NO_PARENT, task.getStoryId());
		assertEquals(-1, task.getHandlerId());
		assertEquals(TaskObject.STATUS_UNCHECK, task.getStatus());
	}

	@Test
	public void testUpdateTask_WithNotExistHandler() {
		long taskId = 1;
		// check task status before test
		TaskObject task = TaskObject.get(taskId);
		assertEquals(1, task.getId());
		assertEquals("TEST_TASK_1", task.getName());
		assertEquals(-1, task.getHandlerId());
		assertEquals(0, task.getPartnersId().size());
		// create task info
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.taskId = task.getId();
		taskInfo.name = "newTask";
		taskInfo.handlerId = task.getHandlerId();
		taskInfo.estimate = task.getEstimate();
		taskInfo.remains = task.getRemains();
		taskInfo.actual = task.getActual();
		taskInfo.notes = task.getNotes();
		// update task
		mSprintBacklogHelper.updateTask(taskInfo, "account_not_exist_handler",
				"");
		// reload task after update
		task.reload();
		assertEquals(1, task.getId());
		assertEquals("newTask", task.getName());
		assertEquals(-1, task.getHandlerId());
		assertEquals(0, task.getPartnersId().size());
	}

	@Test
	public void testUpdateTask_WithNewHandler() {
		long taskId = 1;
		// check task status before test
		TaskObject task = TaskObject.get(taskId);
		assertEquals(1, task.getId());
		assertEquals("TEST_TASK_1", task.getName());
		assertEquals(-1, task.getHandlerId());
		assertEquals(0, task.getPartnersId().size());
		// create task info
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.taskId = task.getId();
		taskInfo.name = "newTask";
		taskInfo.handlerId = task.getHandlerId();
		taskInfo.estimate = task.getEstimate();
		taskInfo.remains = task.getRemains();
		taskInfo.actual = task.getActual();
		taskInfo.notes = task.getNotes();
		// create handler
		AccountObject handler = new AccountObject("account_handler");
		handler.save();
		// update task
		mSprintBacklogHelper.updateTask(taskInfo, "account_handler", "");
		// reload task after update
		task.reload();
		assertEquals(1, task.getId());
		assertEquals("newTask", task.getName());
		assertEquals(handler.getId(), task.getHandler().getId());
		assertEquals(0, task.getPartnersId().size());
	}

	@Test
	public void testUpdateTask_WithNewHandlerAndNewPartners() {
		long taskId = 1;
		// check task status before test
		TaskObject task = TaskObject.get(taskId);
		assertEquals(1, task.getId());
		assertEquals("TEST_TASK_1", task.getName());
		assertEquals(-1, task.getHandlerId());
		assertEquals(0, task.getPartnersId().size());
		// create task info
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.taskId = task.getId();
		taskInfo.name = "newTask";
		taskInfo.handlerId = task.getHandlerId();
		taskInfo.estimate = task.getEstimate();
		taskInfo.remains = task.getRemains();
		taskInfo.actual = task.getActual();
		taskInfo.notes = task.getNotes();
		// create handler
		AccountObject handler = new AccountObject("account_handler");
		handler.save();
		// create partner1
		AccountObject partner1 = new AccountObject("account_partner1");
		partner1.save();
		// create partner2
		AccountObject partner2 = new AccountObject("account_partner2");
		partner2.save();
		// update task
		mSprintBacklogHelper.updateTask(taskInfo, "account_handler",
				"account_partner1;account_partner2");
		// reload task after update
		task.reload();
		assertEquals(1, task.getId());
		assertEquals("newTask", task.getName());
		assertEquals(handler.getId(), task.getHandler().getId());
		assertEquals(2, task.getPartners().size());
		assertEquals(partner1.getId(), task.getPartners().get(0).getId());
		assertEquals(partner2.getId(), task.getPartners().get(1).getId());
	}

	@Test
	public void testAddExistingTasksToStory() {
		long projectId = 1;
		long storyId = 1;
		// get story
		StoryObject story = StoryObject.get(storyId);
		// check story tasks status before test
		ArrayList<TaskObject> tasks = story.getTasks();
		assertEquals(3, tasks.size());
		assertEquals(1, tasks.get(0).getId());
		assertEquals(2, tasks.get(1).getId());
		assertEquals(3, tasks.get(2).getId());
		// create a new task
		TaskObject newTask = new TaskObject(projectId);
		newTask.save();
		// add new existing task
		String newTaskStringId = String.valueOf(newTask.getId());
		String[] selectedTasksStringId = { newTaskStringId };
		mSprintBacklogHelper.addExistingTasksToStory(selectedTasksStringId,
				storyId);
		// get story again
		story.reload();
		// check story tasks status after add new existing task
		ArrayList<TaskObject> newTasks = story.getTasks();
		assertEquals(4, newTasks.size());
		assertEquals(1, newTasks.get(0).getId());
		assertEquals(2, newTasks.get(1).getId());
		assertEquals(3, newTasks.get(2).getId());
		assertEquals(10, newTasks.get(3).getId());
	}

	@Test
	public void testAddExistingTasksToStory_WithTwoExistingTasks() {
		long projectId = 1;
		long storyId = 1;
		// get story
		StoryObject story = StoryObject.get(storyId);
		// check story tasks status before test
		ArrayList<TaskObject> tasks = story.getTasks();
		assertEquals(3, tasks.size());
		assertEquals(1, tasks.get(0).getId());
		assertEquals(2, tasks.get(1).getId());
		assertEquals(3, tasks.get(2).getId());
		// create a new task1
		TaskObject newTask1 = new TaskObject(projectId);
		newTask1.save();
		// create a new task1
		TaskObject newTask2 = new TaskObject(projectId);
		newTask2.save();
		// add new existing tasks
		String newTask1StringId = String.valueOf(newTask1.getId());
		String newTask2StringId = String.valueOf(newTask2.getId());
		String[] selectedTasksStringId = { newTask1StringId, newTask2StringId };
		mSprintBacklogHelper.addExistingTasksToStory(selectedTasksStringId,
				storyId);
		// get story again
		story.reload();
		// check story tasks status after add new existing task
		ArrayList<TaskObject> newTasks = story.getTasks();
		assertEquals(5, newTasks.size());
		assertEquals(1, newTasks.get(0).getId());
		assertEquals(2, newTasks.get(1).getId());
		assertEquals(3, newTasks.get(2).getId());
		assertEquals(10, newTasks.get(3).getId());
		assertEquals(11, newTasks.get(4).getId());
	}

	@Test
	public void testAddExistingStory() {
		long projectId = mCP.getAllProjects().get(0).getId();
		ArrayList<Long> storiesId = new ArrayList<Long>();
		for (int i = 0; i < 3; i++) {
			StoryObject story = new StoryObject(projectId);
			story.setName("QAQ").setNotes("QWQ").setHowToDemo("QOQ")
					.setStatus(StoryObject.STATUS_UNCHECK).save();
			storiesId.add(story.getId());
		}

		mSprintBacklogHelper.addExistingStory(storiesId);
		for (long storyId : storiesId) {
			StoryObject story = StoryObject.get(storyId);
			assertEquals(story.getSprintId(), 1);
		}
	}

	@Test
	public void testGetExistingStories() {
		long projectId = mCP.getAllProjects().get(0).getId();
		ArrayList<Long> storiesId = new ArrayList<Long>();
		for (int i = 0; i < 3; i++) {
			StoryObject story = new StoryObject(projectId);
			story.setName("QAQ").setNotes("QWQ").setHowToDemo("QOQ")
					.setStatus(StoryObject.STATUS_UNCHECK).save();
			storiesId.add(story.getId());
		}

		ArrayList<StoryObject> existingStories = mSprintBacklogHelper
				.getExistingStories();
		assertEquals(3, existingStories.size());
		assertEquals(storiesId.get(0), existingStories.get(0).getId());
		assertEquals(storiesId.get(1), existingStories.get(1).getId());
		assertEquals(storiesId.get(2), existingStories.get(2).getId());
	}

	@Test
	public void testGetSprintBacklogListInfoText() throws JSONException {
		int storyCount = 3;
		int taskCount = 3;
		JSONArray sprintBacklogInfoText = new JSONArray(
				mSprintBacklogHelper.getSprintBacklogListInfoText());
		assertEquals(storyCount, sprintBacklogInfoText.length());
		// assert story info text
		for (int storyIndex = 0; storyIndex < storyCount; storyIndex++) {
			JSONObject storyInfoText = sprintBacklogInfoText
					.getJSONObject(storyIndex);
			assertEquals(storyIndex + 1, storyInfoText.getLong("ID"));
			assertEquals("Story", storyInfoText.getString("Type"));
			assertEquals("", storyInfoText.getString("Tag"));
			assertEquals("TEST_STORY_" + (storyIndex + 1),
					storyInfoText.getString("Name"));
			assertEquals(" ", storyInfoText.getString("Handler"));
			assertEquals(50, storyInfoText.getInt("Value"));
			assertEquals(5, storyInfoText.getInt("Estimate"));
			assertEquals(100, storyInfoText.getInt("Importance"));
			assertEquals("new", storyInfoText.getString("Status"));
			assertEquals("TEST_STORY_NOTE_" + (storyIndex + 1),
					storyInfoText.getString("Notes"));
			assertEquals(1, storyInfoText.getLong("SprintID"));
			assertTrue(storyInfoText.getJSONArray("dateList").length() > 0);
			assertEquals(0, storyInfoText.getJSONObject("dateToHourMap").length());
			assertEquals(false, storyInfoText.getBoolean("leaf"));
			assertEquals(false, storyInfoText.getBoolean("expanded"));
			assertEquals("Story:" + (storyIndex + 1), storyInfoText.getString("id"));
			assertEquals("folder", storyInfoText.getString("cls"));

			// assert task info text
			JSONArray tasksInfoText = storyInfoText.getJSONArray("children");
			assertEquals(taskCount, tasksInfoText.length());
			for (int taskIndex = 0; taskIndex < taskCount; taskIndex++) {
				JSONObject taskInfoText = tasksInfoText
						.getJSONObject(taskIndex);
				assertEquals(storyCount * storyIndex + taskIndex + 1, taskInfoText.getLong("ID"));
				assertEquals("Task", taskInfoText.getString("Type"));
				assertEquals("TEST_TASK_" + (taskIndex + 1),
						taskInfoText.getString("Name"));
				assertEquals("", taskInfoText.getString("Handler"));
				assertEquals(8, taskInfoText.getInt("Estimate"));
				assertEquals("new", taskInfoText.getString("Status"));
				assertEquals("TEST_TASK_NOTES_" + (taskIndex + 1),
						taskInfoText.getString("Notes"));
				assertTrue(taskInfoText.getJSONArray("dateList").length() > 0);
				assertNotNull(taskInfoText.getJSONObject("dateToHourMap").keys());
				assertEquals(true, taskInfoText.getBoolean("leaf"));
				assertEquals(false, taskInfoText.getBoolean("expanded"));
				assertEquals("Task:" + (storyCount * storyIndex + taskIndex + 1),
						taskInfoText.getString("id"));
				assertEquals("file", taskInfoText.getString("cls"));
			}
		}
	}

	@Test
	public void testGetShowSprintBacklogText() throws JSONException {
		String response = mSprintBacklogHelper.getShowSprintBacklogText();

		JSONObject actualJson = new JSONObject(response);
		
		JSONObject sprint = actualJson.getJSONObject("Sprint");
		assertEquals(1, sprint.getInt("Id"));
		assertEquals("Sprint #1", sprint.getString("Name"));
		assertEquals(15, sprint.getInt("CurrentPoint"));
		assertEquals(10, sprint.getInt("LimitedPoint"));
		assertEquals(72, sprint.getInt("TaskPoint"));
		assertEquals("Release #None", sprint.getString("ReleaseID"));
		assertEquals("TEST_SPRINTGOAL_1", sprint.getString("SprintGoal"));
		
		JSONArray stories = actualJson.getJSONArray("Stories");
		for (int i = 0; i < mASTS.getStories().size(); i++) {
			String storyIndex = String.valueOf(i + 1);
			JSONObject story = stories.getJSONObject(i);
			
			assertEquals((i + 1), story.getInt("Id"));
			assertEquals("", story.getString("Tag"));
			assertEquals("TEST_STORY_" + storyIndex,
					story.getString("Name"));
			assertEquals(50, story.getInt("Value"));
			assertEquals(5, story.getInt("Estimate"));
			assertEquals(100, story.getInt("Importance"));
			assertEquals("new", story.getString("Status"));
			assertEquals("TEST_STORY_NOTE_" + storyIndex,
					story.getString("Notes"));
			assertEquals("TEST_STORY_DEMO_" + storyIndex,
					story.getString("HowToDemo"));
			assertEquals(1, story.getLong("Sprint"));
			assertEquals("", story.getString("Link"));
			assertEquals("", story.getString("Release"));
			assertEquals("false", story.getString("Attach"));
			assertEquals(0, story.getJSONArray("AttachFileList").length());
		}
	}

	@Test
	public void testGetAjaxGetSprintBacklogDateInfo() throws JSONException {
		String response = mSprintBacklogHelper
				.getAjaxGetSprintBacklogDateInfo();
		JSONObject json = new JSONObject(response);
		JSONArray dates = json.getJSONArray("Dates");
		assertEquals(10, dates.length());

		for (int i = 0; i < dates.length(); i++) {
			JSONObject date = dates.getJSONObject(i);
			assertEquals("Date_" + (i + 1), date.getString("Id"));
		}
	}

	@Test
	public void testGetStoriesInSprintResponseText() {
		ArrayList<StoryObject> stories = mASTS.getStories();
		String actualResponse = mSprintBacklogHelper.getStoriesInSprintResponseText(
				stories).toString();

		String expectStoryString = "<Story><Id>%s</Id><Link></Link><Name>TEST_STORY_%s</Name><Value>50</Value>"
				+ "<Importance>100</Importance><Estimate>5</Estimate><Status>new</Status><Notes>TEST_STORY_NOTE_%s</Notes>"
				+ "<HowToDemo>TEST_STORY_DEMO_%s</HowToDemo><Release></Release><Sprint>1</Sprint><Tag></Tag></Story>";
		
		assertEquals(true, actualResponse.contains("<ExistingStories>"));
		for (int i = 1; i <= stories.size(); i++) {
			String index = String.valueOf(i);
			assertEquals(true, actualResponse.contains(String.format(expectStoryString,
					index, index, index, index)));
		}
		assertEquals(true, actualResponse.contains("</ExistingStories>"));
	}
}
