package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataInfo.SprintInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class SaveSprintPlanAction extends PermissionAction {
	// private static Log log = LogFactory.getLog(SaveSprintPlanAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintPlan();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		// get project from session or DB
		ProjectObject project = (ProjectObject) SessionManager
				.getProjectObject(request);

		// get parameter info
		String isCreate = request.getParameter("isCreate");

		// get parameter info
		String sprintId = request.getParameter("Id");
		String sprintGoal = TranslateSpecialChar.TranslateXMLChar(request
				.getParameter("Goal"));
		String startDate = request.getParameter("StartDate");
		String interval = request.getParameter("Interval");
		String membersAmount = request.getParameter("Members");
		String hoursCanCommit = request.getParameter("AvaliableDays");
		String focusFactor = request.getParameter("FocusFactor");
		String dailyInfo = TranslateSpecialChar.TranslateXMLChar(request
				.getParameter("DailyScrum"));
		String demoDate = request.getParameter("DemoDate");
		String demoPlace = TranslateSpecialChar.TranslateXMLChar(request
				.getParameter("DemoPlace"));
		String dueDate = request.getParameter("DueDate");

		// set sprint info
		SprintInfo sprintInfo = new SprintInfo();
		sprintInfo.id = Long.parseLong(sprintId);
		sprintInfo.sprintGoal = sprintGoal;
		sprintInfo.startDate = startDate;
		sprintInfo.interval = Integer.parseInt(interval);
		sprintInfo.teamSize = Integer.parseInt(membersAmount);
		sprintInfo.hoursCanCommit = Integer.parseInt(hoursCanCommit);
		sprintInfo.focusFactor = Integer.parseInt(focusFactor);
		sprintInfo.dailyInfo = dailyInfo;
		sprintInfo.demoDate = demoDate;
		sprintInfo.demoPlace = demoPlace;
		sprintInfo.dueDate = dueDate;

		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);

		if ((isCreate != null) && Boolean.parseBoolean(isCreate)) {
			// create sprint
			sprintPlanHelper.createSprint(sprintInfo);
		} else {
			// edit sprint
			sprintPlanHelper.updateSprint(sprintInfo.id, sprintInfo);
		}

		return new StringBuilder("true");
	}
}
