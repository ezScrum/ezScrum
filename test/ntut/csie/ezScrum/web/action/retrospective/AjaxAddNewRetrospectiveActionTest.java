package ntut.csie.ezScrum.web.action.retrospective;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxAddNewRetrospectiveActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private Configuration configuration;
	private String prefix = "TEST_RETROSPECTIVE_";
	private String actionPath = "/ajaxAddNewRetrospective";
	
	public AjaxAddNewRetrospectiveActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案

		super.setUp();

		setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent")); // 設定讀取的
		// struts-config
		// 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(actionPath);

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		CopyProject copyProject = new CopyProject(this.CP);
		copyProject.exeDelete_Project(); // 刪除測試檔案
		
		configuration.setTestMode(false);
		configuration.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		copyProject = null;
		this.CP = null;
		this.CS = null;
		configuration = null;
	}
	
	// case 1: One sprint with adding Good retrospective
	public void testAddGood() throws Exception {	
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe(); // 新增一個 Sprint		
		
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		String sprintID = "1";
		String rName = prefix + "name";
		String rID = "1";
		String rType = ScrumEnum.GOOD_ISSUE_TYPE;
		String rDesc = prefix + "description";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
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
    	String[] a = { sprintID, rID, rName, rType, rDesc };    	
    	String expected = genXML(a[0], a[1], a[2], a[3], a[4]);
//    	System.out.println("response: " + response.getWriterBuffer().toString());
    	assertEquals(expected, response.getWriterBuffer().toString());   	    	
	}		
					
	// case 2: One sprint with adding Improvement retrospective
	public void testAddImprovement() throws Exception {	
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe(); // 新增一個 Sprint		
		
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		String sprintID = "1";
		String rName = prefix + "name";
		String rID = "1";
		String rType = ScrumEnum.IMPROVEMENTS_ISSUE_TYPE;
		String rDesc = prefix + "description";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
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
    	String[] a = { sprintID, rID, rName, rType, rDesc };    	
    	String expected = genXML(a[0], a[1], a[2], a[3], a[4]);
//    	System.out.println("response: " + response.getWriterBuffer().toString());
    	assertEquals(expected, response.getWriterBuffer().toString());   	    	
	}	
	
	// case 3: One sprint with adding Improvement & Good retrospective
	public void testAddImporveAndGood() throws Exception {	
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe(); // 新增一個 Sprint
		
		/*
		 * (I) add Improvement
		 */
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		String sprintID = "1";
		String rName = prefix + "name";
		String rID = "1";
		String rType = ScrumEnum.IMPROVEMENTS_ISSUE_TYPE;
		String rDesc = prefix + "description";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
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
		rDesc = prefix + "description";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
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
    	expected = genXML(sprintID, rID, rName, rType, rDesc);
//    	System.out.println("response: " + response.getWriterBuffer().toString());
    	assertEquals(expected, response.getWriterBuffer().toString());     
	}	
	
	// case 4: One sprint with adding Good & Improvement retrospective
	public void testAddGoodAndImporve() throws Exception {	
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe(); // 新增一個 Sprint
		
		/*
		 * (I) add Good
		 */
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		String sprintID = "1";
		String rName = prefix + "name";
		String rID = "1";
		String rType = ScrumEnum.GOOD_ISSUE_TYPE;
		String rDesc = prefix + "description";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
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
		rDesc = prefix + "description";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
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
