package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxMoveSprintAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxMoveSprintAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintPlan();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		// get project from session or DB
		ProjectObject project = SessionManager.getProjectObject(request);

		// get parameter info
		String oldIdString = request.getParameter("OldID");
		String newIdString = request.getParameter("NewID");
		int oldId = -1;
		int newId = -1;

		if (oldIdString != null) {
			oldId = Integer.parseInt(oldIdString);
		}
		if (newIdString != null) {
			newId = Integer.parseInt(newIdString);
		}
		//移動iterPlan.xml的資訊
		SprintPlanHelper sprintBacklogHelper = new SprintPlanHelper(project);
		sprintBacklogHelper.moveSprint(oldId, newId);

		StringBuilder result = new StringBuilder("true");
		return result;
	}
}
