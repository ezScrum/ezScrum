package ntut.csie.ezScrum.restful.dataMigration.security;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class SecurityModuleTest {
	private Configuration mConfig;
	private CreateProject mCP;
	@Before
	public void setUp(){
		// Set Test Mode
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// Create Project
		mCP = new CreateProject(1);
		mCP.exeCreateForDb();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		createAcoount();
	}
	
	@After
	public void tearDown(){
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除測試檔案
		CopyProject copyProject = new CopyProject(mCP);
		copyProject.exeDelete_Project();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
	}
	
	@Test
	public void testUserNotExist(){
		String accountName = "Mike";
		String accountPassword = "1234";
		boolean checkUser = SecurityModule.checkAccount(accountName, accountPassword);
		assertEquals(false, checkUser);
	}
	
	@Test
	public void testUserNotCorrect(){
		String accountName = "Jimmy";
		String accountPassword = "1234";
		boolean checkUser = SecurityModule.checkAccount(accountName, accountPassword);
		assertEquals(false, checkUser);
	}
	
	@Test
	public void testUserIsCorrect(){
		String accountName = "Jimmy";
		String accountPassword = "93189e2c4c7b1a2c7b16a24d5daa98a9";
		boolean checkUser = SecurityModule.checkAccount(accountName, accountPassword);
		assertEquals(true, checkUser);
	}
	
	private void createAcoount(){
		String userName = "Jimmy";
		String userNickName = "TEST_USER_NICK_NAME";
		String userPassword = "93189e2c4c7b1a2c7b16a24d5daa98a9";
		String userEmail = "TEST_USER_EMAIL";
		AccountObject account = new AccountObject(userName);
		account.setEmail(userEmail)
		  	   .setNickName(userNickName)
		  	   .setPassword(userPassword)
		  	   .save();
	}
}
