package ntut.csie.ezScrum.SaaS;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import junit.framework.TestCase;
import ntut.csie.ezScrum.SaaS.CreateData;
import ntut.csie.ezScrum.SaaS.PMF;
import ntut.csie.ezScrum.SaaS.database.ProjectDataStore;
import ntut.csie.ezScrum.SaaS.database.ScrumRoleDataStore;
import ntut.csie.ezScrum.SaaS.interfaces.pic.internal.Project;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.mapper.ScrumRoleMapper;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IPermission;
import ntut.csie.jcis.account.core.IRole;
import ntut.csie.jcis.project.core.IProjectDescription;
import ntut.csie.jcis.project.core.internal.ProjectDescription;
import ntut.csie.jcis.resource.core.IProject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;


public class ScrumRoleMapperTest extends TestCase {

	// GAE Mock DB in memory
	private final LocalServiceTestHelper dbTestHelper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	private CreateData cd = new CreateData();
	private List<IProject> projectList;
	private List<IAccount> accountList;

	private String[] scrumRoles = {"ProductOwner", "ScrumMaster", "ScrumTeam", "Stakeholder", "Guest"};
	// default permission for each ScrumRole, follow ScrumRole.xml order
	// [PB, RP, SP, SB, TB, U, R, VR, Edit ]
	private boolean[] permission_PO = { true, true, true, true, true, true, true, true, true };
	private boolean[] permission_SM = { true, true, true, true, true, true, true, true, false };
	private boolean[] permission_ST = { true, true, true, true, true, true, true, true, false };
	private boolean[] permission_S = { false, false, false, false, false, false, false, true, false };
	private boolean[] permission_G = { false, false, false, false, false, false, false, false, false};
			
	public ScrumRoleMapperTest(String method) {
		super(method);
	}

	protected void setUp() throws Exception {
		super.setUp();
		dbTestHelper.setUp();
		
		// 
		this.cd.setupProject(1);
		this.cd.exeCreateProject();		
		// 
		this.cd.setupAccount(1);
		this.cd.exeCreateAccount();
		
		this.projectList = this.cd.getProjectList();
		this.accountList = this.cd.getAccountList();		
	}

	protected void tearDown() throws Exception {
		//
		dbTestHelper.tearDown();
		super.tearDown();
	}
	
	public void testGetPermission() {
		System.out.println(">>>>> testGetPermission");
						
		// check default permission when project created		
		IProject project = this.projectList.get(0);
		String projectId = project.getName();
				
		// (I) 直接從資料庫取出來比對
		Project actualProjectDS = this.getProjectFromDS(projectId);
		List<ScrumRole> scrumRoleList = new ArrayList<ScrumRole>();
		
		for (String roleName : this.scrumRoles) {					
			ScrumRole scrumRole = actualProjectDS.getScrumRole(roleName);		
			scrumRoleList.add(scrumRole);			
		}
		
		comparePermission(scrumRoleList.get(0), this.permission_PO);
		comparePermission(scrumRoleList.get(1), this.permission_SM);
		comparePermission(scrumRoleList.get(2), this.permission_ST);
		comparePermission(scrumRoleList.get(3), this.permission_S);
		comparePermission(scrumRoleList.get(4), this.permission_G);		
		scrumRoleList.clear();
		
		// (II) 測試ScrumRoleMapper.getPermission()之正確性
		ScrumRoleMapper srm = new ScrumRoleMapper();
					
		for (String roleName : this.scrumRoles) {					
			ScrumRole scrumRole = srm.getPermission(projectId, roleName);
			scrumRoleList.add(scrumRole);				
		}		
				
		comparePermission(scrumRoleList.get(0), this.permission_PO);
		comparePermission(scrumRoleList.get(1), this.permission_SM);
		comparePermission(scrumRoleList.get(2), this.permission_ST);
		comparePermission(scrumRoleList.get(3), this.permission_S);
		comparePermission(scrumRoleList.get(4), this.permission_G);		
		
		System.out.println(">>>>> testGetPermission done!");		
	}

	public void testUpdatePermission() {				
		System.out.println(">>>>> testUpdatePermission");
		
		IProject project = this.projectList.get(0);
		
		ScrumRoleMapper srm = new ScrumRoleMapper();
		String projectId = project.getName();

		List<ScrumRole> scrumRoleList = new ArrayList<ScrumRole>();	
		
		// (I) 將PO與Guest互換權限做測試 (all on vs all off)
		ScrumRole role = this.genScrumRole(projectId, scrumRoles[0], this.permission_G);
		srm.update(role);	
		
		role = this.genScrumRole(projectId, scrumRoles[4], this.permission_PO);
		srm.update(role);
				
		for (String roleName : this.scrumRoles) {
			Project actualProjectDS = this.getProjectFromDS(projectId);			
			ScrumRole scrumRole = actualProjectDS.getScrumRole(roleName);		
			scrumRoleList.add(scrumRole);			
		}
		
		comparePermission(scrumRoleList.get(0), this.permission_G);
		comparePermission(scrumRoleList.get(1), this.permission_SM);
		comparePermission(scrumRoleList.get(2), this.permission_ST);
		comparePermission(scrumRoleList.get(3), this.permission_S);
		comparePermission(scrumRoleList.get(4), this.permission_PO);
		scrumRoleList.clear();
		
		// (II) 將SM & ST與S互換權限做測試
		role = this.genScrumRole(projectId, scrumRoles[1], this.permission_S);		
		srm.update(role);	
		
		role = this.genScrumRole(projectId, scrumRoles[2], this.permission_S);
		srm.update(role);		

		role = this.genScrumRole(projectId, scrumRoles[3], this.permission_SM);
		srm.update(role);			
		
		for (String roleName : this.scrumRoles) {
			Project actualProjectDS = this.getProjectFromDS(projectId);			
			ScrumRole scrumRole = actualProjectDS.getScrumRole(roleName);		
			scrumRoleList.add(scrumRole);			
		}
		
		comparePermission(scrumRoleList.get(0), this.permission_G);
		comparePermission(scrumRoleList.get(1), this.permission_S);
		comparePermission(scrumRoleList.get(2), this.permission_S);
		comparePermission(scrumRoleList.get(3), this.permission_SM);
		comparePermission(scrumRoleList.get(4), this.permission_PO);
		scrumRoleList.clear();		
		
		System.out.println(">>>>> testUpdatePermission done!");		
	}		
	
	/*
	 * local use
	 */
	private void comparePermission(ScrumRole scrumrole, boolean[] permission) {		
		System.out.println("compare permission of scrumrole = " + scrumrole.getRoleName());
		
		assertEquals(scrumrole.getAccessProductBacklog(), permission[0]);
		assertEquals(scrumrole.getAccessReleasePlan(), permission[1]);
		assertEquals(scrumrole.getAccessSprintPlan(), permission[2]);
		assertEquals(scrumrole.getAccessSprintBacklog(), permission[3]);
		assertEquals(scrumrole.getAccessTaskBoard(), permission[4]);
		assertEquals(scrumrole.getAccessUnplannedItem(), permission[5]);	
		assertEquals(scrumrole.getAccessRetrospective(), permission[6]);
		assertEquals(scrumrole.getReadReport(), permission[7]);
		assertEquals(scrumrole.getEditProject(), permission[8]);
		
		System.out.println("scrumrole comparasion ok!");
		
	}
	
	// 直接從資料庫取出
	private Project getProjectFromDS(String projectId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Project project = null; // fix later

		try {
			// 以 projectID 為 key 來取出相對應的 Project data
			Key key = KeyFactory.createKey(
					ProjectDataStore.class.getSimpleName(), projectId);
			ProjectDataStore p = pm.getObjectById(ProjectDataStore.class, key);

			// Project data 從 DB 轉存到 Memory
			project = new Project(p.getName());

			project.setDisplayName(p.getDisplayName());
			project.setComment(p.getComment());
			project.setManager(p.getManager());
			project.setCreateDate(p.getCreateDate());

			// follow ori.
			IProjectDescription aProjDesc = new ProjectDescription(projectId);
			
			aProjDesc.setName(projectId);	// ID
			aProjDesc.setDisplayName(project.getDisplayName());
			aProjDesc.setComment(project.getComment());		
			aProjDesc.setProjectManager(project.getProjectManager());
			aProjDesc.setCreateDate(project.getCreateDate());
			
			project.setProjectDesc(aProjDesc);
			
			// 有關 Scrum Role 權限設定的資料
			List<ScrumRoleDataStore> scrumRolesDS = p.getScrumRoles();
			System.out.println("DB get ScrumRolesDS.size = " + scrumRolesDS.size());
			
			for (int i = 0; i < scrumRolesDS.size(); i++) {				
				ScrumRoleDataStore srDS = scrumRolesDS.get(i);
				ScrumRole sr = new ScrumRole(projectId, srDS.getRoleName());

				// follow ScrumRole.xml order
				sr.setAccessProductBacklog(srDS.getAccessProductBacklog());
				sr.setAccessReleasePlan(srDS.getAccessReleasePlan());
				sr.setAccessSprintPlan(srDS.getAccessSprintPlan());
				sr.setAccessSprintBacklog(srDS.getAccessSprintBacklog());
				sr.setAccessTaskBoard(srDS.getAccessTaskBoard());
				sr.setAccessUnplannedItem(srDS.getAccessUnplannedItem());				
				sr.setAccessRetrospective(srDS.getAccessRetrospective());
				sr.setReadReport(srDS.getReadReport());
				sr.setEditProject(srDS.getEditProject());

				project.setScrumRole(srDS.getRoleName(), sr);
			}

		} finally {
			pm.close();
		}
		
		return project;			

	}		
	
	private ScrumRole genScrumRole(String projectId, String roleName, boolean[] permission) {		
		ScrumRole sr = new ScrumRole(projectId, roleName);
		
		sr.setAccessProductBacklog(permission[0]);
		sr.setAccessReleasePlan(permission[1]);
		sr.setAccessSprintPlan(permission[2]);
		sr.setAccessSprintBacklog(permission[3]);
		sr.setAccessTaskBoard(permission[4]);
		sr.setAccessUnplannedItem(permission[5]);				
		sr.setAccessRetrospective(permission[6]);
		sr.setReadReport(permission[7]);
		sr.setEditProject(permission[8]);
		
		return sr;
	}
	
}
