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

public class GetEditRetrospectiveInfoAction extends PermissionAction {
	
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
		ProjectObject project = SessionManager.getProject(request);
		// get parameter info
		long serialRetrospectiveId = Long.parseLong(request.getParameter("issueID"));
		RetrospectiveHelper retrospectiveHelper = new RetrospectiveHelper(project);
		RetrospectiveObject retrospective = retrospectiveHelper.getRetrospective(project.getId(), serialRetrospectiveId);
		return retrospectiveHelper.getXML("get", retrospective);
	}
}