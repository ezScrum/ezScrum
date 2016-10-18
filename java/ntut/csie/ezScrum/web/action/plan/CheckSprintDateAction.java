package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

public class CheckSprintDateAction extends PermissionAction {
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintPlan();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get session info
		ProjectObject project = (ProjectObject) SessionManager.getProject(request);

		// get parameter info
		String serialSprintIdString = request.getParameter("Id");
		String startDate = request.getParameter("StartDate");
		String endDate = request.getParameter("EndDate");
				
		long serialSprintId = -1;
		if (serialSprintIdString != null && !serialSprintIdString.isEmpty()) {
			serialSprintId = Long.parseLong(serialSprintIdString);
		}
		
		// Get sprint id
		long sprintId = -1;
		SprintObject sprint = SprintObject.get(project.getId(), serialSprintId);
		if (sprint != null) {
			sprintId = sprint.getId();
		}
		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		return sprintPlanHelper.checkSprintDateOverlapping(sprintId, startDate, endDate);
	}
}
