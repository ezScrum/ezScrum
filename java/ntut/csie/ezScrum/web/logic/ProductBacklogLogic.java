package ntut.csie.ezScrum.web.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Element;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.iteration.support.filter.AProductBacklogFilter;
import ntut.csie.ezScrum.iteration.support.filter.ProductBacklogFilterFactory;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class ProductBacklogLogic {
	
	private IUserSession userSession;
	private IProject project;
	private ProductBacklogMapper productBacklogMapper;
	
	public ProductBacklogLogic(IUserSession session, IProject project) {
		this.userSession = session;
		this.project = project;
		this.productBacklogMapper = new ProductBacklogMapper(this.project, this.userSession);
	}
	
	/**
	 * get stories ,default = importance high to low 
	 * polymorphism get stories by signed and situation
	 * @return
	 */
	public IStory[] getStories(){
		//get Story back and sort it by importance
//		List<IStory> list = connectToGetStory();
		try{
			List<IStory> list = this.productBacklogMapper.getAllStoriesByProjectName();
			list = this.sortStories(list, ScrumEnum.IMPORTANCE, false);	
			return list.toArray(new IStory[list.size()]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new IStory[0];
	}
	
	/**
	 * get stories ,default = importance high to low 
	 * polymorphism get stories by signed and situation
	 * @param release
	 * @return
	 */
	public IStory[] getStoriesByRelease(IReleasePlanDesc release){
		//get Story back and sort it by importance
		String R_ID = release.getID();
		//找出 Release 底下尚未選入 Sprint 的 Stories
//		List<IStory> list = connectToGetStoryByRelease(R_ID, null);
		
		List<IStory> list = this.productBacklogMapper.connectToGetStoryByRelease(R_ID, null);
		
		list = this.sortStories(list, ScrumEnum.IMPORTANCE, false);	
		return list.toArray(new IStory[list.size()]);
	}
	
	/**
	 * Unclosed Issues 根據IMPORTANCE排順序
	 * @param category
	 * @return
	 */
	public IStory[] getUnclosedIssues(String category) {
		List<IStory> list = this.productBacklogMapper.getUnclosedIssues(category);
		list = this.sortStories(list, ScrumEnum.IMPORTANCE, false);	
		return list.toArray(new IStory[list.size()]);
	}
	
	/**
	 * Filter Type : 
	 * 1. null
	 * 2. BACKLOG
	 * 3. DETAIL
	 * 4. DONE
	 * @param filterType
	 * @return
	 */
	public IStory[] getStoriesByFilterType(String filterType){
//    	ProductBacklogHelper pbHelper = new ProductBacklogHelper(project, userSession);
//		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter(filterType, pbHelper.getStories());
//    	IStory[] stories = filter.getStories();						// 回傳過濾後的 Stories
		IStory[] storyList = this.getStories();
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter(filterType, storyList);
    	IStory[] stories = filter.getStories();						// 回傳過濾後的 Stories
    	return stories;
	} 
	
	/************************************************************
	 * 將列表中的Issue ID都加入此Sprint之下
	 * @param issue2 
	 *************************************************************/
	public void addIssueToSprint(List<Long> list, String sprintID) {
		for (long issueID : list) {
//			IIssue issue = m_backlog.getIssue(issueID);
			IIssue issue = this.productBacklogMapper.getIssue(issueID);
			String oldSprintID = issue.getSprintID();
			if (sprintID != null && !sprintID.equals("") && Integer.parseInt(sprintID) >= 0) {

				// history node
				Element history = new Element(ScrumEnum.HISTORY_TAG);

				Date current = new Date();
				String dateTime = DateUtil.format(current, DateUtil._16DIGIT_DATE_TIME_2);
				// history.setAttribute(IIssue.TYPE_HISTORY_ATTR, IIssue.STORY_TYPE_HSITORY_VALUE);
				history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, dateTime);

				// iteration node
				Element iteration = new Element(ScrumEnum.SPRINT_ID);
				iteration.setText(sprintID);
				history.addContent(iteration);
				issue.addTagValue(history);

				// 最後將修改的結果更新至DB
//				m_backlog.updateTagValue(issue);
//				m_backlog.addHistory(issue.getIssueID(), ScrumEnum.SPRINT_TAG, oldSprintID, sprintID);
				// 將Stroy與Srpint對應的關係增加到StoryRelationTable
//				m_backlog.updateStoryRelation(Long.toString(issueID), issue.getReleaseID(), sprintID, null, null, current);
				
				// 最後將修改的結果更新至DB
				this.productBacklogMapper.updateIssueValue(issue);
				this.productBacklogMapper.addHistory(issue.getIssueID(), ScrumEnum.SPRINT_TAG, oldSprintID, sprintID);
				// 將Stroy與Srpint對應的關係增加到StoryRelationTable
				this.productBacklogMapper.updateStoryRelation(Long.toString(issueID), issue.getReleaseID(), sprintID, null, null, current);
			}
		}
	}
	
	/**
	 * 新增Story和Release的關係
	 * add <Release/> tag to the issues
	 * @param list
	 * @param releaseID
	 */
	public void addReleaseTagToIssue(List<Long> list, String releaseID) {
		for (long issueID : list) {
//			IIssue issue = m_backlog.getIssue(issueID);
			IIssue issue = this.productBacklogMapper.getIssue(issueID);

			if (releaseID != null && !releaseID.equals("") && Integer.parseInt(releaseID) >= 0) {
				// history node
				Element history = new Element(ScrumEnum.HISTORY_TAG);
				// history.setAttribute(IIssue.TYPE_HISTORY_ATTR,
				// IIssue.STORY_TYPE_HSITORY_VALUE);

				Date current = new Date();
				history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(current, DateUtil._16DIGIT_DATE_TIME_2));

				// release node
				Element release = new Element(ScrumEnum.RELEASE_TAG);
				release.setText(releaseID);
				history.addContent(release);
				issue.addTagValue(history);

				// 最後將修改的結果更新至DB
//				m_backlog.updateTagValue(issue);
				this.productBacklogMapper.updateIssueValue(issue);

				// 將Stroy與Release對應的關係增加到StoryRelationTable
//				m_backlog.updateStoryRelation(Long.toString(issueID),releaseID, issue.getSprintID(), null, null, current);
				
				this.productBacklogMapper.updateStoryRelation(Long.toString(issueID),releaseID, issue.getSprintID(), null, null, current);
			}
		}
	}
	
	/**
	 * 1. 移除Story和Release的關係
	 * 2. remove <Release/> tag to the issues
	 * @param issueID
	 */
	public void removeReleaseTagFromIssue(String issueID){
//		IIssue issue = m_backlog.getIssue(Integer.parseInt(issueID));
		IIssue issue = this.productBacklogMapper.getIssue(Integer.parseInt(issueID));

		// history node
		Element history = new Element(ScrumEnum.HISTORY_TAG);

		Date current = new Date();
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(current, DateUtil._16DIGIT_DATE_TIME_2));

		// release node
		Element release = new Element(ScrumEnum.RELEASE_TAG);
		release.setText(ScrumEnum.DIGITAL_BLANK_VALUE);
		history.addContent(release);

		issue.addTagValue(history);

		// 最後將修改的結果更新至DB
//		m_backlog.updateTagValue(issue);
		this.productBacklogMapper.updateIssueValue(issue);
		
		// 將Stroy與Release對應的關係從StoryRelationTable移除
//		m_backlog.updateStoryRelation(issueID, "-1", issue.getSprintID(), null, null, current);
		this.productBacklogMapper.updateStoryRelation(issueID, "-1", issue.getSprintID(), null, null, current);
	}
	
	/**
	 * 移除Story和Story的關係
	 * @param issueID
	 */
	public void removeStoryFromSprint(long issueID) {
//		IIssue issue = m_backlog.getIssue(issueID);
		IIssue issue = this.productBacklogMapper.getIssue(issueID);

//		if (!issue.getTagValue(ScrumEnum.SPRINT_ID).equals(ScrumEnum.DIGITAL_BLANK_VALUE)) {

			// history node
			Element history = new Element(ScrumEnum.HISTORY_TAG);
			// history.setAttribute(IIssue.TYPE_HISTORY_ATTR,
			// IIssue.STORY_TYPE_HSITORY_VALUE);

			Date current = new Date();
			history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(new Date(), DateUtil._16DIGIT_DATE_TIME_2));

			// iteration node
			Element iteration = new Element(ScrumEnum.SPRINT_ID);
			iteration.setText(ScrumEnum.DIGITAL_BLANK_VALUE);
			history.addContent(iteration);
			issue.addTagValue(history);

			// 最後將修改的結果更新至DB
			this.productBacklogMapper.updateIssueValue(issue);

			// 將Stroy與Sprint對應的關係從StoryRelationTable移除
			this.productBacklogMapper.updateStoryRelation(Long.toString(issueID), issue.getReleaseID(), ScrumEnum.DIGITAL_BLANK_VALUE, null, null, current);
//		}
	}
	
	
	/**
	 * release plan select stories 
	 * 2010.06.02 by taoyu modify
	 * @return
	 */
	public List<IStory> getAddableStories() {
		IStory[] issues = this.getUnclosedIssues(ScrumEnum.STORY_ISSUE_TYPE);
		
		// 不能直接使用Arrays.asList,因為沒有實作到remove,所以必須要使用Arrays
		List<IStory> list = new ArrayList<IStory>();
		
		for (IStory issue : issues) {
			String story_SID = issue.getSprintID();
			String story_RID = issue.getReleaseID();
			
			// 此 story ID 有包含於 sprint ID，則不列入 list
			if ( (story_SID!=null) && (Integer.parseInt(story_SID)>0) ) {
				continue;
			}
			
			// 此 story ID 有 release ID，則不列入 list
			if ( (story_RID!=null) && (Integer.parseInt(story_RID)>0) ) {
				continue;
			}

			list.add(issue);
		}
		return list;
//		return list.toArray(new IStory[list.size()]);
	}
	
	/**
	 * sprint backlog select stories
	 * 2009.12.18 by chiachi
	 * @param sprintID
	 * @param releaseID
	 * @return
	 */
	public List<IStory> getAddableStories(String sprintID, String releaseID) {
		IStory[] issues = this.getUnclosedIssues(ScrumEnum.STORY_ISSUE_TYPE);
		
		// 不能直接使用Arrays.asList,因為沒有實作到remove,所以必須要使用Arrays
		List<IStory> list = new ArrayList<IStory>();
		
		for (IStory issue : issues) {
			String story_SID = issue.getSprintID();
			String story_RID = issue.getReleaseID();
			
			// 此 story 有包含 sprint ID，則不列入 list
			if ( (story_SID!=null) && (Integer.parseInt(story_SID)>0) ) {
				continue;
			}
			
			// 此 story 有 包含非本release ID，，則不列入 list
			if ( (story_RID!=null) && (Integer.parseInt(story_RID)>0) ) {
				if (!story_RID.equals(releaseID))
					continue;				
			}
			
			list.add(issue);
		}
		return list;
//		return list.toArray(new IStory[list.size()]);
	}
	
	/**
	 * 要注意！
	 * ezScrum記錄Story的資料是以XML的方式儲存
	 * 詳細資訊在mantis_bugnote_text_table
	 * @param issueID
	 * @param name
	 * @param value
	 * @param importance
	 * @param estimation
	 * @param howToDemo
	 * @param note
	 * @return
	 */
//	public boolean editStory(long issueID, String name, String value, String importance,
//			String estimation, String howToDemo, String note){
//		IIssue issue = this.productBacklogMapper.getIssue(issueID);
//		if (history.getChildren().size() > 0) {
//			issue.addTagValue(history);
//			// 最後將修改的結果更新至DB
////			m_backlog.updateTagValue(issue);
//			this.productBacklogMapper.updateIssueValue(issue);
//
//			return true;
//		}
//		return false;
//	}
	
//	public boolean editStory(long issueID, Element history){
//		if (history.getChildren().size() > 0) {
//			IIssue issue = this.productBacklogMapper.getIssue(issueID);
//			issue.addTagValue(history);
//			// 最後將修改的結果更新至DB
////			m_backlog.updateTagValue(issue);
//			this.productBacklogMapper.updateIssueValue(issue);
//
//			return true;
//		}
//		return false;
//	}

//	public Element translateIssueToXML(String value, String importance, String estimation, String howToDemo, String note) {
//		Element history = new Element(ScrumEnum.HISTORY_TAG);
//		// history.setAttribute(IIssue.TYPE_HISTORY_ATTR,
//		// IIssue.STORY_TYPE_HSITORY_VALUE);
//		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(new Date(), DateUtil._16DIGIT_DATE_TIME_2));
//
//		if (importance != null && !importance.equals("")) {
//			Element importanceElem = new Element(ScrumEnum.IMPORTANCE);
//			int temp = (int) Float.parseFloat(importance);
//			importanceElem.setText(temp + "");
//			history.addContent(importanceElem);
//		}
//
//		if (estimation != null && !estimation.equals("")) {
//			Element storyPoint = new Element(ScrumEnum.ESTIMATION);
//			storyPoint.setText(estimation);
//			history.addContent(storyPoint);
//		}
//		
//		if(value != null && !value.equals(""))
//		{
//			Element customValue = new Element(ScrumEnum.VALUE);
//			customValue.setText(value);
//			history.addContent(customValue);
//		}
//		Element howToDemoElem = new Element(ScrumEnum.HOWTODEMO);
//		howToDemoElem.setText(howToDemo);
//		history.addContent(howToDemoElem);
//
//		Element notesElem = new Element(ScrumEnum.NOTES);
//		notesElem.setText(note);
//		history.addContent(notesElem);
//		return history;
//	}
	
	
	private List<IStory> sortStories(List<IStory> list, String type, boolean desc) {
		List<IStory> sortedList = new ArrayList<IStory>();
		//ID & Importance 為數字排序
		if(type.compareTo(ScrumEnum.ID_ATTR)==0||type.compareTo(ScrumEnum.IMPORTANCE)==0){
			if (desc)
			{
				//數字從小到大
				for (IStory issue : list) {
					int index = 0;
					int valueSource = 0;
					if (issue.getValueByType(type) != null)
						valueSource = Integer.parseInt(issue.getValueByType(type));
					for (IStory sortedIssue : sortedList) {
						int valueTarget = 0;
						if (sortedIssue.getValueByType(type) != null)
							valueTarget = Integer.parseInt(sortedIssue.getValueByType(type));
						if (valueSource < valueTarget)
							break;
						index++;
					}
					sortedList.add(index, issue);
				}
			}
			else
			{
				//數字從大到小
				for (IStory issue : list) {
					int index = 0;
					int valueSource = 0;
					if (issue.getValueByType(type) != null)
						valueSource = Integer.parseInt(issue.getValueByType(type));
					for (IStory sortedIssue : sortedList) {
						int valueTarget = 0;
						if (sortedIssue.getValueByType(type) != null)
							valueTarget = Integer.parseInt(sortedIssue.getValueByType(type));
						if (valueSource > valueTarget)
							break;
						index++;
					}
					sortedList.add(index, issue);
				}
			}
		}
		else
		{
			sortedList.addAll(list);
			if(!sortedList.isEmpty())
			{
				//quickSortStoriesName(sortedList,0,sortedList.size()-1,sortedList.size(),type);
				if (desc)	//遞增
					this.insertionSort(sortedList, type);
				else		//遞減
					this.insertionSort_asc(sortedList, type);
			}
		}
		
		return sortedList;
	}
	
	private void insertionSort_asc(List<IStory> sortedList, String type) {
		int length = sortedList.size();
		int firstOutOfOrder, location;
		IStory temp;
	    
	    for(firstOutOfOrder = 1; firstOutOfOrder < length; firstOutOfOrder++) { //Starts at second term, goes until the end of the array.
	    	String firstValue = sortedList.get(firstOutOfOrder).getValueByType(type);
	    	String secondValue = sortedList.get(firstOutOfOrder - 1).getValueByType(type);
	    	
	        if((firstValue.compareTo(secondValue)) > 0) { //If the two are out of order, we move the element to its rightful place.
	            temp = sortedList.get(firstOutOfOrder);
	            location = firstOutOfOrder;
	            
	            do { //Keep moving down the array until we find exactly where it's supposed to go.
	            	sortedList.set(location, sortedList.get(location - 1));
	                location--;
	            }
	            while (location > 0 && (sortedList.get(location-1).getValueByType(type).compareTo(temp.getValueByType(type))) < 0);
	            
	            sortedList.set(location, temp);
	        }
	    }
	}
	
	private void insertionSort(List<IStory> sortedList, String type) {
		int length = sortedList.size();
		int firstOutOfOrder, location;
		IStory temp;
	    
	    for(firstOutOfOrder = 1; firstOutOfOrder < length; firstOutOfOrder++) { //Starts at second term, goes until the end of the array.
	    	String firstValue = sortedList.get(firstOutOfOrder).getValueByType(type);
	    	String secondValue = sortedList.get(firstOutOfOrder - 1).getValueByType(type);
	    	
	        if(firstValue.compareTo(secondValue) < 0) { //If the two are out of order, we move the element to its rightful place.
	            temp = sortedList.get(firstOutOfOrder);
	            location = firstOutOfOrder;
	            
	            do { //Keep moving down the array until we find exactly where it's supposed to go.
	            	sortedList.set(location, sortedList.get(location - 1));
	                location--;
	            }
	            while (location > 0 && sortedList.get(location-1).getValueByType(type).compareTo(temp.getValueByType(type)) > 0);
	            
	            sortedList.set(location, temp);
	        }
	    }
	}
	
//	public IStory[] getStories(String situations){
//	//get Story back and sort it by importance
//	List<IStory> list = this.productacklogMapper.connectToGetStory();
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
	
}
