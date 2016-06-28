package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScrum.issue.mail.service.core.GmailSender;
import ntut.csie.ezScrum.issue.mail.service.core.MailConfiguration;
import ntut.csie.ezScrum.web.action.PermissionAction;

public class SendSprintMailAction extends PermissionAction{
	private static Log log = LogFactory.getLog(SendSprintMailAction.class);

	@Override
	public boolean isValidAction() {
		System.out.println("here");
		return super.getScrumRole().getAccessSprintBacklog();
	}

	@Override
	public boolean isXML() {
		System.out.println("here isXML");
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		MailConfiguration mConfig = new MailConfiguration();
		String senderAddress = mConfig.getAccount();
		String password = mConfig.getPassword();
		String subject = request.getParameter("subject");
		String sprintGoal = request.getParameter("sprintGoal");
		String schedule = request.getParameter("schedule");
		String storyInfo = request.getParameter("storyInfo");
		String recivers = request.getParameter("Partner");
		GmailSender sender = new GmailSender(senderAddress, password);
		String sendResult = sender.send(recivers, subject, sprintGoal, storyInfo, schedule);
		StringBuilder result = new StringBuilder();
		result.append(sendResult);
		return result;
	}
}
