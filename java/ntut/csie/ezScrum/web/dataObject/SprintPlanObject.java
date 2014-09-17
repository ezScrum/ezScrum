package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;
import java.util.Map;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;

public class SprintPlanObject {
	private String id;
	private String goal;
	private String interval;
	private String memberNumber;
	private String factor;
	private String availableDays;
	private String startDate;
	private String endDate;
	private String demoDate;
	private String demoPlace;
	private String notes;
	private String actualCost;
	private Map<Integer,String> taskBoardStageMap;
	private ArrayList<StoryObject> stories = new ArrayList<StoryObject>();
	
	public SprintPlanObject(ISprintPlanDesc sprintDesc) {
		setId(sprintDesc.getID());
		setGoal(sprintDesc.getGoal());
		setInterval(sprintDesc.getInterval());
		setMemberNumber(sprintDesc.getMemberNumber());
		setFactor(sprintDesc.getFocusFactor());
		setAvailableDays(sprintDesc.getAvailableDays());
		setStartDate(sprintDesc.getStartDate());
		setEndDate(sprintDesc.getEndDate());
		setDemoDate(sprintDesc.getDemoDate());
		setDemoPlace(sprintDesc.getDemoPlace());
		setNotes(sprintDesc.getNotes());
		setActualCost(sprintDesc.getActualCost());
		setTaskBoardStageMap(sprintDesc.getTaskBoardStageMap());
    }

	public String getId() {
	    return id;
    }

	public void setId(String id) {
	    this.id = id;
    }

	public String getGoal() {
	    return goal;
    }

	public void setGoal(String goal) {
	    this.goal = goal;
    }

	public String getInterval() {
	    return interval;
    }

	public void setInterval(String interval) {
	    this.interval = interval;
    }

	public String getMemberNumber() {
	    return memberNumber;
    }

	public void setMemberNumber(String memberNumber) {
	    this.memberNumber = memberNumber;
    }

	public String getFactor() {
	    return factor;
    }

	public void setFactor(String factor) {
	    this.factor = factor;
    }

	public String getAvailableDays() {
	    return availableDays;
    }

	public void setAvailableDays(String availableDays) {
	    this.availableDays = availableDays;
    }

	public String getStartDate() {
	    return startDate;
    }

	public void setStartDate(String startDate) {
	    this.startDate = startDate;
    }

	public String getEndDate() {
	    return endDate;
    }

	public void setEndDate(String endDate) {
	    this.endDate = endDate;
    }

	public String getDemoDate() {
	    return demoDate;
    }

	public void setDemoDate(String demoDate) {
	    this.demoDate = demoDate;
    }

	public String getDemoPlace() {
	    return demoPlace;
    }

	public void setDemoPlace(String demoPlace) {
	    this.demoPlace = demoPlace;
    }

	public String getNotes() {
	    return notes;
    }

	public void setNotes(String notes) {
	    this.notes = notes;
    }

	public String getActualCost() {
	    return actualCost;
    }

	public void setActualCost(String actualCost) {
	    this.actualCost = actualCost;
    }

	public Map<Integer,String> getTaskBoardStageMap() {
	    return taskBoardStageMap;
    }

	public void setTaskBoardStageMap(Map<Integer,String> taskBoardStageMap) {
	    this.taskBoardStageMap = taskBoardStageMap;
    }

	public ArrayList<StoryObject> getStories() {
	    return stories;
    }

	public void setStories(ArrayList<StoryObject> stories) {
	    this.stories = stories;
    }
}
