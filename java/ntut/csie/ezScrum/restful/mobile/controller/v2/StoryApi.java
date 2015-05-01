package ntut.csie.ezScrum.restful.mobile.controller.v2;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Path("/stories")
public class StoryApi extends BaseAuthApi {
	
	@Override
	protected Response doGet(long resourceId) {
		JSONObject json = new JSONObject();
		try {
			json.put("msg", "OK");
			json.put("storyId", resourceId);
		} catch (JSONException e) {
		}
		return response(200, json.toString());
	}

}
