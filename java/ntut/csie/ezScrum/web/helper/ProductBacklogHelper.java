package ntut.csie.ezScrum.web.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.StoryInformation;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.ezScrum.web.support.Translation;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.jdom.Element;

public class ProductBacklogHelper {
	private ProductBacklogMapper productBacklogMapper;
	private ProductBacklogLogic productBacklogLogic;
	private IProject project;

	public ProductBacklogHelper(IUserSession userSession, IProject project) {
		this.productBacklogMapper = new ProductBacklogMapper( project, userSession);
		this.productBacklogLogic = new ProductBacklogLogic(userSession, project);
		this.project = project;
	}
	
	/**
	 * Filter Type : 
	 * 1. null
	 * 2. BACKLOG
	 * 3. DETAIL
	 * 4. DONE
	 * @param filterType 
	 * @return 
	 * @return
	 */
	public StringBuilder getShowProductBacklogResponseText(String filterType){
		IStory[] stories = this.productBacklogLogic.getStoriesByFilterType(filterType);
		
		StringBuilder result = new StringBuilder("");
		result.append(new Translation().translateStoryToJson(stories));
		
		return result;
	}
	
	/**
	 * 新增Story
	 * 步驟：
	 * 1.	新增story
	 * 2.	新增Tag至Story上面
	 * 3.	新增story to sprint
	 * 4.	如果這個SprintID有Release資訊，那麼也將此Story加入Release
	 * @param storyInformation 
	 * @return 
	 */
	public IIssue addNewStory(StoryInformation storyInformation){
		String name = storyInformation.getName();
		String importance = storyInformation.getImportance();
		String estimate = storyInformation.getEstimation();
		String value = storyInformation.getValue();
		String howToDemo = storyInformation.getHowToDemo();
		String notes = storyInformation.getNotes();
		String sprintID = storyInformation.getSprintID();
		String tagIDs = storyInformation.getTagIDs();
		
		
		//	1. 新增story
		IIssue story = this.productBacklogMapper.addStory(storyInformation);
		long issueID = story.getIssueID();
		this.editStory(issueID, name, value, importance, estimate, howToDemo, notes);;
		
		//	2. 新增Tag至Story上面
		this.addTagToStory(tagIDs, issueID);
		
		if (sprintID != null || sprintID.length() != 0){
			//	3. 新增story to sprint
			ArrayList<Long> list = this.addStoryToSprint(sprintID, issueID);
			
			//	4. 如果這個SprintID有Release資訊，那麼也將此Story加入Release
			this.addStoryToRelease(sprintID, list);
		}
		
		IIssue issue = this.productBacklogMapper.getIssue(issueID);
		
		return issue;
	}
	
	public IIssue editStory(StoryInformation storyInformation) {
		long issueID = Long.parseLong(storyInformation.getStroyID());
		String name = storyInformation.getName();
		String importance = storyInformation.getImportance();
		String estimate = storyInformation.getEstimation();
		String value = storyInformation.getValue();
		String howToDemo = storyInformation.getHowToDemo();
		String notes = storyInformation.getNotes();
		String sprintID = storyInformation.getSprintID();
		if (sprintID != null | sprintID.length() != 0){
			//	新增story to sprint
			ArrayList<Long> list = this.addStoryToSprint(sprintID, issueID);
			//	如果這個SprintID有Release資訊，那麼也將此Story加入Release
			this.addStoryToRelease(sprintID, list);
		}
		return editStory(issueID, name, value, importance, estimate, howToDemo, notes);
	}
	
	/**
	 * 更新Story資訊
	 * @param issueID
	 * @param name
	 * @param value
	 * @param importance
	 * @param estimate
	 * @param howToDemo
	 * @param note
	 * @return
	 */
	public IIssue editStory(long issueID, String name, String value, String importance, String estimate, String howToDemo, String note) {
		this.productBacklogMapper.modifyName(issueID, name, null);
//		Element history = this.productBacklogLogic.translateIssueToXML(value, importance, estimation, howToDemo, note);
		Element history = this.translateIssueToXML(value, importance, estimate, howToDemo, note);
		if (history.getChildren().size() > 0) {
			IIssue issue = this.productBacklogMapper.getIssue(issueID);
			issue.addTagValue(history);
			
			issue.setSummary(name);
			this.productBacklogMapper.updateIssueValue(issue);

			return this.productBacklogMapper.getIssue(issueID);
		}else{
			return null;
		}
	}
	
	private Element translateIssueToXML(String value, String importance, String estimate, String howToDemo, String note) {
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(new Date(), DateUtil._16DIGIT_DATE_TIME_2));

		if (importance != null && !importance.equals("")) {
			Element importanceElem = new Element(ScrumEnum.IMPORTANCE);
			int temp = (int) Float.parseFloat(importance);
			importanceElem.setText(temp + "");
			history.addContent(importanceElem);
		}

		if (estimate != null && !estimate.equals("")) {
			Element storyPoint = new Element(ScrumEnum.ESTIMATION);
			storyPoint.setText(estimate);
			history.addContent(storyPoint);
		}
		
		if(value != null && !value.equals(""))
		{
			Element customValue = new Element(ScrumEnum.VALUE);
			customValue.setText(value);
			history.addContent(customValue);
		}
		Element howToDemoElem = new Element(ScrumEnum.HOWTODEMO);
		howToDemoElem.setText(howToDemo);
		history.addContent(howToDemoElem);

		Element notesElem = new Element(ScrumEnum.NOTES);
		notesElem.setText(note);
		history.addContent(notesElem);
		return history;
	}
	
	/**
	 * 更新Story並轉換成XML format
	 * @param issueID
	 * @return
	 */
	public StringBuilder getEditStoryInformationResponseText(long issueID){
		IIssue issue = this.productBacklogMapper.getIssue(issueID);
		
		StringBuilder result = new StringBuilder("");
		result.append(new Translation().translateStory(issue));
		return result;
	}
	
	/**
	 * delete story and get json text
	 * @param ID
	 * @return
	 */
	public StringBuilder deleteStory(String ID) {
	//		removeTask(ID);
	//		m_backlog.deleteStory(ID);
		this.removeTask(ID);
		this.productBacklogMapper.deleteStory(ID);
		
		StringBuilder result = new StringBuilder("");
		result.append("{\"success\":true, \"Total\":1, \"Stories\":[{\"Id\":"+ID+"}]}");
		return result;
	}
	
	/**
	 * 取得專案所有的Tag並轉以XML格式輸出。
	 * @return
	 */
	public StringBuilder getTagListResponseText(){
		StringBuilder sb = new StringBuilder();
		try{
			IIssueTag[] tags = this.getTagList();
	
			sb.append("<TagList><Result>success</Result>");
			for(int i = 0; i < tags.length; i++){
				sb.append("<IssueTag>");
				sb.append("<Id>" + tags[i].getTagId() + "</Id>");
				sb.append("<Name>" + new TranslateSpecialChar().TranslateXMLChar(tags[i].getTagName()) + "</Name>");
				sb.append("</IssueTag>");
			}
			sb.append("</TagList>");
		}
		catch(Exception e){
			sb.append("<TagList><Result>false</Result></TagList>");
		}
		return sb;
	}
	
	/**
	 * 新增Tag並轉換成Response所需要的XML format
	 * @param newTagName
	 * @return
	 */
	public StringBuilder getAddNewTagResponsetext(String newTagName){
		String original_tagname = newTagName;
		
		newTagName = new TranslateSpecialChar().TranslateDBChar(newTagName);
		
		StringBuilder sb = new StringBuilder("");
		//先將"\","'"轉換, 判斷DB裡是否存在
		if(newTagName.contains(",")) {
			sb = new StringBuilder("<Tags><Result>false</Result><Message>TagName: \",\" is not allowed</Message></Tags>");
		} else if(this.isTagExist(newTagName)) {
			//轉換"&", "<", ">", """, 通過XML語法
			//因為"\","'"對xml沒影響, 所以使用original(未轉換)
			newTagName = new TranslateSpecialChar().TranslateXMLChar(original_tagname); 
			sb = new StringBuilder("<Tags><Result>false</Result><Message>Tag Name : " + newTagName + " already exist</Message></Tags>");
		} else {
			this.addNewTag(newTagName);
			
			IIssueTag tag = this.getTagByName(newTagName);
			
			sb.append("<Tags><Result>true</Result>");
			sb.append("<IssueTag>");
			sb.append("<Id>" + tag.getTagId() + "</Id>");
			sb.append("<Name>" + new TranslateSpecialChar().TranslateXMLChar(tag.getTagName()) + "</Name>");
			sb.append("</IssueTag>");
			sb.append("</Tags>");
		}
		return sb;
	}
	
	public StringBuilder getDeleteTagReponseText(String tagId){
    	this.productBacklogMapper.deleteTag(tagId);

		StringBuilder result = new StringBuilder("");
		result.append("<TagList><Result>success</Result>");
		result.append("<IssueTag>");
		result.append("<Id>" + tagId + "</Id>");
		result.append("</IssueTag>");
		result.append("</TagList>");

		return result;
	}
	
	/**
	 * 新增Tag至Story上，並回傳加上Tag之後的Story資訊(json format)
	 * @param storyId
	 * @param tagId
	 * @return
	 */
	public StringBuilder getAddStoryTagResponseText(String storyId, String tagId){
		this.addStoryTag(storyId, tagId);
		
		IIssue issue = this.productBacklogMapper.getIssue(Long.parseLong(storyId));
		
		StringBuilder result = new StringBuilder("");
		result.append(this.translateStoryToJson(issue));
		return result;
	}
	
	/**
	 * 移除Story上的Tag，並回傳移除Tag之後的Story資訊(json format)
	 * @param storyId
	 * @param tagId
	 * @return
	 */
	public StringBuilder getRemoveStoryTagResponseText(String storyId, String tagId){
		
		this.removeStoryTag(storyId,tagId);
		IIssue issue = this.productBacklogMapper.getIssue(Long.parseLong(storyId));
		
		StringBuilder result = new StringBuilder("");
		result.append(this.translateStoryToJson(issue));

		return result;
	}
	
	/**
	 * 將story資訊轉換成JSon format
	 * @param issue
	 * @return
	 */
	public StringBuilder translateStoryToJson(IIssue issue){
		StringBuilder result = new StringBuilder("");
		result.append(new Translation().translateStoryToJson(issue));
		
		return result;
	}
	
	private void addStoryToRelease(String sprintID, ArrayList<Long> list) {
		ReleasePlanHelper releaseHelper = new ReleasePlanHelper(this.project);
		String releaseID = releaseHelper.getReleaseID(sprintID);
		if (!(releaseID.equals("0")))
			this.productBacklogLogic.addReleaseTagToIssue(list, releaseID);
	}

	private ArrayList<Long> addStoryToSprint(String sprintID, long issueID) {
		ArrayList<Long> list = new ArrayList<Long>();
		list.add(issueID);
		this.productBacklogLogic.addIssueToSprint(list, sprintID);
		return list;
	}

	private void addTagToStory(String tagIDs, long issueID) {
		String[] IDs = tagIDs.split(",");
		if ( ! (tagIDs.isEmpty()) && IDs.length > 0) {
			for (String tagId : IDs) {
				this.productBacklogMapper.addStoryTag(Long.toString(issueID), tagId);
			}
		}
	}
	
	/**
	 * remove task跟story之間的關係
	 * @param id
	 */
	private void removeTask(String id) {
		IIssue issue = this.productBacklogMapper.getIssue(Long.parseLong(id));
		// 取得issue的的task列表
		List<Long> tasksList = issue.getChildrenID();
		// drop Tasks
		if (tasksList != null) {
			for (Long taskID : tasksList)
				this.productBacklogMapper.removeTask(taskID, Long.parseLong(id));
		}
	}
	
	/**
	 * 確認分類標籤是否存在
	 * @param name
	 * @return
	 */
	public boolean isTagExist(String name) {
		return this.productBacklogMapper.isTagExist(name);
	}
	
	/**
	 * 取得自訂分類標籤列表
	 * @return
	 */
	public IIssueTag[] getTagList() {
		return this.productBacklogMapper.getTagList();
	}

	/**
	 * 取得 story 或 task
	 * @param id
	 * @return
	 */
	public IIssue getIssue(long id) {
		return this.productBacklogMapper.getIssue(id);
	}
	
	/**
	 * 新增自訂分類標籤
	 * @param name
	 */
	public void addNewTag(String name) {
		this.productBacklogMapper.addNewTag(name);
	}
	
	/**
	 * 刪除自訂分類標籤
	 * @param id
	 */
	public void deleteTag(String id) {
		this.productBacklogMapper.deleteTag(id);
	}
	
	/**
	 * 根據Tag name取得tag
	 * @param name
	 * @return
	 */
	public IIssueTag getTagByName(String name){
		return this.productBacklogMapper.getTagByName(name);
	}
	
	/**
	 * 對Story設定自訂分類標籤
	 * @param storyID
	 * @param tagID
	 */
	public void addStoryTag(String storyID, String tagID) {
		this.productBacklogMapper.addStoryTag(storyID, tagID);
	}
	
	/**
	 * 移除Story的自訂分類標籤
	 * @param storyID
	 * @param tagID
	 */
	public void removeStoryTag(String storyID, String tagID) {
		this.productBacklogMapper.removeStoryTag(storyID, tagID);
	}

	public void moveStory(long issueID, String moveID, String type) {
		ArrayList<Long> issueList = new ArrayList<Long>();
		issueList.add(new Long(issueID));
		
		/**
		 * 1. 移動到某個Release內
		 * 2. 移動到某個Sprint中
		 */
		if (type.equals("release")) {
			// 因為移動到Release內，所以他不屬於任何一個Sprint
			productBacklogLogic.removeStoryFromSprint(issueID);
			// 移動到Release內
			productBacklogLogic.addReleaseTagToIssue(issueList, moveID);
		} else {	
			// 將此Story加入其他Sprint
			productBacklogLogic.addIssueToSprint(issueList, moveID);
			// 檢查Sprint是否有存在於某個Release中
			ReleasePlanHelper releasePlan = new ReleasePlanHelper(project);
			String sprintReleaseID = releasePlan.getReleaseID(moveID);
			/**
			 * 1. 如果有的話，將所有Story加入Release
			 * 2. 沒有的話，將此Story的Release設為0
			 */
			if (!(sprintReleaseID.equals("0"))) {
				productBacklogLogic.addReleaseTagToIssue(issueList, sprintReleaseID);
			} else {
				productBacklogLogic.addReleaseTagToIssue(issueList, "0");
			}
		}
	}
}
