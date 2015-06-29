package ntut.csie.ezScrum.web.action.backlog;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;

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
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		// get parameter info
		long issueId = Long.parseLong(request.getParameter("issueID"));
		String backlogType = request.getParameter("type");
		long sprintId = Long.parseLong(request.getParameter("sprintID"));
		String historyId = request.getParameter("historyID");

		StoryObject story = StoryObject.get(issueId);

		try {
			for (HistoryObject history : story.getHistories()) {
				if (history.getId() == Long.parseLong(historyId)) {
					request.setAttribute("History", history);
					break;
				}
			}
		} catch (NumberFormatException e) {
		}
		request.setAttribute("Issue", story);
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
