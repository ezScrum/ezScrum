package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataInfo.StoryInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxAddNewStoryAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxAddNewStoryAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessProductBacklog();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Add New Story in AjaxAddNewStoryAction.");
		ProjectObject project = SessionManager.getProjectObject(request);
		
		String name = request.getParameter("Name");
		String importance = request.getParameter("Importance");
		String estimate = request.getParameter("Estimate");
		String value = request.getParameter("Value");
		String howToDemo = request.getParameter("HowToDemo");
		String notes = request.getParameter("Notes");
		String sprintId = request.getParameter("SprintId");
		String tags = request.getParameter("Tags");
		
		StoryInfo storyInfo = new StoryInfo();
		storyInfo.name = name;
		storyInfo.importance = (importance == null || importance.isEmpty() ? 0 : Integer.parseInt(importance));
		storyInfo.estimate = (estimate == null || estimate.isEmpty() ? 0 : Integer.parseInt(estimate));
		storyInfo.value = (value == null || value.isEmpty() ? 0 : Integer.parseInt(value));
		storyInfo.howToDemo = howToDemo;
		storyInfo.notes = notes;
		storyInfo.sprintId = (sprintId == null || sprintId.isEmpty()) ? -1 : Long.parseLong(sprintId);
		storyInfo.tags = tags;
		
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(project);
		long storyId = productBacklogHelper.addStory(project.getId(), storyInfo);
		StoryObject story = StoryObject.get(storyId);
		StringBuilder result = productBacklogHelper.translateStoryToJson(story);
		return result;
	}
}
