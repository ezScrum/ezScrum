package ntut.csie.ezScrum.web.action.backlog;

import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.core.util.DateUtil;

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
		ProjectObject project = SessionManager.getProjectObject(request);

		// get parameter info
		long sprintId = Long.parseLong(request.getParameter("sprintId"));
		long storyId = Long.parseLong(request.getParameter("issueID"));
		int estimate = request.getParameter("Estimate").equals("") ? 0 : Integer.parseInt(request.getParameter("Estimate"));
		String name = request.getParameter("Name");
		String notes = request.getParameter("Notes");
		String specificTimeString = request.getParameter("SpecificTime");
		
		Date specificDate;
		try {
			specificDate  = DateUtil.parse(specificTimeString, DateUtil._16DIGIT_DATE_TIME);
		} catch (ParseException e) {
			specificDate = new Date();
		}
		
		// 表格的資料
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.name = name;
		taskInfo.storyId = storyId;
		taskInfo.estimate = estimate;
		taskInfo.notes = notes;
		taskInfo.specificTime = specificDate.getTime();
		
		ProjectObject projectObject = ProjectObject.get(project.getName());
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(
				project, sprintId);
		TaskObject task = sprintBacklogHelper.addTask(projectObject.getId(), taskInfo);

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
