package ntut.csie.ezScrum.iteration.core;

import java.util.Date;
import java.util.List;

public interface ITask extends IScrumIssue {
	//支援Scrum的欄位
	public String getEstimated();
	public String getNotes();
	public String getPartners();	
	public long getStoryID();
	public String  getSpecificTime();
	
	
	//	add for GAE
	public void setEstimated(String estimation);
	public void setNotes(String notes);
	public void setPartners(String Partners);
	public void setStoryID(String storyID);
}
