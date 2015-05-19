package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class RemoveSprintPlanAction extends PermissionAction {
	private static Log log = LogFactory.getLog(RemoveSprintPlanAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintPlan();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		log.info(" Remove SprintPlan. ");
		
		// get project from session or DB
		ProjectObject project = SessionManager.getProjectObject(request);
		
		// get parameter info
		String sprintId = request.getParameter("sprintID");

		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, Long.parseLong(sprintId));
		ArrayList<StoryObject> stories = sprintBacklogLogic.getStories();
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(project);
		for (StoryObject story : stories) {
			productBacklogLogic.dropStoryFromSprint(story.getId());
		}
		
		//刪除sprint資訊
		SprintPlanHelper SPhelper = new SprintPlanHelper(project);
		SPhelper.deleteSprint(sprintId);
		StringBuilder result = new StringBuilder("{\"success\":true}");
		return result;
	}
}
