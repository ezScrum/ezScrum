package ntut.csie.ezScrum.web.mapper;

import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.web.dataInfo.SprintInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SerialNumberObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.jcis.core.util.DateUtil;

public class SprintPlanMapper {
	private ProjectObject mProject;
	
	public SprintPlanMapper(ProjectObject project) {
		mProject = project;
	}

	/**
	 * Add New SprintPlan
	 */
	public long addSprint(SprintInfo sprintInfo) {
		SprintObject sprint = new SprintObject(mProject.getId());
		sprint.setInterval(sprintInfo.interval)
		        .setMembers(sprintInfo.membersAmount)
		        .setAvailableHours(sprintInfo.hoursCanCommit)
		        .setFocusFactor(sprintInfo.focusFactor)
		        .setGoal(sprintInfo.sprintGoal)
		        .setStartDate(sprintInfo.startDate)
		        .setDemoDate(sprintInfo.demoDate)
		        .setDemoPlace(sprintInfo.demoPlace)
		        .setDailyInfo(sprintInfo.dailyInfo)
		        .setDueDate(sprintInfo.dueDate)
		        .save();
		return sprint.getId();
	}
	
	/**
	 * Get Sprint By SprintId
	 * @param sprintId
	 * @return SprintObject
	 */
	public SprintObject getSprint(long sprintId) {
		return SprintObject.get(sprintId);
	}

	/**
	 * Get Sprints in Project
	 * @return ArrayList<SprintObject>
	 */
	public ArrayList<SprintObject> getSprints() {
		return mProject.getSprints();
	}
	
	/**
	 * Get Stories in Sprint
	 * @param sprintId
	 * @return ArrayList<StoryObject>
	 */
	public ArrayList<StoryObject> getStoriesBySprintId(long sprintId) {
		SprintObject sprint = SprintObject.get(sprintId);
		ArrayList<StoryObject> storie = sprint.getStories();
		return storie;
	}
	
	/**
	 * Update Sprint by SprintInfo
	 * @param sprintInfo
	 */
	public void updateSprint(SprintInfo sprintInfo) {
		SprintObject sprint = SprintObject.get(sprintInfo.id);
		sprint.setInterval(sprintInfo.interval)
		        .setMembers(sprintInfo.membersAmount)
		        .setAvailableHours(sprintInfo.hoursCanCommit)
		        .setFocusFactor(sprintInfo.focusFactor)
		        .setGoal(sprintInfo.sprintGoal)
		        .setStartDate(sprintInfo.startDate)
		        .setDemoDate(sprintInfo.demoDate)
		        .setDemoPlace(sprintInfo.demoPlace)
		        .setDailyInfo(sprintInfo.dailyInfo)
		        .setDueDate(sprintInfo.dueDate)
		        .save();
	}

	/**
	 * Delete Sprint By SprintId
	 * @param sprintId
	 */
	public void deleteSprint(long sprintId) {
		SprintObject sprint = SprintObject.get(sprintId);
		if (sprint != null) {
			sprint.delete();
		}
	}
	
	/**
	 * Exchange Old Sprint SerialId with new Sprint SerialId
	 */
	public void moveSprint(long oldId, long newId) {
		SprintObject oldSprint = SprintObject.get(oldId);
		SprintObject newSprint = SprintObject.get(newId);
		// exchange Serial Id
		if (oldSprint == null || newSprint == null) {
			return;
		}
		oldSprint.updateSerialId(newId);
		newSprint.updateSerialId(oldId);
	}
	
	public SprintObject getCurrentSprint() {
		return mProject.getCurrentSprint();
	}
	
	public SprintObject getLatestSprint() {
		SprintObject sprint = mProject.getLatestSprint();
		if (sprint == null) {
			long sprintId = SprintObject.getNextSprintId() - 1;
			SerialNumberObject serialNumber = SerialNumberObject.get(mProject.getId());
			long sprintSerialId = serialNumber.getSprintId();
			sprint = new SprintObject(sprintId, sprintSerialId, mProject.getId());
			String todayString = DateUtil.format(new Date(), DateUtil._8DIGIT_DATE_1);
			sprint.setStartDate(todayString).setInterval(2);
		}
		return sprint;
	}
}
