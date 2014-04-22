package ntut.csie.ezScrum.web.dataObject;

import ntut.csie.ezScrum.pic.core.ScrumRole;

public class ProjectRole {
	private ProjectObject mProject;
	private ScrumRole mScrumRole;
	
	public ProjectRole(ProjectObject project, ScrumRole scrumRole) {
		setProject(project);
		setScrumRole(scrumRole);
	}

	public ProjectObject getProject() {
	    return mProject;
    }

	public void setProject(ProjectObject mProject) {
	    this.mProject = mProject;
    }
	
	public ScrumRole getScrumRole() {
	    return mScrumRole;
    }

	public void setScrumRole(ScrumRole mScrumRole) {
	    this.mScrumRole = mScrumRole;
    }
}
