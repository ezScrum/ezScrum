package ntut.csie.ezScrum.dao;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.TokenObject;
import ntut.csie.ezScrum.web.databasEnum.TokenEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TokenDAOTest {
	private MySQLControl mControl = null;
	private Configuration mConfig;
	private CreateAccount mCA;
	private long mAccountId;
	
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
		TokenObject token = new TokenObject(mAccountId);
		token.rehash();
		
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
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);
		
		TokenObject token = TokenDAO.getInstance().get(id);
		
		assertNotNull(token);
		assertEquals(mAccountId, token.getAccountId());
		assertEquals("PUBLIC_TOKEN", token.getPublicToken());
		assertEquals("PRIVATE_TOKEN", token.getPrivateToken());
	}
	
	@Test
	public void testGetByAccountId() {
		IQueryValueSet valueSet = new MySQLQuerySet(); 
		valueSet.addTableName(TokenEnum.TABLE_NAME);
		valueSet.addInsertValue(TokenEnum.ACCOUNT_ID, mAccountId);
		valueSet.addInsertValue(TokenEnum.PUBLIC_TOKEN, "PUBLIC_TOKEN");
		valueSet.addInsertValue(TokenEnum.PRIVATE_TOKEN, "PRIVATE_TOKEN");
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);
		
		TokenObject token = TokenDAO.getInstance().getByAccountId(mAccountId);
		
		assertNotNull(token);
		assertEquals(id, token.getId());
		assertEquals(mAccountId, token.getAccountId());
		assertEquals("PUBLIC_TOKEN", token.getPublicToken());
		assertEquals("PRIVATE_TOKEN", token.getPrivateToken());
	}
	
	@Test
	public void testUpdate() {
		String PUBLIC_TOKEN = "PUBLIC_TOKEN";
		String PRIVATE_TOKEN = "PRIVATE_TOKEN";
		
		IQueryValueSet valueSet = new MySQLQuerySet(); 
		valueSet.addTableName(TokenEnum.TABLE_NAME);
		valueSet.addInsertValue(TokenEnum.ACCOUNT_ID, mAccountId);
		valueSet.addInsertValue(TokenEnum.PUBLIC_TOKEN, PUBLIC_TOKEN);
		valueSet.addInsertValue(TokenEnum.PRIVATE_TOKEN, PRIVATE_TOKEN);
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);
		
		TokenObject token = new TokenObject(id, mAccountId, PUBLIC_TOKEN, PRIVATE_TOKEN);
		token.rehash();
		
		boolean success = TokenDAO.getInstance().update(token);
		
		assertTrue(success);
		assertEquals(mAccountId, token.getAccountId());
		assertNotSame("PUBLIC_TOKEN", token.getPublicToken());
		assertNotSame("PRIVATE_TOKEN", token.getPrivateToken());
		assertEquals(60, token.getPublicToken().length());
		assertEquals(60, token.getPrivateToken().length());
	}
	
	@Test
	public void testDelete() {
		String PUBLIC_TOKEN = "PUBLIC_TOKEN";
		String PRIVATE_TOKEN = "PRIVATE_TOKEN";
		
		IQueryValueSet valueSet = new MySQLQuerySet(); 
		valueSet.addTableName(TokenEnum.TABLE_NAME);
		valueSet.addInsertValue(TokenEnum.ACCOUNT_ID, mAccountId);
		valueSet.addInsertValue(TokenEnum.PUBLIC_TOKEN, PUBLIC_TOKEN);
		valueSet.addInsertValue(TokenEnum.PRIVATE_TOKEN, PRIVATE_TOKEN);
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);
		
		TokenDAO.getInstance().delete(id);
		
		TokenObject token = TokenDAO.getInstance().get(id);
		assertNull(token);
	}
}
