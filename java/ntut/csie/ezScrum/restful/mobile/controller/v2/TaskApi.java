package ntut.csie.ezScrum.restful.mobile.controller.v2;

import java.util.ArrayList;

import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.restful.mobile.support.ConvertSprintBacklog;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.databasEnum.TaskEnum;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;

import org.apache.http.HttpResponse;
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

		ConvertSprintBacklog convertSprintBacklog = new ConvertSprintBacklog();
		return response(200, convertSprintBacklog.readTasksInformationList(tasks));
	}
 
	@Override
	protected Response post(String entity) throws Exception {
		JSONObject jsonEntity = new JSONObject(entity);
		ProjectObject project = ProjectObject.get(jsonEntity.getString("project_name"));
		SprintBacklogMapper sprintBacklogMapper = new SprintBacklogMapper(project);
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.name = jsonEntity.getString(TaskEnum.NAME);
		taskInfo.notes = jsonEntity.getString(TaskEnum.NOTES);
		taskInfo.storyId = jsonEntity.getInt(TaskEnum.STORY_ID);
		taskInfo.handlerId = jsonEntity.getLong(TaskEnum.HANDLER_ID);
		taskInfo.estimate = jsonEntity.getInt(TaskEnum.ESTIMATE);
		taskInfo.remains = jsonEntity.getInt(TaskEnum.REMAIN);
		long newTaskId = sprintBacklogMapper.addTask(project.getId(), taskInfo);
		return response(200, TaskObject.get(newTaskId).toString());
	}

	@Override
	protected Response put(long resourceId, String entity) throws Exception {
		JSONObject jsonEntity = new JSONObject(entity);
		ProjectObject project = ProjectObject.get(jsonEntity.getString("project_name"));
		SprintBacklogMapper sprintBacklogMapper = new SprintBacklogMapper(project);
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.name = jsonEntity.getString(TaskEnum.NAME);
		taskInfo.notes = jsonEntity.getString(TaskEnum.NOTES);
		taskInfo.storyId = jsonEntity.getInt(TaskEnum.STORY_ID);
		taskInfo.handlerId = jsonEntity.getLong(TaskEnum.HANDLER_ID);
		taskInfo.estimate = jsonEntity.getInt(TaskEnum.ESTIMATE);
		taskInfo.remains = jsonEntity.getInt(TaskEnum.REMAIN);
		
		TaskObject task = TaskObject.get(resourceId);
		ArrayList<Long> partnersId = new ArrayList<Long>();
		for (long partnerId : task.getPartnersId()) {
			partnersId.add(partnerId);
		}
		taskInfo.partnersId = partnersId;
		sprintBacklogMapper.updateTask(resourceId, taskInfo);
		return response(200, TaskObject.get(resourceId).toString());
	}

	@Override
	protected Response delete(long resourceId, UriInfo uriInfo) throws Exception {
		TaskObject task = TaskObject.get(resourceId);
		task.delete();
		return response(200, "{\"status\" : SUCCESS}");
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
