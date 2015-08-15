package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.SprintPlanUI;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

public class GetOneSprintPlanAction extends PermissionAction {
	// private static Log log = LogFactory.getLog(GetOneSprintPlanAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessReleasePlan();
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
		ProjectObject project = SessionManager.getProjectObject(request);

		// get parameter info
		String isLatestSprintString = request.getParameter("lastsprint");
		String sprintIdString = request.getParameter("SprintID");

		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		ArrayList<SprintObject> sprints = new ArrayList<>();

		// // 只取得一筆 sprint
		Boolean isLatestSprint = false;
		long sprintId = -1;
		if (isLatestSprintString != null) {
			isLatestSprint = Boolean.parseBoolean(isLatestSprintString);
		}
		else if (sprintIdString != null) {
			sprintId = Long.parseLong(sprintIdString);
		}
		SprintObject sprint = sprintPlanHelper.getOneSprintInformation(isLatestSprint, sprintId);
		sprints.add(sprint);
		SprintPlanUI sprintPlanUI = new SprintPlanUI(sprints);

		Gson gson = new Gson();
		return new StringBuilder(gson.toJson(sprintPlanUI));
	}
}
