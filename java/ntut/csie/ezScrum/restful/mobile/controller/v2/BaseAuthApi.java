package ntut.csie.ezScrum.restful.mobile.controller.v2;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.codehaus.jettison.json.JSONObject;

public abstract class BaseAuthApi {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{resourceId}")
	public Response get(@PathParam("resourceId") long resourceId,
			@HeaderParam("user_id") long userId,
			@HeaderParam("public_token") String publicToken,
			@HeaderParam("disposable_token") String disposableToken,
			@HeaderParam("timestamp") long timestamp) {
		try {
			Response response = response(404, new JSONObject().put("msg", "YO").toString());
			if (TokenValidator.verify(userId, publicToken, disposableToken, timestamp)) {
				response = doGet(resourceId);			
			} else {
				response = response(401, new JSONObject().put("msg", "Unauthorized").toString());
			}
			return response;			
		} catch (Exception e) {
			return response(417, "{\"msg\":\"Error\"}");
		}
	}

	protected abstract Response doGet(long resourceId) throws Exception;

	protected Response response(int statusCode, String entity) {
		ResponseBuilder resBuilder = Response.noContent();
		resBuilder.status(200).entity(entity);
		return resBuilder.build();
	}
}