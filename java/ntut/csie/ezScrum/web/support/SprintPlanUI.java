package ntut.csie.ezScrum.web.support;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.web.dataObject.SprintObject;

public class SprintPlanUI {
	private List<SprintPlanItem> Sprints = new LinkedList<SprintPlanItem>();

	public SprintPlanUI(ArrayList<SprintObject> sprints) {
		if (sprints != null) {
			for (SprintObject sprint : sprints) {
				Sprints.add(new SprintPlanItem(sprint));
			}
		} else {
			Sprints.add(new SprintPlanItem());
		}
	}

	private class SprintPlanItem {
		private String Id = "0";
		private String Goal = "";
		private String StartDate = "";
		private String Interval = "";
		private String Members = "";
		private String AvaliableDays = "";
		private String FocusFactor = "";
		private String DailyScrum = "";
		private String DemoDate = "";
		private String DemoPlace = "";
		private String DueDate = "";

		public SprintPlanItem() {

		}

		public SprintPlanItem(SprintObject sprint) {
			this.Id = String.valueOf(sprint.getId());
			this.Goal = sprint.getSprintGoal();
			this.StartDate = sprint.getStartDateString();
			this.Interval = String.valueOf(sprint.getInterval());
			this.DueDate = sprint.getDueDateString();
			this.Members = String.valueOf(sprint.getMembersAmount());
			this.AvaliableDays = sprint.getHoursCanCommit() + " hours";
			this.FocusFactor = String.valueOf(sprint.getFocusFactor());
			this.DailyScrum = sprint.getDailyInfo();
			this.DemoDate = sprint.getDemoDateString();
			this.DemoPlace = sprint.getDemoPlace();
		}
	}
}
