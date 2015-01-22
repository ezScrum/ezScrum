package ntut.csie.ezScrum.web.mapper;

import java.util.ArrayList;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class SprintBacklogMapperTest extends TestCase {
	
	private SprintBacklogMapper mSprintBacklogMapper;
	private Configuration config = null;
	private CreateProject CP;
	private CreateSprint CS;
	private static long PROJECT_ID = 1;
	
	@Override
	protected void setUp() throws Exception {
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
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		config.setTestMode(false);
		config.save();
		
		ini = null;
		CP = null;
		mSprintBacklogMapper = null;
		
		super.tearDown();
	}
	
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
	
	private TaskInfo createTaskInfo(int id) {
		TaskInfo taskInfo = new TaskInfo(PROJECT_ID);
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
