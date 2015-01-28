package ntut.csie.ezScrum.web.mapper;

import static org.junit.Assert.*;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SprintBacklogMapperTest {
	
	private SprintBacklogMapper mSprintBacklogMapper;
	private Configuration config = null;
	private CreateProject CP;
	private CreateSprint CS;
	private static long PROJECT_ID = 1;
	
	@Before
	public void setUp() throws Exception {
		config = new Configuration();
		config.setTestMode(true);
		config.save();
		
		InitialSQL ini = new InitialSQL(config);
		ini.exe();// 初始化 SQL
		
		CP = new CreateProject(1);
		CP.exeCreate();
		
		CS = new CreateSprint(1, CP);
		CS.exe();
		
		mSprintBacklogMapper = new SprintBacklogMapper(CP.getProjectList().get(0), config.getUserSession());
		
		ini = null;
	}
	
	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		config.setTestMode(false);
		config.save();
		
		ini = null;
		CP = null;
		mSprintBacklogMapper = null;
	}
	
	@Test
	public void testGetTasksMap() {
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
