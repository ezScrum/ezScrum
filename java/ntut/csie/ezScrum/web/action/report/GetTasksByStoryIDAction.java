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
    		if (tasks != null)
    		{
	    		for(TaskObject task : tasks){			
					sb.append("<Task>");
					sb.append("<Id>" + task.getId() + "</Id>");
					sb.append("<Link>" + "" + "</Link>");
					sb.append("<Name>" + tsc.TranslateXMLChar(task.getName()) + "</Name>");
					sb.append("<Estimate>" + task.getEstimate() + "</Estimate>");
					sb.append("<Actual>" + task.getActual() + "</Actual>");
					sb.append("<Handler>" + task.getHandler().getUsername() + "</Handler>");
					sb.append("<Partners>" + tsc.TranslateXMLChar(task.getPartnersUsername()) + "</Partners>");
					sb.append("<Notes>" + tsc.TranslateXMLChar(task.getNotes()) + "</Notes>");
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