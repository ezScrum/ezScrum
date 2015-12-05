package ntut.csie.ezScrum.restful.dataMigration.support;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;

public class ResponseFactoryTest {
	@Test
	public void testGetResponse_WithNullMessageAndNullContent() throws JSONException {
		Response response = ResponseFactory.getResponse(Response.Status.OK, null, null);
		String responseJSONString = response.readEntity(String.class);
		JSONObject responseJSON = new JSONObject(responseJSONString);
		String message = responseJSON.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);
		JSONObject contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals("", message);
		assertEquals(new JSONObject().toString(), contentJSON.toString());
	}
	
	@Test
	public void testGetResponse_WithEmptyMessageAndEmptyContent() throws JSONException {
		Response response = ResponseFactory.getResponse(Response.Status.OK, "", "");
		String responseJSONString = response.readEntity(String.class);
		JSONObject responseJSON = new JSONObject(responseJSONString);
		String message = responseJSON.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);
		JSONObject contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals("", message);
		assertEquals(new JSONObject().toString(), contentJSON.toString());
	}
	
	@Test
	public void testGetResponse() throws JSONException {
		JSONObject expetedContentJSON = new JSONObject();
		expetedContentJSON.put("id", 1);
		expetedContentJSON.put("name", "project01");
		Response response = ResponseFactory.getResponse(Response.Status.OK, "success", expetedContentJSON.toString());
		String responseJSONString = response.readEntity(String.class);
		JSONObject responseJSON = new JSONObject(responseJSONString);
		String message = responseJSON.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);
		JSONObject contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals("", message);
		assertEquals(expetedContentJSON.toString(), contentJSON.toString());
	}
}
