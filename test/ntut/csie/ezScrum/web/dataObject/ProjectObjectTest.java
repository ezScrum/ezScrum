package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProjectObjectTest{
	private Configuration mConfig;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
	}

	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		mConfig = null;
	}
	
	@Test
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
	
	@Test
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
	
	@Test
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
	
	@Test
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
		
		ArrayList<ProjectObject> projects = ProjectObject.getAllProjects();
				
		assertEquals(1, projects.size());
	}
	
	@Test
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
	
	@Test
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
		
		ProjectObject theProject = ProjectObject.get(name);
		
		assertEquals(name, theProject.getName());
		assertEquals(displayName, theProject.getDisplayName());
		assertEquals(comment, theProject.getComment());
		assertEquals(productOwner, theProject.getManager());
		assertEquals(attachFileSize, theProject.getAttachFileSize());
	}
	
	@Test
	public void testGetProjectMemberList() throws Exception {
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
		String userName = "account";
		String nickName = "user name";
		String password = "password";
		String email = "email";
		boolean enable = true;
		// create account
		AccountObject account = new AccountObject(userName);
		account.setNickName(nickName);
		account.setPassword(password);
		account.setEmail(email);
		account.setEnable(enable);
		account.save();
		account.reload();
		
		// create project role
		boolean result = account.createProjectRole(project.getId(), RoleEnum.ProductOwner);
		assertTrue(result);
		
		// GetProjectMemberList
		List<AccountObject> userList = project.getProjectMembers();
		
		assertEquals(1, userList.size());
	}
}
