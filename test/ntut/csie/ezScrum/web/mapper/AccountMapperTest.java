package ntut.csie.ezScrum.web.mapper;

import java.security.MessageDigest;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
	private Configuration mConfig = null;

	public AccountMapperTest(String testMethod) {
		super(testMethod);
	}

	@Before
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mAccountMapper = new AccountMapper();
		
		// ============= release ==============
		ini = null;
		super.setUp();
	}

	@After
	protected void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(mConfig.getDataPath());

		mConfig.setTestMode(false);
		mConfig.save();
		
		// ============= release ==============
		ini = null;
		projectManager = null;
		mAccountMapper = null;
		mConfig = null;
		AccountFactory.getManager().referesh();	// 等之後scrum role也完成外部toDB即可刪掉
		super.tearDown();
	}
	
	@Test
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
		assertEquals(getMd5(password), account.getPassword());
		assertEquals(nickName, account.getNickName());
		assertEquals(email, account.getEmail());
		assertEquals(true, account.getEnable());
	}
	
	@Test
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
	
	@Test
	public void testGetAccountByName() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		long id = createAccount.getAccountList().get(0).getId();
		
		AccountObject account = mAccountMapper.getAccount(id);
		
		assertEquals(id, account.getId());
		assertEquals(createAccount.getAccount_Mail(1), account.getEmail());
		assertEquals(createAccount.getAccount_RealName(1), account.getNickName());
		assertEquals("true", account.getEnable());
	}
	
	@Test
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
	
	@Test
	public void testDeleteAccount() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		long id = createAccount.getAccountList().get(0).getId();
		
		boolean result = mAccountMapper.deleteAccount(id);
		
		assertTrue(result);
	}
	
//	
//	public void testAccountList() throws InterruptedException {
//		CreateAccount createAccount = new CreateAccount(1);
//		createAccount.exe();
//		
//		List<AccountObject> accountList = mAccountMapper.getAccounts();
//		
//		assertEquals(2, accountList.size());
//	}
//	
//	public void testConfirmAccount() throws LogonException {
//		CreateAccount createAccount = new CreateAccount(1);
//		createAccount.exe();
//		String id = createAccount.getAccount_ID(1);
//		String password = createAccount.getAccount_PWD(1);
//		AccountObject account = null;
//		
//        account = mAccountMapper.confirmAccount(id, password);
//        
//        assertEquals(id, account.getUsername());
//	}
//	
//	public void testIsAccountExist() {
//		CreateAccount createAccount = new CreateAccount(1);
//		createAccount.exe();
//		String id = createAccount.getAccount_ID(1);
//		
//        boolean result = mAccountMapper.isAccountExist(id);
//       
//        assertTrue(result);
//	}
	
	private String getMd5(String str) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
		}
		md.update(str.getBytes());
		byte b[] = md.digest();
		str = byte2hex(b);
		return str;
	}

	private String byte2hex(byte b[]) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 255);
			if (stmp.length() == 1) {
				hs = (new StringBuilder(String.valueOf(hs))).append("0").append(stmp).toString();
			} else {
				hs = (new StringBuilder(String.valueOf(hs))).append(stmp).toString();
			}
		}
		return hs;
	}
}
