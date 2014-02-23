package ntut.csie.ezScrum.mysql;

import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.ITSPrefsStorage;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.dataObject.ProjectInformation;
import ntut.csie.ezScrum.web.dataObject.RoleEnum;
import ntut.csie.ezScrum.web.dataObject.UserInformation;
import ntut.csie.ezScrum.web.sqlService.MySQLService;
import ntut.csie.jcis.account.core.IAccount;

public class ProjectRoleTest extends TestCase {
	private MySQLService mService;
	private ezScrumInfoConfig mConfig = new ezScrumInfoConfig();
	private ProjectInformation mProject;
	private IAccount mUser;
	private String mUserId;
	
	public ProjectRoleTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		mService = new MySQLService(new ITSPrefsStorage());
		mService.openConnect();
		
		/**
		 * set up a project and a user
		 */
		ProjectInformation project = new ProjectInformation("name", "name", "comment", "PO_YC", "2");
		mService.createProject(project);
		mProject = mService.getProjectByPid(project.getName());
		UserInformation user = new UserInformation("account", "user name", "password", "email", "true");
		mService.createAccount(user);
		mUser = mService.getAccount(user.getAccount());
		mUserId = "2";
	}

	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		mService.closeConnect();
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(mConfig.getTestDataPath());
		mService = null;
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
		
		List<IAccount> userList = mService.getProjectMemberList(mProject.getId());
		
		assertEquals(1, userList.size());
	}
}
