package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
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
		String sprintID = request.getParameter("sprintID");
		long issueID = Long.parseLong(request.getParameter("issueID"));
		
//		SprintBacklogMapper backlog = (new SprintBacklogLogic(project, userSession, sprintID)).getSprintBacklogMapper();
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, userSession, sprintID);
		
		StringBuilder result = new StringBuilder();
		IIssue issue = sprintBacklogHelper.getIssue(issueID);
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		result.append("<EditTask><Task>");
		result.append("<Id>" + issue.getIssueID() + "</Id>");
		result.append("<Name>" + tsc.TranslateXMLChar(issue.getSummary()) + "</Name>");
		result.append("<Estimation>" + issue.getEstimated() + "</Estimation>");
		result.append("<Actual>" + issue.getActualHour() + "</Actual>");
		result.append("<Handler>" + tsc.TranslateXMLChar(issue.getAssignto()) + "</Handler>");
		result.append("<Remains>" + issue.getRemains() + "</Remains>");
		result.append("<Partners>" + tsc.TranslateXMLChar(issue.getPartners()) + "</Partners>");
		result.append("<Notes>" + tsc.TranslateXMLChar(issue.getNotes()) + "</Notes>");
		result.append("</Task></EditTask>");
		
		
//		SprintBacklog backlog;
//    	try {
//	    	if (sprintID==null||sprintID.equals("")) {	    		
//	    		backlog = new SprintBacklog(project,userSession);
//	    	} else {
//	    		backlog = new SprintBacklog(project,userSession,Integer.parseInt(sprintID));
//	    	}
//	    } catch (Exception e){
//	    	backlog = null;
//	    }
		
//	    StringBuilder result = new StringBuilder();
//	    if (backlog != null) {
//			
//			IIssue issue = backlog.getIssue(issueID);
//			TranslateSpecialChar tsc = new TranslateSpecialChar();
//			
//			result.append("<EditTask><Task>");
//			result.append("<Id>" + issue.getIssueID() + "</Id>");
//			result.append("<Name>" + tsc.TranslateXMLChar(issue.getSummary()) + "</Name>");
//			result.append("<Estimation>" + issue.getEstimated() + "</Estimation>");
//			result.append("<Actual>" + issue.getActualHour() + "</Actual>");
//			result.append("<Handler>" + tsc.TranslateXMLChar(issue.getAssignto()) + "</Handler>");
//	
//			MantisAccountMapper helper = new MantisAccountMapper(project, userSession);
//			
//			result.append("<Remains>" + issue.getRemains() + "</Remains>");
//			result.append("<Partners>" + tsc.TranslateXMLChar(issue.getPartners()) + "</Partners>");
//			result.append("<Notes>" + tsc.TranslateXMLChar(issue.getNotes()) + "</Notes>");
//			result.append("</Task></EditTask>");
//	    } else {
//	    	result = null;
//	    }
		
		return result;
	}
}