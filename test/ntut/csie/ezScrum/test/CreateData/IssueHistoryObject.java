package ntut.csie.ezScrum.test.CreateData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueHistory;

public class IssueHistoryObject {
	private long Id = -1L;
	public String Link = "";
	public String Name = "";
	public String IssueType = "";
	
	public List<IssueHistoryList> IssueHistories = new LinkedList<IssueHistoryList>();
	
	public IssueHistoryObject(IIssue issue) {
		if (issue != null) {
			this.Id = issue.getIssueID();
			this.Link = issue.getIssueLink();
			this.Name = issue.getSummary();
			this.IssueType = issue.getCategory();
			
			if (issue.getHistory().size() > 0) {
				for (IIssueHistory history : issue.getIssueHistories()) {
					if (history.getDescription().length() > 0) {
						this.IssueHistories.add(new IssueHistoryList(history));
					}
				}
			}
		}
	}
	
	public class IssueHistoryList {
		public String Description = "";
		public String HistoryType = "";
		public String ModifiedDate = "";
		
		public IssueHistoryList(IIssueHistory history) {
			parseDate(history.getModifyDate());
			parseType_Desc(history.getDescription());
		}
		
		private void parseType_Desc(String desc) {
			String [] token = desc.split(":");
			
			if (token.length == 2) {
				this.HistoryType = token[0];
				this.Description = token[1];
			} else {
				this.HistoryType = "";
				this.Description = desc;
			}
		}
		
		private void parseDate(long date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
			Date d = new Date(date);
			
			this.ModifiedDate = sdf.format(d);
		}
	}
}
