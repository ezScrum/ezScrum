package ntut.csie.ezScrum.web.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.security.MessageDigest;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.jcis.account.core.LogonException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AccountMapperTest{
	private AccountMapper mAccountMapper;
	private Configuration mConfig = null;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mAccountMapper = new AccountMapper();
		
		// ============= release ==============
		ini = null;
	}

	@After
	public void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
		
		// ============= release ==============
		ini = null;
		projectManager = null;
		mAccountMapper = null;
		mConfig = null;
	}
	
	@Test
	public void testCreateAccount() {
		String userName = "account";
		String password = "account robot";
		String nickName = "account robot";
		String email = "account@mail.com";
		
		AccountInfo userInfo = new AccountInfo();
		userInfo.username = userName;
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
		assertEquals(true, account.getEnable());
	}
	
	@Test
	public void testGetAccountByUsername() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		
		String username = createAccount.getAccountList().get(0).getUsername();
		AccountObject account = mAccountMapper.getAccount(username);
		
		assertEquals(2, account.getId());
		assertEquals(createAccount.getAccount_Mail(1), account.getEmail());
		assertEquals(createAccount.getAccount_RealName(1), account.getNickName());
		assertEquals(true, account.getEnable());
	}
	
	@Test
	public void testGetAllAccounts() throws InterruptedException {
		CreateAccount createAccount = new CreateAccount(3);
		createAccount.exe();
		ArrayList<AccountObject> accounts = mAccountMapper.getAccounts();
		assertEquals(4, accounts.size());
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
		userInfo.username = userName;
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
		assertNull(mAccountMapper.getAccount(id));
	}
	
	@Test
	public void testConfirmAccount() throws LogonException {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		
		String userName = createAccount.getAccount_ID(1);
		String password = createAccount.getAccount_PWD(1);
		AccountObject account = null;
		
        account = mAccountMapper.confirmAccount(userName, password);
        
        assertEquals(userName, account.getUsername());
        assertEquals(getMd5(password), account.getPassword());
	}

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
