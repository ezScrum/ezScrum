package ntut.csie.ezScrum.web.action.project;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.TestTool;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.control.TaskBoard;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.form.LogonForm;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;

import org.codehaus.jettison.json.JSONObject;

import servletunit.struts.MockStrutsTestCase;

public class ViewProjectSummaryActionTest extends MockStrutsTestCase {
	private int mProjectCount = 1;
	private int mAccountCount = 1;
	private Configuration mConfig;
	private CreateProject mCP;
	private CreateAccount mCA;

	public ViewProjectSummaryActionTest(String testMethod) {
		super(testMethod);
	}

	/**
	 * 設定讀取的 struts-config 檔案路徑
	 */
	private void setRequestPathInformation(String actionPath) {
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(actionPath);
	}

	/**
	 * clean previous action info
	 */
	private void cleanActionInformation() {
		clearRequestParameters();
		response.reset();
	}

	private IUserSession getUserSession(AccountObject account) {
		IUserSession userSession = new UserSession(account);
		return userSession;
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		// 新增使用者
		mCA = new CreateAccount(mAccountCount);
		mCA.exe();

		super.setUp();
	}

	protected void tearDown() throws Exception {
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(mConfig.getDataPath());

		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// release
		mCP = null;
		mCA = null;
		mConfig = null;
	}

	/**
	 * 1. admin 建立專案
	 * 2. admin 瀏覽專案
	 */
	public void testAdminViewProjectSummary() {
		/**
		 * 1. admin 建立專案
		 */
		// ================ set action info ========================
		String actionPath = "/AjaxCreateProject";
		setRequestPathInformation(actionPath);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ set request info ========================
		String projectName = "test";
		String projectDisplayName = "Project for Test Create Project";
		String comment = "";
		String projectManager = "ezScrum tester";
		String attachFileSize = "";
		addRequestParameter("Name", projectName);
		addRequestParameter("DisplayName", projectDisplayName);
		addRequestParameter("Comment", comment);
		addRequestParameter("ProjectManager", projectManager);
		addRequestParameter("AttachFileSize", attachFileSize);
		addRequestParameter("from", "createProject");

		// 執行 action
		actionPerform();

		// ================ assert ======================
		StringBuilder expectedResponse = new StringBuilder();
		expectedResponse.append("<Root>").append("<CreateProjectResult>")
				.append("<Result>Success</Result>").append("<ID>test</ID>")
				.append("</CreateProjectResult>").append("</Root>");

		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponse.toString(), actualResponseText);

		// assert database information
		ProjectMapper projectMapper = new ProjectMapper();
		ProjectObject project = projectMapper.getProject(projectName);
		assertEquals(projectName, project.getName());
		assertEquals(projectDisplayName, project.getDisplayName());
		assertEquals(2, project.getAttachFileSize());
		assertEquals(comment, project.getComment());
		assertEquals(projectManager, project.getManager());

		/**
		 * 2. admin 瀏覽專案
		 */
		// ================ set action info ========================
		cleanActionInformation();
		actionPath = "/viewProject";
		setRequestPathInformation(actionPath);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ set request info ========================
		addRequestParameter("PID", projectName);

		// 執行 action
		actionPerform();

		// ================ assert ======================
		verifyForward("SummaryView");
		verifyForwardPath("/Pages/ezScrumContent.jsp");

		String expectIsGuest = "false";
		String actualIsGuest = (String) request.getSession().getAttribute(
				"isGuest");
		assertEquals(expectIsGuest, actualIsGuest);

		verifyNoActionMessages();
		verifyNoActionErrors();
	}

	/**
	 * Integration Test Steps
	 * 		1. admin 新增專案 (setup done)
	 * 		2. admin 新增帳號 (setup done)
	 * 		3. admin assign this account to the project
	 * 		4. user login ezScrum
	 * 		5. user view project list
	 * 		6. user select project
	 */
	public void testUserViewProjectSummary() throws Exception {
		// =============== common data ============================
		AccountObject account = mCA.getAccountList().get(0);
		IUserSession userSession = getUserSession(account);
		long accountId = account.getId();
		long projectId = mCP.getAllProjects().get(0).getId();
		String projectName = mCP.getAllProjects().get(0).getName();

		/**
		 * 3. admin assign this account to the project
		 */

		// ================ set action info ========================
		String actionPath = "/addUser";
		setRequestPathInformation(actionPath);

		// ================ set initial data =======================
		String scrumRole = "ScrumTeam";

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(accountId));
		addRequestParameter("resource", String.valueOf(projectId));
		addRequestParameter("operation", scrumRole);

		// ================ set session info with admin ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ set URL parameter ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		request.setHeader("Referer", "?PID=" + projectName);

		// 執行 add user action
		actionPerform();

		// ================ assert ========================
		long expectedAccountId = account.getId();
		String expectedUsername = account.getUsername();
		String expectedPassword = (new TestTool())
				.getMd5(mCA.getAccount_PWD(1));
		String expectedNickName = account.getNickName();
		String expectedEMail = account.getEmail();
		boolean expectedEnable = account.getEnable();
		String expectedScrumRole = (new TestTool()).getRole(projectName,
				scrumRole);

		StringBuilder expectedResponse = new StringBuilder();
		expectedResponse.append("<Accounts>").append("<AccountInfo>")
				.append("<ID>").append(expectedAccountId).append("</ID>")
				.append("<Account>").append(expectedUsername)
				.append("</Account>").append("<Name>").append(expectedNickName)
				.append("</Name>").append("<Mail>").append(expectedEMail)
				.append("</Mail>").append("<Roles>").append(expectedScrumRole)
				.append("</Roles>").append("<Enable>").append(expectedEnable)
				.append("</Enable>").append("</AccountInfo>")
				.append("</Accounts>");
		String actualResponse = response.getWriterBuffer().toString();
		assertEquals(expectedResponse.toString(), actualResponse);

		// assert database information
		AccountObject actualAccount = new AccountMapper().getAccount(accountId);
		assertNotNull(account);
		assertEquals(expectedAccountId, actualAccount.getId());
		assertEquals(expectedUsername, actualAccount.getUsername());
		assertEquals(expectedPassword, actualAccount.getPassword());
		assertEquals(expectedNickName, actualAccount.getNickName());
		assertEquals(expectedEMail, actualAccount.getEmail());
		assertEquals(expectedEnable, actualAccount.getEnable());

		// 測試 Role 是否正確
		HashMap<String, ProjectRole> roleMap = actualAccount.getRoles();
		boolean isExisted = false;
		for (Entry<String, ProjectRole> role : roleMap.entrySet()) {
			if (scrumRole.equals(role.getValue().getScrumRole().getRoleName())) {
				isExisted = true;
				break;
			}
		}
		assertEquals(roleMap.size(), 1); // ScrumTeam
		assertTrue(isExisted); // ScrumTeam

		/**
		 * 4. user login ezScrum
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();

		// ================ set action info ========================
		actionPath = "/logonSubmit";
		setRequestPathInformation(actionPath);

		// ================== set parameter info ====================
		LogonForm logonForm = new LogonForm();
		logonForm.setUserId(account.getUsername());
		logonForm.setPassword(mCA.getAccount_PWD(1));
		setActionForm(logonForm);

		// 執行 login action
		actionPerform();

		// ================ assert ======================
		verifyForward("success");

		/**
		 * 5. view project list
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();
		ProjectObject project = mCP.getAllProjects().get(0);

		// ================ set action info ========================
		userSession = getUserSession(new AccountMapper().getAccount(accountId));
		actionPath = "/viewProjectList";
		setRequestPathInformation(actionPath);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", userSession);

		// 執行 view project list action
		actionPerform();

		// ================ assert ========================
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		long expectedProjectId = project.getId();
		String expectedName = project.getName();
		String expectedDisplayName = project.getDisplayName();
		String expectedComment = project.getComment();
		String expectedManager = project.getManager();
		String expectedCreateDate = dateFormat.format(project.getCreateTime());
		String expectedDemoDate = "No Plan!";

		// assert response text
		expectedResponse.setLength(0); // clear builder
		expectedResponse.append("<Projects>").append("<Project>")
				.append("<ID>").append(expectedName).append("</ID>")
				.append("<Name>").append(expectedDisplayName).append("</Name>")
				.append("<Comment>").append(expectedComment)
				.append("</Comment>").append("<ProjectManager>")
				.append(expectedManager).append("</ProjectManager>")
				.append("<CreateDate>").append(expectedCreateDate)
				.append("</CreateDate>").append("<DemoDate>")
				.append(expectedDemoDate).append("</DemoDate>")
				.append("</Project>").append("</Projects>");

		actualResponse = response.getWriterBuffer().toString();
		assertEquals(expectedResponse.toString(), actualResponse);

		/**
		 * 6.1 user select project - get Project Description
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();

		// ================ set action info ========================
		actionPath = "/GetProjectDescription";
		setRequestPathInfo(actionPath);

		// ================ set URL parameter ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		request.setHeader("Referer", "?PID=" + projectName);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// 執行 get Project Description action
		actionPerform();

		// ================ assert ======================
		dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
		long expectedAttachFileSize = 2;
		expectedCreateDate = dateFormat.format(project.getCreateTime());

		expectedResponse.setLength(0); // clear builder
		expectedResponse.append("{").append("\"ID\":\"")
				.append(expectedProjectId).append("\",")
				.append("\"ProjectName\":\"").append(expectedName)
				.append("\",").append("\"ProjectDisplayName\":\"")
				.append(expectedDisplayName).append("\",")
				.append("\"Commnet\":\"").append(expectedComment).append("\",")
				.append("\"ProjectManager\":\"").append(expectedManager)
				.append("\",").append("\"AttachFileSize\":\"")
				.append(expectedAttachFileSize).append("\",")
				.append("\"ProjectCreateDate\":\"").append(expectedCreateDate)
				.append("\"").append("}");
		actualResponse = response.getWriterBuffer().toString();
		assertEquals(expectedResponse.toString(), actualResponse);

		/**
		 * 6.2 user select project - get Sprint Description
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();

		// ================ set action info ========================
		actionPath = "/GetTaskBoardDescription";
		setRequestPathInfo(actionPath);

		// ================ set URL parameter ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		request.setHeader("Referer", "?PID=" + projectName);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// 執行 Get TaskBoard Description action
		actionPerform();

		// ================ assert ======================
		expectedResponse.setLength(0);
		expectedResponse.append("{\"ID\":\"0\",")
				.append("\"SprintGoal\":\"\",")
				.append("\"Current_Story_Undone_Total_Point\":\"\",")
				.append("\"Current_Task_Undone_Total_Point\":\"\"}");

		actualResponse = response.getWriterBuffer().toString();
		assertEquals(expectedResponse.toString(), actualResponse);

		/**
		 * 6.3 user select project - get Story Burndown Chat
		 */

		// ================ clean previous action info ========================
		cleanActionInformation();

		int sprintCount = 1;
		int storyCount = 2;
		int taskCount = 2;
		int storyEstValue = 8;
		int taskEstValue = 3;
		CreateSprint CS = new CreateSprint(sprintCount, mCP);
		CS.exe();
		AddStoryToSprint ASS = new AddStoryToSprint(storyCount, storyEstValue,
				CS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASS.exe();
		AddTaskToStory ATS = new AddTaskToStory(taskCount, taskEstValue, ASS,
				mCP);
		ATS.exe();
		actionPath = "/getSprintBurndownChartData";
		setRequestPathInfo(actionPath);

		// ================ set URL parameter ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		request.setHeader("Referer", "?PID=" + projectName);

		// ================ set request info ========================
		addRequestParameter("SprintID", "-1"); // -1代表離現在時間最近的 Sprint
		addRequestParameter("Type", "story");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				getUserSession(account));

		// 執行 get Sprint BurndownChart Data action
		actionPerform();

		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();

		// assert response text
		List<String> sprintDateList = (new TestTool()).getSprintDate(project, getUserSession(account));
		// 減一代表 Sprint 開始的第一天是 SprintPlanning 所以第一天不工作，因此總工作天必須減一。
		int workDateCount = sprintDateList.size() - 1;
		List<String> storyIdealLinePoints = (new TestTool()).getStoryIdealLinePoint(workDateCount, 16.0);

		expectedResponse.setLength(0);
		expectedResponse.append("{\"success\":true,").append("\"Points\":[");
		for (int i = 0; i <= workDateCount; i++) {
			expectedResponse.append("{")
					.append("\"Date\":\"" + sprintDateList.get(i) + "\",")
					.append("\"IdealPoint\":")
					.append(storyIdealLinePoints.get(i)).append(",")
					.append("\"RealPoint\":");
			if (i == 0) {
				expectedResponse.append("16.0},");
			} else {
				expectedResponse.append("\"null\"},");
			}
		}
		expectedResponse.deleteCharAt(expectedResponse.length() - 1);
		expectedResponse.append("]}");

		actualResponse = response.getWriterBuffer().toString();
		
		/**
		 * 6.4 user select project - get Task Burndown Chat
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();
		actionPath = "/getSprintBurndownChartData";
		setRequestPathInfo(actionPath);

		// ================ set URL parameter ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		request.setHeader("Referer", "?PID=" + projectName);

		// ================ set request info ========================
		addRequestParameter("SprintID", "-1"); // -1:代表離現在時間最近的 Sprint
		addRequestParameter("Type", "task");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				getUserSession(account));

		// 執行 get Sprint BurndownChart Data action
		actionPerform();

		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();

		// assert response text
		sprintDateList = (new TestTool()).getSprintDate(project, getUserSession(account));
		// 減一代表 Sprint 開始的第一天是 SprintPlanning 所以第一天不工作，因此總工作天必須減一。
		workDateCount = sprintDateList.size() - 1;
		List<String> taskIdealLinePoints = (new TestTool())
				.getTaskIdealLinePoint(workDateCount, 12.0);

		expectedResponse.setLength(0);
		expectedResponse.append("{\"success\":true,").append("\"Points\":[");
		for (int i = 0; i <= workDateCount; i++) {
			expectedResponse.append("{")
					.append("\"Date\":\"" + sprintDateList.get(i) + "\",")
					.append("\"IdealPoint\":")
					.append(taskIdealLinePoints.get(i)).append(",")
					.append("\"RealPoint\":");
			if (i == 0) {
				expectedResponse.append("12.0},");
			} else {
				expectedResponse.append("\"null\"},");
			}
		}
		expectedResponse.deleteCharAt(expectedResponse.length() - 1);
		expectedResponse.append("]}");

		JSONObject actualResponseJson = new JSONObject(response.getWriterBuffer().toString());
		JSONObject expectResponseJson = new JSONObject(expectedResponse.toString());
		assertEquals(expectResponseJson.getBoolean("success"), actualResponseJson.getBoolean("success"));
		assertEquals(expectResponseJson.getJSONArray("Points").toString(), actualResponseJson.getJSONArray("Points").toString());

		/**
		 * 6.5 user select project
		 */
		// ================ clean previous action info ==============
		cleanActionInformation();

		// ================ set action info ========================
		actionPath = "/viewProject";
		setRequestPathInformation(actionPath);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", userSession);

		// ================== set parameter info ====================
		addRequestParameter("PID", projectName);

		// 執行 view project action
		actionPerform();

		// ================ assert ======================
		verifyForward("SummaryView"); // define in ViewProjectSummaryAction.java
		verifyForwardPath("/Pages/ezScrumContent.jsp"); // define in
														// tiles-defs.xml

		String expectIsGuest = "false";
		String actualIsGuest = (String) request.getSession().getAttribute(
				"isGuest");
		assertEquals(expectIsGuest, actualIsGuest);

		int expectedSprintId = 1;
		String expectedSprintGoal = "TEST_SPRINTGOAL_1";
		String expectedSprint_Current_Story_Undone_Total_Point = "16.0 / 16.0";
		String expectedSprint_Current_Task_Undone_Total_Point = "12.0 / 12.0";
		TaskBoard taskBoard = (TaskBoard) request.getAttribute("TaskBoard");
		assertEquals(expectedSprintId, taskBoard.getSprintId());
		assertEquals(expectedSprintGoal, taskBoard.getSprintGoal());
		assertEquals(expectedSprint_Current_Story_Undone_Total_Point,
				taskBoard.getStoryPoint());
		assertEquals(expectedSprint_Current_Task_Undone_Total_Point,
				taskBoard.getTaskPoint());
		verifyNoActionMessages();
		verifyNoActionErrors();
	}

	/**
	 * 比對資料庫中是否存在此專案的 project name
	 * 		1. assert 不存在
	 * 		2. assert 存在
	 */
	public void testProjectNameIsExisted() {
		String actionPath = "/viewProject";

		/**
		 * 1. project name does not existed
		 */
		String notExistedProjectName = "testNotExisted";
		setRequestPathInformation(actionPath);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ set request info ========================
		addRequestParameter("PID", notExistedProjectName);

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		verifyForward("error");
		verifyForwardPath("/Error.jsp");
		verifyNoActionMessages();
		verifyNoActionErrors();

		/**
		 * 2. project name existed
		 */
		// ================ clean previous action info ==============
		cleanActionInformation();
		String existedProjectName = "TEST_PROJECT_1";
		setRequestPathInformation(actionPath);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ set request info ========================
		addRequestParameter("PID", existedProjectName);

		// 執行 view Project action
		actionPerform();

		// ================ assert ======================
		verifyForward("SummaryView");
		verifyForwardPath("/Pages/ezScrumContent.jsp");

		String expectIsGuest = "false";
		String actualIsGuest = (String) request.getSession().getAttribute(
				"isGuest");
		assertEquals(expectIsGuest, actualIsGuest);
		verifyNoActionMessages();
		verifyNoActionErrors();
	}

	/**
	 * 判斷該使用者是否存在於專案中 1. assert user(ScrumTeam) 不存在於專案 2. assert user(ScrumTeam)
	 * 存在於專案
	 */
	public void testUserIsInProject() {
		String actionPath = "/viewProject";
		String projectName = "TEST_PROJECT_1";

		/**
		 * Permission Denied
		 */
		setRequestPathInformation(actionPath);

		// ================ set session info ========================
		IUserSession userSession = getUserSession(mCA.getAccountList().get(0));
		request.getSession().setAttribute("UserSession", userSession);

		// ================ set request info ========================
		addRequestParameter("PID", projectName);

		// 執行 view Project action
		actionPerform();

		// ================ assert ======================
		verifyForward("permissionDenied");
		verifyForwardPath("/PermissionDenied.jsp");
		verifyNoActionMessages();
		verifyNoActionErrors();

		/**
		 * user(ScrumTeam) 存在於專案
		 */
		cleanActionInformation();
		AddUserToRole addUserToRole = new AddUserToRole(mCP, mCA);
		addUserToRole.exe_SM();

		setRequestPathInformation(actionPath);

		// ================ set session info ========================
		AccountObject account = mCA.getAccountList().get(0);
		userSession = getUserSession(account);
		request.getSession().setAttribute("UserSession", userSession);

		// ================ set request info ========================
		addRequestParameter("PID", projectName);

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		verifyForward("SummaryView");
		verifyForwardPath("/Pages/ezScrumContent.jsp");

		String expectIsGuest = "false";
		String actualIsGuest = (String) request.getSession().getAttribute(
				"isGuest");
		assertEquals(expectIsGuest, actualIsGuest);
		verifyNoActionMessages();
		verifyNoActionErrors();
	}
}
