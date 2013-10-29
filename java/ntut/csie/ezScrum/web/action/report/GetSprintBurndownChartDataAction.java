package ntut.csie.ezScrum.web.action.report;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.SecurityRequestProcessor;
import ntut.csie.ezScrum.web.helper.TaskboardHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetSprintBurndownChartDataAction extends Action {
	private static Log log = LogFactory.getLog(GetSprintBurndownChartDataAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		log.info(" Get Sprint Burndown Chart Data. In Project Summary Page.");

		// get project from session or DB
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		String sprintID = request.getParameter("SprintID");
		String type = request.getParameter("Type");
		// 拿出SprintBurndownChart的資料
		String responseText = new TaskboardHelper(project, session, sprintID).getSprintBurndownChartDataResponseText(type);

		try {
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().write(responseText);
			LogFactory.getLog(SecurityRequestProcessor.class).debug("Current Time : " + new Date().toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
