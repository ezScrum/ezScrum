package ntut.csie.ezScrum.web.action.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetTasksByStoryIDAction extends Action {
	private static Log log = LogFactory.getLog(GetTasksByStoryIDAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		IProject project = (IProject) request.getSession().getAttribute("Project");	
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		
    	String storyID = request.getParameter("storyID");
    	String sprintID = request.getParameter("sprintID");
    	SprintBacklogMapper backlog = (new SprintBacklogLogic(project, userSession, sprintID)).getSprintBacklogMapper();
		
    	// 封裝 Task 成 XML
    	StringBuilder sb = new StringBuilder();
    	TranslateSpecialChar tsc = new TranslateSpecialChar();
    	if(backlog!=null)
    	{
    		sb.append("<Tasks>");
    		// 取出這個 Sprint 的 Tasks
    		Map<Long, ArrayList<TaskObject>> map = backlog.getTasksMap();
    		// 取出 指定 Story 底下的 Tasks
    		ArrayList<TaskObject> tasks = map.get(Long.valueOf(storyID));
    		if (tasks != null) {
	    		for(TaskObject task : tasks) {	
	    			String handlerUsername = task.getHandler() != null ? task.getHandler().getUsername() : "";
	    			
					sb.append("<Task>");
					sb.append("<Id>").append(task.getId()).append("</Id>");
					sb.append("<Link>").append("").append("</Link>");
					sb.append("<Name>").append(tsc.TranslateXMLChar(task.getName())).append("</Name>");
					sb.append("<Estimate>").append(task.getEstimate()).append("</Estimate>");
					sb.append("<Actual>").append(task.getActual()).append("</Actual>");
					sb.append("<Handler>").append(handlerUsername).append("</Handler>");
					sb.append("<Partners>").append(tsc.TranslateXMLChar(task.getPartnersUsername())).append("</Partners>");
					sb.append("<Notes>").append(tsc.TranslateXMLChar(task.getNotes())).append("</Notes>");
					sb.append("</Task>");
				}
    		}
			sb.append("</Tasks>");
    	}
    	else
    	{
    		sb.append("<Tasks></Tasks>");
    	}
		
		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(sb.toString());
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}