package ntut.csie.ezScrum.mysql;

import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;
import ntut.csie.ezScrum.web.sqlService.MySQLService;

public class ScrumRoleTest extends TestCase {
	private MySQLService mService;
	private Configuration configuration;
	private ProjectObject mProject;
	
	public ScrumRoleTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		mService = new MySQLService(configuration);
		mService.openConnect();
		
		/**
		 * set up a project
		 */
		ProjectObject project = new ProjectObject("name", "name", "comment", "PO_YC", "2");
		mService.createProject(project);
		mProject = mService.getProjectByPid(project.getName());
	}

	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		mService.closeConnect();
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());
		
		configuration.setTestMode(false);
		configuration.store();
		
		mService = null;
		configuration = null;
		super.tearDown(); 
	}
	
	public void testCreateScrumRole() {
		RoleEnum role = RoleEnum.ProductOwner;
		ScrumRole scrumRole = new ScrumRole(role);
		boolean result = mService.createScrumRole(mProject.getId(), role, scrumRole);
	
		assertTrue(result);
	}
	
	public void testUpdateScrumRole() {
		RoleEnum role = RoleEnum.ProductOwner;
		ScrumRole scrumRole = new ScrumRole(role);
		String id = mProject.getId();
		mService.createScrumRole(id, role, scrumRole);
		boolean result = mService.updateScrumRole(id, role, scrumRole);
		
		assertTrue(result);
	}
	
	public void testGetProjectWorkerList() {
		// user default admin as the project PO
		mService.createProjectRole(mProject.getId(), "1", RoleEnum.ProductOwner); 
		ScrumRole scrumRole;
		for (RoleEnum role : RoleEnum.values()) {
			scrumRole = new ScrumRole(role);
			mService.createScrumRole(mProject.getId(), role, scrumRole);
		}
		List<AccountObject> userList = mService.getProjectWorkerList(mProject.getId());
		
		assertEquals(1, userList.size());
	}
}
