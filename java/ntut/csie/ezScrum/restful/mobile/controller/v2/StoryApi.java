package ntut.csie.ezScrum.restful.mobile.controller.v2;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Path("/stories")
public class StoryApi extends BaseAuthApi {
	
	@Override
	protected Response get(long resourceId) {
		JSONObject json = new JSONObject();
		try {
			json.put("msg", "OK");
			json.put("storyId", resourceId);
		} catch (JSONException e) {
		}
		return response(200, json.toString());
	}

	@Override
	protected Response getList() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Response post(String entity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Response put(long resourceId, String entity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Response delete(long resourceId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
