package ntut.csie.ezScrum.web.action.unplanned;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplannedItem;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;


public class RemoveUnplannedItemActionTest extends MockStrutsTestCase {
	
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateUnplannedItem mCUI;
	
	private Configuration mConfig;
	
	private String mActionPath = "/removeUnplannedItem";
	
	public RemoveUnplannedItemActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		this.mCP = new CreateProject(1);
		this.mCP.exeCreate(); // 新增一測試專案

		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent")); // 設定讀取的
		// struts-config檔案路徑
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
		mCUI = null;
		mConfig = null;
	}
	
	// case 1: One sprint with 1 Unplanned item
	public void testOneSprint1ui() throws Exception {
		mCS = new CreateSprint(1, this.mCP);
		mCS.exe(); // 新增一個 Sprint

		mCUI = new CreateUnplannedItem(1, mCP, mCS);
		mCUI.exe(); // 新增一個UnplannedItem

		// ================ set initial data =======================
		IProject project = this.mCP.getProjectList().get(0);
		String issueID = "1";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(issueID);
		assertEquals(expected, response.getWriterBuffer().toString());
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(Long.parseLong(issueID), IssueTypeEnum.TYPE_UNPLANNED).size());
	}		
		
	// case 2: One sprint with 2 Unplanned item
	public void testOneSprint2ui() throws Exception {
		this.mCS = new CreateSprint(1, this.mCP);
		this.mCS.exe(); // 新增一個 Sprint

		this.mCUI = new CreateUnplannedItem(2, mCP, mCS);
		this.mCUI.exe(); // 新增兩個UnplannedItem

		// (I) ui 1
		
		// ================ set initial data =======================
		IProject project = this.mCP.getProjectList().get(0);
		String issueID = "1";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(issueID);
		assertEquals(expected, response.getWriterBuffer().toString());
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(Long.parseLong(issueID), IssueTypeEnum.TYPE_UNPLANNED).size());
		
		// (II) ui 2
		
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
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(issueID);
		assertEquals(expected, response.getWriterBuffer().toString());
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(Long.parseLong(issueID), IssueTypeEnum.TYPE_UNPLANNED).size());
	}			
	
	// case 3: Two sprint with 1 Unplanned item
	public void testTwoSprint1ui() throws Exception {
		this.mCS = new CreateSprint(2, this.mCP);
		this.mCS.exe(); // 新增一個 Sprint

		this.mCUI = new CreateUnplannedItem(1, mCP, mCS);
		this.mCUI.exe(); // 新增一個UnplannedItem

		// (I) Sprint 1
		
		// ================ set initial data =======================
		IProject project = this.mCP.getProjectList().get(0);
		String issueID = "1";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(issueID);
		assertEquals(expected, response.getWriterBuffer().toString());
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(Long.parseLong(issueID), IssueTypeEnum.TYPE_UNPLANNED).size());
		
		// (II) sprint 2
		
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
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(issueID);
		assertEquals(expected, response.getWriterBuffer().toString());	
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(Long.parseLong(issueID), IssueTypeEnum.TYPE_UNPLANNED).size());
	}

	// case 4: Two sprint with 2 Unplanned item
	public void testTwoSprint2ui() throws Exception {
		this.mCS = new CreateSprint(2, this.mCP);
		this.mCS.exe(); // 新增兩個 Sprint

		this.mCUI = new CreateUnplannedItem(2, mCP, mCS);
		this.mCUI.exe(); // 新增兩個UnplannedItem

		// (I) sprint1, ui 1
		
		// ================ set initial data =======================
		IProject project = this.mCP.getProjectList().get(0);
		String issueID = "1";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(issueID);
		assertEquals(expected, response.getWriterBuffer().toString());
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(Long.parseLong(issueID), IssueTypeEnum.TYPE_UNPLANNED).size());
		
		// (II) sprint1, ui 2
		
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
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(issueID);
		assertEquals(expected, response.getWriterBuffer().toString());
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(Long.parseLong(issueID), IssueTypeEnum.TYPE_UNPLANNED).size());
		
		// (III) sprint2, ui 1
		
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
		
		// ================ set initial data =======================
		issueID = "3";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(issueID);
		assertEquals(expected, response.getWriterBuffer().toString());
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(Long.parseLong(issueID), IssueTypeEnum.TYPE_UNPLANNED).size());
		
		// (IV) sprint2, ui 2
		
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
		
		// ================ set initial data =======================
		issueID = "4";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(issueID);
		assertEquals(expected, response.getWriterBuffer().toString());
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(Long.parseLong(issueID), IssueTypeEnum.TYPE_UNPLANNED).size());
	}	
	
	private String genXML(String issueID) {
 		StringBuilder result = new StringBuilder("");
		 		
		result.append("<DeleteUnplannedItem>");
		result.append("<Result>true</Result>");		
		//
		result.append("<UnplannedItem>");		
		result.append("<Id>" + issueID + "</Id>");
		result.append("</UnplannedItem>");	
		//
		result.append("</DeleteUnplannedItem>");	
		
		return result.toString();
	}	
}
