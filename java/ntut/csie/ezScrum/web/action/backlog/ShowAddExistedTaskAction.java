package ntut.csie.ezScrum.web.action.backlog;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ShowAddExistedTaskAction extends PermissionAction {
	private static Log log = LogFactory.getLog(ShowAddExistedTaskAction.class);
	
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
		log.info("Show wild Tasks in ShowAddExistedTaskAction");
		
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		ProjectObject projectObject = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		long projectId = projectObject.getId();
		ArrayList<TaskObject> wildedTasks = null;
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, session);
		
		try {
			wildedTasks = sprintBacklogHelper.getWildTasks(projectId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// 封裝 Task 成 XML
    	StringBuilder sb = new StringBuilder();
    	TranslateSpecialChar tsc = new TranslateSpecialChar(); 
    	if(sprintBacklogHelper != null) {
    		sb.append("<Tasks>");
    		/**
    		 * 發現此兩個參數在前端並無用到，因此暫時註解，如果之後發現沒問題，即可刪除
    		 * @author Zam, Alex
    		 * @time 2013/2/6
    		 */
    		if (wildedTasks != null) {
	    		for(int i = 0; i < wildedTasks.size(); i++) {
	    			TaskObject task = wildedTasks.get(i);
					sb.append("<Task>")
					  .append("<Id>").append(task.getId()).append("</Id>")
					  .append("<Link>/ezScrum/showIssueInformation.do?issueID=").append(task.getId()).append("</Link>")
					  .append("<Name>").append(tsc.TranslateXMLChar(task.getName())).append("</Name>")
					  .append("<Status>").append(task.getStatusString()).append("</Status>")
					  .append("<Estimate>").append(task.getEstimate()).append("</Estimate>")
					  .append("<Actual>").append(task.getActual()).append("</Actual>")
					  .append("<Handler>").append(task.getHandler().getUsername()).append("</Handler>")
					  .append("<Partners>").append(task.getPartnersUsername()).append("</Partners>")
					  .append("<Notes>").append(tsc.TranslateXMLChar(task.getNotes())).append("</Notes>")
					.append("</Task>");
				}
    		}
			sb.append("</Tasks>");
    	}

		return sb;
	}
}