package ntut.csie.ezScrum.web.action.report;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.ChangeIssueStatus;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.control.TaskBoard;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
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
		mCP.exeCreateForDb(); // 新增一測試專案

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

		// 設定讀取的 struts-config 檔案路徑
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/showTaskBoard");

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		// clean test project folder in Workspace
		String testProjectPath = "./Workspace/" + mCP.getAllProjects().get(0).getName();
		FileUtils.deleteDirectory(new File(testProjectPath));
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

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
		addRequestParameter("sprintID",
				Integer.toString(mCS.getSprintCount() - 1));
		addRequestParameter("UserID", "ALL");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

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
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project,
				mCS.getSprintsId().get(0));
		TaskBoard exceptedTaskBoard = new TaskBoard(sprintBacklogLogic,
				sprintBacklogLogic.getSprintBacklogMapper());
		TaskBoard actualTaskBoard = (TaskBoard) getMockRequest().getAttribute(
				"TaskBoard");
		assertEquals("10.0 / 96.0", actualTaskBoard.getInitialStoryPoint());
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

		// 測試其餘 request
		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		ArrayList<SprintObject> expectedSprints = sprintPlanHelper.getSprints();
		ArrayList<SprintObject> actualSprints = (ArrayList<SprintObject>) getMockRequest()
				.getAttribute("SprintPlans");
		for (int i = 0; i < expectedSprints.size(); i++) {
			assertEquals(expectedSprints.get(i).getId(), actualSprints.get(i)
					.getId());
		}

		List<String> ExpectedActorList = new LinkedList<String>();
		List<String> ActualActorList = (List<String>) getMockRequest()
				.getAttribute("ActorList");
		ExpectedActorList.add("ALL");
		for (int i = 0; i < ExpectedActorList.size(); i++) {
			assertEquals(ExpectedActorList.get(i), ActualActorList.get(i));
		}

		assertEquals("ALL", getMockRequest().getAttribute("User"));

		// ============= release ==============
		project = null;
		exceptedTaskBoard = null;
		actualTaskBoard = null;
		sprintPlanHelper = null;
		expectedSprints = null;
		actualSprints = null;
	}

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
		assertEquals("0.0 / 96.0", actualTaskBoard.getInitialStoryPoint());

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
		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		ArrayList<SprintObject> expectedSprints = sprintPlanHelper.getSprints();
		ArrayList<SprintObject> actualSprints = (ArrayList<SprintObject>) getMockRequest()
				.getAttribute("SprintPlans");
		for (int i = 0; i < expectedSprints.size(); i++) {
			assertEquals(expectedSprints.get(i).getId(), actualSprints.get(i)
					.getId());
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
		expectedSprints = null;
		exceptedTaskBoard = null;
		sprintPlanHelper = null;
		ActualActorList = null;
		actualSprints = null;
		actualTaskBoard = null;
	}

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
		TaskBoard actualTaskBoard = (TaskBoard) getMockRequest().getAttribute(
				"TaskBoard");
		assertEquals("10.0 / 96.0", actualTaskBoard.getInitialStoryPoint());
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
		assertEquals("10.0 / 96.0", actualTaskBoard.getInitialStoryPoint());
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
}