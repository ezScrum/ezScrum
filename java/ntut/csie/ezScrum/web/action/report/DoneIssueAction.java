package ntut.csie.ezScrum.web.action.report;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
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
		
		if (issueType.equals("Story")) {
			sprintBacklogHelper.closeStory(issueId, name, notes, changeDate);
			// return done issue 相關相關資訊
			StoryObject story = sprintBacklogHelper.getStory(issueId);
			result.append(Translation.translateTaskboardStoryToJson(story));
		} else if (issueType.equals("Task")) {
			sprintBacklogHelper.closeTask(issueId, name, notes, actual, changeDate);
			TaskObject task = sprintBacklogHelper.getTask(issueId);
			result.append(Translation.translateTaskboardTaskToJson(task));
		}
		
		//Send Notification
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		String sender = session.getAccount().getUsername();
		ArrayList<Long> receiversId = project.getProjectMembersId();
		SendNotification(sender, receiversId, project.getId(), issueId, issueType, project.getName());
		
		return result;
	}
	
	private void SendNotification(String sender, ArrayList<Long> receiversId, long projectId, long issueId, String issueType,String projectName){
		NotificationObject notification = new NotificationObject();
		notification.setSender(sender);
		notification.setReceiversId(receiversId);
		notification.setProjectId(projectId);
		notification.setMessageTitle(sender +" Done " + issueType +": " + issueId);
		notification.setMessageBody("In project:" + projectName);
		notification.setFromURL("http://localhost:8080/ezScrum/viewProject.do?projectName=" + projectName);
		String result = notification.send();
		System.out.println(result);
	}
}
