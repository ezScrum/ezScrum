package ntut.csie.ezScrum.web.action.backlog;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowEditIssueHistoryAction extends Action {
	private static Log log = LogFactory.getLog(ShowEditIssueHistoryAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		log.info(" Show Edited Issue History. ");
		
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		// get parameter info
		long issueId = Long.parseLong(request.getParameter("issueID"));
		String backlogType = request.getParameter("type");
		String sprintId = request.getParameter("sprintID");
		String historyId = request.getParameter("historyID");

		IIssue issue;
		if (sprintId == null || sprintId.equals("")) {
			ProductBacklogHelper PBHelper = new ProductBacklogHelper(session, project);
			issue = PBHelper.getIssue(issueId);
		} else {
			SprintBacklogMapper backlog = (new SprintBacklogLogic(project, session, sprintId)).getSprintBacklogMapper();
			issue = backlog.getStory(issueId);
		}

		try {
			for (HistoryObject history : issue.getHistories()) {
				if (history.getId() == Long.parseLong(historyId)) {
					request.setAttribute("History", history);
					break;
				}
			}
		} catch (NumberFormatException e) {
		} catch (SQLException e) {
		}
		request.setAttribute("Issue", issue);
		request.setAttribute("backlogType", backlogType);
		request.setAttribute("sprintID", sprintId);

		ScrumRole sr = new ScrumRoleLogic().getScrumRole(project, session.getAccount());
		if (sr.getAccessProductBacklog()) {
			return mapping.findForward("success");
		} else {
			return mapping.findForward("GuestOnly");
		}
	}
}
