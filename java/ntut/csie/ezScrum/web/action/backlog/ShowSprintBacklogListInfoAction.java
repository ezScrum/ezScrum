package ntut.csie.ezScrum.web.action.backlog;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.SecurityRequestProcessor;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowSprintBacklogListInfoAction extends Action {
	private static Log log = LogFactory.getLog(ShowSprintBacklogListInfoAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Show Sprint Backlog List Information in ShowSprintBacklogListInfo.");

		ProjectObject projectObject = (ProjectObject) SessionManager.getProjectObject(request);
		String sprintIdString = request.getParameter("sprintID");
		long sprintId;
		if (sprintIdString == null) {
			sprintId = -1;
		} else {
			sprintId = Long.parseLong(sprintIdString);
		}

		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(
				projectObject, sprintId);
		String reponseText = this.reContructString(sprintBacklogHelper
				.getSprintBacklogListInfoText());

		response.setContentType("text/html; charset=utf-8");
		try {
			response.getWriter().write(reponseText);
			LogFactory.getLog(SecurityRequestProcessor.class).debug(
					"Current Time : " + new Date().toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	// 重建 json 字串讓資料可以對應 ext TreeGrid Column
	private String reContructString(String jsonStr) {
		while (jsonStr.contains("\"dateToHourMap\":{")) {
			int headIndex = jsonStr.indexOf("\"dateToHourMap\":{");
			int endIndex = jsonStr.indexOf("}", headIndex) + 2;
			String oriSubStr = jsonStr.substring(headIndex, endIndex);
			String changeSubStr = oriSubStr.replace("\"dateToHourMap\":{", "");
			changeSubStr = changeSubStr.replace("}", "");
			if (changeSubStr.equals(","))
				changeSubStr = "";

			jsonStr = jsonStr.replace(oriSubStr, changeSubStr);
		}

		return jsonStr;
	}
}