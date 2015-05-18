package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxShowStoryFromSprintAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxShowStoryFromSprintAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessReleasePlan();
	}

	@Override
	public boolean isXML() {
		return true;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		log.info("Show Story From Sprint in AjaxShowStoryFromSprintAction.");

		// get project from session or DB
		ProjectObject project = (ProjectObject) SessionManager.getProjectObject(request);

		// get parameter info
		String SprintID = request.getParameter("Sid");

		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, Long.parseLong(SprintID));
		ArrayList<StoryObject> stories = sprintBacklogHelper.getStoriesByImportance();

		return sprintBacklogHelper.getStoriesInSprintResponseText(stories);
	}
}
