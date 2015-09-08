package ntut.csie.ezScrum.web.mapper;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataInfo.RetrospectiveInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;

public class RetrospectiveMapper {
	private ProjectObject mProject;
	private SprintObject mSprint;
	
	public RetrospectiveMapper(ProjectObject project) {
		mProject = project;
		mSprint = mProject.getCurrentSprint();
	}
	
	public RetrospectiveMapper(ProjectObject project, long sprintId) {
		mProject = project;
		mSprint = SprintObject.get(sprintId);
		if (mSprint == null) {
			throw new RuntimeException("Sprint#" + sprintId + " is not existed.");
		}
	}
	
	public long addRetrospective(RetrospectiveInfo retrospectiveInfo) {
		long sprintId = -1;
		if (mSprint != null) {
			sprintId = mSprint.getId();
		}
		RetrospectiveObject retrospective = new RetrospectiveObject(mSprint.getProjectId());
		retrospective.setName(retrospectiveInfo.name)
					 .setDescription(retrospectiveInfo.description)
					 .setType(retrospectiveInfo.typeString)
					 .setStatus(retrospectiveInfo.statusString)
					 .setSprintId(sprintId)
					 .save();
		return retrospective.getId();
	}
	
	public RetrospectiveObject getRetrospective(long retrospectiveId) {
		return RetrospectiveObject.get(retrospectiveId);
	}	

	public ArrayList<RetrospectiveObject> getRetrospectivesByType(String type) {
		if (mSprint == null) {
			return new ArrayList<RetrospectiveObject>();
		}
		return mSprint.getRetrospectiveByType(type);
	}
	
	public void updateRetrospective(RetrospectiveInfo retrospectiveInfo) {
		RetrospectiveObject retrospective = RetrospectiveObject.get(retrospectiveInfo.id);
		retrospective.setName(retrospectiveInfo.name)
					 .setDescription(retrospectiveInfo.description)
					 .setType(retrospectiveInfo.typeString)
					 .setStatus(retrospectiveInfo.statusString)
					 .setSprintId(retrospectiveInfo.sprintId)
					 .save();
	}
	
	public void deleteRetrospective(long retrospectiveId) {
		RetrospectiveObject retrospective = RetrospectiveObject.get(retrospectiveId);
		if (retrospective != null) {
			retrospective.delete();
		}
	}
}
