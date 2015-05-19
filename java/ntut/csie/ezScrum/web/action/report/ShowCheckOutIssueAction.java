package ntut.csie.ezScrum.web.action.report;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.support.TranslateSpecialChar;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

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
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		StringBuilder result = new StringBuilder("");
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(project);
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project);

		String defaultHandlerUsername = session.getAccount().getUsername();
		try {
			long issueId = Long.parseLong(request.getParameter("issueID"));
			String issueType = request.getParameter("issueType");
			if (issueType.equalsIgnoreCase("Story")) {
				StoryObject story = productBacklogHelper.getStory(issueId);
				result.append(getStoryJsonString(story));			
			} else if (issueType.equalsIgnoreCase("Task")) {
				TaskObject task = sprintBacklogHelper.getTask(issueId);
				result.append(getTaskJsonString(task, defaultHandlerUsername));
			}
		} catch (Exception e) {
			result.append(getIssueJsonString(null, defaultHandlerUsername));
			log.debug("class : ShowCheckOutTaskAction, method : execute, exception : " + e.toString());
		}
		return result;
	}

	private StringBuilder getIssueJsonString(IIssue issue, String handlerUsername) {
		StringBuilder result = new StringBuilder();
		TranslateSpecialChar translate = new TranslateSpecialChar();
		if (issue != null) {
			result.append("{\"Story\":{")
			        .append("\"Id\":\"").append(issue.getIssueID()).append("\",")
			        .append("\"Name\":\"").append(translate.TranslateJSONChar(issue.getSummary())).append("\",")
			        .append("\"Partners\":\"").append(issue.getPartners()).append("\",")
			        .append("\"Notes\":\"").append(translate.TranslateJSONChar(issue.getNotes())).append("\",")
			        .append("\"Handler\":\"").append(handlerUsername).append("\",")
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
	
	private StringBuilder getTaskJsonString(TaskObject task, String handlerUsername) {
		StringBuilder result = new StringBuilder();
		TranslateSpecialChar translate = new TranslateSpecialChar();
		if (task != null) {
			result.append("{\"Task\":{")
			        .append("\"Id\":\"").append(task.getId()).append("\",")
			        .append("\"Name\":\"").append(translate.TranslateJSONChar(task.getName())).append("\",")
			        .append("\"Partners\":\"").append(task.getPartnersUsername()).append("\",")
			        .append("\"Notes\":\"").append(translate.TranslateJSONChar(task.getNotes())).append("\",")
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
		TranslateSpecialChar translate = new TranslateSpecialChar();
		if (story != null) {
			result.append("{\"Story\":{")
			        .append("\"Id\":\"").append(story.getId()).append("\",")
			        .append("\"Name\":\"").append(translate.TranslateJSONChar(story.getName())).append("\",")
			        .append("\"Partners\":\"").append("\",")
			        .append("\"Notes\":\"").append(translate.TranslateJSONChar(story.getNotes())).append("\",")
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