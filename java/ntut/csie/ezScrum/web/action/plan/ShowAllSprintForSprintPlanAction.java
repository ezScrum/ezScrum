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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

public class ShowAllSprintForSprintPlanAction extends PermissionAction {
	private static Log log = LogFactory.getLog(ShowAllSprintForSprintPlanAction.class);
	
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
		log.info("Show all sprint for SprintPlan.");
		
		// get project from session or DB
		ProjectObject project = SessionManager.getProject(request);
		
		// get parameter info
		String lastsprint = request.getParameter("lastsprint");
		
		ArrayList<SprintObject> sprints = new ArrayList<>();
		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		
		// 只取得最後一筆 sprint
		if (lastsprint != null && Boolean.parseBoolean(lastsprint)) {
			SprintObject latestSprint = sprintPlanHelper.getLatestSprint();
			if (latestSprint != null) {
				sprints.add(latestSprint);
			} else {
				sprints.add(new SprintObject(project.getId()));	// empty
			}
		} else {
			sprints = sprintPlanHelper.getSprints();
		}
		
		SprintPlanUI sprintPlanUI = new SprintPlanUI(sprints);
		
		Gson gson = new Gson();
		return new StringBuilder(gson.toJson(sprintPlanUI));
	}
}
