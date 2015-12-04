package ntut.csie.ezScrum.restful.dataMigration;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.support.JSONChecker;
import ntut.csie.ezScrum.restful.dataMigration.support.JSONDecoder;
import ntut.csie.ezScrum.restful.dataMigration.support.ResponseFactory;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;

@Path("projects/{projectId}/sprints/{sprintId}/stories")
public class StoryRESTfulApi {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createStory(@PathParam("projectId") long projectId, 
			                    @PathParam("sprintId") long sprintId,
			                    String entity) {
		ProjectObject project = ProjectObject.get(projectId);
		SprintObject sprint = SprintObject.get(sprintId);
		if (project == null || sprint == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MEESSAGE, "");
		}

		// Error Checking
		String message = JSONChecker.checkStoryJSON(entity);
		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}

		// Create Story
		StoryObject story = JSONDecoder.toStory(projectId, sprintId, entity);
		story.save();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, story.toString());
	}
	
	@POST
	@Path("/{storyId}/tags")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createTagInStory(@PathParam("projectId") long projectId,
	                                 @PathParam("sprintId") long sprintId, 
	                                 @PathParam("storyId") long storyid,
	                                 String entity) {
		ProjectObject project = ProjectObject.get(projectId);
		SprintObject sprint = SprintObject.get(sprintId);
		StoryObject story = StoryObject.get(storyid);
		if (project == null || sprint == null || story == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MEESSAGE, "");
		}

		// Error Checking
		String message = JSONChecker.checkTagJSON(entity);

		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		// Add Tag To Story
		TagObject tag = JSONDecoder.toTagInStory(entity);
		story.addTag(tag.getId());
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, tag.toString());
	}
	
	@POST
	@Path("/{storyId}/histories")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createHistoryInStory(@PathParam("projectId") long projectId,
	                                 	@PathParam("sprintId") long sprintId,
	                                 	@PathParam("storyId") long storyid,
	                                 	String entity) {
		ProjectObject project = ProjectObject.get(projectId);
		SprintObject sprint = SprintObject.get(sprintId);
		StoryObject story = StoryObject.get(storyid);
		if (project == null || sprint == null || story == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MEESSAGE, "");
		}

		// Error Checking
		String message = JSONChecker.checkHistoryJSON(entity);

		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		// Get HistoryObject
		HistoryObject history = JSONDecoder.toHistory(story.getId(), IssueTypeEnum.TYPE_STORY, entity);
		history.save();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, history.toString());
	}
	
	@POST
	@Path("/{storyId}/attachfiles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAttachfiles(@PathParam("projectId") long projectId,
	        						  @PathParam("sprintId") long sprintId,
	        						  @PathParam("storyId") long storyid,
	        						  String entity) {
		ProjectObject project = ProjectObject.get(projectId);
		SprintObject sprint = SprintObject.get(sprintId);
		StoryObject story = StoryObject.get(storyid);
		if (project == null || sprint == null || story == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MEESSAGE, "");
		}

		// Error Checking
		String message = JSONChecker.checkHistoryJSON(entity);

		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		
		// TODO
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, "");
	}
}
