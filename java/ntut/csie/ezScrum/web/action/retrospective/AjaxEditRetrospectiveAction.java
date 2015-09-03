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
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get project from session or DB
		ProjectObject project = SessionManager.getProjectObject(request);

		TranslateSpecialChar tsc = new TranslateSpecialChar();
		
		// get parameter info
		long retrospectiveId = Long.valueOf(request.getParameter("issueID"));
		
		String name = request.getParameter("Name");
		String description = tsc.TranslateDBChar(request.getParameter("Description"));
		String sprintName = request.getParameter("SprintID");
		String sprintIdString = sprintName.substring(sprintName.indexOf("#") + 1);
		String typeString = request.getParameter("Type");
		String statusString = request.getParameter("Status");
		
		RetrospectiveInfo retrospectiveInfo = new RetrospectiveInfo();
		retrospectiveInfo.id = retrospectiveId;
		long sprintId = Long.parseLong(sprintIdString);
		retrospectiveInfo.sprintId = sprintId;
		retrospectiveInfo.name = name;
		retrospectiveInfo.description = description;
		retrospectiveInfo.type = RetrospectiveObject.getTypeByTypeString(typeString);
		retrospectiveInfo.status = RetrospectiveObject.getStatusByStatusString(statusString);
		RetrospectiveHelper retrospectiveHelper = new RetrospectiveHelper(project);
		retrospectiveHelper.editRetrospective(retrospectiveInfo);
		RetrospectiveObject retrospective = retrospectiveHelper.getRetrospective(retrospectiveId);
				
		return retrospectiveHelper.getXML("edit", retrospective);
	}
}
