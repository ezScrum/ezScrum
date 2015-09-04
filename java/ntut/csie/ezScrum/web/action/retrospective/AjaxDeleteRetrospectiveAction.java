package ntut.csie.ezScrum.web.action.retrospective;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
import ntut.csie.ezScrum.web.helper.RetrospectiveHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxDeleteRetrospectiveAction extends PermissionAction {
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessRetrospective();
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
		long retrospectiveId = Long.parseLong(request.getParameter("issueID"));
		// Create Helper
		RetrospectiveHelper retrospectiveHelper = new RetrospectiveHelper(project);
		// Get Retrospective
		RetrospectiveObject retrospective = retrospectiveHelper.getRetrospective(retrospectiveId);
		// Get Result
		StringBuilder result = retrospectiveHelper.getXML("delete", retrospective);
		// Do delete
		retrospectiveHelper.deleteRetrospective(retrospectiveId);
		return result;
	}
}
