package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplanItem;
import ntut.csie.ezScrum.test.CreateData.DropTask;
import ntut.csie.ezScrum.test.CreateData.EditUnplanItem;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import servletunit.struts.MockStrutsTestCase;

public class ShowIssueHistoryTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateUnplanItem mCUI;
	private Configuration mConfig;
	private ProjectObject mProject;
	private final String mACTION_PATH = "/showIssueHistory";

	public ShowIssueHistoryTest(String testName) {
		super(testName);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增1個 project
		mCP = new CreateProject(1);
		mCP.exeCreateForDb();
		mProject = mCP.getAllProjects().get(0);

		// 新增1個 sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		super.setUp();
		// ================ set action info ========================
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mACTION_PATH);

		ini = null;
	}

	protected void tearDown() throws Exception {
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		ini = null;
		mCP = null;
		mCS = null;
		mCUI = null;
		mConfig = null;
	}

	public void testShowSprintBacklogTreeListInfo() throws Exception {
		ArrayList<Long> idList = mCS.getSprintsId();
		int storyCount = 1;
		int storyEst = 2;
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(storyCount,
		        storyEst, mCS.getSprintsId().size(), mCP,
		        CreateProductBacklog.COLUMN_TYPE_EST);
		addStoryToSprint.exe();

		Thread.sleep(1000);

		int taskCount = 1;
		int taskEst = 2;
		AddTaskToStory addTaskToStory = new AddTaskToStory(taskCount, taskEst,
		        addStoryToSprint, mCP);
		addTaskToStory.exe();

		// ================ set request info ========================
		String projectName = mProject.getName();
		String taskId = String.valueOf(addTaskToStory.getTasksId().get(0));
		request.setHeader("Referer", "?projectName=" + projectName);
		addRequestParameter("sprintID", String.valueOf(idList.get(0)));
		addRequestParameter("issueID", taskId);
		addRequestParameter("issueType", "Task");
		String expectedTaskName = addTaskToStory.getTasks().get(0).getName();
		String expectedIssueType = "Task";
		ArrayList<String> expectedDescription = genArrayList("Create Task #1",
		        "Append to Story #1");
		ArrayList<String> expectedHistoryType = genArrayList("", "");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// 執行 action
		actionPerform();

		// assert
		verifyNoActionErrors();
		verifyNoActionMessages();

		// assert response text
		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject historyObj = new JSONObject(actualResponseText);
		assertEquals(expectedTaskName, historyObj.getString("Name"));
		assertEquals(expectedIssueType, historyObj.getString("IssueType"));
		assertData(expectedHistoryType, expectedDescription,
		        historyObj.getJSONArray("IssueHistories"));
	}

	/**
	 * Story History 的測試 add 1 sprint, 1 story
	 */
	public void testShowStoryHistoryTest1() throws Exception {
		// 加入1個 Sprint
		ArrayList<Long> idList = mCS.getSprintsId();
		long sprintId = idList.get(0);
		// Sprint 加入1個 Story
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(1, 1,
		        idList.size(), mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		addStoryToSprint.exe();
		long storyId = addStoryToSprint.getStories().get(0).getId();

		// ================ set request info ========================
		String projectName = mProject.getName();
		// 設定 Session 資訊
		request.setHeader("Referer", "?projectName=" + projectName);
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		// 設定新增 Task 所需的資訊
		String expectedStoryId = String.valueOf(storyId);
		String expectedSprintId = String.valueOf(sprintId);
		String expectedIssueType = "Story";
		ArrayList<String> expectedDescription = genArrayList("Create Story #"
		        + storyId, "Append to Sprint #1");
		ArrayList<String> expectedHistoryType = genArrayList("", "");

		addRequestParameter("sprintID", expectedSprintId);
		addRequestParameter("issueID", expectedStoryId);
		addRequestParameter("issueType", "Story");

		// 執行 action
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();

		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject historyObj = new JSONObject(actualResponseText);
		assertEquals(expectedIssueType, historyObj.getString("IssueType"));
		assertEquals(addStoryToSprint.getStories().get(0).getName(),
		        historyObj.getString("Name"));
		assertData(expectedHistoryType, expectedDescription,
		        historyObj.getJSONArray("IssueHistories"));
	}

	/**
	 * Story History 的測試 add 1 sprint, 1 story and 1 task
	 */
	public void testShowStoryHistoryTest2() throws Exception {
		// 加入1個 Sprint
		ArrayList<Long> idList = mCS.getSprintsId();
		long sprintId = idList.get(0);
		// Sprint 加入1個 Story
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(1, 1,
		        idList.size(), mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		addStoryToSprint.exe();
		long storyId = addStoryToSprint.getStories().get(0).getId();

		// Story 加入1個 Task
		AddTaskToStory addTaskToStory = new AddTaskToStory(1, 1,
		        addStoryToSprint, mCP);
		addTaskToStory.exe();

		// ================ set request info ========================
		String projectName = mProject.getName();
		// 設定 Session 資訊
		request.setHeader("Referer", "?projectName=" + projectName);
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		// 設定新增 Task 所需的資訊
		String expectedStoryId = String.valueOf(storyId);
		String expectedSprintId = String.valueOf(sprintId);
		String expectedIssueType = "Story";
		ArrayList<String> expectedDescription = genArrayList("Create Story #1",
		        "Append to Sprint #1", "Add Task #1");
		ArrayList<String> expectedHistoryType = genArrayList("", "", "");

		addRequestParameter("sprintID", expectedSprintId);
		addRequestParameter("issueID", expectedStoryId);
		addRequestParameter("issueType", "Story");

		// 執行 action
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();

		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject historyObj = new JSONObject(actualResponseText);
		assertEquals(expectedIssueType, historyObj.getString("IssueType"));
		assertEquals(addStoryToSprint.getStories().get(0).getName(),
		        historyObj.getString("Name"));
		assertData(expectedHistoryType, expectedDescription,
		        historyObj.getJSONArray("IssueHistories"));
	}

	/**
	 * Story History 的測試 add 1 sprint, 1 story and 1 task and drop task
	 */
	public void testShowStoryHistoryTest3() throws Exception {
		// 加入1個 Sprint
		ArrayList<Long> idList = mCS.getSprintsId();
		long sprintId = idList.get(0);
		// Sprint 加入1個 Story
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(1, 1,
		        idList.size(), mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		addStoryToSprint.exe();

		long storyId = addStoryToSprint.getStories().get(0).getId();
		// Story 加入1個 Task
		AddTaskToStory addTaskToStory = new AddTaskToStory(1, 1,
		        addStoryToSprint, mCP);
		addTaskToStory.exe();
		long taskId = addTaskToStory.getTasksId().get(0);

		// drop Task from story
		DropTask dropTask = new DropTask(mCP, sprintId, storyId, taskId);
		dropTask.exe();

		// ================ set request info ========================
		String projectName = mProject.getName();
		// 設定 Session 資訊
		request.setHeader("Referer", "?projectName=" + projectName);
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		// 設定新增 Task 所需的資訊
		String expectedStoryId = String.valueOf(storyId);
		String expectedSprintId = String.valueOf(sprintId);
		String expectedIssueType = "Story";
		ArrayList<String> expectedDescription = genArrayList("Create Story #1",
		        "Append to Sprint #1", "Add Task #1", "Drop Task #1");
		ArrayList<String> expectedHistoryType = genArrayList("", "", "", "");

		addRequestParameter("sprintID", expectedSprintId);
		addRequestParameter("issueID", expectedStoryId);
		addRequestParameter("issueType", "Story");

		// 執行 action
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();

		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject historyObj = new JSONObject(actualResponseText);
		assertEquals(expectedIssueType, historyObj.getString("IssueType"));
		assertEquals(addStoryToSprint.getStories().get(0).getName(),
		        historyObj.getString("Name"));
		assertData(expectedHistoryType, expectedDescription,
		        historyObj.getJSONArray("IssueHistories"));
	}

	/**
	 * change story status
	 */
	public void testShowStoryHistoryTest4() throws Exception {
		// 加入1個 Sprint
		long sprintId = Long.valueOf(mCS.getSprintsId().get(0));
		// Sprint 加入1個 Story
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(1, 1, (int) sprintId, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		addStoryToSprint.exe();
		// projectName
		String projectName = mProject.getName();

		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(mProject, sprintId);
		// story Not Check Out -> Done
		StoryObject story = addStoryToSprint.getStories().get(0);
		sprintBacklogHelper.closeStory(story.getId(), story.getName(), story.getNotes(), "");

		// ================ set request info ========================
		// 設定 Session 資訊
		request.setHeader("Referer", "?projectName=" + projectName);
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// 先 assert story 的 history
		addRequestParameter("sprintID", String.valueOf(sprintId));
		addRequestParameter("issueID", String.valueOf(story.getId()));
		addRequestParameter("issueType", String.valueOf("Story"));

		// 執行 action
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();

		// get assert data
		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject storyHistory = new JSONObject(actualResponseText);

		// assert
		assertEquals(1, storyHistory.getLong("Id"));
		assertEquals(story.getName(), storyHistory.getString("Name"));
		assertEquals("Story", storyHistory.getString("IssueType"));

		JSONArray histories = storyHistory.getJSONArray("IssueHistories");
		assertEquals("Not Check Out => Done", histories.getJSONObject(2).getString("Description"));
		assertEquals("Status", histories.getJSONObject(2).getString("HistoryType"));
	}

	/**
	 * test add 1 task
	 */
	public void testShowTaskHistoryTest1() throws Exception {
		// 加入1個 Sprint
		long sprintId = Long.valueOf(mCS.getSprintsId().get(0));
		// Sprint 加入1個 Story
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(1, 1,
		        (int) sprintId, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		addStoryToSprint.exe();

		// Story 加入1個 Task
		AddTaskToStory addTaskToStory = new AddTaskToStory(1, 1,
		        addStoryToSprint, mCP);
		addTaskToStory.exe();

		// ================ set request info ========================
		String projectName = mProject.getName();
		long taskId = addTaskToStory.getTasksId().get(0);

		// 設定 Session 資訊
		request.setHeader("Referer", "?projectName=" + projectName);
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// 先 assert story 的 history
		addRequestParameter("sprintID", String.valueOf(sprintId));
		addRequestParameter("issueID", String.valueOf(taskId));
		addRequestParameter("issueType", "Task");

		// 執行 action
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();

		// get assert data
		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject historyObj = new JSONObject(actualResponseText);
		ArrayList<String> expectedDescription = genArrayList("Create Task #1",
		        "Append to Story #1");
		ArrayList<String> expectedHistoryType = genArrayList("", "");

		assertData(expectedHistoryType, expectedDescription,
		        historyObj.getJSONArray("IssueHistories"));
	}

	/**
	 * edit task info
	 */
	public void testShowTaskHistoryTest2() throws Exception {
		// 加入1個 Sprint
		long sprintId = Long.valueOf(mCS.getSprintsId().get(0));
		// Sprint 加入1個 Story
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(1, 1,
		        (int) sprintId, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		addStoryToSprint.exe();

		// Story 加入1個 Task
		AddTaskToStory addTaskToStory = new AddTaskToStory(1, 1, addStoryToSprint, mCP);
		addTaskToStory.exe();

		String projectName = mProject.getName();
		long taskId = addTaskToStory.getTasksId().get(0);

		TaskInfo taskInfo = new TaskInfo();
		taskInfo.taskId = taskId;
		taskInfo.name = "崩潰啦";
		taskInfo.estimate = 13;
		taskInfo.actual = 2;
		taskInfo.remains = 8;
		taskInfo.handlerId = 1;
		taskInfo.notes = "煩死啦";

		// edit task info
		SprintBacklogMapper sprintBacklogMapper = new SprintBacklogMapper(mCP.getAllProjects().get(0));
		sprintBacklogMapper.updateTask(taskInfo.taskId, taskInfo);

		// ================ set request info ========================
		// 設定 Session 資訊
		request.setHeader("Referer", "?projectName=" + projectName);
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// 先 assert story 的 history
		addRequestParameter("sprintID", String.valueOf(sprintId));
		addRequestParameter("issueID", String.valueOf(taskId));
		addRequestParameter("issueType", "Task");

		// 執行 action
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();

		// get assert data
		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject historyObj = new JSONObject(actualResponseText);
		ArrayList<String> expectedDescription = genArrayList("Create Task #1",
		        "Append to Story #1", "\"TEST_TASK_1\" => \"崩潰啦\"",
		        "\"TEST_TASK_NOTES_1\" => \"煩死啦\"", "1 => 13", "0 => 2",
		        "1 => 8", "admin");
		ArrayList<String> expectedHistoryType = genArrayList("", "", "Name", "Note",
		        "Estimate", "Actual Hour", "Remains", "Handler");

		assertData(expectedHistoryType, expectedDescription,
		        historyObj.getJSONArray("IssueHistories"));
	}

	/**
	 * change task status
	 */
	public void testShowTaskHistoryTest3() throws Exception {
		// 加入1個 Sprint
		long sprintId = Long.valueOf(mCS.getSprintsId().get(0));
		// Sprint 加入1個 Story
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(1, 1,
		        (int) sprintId, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		addStoryToSprint.exe();

		// Story 加入1個 Task
		AddTaskToStory addTaskToStory = new AddTaskToStory(1, 1, addStoryToSprint, mCP);
		addTaskToStory.exe();

		String projectName = mProject.getName();
		long taskId = addTaskToStory.getTasksId().get(0);

		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(
		        mProject, sprintId);
		// task Not Check Out -> Check Out
		TaskObject task = sprintBacklogHelper.getTask(taskId);
		sprintBacklogHelper.checkOutTask(taskId, task.getName(), "admin", "",
		        task.getNotes(), "");
		// task Check Out -> Done
		sprintBacklogHelper.closeTask(taskId, task.getName(), task.getNotes(),
		        task.getActual(), "");
		// task Done -> Check Out
		sprintBacklogHelper.reopenTask(taskId, task.getName(), task.getNotes(),
		        "");
		// task Check Out -> Not Check Out
		sprintBacklogHelper.resetTask(taskId, task.getName(), task.getNotes(),
		        "");

		// ================ set request info ========================
		// 設定 Session 資訊
		request.setHeader("Referer", "?projectName=" + projectName);
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// 先 assert story 的 history
		addRequestParameter("sprintID", String.valueOf(sprintId));
		addRequestParameter("issueID", String.valueOf(taskId));
		addRequestParameter("issueType", String.valueOf("Task"));

		// 執行 action
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();

		// get assert data
		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject historyObj = new JSONObject(actualResponseText);
		ArrayList<String> expectedDescription = genArrayList("Create Task #1",
		        "Append to Story #1", "Not Check Out => Check Out", "admin",
		        "1 => 0", "Check Out => Done", "Done => Check Out",
		        "Check Out => Not Check Out", "Remove handler admin");
		ArrayList<String> expectedHistoryType = genArrayList("", "", "Status",
		        "Handler", "Remains", "Status", "Status", "Status", "Handler");

		assertData(expectedHistoryType, expectedDescription,
		        historyObj.getJSONArray("IssueHistories"));
	}

	/**
	 * change task handler
	 */
	public void testShowTaskHistoryTest4() throws Exception {
		long sprintId = Long.valueOf(mCS.getSprintsId().get(0));

		// Sprint 加入1個 Story
		AddStoryToSprint ASTS = new AddStoryToSprint(1, 1, (int) sprintId, mCP,
		        CreateProductBacklog.COLUMN_TYPE_EST);
		ASTS.exe();

		// 新增1個 account
		CreateAccount CA = new CreateAccount(1);
		CA.exe();

		// assign role to account
		AddUserToRole AUTR = new AddUserToRole(mCP, CA);
		AUTR.exe_ST();

		// Story 加入1個 Task
		AddTaskToStory ATTS = new AddTaskToStory(1, 1, ASTS, mCP);
		ATTS.exe();

		String projectName = mProject.getName();
		long taskId = ATTS.getTasksId().get(0);

		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(
		        mProject, sprintId);
		// task Not Check Out -> Check Out
		TaskObject task = sprintBacklogHelper.getTask(taskId);
		sprintBacklogHelper.checkOutTask(taskId, task.getName(), "admin",
		        task.getPartnersUsername(), task.getNotes(), "");

		TaskInfo taskInfo = new TaskInfo();
		taskInfo.taskId = task.getId();
		taskInfo.name = task.getName();
		taskInfo.notes = task.getNotes();
		taskInfo.estimate = task.getEstimate();
		taskInfo.actual = task.getActual();
		taskInfo.remains = task.getRemains();
		sprintBacklogHelper.updateTask(taskInfo, AUTR.getNowAccount()
		        .getUsername(), "");

		// ================ set request info ========================
		// 設定 Session 資訊
		request.setHeader("Referer", "?projectName=" + projectName);
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// 先 assert story 的 history
		addRequestParameter("sprintID", String.valueOf(sprintId));
		addRequestParameter("issueID", String.valueOf(taskId));
		addRequestParameter("issueType", "Task");

		// 執行 action
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();

		// get assert data
		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject historyObj = new JSONObject(actualResponseText);
		ArrayList<String> expectedDescription = genArrayList("Create Task #1",
		        "Append to Story #1", "Not Check Out => Check Out", "admin",
		        "admin => TEST_ACCOUNT_ID_1");
		ArrayList<String> expectedHistoryType = genArrayList("", "", "Status",
		        "Handler", "Handler");

		assertData(expectedHistoryType, expectedDescription,
		        historyObj.getJSONArray("IssueHistories"));
	}

	/**
	 * UnplanItem History 的測試 1 unplanItem without editing
	 */
	public void testShowUnplanItemHistoryTest1() throws Exception {
		// 新增一個 UnplanItem
		mCUI = new CreateUnplanItem(1, mCP, mCS);
		mCUI.exe();
		String sprintId = String.valueOf(mCS.getSprintsId().get(0));
		String unplanId = String.valueOf(mCUI.getUnplansId().get(0));

		// ================== set parameter info ====================
		addRequestParameter("sprintID", sprintId);
		addRequestParameter("issueID", unplanId);
		addRequestParameter("issueType", "Unplan");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?projectName=" + mProject.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		ArrayList<String> expectedDescription = genArrayList("Create Unplan #1");
		ArrayList<String> expectedHistoryType = genArrayList("");
		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject historyObj = new JSONObject(actualResponseText);

		assertEquals("Unplan", historyObj.getString("IssueType"));
		assertEquals(mCUI.getUnplans().get(0).getName(),
		        historyObj.getString("Name"));
		assertData(expectedHistoryType, expectedDescription,
		        historyObj.getJSONArray("IssueHistories"));
	}

	/**
	 * UnplanItem History 的測試 1 unplanItem with editing to check out
	 */
	public void testShowUnplanItemHistoryTest2() throws Exception {
		// ================== init ====================
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		Thread.sleep(1000);

		AddUserToRole addUserToRole = new AddUserToRole(mCP, createAccount);
		addUserToRole.exe_ST();
		Thread.sleep(1000);

		mCUI = new CreateUnplanItem(1, mCP, mCS);
		mCUI.exe();
		Thread.sleep(1000);

		EditUnplanItem EU = new EditUnplanItem(mCUI, mCP, createAccount);
		EU.exe_CHECKOUT();
		Thread.sleep(1000);

		String sprintId = String.valueOf(mCS.getSprintsId().get(0));
		String unplanId = String.valueOf(mCUI.getUnplansId().get(0));

		// ================== set parameter info ====================
		addRequestParameter("sprintID", sprintId);
		addRequestParameter("issueID", unplanId);
		addRequestParameter("issueType", "Unplan");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?projectName=" + mProject.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();
		ArrayList<String> expectedDescription = genArrayList("Create Unplan #1",
		        "\"TEST_UNPLAN_NOTES_1\" => \"i am the update one\"",
		        "Not Check Out => Check Out", "TEST_ACCOUNT_ID_1");
		ArrayList<String> expectedHistoryType = genArrayList("", "Note",
		        "Status", "Handler");
		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject historyObj = new JSONObject(actualResponseText);

		assertEquals("Unplan", historyObj.getString("IssueType"));
		assertEquals(mCUI.getUnplans().get(0).getName(), historyObj.getString("Name"));
		assertData(expectedHistoryType, expectedDescription,
		        historyObj.getJSONArray("IssueHistories"));
	}

	/**
	 * UnplanItem History 的測試 1 unplanItem with editing to done
	 */
	public void testShowUnplanItemHistoryTest3() throws Exception {
		// ================== init ====================
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		Thread.sleep(1000);

		AddUserToRole addUserToRole = new AddUserToRole(mCP, createAccount);
		addUserToRole.exe_ST();
		Thread.sleep(1000);

		// 新增一個UnplanItem
		mCUI = new CreateUnplanItem(1, mCP, mCS);
		mCUI.exe();
		Thread.sleep(1000);

		EditUnplanItem EU = new EditUnplanItem(mCUI, mCP, createAccount);
		EU.exe_CHECKOUT();
		Thread.sleep(1000);

		EU = new EditUnplanItem(mCUI, mCP, createAccount);
		EU.exe_DONE();
		Thread.sleep(1000);

		String issueId = String.valueOf(mCUI.getUnplansId().get(0));

		// ================== set parameter info ====================
		addRequestParameter("sprintID", String.valueOf(mCS.getSprintsId().get(0)));
		addRequestParameter("issueID", issueId);
		addRequestParameter("issueType", "Unplan");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?projectName=" + mProject.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();
		ArrayList<String> expectedDescription = genArrayList("Create Unplan #1",
				"\"TEST_UNPLAN_NOTES_1\" => \"i am the update one\"",
		        "Not Check Out => Check Out", "TEST_ACCOUNT_ID_1",
		        "Check Out => Done");
		ArrayList<String> expectedHistoryType = genArrayList("", "Note", "Status",
		        "Handler", "Status");
		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject object = new JSONObject(actualResponseText);

		assertEquals("Unplan", object.get("IssueType"));
		assertEquals(mCUI.getUnplans().get(0).getName(), object.get("Name"));
		assertData(expectedHistoryType, expectedDescription,
		        object.getJSONArray("IssueHistories"));
	}

	/**
	 * 傳入若干個字串，組合成arrayList傳出
	 */
	private ArrayList<String> genArrayList(String... strs) {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (String str : strs) {
			arrayList.add(str);
		}
		return arrayList;
	}

	private void assertData(ArrayList<String> expectedHistoryType,
	        ArrayList<String> expectedDesc, JSONArray actualData)
	        throws JSONException {
		assertEquals(expectedHistoryType.size(), expectedDesc.size());
		assertEquals(expectedDesc.size(), actualData.length());
		for (int i = 0; i < expectedDesc.size(); i++) {
			JSONObject history = actualData.getJSONObject(i);

			String exceptType = expectedHistoryType.get(i);
			String exceptDesc = expectedDesc.get(i);

			assertEquals(exceptType, history.getString("HistoryType"));
			assertEquals(exceptDesc, history.getString("Description"));
		}
	}
}
