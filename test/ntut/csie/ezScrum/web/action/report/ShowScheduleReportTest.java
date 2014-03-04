package ntut.csie.ezScrum.web.action.report;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
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
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.control.ScheduleReport;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class ShowScheduleReportTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private AddStoryToSprint ASS;
	private AddTaskToStory ATS;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private final String ACTION_PATH = "/showScheduleReport";

	public ShowScheduleReportTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe(); // 初始化 SQL

		CP = new CreateProject(1);
		CP.exeCreate(); // 新增一測試專案

		super.setUp();
		// ================ set action info ========================
		setContextDirectory(new File(config.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(ACTION_PATH);

		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe(); // 初始化 SQL

		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(this.config.getTestDataPath());

		super.tearDown();

		// ============= release ==============
		ini = null;
		projectManager = null;
		CP = null;
		CS = null;
		ASS = null;
		ATS = null;
	}

	/**
	 * 正常執行
	 */
	public void testShowScheduleReport_1() throws Exception {
		CS = new CreateSprint(1, CP);
		CS.exe(); // 新增1個Sprint到專案內

		ASS = new AddStoryToSprint(2, 1, CS, CP, CreateProductBacklog.TYPE_ESTIMATION);
		ASS.exe(); // 新增2筆Story到Sprint內

		ATS = new AddTaskToStory(2, 1, ASS, CP);
		ATS.exe(); // 新增2筆Task到Story內

		// ================ set initial data =======================
		IProject project = CP.getProjectList().get(0);
		int SprintID = Integer.parseInt(CS.getSprintIDList().get(0));
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, config.getUserSession(), CS.getSprintIDList().get(0));
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		List<IIssue> stories = sprintBacklogLogic.getStories();

		// ================ set request info ========================
		String projectName = project.getName();
		request.setHeader("Referer", "?PID=" + projectName); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", config.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.getSession().setAttribute("sprintID", SprintID);

		// ================ 執行 action ==============================
		actionPerform();
		// 驗證Path
		verifyForwardPath("/Pages/ShowScheduleReport.jsp");
		verifyForward("success");
		verifyNoActionErrors();

		ScheduleReport ActualReport = (ScheduleReport) request.getAttribute("report");
		String startDate = DateUtil.format(sprintBacklogMapper.getSprintStartDate(), DateUtil._8DIGIT_DATE_1);
		String endtDate = DateUtil.format(sprintBacklogMapper.getSprintEndDate(), DateUtil._8DIGIT_DATE_1);
		String ExpectedDuration = startDate + " ~ " + endtDate;
		assertEquals(SprintID, ActualReport.getIteration());
		assertEquals("./Workspace/TEST_PROJECT_1/_metadata/ScheduleReport/Sprint1/ScheduleReport.png", ActualReport.getPath());
		assertEquals(stories.size(), ActualReport.getStorySize());
		assertEquals(sprintBacklogMapper.getSprintGoal(), ActualReport.getSprintGoal());
		assertEquals(ExpectedDuration, ActualReport.getDuration()); // 驗證Sprint的Duration時間範圍

		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		List<ISprintPlanDesc> ExpectedPlans = sprintPlanHelper.loadListPlans();
		List<ISprintPlanDesc> ActualPlans = (List<ISprintPlanDesc>) request.getAttribute("SprintPlans");
		for (int i = 0; i < ExpectedPlans.size(); i++) {
			assertEquals(ExpectedPlans.get(i).getID(), ActualPlans.get(i).getID());
		}

		// ============= release ==============
		project = null;
		sprintPlanHelper = null;
		sprintBacklogLogic = null;
		sprintBacklogMapper = null;
		stories = null;
		ExpectedPlans = null;
		ActualPlans = null;
	}

	/**
	 * project中沒有sprint
	 */
	public void testShowScheduleReport_2() throws IOException {
		// ================ set initial data =======================
		IProject project = CP.getProjectList().get(0);
		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("No sprints in project!");

		// ================ set request info ========================
		String projectName = project.getName();
		request.setHeader("Referer", "?PID=" + projectName); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", config.getUserSession());
		request.getSession().setAttribute("Project", project);

		// ================ 執行 action ==============================
		actionPerform();
		// 驗證Path
		verifyForward("displayMessage");
		verifyForwardPath("/DisplayMessage.jsp");
		verifyNoActionErrors();

		String actualResponseText = request.getAttribute("message").toString();
		assertEquals(expectedResponseTest.toString(), actualResponseText);

		// ============= release ==============
		project = null;
	}

	/**
	 * project中有sprint 但帳號權限不符合
	 */
	public void testShowScheduleReport_3() throws IOException {
		// ================ set initial data =======================
		IProject project = CP.getProjectList().get(0);
		CS = new CreateSprint(1, CP);
		CS.exe(); // 新增1個Sprint到專案內
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
}
