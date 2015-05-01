package ntut.csie.ezScrum.restful.mobile.controller.v2;

import java.security.MessageDigest;

import ntut.csie.ezScrum.web.dataObject.TokenObject;

public class TokenValidator {

	public static boolean verify(long accountId, String clientPublicToken,
			String clientDisposableToken, long timestamp) {
		boolean isValidate = false;
		TokenObject token = TokenObject.getByAccountId(accountId);
		String disposableToken;
		try {
			disposableToken = genDisposable(token.getPublicToken(),
					token.getPrivateToken(), timestamp);
		} catch (Exception e) {
			return false;
		}
		if (clientPublicToken.equals(token.getPublicToken())
				&& clientDisposableToken.equals(disposableToken)) {
			isValidate = true;
		}
		return isValidate;
	}

	private static String genDisposable(String publicToken,
			String privateToken, long timestamp) throws Exception {
		String plainCode = publicToken + privateToken + timestamp;
		byte[] bytesOfMessage = plainCode.getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(bytesOfMessage);
		return new String(digest);
	}
}
