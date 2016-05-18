package ntut.csie.ezScrum.robust.aspectj.tool;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.security.SecurityModule;
import ntut.csie.ezScrum.restful.dataMigration.support.ResponseFactory;

@Path("switch")
public class AspectJSwitchApi {
	@POST
	@Path("/on")
	@Produces(MediaType.APPLICATION_JSON)
	public Response turnAspectJSwitchOnByActionName(@HeaderParam(SecurityModule.USERNAME_HEADER) String username,
					              @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
					              String actionName
					              ) {
		if (!SecurityModule.isAccountValid(username, password)) {
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, ResponseJSONEnum.ERROR_FORBIDDEN_MESSAGE, "");
		}
		if (actionName == null || actionName.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, ResponseJSONEnum.ERROR_BAD_REQUEST_MESSAGE, "");
		}
		AspectJSwitch.getInstance().turnOnByActionName(actionName);
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, "");
	}
	
	@POST
	@Path("/off")
	@Produces(MediaType.APPLICATION_JSON)
	public Response turnAspectJSwitchOff(@HeaderParam(SecurityModule.USERNAME_HEADER) String username,
					              @HeaderParam(SecurityModule.PASSWORD_HEADER) String password) {
		if (!SecurityModule.isAccountValid(username, password)) {
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, ResponseJSONEnum.ERROR_FORBIDDEN_MESSAGE, "");
		}
		AspectJSwitch.getInstance().turnOff();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, "");
	}
}