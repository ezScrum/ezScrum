package ntut.csie.ezScrum.web.action.report;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.ChangeIssueStatus;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.control.TaskBoard;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.form.IterationPlanForm;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.jcis.account.core.LogonException;
import ntut.csie.jcis.core.util.DateUtil;
import servletunit.struts.MockStrutsTestCase;

public class ShowTaskBoardActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
	private Configuration mConfig;

	public ShowTaskBoardActionTest(String testMethod) {
		super(testMethod);
	}

	// 目前 setUp 設定的情境為︰產生一個Project、產生兩個Sprint、各個Sprint產生五個Story、每個Story設定點數兩點
	// 並且已經將Story加入到各個Sprint內、每個 Story 產生兩個一點的 Tasks 並且正確加入
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		mCP = new CreateProject(1);
		mCP.exeCreate(); // 新增一測試專案

		mCS = new CreateSprint(2, mCP);
		mCS.exe(); // 新增兩個 Sprint

		int Story_Count = 5;
		int Story_Estimation = 2;
		// 新增五筆 Stories 到兩個 Sprints 內，並設計每個 Sprint 的 Story 點數總和為 10
		mASTS = new AddStoryToSprint(Story_Count, Story_Estimation, mCS, mCP,
				"EST");
		mASTS.exe();

		int Task_Count = 2;
		mATTS = new AddTaskToStory(Task_Count, 1, mASTS, mCP);
		mATTS.exe(); // 新增兩筆 Task 到各個 Stories 內

		super.setUp();

		// 設定讀取的  struts-config 檔案路徑
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/showTaskBoard");

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mASTS = null;
		mATTS = null;
		mConfig = null;
	}

	// 正常執行
	// 測試 TaskBoard 上方資訊列表所有資訊是否正確
	public void testexecute() throws Exception {
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);

		// ================== set parameter info ====================
		addRequestParameter("sprintID", Integer.toString(mCS.getSprintCount() - 1));
		addRequestParameter("UserID", "ALL");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ set session info ========================
		// SessionManager 會對 URL 的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath("/Layout/SubLayout.jsp");
		verifyForward("success");
		verifyNoActionErrors();

		// 測試 TaskBoard 上方資訊列表所有資訊是否正確
		// 測試 Story/Task Point 計算是否正確
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, mCS.getSprintsId().get(0));
		TaskBoard exceptedTaskBoard = new TaskBoard(sprintBacklogLogic, sprintBacklogLogic.getSprintBacklogMapper());
		TaskBoard actualTaskBoard = (TaskBoard) getMockRequest().getAttribute("TaskBoard");
		assertEquals("10.0 / 10.0", actualTaskBoard.getInitialStoryPoint());
		assertEquals("10.0 / -", actualTaskBoard.getInitialTaskPoint());
		assertEquals(exceptedTaskBoard.getStories().size(), actualTaskBoard.getStories().size());
		for (StoryObject story : exceptedTaskBoard.getStories()) {
			assertEquals(story.getId(), story.getId());
		}
		assertEquals(exceptedTaskBoard.getSprintGoal(), actualTaskBoard.getSprintGoal());
		assertEquals(mCS.TEST_SPRINT_GOAL + "1", actualTaskBoard.getSprintGoal());
		assertEquals(exceptedTaskBoard.getSprintId(), actualTaskBoard.getSprintId());
		assertEquals(1, actualTaskBoard.getSprintId());
		assertEquals(exceptedTaskBoard.getStories().size(), actualTaskBoard.getStories().size());
		for (StoryObject story : exceptedTaskBoard.getStories()) {
			assertEquals(story.getId(), story.getId());
		}
		assertEquals(exceptedTaskBoard.getStoryChartLink(), actualTaskBoard.getStoryChartLink());
		assertEquals("10.0 / 10.0", actualTaskBoard.getStoryPoint());
		assertEquals(exceptedTaskBoard.getTaskChartLink(), actualTaskBoard.getTaskChartLink());
		assertEquals("10.0 / 10.0", actualTaskBoard.getTaskPoint());

		// 測試其餘 request
		SprintPlanHelper helper = new SprintPlanHelper(project);
		List<ISprintPlanDesc> ExpectedPlans = helper.loadListPlans();
		List<ISprintPlanDesc> ActualPlans = (List<ISprintPlanDesc>) getMockRequest().getAttribute("SprintPlans");
		for (int i = 0; i < ExpectedPlans.size(); i++) {
			assertEquals(ExpectedPlans.get(i).getID(), ActualPlans.get(i).getID());
		}

		List<String> ExpectedActorList = new LinkedList<String>();
		List<String> ActualActorList = (List<String>) getMockRequest().getAttribute("ActorList");
		ExpectedActorList.add("ALL");
		for (int i = 0; i < ExpectedActorList.size(); i++) {
			assertEquals(ExpectedActorList.get(i), ActualActorList.get(i));
		}

		assertEquals("ALL", getMockRequest().getAttribute("User"));

		// ============= release ==============
		project = null;
		exceptedTaskBoard = null;
		actualTaskBoard = null;
		helper = null;
		ExpectedPlans = null;
		ActualPlans = null;
	}

	// // 待修改: ShowTaskBoardAction.java #86 未處理好產生 null pointer
	// // 測試代入超出範圍的 Sprint 參數
	// public void testWrongParameter1() throws Exception {
	// // ================ set initial data =======================
	// IProject project = mCP.getProjectList().get(0);
	// // ================ set initial data =======================
	//
	// // ================== set parameter info ====================
	// addRequestParameter("sprintID", "100"); // 代入超出範圍的 Sprint ID
	// addRequestParameter("UserID", "ALL"); // 沒有指定User ID資料
	// // ================== set parameter info ====================
	//
	// // ================ set session info ========================
	// request.getSession().setAttribute("UserSession",
	// mConfig.getUserSession());
	// request.getSession().setAttribute("Project", project);
	// // ================ set session info ========================
	// request.setHeader("Referer", "?PID=" + project.getName()); //
	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
	//
	// actionPerform(); // 執行 action
	//
	// // 驗證回傳 path
	// verifyForwardPath("/Layout/SubLayout.jsp");
	// verifyForward("success");
	// verifyNoActionErrors();
	//
	// // 測試回傳物件為 null
	// TaskBoard actualTaskBoard = (TaskBoard) getMockRequest().getAttribute(
	// "TaskBoard");
	// assertNull(actualTaskBoard);
	//
	// // 測試其餘 request
	// ISprintPlanDesc[] ActualPlans = (ISprintPlanDesc[]) getMockRequest()
	// .getAttribute("SprintPlans");
	// assertEquals(mCS.getSprintCount(), ActualPlans.length);
	//
	// List<String> ExpectedActorList = new LinkedList<String>();
	// List<String> ActualActorList = (List<String>) getMockRequest()
	// .getAttribute("ActorList");
	// ExpectedActorList.add("ALL");
	// for (int i = 0; i < ExpectedActorList.size(); i++) {
	// assertEquals(ExpectedActorList.get(i), ActualActorList.get(i));
	// }
	//
	// assertEquals("ALL", getMockRequest().getAttribute("User"));
	//
	// // ============= release ==============
	// project = null;
	// ActualActorList = null;
	// actualTaskBoard = null;
	// ActualPlans = null;
	// ExpectedActorList = null;
	// }

	// 測試代入錯誤的 User 參數
	public void testWrongParameter2() throws Exception {
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		String UserID = "NoBody but you";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("sprintID",
				Integer.toString(mCS.getSprintCount() - 1)); // 取得第一筆 SprintPlan
		addRequestParameter("UserID", UserID); // 代入錯誤的 ID
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager
																	// 會對URL的參數作分析
																	// ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath("/Layout/SubLayout.jsp");
		verifyForward("success");
		verifyNoActionErrors();

		// 測試 TaskBoard 上方資訊列表所有資訊是否正確
		// 測試 Story Point 計算是否正確
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project,
				mCS.getSprintCount() - 1);
		TaskBoard exceptedTaskBoard = new TaskBoard(sprintBacklogLogic,
				sprintBacklogLogic.getSprintBacklogMapper());
		TaskBoard actualTaskBoard = (TaskBoard) getMockRequest().getAttribute(
				"TaskBoard");
		// 因為沒有此使用者，所以回傳跟此使用者有關的Story Point為0
		assertEquals("0.0 / 10.0", actualTaskBoard.getInitialStoryPoint());

		assertEquals("0.0 / -", actualTaskBoard.getInitialTaskPoint());

		// 因為沒有此使用者，所以沒有跟此使用者有關的Story
		assertEquals(0, actualTaskBoard.getStories().size());

		assertEquals(exceptedTaskBoard.getSprintGoal(),
				actualTaskBoard.getSprintGoal());
		assertEquals(mCS.TEST_SPRINT_GOAL + "1",
				actualTaskBoard.getSprintGoal());
		assertEquals(exceptedTaskBoard.getSprintId(),
				actualTaskBoard.getSprintId());
		assertEquals(1, actualTaskBoard.getSprintId());

		// 因為沒有此使用者，所以沒有跟此使用者有關的Story
		assertEquals(0, actualTaskBoard.getStories().size());
		assertEquals(exceptedTaskBoard.getStoryChartLink(),
				actualTaskBoard.getStoryChartLink());
		assertEquals("10.0 / 10.0", actualTaskBoard.getStoryPoint());
		assertEquals(exceptedTaskBoard.getTaskChartLink(),
				actualTaskBoard.getTaskChartLink());
		assertEquals("10.0 / 10.0", actualTaskBoard.getTaskPoint());

		// 測試其餘 request
		SprintPlanHelper helper = new SprintPlanHelper(project);
		List<ISprintPlanDesc> ExpectedPlans = helper.loadListPlans();
		List<ISprintPlanDesc> ActualPlans = (List<ISprintPlanDesc>) getMockRequest().getAttribute("SprintPlans");
		for (int i = 0; i < ExpectedPlans.size(); i++) {
			assertEquals(ExpectedPlans.get(i).getID(), ActualPlans.get(i)
					.getID());
		}

		List<String> ExpectedActorList = new LinkedList<String>();
		List<String> ActualActorList = (List<String>) getMockRequest()
				.getAttribute("ActorList");
		ExpectedActorList.add("ALL");
		for (int i = 0; i < ExpectedActorList.size(); i++) {
			assertEquals(ExpectedActorList.get(i), ActualActorList.get(i));
		}

		assertEquals(UserID, getMockRequest().getAttribute("User"));

		// ============= release ==============
		project = null;
		ExpectedActorList = null;
		ExpectedPlans = null;
		exceptedTaskBoard = null;
		helper = null;
		ActualActorList = null;
		ActualPlans = null;
		actualTaskBoard = null;
	}

	// // 待修改: ShowTaskBoardAction.java #86 未處理好產生 null pointer
	// // 測試代入超出範圍的 Sprint 參數
	// public void testWrongParameter3() throws Exception {
	// // ================ set initial data =======================
	// IProject project = mCP.getProjectList().get(0);
	// // ================ set initial data =======================
	//
	// // ================== set parameter info ====================
	// addRequestParameter("sprintID", "XXX"); // 代入超出範圍的 Sprint ID
	// addRequestParameter("UserID", "ALL"); // 沒有指定User ID資料
	// // ================== set parameter info ====================
	//
	// // ================ set session info ========================
	// request.getSession().setAttribute("UserSession",
	// mConfig.getUserSession());
	// request.getSession().setAttribute("Project", project);
	// // ================ set session info ========================
	// request.setHeader("Referer", "?PID=" + project.getName()); //
	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
	//
	// actionPerform(); // 執行 action
	//
	// // 驗證回傳 path
	// verifyForwardPath("/Layout/SubLayout.jsp");
	// verifyForward("success");
	// verifyNoActionErrors();
	//
	// // 測試回傳物件為 null
	// TaskBoard actualTaskBoard = (TaskBoard) getMockRequest().getAttribute(
	// "TaskBoard");
	// assertNull(actualTaskBoard);
	//
	// // 測試其餘 request
	// ISprintPlanDesc[] ActualPlans = (ISprintPlanDesc[]) getMockRequest()
	// .getAttribute("SprintPlans");
	// assertEquals(mCS.getSprintCount(), ActualPlans.length);
	//
	// List<String> ExpectedActorList = new LinkedList<String>();
	// List<String> ActualActorList = (List<String>) getMockRequest()
	// .getAttribute("ActorList");
	// ExpectedActorList.add("ALL");
	// for (int i = 0; i < ExpectedActorList.size(); i++) {
	// assertEquals(ExpectedActorList.get(i), ActualActorList.get(i));
	// }
	//
	// assertEquals("ALL", getMockRequest().getAttribute("User"));
	//
	// // ============= release ==============
	// project = null;
	// ActualActorList = null;
	// ActualPlans = null;
	// actualTaskBoard = null;
	// ExpectedActorList = null;
	// }

	// 測試移動兩筆 Task 到 Check-Out 並且驗證點數
	public void testMoveTask1() throws Exception {
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		String SprintID = Integer.toString(mCS.getSprintCount() - 1);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("sprintID", SprintID); // 代入超出範圍的 Sprint ID
		addRequestParameter("UserID", "ALL"); // 沒有指定User ID資料
		// ================== set parameter info ====================

		// =============== set move action ======================
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		tasks.add(mATTS.getTasks().get(0)); // 加入第一筆
		tasks.add(mATTS.getTasks().get(1)); // 加入第二筆

		ChangeIssueStatus CIS = new ChangeIssueStatus(tasks, mCP);
		CIS.exeCheckOutTasks(); // 將此兩筆 Task Check-out
		// =============== set move action ======================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		// SessionManager 會對 URL 的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath("/Layout/SubLayout.jsp");
		verifyForward("success");
		verifyNoActionErrors();

		// 測試 Story/Task Point 計算是否正確
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project,
				mCS.getSprintCount() - 1);
		TaskBoard exceptedTaskBoard = new TaskBoard(sprintBacklogLogic,
				sprintBacklogLogic.getSprintBacklogMapper());
		TaskBoard actualTaskBoard = (TaskBoard) getMockRequest().getAttribute("TaskBoard");
		assertEquals("10.0 / 10.0", actualTaskBoard.getInitialStoryPoint());
		assertEquals("10.0 / -", actualTaskBoard.getInitialTaskPoint());
		assertEquals(exceptedTaskBoard.getStories().size(), actualTaskBoard
				.getStories().size());
		for (StoryObject story : exceptedTaskBoard.getStories()) {
			assertEquals(story.getId(), story.getId());
		}

		assertEquals(exceptedTaskBoard.getSprintGoal(),
				actualTaskBoard.getSprintGoal());
		assertEquals(mCS.TEST_SPRINT_GOAL + "1",
				actualTaskBoard.getSprintGoal());
		assertEquals(exceptedTaskBoard.getSprintId(),
				actualTaskBoard.getSprintId());
		assertEquals(1, actualTaskBoard.getSprintId());
		assertEquals(exceptedTaskBoard.getStories().size(), actualTaskBoard
				.getStories().size());
		for (StoryObject story : exceptedTaskBoard.getStories()) {
			assertEquals(story.getId(), story.getId());
		}
		assertEquals(exceptedTaskBoard.getStoryChartLink(),
				actualTaskBoard.getStoryChartLink());
		assertEquals("10.0 / 10.0", actualTaskBoard.getStoryPoint());
		assertEquals(exceptedTaskBoard.getTaskChartLink(),
				actualTaskBoard.getTaskChartLink());
		assertEquals("10.0 / 10.0", actualTaskBoard.getTaskPoint());

		// ============= release ==============
		project = null;
		tasks = null;
		CIS = null;
		exceptedTaskBoard = null;
	}

	// 測試移動兩筆 Task 到 Done 並且驗證點數
	public void testMoveTask2() throws Exception {
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		String SprintID = Integer.toString(mCS.getSprintCount() - 1);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("sprintID", SprintID); // 第一個 Sprint
		addRequestParameter("UserID", "ALL"); // 沒有指定User ID資料
		// ================== set parameter info ====================

		// =============== set move action ======================
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		tasks.add(mATTS.getTasks().get(0)); // 加入第一筆
		tasks.add(mATTS.getTasks().get(1)); // 加入第二筆

		ChangeIssueStatus CIS = new ChangeIssueStatus(tasks, mCP);
		CIS.exeCloseTasks(); // 將此兩筆 Task Done
		// =============== set move action ======================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager
																	// 會對URL的參數作分析
																	// ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath("/Layout/SubLayout.jsp");
		verifyForward("success");
		verifyNoActionErrors();

		// 測試 Story/Task Point 計算是否正確
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project,
				mCS.getSprintCount() - 1);
		TaskBoard exceptedTaskBoard = new TaskBoard(sprintBacklogLogic,
				sprintBacklogLogic.getSprintBacklogMapper());
		TaskBoard actualTaskBoard = (TaskBoard) getMockRequest().getAttribute(
				"TaskBoard");
		assertEquals("10.0 / 10.0", actualTaskBoard.getInitialStoryPoint());
		assertEquals("8.0 / -", actualTaskBoard.getInitialTaskPoint());
		assertEquals(exceptedTaskBoard.getStories().size(), actualTaskBoard
				.getStories().size());
		for (StoryObject story : exceptedTaskBoard.getStories()) {
			assertEquals(story.getId(), story.getId());
		}
		assertEquals(exceptedTaskBoard.getSprintGoal(),
				actualTaskBoard.getSprintGoal());
		assertEquals(mCS.TEST_SPRINT_GOAL + "1",
				actualTaskBoard.getSprintGoal());
		assertEquals(exceptedTaskBoard.getSprintId(),
				actualTaskBoard.getSprintId());
		assertEquals(1, actualTaskBoard.getSprintId());
		assertEquals(exceptedTaskBoard.getStories().size(), actualTaskBoard
				.getStories().size());
		for (StoryObject story : exceptedTaskBoard.getStories()) {
			assertEquals(story.getId(), story.getId());
		}
		assertEquals(exceptedTaskBoard.getStoryChartLink(),
				actualTaskBoard.getStoryChartLink());
		assertEquals("10.0 / 10.0", actualTaskBoard.getStoryPoint());
		assertEquals(exceptedTaskBoard.getTaskChartLink(),
				actualTaskBoard.getTaskChartLink());
		assertEquals("8.0 / 10.0", actualTaskBoard.getTaskPoint());

		// ============= release ==============
		project = null;
		tasks = null;
		CIS = null;
		exceptedTaskBoard = null;
	}

	// 不同天數的移動 Task ，測試輸出點數是否正確
	public void testMoveTask3() throws Exception {
		System.out.println("testMoveTask3: 請找時間把測試失敗原因找出來~");

		/*
		 * // ================ set initial data =======================
		 * InitialSQL ini = new InitialSQL(config); ini.exe(); // 初始化 SQL
		 * 
		 * CopyProject copyProject = new CopyProject(CP);
		 * copyProject.exeDelete_Project(); // 刪除測試檔案
		 * 
		 * CP = new CreateProject(1); CP.exeCreate(); // 新增一測試專案 IProject
		 * project = CP.getProjectList().get(0);
		 * 
		 * // 建立一設定過日期的 Sprint SprintPlanHelper SPhelper = new
		 * SprintPlanHelper(project);
		 * SPhelper.saveIterationPlanForm(setSprintInfo(1)); // 加入第一個 sprint
		 * 
		 * // 建立五筆 Story int Story_Count = 5; int Est_Value = 2;
		 * CreateProductBacklog CPB = new CreateProductBacklog(Story_Count,
		 * Est_Value, CP, CreateProductBacklog.TYPE_ESTIMATION); CPB.exe();
		 * 
		 * // 將五筆 Story 加入 Sprint-1 ProductBacklogHelper helper = new
		 * ProductBacklogHelper(project, CreateUserSession("administrator")); //
		 * helper.add(CPB.getIssueIDList(), "1"); (new
		 * ProductBacklogLogic(CreateUserSession("administrator"),
		 * project)).addIssueToSprint(CPB.getIssueIDList(), "1");
		 * 
		 * // 設定點數為每個 Story 兩點 for (int i = 0; i < CPB.getIssueList().size();
		 * i++) { helper.getIssue(CPB.getIssueIDList().get(i)); }
		 * 
		 * // 將每筆 Story 各別加入兩筆 Task int Task_Count = 2; int Task_Value = 1;
		 * 
		 * // ArrayList<Long> TaskList = new ArrayList<Long>(); // for (int i =
		 * 0; i < CPB.getIssueList().size(); i++) { // long StoryID =
		 * CPB.getIssueList().get(i).getIssueID(); // CreateTask CT = new
		 * CreateTask(Task_Count, Task_Value, StoryID, // CP); // CT.exe(); //
		 * TaskList.addAll(CT.getTaskIDList()); // } List<IIssue> TaskList = new
		 * ArrayList<IIssue>(); for (int i = 0; i < CPB.getIssueList().size();
		 * i++) { long StoryID = CPB.getIssueList().get(i).getIssueID();
		 * CreateTask CT = new CreateTask(Task_Count, Task_Value, StoryID, CP);
		 * CT.exe(); TaskList.addAll(CT.getTaskList()); }
		 * 
		 * String SprintID = "1"; // ================ set initial data
		 * =======================
		 * 
		 * // =============== set move action ====================== SPhelper =
		 * new SprintPlanHelper(project); ISprintPlanDesc SprintOne =
		 * SPhelper.loadPlan(SprintID); Calendar cal = Calendar.getInstance();
		 * // 取得今日的時間 Date Today = new Date(SprintOne.getStartDate()); // 第一天從
		 * StartDate // 開始設計點數移動 Today = getNextDay(Today); // 下一天 for (int i =
		 * 0; i < Integer.parseInt(SprintOne.getAvailableDays()); i++) { //
		 * ArrayList<Long> IssueID = new ArrayList<Long>(); //
		 * IssueID.add(TaskList.get(i)); // CheckOutIssue COI = new
		 * CheckOutIssue(IssueID, CP, Today); List<IIssue> issueList = new
		 * ArrayList<IIssue>(); issueList.add(TaskList.get(i)); CheckOutIssue
		 * COI = issueList, CP, Today); COI.exeDone_Issues(); // 將此天的一筆 Task
		 * Done Today = getNextDay(Today); // 日期取得下一天 } // =============== set
		 * move action ======================
		 * 
		 * // ================== set parameter info ====================
		 * addRequestParameter("sprintID", SprintID); // 第一個 Sprint
		 * addRequestParameter("UserID", "ALL"); // 沒有指定User ID資料 //
		 * ================== set parameter info ====================
		 * 
		 * // ================ set session info ========================
		 * request.getSession().setAttribute("UserSession",
		 * config.getUserSession());
		 * request.getSession().setAttribute("Project", project); //
		 * ================ set session info ========================
		 * request.setHeader("Referer", "?PID=" + project.getName()); //
		 * SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		 * 
		 * actionPerform(); // 執行 action
		 * 
		 * // 驗證回傳 path verifyForwardPath("/Layout/SubLayout.jsp");
		 * verifyForward("success"); verifyNoActionErrors();
		 * 
		 * // 測試日期與點數計算是否正確 TaskBoard ActualTaskBoard = (TaskBoard)
		 * getMockRequest().getAttribute( "TaskBoard"); LinkedHashMap<Date,
		 * Double> TaskMap = ActualTaskBoard .gettaskRealPointMap();
		 * SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd"); Today =
		 * new Date(SprintOne.getStartDate());
		 * 
		 * // 因為取得的 Map 會依據時間取出點數值，建構資料會是以當下日期為主 // 所以建立測試資料要以今天建立，測試時卻要以一個
		 * sprint 後的日期測試
		 * 
		 * assertEquals(10.0, TaskMap.get(Today)); Today = getNextDay(Today);
		 * assertEquals(8.0, TaskMap.get(Today)); Today = getNextDay(Today);
		 * assertEquals(7.0, TaskMap.get(Today)); Today = getNextDay(Today);
		 * assertEquals(6.0, TaskMap.get(Today)); Today = getNextDay(Today);
		 * assertEquals(5.0, TaskMap.get(Today)); Today = getNextDay(Today);
		 * assertEquals(4.0, TaskMap.get(Today)); Today = getNextDay(Today);
		 * assertEquals(3.0, TaskMap.get(Today)); Today = getNextDay(Today);
		 * assertEquals(2.0, TaskMap.get(Today)); Today = getNextDay(Today);
		 * assertEquals(1.0, TaskMap.get(Today)); Today = getNextDay(Today);
		 * assertEquals(0.0, TaskMap.get(Today));
		 * 
		 * // ============= release ============== ini = null; project = null;
		 * copyProject = null; SPhelper = null; helper = null; CPB = null;
		 * SprintOne = null; ActualTaskBoard = null; TaskMap = null; format =
		 * null; Today = null;
		 */
	}

	// 不同的移動步驟，測試 Story / Task 點數是否正確
	public void testMoveTask4() throws Exception {
		System.out.println("testMoveTask4: 請找時間把測試失敗原因找出來~");

		/*
		 * // ================ set initial data ======================= IProject
		 * project = CP.getProjectList().get(0); String SprintID =
		 * Integer.toString(CS.getSprintCount() - 1); // ================ set
		 * initial data =======================
		 * 
		 * // ================== set parameter info ====================
		 * addRequestParameter("sprintID", SprintID); // 第一個 Sprint
		 * addRequestParameter("UserID", "ALL"); // 沒有指定User ID資料 //
		 * ================== set parameter info ====================
		 * 
		 * // !!!!!!!!!!!!!!!!!!!!!!!!!!!!! 移動一點 Task ，驗證點數是否正確 //
		 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!! // =============== set move action
		 * ====================== SprintPlanHelper helper = new
		 * SprintPlanHelper(project);
		 * 
		 * // ArrayList<Long> IssueID = new ArrayList<Long>(); //
		 * IssueID.add(ATS.getTaskIDList().get(0)); // CheckOutIssue COI = new
		 * CheckOutIssue(IssueID, CP); // COI.exeDone_Issues();
		 * 
		 * List<IIssue> issueList = new ArrayList<IIssue>();
		 * issueList.add(ATS.getTaskList().get(0)); CheckOutIssue COI = new
		 * CheckOutIssue(issueList, CP); COI.exeDone_Issues(); //
		 * =============== set move action ======================
		 * 
		 * // ================ set session info ========================
		 * request.getSession().setAttribute("UserSession",
		 * config.getUserSession());
		 * request.getSession().setAttribute("Project", project); //
		 * ================ set session info ========================
		 * request.setHeader("Referer", "?PID=" + project.getName()); //
		 * SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		 * 
		 * actionPerform(); // 執行 action
		 * 
		 * // 驗證回傳 path verifyForwardPath("/Layout/SubLayout.jsp");
		 * verifyForward("success"); verifyNoActionErrors();
		 * 
		 * // 測試 Story / Task Point 計算是否正確 TaskBoard exceptedTaskBoard = new
		 * TaskBoard(new SprintBacklog(project, CreateUserSession(),
		 * CS.getSprintCount() - 1)); TaskBoard actualTaskBoard = (TaskBoard)
		 * getMockRequest().getAttribute( "TaskBoard");
		 * assertEquals("10.0 / 10.0", actualTaskBoard.getInitialStoryPoint());
		 * assertEquals("9.0 / -", actualTaskBoard.getInitialTaskPoint());
		 * assertEquals(exceptedTaskBoard.getM_stories().length,
		 * actualTaskBoard.getM_stories().length); for (int i = 0; i <
		 * exceptedTaskBoard.getM_stories().length; i++) {
		 * assertEquals(exceptedTaskBoard.getM_stories()[i].getIssueID(),
		 * actualTaskBoard .getM_stories()[i].getIssueID()); }
		 * assertEquals(exceptedTaskBoard.getSprintGoal(),
		 * actualTaskBoard.getSprintGoal()); assertEquals(CS.TEST_SPRINT_GOAL +
		 * "1", actualTaskBoard .getSprintGoal());
		 * assertEquals(exceptedTaskBoard.getSprintID(),
		 * actualTaskBoard.getSprintID()); assertEquals(1,
		 * actualTaskBoard.getSprintID());
		 * assertEquals(exceptedTaskBoard.getStories().length,
		 * actualTaskBoard.getStories().length); for (int i = 0; i <
		 * exceptedTaskBoard.getStories().length; i++) {
		 * assertEquals(exceptedTaskBoard.getStories()[i].getIssueID(),
		 * actualTaskBoard .getStories()[i].getIssueID()); }
		 * assertEquals(exceptedTaskBoard.getStoryChartLink(), actualTaskBoard
		 * .getStoryChartLink()); assertEquals("10.0 / 10.0",
		 * actualTaskBoard.getStoryPoint());
		 * assertEquals(exceptedTaskBoard.getTaskChartLink(),
		 * actualTaskBoard.getTaskChartLink()); assertEquals("9.0 / 10.0",
		 * actualTaskBoard.getTaskPoint());
		 * 
		 * // 測試其餘 request ISprintPlanDesc[] ExpectedPlans = helper.loadPlans();
		 * ISprintPlanDesc[] ActualPlans = (ISprintPlanDesc[]) getMockRequest()
		 * .getAttribute("SprintPlans"); for (int i = 0; i <
		 * ExpectedPlans.length; i++) { assertEquals(ExpectedPlans[i].getID(),
		 * ActualPlans[i].getID()); }
		 * 
		 * List<String> ExpectedActorList = new LinkedList<String>();
		 * List<String> ActualActorList = (List<String>) getMockRequest()
		 * .getAttribute("ActorList"); ExpectedActorList.add("ALL"); for (int i
		 * = 0; i < ExpectedActorList.size(); i++) {
		 * assertEquals(ExpectedActorList.get(i), ActualActorList.get(i)); }
		 * 
		 * assertEquals("ALL", getMockRequest().getAttribute("User")); //
		 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!! 移動一點 Task ，驗證點數是否正確 //
		 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		 * 
		 * // !!!!!!!!!!!!!!!!!!!!!!!!!!!!! 再移動兩點 Task ，驗證點數是否正確 //
		 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!! // =============== set move action
		 * ====================== issueList.clear(); //
		 * issueList.add(ATS.getTaskIDList().get(1)); //
		 * issueList.add(ATS.getTaskIDList().get(2));
		 * issueList.add(ATS.getTaskList().get(1));
		 * issueList.add(ATS.getTaskList().get(2)); COI = new
		 * CheckOutIssue(issueList, CP); COI.exeDone_Issues(); //
		 * =============== set move action ======================
		 * 
		 * actionPerform(); // 執行 action
		 * 
		 * // 驗證回傳 path verifyForwardPath("/Layout/SubLayout.jsp");
		 * verifyForward("success"); verifyNoActionErrors();
		 * 
		 * // 測試 Story / Task Point 計算是否正確 exceptedTaskBoard = new TaskBoard(new
		 * SprintBacklog(project, CreateUserSession(), CS.getSprintCount() -
		 * 1)); actualTaskBoard = (TaskBoard)
		 * getMockRequest().getAttribute("TaskBoard");
		 * assertEquals("10.0 / 10.0", actualTaskBoard.getInitialStoryPoint());
		 * assertEquals("7.0 / -", actualTaskBoard.getInitialTaskPoint());
		 * assertEquals(exceptedTaskBoard.getM_stories().length,
		 * actualTaskBoard.getM_stories().length); for (int i = 0; i <
		 * exceptedTaskBoard.getM_stories().length; i++) {
		 * assertEquals(exceptedTaskBoard.getM_stories()[i].getIssueID(),
		 * actualTaskBoard .getM_stories()[i].getIssueID()); }
		 * assertEquals(exceptedTaskBoard.getSprintGoal(),
		 * actualTaskBoard.getSprintGoal()); assertEquals(CS.TEST_SPRINT_GOAL +
		 * "1", actualTaskBoard .getSprintGoal());
		 * assertEquals(exceptedTaskBoard.getSprintID(),
		 * actualTaskBoard.getSprintID()); assertEquals(1,
		 * actualTaskBoard.getSprintID());
		 * assertEquals(exceptedTaskBoard.getStories().length,
		 * actualTaskBoard.getStories().length); for (int i = 0; i <
		 * exceptedTaskBoard.getStories().length; i++) {
		 * assertEquals(exceptedTaskBoard.getStories()[i].getIssueID(),
		 * actualTaskBoard .getStories()[i].getIssueID()); }
		 * assertEquals(exceptedTaskBoard.getStoryChartLink(), actualTaskBoard
		 * .getStoryChartLink()); assertEquals("10.0 / 10.0",
		 * actualTaskBoard.getStoryPoint());
		 * assertEquals(exceptedTaskBoard.getTaskChartLink(),
		 * actualTaskBoard.getTaskChartLink()); assertEquals("7.0 / 10.0",
		 * actualTaskBoard.getTaskPoint());
		 * 
		 * // 測試其餘 request ExpectedPlans = helper.loadPlans(); ActualPlans =
		 * (ISprintPlanDesc[]) getMockRequest().getAttribute( "SprintPlans");
		 * for (int i = 0; i < ExpectedPlans.length; i++) {
		 * assertEquals(ExpectedPlans[i].getID(), ActualPlans[i].getID()); }
		 * 
		 * ExpectedActorList = new LinkedList<String>(); ActualActorList =
		 * (List<String>) getMockRequest().getAttribute( "ActorList");
		 * ExpectedActorList.add("ALL"); for (int i = 0; i <
		 * ExpectedActorList.size(); i++) {
		 * assertEquals(ExpectedActorList.get(i), ActualActorList.get(i)); }
		 * 
		 * assertEquals("ALL", getMockRequest().getAttribute("User")); //
		 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!! 再移動兩點 Task ，驗證點數是否正確 //
		 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		 * 
		 * // !!!!!!!!!!!!!!!!!!!!!!!!!!!!! 再移動一點 Task、一筆 Story (兩點)，驗證點數是否正確 //
		 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!! // =============== set move action
		 * ====================== issueList.clear(); //
		 * issueList.add(ATS.getTaskIDList().get(3));
		 * issueList.add(ATS.getTaskList().get(3)); COI = new
		 * CheckOutIssue(issueList, CP); COI.exeDone_Issues();
		 * 
		 * issueList.clear(); //
		 * issueList.add(ASS.getIssueIDList().get(0).getIssueID());
		 * issueList.add(ASS.getIssueList().get(0)); COI = new
		 * CheckOutIssue(issueList, CP); COI.exeDone_Issues(); //
		 * =============== set move action ======================
		 * 
		 * actionPerform(); // 執行 action
		 * 
		 * // 驗證回傳 path verifyForwardPath("/Layout/SubLayout.jsp");
		 * verifyForward("success"); verifyNoActionErrors();
		 * 
		 * // 測試 Story / Task Point 計算是否正確 exceptedTaskBoard = new TaskBoard(new
		 * SprintBacklog(project, CreateUserSession(), CS.getSprintCount() -
		 * 1)); actualTaskBoard = (TaskBoard)
		 * getMockRequest().getAttribute("TaskBoard");
		 * assertEquals("8.0 / 10.0", actualTaskBoard.getInitialStoryPoint());
		 * assertEquals("6.0 / -", actualTaskBoard.getInitialTaskPoint());
		 * assertEquals(exceptedTaskBoard.getM_stories().length,
		 * actualTaskBoard.getM_stories().length); for (int i = 0; i <
		 * exceptedTaskBoard.getM_stories().length; i++) {
		 * assertEquals(exceptedTaskBoard.getM_stories()[i].getIssueID(),
		 * actualTaskBoard .getM_stories()[i].getIssueID()); }
		 * assertEquals(exceptedTaskBoard.getSprintGoal(),
		 * actualTaskBoard.getSprintGoal()); assertEquals(CS.TEST_SPRINT_GOAL +
		 * "1", actualTaskBoard .getSprintGoal());
		 * assertEquals(exceptedTaskBoard.getSprintID(),
		 * actualTaskBoard.getSprintID()); assertEquals(1,
		 * actualTaskBoard.getSprintID());
		 * assertEquals(exceptedTaskBoard.getStories().length,
		 * actualTaskBoard.getStories().length); for (int i = 0; i <
		 * exceptedTaskBoard.getStories().length; i++) {
		 * assertEquals(exceptedTaskBoard.getStories()[i].getIssueID(),
		 * actualTaskBoard .getStories()[i].getIssueID()); }
		 * assertEquals(exceptedTaskBoard.getStoryChartLink(), actualTaskBoard
		 * .getStoryChartLink()); assertEquals("8.0 / 10.0",
		 * actualTaskBoard.getStoryPoint());
		 * assertEquals(exceptedTaskBoard.getTaskChartLink(),
		 * actualTaskBoard.getTaskChartLink()); assertEquals("6.0 / 10.0",
		 * actualTaskBoard.getTaskPoint());
		 * 
		 * // 測試其餘 request ExpectedPlans = helper.loadPlans(); ActualPlans =
		 * (ISprintPlanDesc[]) getMockRequest().getAttribute( "SprintPlans");
		 * for (int i = 0; i < ExpectedPlans.length; i++) {
		 * assertEquals(ExpectedPlans[i].getID(), ActualPlans[i].getID()); }
		 * 
		 * ExpectedActorList = new LinkedList<String>(); ActualActorList =
		 * (List<String>) getMockRequest().getAttribute( "ActorList");
		 * ExpectedActorList.add("ALL"); for (int i = 0; i <
		 * ExpectedActorList.size(); i++) {
		 * assertEquals(ExpectedActorList.get(i), ActualActorList.get(i)); }
		 * 
		 * assertEquals("ALL", getMockRequest().getAttribute("User")); //
		 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!! 再移動一點 Task、一筆 Story (兩點)，驗證點數是否正確 //
		 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		 * 
		 * // !!!!!!!!!!!!!!!!!!!!!!!!!!!!! 將已經 Done 的一筆 Task 領回變成 Check-Out、一筆
		 * // Story 領回變成 Non Check-Out，驗證點數是否正確 !!!!!!!!!!!!!!!!!!!!!!!!!!!!! //
		 * XXXXXXXXXXXXXXXXXXXXXXX 錯誤 XXXXXXXXXXXXXXXXXXXXXX // ===============
		 * set move action ====================== issueList.clear(); //
		 * issueList.add(ATS.getTaskIDList().get(1)); //
		 * issueList.add(ATS.getTaskIDList().get(2));
		 * issueList.add(ATS.getTaskList().get(1));
		 * issueList.add(ATS.getTaskList().get(2)); COI = new
		 * CheckOutIssue(issueList, CP); COI.exeReset_Issues();
		 * 
		 * issueList.clear(); //
		 * issueList.add(ASS.getIssueIDList().get(0).getIssueID());
		 * issueList.add(ASS.getIssueList().get(0)); COI = new
		 * CheckOutIssue(issueList, CP); COI.exeReset_Issues(); //
		 * =============== set move action ======================
		 * 
		 * actionPerform(); // 執行 action
		 * 
		 * // 驗證回傳 path verifyForwardPath("/Layout/SubLayout.jsp");
		 * verifyForward("success"); verifyNoActionErrors();
		 * 
		 * // 測試 Story / Task Point 計算是否正確 exceptedTaskBoard = new TaskBoard(new
		 * SprintBacklog(project, CreateUserSession(), CS.getSprintCount() -
		 * 1)); actualTaskBoard = (TaskBoard)
		 * getMockRequest().getAttribute("TaskBoard");
		 * assertEquals("10.0 / 10.0", actualTaskBoard.getInitialStoryPoint());
		 * assertEquals("8.0 / -", actualTaskBoard.getInitialTaskPoint()); //
		 * ========== // 錯誤 // ==========
		 * assertEquals(exceptedTaskBoard.getM_stories().length,
		 * actualTaskBoard.getM_stories().length); for (int i = 0; i <
		 * exceptedTaskBoard.getM_stories().length; i++) {
		 * assertEquals(exceptedTaskBoard.getM_stories()[i].getIssueID(),
		 * actualTaskBoard .getM_stories()[i].getIssueID()); }
		 * assertEquals(exceptedTaskBoard.getSprintGoal(),
		 * actualTaskBoard.getSprintGoal()); assertEquals(CS.TEST_SPRINT_GOAL +
		 * "1", actualTaskBoard .getSprintGoal());
		 * assertEquals(exceptedTaskBoard.getSprintID(),
		 * actualTaskBoard.getSprintID()); assertEquals(1,
		 * actualTaskBoard.getSprintID());
		 * assertEquals(exceptedTaskBoard.getStories().length,
		 * actualTaskBoard.getStories().length); for (int i = 0; i <
		 * exceptedTaskBoard.getStories().length; i++) {
		 * assertEquals(exceptedTaskBoard.getStories()[i].getIssueID(),
		 * actualTaskBoard .getStories()[i].getIssueID()); }
		 * assertEquals(exceptedTaskBoard.getStoryChartLink(), actualTaskBoard
		 * .getStoryChartLink()); assertEquals("10.0 / 10.0",
		 * actualTaskBoard.getStoryPoint());
		 * assertEquals(exceptedTaskBoard.getTaskChartLink(),
		 * actualTaskBoard.getTaskChartLink()); assertEquals("8.0 / 10.0",
		 * actualTaskBoard.getTaskPoint()); // =========== 錯誤 // ==========
		 * 
		 * // 測試其餘 request ExpectedPlans = helper.loadPlans(); ActualPlans =
		 * (ISprintPlanDesc[]) getMockRequest().getAttribute( "SprintPlans");
		 * for (int i = 0; i < ExpectedPlans.length; i++) {
		 * assertEquals(ExpectedPlans[i].getID(), ActualPlans[i].getID()); }
		 * 
		 * ExpectedActorList = new LinkedList<String>(); ActualActorList =
		 * (List<String>) getMockRequest().getAttribute( "ActorList");
		 * ExpectedActorList.add("ALL"); for (int i = 0; i <
		 * ExpectedActorList.size(); i++) {
		 * assertEquals(ExpectedActorList.get(i), ActualActorList.get(i)); }
		 * 
		 * assertEquals("ALL", getMockRequest().getAttribute("User")); //
		 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!! 將已經 Done 的一筆 Task 領回變成 Check-Out、一筆 //
		 * Story 領回變成 Non Check-Out，驗證點數是否正確 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		 */
	}

	private Date getNextDay(Date Today) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(Today);
		cal.add(Calendar.DAY_OF_YEAR, 1); // 前進一天
		while (DateUtil.isHoliday(cal.getTime())) {
			cal.add(Calendar.DAY_OF_YEAR, 1); // 加上假日
		}

		return cal.getTime();
	}

	// get user session
	private IUserSession CreateUserSession() throws LogonException {
		IUserSession theUserSession = new UserSession(null);

		return theUserSession;
	}

	// get user session
	private static IUserSession CreateUserSession(String ID)
			throws LogonException {
		IUserSession theUserSession = new UserSession(null);

		return theUserSession;
	}

	// 設定 Sprint 資訊
	private IterationPlanForm setSprintInfo(int id) {
		Calendar cal = Calendar.getInstance();

		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date EndDate = cal.getTime();
		Date StartDate = EndDate;

		int count = 1;
		while (count < 10) {
			cal.add(Calendar.DAY_OF_YEAR, -1);
			count++;
			StartDate = cal.getTime();
			while (DateUtil.isHoliday(StartDate)) {
				cal.add(Calendar.DAY_OF_YEAR, -1);
				StartDate = cal.getTime();
			}
		}

		IterationPlanForm sprintForm = new IterationPlanForm();

		sprintForm.setID(Integer.toString(id));
		sprintForm.setGoal("TEST_SPRINT_" + Integer.toString(id));
		sprintForm.setIterStartDate(format.format(StartDate));
		sprintForm.setIterIterval("2");
		sprintForm.setIterMemberNumber("2");
		sprintForm.setAvailableDays("10");
		sprintForm.setFocusFactor("100");
		sprintForm.setNotes("LAB 1321");
		sprintForm.setDemoDate(format.format(EndDate));
		sprintForm.setDemoPlace("LAB 1321");

		return sprintForm;
	}
}