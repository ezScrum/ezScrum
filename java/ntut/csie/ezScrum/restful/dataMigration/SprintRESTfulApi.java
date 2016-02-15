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
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;

@Path("projects/{projectId}/sprints")
public class SprintRESTfulApi {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createSprint(@PathParam("projectId") long projectId, 
								 @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
								 @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
								 String entity
								 ) {
		if (!SecurityModule.isAccountValid(username, password)) {
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		// Get Project
		ProjectObject project = ProjectObject.get(projectId);
		if (project == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MEESSAGE, "");
		}
		// Error Checking
		String message = JSONChecker.checkSprintJSON(entity);
		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		// Create Sprint
		SprintObject sprint = JSONDecoder.toSprint(projectId, entity);
		sprint.save();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, sprint.toString());
	}
}
