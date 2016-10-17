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

	public ArrayList<SprintObject> getSprints() {
		return mSprintPlanLogic.getSprintsSortedByStartDate();
	}

	public SprintObject getCurrentSprint() {
		return mSprintPlanMapper.getCurrentSprint();
	}

	public SprintObject getLatestSprint() {
		return mSprintPlanMapper.getLatestSprint();
	}

	// get next demoDate
	public String getNextDemoDate() {
		ArrayList<SprintObject> sprints = mSprintPlanLogic
				.getSprintsSortedById();
		if (sprints.isEmpty()) {
			return null;
		}
		Date currentDate = new Date();
		long now = currentDate.getTime();

		// 取得下一個Sprint的 Demo Date
		String demoDate = null;
		for (SprintObject sprint : sprints) {
			String sprintDemoDate = sprint.getDemoDateString();
			long timeOfSprintDemoDate = DateUtil.dayFilter(sprintDemoDate)
					.getTime();
			if (timeOfSprintDemoDate > now) {
				demoDate = sprintDemoDate;
			}
		}
		return demoDate;
	}

	/**
	 * 只取得一筆 sprint
	 * 
	 * @param isLatestSprint
	 * @param serialSprintId
	 * @return
	 */
	public SprintObject getOneSprintInformation(boolean isLatestSprint,
			long serialSprintId) {
		if (isLatestSprint) {
			return getLatestSprint();
		} else if (serialSprintId > 0) {
			return getSprint(serialSprintId);
		}
		return null;
	}

	public Date getProjectStartDate() {
		ArrayList<SprintObject> sprints = getSprints();
		return DateUtil.dayFilter(sprints.get(sprints.size() - 1)
				.getStartDateString());
	}

	public Date getProjectEndDate() {
		ArrayList<SprintObject> sprints = getSprints();

		if (!sprints.isEmpty()) {
			return DateUtil.dayFilter(sprints.get(0).getDemoDateString());
		} else {
			return null;
		}
	}

	public SprintObject getSprintByDate(Date date) {
		ArrayList<SprintObject> sprints = mSprintPlanLogic
				.getSprintsSortedByStartDate();

		for (SprintObject sprint : sprints) {
			if (DateUtil.dayFilter(sprint.getStartDateString()).compareTo(date) <= 0
					&& DateUtil.dayFilter(sprint.getDemoDateString())
							.compareTo(date) >= 0) {
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

	public SprintObject getSprint(long serialSprintId) {
		return mSprintPlanMapper.getSprint(serialSprintId);
	}

	public SprintObject updateSprint(SprintInfo sprintInfo) {
		mSprintPlanMapper.updateSprint(sprintInfo);
		return mSprintPlanMapper.getSprint(sprintInfo.serialId);
	}

	public void deleteSprint(long serialSprintId) {
		mSprintPlanMapper.deleteSprint(serialSprintId);
	}

	/**
	 * move the specific sprint to other sprint
	 * 
	 * @param oldId
	 * @param newId
	 */
	public void moveSprint(int oldId, int newId) {
		mSprintPlanMapper.moveSprint(oldId, newId);
	}
	
	public StringBuilder checkSprintDateOverlapping(long sprintId, String startDateString,
			String endDateString, String action){
		ArrayList<SprintObject> sprints = mSprintPlanMapper.getSprints();
		Date startDate = DateUtil.dayFilter(startDateString);
		Date endDate = DateUtil.dayFilter(endDateString);
		String result = "legal";
		for(SprintObject sprint : sprints){
			if (action.equals("edit") && (sprintId == sprint.getId())) {// 不與自己比較
				continue;
			}
			// check 日期的頭尾是否有在各個 sprint 日期範圍內
						if (sprint.contains(startDate) || sprint.contains(endDate)) {
							result = "illegal";
							break;
						}
		}
		
		return new StringBuilder(result);
	}
}
