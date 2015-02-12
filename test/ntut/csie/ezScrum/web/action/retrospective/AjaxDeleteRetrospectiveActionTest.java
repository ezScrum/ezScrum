package ntut.csie.ezScrum.web.action.retrospective;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRetrospective;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxDeleteRetrospectiveActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateRetrospective mCR;
	
	private Configuration mConfig;
	
	private String mActionPath = "/ajaxDeleteRetrospective";
	
	public AjaxDeleteRetrospectiveActionTest(String testMethod) {
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
	
	// case1: delete One good retrospective
	public void testDeleteRetrospectiveWith1g() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe();	
		mCR = new CreateRetrospective(1, 0, mCP, mCS);
		mCR.exe();
		
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);		
		String sprintID = "1";
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
    	String expected = genXML(sprintID, issueID);
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
    	mCR.update();
    	assertEquals(0, mCR.getGoodRetrospectiveCount());
    	assertEquals(0, mCR.getImproveRetrospectiveCount());
    	assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(Long.parseLong(issueID), IssueTypeEnum.TYPE_RETROSPECTIVE).size());
	}
	
	// case2: delete One improvement retrospective
	public void testDeleteRetrospectiveWith1i() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe();	
		mCR = new CreateRetrospective(0, 1, mCP, mCS);
		mCR.exe();
		
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);		
		String sprintID = "1";
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
    	String expected = genXML(sprintID, issueID);
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
    	mCR.update();
    	assertEquals(0, mCR.getGoodRetrospectiveCount());
    	assertEquals(0, mCR.getImproveRetrospectiveCount());
    	assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(Long.parseLong(issueID), IssueTypeEnum.TYPE_RETROSPECTIVE).size());
	}	

	// case3: delete One good & One improvement retrospective
	public void testDeleteRetrospectiveWith1g1i() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe();	
		mCR = new CreateRetrospective(1, 1, mCP, mCS);
		mCR.exe();
		
		// (I) 先刪除good 
		
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);		
		String sprintID = "1";
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
    	String expected = genXML(sprintID, issueID);
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
    	mCR.update();
    	assertEquals(0, mCR.getGoodRetrospectiveCount());
    	assertEquals(1, mCR.getImproveRetrospectiveCount());
    	assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(Long.parseLong(issueID), IssueTypeEnum.TYPE_RETROSPECTIVE).size());
    	
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
    	expected = genXML(sprintID, issueID);
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
    	mCR.update();
    	assertEquals(0, mCR.getGoodRetrospectiveCount());
    	assertEquals(0, mCR.getImproveRetrospectiveCount());
    	assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(Long.parseLong(issueID), IssueTypeEnum.TYPE_RETROSPECTIVE).size());
	}	
	
	// case4: delete One improvement & One good retrospective
	public void testDeleteRetrospectiveWith1i1g() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe();	
		mCR = new CreateRetrospective(1, 1, mCP, mCS);
		mCR.exe();
		
		// (I) 先刪除improve
		
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);		
		String sprintID = "1";
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
    	String expected = genXML(sprintID, issueID);
    	assertEquals(expected, response.getWriterBuffer().toString());
    	assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(Long.parseLong(issueID), IssueTypeEnum.TYPE_RETROSPECTIVE).size());
    	
    	mCR.update();
    	assertEquals(1, mCR.getGoodRetrospectiveCount());
    	assertEquals(0, mCR.getImproveRetrospectiveCount());
    	
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
    	expected = genXML(sprintID, issueID);
    	assertEquals(expected, response.getWriterBuffer().toString());
    	assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(Long.parseLong(issueID), IssueTypeEnum.TYPE_RETROSPECTIVE).size());
    	
    	mCR.update();
    	assertEquals(0, mCR.getGoodRetrospectiveCount());
    	assertEquals(0, mCR.getImproveRetrospectiveCount());    	
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
