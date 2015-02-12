package ntut.csie.ezScrum.web.action.retrospective;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IScrumIssue;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRetrospective;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class GetEditRetrospectiveInfoActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateRetrospective mCR;
	
	private Configuration mConfig;
	
	private String mActionPath = "/getEditRetrospectiveInfo";
	
	public GetEditRetrospectiveInfoActionTest(String testMethod) {
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

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent")); // 設定讀取的
		// struts-config
		// 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		projectManager = null;
		mCP = null;
		mCS = null;
		mCR = null;
		mConfig = null;
	}
	
	// case 1: One sprint with 1 Good
	public void testOneSprint1g() throws Exception {	
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint		
		
		mCR = new CreateRetrospective(1, 0, mCP, mCS);
		mCR.exe();
		
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		String issueID = "1";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);		
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());	
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();
  
    	// 比對資料是否正確
    	IScrumIssue issue = mCR.getGoodRetrospectiveList().get(0);
    	String expected = genXML(issue);
    	assertEquals(expected, response.getWriterBuffer().toString());	   	    	
	}			
	
	// case 2: One sprint with 1 Improvement
	public void testOneSprint1i() throws Exception {	
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint		
		
		mCR = new CreateRetrospective(0, 1, mCP, mCS);
		mCR.exe();
		
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		String issueID = "1";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);		
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());	
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();
  
    	// 比對資料是否正確
    	IScrumIssue issue = mCR.getImproveRetrospectiveList().get(0);
    	String expected = genXML(issue);
     	assertEquals(expected, response.getWriterBuffer().toString());	   	    	
	}	
	
	// case 3: One sprint with 1 Good + 1 Improvement
	public void testOneSprint1g1i() throws Exception {	
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint		
		
		mCR = new CreateRetrospective(1, 1, mCP, mCS);
		mCR.exe();
		
		// (I) 先取得good
		
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		String issueID = "1";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);		
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());	
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();
  
    	// 比對資料是否正確
    	IScrumIssue issue = mCR.getGoodRetrospectiveList().get(0);
    	String expected = genXML(issue);
    	assertEquals(expected, response.getWriterBuffer().toString());	
    	
		// (II) 再取得improvement

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
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());	
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();
  
    	// 比對資料是否正確
    	issue = mCR.getImproveRetrospectiveList().get(0);
    	expected = genXML(issue);
    	assertEquals(expected, response.getWriterBuffer().toString());	    	
	}	

	// case 4: One sprint with 1 Improvement + 1 Good
	public void testOneSprint1i1g() throws Exception {	
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint		
		
		mCR = new CreateRetrospective(1, 1, mCP, mCS);
		mCR.exe();
		
		// (I) 先取得improve
		
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		String issueID = "2";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);		
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());	
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();
  
    	// 比對資料是否正確
    	IScrumIssue issue = mCR.getImproveRetrospectiveList().get(0);
    	String expected = genXML(issue);
    	assertEquals(expected, response.getWriterBuffer().toString());	
    	
		// (II) 再取得improvement
    	
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
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());	
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();
  
    	// 比對資料是否正確
    	issue = mCR.getGoodRetrospectiveList().get(0);
    	expected = genXML(issue);
    	assertEquals(expected, response.getWriterBuffer().toString());	    	
	}	
	
	private String genXML(IIssue issue) {
 		StringBuilder result = new StringBuilder("");
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		
		result.append("<EditRetrospective><Result>true</Result><Retrospective>");
		result.append("<Id>" + issue.getIssueID() + "</Id>");
		result.append("<Link>" + "/ezScrum/showIssueInformation.do?issueID=" + issue.getIssueID() + "</Link>");		
		result.append("<SprintID>" + issue.getSprintID() + "</SprintID>");		
		result.append("<Name>" + tsc.TranslateXMLChar(issue.getSummary()) + "</Name>");		
		result.append("<Type>" + issue.getCategory() + "</Type>");
		result.append("<Description>" + tsc.TranslateXMLChar(issue.getDescription()) + "</Description>");
		result.append("<Status>" + issue.getStatus() + "</Status>");				
		result.append("</Retrospective></EditRetrospective>");	
		
		return result.toString();
	}
}
