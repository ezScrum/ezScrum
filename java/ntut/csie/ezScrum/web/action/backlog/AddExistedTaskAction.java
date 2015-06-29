package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AddExistedTaskAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AddExistedTaskAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintBacklog();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Add wild tasks in AddExistedTaskAction");
		
		// get parameter info
		ProjectObject project = SessionManager.getProjectObject(request);
		String[] selectedTaskIds = request.getParameterValues("selected");
		long sprintId, storyId;
		
		try {
			sprintId = Long.parseLong(request.getParameter("sprintID"));
		} catch (NumberFormatException e) {
			sprintId = -1;
		}
		
		try {
			storyId = Long.parseLong(request.getParameter("issueID"));
		} catch (NumberFormatException e) {
			storyId = -1;
		}

		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, sprintId);
		try {
			sprintBacklogHelper.addExistingTasksToStory(selectedTaskIds, storyId);
		} catch (Exception e) {
			return new StringBuilder(e.getMessage());
		}
		
		return new StringBuilder("");
	}
}
