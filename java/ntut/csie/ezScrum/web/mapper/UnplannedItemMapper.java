package ntut.csie.ezScrum.web.mapper;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataInfo.UnplannedInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.UnplannedObject;

public class UnplannedItemMapper {
	
	private ProjectObject mProject;

	public UnplannedItemMapper(ProjectObject project) {
		mProject = project;
	}
	
	public UnplannedObject getUnplanned(long unplannedId) {
		return UnplannedObject.get(unplannedId);
	}

	public ArrayList<UnplannedObject> getUnplannedsInSprint(long sprintId) {
		SprintObject sprint = SprintObject.get(sprintId);
		ArrayList<UnplannedObject> unplanneds = sprint.getUnplanneds();
		return unplanneds;
	}
	
	public ArrayList<UnplannedObject> getAllUnplanneds() {
		ArrayList<UnplannedObject> unplanneds = mProject.getUnplanneds();
		return unplanneds;
	}

	public long addUnplanned(long projectId, long sprintId, UnplannedInfo unplannedInfo) {
		UnplannedObject unplanned = new UnplannedObject(sprintId, projectId);
		unplanned.setName(unplannedInfo.name).setNotes(unplannedInfo.notes)
			.setEstimate(unplannedInfo.estimate).setStatus(unplannedInfo.status)
			.setActual(unplannedInfo.actual).setHandlerId(unplannedInfo.handlerId)
			.setCreateTime(unplannedInfo.specificTime).save();
		
		for (long partnerId : unplannedInfo.partnersId) {
			unplanned.addPartner(partnerId);
		}
		return unplanned.getId();
	}

	public void updateUnplanned(long unplannedId, UnplannedInfo unplannedInfo) {
		UnplannedObject unplanned = UnplannedObject.get(unplannedId);
		if (unplanned != null) {
			unplanned.setName(unplannedInfo.name).setNotes(unplannedInfo.notes)
			.setEstimate(unplannedInfo.estimate).setActual(unplannedInfo.actual)
			.setHandlerId(unplannedInfo.handlerId).setPartnersId(unplannedInfo.partnersId)
			.setStatus(unplannedInfo.status).setSprintId(unplannedInfo.sprintId)
			.setUpdateTime(unplannedInfo.specificTime).save();
		}
	}

	public void deleteUnplanned(long unplannedId) {
		UnplannedObject unplanned = UnplannedObject.get(unplannedId);
		if (unplanned != null) {
			unplanned.delete();
		}
	}
}
