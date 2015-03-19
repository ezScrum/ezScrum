package ntut.csie.ezScrum.web.mapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.dao.StoryDAO;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataInfo.StoryInfo;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.jcis.resource.core.IProject;

public class ProductBacklogMapper {
	private IProject mProject;
	private MantisService mMantisService;

	public ProductBacklogMapper(IProject project, IUserSession userSession) {
		mProject = project;
	}
	
	public ArrayList<StoryObject> getUnclosedStories() throws SQLException {
		ProjectObject project = ProjectObject.get(mProject.getName());
		ArrayList<StoryObject> list = new ArrayList<StoryObject>();
		ArrayList<StoryObject> stories = StoryDAO.getInstance().getStoriesByProjectId(project.getId());
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

	// get all stories by release
	public ArrayList<StoryObject> getStoryByRelease(String releaseId, String sprintId) {
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
		// process story info tag String
		ArrayList<Long> tagIds = new ArrayList<Long>();
		for (String tagName : storyInfo.tags.split(",")) {
			TagObject tag = TagObject.get(tagName);
			tagIds.add(tag.getId());
		}
		
		StoryObject story = StoryObject.get(storyInfo.id);
		story.setName(storyInfo.name)
	         .setEstimate(storyInfo.estimate)
	         .setImportance(storyInfo.importance)
	         .setNotes(storyInfo.notes)
	         .setHowToDemo(storyInfo.howToDemo)
	         .setSprintId(storyInfo.sprintId)
	         .setSprintId(storyInfo.sprintId)
	         .setStatus(storyInfo.status)
	         .setValue(storyInfo.value)
	         .save();
		story.setTags(tagIds);
	}

	public StoryObject addStory(StoryInfo storyInfo) {
		ProjectObject project = ProjectObject.get(mProject.getName());
		
		// process story info tag String
		ArrayList<Long> tagIds = new ArrayList<Long>();
		for(String tagName : storyInfo.tags.split(",")){
			TagObject tag = TagObject.get(tagName);
			tagIds.add(tag.getId());
		}
		
		StoryObject story = new StoryObject(project.getId());
		story.setName(storyInfo.name)
		     .setEstimate(storyInfo.estimate)
		     .setImportance(storyInfo.importance)
		     .setNotes(storyInfo.notes)
		     .setHowToDemo(storyInfo.howToDemo)
		     .setSprintId(storyInfo.sprintId)
		     .setSprintId(storyInfo.sprintId)
		     .setStatus(storyInfo.status)
		     .setValue(storyInfo.value)
		     .save();
		story.setTags(tagIds);
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
		ProjectObject project = ProjectObject.get(mProject.getName());
		TagObject tag = new TagObject(name, project.getId());
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
		ArrayList<TagObject> tags = TagObject.getTags();
		return tags;
	}

	// 對Story設定自訂分類標籤
	public void addStoryTag(long storyId, long tagId) {
		StoryObject story = StoryObject.get(storyId);
		if(story != null){
			story.addTag(tagId);
		}
	}

	// 移除Story的自訂分類標籤
	public void removeStoryTag(long storyId, long tagId) {
		StoryObject story = StoryObject.get(storyId);
		if(story != null){
			story.removeTag(tagId);
		}
	}

	public void updateTag(long tagId, String tagName) {
		TagObject tag = TagObject.get(tagId);
		if(tag != null){
			tag.setName(tagName);
			tag.save();
		}
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
		AttachFileObject attachFileObjects = mMantisService.getAttachFile(fileId);
		mMantisService.closeConnect();
		return attachFileObjects;
	}
}
