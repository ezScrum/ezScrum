package ntut.csie.ezScrum.web.action.report;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.TaskBoardHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class GetSprintInfoForTaskBoardAction extends PermissionAction {

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
		ProjectObject project = SessionManager.getProjectObject(request);
		// get parameter info
		String sprintIdString = request.getParameter("SprintID");
		long sprintId = -1;
		if (sprintIdString != null && sprintIdString != "") {
			sprintId = Long.parseLong(request.getParameter("SprintID"));
		}
		return new TaskBoardHelper(project, sprintId).getSprintInfoForTaskBoardText();
	}
}
