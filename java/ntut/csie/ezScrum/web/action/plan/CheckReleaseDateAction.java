package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

public class CheckReleaseDateAction extends PermissionAction {
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
		ProjectObject project = (ProjectObject) SessionManager.getProject(request);

		// get parameter info
		String serialReleaseIdString = request.getParameter("Id");
		String startDate = request.getParameter("StartDate");
		String endDate = request.getParameter("EndDate");
		String action = request.getParameter("action");
		if (action == null || action.isEmpty()) {
			return null;
		} 		
		long serialReleaseId = -1;
		if (serialReleaseIdString != null && !serialReleaseIdString.isEmpty()) {
			serialReleaseId = Long.parseLong(serialReleaseIdString);
		}
		
		// Get release id
		long releaseId = -1;
		ReleaseObject release = ReleaseObject.get(project.getId(), serialReleaseId);
		if (release != null) {
			releaseId = release.getId();
		}
		ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(project);
		return releasePlanHelper.checkReleaseDateOverlapping(releaseId, startDate, endDate, action);
	}

}
