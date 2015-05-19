package ntut.csie.ezScrum.restful.mobile.service;

import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.restful.mobile.support.ConvertProductBacklog;
import ntut.csie.ezScrum.web.dataInfo.StoryInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

import com.google.gson.Gson;

public class StoryWebService extends ProjectWebService {
	private ConvertProductBacklog mConvertProductBacklog;
	private ProductBacklogHelper mProductBacklogHelper;
	private SprintBacklogHelper mSprintBacklogHelper;
	private ProjectObject mProject;

	public StoryWebService(AccountObject user, String projectName)
			throws LogonException {
		super(user, projectName);
		mProject = getAllProjects().get(0);
		initialize();
	}

	public StoryWebService(String username, String userpwd, String projectID)
			throws LogonException {
		super(username, userpwd, projectID);
		mProject = getAllProjects().get(0);
		initialize();
	}

	private void initialize() {
		mProductBacklogHelper = new ProductBacklogHelper(mProject);
		mSprintBacklogHelper = new SprintBacklogHelper(mProject);
		mConvertProductBacklog = new ConvertProductBacklog();
	}

	/**
	 * 新增 story 並回傳是否新增成功的資訊
	 * 
	 * @param storyJson
	 * @return
	 * @throws JSONException
	 */
	public String createStory(String storyJson) throws JSONException {
		StoryInfo storyInfo = new StoryInfo(storyJson);
		long storyId = mProductBacklogHelper.addStory(mProject.getId(),
				storyInfo);
		return mConvertProductBacklog.createStory(storyId);
	}

	/**
	 * 修改 story 並回傳修改後的 story
	 * 
	 * @param storyJson
	 * @return
	 * @throws SQLException
	 * @throws JSONException
	 */
	public String updateStory(String storyJson) throws JSONException {
		StoryInfo storyInfo = new StoryInfo(storyJson);
		StoryObject story = mProductBacklogHelper.updateStory(storyInfo.id,
				storyInfo);
		return mConvertProductBacklog.getStory(story);
	}

	/**
	 * 取得 story 中所有的 task
	 * 
	 * @param storyId
	 * @return tasks string
	 */
	public String getTasksInStory(long storyId) {
		ArrayList<TaskObject> tasks = mSprintBacklogHelper
				.getTasksByStoryId(storyId);
		return mConvertProductBacklog.getTasks(tasks);
	}

	public void addExistedTask(long storyId, String taskIdsJson) {
		Gson gson = new Gson();
		String[] taskIds = gson.fromJson(taskIdsJson, String[].class);
		mSprintBacklogHelper.addExistingTasksToStory(taskIds, storyId);
	}
}
