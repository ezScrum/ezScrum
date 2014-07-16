package ntut.csie.ezScrum.mysql;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.sqlService.MySQLService;

public class SystemRoleTest extends TestCase {
	private MySQLService mService;
	private Configuration configuration;

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		mService = new MySQLService(configuration);
		mService.openConnect();
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
	
	public SystemRoleTest(String testMethod) {
		super(testMethod);
	}

	public void testGetSystemRole() {
		ProjectRole role = mService.getSystemRole("1");	// id = 1 means admin
		
		assertNotNull(role);	// 預設應該就要有admin帳號存在了
	}

	public void testCreateSystemRole() {
		String id = "2";
		boolean result = mService.createSystemRole(id);
		
		assertTrue(result);
	}

	public void testDeleteSystemRole() {
		String id = "2";
		mService.createSystemRole(id);
		boolean result = mService.deleteSystemRole(id);
		
		assertTrue(result);
	}
}
