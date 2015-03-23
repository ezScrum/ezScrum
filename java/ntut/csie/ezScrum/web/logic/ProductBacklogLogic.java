package ntut.csie.ezScrum.web.logic;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.iteration.support.filter.AProductBacklogFilter;
import ntut.csie.ezScrum.iteration.support.filter.ProductBacklogFilterFactory;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.jdom.Element;

public class ProductBacklogLogic {
	private IProject mProject;
	private ProductBacklogMapper mProductBacklogMapper;

	public ProductBacklogLogic(IProject project) {
		mProject = project;
		mProductBacklogMapper = new ProductBacklogMapper(mProject);
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
	public ArrayList<StoryObject> getUnclosedStories() {
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
	public ArrayList<StoryObject> getStoriesByFilterType(String filterType) {
		ArrayList<StoryObject> allStories = getStories();
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter(filterType, allStories);
		ArrayList<StoryObject> filteredStories = filter.getStories();						// 回傳過濾後的 Stories
		return filteredStories;
	}

	/************************************************************
	 * 將列表中的 Issue ID 都加入此 Sprint 之下
	 * @param issue2
	 *************************************************************/
	public void addStoriesToSprint(ArrayList<Long> storiesId, long sprintId) {
		for (long issueId : storiesId) {
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
	 * 移除Story和Story的關係
	 * @param issueId
	 */
	public void removeStoryFromSprint(long issueId) {
		StoryObject story = mProductBacklogMapper.getStory(issueId);
		story.setSprintId(-1);
		story.save();
	}

	/**
	 * release plan select stories 2010.06.02 by taoyu modify
	 * @return
	 */
	public ArrayList<StoryObject> getAddableStories() {
		ArrayList<StoryObject> allStories = getUnclosedStories();
		// 不能直接使用Arrays.asList,因為沒有實作到remove,所以必須要使用Arrays
		ArrayList<StoryObject> stories = new ArrayList<StoryObject>();
		for (StoryObject story : allStories) {
			long sprintId = story.getSprintId();
			// 此 story ID 有包含於 sprint ID，則不列入 list
			if (sprintId > 0) {
				continue;
			}
			stories.add(story);
		}
		return stories;
	}

	/**
	 * sprint backlog select stories 2009.12.18 by chiachi
	 * 
	 * @param sprintId
	 */
	public ArrayList<StoryObject> getAddableStories(String sprintId) {
		ArrayList<StoryObject> allStories = getUnclosedStories();
		// 不能直接使用Arrays.asList,因為沒有實作到remove,所以必須要使用Arrays
		ArrayList<StoryObject> stories = new ArrayList<StoryObject>();
		for (StoryObject story : allStories) {
			long sprintIdInStory = story.getSprintId();
			// 此 story 有包含 sprint ID，則不列入 list
			if (sprintIdInStory > 0) {
				continue;
			}
			stories.add(story);
		}
		return stories;
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
}
