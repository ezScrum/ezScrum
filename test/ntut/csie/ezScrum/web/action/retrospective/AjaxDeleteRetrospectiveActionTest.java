package ntut.csie.ezScrum.web.action.retrospective;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRetrospective;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxDeleteRetrospectiveActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private CreateRetrospective CR;
	
	private Configuration configuration;
	
	private String actionPath = "/ajaxDeleteRetrospective";
	
	public AjaxDeleteRetrospectiveActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案

		super.setUp();

		setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent")); // 設定讀取的
		// struts-config
		// 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(this.actionPath);

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		CopyProject copyProject = new CopyProject(this.CP);
		copyProject.exeDelete_Project(); // 刪除測試檔案
		
		configuration.setTestMode(false);
		configuration.store();

		super.tearDown();

		// ============= release ==============
		ini = null;
		copyProject = null;
		this.CP = null;
		this.CS = null;
		configuration = null;
	}
	
	// case1: delete One good retrospective
	public void testDeleteRetrospectiveWith1g() throws Exception {
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe();	
		this.CR = new CreateRetrospective(1, 0, this.CP, this.CS);
		this.CR.exe();
		
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);		
		String sprintID = "1";
		String issueID = "1";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);		
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());	
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();	
    	
    	// 比對資料是否正確
    	String expected = genXML(sprintID, issueID);
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
    	this.CR.update();
    	assertEquals(0, this.CR.getGoodRetrospectiveCount());
    	assertEquals(0, this.CR.getImproveRetrospectiveCount());
	}
	
	// case2: delete One improvement retrospective
	public void testDeleteRetrospectiveWith1i() throws Exception {
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe();	
		this.CR = new CreateRetrospective(0, 1, this.CP, this.CS);
		this.CR.exe();
		
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);		
		String sprintID = "1";
		String issueID = "1";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);		
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());	
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();	
    	
    	// 比對資料是否正確
    	String expected = genXML(sprintID, issueID);
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
    	this.CR.update();
    	assertEquals(0, this.CR.getGoodRetrospectiveCount());
    	assertEquals(0, this.CR.getImproveRetrospectiveCount());
	}	

	// case3: delete One good & One improvement retrospective
	public void testDeleteRetrospectiveWith1g1i() throws Exception {
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe();	
		this.CR = new CreateRetrospective(1, 1, this.CP, this.CS);
		this.CR.exe();
		
		// (I) 先刪除good 
		
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);		
		String sprintID = "1";
		String issueID = "1";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);		
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());	
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();	
    	
    	// 比對資料是否正確
    	String expected = genXML(sprintID, issueID);
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
    	this.CR.update();
    	assertEquals(0, this.CR.getGoodRetrospectiveCount());
    	assertEquals(1, this.CR.getImproveRetrospectiveCount());
    	
		// (II) 再刪除improvement
    	
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
		
		// ================ set initial data =======================
		issueID = "2";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);		
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());	
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();	
    	
    	// 比對資料是否正確
    	expected = genXML(sprintID, issueID);
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
    	this.CR.update();
    	assertEquals(0, this.CR.getGoodRetrospectiveCount());
    	assertEquals(0, this.CR.getImproveRetrospectiveCount());    	
	}	
	
	// case4: delete One improvement & One good retrospective
	public void testDeleteRetrospectiveWith1i1g() throws Exception {
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe();	
		this.CR = new CreateRetrospective(1, 1, this.CP, this.CS);
		this.CR.exe();
		
		// (I) 先刪除improve
		
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);		
		String sprintID = "1";
		String issueID = "2";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);		
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());	
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();	
    	
    	// 比對資料是否正確
    	String expected = genXML(sprintID, issueID);
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
    	this.CR.update();
    	assertEquals(1, this.CR.getGoodRetrospectiveCount());
    	assertEquals(0, this.CR.getImproveRetrospectiveCount());
    	
		// (II) 再刪除good
    	
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
		
		// ================ set initial data =======================
		issueID = "1";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);		
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());	
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();	
    	
    	// 比對資料是否正確
    	expected = genXML(sprintID, issueID);
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
    	this.CR.update();
    	assertEquals(0, this.CR.getGoodRetrospectiveCount());
    	assertEquals(0, this.CR.getImproveRetrospectiveCount());    	
	}
	
	private String genXML(String sprintID, String issueID) {
 		StringBuilder result = new StringBuilder("");
		
		result.append("<DeleteRetrospective><Result>true</Result><Retrospective>");
		result.append("<Id>" + issueID + "</Id>");
		result.append("<SprintID>" + sprintID + "</SprintID>");
		result.append("</Retrospective></DeleteRetrospective>");	
		
		return result.toString();
	}
}
