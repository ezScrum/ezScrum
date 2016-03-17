package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
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
		log.info("Add dropped tasks in AddExistedTaskAction");
		
		// get parameter info
		ProjectObject project = SessionManager.getProject(request);
		String[] selectedSerialTaskIds = request.getParameterValues("selected");
		long serialSprintId, serialStoryId;
		
		try {
			serialSprintId = Long.parseLong(request.getParameter("sprintID"));
		} catch (NumberFormatException e) {
			serialSprintId = -1;
		}
		
		try {
			serialStoryId = Long.parseLong(request.getParameter("issueID"));
		} catch (NumberFormatException e) {
			serialStoryId = -1;
		}

		// Get Sprint
		SprintObject sprint = SprintObject.get(project.getId(), serialSprintId);
		long sprintId = -1;
		if (sprint != null) {
			sprintId = sprint.getId();
		}
		
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, sprintId);
		try {
			// Get Story
			StoryObject story = StoryObject.get(project.getId(), serialStoryId);
			long storyId = -1;
			if (story != null) {
				storyId = story.getId();
			}
			sprintBacklogHelper.addExistingTasksToStory(selectedSerialTaskIds, storyId);
		} catch (Exception e) {
			return new StringBuilder(e.getMessage());
		}
		
		return new StringBuilder("");
	}
}
