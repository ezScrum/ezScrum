package ntut.csie.ezScrum.web.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.helper.AccountHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AccountHelperTest {
	private CreateProject mCP;
	private CreateAccount mCA;
	private int mProjectCount = 1;
	private Configuration mConfig;
	private AccountHelper mAccountHelper;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增 Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		mCA = new CreateAccount(3);
		mCA.exe();
		
		// create account helper
		mAccountHelper = new AccountHelper(mConfig.getUserSession());
		
		// release
		ini = null;
	}

	@After
	public void tearDown() throws IOException, Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		// release
		ini = null;
		mCP = null;
		mConfig = null;
	}
	
	@Test
	public void testValidateUsername() {
		assertEquals("true", mAccountHelper.validateUsername("password123"));
		assertEquals("false", mAccountHelper.validateUsername("password123*"));
	}
	
	@Test
	public void testGetAssignedProject() {
		AccountObject account = mCA.getAccountList().get(0);
		HashMap<String, ProjectRole> rolesMap = account.getProjectRoleMap();
		String projectXML = mAccountHelper.getAssignedProject(account.getId());
		
		StringBuilder expectStringBuilder = new StringBuilder();
		expectStringBuilder.append("<AssignRoleInfo>")
		                   .append("<AccountInfo>")
		                   .append("<ID>" + account.getId() + "</ID>")
		                   .append("<Account>" + account.getUsername() + "</Account>")
		                   .append("<Name>" + account.getNickName() + "</Name>")
		                   .append("<Roles>");
		
		for (Entry<String, ProjectRole> entry : rolesMap.entrySet()) {
			ScrumRole permission = entry.getValue().getScrumRole();
			ProjectObject project = entry.getValue().getProject();
			String resource = permission.getProjectName();
			String operation = permission.getRoleName();
			expectStringBuilder.append("<Assigned>")
			        .append("<ResourceId>").append(project.getId()).append("</ResourceId>")
			        .append("<Resource>").append(resource).append("</Resource>")
			        .append("<Operation>").append(operation).append("</Operation>")
			        .append("</Assigned>");
		}
		expectStringBuilder.append("</Roles>");
		expectStringBuilder.append("<Unassigned>")
		                   .append("<ResourceId>1</ResourceId>")
		                   .append("<Resource>TEST_PROJECT_1</Resource>")
		                   .append("</Unassigned>")
		                   .append("<Unassigned>")
		                   .append("<ResourceId>0</ResourceId>")
		                   .append("<Resource>system</Resource>")
		                   .append("</Unassigned>")
		                   .append("</AccountInfo></AssignRoleInfo>");
		
		assertEquals(expectStringBuilder.toString(), projectXML);
	}
	
	@Test
	public void testAddAssignedRole() {
		AccountObject account1 = mCA.getAccountList().get(0);
		AccountObject account2 = mCA.getAccountList().get(1);
		AccountObject account3 = mCA.getAccountList().get(2);
		ProjectObject project = mCP.getAllProjects().get(0);
		
		// Assign role
		mAccountHelper.addAssignedRole(account1.getId(), project.getId(), "ProductOwner");
		mAccountHelper.addAssignedRole(account2.getId(), project.getId(), "ScrumMaster");
		mAccountHelper.addAssignedRole(account3.getId(), project.getId(), "ScrumTeam");
		
		// get Role
		HashMap<String, ProjectRole> role1 = account1.getRoles();
		HashMap<String, ProjectRole> role2 = account2.getRoles();
		HashMap<String, ProjectRole> role3 = account3.getRoles();
		
		// assert
		assertEquals("ProductOwner", role1.get(project.getName()).getScrumRole().getRoleName());
		assertEquals("ScrumMaster", role2.get(project.getName()).getScrumRole().getRoleName());
		assertEquals("ScrumTeam", role3.get(project.getName()).getScrumRole().getRoleName());
	}
	
	@Test
	public void testRemoveAssignRole() throws Exception {
		AccountObject account1 = mCA.getAccountList().get(0);
		AccountObject account2 = mCA.getAccountList().get(1);
		AccountObject account3 = mCA.getAccountList().get(2);
		ProjectObject project = mCP.getAllProjects().get(0);
		
		// Assign role
		mAccountHelper.addAssignedRole(account1.getId(), project.getId(), "ProductOwner");
		mAccountHelper.addAssignedRole(account2.getId(), project.getId(), "ScrumMaster");
		mAccountHelper.addAssignedRole(account3.getId(), project.getId(), "ScrumTeam");
		
		// remove assign role
		mAccountHelper.removeAssignRole(account1.getId(), project.getId(), "ProductOwner");
		mAccountHelper.removeAssignRole(account2.getId(), project.getId(), "ScrumMaster");
		mAccountHelper.removeAssignRole(account3.getId(), project.getId(), "ScrumTeam");
		
		// get Role
		HashMap<String, ProjectRole> role1 = account1.getRoles();
		HashMap<String, ProjectRole> role2 = account2.getRoles();
		HashMap<String, ProjectRole> role3 = account3.getRoles();
		
		// assert
		assertNull(role1.get(project.getName()));
		assertNull(role2.get(project.getName()));
		assertNull(role3.get(project.getName()));
	}
	
	@Test
	public void testGetManagementView() {
		AccountObject account1 = mCA.getAccountList().get(0);
		AccountObject account2 = mCA.getAccountList().get(1);
		ProjectObject project = mCP.getAllProjects().get(0);
		
		// Assign role
		mAccountHelper.addAssignedRole(account1.getId(), project.getId(), "admin");
		mAccountHelper.addAssignedRole(account2.getId(), project.getId(), "ScrumMaster");
		
		// assert
		assertEquals("Admin_ManagementView", mAccountHelper.getManagementView(account1));
		assertEquals("User_ManagementView", mAccountHelper.getManagementView(account2));
	}
}
