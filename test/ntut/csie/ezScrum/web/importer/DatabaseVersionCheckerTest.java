package ntut.csie.ezScrum.web.importer;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatabaseVersionCheckerTest {
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
	public void testIsDatabaseVersionValid() {
		DatabaseVersionChecker databaseVersionChecker = new DatabaseVersionChecker();
		Assert.assertTrue(databaseVersionChecker.isDatabaseVersionValid());
	}
	
	@Test
	public void testGetReport() {
		DatabaseVersionChecker databaseVersionChecker = new DatabaseVersionChecker();
		Assert.assertEquals("", databaseVersionChecker.getReport());
	}
}
