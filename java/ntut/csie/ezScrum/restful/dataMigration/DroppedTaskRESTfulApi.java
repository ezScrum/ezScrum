package ntut.csie.ezScrum.restful.dataMigration;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.dao.TaskDAO;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
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
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;

@Path("projects/{projectId}/tasks")
public class DroppedTaskRESTfulApi {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createDroppedTask(@PathParam("projectId") long projectId, String entity) {
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		if (project == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MEESSAGE, "");
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
		task.setStoryId(TaskObject.NO_PARENT)
			.setStatus(TaskObject.STATUS_UNCHECK)
			.save();
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project);
		sprintBacklogHelper.dropTask(task.getId());
		// Update Remain
		task.setRemains(remain);
		TaskDAO.getInstance().update(task);
		task = TaskObject.get(task.getId());
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, task.toString());
	}
	
	@POST
	@Path("/{taskId}/histories")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createHistoryInDroppedTask(@PathParam("projectId") long projectId,
	                                 		   @PathParam("taskId") long taskId,
	                                 		   String entity) {
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		TaskObject task = resourceFinder.findDroppedTask(taskId);

		if (project == null || task == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MEESSAGE, "");
		}

		// Error Checking
		String message = JSONChecker.checkHistoryJSON(entity);
		if (!message.isEmpty()) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, message, "");
		}
		// Get HistoryObject
		HistoryObject history = JSONDecoder.toHistory(taskId, IssueTypeEnum.TYPE_TASK, entity);
		history.save();
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, history.toString());
	}
	
	@DELETE
	@Path("/{taskId}/histories/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteHistoryInDroppedTask(@PathParam("projectId") long projectId,
	        							@PathParam("taskId") long taskId,
	        							String entity) {
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		TaskObject task = resourceFinder.findDroppedTask(taskId);

		if (project == null || task == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MEESSAGE, "");
		}
		// Delete Histories
		HistoryDAO.getInstance().deleteByIssue(taskId, IssueTypeEnum.TYPE_TASK);
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, "");
	}
	
	@POST
	@Path("/{taskId}/attachfiles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAttachFileInDroppedTask(@PathParam("projectId") long projectId,
	        						  		   @PathParam("taskId") long taskId,
	        						  		   String entity) throws IOException {
		ResourceFinder resourceFinder = new ResourceFinder();
		ProjectObject project = resourceFinder.findProject(projectId);
		TaskObject task = resourceFinder.findDroppedTask(taskId);

		if (project == null || task == null) {
			return ResponseFactory.getResponse(Response.Status.NOT_FOUND, ResponseJSONEnum.ERROR_NOT_FOUND_MEESSAGE, "");
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
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, "");
	}
}
