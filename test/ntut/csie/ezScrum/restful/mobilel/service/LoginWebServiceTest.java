package ntut.csie.ezScrum.restful.mobilel.service;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.restful.mobile.service.LoginWebService;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.jcis.account.core.LogonException;

public class LoginWebServiceTest {
	private Configuration mConfig;
	
	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
	}
	
	@After
	public void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mConfig.setTestMode(false);
		mConfig.save();
		// release
		mConfig = null;
	}
	
	// IAccountManager.confirmAccount() input不在DB中的account會跳exception(待解決)
	@Test
	public void testgetAccount() throws LogonException {
		AccountObject account;
		String username = "guest";
		String userpwd = "guest";
		LoginWebService login;
		// use guest login return null account
		login = new LoginWebService(username, userpwd);
		account = login.getAccount();
		assertNull(account);
		username = "admin";
		userpwd = "admin";
		login = new LoginWebService(username, userpwd);
		account = login.getAccount();
		assertNotNull(account);
		assertEquals(username, account.getUsername());
	}
}
