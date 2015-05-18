package ntut.csie.ezScrum.web.action.report;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.control.ScheduleReport;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
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
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		String iter = request.getParameter("sprintID");
		ScheduleReport report = null;
		if (iter == null) {
			report = new ScheduleReport(project, session);
		} else {
			report = new ScheduleReport(project, session, Integer.parseInt(iter));
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

		SprintPlanHelper spHelper = new SprintPlanHelper(project);
		List<ISprintPlanDesc> plans = spHelper.loadListPlans();
		Collections.reverse(plans);  // 資料反轉、倒序
		request.setAttribute("SprintPlans", plans);  // set sprint combo

		AccountObject account = session.getAccount();
		ScrumRole sr = new ScrumRoleLogic().getScrumRole(project, account);
		if (sr.getReadReport()) {
			return mapping.findForward("success");
		} else {
			String message = "System failure!";// "目前無法產生任何圖表"
			request.setAttribute("message", message);
			return mapping.findForward("displayMessage");
		}
	}

}
