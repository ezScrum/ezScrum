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

public class AjaxEditStoryAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxEditStoryAction.class);
	
	@Override
	public boolean isValidAction() {
		return (super.getScrumRole().getAccessProductBacklog() && 
				super.getScrumRole().getAccessTaskBoard());
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Edit Story in AjaxEditStoryAction.");
		
		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
		
		// get parameter info
		long id = Long.parseLong(request.getParameter("issueID"));
		String name = request.getParameter("Name");
		String importances = request.getParameter("Importance");
		String estimate = request.getParameter("Estimate");
		String value = request.getParameter("Value");
		String howToDemo = request.getParameter("HowToDemo");
		String notes = request.getParameter("Notes");
		
		StoryInfo storyInfo = new StoryInfo();
		storyInfo.id = id;
		storyInfo.name = name;
		storyInfo.importance = Integer.parseInt(importances);
		storyInfo.estimate = Integer.parseInt(estimate);
		storyInfo.value = Integer.parseInt(value);
		storyInfo.howToDemo = howToDemo;
		storyInfo.notes = notes;
		
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(project);
		StoryObject story = productBacklogHelper.getStory(id);
		storyInfo.sprintId = story.getSprintId();
		story = productBacklogHelper.updateStory(id, storyInfo);
		StringBuilder result = productBacklogHelper.translateStoryToJson(story);
		return result;
	}
}
