package ntut.csie.ezScrum.web.action.project;

import java.io.File;
import java.util.List;

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
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class GetSprintBurndownChartDataActionTest extends MockStrutsTestCase {
	
	private Configuration configuration;
	private CreateProject CP;
	private CreateAccount CA;
	private int ProjectCount = 1;
	private int AccountCount = 1;
	private final String ActionPath_GetSprintBurndownChartData = "/getSprintBurndownChartData";
	
	public GetSprintBurndownChartDataActionTest(String testMethod) {
        super(testMethod);
    }
	
	private void setRequestPathInformation( String actionPath ){
    	setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	setRequestPathInfo( actionPath );
	}
	
	/**
	 * clean previous action info
	 */
/*	private void cleanActionInformation(){
		clearRequestParameters();
		this.response.reset();
	}*/
	
	/**
	 * 取得一般使用者的UserSession
	 * @param account
	 * @return
	 */
    private IUserSession getUserSession(AccountObject account){
    	IUserSession userSession = new UserSession(account);
    	return userSession;
    }
    
	protected void setUp() throws Exception{
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		
		// 新增Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();
		
		// 新增使用者
		this.CA = new CreateAccount(this.AccountCount);
		this.CA.exe();
		
		super.setUp();
		
		this.setRequestPathInformation( this.ActionPath_GetSprintBurndownChartData );
    	// ============= release ==============
    	ini = null;
	}
	
	protected void tearDown()throws Exception{
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase( configuration.getDataPath() );
		
		configuration.setTestMode(false);
		configuration.save();

		super.tearDown();
		
		ini = null;
		projectManager = null;
		configuration = null;
	}
	
//	/**
//	 * 測試 "admin"，
//	 * 在沒有建立任何Sprint的情況下，
//	 * 是否能取得正確的Sprint Burndown Chart for story資訊。
//	 * response text : {"Points":[],"success":true}
//	 */
//	public void testAdminGetSprintBurndownChartDataAction_Story(){
//		IProject project = this.CP.getProjectList().get(0);
//		String projectID = project.getName();
//		
//    	// ================ set URL parameter ========================
//		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
//		
//		// ================ set request info ========================
//		addRequestParameter("SprintID", "0");
//		addRequestParameter("Type", "story");
//		
//		// ================ set session info ========================
//		request.getSession().setAttribute("UserSession", this.config.getUserSession());
//		
//		// ================ 執行 action ======================
//		actionPerform();
//		
//		// ================ assert ======================
//		verifyNoActionErrors();
//		verifyNoActionMessages();
//		
//		//	assert response text
//		String actualResponseText = response.getWriterBuffer().toString();
//		String expectResponseText = "{\"Points\":[],\"success\":true}";
//		assertEquals(expectResponseText, actualResponseText);
//	}
//	
//	/**
//	 * 測試 "admin"，
//	 * 在沒有建立任何Sprint的情況下，
//	 * 是否能取得正確的Sprint Burndown Chart for Task資訊。
//	 * response text : {"Points":[],"success":true}
//	 */
//	public void testAdminGetSprintBurndownChartDataAction_Task(){
//		IProject project = this.CP.getProjectList().get(0);
//		String projectID = project.getName();
//		
//    	// ================ set URL parameter ========================
//		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
//		
//		// ================ set request info ========================
//		addRequestParameter("SprintID", "0");
//		addRequestParameter("Type", "task");
//		
//		// ================ set session info ========================
//		request.getSession().setAttribute("UserSession", this.config.getUserSession());
//		
//		// ================ 執行 action ======================
//		actionPerform();
//		
//		// ================ assert ======================
//		verifyNoActionErrors();
//		verifyNoActionMessages();
//		
//		//	assert response text
//		String actualResponseText = response.getWriterBuffer().toString();
//		String expectResponseText = "{\"Points\":[],\"success\":true}";
//		assertEquals(expectResponseText, actualResponseText);
//	}
//	
//	/**
//	 * 測試 "一般使用者"，
//	 * 在沒有建立任何Sprint的情況下，
//	 * 是否能取得正確的Sprint Burndown Chart for story資訊。
//	 * response text : {"Points":[],"success":true}
//	 */
//	public void testUserGetSprintBurndownChartDataAction_Story(){
//		IProject project = this.CP.getProjectList().get(0);
//		String projectID = project.getName();
//		
//    	// ================ set URL parameter ========================
//		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
//		
//		// ================ set request info ========================
//		addRequestParameter("SprintID", "0");
//		addRequestParameter("Type", "story");
//		
//		// ================ set session info ========================
//		request.getSession().setAttribute("UserSession", this.config.getUserSession());
//		
//		// ================ 執行 action ======================
//		actionPerform();
//		
//		// ================ assert ======================
//		verifyNoActionErrors();
//		verifyNoActionMessages();
//		
//		//	assert response text
//		String actualResponseText = response.getWriterBuffer().toString();
//		String expectResponseText = "{\"Points\":[],\"success\":true}";
//		assertEquals(expectResponseText, actualResponseText);
//	}
//	
//	/**
//	 * 測試 "一般使用者"，
//	 * 在沒有建立任何Sprint的情況下，
//	 * 是否能取得正確的Sprint Burndown Chart for Task資訊。
//	 * response text : {"Points":[],"success":true}
//	 */
//	public void testUserGetSprintBurndownChartDataAction_Task(){
//		IProject project = this.CP.getProjectList().get(0);
//		String projectID = project.getName();
//		
//    	// ================ set URL parameter ========================
//		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
//		
//		// ================ set request info ========================
//		addRequestParameter("SprintID", "0");
//		addRequestParameter("Type", "task");
//		
//		// ================ set session info ========================
//		request.getSession().setAttribute("UserSession", this.config.getUserSession());
//		
//		// ================ 執行 action ======================
//		actionPerform();
//		
//		// ================ assert ======================
//		verifyNoActionErrors();
//		verifyNoActionMessages();
//		
//		//	assert response text
//		String actualResponseText = response.getWriterBuffer().toString();
//		String expectResponseText = "{\"Points\":[],\"success\":true}";
//		assertEquals(expectResponseText, actualResponseText);
//	}
	
	/**
	 * 測試 "一般使用者"，
	 * 在該專案有一個sprint正在進行的情況下，
	 * 是否能取得正確的Sprint Burndown Chart for Story資訊。
	 * @throws Exception
	 */
	public void testUserGetSprintBurndownChartDataAction_StoryAndInformation() throws Exception{
		IProject project = this.CP.getProjectList().get(0); 
		String projectID = project.getName();
		AccountObject account = this.CA.getAccountList().get(0);
		
		AddUserToRole addUserToRole = new AddUserToRole(this.CP, this.CA);
		addUserToRole.exe_ST();
		
		int sprintCount = 1;
		int storyCount = 2;
		int taskCount = 2;
		int storyEstValue = 8;
		int taskEstValue = 3;
		CreateSprint CS = new CreateSprint(sprintCount, this.CP);
		CS.exe();
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(storyCount, storyEstValue, CS, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStoryToSprint.exe();
		AddTaskToStory addTaskToStory = new AddTaskToStory(taskCount, taskEstValue, addStoryToSprint, CP);
		addTaskToStory.exe();
		
    	// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ set request info ========================
		addRequestParameter("SprintID", "-1");	//	-1:代表離現在時間最近的Sprint
		addRequestParameter("Type", "story");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", this.getUserSession(account));
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		TestTool tool = new TestTool();
		List<String> sprintDateList = tool.getSprintDate(project, this.getUserSession(account));
		//	減一代表Sprint開始的第一天是SprintPlanning所以第一天不工作，因此總工作天必須減一。
		int workDateCount = sprintDateList.size() - 1;
		List<String> storyIdealLinePoints = tool.getStoryIdealLinePoint( workDateCount, 16.0 );
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"success\":true,")
							.append("\"Points\":[");
		for(int i = 0; i <= workDateCount; i++) {
			expectedResponseText.append("{")
								.append("\"Date\":\"").append(sprintDateList.get(i)).append("\",")
								.append("\"IdealPoint\":").append(storyIdealLinePoints.get(i)).append(",")
								.append("\"RealPoint\":");
			if(i == 0) {
				expectedResponseText.append("16.0},");
			} else {
				expectedResponseText.append("\"null\"},");
			}
		}
		expectedResponseText.deleteCharAt(expectedResponseText.length() - 1);
		expectedResponseText.append("]}");
		
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
	
	/**
	 * 測試 "一般使用者"，
	 * 在該專案在有一個sprint正在進行的情況下，
	 * 是否能取得正確的Sprint Burndown Chart for Task資訊。
	 * @throws Exception
	 */
	public void testUserGetSprintBurndownChartDataAction_TaskAndInformation() throws Exception{
		IProject project = this.CP.getProjectList().get(0); 
		String projectID = project.getName();
		AccountObject account = this.CA.getAccountList().get(0);
		
		AddUserToRole addUserToRole = new AddUserToRole(this.CP, this.CA);
		addUserToRole.exe_ST();
		
		int sprintCount = 1;
		int storyCount = 2;
		int taskCount = 2;
		int storyEstValue = 8;
		int taskEstValue = 3;
		CreateSprint CS = new CreateSprint(sprintCount, this.CP);
		CS.exe();
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(storyCount, storyEstValue, CS, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStoryToSprint.exe();
		AddTaskToStory addTaskToStory = new AddTaskToStory(taskCount, taskEstValue, addStoryToSprint, CP);
		addTaskToStory.exe();
		
    	// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ set request info ========================
		addRequestParameter("SprintID", "-1");	//	-1:代表離現在時間最近的Sprint
		addRequestParameter("Type", "task");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", this.getUserSession(account));
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		TestTool tool = new TestTool();
		List<String> sprintDateList = tool.getSprintDate(project, this.getUserSession(account));
//		減一代表Sprint開始的第一天是SprintPlanning所以第一天不工作，因此總工作天必須減一。
		int workDateCount = sprintDateList.size() - 1;
		List<String> taskIdealLinePoints = tool.getTaskIdealLinePoint( workDateCount, 12.0 );
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"success\":true,")
							.append("\"Points\":[");
		for(int i = 0; i <= workDateCount; i++) {
			expectedResponseText.append("{")
								.append("\"Date\":\"").append(sprintDateList.get(i)).append("\",")
								.append("\"IdealPoint\":").append(taskIdealLinePoints.get(i)).append(",")
								.append("\"RealPoint\":");
			if(i == 0) {
				expectedResponseText.append("12.0},");
			} else {
				expectedResponseText.append("\"null\"},");
			}
		}
		expectedResponseText.deleteCharAt(expectedResponseText.length() - 1);
		expectedResponseText.append("]}");
		
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
