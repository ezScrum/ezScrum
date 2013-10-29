package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;

public class SprintObject {
	public String id;
	public String sprintGoal;
	public String startDate;
	public String demoDate;
	public String endDate;
	public String interval;
	public String focusFactor;
	public String members;
	public String hoursCanCommit;
	public String notes;
	public String demoPlace;
	public String actualCost;
	public List<StoryObject> storyList = new ArrayList<StoryObject>();
	
	public SprintObject() {}
	
	public SprintObject(ISprintPlanDesc sprintDesc) {
		id = sprintDesc.getID();
		startDate = sprintDesc.getStartDate();
		interval = sprintDesc.getInterval();
		members = sprintDesc.getMemberNumber();
		focusFactor = sprintDesc.getFocusFactor();
		sprintGoal = sprintDesc.getGoal();
		hoursCanCommit = sprintDesc.getAvailableDays();
		demoDate = sprintDesc.getDemoDate();
		demoPlace = sprintDesc.getDemoPlace();
	}
	
	public void addStory(StoryObject story) {
		storyList.add(story);
	}
	
	public String toString() {
		String sprint = "id :" + id + 
				", startDate :" + startDate +
				", interval :" + interval +
				", members :" + members +
				", focusFactor :" + focusFactor +
				", hoursCanCommit :" + hoursCanCommit +
				", goal :" + sprintGoal +
				", demoDate :" + demoDate +
				", notes :" + notes +
				", demoPlace :" + demoPlace +
				", endDate :" + endDate +
				", actualCost :" + actualCost;
		for (StoryObject story : storyList)
			sprint += "\n" + story.toString();
		return sprint;
	}
}
