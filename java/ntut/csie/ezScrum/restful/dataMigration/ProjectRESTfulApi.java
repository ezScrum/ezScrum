package ntut.csie.ezScrum.restful.dataMigration;

import java.util.ArrayList;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.AccountJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ScrumRoleJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.support.JSONChecker;
import ntut.csie.ezScrum.restful.dataMigration.support.JSONDecoder;
import ntut.csie.ezScrum.restful.dataMigration.support.ResponseFactory;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.databaseEnum.RoleEnum;

@Path("projects")
public class ProjectRESTfulApi {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createProject(String entity) {
		// Error Checking
		String message = JSONChecker.checkProjectJSON(entity);

		if (!message.equals("")) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		// Create Project
		ProjectObject project = JSONDecoder.toProject(entity);
		project.save(project.getCreateTime());
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, project.toString());
	}

	@PUT
	@Path("/{projectId}/scrumroles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateScrumRoles(@PathParam("projectId") long projectId, String entity) throws JSONException {
		// Get Project
		ProjectObject project = ProjectObject.get(projectId);
		if (project == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MEESSAGE, "");
		}
				
		// Error Checking
		String message = JSONChecker.checkScrumRolesJSON(entity);

		if (!message.equals("")) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		
		// Create Scrum Roles
		ArrayList<ScrumRole> scrumRoles = JSONDecoder.toScrumRoles(project.getName(), entity);
		for (ScrumRole scrumRole : scrumRoles) {
			project.updateScrumRole(scrumRole);
		}
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, entity);
	}

	@POST
	@Path("/{projectId}/projectroles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createProjectRole(@PathParam("projectId") long projectId, String entity) throws JSONException {
		// Get Project
		ProjectObject project = ProjectObject.get(projectId);
		if (project == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MEESSAGE, "");
		}
				
		// Error Checking
		String message = JSONChecker.checkProjectRoleJSON(entity);

		if (!message.equals("")) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		
		// Create ProjectRole
		JSONObject projectRoleJSON = new JSONObject(entity);
		String userName = projectRoleJSON.getString(AccountJSONEnum.USERNAME);
		String roleName = projectRoleJSON.getString(ScrumRoleJSONEnum.ROLE);
		AccountObject account = AccountObject.get(userName);
		account.joinProjectWithScrumRole(project.getId(), RoleEnum.valueOf(roleName));
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, entity);
	}

	@POST
	@Path("/{projectId}/tags")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createTag(@PathParam("projectId") long projectId, String entity) throws JSONException {
		// Get Project
		ProjectObject project = ProjectObject.get(projectId);
		if (project == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MEESSAGE, "");
		}
				
		// Error Checking
		String message = JSONChecker.checkTagJSON(entity);

		if (!message.equals("")) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		
		// Create tag
		TagObject tag = JSONDecoder.toTag(projectId, entity);
		tag.save();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, entity);
	}
}
