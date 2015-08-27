package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class CheckReleaseDateAction extends PermissionAction {
	private static Log log = LogFactory.getLog(CheckReleaseDateAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessReleasePlan();
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
		ProjectObject project = (ProjectObject) SessionManager.getProjectObject(request);

		// get parameter info
		String releaseID = request.getParameter("Id");
		String startDate = request.getParameter("StartDate");
		String dueDate = request.getParameter("EndDate");

		if (request.getParameter("action") == null || request.getParameter("action").isEmpty()) {
			return null;
		} 		
		String Action = request.getParameter("action");
		
		ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(project);
		return releasePlanHelper.checkReleaseDateOverlapping(releaseID, startDate, dueDate, Action);
	}

}
