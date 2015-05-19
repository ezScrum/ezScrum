package ntut.csie.ezScrum.web.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.jcis.core.util.DateUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SprintBacklogLogicTest {
	private SprintBacklogLogic mSprintBacklogLogic = null;
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

		// create test data
		int PROJECT_COUNT = 1;
		int SPRINT_COUNT = 1;
		int STORY_COUNT = 3;
		int STORY_ESTIMATE = 5;
		int TASK_COUNT = 3;
		int TASK_ESTIMATE = 8;
		String STORY_COLUMNBESET = "EST";

		mCP = new CreateProject(PROJECT_COUNT);
		mCP.exeCreate();

		mCS = new CreateSprint(SPRINT_COUNT, mCP);
		mCS.exe();

		mASTS = new AddStoryToSprint(STORY_COUNT, STORY_ESTIMATE, mCS, mCP,
				STORY_COLUMNBESET);
		mASTS.exe();

		mATTS = new AddTaskToStory(TASK_COUNT, TASK_ESTIMATE, mASTS, mCP);
		mATTS.exe();

		ProjectObject project = mCP.getAllProjects().get(0);
		long sprintId = mCS.getSprintsId().get(0);
		mSprintBacklogLogic = new SprintBacklogLogic(project, sprintId);
	}

	@After
	public void tearDown() {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		mSprintBacklogLogic = null;
		mConfig = null;
		ini = null;
		mCP = null;
		mCS = null;
		mASTS = null;
		mATTS = null;
	}

	@Test
	public void testCheckOutTask() {
		String username = "TEST_ACCOUNT_";
		String nickname = "TEST_ACCOUNT_NICKNAME_";
		String email = "TEST_EMAIL_";
		String password = "TEST_PAASWORD_";
		boolean enable = true;
		String CHECKOUT_NAME = "CHECKOUT_NAME";
		String CHECKOUT_NOTE = "CHECKOUT_NOTE";
		String CHECKOUT_TIME = "2015/01/29-16:00:00";

		// create 2 account
		for (int i = 0; i < 2; i++) {
			AccountObject account = new AccountObject(username + (i + 1));
			account.setNickName(nickname + (i + 1));
			account.setPassword(password + (i + 1));
			account.setEmail(email + (i + 1));
			account.setEnable(enable);
			account.save();
		}

		TaskObject checkOutTask = mATTS.getTasks().get(0);
		String checkoutHandlerUserName = "TEST_ACCOUNT_1";

		// prepare partnerList
		StringBuilder partnerUserNameBuilder = new StringBuilder();
		partnerUserNameBuilder.append("TEST_ACCOUNT_2");

		// check out task
		mSprintBacklogLogic.checkOutTask(checkOutTask.getId(), CHECKOUT_NAME,
				checkoutHandlerUserName, partnerUserNameBuilder.toString(),
				CHECKOUT_NOTE, CHECKOUT_TIME);
		checkOutTask.reload();
		// assert
		assertEquals(TaskObject.STATUS_CHECK, checkOutTask.getStatus());
		assertEquals("assigned", checkOutTask.getStatusString());
		assertEquals(2, checkOutTask.getHandlerId());
		assertEquals(3, checkOutTask.getPartnersId().get(0));
		assertEquals(
				DateUtil.dayFillter(CHECKOUT_TIME, DateUtil._16DIGIT_DATE_TIME)
						.getTime(), checkOutTask.getUpdateTime());
	}

	@Test
	public void testCloseTask() {
		String DONE_NAME = "CHECKOUT_NAME";
		String DONE_NOTE = "CHECKOUT_NOTE";
		String CHECKOUT_TIME = "2015/01/29-16:00:00";

		TaskObject task = mATTS.getTasks().get(0);
		TaskObject task2 = mATTS.getTasks().get(1);
		TaskObject task3 = mATTS.getTasks().get(2);

		// Done Issue
		mSprintBacklogLogic.closeTask(task.getId(), DONE_NAME, DONE_NOTE,
				task.getActual(), CHECKOUT_TIME);
		mSprintBacklogLogic.closeTask(task2.getId(), DONE_NAME, DONE_NOTE,
				task2.getActual(), CHECKOUT_TIME);

		task.reload();
		task2.reload();
		task3.reload();

		// assert
		// Task 1
		assertEquals(TaskObject.STATUS_DONE, task.getStatus());
		assertEquals("closed", task.getStatusString());
		assertEquals(DONE_NAME, task.getName());
		assertEquals(DONE_NOTE, task.getNotes());
		assertEquals(0, task.getRemains());
		assertEquals(
				DateUtil.dayFillter(CHECKOUT_TIME, DateUtil._16DIGIT_DATE_TIME)
						.getTime(), task.getUpdateTime());
		// Task 2
		assertEquals(TaskObject.STATUS_DONE, task2.getStatus());
		assertEquals("closed", task2.getStatusString());
		assertEquals(DONE_NAME, task2.getName());
		assertEquals(DONE_NOTE, task2.getNotes());
		assertEquals(0, task2.getRemains());
		assertEquals(
				DateUtil.dayFillter(CHECKOUT_TIME, DateUtil._16DIGIT_DATE_TIME)
						.getTime(), task2.getUpdateTime());
		// Task 3
		assertEquals(TaskObject.STATUS_UNCHECK, task3.getStatus());
		assertEquals("new", task3.getStatusString());
		assertTrue(task3.getName() != DONE_NAME);
		assertTrue(task3.getNotes() != DONE_NOTE);
		assertEquals(8, task3.getRemains());
		assertTrue(DateUtil.dayFillter(CHECKOUT_TIME,
				DateUtil._16DIGIT_DATE_TIME).getTime() != task3.getUpdateTime());
	}

	@Test
	public void testGetTaskCurrentEstimatePoint() {
		int STORY_COUNT = 3;
		int TASK_COUNT = 3;
		int TASK_ESTIMATE = 8;

		double totalEstimatePoint = mSprintBacklogLogic.getTaskEstimatePoints();
		double expectedTotalEstimatePont = TASK_COUNT * TASK_ESTIMATE
				* STORY_COUNT;
		assertEquals(expectedTotalEstimatePont, totalEstimatePoint);
	}

	@Test
	public void testGetTaskCurrnetRemainsPoint() {
		int STORY_COUNT = 3;
		int TASK_COUNT = 3;
		int TASK_ESTIMATE = 8;

		// Original Task remain hours
		double totalRemainPoint = mSprintBacklogLogic.getTaskRemainsPoints();
		double expectedTotalEstimatePont = TASK_COUNT * TASK_ESTIMATE
				* STORY_COUNT;
		assertEquals(expectedTotalEstimatePont, totalRemainPoint);

		// After done 1 task
		TaskObject doingTask = mATTS.getTasks().get(0);
		int estimate = doingTask.getEstimate();
		mSprintBacklogLogic.getSprintBacklogMapper().closeTask(
				doingTask.getId(), doingTask.getName(), doingTask.getNotes(),
				doingTask.getActual(), new Date());
		// assert
		totalRemainPoint = mSprintBacklogLogic.getTaskRemainsPoints();
		expectedTotalEstimatePont = TASK_COUNT * TASK_ESTIMATE * STORY_COUNT
				- estimate;
		assertEquals(expectedTotalEstimatePont, totalRemainPoint);

		// After done 2 task
		doingTask = mATTS.getTasks().get(1);
		estimate = doingTask.getEstimate();
		mSprintBacklogLogic.getSprintBacklogMapper().closeTask(
				doingTask.getId(), doingTask.getName(), doingTask.getNotes(),
				doingTask.getActual(), new Date());
		// assert
		totalRemainPoint = mSprintBacklogLogic.getTaskRemainsPoints();
		expectedTotalEstimatePont = TASK_COUNT * TASK_ESTIMATE * STORY_COUNT
				- (estimate * 2);
		assertEquals(expectedTotalEstimatePont, totalRemainPoint);

		// After done all tasks
		for (TaskObject task : mATTS.getTasks()) {
			mSprintBacklogLogic.getSprintBacklogMapper().closeTask(
					task.getId(), task.getName(), task.getNotes(),
					doingTask.getActual(), new Date());
		}
		// assert
		totalRemainPoint = mSprintBacklogLogic.getTaskRemainsPoints();
		assertEquals(0, totalRemainPoint);
	}

	@Test
	public void testGetStoryPoints() {
		assertEquals(15, mSprintBacklogLogic.getTotalStoryPoints());
	}

	@Test
	public void testGetStoryUnclosedPoint() {
		// close 2 stories, should remain 1 story not closed
		mASTS.getStories().get(0).setStatus(StoryObject.STATUS_DONE).save();
		mASTS.getStories().get(1).setStatus(StoryObject.STATUS_DONE).save();
		assertEquals(5, mSprintBacklogLogic.getStoryUnclosedPoints());
	}

	@Test
	public void testGetStories() {
		ArrayList<StoryObject> stories = mSprintBacklogLogic.getStories();
		StoryObject story1 = stories.get(0);
		StoryObject story2 = stories.get(1);
		StoryObject story3 = stories.get(2);
		assertEquals(true, story1.getId() < story2.getId());
		assertEquals(true, story2.getId() < story3.getId());
	}

	@Test
	public void testGetStoriesByImp() {
		ProjectObject project = mCP.getAllProjects().get(0);
		for (int i = 0; i < 5; i++) {
			StoryObject story = new StoryObject(project.getId());
			story.setName("TEST_" + (i + 1)).setNotes("TEST_NOTES_" + (i + 1))
					.setSprintId(1).setHowToDemo("barbarbar")
					.setImportance(i * 2 + 5).save();
		}

		ArrayList<StoryObject> stories = mSprintBacklogLogic.getStoriesByImp();
		for (int i = 0; i < stories.size() - 1; i++) {
			int importance1 = stories.get(i).getImportance();
			int importance2 = stories.get(i + 1).getImportance();
			assertTrue(importance1 >= importance2);
		}

	}
}
