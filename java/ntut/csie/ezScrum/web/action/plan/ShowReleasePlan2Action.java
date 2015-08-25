package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ShowReleasePlan2Action extends PermissionAction {
	private static Log log = LogFactory.getLog(ShowReleasePlan2Action.class);
	
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
		log.info("Show ReleasePlan.");
		
		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
    	
		ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(project);
		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		
		ArrayList<ReleaseObject> releases = releasePlanHelper.getReleases();
		ArrayList<ReleaseObject> releasesSortedByStartDate = releasePlanHelper.sortStartDate(releases);
	
    	StringBuilder result = new StringBuilder(releasePlanHelper.setJson(releasesSortedByStartDate, sprintPlanHelper));
		return result;
	}	
}