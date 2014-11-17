package ntut.csie.ezScrum.restful.mobile.service;

import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.restful.mobile.support.ConvertProductBacklog;
import ntut.csie.ezScrum.restful.mobile.util.ProductBacklogUtil;
import ntut.csie.ezScrum.web.dataObject.StoryInformation;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.jcis.account.core.LogonException;
import ntut.csie.jcis.resource.core.IProject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;

public class ProductBacklogWebService extends ProjectWebService {
	private ConvertProductBacklog mConvertProductBacklog = new ConvertProductBacklog();
	private ProductBacklogHelper mProductBacklogHelper;
	private String mResponseString;
	private IProject mProject;
	private IUserSession mUserSession;
	private ProductBacklogLogic mProductBacklogLogic;

	public ProductBacklogWebService(String userName, String userPassward, String projectId) throws LogonException {
		super(userName, userPassward, projectId);
		mProject = super.getProjectList().get(0);
		mUserSession = new UserSession(super.getAccount());
		mProductBacklogHelper = new ProductBacklogHelper(mUserSession, mProject);
		mProductBacklogLogic = new ProductBacklogLogic(mUserSession, mProject);
	}

	public String getRESTFulResponseString() {
		return mResponseString;
	}

	/**
	 * 新增一筆 Story
	 * @throws JSONException
	 */
	public void createStory(JSONObject storyProperties) throws JSONException {
		StoryInformation storyInformation = new StoryInformation();
		storyInformation.setName(storyProperties.getString("name"));
		storyInformation.setImportance(storyProperties.getString("importance"));
		storyInformation.setEstimation(storyProperties.getString("estimation"));
		storyInformation.setValue(storyProperties.getString("value"));
		storyInformation.setHowToDemo(storyProperties.getString("howToDemo"));
		storyInformation.setNotes(storyProperties.getString("notes"));
		storyInformation.setSprintID(storyProperties.getString("sprintID"));
		storyInformation.setTagIDs(storyProperties.getString("tagIDs"));

		IIssue story = mProductBacklogHelper.addNewStory(storyInformation);
		mResponseString = mConvertProductBacklog.createStory(story.getIssueID());
	}

	/****
	 * 讀取所有 Story
	 * @param filterType
	 */
	public void readStory(String filterType) {
		IStory[] storyList = mProductBacklogLogic.getStoriesByFilterType(filterType);
		mResponseString = mConvertProductBacklog.readStoryList(storyList);
	}

	/****
	 * 刪除 Story
	 * @param storyProperties
	 */
	public void deleteStory(String storyId) {
		mProductBacklogHelper.deleteStory(storyId);
		IStory[] storyList = mProductBacklogLogic.getStoriesByFilterType(null);
		try {
			mResponseString = mConvertProductBacklog.deleteStory(storyList, storyId);
		} catch (JSONException e) {
			System.out.println("class: ProductBacklogWebService, method: deleteStory, exception: " + e.toString());
			e.printStackTrace();
		}
	}

	/****
	 * 更新 Story
	 * @param storyProperties
	 */
	public void updateStory(JSONObject storyProperties) {
		Long issueId = null;
		ArrayList<Long> issueIdList = new ArrayList<Long>();
		IStory targetStory = null;
		ArrayList<TagObject> issueTagList = new ArrayList<TagObject>();
		
		try {
			JSONArray tagArray = storyProperties.getJSONArray(ProductBacklogUtil.TAG_TAGLIST);
			// 由 client 端取回的 storyId
			String id = storyProperties.getString(ProductBacklogUtil.TAG_ID);
			String storyName = storyProperties.getString(ProductBacklogUtil.TAG_NAME);
			String estimation = storyProperties.getString(ProductBacklogUtil.TAG_ESTIMATION);
			String importance = storyProperties.getString(ProductBacklogUtil.TAG_IMPORTANCE);
			String value = storyProperties.getString(ProductBacklogUtil.TAG_VALUE);
			String howToDemo = storyProperties.getString(ProductBacklogUtil.TAG_HOWTODEMO);
			String notes = storyProperties.getString(ProductBacklogUtil.TAG_NOTES);
			String sprint = storyProperties.getString(ProductBacklogUtil.TAG_SPRINT);
			issueId = Long.valueOf(id);

			mProductBacklogHelper.editStory(issueId, storyName, value, importance, estimation, howToDemo, notes, true);

			ArrayList<TagObject> tagList = mProductBacklogHelper.getTagList();
			for (int i = 0; i < tagList.size(); i++) {
				mProductBacklogHelper.removeStoryTag(String.valueOf(issueId), tagList.get(i).getId());
			}

			for (int i = 0; i < tagArray.length(); i++) {
				if (mProductBacklogHelper.isTagExist(tagArray.getString(i))) {
					TagObject issueTag = mProductBacklogHelper.getTagByName(tagArray.getString(i));
					mProductBacklogHelper.addStoryTag(String.valueOf(issueId), issueTag.getId());
					issueTagList.add(issueTag); // 檢查用
				}
			}

			// 變更 Sprint 的內容
			issueIdList.add(issueId);
			mProductBacklogLogic.addIssueToSprint(issueIdList, sprint);

			// 重新取出 edit 後的 Story
			IStory[] storyList = mProductBacklogLogic.getStoriesByFilterType(null);

			for (IStory iStory : storyList) { // 找出指定 id 的Story
				if (iStory.getIssueID() == Long.valueOf(storyProperties.getString(ProductBacklogUtil.TAG_ID))) {
					targetStory = iStory;
					break;
				}
			}

			// 未完成:回傳無確認是否正確完成， 確認是否更新成功
			mResponseString = mConvertProductBacklog.updateStory(targetStory, issueId, storyName, value, importance, estimation, howToDemo, notes, issueTagList);
		} catch (JSONException e) {
			System.out.println("class: ProductBacklogWebService, " + "method: updateStory, " + "exception: " + e.toString());
			e.printStackTrace();
		}
	}

	/****
	 * 讀取指定 Story
	 * @param storyId
	 */
	public void readStoryById(long storyId) {
		IStory targetStory = null;
		IStory[] storyList = mProductBacklogLogic.getStoriesByFilterType(null);

		// 找出指定 id 的 Story
		for (IStory story : storyList) {
			if (story.getIssueID() == storyId) {
				targetStory = story;
				break;
			}
		}
		mResponseString = mConvertProductBacklog.readStory(targetStory);
	}

	/**
	 * 取得所有的 tag
	 */
	public void readAllTags() {
		ArrayList<TagObject> tagList = mProductBacklogHelper.getTagList();
		mResponseString = new Gson().toJson(tagList);
	}

	/**
	 * 取得單一的 Story History
	 * @throws SQLException 
	 */
	public void readStoryHistory(long storyId) throws SQLException {
		IIssue issue = mProductBacklogHelper.getIssue(storyId);
		if (issue.getHistories().size() > 0) {
			try {
				mResponseString = mConvertProductBacklog.getStoryHistory(issue.getHistories());
			} catch (JSONException e) {
				System.out.println("class: ProductBacklogWebService, method: readStoryHistory, exception: " + e.toString());
				e.printStackTrace();
			}
		}
	}
}
