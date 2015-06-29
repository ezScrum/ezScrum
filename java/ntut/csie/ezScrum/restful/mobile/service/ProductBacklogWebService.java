package ntut.csie.ezScrum.restful.mobile.service;

import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.restful.mobile.support.ConvertProductBacklog;
import ntut.csie.ezScrum.web.dataInfo.StoryInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

import com.google.gson.Gson;

public class ProductBacklogWebService extends ProjectWebService {
	private ConvertProductBacklog mConvertProductBacklog = new ConvertProductBacklog();
	private ProductBacklogHelper mProductBacklogHelper;
	private String mResponse;
	private ProjectObject mProject;

	public ProductBacklogWebService(String username, String passward, String projectName) throws LogonException {
		super(username, passward, projectName);
		mProject = getAllProjects().get(0);
		mProductBacklogHelper = new ProductBacklogHelper(mProject);
	}

	public String getRESTFulResponseString() {
		return mResponse;
	}

	/**
	 * 新增一筆 Story
	 * @throws JSONException
	 */
	public void createStory(String storyJson) throws JSONException {
		StoryInfo storyInfo = new StoryInfo(storyJson);
		long storyId = mProductBacklogHelper.addStory(mProject.getId(), storyInfo);
		mResponse = mConvertProductBacklog.createStory(storyId);
	}
	
	/****
	 * 讀取指定 Story
	 * @param storyId
	 */
	public void getStory(long storyId) {
		StoryObject story = mProductBacklogHelper.getStory(storyId);
		mResponse = mConvertProductBacklog.getStory(story);
	}
	
	public void getStories() {
		ArrayList<StoryObject> stories = mProductBacklogHelper.getStories();
		mResponse = mConvertProductBacklog.readStoryList(stories);
	}

	/****
	 * Get Stories by filter
	 * @param filterType
	 */
	public void getStoriesByFilter(String filterType) {
		ArrayList<StoryObject> stories = mProductBacklogHelper.getStoriesByFilterType(filterType);
		mResponse = mConvertProductBacklog.readStoryList(stories);
	}

	/****
	 * 更新 Story
	 * @param storyJson
	 */
	public void updateStory(String storyJson) {
		
		try {
			StoryInfo storyInfo = new StoryInfo(storyJson);
			long storyId = storyInfo.id;
			mProductBacklogHelper.updateStory(storyId, storyInfo);
			mResponse = mConvertProductBacklog.updateStory(true);
		} catch (Exception e) {
			mResponse = mConvertProductBacklog.updateStory(false);
			e.printStackTrace();
		}
	}
	
	/****
	 * 刪除 Story
	 * @param storyId
	 */
	public void deleteStory(long storyId) {
		mProductBacklogHelper.deleteStory(storyId);
		StoryObject story = mProductBacklogHelper.getStory(storyId);;
		mResponse = mConvertProductBacklog.deleteStory(story);
	}

	/**
	 * 取得單一的 Story History
	 * @throws SQLException 
	 */
	public void getStoryHistory(long storyId) {
		StoryObject story = mProductBacklogHelper.getStory(storyId);
		if (story.getHistories().size() > 0) {
			mResponse = mConvertProductBacklog.getStoryHistory(story.getHistories());
		}
	}
	
	/**
	 * 取得所有的 tag
	 */
	public void getAllTags() {
		ArrayList<TagObject> tags = mProductBacklogHelper.getTagList();
		mResponse = mConvertProductBacklog.getTagList(tags);
	}
}
