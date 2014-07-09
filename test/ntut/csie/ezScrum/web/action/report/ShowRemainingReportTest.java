package ntut.csie.ezScrum.web.action.report;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.control.RemainingWorkReport;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class ShowRemainingReportTest extends MockStrutsTestCase {
	private CreateProject CP;
	private IProject project;
	private Configuration configuration;

	public ShowRemainingReportTest(String testMethod) {
		super(testMethod);
	}

	// 目前 setUp 設定的情境為︰產生一個Project、產生兩個Sprint、各個Sprint產生五個Story、每個Story設定點數兩點
	// 並且已經將Story加入到各個Sprint內、每個 Story 產生兩個一點的 Tasks 並且正確加入
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案
		this.project = this.CP.getProjectList().get(0);

		super.setUp();

		setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent")); // 設定讀取的
		// struts-config檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/showRemainingReport");

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getTestDataPath());
		
		configuration.setTestMode(false);
		configuration.store();

		super.tearDown();

		// ============= release ==============
		ini = null;
		this.CP = null;
		this.project = null;
		configuration = null;
	}

	/**
	 * No Sprint and show error
	 */
	public void testShowRemainingReport_Error_1() throws Exception {
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);

		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath("/DisplayMessage.jsp");
		verifyForward("displayMessage");
		verifyNoActionErrors();
		String expectedMessage = "No sprints in project!";
		String resultMessage = (String) request.getAttribute("message");
		assertEquals(expectedMessage, resultMessage);
	}
	
	/**
	 * project中有sprint 但帳號權限不符合
	 */
	public void testShowRemainingReport_Error_2() throws IOException {
		// ================ set initial data =======================
		CreateSprint CS = new CreateSprint(1, this.CP);
		CS.exe(); 	// 新增兩個 Sprint
		IProject project = CP.getProjectList().get(0);
		// 新增帳號
		CreateAccount testAccount = new CreateAccount(1);
		testAccount.exe();
		AddUserToRole addUserToRole = new AddUserToRole(CP, testAccount);
		addUserToRole.exe_Guest();
		// 使用無權限的帳號資訊塞到UserSession
//		IAccount theAccount = null;
//		IAccountManager manager = AccountFactory.getManager();
//		theAccount = manager.getAccount(testAccount.getAccount_ID(1));
		UserObject theAccount = new AccountMapper().getAccount(testAccount.getAccount_ID(1));
		IUserSession theUserSession = new UserSession(theAccount);

		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("System failure!");

		// ================ set request info ========================
		String projectName = project.getName();
		request.setHeader("Referer", "?PID=" + projectName); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", theUserSession);
		request.getSession().setAttribute("Project", project);

		// ================ 執行 action ==============================
		actionPerform();
		// 驗證Path
		verifyForward("displayMessage");
		verifyForwardPath("/DisplayMessage.jsp");
		verifyNoActionErrors();

		String actualResponseText = request.getAttribute("message").toString();
		assertEquals(expectedResponseTest.toString(), actualResponseText);
	}

	/**
	 * 判斷Task report的資料是否正確
	 */
	public void testShowRemainingReport_Task_1() throws Exception {
		// ================ set initial data =======================
		final int SPRINT_COUNT = 1, STORY_COUNT = 5, TASK_COUNT = 2, STORY_EST = 2, TASK_EST = 1;
		CreateSprint CS = new CreateSprint(SPRINT_COUNT, this.CP);
		CS.exe(); 	// 新增兩個 Sprint
		AddStoryToSprint ASS = new AddStoryToSprint(STORY_COUNT, STORY_EST, CS, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		ASS.exe(); 	// 新增五筆 Stories 到兩個 Sprints 內，並設計每個 Sprint 的 Story 點數總和為
		AddTaskToStory ATS = new AddTaskToStory(TASK_COUNT, TASK_EST, ASS, this.CP);
		ATS.exe(); 	// 新增兩筆 Task 到各個 Stories 內
		Thread.sleep(1000);

		String sprintID = CS.getSprintIDList().get(0);
		// 將第一個Story的 : 1個task設為done, 1個task設為checkout
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, configuration.getUserSession(), sprintID);
		sprintBacklogLogic.doneIssue(ATS.getTaskList().get(0).getIssueID(), ATS.getTaskList().get(0).getSummary(), "", null, null);
		sprintBacklogLogic.checkOutTask(ATS.getTaskList().get(1).getIssueID(), ATS.getTaskList().get(1).getSummary(), configuration.USER_ID, "", ATS.getTaskList().get(1).getNotes(), null);
		Thread.sleep(1000);

		// ================== set parameter info ====================
		addRequestParameter("sprintID", sprintID); // 取得第一筆 SprintPlan
		addRequestParameter("type", "Task"); // 沒有指定User ID資料

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);

		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath("/Pages/ShowRemainingReport.jsp");
		verifyForward("success");
		verifyNoActionErrors();
		RemainingWorkReport report = (RemainingWorkReport) request.getAttribute("RemainingWorkReport");
		// 觀看回來的NonCheckOut, CheckOut, Done數量是否正確
		assertEquals(STORY_COUNT * TASK_COUNT, report.getTotalQuantity());
		assertEquals(STORY_COUNT * TASK_COUNT - 2, report.getNonAssignQuantity());
		assertEquals(1, report.getAssignedQuantity());
		assertEquals(1, report.getDoneQuantity());

		assertEquals(CS.getSprintIDList().get(0), request.getAttribute("iteration").toString());
		assertEquals(false, request.getAttribute("OutofSprint"));
		assertEquals("", request.getAttribute("setDate"));
		assertEquals("./Workspace/TEST_PROJECT_1/_metadata/RemainingWork/Report/RemainingWork1.png", report.getRemainingWorkChartPath());
	}

	/**
	 * 將Date設為日期外，系統應該會回傳OutOfDay的訊息並將日期改設為當日繼續執行
	 */
	public void testShowRemainingReport_Task_2() throws Exception {
		// ================ set initial data =======================
		final int SPRINT_COUNT = 1, STORY_COUNT = 5, TASK_COUNT = 2, STORY_EST = 2, TASK_EST = 1;
		CreateSprint CS = new CreateSprint(SPRINT_COUNT, this.CP);
		CS.exe(); 	// 新增兩個 Sprint
		AddStoryToSprint ASS = new AddStoryToSprint(STORY_COUNT, STORY_EST, CS, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		ASS.exe(); 	// 新增五筆 Stories 到兩個 Sprints 內，並設計每個 Sprint 的 Story 點數總和為
		AddTaskToStory ATS = new AddTaskToStory(TASK_COUNT, TASK_EST, ASS, this.CP);
		ATS.exe(); 	// 新增兩筆 Task 到各個 Stories 內
		Thread.sleep(1000);

		String sprintID = CS.getSprintIDList().get(0);
		// 將第一個Sprint第一個Story的 : 1個task設為done, 1個task設為checkout
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, configuration.getUserSession(), sprintID);
		sprintBacklogLogic.doneIssue(ATS.getTaskList().get(0).getIssueID(), ATS.getTaskList().get(0).getSummary(), "", null, null);
		sprintBacklogLogic.checkOutTask(ATS.getTaskList().get(1).getIssueID(), ATS.getTaskList().get(1).getSummary(), configuration.USER_ID, "", ATS.getTaskList().get(1).getNotes(), null);
		Thread.sleep(1000);

		// ================== set parameter info ====================
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd-HH:mm");

		long today = System.currentTimeMillis();
		long ONE_DAY = 24 * 60 * 60 * 1000;
		String setDate = format.format(new Date(today + ONE_DAY));
		String expectedDate = format.format(new Date(today));
		addRequestParameter("Date", setDate);
		addRequestParameter("type", "Task"); // 沒有指定User ID資料

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);

		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath("/Pages/ShowRemainingReport.jsp");
		verifyForward("success");
		verifyNoActionErrors();
		RemainingWorkReport report = (RemainingWorkReport) request.getAttribute("RemainingWorkReport");
		// 觀看回來的NonCheckOut, CheckOut, Done數量是否正確
		assertEquals(STORY_COUNT * TASK_COUNT, report.getTotalQuantity());
		assertEquals(STORY_COUNT * TASK_COUNT - 2, report.getNonAssignQuantity());
		assertEquals(1, report.getAssignedQuantity());
		assertEquals(1, report.getDoneQuantity());

		assertEquals(CS.getSprintIDList().get(0), request.getAttribute("iteration").toString());
		assertEquals("OutOfDay", request.getAttribute("OutofSprint"));
		assertEquals(expectedDate, request.getAttribute("setDate"));
		assertEquals("./Workspace/TEST_PROJECT_1/_metadata/RemainingWork/Report/RemainingWork1.png", report.getRemainingWorkChartPath());
	}

	/**
	 * 全部的參數都沒給，應該要使用最近的sprint與Task的type
	 */
	public void testShowRemainingReport_Task_3() throws Exception {
		// ================ set initial data =======================
		final int SPRINT_COUNT = 1, STORY_COUNT = 5, TASK_COUNT = 2, STORY_EST = 2, TASK_EST = 1;
		CreateSprint CS = new CreateSprint(SPRINT_COUNT, this.CP);
		CS.exe(); 	// 新增兩個 Sprint
		AddStoryToSprint ASS = new AddStoryToSprint(STORY_COUNT, STORY_EST, CS, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		ASS.exe(); 	// 新增五筆 Stories 到兩個 Sprints 內，並設計每個 Sprint 的 Story 點數總和為
		AddTaskToStory ATS = new AddTaskToStory(TASK_COUNT, TASK_EST, ASS, this.CP);
		ATS.exe(); 	// 新增兩筆 Task 到各個 Stories 內
		Thread.sleep(1000);

		String sprintID = CS.getSprintIDList().get(0);
		// 將第一個Story的 : 1個task設為done, 1個task設為checkout
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, configuration.getUserSession(), sprintID);
		sprintBacklogLogic.doneIssue(ATS.getTaskList().get(0).getIssueID(), ATS.getTaskList().get(0).getSummary(), "", null, null);
		sprintBacklogLogic.checkOutTask(ATS.getTaskList().get(1).getIssueID(), ATS.getTaskList().get(1).getSummary(), configuration.USER_ID, "", ATS.getTaskList().get(1).getNotes(), null);
		Thread.sleep(1000);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);

		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath("/Pages/ShowRemainingReport.jsp");
		verifyForward("success");
		verifyNoActionErrors();
		RemainingWorkReport report = (RemainingWorkReport) request.getAttribute("RemainingWorkReport");
		// 觀看回來的NonCheckOut, CheckOut, Done數量是否正確
		assertEquals(STORY_COUNT * TASK_COUNT, report.getTotalQuantity());
		assertEquals(STORY_COUNT * TASK_COUNT - 2, report.getNonAssignQuantity());
		assertEquals(1, report.getAssignedQuantity());
		assertEquals(1, report.getDoneQuantity());

		assertEquals(CS.getSprintIDList().get(0), request.getAttribute("iteration").toString());
		assertEquals(false, request.getAttribute("OutofSprint"));
		assertEquals("", request.getAttribute("setDate"));
		assertEquals("./Workspace/TEST_PROJECT_1/_metadata/RemainingWork/Report/RemainingWork1.png", report.getRemainingWorkChartPath());
	}

	/**
	 * 超過最後一個sprint的時間，應該要拿最後一個sprint的資料
	 */
	public void testShowRemainingReport_Task_4() throws Exception {
		// ================ set initial data =======================
		final int SPRINT_COUNT = 1, STORY_COUNT = 5, TASK_COUNT = 2, STORY_EST = 2, TASK_EST = 1;
		CreateSprint CS = new CreateSprint(SPRINT_COUNT, this.CP);
		CS.exe(); 	// 新增兩個 Sprint
		AddStoryToSprint ASS = new AddStoryToSprint(STORY_COUNT, STORY_EST, CS, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		ASS.exe(); 	// 新增五筆 Stories 到兩個 Sprints 內，並設計每個 Sprint 的 Story 點數總和為
		AddTaskToStory ATS = new AddTaskToStory(TASK_COUNT, TASK_EST, ASS, this.CP);
		ATS.exe(); 	// 新增兩筆 Task 到各個 Stories 內
		Thread.sleep(1000);

		String sprintID = CS.getSprintIDList().get(0);
		// 將第一個Sprint第一個Story的 : 1個task設為done, 1個task設為checkout
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, configuration.getUserSession(), sprintID);
		sprintBacklogLogic.doneIssue(ATS.getTaskList().get(0).getIssueID(), ATS.getTaskList().get(0).getSummary(), "", null, null);
		sprintBacklogLogic.checkOutTask(ATS.getTaskList().get(1).getIssueID(), ATS.getTaskList().get(1).getSummary(), configuration.USER_ID, "", ATS.getTaskList().get(1).getNotes(), null);
		Thread.sleep(1000);

		// ================== set parameter info ====================
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd-HH:mm");

		long today = System.currentTimeMillis();
		long ONE_DAY = 15 * 24 * 60 * 60 * 1000;
		String setDate = format.format(new Date(today + ONE_DAY));
		String expectedDate = format.format(new Date(today));
		addRequestParameter("Date", setDate);
		addRequestParameter("type", "Task"); // 沒有指定User ID資料

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);

		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath("/Pages/ShowRemainingReport.jsp");
		verifyForward("success");
		verifyNoActionErrors();
		RemainingWorkReport report = (RemainingWorkReport) request.getAttribute("RemainingWorkReport");
		// 觀看回來的NonCheckOut, CheckOut, Done數量是否正確
		assertEquals(STORY_COUNT * TASK_COUNT, report.getTotalQuantity());
		assertEquals(STORY_COUNT * TASK_COUNT - 2, report.getNonAssignQuantity());
		assertEquals(1, report.getAssignedQuantity());
		assertEquals(1, report.getDoneQuantity());

		assertEquals(CS.getSprintIDList().get(0), request.getAttribute("iteration").toString());
		assertEquals("OutOfDay", request.getAttribute("OutofSprint"));
		assertEquals(expectedDate, request.getAttribute("setDate"));
		assertEquals("./Workspace/TEST_PROJECT_1/_metadata/RemainingWork/Report/RemainingWork1.png", report.getRemainingWorkChartPath());
	}
	
	/**
	 * 判斷Story report的資料是否正確
	 */
	public void testShowRemainingReport_Story_1() throws Exception {
		// ================ set initial data =======================
		final int SPRINT_COUNT = 1, STORY_COUNT = 5, TASK_COUNT = 1, STORY_EST = 2, TASK_EST = 1;
		CreateSprint CS = new CreateSprint(SPRINT_COUNT, this.CP);
		CS.exe(); 	// 新增兩個 Sprint
		AddStoryToSprint ASS = new AddStoryToSprint(STORY_COUNT, STORY_EST, CS, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		ASS.exe(); 	// 新增五筆 Stories 到兩個 Sprints 內，並設計每個 Sprint 的 Story 點數總和為
		AddTaskToStory ATS = new AddTaskToStory(TASK_COUNT, TASK_EST, ASS, this.CP);
		ATS.exe(); 	// 新增兩筆 Task 到各個 Stories 內
		Thread.sleep(1000);

		String sprintID = CS.getSprintIDList().get(0);
		// 1個story設為done, 1個task設為checkout
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, configuration.getUserSession(), sprintID);
		sprintBacklogLogic.doneIssue(ASS.getIssueList().get(0).getIssueID(), ASS.getIssueList().get(0).getSummary(), "", null, null);
		sprintBacklogLogic.checkOutTask(ATS.getTaskList().get(1).getIssueID(), ATS.getTaskList().get(1).getSummary(), configuration.USER_ID, "", ATS.getTaskList().get(1).getNotes(), null);
		Thread.sleep(1000);

		// ================== set parameter info ====================
		addRequestParameter("sprintID", sprintID); // 取得第一筆 SprintPlan
		addRequestParameter("type", "Story"); // 沒有指定User ID資料

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);

		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath("/Pages/ShowRemainingReport.jsp");
		verifyForward("success");
		verifyNoActionErrors();
		RemainingWorkReport report = (RemainingWorkReport) request.getAttribute("RemainingWorkReport");
		// 觀看回來的NonCheckOut, CheckOut, Done數量是否正確
		assertEquals(STORY_COUNT, report.getTotalQuantity());
		assertEquals(STORY_COUNT - 2, report.getNonAssignQuantity());
		assertEquals(1, report.getAssignedQuantity());
		assertEquals(1, report.getDoneQuantity());

		assertEquals(CS.getSprintIDList().get(0), request.getAttribute("iteration").toString());
		assertEquals(false, request.getAttribute("OutofSprint"));
		assertEquals("", request.getAttribute("setDate"));
		assertEquals("./Workspace/TEST_PROJECT_1/_metadata/RemainingWork/Report/RemainingWork1.png", report.getRemainingWorkChartPath());
	}

	/**
	 * 將Date設為日期外，系統應該會回傳OutOfDay的訊息並將日期改設為當日繼續執行
	 */
	public void testShowRemainingReport_Story_2() throws Exception {
		// ================ set initial data =======================
		final int SPRINT_COUNT = 1, STORY_COUNT = 5, TASK_COUNT = 1, STORY_EST = 2, TASK_EST = 1;
		CreateSprint CS = new CreateSprint(SPRINT_COUNT, this.CP);
		CS.exe(); 	// 新增兩個 Sprint
		AddStoryToSprint ASS = new AddStoryToSprint(STORY_COUNT, STORY_EST, CS, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		ASS.exe(); 	// 新增五筆 Stories 到兩個 Sprints 內，並設計每個 Sprint 的 Story 點數總和為
		AddTaskToStory ATS = new AddTaskToStory(TASK_COUNT, TASK_EST, ASS, this.CP);
		ATS.exe(); 	// 新增兩筆 Task 到各個 Stories 內
		Thread.sleep(1000);

		String sprintID = CS.getSprintIDList().get(0);
		// 將第一個Sprint第一個Story的 : 1個task設為done, 1個task設為checkout
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, configuration.getUserSession(), sprintID);
		sprintBacklogLogic.doneIssue(ASS.getIssueList().get(0).getIssueID(), ASS.getIssueList().get(0).getSummary(), "", null, null);
		sprintBacklogLogic.checkOutTask(ATS.getTaskList().get(1).getIssueID(), ATS.getTaskList().get(1).getSummary(), configuration.USER_ID, "", ATS.getTaskList().get(1).getNotes(), null);
		Thread.sleep(1000);

		// ================== set parameter info ====================
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd-HH:mm");

		long today = System.currentTimeMillis();
		long ONE_DAY = 24 * 60 * 60 * 1000;
		String setDate = format.format(new Date(today + ONE_DAY));
		String expectedDate = format.format(new Date(today));
		addRequestParameter("Date", setDate);
		addRequestParameter("type", "Story"); // 沒有指定User ID資料

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);

		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath("/Pages/ShowRemainingReport.jsp");
		verifyForward("success");
		verifyNoActionErrors();
		RemainingWorkReport report = (RemainingWorkReport) request.getAttribute("RemainingWorkReport");
		// 觀看回來的NonCheckOut, CheckOut, Done數量是否正確
		assertEquals(STORY_COUNT, report.getTotalQuantity());
		assertEquals(STORY_COUNT - 2, report.getNonAssignQuantity());
		assertEquals(1, report.getAssignedQuantity());
		assertEquals(1, report.getDoneQuantity());

		assertEquals(CS.getSprintIDList().get(0), request.getAttribute("iteration").toString());
		assertEquals("OutOfDay", request.getAttribute("OutofSprint"));
		assertEquals(expectedDate, request.getAttribute("setDate"));
		assertEquals("./Workspace/TEST_PROJECT_1/_metadata/RemainingWork/Report/RemainingWork1.png", report.getRemainingWorkChartPath());
	}
	
	/**
	 * 超過最後一個sprint的時間，應該要拿最後一個sprint的資料
	 */
	public void testShowRemainingReport_Story_3() throws Exception {
		// ================ set initial data =======================
		final int SPRINT_COUNT = 1, STORY_COUNT = 5, TASK_COUNT = 1, STORY_EST = 2, TASK_EST = 1;
		CreateSprint CS = new CreateSprint(SPRINT_COUNT, this.CP);
		CS.exe(); 	// 新增兩個 Sprint
		AddStoryToSprint ASS = new AddStoryToSprint(STORY_COUNT, STORY_EST, CS, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		ASS.exe(); 	// 新增五筆 Stories 到兩個 Sprints 內，並設計每個 Sprint 的 Story 點數總和為
		AddTaskToStory ATS = new AddTaskToStory(TASK_COUNT, TASK_EST, ASS, this.CP);
		ATS.exe(); 	// 新增兩筆 Task 到各個 Stories 內
		Thread.sleep(1000);

		String sprintID = CS.getSprintIDList().get(0);
		// 將第一個Sprint第一個Story的 : 1個task設為done, 1個task設為checkout
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, configuration.getUserSession(), sprintID);
		sprintBacklogLogic.doneIssue(ASS.getIssueList().get(0).getIssueID(), ASS.getIssueList().get(0).getSummary(), "", null, null);
		sprintBacklogLogic.checkOutTask(ATS.getTaskList().get(1).getIssueID(), ATS.getTaskList().get(1).getSummary(), configuration.USER_ID, "", ATS.getTaskList().get(1).getNotes(), null);
		Thread.sleep(1000);

		// ================== set parameter info ====================
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd-HH:mm");

		long today = System.currentTimeMillis();
		long ONE_DAY = 15 * 24 * 60 * 60 * 1000;
		String setDate = format.format(new Date(today + ONE_DAY));
		String expectedDate = format.format(new Date(today));
		addRequestParameter("Date", setDate);
		addRequestParameter("type", "Story"); // 沒有指定User ID資料

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);

		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath("/Pages/ShowRemainingReport.jsp");
		verifyForward("success");
		verifyNoActionErrors();
		RemainingWorkReport report = (RemainingWorkReport) request.getAttribute("RemainingWorkReport");
		// 觀看回來的NonCheckOut, CheckOut, Done數量是否正確
		assertEquals(STORY_COUNT, report.getTotalQuantity());
		assertEquals(STORY_COUNT - 2, report.getNonAssignQuantity());
		assertEquals(1, report.getAssignedQuantity());
		assertEquals(1, report.getDoneQuantity());

		assertEquals(CS.getSprintIDList().get(0), request.getAttribute("iteration").toString());
		assertEquals("OutOfDay", request.getAttribute("OutofSprint"));
		assertEquals(expectedDate, request.getAttribute("setDate"));
		assertEquals("./Workspace/TEST_PROJECT_1/_metadata/RemainingWork/Report/RemainingWork1.png", report.getRemainingWorkChartPath());
	}
}
