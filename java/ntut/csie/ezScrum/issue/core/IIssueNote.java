package ntut.csie.ezScrum.issue.core;

import java.io.Serializable;

public interface IIssueNote extends  Serializable{
	public long getNoteID();
	public void setNoteID(long id);
	public String getText();
	public void setText(String text);
	public String getHandler();
	public void setHandler(String handler);
	public long getIssueID();
	public void setIssueID(long id);
	public long getModifiedDate();
	public void setModifiedDate(long date);
	public long getSubmittedDate();
	public void setSubmittedDate(long date);
}
