package ntut.csie.ezScrum.restful.mobile.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.ProjectHelper;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.jcis.account.core.LogonException;

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
		UserSession userSession = new UserSession(super.getAccount());
		mSprintBacklogHelper = new SprintBacklogHelper(mProject, userSession);
		mProductBacklogHelper = new ProductBacklogHelper(userSession, super.getProjectList().get(0));
		mProjectHelper = new ProjectHelper();
		mProject = mProjectHelper.getProject(projectId);
	}
	
	/**
	 * 取得可以被加到 story 的已存在 task
	 * @return
	 * @throws SQLException 
	 */
	public String getTasksWithNoParent() throws SQLException {
		ArrayList<TaskObject> existedTask = mProductBacklogHelper.getTasksWithNoParent();
		Gson gson = new Gson();
		return gson.toJson(existedTask);
	}
	
	/**
	 * 在 story 中加入新的 task
	 * @param storyId
	 * @param task
	 * @return
	 */
	public String createTaskInStory(TaskInfo taskInfo) {
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
	 * 編輯 task
	 * @param taskJson
	 * @return
	 */
	public String updateTask(TaskInfo taskInfo, String handlerUsername, String partnersUsername) {
		mSprintBacklogHelper.updateTask(taskInfo, handlerUsername, partnersUsername);
		return "true";
	}
}
