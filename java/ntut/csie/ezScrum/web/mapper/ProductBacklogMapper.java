package ntut.csie.ezScrum.web.mapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IITSService;
import ntut.csie.ezScrum.issue.sql.service.core.ITSServiceFactory;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.iteration.iternal.Story;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.StoryInformation;
import ntut.csie.jcis.resource.core.IProject;

public class ProductBacklogMapper {

	private IProject m_project;
	private ITSServiceFactory m_itsFactory;
	private Configuration m_config;
	private IUserSession m_userSession;

	public ProductBacklogMapper(IProject project, IUserSession userSession) {
		m_project = project;
		m_userSession = userSession;

		//初始ITS的設定
		m_itsFactory = ITSServiceFactory.getInstance();
		m_config = new Configuration(m_userSession);

	}

	public List<IStory> getUnclosedIssues(String category) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		IIssue[] issues = itsService.getIssues(m_project.getName(), category);
		itsService.closeConnect();
		List<IStory> list = new ArrayList<IStory>();

		for (IIssue issue : issues) {
			if (ITSEnum.getStatus(issue.getStatus()) < ITSEnum.CLOSED_STATUS) list.add(new Story(issue));
		}
		return list;
	}

	//回傳某種 category的issue
	public IIssue[] getIssues(String category) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		IIssue[] issues = itsService.getIssues(m_project.getName(), category);
		itsService.closeConnect();
		return issues;
	}

	public void updateStoryRelation(String issueID, String releaseID, String sprintID, String estimate, String importance, Date date) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		itsService.updateStoryRelationTable(issueID, m_project.getName(), releaseID, sprintID, estimate, importance, date);
		itsService.closeConnect();
	}

	//	get all stories
	public List<IStory> getAllStoriesByProjectName() {

		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		//找出Category為Story
		/*
		IIssue[] issues = itsService.getIssues(m_project.getName(), ScrumEnum.STORY_ISSUE_TYPE);
		
		List<IStory> list = new ArrayList<IStory>();
		for (IIssue issue : issues) {
				list.add(new Story(issue));
		}
		*/

		List<IStory> issues = itsService.getStorys(m_project.getName());
		itsService.closeConnect();
		return issues;
	}

	//	get all stories by release
	public List<IStory> connectToGetStoryByRelease(String releaseID, String sprintID) {

		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		//找出Category為Story
		IIssue[] issues = itsService.getIssues(m_project.getName(), ScrumEnum.STORY_ISSUE_TYPE, releaseID, sprintID, null);

		List<IStory> list = new ArrayList<IStory>();
		for (IIssue issue : issues) {
			list.add(new Story(issue));
		}

		itsService.closeConnect();
		return list;
	}

	public IIssue getIssue(long id) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		IIssue issue = itsService.getIssue(id);
		itsService.closeConnect();
		return issue;
	}

	//	public void updateTagValue(IIssue issue) {
	public void updateIssueValue(IIssue issue) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		itsService.updateBugNote(issue);
		itsService.closeConnect();

	}

	/**
	 * 由於GAE使用
	 * 
	 * @param issue
	 * @param storyInformation
	 */
	//	public void updateIssueValue(IIssue issue, StoryInformation storyInformation) {
	//		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID,m_itsPrefs);
	//		itsService.openConnect();
	//		itsService.updateBugNote(issue);
	//		itsService.closeConnect();
	//		
	//	}

	//	public IIssue addStory(String name, String description){
	public IIssue addStory(StoryInformation storyInformation) {
		String name = storyInformation.getName();
		String description = storyInformation.getDescription();
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		IIssue story = new Issue();

		story.setProjectID(m_project.getName());
		story.setSummary(name);
		story.setDescription(description);
		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		long storyID = itsService.newIssue(story);

		itsService.closeConnect();
		return this.getIssue(storyID);
	}

	public void updateHistoryModifiedDate(long issueID, long historyID, Date date) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		itsService.updateHistoryModifiedDate(issueID, historyID, date);
		itsService.closeConnect();
	}

	public void modifyName(long storyId, String name, Date modifyDate) {
		IIssue task = this.getIssue(storyId);
		if (!task.getSummary().equals(name))
		{
			IITSService itsService = m_itsFactory.getService(m_config);
			itsService.openConnect();
			itsService.updateName(task, name, modifyDate);
			itsService.closeConnect();
		}
	}

	//delete story 用
	public void deleteStory(String ID) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		itsService.deleteStory(ID);
		itsService.closeConnect();
	}

	public void removeTask(long taskID, long parentID) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		itsService.removeRelationship(parentID, taskID, ITSEnum.PARENT_RELATIONSHIP);
		itsService.closeConnect();
	}

	// 新增自訂分類標籤
	public void addNewTag(String name) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		itsService.addNewTag(name, m_project.getName());
		itsService.closeConnect();
	}

	// 刪除自訂分類標籤
	public void deleteTag(String id) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		itsService.deleteTag(id, m_project.getName());
		itsService.closeConnect();
	}

	// 取得自訂分類標籤列表
	public IIssueTag[] getTagList() {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		IIssueTag[] tags = itsService.getTagList(m_project.getName());
		itsService.closeConnect();

		return tags;
	}

	// 對Story設定自訂分類標籤
	public void addStoryTag(String storyID, String tagID) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		itsService.addStoryTag(storyID, tagID);
		itsService.closeConnect();
	}

	// 移除Story的自訂分類標籤
	public void removeStoryTag(String storyID, String tagID) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		itsService.removeStoryTag(storyID, tagID);
		itsService.closeConnect();
	}

	public void updateTag(String tagId, String tagName) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		itsService.updateTag(tagId, tagName, m_project.getName());
		itsService.closeConnect();
	}

	public boolean isTagExist(String name) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		boolean result = itsService.isTagExist(name, m_project.getName());
		itsService.closeConnect();

		return result;
	}

	public IIssueTag getTagByName(String name) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		IIssueTag tag = itsService.getTagByName(name, m_project.getName());
		itsService.closeConnect();

		return tag;
	}

	public void addAttachFile(long issueID, String targetPath) {
		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		File attachFile = new File(targetPath);
		itsService.addAttachFile(issueID, attachFile);
		itsService.closeConnect();
	}

	public void deleteAttachFile(long fileID) {
		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		itsService.deleteAttachFile(fileID);
		itsService.closeConnect();
	}

	/**
	 * 抓取attach file ，不透過 mantis
	 */
	public File getAttachfile(String fileID) {
		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		File file = itsService.getAttachFile(fileID);
		itsService.closeConnect();
		return file;
	}

	/**
	 * 抓取attach file ，不透過 mantis，並且透過檔案名稱
	 */
	public File getAttachfileByName(String fileName) {
		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		File file = itsService.getAttachFileByName(fileName);
		itsService.closeConnect();
		return file;
	}

	public void addHistory(long issueID, String typeName, String oldValue, String newValue) {
		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		itsService.addHistory(issueID, typeName, oldValue, newValue);
		itsService.closeConnect();
	}

	// =================never use===============
	//	public void modifyTagValue(List<IIssue> list) {
	//		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID,m_itsPrefs);
	//		itsService.openConnect();
	//		for (IIssue issue : list) 
	//			itsService.updateBugNote(issue);
	//		itsService.closeConnect();
	//	}

	//	@Deprecated
	//	public IIssue[] getIssues() {
	//		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID,m_itsPrefs);
	//		itsService.openConnect();
	//		IIssue[] issues = itsService.getIssues(m_project.getName(), ScrumEnum.STORY_ISSUE_TYPE);
	//		List<IIssue> list = new ArrayList<IIssue>();
	//	
	//		for (IIssue issue : issues) {
	//			list.add(issue);
	//		}
	//	
	//		list = sort(list, ScrumEnum.IMPORTANCE);
	//		itsService.closeConnect();
	//		return list.toArray(new IIssue[list.size()]);
	//	}

	//	@Deprecated
	//	private List<IIssue> sort(List<IIssue> list, String tagName) {
	//		ArrayList<IIssue> sortedList = new ArrayList<IIssue>();
	//		for (IIssue issue : list) {
	//			int index = 0;
	//			int valueSource = 0;
	//			if (issue.getTagValue(tagName) != null)
	//				valueSource = Integer.parseInt(issue.getTagValue(tagName));
	//			for (IIssue sortedIssue : sortedList) {
	//				int valueTarget = 0;
	//				if (sortedIssue.getTagValue(tagName) != null)
	//					valueTarget = Integer.parseInt(sortedIssue
	//							.getTagValue(tagName));
	//				if (valueSource > valueTarget)
	//					break;
	//				index++;
	//			}
	//			sortedList.add(index, issue);
	//		}
	//
	//		return sortedList;
	//	}

	//	public IProject getProject() {
	//		return m_project;
	//	}

	//用來讓sort功能 convert stories
	//	private List<IStory> convertStories(List<IStory> list){
	//		ArrayList<IStory> sortedList = new ArrayList<IStory>();
	//		for(int i=0;i<list.size();i++){
	//			//從最後面的story開始add進 sortedList
	//			IStory story = list.get((list.size()-1)-i);
	//			sortedList.add(story);
	//		}
	//		return sortedList;
	//	}

	//	public IStory[] getStories(String situations){
	//	//get Story back and sort it by importance
	//	List<IStory> list = connectToGetStory();
	//	//切割出所有排序項目
	//	String[] situation = situations.split(",");
	//		
	//	//最重要的(先按的)排序項目最後排
	//	for (int i = 0; i < situation.length ; i++)
	//	{
	//		String signed = situation[i].substring(0, 1);
	//		//+ 為遞減 ; - 為遞增 
	//		if(signed.compareTo("+") == 0)
	//			list = sortStories(list, situation[i].substring(1), false);
	//		else
	//			list = sortStories(list, situation[i].substring(1), true);
	//	}
	//		
	//	return list.toArray(new IStory[list.size()]);
	//}

	//	public void updateTagValue(List<IIssue> list) {
	//	IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID,m_itsPrefs);
	//	itsService.openConnect();
	//	for (IIssue issue : list) 
	//		itsService.updateBugNote(issue);
	//	itsService.closeConnect();		
	//}

	// =================move to product backlog logic===============

	//	public IStory[] getUnclosedIssues(String category) {
	//	IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID,m_itsPrefs);
	//	itsService.openConnect();
	//	IIssue[] issues = itsService.getIssues(m_project.getName(), category);
	//	List<IStory> list = new ArrayList<IStory>();
	//
	//	for (IIssue issue : issues) {
	//		if (ITSEnum.getStatus(issue.getStatus())< ITSEnum.CLOSED_STATUS)
	//			list.add(new Story(issue));
	//	}
	//	
	//	list = sortStories(list, ScrumEnum.IMPORTANCE, false);	
	//
	//	itsService.closeConnect();
	//	return list.toArray(new IStory[list.size()]);
	//}

	//private List<IStory> sortStories(List<IStory> list, String type, boolean desc) {
	//	List<IStory> sortedList = new ArrayList<IStory>();
	//	//ID & Importance 為數字排序
	//	if(type.compareTo(ScrumEnum.ID_ATTR)==0||type.compareTo(ScrumEnum.IMPORTANCE)==0){
	//		if (desc)
	//		{
	//			//數字從小到大
	//			for (IStory issue : list) {
	//				int index = 0;
	//				int valueSource = 0;
	//				if (issue.getValueByType(type) != null)
	//					valueSource = Integer.parseInt(issue.getValueByType(type));
	//				for (IStory sortedIssue : sortedList) {
	//					int valueTarget = 0;
	//					if (sortedIssue.getValueByType(type) != null)
	//						valueTarget = Integer.parseInt(sortedIssue.getValueByType(type));
	//					if (valueSource < valueTarget)
	//						break;
	//					index++;
	//				}
	//				sortedList.add(index, issue);
	//			}
	//		}
	//		else
	//		{
	//			//數字從大到小
	//			for (IStory issue : list) {
	//				int index = 0;
	//				int valueSource = 0;
	//				if (issue.getValueByType(type) != null)
	//					valueSource = Integer.parseInt(issue.getValueByType(type));
	//				for (IStory sortedIssue : sortedList) {
	//					int valueTarget = 0;
	//					if (sortedIssue.getValueByType(type) != null)
	//						valueTarget = Integer.parseInt(sortedIssue.getValueByType(type));
	//					if (valueSource > valueTarget)
	//						break;
	//					index++;
	//				}
	//				sortedList.add(index, issue);
	//			}
	//		}
	//	}
	//	else
	//	{
	//		sortedList.addAll(list);
	//		if(!sortedList.isEmpty())
	//		{
	//			//quickSortStoriesName(sortedList,0,sortedList.size()-1,sortedList.size(),type);
	//			if (desc)	//遞增
	//				insertionSort(sortedList, type);
	//			else		//遞減
	//				insertionSort_asc(sortedList, type);
	//		}
	//	}
	//	
	//	return sortedList;
	//}
	//
	//public void insertionSort_asc(List<IStory> sortedList, String type) {
	//	int length = sortedList.size();
	//	int firstOutOfOrder, location;
	//	IStory temp;
	//    
	//    for(firstOutOfOrder = 1; firstOutOfOrder < length; firstOutOfOrder++) { //Starts at second term, goes until the end of the array.
	//    	String firstValue = sortedList.get(firstOutOfOrder).getValueByType(type);
	//    	String secondValue = sortedList.get(firstOutOfOrder - 1).getValueByType(type);
	//    	
	//        if((firstValue.compareTo(secondValue)) > 0) { //If the two are out of order, we move the element to its rightful place.
	//            temp = sortedList.get(firstOutOfOrder);
	//            location = firstOutOfOrder;
	//            
	//            do { //Keep moving down the array until we find exactly where it's supposed to go.
	//            	sortedList.set(location, sortedList.get(location - 1));
	//                location--;
	//            }
	//            while (location > 0 && (sortedList.get(location-1).getValueByType(type).compareTo(temp.getValueByType(type))) < 0);
	//            
	//            sortedList.set(location, temp);
	//        }
	//    }
	//}

	//	public void insertionSort(List<IStory> sortedList, String type) {
	//		int length = sortedList.size();
	//		int firstOutOfOrder, location;
	//		IStory temp;
	//	    
	//	    for(firstOutOfOrder = 1; firstOutOfOrder < length; firstOutOfOrder++) { //Starts at second term, goes until the end of the array.
	//	    	String firstValue = sortedList.get(firstOutOfOrder).getValueByType(type);
	//	    	String secondValue = sortedList.get(firstOutOfOrder - 1).getValueByType(type);
	//	    	
	//	        if(firstValue.compareTo(secondValue) < 0) { //If the two are out of order, we move the element to its rightful place.
	//	            temp = sortedList.get(firstOutOfOrder);
	//	            location = firstOutOfOrder;
	//	            
	//	            do { //Keep moving down the array until we find exactly where it's supposed to go.
	//	            	sortedList.set(location, sortedList.get(location - 1));
	//	                location--;
	//	            }
	//	            while (location > 0 && sortedList.get(location-1).getValueByType(type).compareTo(temp.getValueByType(type)) > 0);
	//	            
	//	            sortedList.set(location, temp);
	//	        }
	//	    }
	//	}

	// =================move to product backlog logic and product backlog mapper===============
	/*get stories ,default = importance high to low
	  polymorphism get stories by signed and situation*/
	//	public IStory[] getStories(){
	//		//get Story back and sort it by importance
	//		List<IStory> list = connectToGetStory();
	//		list = sortStories(list, ScrumEnum.IMPORTANCE, false);	
	//		return list.toArray(new IStory[list.size()]);
	//	}

	/*get stories ,default = importance high to low
	  polymorphism get stories by signed and situation*/
	//	public IStory[] getStoriesByRelease(IReleasePlanDesc release){
	//		//get Story back and sort it by importance
	//		String R_ID = release.getID();
	//		//找出 Release 底下尚未選入 Sprint 的 Stories
	//		List<IStory> list = connectToGetStoryByRelease(R_ID, null);
	//		//找出 Release 底下已選入 Sprint 的 Stories
	////		if(release.getSprints()!=null){
	////			for(String sprintID : release.getSprints())	{
	////				list.addAll(connectToGetStoryByRelease(null, sprintID));
	////			}
	////		}
	//		list = sortStories(list, ScrumEnum.IMPORTANCE, false);	
	//		return list.toArray(new IStory[list.size()]);
	//	}

}
