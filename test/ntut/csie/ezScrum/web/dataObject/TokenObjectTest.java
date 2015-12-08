package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import ntut.csie.ezScrum.dao.TokenDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TokenObjectTest {
	private Configuration mConfig = null;
	private CreateAccount mCA;
	private long mAccountId;
	private String mPlatformType;
	
	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCA = new CreateAccount(1);
		mCA.exe();
		
		mAccountId = mCA.getAccountList().get(0).getId();
		mPlatformType = "windows";
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
		
		mConfig = null;
		mCA = null;
	}
	
	@Test
	public void testSave_create() {
		TokenObject token = TokenDAO.getInstance().get(1);
		assertNull(token);
		
		token = new TokenObject(mAccountId, mPlatformType);
		token.save();
		
		TokenObject newToken = TokenDAO.getInstance().get(1);
		assertNotNull(newToken);
		assertEquals(mAccountId, newToken.getAccountId());
		assertEquals(newToken.getPublicToken(), token.getPublicToken());
		assertEquals(newToken.getPrivateToken(), token.getPrivateToken());
	}
	
	@Test
	public void testSave_update() {
		TokenObject token = TokenDAO.getInstance().get(1);
		assertNull(token);
		
		token = new TokenObject(mAccountId, mPlatformType);
		token.save();
		
		TokenObject newToken = TokenDAO.getInstance().get(1);
		assertNotNull(newToken);
		assertEquals(mAccountId, newToken.getAccountId());
		assertEquals(newToken.getPublicToken(), token.getPublicToken());
		assertEquals(newToken.getPrivateToken(), token.getPrivateToken());
		
		token.rehash();
		newToken = TokenDAO.getInstance().get(1);
		assertNotNull(newToken);
		assertEquals(mAccountId, newToken.getAccountId());
		assertEquals(newToken.getPublicToken(), token.getPublicToken());
		assertEquals(newToken.getPrivateToken(), token.getPrivateToken());
	}
	
	@Test
	public void testRehash() {
		TokenObject token = TokenDAO.getInstance().get(1);
		assertNull(token);
		
		token = new TokenObject(mAccountId, mPlatformType);
		String oldPublicToken = token.getPublicToken();
		String oldPrivateToken = token.getPrivateToken();
		
		token.rehash();
		assertNotSame(token.getPublicToken(), oldPublicToken);
		assertNotSame(token.getPrivateToken(), oldPrivateToken);
	}
}
