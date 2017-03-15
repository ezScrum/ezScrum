package ntut.csie.ezScrum.web.action.report;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.iteration.support.TranslateSpecialChar;

public class ShowCheckOutIssueAction extends PermissionAction {
	private static Log log = LogFactory.getLog(ShowCheckOutIssueAction.class);

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

		// get project from session or DB
		ProjectObject project = SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		StringBuilder result = new StringBuilder("");
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(project);
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project);

		String defaultHandlerUsername = session.getAccount().getUsername();
		try {
			long serialNumber = Long.parseLong(request.getParameter("issueID"));
			String issueType = request.getParameter("issueType");
			if (issueType.equalsIgnoreCase("Story")) {
				StoryObject story = productBacklogHelper.getStory(project.getId(), serialNumber);
				result.append(getStoryJsonString(story));			
			} else if (issueType.equalsIgnoreCase("Task")) {
				TaskObject task = sprintBacklogHelper.getTask(project.getId(), serialNumber);
				result.append(getTaskJsonString(task, defaultHandlerUsername));
			}
		} catch (Exception e) {
			result.append("");
			log.debug("class : ShowCheckOutTaskAction, method : execute, exception : " + e.toString());
		}
		return result;
	}
	
	private StringBuilder getTaskJsonString(TaskObject task, String handlerUsername) {
		StringBuilder result = new StringBuilder();
		if (task != null) {
			result.append("{\"Task\":{")
			        .append("\"Id\":\"").append(task.getId()).append("\",")
			        .append("\"Name\":\"").append(TranslateSpecialChar.TranslateJSONChar(task.getName())).append("\",")
			        .append("\"Partners\":\"").append(TranslateSpecialChar.TranslateXMLChar(task.getPartnersUsername())).append("\",")
			        .append("\"Notes\":\"").append(TranslateSpecialChar.TranslateJSONChar(task.getNotes())).append("\",")
			        .append("\"Handler\":\"").append(handlerUsername).append("\",")
			        .append("\"IssueType\":\"").append("Task").append("\"")
			        .append("},")
			        .append("\"success\":true,")
			        .append("\"Total\":1")
			        .append("}");
		} else {
			result.append("");
		}
		return result;
	}
	
	private StringBuilder getStoryJsonString(StoryObject story) {
		StringBuilder result = new StringBuilder();
		if (story != null) {
			result.append("{\"Story\":{")
			        .append("\"Id\":\"").append(story.getId()).append("\",")
			        .append("\"Name\":\"").append(TranslateSpecialChar.TranslateJSONChar(story.getName())).append("\",")
			        .append("\"Partners\":\"").append("\",")
			        .append("\"Notes\":\"").append(TranslateSpecialChar.TranslateJSONChar(story.getNotes())).append("\",")
			        .append("\"Handler\":\"").append("\",")
			        .append("\"IssueType\":\"").append("Story").append("\",")
			        .append("},")
			        .append("\"success\":true,")
			        .append("\"Total\":1")
			        .append("}");
		} else {
			result.append("");
		}
		return result;
	}
}