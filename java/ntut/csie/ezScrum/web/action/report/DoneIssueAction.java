package ntut.csie.ezScrum.web.action.report;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
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
		String notes = request.getParameter("Notes");
		String changeDate = request.getParameter("ChangeDate");
		int actual = Integer.parseInt(request.getParameter("Actualhour"));
		String issueType = request.getParameter("IssueType");

		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, session);
		StringBuilder result = new StringBuilder("");
		
		if (issueType.equals("Story")) {
			sprintBacklogHelper.closeStory(issueId, notes, changeDate);
			// return done issue 相關相關資訊
			IIssue issue = sprintBacklogHelper.getStory(issueId);
			result.append(new Translation().translateTaskboardIssueToJson(issue));
		} else if (issueType.equals("Task")) {
			sprintBacklogHelper.closeTask(issueId, name, notes, actual, changeDate);
			TaskObject task = sprintBacklogHelper.getTask(issueId);
			result.append(new Translation().translateTaskboardTaskToJson(task));
		}

		return result;
	}
}
