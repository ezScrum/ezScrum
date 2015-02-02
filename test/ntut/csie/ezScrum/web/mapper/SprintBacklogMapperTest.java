package ntut.csie.ezScrum.web.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.jcis.resource.core.IProject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SprintBacklogMapperTest {

	private SprintBacklogMapper mSprintBacklogMapper;
	private Configuration mConfiguration = null;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
	private static long PROJECT_ID = 1;

	@Before
	public void setUp() throws Exception {
		// initialize database
		mConfiguration = new Configuration();
		mConfiguration.setTestMode(true);
		mConfiguration.save();
		InitialSQL ini = new InitialSQL(mConfiguration);
		ini.exe();// 初始化 SQL
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

		IProject project = mCP.getProjectList().get(0);
		IUserSession userSession = mConfiguration.getUserSession();
		mSprintBacklogMapper = new SprintBacklogMapper(project, userSession);
	}

	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfiguration);
		ini.exe();
		ini = null;
		mCP = null;
		mSprintBacklogMapper = null;
		mConfiguration.setTestMode(false);
		mConfiguration.save();
	}

	@Test
	public void testGetTasksMap() {
		Map<Long, ArrayList<TaskObject>> tasksMap = mSprintBacklogMapper
				.getTasksMap();
		assertEquals(3, tasksMap.size());
		assertTrue(tasksMap.containsKey(1L));
		assertTrue(tasksMap.containsKey(2L));
		assertTrue(tasksMap.containsKey(3L));
		ArrayList<TaskObject> tasksInStory1 = tasksMap.get(1L);
		assertEquals(3, tasksInStory1.size());
		// check project id in tasksInStory1
		assertEquals(1, tasksInStory1.get(0).getProjectId());
		assertEquals(1, tasksInStory1.get(1).getProjectId());
		assertEquals(1, tasksInStory1.get(2).getProjectId());
		// check story id in tasksInStory1
		assertEquals(1, tasksInStory1.get(0).getStoryId());
		assertEquals(1, tasksInStory1.get(1).getStoryId());
		assertEquals(1, tasksInStory1.get(2).getStoryId());
		// check task id in tasksInStory1
		assertEquals(1, tasksInStory1.get(0).getId());
		assertEquals(2, tasksInStory1.get(1).getId());
		assertEquals(3, tasksInStory1.get(2).getId());
		ArrayList<TaskObject> tasksInStory2 = tasksMap.get(2L);
		assertEquals(3, tasksInStory2.size());
		// check project id in tasksInStory2
		assertEquals(1, tasksInStory2.get(0).getProjectId());
		assertEquals(1, tasksInStory2.get(1).getProjectId());
		assertEquals(1, tasksInStory2.get(2).getProjectId());
		// check story id in tasksInStory2
		assertEquals(2, tasksInStory2.get(0).getStoryId());
		assertEquals(2, tasksInStory2.get(1).getStoryId());
		assertEquals(2, tasksInStory2.get(2).getStoryId());
		// check task id in tasksInStory2
		assertEquals(4, tasksInStory2.get(0).getId());
		assertEquals(5, tasksInStory2.get(1).getId());
		assertEquals(6, tasksInStory2.get(2).getId());
		ArrayList<TaskObject> tasksInStory3 = tasksMap.get(3L);
		assertEquals(3, tasksInStory3.size());
		// check project id in tasksInStory3
		assertEquals(1, tasksInStory3.get(0).getProjectId());
		assertEquals(1, tasksInStory3.get(1).getProjectId());
		assertEquals(1, tasksInStory3.get(2).getProjectId());
		// check story id in tasksInStory3
		assertEquals(3, tasksInStory3.get(0).getStoryId());
		assertEquals(3, tasksInStory3.get(1).getStoryId());
		assertEquals(3, tasksInStory3.get(2).getStoryId());
		// check task id in tasksInStory3
		assertEquals(7, tasksInStory3.get(0).getId());
		assertEquals(8, tasksInStory3.get(1).getId());
		assertEquals(9, tasksInStory3.get(2).getId());
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
	public void testGetDroppedTasksMap() {
		// check dropped stories before test
		Map<Long, ArrayList<TaskObject>> droppedTasksMap = mSprintBacklogMapper.getDroppedTasksMap();
		assertEquals(0, droppedTasksMap.size());
		// create product backlog helper
		IProject project = mCP.getProjectList().get(0);
		IUserSession userSession = mConfiguration.getUserSession();
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(userSession, project);
		// remove story 2 from sprint
		productBacklogHelper.removeStoryFromSprint(1);
		// check dropped stories after add a dropped story
		droppedTasksMap = mSprintBacklogMapper.getDroppedTasksMap();
		assertEquals(3, droppedTasksMap.get(1).size());
		assertEquals(1, droppedTasksMap.get(1).get(0).getId());
		assertEquals(2, droppedTasksMap.get(1).get(1).getId());
		assertEquals(3, droppedTasksMap.get(1).get(2).getId());
	}

	@Test
	public void testGetAllStories() {
		String projectName = mCP.getProjectList().get(0).getName();
		List<IIssue> stories = mSprintBacklogMapper.getAllStories("Story");
		assertEquals(3, stories.size());
		// check project id
		assertEquals(projectName, stories.get(0).getProjectID());
		assertEquals(projectName, stories.get(1).getProjectID());
		assertEquals(projectName, stories.get(2).getProjectID());
		// check story id
		assertEquals(1, stories.get(0).getIssueID());
		assertEquals(2, stories.get(1).getIssueID());
		assertEquals(3, stories.get(2).getIssueID());
		// check issue type
		assertEquals("Story", stories.get(0).getCategory());
		assertEquals("Story", stories.get(1).getCategory());
		assertEquals("Story", stories.get(2).getCategory());
	}

	@Test
	public void testGetStoriesBySprintId_WithNotExistSprintId() {
		IIssue[] stories = mSprintBacklogMapper.getStoriesBySprintId(-1);
		assertEquals(0, stories.length);
	}

	@Test
	public void testGetStoriesBySprintId() {
		String projectName = mCP.getProjectList().get(0).getName();
		IIssue[] stories = mSprintBacklogMapper.getStoriesBySprintId(1);
		assertEquals(3, stories.length);
		// check project id
		assertEquals(projectName, stories[0].getProjectID());
		assertEquals(projectName, stories[1].getProjectID());
		assertEquals(projectName, stories[2].getProjectID());
		// check story id
		assertEquals(1, stories[0].getIssueID());
		assertEquals(2, stories[1].getIssueID());
		assertEquals(3, stories[2].getIssueID());
		// check issue type
		assertEquals("Story", stories[0].getCategory());
		assertEquals("Story", stories[1].getCategory());
		assertEquals("Story", stories[2].getCategory());
	}

	@Test
	public void testGetTasksByStoryId_WithNotExistStory() {
		ArrayList<TaskObject> tasks = mSprintBacklogMapper
				.getTasksByStoryId(-1);
		assertEquals(0, tasks.size());
	}

	@Test
	public void testGetTasksByStoryId_WithStory1() {
		ArrayList<TaskObject> tasks = mSprintBacklogMapper.getTasksByStoryId(1);
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
		ArrayList<TaskObject> tasks = mSprintBacklogMapper.getTasksByStoryId(2);
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
		ArrayList<TaskObject> tasks = mSprintBacklogMapper.getTasksByStoryId(3);
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
		IIssue[] droppedStories = mSprintBacklogMapper.getDroppedStories();
		assertEquals(0, droppedStories.length);
		List<IIssue> allStories = mSprintBacklogMapper.getAllStories("Story");
		assertEquals("1", allStories.get(0).getSprintID());
		assertEquals("1", allStories.get(1).getSprintID());
		assertEquals("1", allStories.get(2).getSprintID());
		// create product backlog helper
		IProject project = mCP.getProjectList().get(0);
		IUserSession userSession = mConfiguration.getUserSession();
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(userSession, project);
		// remove story 2 from sprint
		productBacklogHelper.removeStoryFromSprint(1);
		// check dropped stories after add a dropped story
		droppedStories = mSprintBacklogMapper.getDroppedStories();
		allStories = mSprintBacklogMapper.getAllStories("Story");
		assertEquals("1", allStories.get(0).getSprintID());
		assertEquals("0", allStories.get(1).getSprintID());
		assertEquals("1", allStories.get(2).getSprintID());
		assertEquals(1, droppedStories.length);
	}

	@Test
	public void testGetStory_WithNotExistStoryId() {
		IIssue story = mSprintBacklogMapper.getStory(-1);
		assertEquals(null, story);
	}

	@Test
	public void testGetStory() {
		String projectName = mCP.getProjectList().get(0).getName();
		IIssue story1 = mSprintBacklogMapper.getStory(1);
		IIssue story2 = mSprintBacklogMapper.getStory(2);
		IIssue story3 = mSprintBacklogMapper.getStory(3);
		// check project id
		assertEquals(projectName, story1.getProjectName());
		assertEquals(projectName, story2.getProjectName());
		assertEquals(projectName, story3.getProjectName());
		// check story id
		assertEquals(1, story1.getIssueID());
		assertEquals(2, story2.getIssueID());
		assertEquals(3, story3.getIssueID());
		// check issue type
		assertEquals("Story", story1.getCategory());
		assertEquals("Story", story2.getCategory());
		assertEquals("Story", story3.getCategory());
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
		// get old task id = 1
		TaskObject oldTask = TaskObject.get(1);
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
		taskInfo.taskId = 1;
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
		mSprintBacklogMapper.updateTask(taskInfo);
		// get new task
		TaskObject newTask = TaskObject.get(1);
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

		long taskId = mSprintBacklogMapper.addTask(PROJECT_ID, taskInfo);

		TaskObject actualTask = TaskObject.get(taskId);
		assertEquals(taskInfo.name, actualTask.getName());
		assertEquals(taskInfo.notes, actualTask.getNotes());
		assertEquals(taskInfo.estimate, actualTask.getEstimate());
		assertEquals(taskInfo.estimate, actualTask.getRemains());
		assertEquals(0, actualTask.getActual());
		assertEquals(taskInfo.handlerId, actualTask.getHandlerId());
		assertEquals(taskInfo.partnersId.get(0), actualTask.getPartnersId()
				.get(0));
	}

	@Test
	public void testAddExistingTasks() {
		// get story
		MantisService mantisService = new MantisService(mConfiguration);
		mantisService.openConnect();
		IIssue story = mantisService.getIssue(1);
		mantisService.closeConnect();
		// check story tasks status before test
		List<Long> oldTaskIds = story.getChildrenId();
		assertEquals(3, oldTaskIds.size());
		assertEquals(1, oldTaskIds.get(0));
		assertEquals(2, oldTaskIds.get(1));
		assertEquals(3, oldTaskIds.get(2));
		// create a new task
		TaskObject newTask = new TaskObject(1);
		newTask.save();
		// check new task status before be added to story
		assertEquals(1, newTask.getProjectId());
		assertEquals(-1, newTask.getStoryId());
		assertEquals(10, newTask.getId());
		// add new task to story
		ArrayList<Long> tasksId = new ArrayList<Long>();
		tasksId.add(10L);
		mSprintBacklogMapper.addExistingTasks(tasksId, 1);
		// get story again
		mantisService.openConnect();
		story = mantisService.getIssue(1);
		mantisService.closeConnect();
		// check story tasks status after add new task
		List<Long> newTaskIds = story.getChildrenId();
		assertEquals(4, newTaskIds.size());
		assertEquals(1, newTaskIds.get(0));
		assertEquals(2, newTaskIds.get(1));
		assertEquals(3, newTaskIds.get(2));
		assertEquals(10, newTaskIds.get(3));
	}

	@Test
	public void testAddExistingTasks_WithTwoExistingTasks() {
		// get story
		MantisService mantisService = new MantisService(mConfiguration);
		mantisService.openConnect();
		IIssue story = mantisService.getIssue(1);
		mantisService.closeConnect();
		// check story tasks status before test
		List<Long> oldTaskIds = story.getChildrenId();
		assertEquals(3, oldTaskIds.size());
		assertEquals(1, oldTaskIds.get(0));
		assertEquals(2, oldTaskIds.get(1));
		assertEquals(3, oldTaskIds.get(2));
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
		// check new task status before be added to story
		assertEquals(1, newTask2.getProjectId());
		assertEquals(-1, newTask2.getStoryId());
		assertEquals(11, newTask2.getId());
		// add new task 1 and new task 2 to story
		ArrayList<Long> tasksId = new ArrayList<Long>();
		tasksId.add(10L);
		tasksId.add(11L);
		mSprintBacklogMapper.addExistingTasks(tasksId, 1);
		// get story again
		mantisService.openConnect();
		story = mantisService.getIssue(1);
		mantisService.closeConnect();
		// check story tasks status after add new task
		List<Long> newTaskIds = story.getChildrenId();
		assertEquals(5, newTaskIds.size());
		assertEquals(1, newTaskIds.get(0));
		assertEquals(2, newTaskIds.get(1));
		assertEquals(3, newTaskIds.get(2));
		assertEquals(10, newTaskIds.get(3));
		assertEquals(11, newTaskIds.get(4));
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
		String CLOSE_NOTE = "CLOSE_NOTE";

		IIssue story = mASTS.getStories().get(0);
		long storyId = story.getIssueID();

		// story default status is NEW
		assertEquals("new", story.getStatus());

		mSprintBacklogMapper.closeStory(storyId, CLOSE_NOTE, "");

		MantisService service = new MantisService(mConfiguration);
		service.openConnect();
		story = service.getIssue(storyId);
		service.closeConnect();

		assertEquals("closed", story.getStatus());
	}

	@Test
	public void testReopenStory() {
		String REOPEN_NAME = "REOPEN_NAME";
		String REOPEN_NOTE = "REOPEN_NOTE";

		IIssue story = mASTS.getStories().get(0);
		long storyId = story.getIssueID();

		// story default status is NEW
		assertEquals("new", story.getStatus());

		MantisService service = new MantisService(mConfiguration);
		service.openConnect();

		// set story's status to CLOSED and assert it
		service.changeStatusToClosed(storyId, ITSEnum.FIXED_RESOLUTION,
				"", new Date());
		story = service.getIssue(storyId);
		assertEquals("closed", story.getStatus());
		
		// reopen the story
		mSprintBacklogMapper.reopenStory(storyId, REOPEN_NAME, REOPEN_NOTE, "");
		
		story = service.getIssue(storyId);
		service.closeConnect();
		assertEquals("new", story.getStatus());
		assertEquals(REOPEN_NAME, story.getSummary());
	}

	@Test
	public void testCloseTask() {
		String CLOSE_NAME = "CLOSE_NAME";
		String CLOSE_NOTE = "CLOSE_NOTE";
		Date SPECIFIC_DATE = new Date(System.currentTimeMillis());

		// assert status, default status should be UNCHECK
		TaskObject task = mATTS.getTasks().get(0);
		assertEquals(TaskObject.STATUS_UNCHECK, task.getStatus());
		assertEquals(8, task.getRemains());

		mSprintBacklogMapper.closeTask(task.getId(), CLOSE_NAME, CLOSE_NOTE,
				SPECIFIC_DATE);

		TaskObject closedTask = TaskObject.get(task.getId());
		assertEquals(CLOSE_NAME, closedTask.getName());
		assertEquals(CLOSE_NOTE, closedTask.getNotes());
		assertEquals(0, closedTask.getRemains());
		assertEquals(TaskObject.STATUS_DONE, closedTask.getStatus());
		assertEquals(SPECIFIC_DATE.getTime(), closedTask.getUpdateTime());
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

		mSprintBacklogMapper.resetTask(task.getId(), RESET_NAME, RESET_NOTE,
				SPECIFIC_DATE);

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

		mSprintBacklogMapper.reopenTask(task.getId(), REOPEN_NAME, REOPEN_NOTE,
				SPECIFIC_DATE);

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
