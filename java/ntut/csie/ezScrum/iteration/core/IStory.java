package ntut.csie.ezScrum.iteration.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssueHistory;

import org.jdom.Element;

public interface IStory extends IScrumIssue {
	//	add for GAE
	public void setName(String name);
	public String getName();
	public String getValue();
	public void setValue(String value);
	public String getDescription();
	public void setStoryId(long storyId);
	public long getStoryId(); 
	public void setStoryLink(String storyLink);
	public String getStoryLink();
	public void setStatus(String status);
//	public String getStatus();
//	public String getSprintId();
	public void setSprintId(String sprintId);
//	public String getReleaseId();
	public void setReleaseId(String releaseId);
	public void setImportance(String importance);
	public void setEstimated(String estimated);
	public void setHowToDemo(String howToDemo);
	public void setNotes(String notes);
	public void setDescription(String description);
	public String getCategory();
	
	//支援Scrum的欄位
	public String getEstimated();
	public String getImportance();
	public String getHowToDemo();
	public String getNotes();
	
	public String getValueByType(String type);

	//支援scrum但需要修改的操作
	public void setTagContent(Element history);
	public Element getTagContentRoot();
	
	public String getTagValue(String name, Date date);
	public String getTagValue(String name);
	public void addTagValue(Element element);
	
	
}
