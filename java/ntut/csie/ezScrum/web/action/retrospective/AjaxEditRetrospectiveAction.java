package ntut.csie.ezScrum.web.action.retrospective;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.RetrospectiveHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxEditRetrospectiveAction extends PermissionAction {
	
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
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get project from session or DB
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		TranslateSpecialChar tsc = new TranslateSpecialChar();
		
		// get parameter info
		long issueID = Long.valueOf(request.getParameter("issueID"));
		
		String name = request.getParameter("Name");
		String sprintName = request.getParameter("SprintID");
		String sprintID = sprintName.substring(sprintName.indexOf("#") + 1);
		String type = request.getParameter("Type");
		String description = tsc.TranslateDBChar(request.getParameter("Description"));
		String status = request.getParameter("Status");
		
		RetrospectiveHelper rh = new RetrospectiveHelper(project,session);
		rh.edit(issueID, name, description, sprintID, type, status);
		IIssue issue = rh.get(issueID);
				
		return rh.getXML("edit", issue);
	}
}
