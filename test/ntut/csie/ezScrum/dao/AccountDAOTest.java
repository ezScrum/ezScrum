package ntut.csie.ezScrum.dao;

import static org.junit.Assert.*;

import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.databasEnum.AccountEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AccountDAOTest {
	private MySQLControl mControl = null;
	private Configuration mConfig;

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
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// ============= release ==============
		ini = null;
		mConfig = null;
		mControl = null;
    }
	
	@Test
	public void testCreate() throws SQLException{
		String userName = "TEST_USERNAME_";
		String nickName =  "TEST_NICKNAME_";
		String email = "TEST_EMAIL_";
		String password = "TEST_PASSWORD_";
		boolean enable = true;
		// create 3 account
		for(int i = 0; i < 3; i++){
			AccountObject account = new AccountObject(userName + (i + 1));
			account.setNickName(nickName + (i + 1)).setEmail(email + (i + 1))
			       .setPassword(password + (i + 1)).setEnable(enable);
			long accountId = AccountDAO.getInstance().create(account);
			assertNotSame(-1, accountId);
		}
		
		for(int i = 0; i < 3; i++){
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(AccountEnum.TABLE_NAME);
			valueSet.addEqualCondition(AccountEnum.ID, (i + 2));
			String query = valueSet.getSelectQuery();
			ResultSet resultSet = mControl.executeQuery(query);
			if (resultSet.next()) {
				AccountObject account = AccountDAO.getInstance().convertAccount(resultSet);
				assertEquals(userName + (i + 1), account.getUsername());
				assertEquals(nickName + (i + 1), account.getNickName());
				assertEquals(email + (i + 1), account.getEmail());
				assertEquals(getMd5(password + (i + 1)), account.getPassword());
				assertTrue(enable == account.getEnable());
			}
		}
	}
	
	@Test
	public void testGet(){
		String userName = "TEST_USERNAME_";
		String nickName =  "TEST_NICKNAME_";
		String email = "TEST_EMAIL_";
		String password = "TEST_PASSWORD_";
		boolean enable = true;
		// create 3 account
		for(int i = 0; i < 3; i++){
			AccountObject account = new AccountObject(userName + (i + 1));
			account.setNickName(nickName + (i + 1)).setEmail(email + (i + 1))
			       .setPassword(password + (i + 1)).setEnable(enable);
			long accountId = AccountDAO.getInstance().create(account);
			assertNotSame(-1, accountId);
		}
		
		ArrayList<AccountObject> accountList = new ArrayList<AccountObject>();
		for (int i = 0; i < 3; i++) {
			accountList.add(AccountDAO.getInstance().get(i + 2));
		}
		assertEquals(3, accountList.size());
		
		// assert
		for(int i = 0 ; i < 3 ; i++){
			assertEquals(userName + (i + 1), accountList.get(i).getUsername());
			assertEquals(nickName + (i + 1), accountList.get(i).getNickName());
			assertEquals(email + (i + 1), accountList.get(i).getEmail());
			assertEquals(getMd5(password + (i + 1)), accountList.get(i).getPassword());
			assertEquals(enable, accountList.get(i).getEnable());
		}
	}
	
	@Test
	public void testUpdate(){
		String userName = "TEST_USERNAME_1";
		String nickName =  "TEST_NICKNAME_1";
		String email = "TEST_EMAIL_1";
		String password = "TEST_PASSWORD_1";
		boolean enable = true;
		// create
		AccountObject account = new AccountObject(userName);
		account.setNickName(nickName).setEmail(email)
		       .setPassword(password).setEnable(enable);
		long accountId = AccountDAO.getInstance().create(account);
		assertNotSame(-1, accountId);
		// update 
		account = AccountDAO.getInstance().get(accountId);
		account.setNickName("hello");
		account.setPassword("12312113134546");
		account.setEmail("hello@gmail.com");
		account.setEnable(true);
		boolean updateResult = AccountDAO.getInstance().update(account);
		assertTrue(updateResult);
		// get 
		AccountObject newAccount = AccountDAO.getInstance().get(accountId);
		
		// assert
		assertEquals(account.getNickName(), newAccount.getNickName());
		assertEquals(getMd5(account.getPassword()), newAccount.getPassword());
		assertEquals(account.getEmail(), newAccount.getEmail());
		assertEquals(account.getEnable(), newAccount.getEnable());
	}
	
	@Test
	public void testDelete() throws SQLException{
		String userName = "TEST_USERNAME_";
		String nickName =  "TEST_NICKNAME_";
		String email = "TEST_EMAIL_";
		String password = "TEST_PASSWORD_";
		boolean enable = true;
		// create 3 account
		for(int i = 0; i < 3; i++){
			AccountObject account = new AccountObject(userName + (i + 1));
			account.setNickName(nickName + (i + 1)).setEmail(email + (i + 1))
			       .setPassword(password + (i + 1)).setEnable(enable);
			long accountId = AccountDAO.getInstance().create(account);
			assertNotSame(-1, accountId);
		}
		
		ArrayList<AccountObject> accountList = new ArrayList<AccountObject>();
		for(int i = 0; i < 3 ; i++){
			accountList.add(AccountDAO.getInstance().get(i + 2));
		}
		assertEquals(3, accountList.size());
		
		// delete
		boolean deleteResult = AccountDAO.getInstance().delete(2);
		assertTrue(deleteResult);
		
		// reload accounts
		accountList.clear();
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addBigCondition(AccountEnum.ID, "1");
		String query = valueSet.getSelectQuery();
		ResultSet resultSet = mControl.executeQuery(query);
		while (resultSet.next()) {
			accountList.add(AccountDAO.getInstance().convertAccount(resultSet));
		}
		assertEquals(2, accountList.size());
	}
	
	@Test
	public void testGetSystemRole(){
		String userName = "TEST_USERNAME_1";
		String nickName =  "TEST_NICKNAME_1";
		String email = "TEST_EMAIL_1";
		String password = "TEST_PASSWORD_1";
		boolean enable = true;
		// create
		AccountObject account = new AccountObject(userName);
		account.setNickName(nickName).setEmail(email)
		       .setPassword(password).setEnable(enable);
		long accountId = AccountDAO.getInstance().create(account);
		assertNotSame(-1, accountId);
		
		// get Admin
		AccountObject adminAccount = AccountDAO.getInstance().get(1);
		// getSystemRole
		ProjectRole adminRole = adminAccount.getSystemRole(); // admin
		ProjectRole newAccountRole = account.getSystemRole();
		// assert
		assertTrue(adminRole != null);
		assertTrue(newAccountRole == null);
	}
	
	@Test
	public void testGetAccounts(){
		String userName = "TEST_USERNAME_";
		String nickName =  "TEST_NICKNAME_";
		String email = "TEST_EMAIL_";
		String password = "TEST_PASSWORD_";
		boolean enable = true;
		// create 3 account
		for(int i = 0; i < 3; i++){
			AccountObject account = new AccountObject(userName + (i + 1));
			account.setNickName(nickName + (i + 1)).setEmail(email + (i + 1))
			       .setPassword(password + (i + 1)).setEnable(enable);
			long accountId = AccountDAO.getInstance().create(account);
			assertNotSame(-1, accountId);
		}
		// getAccounts
		List<AccountObject> accountList = AccountDAO.getInstance().getAllAccounts();
		assertEquals(4, accountList.size());
	}
	
	@Test
	public void testConvertAccount() throws SQLException{
		// create 1 account
		String userName = "TEST_USERNAME_1";
		String nickName =  "TEST_NICKNAME_1";
		String email = "TEST_EMAIL_1";
		String password = "TEST_PASSWORD_1";
		boolean enable = true;
		// create
		AccountObject account = new AccountObject(userName);
		account.setNickName(nickName).setEmail(email)
		       .setPassword(password).setEnable(enable);
		long accountId = AccountDAO.getInstance().create(account);
		assertNotSame(-1, accountId);
		// Query
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AccountEnum.TABLE_NAME);
		valueSet.addEqualCondition(AccountEnum.ID, accountId);
		String query = valueSet.getSelectQuery();
		ResultSet resultSet = mControl.executeQuery(query);
		assertTrue(resultSet.first());
		
		// call convertAccount
		AccountObject convertAccount = AccountDAO.getInstance().convertAccount(resultSet);
		// assert
		assertEquals(userName, convertAccount.getUsername());
		assertEquals(nickName, convertAccount.getNickName());
		assertEquals(email, convertAccount.getEmail());
		assertEquals(getMd5(password), convertAccount.getPassword());
		assertTrue(enable && convertAccount.getEnable());
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
