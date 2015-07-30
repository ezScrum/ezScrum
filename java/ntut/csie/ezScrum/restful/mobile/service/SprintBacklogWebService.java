package ntut.csie.ezScrum.restful.mobile.service;

import java.util.ArrayList;

import ntut.csie.ezScrum.restful.mobile.support.ConvertSprintBacklog;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

public class SprintBacklogWebService extends ProjectWebService {
	SprintBacklogMapper mSprintBacklogMapper;
	SprintBacklogLogic mSprintBacklogLogic;

	public SprintBacklogWebService(String username, String userpwd, String projectName, long sprintId) throws LogonException {
		super(username, userpwd, projectName);
		mSprintBacklogLogic = new SprintBacklogLogic(getAllProjects().get(0), sprintId);
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
	}

	public SprintBacklogWebService(String username, String userpwd, String projectName) throws LogonException {
		super(username, userpwd, projectName);
		mSprintBacklogLogic = new SprintBacklogLogic(getAllProjects().get(0), -1);
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
	}

	public String getStoriesIdJsonStringInSprint() throws JSONException {
		return ConvertSprintBacklog.getStoriesIdJsonStringInSprint(mSprintBacklogLogic.getStoriesSortedByIdInSprint());
	}

	public String getTasksIdJsonStringInStory(long storyId) throws JSONException {
		ArrayList<TaskObject> tasks = mSprintBacklogMapper.getStory(storyId).getTasks();
		return ConvertSprintBacklog.getTasksIdJsonStringInStory(storyId, tasks);
	}

	/**
	 * 取得task history list
	 * 
	 * @param taskId
	 * @return
	 * @throws JSONException
	 */
	public String getTaskHsitoryJsonString(long taskId) throws JSONException {
		TaskObject task = TaskObject.get(taskId);
		return ConvertSprintBacklog.getTaskHistoriesJsonString(task.getHistories());
	}

	/**
	 * 取得task information
	 * 
	 * @param taskId
	 * @return
	 * @throws JSONException
	 */
	public String getTaskJsonString(long taskId) throws JSONException {
		TaskObject task = TaskObject.get(taskId);
		return ConvertSprintBacklog.getTaskJsonString(task);
	}

	/**
	 * 取得 Sprint 的 Sprint Backlog(Sprint 底下的 Story 及 Task)
	 * 
	 * @return
	 * @throws JSONException
	 */
	public String getSprintBacklogJsonString() throws JSONException {
		SprintObject sprint = mSprintBacklogMapper.getSprint();
		return ConvertSprintBacklog.getSprintBacklogJsonString(sprint);
	}
	
	/**
	 * get Sprint and sprint 底下的 Story 及 Task JSON string
	 * @return
	 */
	public String getTaskboardJsonString() {
		SprintObject sprint = mSprintBacklogMapper.getSprint();
		return new ConvertSprintBacklog().getTaskboardJsonString(sprint);
	}
}
