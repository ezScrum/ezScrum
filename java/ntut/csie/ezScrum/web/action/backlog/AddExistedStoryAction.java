package ntut.csie.ezScrum.web.action.backlog;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AddExistedStoryAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AddExistedStoryAction.class);
	
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
		log.info("Add dropped stories in AddExistedStoryAction");
		
		// get parameter info
		ProjectObject project = SessionManager.getProject(request);
		String[] selectedSerialStoriesId = request.getParameterValues("selects");
		long serialSprintId;
		try {
			serialSprintId = Long.parseLong(request.getParameter("sprintID"));
		} catch (NumberFormatException e) {
			serialSprintId = -1;
		}
		
		ArrayList<Long> addedStoriesId = new ArrayList<Long>();
		for (String storyId : selectedSerialStoriesId) {
			addedStoriesId.add(Long.parseLong(storyId));
		}
		SprintObject sprint = SprintObject.get(project.getId(), serialSprintId);
		long sprintId = -1;
		if (sprint != null) {
			sprintId = sprint.getId();
		}
		
		try {			
			SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, sprintId);
			sprintBacklogHelper.addExistingStory(addedStoriesId);
		} catch(Exception e) {
		}
		
		return new StringBuilder("");
	}
}
