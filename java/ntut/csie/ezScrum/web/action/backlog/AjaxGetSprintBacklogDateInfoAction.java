package ntut.csie.ezScrum.web.action.backlog;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AjaxGetSprintBacklogDateInfoAction extends Action {
	private static Log log = LogFactory.getLog(AjaxGetSprintBacklogDateInfoAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		log.info("Get Sprint Backlog Date.");

		ProjectObject project = SessionManager.getProjectObject(request);
		String sprintIdString = request.getParameter("sprintID");
		
		long sprintId = -1;

		if (sprintIdString != null && sprintIdString.length() > 0) {
			sprintId = Long.parseLong(request.getParameter("sprintID"));
		}

		String result = (new SprintBacklogHelper(project, sprintId)).getAjaxGetSprintBacklogDateInfo();
		response.setContentType("text/html; charset=utf-8");
		try {
			response.getWriter().write(result);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}