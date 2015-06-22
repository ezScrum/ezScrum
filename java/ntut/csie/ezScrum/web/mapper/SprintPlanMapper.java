package ntut.csie.ezScrum.web.mapper;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataInfo.SprintInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

public class SprintPlanMapper {
	private ProjectObject mProject;
	private long mProjectId;
	
	public SprintPlanMapper(ProjectObject project) {
		mProject = new ProjectMapper().getProject(project.getName());
		mProjectId = mProject.getId();
	}

	/**
	 * Add New SprintPlan
	 */
	public SprintObject addSprint(SprintInfo sprintInfo) {
		SprintObject sprint = new SprintObject(mProjectId);
		sprint.setInterval(sprintInfo.interval)
		        .setMembers(sprintInfo.members)
		        .setHoursCanCommit(sprintInfo.hoursCanCommit)
		        .setFocusFactor(sprintInfo.focusFactor)
		        .setSprintGoal(sprintInfo.sprintGoal)
		        .setStartDate(sprintInfo.startDate)
		        .setDemoDate(sprintInfo.demoDate)
		        .setDemoPlace(sprintInfo.demoPlace)
		        .setDailyInfo(sprintInfo.dailyInfo)
		        .save();
		return getSprint(sprint.getId());
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
		        .setMembers(sprintInfo.members)
		        .setHoursCanCommit(sprintInfo.hoursCanCommit)
		        .setFocusFactor(sprintInfo.focusFactor)
		        .setSprintGoal(sprintInfo.sprintGoal)
		        .setStartDate(sprintInfo.startDate)
		        .setDemoDate(sprintInfo.demoDate)
		        .setDemoPlace(sprintInfo.demoPlace)
		        .setDailyInfo(sprintInfo.dailyInfo)
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
		oldSprint.updateSerialId(newId);
		newSprint.updateSerialId(oldId);
	}
}
