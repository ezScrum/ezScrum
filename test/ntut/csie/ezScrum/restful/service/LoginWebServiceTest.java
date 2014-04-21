package ntut.csie.ezScrum.restful.service;

import junit.framework.TestCase;
import ntut.csie.ezScrum.restful.mobile.service.LoginWebService;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.LogonException;

public class LoginWebServiceTest extends TestCase {
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	
	public LoginWebServiceTest(String testMethod) {
		super(testMethod);
	}
	
	protected void setUp() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		super.setUp();
		
		// release
		ini = null;
	}
	
	protected void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		super.tearDown();
		
		// release
    	ini = null;
	}
	
	// IAccountManager.confirmAccount() input不在DB中的account會跳exception(待解決)
	public void testgetAccount() throws LogonException {
		IAccount account;
		String username = "guest";
		String userpwd = "guest";
		LoginWebService login;
		
		// use guest login return null account
		login = new LoginWebService(username, userpwd);
		account = login.getAccount();
		assertNotNull(account);
		assertEquals(true, account.isGuest());
		assertEquals(username, account.getID());
		
		username = "admin";
		userpwd = "admin";
		
		login = new LoginWebService(username, userpwd);
		account = login.getAccount();
		assertNotNull(account);
		assertEquals(false, account.isGuest());
		assertEquals(username, account.getID());
	}
}
