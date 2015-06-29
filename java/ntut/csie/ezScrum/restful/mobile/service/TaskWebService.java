package ntut.csie.ezScrum.restful.mobile.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.ProjectHelper;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.account.core.LogonException;
import ntut.csie.jcis.resource.core.IProject;

import com.google.gson.Gson;

public class TaskWebService extends ProjectWebService {
	private SprintBacklogHelper mSprintBacklogHelper;
	private ProductBacklogHelper mProductBacklogHelper;
	private ProjectHelper mProjectHelper;
	private ProjectObject mProject;
	
	public TaskWebService(AccountObject user, String projectId) throws LogonException {
		super(user, projectId);
		initialize(Long.parseLong(projectId));
	}
	
	public TaskWebService(String username, String userpwd, String projectId) throws LogonException {
		super(username, userpwd, projectId);
		initialize(Long.parseLong(projectId));
	}
	
	private void initialize(long projectId) {
		mProjectHelper = new ProjectHelper();
		mProject = mProjectHelper.getProject(projectId);
		mSprintBacklogHelper = new SprintBacklogHelper(mProject);
		mProductBacklogHelper = new ProductBacklogHelper(mProject);
	}
	
	/**
	 * 取得可以被加到 story 的已存在 task
	 * @return
	 * @throws SQLException 
	 */
	public String getTasksWithNoParent() throws SQLException {
		ArrayList<TaskObject> existingTasks = mProductBacklogHelper.getTasksWithNoParent();
		Gson gson = new Gson();
		return gson.toJson(existingTasks);
	}
	
	/**
	 * 在 story 中加入新的 task (TaskInfo 可能不完全，待檢查!!!)
	 * @param storyId
	 * @param task
	 * @return
	 * @throws JSONException 
	 */
	public String createTaskInStory(long storyId, String taskJSonString) throws JSONException {
		JSONObject taskJSon = new JSONObject(taskJSonString);
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.taskId = Long.parseLong(taskJSon.getString("id"));
		taskInfo.name = taskJSon.getString("name");
		taskInfo.estimate = Integer.parseInt(taskJSon.getString("estimate"));
		taskInfo.remains = Integer.parseInt(taskJSon.getString("remains"));
		taskInfo.notes = taskJSon.getString("notes");
		taskInfo.actual = Integer.parseInt(taskJSon.getString("actual"));
		taskInfo.storyId = storyId;
		taskInfo.projectId = mProject.getId();
		
		TaskObject task = mSprintBacklogHelper.addTask(mProject.getId(), taskInfo);
		return String.valueOf(task.getId());
	}
	
	/**
	 * 刪除 task
	 * @param taskId
	 * @return
	 */
	public void deleteTask(String taskId, String storyId) {
		mSprintBacklogHelper.deleteTask(Long.parseLong(taskId));
	}
	
	/**
	 * 將 task 從 story 中移除
	 * @param taskId
	 * @param storyId
	 */
	public void dropTask(String taskId, String storyId) {
		mSprintBacklogHelper.dropTask(Long.parseLong(taskId));
	}
	
	/**
	 * 編輯 task (TaskInfo 可能不完全，待檢查!!!)
	 * @param taskJson
	 * @return
	 * @throws JSONException 
	 */
	public String updateTask(String taskJSonString) throws JSONException {
		JSONObject taskJSon = new JSONObject(taskJSonString);
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.taskId = Long.parseLong(taskJSon.getString("id"));
		taskInfo.name = taskJSon.getString("name");
		taskInfo.estimate = Integer.parseInt(taskJSon.getString("estimate"));
		taskInfo.remains = Integer.parseInt(taskJSon.getString("remains"));
		taskInfo.notes = taskJSon.getString("notes");
		taskInfo.actual = Integer.parseInt(taskJSon.getString("actual"));
		
		mSprintBacklogHelper.updateTask(taskInfo, taskJSon.getString("handler"), taskJSon.getString("partner"));
		return "true";
	}
}
