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
		String userName = "account";
		String password = "account robot";
		String nickName = "account robot";
		String email = "account@mail.com";
		
		AccountInfo userInfo = new AccountInfo();
		userInfo.userName = userName;
		userInfo.password = password;
		userInfo.nickName = nickName;
		userInfo.email = email;
		
		AccountObject account = mAccountMapper.createAccount(userInfo);
		assertEquals(userName, account.getUsername());
		assertEquals(password, account.getPassword());
		assertEquals(nickName, account.getNickName());
		assertEquals(email, account.getEmail());
		assertEquals(true, account.getEnable());
	}
	
	public void testUpdateAccount() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		long id = createAccount.getAccountList().get(0).getId();
		String userName = createAccount.getAccount_ID(1);
		String nickName = "account robot";
		String password = "account robot";
		String email = "update@mail.com";
		boolean enable = true;
		
		AccountInfo userInfo = new AccountInfo();
		userInfo.id = id;
		userInfo.userName = userName;
		userInfo.password = password;
		userInfo.nickName = nickName;
		userInfo.email = email;
		userInfo.enable = enable;
		
		AccountObject userObject = mAccountMapper.updateAccount(userInfo);
		assertEquals(userName, userObject.getUsername());
		assertEquals(nickName, userObject.getNickName());
		assertEquals(email, userObject.getEmail());
		assertEquals(enable, userObject.getEnable());
	}
	
	public void testDeleteAccount() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		long id = createAccount.getAccountList().get(0).getId();
		
		boolean result = mAccountMapper.deleteAccount(id);
		
		assertTrue(result);
	}
	
	public void testGetAccountById() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		long id = createAccount.getAccountList().get(0).getId();

		AccountObject account = mAccountMapper.getAccount(id);
		
		assertEquals(id, account.getId());
		assertEquals(createAccount.getAccount_Mail(1), account.getEmail());
		assertEquals(createAccount.getAccount_RealName(1), account.getNickName());
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
