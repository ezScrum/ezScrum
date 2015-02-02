package ntut.csie.ezScrum.web.mapper;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;

public class ScrumRoleMapper {
	
	public ScrumRoleMapper() {}
	
	// ezScrum v1.8
	public void updateScrumRole(long projectId, ScrumRole scrumRole) {
		ProjectObject project = ProjectObject.get(projectId);
		project.updateScrumRole(scrumRole);
	}
	
	// ezScrum v1.8
	public ScrumRole getScrumRole(long projectId, String projectName, String roleName) {
		ProjectObject project = ProjectObject.get(projectId);
		ScrumRole scrumRole = project.getScrumRole(RoleEnum.valueOf(roleName));
		return scrumRole;
	}
}
