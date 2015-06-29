package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.security.MessageDigest;
import java.sql.ResultSet;

import ntut.csie.ezScrum.dao.AccountDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.databasEnum.AccountEnum;
import ntut.csie.ezScrum.web.databasEnum.ProjectRoleEnum;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;

import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AccountObjectTest{
	private MySQLControl mControl = null;
	private Configuration mConfig = null;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mControl = new MySQLControl(mConfig);
		mControl.connect();
	}

	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
		
		projectManager = null;
		mControl = null;
		mConfig = null;
	}
	
	@Test
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
	
	@Test
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
	
	@Test
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
	
	@Test
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
		
		JSONObject json = new JSONObject();

		json.put(AccountEnum.ID, account.getId())
		        .put(AccountEnum.USERNAME, account.getUsername())
		        .put(AccountEnum.NICK_NAME, account.getNickName())
		        .put(AccountEnum.EMAIL, account.getEmail())
		        .put(AccountEnum.ENABLE, account.getEnable())
		        .put(AccountEnum.CREATE_TIME, account.getCreateTime())
		        .put(AccountEnum.UPDATE_TIME, account.getUpdateTime());
		
		String expectedString = json.toString();
		assertEquals(expectedString, account.toString());
	}
	
	@Test
	public void testCreateProjectRole() throws Exception {
		/**
		 * set up a project and a user
		 */
		ProjectObject project = new ProjectObject("name");
		project.setDisplayName("name")
			.setComment("comment")
			.setManager("PO_YC")
			.setAttachFileSize(2)
			.save();
		project.reload();
		String userName = "account";
		String nickName = "user name";
		String password = "password";
		String email = "email";
		boolean enable = true;
		// create account
		AccountObject account = new AccountObject(userName);
		account.setNickName(nickName);
		account.setPassword(password);
		account.setEmail(email);
		account.setEnable(enable);
		account.save();
		account.reload();
		// create project role
		boolean result = account.createProjectRole(project.getId(), RoleEnum.ProductOwner);
		assertTrue(result);
	}
	
	@Test
	public void testDeleteProjectRole() throws Exception {
		/**
		 * set up a project and a user
		 */
		ProjectObject project = new ProjectObject("name");
		project.setDisplayName("name")
			.setComment("comment")
			.setManager("PO_YC")
			.setAttachFileSize(2)
			.save();
		project.reload();
		String userName = "account";
		String nickName = "user name";
		String password = "password";
		String email = "email";
		boolean enable = true;
		// create account
		AccountObject account = new AccountObject(userName);
		account.setNickName(nickName);
		account.setPassword(password);
		account.setEmail(email);
		account.setEnable(enable);
		account.save();
		account.reload();
		
		// create project role
		boolean result = account.createProjectRole(project.getId(), RoleEnum.ProductOwner);
		assertTrue(result);
		
		// delete project role
		result = account.deleteProjectRole(project.getId(), RoleEnum.ProductOwner);
		assertTrue(result);
	}
	
	@Test
	public void testGetSystemRole() throws Exception {
		AccountObject adminAccount = AccountDAO.getInstance().get(1);
		
		ProjectRole role = adminAccount.getSystemRole();
		assertNotNull(role);	// 預設應該就要有admin帳號存在了
	}
	
	@Test
	public void testCreateSystemRole() throws Exception {
		String userName = "account";
		String nickName = "user name";
		String password = "password";
		String email = "email";
		boolean enable = true;
		// create account
		AccountObject account = new AccountObject(userName);
		account.setNickName(nickName);
		account.setPassword(password);
		account.setEmail(email);
		account.setEnable(enable);
		account.save();
		account.reload();
		// create System Role
		boolean result = account.createSystemRole();
		assertTrue(result);
		// assert is system role
		ProjectRole role = account.getSystemRole();
		assertNotNull(role);
	}
	
	@Test
	public void testDeleteSystemRole() throws Exception {
		String userName = "account";
		String nickName = "user name";
		String password = "password";
		String email = "email";
		boolean enable = true;
		// create account
		AccountObject account = new AccountObject(userName);
		account.setNickName(nickName);
		account.setPassword(password);
		account.setEmail(email);
		account.setEnable(enable);
		account.save();
		account.reload();
		// create role
		boolean result = account.createSystemRole();
		assertTrue(result);
		// delete role
		result = account.deleteSystemRole();
		assertTrue(result);
		// assert not system role
		ProjectRole role = account.getSystemRole();
		assertNull(role);
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
