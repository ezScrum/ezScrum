package ntut.csie.ezScrum.web.action.unplanned;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplannedItem;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class ShowUnplannedItemActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateUnplannedItem mCUI;
	private Configuration mConfig;
	private String mActionPath = "/GetUnplannedItems";

	// 測試用的假資料
	private String mTEST_NAME = "TEST_UNPLANNED_";
	private String mTEST_EST = "2";
	private String mTEST_LINK = "/ezScrum/showIssueInformation.do?issueID=";
	private String mTEST_HANDLER = "";
	private String mTEST_PARTNER = "";
	private String mTEST_NOTE = "TEST_UNPLANNED_NOTES_";

	public ShowUnplannedItemActionTest(String testMethod) {
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
		mCUI = null;
		mConfig = null;
	}

	// case 1: No sprint
	public void testNoSprint() throws Exception {
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("SprintID", Integer.toString(-1));
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

		// 比對資料是否正確(sprintID = -1)
		String expected = "<UnplannedItems><Sprint><Id>-1</Id><Name>Sprint -1</Name></Sprint></UnplannedItems>";
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 2: One sprint with No UnplannedItem
	public void testOneSprint() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint

		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("SprintID", Integer.toString(1)); // SprintID從1開始
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確(sprintID = 1)
		String expected = "<UnplannedItems><Sprint><Id>1</Id><Name>Sprint 1</Name></Sprint></UnplannedItems>";
		assertEquals(expected, response.getWriterBuffer().toString());
	}
	
	// case 3: One sprint with One UnplannedItem
	public void testOneSprint1ui() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint
		
		mCUI = new CreateUnplannedItem(1, mCP, mCS);
		mCUI.exe(); // 新增一個UnplannedItem

		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("SprintID", Integer.toString(1)); // SprintID從1開始
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML("1", 1, 1);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 4: One sprint with Two UnplannedItem
	public void testOneSprint2ui() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint
		
		mCUI = new CreateUnplannedItem(2, mCP, mCS);
		mCUI.exe(); // 新增兩個UnplannedItem

		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("SprintID", Integer.toString(1)); // SprintID從1開始
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML("1", 1, 2);
		assertEquals(expected, response.getWriterBuffer().toString());
	}	

	// case 5: Two sprint with One UnplannedItem
	public void testTwoSprint1ui() throws Exception {
		mCS = new CreateSprint(2, mCP);
		mCS.exe(); // 新增一個 Sprint
		
		mCUI = new CreateUnplannedItem(1, mCP, mCS);
		mCUI.exe(); // 新增一個UnplannedItem

		// (I) test Sprint 1
		
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("SprintID", Integer.toString(1)); // SprintID從1開始
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML("1", 1, 1);
		assertEquals(expected, response.getWriterBuffer().toString());
		
		// (II) test Sprint 2
		
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
		
		// ================ set initial data =======================
		project = mCP.getProjectList().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("SprintID", Integer.toString(2));
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML("2", 2, 1);
		assertEquals(expected, response.getWriterBuffer().toString());		
	}
	
	// case 6: Two sprint with Two UnplannedItem
	public void testTwoSprint2ui() throws Exception {
		mCS = new CreateSprint(2, mCP);
		mCS.exe(); // 新增一個 Sprint
		
		mCUI = new CreateUnplannedItem(2, mCP, mCS);
		mCUI.exe(); // 新增一個UnplannedItem

		// (I) test Sprint 1
		
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("SprintID", Integer.toString(1)); // SprintID從1開始
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML("1", 1, 2);
		assertEquals(expected, response.getWriterBuffer().toString());
		
		// (II) test Sprint 2
		
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
		
		// ================ set initial data =======================
		project = mCP.getProjectList().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("SprintID", Integer.toString(2));
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML("2", 3, 2);
		assertEquals(expected, response.getWriterBuffer().toString());		
	}
	
	// 產生某一個sprint下的所有unplanned item(s)
	private String genXML(String sprintID, int startIssueID, int itemCount) {
 		StringBuilder result = new StringBuilder("");
    	TranslateSpecialChar tsc = new TranslateSpecialChar();				
		
		result.append("<UnplannedItems>");
 		// sprint
		result.append("<Sprint>");	 		
		result.append("<Id>" + sprintID + "</Id>");
		result.append("<Name>" + "Sprint " + sprintID + "</Name>");
		result.append("</Sprint>");			
 		// unplanned item
		for(int i = 0; i < itemCount; i++)
		{		
			result.append("<UnplannedItem>");	 		
			result.append("<Id>" + String.valueOf(startIssueID+i) + "</Id>");
			result.append("<Link>" + tsc.TranslateXMLChar(mTEST_LINK + String.valueOf(startIssueID+i)) + "</Link>");		
			result.append("<Name>" + "p1s" + sprintID + "_" + mTEST_NAME + String.valueOf(i+1) + "</Name>");
			result.append("<SprintID>" + sprintID + "</SprintID>");
			result.append("<Estimate>" + mTEST_EST + "</Estimate>");
			result.append("<Status>" + "new" + "</Status>");
			result.append("<ActualHour>" + "0" + "</ActualHour>");
			result.append("<Handler>" + mTEST_HANDLER + "</Handler>");
			result.append("<Partners>" + mTEST_PARTNER + "</Partners>");	
			result.append("<Notes>" + mTEST_NOTE + String.valueOf(i+1) + "</Notes>");			
		result.append("</UnplannedItem>");	
		}		
		//
		result.append("</UnplannedItems>");

		return result.toString();
	}

}
