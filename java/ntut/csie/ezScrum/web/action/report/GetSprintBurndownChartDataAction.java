package ntut.csie.ezScrum.web.action.report;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.SecurityRequestProcessor;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.TaskBoardHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetSprintBurndownChartDataAction extends Action {
	private static Log log = LogFactory.getLog(GetSprintBurndownChartDataAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		log.info(" Get Sprint Burndown Chart Data. In Project Summary Page.");

		ProjectObject project = SessionManager.getProjectObject(request);
		long sprintId = Long.parseLong(request.getParameter("SprintID"));
		String type = request.getParameter("Type");
		// 拿出 SprintBurndownChart 的資料
		String responseText = new TaskBoardHelper(project, sprintId).getSprintBurndownChartDataResponseText(type);

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
