package ntut.csie.ezScrum.web.action.report;

import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.control.ScheduleReport;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowScheduleReportAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		// get project from session or DB
		ProjectObject project = SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession()
				.getAttribute("UserSession");

		String serialSprintIdString = request.getParameter("sprintID");
		ScheduleReport report = null;
		if (serialSprintIdString == null) {
			report = new ScheduleReport(project, session);
		} else {
			long serialSprintId = Long.parseLong(serialSprintIdString);
			SprintObject sprint = SprintObject.get(project.getId(), serialSprintId);
			report = new ScheduleReport(project, session,
					sprint.getId());
		}

		try {
			report.generateChart();
		} catch (NullPointerException ex) {
			// "專案中目前沒有存在sprint!";
			String message = "No sprints in project!";
			request.setAttribute("message", message);
			return mapping.findForward("displayMessage");
		} catch (Exception ex) {
			// "目前無法產生任何圖表";
			String message = "System failure!";
			request.setAttribute("message", message);
			return mapping.findForward("displayMessage");
		}

		request.setAttribute("report", report);

		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		ArrayList<SprintObject> sprints = sprintPlanHelper.getSprints();
		Collections.reverse(sprints); // 資料反轉、倒序
		request.setAttribute("SprintPlans", sprints); // set sprint combo

		AccountObject account = session.getAccount();
		ScrumRole scrumRole = new ScrumRoleLogic().getScrumRole(project,
				account);
		if (scrumRole.getAccessReport()) {
			return mapping.findForward("success");
		} else {
			String message = "System failure!";// "目前無法產生任何圖表"
			request.setAttribute("message", message);
			return mapping.findForward("displayMessage");
		}
	}

}
