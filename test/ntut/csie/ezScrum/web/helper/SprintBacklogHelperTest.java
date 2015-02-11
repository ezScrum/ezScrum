package ntut.csie.ezScrum.web.helper;

import static org.junit.Assert.assertEquals;

import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.jcis.resource.core.IProject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SprintBacklogHelperTest{
	private SprintBacklogHelper mSprintBacklogHelper;
	private Configuration mConfig = null;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
	
	@Before
	public void setUp() throws Exception  {
		// initialize database
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		InitialSQL ini = new InitialSQL(mConfig);
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
		IUserSession userSession = mConfig.getUserSession();
		String sprintId = "1";
		mSprintBacklogHelper = new SprintBacklogHelper(project, userSession, sprintId);
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
		mSprintBacklogHelper.updateTask(taskInfo, "account_not_exist_handler", "");
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
		mSprintBacklogHelper.updateTask(taskInfo, "account_handler", "account_partner1;account_partner2");
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
		MantisService mantisService = new MantisService(mConfig);
		mantisService.openConnect();
		IIssue story = mantisService.getIssue(storyId);
		mantisService.closeConnect();
		// check story tasks status before test
		List<Long> oldTaskIds = story.getChildrenId();
		assertEquals(3, oldTaskIds.size());
		assertEquals(1, oldTaskIds.get(0));
		assertEquals(2, oldTaskIds.get(1));
		assertEquals(3, oldTaskIds.get(2));
		// create a new task
		TaskObject newTask = new TaskObject(projectId);
		newTask.save();
		// add new existing task
		String newTaskStringId = String.valueOf(newTask.getId());
		String[] selectedTasksStringId = {newTaskStringId};
		mSprintBacklogHelper.addExistingTasksToStory(selectedTasksStringId, storyId);
		// get story again
		mantisService.openConnect();
		story = mantisService.getIssue(storyId);
		mantisService.closeConnect();
		// check story tasks status after add new existing task
		List<Long> newTaskIds = story.getChildrenId();
		assertEquals(4, newTaskIds.size());
		assertEquals(1, newTaskIds.get(0));
		assertEquals(2, newTaskIds.get(1));
		assertEquals(3, newTaskIds.get(2));
		assertEquals(10, newTaskIds.get(3));
	}

	@Test
	public void testAddExistingTasksToStory_WithTwoExistingTasks() {
		long projectId = 1;
		long storyId = 1;
		// get story
		MantisService mantisService = new MantisService(mConfig);
		mantisService.openConnect();
		IIssue story = mantisService.getIssue(storyId);
		mantisService.closeConnect();
		// check story tasks status before test
		List<Long> oldTaskIds = story.getChildrenId();
		assertEquals(3, oldTaskIds.size());
		assertEquals(1, oldTaskIds.get(0));
		assertEquals(2, oldTaskIds.get(1));
		assertEquals(3, oldTaskIds.get(2));
		// create a new task1
		TaskObject newTask1 = new TaskObject(projectId);
		newTask1.save();
		// create a new task1
		TaskObject newTask2 = new TaskObject(projectId);
		newTask2.save();
		// add new existing tasks
		String newTask1StringId = String.valueOf(newTask1.getId());
		String newTask2StringId = String.valueOf(newTask2.getId());
		String[] selectedTasksStringId = {newTask1StringId, newTask2StringId};
		mSprintBacklogHelper.addExistingTasksToStory(selectedTasksStringId, storyId);
		// get story again
		mantisService.openConnect();
		story = mantisService.getIssue(storyId);
		mantisService.closeConnect();
		// check story tasks status after add new existing task
		List<Long> newTaskIds = story.getChildrenId();
		assertEquals(5, newTaskIds.size());
		assertEquals(1, newTaskIds.get(0));
		assertEquals(2, newTaskIds.get(1));
		assertEquals(3, newTaskIds.get(2));
		assertEquals(10, newTaskIds.get(3));
		assertEquals(11, newTaskIds.get(4));
	}
}
