package ntut.csie.ezScrum.restful.mobile.support;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssueHistory;
import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.restful.mobile.util.SprintBacklogUtil;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;

public class ConvertProductBacklog {
	public ConvertProductBacklog(){
	}
	
	/****
	 * 讀取 Product Backlog 中指定的 story
	 * @param story
	 * @return
	 */
	public String readStory(IStory iStory) {
		Gson gson = new Gson();
		StoryObject story = new StoryObject(iStory);
		return gson.toJson(story);
	}
	
	/****
	 * 讀取Product Backlog 所有的 story
	 * @param iStoryList
	 * @return
	 */
	public String readStoryList(IStory[] iStoryList) {
		Gson gson = new Gson();
		List<StoryObject> storyList = new ArrayList<StoryObject>();
		for (IStory iStory : iStoryList)
			storyList.add(new StoryObject(iStory));
		return gson.toJson(storyList);
	}
	
	/****
	 * 確認 Story 是否被新增成功，回傳成敗的 json string 
	 * @param issueID
	 * @return
	 * @throws JSONException
	 */
	public String createStory( Long issueID ) throws JSONException{
		JSONObject createStoryResponse = new JSONObject();
		if( issueID == null ){
			createStoryResponse.put( "status", "FAILED" );
			createStoryResponse.put( "issueID", "NULL" );
		}else{
			createStoryResponse.put( "status", "SUCCESS" );
			createStoryResponse.put( "issueID", String.valueOf(issueID) );
		}
		return createStoryResponse.toString();
	}

	
	/****
	 * 刪除 story 
	 * @param storyID
	 * @return
	 * @throws JSONException
	 */
	public String deleteStory(IStory[] iStories, String storyID ) throws JSONException{
		JSONObject deleteStoryResponse = new JSONObject();
		boolean isExist = false;
		for( IStory s:iStories ){
			if(String.valueOf(s.getIssueID()).compareTo(storyID) == 0)
			{
				isExist = true;
			}
		}		
		if( isExist ){
			deleteStoryResponse.put( "status", "FAILED" );
			deleteStoryResponse.put( "issueID", String.valueOf(storyID) );
		}else{
			deleteStoryResponse.put( "status", "SUCCESS" );
			deleteStoryResponse.put( "issueID", String.valueOf(storyID) );
		}
		return deleteStoryResponse.toString();
	}

	/****
	 * 更新story 至 Product Backlog
	 * @param String.vissueID
	 * @return
	 * @throws JSONException
	 */
	public String updateStory(IStory targetStory ,Long issueID, String storyName, String value, String importance, 
			String estimation, String howToDemo, String notes, List<IIssueTag> issueTagList) throws JSONException{
		JSONObject updateStoryResponse = new JSONObject();

		boolean isCorrect = true;

		if(targetStory.getIssueID() != issueID){
			isCorrect = false;
		}
		if(String.valueOf(targetStory.getName()).compareTo(storyName) != 0){
			isCorrect = false;
		}
		if(String.valueOf(targetStory.getValue()).compareTo(value) != 0){
			isCorrect = false;
		}
		if(String.valueOf(targetStory.getImportance()).compareTo(importance) != 0){
			isCorrect = false;
		}
		if(String.valueOf(targetStory.getEstimated()).compareTo(estimation) != 0){
			isCorrect = false;
		}
		if(String.valueOf(targetStory.getHowToDemo()).compareTo(howToDemo) != 0){
			isCorrect = false;
		}
		if(String.valueOf(targetStory.getNotes()).compareTo(notes) != 0){
			isCorrect = false;
		}
		for (IIssueTag issueTag : targetStory.getTag()) {
			for (IIssueTag tag : issueTagList) {
				if (issueTag.getTagName().equals(tag.getTagName())) {
					isCorrect = true;
					break;
				} else {
					isCorrect = false;
				}
			}
		}
		if( isCorrect ){
			updateStoryResponse.put( "status", "SUCCESS" );
		}else{
			updateStoryResponse.put( "status", "FAILED" );
		}
		return updateStoryResponse.toString();
	}
	/***
	 * 取得 taglist 的json string
	 * @param tagList
	 * @return
	 * @throws JSONException
	 */
	public String getTagList( IIssueTag[] tagList ) throws JSONException{
		JSONObject tagJsonObject = new JSONObject();
		JSONArray tagListJsonArray = new JSONArray();
		for( IIssueTag tag:tagList ){
			String tagName = tag.getTagName();
			tagListJsonArray.put(tagName);
		}
		tagJsonObject.put("tagList", tagListJsonArray);
		return tagJsonObject.toString();
	}
	
	/***
	 * 取得story history list 的json string
	 * @param storyHistoryList
	 * @return
	 * @throws JSONException
	 */
	public String getStoryHistory (List<IIssueHistory> issueHistories) throws JSONException
	{
		JSONObject storyHistoryJsonObject = new JSONObject();
		JSONArray storyHistoryJsonArray = new JSONArray();
		for(IIssueHistory istoryHistory :issueHistories)
		{
			String modifyDate =parseDate(istoryHistory.getModifyDate());
			HistoryItemInfo historyItemInfo = new HistoryItemInfo( istoryHistory.getDescription() );
			
			JSONObject storyHistory = new JSONObject();
			storyHistory.put( SprintBacklogUtil.TAG_MODIFYDATE, modifyDate  );	// ModifyDate
			storyHistory.put( SprintBacklogUtil.TAG_HISTORYTYPE, historyItemInfo.getType() );			// Type
			storyHistory.put( SprintBacklogUtil.TAG_DESCRIPTION, historyItemInfo.getDescription() );	// Description
			storyHistoryJsonArray.put(storyHistory);	
		}
		storyHistoryJsonObject.put(SprintBacklogUtil.TAG_STORYHISTORYLIST, storyHistoryJsonArray);
		return storyHistoryJsonObject.toString();		
		
	}
	/**
	 * 轉換story history輸出格式
	 * @author SPARK
	 */
	private class HistoryItemInfo{
		private String description;
		private String type;
		public HistoryItemInfo( String desc ){
			this.parse( desc );
		}

		private void parse(String desc) {
			String [] token = desc.split(":");
			if ( token.length == 2 ) {
				this.setType( token[0].trim() );
				this.setDescription( token[1].trim() );
			} else {
				this.setType("");
				this.setDescription(desc.trim());
			}
		}
		private void setDescription(String description) {
			this.description = description;
		}
		private void setType(String type) {
			this.type = type;
		}
		public String getDescription() {
			return description;
		}
		public String getType() {
			return type;
		}
	}
	
	private String parseDate(long date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
		Date d = new Date(date);
		
		return  sdf.format(d);
	}
	
}