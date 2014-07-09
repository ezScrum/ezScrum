package ntut.csie.ezScrum.web.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.jcis.core.util.DateUtil;

import org.jdom.Element;

public class SprintBacklogTreeStructureTest extends TestCase {
	private IIssue story;
	private IIssue task;
	private Element root;
	private SprintBacklogLogic sprintBacklogLogic;
	private Configuration configuration = null;
	
	public SprintBacklogTreeStructureTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp(){
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		this.story = new Issue();
		this.story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		this.task = new Issue();
		this.task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
		this.root = new Element(ScrumEnum.ROOT_TAG);
		this.sprintBacklogLogic = new SprintBacklogLogic();
    }

    protected void tearDown(){
    	configuration.setTestMode(false);
		configuration.store();
    	this.story = null;
    	this.task = null;
    	this.root = null;
    	this.sprintBacklogLogic = null;
    	configuration = null;
    }
    
    /*-----------------------------------------------------------
	*	測試根據 Task 歷史紀錄取得 Remaining Hours
	-------------------------------------------------------------*/
    // Sprint Date 沒有跳過假日
	public void testRemainingBySprintDaysCase_1()
	{
		// Remaining Hours History 1
        Element history_1 = new Element(ScrumEnum.HISTORY_TAG);        
        history_1.setAttribute(ScrumEnum.ID_HISTORY_ATTR, "20100712171219");
        Element remains = new Element(ScrumEnum.REMAINS);        
        remains.setText("5");
        history_1.addContent(remains);
        // Remaining Hours History 2
        Element history_2 = new Element(ScrumEnum.HISTORY_TAG);        
        history_2.setAttribute(ScrumEnum.ID_HISTORY_ATTR, "20100713171219");
        Element remains_1 = new Element(ScrumEnum.REMAINS);        
        remains_1.setText("0");
        history_2.addContent(remains_1);
        // 加入 History root
        this.root.addContent(history_1);
        this.root.addContent(history_2);
        // 歷史紀錄的時間
		Date hisDate_1 = DateUtil.dayFillter(history_1
				.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR),
				DateUtil._16DIGIT_DATE_TIME_2);
		Date hisDate_2 = DateUtil.dayFillter(history_2
				.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR),
				DateUtil._16DIGIT_DATE_TIME_2);
		// 將歷史紀錄設定至 Task
        this.task.setTagContent(root);
        assertEquals("5", this.task.getTagValue(ScrumEnum.REMAINS, hisDate_1));
        assertEquals("0", this.task.getTagValue(ScrumEnum.REMAINS, hisDate_2));

        this.sprintBacklogLogic.calculateSprintBacklogDateList(new Date("2010/07/12"), 5);
        ArrayList<Date> dateList = this.sprintBacklogLogic.getCurrentDateList();
        SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure(
							        		this.story, 
							        		new IIssue[]{ this.task }, 
							        		dateList);
        List<SprintBacklogTreeStructure> tasksTree = tree.GetTasksTreeListForTest();
        for (SprintBacklogTreeStructure taskTree : tasksTree){
        	assertEquals("5.0", taskTree.GetDatetoRemainMap().get("Date_1"));
        	assertEquals("0.0", taskTree.GetDatetoRemainMap().get("Date_2"));
        	assertEquals("0.0", taskTree.GetDatetoRemainMap().get("Date_3"));
        	assertEquals("0.0", taskTree.GetDatetoRemainMap().get("Date_4"));
        	assertEquals("0.0", taskTree.GetDatetoRemainMap().get("Date_5"));
        }
	}
	
	// Sprint Date 有跳過假日
	public void testRemainingBySprintDaysCase_2()
	{
		// Remaining Hours History 1
        Element history_1 = new Element(ScrumEnum.HISTORY_TAG);        
        history_1.setAttribute(ScrumEnum.ID_HISTORY_ATTR, "20100714171219");
        Element remains = new Element(ScrumEnum.REMAINS);        
        remains.setText("13");
        history_1.addContent(remains);
        // Remaining Hours History 2
        Element history_2 = new Element(ScrumEnum.HISTORY_TAG);        
        history_2.setAttribute(ScrumEnum.ID_HISTORY_ATTR, "20100716171219");
        Element remains_1 = new Element(ScrumEnum.REMAINS);        
        remains_1.setText("8");
        history_2.addContent(remains_1);
        // Remaining Hours History 3
        Element history_3 = new Element(ScrumEnum.HISTORY_TAG);        
        history_3.setAttribute(ScrumEnum.ID_HISTORY_ATTR, "20100719171219");
        Element remains_3 = new Element(ScrumEnum.REMAINS);        
        remains_3.setText("3");
        history_3.addContent(remains_3);
        // Remaining Hours History 4
        Element history_4 = new Element(ScrumEnum.HISTORY_TAG);        
        history_4.setAttribute(ScrumEnum.ID_HISTORY_ATTR, "20100720171219");
        Element remains_4 = new Element(ScrumEnum.REMAINS);        
        remains_4.setText("0");
        history_4.addContent(remains_4);
        // 加入 History root
        this.root.addContent(history_1);
        this.root.addContent(history_2);
        this.root.addContent(history_3);
        this.root.addContent(history_4);
        // 歷史紀錄的時間
		Date hisDate_1 = DateUtil.dayFillter(history_1
				.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR),
				DateUtil._16DIGIT_DATE_TIME_2);
		Date hisDate_2 = DateUtil.dayFillter(history_2
				.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR),
				DateUtil._16DIGIT_DATE_TIME_2);
		Date hisDate_3 = DateUtil.dayFillter(history_3
				.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR),
				DateUtil._16DIGIT_DATE_TIME_2);
		Date hisDate_4 = DateUtil.dayFillter(history_4
				.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR),
				DateUtil._16DIGIT_DATE_TIME_2);
		// 將歷史紀錄設定至 Task
        this.task.setTagContent(root);
        assertEquals("13", this.task.getTagValue(ScrumEnum.REMAINS, hisDate_1));
        assertEquals("8", this.task.getTagValue(ScrumEnum.REMAINS, hisDate_2));
        assertEquals("3", this.task.getTagValue(ScrumEnum.REMAINS, hisDate_3));
        assertEquals("0", this.task.getTagValue(ScrumEnum.REMAINS, hisDate_4));

        this.sprintBacklogLogic.calculateSprintBacklogDateList(new Date("2010/07/14"), 5);
        ArrayList<Date> dateList = this.sprintBacklogLogic.getCurrentDateList();
        SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure(
							        		this.story, 
							        		new IIssue[]{ this.task }, 
							        		dateList);
        List<SprintBacklogTreeStructure> tasksTree = tree.GetTasksTreeListForTest();
        for (SprintBacklogTreeStructure taskTree : tasksTree){
        	assertEquals("13.0", taskTree.GetDatetoRemainMap().get("Date_1"));
        	assertEquals("13.0", taskTree.GetDatetoRemainMap().get("Date_2"));
        	assertEquals("8.0", taskTree.GetDatetoRemainMap().get("Date_3"));
        	assertEquals("3.0", taskTree.GetDatetoRemainMap().get("Date_4"));
        	assertEquals("0.0", taskTree.GetDatetoRemainMap().get("Date_5"));
        }
	}
	
	// History 沒有 Remain 將以 ESTIMATION 代替
	public void testRemainingBySprintDaysCase_3()
	{
		// Remaining Hours History 1
        Element history_1 = new Element(ScrumEnum.HISTORY_TAG);        
        history_1.setAttribute(ScrumEnum.ID_HISTORY_ATTR, "20100712171219");
        Element remains = new Element(ScrumEnum.ESTIMATION);        
        remains.setText("5");
        history_1.addContent(remains);
        // Remaining Hours History 2
        Element history_2 = new Element(ScrumEnum.HISTORY_TAG);        
        history_2.setAttribute(ScrumEnum.ID_HISTORY_ATTR, "20100714171219");
        Element remains_1 = new Element(ScrumEnum.REMAINS);        
        remains_1.setText("0");
        history_2.addContent(remains_1);
        // 加入 History root
        this.root.addContent(history_1);
        this.root.addContent(history_2);
        // 歷史紀錄的時間
		Date hisDate_1 = DateUtil.dayFillter(history_1
				.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR),
				DateUtil._16DIGIT_DATE_TIME_2);
		Date hisDate_2 = DateUtil.dayFillter(history_2
				.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR),
				DateUtil._16DIGIT_DATE_TIME_2);
		// 將歷史紀錄設定至 Task
        this.task.setTagContent(root);
        assertEquals("5", this.task.getTagValue(ScrumEnum.ESTIMATION, hisDate_1));
        assertEquals("0", this.task.getTagValue(ScrumEnum.REMAINS, hisDate_2));

        this.sprintBacklogLogic.calculateSprintBacklogDateList(new Date("2010/07/12"), 3);
        ArrayList<Date> dateList = this.sprintBacklogLogic.getCurrentDateList();
        SprintBacklogTreeStructure tree = new SprintBacklogTreeStructure(
							        		this.story, 
							        		new IIssue[]{ this.task }, 
							        		dateList);
        List<SprintBacklogTreeStructure> tasksTree = tree.GetTasksTreeListForTest();
        for (SprintBacklogTreeStructure taskTree : tasksTree){
        	assertEquals("5.0", taskTree.GetDatetoRemainMap().get("Date_1"));
        	assertEquals("5.0", taskTree.GetDatetoRemainMap().get("Date_2"));
        	assertEquals("0.0", taskTree.GetDatetoRemainMap().get("Date_3"));
        }
	}
}
