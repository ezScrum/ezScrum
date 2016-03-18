package ntut.csie.ezScrum.web.mapper;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataInfo.ReleaseInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;

public class ReleasePlanMapper {
	private ProjectObject mProject;

	public ReleasePlanMapper(ProjectObject project) {
		mProject = project;
	}	
	
	public long addRelease(ReleaseInfo releaseInfo) {
		ReleaseObject release = new ReleaseObject(mProject.getId());
		release.setName(releaseInfo.name)
		       .setDescription(releaseInfo.description)
		       .setStartDate(releaseInfo.startDate)
		       .setEndDate(releaseInfo.endDate)
		       .save();
		return release.getId();
	}
	
	public ReleaseObject getRelease(long releaseId) {
		return ReleaseObject.get(releaseId);
	}
		
	public ArrayList<ReleaseObject> getReleases() {
		return mProject.getReleases();
	}	
	
	// 修改 release plan
	public void updateRelease(ReleaseInfo releaseInfo) {
		ReleaseObject release = ReleaseObject.get(releaseInfo.id);
		release.setName(releaseInfo.name)
		       .setDescription(releaseInfo.description)
		       .setStartDate(releaseInfo.startDate)
		       .setEndDate(releaseInfo.endDate)
		       .save();
	}
	
	// 刪除 Release
	public void deleteRelease(long releaseId) {
		ReleaseObject release = ReleaseObject.get(releaseId);
		if (release != null) {
			release.delete();
		}
	}
}
