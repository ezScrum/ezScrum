package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class DeleteExistedTaskAction extends PermissionAction {
//	private static Log log = LogFactory.getLog(AjaxAddSprintTaskAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintBacklog();
	}

	@Override
	public boolean isXML() {
		// HTML
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		String sprintID = request.getParameter("sprintID");
		String[] issueIDs = request.getParameterValues("selected");
		long[] taskIDs = new long[issueIDs.length];
		for( int i = 0; i < issueIDs.length; i++ ){
			taskIDs[i] = Long.parseLong(issueIDs[i]);
		}
//		int i = 0;
//		for(String id : issueIDs)
//			taskIDs[i++] = Long.parseLong(id);
		
		SprintBacklogMapper backlog = (new SprintBacklogLogic(project, session, sprintID)).getSprintBacklogMapper();
		backlog.deleteExistingTask(taskIDs);
		return new StringBuilder("");
	}
}
