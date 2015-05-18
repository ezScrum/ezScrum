package ntut.csie.ezScrum.web.action.report;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
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
import ntut.csie.ezScrum.web.control.ScheduleReport;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class ShowScheduleReportActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
	private Configuration mConfig;
	private final String mActionPath = "/showScheduleReport";

	public ShowScheduleReportActionTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		mCP = new CreateProject(1);
		mCP.exeCreate(); // 新增一測試專案

		super.setUp();
		// ================ set action info ========================
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);

		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		projectManager = null;
		mCP = null;
		mCS = null;
		mASTS = null;
		mATTS = null;
		mConfig = null;
	}

	/**
	 * 正常執行
	 */
	public void testShowScheduleReport_1() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增1個Sprint到專案內

		mASTS = new AddStoryToSprint(2, 1, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mASTS.exe(); // 新增2筆Story到Sprint內

		mATTS = new AddTaskToStory(2, 1, mASTS, mCP);
		mATTS.exe(); // 新增2筆Task到Story內

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long sprintId = mCS.getSprintsId().get(0);
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, mCS.getSprintsId().get(0));
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		List<StoryObject> stories = sprintBacklogLogic.getStories();

		// ================ set request info ========================
		String projectName = project.getName();
		request.setHeader("Referer", "?PID=" + projectName); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.getSession().setAttribute("sprintID", sprintId);

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
		assertEquals(sprintId, ActualReport.getIteration());
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
		IProject project = mCP.getProjectList().get(0);
		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("No sprints in project!");

		// ================ set request info ========================
		String projectName = project.getName();
		request.setHeader("Referer", "?PID=" + projectName); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
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
		IProject project = mCP.getProjectList().get(0);
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增1個Sprint到專案內
		// 新增帳號
		CreateAccount CA = new CreateAccount(1);
		CA.exe();
		AddUserToRole AUTR = new AddUserToRole(mCP, CA);
		AUTR.exe_Guest();
		// 使用無權限的帳號資訊塞到UserSession
		AccountObject account = new AccountMapper().getAccount(CA.getAccount_ID(1));
		IUserSession userSession = new UserSession(account);

		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("System failure!");

		// ================ set request info ========================
		String projectName = project.getName();
		request.setHeader("Referer", "?PID=" + projectName); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", userSession);
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
