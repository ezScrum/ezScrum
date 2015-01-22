package ntut.csie.ezScrum.web.action.retrospective;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRetrospective;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxEditRetrospectiveActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private CreateRetrospective CR;
	
	private Configuration configuration;
	
	private String prefix = "TEST_RETROSPECTIVE_EDIT_";
	private String[] rStatus = { "new", "closed", "resolved", "assigned" };
	private String actionPath = "/ajaxEditRetrospective";
	
	public AjaxEditRetrospectiveActionTest(String testMethod) {
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
		configuration.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		copyProject = null;
		this.CP = null;
		this.CS = null;
		configuration = null;
	}
	
	// case 1: One sprint with editing Good retrospective
	// 將  name, type, description, status 更新
	public void testEditGood() throws Exception {	
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe(); // 新增一個 Sprint				
		this.CR = new CreateRetrospective(1, 0, this.CP, this.CS);
		this.CR.exe();
		
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);		
		String sprintID = "1";
		String issueID = "1";
		String rName = this.prefix + "updateName";
		String rID = "1";
		String rType = ScrumEnum.IMPROVEMENTS_ISSUE_TYPE;
		String rDesc = this.prefix + "updateDescription";
		String rStatus = this.rStatus[1];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
		addRequestParameter("Status", rStatus);				
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
    	String[] a = { sprintID, rID, rName, rType, rDesc, rStatus };    	
    	String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5]);
    	assertEquals(expected, response.getWriterBuffer().toString());   	    	
	}		
					
	// case 2: One sprint with adding Improvement retrospective
	// 將  name, type, description, status 更新	
	public void testEditImprovement() throws Exception {	
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe(); // 新增一個 Sprint		
		this.CR = new CreateRetrospective(0, 1, this.CP, this.CS);
		this.CR.exe();		
		
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);		
		String sprintID = "1";
		String issueID = "1";
		String rName = this.prefix + "updateName";
		String rID = "1";
		String rType = ScrumEnum.GOOD_ISSUE_TYPE;
		String rDesc = this.prefix + "updateDescription";
		String rStatus = this.rStatus[1];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
		addRequestParameter("Status", rStatus);					
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
    	String[] a = { sprintID, rID, rName, rType, rDesc, rStatus };    	
    	String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5]);
    	assertEquals(expected, response.getWriterBuffer().toString());   	    	
	}	
	
	// case 3: One sprint with editing Good retrospective twice
	// 將  name, type, description, status 更新後再更新回原資料		
	public void testEditGood2() throws Exception {	
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe(); // 新增一個 Sprint				
		this.CR = new CreateRetrospective(1, 0, this.CP, this.CS);
		this.CR.exe();
		
		/*
		 * (I)
		 */
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);		
		String sprintID = "1";
		String issueID = "1";
		String rName = this.prefix + "updateName";
		String rID = "1";
		String rType = ScrumEnum.IMPROVEMENTS_ISSUE_TYPE;
		String rDesc = this.prefix + "updateDescription";
		String rStatus = this.rStatus[2];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
		addRequestParameter("Status", rStatus);				
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
  
    	/*
    	 * (II)
    	 */
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
    	
		// ================ set initial data =======================
		rName = this.prefix + "name";
		rID = "1";
		rType = ScrumEnum.GOOD_ISSUE_TYPE;
		rDesc = this.prefix + "description";
		rStatus = this.rStatus[0];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
		addRequestParameter("Status", rStatus);					
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
    	String[] a = { sprintID, rID, rName, rType, rDesc, rStatus };    	
    	String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5]);
    	assertEquals(expected, response.getWriterBuffer().toString());   	   
	}	
	
	// case 4: One sprint with editing Improvement retrospective twice
	// 將  name, type, description, status 更新後再更新回原資料	
	public void testEditImporvement2() throws Exception {	
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe(); // 新增一個 Sprint		
		this.CR = new CreateRetrospective(0, 1, this.CP, this.CS);
		this.CR.exe();		
		
		/*
		 * (I)
		 */
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);		
		String sprintID = "1";
		String issueID = "1";
		String rName = this.prefix + "updateName";
		String rID = "1";
		String rType = ScrumEnum.GOOD_ISSUE_TYPE;
		String rDesc = this.prefix + "updateDescription";
		String rStatus = this.rStatus[3];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
		addRequestParameter("Status", rStatus);					
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
  
    	/*
    	 * (II)
    	 */
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
    	
		// ================ set initial data =======================
		rName = this.prefix + "name";
		rID = "1";
		rType = ScrumEnum.IMPROVEMENTS_ISSUE_TYPE;
		rDesc = this.prefix + "description";
		rStatus = this.rStatus[0];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);		
		addRequestParameter("Status", rStatus);					
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
    	String[] a = { sprintID, rID, rName, rType, rDesc, rStatus };    	
    	String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5]);
    	assertEquals(expected, response.getWriterBuffer().toString());  
	}
	
	
	private String genXML(String sprintID, String issueID, String name, String type, String desc, String status) {
 		StringBuilder result = new StringBuilder("");
		
		result.append("<EditRetrospective><Result>true</Result><Retrospective>");
		result.append("<Id>" + issueID + "</Id>");
		result.append("<Link>" + "/ezScrum/showIssueInformation.do?issueID=" + issueID + "</Link>");
		result.append("<SprintID>" + sprintID + "</SprintID>");
		result.append("<Name>" + name + "</Name>");
		result.append("<Type>" + type + "</Type>");
		result.append("<Description>" + desc + "</Description>");
		result.append("<Status>" + status + "</Status>");
		result.append("</Retrospective></EditRetrospective>");	
		
		return result.toString();
	}
}
