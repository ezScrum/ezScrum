package ntut.csie.ezScrum.web.action.backlog;

import java.text.ParseException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AddExistedStoryAction extends PermissionAction {
	
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
		
		// get parameter info
		ProjectObject project = SessionManager.getProjectObject(request);
		String[] storiesId = request.getParameterValues("selects");
		long sprintId;
		try {
			sprintId = Long.parseLong(request.getParameter("sprintID"));
		} catch (ParseException e) {
			sprintId = -1;
		}
		
		ArrayList<Long> addedStoriesId = new ArrayList<Long>();
		for (String storyId : storiesId) {
			addedStoriesId.add(Long.parseLong(storyId));
		}
		
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, sprintId);
		sprintBacklogHelper.addExistingStory(addedStoriesId);
		
		return new StringBuilder("");
	}
}
