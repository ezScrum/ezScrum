package ntut.csie.ezScrum.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;

public class AttachFileDAOTest {
	private MySQLControl mControl = null;
	private Configuration mConfig;

	@Before
    public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mControl = new MySQLControl(mConfig);
		mControl.connect();
    }

	@After
    public void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// ============= release ==============
		ini = null;
		mConfig = null;
		mControl = null;
    }
	
	@Test
	public void testCreate() {
		
	}
}
