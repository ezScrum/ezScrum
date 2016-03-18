package ntut.csie.ezScrum.web.action.report;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;


public class GetReleaseBurndownChartDataAction extends PermissionAction {
	
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
		ProjectObject project = SessionManager.getProject(request);
		String serialReleaseIdString = request.getParameter("ReleaseID");			// get release ID
		long serialReleaseId = Long.parseLong(serialReleaseIdString);
		ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(project);
		// Get release if
		long releaseId = -1;
		ReleaseObject release = ReleaseObject.get(project.getId(), serialReleaseId);
		if (release != null) {
			releaseId = release.getId();
		}
		return releasePlanHelper.getReleaseBurndownChartData(releaseId);
	}
}
