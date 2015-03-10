package ntut.csie.ezScrum.web.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.helper.AccountHelper;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;

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
		String aa = mAccountHelper.getAssignedProject(mCA.getAccountList().get(0).getId());
		assertTrue("todo", false);
	}
	
	@Test
	public void testAddAssignedRole() {
		assertTrue("todo", false);
	}
	
	@Test
	public void testRemoveAssignRole() {
		assertTrue("todo", false);
	}
	
	@Test
	public void testGetManagementView() {
		assertTrue("todo", false);
	}
}
