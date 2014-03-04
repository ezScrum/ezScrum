package ntut.csie.ezScrum.mysql;

import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.ITSPrefsStorage;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.RoleEnum;
import ntut.csie.ezScrum.web.dataObject.UserInformation;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.sqlService.MySQLService;

public class ScrumRoleTest extends TestCase {
	private MySQLService mService;
	private ezScrumInfoConfig mConfig = new ezScrumInfoConfig();
	private ProjectObject mProject;
	
	public ScrumRoleTest(String testMethod) {
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
		ProjectObject project = new ProjectObject("name", "name", "comment", "PO_YC", "2");
		mService.createProject(project);
		mProject = mService.getProjectByPid(project.getName());
		UserInformation user = new UserInformation("account", "user name", "password", "email", "true");
		mService.createAccount(user);
		mService.createProjectRole(mProject.getId(), "2", RoleEnum.ProductOwner);
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
	
	public void testCreateScrumRole() {
		RoleEnum role = RoleEnum.ProductOwner;
		ScrumRole scrumRole = new ScrumRole(role);
		boolean result = mService.createScrumRole(mProject.getId(), role, scrumRole);
	
		assertTrue(result);
	}
	
	public void testGetProjectWorkerList() {
		ScrumRole scrumRole;
		for (RoleEnum role : RoleEnum.values()) {
			scrumRole = new ScrumRole(role);
			mService.createScrumRole(mProject.getId(), role, scrumRole);
		}
		
		List<UserObject> userList = mService.getProjectWorkerList(mProject.getId());
		
		assertEquals(1, userList.size());
	}
}
