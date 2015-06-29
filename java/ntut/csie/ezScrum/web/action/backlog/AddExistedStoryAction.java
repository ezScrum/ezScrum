package ntut.csie.ezScrum.web.action.backlog;

import java.util.ArrayList;

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
		log.info("Add wild stories in AddExistedStoryAction");
		
		// get parameter info
		ProjectObject project = SessionManager.getProjectObject(request);
		String[] selectedStoriesId = request.getParameterValues("selects");
		long sprintId;
		try {
			sprintId = Long.parseLong(request.getParameter("sprintID"));
		} catch (NumberFormatException e) {
			sprintId = -1;
		}
		
		ArrayList<Long> addedStoriesId = new ArrayList<Long>();
		for (String storyId : selectedStoriesId) {
			addedStoriesId.add(Long.parseLong(storyId));
		}
		
		try {			
			SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, sprintId);
			sprintBacklogHelper.addExistingStory(addedStoriesId);
		} catch(Exception e) {
		}
		
		return new StringBuilder("");
	}
}
