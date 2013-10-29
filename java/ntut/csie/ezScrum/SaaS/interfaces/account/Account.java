package ntut.csie.ezScrum.SaaS.interfaces.account;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ntut.csie.ezScrum.SaaS.util.ScrumEnum;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IPermission;
import ntut.csie.jcis.account.core.IRole;
import ntut.csie.jcis.account.core.internal.Actor;
import ntut.csie.jcis.account.core.internal.Permission;

public class Account implements IAccount {
	private static final long serialVersionUID = -734806544424933436L;
	private String m_id = "";
	private String m_name = "";
	private boolean m_isGuest = false;
	private String m_password = "";
	private String m_email = "";
	private String m_enable = "";	// fix later
	
	private List<IPermission> m_permissions = new ArrayList<IPermission>();
	
	public Account(String id, String name, String password, boolean isGuest) {
		m_id = id;
		m_name = name; 
		m_password = password;
		m_isGuest = isGuest;
	}

	public String getPassword() {
		return m_password;
	}
	
	public void setPassword(String password) {
		m_password = password;
	}

	public boolean isGuest() {
		return m_isGuest;
	}

	public String getEmail() {
		return m_email;
	}

	public void setEmail(String email) {
		m_email = email;
	}
	
    // fix later
    public void setEnable(String enable){
    	m_enable = enable;
    }
    
    // fix later
    public String getEnable() {
    	return m_enable;
    }
    
	public String getID() {
		return m_id;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
	}

	public boolean checkPermission(String permName) {
		
//		System.out.println("AOP SaaS account checkPermission with String");
//		System.out.println("  m_id = " + m_id + "; m_name = " + m_name);
		
//		if (m_id.equals("admin"))
//			return true;
		
		for (int i=0; i<m_permissions.size(); i++) {
			IPermission p = m_permissions.get(i);
			if (p.getPermissionName().equals(permName)) {
				return true;
			}
		}
		return false;
	}

	public void addPermission(IPermission permission) {
//		System.out.println("AOP SaaS account addPermission");
		
		m_permissions.add(permission);
	}

	public void removePermission(String permName) {
		System.out.println("AOP SaaS account removePermission");
		
		for (int i=0; i<m_permissions.size(); i++) {
			IPermission p = m_permissions.get(i);
			if (p.getPermissionName().equals(permName)) {
				m_permissions.remove(i);
				break;
			}
		}
	}

	public List<IPermission> getPermissionList() {
		return m_permissions;
	}

	public IPermission getPermission(String projectName) {
		for (int i=0; i<m_permissions.size(); i++) {
			IPermission p = m_permissions.get(i);
			if (p.getPermissionName().equals(ScrumEnum.ADMINISTRATOR_PERMISSION))
				return new Permission(projectName+"_"+ScrumEnum.SCRUMROLE_ADMINISTRATOR, projectName, ScrumEnum.SCRUMROLE_ADMINISTRATOR);
			else if (p.getResourceName().equals(projectName))
				return p;
		} 
		return new Permission(projectName+"_"+ScrumEnum.SCRUMROLE_GUEST, projectName, ScrumEnum.SCRUMROLE_GUEST);
	}

	@Override
	public boolean isInRole(IRole role) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean checkPermission(IPermission permission) {
//		System.out.println("AOP SaaS account checkPermission with IPermission");
		
//		System.out.println("  m_id = " + m_id + "; m_name = " + m_name);
		
		if (m_id.equals("admin"))
			return true;
		
		if (permission == null)	{
			System.out.println("input permission null !!");
		} 
		
		// res = Project, op = ScrumRole
		List<IPermission> permList = this.getPermissionList();
		for (int i=0; i< permList.size(); i++)
		{
			IPermission ownedPermission = permList.get(i);
			String project = ownedPermission.getResourceName();
			String scrumRole = ownedPermission.getOperation();
			
//			System.out.println(" owned permList[" + i +  "], res = " + project + ", op = " + scrumRole);
			
			if (project.equals(permission.getResourceName()) && scrumRole.equals(permission.getOperation()))
				return true;
		}
		
		/*
		 * no RBAC concept 
		 */
//		Iterator<IRole> ir = m_RoleSet.iterator();
//
//		int i = 0;
//		while (ir.hasNext()) {
//			i++;
//			System.out.println("role has next! " + i);
//			IRole role = (IRole) ir.next();
//
//			if (role.checkPermission(permission)) {
//				return true;
//			}
//		}

		return false;
	}

	@Override
	public int getType() {
		return 0;
	}

	// fix later...
	private Set<IRole> m_RoleSet = new HashSet<IRole>();
	
	public IRole[] getRoles() {
		IRole[] roles = (IRole[]) m_RoleSet.toArray(new IRole[0]);

		return roles;
	}

	public void addRole(IRole role) {
		m_RoleSet.add(role);
	}

	public void removeRole(IRole role) {
		m_RoleSet.remove(role);
	}

	@Override
	public boolean isPersistent() {
		return false;
	}

}
