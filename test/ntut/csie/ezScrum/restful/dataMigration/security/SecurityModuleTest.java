package ntut.csie.ezScrum.restful.dataMigration.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.dao.AccountDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class SecurityModuleTest {
	private Configuration mConfig;
	
	@Before
	public void setUp(){
		// Set Test Mode
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
	}
	
	@After
	public void tearDown(){
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
	}
	
	@Test
	public void testIsAccountValid_UserNotAdmin(){
		String username = "Jimmy";
		String userNickname = "TEST_USER_NICK_NAME";
		String userPassword = "93189e2c4c7b1a2c7b16a24d5daa98a9";
		String userEmail = "TEST_USER_EMAIL";
		AccountObject account = new AccountObject(username);
		account.setEmail(userEmail)
		  	   .setNickName(userNickname)
		  	   .setPassword(userPassword)
		  	   .save();
		boolean isAccountValid = SecurityModule.isAccountValid(AccountDAO.getMd5(username), userPassword);
		assertFalse(isAccountValid);
	}
	
	@Test
	public void testIsAccountValid_UserIsCorrect(){
		final String ADMIN_USERNAME = "admin";
		AccountObject admin = AccountObject.get(ADMIN_USERNAME);
		boolean isAccountValid = SecurityModule.isAccountValid(AccountDAO.getMd5(ADMIN_USERNAME), admin.getPassword());
		assertTrue(isAccountValid);
	}
	
	@Test
	public void testIsAccountValid_UserIsCorrect_AnotherAdmin(){
		String username = "Jimmy";
		String userNickname = "TEST_USER_NICK_NAME";
		String userPassword = "93189e2c4c7b1a2c7b16a24d5daa98a9";
		String userEmail = "TEST_USER_EMAIL";
		AccountObject account = new AccountObject(username);
		account.setEmail(userEmail)
		  	   .setNickName(userNickname)
		  	   .setPassword(userPassword)
		  	   .save();
		account.createSystemRole();
		boolean isAccountValid = SecurityModule.isAccountValid(AccountDAO.getMd5(username), userPassword);
		assertTrue(isAccountValid);
	}
	
	@Test
	public void testIsAccountValid_UserIsNull(){
		boolean isAccountValid = SecurityModule.isAccountValid(null, null);
		assertFalse(isAccountValid);
	}
	
	@Test
	public void testIsAccountValid_UserIsEmpty(){
		boolean isAccountValid = SecurityModule.isAccountValid("", "");
		assertFalse(isAccountValid);
	}
}
