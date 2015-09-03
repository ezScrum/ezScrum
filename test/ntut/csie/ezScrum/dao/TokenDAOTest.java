package ntut.csie.ezScrum.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.TokenObject;
import ntut.csie.ezScrum.web.databaseEnum.TokenEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TokenDAOTest {
	private MySQLControl mControl = null;
	private Configuration mConfig;
	private CreateAccount mCA;
	private long mAccountId;
	private String mPlatformType;
	
	@Before
	public void setUp() {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mCA = new CreateAccount(1);
		mCA.exe();
		
		mAccountId = mCA.getAccountList().get(0).getId();
		mPlatformType = "andorid";

		mControl = new MySQLControl(mConfig);
		mControl.connect();
	}
	
	@After
	public void tearDown() {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mConfig = null;
		mControl = null;
		mCA = null;
	}
	
	@Test
	public void testCreate() throws SQLException {
		TokenObject token = new TokenObject(mAccountId, mPlatformType);
		
		long id = TokenDAO.getInstance().create(token);
		
		assertEquals(1, id);
		
		IQueryValueSet valueSet = new MySQLQuerySet(); 
		valueSet.addTableName(TokenEnum.TABLE_NAME);
		valueSet.addEqualCondition(TokenEnum.ID, id);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		boolean exist = false;
		while (result.next()) {
			exist = true;
		}
		assertEquals(true, exist);
	}
	
	@Test
	public void testGet() {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TokenEnum.TABLE_NAME);
		valueSet.addInsertValue(TokenEnum.ACCOUNT_ID, mAccountId);
		valueSet.addInsertValue(TokenEnum.PUBLIC_TOKEN, "PUBLIC_TOKEN");
		valueSet.addInsertValue(TokenEnum.PRIVATE_TOKEN, "PRIVATE_TOKEN");
		valueSet.addInsertValue(TokenEnum.PLATFORM_TYPE, "PLATFORM_TYPE");
		valueSet.addInsertValue(TokenEnum.CREATE_TIME, String.valueOf(System.currentTimeMillis()));
		valueSet.addInsertValue(TokenEnum.UPDATE_TIME, String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);
		
		TokenObject token = TokenDAO.getInstance().get(id);
		
		assertNotNull(token);
		assertEquals(mAccountId, token.getAccountId());
		assertEquals("PUBLIC_TOKEN", token.getPublicToken());
		assertEquals("PRIVATE_TOKEN", token.getPrivateToken());
		assertEquals("PLATFORM_TYPE", token.getPlatformType());
	}
	
	@Test
	public void testGetByAccountId() {
		IQueryValueSet valueSet = new MySQLQuerySet(); 
		valueSet.addTableName(TokenEnum.TABLE_NAME);
		valueSet.addInsertValue(TokenEnum.ACCOUNT_ID, mAccountId);
		valueSet.addInsertValue(TokenEnum.PUBLIC_TOKEN, "PUBLIC_TOKEN");
		valueSet.addInsertValue(TokenEnum.PRIVATE_TOKEN, "PRIVATE_TOKEN");
		valueSet.addInsertValue(TokenEnum.PLATFORM_TYPE, "PLATFORM_TYPE");
		valueSet.addInsertValue(TokenEnum.CREATE_TIME, String.valueOf(System.currentTimeMillis()));
		valueSet.addInsertValue(TokenEnum.UPDATE_TIME, String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);
		
		ArrayList<TokenObject> tokens = TokenDAO.getInstance().getByAccountId(mAccountId);
		
		assertEquals(1, tokens.size());
		assertEquals(id, tokens.get(0).getId());
		assertEquals(mAccountId, tokens.get(0).getAccountId());
		assertEquals("PUBLIC_TOKEN", tokens.get(0).getPublicToken());
		assertEquals("PRIVATE_TOKEN", tokens.get(0).getPrivateToken());
		assertEquals("PLATFORM_TYPE", tokens.get(0).getPlatformType());
	}
	
	@Test
	public void testUpdate() {
		String PUBLIC_TOKEN = "PUBLIC_TOKEN";
		String PRIVATE_TOKEN = "PRIVATE_TOKEN";
		String PLATFORM_TYPE = "PLATFORM_TYPE";
		long CREATE_TIME = System.currentTimeMillis();
		long UPDATE_TIME = System.currentTimeMillis();
		
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TokenEnum.TABLE_NAME);
		valueSet.addInsertValue(TokenEnum.ACCOUNT_ID, mAccountId);
		valueSet.addInsertValue(TokenEnum.PUBLIC_TOKEN, PUBLIC_TOKEN);
		valueSet.addInsertValue(TokenEnum.PRIVATE_TOKEN, PRIVATE_TOKEN);
		valueSet.addInsertValue(TokenEnum.PLATFORM_TYPE, PLATFORM_TYPE);
		valueSet.addInsertValue(TokenEnum.CREATE_TIME, String.valueOf(CREATE_TIME));
		valueSet.addInsertValue(TokenEnum.UPDATE_TIME, String.valueOf(UPDATE_TIME));
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);
		
		TokenObject token = new TokenObject(id, mAccountId, PUBLIC_TOKEN, PRIVATE_TOKEN,
		        PLATFORM_TYPE, CREATE_TIME, UPDATE_TIME);
		token.rehash();
		
		boolean success = TokenDAO.getInstance().update(token);
		
		assertTrue(success);
		assertEquals(mAccountId, token.getAccountId());
		assertNotSame("PUBLIC_TOKEN", token.getPublicToken());
		assertNotSame("PRIVATE_TOKEN", token.getPrivateToken());
		assertEquals("PLATFORM_TYPE", token.getPlatformType());
		assertEquals(60, token.getPublicToken().length());
		assertEquals(60, token.getPrivateToken().length());
	}
	
	@Test
	public void testDelete() {
		String PUBLIC_TOKEN = "PUBLIC_TOKEN";
		String PRIVATE_TOKEN = "PRIVATE_TOKEN";
		String PLATFORM_TYPE = "PLATFORM_TYPE";
		
		IQueryValueSet valueSet = new MySQLQuerySet(); 
		valueSet.addTableName(TokenEnum.TABLE_NAME);
		valueSet.addInsertValue(TokenEnum.ACCOUNT_ID, mAccountId);
		valueSet.addInsertValue(TokenEnum.PUBLIC_TOKEN, PUBLIC_TOKEN);
		valueSet.addInsertValue(TokenEnum.PRIVATE_TOKEN, PRIVATE_TOKEN);
		valueSet.addInsertValue(TokenEnum.PLATFORM_TYPE, PLATFORM_TYPE);
		valueSet.addInsertValue(TokenEnum.CREATE_TIME, String.valueOf(System.currentTimeMillis()));
		valueSet.addInsertValue(TokenEnum.UPDATE_TIME, String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);
		
		TokenDAO.getInstance().delete(id);
		
		TokenObject token = TokenDAO.getInstance().get(id);
		assertNull(token);
	}
}
