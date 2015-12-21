package ntut.csie.ezScrum.web.mapper;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataInfo.RetrospectiveInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;

public class RetrospectiveMapper {
	private ProjectObject mProject;
	
	public RetrospectiveMapper(ProjectObject project) {
		mProject = project;
	}
	
	public ProjectObject getProjct() {
		return mProject;
	}
	
	public long addRetrospective(RetrospectiveInfo retrospectiveInfo) {
		RetrospectiveObject retrospective = new RetrospectiveObject(mProject.getId());
		retrospective.setName(retrospectiveInfo.name)
					 .setDescription(retrospectiveInfo.description)
					 .setType(retrospectiveInfo.type)
					 .setStatus(retrospectiveInfo.status)
					 .setSprintId(retrospectiveInfo.sprintId)
					 .save();
		return retrospective.getId();
	}
	
	public RetrospectiveObject getRetrospective(long retrospectiveId) {
		return RetrospectiveObject.get(retrospectiveId);
	}	
	
	public ArrayList<RetrospectiveObject> getAllGoods() {
		return mProject.getGoods();
	}
	
	public ArrayList<RetrospectiveObject> getAllImprovements() {
		return mProject.getImprovements();
	}
	
	public ArrayList<RetrospectiveObject> getGoodsInSprint(long sprintId) {
		SprintObject sprint = SprintObject.get(sprintId);
		if (sprint == null) {
			return new ArrayList<RetrospectiveObject>();
		} else {
			return sprint.getGoods();
		}
	}

	public ArrayList<RetrospectiveObject> getImprovementsInSprint(long sprintId) {
		SprintObject sprint = SprintObject.get(sprintId);
		if (sprint == null) {
			return new ArrayList<RetrospectiveObject>();
		} else {
			return sprint.getImprovements();
		}
	}
	
	public void updateRetrospective(RetrospectiveInfo retrospectiveInfo) {
		RetrospectiveObject retrospective = RetrospectiveObject.get(retrospectiveInfo.id);
		retrospective.setName(retrospectiveInfo.name)
					 .setDescription(retrospectiveInfo.description)
					 .setType(retrospectiveInfo.type)
					 .setStatus(retrospectiveInfo.status)
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
