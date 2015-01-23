package ntut.csie.ezScrum.mysql;

import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;
import ntut.csie.ezScrum.web.sqlService.MySQLService;

public class ProjectRoleTest extends TestCase {
	private MySQLService mService;
	private Configuration mConfig;
	private ProjectObject mProject;
	private AccountObject mUser;
	private String mUserId;
	
	public ProjectRoleTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		mService = new MySQLService(mConfig);
		mService.openConnect();
		
		/**
		 * set up a project and a user
		 */
		ProjectObject project = new ProjectObject("name");
		project.setDisplayName("name")
			.setComment("comment")
			.setManager("PO_YC")
			.setAttachFileSize(2)
			.save();
		project.reload();
		AccountInfo user = new AccountInfo("account", "user name", "password", "email", "true");
		mService.createAccount(user);
		mUser = mService.getAccount(user.getAccount());
		mUserId = mUser.getId();
	}

	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		mService.closeConnect();
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(mConfig.getDataPath());
		
		mConfig.setTestMode(false);
		mConfig.save();
		
		mService = null;
		mConfig = null;
		super.tearDown(); 
	}
	
	public void testCreateProjectRole() {
		boolean result = mService.createProjectRole(mProject.getId(), mUserId, RoleEnum.ProductOwner);
		
		assertTrue(result);
	}
	
	public void testDeleteProjectRole() {
		mService.createProjectRole(mProject.getId(), mUserId, RoleEnum.ProductOwner);
		boolean result = mService.deleteProjectRole(mProject.getId(), mUserId, RoleEnum.ProductOwner);
		
		assertTrue(result);
	}
	
	public void testGetProjectMemberList() {
		mService.createProjectRole(mProject.getId(), mUserId, RoleEnum.ProductOwner);
		List<AccountObject> userList = mService.getProjectMembers(mProject.getId());
		
		assertEquals(1, userList.size());
	}
	
	public void testGetProjectRoleList() {
		RoleEnum role = RoleEnum.ProductOwner;
		ScrumRole scrumRole = new ScrumRole(role);
		mService.createScrumRole(mProject.getId(), role, scrumRole);
		mService.createProjectRole(mProject.getId(), mUserId, role);
		HashMap<String, ProjectRole> result = mService.getProjectRoleList(mUserId);
		
		assertEquals(1, result.size());
	}
}
