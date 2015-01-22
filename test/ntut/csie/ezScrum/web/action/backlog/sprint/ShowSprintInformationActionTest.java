package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.TestTool;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class ShowSprintInformationActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private Configuration configuration;
	private final String ACTION_PATH = "/showSprintInformation";
	private IProject project;

	public ShowSprintInformationActionTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();

		//	新增一測試專案
		this.CP = new CreateProject(1);
		this.CP.exeCreate();
		this.project = this.CP.getProjectList().get(0);

		super.setUp();

		setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(this.ACTION_PATH);

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();

		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());
		
		configuration.setTestMode(false);
		configuration.save();

		// ============= release ==============
		ini = null;
		this.CP = null;
		configuration = null;

		super.tearDown();
	}

	/**
	 * 專案沒有一個 Sprint
	 */
	public void testShowInformationAction_1() {
		// ================ set request info ========================
		String sprintID = "1";
		String projectName = this.project.getName();
		
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", sprintID);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("error");
		verifyForwardPath("/Error.jsp");
	}

	/**
	 * 專案擁有一個Sprint但該專案沒有成員
	 */
	public void testShowInformationAction_2() {
		int sprintCount = 1;
		CreateSprint createSprint = new CreateSprint(sprintCount, this.CP);
		createSprint.exe();

		// ================ set request info ========================
		String expectedSprintID = "1";
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", expectedSprintID);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		/*
		 * in struts-config.xml
		 * <forward name="success" path="sprintInformation.show"/>
		 * 因此根據sprintInformation.show必須到tiles-defs.xml找到要轉發的頁面
		 * in tiles-defs.xml
		 * <definition name="sprintInformation.show" path="/Pages/ShowSprintInformation.jsp"></definition>
		 */
		verifyForward("success");
		verifyForwardPath("/Pages/ShowSprintInformation.jsp");

		String actualSprintID = String.valueOf(request.getAttribute("SprintID"));
		String actualStoryPoint = String.valueOf(request.getAttribute("StoryPoint"));
		ISprintPlanDesc actualSprintPlan = (ISprintPlanDesc) request.getAttribute("SprintPlan");
		String actualActors = String.valueOf(request.getAttribute("Actors"));
		String actualSprintPeriod = String.valueOf(request.getAttribute("SprintPeriod"));
		@SuppressWarnings("unchecked")
		List<IIssue> actualIIssueList = (List<IIssue>) request.getAttribute("Stories");

		TestTool testTool = new TestTool();
		Date today = createSprint.Today;
		Date startDate = testTool.getSprintStartDate(CreateSprint.SPRINT_INTERVAL, today);
		Date endDate = testTool.getSprintEndDate(CreateSprint.SPRINT_INTERVAL, today);
		String expectedSprintPeriod = testTool.transformDate(startDate) + " to " + testTool.transformDate(endDate);

		assertEquals(expectedSprintID, actualSprintID);		//	verify sprint ID
		assertEquals("0", actualStoryPoint);				//	verify story points
		assertNotNull(actualSprintPlan);					//	verify sprint plan object
		assertEquals(createSprint.TEST_SPRINT_GOAL + expectedSprintID, actualSprintPlan.getGoal());	//	verify sprint goal
		assertEquals("[]", actualActors);					//	verify 參與此專案的人(因為尚未加入團隊成員因此為空的)
		assertEquals(expectedSprintPeriod, actualSprintPeriod);		//	verify Sprint週期
		//	verify story information
		assertNotNull(actualIIssueList);
		assertEquals(0, actualIIssueList.size());
		assertEquals("0", actualStoryPoint);				//	verify story points
	}

	/**
	 * 專案擁有一個Sprint且專案擁有一位成員
	 * 
	 * @throws Exception
	 */
	public void testShowInformationAction_3() throws Exception {
		int sprintCount = 1;
		CreateSprint createSprint = new CreateSprint(sprintCount, this.CP);
		createSprint.exe();

		int storyCount = 1;
		int expectStoryEstimation = 5;
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(storyCount, expectStoryEstimation, createSprint, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStoryToSprint.exe();

		int accountCount = 1;
		CreateAccount createAccount = new CreateAccount(accountCount);
		createAccount.exe();
		AddUserToRole addUserToRole = new AddUserToRole(this.CP, createAccount);
		addUserToRole.exe_ST();

		// ================ set request info ========================
		String expectedSprintID = "1";
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", expectedSprintID);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		/*
		 * in struts-config.xml
		 * <forward name="success" path="sprintInformation.show"/>
		 * 因此根據sprintInformation.show必須到tiles-defs.xml找到要轉發的頁面
		 * in tiles-defs.xml
		 * <definition name="sprintInformation.show" path="/Pages/ShowSprintInformation.jsp"></definition>
		 */
		verifyForward("success");
		verifyForwardPath("/Pages/ShowSprintInformation.jsp");

		String actualSprintID = String.valueOf(request.getAttribute("SprintID"));
		String actualStoryPoint = String.valueOf(request.getAttribute("StoryPoint"));
		ISprintPlanDesc actualSprintPlan = (ISprintPlanDesc) request.getAttribute("SprintPlan");
		String actualSprintPeriod = String.valueOf(request.getAttribute("SprintPeriod"));
		@SuppressWarnings("unchecked")
		List<IIssue> actualIIssueList = (List<IIssue>) request.getAttribute("Stories");
		@SuppressWarnings("unchecked")
		List<String> actualActors = (List<String>) request.getAttribute("Actors");

		TestTool testTool = new TestTool();
		Date today = createSprint.Today;
		Date startDate = testTool.getSprintStartDate(CreateSprint.SPRINT_INTERVAL, today);
		Date endDate = testTool.getSprintEndDate(CreateSprint.SPRINT_INTERVAL, today);
		String expectedSprintPeriod = testTool.transformDate(startDate) + " to " + testTool.transformDate(endDate);

		/**
		 * 1. verify sprint ID 2. verify story information 3. verify sprint plan information 5. verify Sprint週期 6. verify 參與此專案的人
		 */
		//	verify sprint ID
		assertEquals(expectedSprintID, actualSprintID);

		//	verify story information
		assertNotNull(actualIIssueList);
		assertEquals((new CreateProductBacklog()).TEST_STORY_NAME + 1, actualIIssueList.get(0).getSummary());
		assertEquals(String.valueOf(expectStoryEstimation), actualStoryPoint);

		//	verify sprint plan information
		assertNotNull(actualSprintPlan);
		assertEquals(createSprint.TEST_SPRINT_GOAL + expectedSprintID, actualSprintPlan.getGoal());

		//	verify Sprint週期
		assertEquals(expectedSprintPeriod, actualSprintPeriod);

		//	verify 參與此專案的人
		String expectAccountID = createAccount.getAccount_ID(1);
		boolean isExistedAccount = false;
		for (String accountID : actualActors) {
			if (accountID.equals(expectAccountID)) {
				isExistedAccount = true;
				break;
			}
		}
		assertTrue(isExistedAccount);
	}

	/**
	 * 登入的使用者沒有AccessSprintBacklog的權限
	 */
	public void testShowInformationAction_4() {
		int sprintCount = 1;
		CreateSprint createSprint = new CreateSprint(sprintCount, this.CP);
		createSprint.exe();

		int accountCount = 1;
		CreateAccount createAccount = new CreateAccount(accountCount);
		createAccount.exe();
		AddUserToRole addUserToRole = new AddUserToRole(this.CP, createAccount);
		addUserToRole.exe_Guest();

		// ================ set request info ========================
		String expectedSprintID = "1";
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", expectedSprintID);

		// ================ set session info ========================
		AccountObject account = createAccount.getAccountList().get(0);
		UserSession newUser = new UserSession(new AccountMapper().getAccount(account.getAccount()));
		request.getSession().setAttribute("UserSession", newUser);

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		/*
		 * in struts-config.xml
		 * <forward name="GuestOnly" path="Guest.Summary" />
		 * 因此根據Guest.Summary必須到tiles-defs.xml找到要轉發的頁面
		 * in tiles-defs.xml
		 * <definition name="Guest.Summary" extends="base.layout">
		 * <definition name="base.layout" path="/Layout/Layout.jsp">
		 */
		verifyForward("GuestOnly");
		verifyForwardPath("/Layout/Layout.jsp");
	}

	/**
	 * 登入的使用者擁有AccessSprintBacklog的權限
	 */
	public void testShowInformationAction_5() {
		int sprintCount = 1;
		CreateSprint createSprint = new CreateSprint(sprintCount, this.CP);
		createSprint.exe();

		int accountCount = 1;
		CreateAccount createAccount = new CreateAccount(accountCount);
		createAccount.exe();
		AddUserToRole addUserToRole = new AddUserToRole(this.CP, createAccount);
		addUserToRole.exe_PO();

		// ================ set request info ========================
		String expectedSprintID = "1";
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", expectedSprintID);

		// ================ set session info ========================
		AccountObject account = createAccount.getAccountList().get(0);
		UserSession newUser = new UserSession(new AccountMapper().getAccount(account.getAccount()));
		request.getSession().setAttribute("UserSession", newUser);

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		/*
		 * in struts-config.xml
		 * <forward name="success" path="sprintInformation.show"/>
		 * 因此根據sprintInformation.show必須到tiles-defs.xml找到要轉發的頁面
		 * in tiles-defs.xml
		 * <definition name="sprintInformation.show" path="/Pages/ShowSprintInformation.jsp"></definition>
		 */
		verifyForward("success");
		verifyForwardPath("/Pages/ShowSprintInformation.jsp");
	}
}
