package ntut.csie.ezScrum.restful.mobile.controller.v2;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import ntut.csie.ezScrum.web.dataObject.AccountObject;

import org.codehaus.jettison.json.JSONObject;

public abstract class BaseAuthApi {

	private final static int METHOD_GET = 0, METHOD_GET_LIST = 1,
			METHOD_POST = 2, METHOD_PUT = 3, METHOD_DELETE = 4;
	private final static boolean IGNORE_VERIFY = true; // set true for testing easily

	private AccountObject mUser;

	@GET
	@Path("/{resourceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseOfGet(@PathParam("resourceId") long resourceId,
			@HeaderParam("user_id") long userId,
			@HeaderParam("public_token") String publicToken,
			@HeaderParam("disposable_token") String disposableToken,
			@HeaderParam("adjusted_time") long adjuestedTime, @Context UriInfo uriInfo) {
		return doMethod(METHOD_GET, resourceId, userId, publicToken,
				disposableToken, adjuestedTime, null, uriInfo);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseOfGetList(@HeaderParam("user_id") long userId,
			@HeaderParam("public_token") String publicToken,
			@HeaderParam("disposable_token") String disposableToken,
			@HeaderParam("adjusted_time") long adjuestedTime, @Context UriInfo uriInfo) {
		return doMethod(METHOD_GET_LIST, null, userId, publicToken,
				disposableToken, adjuestedTime, null, uriInfo);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseOfPost(@HeaderParam("user_id") long userId,
			@HeaderParam("public_token") String publicToken,
			@HeaderParam("disposable_token") String disposableToken,
			@HeaderParam("adjusted_time") long adjuestedTime, String entity) {
		return doMethod(METHOD_POST, null, userId, publicToken,
				disposableToken, adjuestedTime, entity, null);
	}

	@PUT
	@Path("/{resourceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseOfPut(@PathParam("resourceId") long resourceId,
			@HeaderParam("user_id") long userId,
			@HeaderParam("public_token") String publicToken,
			@HeaderParam("disposable_token") String disposableToken,
			@HeaderParam("adjusted_time") long adjuestedTime, String entity) {
		return doMethod(METHOD_PUT, resourceId, userId, publicToken,
				disposableToken, adjuestedTime, entity, null);
	}

	@DELETE
	@Path("/{resourceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseOfDelete(@PathParam("resourceId") long resourceId,
			@HeaderParam("user_id") long userId,
			@HeaderParam("public_token") String publicToken,
			@HeaderParam("disposable_token") String disposableToken,
			@HeaderParam("adjusted_time") long adjuestedTime, @Context UriInfo uriInfo) {
		return doMethod(METHOD_DELETE, resourceId, userId, publicToken,
				disposableToken, adjuestedTime, null, uriInfo);
	}

	protected Response response(int statusCode, String entity) {
		ResponseBuilder resBuilder = Response.noContent();
		resBuilder.status(200).entity(entity);
		return resBuilder.build();
	}

	protected Response responseOK() {
		return response(200, "{\"msg\":\"ok\"}");
	}

	protected AccountObject getUser() {
		return mUser;
	}

	protected abstract Response get(long resourceId, UriInfo uriInfo) throws Exception;
	protected abstract Response getList(UriInfo uriInfo) throws Exception;
	protected abstract Response post(String entity) throws Exception;
	protected abstract Response put(long resourceId, String entity)
			throws Exception;
	protected abstract Response delete(long resourceId, UriInfo uriInfo) throws Exception;
	protected abstract boolean permissionCheck(AccountObject user, UriInfo uriInfo);
	protected abstract boolean ownerCheck(AccountObject user, UriInfo uriInfo);

	private Response doMethod(int method, Long resourceId, long userId,
			String publicToken, String disposableToken, long adjustedTime,
			String entity, UriInfo uriInfo) {

		try {
			Response response = response(404, "{\"msg\":\"Not Found\"}");
			
			if (IGNORE_VERIFY
					|| TokenValidator.verify(userId, publicToken,
							disposableToken, adjustedTime)) {
				mUser = AccountObject.get(userId);
				
				// 不是 admi，要做 Project 權限及所有權的檢查
				if (!mUser.getSystemRole().getScrumRole().isAdmin()) {
					if (!permissionCheck(mUser, uriInfo)) {
						return response(401, "{\"msg\":\"Unauthorized\"}");
					}
					
					if (!ownerCheck(mUser, uriInfo)) {
						return response(403, "{\"msg\":\"Forbidden\"}");
					}
				}
				
				switch (method) {
				case METHOD_GET:
					response = get(resourceId, uriInfo);
					break;
				case METHOD_GET_LIST:
					response = getList(uriInfo);
					break;
				case METHOD_POST:
					response = post(entity);
					break;
				case METHOD_PUT:
					response = put(resourceId, entity);
					break;
				case METHOD_DELETE:
					response = delete(resourceId, uriInfo);
					break;
				}
			} else {
				response = response(406,
						new JSONObject().put("msg", "Token is no acceptable").toString());
			}
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			return response(417, "{\"msg\":\"" + e.getMessage() + "\"}");
		}
	}
}