package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AddExistedTaskAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AddExistedTaskAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintBacklog();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Add wild tasks in AddExistedTaskAction");
		
		// get session info
		ProjectObject project = (ProjectObject) SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		String[] selectedTaskIds = request.getParameterValues("selected");
		Long sprintId = Long.parseLong(request.getParameter("sprintID"));
		Long storyId = Long.parseLong(request.getParameter("issueID"));

		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, session, sprintId);
		
		sprintBacklogHelper.addExistingTask(selectedTaskIds, storyId);
		return new StringBuilder("");
	}
}
