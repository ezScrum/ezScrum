package ntut.csie.ezScrum.restful.mobile.controller.v2;

import java.security.MessageDigest;

import ntut.csie.ezScrum.web.dataObject.TokenObject;

public class TokenValidator {
	private final static boolean CHECK_TIME = true;
	private final static boolean CHECK_PUBLIC_TOKEN = true;
	private final static boolean CHECK_DISPOSABLE_TOKEN = true;
	private final static int EXPIRED_TIME = 60;
	
	public static boolean verify(long accountId, String clientPublicToken,
	        String clientDisposableToken, long timestamp) throws Exception {
		TokenObject token = TokenObject.get(accountId, clientPublicToken);
		String disposableToken;
		try {
			disposableToken = genDisposable(token.getPublicToken(), token.getPrivateToken(), timestamp);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		if (CHECK_TIME) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - timestamp > EXPIRED_TIME) {
				throw new Exception("Request is expired");
			}
		}
		
		if (CHECK_PUBLIC_TOKEN) {
			if (!clientPublicToken.equals(token.getPublicToken())) {
				throw new Exception("Public token error");
			}
		}
		
		if (CHECK_DISPOSABLE_TOKEN) {
			if (!clientDisposableToken.equals(disposableToken)) {
				throw new Exception("Token is not acceptable");
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
