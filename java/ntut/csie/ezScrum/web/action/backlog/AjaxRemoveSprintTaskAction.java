package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.*;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxRemoveSprintTaskAction extends PermissionAction {
//	private static Log log = LogFactory.getLog(AjaxRemoveSprintTaskAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintBacklog();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		//  get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		//  get parameter info
		String sprintID = request.getParameter("sprintID");
		long issueID = Long.parseLong(request.getParameter("issueID"));
		long parentID = Long.parseLong(request.getParameter("parentID"));

		//  remove the task and clear its info
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, session, sprintID);
		sprintBacklogHelper.removeTask(issueID, parentID);
		
		StringBuilder result = new StringBuilder();
		result.append("<DropTask><Result>true</Result><Task><Id>")
				.append(issueID)
				.append("</Id></Task></DropTask>");
		
		return result;		
		
		/*
		 *  remove the task and clear its info
		 */
//		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, session, sprintID);
//		SprintBacklogMapper backlog = sprintBacklogLogic.getSprintBacklogMapper();

		
//		String name = backlog.getIssue(issueID).getSummary();
		
		// reset status, handler
//		backlog.resetTask(issueID, name, null, "");// name 不變
//		String est = sprintBacklogLogic.getTaskById(issueID).getEstimated();
		// reset partner, remaining hour, actual hour 
//		sprintBacklogLogic.editTask(issueID, null, est, est, "", "", "0", null, new Date());
		// remove relation
//		backlog.removeTask(issueID, parentID);
		
		// reset status, handler
//		String est = sprintBacklogHelper.getTaskById(issueID).getEstimated();
		// reset partner, remaining hour, actual hour
		// remove relation
		
		
//		SprintBacklogMapper backlog = new SprintBacklogMapper(project, session, sprintID);
//		
//		String name = backlog.getIssue(issueID).getSummary();
//		
//		// reset status, handler
//		backlog.resetTask(issueID, name, null, "");// name 不變
//		String est = backlog.getTaskById(issueID).getEstimated();
//		// reset partner, remaining hour, actual hour 
//		backlog.editTask(issueID, null, est, est, "", "", "0", null, new Date());
//		// remove relation
//		backlog.removeTask(issueID, parentID);
//		
//		StringBuilder result = new StringBuilder();
//		result.append("<DropTask><Result>true</Result><Task><Id>")
//				.append(issueID)
//				.append("</Id></Task></DropTask>");
//		
//		return result;
	}
}
