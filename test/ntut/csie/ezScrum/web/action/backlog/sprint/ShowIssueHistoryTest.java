package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
import ntut.csie.ezScrum.test.CreateData.IssueHistoryObject;
import ntut.csie.ezScrum.test.CreateData.IssueHistoryObject.IssueHistoryList;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

import com.google.gson.Gson;

public class ShowIssueHistoryTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private final String ACTION_PATH = "/showIssueHistory";
	private IProject project;
	private CreateUnplannedItem CU;
	private static final long TOLERANCE = 30;

	public ShowIssueHistoryTest(String testName) {
		super(testName);
	}

	protected void setUp() throws Exception {
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		this.CP = new CreateProject(1);
		this.CP.exeCreate();// 新增一測試專案
		this.project = this.CP.getProjectList().get(0);

		this.CS = new CreateSprint(2, this.CP);
		this.CS.exe();

		super.setUp();
		// ================ set action info ========================
		setContextDirectory(new File(config.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(this.ACTION_PATH);

		ini = null;
	}

	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe();

		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(this.config.getTestDataPath());

		super.tearDown();

		ini = null;
		projectManager = null;
		this.CP = null;
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
		List<String> expectedDescription = genArrayList("New Issue", "Append to Story 1", " 0 => 2", " 0 => 2");
		List<String> expectedHistoryType = genArrayList("", "", "Estimation ", "Remains ");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());

		// ================ 執行 action ==============================
		actionPerform();

		// ================ assert =============================
		verifyNoActionErrors();
		verifyNoActionMessages();
		// assert response text
		String actualResponseText = response.getWriterBuffer().toString();
		Gson gson = new Gson();
		IssueHistoryObject historyObj = gson.fromJson(actualResponseText, IssueHistoryObject.class);

		assertEquals(expectedTaskName, historyObj.Name);
		assertEquals(expectedIssueType, historyObj.IssueType);
		assertEquals(expectedLink, historyObj.Link);

		assertData(expectedDescription, expectedHistoryType, historyObj, genArrayList("New Issue", "Append to Story 1", " 0 => 2", " 0 => 2"));
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
		request.getSession().setAttribute("UserSession", config.getUserSession());
		request.getSession().setAttribute("Project", project);
		// 設定新增Task所需的資訊
		String expectedStoryID = String.valueOf(storyID);
		String expectedSprintID = String.valueOf(sprintID);
		List<String> expectedDescription = genArrayList("New Issue", " -1 => 1", " 0 => 50", " 0 => 1", " 0 => 1", " 0 => 100");
		List<String> expectedHistoryType = genArrayList("", "Sprint ", "Value ", "Sprint ", "Estimation ", "Importance ");

		addRequestParameter("sprintID", expectedSprintID);
		addRequestParameter("issueID", expectedStoryID);

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		Gson gson = new Gson();
		String actualResponseText = response.getWriterBuffer().toString();
		IssueHistoryObject historyObj = gson.fromJson(actualResponseText, IssueHistoryObject.class);
		assertEquals(addStory_Sprint.getIssueList().get(0).getCategory(), historyObj.IssueType);
		assertEquals(addStory_Sprint.getIssueList().get(0).getIssueLink(), historyObj.Link);
		assertEquals(addStory_Sprint.getIssueList().get(0).getSummary(), historyObj.Name);

		assertData(expectedDescription, expectedHistoryType, historyObj, genArrayList("New Issue", " -1 => 1", " 0 => 50", " 0 => 1", " 0 => 1", " 0 => 100"));
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
		request.getSession().setAttribute("UserSession", config.getUserSession());
		request.getSession().setAttribute("Project", project);
		// 設定新增Task所需的資訊
		String expectedStoryID = String.valueOf(storyID);
		String expectedSprintID = String.valueOf(sprintID);
		List<String> expectedDescription = genArrayList("New Issue", " -1 => 1", " 0 => 50", " 0 => 1", " 0 => 1", " 0 => 100", "Add Task 2");
		List<String> expectedHistoryType = genArrayList("", "Sprint ", "Value ", "Sprint ", "Estimation ", "Importance ", "");

		addRequestParameter("sprintID", expectedSprintID);
		addRequestParameter("issueID", expectedStoryID);

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		Gson gson = new Gson();
		String actualResponseText = response.getWriterBuffer().toString();
		IssueHistoryObject historyObj = gson.fromJson(actualResponseText, IssueHistoryObject.class);
		assertEquals(addStory_Sprint.getIssueList().get(0).getCategory(), historyObj.IssueType);
		assertEquals(addStory_Sprint.getIssueList().get(0).getIssueLink(), historyObj.Link);
		assertEquals(addStory_Sprint.getIssueList().get(0).getSummary(), historyObj.Name);

		assertData(expectedDescription, expectedHistoryType, historyObj, genArrayList("New Issue", " -1 => 1", " 0 => 50", " 0 => 1", " 0 => 1", " 0 => 100"));
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
		request.getSession().setAttribute("UserSession", config.getUserSession());
		request.getSession().setAttribute("Project", project);
		// 設定新增Task所需的資訊
		String expectedStoryID = String.valueOf(storyID);
		String expectedSprintID = String.valueOf(sprintID);
		List<String> expectedDescription = genArrayList("New Issue", " -1 => 1", " 0 => 50", " 0 => 1", " 0 => 1", " 0 => 100", "Add Task 2", "Drop Task 2");
		List<String> expectedHistoryType = genArrayList("", "Sprint ", "Value ", "Sprint ", "Estimation ", "Importance ", "", "");

		addRequestParameter("sprintID", expectedSprintID);
		addRequestParameter("issueID", expectedStoryID);

		// ================ 執行 action ====================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		Gson gson = new Gson();
		String actualResponseText = response.getWriterBuffer().toString();
		IssueHistoryObject historyObj = gson.fromJson(actualResponseText, IssueHistoryObject.class);
		assertEquals(addStory_Sprint.getIssueList().get(0).getCategory(), historyObj.IssueType);
		assertEquals(addStory_Sprint.getIssueList().get(0).getIssueLink(), historyObj.Link);
		assertEquals(addStory_Sprint.getIssueList().get(0).getSummary(), historyObj.Name);

		assertData(expectedDescription, expectedHistoryType, historyObj, genArrayList("New Issue", " -1 => 1", " 0 => 50", " 0 => 1", " 0 => 1", " 0 => 100"));
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
		request.getSession().setAttribute("UserSession", config.getUserSession());
		request.getSession().setAttribute("Project", project);

		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		List<String> expectedDescription = genArrayList("New Issue", " 0 => 1", " 0 => 2");
		List<String> expectedHistoryType = genArrayList("", "Sprint ", "Estimation ");
		Gson gson = new Gson();
		String actualResponseText = response.getWriterBuffer().toString();
		IssueHistoryObject historyObj = gson.fromJson(actualResponseText, IssueHistoryObject.class);
		assertEquals(CU.getIssueList().get(0).getCategory(), historyObj.IssueType);
		assertEquals(CU.getIssueList().get(0).getIssueLink(), historyObj.Link);
		assertEquals(CU.getIssueList().get(0).getSummary(), historyObj.Name);

		assertData(expectedDescription, expectedHistoryType, historyObj, genArrayList("New Issue", " 0 => 1", " 0 => 2"));
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
		request.getSession().setAttribute("UserSession", config.getUserSession());
		request.getSession().setAttribute("Project", project);

		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();
		List<String> expectedDescription = genArrayList("New Issue", " 0 => 1", " 0 => 2", " \"Not Checked Out\" => \"Checked Out\"");
		List<String> expectedHistoryType = genArrayList("", "Sprint ", "Estimation ", "Status ");
		Gson gson = new Gson();
		String actualResponseText = response.getWriterBuffer().toString();
		IssueHistoryObject historyObj = gson.fromJson(actualResponseText, IssueHistoryObject.class);
		assertEquals(CU.getIssueList().get(0).getCategory(), historyObj.IssueType);
		assertEquals(CU.getIssueList().get(0).getIssueLink(), historyObj.Link);
		assertEquals(CU.getIssueList().get(0).getSummary(), historyObj.Name);

		assertData(expectedDescription, expectedHistoryType, historyObj, genArrayList("New Issue", " 0 => 1", " 0 => 2"));
	}

	/**
	 * UnplanedItem History的測試 1 unplanedItem with editing to done
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
		request.getSession().setAttribute("UserSession", config.getUserSession());
		request.getSession().setAttribute("Project", project);

		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();
		List<String> expectedDescription = genArrayList("New Issue", " 0 => 1", " 0 => 2", " \"Not Checked Out\" => \"Checked Out\"", " \"Checked Out\" => \"Done\"");
		List<String> expectedHistoryType = genArrayList("", "Sprint ", "Estimation ", "Status ", "Status ");
		Gson gson = new Gson();
		String actualResponseText = response.getWriterBuffer().toString();
		IssueHistoryObject historyObj = gson.fromJson(actualResponseText, IssueHistoryObject.class);
		assertEquals(CU.getIssueList().get(0).getCategory(), historyObj.IssueType);
		assertEquals(CU.getIssueList().get(0).getIssueLink(), historyObj.Link);
		assertEquals(CU.getIssueList().get(0).getSummary(), historyObj.Name);

		assertData(expectedDescription, expectedHistoryType, historyObj, genArrayList("New Issue", " 0 => 1", " 0 => 2"));
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

	/**
	 * Function: 比對期望資料與DB資料是否相符，解決相同時間的資料query排序不同問題，也須通過測試 step 1. 將所有DB data依時間為key建立一個MAP step 2. 將預期資料與DB資料做比對 step 3. step2 比對錯誤時判斷是否有這筆資料但時間相同
	 * 
	 * @param expectedDescription
	 * @param expectedHistoryType
	 * @param historyObj
	 */
	private void assertData(List<String> expectedDescription, List<String> expectedHistoryType, IssueHistoryObject historyObj, List<String>... group) {
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		for (int i = 0; i < historyObj.IssueHistories.size(); i++) {
			IssueHistoryList issueHistory = historyObj.IssueHistories.get(i);
			if (map.get(issueHistory.ModifiedDate) == null) {
				ArrayList<String> list = new ArrayList<String>();
				list.add(issueHistory.Description);
				map.put(issueHistory.ModifiedDate, list);
//				System.out.println("new array : " + issueHistory.ModifiedDate + " " + issueHistory.Description);
			} else {
				map.get(issueHistory.ModifiedDate).add(issueHistory.Description);
//				System.out.println("already exist : " + issueHistory.ModifiedDate + " " + issueHistory.Description);
			}
		}

		for (int i = 0; i < historyObj.IssueHistories.size(); i++) {
			IssueHistoryList issueHistory = historyObj.IssueHistories.get(i);

			boolean hitFlag = false;
			if (expectedDescription.get(i).equals(issueHistory.Description)
			        && expectedHistoryType.get(i).equals(issueHistory.HistoryType)) {
				hitFlag = true;
//				System.out.println("normal hit true");
			} else {
//				System.out.println("normal not hit " + issueHistory.Description);
				Set<String> keySet = map.keySet();
				for (String key : keySet) {
					for (String desc : map.get(key)) {
//						System.out.println("iterate map key:" + key + " value:" + desc);
						if (desc.equals(expectedDescription.get(i))) {
//							System.out.println("equal : " + desc + " == " + expectedDescription.get(i));
							if (map.get(key).size() > 1) {
//								System.out.println("Hit true and size != 1 ");
								hitFlag = true;
								break;
							} else {
								if (group.length != 0) {
									for (int j = 0; j < group.length; j++) {
										if (group[j].contains(expectedDescription.get(i))) {
											hitFlag = true;
											break;
										}
									}
								} else {
									hitFlag = false;
								}
							}
						}
					}
					if (hitFlag) {
						break;
					}
				}
			}
			assertEquals(true, hitFlag);
		}
	}
}
