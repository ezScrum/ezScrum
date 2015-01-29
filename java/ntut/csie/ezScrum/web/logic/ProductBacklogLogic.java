package ntut.csie.ezScrum.web.logic;

import java.sql.SQLException;
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
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
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
	public IStory[] getStories() {
		try {
			ArrayList<IStory> list = mProductBacklogMapper.getAllStoriesByProjectName();
			list = sortStories(list, ScrumEnum.IMPORTANCE, false);
			return list.toArray(new IStory[list.size()]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new IStory[0];
	}

	/**
	 * get stories ,default = importance high to low polymorphism get stories by signed and situation
	 * @param release
	 * @return
	 */
	public IStory[] getStoriesByRelease(IReleasePlanDesc release) {
		// get Story back and sort it by importance
		String releaseId = release.getID();
		ArrayList<IStory> list = mProductBacklogMapper.connectToGetStoryByRelease(releaseId, null);
		list = sortStories(list, ScrumEnum.IMPORTANCE, false);
		return list.toArray(new IStory[list.size()]);
	}

	/**
	 * Unclosed Issues 根據IMPORTANCE排順序
	 * @param category
	 */
	public IStory[] getUnclosedIssues(String category) throws SQLException {
		ArrayList<IStory> list = mProductBacklogMapper.getUnclosedIssues(category);
		list = sortStories(list, ScrumEnum.IMPORTANCE, false);
		return list.toArray(new IStory[list.size()]);
	}

	/**
	 * Filter Type : 1. null 2. BACKLOG 3. DETAIL 4. DONE
	 * 
	 * @param filterType
	 * @return
	 */
	public IStory[] getStoriesByFilterType(String filterType) {
		IStory[] storyList = getStories();
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter(filterType, storyList);
		IStory[] stories = filter.getStories();						// 回傳過濾後的 Stories
		return stories;
	}

	/************************************************************
	 * 將列表中的 Issue ID 都加入此 Sprint 之下
	 * @param issue2
	 *************************************************************/
	public void addIssueToSprint(List<Long> issueIdList, String sprintId) {
		for (long issueId : issueIdList) {
			IIssue issue = mProductBacklogMapper.getIssue(issueId);
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
				mProductBacklogMapper.updateIssueValue(issue, false);
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
			IIssue issue = mProductBacklogMapper.getIssue(issueId);

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
				mProductBacklogMapper.updateIssueValue(issue, false);
				mProductBacklogMapper.updateStoryRelation(issueId, releaseId, issue.getSprintID(), null, null, current);
			}
		}
	}

	/**
	 * 1. 移除 Story 和 Release 的關係 2. remove <Release/> tag to the issues
	 * @param issueId
	 */
	public void removeReleaseTagFromIssue(long issueId) {
		IIssue issue = mProductBacklogMapper.getIssue(issueId);

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
		mProductBacklogMapper.updateIssueValue(issue, true);
		mProductBacklogMapper.updateStoryRelation(issueId, "-1", issue.getSprintID(), null, null, current);
	}

	/**
	 * 移除Story和Story的關係
	 * @param issueId
	 */
	public void removeStoryFromSprint(long issueId) {
		IIssue issue = mProductBacklogMapper.getIssue(issueId);

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
		mProductBacklogMapper.updateIssueValue(issue, true);
		mProductBacklogMapper.updateStoryRelation(issueId, issue.getReleaseID(), ScrumEnum.DIGITAL_BLANK_VALUE, null, null, current);
	}

	/**
	 * release plan select stories 2010.06.02 by taoyu modify
	 * @return
	 */
	public ArrayList<IStory> getAddableStories() throws SQLException {
		IStory[] issues = getUnclosedIssues(ScrumEnum.STORY_ISSUE_TYPE);

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
		IStory[] issues = getUnclosedIssues(ScrumEnum.STORY_ISSUE_TYPE);

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

	private ArrayList<IStory> sortStories(ArrayList<IStory> stories, String type, boolean desc) {
		ArrayList<IStory> sortedList = new ArrayList<IStory>();
		// ID & Importance 為數字排序
		if (type.compareTo(ScrumEnum.ID_ATTR) == 0 || type.compareTo(ScrumEnum.IMPORTANCE) == 0) {
			if (desc) {
				// 數字從小到大
				for (IStory issue : stories) {
					int index = 0;
					int valueSource = 0;
					if (issue.getValueByType(type) != null) {
						valueSource = Integer.parseInt(issue.getValueByType(type));
					}
					for (IStory sortedIssue : sortedList) {
						int valueTarget = 0;
						if (sortedIssue.getValueByType(type) != null) {
							valueTarget = Integer.parseInt(sortedIssue.getValueByType(type));
						}
						if (valueSource < valueTarget) {
							break;
						}
						index++;
					}
					sortedList.add(index, issue);
				}
			} else {
				// 數字從大到小
				for (IStory issue : stories) {
					int index = 0;
					int valueSource = 0;
					if (issue.getValueByType(type) != null) {
						valueSource = Integer.parseInt(issue.getValueByType(type));
					}
					for (IStory sortedIssue : sortedList) {
						int valueTarget = 0;
						if (sortedIssue.getValueByType(type) != null) {
							valueTarget = Integer.parseInt(sortedIssue.getValueByType(type));
						}
						if (valueSource > valueTarget) {
							break;
						}
						index++;
					}
					sortedList.add(index, issue);
				}
			}
		} else {
			sortedList.addAll(stories);
			if (!sortedList.isEmpty()) {
				if (desc) {
					insertionSort(sortedList, type); // 遞增
				}
				else {
					insertionSort_asc(sortedList, type); // 遞減
				}
			}
		}
		return sortedList;
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
