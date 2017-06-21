package ntut.csie.ezScrum.web.action.report;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.NotificationObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.Translation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ResetTaskAction extends PermissionAction {
	private static Log log = LogFactory.getLog(ResetTaskAction.class);

	@Override
	public boolean isValidAction() {
		return (super.getScrumRole().getAccessProductBacklog() && (!super
				.getScrumRole().isGuest()));
	}

	@Override
	public boolean isXML() {
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Reset Task in ResetTaskAction.");
		// get project from session or DB
		ProjectObject project = SessionManager.getProject(request);

		// get parameter info
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(
				project);
		long issueId = Long.parseLong(request.getParameter("Id"));
		String name = request.getParameter("Name");
		String bugNote = request.getParameter("Notes");
		String changeDate = request.getParameter("ChangeDate");

		sprintBacklogHelper.resetTask(issueId, name, bugNote, changeDate);

		// return reset task的相關資訊
		TaskObject task = sprintBacklogHelper.getTask(issueId);
		// IIssue issue = sprintBacklogHelper.getStory(issueID);
		StringBuilder result = new StringBuilder("");
		result.append(Translation.translateTaskboardTaskToJson(task));

		//Send Notification
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		String accToken = session.getAccount().getToken();
		String sender = session.getAccount().getUsername();
		ArrayList<Long> receiversId = project.getProjectMembersId();
		SendNotification(sender,accToken, receiversId, issueId, project.getName());

		return result;
	}
	
	private void SendNotification(String sender, String accToken, ArrayList<Long> receiversId, long taskId,String projectName){
		Configuration configuration = new Configuration();
		String fromServiceUrl;
		if(configuration.getServerUrl() == "127.0.0.1")
			fromServiceUrl = "localhost";
		else
			fromServiceUrl = configuration.getServerUrl();
		NotificationObject notification = new NotificationObject();
		notification.setSender(sender);
		notification.setAccToken(accToken);
		notification.setReceiversId(receiversId);
		notification.setMessageTitle(sender + " reset Task: " + taskId);
		notification.setMessageBody("In project:" + projectName);
		notification.setFromURL("http://"+fromServiceUrl+":8080/ezScrum/viewProject.do?projectName=" + projectName);
		String result = notification.send();
		System.out.println(result);
	}
}
