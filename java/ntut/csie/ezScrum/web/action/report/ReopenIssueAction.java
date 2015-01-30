package ntut.csie.ezScrum.web.action.report;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.Translation;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ReopenIssueAction extends PermissionAction {
	private static Log log = LogFactory.getLog(ReopenIssueAction.class);

	@Override
	public boolean isValidAction() {
		return (super.getScrumRole().getAccessProductBacklog() && (!super.getScrumRole().isGuest()));
	}

	@Override
	public boolean isXML() {
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		log.info("Reopen Issue in ReopenIssueAction.");

		// get project from session or DB
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		// get parameter info
		long issueID = Long.parseLong(request.getParameter("Id"));
		String name = request.getParameter("Name");
		String bugNote = request.getParameter("Notes");
		String changeDate = request.getParameter("ChangeDate");

		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, session);
		sprintBacklogHelper.reopenIssue(issueID, name, bugNote, changeDate);

		// return re open 的 issue的相關資訊
		IIssue issue = sprintBacklogHelper.getStory(issueID);
		StringBuilder result = new StringBuilder("");
		result.append(new Translation().translateTaskboardIssueToJson(issue));

		return result;
	}
}
