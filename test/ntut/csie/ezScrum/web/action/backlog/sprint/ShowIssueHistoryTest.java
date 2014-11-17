package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplannedItem;
import ntut.csie.ezScrum.test.CreateData.DropTask;
import ntut.csie.ezScrum.test.CreateData.EditUnplannedItem;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import servletunit.struts.MockStrutsTestCase;

public class ShowIssueHistoryTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private Configuration configuration;
	private final String ACTION_PATH = "/showIssueHistory";
	private IProject project;
	private CreateUnplannedItem CU;

	public ShowIssueHistoryTest(String testName) {
		super(testName);
	}

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		this.CP = new CreateProject(1);
		this.CP.exeCreate();// 新增一測試專案
		this.project = this.CP.getProjectList().get(0);

		this.CS = new CreateSprint(2, this.CP);
		this.CS.exe();

		super.setUp();
		// ================ set action info ========================
		setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(this.ACTION_PATH);

		ini = null;
	}

	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();

		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());
		
		configuration.setTestMode(false);
		configuration.store();

		super.tearDown();

		ini = null;
		projectManager = null;
		this.CP = null;
		configuration = null;
	}

	public void testShowSprintBacklogTreeListInfo() throws Exception {
		List<String> idList = this.CS.getSprintIDList();
		int sprintID = Integer.parseInt(idList.get(0));
		int storyCount = 1;
		int storyEst = 2;
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(storyCount, storyEst, sprintID, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStoryToSprint.exe();

		Thread.sleep(1000);

		int taskCount = 1;
		int taskEst = 2;
		AddTaskToStory addTaskToStory = new AddTaskToStory(taskCount, taskEst, addStoryToSprint, this.CP);
		addTaskToStory.exe();

		// ================ set request info ========================
		String projectName = this.project.getName();
		String issueID = String.valueOf(addTaskToStory.getTaskIDList().get(0));
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", idList.get(0));
		addRequestParameter("issueID", issueID);
		String expectedTaskName = addTaskToStory.getTaskList().get(0).getSummary();
		String expectedIssueType = addTaskToStory.getTaskList().get(0).getCategory();
		String expectedLink = "/ezScrum/showIssueInformation.do?issueID=" + issueID;
		List<String> expectedDescription = genArrayList("Create Task #2", "Append to Story #1");
		List<String> expectedHistoryType = genArrayList("", "");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		// ================ 執行 action ==============================
		actionPerform();

		// ================ assert =============================
		verifyNoActionErrors();
		verifyNoActionMessages();
		// assert response text
		String actualResponseText = response.getWriterBuffer().toString();

		JSONObject historyObj = new JSONObject(actualResponseText);

		assertEquals(expectedTaskName, historyObj.getString("Name"));
		assertEquals(expectedIssueType, historyObj.getString("IssueType"));
		assertEquals(expectedLink, historyObj.getString("Link"));
		assertData(expectedHistoryType, expectedDescription, historyObj.getJSONArray("IssueHistories"));
	}

	/**
	 * Story History 的測試 add 1 sprint, 1 story
	 */
	public void testShowStoryHistoryTest1() throws Exception {
		// 加入1個Sprint
		int sprintID = Integer.valueOf(this.CS.getSprintIDList().get(0));
		// Sprint加入1個Story
		AddStoryToSprint addStory_Sprint = new AddStoryToSprint(1, 1, sprintID, CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStory_Sprint.exe();
		int storyID = (int) addStory_Sprint.getIssueList().get(0).getIssueID();

		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		// 設定新增Task所需的資訊
		String expectedStoryID = String.valueOf(storyID);
		String expectedSprintID = String.valueOf(sprintID);
		List<String> expectedDescription = genArrayList("Create Story #" + storyID, "Append to Sprint #1");
		List<String> expectedHistoryType = genArrayList("", "");

		addRequestParameter("sprintID", expectedSprintID);
		addRequestParameter("issueID", expectedStoryID);

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		String actualResponseText = response.getWriterBuffer().toString();

		JSONObject historyObj = new JSONObject(actualResponseText);

		assertEquals(addStory_Sprint.getIssueList().get(0).getCategory(), historyObj.getString("IssueType"));
		assertEquals(addStory_Sprint.getIssueList().get(0).getIssueLink(), historyObj.getString("Link"));
		assertEquals(addStory_Sprint.getIssueList().get(0).getSummary(), historyObj.getString("Name"));
		assertData(expectedHistoryType, expectedDescription, historyObj.getJSONArray("IssueHistories"));
	}

	/**
	 * Story History 的測試 add 1 sprint, 1 story and 1 task
	 */
	public void testShowStoryHistoryTest2() throws Exception {
		// 加入1個Sprint
		int sprintID = Integer.valueOf(this.CS.getSprintIDList().get(0));
		// Sprint加入1個Story
		AddStoryToSprint addStory_Sprint = new AddStoryToSprint(1, 1, sprintID, CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStory_Sprint.exe();
		Thread.sleep(1000);
		int storyID = (int) addStory_Sprint.getIssueList().get(0).getIssueID();

		// Story加入1個Task
		AddTaskToStory addTask_Story = new AddTaskToStory(1, 1, addStory_Sprint, CP);
		addTask_Story.exe();

		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		// 設定新增Task所需的資訊
		String expectedStoryID = String.valueOf(storyID);
		String expectedSprintID = String.valueOf(sprintID);
		List<String> expectedDescription = genArrayList("Create Story #1", "Append to Sprint #1", "Add Task #2");
		List<String> expectedHistoryType = genArrayList("", "", "");

		addRequestParameter("sprintID", expectedSprintID);
		addRequestParameter("issueID", expectedStoryID);

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		String actualResponseText = response.getWriterBuffer().toString();
		
		JSONObject historyObj = new JSONObject(actualResponseText);

		assertEquals(addStory_Sprint.getIssueList().get(0).getCategory(), historyObj.getString("IssueType"));
		assertEquals(addStory_Sprint.getIssueList().get(0).getIssueLink(), historyObj.getString("Link"));
		assertEquals(addStory_Sprint.getIssueList().get(0).getSummary(), historyObj.getString("Name"));
		assertData(expectedHistoryType, expectedDescription, historyObj.getJSONArray("IssueHistories"));
	}

	/**
	 * Story History 的測試 add 1 sprint, 1 story and 1 task and drop task
	 */
	public void testShowStoryHistoryTest3() throws Exception {
		// 加入1個Sprint
		int sprintID = Integer.valueOf(this.CS.getSprintIDList().get(0));
		// Sprint加入1個Story
		AddStoryToSprint addStory_Sprint = new AddStoryToSprint(1, 1, sprintID, CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStory_Sprint.exe();
		Thread.sleep(1000);

		int storyID = (int) addStory_Sprint.getIssueList().get(0).getIssueID();
		// Story加入1個Task
		AddTaskToStory addTask_Story = new AddTaskToStory(1, 1, addStory_Sprint, CP);
		addTask_Story.exe();
		Thread.sleep(1000);

		int taskID = addTask_Story.getTaskIDList().get(0).intValue();
		// drop Task from story
		DropTask dropTask = new DropTask(CP, sprintID, storyID, taskID);
		dropTask.exe();

		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		// 設定新增Task所需的資訊
		String expectedStoryID = String.valueOf(storyID);
		String expectedSprintID = String.valueOf(sprintID);
		List<String> expectedDescription = genArrayList("Create Story #1", "Append to Sprint #1", "Add Task #2", "Drop Task #2");
		List<String> expectedHistoryType = genArrayList("", "", "", "");

		addRequestParameter("sprintID", expectedSprintID);
		addRequestParameter("issueID", expectedStoryID);

		// ================ 執行 action ====================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		String actualResponseText = response.getWriterBuffer().toString();
		
		JSONObject historyObj = new JSONObject(actualResponseText);

		assertEquals(addStory_Sprint.getIssueList().get(0).getCategory(), historyObj.getString("IssueType"));
		assertEquals(addStory_Sprint.getIssueList().get(0).getIssueLink(), historyObj.getString("Link"));
		assertEquals(addStory_Sprint.getIssueList().get(0).getSummary(), historyObj.getString("Name"));
		assertData(expectedHistoryType, expectedDescription, historyObj.getJSONArray("IssueHistories"));
	}

	/**
	 * UnplanedItem History的測試 1 unplanedItem without editing
	 */
	public void testShowUnplanedItemHistoryTest1() throws Exception {
		this.CU = new CreateUnplannedItem(1, CP, CS);
		this.CU.exe(); // 新增一個UnplannedItem
		String issueID = String.valueOf(CU.getIdList().get(0));

		// ================== set parameter info ====================
		addRequestParameter("sprintID", CS.getSprintIDList().get(0));
		addRequestParameter("issueID", issueID);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);

		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		List<String> expectedDescription = genArrayList("Create Unplanned #1", "Append to Sprint #1");
		List<String> expectedHistoryType = genArrayList("", "");
		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject historyObj = new JSONObject(actualResponseText);

		assertEquals(CU.getIssueList().get(0).getCategory(), historyObj.getString("IssueType"));
		assertEquals(CU.getIssueList().get(0).getIssueLink(), historyObj.getString("Link"));
		assertEquals(CU.getIssueList().get(0).getSummary(), historyObj.getString("Name"));
		assertData(expectedHistoryType, expectedDescription, historyObj.getJSONArray("IssueHistories"));
	}

	/**
	 * UnplanedItem History的測試 1 unplanedItem with editing to check out
	 */
	public void testShowUnplanedItemHistoryTest2() throws Exception {
		// ================== init ====================
		CreateAccount CA = new CreateAccount(1);
		CA.exe();

		Thread.sleep(1000);

		AddUserToRole addUserToRole = new AddUserToRole(CP, CA);
		addUserToRole.exe_ST();

		Thread.sleep(1000);

		this.CU = new CreateUnplannedItem(1, CP, CS);
		this.CU.exe(); // 新增一個UnplannedItem
		EditUnplannedItem EU = new EditUnplannedItem(CU, CP, CA);
		EU.exe_CO();
		String issueID = String.valueOf(CU.getIdList().get(0));

		// ================== set parameter info ====================
		addRequestParameter("sprintID", CS.getSprintIDList().get(0));
		addRequestParameter("issueID", issueID);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);

		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();
		List<String> expectedDescription = genArrayList("Create Unplanned #1", "Append to Sprint #1", "Not Check Out => Check Out", "TEST_ACCOUNT_ID_1", "TEST_UNPLANNED_NOTES_1 => i am the update one");
		List<String> expectedHistoryType = genArrayList("", "", "Status", "Handler", "Note");
		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject historyObj = new JSONObject(actualResponseText);

		assertEquals(CU.getIssueList().get(0).getCategory(), historyObj.getString("IssueType"));
		assertEquals(CU.getIssueList().get(0).getIssueLink(), historyObj.getString("Link"));
		assertEquals(CU.getIssueList().get(0).getSummary(), historyObj.getString("Name"));
		assertData(expectedHistoryType, expectedDescription, historyObj.getJSONArray("IssueHistories"));
	}

	/**
	 * UnplanedItem History 的測試 1 unplanedItem with editing to done
	 */
	public void testShowUnplanedItemHistoryTest3() throws Exception {
		// ================== init ====================
		CreateAccount CA = new CreateAccount(1);
		CA.exe();

		Thread.sleep(1000);

		AddUserToRole addUserToRole = new AddUserToRole(CP, CA);
		addUserToRole.exe_ST();

		Thread.sleep(1000);

		this.CU = new CreateUnplannedItem(1, CP, CS);
		this.CU.exe(); // 新增一個UnplannedItem

		Thread.sleep(1000);

		EditUnplannedItem EU = new EditUnplannedItem(CU, CP, CA);
		EU.exe_CO();
		Thread.sleep(1000);
		
		EU = new EditUnplannedItem(CU, CP, CA);
		EU.exe_DONE();
		String issueID = String.valueOf(CU.getIdList().get(0));

		// ================== set parameter info ====================
		addRequestParameter("sprintID", CS.getSprintIDList().get(0));
		addRequestParameter("issueID", issueID);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);

		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null); 
		verifyNoActionErrors();
		List<String> expectedDescription = genArrayList("Create Unplanned #1", "Append to Sprint #1", "Not Check Out => Check Out", "TEST_ACCOUNT_ID_1", "TEST_UNPLANNED_NOTES_1 => i am the update one", "Check Out => Done");
		List<String> expectedHistoryType = genArrayList("", "", "Status", "Handler", "Note", "Status");
		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject object = new JSONObject(actualResponseText);
		
		assertEquals(CU.getIssueList().get(0).getCategory(), object.get("IssueType"));
		assertEquals(CU.getIssueList().get(0).getIssueLink(), object.get("Link"));
		assertEquals(CU.getIssueList().get(0).getSummary(), object.get("Name"));
		assertData(expectedHistoryType, expectedDescription, object.getJSONArray("IssueHistories"));
	}

	/**
	 * 傳入若干個字串，組合成arrayList傳出
	 */
	private List<String> genArrayList(String... strs) {
		List<String> arrayList = new ArrayList<String>();
		for (String str : strs) {
			arrayList.add(str);
		}
		return arrayList;
	}
	
	private void assertData(List<String> exceptedHistoryType, List<String> exceptedDesc, JSONArray actualData) throws JSONException {
		for (int i = 0; i < exceptedHistoryType.size(); i++) {
			JSONObject history = actualData.getJSONObject(i);
			
			String exceptType = exceptedHistoryType.get(i);
			String exceptDesc = exceptedDesc.get(i);
			
			assertEquals(exceptType, history.getString("HistoryType"));
			assertEquals(exceptDesc, history.getString("Description"));
		}
	}
}
