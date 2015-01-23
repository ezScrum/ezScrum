package ntut.csie.ezScrum.mysql;

import java.util.ArrayList;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.sqlService.MySQLService;

public class AccountObjectTest extends TestCase {
	private MySQLService mService;
	private Configuration configuration = null;
	
	public AccountObjectTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		mService = new MySQLService(configuration);
		mService.openConnect();
	}

	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		mService.closeConnect();
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());
		
		configuration.setTestMode(false);
		configuration.save();
		
		mService = null;
		configuration = null;
		super.tearDown();
	}
	
	public void testCreateAccount() {
		String id = "account";
		String name = "account robot";
		String password = "account robot";
		String email = "iaccount@mail.com";
		String enable = "true";
		
		AccountInfo user = new AccountInfo(id, name, password, email, enable);
		boolean result = mService.createAccount(user);
		
		assertTrue(result);
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
		boolean result = mService.updateAccount(user);
		
		assertTrue(result);
	}
	
	public void testDeleteAccount() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		AccountObject account = createAccount.getAccountList().get(0);
		
		boolean result = account.delete();
		
		assertTrue(result);
	}
	
	public void testGetAccountById() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		String id = createAccount.getAccount_ID(1);
		
		AccountObject result = mService.getAccount(id);
		
		assertEquals(id, result.getUsername());
	}
	
	public void testGetAccountList() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		
		ArrayList<AccountObject> accounts = AccountObject.getAccounts();
		
		assertEquals(2, accounts.size());	// include admin
	}
	
	public void testConfirmAccount() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		String id = createAccount.getAccount_ID(1);
		String password = createAccount.getAccount_PWD(1);
		
		AccountObject result = mService.confirmAccount(id, password);
		
		assertEquals(id, result.getUsername());
	}
}
