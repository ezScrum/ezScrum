package ntut.csie.ezScrum.web.helper;

import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.web.dataInfo.SprintInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.logic.SprintPlanLogic;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.jcis.core.util.DateUtil;

public class SprintPlanHelper {
	private SprintPlanMapper mSprintPlanMapper;
	private SprintPlanLogic mSprintPlanLogic;
	
	public SprintPlanHelper(ProjectObject project) {
		mSprintPlanMapper = new SprintPlanMapper(project);
		mSprintPlanLogic = new SprintPlanLogic(project);
	}

	public ArrayList<SprintObject> loadSprints() {
		return mSprintPlanLogic.getSprintsSortedByStartDate();
	}

	public SprintObject getCurrentSprint() {
		return mSprintPlanLogic.getCurrentSprint();
	}

	// load the last plan, so perhaps the return is not the current plan.
	public SprintObject loadCurrentSprint() {
		return mSprintPlanLogic.loadCurrentSprint();
	}

	// get next demoDate
	public String getNextDemoDate() {
		ArrayList<SprintObject> sprints = mSprintPlanLogic.getSprintsSortedById();
		if (sprints.size() == 0){
			return null;
		}
		if (getCurrentSprint() != null) {
			SprintObject sprint = getCurrentSprint();
			return sprint.getDemoDate();
		}
		Date currentDate = new Date();
		
		// 取得下一個Sprint的 Demo Date
		String demoDate = null;
		for (SprintObject sprint : sprints) {
			String sprintDemoDate = sprint.getDemoDate();
			if (DateUtil.dayFilter(sprintDemoDate).getTime() > currentDate.getTime()) {
				if (demoDate == null) {
					demoDate = sprintDemoDate;
				} else if (DateUtil.dayFilter(demoDate).getTime() > DateUtil.dayFilter(sprintDemoDate).getTime()) {
					demoDate = sprintDemoDate;
				}
			}
		}
		return demoDate;
	}

	/**
	 * 只取得一筆 sprint
	 * @param isLatestSprint
	 * @param sprintId
	 * @return
	 */
	public SprintObject getOneSprintInformation(boolean isLatestSprint, long sprintId) {
		if (isLatestSprint) {
			return getLastestSprint();
		} else if (sprintId > 0) {
			return getSprint(sprintId);
		}
		return null;
	}

	public Date getProjectStartDate() {
		ArrayList<SprintObject> sprints = loadSprints();
		return DateUtil.dayFilter(sprints.get(sprints.size() - 1).getStartDate());
	}

	public Date getProjectEndDate() {
		ArrayList<SprintObject> sprints = loadSprints();

		if (sprints.size() > 0) {
			return DateUtil.dayFilter(sprints.get(0).getDemoDate());
		} else {
			return null;
		}
	}

	public SprintObject getLastestSprint() {
		return mSprintPlanLogic.getLatestSprint();
	}

	public SprintObject getSprintByDate(Date date) {
		ArrayList<SprintObject> sprints = mSprintPlanLogic.getSprintsSortedByStartDate();

		for (SprintObject sprint : sprints) {
			if (DateUtil.dayFilter(sprint.getStartDate()).compareTo(date) <= 0
			        && DateUtil.dayFilter(sprint.getDemoDate()).compareTo(date) >= 0) {
				return sprint;
			}
		}
		return null;
	}

	public void createSprint(SprintInfo sprintInfo) {
		mSprintPlanMapper.addSprint(sprintInfo);
	}

	public ArrayList<SprintObject> getAllSprints() {
		return mSprintPlanMapper.getSprints();
	}

	public SprintObject getSprint(long sprintId) {
		return mSprintPlanMapper.getSprint(sprintId);
	}

	public SprintObject updateSprint(long sprintId, SprintInfo sprintInfo) {
		mSprintPlanMapper.updateSprint(sprintInfo);
		return mSprintPlanMapper.getSprint(sprintId);
	}

	public void deleteSprint(long sprintId) {
		mSprintPlanMapper.deleteSprint(sprintId);
	}
	
	/**
	 * move the specific sprint to other sprint
	 * @param oldId
	 * @param newId
	 */
	public void moveSprint(int oldId, int newId) {
		mSprintPlanMapper.moveSprint(oldId, newId);
	}
}
