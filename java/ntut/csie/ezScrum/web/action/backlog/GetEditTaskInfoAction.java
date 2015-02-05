package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class GetEditTaskInfoAction extends PermissionAction {
//	private static Log log = LogFactory.getLog(GetEditTaskInfoAction.class);

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
		
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		String sprintId = request.getParameter("sprintID");
		long taskId = Long.parseLong(request.getParameter("issueID"));
		
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, userSession, sprintId);
		
		StringBuilder result = new StringBuilder();
		TaskObject task = sprintBacklogHelper.getTask(taskId);
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		
		String handlerUsername = "";
		AccountObject handler = task.getHandler();
		if (handler != null) {
			handlerUsername = handler.getUsername();
		}
		result.append("<EditTask><Task>");
		result.append("<Id>" + task.getId() + "</Id>");
		result.append("<Name>" + tsc.TranslateXMLChar(task.getName()) + "</Name>");
		result.append("<Estimate>" + task.getEstimate() + "</Estimate>");
		result.append("<Actual>" + task.getActual() + "</Actual>");
		result.append("<Handler>" + tsc.TranslateXMLChar(handlerUsername) + "</Handler>");
		result.append("<Remains>" + task.getRemains() + "</Remains>");
		result.append("<Partners>" + tsc.TranslateXMLChar(task.getPartnersUsername()) + "</Partners>");
		result.append("<Notes>" + tsc.TranslateXMLChar(task.getNotes()) + "</Notes>");
		result.append("</Task></EditTask>");
		
		return result;
	}
}