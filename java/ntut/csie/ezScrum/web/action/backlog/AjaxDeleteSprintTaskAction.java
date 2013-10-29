package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxDeleteSprintTaskAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxAddSprintTaskAction.class);
	
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
		
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		String sprintID = request.getParameter("sprintID");
		long parentID = Long.parseLong(request.getParameter("parentID"));
		// delete task 單選的 task ID
		long issueID = Long.parseLong(request.getParameter("issueID"));
		
		SprintBacklogMapper backlog = new SprintBacklogLogic(project, session, sprintID).getSprintBacklogMapper();
		backlog.deleteTask(issueID, parentID);
		String result = "<DeleteTask><Result>true</Result><Task><Id>" + issueID + "</Id></Task></DeleteTask>";
		return new StringBuilder(result);
	}
}
