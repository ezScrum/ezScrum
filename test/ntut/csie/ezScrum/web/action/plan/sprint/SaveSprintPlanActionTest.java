package ntut.csie.ezScrum.web.action.plan.sprint;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.TestTool;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class SaveSprintPlanActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private Configuration mConfig;
	private final String mActionPath = "/saveSprintPlan";
	private IProject mProject;
	
	public SaveSprintPlanActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		//	新增一個測試專案
    	mCP = new CreateProject(1);
    	mCP.exeCreate();
    	mProject = mCP.getProjectList().get(0);
    	
    	super.setUp();
    	
    	setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	setRequestPathInfo( mActionPath );
    	
    	// ============= release ==============
    	ini = null;
    }
	
    protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();
    	
    	// ============= release ==============
    	ini = null;
    	mCP = null;
    	mConfig = null;
    	
    	super.tearDown();
    }
    
    /**
     * 1. create sprint
     */
    public void testSaveSprint_1(){
		// ================ set request info ========================
    	TestTool testTool = new TestTool();
    	Calendar cal = Calendar.getInstance();
    	Date today = cal.getTime();
    	String sprintID = "1";
    	String sprintGoal = "test sprint goal";
    	String sprintStartDate = testTool.transformDate(today);
    	String sprintInterval = "2";
    	String sprintMembers = "4";
    	String sprintAvaliableDays = "100";
    	String sprintFocusFactor = "100";
    	String sprintDailyScrum = "Lab 1321";
    	String sprintDemoDate = testTool.calcaulateDueDate(sprintInterval, today);
    	String sprintDemoPlace = "Lab 1321";
    	
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("isCreate", "True");
		addRequestParameter("Id", sprintID);
		addRequestParameter("Goal", sprintGoal);
		addRequestParameter("StartDate", sprintStartDate);
		addRequestParameter("Interval", sprintInterval);
		addRequestParameter("Members", sprintMembers);
		addRequestParameter("AvaliableDays", sprintAvaliableDays);
		addRequestParameter("FocusFactor", sprintFocusFactor);
		addRequestParameter("DailyScrum", sprintDailyScrum);
		addRequestParameter("DemoDate", sprintDemoDate);
		addRequestParameter("DemoPlace", sprintDemoPlace);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		StringBuilder expectedResponseText = new StringBuilder("true");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
		
		//	assert sprint information
		checkSprintInformation(projectName, sprintID, sprintGoal, sprintStartDate, sprintInterval, sprintMembers, sprintAvaliableDays, sprintFocusFactor, sprintDailyScrum, sprintDemoDate, sprintDemoPlace);
    }
    
    
    /**
     * 2. edit sprint
     */
    public void testSaveSprint_2(){
    	int count = 1;
    	CreateSprint CS = new CreateSprint(count, mCP);
    	CS.exe();
    	
		// ================ set request info ========================
    	TestTool testTool = new TestTool();
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(cal.getTime());
    	cal.add(Calendar.DATE, 2);
    	Date date = cal.getTime();
    	String sprintID = "1";
    	String sprintGoal = "test sprint goal";
    	String sprintStartDate = testTool.transformDate( date );
    	String sprintInterval = "2";
    	String sprintMembers = "5";
    	String sprintAvaliableDays = "120";
    	String sprintFocusFactor = "80";
    	String sprintDailyScrum = "Lab 1321 Daily Scrum";
    	String sprintDemoDate = testTool.calcaulateDueDate(sprintInterval, date);
    	String sprintDemoPlace = "Lab 1321 Demo Place";
    	
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("isCreate", "false");
		addRequestParameter("Id", sprintID);
		addRequestParameter("Goal", sprintGoal);
		addRequestParameter("StartDate", sprintStartDate);
		addRequestParameter("Interval", sprintInterval);
		addRequestParameter("Members", sprintMembers);
		addRequestParameter("AvaliableDays", sprintAvaliableDays);
		addRequestParameter("FocusFactor", sprintFocusFactor);
		addRequestParameter("DailyScrum", sprintDailyScrum);
		addRequestParameter("DemoDate", sprintDemoDate);
		addRequestParameter("DemoPlace", sprintDemoPlace);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		StringBuilder expectedResponseText = new StringBuilder("true");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
		
		//	assert sprint information
		checkSprintInformation(projectName, sprintID, sprintGoal, sprintStartDate, sprintInterval, sprintMembers, sprintAvaliableDays, sprintFocusFactor, sprintDailyScrum, sprintDemoDate, sprintDemoPlace);
    }
    
    private void checkSprintInformation(String projectName, String sprintID, String sprintGoal, String sprintStartDate, String sprintInterval, String sprintMembers, String sprintAvaliableDays, String sprintFocusFactor, String sprintDailyScrum, String sprintDemoDate, String sprintDemoPlace){
		//	clear request and response
		clearRequestParameters();
		response.reset();
		
		//	set get sprint information action path
    	setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	setRequestPathInfo( "/GetSprintPlan" );
		
		// ================ set request info ========================
		projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("lastsprint", sprintID);
		addRequestParameter("SprintID", sprintID);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"Sprints\":[{");
		expectedResponseText.append("\"Id\":\"" + sprintID + "\",");
		expectedResponseText.append("\"Goal\":\"" + sprintGoal + "\",");
		expectedResponseText.append("\"StartDate\":\"" + sprintStartDate + "\",");
		expectedResponseText.append("\"Interval\":\"" + sprintInterval + "\",");
		expectedResponseText.append("\"Members\":\"" + sprintMembers + "\",");
		expectedResponseText.append("\"AvaliableDays\":\"" + sprintAvaliableDays + " hours\",");
		expectedResponseText.append("\"FocusFactor\":\"" + sprintFocusFactor + "\",");
		expectedResponseText.append("\"DailyScrum\":\"" + sprintDailyScrum + "\",");
		expectedResponseText.append("\"DemoDate\":\"" + sprintDemoDate + "\",");
		expectedResponseText.append("\"DemoPlace\":\"" + sprintDemoPlace + "\",");
		expectedResponseText.append("\"DueDate\":\"" + sprintDemoDate + "\"");
		expectedResponseText.append("}]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
    }
}
