package ntut.csie.ezScrum.mysql;

import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.sqlService.MySQLService;

public class ProjectTest extends TestCase {
	private MySQLService mService;
	private Configuration configuration;
	
	public ProjectTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
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
		configuration.save();
		
		mService = null;
		configuration = null;
		super.tearDown();
	}
	
	public void testCreateProject() {
		String id = "testProject";
		String name = "testProject";
		String comment = "comment";
		String productOwner = "PO";
		String attachFileSize = "2";
		
		ProjectObject project = new ProjectObject(id, name, comment, productOwner, attachFileSize);
		boolean result = mService.createProject(project);
		
		assertTrue(result);
	}
	
	public void testDeleteProject() {
		String id = "testProject";
		String name = "testProject";
		String comment = "comment";
		String productOwner = "PO";
		String attachFileSize = "2";
		
		ProjectObject project = new ProjectObject(id, name, comment, productOwner, attachFileSize);
		mService.createProject(project);
		boolean result = mService.deleteProject(id);
				
		assertTrue(result);
	}
	
	public void testUpdateProject() {
		String id = "testProject";
		String name = "testProject";
		String comment = "comment";
		String updateComment = "update comment";
		String productOwner = "PO";
		String attachFileSize = "2";
		
		ProjectObject project = new ProjectObject(id, name, comment, productOwner, attachFileSize);
		mService.createProject(project);
		project = new ProjectObject(id, name, updateComment, productOwner, attachFileSize);
		boolean result = mService.updateProject(project);
				
		assertTrue(result);
	}
	
	public void testGetProjectList() {
		String id = "testProject";
		String name = "testProject";
		String comment = "comment";
		String updateComment = "update comment";
		String productOwner = "PO";
		String attachFileSize = "2";
		
		ProjectObject project = new ProjectObject(id, name, comment, productOwner, attachFileSize);
		mService.createProject(project);
		project = new ProjectObject(id, name, updateComment, productOwner, attachFileSize);
		List<ProjectObject> result = mService.getProjectList();
				
		assertEquals(1, result.size());
	}
	
	public void testGetProjectById() {
		String id = "testProject";
		String name = "testProject";
		String comment = "comment";
		String productOwner = "PO";
		String attachFileSize = "2";
		
		ProjectObject project = new ProjectObject(id, name, comment, productOwner, attachFileSize);
		mService.createProject(project);
		ProjectObject result = mService.getProjectById("1");	// only one project
				
		assertEquals(id, result.getName());
	}
	
	public void testGetProjectByPid() {
		String id = "testProject";
		String name = "testProject";
		String comment = "comment";
		String productOwner = "PO";
		String attachFileSize = "2";
		
		ProjectObject project = new ProjectObject(id, name, comment, productOwner, attachFileSize);
		mService.createProject(project);
		ProjectObject result = mService.getProjectByPid(id);	// only one project
		
		assertEquals(id, result.getName());
	}
}
