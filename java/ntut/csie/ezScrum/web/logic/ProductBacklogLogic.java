package ntut.csie.ezScrum.web.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.support.filter.AProductBacklogFilter;
import ntut.csie.ezScrum.iteration.support.filter.ProductBacklogFilterFactory;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;

public class ProductBacklogLogic {
	private ProjectObject mProject;
	private ProductBacklogMapper mProductBacklogMapper;

	public ProductBacklogLogic(ProjectObject project) {
		mProject = project;
		mProductBacklogMapper = new ProductBacklogMapper(mProject);
	}

	/**
	 * Get stories ,default = importance high to low polymorphism get stories by signed and situation
	 * 
	 * @return
	 */
	public ArrayList<StoryObject> getStories() {
		ArrayList<StoryObject> stories = mProductBacklogMapper.getStories();
		stories = sortStoriesByImportance(stories);
		return stories;
	}

	/**
	 * Get stories ,default = importance high to low polymorphism get stories by signed and situation
	 * @param release
	 * @return
	 */
	public ArrayList<StoryObject> getStoriesByRelease(IReleasePlanDesc release) {
		// get Story back and sort it by importance
		String releaseId = release.getID();
		ArrayList<StoryObject> stories = mProductBacklogMapper.getStoriesByRelease(releaseId);
		stories = sortStoriesByImportance(stories);
		return stories;
	}

	/**
	 * Unclosed story 根據 IMPORTANCE 排順序
	 */
	public ArrayList<StoryObject> getUnclosedStories() {
		ArrayList<StoryObject> stories = mProductBacklogMapper.getUnclosedStories();
		stories = sortStoriesByImportance(stories);
		return stories;
	}
	
	/**
	 * 取出可以被加到 sprint 的 stories，story 為 unclosed 且沒有被加到 sprint 內
	 * @return
	 */
	public ArrayList<StoryObject> getAddableStories() {
		ArrayList<StoryObject> stories = new ArrayList<StoryObject>();
		for (StoryObject story : getUnclosedStories()) {
			if (story.getSprintId() <= 0) {
				stories.add(story);
			}
		}
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
	 * 將列表中的 Story ID 都加入此 Sprint 之下
	 *************************************************************/
	public void addStoriesToSprint(ArrayList<Long> storiesId, long sprintId) {
		for (long storyId : storiesId) {
			StoryObject story = mProductBacklogMapper.getStory(storyId);
			if (sprintId > 0 && story != null) {
				// 更新 Stroy與Srpint對應的關係
				mProductBacklogMapper.updateStoryRelation(storyId, sprintId, story.getEstimate(), story.getImportance(), new Date());
			}
		}
	}

	/**
	 * 移除 Story 和 Sprint 的關係
	 * @param storyId
	 */
	public void dropStoryFromSprint(long storyId) {
		StoryObject story = mProductBacklogMapper.getStory(storyId);
		story.setSprintId(-1);
		story.save();
	}

	/**
	 * get stories which are status new and no parent
	 * @return
	 */
	public ArrayList<StoryObject> getExistingStories() {
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
