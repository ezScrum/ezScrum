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

public class DoneIssueAction extends PermissionAction {
	private static Log log = LogFactory.getLog(DoneIssueAction.class);

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
		log.info("Done Issue in DoneIssueAction.");

		// get project from session or DB
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		// get parameter info
		long issueId = Long.parseLong(request.getParameter("Id"));
		String name = request.getParameter("Name");
		String bugNote = request.getParameter("Notes");
		String changeDate = request.getParameter("ChangeDate");
		String ActualHour = request.getParameter("Actualhour");
		int issueType = Integer.parseInt(request.getParameter("IssueType"));

		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, session);
		sprintBacklogHelper.doneIssue(issueId, issueType, name, bugNote, changeDate, ActualHour);

		// return done issue 相關相關資訊
		IIssue issue = sprintBacklogHelper.getIssue(issueId);
		StringBuilder result = new StringBuilder("");
		result.append(new Translation().translateTaskboardIssueToJson(issue));

		return result;
	}
}
