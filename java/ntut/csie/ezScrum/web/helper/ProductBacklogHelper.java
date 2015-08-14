package ntut.csie.ezScrum.web.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataInfo.StoryInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.ezScrum.web.support.Translation;

public class ProductBacklogHelper {
	private ProductBacklogMapper mProductBacklogMapper;
	private ProductBacklogLogic mProductBacklogLogic;
	private ProjectObject mProject;

	public ProductBacklogHelper(ProjectObject project) {
		mProductBacklogMapper = new ProductBacklogMapper(project);
		mProductBacklogLogic = new ProductBacklogLogic(project);
		mProject = project;
	}

	/**
	 * Filter Type : 1. null 2. BACKLOG 3. DETAIL 4. DONE
	 * 
	 * @param filterType
	 * @return StringBuilder
	 */
	public StringBuilder getShowProductBacklogResponseText(String filterType) {
		ArrayList<StoryObject> stories = mProductBacklogLogic.getStoriesByFilterType(filterType);
		StringBuilder result = new StringBuilder("");
		result.append(Translation.translateStoriesToJson(stories));
		return result;
	}

	/**
	 * 新增 Story
	 * 
	 * @param storyInfo
	 * @return StoryObject
	 */
	public long addStory(long projectId, StoryInfo storyInfo) {
		StoryObject newStory = mProductBacklogMapper.addStory(projectId, storyInfo);
		return newStory.getId();
	}

	// 秀出此 release 加入的 stories，以及此 release 的 sprint 包含的 stories
	public ArrayList<StoryObject> getStoriesByRelease(IReleasePlanDesc desc) {
		ArrayList<StoryObject> stories = mProductBacklogLogic.getStoriesByRelease(desc);
		return stories;
	}
	
	public ArrayList<StoryObject> getAddableStories() {
		return mProductBacklogLogic.getAddableStories();
	}
	
	public ArrayList<StoryObject> getStoriesByFilterType(String filterType) {
		return mProductBacklogLogic.getStoriesByFilterType(filterType);
	}
	
	public ArrayList<StoryObject> getStories() {
		return mProductBacklogLogic.getStories();
	}

	/**
	 * 更新 Story 資訊
	 * 
	 * @param storyInfo
	 * @return StoryObject
	 */
	public StoryObject updateStory(long id, StoryInfo storyInfo) {
		mProductBacklogMapper.updateStory(storyInfo);
		return mProductBacklogMapper.getStory(id);
	}

	/**
	 * delete story and get json text
	 * 
	 * @param storyId
	 * @return StringBuilder
	 */
	public StringBuilder deleteStory(long storyId) {
		StoryObject story = getStory(storyId);
		StringBuilder result = new StringBuilder("");

		if (story != null) {
			removeTask(storyId);
			mProductBacklogMapper.deleteStory(storyId);
			result.append("{\"success\":true, \"Total\":1, \"Stories\":[{\"Id\":" + storyId + "}]}");
		} else {
			result.append("{\"success\":false, \"Total\":0, \"Stories\":[{}]}");
		}
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
	public StringBuilder getAddStoryTagResponseText(long storyId, long tagId) {
		addStoryTag(storyId, tagId);
		StoryObject story = mProductBacklogMapper.getStory(storyId);
		StringBuilder result = new StringBuilder("");
		result.append(translateStoryToJson(story));
		return result;
	}

	/**
	 * 移除 Story 上的 Tag，並回傳移除 Tag 之後的 Story 資訊(json format)
	 * 
	 * @param storyId
	 * @param tagId
	 * @return StringBuilder
	 */
	public StringBuilder getRemoveStoryTagResponseText(long storyId, long tagId) {
		removeStoryTag(storyId, tagId);
		StoryObject story = mProductBacklogMapper.getStory(storyId);
		StringBuilder result = new StringBuilder("");
		result.append(translateStoryToJson(story));
		return result;
	}

	/**
	 * 將 story 資訊轉換成 JSon format
	 * 
	 * @param story
	 * @return StringBuilder
	 */
	public StringBuilder translateStoryToJson(StoryObject story) {
		StringBuilder result = new StringBuilder("");
		result.append(Translation.translateStoryToJson(story));
		return result;
	}
	
	/**
	 * 將 story 資訊轉換成 JSon format
	 * 
	 * @param story
	 * @return StringBuilder
	 */
	public StringBuilder translateStoryToXML(StoryObject story) {
		StringBuilder result = new StringBuilder("");
		result.append(Translation.translateStoryToXML(story));
		return result;
	}

	// 透過 map 得到所有 sprint 的 stories
	public Map<Long, ArrayList<StoryObject>> getSprintHashMap() {
		ArrayList<StoryObject> stories;
		Map<Long, ArrayList<StoryObject>> map = new HashMap<Long, ArrayList<StoryObject>>();
		try {
			stories = mProductBacklogLogic.getStories();
			for (StoryObject story : stories) {
				long iteration = story.getSprintId();
				if (map.get(iteration) == null) {
					ArrayList<StoryObject> list = new ArrayList<StoryObject>();
					list.add(story);
					map.put(iteration, list);
				} else {
					ArrayList<StoryObject> list = map.get(iteration);
					list.add(story);
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<TaskObject> getTasksWithNoParent() {
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		tasks = mProject.getTasksWithNoParent();
		return tasks;
	}

	/**
	 * remove task 跟 story 之間的關係
	 * 
	 * @param storyId
	 */
	private void removeTask(long storyId) {
		StoryObject story = mProductBacklogMapper.getStory(storyId);
		// 取得story的的task列表
		ArrayList<TaskObject> tasks = story.getTasks();
		// drop Tasks
		if (tasks.size() > 0) {
			for (TaskObject task : tasks)
				mProductBacklogMapper.removeTask(task.getId(), storyId);
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
	 * @param storyId
	 * @return StoryObject
	 */
	public StoryObject getStory(long storyId) {
		return mProductBacklogMapper.getStory(storyId);
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
	public void addStoryTag(long storyId, long tagId) {
		mProductBacklogMapper.addTagToStory(storyId, tagId);
	}

	/**
	 * 移除 Story 的自訂分類標籤
	 * 
	 * @param storyId
	 * @param tagId
	 */
	public void removeStoryTag(long storyId, long tagId) {
		mProductBacklogMapper.removeTagFromStory(storyId, tagId);
	}

	public void moveStory(long storyId, long targetSprintId) {
		ArrayList<Long> stories = new ArrayList<Long>();
		stories.add(new Long(storyId));
		// 將此Story加入其他Sprint
		mProductBacklogLogic.addStoriesToSprint(stories, targetSprintId);
	}

	public void dropStoryFromSprint(long storyId) {
		mProductBacklogLogic.dropStoryFromSprint(storyId);
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
		if (userObject.getUsername().equals("admin")) {
			return true;
		}
		for (AccountObject member : memberList) {
			if (member.getId() == userObject.getId()) {
				return true;
			}
		}
		return false;
	}
}
