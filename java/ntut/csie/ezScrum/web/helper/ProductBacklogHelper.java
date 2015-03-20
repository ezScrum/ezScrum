package ntut.csie.ezScrum.web.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataInfo.StoryInfo;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.ezScrum.web.support.Translation;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.jdom.Element;

public class ProductBacklogHelper {
	private ProductBacklogMapper mProductBacklogMapper;
	private ProductBacklogLogic mProductBacklogLogic;
	private IProject mProject;

	public ProductBacklogHelper(IUserSession userSession, IProject project) {
		mProductBacklogMapper = new ProductBacklogMapper(project, userSession);
		mProductBacklogLogic = new ProductBacklogLogic(userSession, project);
		mProject = project;
	}

	/**
	 * Filter Type : 1. null 2. BACKLOG 3. DETAIL 4. DONE
	 * 
	 * @param filterType
	 * @return StringBuilder
	 */
	public StringBuilder getShowProductBacklogResponseText(String filterType) {
		IStory[] stories = mProductBacklogLogic.getStoriesByFilterType(filterType);

		StringBuilder result = new StringBuilder("");
		result.append(new Translation().translateStoryToJson(stories));

		return result;
	}

	/**
	 * 新增 Story 步驟: 1. 新增 Story 2. 新增 Tag 至 Story 上面 3. 新增 Story to Sprint 4. 如果這個 SprintID 有 Release 資訊，那麼也將此 Story 加入 Release
	 * 
	 * @param storyInformation
	 * @return IIssue
	 */
	public IIssue addNewStory(StoryInfo storyInformation) {
		String name = storyInformation.getName();
		String importance = storyInformation.getImportance();
		String estimate = storyInformation.getEstimation();
		String value = storyInformation.getValue();
		String howToDemo = storyInformation.getHowToDemo();
		String notes = storyInformation.getNotes();
		String sprintId = storyInformation.getSprintID();
		String tagIds = storyInformation.getTagIDs();

		// 1. 新增story
		IIssue story = mProductBacklogMapper.addStory(storyInformation);
		long issueId = story.getIssueID();
		editStory(issueId, name, value, importance, estimate, howToDemo, notes, false);

		// 2. 新增Tag至Story上面
		addTagToStory(tagIds, issueId);

		if (sprintId != "" || sprintId.length() != 0) {
			// 3. 新增story to sprint
			ArrayList<Long> list = addStoryToSprint(sprintId, issueId);
			// 4. 如果這個SprintID有Release資訊，那麼也將此Story加入Release
			addStoryToRelease(sprintId, list);
		}
		IIssue issue = mProductBacklogMapper.getStory(issueId);
		return issue;
	}

	// 秀出此 release 加入的 stories，以及此 release 的 sprint 包含的 stories
	public IStory[] getStoriesByRelease(IReleasePlanDesc desc) {
		try {
			IStory[] stories = mProductBacklogLogic.getStoriesByRelease(desc);
			ArrayList<IStory> list = new ArrayList<IStory>();

			for (IStory story : stories) {
				String releaseId = story.getReleaseID();

				if (releaseId != null) {
					// story 有此 release ID 的資訊，則加入為 release backlog
					if (releaseId.equals(desc.getID())) {
						list.add(story);
					}
				}
			}
			return list.toArray(new IStory[list.size()]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new IStory[0];
	}

	public IIssue editStory(StoryInfo storyInformation) {
		long issueId = Long.parseLong(storyInformation.getStroyID());
		String name = storyInformation.getName();
		String importance = storyInformation.getImportance();
		String estimate = storyInformation.getEstimation();
		String value = storyInformation.getValue();
		String howToDemo = storyInformation.getHowToDemo();
		String notes = storyInformation.getNotes();
		String sprintId = storyInformation.getSprintID();
		if (sprintId != null | sprintId.length() != 0) {
			// 新增story to sprint
			ArrayList<Long> list = addStoryToSprint(sprintId, issueId);
			// 如果這個SprintID有Release資訊，那麼也將此Story加入Release
			addStoryToRelease(sprintId, list);
		}
		return editStory(issueId, name, value, importance, estimate, howToDemo, notes, true);
	}

	/**
	 * 更新 Story 資訊
	 * 
	 * @param issueId
	 * @param name
	 * @param value
	 * @param importance
	 * @param estimate
	 * @param howToDemo
	 * @param notes
	 * @return IIssue
	 */
	public IIssue editStory(long issueId, String name, String value, String importance, String estimate, String howToDemo, String notes, boolean addHistory) {
		mProductBacklogMapper.modifyStoryName(issueId, name, new Date());
		Element history = translateIssueToXML(value, importance, estimate, howToDemo, notes);
		if (history.getChildren().size() > 0) {
			IIssue issue = mProductBacklogMapper.getStory(issueId);
			issue.addTagValue(history);
			issue.setSummary(name);
			mProductBacklogMapper.updateStory(issue, addHistory);
			return mProductBacklogMapper.getStory(issueId);
		} else {
			return null;
		}
	}

	private Element translateIssueToXML(String value, String importance, String estimate, String howToDemo, String notes) {
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(new Date(), DateUtil._16DIGIT_DATE_TIME_2));

		if (importance != null && !importance.equals("")) {
			Element importanceElem = new Element(ScrumEnum.IMPORTANCE);
			importanceElem.setText(importance);
			history.addContent(importanceElem);
		}

		if (estimate != null && !estimate.equals("")) {
			Element storyPoint = new Element(ScrumEnum.ESTIMATION);
			storyPoint.setText(estimate);
			history.addContent(storyPoint);
		}

		if (value != null && !value.equals("")) {
			Element customValue = new Element(ScrumEnum.VALUE);
			customValue.setText(value);
			history.addContent(customValue);
		}

		Element howToDemoElem = new Element(ScrumEnum.HOWTODEMO);
		howToDemoElem.setText(howToDemo);
		history.addContent(howToDemoElem);

		Element notesElem = new Element(ScrumEnum.NOTES);
		notesElem.setText(notes);
		history.addContent(notesElem);
		return history;
	}

	/**
	 * 更新 Story 並轉換成 XML format
	 * 
	 * @param issueId
	 * @return StringBuilder
	 */
	public StringBuilder getEditStoryInformationResponseText(long issueId) {
		IIssue issue = mProductBacklogMapper.getStory(issueId);

		StringBuilder result = new StringBuilder("");
		result.append(new Translation().translateStory(issue));
		return result;
	}

	/**
	 * delete story and get json text
	 * 
	 * @param id
	 * @return StringBuilder
	 */
	public StringBuilder deleteStory(String id) {
		removeTask(id);
		mProductBacklogMapper.deleteStory(id);

		StringBuilder result = new StringBuilder("");
		result.append("{\"success\":true, \"Total\":1, \"Stories\":[{\"Id\":" + id + "}]}");
		return result;
	}

	/**
	 * 取得專案所有的 Tag 並轉以 XML 格式輸出。
	 * 
	 * @return StringBuilder
	 */
	public StringBuilder getTagListResponseText() {
		StringBuilder sb = new StringBuilder();
		try {
			ArrayList<TagObject> tags = getTagList();

			sb.append("<TagList><Result>success</Result>");
			for (int i = 0; i < tags.size(); i++) {
				sb.append("<IssueTag>");
				sb.append("<Id>" + tags.get(i).getId() + "</Id>");
				sb.append("<Name>" + new TranslateSpecialChar().TranslateXMLChar(tags.get(i).getName()) + "</Name>");
				sb.append("</IssueTag>");
			}
			sb.append("</TagList>");
		} catch (Exception e) {
			sb.append("<TagList><Result>false</Result></TagList>");
		}
		return sb;
	}

	/**
	 * 新增Tag並轉換成Response所需要的XML format
	 * 
	 * @param newTagName
	 * @return StringBuilder
	 */
	public StringBuilder getAddNewTagResponsetext(String newTagName) {
		String original_tagname = newTagName;

		newTagName = new TranslateSpecialChar().TranslateDBChar(newTagName);

		StringBuilder sb = new StringBuilder("");
		// 先將"\","'"轉換, 判斷DB裡是否存在
		if (newTagName.contains(",")) {
			sb = new StringBuilder("<Tags><Result>false</Result><Message>TagName: \",\" is not allowed</Message></Tags>");
		} else if (isTagExist(newTagName)) {
			// 轉換"&", "<", ">", """, 通過XML語法
			// 因為"\","'"對xml沒影響, 所以使用original(未轉換)
			newTagName = new TranslateSpecialChar().TranslateXMLChar(original_tagname);
			sb = new StringBuilder("<Tags><Result>false</Result><Message>Tag Name : " + newTagName + " already exist</Message></Tags>");
		} else {
			addNewTag(newTagName);
			TagObject tag = getTagByName(newTagName);

			sb.append("<Tags><Result>true</Result>");
			sb.append("<IssueTag>");
			sb.append("<Id>" + tag.getId() + "</Id>");
			sb.append("<Name>" + new TranslateSpecialChar().TranslateXMLChar(tag.getName()) + "</Name>");
			sb.append("</IssueTag>");
			sb.append("</Tags>");
		}
		return sb;
	}

	public StringBuilder getDeleteTagReponseText(long tagId) {
		mProductBacklogMapper.deleteTag(tagId);

		StringBuilder result = new StringBuilder("");
		result.append("<TagList><Result>success</Result>");
		result.append("<IssueTag>");
		result.append("<Id>" + tagId + "</Id>");
		result.append("</IssueTag>");
		result.append("</TagList>");

		return result;
	}

	/**
	 * 新增 Tag 至 Story 上，並回傳加上 Tag 之後的 Story 資訊(json format)
	 * 
	 * @param storyId
	 * @param tagId
	 * @return StringBuilder
	 */
	public StringBuilder getAddStoryTagResponseText(String storyId, long tagId) {
		addStoryTag(storyId, tagId);
		IIssue issue = mProductBacklogMapper.getStory(Long.parseLong(storyId));
		StringBuilder result = new StringBuilder("");
		result.append(translateStoryToJson(issue));
		return result;
	}

	/**
	 * 移除 Story 上的 Tag，並回傳移除 Tag 之後的 Story 資訊(json format)
	 * 
	 * @param storyId
	 * @param tagId
	 * @return StringBuilder
	 */
	public StringBuilder getRemoveStoryTagResponseText(String storyId, long tagId) {
		removeStoryTag(storyId, tagId);
		IIssue issue = mProductBacklogMapper.getStory(Long.parseLong(storyId));
		StringBuilder result = new StringBuilder("");
		result.append(translateStoryToJson(issue));
		return result;
	}

	/**
	 * 將 story 資訊轉換成 JSon format
	 * 
	 * @param issue
	 * @return StringBuilder
	 */
	public StringBuilder translateStoryToJson(IIssue issue) {
		StringBuilder result = new StringBuilder("");
		result.append(new Translation().translateStoryToJson(issue));
		return result;
	}

	private void addStoryToRelease(String sprintId, ArrayList<Long> list) {
		ReleasePlanHelper releaseHelper = new ReleasePlanHelper(mProject);
		String releaseId = releaseHelper.getReleaseID(sprintId);
		if (!(releaseId.equals("0"))) {
			mProductBacklogLogic.addReleaseTagToIssue(list, releaseId);
		}
	}

	// remove <Release/> && <Iteration/> tag Info.
	// become to <Release>0</Release> && <Iteration>0</Iteration>

	/**
	 * remove <Release/> && <Iteration/> tag Info. become to <Release>0</Release> && <Iteration>0</Iteration>
	 * 
	 * @param issueId
	 */
	public void removeReleaseSprint(long issueId) {
		IIssue issue = mProductBacklogMapper.getStory(issueId);

		// history node
		Element history = new Element(ScrumEnum.HISTORY_TAG);

		Date current = new Date();
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(
		        current, DateUtil._16DIGIT_DATE_TIME_2));

		// release node
		Element release = new Element(ScrumEnum.RELEASE_TAG);
		release.setText(ScrumEnum.DIGITAL_BLANK_VALUE);
		history.addContent(release);

		// iteration node
		Element iteration = new Element(ScrumEnum.SPRINT_ID);
		iteration.setText(ScrumEnum.DIGITAL_BLANK_VALUE);
		history.addContent(iteration);

		issue.addTagValue(history);

		// 最後將修改的結果更新至DB
		mProductBacklogMapper.updateStory(issue, true);

		// 將 Story 與 Release 對應的關係從 StoryRelationTable 移除
		mProductBacklogMapper.updateStoryRelation(issueId, "-1", ScrumEnum.DIGITAL_BLANK_VALUE, null, null, current);
	}

	private ArrayList<Long> addStoryToSprint(String sprintId, long issueId) {
		ArrayList<Long> list = new ArrayList<Long>();
		list.add(issueId);
		mProductBacklogLogic.addIssueToSprint(list, sprintId);
		return list;
	}

	// 透過 map 得到所有 sprint 的 stories
	public Map<String, ArrayList<IIssue>> getSprintHashMap() {
		IStory[] stories;
		Map<String, ArrayList<IIssue>> map = new HashMap<String, ArrayList<IIssue>>();
		try {
			stories = mProductBacklogLogic.getStories();
			for (IStory story : stories) {
				String iteration = story.getSprintID();
				if (map.get(iteration) == null) {
					ArrayList<IIssue> list = new ArrayList<IIssue>();
					list.add(story);
					map.put(iteration, list);
				} else {
					ArrayList<IIssue> list = map.get(iteration);
					list.add(story);
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void addTagToStory(String tagIds, long issueId) {
		String[] ids = tagIds.split(",");
		if (!(tagIds.isEmpty()) && ids.length > 0) {
			for (String tagId : ids) {
				mProductBacklogMapper.addTagToStory(Long.toString(issueId), Long.parseLong(tagId));
			}
		}
	}

	public IIssue[] getWildTasks() throws SQLException {
		IIssue[] issues = mProductBacklogMapper.getStories(ScrumEnum.TASK_ISSUE_TYPE);

		// 不能直接使用Arrays.asList,因為沒有實作到remove,所以必需要使用ArrayList
		ArrayList<IIssue> list = new ArrayList<IIssue>();
		list.addAll(Arrays.asList(issues));
		for (int i = list.size() - 1; i >= 0; i--) {
			IIssue issue = list.get(i);
			long parentsID = issue.getParentId();
			if (parentsID > 0) list.remove(i);
		}
		return list.toArray(new IIssue[list.size()]);
	}
	
	public ArrayList<TaskObject> getTasksWithNoParent() {
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		ProjectObject project = ProjectObject.get(mProject.getName());
		tasks = project.getTasksWithNoParent();
		return tasks;
	}

	/**
	 * remove task 跟 story 之間的關係
	 * 
	 * @param id
	 */
	private void removeTask(String id) {
		IIssue issue = mProductBacklogMapper.getStory(Long.parseLong(id));
		// 取得issue的的task列表
		ArrayList<Long> tasksList = issue.getChildrenId();
		// drop Tasks
		if (tasksList != null) {
			for (Long taskId : tasksList)
				mProductBacklogMapper.removeTask(taskId, Long.parseLong(id));
		}
	}

	/**
	 * 確認分類標籤是否存在
	 * 
	 * @param name
	 * @return boolean
	 */
	public boolean isTagExist(String name) {
		return mProductBacklogMapper.isTagExisting(name);
	}

	/**
	 * 取得自訂分類標籤列表
	 * 
	 * @return ArrayList
	 */
	public ArrayList<TagObject> getTagList() {
		return mProductBacklogMapper.getTags();
	}

	/**
	 * 取得 story 或 task
	 * 
	 * @param id
	 * @return IIssue
	 */
	public IIssue getIssue(long id) {
		return mProductBacklogMapper.getStory(id);
	}

	/**
	 * 新增自訂分類標籤
	 * 
	 * @param name
	 */
	public long addNewTag(String name) {
		return mProductBacklogMapper.addNewTag(name);
	}

	/**
	 * 刪除自訂分類標籤
	 * 
	 * @param id
	 */
	public void deleteTag(long id) {
		mProductBacklogMapper.deleteTag(id);
	}

	/**
	 * 根據 Tag name 取得 tag
	 * 
	 * @param name
	 * @return
	 */
	public TagObject getTagByName(String name) {
		return mProductBacklogMapper.getTagByName(name);
	}

	/**
	 * 對 Story 設定自訂分類標籤
	 * 
	 * @param storyId
	 * @param tagId
	 */
	public void addStoryTag(String storyId, long tagId) {
		mProductBacklogMapper.addTagToStory(storyId, tagId);
	}

	/**
	 * 移除 Story 的自訂分類標籤
	 * 
	 * @param storyId
	 * @param tagId
	 */
	public void removeStoryTag(String storyId, long tagId) {
		mProductBacklogMapper.removeTagFromStory(storyId, tagId);
	}

	public void moveStory(long issueId, String moveId, String type) {
		ArrayList<Long> issueList = new ArrayList<Long>();
		issueList.add(new Long(issueId));

		/**
		 * 1. 移動到某個Release內 2. 移動到某個Sprint中
		 */
		if (type.equals("release")) {
			// 因為移動到Release內，所以他不屬於任何一個Sprint
			mProductBacklogLogic.removeStoryFromSprint(issueId);
			// 移動到Release內
			mProductBacklogLogic.addReleaseTagToIssue(issueList, moveId);
		} else {
			// 將此Story加入其他Sprint
			mProductBacklogLogic.addIssueToSprint(issueList, moveId);
			// 檢查Sprint是否有存在於某個Release中
			ReleasePlanHelper releasePlan = new ReleasePlanHelper(mProject);
			String sprintReleaseID = releasePlan.getReleaseID(moveId);
			/**
			 * 1. 如果有的話，將所有Story加入Release 2. 沒有的話，將此Story的Release設為0
			 */
			if (!(sprintReleaseID.equals("0"))) {
				mProductBacklogLogic.addReleaseTagToIssue(issueList, sprintReleaseID);
			} else {
				mProductBacklogLogic.addReleaseTagToIssue(issueList, "0");
			}
		}
	}

	public void removeStoryFromSprint(long issueId) {
		mProductBacklogLogic.removeStoryFromSprint(issueId);
	}

	public void removeReleaseTagFromIssue(long issueId) {
		mProductBacklogLogic.removeReleaseTagFromIssue(issueId);
	}

	public long addAttachFile(AttachFileInfo attachFileInfo, File file) throws IOException {
		// create folder to put file
		String folderPath = System.getProperty("ntut.csie.jcis.resource.WorkspaceRoot") + File.separator + "AttachFile" + File.separator + attachFileInfo.projectName;
		new File(folderPath).mkdirs();

		attachFileInfo.path = folderPath + File.separator + System.currentTimeMillis() + "_" + attachFileInfo.name;
		File targetFile = new File(attachFileInfo.path);

		// move file from tmp folder to "AttachFile" folder
		copyFile(file, targetFile);

		return mProductBacklogMapper.addAttachFile(attachFileInfo);
	}

	public void deleteAttachFile(long fileId) {
		AttachFileObject attachFile = getAttachFile(fileId);

		File file = new File(attachFile.getPath());
		file.delete();

		mProductBacklogMapper.deleteAttachFile(fileId);
	}

	// for ezScrum v1.8
	public AttachFileObject getAttachFile(long fileId) {
		return mProductBacklogMapper.getAttachfile(fileId);
	}

	private void copyFile(File srcFile, File destFile) throws IOException {
		Files.copy(srcFile.toPath(), destFile.toPath());
	}
	
	public boolean checkAccountInProject(List<AccountObject> memberList, AccountObject userObject) {
		if(userObject.getUsername().equals("admin")) {
			return true;
		}
		for(AccountObject member : memberList) {
			if(member.getId() == userObject.getId()) {
				return true;
			}
		}
		return false;
	}
}
