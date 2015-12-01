package ntut.csie.ezScrum.web.action.retrospective;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataInfo.RetrospectiveInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
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
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		// get project from session or DB
		ProjectObject project = SessionManager.getProjectObject(request);
		
		// get parameter info
		long retrospectiveId = Long.valueOf(request.getParameter("issueID"));
		
		String name = request.getParameter("Name");
		String sprintName = request.getParameter("SprintID");
		long sprintId = Long.parseLong(sprintName.substring(sprintName.indexOf("#") + 1));
		String type = request.getParameter("Type");
		String description = TranslateSpecialChar.TranslateDBChar(request.getParameter("Description"));
		String status = request.getParameter("Status");
		
		RetrospectiveHelper retrospectiveHelper = new RetrospectiveHelper(project);
		
		RetrospectiveInfo retrospectiveInfo = new RetrospectiveInfo();
		retrospectiveInfo.id = retrospectiveId;
		retrospectiveInfo.name = name;
		retrospectiveInfo.description = description;
		retrospectiveInfo.sprintId = sprintId;
		retrospectiveInfo.type = type;
		retrospectiveInfo.status = status;
		
		retrospectiveHelper.editRetrospective(retrospectiveInfo); 
		RetrospectiveObject retrospective = retrospectiveHelper.getRetrospective(retrospectiveId);
				
		return retrospectiveHelper.getXML("edit", retrospective);
	}
}
