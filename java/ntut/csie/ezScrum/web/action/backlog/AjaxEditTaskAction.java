package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxEditTaskAction extends PermissionAction {
	
	@Override
	public boolean isValidAction() {
		return (super.getScrumRole().getAccessSprintBacklog() && 
				super.getScrumRole().getAccessTaskBoard());
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get project from session or DB
		ProjectObject project = SessionManager.getProjectObject(request);
		
		// get parameter info
		long sprintId;
		
		String sprintIdString = request.getParameter("sprintID");

		if (sprintIdString == null || sprintIdString.length() == 0) {
			sprintId = -1;
		} else {
			sprintId = Long.parseLong(sprintIdString);
		}
		long taskId = Long.parseLong(request.getParameter("issueID"));
		
		// 表格的資料
		String name = request.getParameter("Name");
		String handler = request.getParameter("HandlerComboBox_ForEditTask");
		String partners = request.getParameter("Partners");
		int estimate = Integer.parseInt(request.getParameter("Estimate"));
		int remains = Integer.parseInt(request.getParameter("Remains"));
		int actual = Integer.parseInt(request.getParameter("Actual"));
		String notes = request.getParameter("Notes");
		
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.taskId = taskId;
		taskInfo.name = name;
		taskInfo.estimate = estimate;
		taskInfo.remains = remains;
		taskInfo.notes = notes;
		taskInfo.actual = actual;
		
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, sprintId);
		sprintBacklogHelper.updateTask(taskInfo, handler, partners);
		
		TaskObject task = TaskObject.get(taskId);
		String handlerUsername = task.getHandler() != null ? task.getHandler().getUsername() : "";
		
		StringBuilder result = new StringBuilder("");
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		result.append("<EditTask><Result>true</Result><Task>");
		result.append("<Id>").append(task.getId()).append("</Id>");
		result.append("<Link>").append("LINK").append("</Link>");
		result.append("<Name>").append(tsc.TranslateXMLChar(task.getName())).append("</Name>");
		result.append("<Estimate>").append(task.getEstimate()).append("</Estimate>");
		result.append("<Actual>").append(task.getActual()).append("</Actual>");
		result.append("<Handler>").append(handlerUsername).append("</Handler>");
		result.append("<Partners>").append(tsc.TranslateXMLChar(task.getPartnersUsername())).append("</Partners>");
		result.append("<Remains>").append(task.getRemains()).append("</Remains>");
		result.append("<Notes>").append(tsc.TranslateXMLChar(task.getNotes())).append("</Notes>");
		result.append("</Task></EditTask>");
		
		return result;
	}
}
