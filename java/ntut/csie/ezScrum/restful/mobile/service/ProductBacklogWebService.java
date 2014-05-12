package ntut.csie.ezScrum.restful.mobile.service;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.restful.mobile.support.ConvertProductBacklog;
import ntut.csie.ezScrum.restful.mobile.util.ProductBacklogUtil;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.ezScrum.web.dataObject.StoryInformation;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.jcis.account.core.LogonException;
import ntut.csie.jcis.resource.core.IProject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;

public class ProductBacklogWebService extends ProjectWebService {
	private ConvertProductBacklog cpbForRESTFul = new ConvertProductBacklog();
	private ProductBacklogHelper pbHelper ;
	private String responseString;
	private IProject project;
	private IUserSession userSession;
	private ntut.csie.ezScrum.web.helper.ProductBacklogHelper productBacklogHelper;
	private ProductBacklogLogic productBacklogLogic;
	
	public ProductBacklogWebService(String username, String userpwd , String projectID) throws LogonException {
		super(username, userpwd , projectID);
		this.project = super.getProjectList().get(0);
		this.userSession = new UserSession(super.getAccount());
		this.pbHelper = new ProductBacklogHelper(this.project , this.userSession);
		this.productBacklogHelper = new ntut.csie.ezScrum.web.helper.ProductBacklogHelper(this.userSession, this.project);
		this.productBacklogLogic = new ProductBacklogLogic(this.userSession, this.project);
	}
	
	public String getRESTFulResponseString() {
		return responseString;
	}
	
	/**
	 * 新增一筆Story
	 * @throws JSONException 
	 */
	public void createStory(JSONObject storyProperties) throws JSONException{
		StoryInformation storyInformation = new StoryInformation();
		storyInformation.setName(storyProperties.getString("name"));
		storyInformation.setImportance(storyProperties.getString("importance"));
		storyInformation.setEstimation(storyProperties.getString("estimation"));
		storyInformation.setValue(storyProperties.getString("value"));
		storyInformation.setHowToDemo(storyProperties.getString("howToDemo"));
		storyInformation.setNotes(storyProperties.getString("notes"));
		storyInformation.setSprintID(storyProperties.getString("sprintID"));
		storyInformation.setTagIDs(storyProperties.getString("tagIDs"));
		
		IIssue storyIIssue = productBacklogHelper.addNewStory(storyInformation);
		responseString = cpbForRESTFul.createStory(storyIIssue.getIssueID());
	}
	
	/****
	 * 讀取所有Story
	 * @param filterType
	 */
	public void readStory(String filterType){
		IStory[] storyList = (new ProductBacklogLogic(this.userSession, this.project)).getStoriesByFilterType(filterType);
		
		responseString = cpbForRESTFul.readStoryList(storyList);
	}

	/****
	 * 刪除story
	 * @param storyProperties
	 */
	public void deleteStory(String storyID){
		this.productBacklogHelper.deleteStory(storyID);
		IStory[] storyList = (new ProductBacklogLogic(this.userSession, this.project)).getStoriesByFilterType(null);
		try {
			responseString = cpbForRESTFul.deleteStory(storyList, storyID);
		} catch (JSONException e) {
			System.out.println("class: ProductBacklogWebService, method: deleteStory, exception: "+ e.toString());
			e.printStackTrace();
		}
	}

	/****
	 * 更新story
	 * @param storyProperties
	 */
	public void updateStory(JSONObject storyProperties) {
		Long issueID = null;
		ArrayList<Long> issueIDList = new ArrayList<Long>();
		IStory targetStory = null;
		List<IIssueTag> issueTagList = new ArrayList<IIssueTag>();
		try {
			JSONArray tagArray = storyProperties.getJSONArray(ProductBacklogUtil.TAG_TAGLIST);
			
			String id = storyProperties.getString(ProductBacklogUtil.TAG_ID);//由client端取回的storyID
		
			String storyName = storyProperties.getString(ProductBacklogUtil.TAG_NAME);
			String estimation = storyProperties.getString(ProductBacklogUtil.TAG_ESTIMATION);
			String importance = storyProperties.getString(ProductBacklogUtil.TAG_IMPORTANCE);
			String value = storyProperties.getString(ProductBacklogUtil.TAG_VALUE);
			String howToDemo = storyProperties.getString(ProductBacklogUtil.TAG_HOWTODEMO);
			String notes = storyProperties.getString(ProductBacklogUtil.TAG_NOTES);
			String sprint = storyProperties.getString(ProductBacklogUtil.TAG_SPRINT);
			issueID = Long.valueOf(id);
			
			this.productBacklogHelper.editStory(issueID, storyName, value, importance, estimation, howToDemo, notes);
			
			IIssueTag[] tagList = this.productBacklogHelper.getTagList();
			for(int i = 0;i < tagList.length;i++){
				this.productBacklogHelper.removeStoryTag(String.valueOf(issueID), String.valueOf(tagList[i].getTagId()));
			}
			
			for(int i = 0; i < tagArray.length(); i++){
				if(this.productBacklogHelper.isTagExist(tagArray.getString(i))){
					IIssueTag issueTag = this.productBacklogHelper.getTagByName(tagArray.getString(i));
					this.productBacklogHelper.addStoryTag(String.valueOf(issueID), String.valueOf(issueTag.getTagId()));
					
					issueTagList.add(issueTag);//檢查用
				}
			}
			
			//變更sprint的內容
			issueIDList.add(issueID);
			this.productBacklogLogic.addIssueToSprint(issueIDList, sprint);
			
			//重新取出edit後的story
			IStory[] storyList = (new ProductBacklogLogic(this.userSession, this.project)).getStoriesByFilterType(null);
			
			for (IStory iStory : storyList) { // 找出指定id的Story
				if (iStory.getIssueID() == Long.valueOf(storyProperties.getString(ProductBacklogUtil.TAG_ID))) {
					targetStory = iStory;
					break;
				}
			}
			
			//未完成:回傳無確認是否正確完成
			responseString = cpbForRESTFul.updateStory(targetStory, issueID, storyName, value, importance, estimation, howToDemo, notes, issueTagList);//確認是否更新成功
		} catch (JSONException e) {
			System.out.println(	"class: ProductBacklogWebService, " + "method: updateStory, " + "exception: " + e.toString());
			e.printStackTrace();
		}
	}
	
	/****
	 * 讀取指定Story
	 * @param storyID
	 */
	public void readStoryByID(String storyID) {
		IStory targetStory = null;
		IStory[] storyList = (new ProductBacklogLogic(this.userSession, this.project)).getStoriesByFilterType(null);
		
		// 找出指定id的Story
		for (IStory story : storyList) { 
			if (story.getIssueID() == Long.valueOf(storyID)) {
				targetStory = story;
				break;
			}
		}
		
		responseString = cpbForRESTFul.readStory(targetStory);
	}
	
	/**
	 * 取得所有的tag
	 */
	public void readAllTags(){
		IIssueTag[] iIssueTagList = this.productBacklogHelper.getTagList();
		List<TagObject> tagList = new ArrayList<TagObject>();
		for(IIssueTag iIssueTag : iIssueTagList)
			tagList.add(new TagObject(iIssueTag));
		responseString = new Gson().toJson(tagList);
	}
	
	/**
	 * 取得單一的StoryHistory
	 */	
	public void readStoryHistory(String storyID){
		IIssue issue = pbHelper.getIssue(Long.parseLong(storyID));						
		if (issue.getHistory().size() > 0) {
			try {
				responseString = this.cpbForRESTFul.getStoryHistory(issue.getIssueHistories());
			} catch (JSONException e) {
				System.out.println("class: ProductBacklogWebService, method: readStoryHistory, exception: "+ e.toString());
				e.printStackTrace();
			}
		}		
	}
		
}
