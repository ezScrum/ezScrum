package ntut.csie.ezScrum.web.support;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.jcis.core.util.DateUtil;

import org.jdom.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SprintBacklogTreeStructureTest {
	private IIssue mStory;
	private TaskObject mTask;
	private SprintBacklogLogic mSprintBacklogLogic;
	private Configuration mConfiguration = null;
	private AccountObject mHandler;
	
	@Before
	public void setUp() {
		long projectId = 1;
		// initialize database
		mConfiguration = new Configuration();
		mConfiguration.setTestMode(true);
		mConfiguration.save();
		InitialSQL ini = new InitialSQL(mConfiguration);
		ini.exe();// 初始化 SQL
		ini = null;
		mStory = new Issue();
		mStory.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		mHandler = new AccountObject("account_handler");
		mHandler.save();
		mTask = new TaskObject(projectId);
		mTask.setHandlerId(mHandler.getId());
		mSprintBacklogLogic = new SprintBacklogLogic();
	}

	@After
	public void tearDown() {
		InitialSQL ini = new InitialSQL(mConfiguration);
		ini.exe();
		ini = null;
		mConfiguration.setTestMode(false);
		mConfiguration.save();
		mStory = null;
		mTask = null;
		mSprintBacklogLogic = null;
		mConfiguration = null;
	}

	/*-----------------------------------------------------------
	 *	測試根據 Task 歷史紀錄取得 Remaining Hours
	-------------------------------------------------------------*/
	// Sprint Date 沒有跳過假日
	@Test
	public void testRemainingBySprintDaysCase_1() {
		
		mSprintBacklogLogic.calculateSprintBacklogDateList(new Date(
				"2010/07/12"), 5);
		ArrayList<Date> dates = mSprintBacklogLogic.getCurrentDateList();
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		tasks.add(mTask);
		SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure(
				mStory, tasks, dates);
		List<SprintBacklogTreeStructure> trees = tree
				.GetTasksTreeListForTest();
		for (SprintBacklogTreeStructure taskTree : trees) {
			assertEquals("5.0", taskTree.GetDatetoRemainMap().get("Date_1"));
			assertEquals("0.0", taskTree.GetDatetoRemainMap().get("Date_2"));
			assertEquals("0.0", taskTree.GetDatetoRemainMap().get("Date_3"));
			assertEquals("0.0", taskTree.GetDatetoRemainMap().get("Date_4"));
			assertEquals("0.0", taskTree.GetDatetoRemainMap().get("Date_5"));
		}
	}

//	// Sprint Date 有跳過假日
//	@Test
//	public void testRemainingBySprintDaysCase_2() {
////		// Remaining Hours History 1
////		Element history1 = new Element(ScrumEnum.HISTORY_TAG);
////		history1.setAttribute(ScrumEnum.ID_HISTORY_ATTR, "20100714171219");
////		Element remains1 = new Element(ScrumEnum.REMAINS);
////		remains1.setText("13");
////		history1.addContent(remains1);
////		// Remaining Hours History 2
////		Element history2 = new Element(ScrumEnum.HISTORY_TAG);
////		history2.setAttribute(ScrumEnum.ID_HISTORY_ATTR, "20100716171219");
////		Element remains2 = new Element(ScrumEnum.REMAINS);
////		remains2.setText("8");
////		history2.addContent(remains2);
////		// Remaining Hours History 3
////		Element history3 = new Element(ScrumEnum.HISTORY_TAG);
////		history3.setAttribute(ScrumEnum.ID_HISTORY_ATTR, "20100719171219");
////		Element remains3 = new Element(ScrumEnum.REMAINS);
////		remains3.setText("3");
////		history3.addContent(remains3);
////		// Remaining Hours History 4
////		Element history4 = new Element(ScrumEnum.HISTORY_TAG);
////		history4.setAttribute(ScrumEnum.ID_HISTORY_ATTR, "20100720171219");
////		Element remains4 = new Element(ScrumEnum.REMAINS);
////		remains4.setText("0");
////		history4.addContent(remains4);
////		// 加入 History root
////		mRoot.addContent(history1);
////		mRoot.addContent(history2);
////		mRoot.addContent(history3);
////		mRoot.addContent(history4);
////		// 歷史紀錄的時間
////		Date hisDate1 = DateUtil.dayFillter(
////				history1.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR),
////				DateUtil._16DIGIT_DATE_TIME_2);
////		Date hisDate2 = DateUtil.dayFillter(
////				history2.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR),
////				DateUtil._16DIGIT_DATE_TIME_2);
////		Date hisDate3 = DateUtil.dayFillter(
////				history3.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR),
////				DateUtil._16DIGIT_DATE_TIME_2);
////		Date hisDate4 = DateUtil.dayFillter(
////				history4.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR),
////				DateUtil._16DIGIT_DATE_TIME_2);
////		// 將歷史紀錄設定至 Task
////		mTask.setTagContent(mRoot);
////		assertEquals("13", mTask.getTagValue(ScrumEnum.REMAINS, hisDate1));
////		assertEquals("8", mTask.getTagValue(ScrumEnum.REMAINS, hisDate2));
////		assertEquals("3", mTask.getTagValue(ScrumEnum.REMAINS, hisDate3));
////		assertEquals("0", mTask.getTagValue(ScrumEnum.REMAINS, hisDate4));
//		mSprintBacklogLogic.calculateSprintBacklogDateList(new Date(
//				"2010/07/14"), 5);
//		ArrayList<Date> dates = mSprintBacklogLogic.getCurrentDateList();
//		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
//		tasks.add(mTask);
//		SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure(
//				mStory, tasks, dates);
//		List<SprintBacklogTreeStructure> trees = tree
//				.GetTasksTreeListForTest();
//		for (SprintBacklogTreeStructure taskTree : trees) {
//			assertEquals("13.0", taskTree.GetDatetoRemainMap().get("Date_1"));
//			assertEquals("13.0", taskTree.GetDatetoRemainMap().get("Date_2"));
//			assertEquals("8.0", taskTree.GetDatetoRemainMap().get("Date_3"));
//			assertEquals("3.0", taskTree.GetDatetoRemainMap().get("Date_4"));
//			assertEquals("0.0", taskTree.GetDatetoRemainMap().get("Date_5"));
//		}
//	}
//
//	// History 沒有 Remain 將以 ESTIMATION 代替
//	@Test
//	public void testRemainingBySprintDaysCase_3() {
////		// Remaining Hours History 1
////		Element history1 = new Element(ScrumEnum.HISTORY_TAG);
////		history1.setAttribute(ScrumEnum.ID_HISTORY_ATTR, "20100712171219");
////		Element remains1 = new Element(ScrumEnum.ESTIMATION);
////		remains1.setText("5");
////		history1.addContent(remains1);
////		// Remaining Hours History 2
////		Element history2 = new Element(ScrumEnum.HISTORY_TAG);
////		history2.setAttribute(ScrumEnum.ID_HISTORY_ATTR, "20100714171219");
////		Element remains2 = new Element(ScrumEnum.REMAINS);
////		remains2.setText("0");
////		history2.addContent(remains2);
////		// 加入 History root
////		mRoot.addContent(history1);
////		mRoot.addContent(history2);
////		// 歷史紀錄的時間
////		Date hisDate1 = DateUtil.dayFillter(
////				history1.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR),
////				DateUtil._16DIGIT_DATE_TIME_2);
////		Date hisDate2 = DateUtil.dayFillter(
////				history2.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR),
////				DateUtil._16DIGIT_DATE_TIME_2);
////		// 將歷史紀錄設定至 Task
////		mTask.setTagContent(mRoot);
////		assertEquals("5", mTask.getTagValue(ScrumEnum.ESTIMATION, hisDate1));
////		assertEquals("0", mTask.getTagValue(ScrumEnum.REMAINS, hisDate2));
//		mSprintBacklogLogic.calculateSprintBacklogDateList(new Date(
//				"2010/07/12"), 3);
//		ArrayList<Date> dates = mSprintBacklogLogic.getCurrentDateList();
//		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
//		tasks.add(mTask);
//		SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure(
//				mStory, tasks, dates);
//		List<SprintBacklogTreeStructure> trees = tree
//				.GetTasksTreeListForTest();
//		for (SprintBacklogTreeStructure taskTree : trees) {
//			assertEquals("5.0", taskTree.GetDatetoRemainMap().get("Date_1"));
//			assertEquals("5.0", taskTree.GetDatetoRemainMap().get("Date_2"));
//			assertEquals("0.0", taskTree.GetDatetoRemainMap().get("Date_3"));
//		}
//	}
}
