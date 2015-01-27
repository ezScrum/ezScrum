package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
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
		String sprintID = request.getParameter("sprintId");
		long issueID = Long.parseLong(request.getParameter("issueID"));
		
		// 表格的資料
		String taskName = request.getParameter("Name");
		String handler = request.getParameter("HandlerComboBox_ForEditTask");
		String partners = request.getParameter("Partners");
		String estimate = request.getParameter("Estimate");
		String remains = request.getParameter("Remains");
		String actual = request.getParameter("Actual");
		String notes = request.getParameter("Notes");
		
//		SprintBacklogMapper backlog = new SprintBacklogMapper(project, session, sprintID);
//		backlog.editTask(issueID, taskName, estimation, remains, handler, partners, actual, notes, null);
		
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, session, sprintID);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		sprintBacklogLogic.editTask(issueID, taskName, estimate, remains, handler, partners, actual, notes, null);
		
		IIssue issue = sprintBacklogMapper.getStory(issueID);		
		StringBuilder result = new StringBuilder("");
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		result.append("<EditTask><Result>true</Result><Task>");
		result.append("<Id>" + issue.getIssueID() + "</Id>");
		result.append("<Link>" + tsc.TranslateXMLChar(issue.getIssueLink()) + "</Link>");
		result.append("<Name>" + tsc.TranslateXMLChar(issue.getSummary()) + "</Name>");
		result.append("<Estimate>" + issue.getEstimated() + "</Estimate>");
		result.append("<Actual>" + issue.getActualHour() + "</Actual>");
		result.append("<Handler>" + issue.getAssignto() + "</Handler>");
		result.append("<Partners>" + tsc.TranslateXMLChar(issue.getPartners()) + "</Partners>");
		result.append("<Remains>" + issue.getRemains() + "</Remains>");
		result.append("<Notes>" + tsc.TranslateXMLChar(issue.getNotes()) + "</Notes>");
		result.append("</Task></EditTask>");
		
		return result;
	}
}
