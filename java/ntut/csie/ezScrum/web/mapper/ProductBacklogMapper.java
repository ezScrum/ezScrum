package ntut.csie.ezScrum.web.mapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataInfo.StoryInfo;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;

public class ProductBacklogMapper {
	private ProjectObject mProject;
	private MantisService mMantisService;

	public ProductBacklogMapper(ProjectObject project) {
		mProject = project;
		Configuration config = new Configuration();
		mMantisService = new MantisService(config);
	}
	
	public ArrayList<StoryObject> getUnclosedStories() {
		ArrayList<StoryObject> unclosedStories = new ArrayList<StoryObject>();
		ArrayList<StoryObject> stories = mProject.getStories();
		for (StoryObject story : stories) {
			if (story.getStatus() != StoryObject.STATUS_DONE) {
				unclosedStories.add(story);
			}
		}
		return unclosedStories;
	}

	public void updateStoryRelation(long storyId, long sprintId, int estimate, int importance, Date date) {
		StoryObject story = StoryObject.get(storyId);
		story.setSprintId(sprintId)
		     .setEstimate(estimate)
		     .setImportance(importance)
		     .save(date.getTime());
	}

	// get all stories by release
	public ArrayList<StoryObject> getStoriesByRelease(String releaseId) {
		ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(mProject);
		IReleasePlanDesc releasePlan = releasePlanHelper.getReleasePlan(releaseId);
		
		ArrayList<StoryObject> storie = new ArrayList<StoryObject>();

		for (ISprintPlanDesc sprint : releasePlan.getSprintDescList()) {
			ArrayList<StoryObject> storiesInSprint = StoryObject.getStoriesBySprintId(Long.parseLong(sprint.getID()));
			for (StoryObject story : storiesInSprint) {
				storie.add(story);
			}
		}
		return storie;
	}

	public StoryObject getStory(long id) {
		return StoryObject.get(id);
	}
	
	public void updateStory(StoryInfo storyInfo) {
		ArrayList<Long> tagsId = getTagsIdByTagsName(storyInfo.tags);
		StoryObject story = StoryObject.get(storyInfo.id);
		story.setName(storyInfo.name)
	         .setEstimate(storyInfo.estimate)
	         .setImportance(storyInfo.importance)
	         .setNotes(storyInfo.notes)
	         .setHowToDemo(storyInfo.howToDemo)
	         .setSprintId(storyInfo.sprintId)
	         .setStatus(storyInfo.status)
	         .setValue(storyInfo.value)
	         .setTags(tagsId)
	         .save();
	}

	public StoryObject addStory(long projectId, StoryInfo storyInfo) {
		ArrayList<Long> tagsId = getTagsIdByTagsName(storyInfo.tags);
		StoryObject story = new StoryObject(projectId);
		story.setName(storyInfo.name)
		     .setEstimate(storyInfo.estimate)
		     .setImportance(storyInfo.importance)
		     .setNotes(storyInfo.notes)
		     .setHowToDemo(storyInfo.howToDemo)
		     .setSprintId(storyInfo.sprintId)
		     .setStatus(storyInfo.status)
		     .setValue(storyInfo.value)
		     .setTags(tagsId)
		     .save();
		System.out.println(story);
		return getStory(story.getId());
	}

	public void modifyStoryName(long storyId, String name, Date modifyDate) {
		StoryObject story = getStory(storyId);

		if ((story != null) && (!story.getName().equals(name))) {
			story.setName(name);
			story.save(modifyDate.getTime());
		}
	}
	
	// delete story 用
	public void deleteStory(long storyId) {
		StoryObject story = StoryObject.get(storyId);
		if(story != null){
			story.delete();
		}
	}

	public void removeTask(long taskId, long parentId) {
		StoryObject story = StoryObject.get(parentId);
		ArrayList<TaskObject> tasks = story.getTasks();
		for(TaskObject taskObject : tasks){
			if(taskObject.getId() == taskId){
				taskObject.setStoryId(TaskObject.NO_PARENT);
				taskObject.save();
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
		if(tag != null){
			tag.delete();
		}
	}

	// 取得自訂分類標籤列表
	public ArrayList<TagObject> getTags() {
		ProjectObject project = ProjectObject.get(mProject.getName());
		ArrayList<TagObject> tags = project.getTags();
		return tags;
	}

	// 對Story設定自訂分類標籤
	public void addTagToStory(long storyId, long tagId) {
		StoryObject story = StoryObject.get(storyId);
		if(story != null){
			story.addTag(tagId);
			story.save();
		}
	}

	// 移除Story的自訂分類標籤
	public void removeTagFromStory(long storyId, long tagId) {
		StoryObject story = StoryObject.get(storyId);
		if(story != null){
			story.removeTag(tagId);
			story.save();
		}
	}

	public void updateTag(long tagId, String tagName) {
		TagObject tag = TagObject.get(tagId);
		if(tag != null){
			tag.setName(tagName);
			tag.save();
		}
	}

	public boolean isTagExisting(String name) {
		ProjectObject project = ProjectObject.get(mProject.getName());
		TagObject tag = project.getTagByName(name);

		if (tag != null) {
			return true;
		}
		return false;
	}

	public TagObject getTagByName(String name) {
		ProjectObject project = ProjectObject.get(mProject.getName());
		return project.getTagByName(name);
	}

	public long addAttachFile(AttachFileInfo attachFileInfo) {
		mMantisService.openConnect();
		long id = mMantisService.addAttachFile(attachFileInfo);
		mMantisService.closeConnect();
		return id;
	}

	// for ezScrum v1.8
	public void deleteAttachFile(long fileId) {
		mMantisService.openConnect();
		mMantisService.deleteAttachFile(fileId);
		mMantisService.closeConnect();
	}

	/**
	 * 抓取attach file for ezScrum v1.8
	 */
	public AttachFileObject getAttachfile(long fileId) {
		mMantisService.openConnect();
		AttachFileObject attachFiles = mMantisService.getAttachFile(fileId);
		mMantisService.closeConnect();
		return attachFiles;
	}
	
	private ArrayList<Long> getTagsIdByTagsName(String originTagsName){
		// process story info tag String
		ArrayList<Long> tagsId = new ArrayList<Long>();
		if (originTagsName != null && originTagsName.length() > 0) {
			for (String tagName : originTagsName.split(",")) {
				TagObject tag = TagObject.get(tagName);
				if (tag != null) {
					tagsId.add(tag.getId());
				}
			}
		}
		return tagsId;
	}
	
	public ArrayList<StoryObject> getStories() {
		return mProject.getStories();
	}
	
	// will remove
	@ Deprecated
	public IIssue[] getIssues(String category) throws SQLException {
		mMantisService.openConnect();
		IIssue[] issues = mMantisService.getIssues(mProject.getName(), category);
		mMantisService.closeConnect();
		return issues;
	}
	
	// will remove
	@ Deprecated
	public void updateIssueValue(IIssue newIssue, boolean addHistory) {
		long issueId = newIssue.getIssueID();
		IIssue oldIssue = getIssue(issueId);
		
		if (addHistory) {
			if (!newIssue.getSummary().equals(oldIssue.getSummary())) {
				addHistory(issueId, newIssue.getIssueType(), HistoryObject.TYPE_NAME,
						oldIssue.getSummary(), newIssue.getSummary());
			}
			if (!newIssue.getValue().equals(oldIssue.getValue())) {
				addHistory(issueId, newIssue.getIssueType(), HistoryObject.TYPE_VALUE,
						oldIssue.getValue(), newIssue.getValue());
			}
			if (!newIssue.getImportance().equals(oldIssue.getImportance())) {
				addHistory(issueId, newIssue.getIssueType(), HistoryObject.TYPE_IMPORTANCE,
						oldIssue.getImportance(), newIssue.getImportance());
			}
			if (!newIssue.getEstimated().equals(oldIssue.getEstimated())) {
				addHistory(issueId, newIssue.getIssueType(), HistoryObject.TYPE_ESTIMATE,
						oldIssue.getEstimated(), newIssue.getEstimated());
			}
			if (!newIssue.getHowToDemo().equals(oldIssue.getHowToDemo())) {
				addHistory(issueId, newIssue.getIssueType(), HistoryObject.TYPE_HOWTODEMO,
						oldIssue.getHowToDemo(), newIssue.getHowToDemo());
			}
			if (!newIssue.getNotes().equals(oldIssue.getNotes())) {
				addHistory(issueId, newIssue.getIssueType(), HistoryObject.TYPE_NOTE,
						oldIssue.getNotes(), newIssue.getNotes());
			}
		}
		mMantisService.openConnect();
		mMantisService.updateBugNote(newIssue);
		mMantisService.closeConnect();
	}
	
	// will remove
	@ Deprecated
	public void addHistory(long issueId, int issueType, int historyType,
			String oldValue, String newValue) {
		HistoryObject history = new HistoryObject(issueId, issueType, historyType,
				oldValue, newValue, System.currentTimeMillis());
		history.save();
	}
	
	// will remove
	@ Deprecated
	public IIssue getIssue(long id) {
		mMantisService.openConnect();
		IIssue issue = mMantisService.getIssue(id);
		mMantisService.closeConnect();
		return issue;
	}
	
	// will remove
	@ Deprecated
	public void updateStoryRelation(long issueId, String releaseId,
			String sprintId, String estimate, String importance, Date date) {
		mMantisService.openConnect();
		mMantisService.updateStoryRelationTable(issueId, mProject.getName(),
				releaseId, sprintId, estimate, importance, date);
		mMantisService.closeConnect();
	}
}
