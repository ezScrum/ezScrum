package ntut.csie.ezScrum.restful.mobile.controller.v2;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.TokenObject;
import ntut.csie.ezScrum.web.databaseEnum.TokenEnum;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Path("/api")
public class LoginApi {
	
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public String login(String loginJson) throws JSONException {
		// get JSONObject
		JSONObject jsonEntity = new JSONObject(loginJson);
		// confirm User Account
		AccountObject account = AccountObject.confirmAccount(jsonEntity.getString(TokenEnum.USERNAME), jsonEntity.getString(TokenEnum.PASSWORD));
		
		if (account == null) {
			return "";
		}
		
		// create Token Object
		TokenObject token = new TokenObject(account.getId(), jsonEntity.getString(TokenEnum.PLATFORM_TYPE));
		token.save();
		
		// create Response JSONObject
		JSONObject respEntity = new JSONObject();
		respEntity.put(TokenEnum.PUBLIC_TOKEN, token.getPublicToken())
		          .put(TokenEnum.PRIVATE_TOKEN, token.getPrivateToken())
		          .put(TokenEnum.SERVER_TIME, System.currentTimeMillis());
		return respEntity.toString();
	}
	
	@GET
	@Path("/server_time")
	@Produces(MediaType.APPLICATION_JSON)
	public String getServerTime() throws JSONException {
		JSONObject respEntity = new JSONObject();
		respEntity.put(TokenEnum.SERVER_TIME, System.currentTimeMillis());
		return respEntity.toString();
	}
}
