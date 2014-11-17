package ntut.csie.ezScrum.iteration.iternal;

import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueHistory;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.iteration.core.IScrumIssue;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;

public class ScrumIssue extends Issue implements IScrumIssue {
	public ScrumIssue(){}
	
	public ScrumIssue(IIssue issue) throws SQLException{
		setIssue(issue);
	}
	
	@Override
	public String getName() {
		return getSummary();
	}

	@Override
	public String getSprintID() {
		String temp=getTagValue(ScrumEnum.SPRINT_ID);
		if(temp==null)
			return "-1";
		return temp;
		
	}
}
