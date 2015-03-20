package ntut.csie.ezScrum.web.logic;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class ProductBacklogLogic {
	private IUserSession mUserSession;
	private IProject mProject;
	private ProductBacklogMapper mProductBacklogMapper;

	public ProductBacklogLogic(IUserSession session, IProject project) {
		mUserSession = session;
		mProject = project;
		mProductBacklogMapper = new ProductBacklogMapper(mProject, mUserSession);
	}

	/**
	 * get stories ,default = importance high to low polymorphism get stories by signed and situation
	 * 
	 * @return
	 */
	public ArrayList<StoryObject> getStories() {
		ArrayList<StoryObject> stories = mProductBacklogMapper.getStories();
		stories = sortStoriesByImportance(stories);
		return stories;
	}

	/**
	 * get stories ,default = importance high to low polymorphism get stories by signed and situation
	 * @param release
	 * @return
	 */
	public ArrayList<StoryObject> getStoriesByRelease(IReleasePlanDesc release) {
		// get Story back and sort it by importance
		String releaseId = release.getID();
		ArrayList<StoryObject> stories = mProductBacklogMapper.getStoryByRelease(releaseId, null);
		stories = sortStoriesByImportance(stories);
		return stories;
	}

	/**
	 * Unclosed Issues 根據IMPORTANCE排順序
	 * @param category
	 */
	public ArrayList<StoryObject> getUnclosedStories(String category) throws SQLException {
		ArrayList<StoryObject> stories = mProductBacklogMapper.getUnclosedStories();
		stories = sortStoriesByImportance(stories);
		return stories;
	}

	/**
	 * Filter Type : 1. null 2. BACKLOG 3. DETAIL 4. DONE
	 * 
	 * @param filterType
	 * @return
	 */
	public IStory[] getStoriesByFilterType(String filterType) {
		ArrayList<StoryObject> allStories = getStories();
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter(filterType, allStories);
		IStory[] stories = filter.getStories();						// 回傳過濾後的 Stories
		return stories;
	}

	/************************************************************
	 * 將列表中的 Issue ID 都加入此 Sprint 之下
	 * @param issue2
	 *************************************************************/
	public void addIssueToSprint(List<Long> issueIdList, String sprintId) {
		for (long issueId : issueIdList) {
			IIssue issue = mProductBacklogMapper.getStory(issueId);
			String oldSprintId = issue.getSprintID();
			
			if (sprintId != null && !sprintId.equals("") &&
					Integer.parseInt(sprintId) >= 0) {

				// history node
				Element history = new Element(ScrumEnum.HISTORY_TAG);

				Date current = new Date();
				String dateTime = DateUtil.format(current, DateUtil._16DIGIT_DATE_TIME_2);
				history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, dateTime);

				// iteration node
				Element iteration = new Element(ScrumEnum.SPRINT_ID);
				iteration.setText(sprintId);
				history.addContent(iteration);
				issue.addTagValue(history);

				// 最後將修改的結果更新至DB
				mProductBacklogMapper.updateStory(issue, false);
				mProductBacklogMapper.addHistory(issue.getIssueID(), issue.getIssueType(), HistoryObject.TYPE_APPEND, oldSprintId, sprintId);
				// 將Stroy與Srpint對應的關係增加到StoryRelationTable
				mProductBacklogMapper.updateStoryRelation(issueId, issue.getReleaseID(), sprintId, null, null, current);
			}
		}
	}

	/**
	 * 新增Story和Release的關係 add <Release/> tag to the issues
	 * 
	 * @param issueList
	 * @param releaseId
	 */
	public void addReleaseTagToIssue(List<Long> issueList, String releaseId) {
		for (long issueId : issueList) {
			IIssue issue = mProductBacklogMapper.getStory(issueId);

			if (releaseId != null && !releaseId.equals("") && Integer.parseInt(releaseId) >= 0) {
				// history node
				Element history = new Element(ScrumEnum.HISTORY_TAG);
				Date current = new Date();
				history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(current, DateUtil._16DIGIT_DATE_TIME_2));

				// release node
				Element release = new Element(ScrumEnum.RELEASE_TAG);
				release.setText(releaseId);
				history.addContent(release);
				issue.addTagValue(history);

				// 最後將修改的結果更新至DB
				mProductBacklogMapper.updateStory(issue, false);
				mProductBacklogMapper.updateStoryRelation(issueId, releaseId, issue.getSprintID(), null, null, current);
			}
		}
	}

	/**
	 * 1. 移除 Story 和 Release 的關係 2. remove <Release/> tag to the issues
	 * @param issueId
	 */
	public void removeReleaseTagFromIssue(long issueId) {
		IIssue issue = mProductBacklogMapper.getStory(issueId);

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
		mProductBacklogMapper.updateStory(issue, true);
		mProductBacklogMapper.updateStoryRelation(issueId, "-1", issue.getSprintID(), null, null, current);
	}

	/**
	 * 移除Story和Story的關係
	 * @param issueId
	 */
	public void removeStoryFromSprint(long issueId) {
		IIssue issue = mProductBacklogMapper.getStory(issueId);

		// history node
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		Date current = new Date();
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(new Date(), DateUtil._16DIGIT_DATE_TIME_2));

		// iteration node
		Element iteration = new Element(ScrumEnum.SPRINT_ID);
		iteration.setText(ScrumEnum.DIGITAL_BLANK_VALUE);
		history.addContent(iteration);
		issue.addTagValue(history);

		// 最後將修改的結果更新至DB
		mProductBacklogMapper.updateStory(issue, true);
		mProductBacklogMapper.updateStoryRelation(issueId, issue.getReleaseID(), ScrumEnum.DIGITAL_BLANK_VALUE, null, null, current);
	}

	/**
	 * release plan select stories 2010.06.02 by taoyu modify
	 * @return
	 */
	public ArrayList<IStory> getAddableStories() throws SQLException {
		IStory[] issues = getUnclosedStories(ScrumEnum.STORY_ISSUE_TYPE);

		// 不能直接使用Arrays.asList,因為沒有實作到remove,所以必須要使用Arrays
		ArrayList<IStory> stories = new ArrayList<IStory>();

		for (IStory issue : issues) {
			String story_SID = issue.getSprintID();
			String story_RID = issue.getReleaseID();

			// 此 story ID 有包含於 sprint ID，則不列入 list
			if ((story_SID != null) && (Integer.parseInt(story_SID) > 0)) {
				continue;
			}

			// 此 story ID 有 release ID，則不列入 list
			if ((story_RID != null) && (Integer.parseInt(story_RID) > 0)) {
				continue;
			}

			stories.add(issue);
		}
		return stories;
	}

	/**
	 * sprint backlog select stories 2009.12.18 by chiachi
	 * 
	 * @param sprintId
	 * @param releaseId
	 */
	public ArrayList<IStory> getAddableStories(String sprintId, String releaseId) throws SQLException {
		IStory[] issues = getUnclosedStories(ScrumEnum.STORY_ISSUE_TYPE);

		// 不能直接使用Arrays.asList,因為沒有實作到remove,所以必須要使用Arrays
		ArrayList<IStory> list = new ArrayList<IStory>();

		for (IStory issue : issues) {
			String story_SID = issue.getSprintID();
			String story_RID = issue.getReleaseID();

			// 此 story 有包含 sprint ID，則不列入 list
			if ((story_SID != null) && (Integer.parseInt(story_SID) > 0)) {
				continue;
			}

			// 此 story 有 包含非本release ID，，則不列入 list
			if ((story_RID != null) && (Integer.parseInt(story_RID) > 0)) {
				if (!story_RID.equals(releaseId)) continue;
			}
			list.add(issue);
		}
		return list;
	}
	
	private ArrayList<StoryObject> sortStoriesByImportance(ArrayList<StoryObject> stories) {
		Collections.sort(stories, new Comparator<StoryObject>() {
			@Override
			public int compare(StoryObject story1, StoryObject story2) {
				return story1.getImportance() - story2.getImportance();
			}
		});
		return stories;
	}

	private void insertionSort_asc(List<IStory> sortedList, String type) {
		int length = sortedList.size();
		int firstOutOfOrder, location;
		IStory temp;

		for (firstOutOfOrder = 1; firstOutOfOrder < length; firstOutOfOrder++) { // Starts at second term, goes until the end of the array.
			String firstValue = sortedList.get(firstOutOfOrder).getValueByType(type);
			String secondValue = sortedList.get(firstOutOfOrder - 1).getValueByType(type);

			if ((firstValue.compareTo(secondValue)) > 0) { // If the two are out of order, we move the element to its rightful place.
				temp = sortedList.get(firstOutOfOrder);
				location = firstOutOfOrder;

				do { // Keep moving down the array until we find exactly where it's supposed to go.
					sortedList.set(location, sortedList.get(location - 1));
					location--;
				} while (location > 0 && (sortedList.get(location - 1).getValueByType(type).compareTo(temp.getValueByType(type))) < 0);

				sortedList.set(location, temp);
			}
		}
	}

	private void insertionSort(List<IStory> sortedList, String type) {
		int length = sortedList.size();
		int firstOutOfOrder, location;
		IStory temp;

		for (firstOutOfOrder = 1; firstOutOfOrder < length; firstOutOfOrder++) { // Starts at second term, goes until the end of the array.
			String firstValue = sortedList.get(firstOutOfOrder).getValueByType(type);
			String secondValue = sortedList.get(firstOutOfOrder - 1).getValueByType(type);

			if (firstValue.compareTo(secondValue) < 0) { // If the two are out of order, we move the element to its rightful place.
				temp = sortedList.get(firstOutOfOrder);
				location = firstOutOfOrder;

				do { // Keep moving down the array until we find exactly where it's supposed to go.
					sortedList.set(location, sortedList.get(location - 1));
					location--;
				} while (location > 0 && sortedList.get(location - 1).getValueByType(type).compareTo(temp.getValueByType(type)) > 0);

				sortedList.set(location, temp);
			}
		}
	}
}
