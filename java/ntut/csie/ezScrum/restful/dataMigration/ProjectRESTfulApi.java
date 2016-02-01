package ntut.csie.ezScrum.restful.dataMigration;

import java.util.ArrayList;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.soap.AddressingFeature.Responses;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.AccountJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ScrumRoleJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.security.SecurityModule;
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
	public Response createProject(String entity, 
								  @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
								  @HeaderParam(SecurityModule.PASSWORD_HEADER) String password) {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		
		// Error Checking
		String message = JSONChecker.checkProjectJSON(entity);

		if (!message.equals("")) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		// Create Project
		ProjectObject project = JSONDecoder.toProject(entity);
		// Check for existing Project
		ProjectObject existedProject = ProjectObject.get(project.getName());
		if (existedProject != null) {
			return ResponseFactory.getResponse(Response.Status.CONFLICT, ResponseJSONEnum.ERROR_RESOURCE_EXIST_MESSAGE, "");
		}
		project.save(project.getCreateTime());
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, project.toString());
	}

	@PUT
	@Path("/{projectId}/scrumroles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateScrumRolesInProject(@PathParam("projectId") long projectId, String entity,
											  @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
											  @HeaderParam(SecurityModule.PASSWORD_HEADER) String password) throws JSONException {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		
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
	public Response createProjectRoleInProject(@PathParam("projectId") long projectId, 
											   @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
											   @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
											   String entity) throws JSONException {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
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
	public Response createTagInProject(@PathParam("projectId") long projectId, 
									   @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
									   @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
									   String entity) throws JSONException {
		if(!SecurityModule.isAccountValid(username,password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
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
		// Check for existing tag
		TagObject existingTag = TagObject.get(tag.getName());
		if (existingTag != null) {
			return ResponseFactory.getResponse(Response.Status.CONFLICT, ResponseJSONEnum.ERROR_RESOURCE_EXIST_MESSAGE, "");
		}
		tag.save();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, entity);
	}
}
