package ntut.csie.ezScrum.web.helper;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.tools.javac.comp.Check;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.SprintBacklogDateColumn;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;

public class SprintBacklogHelperTest{
	private SprintBacklogHelper mSprintBacklogHelper;
	private Configuration mConfiguration = null;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
	
	@Before
	public void setUp() throws Exception  {
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
		String sprintId = "1";
		mSprintBacklogHelper = new SprintBacklogHelper(project, userSession, sprintId);
    }

	@After
	public void tearDown() throws Exception {
    	InitialSQL ini = new InitialSQL(mConfiguration);
		ini.exe();
		ini = null;
		mCP = null;
		mSprintBacklogHelper = null;
		mConfiguration.setTestMode(false);
		mConfiguration.save();
    }
	
	@Test
	public void testRemoveTask()
	{
		// get task one
		TaskObject task = TaskObject.get(1);
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
}
