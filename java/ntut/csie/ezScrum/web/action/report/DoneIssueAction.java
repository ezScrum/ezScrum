package ntut.csie.ezScrum.web.action.report;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScru.web.microservice.MicroserviceProxy;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.NotificationObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.Translation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class DoneIssueAction extends PermissionAction {
	private static Log log = LogFactory.getLog(DoneIssueAction.class);

	@Override
	public boolean isValidAction() {
		return (super.getScrumRole().getAccessProductBacklog() && (!super.getScrumRole().isGuest()));
	}

	@Override
	public boolean isXML() {
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		log.info("Done Issue in DoneIssueAction.");

		// get project from session or DB
		ProjectObject project = SessionManager.getProject(request);

		// get parameter info
		long issueId = Long.parseLong(request.getParameter("Id"));
		String name = request.getParameter("Name");
		String notes = request.getParameter("Notes");
		String changeDate = request.getParameter("ChangeDate");
		int actual = Integer.parseInt(request.getParameter("Actualhour"));
		String issueType = request.getParameter("IssueType");

		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project);
		StringBuilder result = new StringBuilder("");

		//Send Notification
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		AccountObject account = session.getAccount();
		ArrayList<Long> recipients_id = project.getProjectMembersId();
		String eventSource = request.getRequestURL().toString();
		String messageResponse = SendNotification(account, recipients_id, issueId, issueType, eventSource, project);
		
		
		if (issueType.equals("Story")) {
			sprintBacklogHelper.closeStory(issueId, name, notes, changeDate);
			// return done issue 相關相關資訊
			StoryObject story = sprintBacklogHelper.getStory(issueId);
			result.append(Translation.translateTaskboardStoryToJson(story, messageResponse));
		} else if (issueType.equals("Task")) {
			sprintBacklogHelper.closeTask(issueId, name, notes, actual, changeDate);
			TaskObject task = sprintBacklogHelper.getTask(issueId);
			result.append(Translation.translateTaskboardTaskToJson(task, messageResponse));
		}
		
		return result;
	}
	
	private String SendNotification(AccountObject sender, ArrayList<Long> recipients_id, long issueId, String issueType, String eventSource, ProjectObject project){
		String title = sender.getUsername() +" Done " + issueType +": " + issueId;
		String body = "In project:" + project.getName();
		
		MicroserviceProxy ap = new MicroserviceProxy(sender.getToken());
		NotificationObject notificationObject = new NotificationObject(title, body, eventSource);
		notificationObject.addMessageFilter("From", "ezScrum");
		notificationObject.addMessageFilter("Id", project.getName());
		notificationObject.addMessageFilter("event", "TaskBoard");
		AccountRESTClientProxy ap = new AccountRESTClientProxy(sender.getToken());
		return ap.sendNotification(sender.getId(), recipients_id, notificationObject);
	}
}
