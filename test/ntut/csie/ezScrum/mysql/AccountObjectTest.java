package ntut.csie.ezScrum.mysql;

import java.security.MessageDigest;
import java.sql.ResultSet;

import junit.framework.TestCase;
import ntut.csie.ezScrum.dao.AccountDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.databasEnum.AccountEnum;
import ntut.csie.ezScrum.web.sqlService.MySQLService;

public class AccountObjectTest extends TestCase {
	private MySQLService mService;
	private MySQLControl mControl = null;
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
		
		mControl = new MySQLControl(configuration);
		mControl.connection();
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
		mControl = null;
		mService = null;
		configuration = null;
		super.tearDown();
	}
	
	public void testCreateAccount() throws Exception {
		String userName = "TEST_ACCOUNT_USERNAME";
		String nickName = "TEST_ACCOUNT_NICKNAME";
		String password = "TEST_ACCOUNT_PASSWORD";
		String email = "TEST_ACCOUNT_EMAIL";
		boolean enable = true;
		
		AccountObject account = new AccountObject(userName);
		account.setNickName(nickName);
		account.setPassword(password);
		account.setEmail(email);
		account.setEnable(enable);
		account.save();
		account.reload();

		// Query
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addEqualCondition(AccountEnum.ID, account.getId());
		String query = valueSet.getSelectQuery();
		ResultSet resultSet = mControl.executeQuery(query);
		assertTrue(resultSet.first());
		// assertion
		AccountObject createAccount = AccountDAO.getInstance().convertAccount(resultSet);
		assertEquals(userName, createAccount.getUsername());
		assertEquals(nickName, createAccount.getNickName());
		assertEquals(email, createAccount.getEmail());
		assertEquals(enable, createAccount.getEnable());
	}
	
	public void testUpdateAccount() throws Exception {
		// test data
		String userName = "TEST_ACCOUNT_USERNAME";
		String nickName = "TEST_ACCOUNT_NICKNAME";
		String password = "TEST_ACCOUNT_PASSWORD";
		String email = "TEST_ACCOUNT_EMAIL";
		boolean enable = true;
		
		AccountObject account = new AccountObject(userName);
		account.setNickName(nickName);
		account.setPassword(password);
		account.setEmail(email);
		account.setEnable(enable);
		account.save();
		account.reload();
		
		String newUserName = "TEST_ACCOUNT_USERNAME_NEW";
		String newNickName = "TEST_ACCOUNT_NICKNAME_NEW";
		String newPassword = "TEST_ACCOUNT_PASSWORD_NEW";
		String newEmail = "TEST_ACCOUNT_EMAIL_NEW";
		
		AccountObject newAccount = new AccountObject(account.getId(), newUserName);
		newAccount.setNickName(newNickName)
		          .setEmail(newEmail)
		          .setPassword(newPassword)
		          .save();
		newAccount.reload();
		
		// Query
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addEqualCondition(AccountEnum.ID, newAccount.getId());
		String query = valueSet.getSelectQuery();
		ResultSet resultSet = mControl.executeQuery(query);
		assertTrue(resultSet.first());
		
		// assertion
		AccountObject updateAccount = AccountDAO.getInstance().convertAccount(resultSet);
		assertEquals(userName, updateAccount.getUsername());
		assertEquals(newNickName, updateAccount.getNickName());
		assertEquals(getMd5(newPassword), updateAccount.getPassword());
		assertEquals(newEmail, updateAccount.getEmail());
	}
	
	public void testDeleteAccount() throws Exception {
		// test data
		String userName = "TEST_ACCOUNT_USERNAME";
		String nickName = "TEST_ACCOUNT_NICKNAME";
		String password = "TEST_ACCOUNT_PASSWORD";
		String email = "TEST_ACCOUNT_EMAIL";
		boolean enable = true;

		AccountObject account = new AccountObject(userName);
		account.setNickName(nickName);
		account.setPassword(password);
		account.setEmail(email);
		account.setEnable(enable);
		account.save();
		account.reload();
		// delete
		assertTrue(account.delete());
		
		// Query
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addEqualCondition(AccountEnum.ID, account.getId());
		String query = valueSet.getSelectQuery();
		ResultSet resultSet = mControl.executeQuery(query);
		assertFalse(resultSet.first());
	}
	
	public void testToString() throws Exception{
		// test data
		String userName = "TEST_ACCOUNT_USERNAME";
		String nickName = "TEST_ACCOUNT_NICKNAME";
		String password = "TEST_ACCOUNT_PASSWORD";
		String email = "TEST_ACCOUNT_EMAIL";
		boolean enable = true;

		AccountObject account = new AccountObject(userName);
		account.setNickName(nickName);
		account.setPassword(password);
		account.setEmail(email);
		account.setEnable(enable);
		account.save();
		account.reload();
		
		String expectedString = "username :" + userName +
		        ", password :" + getMd5(password) +
		        ", email :" + email +
		        ", name :" + nickName +
		        ", enable :" + Boolean.toString(enable);
		assertEquals(expectedString, account.toString());
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
