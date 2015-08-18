package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

public class GetSprintPlanComboInfoWithAllAction extends PermissionAction {
	// private static Log log =
	// LogFactory.getLog(GetSprintPlanComboInfoWithAllAction.class);

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

		// get parameter
		String currentSprintId = request.getParameter("SprintID");

		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		ArrayList<SprintObject> sprints = sprintPlanHelper.getSprints();

		SprintObject currentSprint = null;

		if (currentSprintId == null) {
			currentSprint = sprintPlanHelper.getCurrentSprint();
		} else {
			currentSprint = sprintPlanHelper.getSprint(Long
					.parseLong(currentSprintId));
		}

		SprintPlanUI sprintPlanUI = new SprintPlanUI(sprints, currentSprint);

		Gson gson = new Gson();
		return new StringBuilder(gson.toJson(sprintPlanUI));
	}

	private class SprintPlanUI {
		private List<SprintPlanItem> Sprints = new LinkedList<SprintPlanItem>();
		private SprintPlanItem CurrentSprint = null;

		public SprintPlanUI(ArrayList<SprintObject> sprints,
				SprintObject currentSprint) {
			this.CurrentSprint = new SprintPlanItem(currentSprint);

			// 多一個 all 的選項
			if (sprints != null && sprints.size() > 0) {
				this.Sprints.add(new SprintPlanItem("ALL", "ALL"));
			}

			for (int i = sprints.size() - 1; i >= 0; i--) {
				Sprints.add(new SprintPlanItem(sprints.get(i)));
			}
		}
	}

	private class SprintPlanItem {
		private String Id = "";
		private String Info = "";

		public SprintPlanItem(String id, String info) {
			this.Id = id;
			this.Info = info;
		}

		public SprintPlanItem(SprintObject sprint) {
			if (sprint != null) {
				this.Id = String.valueOf(sprint.getId());
				this.Info = "Sprint #" + String.valueOf(sprint.getId());
			} else {
				this.Id = "0";
				this.Info = "Sprint None";
			}
		}
	}
}
