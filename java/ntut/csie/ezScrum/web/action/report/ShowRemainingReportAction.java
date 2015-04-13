package ntut.csie.ezScrum.web.action.report;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.control.RemainingWorkReport;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowRemainingReportAction extends Action {
	private static Log log = LogFactory.getLog(ShowRemainingReportAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		// get project from session or DB
		IProject project = SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		String sprintId = request.getParameter("sprintID");
		String category = request.getParameter("type");
		String date = request.getParameter("Date");

		if (category == null) {
			category = ScrumEnum.TASK_ISSUE_TYPE;		// default category
		}

		SprintPlanHelper spHelper = new SprintPlanHelper(project);
		List<ISprintPlanDesc> plans = spHelper.loadListPlans();
		Collections.reverse(plans);  // 資料反轉、倒序
		request.setAttribute("SprintPlans", plans);  // set sprint combo

		// 專案中目前沒有存在 sprint
		if (plans.size() == 0) {
			String message = "No sprints in project!";
			request.setAttribute("message", message);

			return mapping.findForward("displayMessage");
		}

		String[] TypeList = new String[] {
		        ScrumEnum.TASK_ISSUE_TYPE, ScrumEnum.STORY_ISSUE_TYPE, ScrumEnum.ISSUE_ISSUE_TYPE, ScrumEnum.BUG_ISSUE_TYPE, ScrumEnum.FEATURE_ISSUE_TYPE
		};

		request.setAttribute("TypeList", TypeList);
		request.setAttribute("type", category);			// set catetory combo
		request.setAttribute("OutofSprint", false);		// set default value

		Date Today = new Date();					// 設定今日日期
		Date currentDate = null;
		if (date != null && date != "") {				// 得到使用者設定的時間
			date += ":00";
			currentDate = DateUtil.dayFillter(date, DateUtil._16DIGIT_DATE_TIME);
		}

		// 報表產生以兩種方式，一種對時間、另一種對sprint or type
		// 因此如果有時間就完全依照時間去產生報表
		try {
			RemainingWorkReport report = null;
			int currentID = -1;
			if (currentDate != null) {
				// generate by date

				// 日期不能超過今天，以及不存在於 sprint 區間
				if (currentDate.compareTo(Today) >= 0) {
					// 日期超過今天，回傳 defualt 為當下時間的報表
					request.setAttribute("OutofSprint", "OutOfDay");
					currentDate = Today;		// default set today to show report
				}

				currentID = spHelper.getSprintIDbyDate(currentDate);

				// 若 user 選擇的日期不在任何 sprint 內，且目前沒有進行中的 sprint
				if (currentID == -1) {
					currentID = Integer.parseInt(spHelper.loadCurrentPlan().getID());
				}

				report = new RemainingWorkReport(project, session, category, currentID, currentDate);

				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd-HH:mm");
				request.setAttribute("setDate", format.format(currentDate));
			} else {
				// generate by sprint
				if (sprintId == null) {
					currentID = Integer.parseInt(spHelper.loadCurrentPlan().getID());
				} else {
					currentID = Integer.parseInt(sprintId);
				}

				report = new RemainingWorkReport(project, session, category, currentID);

				request.setAttribute("setDate", "");
			}

			request.setAttribute("iteration", currentID);
			request.setAttribute("RemainingWorkReport", report);

			AccountObject account = session.getAccount();
			ScrumRole sr = new ScrumRoleLogic().getScrumRole(project, account);
			if (sr.getReadReport()) {
				return mapping.findForward("success");
			}
		} catch (Exception e) {
			this.log.debug(e.toString());
		}

		String message = "System failure!";// "目前無法產生任何圖表"
		request.setAttribute("message", message);
		return mapping.findForward("displayMessage");
	}
}