package ntut.csie.ezScrum.web.mapper;

import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.jcis.account.core.AccountFactory;
import ntut.csie.jcis.account.core.LogonException;

public class AccountMapperTest extends TestCase {
	private AccountMapper mAccountMapper;
	private Configuration configuration = null;

	public AccountMapperTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		mAccountMapper = new AccountMapper();
		// ============= release ==============
		ini = null;
		super.setUp();
	}

	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());

		configuration.setTestMode(false);
		configuration.save();
		
		// ============= release ==============
		ini = null;
		projectManager = null;
		mAccountMapper = null;
		configuration = null;
		AccountFactory.getManager().referesh();	// 等之後scrum role也完成外部toDB即可刪掉
		super.tearDown();
	}
	
	public void testCreateAccount() {
		String id = "account";
		String name = "account robot";
		String password = "account robot";
		String email = "account@mail.com";
		String enable = "true";
		
		AccountInfo user = new AccountInfo(id, name, password, email, enable);
		AccountObject account = mAccountMapper.createAccount(user);
		
		assertEquals(id, account.getUsername());
		assertEquals(name, account.getName());
		assertEquals(email, account.getEmail());
		assertEquals(enable, account.getEnable());
	}
	
	public void testUpdateAccount() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		String id = createAccount.getAccountList().get(0).getId();
		String account = createAccount.getAccount_ID(1);
		String name = "account robot";
		String password = "account robot";
		String email = "update@mail.com";
		String enable = "true";
		
		AccountInfo user = new AccountInfo(id, account, name, password, email, enable);
		AccountObject userObject = mAccountMapper.updateAccount(user);
		
		assertEquals(account, userObject.getUsername());
		assertEquals(name, userObject.getName());
		assertEquals(email, userObject.getEmail());
		assertEquals(enable, userObject.getEnable());
	}
	
	public void testDeleteAccount() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		String id = createAccount.getAccountList().get(0).getId();
		
		boolean result = mAccountMapper.deleteAccount(id);
		
		assertTrue(result);
	}
	
	public void testGetAccountById() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		String id = createAccount.getAccountList().get(0).getId();

		AccountObject account = mAccountMapper.getAccountById(id);
		
		assertEquals(id, account.getId());
		assertEquals(createAccount.getAccount_Mail(1), account.getEmail());
		assertEquals(createAccount.getAccount_RealName(1), account.getName());
		assertEquals("true", account.getEnable());
	}
	
	public void testAccountList() throws InterruptedException {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		
		List<AccountObject> accountList = mAccountMapper.getAccounts();
		
		assertEquals(2, accountList.size());
	}
	
	public void testConfirmAccount() throws LogonException {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		String id = createAccount.getAccount_ID(1);
		String password = createAccount.getAccount_PWD(1);
		AccountObject account = null;
		
        account = mAccountMapper.confirmAccount(id, password);
        
        assertEquals(id, account.getUsername());
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
