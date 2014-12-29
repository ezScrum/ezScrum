package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxAddSprintTaskAction extends PermissionAction {

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintBacklog();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession()
				.getAttribute("UserSession");

		// get parameter info
		String sprintId = request.getParameter("sprintId");
		String storyId = request.getParameter("issueID");

		// 表格的資料
		TaskInfo taskInfomation = new TaskInfo();
		taskInfomation.name = request.getParameter("Name");
		taskInfomation.estiamte = Integer.parseInt(request.getParameter("Estimate"));
		taskInfomation.notes = request.getParameter("Notes");
		taskInfomation.specificTime = Long.parseLong(request.getParameter("SpecificTime"));
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(
				project, session, sprintId);
		IIssue issue = sprintBacklogHelper.createTaskInStory(storyId,
				taskInfomation);

		// 組出回傳資訊
		StringBuilder sb = new StringBuilder();
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		sb.append("<AddNewTask><Result>true</Result><Task>");
		sb.append("<Id>" + issue.getIssueID() + "</Id>");
		sb.append("<Link>" + tsc.TranslateXMLChar(issue.getIssueLink())
				+ "</Link>");
		sb.append("<Name>" + tsc.TranslateXMLChar(issue.getSummary())
				+ "</Name>");
		sb.append("<Estimate>" + issue.getEstimated() + "</Estimate>");
		sb.append("<Actual>" + issue.getActualHour() + "</Actual>");
		sb.append("<Notes>" + tsc.TranslateXMLChar(issue.getNotes())
				+ "</Notes>");
		sb.append("</Task></AddNewTask>");
		return sb;
	}
}
