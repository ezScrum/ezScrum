package ntut.csie.ezScrum.web.mapper;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataInfo.UnplanInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;

public class UnplanItemMapper {
	private ProjectObject mProject;

	public UnplanItemMapper(ProjectObject project) {
		mProject = project;
	}
	
	public ProjectObject getProject() {
		return mProject;
	}
	
	public UnplanObject getUnplan(long unplanId) {
		return UnplanObject.get(unplanId);
	}

	public ArrayList<UnplanObject> getUnplansInSprint(long sprintId) {
		SprintObject sprint = SprintObject.get(sprintId);
		ArrayList<UnplanObject> unplans = new ArrayList<>();
		if (sprint != null) {
			unplans = sprint.getUnplans();
		}
		return unplans;
	}
	
	public ArrayList<UnplanObject> getAllUnplans() {
		ArrayList<UnplanObject> unplans = mProject.getUnplans();
		return unplans;
	}

	public long addUnplan(long projectId, long sprintId, UnplanInfo unplanInfo) {
		UnplanObject unplan = new UnplanObject(sprintId, projectId);
		unplan.setName(unplanInfo.name).setNotes(unplanInfo.notes)
			.setEstimate(unplanInfo.estimate).setStatus(unplanInfo.status)
			.setActual(unplanInfo.actual).setHandlerId(unplanInfo.handlerId)
			.setCreateTime(unplanInfo.specificTime).save();
		
		for (long partnerId : unplanInfo.partnersId) {
			unplan.addPartner(partnerId);
		}
		return unplan.getId();
	}

	public void updateUnplan(long unplanId, UnplanInfo unplanInfo) {
		UnplanObject unplan = UnplanObject.get(unplanId);
		if (unplan != null) {
			unplan.setName(unplanInfo.name).setNotes(unplanInfo.notes)
			.setEstimate(unplanInfo.estimate).setActual(unplanInfo.actual)
			.setHandlerId(unplanInfo.handlerId).setPartnersId(unplanInfo.partnersId)
			.setStatus(unplanInfo.status).setSprintId(unplanInfo.sprintId)
			.setUpdateTime(unplanInfo.specificTime).save();
		}
	}

	public void deleteUnplan(long unplanId) {
		UnplanObject unplan = UnplanObject.get(unplanId);
		if (unplan != null) {
			unplan.delete();
		}
	}
}
