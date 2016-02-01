package ntut.csie.ezScrum.restful.dataMigration;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.security.SecurityModule;
import ntut.csie.ezScrum.restful.dataMigration.support.JSONChecker;
import ntut.csie.ezScrum.restful.dataMigration.support.JSONDecoder;
import ntut.csie.ezScrum.restful.dataMigration.support.ResponseFactory;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

@Path("projects/{projectId}/sprints/{sprintId}/accounts")
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
		AccountObject existedAccount = AccountObject.get(account.getUsername());
		if (existedAccount != null) {
			return ResponseFactory.getResponse(Response.Status.CONFLICT, ResponseJSONEnum.ERROR_RESOURCE_EXIST_MESSAGE, "");
		}
		account.save();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, account.toString());
	}
}
