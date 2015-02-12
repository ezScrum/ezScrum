package ntut.csie.ezScrum.iteration.support.filter;

import java.util.Date;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.iteration.iternal.Story;
import ntut.csie.ezScrum.iteration.iternal.Task;
import ntut.csie.jcis.core.util.DateUtil;

import org.jdom.Element;

public class StoryDataForFilter {
	// for filter Backlog、Detail、Done
	private IStory[] mStories = null;
	// for filter name、description、handler
	private IStory[] mStoriesInfo = null;
	private ITask[] mTasksInfo = null;
	
	public StoryDataForFilter() {
		mStories = new IStory[10];

		for (int i=0 ; i<10 ; i++) {
			mStories[i] = new Story();
		}
		
		init();
	}
	
	public StoryDataForFilter(String info) {
		mStoriesInfo = new IStory[10];
		mTasksInfo = new ITask[10];

		for (int i=0 ; i<10 ; i++) {
			mStoriesInfo[i] = new Story();
			mTasksInfo[i] = new Task();
		}
		initStory(info);
		initTask(info);
	}
	
	public IStory[] getStorirs() {
		return mStories;
	}
	
	public IStory[] getStorirsByInfo() {
		return mStoriesInfo;
	}
	
	public ITask[] getTasksByInfo() {
		return mTasksInfo;
	
	}
	private void init() {
		// for backlogged, total = 5
		mStories[0].getTagContentRoot().addContent(getBacklogElement1());
		mStories[0].setStatus(ITSEnum.S_NEW_STATUS);
		
		mStories[1].getTagContentRoot().addContent(getBacklogElement1());
		mStories[1].setStatus(ITSEnum.S_NEW_STATUS);
		
		mStories[2].getTagContentRoot().addContent(getBacklogElement2());
		mStories[2].setStatus(ITSEnum.S_NEW_STATUS);
		
		mStories[3].getTagContentRoot().addContent(getBacklogElement3());
		mStories[3].setStatus(ITSEnum.S_NEW_STATUS);
		
		mStories[4].getTagContentRoot().addContent(getBacklogElement4());
		mStories[4].setStatus(ITSEnum.S_NEW_STATUS);
		
		// for detailed, total = 2
		mStories[5].getTagContentRoot().addContent(getDetailElement1());
		mStories[5].setStatus(ITSEnum.S_NEW_STATUS);
		
		mStories[6].getTagContentRoot().addContent(getDetailElement2());
		mStories[6].setStatus(ITSEnum.S_NEW_STATUS);
		
		// for done, total = 3
		mStories[7].getTagContentRoot().addContent(getDoneElement());
		mStories[7].setStatus(ITSEnum.S_CLOSED_STATUS);
		
		mStories[8].getTagContentRoot().addContent(getDoneElement());
		mStories[8].setStatus(ITSEnum.S_CLOSED_STATUS);
		
		mStories[9].getTagContentRoot().addContent(getDoneElement());
		mStories[9].setStatus(ITSEnum.S_CLOSED_STATUS);
	}
	
	private void initStory(String info) {
		// set ID 
		mStoriesInfo[0].setIssueID(1);
		mStoriesInfo[1].setIssueID(2);
		mStoriesInfo[2].setIssueID(3);
		mStoriesInfo[3].setIssueID(4);
		mStoriesInfo[4].setIssueID(5);
		mStoriesInfo[5].setIssueID(6);
		mStoriesInfo[6].setIssueID(7);
		mStoriesInfo[7].setIssueID(8);
		mStoriesInfo[8].setIssueID(9);
		mStoriesInfo[9].setIssueID(10);
		
		// summary contains info
		mStoriesInfo[0].setSummary(info);
		mStoriesInfo[1].setSummary(info + "_Story_Test");
		mStoriesInfo[2].setSummary("Story_Test_" + info + "_Story_Test");
		mStoriesInfo[3].setSummary("Story_Test_" + info);
		mStoriesInfo[4].setSummary(info + info + info);
		
		mStoriesInfo[5].setSummary("");
		mStoriesInfo[6].setSummary("");
		mStoriesInfo[7].setSummary("");		
		mStoriesInfo[8].setSummary("");
		mStoriesInfo[9].setSummary("");
		
		// description contains info
		mStoriesInfo[0].setDescription("");
		mStoriesInfo[1].setDescription("");
		mStoriesInfo[2].setDescription("");
		mStoriesInfo[3].setDescription("");
		mStoriesInfo[4].setDescription("");
		
		mStoriesInfo[5].setDescription(info);
		mStoriesInfo[6].setDescription(info + info + info);
		mStoriesInfo[7].setDescription(info + "_Story_Test");
		mStoriesInfo[8].setDescription("Story_Test_" + info + "_Story_Test");
		mStoriesInfo[9].setDescription("Story_Test_" + info);
	}
	
	private void initTask(String info) {
		// set ID 
		mTasksInfo[0].setIssueID(1);
		mTasksInfo[1].setIssueID(2);
		mTasksInfo[2].setIssueID(3);
		mTasksInfo[3].setIssueID(4);
		mTasksInfo[4].setIssueID(5);
		mTasksInfo[5].setIssueID(6);
		mTasksInfo[6].setIssueID(7);
		mTasksInfo[7].setIssueID(8);
		mTasksInfo[8].setIssueID(9);
		mTasksInfo[9].setIssueID(10);
		
		// summary contains info
		mTasksInfo[0].setSummary(info);
		mTasksInfo[1].setSummary(info + "_Story_Test");
		mTasksInfo[2].setSummary("Story_Test_" + info + "_Story_Test");
		mTasksInfo[3].setSummary("Story_Test_" + info);
		mTasksInfo[4].setSummary(info + info + info);
		
		mTasksInfo[5].setSummary("");
		mTasksInfo[6].setSummary("");
		mTasksInfo[7].setSummary("");		
		mTasksInfo[8].setSummary("");
		mTasksInfo[9].setSummary("");

		// description contains info
		mTasksInfo[0].setDescription("");
		mTasksInfo[1].setDescription("");
		mTasksInfo[2].setDescription("");
		mTasksInfo[3].setDescription("");
		mTasksInfo[4].setDescription("");
		
		mTasksInfo[5].setDescription(info);
		mTasksInfo[6].setDescription(info + info + info);
		mTasksInfo[7].setDescription(info + "_Story_Test");
		mTasksInfo[8].setDescription("Story_Test_" + info + "_Story_Test");
		mTasksInfo[9].setDescription("Story_Test_" + info);
		
		// handlers contains info
		mTasksInfo[0].setAssignto("Yoman");
		mTasksInfo[1].setAssignto("Waterman");
		mTasksInfo[2].setAssignto("Ironman");
		mTasksInfo[3].setAssignto(info);
		mTasksInfo[4].setAssignto(info);
		mTasksInfo[5].setAssignto(info);
		mTasksInfo[6].setAssignto(info);
		mTasksInfo[7].setAssignto(info);
		mTasksInfo[8].setAssignto("superman");
		mTasksInfo[9].setAssignto("noman");
	}
	
	private Element getBacklogElement1() {
		Date date = new Date();
		date.setTime(date.getTime() - 100000);
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(date, DateUtil._16DIGIT_DATE_TIME_2));
		history.addContent(getValueElement("0"));	// add value 0
		history.addContent(getImpElement("0"));	// add Imp. 0
		history.addContent(getEstElement("0"));	// add Est. 0
		return history;
	}
	
	private Element getBacklogElement2() {
		Date date = new Date();
		date.setTime(date.getTime() - 100000);
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(date, DateUtil._16DIGIT_DATE_TIME_2));
		history.addContent(getValueElement("0"));	// add value 0
		history.addContent(getImpElement("0"));	// add Imp. 0
		history.addContent(getEstElement("10"));	// add Est. 10
		return history;
	}
	
	private Element getBacklogElement3() {
		Date date = new Date();
		date.setTime(date.getTime() - 100000);
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(date, DateUtil._16DIGIT_DATE_TIME_2));
		history.addContent(getValueElement("10"));// add value 10
		history.addContent(getImpElement("0"));	// add Imp. 0
		history.addContent(getEstElement("10"));	// add Est. 10
		return history;
	}
	
	private Element getBacklogElement4() {
		Date date = new Date();
		date.setTime(date.getTime() - 100000);
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(date, DateUtil._16DIGIT_DATE_TIME_2));
		history.addContent(getValueElement("10"));// add value 10
		history.addContent(getImpElement("10"));	// add Imp. 10
		history.addContent(getEstElement("0"));	// add Est. 0
		return history;
	}
	
	private Element getDetailElement1() {
		Date date = new Date();
		date.setTime(date.getTime() - 100000);
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(date, DateUtil._16DIGIT_DATE_TIME_2));
		history.addContent(getValueElement("10"));// add value 10
		history.addContent(getImpElement("10"));	// add Imp. 10
		history.addContent(getEstElement("10"));	// add Est. 10
		return history;
	}
	
	private Element getDetailElement2() {
		Date date = new Date();
		date.setTime(date.getTime() - 100000);
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(date, DateUtil._16DIGIT_DATE_TIME_2));
		history.addContent(getValueElement("100"));// add value 10
		history.addContent(getImpElement("100"));	// add Imp. 10
		history.addContent(getEstElement("10"));	// add Est. 0
		return history;
	}
	
	private Element getDoneElement() {
		Date date = new Date();
		date.setTime(date.getTime() - 100000);
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(date, DateUtil._16DIGIT_DATE_TIME_2));	
		history.addContent(getValueElement("100"));// add value 100
		history.addContent(getImpElement("100"));	// add Imp. 100
		history.addContent(getEstElement("5"));	// add Est. 5
		return history;
	}
	
	private Element getValueElement(String v) {
		Element e = new Element(ScrumEnum.VALUE);
		e.setText(v);
		return e;
	}
	
	private Element getImpElement(String v) {
		Element e = new Element(ScrumEnum.IMPORTANCE);
		e.setText(v);
		return e;
	}
	
	private Element getEstElement(String v) {
		Element e = new Element(ScrumEnum.ESTIMATION);
		e.setText(v);
		return e;
	}
}