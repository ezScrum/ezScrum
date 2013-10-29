package ntut.csie.ezScrum.web.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.mapper.ScrumRoleMapper;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IPermission;
import ntut.csie.jcis.account.core.IRole;
import ntut.csie.jcis.resource.core.IProject;

public class ScrumRoleLogic {

	private Map<String, ScrumRole> scrumRoles;

	public ScrumRoleLogic() {}

	public ScrumRole getScrumRole(IProject project, IAccount account) {
		synchronized (this) {
			scrumRoles = this.getScrumRoles(account);
			ScrumRole sr = scrumRoles.get(project.getName());
			return sr;
		}
	}

	// 透過account 得到相對專案的權限
	public Map<String, ScrumRole> getScrumRoles(IAccount account) {
		synchronized (this) {
			this.setScrumRoles(account);
			return scrumRoles;
		}
	}

	public void setScrumRoles(IAccount account) {
		scrumRoles = new HashMap<String, ScrumRole>();
		IPermission permAdmin = (new AccountMapper()).getPermission("system_admin");

		// 如果帳號為admin的角色，則給他所有的權限
		if (account.checkPermission(permAdmin)) {
			synchronized (this) {
				List<IProject> projects = (new ProjectMapper()).getAllProjectList();
				for (IProject project : projects) {
					ScrumRole s_role = new ScrumRole(project.getName(), ScrumEnum.SCRUMROLE_ADMINISTRATOR);
					s_role.setAccessProductBacklog(Boolean.TRUE);
					s_role.setAccessReleasePlan(Boolean.TRUE);
					s_role.setAccessSprintPlan(Boolean.TRUE);
					s_role.setAccessSprintBacklog(Boolean.TRUE);
					s_role.setAccessTaskBoard(Boolean.TRUE);
					s_role.setAccessUnplannedItem(Boolean.TRUE);
					s_role.setAccessRetrospective(Boolean.TRUE);
					s_role.setReadReport(Boolean.TRUE);
					s_role.setEditProject(Boolean.TRUE);		// edit project set
					s_role.setisAdmin(Boolean.TRUE);			// admin set
					scrumRoles.put(project.getName(), s_role);
				}
			}
		} else {
			// 不是admin的角色, 只是一般的使用者
			IRole[] roles = account.getRoles();
			for (IRole role : roles) {
				IPermission[] permissions = role.getPermisions();
				for (IPermission per : permissions) {// get permission from account
					String resource = per.getResourceName();
					String permission = per.getOperation();
					ScrumRole s_role = null;

					// check system actor
					if (!resource.equals("system")) {
						s_role = (new ScrumRoleMapper()).getPermission(resource, permission);
					}

					// check guest actor
					if (permission.equalsIgnoreCase("Guest")) {
						s_role.setisGuest(true);
					}

					// add to map
					if (s_role != null) {
						scrumRoles.put(resource, s_role);
					}
				}
			}
		}
	}
}
