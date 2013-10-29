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
	private IStory[] stories = null;
	
	// for filter name、description、handler
	private IStory[] storie_info = null;
	private ITask[] tasks_info = null;
	
	public StoryDataForFilter() {
		this.stories = new IStory[10];

		for (int i=0 ; i<10 ; i++) {
			this.stories[i] = new Story();
		}
		
		init();
	}
	
	public StoryDataForFilter(String info) {
		this.storie_info = new IStory[10];
		this.tasks_info = new ITask[10];

		for (int i=0 ; i<10 ; i++) {
			this.storie_info[i] = new Story();
			this.tasks_info[i] = new Task();
		}
		
		init_Story(info);
		init_Task(info);
	}
	
	public IStory[] getStorirs() {
		return this.stories;
	}
	
	public IStory[] getStorirs_byInfo() {
		return this.storie_info;
	}
	
	public ITask[] getTasks_byInfo() {
		return this.tasks_info;
	
	}
	private void init() {
		// for backlogged, total = 5
		this.stories[0].getTagContentRoot().addContent(getBacklog_element1());
		this.stories[0].setStatus(ITSEnum.S_NEW_STATUS);
		
		this.stories[1].getTagContentRoot().addContent(getBacklog_element1());
		this.stories[1].setStatus(ITSEnum.S_NEW_STATUS);
		
		this.stories[2].getTagContentRoot().addContent(getBacklog_element2());
		this.stories[2].setStatus(ITSEnum.S_NEW_STATUS);
		
		this.stories[3].getTagContentRoot().addContent(getBacklog_element3());
		this.stories[3].setStatus(ITSEnum.S_NEW_STATUS);
		
		this.stories[4].getTagContentRoot().addContent(getBacklog_element4());
		this.stories[4].setStatus(ITSEnum.S_NEW_STATUS);
		
		// for detailed, total = 2
		this.stories[5].getTagContentRoot().addContent(getDetail_element1());
		this.stories[5].setStatus(ITSEnum.S_NEW_STATUS);
		
		this.stories[6].getTagContentRoot().addContent(getDetail_element2());
		this.stories[6].setStatus(ITSEnum.S_NEW_STATUS);
		
		// for done, total = 3
		this.stories[7].getTagContentRoot().addContent(getDone_element());
		this.stories[7].setStatus(ITSEnum.S_CLOSED_STATUS);
		
		this.stories[8].getTagContentRoot().addContent(getDone_element());
		this.stories[8].setStatus(ITSEnum.S_CLOSED_STATUS);
		
		this.stories[9].getTagContentRoot().addContent(getDone_element());
		this.stories[9].setStatus(ITSEnum.S_CLOSED_STATUS);
	}
	
	private void init_Story(String info) {
		// set ID 
		this.storie_info[0].setIssueID(1);
		this.storie_info[1].setIssueID(2);
		this.storie_info[2].setIssueID(3);
		this.storie_info[3].setIssueID(4);
		this.storie_info[4].setIssueID(5);
		this.storie_info[5].setIssueID(6);
		this.storie_info[6].setIssueID(7);
		this.storie_info[7].setIssueID(8);
		this.storie_info[8].setIssueID(9);
		this.storie_info[9].setIssueID(10);
		
		
		// summary contains info
		this.storie_info[0].setSummary(info);
		this.storie_info[1].setSummary(info + "_Story_Test");
		this.storie_info[2].setSummary("Story_Test_" + info + "_Story_Test");
		this.storie_info[3].setSummary("Story_Test_" + info);
		this.storie_info[4].setSummary(info + info + info);
		
		this.storie_info[5].setSummary("");
		this.storie_info[6].setSummary("");
		this.storie_info[7].setSummary("");		
		this.storie_info[8].setSummary("");
		this.storie_info[9].setSummary("");

		
		// description contains info
		this.storie_info[0].setDescription("");
		this.storie_info[1].setDescription("");
		this.storie_info[2].setDescription("");
		this.storie_info[3].setDescription("");
		this.storie_info[4].setDescription("");
		
		this.storie_info[5].setDescription(info);
		this.storie_info[6].setDescription(info + info + info);
		this.storie_info[7].setDescription(info + "_Story_Test");
		this.storie_info[8].setDescription("Story_Test_" + info + "_Story_Test");
		this.storie_info[9].setDescription("Story_Test_" + info);
		
	}
	
	private void init_Task(String info) {
		// set ID 
		this.tasks_info[0].setIssueID(1);
		this.tasks_info[1].setIssueID(2);
		this.tasks_info[2].setIssueID(3);
		this.tasks_info[3].setIssueID(4);
		this.tasks_info[4].setIssueID(5);
		this.tasks_info[5].setIssueID(6);
		this.tasks_info[6].setIssueID(7);
		this.tasks_info[7].setIssueID(8);
		this.tasks_info[8].setIssueID(9);
		this.tasks_info[9].setIssueID(10);
		
		
		// summary contains info
		this.tasks_info[0].setSummary(info);
		this.tasks_info[1].setSummary(info + "_Story_Test");
		this.tasks_info[2].setSummary("Story_Test_" + info + "_Story_Test");
		this.tasks_info[3].setSummary("Story_Test_" + info);
		this.tasks_info[4].setSummary(info + info + info);
		
		this.tasks_info[5].setSummary("");
		this.tasks_info[6].setSummary("");
		this.tasks_info[7].setSummary("");		
		this.tasks_info[8].setSummary("");
		this.tasks_info[9].setSummary("");

	
		// description contains info
		this.tasks_info[0].setDescription("");
		this.tasks_info[1].setDescription("");
		this.tasks_info[2].setDescription("");
		this.tasks_info[3].setDescription("");
		this.tasks_info[4].setDescription("");
		
		this.tasks_info[5].setDescription(info);
		this.tasks_info[6].setDescription(info + info + info);
		this.tasks_info[7].setDescription(info + "_Story_Test");
		this.tasks_info[8].setDescription("Story_Test_" + info + "_Story_Test");
		this.tasks_info[9].setDescription("Story_Test_" + info);
		
		
		// handlers contains info
		this.tasks_info[0].setAssignto("Yoman");
		this.tasks_info[1].setAssignto("Waterman");
		this.tasks_info[2].setAssignto("Ironman");
		this.tasks_info[3].setAssignto(info);
		this.tasks_info[4].setAssignto(info);
		this.tasks_info[5].setAssignto(info);
		this.tasks_info[6].setAssignto(info);
		this.tasks_info[7].setAssignto(info);
		this.tasks_info[8].setAssignto("superman");
		this.tasks_info[9].setAssignto("noman");
	}
	
	private Element getBacklog_element1() {
		Date d = new Date();
		d.setTime(d.getTime() - 100000);
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(d, DateUtil._16DIGIT_DATE_TIME_2));
		history.addContent(getValue_element("0"));	// add value 0
		history.addContent(getImp_element("0"));	// add Imp. 0
		history.addContent(getEst_element("0"));	// add Est. 0
		
		return history;
	}
	
	private Element getBacklog_element2() {
		Date d = new Date();
		d.setTime(d.getTime() - 100000);
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(d, DateUtil._16DIGIT_DATE_TIME_2));
		history.addContent(getValue_element("0"));	// add value 0
		history.addContent(getImp_element("0"));	// add Imp. 0
		history.addContent(getEst_element("10"));	// add Est. 10
		
		return history;
	}
	
	private Element getBacklog_element3() {
		Date d = new Date();
		d.setTime(d.getTime() - 100000);
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(d, DateUtil._16DIGIT_DATE_TIME_2));
		history.addContent(getValue_element("10"));// add value 10
		history.addContent(getImp_element("0"));	// add Imp. 0
		history.addContent(getEst_element("10"));	// add Est. 10
		
		return history;
	}
	
	private Element getBacklog_element4() {
		Date d = new Date();
		d.setTime(d.getTime() - 100000);
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(d, DateUtil._16DIGIT_DATE_TIME_2));
		history.addContent(getValue_element("10"));// add value 10
		history.addContent(getImp_element("10"));	// add Imp. 10
		history.addContent(getEst_element("0"));	// add Est. 0
		
		return history;
	}
	
	private Element getDetail_element1() {
		Date d = new Date();
		d.setTime(d.getTime() - 100000);
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(d, DateUtil._16DIGIT_DATE_TIME_2));
		history.addContent(getValue_element("10"));// add value 10
		history.addContent(getImp_element("10"));	// add Imp. 10
		history.addContent(getEst_element("10"));	// add Est. 10
		
		return history;
	}
	
	private Element getDetail_element2() {
		Date d = new Date();
		d.setTime(d.getTime() - 100000);
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(d, DateUtil._16DIGIT_DATE_TIME_2));
		history.addContent(getValue_element("100"));// add value 10
		history.addContent(getImp_element("100"));	// add Imp. 10
		history.addContent(getEst_element("10"));	// add Est. 0
		
		return history;
	}
	
	private Element getDone_element() {
		Date d = new Date();
		d.setTime(d.getTime() - 100000);
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(d, DateUtil._16DIGIT_DATE_TIME_2));	
		history.addContent(getValue_element("100"));// add value 100
		history.addContent(getImp_element("100"));	// add Imp. 100
		history.addContent(getEst_element("5"));	// add Est. 5
		
		return history;
	}
	
	private Element getValue_element(String v) {
		Element e = new Element(ScrumEnum.VALUE);
		e.setText(v);
		
		return e;
	}
	
	private Element getImp_element(String v) {
		Element e = new Element(ScrumEnum.IMPORTANCE);
		e.setText(v);
		
		return e;
	}
	
	private Element getEst_element(String v) {
		Element e = new Element(ScrumEnum.ESTIMATION);
		e.setText(v);
		
		return e;
	}
}