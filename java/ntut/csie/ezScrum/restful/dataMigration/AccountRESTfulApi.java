package ntut.csie.ezScrum.restful.dataMigration;

import java.io.IOException;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ntut.csie.ezScru.web.microservice.AccountRESTClient;
import ntut.csie.ezScru.web.microservice.AccountRESTClientProxy;
import ntut.csie.ezScru.web.microservice.IAccountController;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.security.SecurityModule;
import ntut.csie.ezScrum.restful.dataMigration.support.JSONChecker;
import ntut.csie.ezScrum.restful.dataMigration.support.JSONDecoder;
import ntut.csie.ezScrum.restful.dataMigration.support.ResponseFactory;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

@Path("accounts")
public class AccountRESTfulApi {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAccount(@HeaderParam(SecurityModule.USERNAME_HEADER) String username,
					              @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
					              String entity
					              ) {
		if (!SecurityModule.isAccountValid(username, password)) {
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		// Error Checking
		String message = JSONChecker.checkAccountJSON(entity);
		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		// Create Account
		AccountObject account = JSONDecoder.toAccount(entity);
		// Check for existing account
		AccountRESTClient clientService = new AccountRESTClient();
		try {
			String orignName = SecurityModule.username(username, password);
			String token = clientService.Login(orignName, password);
			IAccountController accountService = new AccountRESTClientProxy(token);
			AccountObject existedAccount = accountService.getAccount(account.getUsername());
			if (existedAccount != null) {
				return ResponseFactory.getResponse(Response.Status.CONFLICT, ResponseJSONEnum.ERROR_RESOURCE_EXIST_MESSAGE, "");
			}
			AccountInfo accountInfo = new AccountInfo();
			accountInfo.username = account.getUsername();
			accountInfo.nickName = account.getNickName();
			accountInfo.email = account.getEmail();
			accountInfo.password = account.getUsername();
			accountInfo.enable = account.getEnable();
			AccountObject result = accountService.createAccount(accountInfo);
			
			return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, result.toString());
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseFactory.getResponse(Response.Status.CONFLICT, ResponseJSONEnum.FAIL_TO_CONNECT_ACCOUNT_SERVICE, "");
		}
		
		
	}
}
