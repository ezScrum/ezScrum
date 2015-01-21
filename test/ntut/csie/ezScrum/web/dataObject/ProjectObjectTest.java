package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;

public class ProjectObjectTest extends TestCase {
	private Configuration mConfig;
	
	public ProjectObjectTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.store();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
	}

	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(mConfig.getDataPath());
		
		mConfig.setTestMode(false);
		mConfig.store();
		
		mConfig = null;
		super.tearDown();
	}
	
	public void testCreateProject() {
		String name = "testProject";
		String displayName = "testDisplayName";
		String comment = "comment";
		String productOwner = "PO";
		long attachFileSize = 2;
		
		ProjectObject project = new ProjectObject(name);
		project
			.setDisplayName(displayName)
			.setComment(comment)
			.setManager(productOwner)
			.setAttachFileSize(attachFileSize)
			.save();
		project.reload();
		
		assertNotSame(-1, project.getId());
	}
	
	public void testDeleteProject() {
		String name = "testProject";
		String displayName = "testDisplayName";
		String comment = "comment";
		String productOwner = "PO";
		long attachFileSize = 2;
		
		ProjectObject project = new ProjectObject(name);
		project
			.setDisplayName(displayName)
			.setComment(comment)
			.setManager(productOwner)
			.setAttachFileSize(attachFileSize)
			.save();
		project.reload();
		
		assertNotSame(-1, project.getId());
	}
	
	public void testUpdateProject() {
		String name = "testProject";
		String displayName = "testDisplayName";
		String comment = "comment";
		String updateComment = "update comment";
		String productOwner = "PO";
		long attachFileSize = 2;
		
		ProjectObject project = new ProjectObject(name);
		project
			.setDisplayName(displayName)
			.setComment(comment)
			.setManager(productOwner)
			.setAttachFileSize(attachFileSize)
			.save();
		
		project
			.setComment(updateComment)
			.save();
		project.reload();
		
		assertNotSame(-1, project.getId());
		assertEquals(updateComment, project.getComment());
	}
	
	public void testGetProjectList() {
		String name = "testProject";
		String displayName = "testDisplayName";
		String comment = "comment";
		String updateComment = "update comment";
		String productOwner = "PO";
		long attachFileSize = 2;
		
		ProjectObject project = new ProjectObject(name);
		project
			.setDisplayName(displayName)
			.setComment(comment)
			.setManager(productOwner)
			.setAttachFileSize(attachFileSize)
			.save();
		
		project
			.setComment(updateComment)
			.save();
		project.reload();
		
		ArrayList<ProjectObject> projects = ProjectObject.getProjects();
				
		assertEquals(1, projects.size());
	}
	
	public void testGetProject() {
		String name = "testProject";
		String displayName = "testDisplayName";
		String comment = "comment";
		String productOwner = "PO";
		long attachFileSize = 2;
		
		ProjectObject project = new ProjectObject(name);
		project
			.setDisplayName(displayName)
			.setComment(comment)
			.setManager(productOwner)
			.setAttachFileSize(attachFileSize)
			.save();
		project.reload();
		
		ProjectObject theProject = ProjectObject.get(project.getId());
		
		assertEquals(name, theProject.getName());
	}
	
	public void testGetProjectByName() {
		String name = "testProject";
		String displayName = "testDisplayName";
		String comment = "comment";
		String productOwner = "PO";
		long attachFileSize = 2;
		
		ProjectObject project = new ProjectObject(name);
		project
			.setDisplayName(displayName)
			.setComment(comment)
			.setManager(productOwner)
			.setAttachFileSize(attachFileSize)
			.save();
		
		ProjectObject theProject = ProjectObject.getProjectByName(name);
		
		assertEquals(name, theProject.getName());
		assertEquals(displayName, theProject.getDisplayName());
		assertEquals(comment, theProject.getComment());
		assertEquals(productOwner, theProject.getManager());
		assertEquals(attachFileSize, theProject.getAttachFileSize());
	}
}
