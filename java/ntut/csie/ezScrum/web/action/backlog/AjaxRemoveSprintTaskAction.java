package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
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
		ProjectObject project = SessionManager.getProject(request);
		
		//  get parameter info
		long serialSprintId = Long.parseLong(request.getParameter("sprintID"));
		long serialTaskId = Long.parseLong(request.getParameter("issueID"));

		
		// Get Sprint
		SprintObject sprint = SprintObject.get(project.getId(), serialSprintId);
		long sprintId = -1;
		if (sprint != null) {
			sprintId = sprint.getId();
		}
		
		//  remove the task and clear its info
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, sprintId);
		
		// Get Task
		TaskObject task = TaskObject.get(project.getId(), serialTaskId);
		long taskId = -1;
		if (task != null) {
			taskId = task.getId();
		}
		sprintBacklogHelper.dropTask(taskId);
		
		StringBuilder result = new StringBuilder();
		result.append("<DropTask><Result>true</Result><Task><Id>")
				.append(serialTaskId)
				.append("</Id></Task></DropTask>");
		
		return result;
	}
}
