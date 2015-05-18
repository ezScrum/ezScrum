package ntut.csie.ezScrum.web.mapper;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SprintBacklogMapperTest {

	private SprintBacklogMapper mSprintBacklogMapper;
	private Configuration mConfig = null;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
	private static long mProjectId = 1;

	@Before
	public void setUp() throws Exception {
		// Initialize database
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		ini = null;

		// create test data
		int projectCount = 1;
		int sprintCount = 1;
		int storyCount = 3;
		int storyEstimate = 5;
		int taskCount = 3;
		int taskEstimate = 8;
		String columnBeSet = "EST";
		
		mCP = new CreateProject(projectCount);
		mCP.exeCreate();

		mCS = new CreateSprint(sprintCount, mCP);
		mCS.exe();

		mASTS = new AddStoryToSprint(storyCount, storyEstimate, mCS, mCP, columnBeSet);
		mASTS.exe();

		mATTS = new AddTaskToStory(taskCount, taskEstimate, mASTS, mCP);
		mATTS.exe();

		ProjectObject project = mCP.getAllProjects().get(0);
		mSprintBacklogMapper = new SprintBacklogMapper(project);
	}

	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
		
		ini = null;
		mCP = null;
		mCS = null;
		mASTS = null;
		mATTS = null;
		mConfig = null;
		mSprintBacklogMapper = null;
		projectManager = null;
	}

	@Test
	public void testGetAllTasks() {
		ArrayList<TaskObject> tasks = mSprintBacklogMapper.getAllTasks();
		assertEquals(9, tasks.size());
		// check project id
		for (TaskObject task : tasks) {
			assertEquals(1, task.getProjectId());
		}
		// check story id
		assertEquals(1, tasks.get(0).getStoryId());
		assertEquals(1, tasks.get(1).getStoryId());
		assertEquals(1, tasks.get(2).getStoryId());
		assertEquals(2, tasks.get(3).getStoryId());
		assertEquals(2, tasks.get(4).getStoryId());
		assertEquals(2, tasks.get(5).getStoryId());
		assertEquals(3, tasks.get(6).getStoryId());
		assertEquals(3, tasks.get(7).getStoryId());
		assertEquals(3, tasks.get(8).getStoryId());
		// check task id
		for (int i = 0; i < tasks.size(); i++) {
			assertEquals(i + 1, tasks.get(i).getId());
		}
	}

	@Test
	public void testGetAllStories() {
		long projectId = mCP.getAllProjects().get(0).getId();
		ArrayList<StoryObject> stories = mSprintBacklogMapper.getAllStories();
		assertEquals(3, stories.size());
		// check project id
		assertEquals(projectId, stories.get(0).getProjectId());
		assertEquals(projectId, stories.get(1).getProjectId());
		assertEquals(projectId, stories.get(2).getProjectId());
		// check story id
		assertEquals(1, stories.get(0).getId());
		assertEquals(2, stories.get(1).getId());
		assertEquals(3, stories.get(2).getId());
	}

	@Test
	public void testGetStoriesBySprintId_WithNotExistSprintId() {
		ArrayList<StoryObject> stories = mSprintBacklogMapper.getStoriesBySprintId(-1);
		assertEquals(0, stories.size());
	}

	@Test
	public void testGetStoriesBySprintId() {
		long projectId = mCP.getAllProjects().get(0).getId();
		ArrayList<StoryObject> stories = mSprintBacklogMapper.getStoriesBySprintId(1);
		assertEquals(3, stories.size());
		// check project id
		assertEquals(projectId, stories.get(0).getProjectId());
		assertEquals(projectId, stories.get(1).getProjectId());
		assertEquals(projectId, stories.get(2).getProjectId());
		// check story id
		assertEquals(1, stories.get(0).getId());
		assertEquals(2, stories.get(1).getId());
		assertEquals(3, stories.get(2).getId());
	}

	@Test
	public void testGetTasksByStoryId_WithNotExistStory() {
		long notExistStoryId = -1;
		ArrayList<TaskObject> tasks = mSprintBacklogMapper.getTasksByStoryId(notExistStoryId);
		assertEquals(0, tasks.size());
	}

	@Test
	public void testGetTasksByStoryId_WithStory1() {
		long story1Id = 1;
		ArrayList<TaskObject> tasks = mSprintBacklogMapper.getTasksByStoryId(story1Id);
		assertEquals(3, tasks.size());
		// check project id
		assertEquals(1, tasks.get(0).getProjectId());
		assertEquals(1, tasks.get(1).getProjectId());
		assertEquals(1, tasks.get(2).getProjectId());
		// check story id
		assertEquals(1, tasks.get(0).getStoryId());
		assertEquals(1, tasks.get(1).getStoryId());
		assertEquals(1, tasks.get(2).getStoryId());
		// check task id
		assertEquals(1, tasks.get(0).getId());
		assertEquals(2, tasks.get(1).getId());
		assertEquals(3, tasks.get(2).getId());
	}

	@Test
	public void testGetTasksByStoryId_WithStory2() {
		long story2Id = 2;
		ArrayList<TaskObject> tasks = mSprintBacklogMapper.getTasksByStoryId(story2Id);
		assertEquals(3, tasks.size());
		// check project id
		assertEquals(1, tasks.get(0).getProjectId());
		assertEquals(1, tasks.get(1).getProjectId());
		assertEquals(1, tasks.get(2).getProjectId());
		// check story id
		assertEquals(2, tasks.get(0).getStoryId());
		assertEquals(2, tasks.get(1).getStoryId());
		assertEquals(2, tasks.get(2).getStoryId());
		// check task id
		assertEquals(4, tasks.get(0).getId());
		assertEquals(5, tasks.get(1).getId());
		assertEquals(6, tasks.get(2).getId());
	}

	@Test
	public void testGetTasksByStoryId_WithStory3() {
		long story3Id = 3;
		ArrayList<TaskObject> tasks = mSprintBacklogMapper.getTasksByStoryId(story3Id);
		assertEquals(3, tasks.size());
		// check project id
		assertEquals(1, tasks.get(0).getProjectId());
		assertEquals(1, tasks.get(1).getProjectId());
		assertEquals(1, tasks.get(2).getProjectId());
		// check story id
		assertEquals(3, tasks.get(0).getStoryId());
		assertEquals(3, tasks.get(1).getStoryId());
		assertEquals(3, tasks.get(2).getStoryId());
		// check task id
		assertEquals(7, tasks.get(0).getId());
		assertEquals(8, tasks.get(1).getId());
		assertEquals(9, tasks.get(2).getId());
	}

	@Test
	public void testGetDroppedStories() {
		// check dropped stories before test
		ArrayList<StoryObject> droppedStories = mSprintBacklogMapper.getDroppedStories();
		assertEquals(0, droppedStories.size());
		ArrayList<StoryObject> allStories = mSprintBacklogMapper.getAllStories();
		assertEquals(1, allStories.get(0).getSprintId());
		assertEquals(1, allStories.get(1).getSprintId());
		assertEquals(1, allStories.get(2).getSprintId());
		
		// remove story id = 1 from sprint
		allStories.get(0).setSprintId(StoryObject.DEFAULT_VALUE).save();
		
		// check dropped stories after add a dropped story
		droppedStories = mSprintBacklogMapper.getDroppedStories();
		allStories = mSprintBacklogMapper.getAllStories();
		
		assertEquals(StoryObject.DEFAULT_VALUE, droppedStories.get(0).getSprintId());
		assertEquals(1, allStories.get(0).getSprintId());
		assertEquals(1, allStories.get(1).getSprintId());
		assertEquals(1, droppedStories.size());
		assertEquals(2, allStories.size());
	}

	@Test
	public void testGetStory_WithNotExistStoryId() {
		long notExistStoryId = -1;
		StoryObject story = mSprintBacklogMapper.getStory(notExistStoryId);
		assertEquals(null, story);
	}

	@Test
	public void testGetStory() {
		long projectId = mCP.getAllProjects().get(0).getId();
		StoryObject story1 = mSprintBacklogMapper.getStory(1);
		StoryObject story2 = mSprintBacklogMapper.getStory(2);
		StoryObject story3 = mSprintBacklogMapper.getStory(3);
		// check project id
		assertEquals(projectId, story1.getProjectId());
		assertEquals(projectId, story2.getProjectId());
		assertEquals(projectId, story3.getProjectId());
		// check story id
		assertEquals(1, story1.getId());
		assertEquals(2, story2.getId());
		assertEquals(3, story3.getId());
	}

	@Test
	public void testGetTask() {
		TaskObject task = null, expectTask = null;

		// assert first task
		task = mSprintBacklogMapper.getTask(mATTS.getTasksId().get(0));
		expectTask = mATTS.getTasks().get(0);
		assertEquals(expectTask.getName(), task.getName());
		assertEquals(expectTask.getNotes(), task.getNotes());
		assertEquals(expectTask.getActual(), task.getActual());
		assertEquals(expectTask.getEstimate(), task.getEstimate());
		assertEquals(expectTask.getStoryId(), task.getStoryId());
		assertEquals(expectTask.getProjectId(), task.getProjectId());
		assertEquals(expectTask.getRemains(), task.getRemains());

		// assert second task
		task = mSprintBacklogMapper.getTask(mATTS.getTasksId().get(1));
		expectTask = mATTS.getTasks().get(1);
		assertEquals(expectTask.getName(), task.getName());
		assertEquals(expectTask.getNotes(), task.getNotes());
		assertEquals(expectTask.getActual(), task.getActual());
		assertEquals(expectTask.getEstimate(), task.getEstimate());
		assertEquals(expectTask.getStoryId(), task.getStoryId());
		assertEquals(expectTask.getProjectId(), task.getProjectId());
		assertEquals(expectTask.getRemains(), task.getRemains());

		// assert third task
		task = mSprintBacklogMapper.getTask(mATTS.getTasksId().get(2));
		expectTask = mATTS.getTasks().get(2);
		assertEquals(expectTask.getName(), task.getName());
		assertEquals(expectTask.getNotes(), task.getNotes());
		assertEquals(expectTask.getActual(), task.getActual());
		assertEquals(expectTask.getEstimate(), task.getEstimate());
		assertEquals(expectTask.getStoryId(), task.getStoryId());
		assertEquals(expectTask.getProjectId(), task.getProjectId());
		assertEquals(expectTask.getRemains(), task.getRemains());

		// assert fourth task
		task = mSprintBacklogMapper.getTask(mATTS.getTasksId().get(3));
		expectTask = mATTS.getTasks().get(3);
		assertEquals(expectTask.getName(), task.getName());
		assertEquals(expectTask.getNotes(), task.getNotes());
		assertEquals(expectTask.getActual(), task.getActual());
		assertEquals(expectTask.getEstimate(), task.getEstimate());
		assertEquals(expectTask.getStoryId(), task.getStoryId());
		assertEquals(expectTask.getProjectId(), task.getProjectId());
		assertEquals(expectTask.getRemains(), task.getRemains());

		// assert fifth task
		task = mSprintBacklogMapper.getTask(mATTS.getTasksId().get(4));
		expectTask = mATTS.getTasks().get(4);
		assertEquals(expectTask.getName(), task.getName());
		assertEquals(expectTask.getNotes(), task.getNotes());
		assertEquals(expectTask.getActual(), task.getActual());
		assertEquals(expectTask.getEstimate(), task.getEstimate());
		assertEquals(expectTask.getStoryId(), task.getStoryId());
		assertEquals(expectTask.getProjectId(), task.getProjectId());
		assertEquals(expectTask.getRemains(), task.getRemains());

		// assert sixth task
		task = mSprintBacklogMapper.getTask(mATTS.getTasksId().get(5));
		expectTask = mATTS.getTasks().get(5);
		assertEquals(expectTask.getName(), task.getName());
		assertEquals(expectTask.getNotes(), task.getNotes());
		assertEquals(expectTask.getActual(), task.getActual());
		assertEquals(expectTask.getEstimate(), task.getEstimate());
		assertEquals(expectTask.getStoryId(), task.getStoryId());
		assertEquals(expectTask.getProjectId(), task.getProjectId());
		assertEquals(expectTask.getRemains(), task.getRemains());

		// assert seventh task
		task = mSprintBacklogMapper.getTask(mATTS.getTasksId().get(6));
		expectTask = mATTS.getTasks().get(6);
		assertEquals(expectTask.getName(), task.getName());
		assertEquals(expectTask.getNotes(), task.getNotes());
		assertEquals(expectTask.getActual(), task.getActual());
		assertEquals(expectTask.getEstimate(), task.getEstimate());
		assertEquals(expectTask.getStoryId(), task.getStoryId());
		assertEquals(expectTask.getProjectId(), task.getProjectId());
		assertEquals(expectTask.getRemains(), task.getRemains());

		// assert eighth task
		task = mSprintBacklogMapper.getTask(mATTS.getTasksId().get(7));
		expectTask = mATTS.getTasks().get(7);
		assertEquals(expectTask.getName(), task.getName());
		assertEquals(expectTask.getNotes(), task.getNotes());
		assertEquals(expectTask.getActual(), task.getActual());
		assertEquals(expectTask.getEstimate(), task.getEstimate());
		assertEquals(expectTask.getStoryId(), task.getStoryId());
		assertEquals(expectTask.getProjectId(), task.getProjectId());
		assertEquals(expectTask.getRemains(), task.getRemains());

		// assert nine task
		task = mSprintBacklogMapper.getTask(mATTS.getTasksId().get(8));
		expectTask = mATTS.getTasks().get(8);
		assertEquals(expectTask.getName(), task.getName());
		assertEquals(expectTask.getNotes(), task.getNotes());
		assertEquals(expectTask.getActual(), task.getActual());
		assertEquals(expectTask.getEstimate(), task.getEstimate());
		assertEquals(expectTask.getStoryId(), task.getStoryId());
		assertEquals(expectTask.getProjectId(), task.getProjectId());
		assertEquals(expectTask.getRemains(), task.getRemains());
	}

	@Test
	public void testUpdateTask() {
		long taskId = 1;
		// get old task
		TaskObject oldTask = TaskObject.get(taskId);
		// check task status before update task
		assertEquals(1, oldTask.getId());
		assertEquals("TEST_TASK_1", oldTask.getName());
		assertEquals(-1, oldTask.getHandlerId());
		assertEquals(8, oldTask.getEstimate());
		assertEquals(8, oldTask.getRemains());
		assertEquals(0, oldTask.getActual());
		assertEquals("TEST_TASK_NOTES_1", oldTask.getNotes());
		assertEquals(0, oldTask.getPartnersId().size());
		// create task info
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.taskId = taskId;
		// set new value
		taskInfo.name = "NEW_TEST_TASK_NAME";
		taskInfo.handlerId = 1;
		taskInfo.estimate = 5;
		taskInfo.remains = 3;
		taskInfo.actual = 6;
		taskInfo.notes = "NEW_TEST_TASK_NOTES";
		ArrayList<Long> partnersId = new ArrayList<Long>();
		partnersId.add(1L);
		partnersId.add(2L);
		taskInfo.partnersId = partnersId;
		// update task
		mSprintBacklogMapper.updateTask(taskId, taskInfo);
		// get new task
		TaskObject newTask = TaskObject.get(taskId);
		// check new task status after update
		assertEquals(1, newTask.getId());
		assertEquals("NEW_TEST_TASK_NAME", newTask.getName());
		assertEquals(1, newTask.getHandlerId());
		assertEquals(5, newTask.getEstimate());
		assertEquals(3, newTask.getRemains());
		assertEquals(6, newTask.getActual());
		assertEquals("NEW_TEST_TASK_NOTES", newTask.getNotes());
		assertEquals(2, newTask.getPartnersId().size());
		assertEquals(1, newTask.getPartnersId().get(0));
		assertEquals(2, newTask.getPartnersId().get(1));
	}

	@Test
	public void testAddTask() {
		// create task info
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.name = "NEW_TEST_TASK_NAME";
		taskInfo.handlerId = 1;
		taskInfo.estimate = 5;
		taskInfo.actual = 6;
		taskInfo.notes = "NEW_TEST_TASK_NOTES";
		taskInfo.partnersId.add(1L);

		long taskId = mSprintBacklogMapper.addTask(mProjectId, taskInfo);

		TaskObject actualTask = TaskObject.get(taskId);
		assertEquals(taskInfo.name, actualTask.getName());
		assertEquals(taskInfo.notes, actualTask.getNotes());
		assertEquals(taskInfo.estimate, actualTask.getEstimate());
		assertEquals(taskInfo.estimate, actualTask.getRemains());
		assertEquals(0, actualTask.getActual());
		assertEquals(taskInfo.handlerId, actualTask.getHandlerId());
		assertEquals(taskInfo.partnersId.get(0), actualTask.getPartnersId().get(0));
	}

	@Test
	public void testAddExistingTasksToStory() {
		// get story
		StoryObject story = mASTS.getStories().get(0);
		
		// check story tasks status before test
		ArrayList<TaskObject> oldTaskIds = story.getTasks();
		assertEquals(3, oldTaskIds.size());
		assertEquals(1, oldTaskIds.get(0).getId());
		assertEquals(2, oldTaskIds.get(1).getId());
		assertEquals(3, oldTaskIds.get(2).getId());
		
		// create a new task
		TaskObject newTask = new TaskObject(1);
		newTask.save();
		// check new task status before be added to story
		assertEquals(1, newTask.getProjectId());
		assertEquals(-1, newTask.getStoryId());
		assertEquals(10, newTask.getId());
		
		// add new existing task to story
		ArrayList<Long> tasksId = new ArrayList<Long>();
		tasksId.add(newTask.getId());
		
		mSprintBacklogMapper.addExistingTasksToStory(tasksId, 1);
		
		// get story again
		story.reload();
		
		// check story tasks status after add new existing task
		List<TaskObject> newTaskIds = story.getTasks();
		assertEquals(4, newTaskIds.size());
		assertEquals(1, newTaskIds.get(0).getId());
		assertEquals(2, newTaskIds.get(1).getId());
		assertEquals(3, newTaskIds.get(2).getId());
		assertEquals(10, newTaskIds.get(3).getId());
	}

	@Test
	public void testAddExistingTasksToStory_WithTwoExistingTasks() {
		StoryObject story = mASTS.getStories().get(0);
		
		// create a new task 1
		TaskObject newTask1 = new TaskObject(1);
		newTask1.save();
		// check new task status before be added to story
		assertEquals(1, newTask1.getProjectId());
		assertEquals(-1, newTask1.getStoryId());
		assertEquals(10, newTask1.getId());
		
		// create a new task 2
		TaskObject newTask2 = new TaskObject(1);
		newTask2.save();
		// check new existing task status before be added to story
		assertEquals(1, newTask2.getProjectId());
		assertEquals(-1, newTask2.getStoryId());
		assertEquals(11, newTask2.getId());
		
		// add new task 1 and new task 2 to story
		ArrayList<Long> tasksId = new ArrayList<Long>();
		tasksId.add(10L);
		tasksId.add(11L);
		mSprintBacklogMapper.addExistingTasksToStory(tasksId, story.getId());
		
		// reload story again
		story.reload();
		
		// check story tasks status after add existing new tasks
		ArrayList<TaskObject> tasks = story.getTasks();
		assertEquals(5, tasks.size());
		assertEquals(1, tasks.get(0).getId());
		assertEquals(2, tasks.get(1).getId());
		assertEquals(3, tasks.get(2).getId());
		assertEquals(10, tasks.get(3).getId());
		assertEquals(11, tasks.get(4).getId());
	}

	@Test
	public void testGetTasksWithNoParent() {
		long projectId = mCP.getAllProjects().get(0).getId();

		// add two task, no parent
		String TEST_NAME = "NEW_TEST_TASK_NAME_";
		String TEST_NOTE = "NEW_TEST_TASK_NOTE_";
		int TEST_EST = 5;
		int TEST_HANDLER = 1;
		int TEST_ACTUAL = 3;

		TaskObject expectTask1 = new TaskObject(projectId);
		expectTask1.setName(TEST_NAME + 1).setNotes(TEST_NOTE + 1)
				.setEstimate(TEST_EST).setHandlerId(TEST_HANDLER)
				.setActual(TEST_ACTUAL).save();

		TaskObject expectTask2 = new TaskObject(projectId);
		expectTask2.setName(TEST_NAME + 2).setNotes(TEST_NOTE + 2)
				.setEstimate(TEST_EST + 2).setHandlerId(TEST_HANDLER)
				.setActual(TEST_ACTUAL + 2).save();

		// assert size
		ArrayList<TaskObject> tasks = mSprintBacklogMapper
				.getTasksWithNoParent(projectId);
		assertEquals(2, tasks.size());

		// assert first task
		TaskObject actualTask = tasks.get(0);
		assertEquals(expectTask1.getName(), actualTask.getName());
		assertEquals(expectTask1.getNotes(), actualTask.getNotes());
		assertEquals(expectTask1.getEstimate(), actualTask.getEstimate());
		assertEquals(expectTask1.getActual(), actualTask.getActual());
		assertEquals(expectTask1.getHandlerId(), actualTask.getHandlerId());

		// assert second task
		actualTask = tasks.get(1);
		assertEquals(expectTask2.getName(), actualTask.getName());
		assertEquals(expectTask2.getNotes(), actualTask.getNotes());
		assertEquals(expectTask2.getEstimate(), actualTask.getEstimate());
		assertEquals(expectTask2.getActual(), actualTask.getActual());
		assertEquals(expectTask2.getHandlerId(), actualTask.getHandlerId());
	}

	@Test
	public void testDeleteExistingTask() {
		long projectId = mCP.getAllProjects().get(0).getId();

		// add two task, no parent
		String TEST_NAME = "NEW_TEST_TASK_NAME_";
		String TEST_NOTE = "NEW_TEST_TASK_NOTE_";
		int TEST_EST = 5;
		int TEST_HANDLER = 1;
		int TEST_ACTUAL = 3;

		TaskObject expectTask1 = new TaskObject(projectId);
		expectTask1.setName(TEST_NAME + 1).setNotes(TEST_NOTE + 1)
				.setEstimate(TEST_EST).setHandlerId(TEST_HANDLER)
				.setActual(TEST_ACTUAL).save();

		TaskObject expectTask2 = new TaskObject(projectId);
		expectTask2.setName(TEST_NAME + 2).setNotes(TEST_NOTE + 2)
				.setEstimate(TEST_EST + 2).setHandlerId(TEST_HANDLER)
				.setActual(TEST_ACTUAL + 2).save();

		long[] deleteId = new long[2];
		deleteId[0] = expectTask1.getId();
		deleteId[1] = expectTask2.getId();

		// delete these tasks
		mSprintBacklogMapper.deleteExistingTask(deleteId);

		assertEquals(null, TaskObject.get(expectTask1.getId()));
		assertEquals(null, TaskObject.get(expectTask2.getId()));
	}

	@Test
	public void testDropTask() {
		long taskId = mATTS.getTasks().get(0).getId();

		mSprintBacklogMapper.dropTask(taskId);

		TaskObject task = TaskObject.get(taskId);
		assertEquals(TaskObject.NO_PARENT, task.getStoryId());
	}

	@Test
	public void testCloseStory() {
		String closeNote = "CLOSE_NOTE";
		String closeName = "CLOSE_NAME";
		StoryObject story = mASTS.getStories().get(0);
		long storyId = story.getId();
		Date updateTime = DateUtil.dayFillter("2015/03/30-11:35:27", DateUtil._16DIGIT_DATE_TIME);

		// story default status is UNCHECK
		assertEquals(StoryObject.STATUS_UNCHECK, story.getStatus());
		
		mSprintBacklogMapper.closeStory(storyId, closeName, closeNote, updateTime);
		story = StoryObject.get(storyId);
		assertEquals(StoryObject.STATUS_DONE, story.getStatus());
	}

	@Test
	public void testReopenStory() {
		String reopenName = "REOPEN_NAME";
		String reopenNote = "REOPEN_NOTE";
		String closeName = "CLOSE_NAME";
		StoryObject story = mASTS.getStories().get(0);
		long storyId = story.getId();
		Date updateTime = DateUtil.dayFillter("2015/03/30-11:35:27", DateUtil._16DIGIT_DATE_TIME);

		// story default status is UNCHECK
		assertEquals(StoryObject.STATUS_UNCHECK, story.getStatus());

		// set story's status to DONE and assert it
		mSprintBacklogMapper.closeStory(storyId, closeName, reopenNote, updateTime);
		story = StoryObject.get(storyId);
		assertEquals(StoryObject.STATUS_DONE, story.getStatus());
		
		// reopen the story
		updateTime = DateUtil.dayFillter("2015/03/30-11:40:27", DateUtil._16DIGIT_DATE_TIME);;
		mSprintBacklogMapper.reopenStory(storyId, reopenName, reopenName, updateTime);
		story = StoryObject.get(storyId);
		assertEquals(StoryObject.STATUS_UNCHECK, story.getStatus());
		assertEquals(reopenName, story.getName());
	}

	@Test
	public void testCloseTask() {
		String closeName = "CLOSE_NAME";
		String closeNote = "CLOSE_NOTE";
		int actual = 3;
		Date specificDate = new Date(System.currentTimeMillis());

		// assert status, default status should be UNCHECK
		TaskObject task = mATTS.getTasks().get(0);
		assertEquals(TaskObject.STATUS_UNCHECK, task.getStatus());
		assertEquals(8, task.getRemains());

		mSprintBacklogMapper.closeTask(task.getId(), closeName, closeNote, actual, specificDate);

		TaskObject closedTask = TaskObject.get(task.getId());
		assertEquals(closeName, closedTask.getName());
		assertEquals(closeNote, closedTask.getNotes());
		assertEquals(0, closedTask.getRemains());
		assertEquals(TaskObject.STATUS_DONE, closedTask.getStatus());
		assertEquals(specificDate.getTime(), closedTask.getUpdateTime());
	}

	@Test
	public void testResetTask() {
		String RESET_NAME = "RESET_NAME";
		String RESET_NOTE = "RESET_NOTE";
		Date SPECIFIC_DATE = new Date(System.currentTimeMillis());

		// assert status, default status should be UNCHECK
		TaskObject task = mATTS.getTasks().get(0);
		task.setStatus(TaskObject.STATUS_CHECK).save();
		assertEquals(TaskObject.STATUS_CHECK, task.getStatus());

		mSprintBacklogMapper.resetTask(task.getId(), RESET_NAME, RESET_NOTE, SPECIFIC_DATE);

		TaskObject resetTask = TaskObject.get(task.getId());
		assertEquals(RESET_NAME, resetTask.getName());
		assertEquals(RESET_NOTE, resetTask.getNotes());
		assertEquals(TaskObject.STATUS_UNCHECK, resetTask.getStatus());
		assertEquals(SPECIFIC_DATE.getTime(), resetTask.getUpdateTime());
	}

	@Test
	public void testReopenTask() {
		String REOPEN_NAME = "REOPEN_NAME";
		String REOPEN_NOTE = "REOPEN_NOTE";
		Date SPECIFIC_DATE = new Date(System.currentTimeMillis());

		// assert status, default status should be UNCHECK
		TaskObject task = mATTS.getTasks().get(0);
		task.setStatus(TaskObject.STATUS_DONE).save();
		assertEquals(TaskObject.STATUS_DONE, task.getStatus());

		mSprintBacklogMapper.reopenTask(task.getId(), REOPEN_NAME, REOPEN_NOTE, SPECIFIC_DATE);

		TaskObject reopenTask = TaskObject.get(task.getId());
		assertEquals(REOPEN_NAME, reopenTask.getName());
		assertEquals(REOPEN_NOTE, reopenTask.getNotes());
		assertEquals(TaskObject.STATUS_CHECK, reopenTask.getStatus());
		assertEquals(SPECIFIC_DATE.getTime(), reopenTask.getUpdateTime());
	}

	@Test
	public void testCheckOutTask() {
		String CHECKOUT_NAME = "CHECKOUT_NAME";
		String CHECKOUT_NOTE = "CHECKOUT_NOTE";
		long CHECKOUT_HANDLER = 1;
		ArrayList<Long> CHECKOUT_PARTNERS = new ArrayList<Long>();
		CHECKOUT_PARTNERS.add(1L);
		Date SPECIFIC_DATE = new Date(System.currentTimeMillis());

		// assert status, default status should be UNCHECK
		TaskObject task = mATTS.getTasks().get(0);
		assertEquals(TaskObject.STATUS_UNCHECK, task.getStatus());

		mSprintBacklogMapper.checkOutTask(task.getId(), CHECKOUT_NAME,
				CHECKOUT_HANDLER, CHECKOUT_PARTNERS, CHECKOUT_NOTE,
				SPECIFIC_DATE);

		TaskObject checkoutTask = TaskObject.get(task.getId());
		assertEquals(CHECKOUT_NAME, checkoutTask.getName());
		assertEquals(CHECKOUT_NOTE, checkoutTask.getNotes());
		assertEquals(TaskObject.STATUS_CHECK, checkoutTask.getStatus());
		assertEquals(1L, checkoutTask.getHandlerId());
		assertEquals(1L, checkoutTask.getPartnersId().get(0));
		assertEquals(SPECIFIC_DATE.getTime(), checkoutTask.getUpdateTime());
	}

	@Test
	public void testDeleteTask() {
		// get all tasks id
		ArrayList<Long> tasksId = mATTS.getTasksId();

		// delete the tasks
		for (long taskId : tasksId) {
			mSprintBacklogMapper.deleteTask(taskId);
		}

		// all tasks should be deleted
		for (long taskId : tasksId) {
			TaskObject task = TaskObject.get(taskId);
			assertEquals(null, task);
		}
	}
}
