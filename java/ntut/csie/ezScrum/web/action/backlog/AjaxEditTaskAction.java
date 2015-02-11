package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxEditTaskAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxEditTaskAction.class);
	
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
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		String sprintId = request.getParameter("sprintId");
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
		
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, session, sprintId);
		sprintBacklogHelper.updateTask(taskInfo, handler, partners);
		
		TaskObject task = TaskObject.get(taskId);
		String handlerUsername = task.getHandler() != null ? task.getHandler().getUsername() : "";
		
		StringBuilder result = new StringBuilder("");
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		result.append("<EditTask><Result>true</Result><Task>");
		result.append("<Id>" + task.getId() + "</Id>");
		result.append("<Link>" + "LINK" + "</Link>");
		result.append("<Name>" + tsc.TranslateXMLChar(task.getName()) + "</Name>");
		result.append("<Estimate>" + task.getEstimate() + "</Estimate>");
		result.append("<Actual>" + task.getActual() + "</Actual>");
		result.append("<Handler>" + handlerUsername + "</Handler>");
		result.append("<Partners>" + tsc.TranslateXMLChar(task.getPartnersUsername()) + "</Partners>");
		result.append("<Remains>" + task.getRemains() + "</Remains>");
		result.append("<Notes>" + tsc.TranslateXMLChar(task.getNotes()) + "</Notes>");
		result.append("</Task></EditTask>");
		
		return result;
	}
}
