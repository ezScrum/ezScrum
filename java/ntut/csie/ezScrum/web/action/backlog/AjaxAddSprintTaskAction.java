package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.resource.core.IProject;

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
		ProjectObject projectObject = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession()
				.getAttribute("UserSession");

		// get parameter info
		String sprintId = request.getParameter("sprintId");
		String storyId = request.getParameter("issueID");

		// 表格的資料
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.name = request.getParameter("Name");
		taskInfo.projectId = Long.parseLong(projectObject.getId());
		taskInfo.storyId = Long.parseLong(storyId);
		taskInfo.estiamte = Integer.parseInt(request.getParameter("Estimate"));
		taskInfo.notes = request.getParameter("Notes");
		taskInfo.specificTime = Long.parseLong(request.getParameter("SpecificTime"));
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(
				project, session, sprintId);
		TaskObject task = sprintBacklogHelper.createTaskInStory(taskInfo);

		// 組出回傳資訊
		StringBuilder sb = new StringBuilder();
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		sb.append("<AddNewTask><Result>true</Result><Task>");
		sb.append("<Id>" + task.getId() + "</Id>");
		sb.append("<Link>/ezScrum/showIssueInformation.do?issueID=" + task.getId()
				+ "</Link>");
		sb.append("<Name>" + tsc.TranslateXMLChar(task.getName())
				+ "</Name>");
		sb.append("<Estimate>" + task.getEstimate() + "</Estimate>");
		sb.append("<Actual>" + task.getActual() + "</Actual>");
		sb.append("<Notes>" + tsc.TranslateXMLChar(task.getNotes())
				+ "</Notes>");
		sb.append("</Task></AddNewTask>");
		return sb;
	}
}
