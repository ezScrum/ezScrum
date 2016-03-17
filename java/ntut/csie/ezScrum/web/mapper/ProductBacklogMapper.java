package ntut.csie.ezScrum.web.mapper;

import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataInfo.StoryInfo;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;

public class ProductBacklogMapper {
	private ProjectObject mProject;

	public ProductBacklogMapper(ProjectObject project) {
		mProject = project;
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
	public ArrayList<StoryObject> getStoriesByRelease(long releaseId) {
		ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(mProject);
		ReleaseObject release = releasePlanHelper.getReleasePlan(releaseId);
		
		ArrayList<StoryObject> stories = new ArrayList<StoryObject>();

		for (SprintObject sprint : release.getSprints()) {
			ArrayList<StoryObject> storiesInSprint = sprint.getStories();
			for (StoryObject story : storiesInSprint) {
				stories.add(story);
			}
		}
		return stories;
	}

	public StoryObject getStory(long id) {
		return StoryObject.get(id);
	}
	
	public StoryObject getStory(long projectId, long serialStoryId) {
		return StoryObject.get(projectId, serialStoryId);
	}
	
	public void updateStory(StoryInfo storyInfo) {
		ArrayList<Long> tagsId = getTagsIdByTagsName(storyInfo.tags);
		StoryObject story = StoryObject.get(mProject.getId(), storyInfo.serialId);
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
	
	public void deleteStory(long projectId, long serialStoryId) {
		StoryObject story = StoryObject.get(projectId, serialStoryId);
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
		AttachFileObject attachFile = new AttachFileObject();
        attachFile.setIssueId(attachFileInfo.issueId)
                  .setIssueType(attachFileInfo.issueType)
                  .setName(attachFileInfo.name)
                  .setContentType(attachFileInfo.contentType)
                  .setPath(attachFileInfo.path)
                  .save();
		return attachFile.getId();
	}

	// for ezScrum v1.8
	public void deleteAttachFile(long fileId) {
		AttachFileObject attachFile = AttachFileObject.get(fileId);
		if (attachFile != null) {
			attachFile.delete();
		}
	}

	/**
	 * 抓取attach file for ezScrum v1.8
	 */
	public AttachFileObject getAttachfile(long fileId) {
		return AttachFileObject.get(fileId);
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
}
