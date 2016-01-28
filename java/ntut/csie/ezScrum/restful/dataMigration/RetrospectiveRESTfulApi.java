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
import ntut.csie.ezScrum.restful.dataMigration.support.ResourceFinder;
import ntut.csie.ezScrum.restful.dataMigration.support.ResponseFactory;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;

@Path("projects/{projectId}/sprints/{sprintId}/retrospectives")
public class RetrospectiveRESTfulApi {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createRetrospective(@PathParam("projectId") long projectId, 
                                        @PathParam("sprintId") long sprintId,
                                        @HeaderParam("username") String username,
                                        @HeaderParam("password") String password,
                                        String entity
                                        ) {
		if (!SecurityModule.isAccountValid(username, password)) {
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		SprintObject sprint = resourceFinder.findSprint(sprintId);
		if (project == null || sprint == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MEESSAGE, "");
		}
		// Error Checking
		String message = JSONChecker.checkRetrospectiveJSON(entity);
		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		// Create Retrospective
		RetrospectiveObject retrospective = JSONDecoder.toRetrospective(projectId, sprintId, entity);
		retrospective.save();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, retrospective.toString());
	}
}
