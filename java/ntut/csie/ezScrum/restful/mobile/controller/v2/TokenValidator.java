package ntut.csie.ezScrum.restful.mobile.controller.v2;

import java.security.MessageDigest;

import ntut.csie.ezScrum.web.dataObject.TokenObject;

public class TokenValidator {
	
	private final static boolean CHECK_TIME = true;
	private final static boolean CHECK_PUBLIC_TOKEN = true;
	private final static boolean CHECK_DISPOSABLE_TOKEN = true;
	private final static int EXPIRED_TIME = 60;

	public static boolean verify(long accountId, String clientPublicToken,
			String clientDisposableToken, long timestamp) {
		TokenObject token = TokenObject.getByAccountId(accountId);
		String disposableToken;
		try {
			disposableToken = genDisposable(token.getPublicToken(),
					token.getPrivateToken(), timestamp);
		} catch (Exception e) {
			return false;
		}
		
		if (CHECK_TIME) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - timestamp > EXPIRED_TIME) {
				return false;
			}
		}
		
		if (CHECK_PUBLIC_TOKEN) {
			if (!clientPublicToken.equals(token.getPublicToken())) {
				return false;
			}
		}
		
		if (CHECK_DISPOSABLE_TOKEN) {
			if (!clientDisposableToken.equals(disposableToken)) {
				return false;
			}
		}
		
		return true;
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
