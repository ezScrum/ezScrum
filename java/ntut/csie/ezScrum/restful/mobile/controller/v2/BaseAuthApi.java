package ntut.csie.ezScrum.restful.mobile.controller.v2;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public abstract class BaseAuthApi {
	
	private boolean validate() {
		return true;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{storyId}")
	public Response get(@PathParam("storyId") long storyId) {
		JSONObject json = new JSONObject();
		try {
			json.put("msg", "OK");
			json.put("storyId", storyId);
		} catch (JSONException e) {
		}
		
		ResponseBuilder resBuilder = Response.noContent();
		resBuilder.status(200).entity(json.toString());
		return resBuilder.build();
	}
}