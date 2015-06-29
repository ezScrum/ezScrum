package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class GetEditTaskInfoAction extends PermissionAction {

	@Override
	public boolean isValidAction() {
		return (super.getScrumRole().getAccessSprintBacklog() && 
				super.getScrumRole().getAccessTaskBoard());
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
		
		// get parameter info
		long sprintId;
		
		String sprintIdString = request.getParameter("sprintID");
		
		if (sprintIdString == null || sprintIdString.length() == 0) {
			sprintId = -1;
		} else {
			sprintId = Long.parseLong(sprintIdString);
		}
		
		long taskId = Long.parseLong(request.getParameter("issueID"));
		
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, sprintId);
		
		StringBuilder result = new StringBuilder();
		TaskObject task = sprintBacklogHelper.getTask(taskId);
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		
		String handlerUsername = task.getHandler() != null ? task.getHandler().getUsername() : "";

		result.append("<EditTask><Task>");
		result.append("<Id>").append(task.getId()).append("</Id>");
		result.append("<Name>").append(tsc.TranslateXMLChar(task.getName())).append("</Name>");
		result.append("<Estimate>").append(task.getEstimate()).append("</Estimate>");
		result.append("<Actual>").append(task.getActual()).append("</Actual>");
		result.append("<Handler>").append(tsc.TranslateXMLChar(handlerUsername)).append("</Handler>");
		result.append("<Remains>").append(task.getRemains()).append("</Remains>");
		result.append("<Partners>").append(tsc.TranslateXMLChar(task.getPartnersUsername())).append("</Partners>");
		result.append("<Notes>").append(tsc.TranslateXMLChar(task.getNotes())).append("</Notes>");
		result.append("</Task></EditTask>");
		
		return result;
	}
}