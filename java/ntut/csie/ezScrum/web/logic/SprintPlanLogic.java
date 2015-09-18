package ntut.csie.ezScrum.web.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
	 * Sort Sprints by StartDate
	 * @param sprints
	 * @return ArrayList<SprintObject>
	 */
	private ArrayList<SprintObject> sortedByStartDate(ArrayList<SprintObject> sprints) {
		Collections.sort(sprints, new Comparator<SprintObject>() {
			@Override
			public int compare(SprintObject o1, SprintObject o2) {
				return DateUtil.dayFilter(o2.getStartDateString()).compareTo(DateUtil.dayFilter(o1.getStartDateString()));
			}
		});
		return sprints;
	}

	/**
	 * Sort Sprints by SprintId in descent
	 * @param sprints
	 * @return ArrayList<SprintObject>
	 */
	private ArrayList<SprintObject> sortedById(ArrayList<SprintObject> sprints) {
		Collections.sort(sprints, new Comparator<SprintObject>() {
			@Override
			public int compare(SprintObject o1, SprintObject o2) {
				return (int) (o2.getId() - o1.getId());
			}
		});
		return sprints;
	}
}
