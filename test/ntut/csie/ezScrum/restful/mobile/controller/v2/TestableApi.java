package ntut.csie.ezScrum.restful.mobile.controller.v2;

import ntut.csie.ezScrum.web.dataObject.TokenObject;

import org.apache.http.HttpRequest;

public class TestableApi {
	
	protected void setHeaders(HttpRequest request, long accountId, String platformType) {
		TokenObject token = TokenObject.getByPlatform(accountId, platformType);
		long currentTime = System.currentTimeMillis();
		request.setHeader("user_id", String.valueOf(accountId));
		request.setHeader("public_token", token.getPublicToken());
		request.setHeader("disposable_token", token.getDisposableToken(currentTime));
		request.setHeader("timestamp", String.valueOf(currentTime));
	}
}