package ntut.csie.ezScrum.restful.mobile.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.restful.mobile.support.ConvertProductBacklog;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

import com.google.gson.Gson;

public class StoryWebService extends ProjectWebService{
	private ConvertProductBacklog mConvertProductBacklog;
	private ProductBacklogHelper mProductBacklogHelper;
	private SprintBacklogHelper mSprintBacklogHelper;
	
	public StoryWebService(AccountObject user, String projectID) throws LogonException {
		super(user, projectID);
		initialize();
	}
	
	public StoryWebService(String username, String userpwd, String projectID) throws LogonException {
		super(username, userpwd, projectID);
		initialize();
	}
	
	private void initialize() {
		UserSession userSession = new UserSession(super.getAccount());
		mProductBacklogHelper = new ProductBacklogHelper(userSession, super.getProjectList().get(0));
		mSprintBacklogHelper = new SprintBacklogHelper(super.getProjectList().get(0), userSession);
		mConvertProductBacklog = new ConvertProductBacklog();
	}
	
	/**
	 * 新增 story 並回傳是否新增成功的資訊
	 * @param storyJson
	 * @return
	 * @throws JSONException
	 */
	public String createStory(String storyJson) throws JSONException {
		StoryObject storyObj = new Gson().fromJson(storyJson, StoryObject.class);
		IIssue storyIIssue = mProductBacklogHelper.addNewStory(storyObj.toStoryInformation());
		return mConvertProductBacklog.createStory(storyIIssue.getIssueID());
	}

	
	/**
	 * 修改 story 並回傳修改後的 story
	 * @param storyJson
	 * @return
	 * @throws SQLException 
	 * @throws JSONException
	 */
	public String updateStory(String storyJson) throws SQLException {
		StoryObject editStoryObj = new Gson().fromJson(storyJson, StoryObject.class);
		StoryObject newStory = new StoryObject(mProductBacklogHelper.editStory(editStoryObj.toStoryInformation()));
		for (TagObject originTag : newStory.tagList) {
			boolean isExist = false;
			// 判斷舊的 tag 是否還存在
			for (TagObject editTag : editStoryObj.tagList)
				if (originTag.getName().equals(editTag.getName()))
					isExist =  true;
			// 如果 tag 不存在就刪掉，否則將已經存在的 tag 從待加入的 tag list 中移除
			if (!isExist) {
				mProductBacklogHelper.removeTagFromStory(newStory.id, originTag.getId());
			} else
				editStoryObj.tagList.remove(originTag);
		}
		// 將新的 tag 加入 story
		for (TagObject editTag : editStoryObj.tagList) {
			System.out.println("editTag : " + editTag);
			mProductBacklogHelper.addTagToStory(editStoryObj.id, editTag.getId());
		}
		newStory = new StoryObject(mProductBacklogHelper.getStory(Long.parseLong(newStory.id)));
		Gson gson = new Gson();
		return gson.toJson(newStory);
	}
	
	/**
	 * 取得 story 中所有的 task 
	 * @param storyId
	 * @return tasks string
	 */
	public String getTaskInStory(String storyId) {
		ArrayList<TaskObject> tasks = mSprintBacklogHelper.getTasksByStoryId(Long.parseLong(storyId));
		return tasks.toString();
	}
	
	public void addExistedTask(String storyId, String taskIdsJson) {
		Gson gson = new Gson();
		String[] taskIds = gson.fromJson(taskIdsJson, String[].class);
		mSprintBacklogHelper.addExistingTasksToStory(taskIds, Long.parseLong(storyId));
	}
}
