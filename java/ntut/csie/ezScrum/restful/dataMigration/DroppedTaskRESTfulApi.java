package ntut.csie.ezScrum.restful.dataMigration;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.dao.TaskDAO;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.security.SecurityModule;
import ntut.csie.ezScrum.restful.dataMigration.support.FileDecoder;
import ntut.csie.ezScrum.restful.dataMigration.support.JSONChecker;
import ntut.csie.ezScrum.restful.dataMigration.support.JSONDecoder;
import ntut.csie.ezScrum.restful.dataMigration.support.ResourceFinder;
import ntut.csie.ezScrum.restful.dataMigration.support.ResponseFactory;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;

@Path("projects/{projectId}/tasks")
public class DroppedTaskRESTfulApi {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createDroppedTask(@PathParam("projectId") long projectId, 
									  @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
									  @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
									  String entity
									  ) {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		if (project == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}

		// Error Checking
		String message = JSONChecker.checkTaskJSON(entity);
		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		// Create Dropped Task
		long storyId = -1;
		TaskObject task = JSONDecoder.toTask(projectId, storyId, entity);
		int remain = task.getRemains();
		task.save();
		// Update Remain
		task.setRemains(remain);
		TaskDAO.getInstance().update(task);
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, task.toString());
	}
	
	@POST
	@Path("/{taskId}/appended_to_or_removed_from_stories/{appendedToOrRemovedFromStoryId}/histories")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createHistoryInDroppedTask(@PathParam("projectId") long projectId,
	                                 		   @PathParam("taskId") long taskId,
	                                 		   @PathParam("appendedToOrRemovedFromStoryId") long appendedToOrRemovedFromStoryId,
	                                 		   @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
	    									   @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
	                                 		   String entity) {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		TaskObject task = resourceFinder.findDroppedTask(taskId);

		if (project == null || task == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}

		// Error Checking
		String message = JSONChecker.checkHistoryJSON(entity);
		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		// Get HistoryObject
		HistoryObject history = JSONDecoder.toHistory(taskId, IssueTypeEnum.TYPE_TASK, entity, appendedToOrRemovedFromStoryId);
		history.save();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, history.toString());
	}
	
	@DELETE
	@Path("/{taskId}/histories/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteHistoryInDroppedTask(@PathParam("projectId") long projectId,
	        								   @PathParam("taskId") long taskId,
	        								   @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
	        								   @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
	        								   String entity) {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		TaskObject task = resourceFinder.findDroppedTask(taskId);

		if (project == null || task == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}
		// Delete Histories
		HistoryDAO.getInstance().deleteByIssue(taskId, IssueTypeEnum.TYPE_TASK);
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, "");
	}
	
	@POST
	@Path("/{taskId}/attachfiles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAttachFileInDroppedTask(@PathParam("projectId") long projectId,
	        						  		      @PathParam("taskId") long taskId,
	        						  		      @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
	        						  		      @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
	        						  		      String entity) throws IOException {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		TaskObject task = resourceFinder.findDroppedTask(taskId);

		if (project == null || task == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}
		
		// Error Checking
		String message = JSONChecker.checkAttachFileJSON(entity);
		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		
		AttachFileInfo attachFileInfo = JSONDecoder.toAttachFileInfo(project.getName(), taskId, IssueTypeEnum.TYPE_TASK, entity);
        String base64BinaryString = JSONDecoder.toBase64BinaryString(entity);
        File file = FileDecoder.toFile(attachFileInfo.name, base64BinaryString);
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(project);
		productBacklogHelper.addAttachFile(attachFileInfo, file);
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, "");
	}
}
