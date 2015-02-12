package ntut.csie.ezScrum.web.action.retrospective;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxAddNewRetrospectiveActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private String mPrefix = "TEST_RETROSPECTIVE_";
	private String mActionPath = "/ajaxAddNewRetrospective";
	
	public AjaxAddNewRetrospectiveActionTest(String testMethod) {
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
		mConfig = null;
	}
	
	// case 1: One sprint with adding Good retrospective
	public void testAddGood() throws Exception {	
		this.mCS = new CreateSprint(1, this.mCP);
		this.mCS.exe(); // 新增一個 Sprint		
		
		// ================ set initial data =======================
		IProject project = this.mCP.getProjectList().get(0);
		String sprintID = "1";
		String rName = mPrefix + "name";
		String rID = "1";
		String rType = ScrumEnum.GOOD_ISSUE_TYPE;
		String rDesc = mPrefix + "description";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
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
    	String[] a = { sprintID, rID, rName, rType, rDesc };    	
    	String expected = genXML(a[0], a[1], a[2], a[3], a[4]);
//    	System.out.println("response: " + response.getWriterBuffer().toString());
    	assertEquals(expected, response.getWriterBuffer().toString());   	    	
	}		
					
	// case 2: One sprint with adding Improvement retrospective
	public void testAddImprovement() throws Exception {	
		this.mCS = new CreateSprint(1, this.mCP);
		this.mCS.exe(); // 新增一個 Sprint		
		
		// ================ set initial data =======================
		IProject project = this.mCP.getProjectList().get(0);
		String sprintID = "1";
		String rName = mPrefix + "name";
		String rID = "1";
		String rType = ScrumEnum.IMPROVEMENTS_ISSUE_TYPE;
		String rDesc = mPrefix + "description";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
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
    	String[] a = { sprintID, rID, rName, rType, rDesc };    	
    	String expected = genXML(a[0], a[1], a[2], a[3], a[4]);
//    	System.out.println("response: " + response.getWriterBuffer().toString());
    	assertEquals(expected, response.getWriterBuffer().toString());   	    	
	}	
	
	// case 3: One sprint with adding Improvement & Good retrospective
	public void testAddImporveAndGood() throws Exception {	
		this.mCS = new CreateSprint(1, this.mCP);
		this.mCS.exe(); // 新增一個 Sprint
		
		/*
		 * (I) add Improvement
		 */
		// ================ set initial data =======================
		IProject project = this.mCP.getProjectList().get(0);
		String sprintID = "1";
		String rName = mPrefix + "name";
		String rID = "1";
		String rType = ScrumEnum.IMPROVEMENTS_ISSUE_TYPE;
		String rDesc = mPrefix + "description";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
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
    	String[] a = { sprintID, rID, rName, rType, rDesc };    	
    	String expected = genXML(a[0], a[1], a[2], a[3], a[4]);
//    	System.out.println("response: " + response.getWriterBuffer().toString());
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
    	
    	/*
    	 * (II) add Good
    	 */    	    	
    	
		// ================ set initial data =======================
		rID = "2";
		rType = ScrumEnum.GOOD_ISSUE_TYPE;
		rDesc = mPrefix + "description";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
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
    	expected = genXML(sprintID, rID, rName, rType, rDesc);
//    	System.out.println("response: " + response.getWriterBuffer().toString());
    	assertEquals(expected, response.getWriterBuffer().toString());     
	}	
	
	// case 4: One sprint with adding Good & Improvement retrospective
	public void testAddGoodAndImporve() throws Exception {	
		this.mCS = new CreateSprint(1, this.mCP);
		this.mCS.exe(); // 新增一個 Sprint
		
		/*
		 * (I) add Good
		 */
		// ================ set initial data =======================
		IProject project = this.mCP.getProjectList().get(0);
		String sprintID = "1";
		String rName = mPrefix + "name";
		String rID = "1";
		String rType = ScrumEnum.GOOD_ISSUE_TYPE;
		String rDesc = mPrefix + "description";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
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
    	String[] a = { sprintID, rID, rName, rType, rDesc };    	
    	String expected = genXML(a[0], a[1], a[2], a[3], a[4]);
//    	System.out.println("response: " + response.getWriterBuffer().toString());
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
    	
    	/*
    	 * (II) add Improvement
    	 */    	    	
    	
		// ================ set initial data =======================
		rID = "2";
		rType = ScrumEnum.IMPROVEMENTS_ISSUE_TYPE;
		rDesc = mPrefix + "description";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
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
    	expected = genXML(sprintID, rID, rName, rType, rDesc);
//    	System.out.println("response: " + response.getWriterBuffer().toString());
    	assertEquals(expected, response.getWriterBuffer().toString());     
	}
	
	
	
	private String genXML(String sprintID, String issueID, String name, String type, String desc) {
 		StringBuilder result = new StringBuilder("");
		
		result.append("<AddNewRetrospective><Result>true</Result><Retrospective>");
		result.append("<Id>" + issueID + "</Id>");
		result.append("<Link>" + "/ezScrum/showIssueInformation.do?issueID=" + issueID + "</Link>");
		result.append("<SprintID>" + sprintID + "</SprintID>");
		result.append("<Name>" + name + "</Name>");
		result.append("<Type>" + type + "</Type>");
		result.append("<Description>" + desc + "</Description>");
		result.append("<Status>" + "new" + "</Status>");
		result.append("</Retrospective></AddNewRetrospective>");	
		
		return result.toString();
	}
}
