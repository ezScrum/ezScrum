package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxDeleteSprintTaskAction extends PermissionAction {
	
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
		ProjectObject project = (ProjectObject) SessionManager.getProjectObject(request);
		
		// get parameter info
		long sprintId = Long.parseLong(request.getParameter("sprintID"));
		// delete task 單選的 task ID
		long taskId = Long.parseLong(request.getParameter("issueID"));
		
		SprintBacklogMapper backlog = new SprintBacklogLogic(project, sprintId).getSprintBacklogMapper();
		backlog.deleteTask(taskId);
		String result = "<DeleteTask><Result>true</Result><Task><Id>" + taskId + "</Id></Task></DeleteTask>";
		return new StringBuilder(result);
	}
}
