package ntut.csie.ezScrum.restful.mobile.controller;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ntut.csie.ezScrum.restful.mobile.service.LoginWebService;
import ntut.csie.ezScrum.restful.mobile.support.InformationDecoder;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.jcis.account.core.LogonException;

import com.google.gson.Gson;

@Path("/user")
public class LoginWebServiceController {
	private LoginWebService service = null;
	
	/***
	 * 取得帳號是否存在資訊
	 * http://IP:8080/ezScrum/web-service/user/login?username={userName}&password={password}
	 * */
	@GET
	@Path("login")
	@Produces(MediaType.APPLICATION_JSON)
	public String login(@QueryParam("username") String username, @QueryParam("password") String password) {
		String response = "";
		try {
			InformationDecoder decodeInfo = new InformationDecoder();
			decodeInfo.decode(username, password);
			service = new LoginWebService(decodeInfo.getDecodeUsername(), decodeInfo.getDecodePwd());
			AccountObject theAccount = service.getAccount();
			Gson gson = new Gson();
			if (theAccount != null) {
				response = gson.toJson(Boolean.TRUE);
			} else {
				response = gson.toJson(Boolean.FALSE);
			}
		} catch (LogonException e) {
			System.out.println("class: LoginWebServiceController, method: login, exception: " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("class: LoginWebServiceController, method: login, exception: " + e.toString());
			e.printStackTrace();
		}
		return response;
	}
}
