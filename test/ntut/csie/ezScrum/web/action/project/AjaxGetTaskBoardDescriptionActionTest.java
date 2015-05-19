package ntut.csie.ezScrum.web.action.project;

import java.io.File;
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
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxGetTaskBoardDescriptionActionTest extends MockStrutsTestCase {
	private int mProjectCount = 1;
	private int mAccountCount = 1;
	private final String mActionPathGetTaskBoardDescription = "/GetTaskBoardDescription";
	private Configuration mConfig;
	private CreateProject mCP;
	private CreateAccount mCA;
	
	public AjaxGetTaskBoardDescriptionActionTest(String testMethod) {
		super(testMethod);
	}

	private void setRequestPathInformation(String actionPath) {
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(actionPath);
	}

	/**
	 * clean previous action info
	 */
	/*	private void cleanActionInformation(){
			clearRequestParameters();
			response.reset();
		}*/

	/**
	 * 取得一般使用者的UserSession
	 * 
	 * @param account
	 * @return
	 */
	private IUserSession getUserSession(AccountObject account) {
		IUserSession userSession = new UserSession(account);
		return userSession;
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		// 新增使用者
		mCA = new CreateAccount(mAccountCount);
		mCA.exe();

		super.setUp();

		// ================ set action info ========================
		setRequestPathInformation(mActionPathGetTaskBoardDescription);
	}

	protected void tearDown() throws Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// release
		mConfig = null;
		mCP = null;
		mCA = null;
	}

	/**
	 * 測試admin登入專案後，是否能取得正確的該專案的Taskboard資訊。 response text : {"ID":"0","SprintGoal":"","Current_Story_Undone_Total_Point":"","Current_Task_Undone_Total_Point":""}
	 */
	public void testAdminAjaxGetTaskBoardDescriptionAction() {
		String projectID = mCP.getProjectList().get(0).getName();

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		//	assert response text
		String actualResponseText = response.getWriterBuffer().toString();
		String expectResponseText =
		        //{"ID":"0","SprintGoal":"","Current_Story_Undone_Total_Point":"","Current_Task_Undone_Total_Point":""}
		        "{\"ID\":\"0\"," +
		                "\"SprintGoal\":\"\"," +
		                "\"Current_Story_Undone_Total_Point\":\"\"," +
		                "\"Current_Task_Undone_Total_Point\":\"\"}";
		assertEquals(expectResponseText, actualResponseText);
	}

	/**
	 * 測試一般使用者在沒有加入該專案下，是否會回傳權限不足的警告訊息。 response text:{"PermissionAction":{"ActionCheck":"false", "Id":0}}
	 */
	public void testUserAjaxGetTaskBoardDescriptionAction_NotInProject() {
		String projectID = mCP.getProjectList().get(0).getName();
		AccountObject account = mCA.getAccountList().get(0);

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", getUserSession(account));

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		//	assert response text
		String actualResponseText = response.getWriterBuffer().toString();
		String expectResponseText = "{\"PermissionAction\":{\"ActionCheck\":\"false\", \"Id\":0}}";
		assertEquals(expectResponseText, actualResponseText);
	}

	/**
	 * 測試一般使用者登入專案後，該專案在沒有任何的sprint進行的情況下，是否能取得正確的Taskboard資訊。 response text : {"ID":"0","SprintGoal":"","Current_Story_Undone_Total_Point":"","Current_Task_Undone_Total_Point":""}
	 */
	public void testUserAjaxGetTaskBoardDescriptionAction_InProjectAndNoInformation() {
		String projectID = mCP.getProjectList().get(0).getName();
		AccountObject account = mCA.getAccountList().get(0);

		AddUserToRole addUserToRole = new AddUserToRole(mCP, mCA);
		addUserToRole.exe_ST();

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", getUserSession(new AccountMapper().getAccount(account.getUsername())));

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		//	assert response text
		String actualResponseText = response.getWriterBuffer().toString();
		String expectResponseText =
		        "{\"ID\":\"0\"," +
		                "\"SprintGoal\":\"\"," +
		                "\"Current_Story_Undone_Total_Point\":\"\"," +
		                "\"Current_Task_Undone_Total_Point\":\"\"}";
		assertEquals(expectResponseText, actualResponseText);
	}

	/**
	 * 測試一般使用者登入專案後，該專案在有一個sprint正在進行的情況下，是否能取得正確的Taskboard資訊。
	 * 
	 * @throws Exception
	 */
	public void testUserAjaxGetTaskBoardDescriptionAction_InProjectAndInformation() throws Exception {
		IProject project = mCP.getProjectList().get(0);
		String projectID = project.getName();
		AccountObject account = mCA.getAccountList().get(0);

		AddUserToRole addUserToRole = new AddUserToRole(mCP, mCA);
		addUserToRole.exe_ST();

		int sprintCount = 1;
		int storyCount = 2;
		int taskCount = 2;
		int storyEstValue = 8;
		int taskEstValue = 3;
		CreateSprint CS = new CreateSprint(sprintCount, mCP);
		CS.exe();
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(storyCount, storyEstValue, CS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		addStoryToSprint.exe();
		AddTaskToStory addTaskToStory = new AddTaskToStory(taskCount, taskEstValue, addStoryToSprint, mCP);
		addTaskToStory.exe();

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", getUserSession(new AccountMapper().getAccount(account.getUsername())));

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		//	assert response text
		String expectedSprint_Goal = "TEST_SPRINTGOAL_1";
		String expectedSprint_Current_Story_Undone_Total_Point = "16.0 / 16.0";
		String expectedSprint_Current_Task_Undone_Total_Point = "12.0 / 12.0";
		String actualResponseText = response.getWriterBuffer().toString();
		String expectResponseText =
		        "{\"ID\":\"0\"," +
		                "\"SprintGoal\":\"" + expectedSprint_Goal + "\"," +
		                "\"Current_Story_Undone_Total_Point\":\"" + expectedSprint_Current_Story_Undone_Total_Point + "\"," +
		                "\"Current_Task_Undone_Total_Point\":\"" + expectedSprint_Current_Task_Undone_Total_Point + "\"" +
		                "}";
		assertEquals(expectResponseText, actualResponseText);
	}
}
