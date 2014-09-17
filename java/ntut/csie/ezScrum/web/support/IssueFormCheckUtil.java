package ntut.csie.ezScrum.web.support;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IITSService;
import ntut.csie.ezScrum.issue.sql.service.core.ITSServiceFactory;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class IssueFormCheckUtil {
	final public static String CHANGE_STATUS = "ChangeStatus";

	public static boolean isChangedStatus(long issueID, String value,
			IProject project, IUserSession session) {
		boolean flag = false;
		IITSService service = null;
		try {
			Configuration config = new Configuration(session);
			service = ITSServiceFactory.getInstance().getService(config);
			service.openConnect();
			IIssue issue = service.getIssue(issueID);
			if (issue.getLastUpdate() == null)
				flag = true;
			else if (issue.getLastUpdate().getTime() < DateUtil.parse(value,
					DateUtil._16DIGIT_DATE_TIME).getTime())
				flag = true;
		} catch (Exception e) {
			return false;
		} finally{
			service.closeConnect();
		}
		return flag;
	}
}
