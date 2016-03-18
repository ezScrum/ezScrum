package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.core.util.DateUtil;

public class GetSprintPlanComboInfoAction extends PermissionAction {
	private static Log log = LogFactory
			.getLog(GetSprintPlanComboInfoAction.class);

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

		log.info("Get Sprint Plan Combo Information in GetSprintPlanComboInfoAction");

		// get project from session or DB
		ProjectObject project = (ProjectObject) SessionManager
				.getProject(request);

		// get parameter
		String currentSerialSprintId = request.getParameter("SprintID");

		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		ArrayList<SprintObject> sprints = sprintPlanHelper.getSprints();

		SprintObject currentSprint = null;

		if (currentSerialSprintId == null) {
			currentSprint = sprintPlanHelper.getCurrentSprint();
		} else {
			currentSprint = sprintPlanHelper.getSprint(Long
					.parseLong(currentSerialSprintId));
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
			if (sprints != null) {
				for (SprintObject sprint : sprints) {
					Sprints.add(new SprintPlanItem(sprint));
				}
			} else {
				Sprints.add(new SprintPlanItem(null));
			}
		}
	}

	private class SprintPlanItem {
		private String Id = "";
		private String Info = "";
		private String Edit = "";

		public SprintPlanItem(SprintObject sprint) {
			Date endDate;
			if (sprint != null) {
				endDate = DateUtil.dayFilter(sprint.getDueDateString());
				this.Id = String.valueOf(sprint.getSerialId());
				this.Info = "Sprint #" + String.valueOf(sprint.getSerialId());
				if (isOverSprint(endDate)) {
					this.Edit = "false";
				} else {
					this.Edit = "true";
				}
			} else {
				this.Id = "0";
				this.Info = "Sprint None";
				this.Edit = "false";
			}
		}

		public boolean isOverSprint(Date endDate) {
			Date today = new Date();
			if (today.after(endDate)) {
				return true;
			} else {
				return false;
			}

		}
	}
}
