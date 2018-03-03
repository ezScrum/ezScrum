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
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;

@Path("projects/{projectId}/stories")
public class DroppedStoryRESTfulApi {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createDroppedStory(@PathParam("projectId") long projectId, 
								       @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
								       @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
									   String entity) {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		if (project == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}

		// Error Checking
		String message = JSONChecker.checkStoryJSON(entity);
		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}

		// Create Story
		long sprintId = -1;
		StoryObject story = JSONDecoder.toStory(projectId, sprintId, entity);
		story.save();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, story.toString());
	}

	@POST
	@Path("/{storyId}/tags")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createTagInDroppedStory(@PathParam("projectId") long projectId,
			        						@PathParam("storyId") long storyId,
			        						@HeaderParam(SecurityModule.USERNAME_HEADER) String username,
										    @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
			        						String entity) {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		StoryObject droppedStory = resourceFinder.findDroppedStory(storyId);

		if (project == null || droppedStory == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}

		// Error Checking
		String message = JSONChecker.checkTagJSON(entity);
		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		// Add Tag To Story
		TagObject tag = JSONDecoder.toTagInStory(entity);
		droppedStory.addTag(tag.getId());
		droppedStory.save();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, tag.toString());
	}

	@POST
	@Path("/{storyId}/appended_to_or_removed_from_sprints/{appendedToOrRemovedFromSprintId}/added_or_dropped_tasks/{addedOrDroppedTaskId}/histories")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createHistoryInDroppedStory(@PathParam("projectId") long projectId,
			        							@PathParam("storyId") long storyId,
			        							@PathParam("appendedToOrRemovedFromSprintId") long appendedToOrRemovedFromSprintId,
			        							@PathParam("addedOrDroppedTaskId") long addedOrDroppedTaskId,
			        							@HeaderParam(SecurityModule.USERNAME_HEADER) String username,
												@HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
			        							String entity) {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		StoryObject droppedStory = resourceFinder.findDroppedStory(storyId);

		if (project == null || droppedStory == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}

		// Error Checking
		String message = JSONChecker.checkHistoryJSON(entity);
		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		// Get HistoryObject
		long relationshipId = 0;
		if(appendedToOrRemovedFromSprintId != 0){
			relationshipId = appendedToOrRemovedFromSprintId;
		}
		else if(addedOrDroppedTaskId != 0){
			relationshipId = addedOrDroppedTaskId;
		}
		HistoryObject history = JSONDecoder.toHistory(droppedStory.getId(), IssueTypeEnum.TYPE_STORY, entity, relationshipId);
		history.save();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, history.toString());
	}
	
	@DELETE
	@Path("/{storyId}/histories/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteHistoryInDroppedStory(@PathParam("projectId") long projectId,
			        							@PathParam("storyId") long storyId,
			        							@HeaderParam(SecurityModule.USERNAME_HEADER) String username,
												@HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
			        							String entity) {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		StoryObject droppedStory = resourceFinder.findDroppedStory(storyId);

		if (project == null || droppedStory == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}

		// Delete Histories
		HistoryDAO.getInstance().deleteByIssue(storyId, IssueTypeEnum.TYPE_STORY);
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, "");
	}

	@POST
	@Path("/{storyId}/attachfiles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAttachFileInDroppedStory(@PathParam("projectId") long projectId,
					        					   @PathParam("storyId") long storyId,
					        					   @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
												   @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
					        					   String entity) throws IOException {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		StoryObject droppedStory = resourceFinder.findDroppedStory(storyId);

		if (project == null || droppedStory == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}

		// Error Checking
		String message = JSONChecker.checkAttachFileJSON(entity);
		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}

		AttachFileInfo attachFileInfo = JSONDecoder.toAttachFileInfo(project.getName(), storyId, IssueTypeEnum.TYPE_STORY, entity);
		String base64BinaryString = JSONDecoder.toBase64BinaryString(entity);
		File file = FileDecoder.toFile(attachFileInfo.name, base64BinaryString);
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(project);
		productBacklogHelper.addAttachFile(attachFileInfo, file);
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, "");
	}
	
	@POST
	@Path("/{storyId}/tasks")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createTaskInDroppedStory(@PathParam("projectId") long projectId,
                               				 @PathParam("storyId") long storyId,
                               				 @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
										     @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
                               				 String entity) {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		StoryObject story = resourceFinder.findDroppedStory(storyId);
		if (project == null || story == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}
		
		// Error Checking
		String message = JSONChecker.checkTaskJSON(entity);
		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		// Create Task
		TaskObject task = JSONDecoder.toTask(projectId, storyId, entity);
		int remain = task.getRemains();
		task.save();
		// Update Remain
		task.setRemains(remain);
		TaskDAO.getInstance().update(task);
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, task.toString());
	}
	
	@POST
	@Path("/{storyId}/tasks/{taskId}/histories")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createHistoryInTask(@PathParam("projectId") long projectId,
	                                 	@PathParam("storyId") long storyId,
	                                 	@PathParam("taskId") long taskId,
	                                 	@HeaderParam(SecurityModule.USERNAME_HEADER) String username,
									    @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
	                                 	String entity) {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		StoryObject story = resourceFinder.findDroppedStory(storyId);
		TaskObject task = resourceFinder.findTaskInDroppedStory(taskId);
		
		if (project == null || story == null || task == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}

		// Error Checking
		String message = JSONChecker.checkHistoryJSON(entity);
		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		// Get HistoryObject
		HistoryObject history = JSONDecoder.toHistory(taskId, IssueTypeEnum.TYPE_TASK, entity);
		history.save();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, history.toString());
	}
	
	@DELETE
	@Path("/{storyId}/tasks/{taskId}/histories")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteHistoryInTask(@PathParam("projectId") long projectId,
	        							@PathParam("storyId") long storyId,
	        							@PathParam("taskId") long taskId,
	        							@HeaderParam(SecurityModule.USERNAME_HEADER) String username,
									    @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
	        							String entity) {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		StoryObject story = resourceFinder.findDroppedStory(storyId);
		TaskObject task = resourceFinder.findTaskInDroppedStory(taskId);
		
		if (project == null || story == null || task == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}
		// Delete Histories
		HistoryDAO.getInstance().deleteByIssue(taskId, IssueTypeEnum.TYPE_TASK);
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, "");
	}
	
	@POST
	@Path("/{storyId}/tasks/{taskId}/attachfiles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAttachFileInTask(@PathParam("projectId") long projectId,
	        						  	   @PathParam("storyId") long storyId,
	        						  	   @PathParam("taskId") long taskId,
	        						  	   @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
										   @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
										   String entity) throws IOException {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		StoryObject story = resourceFinder.findDroppedStory(storyId);
		TaskObject task = resourceFinder.findTaskInDroppedStory(taskId);
		
		if (project == null || story == null || task == null) {
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
