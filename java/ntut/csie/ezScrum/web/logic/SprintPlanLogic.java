package ntut.csie.ezScrum.web.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.jcis.core.util.DateUtil;

public class SprintPlanLogic {
	private SprintPlanMapper mSprintPlanMapper;

	public SprintPlanLogic(ProjectObject project) {
		mSprintPlanMapper = new SprintPlanMapper(project);
	}

	/**
	 * @return sprint array, sort descent by ID
	 */
	public ArrayList<SprintObject> getSprintsSortedById() {
		ArrayList<SprintObject> sprints = mSprintPlanMapper.getSprints();
		sprints = sortedById(sprints);
		return sprints;
	}
	
	/**
	 * @return sprint list, sort by StartDate
	 */
	public ArrayList<SprintObject> getSprintsSortedByStartDate() {
		ArrayList<SprintObject> sprints = mSprintPlanMapper.getSprints();
		sprints = sortedByStartDate(sprints);
		return sprints;
	}
	
	/**
	 * Load current plan, when no current sprint exist load the last sprint instead.
	 */
	public SprintObject loadCurrentSprint() {
		// get current sprint
		SprintObject sprint = getCurrentSprint();
		// if no current sprint, get latest sprint instead.
		if (sprint == null) {
			sprint = getLatestSprint();
		}
		return sprint;
	}
	
	public SprintObject getLatestSprint() {
		ArrayList<SprintObject> sprints = getSprintsSortedByStartDate();
		if (sprints.size() == 0) {
			return null;
		} else {
			return sprints.get(0);
		}
	}
	
	public SprintObject getCurrentSprint() {
		Date current = new Date();
		ArrayList<SprintObject> sprints = getSprintsSortedById();
		for (SprintObject sprint : sprints) {
			if (sprint.contains(current)) {
				return sprint;
			}
		}
		return null;
	}
	
	/**
	 * Sort Sprints by StartDate
	 * @param list
	 * @return ArrayList<SprintObject>
	 */
	private ArrayList<SprintObject> sortedByStartDate(ArrayList<SprintObject> list) {
		Collections.sort(list, new Comparator<SprintObject>() {
			@Override
			public int compare(SprintObject o1, SprintObject o2) {
				return DateUtil.dayFilter(o2.getStartDate()).compareTo(DateUtil.dayFilter(o1.getStartDate()));
			}
		});
		return list;
	}

	/**
	 * Sort Sprints by SprintId in descent
	 * @param list
	 * @return ArrayList<SprintObject>
	 */
	private ArrayList<SprintObject> sortedById(ArrayList<SprintObject> list) {
		Collections.sort(list, new Comparator<SprintObject>() {
			@Override
			public int compare(SprintObject o1, SprintObject o2) {
				return (int) (o2.getId() - o1.getId());
			}
		});
		return list;
	}
}
