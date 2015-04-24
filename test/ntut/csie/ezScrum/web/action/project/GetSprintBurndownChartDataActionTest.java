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
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.jcis.resource.core.IProject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import servletunit.struts.MockStrutsTestCase;

public class GetSprintBurndownChartDataActionTest extends MockStrutsTestCase {
	private int mProjectCount = 1;
	private int mAccountCount = 1;
	private final String mActionPathGetSprintBurndownChartData = "/getSprintBurndownChartData";
	private Configuration mConfig;
	private CreateProject mCP;
	private CreateAccount mCA;
	
	public GetSprintBurndownChartDataActionTest(String testMethod) {
        super(testMethod);
    }
	
	private void setRequestPathInformation(String actionPath){
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
	 * @param account
	 * @return
	 */
    private IUserSession getUserSession(AccountObject account){
    	IUserSession userSession = new UserSession(account);
    	return userSession;
    }
    
	protected void setUp() throws Exception{
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
		
		setRequestPathInformation( mActionPathGetSprintBurndownChartData );
	}
	
	protected void tearDown()throws Exception{
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
	 * 測試 "admin"，
	 * 在沒有建立任何Sprint的情況下，
	 * 是否能取得正確的Sprint Burndown Chart for story資訊。
	 * response text : {"Points":[],"success":true}
	 */
	public void testAdminGetSprintBurndownChartDataAction_Story(){
		IProject project = mCP.getProjectList().get(0);
		String projectID = project.getName();
		
    	// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ set request info ========================
		addRequestParameter("SprintID", "0");
		addRequestParameter("Type", "story");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		String actualResponseText = response.getWriterBuffer().toString();
		String expectResponseText = "{\"Points\":[],\"success\":true}";
		assertEquals(expectResponseText, actualResponseText);
	}
	
	/**
	 * 測試 "admin"，
	 * 在沒有建立任何Sprint的情況下，
	 * 是否能取得正確的Sprint Burndown Chart for Task資訊。
	 * response text : {"Points":[],"success":true}
	 */
	public void testAdminGetSprintBurndownChartDataAction_Task(){
		IProject project = mCP.getProjectList().get(0);
		String projectID = project.getName();
		
    	// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ set request info ========================
		addRequestParameter("SprintID", "0");
		addRequestParameter("Type", "task");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		String actualResponseText = response.getWriterBuffer().toString();
		String expectResponseText = "{\"Points\":[],\"success\":true}";
		assertEquals(expectResponseText, actualResponseText);
	}
	
	/**
	 * 測試 "一般使用者"，
	 * 在沒有建立任何Sprint的情況下，
	 * 是否能取得正確的Sprint Burndown Chart for story資訊。
	 * response text : {"Points":[],"success":true}
	 */
	public void testUserGetSprintBurndownChartDataAction_Story(){
		IProject project = mCP.getProjectList().get(0);
		String projectID = project.getName();
		
    	// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ set request info ========================
		addRequestParameter("SprintID", "0");
		addRequestParameter("Type", "story");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		String actualResponseText = response.getWriterBuffer().toString();
		String expectResponseText = "{\"Points\":[],\"success\":true}";
		assertEquals(expectResponseText, actualResponseText);
	}
	
	/**
	 * 測試 "一般使用者"，
	 * 在沒有建立任何Sprint的情況下，
	 * 是否能取得正確的Sprint Burndown Chart for Task資訊。
	 * response text : {"Points":[],"success":true}
	 */
	public void testUserGetSprintBurndownChartDataAction_Task(){
		IProject project = mCP.getProjectList().get(0);
		String projectID = project.getName();
		
    	// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ set request info ========================
		addRequestParameter("SprintID", "0");
		addRequestParameter("Type", "task");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		String actualResponseText = response.getWriterBuffer().toString();
		String expectResponseText = "{\"Points\":[],\"success\":true}";
		assertEquals(expectResponseText, actualResponseText);
	}
	
	/**
	 * 測試 "一般使用者"，
	 * 在該專案有一個sprint正在進行的情況下，
	 * 是否能取得正確的Sprint Burndown Chart for Story資訊。
	 * @throws Exception
	 */
	public void testUserGetSprintBurndownChartDataAction_StoryAndInformation() throws Exception{
		ProjectObject project = mCP.getAllProjects().get(0); 
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
		
		// ================ set request info ========================
		addRequestParameter("SprintID", "-1");	//	-1:代表離現在時間最近的Sprint
		addRequestParameter("Type", "story");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", getUserSession(account));
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		TestTool tool = new TestTool();
		List<String> sprintDateList = tool.getSprintDate(project, getUserSession(account));
		//	減一代表Sprint開始的第一天是SprintPlanning所以第一天不工作，因此總工作天必須減一。
		int workDateCount = sprintDateList.size() - 1;
		List<String> storyIdealLinePoints = tool.getStoryIdealLinePoint(workDateCount, 16.0);
		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject actualResponseJson = new JSONObject(actualResponseText);
		JSONArray points = actualResponseJson.getJSONArray("Points");
		assertEquals(true, actualResponseJson.getBoolean("success"));
		assertEquals(10, points.length());
		for(int i = 0; i <= workDateCount; i++) {
			assertEquals(sprintDateList.get(i), points.getJSONObject(i).getString("Date"));
			assertEquals(storyIdealLinePoints.get(i), String.valueOf(points.getJSONObject(i).getDouble("IdealPoint")));
			if(i == 0) {
				assertEquals(16.0d, points.getJSONObject(i).getDouble("RealPoint"));
			} else {
				assertEquals("null", points.getJSONObject(i).getString("RealPoint"));
			}
		}
	}
	
	/**
	 * 測試 "一般使用者"，
	 * 在該專案在有一個sprint正在進行的情況下，
	 * 是否能取得正確的Sprint Burndown Chart for Task資訊。
	 * @throws Exception
	 */
	public void testUserGetSprintBurndownChartDataAction_TaskAndInformation() throws Exception{
		ProjectObject project = mCP.getAllProjects().get(0); 
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
		
		// ================ set request info ========================
		addRequestParameter("SprintID", "-1");	//	-1:代表離現在時間最近的Sprint
		addRequestParameter("Type", "task");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", getUserSession(account));
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		TestTool tool = new TestTool();
		List<String> sprintDateList = tool.getSprintDate(project, getUserSession(account));
		//	減一代表Sprint開始的第一天是SprintPlanning所以第一天不工作，因此總工作天必須減一。
		int workDateCount = sprintDateList.size() - 1;
		List<String> taskIdealLinePoints = tool.getTaskIdealLinePoint(workDateCount, 12.0);
		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject actualResponseJson = new JSONObject(actualResponseText);
		JSONArray points = actualResponseJson.getJSONArray("Points");
		assertEquals(true, actualResponseJson.getBoolean("success"));
		assertEquals(10, points.length());
		for(int i = 0; i <= workDateCount; i++) {
			assertEquals(sprintDateList.get(i), points.getJSONObject(i).getString("Date"));
			assertEquals(taskIdealLinePoints.get(i), String.valueOf(points.getJSONObject(i).getDouble("IdealPoint")));
			if(i == 0) {
				assertEquals(12.0d, points.getJSONObject(i).getDouble("RealPoint"));
			} else {
				assertEquals("null", points.getJSONObject(i).getString("RealPoint"));
			}
		}
	}
}
