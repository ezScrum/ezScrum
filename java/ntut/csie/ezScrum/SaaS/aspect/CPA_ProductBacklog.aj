/*
 * [AspectJ 語法參考]
 * 
 * 1.取代原有函式的執行(代入與回傳的參數型態保持一致)
 *   pointcut 切點函式名稱(代入參數型態 代入參數名稱)
 *   : execution(回傳參數型態 欲取代的函式名稱(代入參數型態)) && arg(代入參數名稱);
 *   	 
 *   回傳參數型態 around(代入參數型態 代入參數名稱)
 *   : 切點函式名稱(代入參數名稱) {
 *   	// replaced code
 *   }
 *   
 *   ex: 
 *   pointcut replaceFunc(String arg0, String arg1)
 *   : execution(String className.replacedFunc(String, String)) && arg(arg0, arg1);
 *   
 *   String around(String arg0, String arg1)
 *   : replaceFunc(arg0, arg1) {
 *   	System.out.println("我要取代妳的功能: " + thisJoinPoint);
 *   }
 *   
 *   2.
 */
package ntut.csie.ezScrum.SaaS.aspect;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import ntut.csie.ezScrum.SaaS.PMF;
import ntut.csie.ezScrum.SaaS.action.support.DifferentDataTypeTranslation;
import ntut.csie.ezScrum.SaaS.database.ProjectDataStore;
import ntut.csie.ezScrum.SaaS.database.StoryDataStore;
import ntut.csie.ezScrum.SaaS.database.TagDataStore;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueHistory;
import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.iteration.iternal.Story;
import ntut.csie.ezScrum.web.dataObject.StoryInformation;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public aspect CPA_ProductBacklog {
	private String projectId = "";
	
	// replace: constructor of public ProductBacklogMapper.new(IProject project, IUserSession userSession)
	pointcut ProductBacklogMapperConstructorPC(IProject project, IUserSession userSession) 
	: execution(ProductBacklogMapper.new(IProject, IUserSession)) && args(project, userSession);

	void around(IProject project, IUserSession userSession)
	: ProductBacklogMapperConstructorPC(project, userSession) {
		System.out.println("replaced by AOP...ProductBacklogMapper Constructor: " + thisJoinPoint);
		
		this.projectId = project.getName();
	}
	
	private IStory tranStory(StoryDataStore storyDS) {
		IIssue issue = new Issue();
		issue.setIssueID(Long.parseLong(storyDS.getStoryId()));
		issue.setSummary(storyDS.getName());
		issue.setIssueLink("");
		issue.setStatus(ITSEnum.getStatus(storyDS.getStatusValue()));
		
		Story story = new Story(issue);
		story.setValue(storyDS.getValue());
		story.setImportance(storyDS.getImportance());
		story.setEstimated(storyDS.getEstimation());
		story.setHowToDemo(storyDS.getHowToDemo());
		story.setNotes(storyDS.getNotes());
		story.setSprintId(storyDS.getSprintId());
		story.setReleaseId(storyDS.getReleaseId());
		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		
		List<IIssueTag> tagList = new ArrayList<IIssueTag>();
		for(String tagName:storyDS.getTagsList()){
			TagDataStore tagDS = this.getTagDataStore(tagName);
			if( tagDS != null ){
				tagList.add(DifferentDataTypeTranslation.tranTag(tagDS));
			}
		}
		story.setTag(tagList);
		
		DifferentDataTypeTranslation.tranStoryHistory(story, storyDS);
		
		return story;
	}
	
//	private String tranHistoryXML(String fieldName, String oldValue, String newValue, int type, Date modifyDate){
//		/// <History>
//		// <user_id></user_id>
//		// <date_modified></date_modified>
//		// <field_name></field_name>
//		// <old_value></old_value>
//		// <new_value></new_value>
//		// <type></type>
//		// </History>
//		StringBuilder sb = new StringBuilder();
//		sb.append("<Histories><HistoryList><History");
//		sb.append(" id=\"" + "0" + "\"");
//		if (modifyDate == null) {
//			sb.append(" date_modified=\"" + new Timestamp(new Date().getTime()).toString() + "\"");
//		} else {
//			sb.append(" date_modified=\"" + DateUtil.format(modifyDate, DateUtil._16DIGIT_DATE_TIME_MYSQL) + "\"");
//		}
//		sb.append(" field_name=\"" + fieldName + "\"");
//		sb.append(" old_value=\"" + oldValue.replace("\"", "\\\"") + "\"");
//		sb.append(" new_value=\"" + newValue.replace("\"", "\\\"") + "\"");
//		sb.append(" type=\"" + type + "\"/>");
//		sb.append("</HistoryList>");	
//		sb.append("</Histories>");
//		return sb.toString();
//	}
	
	/**
	 * get story
	 */
	//	replace: public IIssue getIssue(long id)
	pointcut getIssuePC(long id)
	: execution(IIssue ProductBacklogMapper.getIssue(long)) && args(id);

	IIssue around(long id)
	: getIssuePC(id) {	
		System.out.println("replaced by AOP...getIssue: " + thisJoinPoint);		
		
		String storyId = String.valueOf(id);
		IStory story;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Key projectkey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
			Key storyKey = new KeyFactory.Builder(projectkey).addChild(StoryDataStore.class.getSimpleName(), storyId).getKey();
			StoryDataStore storyDS = pm.getObjectById(StoryDataStore.class, storyKey);
		
			story = this.tranStory(storyDS);
		
		} finally {
			pm.close();
		}
		return story;
	}
	
	/**
	 * get all stories
	 */
	// replace: List<IStory> getAllStoriesByProjectName()
	pointcut getAllStoriesByProjectNamePC()
	: execution(List<IStory> ProductBacklogMapper.getAllStoriesByProjectName()) && args();

	List<IStory> around()
	: getAllStoriesByProjectNamePC() {	
		System.out.println("replaced by AOP...getAllStoriesByProjectName: " + thisJoinPoint);		
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		List<StoryDataStore> storiesDS = new ArrayList<StoryDataStore>();
		List<IStory> stories = new ArrayList<IStory>();
		try {
			Key projectkey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(),this.projectId);
			ProjectDataStore projectDS = pm.getObjectById(ProjectDataStore.class, projectkey);
			storiesDS.addAll(projectDS.getStories());
		} finally {
			pm.close();  
		}
		
		for(StoryDataStore storyDS : storiesDS) {
			stories.add(this.tranStory(storyDS));
		}
		
		return stories;
	}
	
	/**
	 * add new story
	 * @param storyInformation
	 */
	// replace: public IIssue addStory(StoryInformation storyInformation)
	pointcut addStoryPC(StoryInformation storyInformation)
	: execution(IIssue ProductBacklogMapper.addStory(StoryInformation)) && args(storyInformation);

	IIssue around(StoryInformation storyInformation)
	: addStoryPC(storyInformation) {	
		System.out.println("replaced by AOP...addStory: " + thisJoinPoint);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key projectkey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		ProjectDataStore projectDS = pm.getObjectById(ProjectDataStore.class, projectkey);
		String storyId = projectDS.getNewIssueID();	
		
		Key storyKey = new KeyFactory.Builder(projectkey).addChild(StoryDataStore.class.getSimpleName(), storyId).getKey();
		StoryDataStore storyDS = new StoryDataStore(storyKey);
		storyDS.setStoryId(storyId);
		storyDS.setStatusValue(ITSEnum.NEW_STATUS); 
		storyDS.setName(storyInformation.getName());
		storyDS.setValue(storyInformation.getValue());
		storyDS.setImportance(storyInformation.getImportance());
		storyDS.setEstimation(storyInformation.getEstimation());
		storyDS.setHowToDemo(storyInformation.getHowToDemo());
		storyDS.setNotes(storyInformation.getNotes());
		storyDS.setSprintId(storyInformation.getSprintID());
		storyDS.setReleaseId(storyInformation.getReleaseID());
		storyDS.setProjectId(this.projectId);
		
		//addMantisActionHistory
		storyDS.getHistorylist().add(DifferentDataTypeTranslation.tranHistoryXML(IIssueHistory.EMPTY_FIELD_NAME,
				IIssueHistory.ZERO_OLD_VALUE, IIssueHistory.ZERO_NEW_VALUE, IIssueHistory.ISSUE_NEW_TYPE,
				IIssueHistory.NOW_MODIFY_DATE));
		if (!storyInformation.getEstimation().equals(IIssueHistory.ZERO_OLD_VALUE)) {
			storyDS.getHistorylist().add(DifferentDataTypeTranslation.tranHistoryXML(ScrumEnum.ESTIMATION,
					IIssueHistory.ZERO_OLD_VALUE, storyInformation.getEstimation(), IIssueHistory.OTHER_TYPE,
					IIssueHistory.NOW_MODIFY_DATE));
		}
		
		int newIssueId = Integer.parseInt(storyId)+1;
		projectDS.setNewIssueID(String.valueOf(newIssueId));
		projectDS.getStories().add(storyDS);
		
		try {
			pm.makePersistent(projectDS);
			pm.makePersistent(storyDS);
		} finally {
			pm.close();
		}
		return this.tranStory(storyDS);
	}
	
	/**
	 * edit story
	 */
	//	replace: public void updateIssueValue(IIssue issue)
	pointcut updateIssueValuePC(IIssue issue)
	: execution(void ProductBacklogMapper.updateIssueValue(IIssue)) && args(issue);

	void around(IIssue issue)
	: updateIssueValuePC(issue) {	
		System.out.println("replaced by AOP...updateIssueValue: " + thisJoinPoint);		
		
		String storyId = String.valueOf(issue.getIssueID());
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key projectkey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		Key key = new KeyFactory.Builder(projectkey).addChild(StoryDataStore.class.getSimpleName(), storyId).getKey();
		//this.addMantisActionHistory(fieldName, oldValue, newValue, type, modifyDate)
		//	StoryInformation storyInformation = new StoryInformation(name, importance, estimation, value, howToDemo, notes, description, sprintID, releaseID, tagIDs)
		
		try {
			StoryDataStore storyDS = pm.getObjectById(StoryDataStore.class, key);
			String value = this.checkValue(storyDS.getValue(), issue.getTagValue(ScrumEnum.VALUE, new Date()));
			String importance = this.checkValue(storyDS.getImportance(), issue.getTagValue(ScrumEnum.IMPORTANCE, new Date()));
			String estimation = this.checkValue(storyDS.getEstimation(), issue.getTagValue(ScrumEnum.ESTIMATION, new Date()));
			String howToDemo = this.checkValue(storyDS.getHowToDemo(), issue.getTagValue(ScrumEnum.HOWTODEMO, new Date()));
			String notes = this.checkValue(storyDS.getNotes(), issue.getTagValue(ScrumEnum.NOTES, new Date()));
			String sprintId = this.checkValue(storyDS.getSprintId(), issue.getTagValue(ScrumEnum.SPRINT_ID, new Date()));
			String releaseId = this.checkValue(storyDS.getReleaseId(), issue.getTagValue(ScrumEnum.RELEASE_TAG, new Date()));
			
			storyDS.setStoryId(storyId);
			storyDS.setName(issue.getSummary());
			storyDS.setValue(value);
			storyDS.setImportance(importance);
			storyDS.setEstimation(estimation);
			storyDS.setHowToDemo(howToDemo);
			storyDS.setNotes(notes);
			storyDS.setStatusValue(ITSEnum.getStatus(issue.getStatus()));
			storyDS.setSprintId(sprintId);
			storyDS.setReleaseId(releaseId);
			
			List<IIssueHistory> storyHistories = issue.getHistory();
			for(int i=storyDS.getHistorylist().size(); i<storyHistories.size(); i++) {
				IIssueHistory storyHistory = storyHistories.get(i);
				storyDS.getHistorylist().add(DifferentDataTypeTranslation.tranHistoryXML(storyHistory.getFieldName(), storyHistory.getOldValue(), storyHistory.getNewValue(), storyHistory.getType(), new Date(storyHistory.getModifyDate())));
			}

			pm.makePersistent(storyDS);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * 由於現有ezScrum將Story的各項資訊以XML格式儲存，這樣子的作法會導致要更新Story的各項資訊時，回重複更新導致資訊錯誤
	 * 因此使用此一函式比對ezScrum 中的xml檔案和目前GAE datastore中的值
	 * @param oldValue
	 * @param newValue
	 * @return
	 */
	private String checkValue(String oldValue, String newValue){
		if( oldValue.equals(newValue) || newValue == null){
			return oldValue;
		}else{
			return newValue;
		}
	}
	
	/**
	 * 
	 * @param storyId
	 * @param name
	 * @param modifyDate
	 */
	//	replace: public void modifyName(long storyId, String name, Date modifyDate) 
	pointcut modifyNamePT(long storyId, String name, Date modifyDate) 
	: execution(void ProductBacklogMapper.modifyName(long, String, Date)) && args(storyId, name, modifyDate);
	
	void around(long storyId, String name, Date modifyDate) 
	:modifyNamePT(storyId, name, modifyDate){
		System.out.println("replaced by AOP...modifyName: " + thisJoinPoint);
	}
	
	//	replace: public void updateStoryRelation(String issueID,String releaseID , String sprintID, String estimation, String importance,Date date)
	pointcut updateStoryRelationPC(String issueID, String releaseID , String sprintID, String estimation, String importance, Date date)
	: execution(void ProductBacklogMapper.updateStoryRelation(String, String , String, String, String, Date)) && args(issueID, releaseID, sprintID, estimation, importance, date);

	void around(String issueID, String releaseID , String sprintID, String estimation, String importance, Date date)
	: updateStoryRelationPC(issueID, releaseID, sprintID, estimation, importance, date) {	
		System.out.println("replaced by AOP...updateStoryRelation: " + thisJoinPoint);		
	}
	
	//	replcae: public void addHistory(long issueID, String typeName, String oldValue, String newValue) {
	pointcut addHistoryPC(long issueID, String typeName, String oldValue, String newValue) 
	: execution(void ProductBacklogMapper.addHistory(long, String , String, String)) && args(issueID, typeName, oldValue, newValue);

	void around(long issueID, String typeName, String oldValue, String newValue)
	: addHistoryPC(issueID, typeName, oldValue, newValue) {	
		System.out.println("replaced by AOP...addHistory: " + thisJoinPoint);		
	}
	
	/**
	 * delete story
	 * @param ID
	 */
	//	replace: public void deleteStory(String ID) 
	pointcut deleteStoryPT(String ID) 
	: execution(void ProductBacklogMapper.deleteStory(String)) && args(ID);
	
	void around(String ID) 
	:deleteStoryPT(ID){
		System.out.println("replaced by AOP...deleteStory: " + thisJoinPoint);	
		String storyId = ID;
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key projectkey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		ProjectDataStore project = pm.getObjectById(ProjectDataStore.class, projectkey);
		
		Key storyKey = new KeyFactory.Builder(projectkey).addChild(StoryDataStore.class.getSimpleName(), storyId).getKey();
		
		StoryDataStore removeStoryDS = pm.getObjectById(StoryDataStore.class, storyKey);
		if (removeStoryDS != null) {
//			//移除和Task的關聯
//			List<TaskDataStore> tasks = removeStoryDS.getTasklist();
//			for (int i=0; i<tasks.size(); i++) {
//				TaskDataStore task = tasks.get(i);
//				task.setParentID("");
//			}
//			pm.makePersistentAll(tasks);
			
			project.getStories().remove(removeStoryDS);
			try {
				pm.deletePersistent(removeStoryDS);
			} finally {
				pm.close();
			}
		}
	}
	
	/**
	 * get all Unclosed stories
	 */
	// replace: List<IStory> getUnclosedIssues(String category)
	pointcut getUnclosedIssuesPC(String category)
	: execution(List<IStory> ProductBacklogMapper.getUnclosedIssues(String)) && args(category);

	List<IStory> around(String category)
	: getUnclosedIssuesPC(category) {	
		System.out.println("replaced by AOP...getUnclosedIssues: " + thisJoinPoint);		
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(StoryDataStore.class);
		query.setFilter("projectId == '" + this.projectId + "' && " + "sprintId == '" + ScrumEnum.DIGITAL_BLANK_VALUE + "'");
		@SuppressWarnings("unchecked")
		List<StoryDataStore> result = (List<StoryDataStore>) query.execute();
		
		List<IStory> stories = new ArrayList<IStory>();
		
		for(StoryDataStore storyDS : result) {
			stories.add(this.tranStory(storyDS));
		}
		
		return stories;
	}
	
	
	/**
	 * ------------------------- Tag -------------------------
	 */
	/**
	 * Get Tag Data Store by TagName
	 * @param tagName
	 * @return
	 */
	private TagDataStore getTagDataStore(String tagName){
		//	get tag data store
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key projectkey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		ProjectDataStore projectDS = pm.getObjectById(ProjectDataStore.class, projectkey);
		
		for(TagDataStore tagDS:projectDS.getTags()){
			if( tagDS.getTagName().equals(tagName) ){
				return tagDS;
			}
		}
		return null;
	}
	
	/**
	 * 轉換Tag Name特殊字元。
	 * 由於Web版在新增Tag時，必須處理對於MySql Query會造成錯誤的字串(ex: ' 和 \ )。
	 * 因此必須透過此Method來還原使用者輸入的 Tag Name。
	 * @param tagName
	 * @return
	 */
	private String parseTagName(String tagName){
		if (tagName.contains("\\'")) {
			tagName = tagName.replace("\\'", "'");
		}
		if (tagName.contains("\\\\")) {
			tagName = tagName.replace("\\\\", "\\");
		}
		return tagName;
	}
	
	/**
	 * Get all tags
	 */
	//	replace: public IIssueTag[] getTagList() 
	pointcut getTagListPT() 
	: execution(IIssueTag[] ProductBacklogMapper.getTagList()) && args();
	
	IIssueTag[] around() 
	:getTagListPT(){
		System.out.println("replaced by AOP...getTagListPT: " + thisJoinPoint);	
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key projectkey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		ProjectDataStore projectDS = pm.getObjectById(ProjectDataStore.class, projectkey);
		
		List<TagDataStore> tagDSList = projectDS.getTags();
		IIssueTag[] issueTagArr = new IIssueTag[tagDSList.size()];
		for( int i = 0; i < tagDSList.size(); i++ ){
			issueTagArr[i] = DifferentDataTypeTranslation.tranTag(tagDSList.get(i));
		}
		return issueTagArr;
	}
	
	/**
	 * 取得Tag by TagName
	 * @param name
	 */
	//	replace: public IIssueTag getTagByName(String name)
	pointcut getTagByNamePT(String name)
	: execution(IIssueTag ProductBacklogMapper.getTagByName(String)) && args(name);
	
	IIssueTag around(String name)
	:getTagByNamePT(name){
		System.out.println("replaced by AOP...getTagByNamePT: " + thisJoinPoint);
		String tagName = this.parseTagName(name);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key projectkey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		ProjectDataStore projectDS = pm.getObjectById(ProjectDataStore.class, projectkey);
		List<TagDataStore> tagDSList = projectDS.getTags();
		IIssueTag issueTag = null;
		for( TagDataStore tagDS:tagDSList ){
			if(tagDS.getTagName().equals(tagName)){
				issueTag = DifferentDataTypeTranslation.tranTag(tagDS);
				break;
			}
		}
		return issueTag;
	}
	
	/**
	 * 新增專案的分類標籤
	 * @param name
	 */
	//	replace: public void addNewTag(String name)
	pointcut addNewTagPT(String name)
	: execution(void ProductBacklogMapper.addNewTag(String)) && args(name);
	
	void around(String name)
	:addNewTagPT(name){
		System.out.println("replaced by AOP...addNewTagPT: " + thisJoinPoint);	
		
		String tagName = this.parseTagName(name);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key projectkey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		ProjectDataStore projectDS = pm.getObjectById(ProjectDataStore.class, projectkey);
		
		String tagID = String.valueOf(projectDS.getNewTagID());
		Key tagKey = new KeyFactory.Builder(projectkey).addChild(TagDataStore.class.getSimpleName(), tagID).getKey();
		
		TagDataStore newTagDS = new TagDataStore(tagKey);
		newTagDS.setTagId(tagID);
		newTagDS.setTagName(tagName);
		
		int newTagID = Integer.parseInt(tagID) + 1;
		projectDS.setNewTagID(String.valueOf(newTagID));
		projectDS.getTags().add(newTagDS);
		try {
			pm.makePersistent(projectDS);
			pm.makePersistent(newTagDS);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * 刪除Tag。
	 * 1. 移除story and tag relation
	 * 2. 移除project and tag relation
	 * @param id
	 */
	//	replace: public void deleteTag(String id)
	pointcut deleteTagPT(String id)
	: execution(void ProductBacklogMapper.deleteTag(String)) && args(id);
	
	void around(String id)
	:deleteTagPT(id){
		System.out.println("replaced by AOP...deleteTagPT: " + thisJoinPoint);
		String tagID = id;
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key projectkey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		ProjectDataStore projectDS = pm.getObjectById(ProjectDataStore.class, projectkey);
		
		Key tagKey = new KeyFactory.Builder(projectkey).addChild(TagDataStore.class.getSimpleName(), tagID).getKey();
		TagDataStore removeTagDataStore = pm.getObjectById(TagDataStore.class, tagKey);
		
		if( removeTagDataStore != null ){
			//	remove story and tag relation
			List<StoryDataStore> storyDSList = projectDS.getStories();
			for(StoryDataStore storyDS:storyDSList){
				List<String> tagNames = storyDS.getTagsList();
				for(String tagName:tagNames){
					if(tagName.equals(removeTagDataStore.getTagName())){
						storyDS.getTagsList().remove(tagName);
					}
				}
			}
			
			//	remove project and tag relation
			projectDS.getTags().remove(removeTagDataStore);
		}
		
		try {
			pm.deletePersistent(removeTagDataStore);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * 對Story設定自訂分類標籤
	 * @param storyID
	 * @param tagID
	 */
	//	replace: public void addStoryTag(String storyID, String tagID)
	pointcut addStoryTagPT(String storyID, String tagID)
	: execution(void ProductBacklogMapper.addStoryTag(String, String)) && args(storyID, tagID);
	
	void around(String storyID, String tagID)
	:addStoryTagPT(storyID, tagID){
		System.out.println("replaced by AOP...addStoryTagPT: " + thisJoinPoint);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key projectkey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		
		//	get story data store
		Key storyKey = new KeyFactory.Builder(projectkey).addChild(StoryDataStore.class.getSimpleName(), storyID).getKey();
		StoryDataStore storytDS = pm.getObjectById(StoryDataStore.class, storyKey);
		
		// get tag data store
		Query query = pm.newQuery(TagDataStore.class);
		query.setFilter("tagId == '" + tagID +"'");
	    
	    @SuppressWarnings("unchecked")
		List<TagDataStore> result = (List<TagDataStore>) query.execute();
		for(TagDataStore tagDS : result) {
			storytDS.getTagsList().add(tagDS.getTagName());
		}
		
		try {
			pm.makePersistent(storytDS);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * 移除Story的自訂分類標籤
	 * @param storyID
	 * @param tagID
	 */
	//	replace: public void removeStoryTag(String storyID, String tagID)
	pointcut removeStoryTagPT(String storyID, String tagID)
	: execution(void ProductBacklogMapper.removeStoryTag(String, String)) && args(storyID, tagID);
	
	void around(String storyID, String tagID)
	:removeStoryTagPT(storyID, tagID){
		System.out.println("replaced by AOP...removeStoryTagPT: " + thisJoinPoint);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key projectkey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		
		//	get story data store
		Key storyKey = new KeyFactory.Builder(projectkey).addChild(StoryDataStore.class.getSimpleName(), storyID).getKey();
		StoryDataStore storytDS = pm.getObjectById(StoryDataStore.class, storyKey);
		
		//	get tag data store
		Query query = pm.newQuery(TagDataStore.class);
		query.setFilter("tagId == '" + tagID +"'");
	    
	    @SuppressWarnings("unchecked")
		List<TagDataStore> result = (List<TagDataStore>) query.execute();
		for(TagDataStore tagDS : result) {
			storytDS.getTagsList().remove(tagDS.getTagName());
		}
		
		try {
			pm.makePersistent(storytDS);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * 驗證專案是否已存在分類標籤
	 * @param name
	 */
	//	replace: public boolean isTagExist(String name)
	pointcut isTagExistPT(String name)
	: execution(boolean ProductBacklogMapper.isTagExist(String)) && args(name);
	
	boolean around(String name)
	:isTagExistPT(name){
		System.out.println("replaced by AOP...isTagExistPT: " + thisJoinPoint);
		
		String tagName = this.parseTagName(name);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key projectkey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		ProjectDataStore projectDS = pm.getObjectById(ProjectDataStore.class, projectkey);
		List<TagDataStore> tagDSList = projectDS.getTags();
		boolean isTagExist = false;
		for( TagDataStore tag:tagDSList ){
			if(tag.getTagName().equals(tagName)){
				isTagExist = true;
				break;
			}
		}
		return isTagExist;
	}
}