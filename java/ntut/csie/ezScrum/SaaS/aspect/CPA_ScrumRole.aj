package ntut.csie.ezScrum.SaaS.aspect;

import java.util.List;

import javax.jdo.PersistenceManager;

import ntut.csie.ezScrum.SaaS.PMF;
import ntut.csie.ezScrum.SaaS.database.ProjectDataStore;
import ntut.csie.ezScrum.SaaS.database.ScrumRoleDataStore;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.mapper.ScrumRoleMapper;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;


// 有關資料庫物件存取的程式碼 (DataStore)必須做 data nucleus enhance 的動作
public aspect CPA_ScrumRole {

	/*
	 * ScrumRoleMapper (original from ScrumRoleManager in ezScrum_Model)
	 */	

	// replace: public void update(ScrumRole role)
	pointcut updatePC(ScrumRole role) 
	: execution(void ScrumRoleMapper.update(ScrumRole)) && args(role);

	void around(ScrumRole role)
	: updatePC(role) {
		System.out.println("replaced by AOP...updatePC: " + thisJoinPoint);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		String projectId = role.getProjectName();

		try {
			Key key = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), projectId);
			ProjectDataStore p = pm.getObjectById(ProjectDataStore.class, key);
			
			// 取出專案的ScrumRole設定
			List<ScrumRoleDataStore> scrumRolesDS = p.getScrumRoles();
						
			for (int i=0; i < scrumRolesDS.size(); i++) {									
				ScrumRoleDataStore srDS = scrumRolesDS.get(i);
								
				if (srDS.getRoleName().equals(role.getRoleName())) {									
					srDS.setAccessProductBacklog(role.getAccessProductBacklog());
					srDS.setAccessReleasePlan(role.getAccessReleasePlan());
					srDS.setAccessSprintBacklog(role.getAccessSprintBacklog());
					srDS.setAccessSprintPlan(role.getAccessSprintPlan());
					srDS.setAccessTaskBoard(role.getAccessTaskBoard());
					srDS.setAccessUnplannedItem(role.getAccessUnplannedItem());
					srDS.setAccessRetrospective(role.getAccessRetrospective());				
					srDS.setReadReport(role.getReadReport());
					srDS.setEditProject(role.getEditProject());
					
					pm.makePersistent(srDS);	// save ScrumRole
					break;
				}
			}
			pm.makePersistent(p);	// save Project with updated ScrumRole
		} finally {
			pm.close();
		}		
	}
	
	// replace: public ScrumRole getPermission(String resource, String roleName)
	pointcut getPermissionPC(String resource, String roleName) 
	: execution(ScrumRole ScrumRoleMapper.getPermission(String, String)) && args(resource, roleName);

	ScrumRole around(String resource, String roleName)
	: getPermissionPC(resource, roleName) {
		System.out.println("replaced by AOP...getPermissionPC: " + thisJoinPoint);
		
		ScrumRole sr = new ScrumRole(resource, roleName);		
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			Key key = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), resource);
			ProjectDataStore p = pm.getObjectById(ProjectDataStore.class, key);
			
			// 取出專案的 Scrum Role 權限設定資料
			List<ScrumRoleDataStore> scrumRolesDS = p.getScrumRoles();
			
			for (int i = 0; i < scrumRolesDS.size(); i++) {
				ScrumRoleDataStore srDS = scrumRolesDS.get(i);

				if (srDS.getRoleName().equals(roleName)) {			
					sr.setEditProject(srDS.getEditProject());
					//
					sr.setAccessProductBacklog(srDS.getAccessProductBacklog());
					sr.setAccessReleasePlan(srDS.getAccessReleasePlan());
					sr.setAccessSprintBacklog(srDS.getAccessSprintBacklog());
					sr.setAccessSprintPlan(srDS.getAccessSprintPlan());
					sr.setAccessTaskBoard(srDS.getAccessTaskBoard());
					sr.setAccessRetrospective(srDS.getAccessRetrospective());
					// 少用到的功能
					sr.setAccessUnplannedItem(srDS.getAccessUnplannedItem());
					sr.setReadReport(srDS.getReadReport());
				}
			}

		} finally {
			pm.close();
		}		

		return sr;
	}
	
}