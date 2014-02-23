package ntut.csie.ezScrum.web.dataObject;

import ntut.csie.ezScrum.pic.core.ScrumRole;

public class ProjectRole {
	private ProjectInformation mProject;
	private ScrumRole mScrumRole;
	
	public ProjectRole(ProjectInformation project, ScrumRole scrumRole) {
		setProject(project);
		setScrumRole(scrumRole);
	}

	public ProjectInformation getProject() {
	    return mProject;
    }

	public void setProject(ProjectInformation mProject) {
	    this.mProject = mProject;
    }
	public ScrumRole getScrumRole() {
	    return mScrumRole;
    }

	public void setScrumRole(ScrumRole mScrumRole) {
	    this.mScrumRole = mScrumRole;
    }
}
