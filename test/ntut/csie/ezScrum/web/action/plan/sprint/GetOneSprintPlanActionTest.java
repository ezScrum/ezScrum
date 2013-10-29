package ntut.csie.ezScrum.web.action.plan.sprint;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.TestTool;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class GetOneSprintPlanActionTest extends MockStrutsTestCase {

	private CreateProject CP;
	private CreateSprint CS;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private final String ACTION_PATH = "/GetSprintPlan";
	private IProject project;
	
	public GetOneSprintPlanActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		//	新增一個測試專案
    	this.CP = new CreateProject(1);
    	this.CP.exeCreate();
    	this.project = this.CP.getProjectList().get(0);
    	
    	// 	新增兩個Sprint
    	this.CS = new CreateSprint(2, this.CP);
    	this.CS.exe();
    	
    	super.setUp();
    	
    	setContextDirectory(new File(config.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	setRequestPathInfo( this.ACTION_PATH );
    	
    	// ============= release ==============
    	ini = null;
    }
	
    protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(this.config.getTestDataPath());
    	
    	
    	// ============= release ==============
    	ini = null;
    	this.CP = null;
    	this.CS = null;
    	
    	super.tearDown();
    }
    
    public void testShowSprint_1(){
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("lastsprint", this.CS.getSprintIDList().get(0));
		addRequestParameter("SprintID", this.CS.getSprintIDList().get(0));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		TestTool testTool = new TestTool();
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"Sprints\":[{");
		expectedResponseText.append("\"Id\":\"" + this.CS.getSprintIDList().get(0) + "\",");
		expectedResponseText.append("\"Goal\":\"" + this.CS.TEST_SPRINT_GOAL + 1 + "\",");
		expectedResponseText.append("\"StartDate\":\"" + testTool.transformDate(this.CS.Today) + "\",");
		expectedResponseText.append("\"Interval\":\"" + CreateSprint.SPRINT_INTERVAL + "\",");
		expectedResponseText.append("\"Members\":\"" + CreateSprint.SPRINT_MEMBER + "\",");
		expectedResponseText.append("\"AvaliableDays\":\"" + CreateSprint.SPRINT_AVAILABLE_DAY + " hours\",");
		expectedResponseText.append("\"FocusFactor\":\"" + CreateSprint.SPRINT_FOCUS_FACTOR + "\",");
		expectedResponseText.append("\"DailyScrum\":\"" + this.CS.TEST_SPRINT_NOTE + 1 + "\",");
		expectedResponseText.append("\"DemoDate\":\"" + testTool.calcaulateDueDate(CreateSprint.SPRINT_INTERVAL, this.CS.Today) + "\",");
		expectedResponseText.append("\"DemoPlace\":\"" + CreateSprint.SPRINT_DEMOPLACE + "\",");
		expectedResponseText.append("\"DueDate\":\"" + testTool.calcaulateDueDate(CreateSprint.SPRINT_INTERVAL, this.CS.Today) + "\"");
		expectedResponseText.append("}]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
    }
    
    public void testShowSprint_2(){
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("lastsprint", "-1");
		addRequestParameter("SprintID", "-1");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"Sprints\":[{");
		expectedResponseText.append("\"Id\":\"" + "-1" + "\",");
		expectedResponseText.append("\"Goal\":\"\",");
		expectedResponseText.append("\"StartDate\":\"\",");
		expectedResponseText.append("\"Interval\":\"\",");
		expectedResponseText.append("\"Members\":\"0\",");
		expectedResponseText.append("\"AvaliableDays\":\"0 hours\",");
		expectedResponseText.append("\"FocusFactor\":\"0\",");
		expectedResponseText.append("\"DailyScrum\":\"\",");
		expectedResponseText.append("\"DemoDate\":\"\",");
		expectedResponseText.append("\"DemoPlace\":\"\",");
		expectedResponseText.append("\"DueDate\":\"\"");
		expectedResponseText.append("}]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
    }
}
