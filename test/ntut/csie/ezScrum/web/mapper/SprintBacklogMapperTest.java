package ntut.csie.ezScrum.web.mapper;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
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
		
		mASTS = new AddStoryToSprint(STORY_COUNT, STORY_ESTIMATE, mCS, mCP, CREATE_PRODUCTBACKLOG_TYPE);
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
		Map<Long, ArrayList<TaskObject>> tasksMap = mSprintBacklogMapper.getTasksMap();
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
		for (TaskObject task : tasks){
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
		for (int i = 0; i < tasks.size(); i++){
			assertEquals(i + 1, tasks.get(i).getId());
		}
	}
	
	@Test
	public void testGetDropedTasksMap() {
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
		ArrayList<TaskObject> tasks = mSprintBacklogMapper.getTasksByStoryId(-1);
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
		assertEquals(0, oldTask.getRemains());
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
		assertEquals(taskInfo.partnersId.get(0), actualTask.getPartnersId().get(0));
	}
	
	@Test
	public void testAddExistingTasks() {
		
	}
	
	@Test
	public void testGetTasksWithNoParent() {
	}
	
	@Test
	public void testDeleteExistingTask() {
	}
	
	@Test
	public void testDropTask() {
	}
	
	@Test
	public void testCloseStory() {
	}
	
	@Test
	public void testReopenStory() {
	}
	
	@Test
	public void testCloseTask() {
	}
	
	@Test
	public void testResetTask() {
	}
	
	@Test
	public void testReopenTask() {
	}
	
	@Test
	public void testCheckOutTask() {
	}
	
	@Test
	public void testDeleteTask() {
	}
}
