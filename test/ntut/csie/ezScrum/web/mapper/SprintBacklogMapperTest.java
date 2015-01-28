package ntut.csie.ezScrum.web.mapper;

import static org.junit.Assert.assertEquals;

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
		int PROJECT_AMOUNT = 1;
		int SPRINT_AMOUNT = 1;
		int STORY_AMOUNT = 3;
		int STORY_ESTIMATE = 5;
		int TASK_AMOUNT = 3;
		int TASK_ESTIMATE = 8;
		String CREATE_PRODUCTBACKLOG_TYPE = "EST";
		mCP = new CreateProject(PROJECT_AMOUNT);
		mCP.exeCreate();
		mCS = new CreateSprint(SPRINT_AMOUNT, mCP);
		mCS.exe();
		mASTS = new AddStoryToSprint(STORY_AMOUNT, STORY_ESTIMATE, mCS, mCP, CREATE_PRODUCTBACKLOG_TYPE);
		mASTS.exe();
		mATTS = new AddTaskToStory(TASK_AMOUNT, TASK_ESTIMATE, mASTS, mCP);
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
		List<IIssue> stories = mASTS.getStories();
		ArrayList<TaskObject> tasksInStory1 = tasksMap.get(0);
		ArrayList<TaskObject> tasksInStory2 = tasksMap.get(1);
		ArrayList<TaskObject> tasksInStory3 = tasksMap.get(2);
	}
	
	@Test
	public void testGetAllTasks() {
	}
	
	@Test
	public void testGetDropedTasksMap() {
	}
	
	@Test
	public void testGetAllStories() {
	}
	
	@Test
	public void testGetStoriesBySprintId() {
	}
	
	@Test
	public void testGetTasksByStoryId() {
	}
	
	@Test
	public void testGetDroppedStories() {
	}
	
	@Test
	public void testGetStory() {
	}
	
	@Test
	public void testGetTask() {
	}
	
	@Test
	public void testUpdateTask() {
	}
	
	@Test
	public void testAddTask() {
		TaskInfo taskInfo = createTaskInfo(1);
		
		long taskId = mSprintBacklogMapper.addTask(PROJECT_ID, taskInfo);
		
		TaskObject actualTask = TaskObject.get(taskId);
		assertEquals(taskInfo.name, actualTask.getName());
		assertEquals(taskInfo.notes, actualTask.getNotes());
		assertEquals(taskInfo.estimate, actualTask.getEstimate());
		assertEquals(0, actualTask.getActual());
		assertEquals(taskInfo.handlerId, actualTask.getHandlerId());
		assertEquals(taskInfo.estimate, actualTask.getRemains());
		assertEquals(taskInfo.partnersId.get(0), actualTask.getPartnersId().get(0));
	}
	
	@Test
	public void testAddExistingTask() {
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
	
	private TaskInfo createTaskInfo(int id) {
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.name = "TEST_TASK_NAME_" + id;
		taskInfo.notes = "TEST_TASK_NOTES_" + id;
		taskInfo.handlerId = id;
		taskInfo.estimate = id;
		taskInfo.actualHour = id;
		
		ArrayList<Long> partnersId = new ArrayList<Long>();
		partnersId.add((long)id);
		taskInfo.partnersId = partnersId;
		
		taskInfo.specificTime = System.currentTimeMillis();
		
		return taskInfo;
	}
}
