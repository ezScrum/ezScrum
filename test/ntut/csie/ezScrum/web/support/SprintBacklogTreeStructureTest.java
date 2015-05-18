package ntut.csie.ezScrum.web.support;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.jcis.core.util.DateUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SprintBacklogTreeStructureTest {
	private StoryObject mStory;
	private TaskObject mTask;
	private SprintBacklogLogic mSprintBacklogLogic;
	private Configuration mConfig = null;
	private AccountObject mHandler = null;
	private CreateProject mCP = null;
	private final static int mPROJECT_COUNT = 1;
	private long mProjectId = -1;
	
	@Before
	public void setUp() {
		// initialize database
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();// 初始化 SQL
		ini = null;
		
		// create project
		mCP = new CreateProject(mPROJECT_COUNT);
		mCP.exeCreate();
		mProjectId = mCP.getAllProjects().get(0).getId();
		// create story
		mStory = new StoryObject(mProjectId);
		mStory.save();
		// create handler
		mHandler = new AccountObject("account_handler");
		mHandler.save();
		// create a task
		mTask = new TaskObject(mProjectId);
		mTask.setHandlerId(mHandler.getId());
		mTask.setEstimate(10);
		mTask.save();
		mSprintBacklogLogic = new SprintBacklogLogic();
	}

	@After
	public void tearDown() {
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
		mStory = null;
		mTask = null;
		mHandler = null;
		mSprintBacklogLogic = null;
		mConfig = null;
	}

	/*-----------------------------------------------------------
	 *	測試根據 Task 歷史紀錄取得 Remaining Hours
	-------------------------------------------------------------*/
	// Sprint Date 沒有跳過假日
	@Test
	public void testTaskRemainsByHistories() {
		// check task remains before test
		Date taskCreateDate = new Date(mTask.getCreateTime());
		assertEquals(10.0, mTask.getRemains(taskCreateDate));
		// add a change remains history
		mTask.setRemains(9);
		mTask.save(DateUtil.dayFillter("2010/07/12", DateUtil._8DIGIT_DATE_1).getTime());
		// add a change remains history
		mTask.setRemains(8);
		mTask.save(DateUtil.dayFillter("2010/07/13", DateUtil._8DIGIT_DATE_1).getTime());
		// add a change remains history
		mTask.setRemains(7);
		mTask.save(DateUtil.dayFillter("2010/07/14", DateUtil._8DIGIT_DATE_1).getTime());
		// add a change remains history
		mTask.setRemains(6);
		mTask.save(DateUtil.dayFillter("2010/07/15", DateUtil._8DIGIT_DATE_1).getTime());
		// add a change remains history
		mTask.setRemains(5);
		mTask.save(DateUtil.dayFillter("2010/07/16", DateUtil._8DIGIT_DATE_1).getTime());
		mSprintBacklogLogic.calculateSprintBacklogDateList(DateUtil.dayFillter("2010/07/12", DateUtil._8DIGIT_DATE_1), 5);
		ArrayList<Date> dates = mSprintBacklogLogic.getCurrentDateList();
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		tasks.add(mTask);
		SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure(mStory, tasks, dates);
		List<SprintBacklogTreeStructure> trees = tree.GetTasksTreeListForTest();
		for (SprintBacklogTreeStructure taskTree : trees) {
			assertEquals("9.0", taskTree.GetDatetoRemainMap().get("Date_1"));
			assertEquals("8.0", taskTree.GetDatetoRemainMap().get("Date_2"));
			assertEquals("7.0", taskTree.GetDatetoRemainMap().get("Date_3"));
			assertEquals("6.0", taskTree.GetDatetoRemainMap().get("Date_4"));
			assertEquals("5.0", taskTree.GetDatetoRemainMap().get("Date_5"));
		}
	}
	
	@Test
	public void testTaskRemainsByHistories_2() {
		// check task remains before test
		Date taskCreateDate = new Date(mTask.getCreateTime());
		assertEquals(10.0, mTask.getRemains(taskCreateDate));
		// add a change remains history on 2010/7/12
		mTask.setRemains(5);
		mTask.save(DateUtil.dayFillter("2010/07/12", DateUtil._8DIGIT_DATE_1).getTime());
		// add a change remains history on 2010/7/14
		mTask.setRemains(0);
		mTask.save(DateUtil.dayFillter("2010/07/14", DateUtil._8DIGIT_DATE_1).getTime());
		mSprintBacklogLogic.calculateSprintBacklogDateList(DateUtil.dayFillter("2010/07/12", DateUtil._8DIGIT_DATE_1), 3);
		ArrayList<Date> dates = mSprintBacklogLogic.getCurrentDateList();
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		tasks.add(mTask);
		SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure(mStory, tasks, dates);
		List<SprintBacklogTreeStructure> trees = tree.GetTasksTreeListForTest();
		for (SprintBacklogTreeStructure taskTree : trees) {
			assertEquals("5.0", taskTree.GetDatetoRemainMap().get("Date_1"));
			assertEquals("5.0", taskTree.GetDatetoRemainMap().get("Date_2"));
			assertEquals("0.0", taskTree.GetDatetoRemainMap().get("Date_3"));
		}
	}

	// Sprint Date 有跳過假日
	@Test
	public void testTaskRemainsByHistories_WithPassHoliday() {
		// check task remains before test
		Date taskCreateDate = new Date(mTask.getCreateTime());
		assertEquals(10.0, mTask.getRemains(taskCreateDate));
		// add a change remains history on 2010/7/14
		mTask.setRemains(13);
		mTask.save(DateUtil.dayFillter("2010/07/14", DateUtil._8DIGIT_DATE_1).getTime());
		// add a change remains history on 2010/7/16
		mTask.setRemains(8);
		mTask.save(DateUtil.dayFillter("2010/07/16", DateUtil._8DIGIT_DATE_1).getTime());
		// add a change remains history on 2010/7/19
		mTask.setRemains(3);
		mTask.save(DateUtil.dayFillter("2010/07/19", DateUtil._8DIGIT_DATE_1).getTime());
		// add a change remains history on 2010/7/20
		mTask.setRemains(0);
		mTask.save(DateUtil.dayFillter("2010/07/20", DateUtil._8DIGIT_DATE_1).getTime());
		mSprintBacklogLogic.calculateSprintBacklogDateList(DateUtil.dayFillter("2010/07/14", DateUtil._8DIGIT_DATE_1), 5);
		ArrayList<Date> dates = mSprintBacklogLogic.getCurrentDateList();
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		tasks.add(mTask);
		SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure(mStory, tasks, dates);
		List<SprintBacklogTreeStructure> trees = tree.GetTasksTreeListForTest();
		for (SprintBacklogTreeStructure taskTree : trees) {
			assertEquals("13.0", taskTree.GetDatetoRemainMap().get("Date_1"));
			assertEquals("13.0", taskTree.GetDatetoRemainMap().get("Date_2"));
			assertEquals("8.0", taskTree.GetDatetoRemainMap().get("Date_3"));
			assertEquals("3.0", taskTree.GetDatetoRemainMap().get("Date_4"));
			assertEquals("0.0", taskTree.GetDatetoRemainMap().get("Date_5"));
		}
	}
	
	// History 沒有 Remain 將以 ESTIMATION 代替
	@Test
	public void testTaskRemainsByHistories_WithNoChangeRemainsHistory() {
		// check task remains before test
		Date taskCreateDate = new Date(mTask.getCreateTime());
		assertEquals(10.0, mTask.getRemains(taskCreateDate));
		mSprintBacklogLogic.calculateSprintBacklogDateList(DateUtil.dayFillter("2010/07/12", DateUtil._8DIGIT_DATE_1), 3);
		ArrayList<Date> dates = mSprintBacklogLogic.getCurrentDateList();
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		tasks.add(mTask);
		SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure(mStory, tasks, dates);
		List<SprintBacklogTreeStructure> trees = tree.GetTasksTreeListForTest();
		for (SprintBacklogTreeStructure taskTree : trees) {
			assertEquals("10.0", taskTree.GetDatetoRemainMap().get("Date_1"));
			assertEquals("10.0", taskTree.GetDatetoRemainMap().get("Date_2"));
			assertEquals("10.0", taskTree.GetDatetoRemainMap().get("Date_3"));
		}
	}
}
