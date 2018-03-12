package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
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
		ProjectObject project = SessionManager.getProject(request);
		
		// get parameter info
		long serialSprintId;
		
		String serialSprintIdString = request.getParameter("sprintID");

		if (serialSprintIdString == null || serialSprintIdString.length() == 0) {
			serialSprintId = -1;
		} else {
			serialSprintId = Long.parseLong(serialSprintIdString);
		}
		long serialTaskId = Long.parseLong(request.getParameter("issueID"));
		
		// 表格的資料
		String name = request.getParameter("Name");
		String handler = request.getParameter("HandlerComboBox_ForEditTask");
		String partners = request.getParameter("Partners");
		int estimate = Integer.parseInt(request.getParameter("Estimate"));
		int remains = Integer.parseInt(request.getParameter("Remains"));
		//int actual = Integer.parseInt(request.getParameter("Actual"));
		String notes = request.getParameter("Notes");
		
		// Get Task
		TaskObject tempTask = TaskObject.get(project.getId(), serialTaskId);
		long taskId = -1;
		if (tempTask != null) {
			taskId = tempTask.getId();
		}
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.id = taskId;
		taskInfo.name = name;
		taskInfo.estimate = estimate;
		taskInfo.remains = remains;
		taskInfo.notes = notes;
		//taskInfo.actual = actual;
		
		// Get Sprint
		SprintObject sprint = SprintObject.get(project.getId(), serialSprintId);
		long sprintId = -1;
		if (sprint != null) {
			sprintId = sprint.getId();
		}
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, sprintId);
		sprintBacklogHelper.updateTask(taskInfo, handler, partners);
		
		TaskObject task = TaskObject.get(project.getId(), serialTaskId);
		String handlerUsername = task.getHandler() != null ? task.getHandler().getUsername() : "";
		
		StringBuilder result = new StringBuilder("");
		result.append("<EditTask><Result>true</Result><Task>");
		result.append("<Id>").append(task.getSerialId()).append("</Id>");
		result.append("<Link>").append("LINK").append("</Link>");
		result.append("<Name>").append(TranslateSpecialChar.TranslateXMLChar(task.getName())).append("</Name>");
		result.append("<Estimate>").append(task.getEstimate()).append("</Estimate>");
		//result.append("<Actual>").append(task.getActual()).append("</Actual>");
		result.append("<Handler>").append(handlerUsername).append("</Handler>");
		result.append("<Partners>").append(TranslateSpecialChar.TranslateXMLChar(task.getPartnersUsername())).append("</Partners>");
		result.append("<Remains>").append(task.getRemains()).append("</Remains>");
		result.append("<Notes>").append(TranslateSpecialChar.TranslateXMLChar(task.getNotes())).append("</Notes>");
		result.append("</Task></EditTask>");
		
		return result;
	}
}
