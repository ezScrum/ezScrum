package ntut.csie.ezScrum.web.mapper;

import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.dataObject.UserInformation;
import ntut.csie.jcis.account.core.AccountFactory;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IActor;
import ntut.csie.jcis.account.core.LogonException;

public class AccountMapperTest extends TestCase {
	private ezScrumInfoConfig mConfig = new ezScrumInfoConfig();
	private AccountMapper mAccountMapper;

	public AccountMapperTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL
		mAccountMapper = new AccountMapper();
		// ============= release ==============
		ini = null;
		super.setUp();
	}

	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(mConfig.getTestDataPath());

		// ============= release ==============
		ini = null;
		projectManager = null;
		mAccountMapper = null;
		AccountFactory.getManager().referesh();	// 等之後scrum role也完成外部toDB即可刪掉
		super.tearDown();
	}
	
	public void testCreateAccount() {
		String id = "account";
		String name = "account robot";
		String password = "account robot";
		String email = "account@mail.com";
		String enable = "true";
		String roles = "user";
		
		UserInformation user = new UserInformation(id, name, password, email, enable);
		IAccount account = mAccountMapper.createAccount(user, roles);
		
		assertEquals(id, account.getID());
		assertEquals(name, account.getName());
		assertEquals(email, account.getEmail());
		assertEquals(enable, account.getEnable());
		assertEquals(roles, account.getRoles()[0].getRoleId());
	}
	
	public void testUpdateAccount() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		String id = createAccount.getAccount_ID(1);
		String name = "account robot";
		String password = "account robot";
		String email = "update@mail.com";
		String enable = "true";
		String roles = "user";
		
		UserInformation user = new UserInformation(id, name, password, email, enable);
		IAccount account = mAccountMapper.updateAccount(user);
		
		assertEquals(id, account.getID());
		assertEquals(name, account.getName());
		assertEquals(email, account.getEmail());
		assertEquals(enable, account.getEnable());
		assertEquals(roles, account.getRoles()[0].getRoleId());
	}
	
	public void testDeleteAccount() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		String id = createAccount.getAccount_ID(1);
		
		boolean result = mAccountMapper.deleteAccount(id);
		
		assertTrue(result);
	}
	
	public void testGetAccountById() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		String id = createAccount.getAccount_ID(1);

		IAccount account = mAccountMapper.getAccount(id);
		
		assertEquals(id, account.getID());
		assertEquals(createAccount.getAccount_Mail(1), account.getEmail());
		assertEquals(createAccount.getAccount_RealName(1), account.getName());
		assertEquals("true", account.getEnable());
		assertEquals("user", account.getRoles()[0].getRoleId());
	}
	
	public void testAccountList() throws InterruptedException {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		
		List<IActor> accountList = mAccountMapper.getAccountList();
		
		assertEquals(2, accountList.size());
	}
	
	public void testConfirmAccount() throws LogonException {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		String id = createAccount.getAccount_ID(1);
		String password = createAccount.getAccount_PWD(1);
		IAccount account = null;
		
        account = mAccountMapper.confirmAccount(id, password);
        
        assertEquals(id, account.getID());
	}
	
	public void testIsAccountExist() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		String id = createAccount.getAccount_ID(1);
		
        boolean result = mAccountMapper.isAccountExist(id);
       
        assertTrue(result);
	}
	
//	public void testGetPermission() {
//		
//	}
//	
//	public void testCreateRole() {
//		
//	}
//
//	public void testRemoveRole() {
//		
//	}
//	
//	public void testAddRole() {
//		
//	}
//	
//	public void testIsCreatePermission() {
//		
//	}
}
