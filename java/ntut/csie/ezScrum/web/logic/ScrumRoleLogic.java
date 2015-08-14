package ntut.csie.ezScrum.web.logic;

import java.util.HashMap;
import java.util.Map;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.jcis.resource.core.IProject;

public class ScrumRoleLogic {

	private Map<String, ScrumRole> scrumRoles;

	public ScrumRoleLogic() {}
	
	public ScrumRole getScrumRole(IProject project, AccountObject account) {
		synchronized (this) {
			HashMap<String, ProjectRole> roles = account.getRoles();
			ScrumRole scrumRole = null;
			ProjectRole role = roles.get("system");
			if (role != null) {
				scrumRole = role.getScrumRole();
			} else {
				if (roles == null || project == null) return null;
				role = roles.get(project.getName());
				if (role == null) return null;
				scrumRole = role.getScrumRole();
			} 
			return scrumRole;
		}
	}
	
	public ScrumRole getScrumRole(ProjectObject project, AccountObject account) {
		synchronized (this) {
			HashMap<String, ProjectRole> roles = account.getRoles();
			ScrumRole scrumRole = null;
			ProjectRole role = roles.get("system");
			if (role != null) {
				scrumRole = role.getScrumRole();
			} else {
				role = roles.get(project.getName());
				if (role == null) return null;
				scrumRole = role.getScrumRole();
			} 
			return scrumRole;
		}
	}
}
