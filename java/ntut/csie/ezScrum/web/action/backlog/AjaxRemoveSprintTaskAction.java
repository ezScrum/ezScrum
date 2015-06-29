package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxRemoveSprintTaskAction extends PermissionAction {

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintBacklog();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ProjectObject project = SessionManager.getProjectObject(request);
		
		//  get parameter info
		long sprintId = Long.parseLong(request.getParameter("sprintID"));
		long taskId = Long.parseLong(request.getParameter("issueID"));

		//  remove the task and clear its info
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, sprintId);
		sprintBacklogHelper.dropTask(taskId);
		
		StringBuilder result = new StringBuilder();
		result.append("<DropTask><Result>true</Result><Task><Id>")
				.append(taskId)
				.append("</Id></Task></DropTask>");
		
		return result;
	}
}
