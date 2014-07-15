package ntut.csie.ezScrum.web.dataObject;

import ntut.csie.ezScrum.issue.core.IIssueHistory;


public class HistoryObject {
	public Long id;
	public Long issue_ID;
	public String issue_type = "";
	public int type;
	public String description = "";
	public Long updateTime;
	
	public HistoryObject() {}
	
	public HistoryObject(IIssueHistory history) {
		id = history.getHistoryID();
		issue_ID = history.getIssueID();
		description = history.getDescription();
		type = history.getType();
		updateTime = history.getModifyDate();
	}
}
