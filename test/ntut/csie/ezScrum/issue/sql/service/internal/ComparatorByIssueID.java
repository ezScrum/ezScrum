package ntut.csie.ezScrum.issue.sql.service.internal;

import java.util.Comparator;
import ntut.csie.ezScrum.issue.core.IIssue;

public class ComparatorByIssueID implements Comparator<IIssue>
{
	@Override
	public int compare(IIssue arg0, IIssue arg1) {

		return (arg0.getIssueID() < arg1.getIssueID())?0:1;
	}
	
}
