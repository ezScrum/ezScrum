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
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;

@Path("projects/{projectId}/sprints/{sprintId}/stories")
public class StoryRESTfulApi {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createStory(@PathParam("projectId") long projectId, 
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
		String message = JSONChecker.checkStoryJSON(entity);
		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}

		// Create Story
		StoryObject story = JSONDecoder.toStory(projectId, sprintId, entity);
		story.save();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, story.toString());
	}
	
	@POST
	@Path("/{storyId}/tags")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createTagInStory(@PathParam("projectId") long projectId,
	                                 @PathParam("sprintId") long sprintId, 
	                                 @PathParam("storyId") long storyId,
	                                 @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
	 			                     @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
	                                 String entity) {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		SprintObject sprint = resourceFinder.findSprint(sprintId);
		StoryObject story = resourceFinder.findStory(storyId);
		
		if (project == null || sprint == null || story == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}

		// Error Checking
		String message = JSONChecker.checkTagJSON(entity);

		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		// Add Tag To Story
		TagObject tag = JSONDecoder.toTagInStory(entity);
		story.addTag(tag.getId());
		story.save();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, tag.toString());
	}
	
	@POST
	@Path("/{storyId}/histories")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createHistoryInStory(@PathParam("projectId") long projectId,
	                                 	 @PathParam("sprintId") long sprintId,
	                                 	 @PathParam("storyId") long storyId,
	                                 	 @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
	    			                     @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
	                                 	 String entity) {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		SprintObject sprint = resourceFinder.findSprint(sprintId);
		StoryObject story = resourceFinder.findStory(storyId);

		if (project == null || sprint == null || story == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}

		// Error Checking
		String message = JSONChecker.checkHistoryJSON(entity);

		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		// Get HistoryObject
		HistoryObject history = JSONDecoder.toHistory(story.getId(), IssueTypeEnum.TYPE_STORY, entity, sprintId);
		history.save();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, history.toString());
	}
	
	@DELETE
	@Path("/{storyId}/histories/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteHistoryInStory(@PathParam("projectId") long projectId,
	        							 @PathParam("sprintId") long sprintId,
	        							 @PathParam("storyId") long storyId,
	        							 @HeaderParam(SecurityModule.USERNAME_HEADER) String username,
	     			                     @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
	        							 String entity) {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		SprintObject sprint = resourceFinder.findSprint(sprintId);
		StoryObject story = resourceFinder.findStory(storyId);

		if (project == null || sprint == null || story == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MESSAGE, "");
		}
		// Delete Histories
		HistoryDAO.getInstance().deleteByIssue(storyId, IssueTypeEnum.TYPE_STORY);
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, "");
	}
	
	@POST
	@Path("/{storyId}/attachfiles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAttachFileInStory(@PathParam("projectId") long projectId,
	        						  		@PathParam("sprintId") long sprintId,
	        						  		@PathParam("storyId") long storyId,
	        						  		@HeaderParam(SecurityModule.USERNAME_HEADER) String username,
	        						  		@HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
	        						  		String entity) throws IOException {
		if(!SecurityModule.isAccountValid(username, password)){
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, "", "");
		}
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		SprintObject sprint = resourceFinder.findSprint(sprintId);
		StoryObject story = resourceFinder.findStory(storyId);
		
		if (project == null || sprint == null || story == null) {
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
}