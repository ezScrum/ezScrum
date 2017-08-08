package ntut.csie.ezScru.web.microservice;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import ntut.csie.ezScru.web.microservice.command.ICommand;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.helper.AccountHelper;

public class AccountRESTClientProxyTest {
	private MySQLControl mControl = null;
	private Configuration mConfig;
	String token = "";
	
	private IAccount mAccountRESTClientProxy;
	@Before
	public void setUp() {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mControl = new MySQLControl(mConfig);
		mControl.connect();
		
		mAccountRESTClientProxy = new MicroserviceProxy(token);
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
	public void validateUsernameTest(){
		String result = mAccountRESTClientProxy.validateUsername("admin");
		boolean check = Boolean.valueOf(result);
		Assert.assertFalse(check);
	}
	@Test
	public void validateUsernameTest_true(){
		String result = mAccountRESTClientProxy.validateUsername("Test");
		boolean check = Boolean.valueOf(result);
		Assert.assertTrue(check);
	}
	@Test
	public void createTest_true(){
		AccountInfo accountInfo = new AccountInfo();
		accountInfo.email = "TEST@gmail.com";
		accountInfo.username = "TEST";
		accountInfo.password = "TEST";
		accountInfo.nickName = "TEST";
		accountInfo.enable = true;
		AccountObject result = mAccountRESTClientProxy.createAccount(accountInfo);
		
	}
	
}
