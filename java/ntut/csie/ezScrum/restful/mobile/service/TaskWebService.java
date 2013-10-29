package ntut.csie.ezScrum.restful.mobile.service;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.jcis.account.core.LogonException;

public class TaskWebService extends ProjectWebService{
	SprintBacklogHelper mSprintBacklogHelper;
	ProductBacklogHelper mProductBacklogHelper;
	
	public TaskWebService(UserObject user, String projectID) throws LogonException {
		super(user, projectID);
		initialize();
	}
	
	public TaskWebService(String username, String userpwd, String projectID) throws LogonException {
		super(username, userpwd, projectID);
		initialize();
	}
	
	private void initialize() {
		UserSession userSession = new UserSession(super.getAccount());
		mSprintBacklogHelper = new SprintBacklogHelper(super.getProjectList().get(0), userSession);
		mProductBacklogHelper = new ProductBacklogHelper(super.getProjectList().get(0), userSession);
	}
	
	/**
	 * 取得可以被加到 story 的已存在 task
	 * @return
	 */
	public String getExistedTask() {
		IIssue[] existedTask = mProductBacklogHelper.getAddableTasks();
		List<TaskObject> existedTaskList = new ArrayList<TaskObject>();
		for (IIssue task : existedTask)
			existedTaskList.add(new TaskObject(task));
		Gson gson = new Gson();
		return gson.toJson(existedTaskList);
	}
	
	/**
	 * 在 story 中加入新的 task
	 * @param storyID
	 * @param task
	 * @return
	 */
	public String createTaskInStory(String storyID, TaskObject task) {
		return Long.toString(mSprintBacklogHelper.createTaskInStory(storyID, task).getIssueID());
	}
	
	/**
	 * 刪除 task
	 * @param taskID
	 * @return
	 */
	public void deleteTask(String taskID, String storyID) {
		mSprintBacklogHelper.deleteTask(taskID, storyID);
	}
	
	/**
	 * 將 task 從 story 中移除
	 * @param taskID
	 * @param storyID
	 */
	public void dropTask(String taskID, String storyID) {
		mSprintBacklogHelper.dropTask(taskID, storyID);
	}
	
	/**
	 * 編輯 task
	 * @param taskJson
	 * @return
	 */
	public String updateTask(String taskJson) {
		Gson gson = new Gson();
		TaskObject task = gson.fromJson(taskJson, TaskObject.class);
		return Boolean.toString(mSprintBacklogHelper.editTask(task));
	}
}
