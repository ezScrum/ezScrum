package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class EditIssueHistoryAction extends Action {
	private static Log log = LogFactory.getLog(EditIssueHistoryAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get session info
		IProject project = (IProject) request.getSession().getAttribute("Project");
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		// get parameter info
		long issueID = Long.parseLong(request.getParameter("issueID"));
		String backlogType = request.getParameter("type");
		String sprintID = request.getParameter("sprintID");
		long historyID = Long.parseLong(request.getParameter("historyID"));
		String modifyDate = request.getParameter("ModifyDate");

		IIssue issue;
		if (sprintID == null || sprintID.equals("")) {
			ProductBacklogHelper helper = new ProductBacklogHelper(project, session);
			helper.updateHistoryModifiedDate(issueID, historyID, DateUtil.dayFillter(modifyDate, DateUtil._16DIGIT_DATE_TIME));
			issue = helper.getIssue(issueID);
		} else {
			SprintBacklogMapper backlog = (new SprintBacklogLogic(project, session, sprintID)).getSprintBacklogMapper();
			backlog.updateHistoryModifiedDate(issueID, historyID, DateUtil.dayFillter(modifyDate, DateUtil._16DIGIT_DATE_TIME));
			issue = backlog.getIssue(issueID);
		}

		request.setAttribute("Issue", issue);
		request.setAttribute("backlogType", backlogType);
		request.setAttribute("sprintID", sprintID);

//		ScrumRole sr = new ScrumRoleManager().getScrumRole(project, session.getAccount());
		ScrumRole sr = new ScrumRoleLogic().getScrumRole(project, session.getAccount());
		if (sr.getAccessProductBacklog()) {
			return mapping.findForward("success");
		} else {
			return mapping.findForward("GuestOnly");
		}
	}
}
