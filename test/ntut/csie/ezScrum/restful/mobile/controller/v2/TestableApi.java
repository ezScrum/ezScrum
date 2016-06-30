package ntut.csie.ezScrum.restful.mobile.controller.v2;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import ntut.csie.ezScrum.web.dataObject.TokenObject;

public class TestableApi {
	public static MultivaluedMap<String, Object> getHeaders(long accountId, String platformType) {
		TokenObject token = TokenObject.getByPlatform(accountId, platformType);
		long currentTime = System.currentTimeMillis();
		MultivaluedMap<String, Object> headersMap = new MultivaluedHashMap<String, Object>();
		headersMap.add("user_id", String.valueOf(accountId));
		headersMap.add("public_token", token.getPublicToken());
		headersMap.add("disposable_token", token.getDisposableToken(currentTime));
		headersMap.add("timestamp", String.valueOf(currentTime));
		return headersMap;
	}
}