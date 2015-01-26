package ntut.csie.ezScrum.dao;

import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.databasEnum.AccountEnum;
import ntut.csie.ezScrum.web.databasEnum.ProjectRoleEnum;
import junit.framework.TestCase;

public class AccountDAOTest extends TestCase{
	private MySQLControl mControl = null;
	private Configuration mConfig;

	public AccountDAOTest(String testMethod) {
		super(testMethod);
	}

	@Override
    protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mControl = new MySQLControl(mConfig);
		mControl.connection();
		super.setUp();
    }

	@Override
    protected void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		// ============= release ==============
		ini = null;
		mConfig = null;
		mControl = null;
		super.tearDown();
    }
	
	public void testCreate() throws SQLException{
		String accountName = "TEST_ACCOUNTNAME_";
		String name =  "TEST_NAME_";
		String email = "TEST_EMAIL_";
		String password = "TEST_PASSWORD_";
		boolean enable = true;
		// create 3 account
		for(int i = 0; i < 3; i++){
			AccountObject account = new AccountObject(accountName + (i + 1), name + (i + 1), password  + (i + 1), email + (i + 1), enable);
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
				AccountObject account = AccountDAO.getInstance().convert(resultSet);
				assertEquals(accountName + (i + 1), account.getAccount());
				assertEquals(name + (i + 1), account.getName());
				assertEquals(email + (i + 1), account.getEmail());
				assertEquals(getMd5(password + (i + 1)), account.getPassword());
				assertTrue(enable == account.getEnable());
			}
		}
	}
	
	public void testGet(){
		String accountName = "TEST_ACCOUNTNAME_";
		String name =  "TEST_NAME_";
		String email = "TEST_EMAIL_";
		String password = "TEST_PASSWORD_";
		boolean enable = true;
		// create 3 account
		for(int i = 0; i < 3; i++){
			AccountObject account = new AccountObject(accountName + (i + 1), name + (i + 1), password  + (i + 1), email + (i + 1), enable);
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
			assertEquals(accountName + (i + 1), accountList.get(i).getAccount());
			assertEquals(name + (i + 1), accountList.get(i).getName());
			assertEquals(email + (i + 1), accountList.get(i).getEmail());
			assertEquals(getMd5(password + (i + 1)), accountList.get(i).getPassword());
			assertEquals(enable, accountList.get(i).getEnable());
		}
	}
	
	public void testUpdate(){
		String accountName = "TEST_ACCOUNTNAME_1";
		String name =  "TEST_NAME_1";
		String email = "TEST_EMAIL_1";
		String password = "TEST_PASSWORD_1";
		boolean enable = true;
		// create
		AccountObject account = new AccountObject(accountName, name, password, email, enable);
		long accountId = AccountDAO.getInstance().create(account);
		assertNotSame(-1, accountId);
		// update 
		account = AccountDAO.getInstance().get(accountId);
		account.setName("hello");
		account.setPassword("12312113134546");
		account.setEmail("hello@gmail.com");
		account.setEnable(true);
		boolean updateResult = AccountDAO.getInstance().update(account);
		assertTrue(updateResult);
		// get 
		AccountObject newAccount = AccountDAO.getInstance().get(accountId);
		
		// assert
		assertEquals(account.getName(), newAccount.getName());
		assertEquals(getMd5(account.getPassword()), newAccount.getPassword());
		assertEquals(account.getEmail(), newAccount.getEmail());
		assertEquals(account.getEnable(), newAccount.getEnable());
	}
	
	public void testDelete() throws SQLException{
		String accountName = "TEST_ACCOUNTNAME_";
		String name =  "TEST_NAME_";
		String email = "TEST_EMAIL_";
		String password = "TEST_PASSWORD_";
		boolean enable = true;
		// create 3 account
		for(int i = 0; i < 3; i++){
			AccountObject account = new AccountObject(accountName + (i + 1), name + (i + 1), password  + (i + 1), email + (i + 1), enable);
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
			accountList.add(AccountDAO.getInstance().convert(resultSet));
		}
		assertEquals(2, accountList.size());
	}
	
	public void testGetSystemRole(){
		String accountName = "TEST_ACCOUNTNAME_1";
		String name =  "TEST_NAME_1";
		String email = "TEST_EMAIL_1";
		String password = "TEST_PASSWORD_1";
		boolean enable = true;
		// create
		AccountObject account = new AccountObject(accountName, name, password, email, enable);
		long accountId = AccountDAO.getInstance().create(account);
		assertNotSame(-1, accountId);
		// assert
		ProjectRole adminRole = AccountObject.getSystemRole(1); // admin
		ProjectRole newAccountRole = AccountObject.getSystemRole(account.getId());
		assertTrue(adminRole != null);
		assertTrue(newAccountRole == null);
	}
	
	public void testGetAccounts(){
		String accountName = "TEST_ACCOUNTNAME_";
		String name =  "TEST_NAME_";
		String email = "TEST_EMAIL_";
		String password = "TEST_PASSWORD_";
		boolean enable = true;
		// create 3 account
		for(int i = 0; i < 3; i++){
			AccountObject account = new AccountObject(accountName + (i + 1), name + (i + 1), password  + (i + 1), email + (i + 1), enable);
			long accountId = AccountDAO.getInstance().create(account);
			assertNotSame(-1, accountId);
		}
		// getAccounts
		List<AccountObject> accountList = AccountDAO.getInstance().getAccounts();
		assertEquals(4, accountList.size());
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
