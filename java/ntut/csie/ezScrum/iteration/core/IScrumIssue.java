package ntut.csie.ezScrum.iteration.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueHistory;

import org.jdom.Element;

public interface IScrumIssue extends IIssue{
	final public static String STRING_BLANK_VALUE = "";
	final public static String DIGITAL_BLANK_VALUE = "0";
	
	//支援Scrum的欄位
	public String getName();
	public String getSprintID();

	//支援scrum但需要修改的操作
	public void setTagContent(Element history);
	public Element getTagContentRoot();
	
	public String getTagValue(String name, Date date);	
	public String getTagValue(String name);
	public void addTagValue(Element element);
}
