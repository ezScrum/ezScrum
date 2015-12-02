package ntut.csie.ezScrum.restful.dataMigration;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ntut.csie.ezScrum.restful.dataMigration.support.JSONChecker;
import ntut.csie.ezScrum.restful.dataMigration.support.JSONDecoder;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;

@Path("projects")
public class ProjectRESTfulApi {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createProject(String entity) {
		String projectJSONString = JSONChecker.checkProjectJSON(entity);
		ProjectObject project = JSONDecoder.toProject(projectJSONString);
		if (project != null) {
			return Response.status(Response.Status.OK).entity(project.toString()).build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
}
