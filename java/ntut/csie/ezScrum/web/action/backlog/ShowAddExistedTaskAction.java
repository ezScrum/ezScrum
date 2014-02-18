package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ShowAddExistedTaskAction extends PermissionAction {
//	private static Log log = LogFactory.getLog(ShowAddExistedTaskAction.class);
	
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
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
//		String issueID = request.getParameter("issueID");
		String sprintID = request.getParameter("sprintID");
		
		IIssue[] issues = (new ProductBacklogHelper(project, session)).getAddableTasks();
		
		SprintBacklogMapper backlog = (new SprintBacklogLogic(project, session, sprintID)).getSprintBacklogMapper();
//		NumberFormat formater =  NumberFormat.getInstance();
		
		// 封裝 Task 成 XML
    	StringBuilder sb = new StringBuilder();
    	TranslateSpecialChar tsc = new TranslateSpecialChar(); 
    	if(backlog!=null){
    		sb.append("<Tasks>");
    		/**
    		 * 發現此兩個參數在前端並無用到，因此暫時註解，如果之後發現沒問題，即可刪除
    		 * @author Zam, Alex
    		 * @time 2013/2/6
    		 */
//    		sb.append("<TaskPointMsg>" + formater.format(backlog.getCurrentPoint(ScrumEnum.TASK_ISSUE_TYPE)) + "</TaskPointMsg>");
//    		sb.append("<TaskStoryPointMsg>" + formater.format(backlog.getCurrentPoint(Long.parseLong(issueID))) + "</TaskStoryPointMsg>");
    		if (issues != null)
    		{
	    		for(int i = 0; i < issues.length; i++){			
					sb.append("<Task>");
					sb.append("<Id>" + issues[i].getIssueID() + "</Id>");
					sb.append("<Link>" + tsc.TranslateXMLChar(issues[i].getIssueLink()) + "</Link>");
					sb.append("<Name>" + tsc.TranslateXMLChar(issues[i].getSummary()) + "</Name>");
					sb.append("<Status>" + issues[i].getStatus() + "</Status>");
					sb.append("<Estimate>" + issues[i].getEstimated() + "</Estimate>");
					sb.append("<Actual>" + issues[i].getActualHour() + "</Actual>");
					sb.append("<Handler>" + issues[i].getAssignto() + "</Handler>");
					sb.append("<Partners>" + issues[i].getPartners() + "</Partners>");
					sb.append("<Notes>" + tsc.TranslateXMLChar(issues[i].getNotes()) + "</Notes>");
					sb.append("</Task>");
				}
    		}
			sb.append("</Tasks>");
    	}

		return sb;
	}
}