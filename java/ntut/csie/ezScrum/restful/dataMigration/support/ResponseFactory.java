package ntut.csie.ezScrum.restful.dataMigration.support;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;

public class ResponseFactory {
	public static Response getResponse(Status status, String message, String content) {
		JSONObject responseJSON = new JSONObject();
		try {
			responseJSON.put(ResponseJSONEnum.JSON_KEY_MESSAGE, message);
			responseJSON.put(ResponseJSONEnum.JSON_KEY_CONTENT, content);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String entity = responseJSON.toString();
		return Response.status(status).entity(entity).build();
	}
}
