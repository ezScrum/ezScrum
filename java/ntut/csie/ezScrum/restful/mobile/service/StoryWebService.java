package ntut.csie.ezScrum.restful.mobile.service;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.restful.mobile.support.ConvertProductBacklog;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

import com.google.gson.Gson;

public class StoryWebService extends ProjectWebService{
	private ConvertProductBacklog mConvertProductBacklog;
	private ProductBacklogHelper mProductBacklogHelper;
	private SprintBacklogHelper mSprintBacklogHelper;
	
	public StoryWebService(UserObject user, String projectID) throws LogonException {
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
	 * @throws JSONException
	 */
	public String updateStory(String storyJson) {
		StoryObject editStoryObj = new Gson().fromJson(storyJson, StoryObject.class);
		StoryObject newStoryObj = new StoryObject(mProductBacklogHelper.editStory(editStoryObj.toStoryInformation()));
		for (TagObject originTag : newStoryObj.tagList) {
			boolean isExist = false;
			// 判斷舊的 tag 是否還存在
			for (TagObject editTag : editStoryObj.tagList)
				if (originTag.getTagName().equals(editTag.getTagName()))
					isExist =  true;
			// 如果 tag 不存在就刪掉，否則將已經存在的 tag 從待加入的 tag list 中移除
			if (!isExist) {
				mProductBacklogHelper.removeStoryTag(newStoryObj.id, originTag.getTagID());
			} else
				editStoryObj.tagList.remove(originTag);
		}
		// 將新的 tag 加入 story
		for (TagObject editTag : editStoryObj.tagList) {
			System.out.println("editTag : " + editTag);
			mProductBacklogHelper.addStoryTag(editStoryObj.id, editTag.getTagID());
		}
		newStoryObj = new StoryObject(mProductBacklogHelper.getIssue(Long.parseLong(newStoryObj.id)));
		Gson gson = new Gson();
		return gson.toJson(newStoryObj);
	}
	
	/**
	 * 取得 story 中所有的 task 
	 * @param storyID
	 * @return
	 * @throws JSONException
	 */
	public String getTaskInStory(String storyID) throws JSONException {
		Gson gson = new Gson();
		IIssue[] tasks = mSprintBacklogHelper.getTaskInStory(storyID);
		List<TaskObject> taskList = new ArrayList<TaskObject>();
		for (IIssue task : tasks)
			taskList.add(new TaskObject(task));
		return gson.toJson(taskList);
	}
	
	public void addExistedTask(String storyID, String taskIDsJson) {
		Gson gson = new Gson();
		String[] taskIDs = gson.fromJson(taskIDsJson, String[].class);
		mSprintBacklogHelper.addExistedTask(storyID, taskIDs);
	}
}
