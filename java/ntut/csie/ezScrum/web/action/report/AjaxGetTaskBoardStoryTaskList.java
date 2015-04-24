package ntut.csie.ezScrum.web.action.report;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.TaskBoardHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxGetTaskBoardStoryTaskList extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxGetTaskBoardStoryTaskList.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessTaskBoard();
	}

	@Override
	public boolean isXML() {
		return false;	// html
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		// get project from session or DB
		ProjectObject project = (ProjectObject) SessionManager.getProjectObject(request);
		// get parameter info
		String sprintIdString = request.getParameter("sprintID");
		
		long sprintId;
		try {
			sprintId = Long.parseLong(sprintIdString);
		} catch (Exception e) {
			sprintId = -1;
		}

		String name = "ALL";
		if (request.getParameter("UserID") != null) { 
			name = request.getParameter("UserID");	// filter name
		}

		return new TaskBoardHelper(project, sprintId).getTaskBoardStoryTaskListText(name);
	}
}
