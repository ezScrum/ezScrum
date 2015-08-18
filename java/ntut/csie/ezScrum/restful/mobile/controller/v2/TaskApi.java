package ntut.csie.ezScrum.restful.mobile.controller.v2;

import java.util.ArrayList;

import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ntut.csie.ezScrum.restful.mobile.support.ConvertSprintBacklog;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.databasEnum.TaskEnum;

import org.codehaus.jettison.json.JSONObject;

@Path("/tasks")
public class TaskApi extends BaseAuthApi {

	@Override
	protected Response get(long resourceId, UriInfo uriInfo) throws Exception {
		TaskObject task = TaskObject.get(resourceId);
		
		if(task != null){
			return response(200, task.toString());
		}
		return response(404, "Not found");
	}

	@Override
	protected Response getList(UriInfo uriInfo) throws Exception {
		MultivaluedMap<String, String> queries = uriInfo.getQueryParameters();
		String projectName = queries.get("project_name").get(0);
		ProjectObject project = ProjectObject.get(projectName);
		ArrayList<StoryObject> storie = project.getStories();
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		
		for (StoryObject story : storie) {
			tasks.addAll(story.getTasks());
		}

		return response(200, ConvertSprintBacklog.getTasksJsonString(tasks));
	}
 
	@Override
	protected Response post(String entity) throws Exception {
		JSONObject jsonEntity = new JSONObject(entity);
		ProjectObject project = ProjectObject.get(jsonEntity.getString("project_name"));
		TaskObject task = new TaskObject(project.getId());
		task.setName(jsonEntity.getString(TaskEnum.NAME))
		        .setNotes(jsonEntity.getString(TaskEnum.NOTES))
		        .setStoryId(jsonEntity.getInt(TaskEnum.STORY_ID))
		        .setHandlerId(jsonEntity.getLong(TaskEnum.HANDLER_ID))
		        .setEstimate(jsonEntity.getInt(TaskEnum.ESTIMATE))
		        .setRemains(jsonEntity.getInt(TaskEnum.REMAIN))
		        .save();
		long newTaskId = task.getId();
		return response(200, TaskObject.get(newTaskId).toString());
	}

	@Override
	protected Response put(long resourceId, String entity) throws Exception {
		JSONObject jsonEntity = new JSONObject(entity);
		TaskObject task = TaskObject.get(resourceId);
		task.setName(jsonEntity.getString(TaskEnum.NAME))
		        .setNotes(jsonEntity.getString(TaskEnum.NOTES))
		        .setStoryId(jsonEntity.getInt(TaskEnum.STORY_ID))
		        .setHandlerId(jsonEntity.getLong(TaskEnum.HANDLER_ID))
		        .setEstimate(jsonEntity.getInt(TaskEnum.ESTIMATE))
		        .setRemains(jsonEntity.getInt(TaskEnum.REMAIN))
		        .save();
		return response(200, TaskObject.get(task.getId()).toString());
	}

	@Override
	protected Response delete(long resourceId, UriInfo uriInfo) throws Exception {
		TaskObject task = TaskObject.get(resourceId);
		task.delete();
		return responseOK();
	}

	@Override
	protected boolean permissionCheck(AccountObject user, UriInfo uriInfo) {
		return true;
	}

	@Override
	protected boolean ownerCheck(AccountObject user, UriInfo uriInfo) {
		return true;
	}

}
