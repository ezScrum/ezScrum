package ntut.csie.ezScrum.web.action.retrospective;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

public class ShowRetrospectiveActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateRetrospective mCR;
	private Configuration mConfig;
	
	public ShowRetrospectiveActionTest(String testMethod) {
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
		setRequestPathInfo("/showRetrospective2");

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

	// case 1: no sprint
	public void testNoSprint() throws Exception {
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("sprintID", Integer.toString(-1)); // 取得第一筆 SprintPlan
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
  
    	// 比對資料是否正確(sprintID = -1)
    	String expected = "<Retrospectives><Sprint><Id>-1</Id><Name>Sprint #-1</Name></Sprint></Retrospectives>";
//    	System.out.println("response: " + response.getWriterBuffer().toString());
    	assertEquals(expected, response.getWriterBuffer().toString());   	    	
	}	
	
	// case 2: One sprint with no retrospective
	public void testOneSprint() throws Exception {	
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint		
		
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		// 測試不代入 sprint ID
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
  
    	// 比對資料是否正確(sprintID = -1)
    	String expected = "<Retrospectives><Sprint><Id>1</Id><Name>Sprint #1</Name></Sprint></Retrospectives>";
//    	System.out.println("response: " + response.getWriterBuffer().toString());
    	assertEquals(expected, response.getWriterBuffer().toString());   	    	
	}		
	
	// case 3: One sprint with 1 Good
	public void testOneSprint1g() throws Exception {	
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint		
		
		mCR = new CreateRetrospective(1, 0, mCP, mCS);
		mCR.exe();
		
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		long sprintID = mCS.getSprintsId().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("sprintID", String.valueOf(sprintID)); // 取得第一筆 SprintPlan
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
    	String expected = genXML(String.valueOf(sprintID));
    	assertEquals(expected, response.getWriterBuffer().toString());	   	    	
	}			
	
	// case 4: One sprint with 1 Improvement
	public void testOneSprint1i() throws Exception {	
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint		
		
		mCR = new CreateRetrospective(0, 1, mCP, mCS);
		mCR.exe();
		
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		long sprintID = mCS.getSprintsId().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("sprintID", String.valueOf(sprintID)); // 取得第一筆 SprintPlan
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
    	String expected = genXML(String.valueOf(sprintID));
     	assertEquals(expected, response.getWriterBuffer().toString());	   	    	
	}	
	
	// case 5: One sprint with 1 Good + 1 Improvement
	public void testOneSprint1g1i() throws Exception {	
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint		
		
		mCR = new CreateRetrospective(1, 1, mCP, mCS);
		mCR.exe();
		
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		long sprintID = mCS.getSprintsId().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("sprintID", String.valueOf(sprintID)); // 取得第一筆 SprintPlan
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
    	String expected = genXML(String.valueOf(sprintID));
    	assertEquals(expected, response.getWriterBuffer().toString());	   	    	
	}				
	
	private String genXML(String sprintID) {
    	TranslateSpecialChar tsc = new TranslateSpecialChar();				
		StringBuilder sb = new StringBuilder();
		sb.append("<Retrospectives><Sprint><Id>" + sprintID + "</Id><Name>Sprint #" + sprintID + "</Name></Sprint>");
		
		// good
    	List<IScrumIssue> goodRes = mCR.getGoodRetrospectiveList();		
		for(int i = 0; i < goodRes.size(); i++){			
			IScrumIssue goodR = goodRes.get(i);
			if (goodR.getSprintID().compareTo(sprintID) == 0) {
				sb.append("<Retrospective>");
				sb.append("<Id>" + goodR.getIssueID() + "</Id>");
				sb.append("<Link>" + tsc.TranslateXMLChar(goodR.getIssueLink()) + "</Link>");
				sb.append("<SprintID>" + goodR.getSprintID() + "</SprintID>");
				sb.append("<Name>" + tsc.TranslateXMLChar(goodR.getName()) + "</Name>");
				sb.append("<Type>" + goodR.getCategory() + "</Type>");
				sb.append("<Description>" + tsc.TranslateXMLChar(goodR.getDescription()) + "</Description>");
				sb.append("<Status>" + goodR.getStatus() + "</Status>");
				sb.append("</Retrospective>");
			}
		}		
		
		// improve
		List<IScrumIssue> improveRes = mCR.getImproveRetrospectiveList();		
		for(int i = 0; i < improveRes.size(); i++){
			IScrumIssue improveR = improveRes.get(i);
			if (improveR.getSprintID().compareTo(sprintID) == 0) {
				sb.append("<Retrospective>");
				sb.append("<Id>" + improveR.getIssueID() + "</Id>");
				sb.append("<Link>" + tsc.TranslateXMLChar(improveR.getIssueLink()) + "</Link>");
				sb.append("<SprintID>" + improveR.getSprintID() + "</SprintID>");
				sb.append("<Name>" + tsc.TranslateXMLChar(improveR.getName()) + "</Name>");
				sb.append("<Type>" + improveR.getCategory() + "</Type>");
				sb.append("<Description>" + tsc.TranslateXMLChar(improveR.getDescription()) + "</Description>");
				sb.append("<Status>" + improveR.getStatus() + "</Status>");
				sb.append("</Retrospective>");
			}
		}
		
		sb.append("</Retrospectives>");
		return sb.toString();
	}
}
