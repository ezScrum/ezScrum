package ntut.csie.ezScrum.web.logic;

import java.util.HashMap;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;

public class ScrumRoleLogic {
	public ScrumRoleLogic() {}
	
	public ScrumRole getScrumRole(ProjectObject project, AccountObject account) {
		synchronized (this) {
			HashMap<String, ProjectRole> roles = account.getRoles();
			ScrumRole scrumRole = null;
			if(account.isAdmin() == true){
				ProjectRole projectRole =  accountIsAdmin(account);
				roles.put("system", projectRole);
			}
			ProjectRole role = roles.get("system");
			if (role != null) {
				scrumRole = role.getScrumRole();}
			 else {
				role = roles.get(project.getName());
				if (role == null) return null;
				scrumRole = role.getScrumRole();
			} 
			return scrumRole;
		}
	}
	
	public ProjectRole accountIsAdmin(AccountObject account){
		ProjectRole projectRole = null;
		ProjectObject project = new ProjectObject(0, "system");
		project.setDisplayName("system")
		       .setComment("system")
			   .setManager("admin")
			   .setAttachFileSize(0)
			   .setCreateTime(0);
		ScrumRole scrumRole = new ScrumRole("system", "admin");
		scrumRole.setisAdmin(true);
		projectRole = new ProjectRole(project, scrumRole);
		return projectRole;
	}
}
