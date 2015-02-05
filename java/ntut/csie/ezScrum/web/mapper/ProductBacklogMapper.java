package ntut.csie.ezScrum.web.mapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.iteration.iternal.Story;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataInfo.StoryInfo;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.jcis.resource.core.IProject;

public class ProductBacklogMapper {
	private IProject mProject;
	private Configuration mConfig;
	private IUserSession mUserSession;
	private MantisService mMantisService;

	public ProductBacklogMapper(IProject project, IUserSession userSession) {
		mProject = project;
		mUserSession = userSession;
		mConfig = new Configuration(mUserSession);
		mMantisService = new MantisService(mConfig);
	}

	public ArrayList<IStory> getUnclosedIssues(String category)
			throws SQLException {
		mMantisService.openConnect();
		IIssue[] issues = mMantisService
				.getIssues(mProject.getName(), category);
		mMantisService.closeConnect();

		ArrayList<IStory> list = new ArrayList<IStory>();
		for (IIssue issue : issues) {
			if (ITSEnum.getStatus(issue.getStatus()) < ITSEnum.CLOSED_STATUS) {
				list.add(new Story(issue));
			}
		}
		return list;
	}

	// 回傳某種 category的issue
	public IIssue[] getIssues(String category) throws SQLException {
		mMantisService.openConnect();
		IIssue[] issues = mMantisService.getIssues(mProject.getName(), category);
		mMantisService.closeConnect();
		return issues;
	}

	public void updateStoryRelation(long issueId, String releaseId,
			String sprintId, String estimate, String importance, Date date) {
		mMantisService.openConnect();
		mMantisService.updateStoryRelationTable(issueId, mProject.getName(),
				releaseId, sprintId, estimate, importance, date);
		mMantisService.closeConnect();
	}

	// get all stories
	public ArrayList<IStory> getAllStoriesByProjectName() {
		mMantisService.openConnect();
		ArrayList<IStory> issues = mMantisService.getStorys(mProject.getName());
		mMantisService.closeConnect();
		return issues;
	}

	// get all stories by release
	public ArrayList<IStory> connectToGetStoryByRelease(String releaseId,
			String sprintId) {

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

	public IIssue getIssue(long id) {
		mMantisService.openConnect();
		IIssue issue = mMantisService.getIssue(id);
		mMantisService.closeConnect();
		return issue;
	}
	
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

	public IIssue updateIssueValue(long issueId, String name, String value, String importance, String estimate, String howToDemo, String notes) {
		IIssue issue = getIssue(issueId);
		
		mMantisService.openConnect();
		mMantisService.updateBugNote(issue);
		mMantisService.closeConnect();
		
		return getIssue(issueId);
	}

	public IIssue addStory(StoryInfo storyInformation) {
		mMantisService.openConnect();
		IIssue story = new Issue();

		story.setProjectID(mProject.getName());
		story.setSummary(storyInformation.getName());
		story.setDescription(storyInformation.getDescription());
		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		long storyId = mMantisService.newIssue(story);

		mMantisService.closeConnect();
		return getIssue(storyId);
	}

	

	public void modifyName(long storyId, String name, Date modifyDate) {
		IIssue task = getIssue(storyId);

		if (!task.getSummary().equals(name)) {
			mMantisService.openConnect();
			mMantisService.updateName(task, name, modifyDate);
			mMantisService.closeConnect();
		}
	}
	
	// delete story 用
	public void deleteStory(String storyId) {
		mMantisService.openConnect();
		mMantisService.deleteStory(storyId);
		mMantisService.closeConnect();
	}

	public void removeTask(long taskId, long parentId) {
		mMantisService.openConnect();
		mMantisService.removeRelationship(parentId, taskId, ITSEnum.PARENT_RELATIONSHIP);
		mMantisService.closeConnect();
	}

	// 新增自訂分類標籤
	public long addNewTag(String name) {
		mMantisService.openConnect();
		long id = mMantisService.addNewTag(name, mProject.getName());
		mMantisService.closeConnect();
		return id;
	}

	// 刪除自訂分類標籤
	public void deleteTag(long id) {
		mMantisService.openConnect();
		mMantisService.deleteTag(id, mProject.getName());
		mMantisService.closeConnect();
	}

	// 取得自訂分類標籤列表
	public ArrayList<TagObject> getTagList() {
		mMantisService.openConnect();
		ArrayList<TagObject> tags = mMantisService.getTagList(mProject.getName());
		mMantisService.closeConnect();
		return tags;
	}

	// 對Story設定自訂分類標籤
	public void addStoryTag(String storyId, long tagId) {
		mMantisService.openConnect();
		mMantisService.addStoryTag(storyId, tagId);
		mMantisService.closeConnect();
	}

	// 移除Story的自訂分類標籤
	public void removeStoryTag(String storyId, long tagId) {
		mMantisService.openConnect();
		mMantisService.removeStoryTag(storyId, tagId);
		mMantisService.closeConnect();
	}

	public void updateTag(long tagId, String tagName) {
		mMantisService.openConnect();
		mMantisService.updateTag(tagId, tagName, mProject.getName());
		mMantisService.closeConnect();
	}

	public boolean isTagExist(String name) {
		mMantisService.openConnect();
		boolean result = mMantisService.isTagExist(name, mProject.getName());
		mMantisService.closeConnect();
		return result;
	}

	public TagObject getTagByName(String name) {
		mMantisService.openConnect();
		TagObject tag = mMantisService.getTagByName(name, mProject.getName());
		mMantisService.closeConnect();
		return tag;
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
		AttachFileObject attachFileObjects = mMantisService
				.getAttachFile(fileId);
		mMantisService.closeConnect();
		return attachFileObjects;
	}

	public void addHistory(long issueId, int issueType, int historyType,
			String oldValue, String newValue) {
		HistoryObject history = new HistoryObject(issueId, issueType, historyType,
				oldValue, newValue, System.currentTimeMillis());
		history.save();
	}
}
