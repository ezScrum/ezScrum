package ntut.csie.ezScrum.web.export;

import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatabaseVersionCheckerTest {
	private ezScrumInfoConfig config = new ezScrumInfoConfig();

	@Before
	public void setUp() throws Exception {
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(config);
		ini.exe();

		ini = null;
	}

	@After
	public void tearDown() throws Exception {
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(config);
		ini.exe();

		ini = null;
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
