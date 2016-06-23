package ntut.csie.ezScrum.restful.mobile.controller.v2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.MessageDigest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.web.dataObject.TokenObject;

public class TokenValidatorTest {
	private Configuration mConfig = null;
	private CreateAccount mCA;
	private long mAccountId;
	private String mPlatform;
	private TokenObject mToken;
	
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
		mPlatform = "andorid";
		
		mToken = new TokenObject(mAccountId, mPlatform);
		mToken.save();
	}

	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
		
		mConfig = null;
		mCA = null;
	}
	
	@Test
	public void testVerify() throws Exception {
		String publicToken = mToken.getPublicToken();
		String privateToken = mToken.getPrivateToken();
		long timestamp = System.currentTimeMillis();
		String disposableToken = genDisposable(publicToken, privateToken, timestamp);
		
		boolean isValidate = TokenValidator.verify(mAccountId, publicToken, disposableToken, timestamp);
		assertTrue(isValidate);
		
		isValidate = TokenValidator.verify(mAccountId, "test", "tttt", timestamp);
		assertFalse(isValidate);
	}
	
	private static String genDisposable(String publicToken,
			String privateToken, long timestamp) throws Exception {
		String plainCode = publicToken + privateToken + timestamp;
		byte[] bytesOfMessage = plainCode.getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] digest = md.digest(bytesOfMessage);
		return new String(digest);
	}
}
