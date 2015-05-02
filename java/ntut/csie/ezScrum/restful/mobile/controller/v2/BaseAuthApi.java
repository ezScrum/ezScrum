package ntut.csie.ezScrum.restful.mobile.controller.v2;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.codehaus.jettison.json.JSONObject;

public abstract class BaseAuthApi {

	private final static int METHOD_GET = 0, METHOD_GET_LIST = 1,
			METHOD_POST = 2, METHOD_PUT = 3, METHOD_DELETE = 4;
	private final static boolean IGNORE = true;

	@GET
	@Path("/{resourceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseOfGet(@PathParam("resourceId") long resourceId,
			@HeaderParam("user_id") long userId,
			@HeaderParam("public_token") String publicToken,
			@HeaderParam("disposable_token") String disposableToken,
			@HeaderParam("timestamp") long timestamp) {
		return doMethod(METHOD_GET, resourceId, userId, publicToken,
				disposableToken, timestamp, null);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseOfGetList(@HeaderParam("user_id") long userId,
			@HeaderParam("public_token") String publicToken,
			@HeaderParam("disposable_token") String disposableToken,
			@HeaderParam("timestamp") long timestamp) {
		return doMethod(METHOD_GET_LIST, null, userId, publicToken,
				disposableToken, timestamp, null);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseOfPost(@HeaderParam("user_id") long userId,
			@HeaderParam("public_token") String publicToken,
			@HeaderParam("disposable_token") String disposableToken,
			@HeaderParam("timestamp") long timestamp, String entity) {
		return doMethod(METHOD_POST, null, userId, publicToken,
				disposableToken, timestamp, entity);
	}

	@PUT
	@Path("/{resourceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseOfPost(@PathParam("resourceId") long resourceId,
			@HeaderParam("user_id") long userId,
			@HeaderParam("public_token") String publicToken,
			@HeaderParam("disposable_token") String disposableToken,
			@HeaderParam("timestamp") long timestamp, String entity) {
		return doMethod(METHOD_PUT, resourceId, userId, publicToken,
				disposableToken, timestamp, entity);
	}
	
	@DELETE
	@Path("/{resourceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseOfPost(@PathParam("resourceId") long resourceId,
			@HeaderParam("user_id") long userId,
			@HeaderParam("public_token") String publicToken,
			@HeaderParam("disposable_token") String disposableToken,
			@HeaderParam("timestamp") long timestamp) {
		return doMethod(METHOD_DELETE, resourceId, userId, publicToken,
				disposableToken, timestamp, null);
	}

	protected Response response(int statusCode, String entity) {
		ResponseBuilder resBuilder = Response.noContent();
		resBuilder.status(200).entity(entity);
		return resBuilder.build();
	}

	protected abstract Response get(long resourceId) throws Exception;

	protected abstract Response getList() throws Exception;

	protected abstract Response post(String entity) throws Exception;

	protected abstract Response put(long resourceId, String entity)
			throws Exception;

	protected abstract Response delete(long resourceId) throws Exception;

	private Response doMethod(int method, Long resourceId, long userId,
			String publicToken, String disposableToken, long timestamp,
			String entity) {
		try {
			Response response = response(404, new JSONObject().put("msg", "YO")
					.toString());
			if (IGNORE
					|| TokenValidator.verify(userId, publicToken,
							disposableToken, timestamp)) {
				switch (method) {
				case METHOD_GET:
					response = get(resourceId);
					break;
				case METHOD_GET_LIST:
					response = getList();
					break;
				case METHOD_POST:
					response = post(entity);
					break;
				case METHOD_PUT:
					response = put(resourceId, entity);
					break;
				case METHOD_DELETE:
					response = delete(resourceId);
					break;
				}
			} else {
				response = response(401,
						new JSONObject().put("msg", "Unauthorized").toString());
			}
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			return response(417, "{\"msg\":\"Error\"}");
		}
	}
}