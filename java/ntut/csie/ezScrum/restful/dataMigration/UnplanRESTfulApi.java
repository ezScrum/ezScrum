package ntut.csie.ezScrum.restful.dataMigration;

import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.dao.UnplanDAO;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.security.SecurityModule;
import ntut.csie.ezScrum.restful.dataMigration.support.JSONChecker;
import ntut.csie.ezScrum.restful.dataMigration.support.JSONDecoder;
import ntut.csie.ezScrum.restful.dataMigration.support.ResourceFinder;
import ntut.csie.ezScrum.restful.dataMigration.support.ResponseFactory;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;

@Path("projects/{projectId}/sprints/{sprintId}/unplans")
public class UnplanRESTfulApi {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUnplan(@PathParam("projectId") long projectId,
	        					 @PathParam("sprintId") long sprintId,
	        					 @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
	        					 @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
	        					 String entity) {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		SprintObject sprint = resourceFinder.findSprint(sprintId);
		if (project == null || sprint == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}
		
		// Error Checking
		String message = JSONChecker.checkUnplanJSON(entity);
		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}

		// Create Unplan
		UnplanObject unplan = JSONDecoder.toUnplan(projectId, sprintId, entity);
		int actual = unplan.getActual();
		unplan.save();
		// Update Actual
		unplan.setActual(actual);
		UnplanDAO.getInstance().update(unplan);
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, unplan.toString());
	}

	@POST
	@Path("/{unplanId}/histories")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createHistoryInUnplan(@PathParam("projectId") long projectId,
	                                 	  @PathParam("sprintId") long sprintId,
	                                 	  @PathParam("unplanId") long unplanId,
	                                 	  @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
	    	        					  @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
	                                 	  String entity) {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		SprintObject sprint = resourceFinder.findSprint(sprintId);
		UnplanObject unplan = resourceFinder.findUnplan(unplanId);

		if (project == null || sprint == null || unplan == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}

		// Error Checking
		String message = JSONChecker.checkHistoryJSON(entity);
		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		// Get HistoryObject
		HistoryObject history = JSONDecoder.toHistory(unplanId, IssueTypeEnum.TYPE_UNPLAN, entity);
		history.save();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, history.toString());
	}
	
	@DELETE
	@Path("/{unplanId}/histories")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteHistoryInUnplan(@PathParam("projectId") long projectId,
       	  								  @PathParam("sprintId") long sprintId,
       	  								  @PathParam("unplanId") long unplanId,
       	  								  @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
       	  								  @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
       	  								  String entity) {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		SprintObject sprint = resourceFinder.findSprint(sprintId);
		UnplanObject unplan = resourceFinder.findUnplan(unplanId);

		if (project == null || sprint == null || unplan == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}
		// Delete Histories
		HistoryDAO.getInstance().deleteByIssue(unplanId, IssueTypeEnum.TYPE_UNPLAN);
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, "");
	}
}