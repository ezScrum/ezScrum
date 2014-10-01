package ntut.csie.ezScrum.web.mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IITSService;
import ntut.csie.ezScrum.issue.sql.service.core.ITSServiceFactory;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.iteration.iternal.Story;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.StoryInformation;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.jcis.resource.core.IProject;

public class ProductBacklogMapper {

	private IProject m_project;
	private ITSServiceFactory m_itsFactory;
	private Configuration mConfig;
	private IUserSession m_userSession;
	private MantisService mMantisService;

	public ProductBacklogMapper(IProject project, IUserSession userSession) {
		m_project = project;
		m_userSession = userSession;

		//初始ITS的設定
		m_itsFactory = ITSServiceFactory.getInstance();
		mConfig = new Configuration(m_userSession);
		mMantisService = new MantisService(mConfig);
	}

	public List<IStory> getUnclosedIssues(String category) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		IIssue[] issues = itsService.getIssues(m_project.getName(), category);
		itsService.closeConnect();
		List<IStory> list = new ArrayList<IStory>();

		for (IIssue issue : issues) {
			if (ITSEnum.getStatus(issue.getStatus()) < ITSEnum.CLOSED_STATUS) list.add(new Story(issue));
		}
		return list;
	}

	//回傳某種 category的issue
	public IIssue[] getIssues(String category) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		IIssue[] issues = itsService.getIssues(m_project.getName(), category);
		itsService.closeConnect();
		return issues;
	}

	public void updateStoryRelation(String issueID, String releaseID, String sprintID, String estimate, String importance, Date date) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		itsService.updateStoryRelationTable(issueID, m_project.getName(), releaseID, sprintID, estimate, importance, date);
		itsService.closeConnect();
	}

	//	get all stories
	public List<IStory> getAllStoriesByProjectName() {

		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		//找出Category為Story
		/*
		IIssue[] issues = itsService.getIssues(m_project.getName(), ScrumEnum.STORY_ISSUE_TYPE);
		
		List<IStory> list = new ArrayList<IStory>();
		for (IIssue issue : issues) {
				list.add(new Story(issue));
		}
		*/

		List<IStory> issues = itsService.getStorys(m_project.getName());
		itsService.closeConnect();
		return issues;
	}

	//	get all stories by release
	public List<IStory> connectToGetStoryByRelease(String releaseID, String sprintID) {

		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		//找出Category為Story
		IIssue[] issues = itsService.getIssues(m_project.getName(), ScrumEnum.STORY_ISSUE_TYPE, releaseID, sprintID, null);

		List<IStory> list = new ArrayList<IStory>();
		for (IIssue issue : issues) {
			list.add(new Story(issue));
		}

		itsService.closeConnect();
		return list;
	}

	public IIssue getIssue(long id) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		IIssue issue = itsService.getIssue(id);
		itsService.closeConnect();
		return issue;
	}

	//	public void updateTagValue(IIssue issue) {
	public void updateIssueValue(IIssue issue) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		itsService.updateBugNote(issue);
		itsService.closeConnect();

	}

	/**
	 * 由於GAE使用
	 * 
	 * @param issue
	 * @param storyInformation
	 */
	//	public void updateIssueValue(IIssue issue, StoryInformation storyInformation) {
	//		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID,m_itsPrefs);
	//		itsService.openConnect();
	//		itsService.updateBugNote(issue);
	//		itsService.closeConnect();
	//		
	//	}

	//	public IIssue addStory(String name, String description){
	public IIssue addStory(StoryInformation storyInformation) {
		String name = storyInformation.getName();
		String description = storyInformation.getDescription();
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		IIssue story = new Issue();

		story.setProjectID(m_project.getName());
		story.setSummary(name);
		story.setDescription(description);
		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		long storyID = itsService.newIssue(story);

		itsService.closeConnect();
		return this.getIssue(storyID);
	}

	public void updateHistoryModifiedDate(long issueID, long historyID, Date date) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		itsService.updateHistoryModifiedDate(issueID, historyID, date);
		itsService.closeConnect();
	}

	public void modifyName(long storyId, String name, Date modifyDate) {
		IIssue task = this.getIssue(storyId);
		if (!task.getSummary().equals(name))
		{
			IITSService itsService = m_itsFactory.getService(mConfig);
			itsService.openConnect();
			itsService.updateName(task, name, modifyDate);
			itsService.closeConnect();
		}
	}

	//delete story 用
	public void deleteStory(String ID) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		itsService.deleteStory(ID);
		itsService.closeConnect();
	}

	public void removeTask(long taskID, long parentID) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		itsService.removeRelationship(parentID, taskID, ITSEnum.PARENT_RELATIONSHIP);
		itsService.closeConnect();
	}

	// 新增自訂分類標籤
	public long addNewTag(String name) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		long id = itsService.addNewTag(name, m_project.getName());
		itsService.closeConnect();
		return id;
	}

	// 刪除自訂分類標籤
	public void deleteTag(long id) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		itsService.deleteTag(id, m_project.getName());
		itsService.closeConnect();
	}

	// 取得自訂分類標籤列表
	public ArrayList<TagObject> getTagList() {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		ArrayList<TagObject> tags = itsService.getTagList(m_project.getName());
		itsService.closeConnect();

		return tags;
	}

	// 對Story設定自訂分類標籤
	public void addStoryTag(String storyID, long tagID) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		itsService.addStoryTag(storyID, tagID);
		itsService.closeConnect();
	}

	// 移除Story的自訂分類標籤
	public void removeStoryTag(String storyID, long tagID) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		itsService.removeStoryTag(storyID, tagID);
		itsService.closeConnect();
	}

	public void updateTag(long tagId, String tagName) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		itsService.updateTag(tagId, tagName, m_project.getName());
		itsService.closeConnect();
	}

	public boolean isTagExist(String name) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		boolean result = itsService.isTagExist(name, m_project.getName());
		itsService.closeConnect();

		return result;
	}

	public TagObject getTagByName(String name) {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfig);
		itsService.openConnect();
		TagObject tag = itsService.getTagByName(name, m_project.getName());
		itsService.closeConnect();

		return tag;
	} 

	public long addAttachFile(AttachFileInfo attachFileInfo) {
		IITSService itsService = m_itsFactory.getService(mConfig);
		itsService.openConnect();
		long id = itsService.addAttachFile(attachFileInfo);
		itsService.closeConnect();
		return id;
	}

	// for ezScrum v1.8
	public void deleteAttachFile(long fileId) {
		mMantisService.openConnect();
		mMantisService.deleteAttachFile(fileId);
		mMantisService.closeConnect();
	}

	/**
	 * 抓取attach file
	 * for ezScrum v1.8
	 */
	public AttachFileObject getAttachfile(long fileId) {
		MantisService mantisService = new MantisService(mConfig);
		mantisService.openConnect();
		AttachFileObject attachFileObjects = mantisService.getAttachFile(fileId);
		mantisService.closeConnect();
		return attachFileObjects;
	}

	// for ezScrum v1.8
	public ArrayList<AttachFileObject> getAttachfilesByStoryId(long storyId) {
		MantisService mantisService = new MantisService(mConfig);
		mantisService.openConnect();
		ArrayList<AttachFileObject> attachFileObjects = mantisService.getAttachFilesByStoryId(storyId);
		mantisService.closeConnect();
		return attachFileObjects;
	}
	
	// for ezScrum v1.8
	public ArrayList<AttachFileObject> getAttachfilesByTaskId(long taskId) {
		MantisService mantisService = new MantisService(mConfig);
		mantisService.openConnect();
		ArrayList<AttachFileObject> attachFileObjects = mantisService.getAttachFilesByTaskId(taskId);
		mantisService.closeConnect();
		return attachFileObjects;
    }

	public void addHistory(long issueID, String typeName, String oldValue, String newValue) {
		IITSService itsService = m_itsFactory.getService(mConfig);
		itsService.openConnect();
		itsService.addHistory(issueID, typeName, oldValue, newValue);
		itsService.closeConnect();
	}

}
