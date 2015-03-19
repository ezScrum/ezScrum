package ntut.csie.ezScrum.web.mapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.dao.StoryDAO;
import ntut.csie.ezScrum.dao.TagDAO;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.iteration.iternal.Story;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataInfo.StoryInfo;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class ProductBacklogMapper {
	private ProjectObject mProject;
	private MantisService mMantisService;

	public ProductBacklogMapper(ProjectObject project, IUserSession userSession) {
		mProject = project;
	}

	
	public ArrayList<StoryObject> getUnclosedStories() throws SQLException {
		ArrayList<StoryObject> list = new ArrayList<StoryObject>();
		ArrayList<StoryObject> stories = StoryDAO.getInstance().getStoriesByProjectId(mProject.getId());
		for (StoryObject story : stories) {
			if (story.getStatus() != StoryObject.STATUS_DONE) {
				list.add(story);
			}
		}
		return list;
	}

	public void updateStoryRelation(long issueId, String sprintId, String estimate, String importance, Date date) {
		StoryObject story = StoryObject.get(issueId);
		story.setSprintId(Long.parseLong(sprintId))
		     .setEstimate(Integer.parseInt(estimate))
		     .setImportance(Integer.parseInt(importance))
		     .setUpdateTime(date.getTime())
		     .save();
	}

	// get all stories
	public ArrayList<StoryObject> getAllStoriesByProjectId() {
		return StoryObject.getAllStoriesByProjectId(mProject.getId());
	}

	// TODO
	// get all stories by release
	public ArrayList<IStory> connectToGetStoryByRelease(String releaseId, String sprintId) {
		mMantisService.openConnect();
		// 找出 Category 為 Story
		IIssue[] issues = mMantisService.getIssues(mProject.getName(),
				ScrumEnum.STORY_ISSUE_TYPE, releaseId, sprintId, null);

		ArrayList<IStory> list = new ArrayList<IStory>();
		for (IIssue issue : issues) {
			list.add(new Story(issue));
		}

		mMantisService.closeConnect();
		return list;
	}

	public StoryObject getStory(long id) {
		return StoryObject.get(id);
	}
	
	// TODO
	public void updateStory(StoryObject newStory, boolean addHistory) {
		long storyId = newStory.getId();
		StoryObject oldStory = getStory(storyId);
		
		if (addHistory) {
			if (!newStory.getValue() == oldStory.getValue()) {
				addHistory(issueId, newIssue.getIssueType(), HistoryObject.TYPE_VALUE,
						oldStory.getValue(), newIssue.getValue());
			}
			if (!newIssue.getImportance().equals(oldStory.getImportance())) {
				addHistory(issueId, newIssue.getIssueType(), HistoryObject.TYPE_IMPORTANCE,
						oldStory.getImportance(), newIssue.getImportance());
			}
			if (!newIssue.getEstimated().equals(oldStory.getEstimated())) {
				addHistory(issueId, newIssue.getIssueType(), HistoryObject.TYPE_ESTIMATE,
						oldStory.getEstimated(), newIssue.getEstimated());
			}
			if (!newIssue.getHowToDemo().equals(oldStory.getHowToDemo())) {
				addHistory(issueId, newIssue.getIssueType(), HistoryObject.TYPE_HOWTODEMO,
						oldStory.getHowToDemo(), newIssue.getHowToDemo());
			}
			if (!newIssue.getNotes().equals(oldStory.getNotes())) {
				addHistory(issueId, newIssue.getIssueType(), HistoryObject.TYPE_NOTE,
						oldStory.getNotes(), newIssue.getNotes());
			}
		}
	}

	// TODO
	public IIssue addStory(StoryInfo storyInformation) {
		
		mMantisService.openConnect();
		IIssue story = new Issue();

		story.setProjectID(mProject.getName());
		story.setSummary(storyInformation.getName());
		story.setDescription(storyInformation.getDescription());
		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		long storyId = mMantisService.newIssue(story);

		mMantisService.closeConnect();
		return getStory(storyId);
	}

	

	public void modifyName(long storyId, String name, Date modifyDate) {
		StoryObject story = getStory(storyId);

		if (!story.getName().equals(name)) {
			story.setName(name);
			story.setUpdateTime(modifyDate.getTime());
			story.save();
		}
	}
	
	// delete story 用
	public void deleteStory(String storyId) {
		StoryObject story = StoryObject.get(Long.parseLong(storyId));
		story.delete();
	}

	public void removeTask(long taskId, long parentId) {
		StoryObject story = StoryObject.get(parentId);
		ArrayList<TaskObject> tasks = story.getTasks();
		for(TaskObject taskObject : tasks){
			if(taskObject.getId() == taskId){
				taskObject.delete();
			}
		}
	}

	// 新增自訂分類標籤
	public long addNewTag(String name) {
		TagObject tag = new TagObject(name, mProject.getId());
		tag.save();
		return tag.getId();
	}

	// 刪除自訂分類標籤
	public void deleteTag(long id) {
		TagObject tag = TagObject.get(id);
		tag.delete();
	}

	// 取得自訂分類標籤列表
	public ArrayList<TagObject> getTags() {
		ArrayList<TagObject> tags = TagObject.getTags();
		return tags;
	}

	// 對Story設定自訂分類標籤
	public void addStoryTag(String storyId, long tagId) {
		StoryObject story = StoryObject.get(Long.parseLong(storyId));
		story.addTag(tagId);
	}

	// 移除Story的自訂分類標籤
	public void removeStoryTag(String storyId, long tagId) {
		StoryObject story = StoryObject.get(Long.parseLong(storyId));
		story.removeTag(tagId);
	}

	public void updateTag(long tagId, String tagName) {
		TagObject tag = TagObject.get(tagId);
		tag.setName(tagName);
		tag.save();
	}

	public boolean isTagExist(String name) {
		TagObject tag = TagObject.get(name);
		
		if(tag != null){
			return true;
		}
		return false;
	}

	public TagObject getTagByName(String name) {
		return TagObject.get(name);
	}

	public long addAttachFile(AttachFileInfo attachFileInfo) {
		mMantisService.openConnect();
		long id = mMantisService.addAttachFile(attachFileInfo);
		mMantisService.closeConnect();
		return id;
	}

	// TODO
	// for ezScrum v1.8
	public void deleteAttachFile(long fileId) {
		mMantisService.openConnect();
		mMantisService.deleteAttachFile(fileId);
		mMantisService.closeConnect();
	}

	// TODO
	/**
	 * 抓取attach file for ezScrum v1.8
	 */
	public AttachFileObject getAttachfile(long fileId) {
		mMantisService.openConnect();
		AttachFileObject attachFileObjects = mMantisService.getAttachFile(fileId);
		mMantisService.closeConnect();
		return attachFileObjects;
	}

	public void addHistory(long issueId, int issueType, int historyType, String oldValue, String newValue) {
		HistoryObject history = new HistoryObject(issueId, issueType, historyType, oldValue, newValue, System.currentTimeMillis());
		history.save();
	}
}
