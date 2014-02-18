package ntut.csie.ezScrum.web.action.unplanned;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AddNewUnplannedItemActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private String actionPath = "/addNewUnplannedItem";
	
	private String prefix = "TEST_UNPLANNED_";
	private String testPartner = "TEST_PARTNER_";
	private String testEstimation = "99";	
	private String testNote = "TEST_NOTE_";
	
	public AddNewUnplannedItemActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe(); // 初始化 SQL

		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案
		
		super.setUp();

		setContextDirectory(new File(config.getBaseDirPath() + "/WebContent")); // 設定讀取的
		// struts-config 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(actionPath);

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe(); // 初始化 SQL

		//	刪除外部檔案
		CopyProject copyProject = new CopyProject(this.CP);
		copyProject.exeDelete_Project(); // 刪除測試檔案

		super.tearDown();

		// ============= release ==============
		ini = null;
		copyProject = null;
		this.CP = null;
		this.CS = null;
	}

	// case 1: One sprint with adding unplanned item
	public void testOneSprint1ui() throws Exception {	
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe(); // 新增一個 Sprint	
		
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		String sprintID = "1";
		String uName = this.prefix + "name";
		String uID = "1";
		String uHandler = "";
		String uPartners = "";
		String uEstimation ="0";
		String uNotes = "";
		String uSpecificTime = "";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", uName);
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("Estimate", uEstimation);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime",uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());	
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();
  
    	// 比對資料是否正確
    	String[] a = { sprintID, uID, uName, uHandler, uPartners, uEstimation, uNotes};    	
    	String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5], a[6]);
    	assertEquals(expected, response.getWriterBuffer().toString());   	    	
	}	
	
	// case 2: One sprint with adding 2 unplanned items
	public void testOneSprint2ui() throws Exception {	
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe(); // 新增一個 Sprint	
		
		// (I) test Sprint 1
		
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		String uID = "1";
		
		String uName = this.prefix + "name";
		String sprintID = "1";
		String uHandler = "";
		String uPartners = this.testPartner + uID;
		String uEstimation = this.testEstimation;
		String uNotes = this.testNote;
		String uSpecificTime = "";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", uName);
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("Estimate", uEstimation);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime",uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());	
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();
  
    	// 比對資料是否正確
    	String[] a = { sprintID, uID, uName, uHandler, uPartners, uEstimation, uNotes};    	
    	String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5], a[6]);
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
		// (II) test Sprint 2
		
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
		
		// ================ set initial data =======================
		project = this.CP.getProjectList().get(0);
		uID = "2";
		
		uName = this.prefix + "name";
		sprintID = "1";
		uHandler = "";
		uPartners = this.testPartner + uID;
		uEstimation = this.testEstimation;
		uNotes = this.testNote;
		uSpecificTime = "";		
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", uName);
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("Estimate", uEstimation);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime",uSpecificTime);		
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
    	String[] b = { sprintID, uID, uName, uHandler, uPartners, uEstimation, uNotes};    			
    	expected = genXML(b[0], b[1], b[2], b[3], b[4], b[5], b[6]);
		assertEquals(expected, response.getWriterBuffer().toString());	    	
	}	
	
	// case 3: Two sprint with adding unplanned item
	public void testTwoSprint1ui() throws Exception {
		this.CS = new CreateSprint(2, this.CP);
		this.CS.exe();

		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		String sprintID = "2";
		String uName = this.prefix + "name";
		String uID = "1";
		String uHandler = "";
		String uPartners = "";
		String uEstimation = "0";
		String uNotes = "";
		String uSpecificTime = "";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", uName);
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("Estimate", uEstimation);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime", uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String[] a = { sprintID, uID, uName, uHandler, uPartners, uEstimation, uNotes };
		String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5], a[6]);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 4: Two sprint with adding 2 unplanned items
	public void testTwoSprint2ui() throws Exception {	
		this.CS = new CreateSprint(2, this.CP);
		this.CS.exe();
		
		// (I) test Sprint 1
		
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		String uID = "1";
		
		String uName = this.prefix + "name";
		String sprintID = "1";
		String uHandler = "";
		String uPartners = this.testPartner + uID;
		String uEstimation = this.testEstimation;
		String uNotes = this.testNote;
		String uSpecificTime = "";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", uName);
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("Estimate", uEstimation);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime",uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());	
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();
  
    	// 比對資料是否正確
    	String[] a = { sprintID, uID, uName, uHandler, uPartners, uEstimation, uNotes};    	
    	String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5], a[6]);
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
		// (II) test Sprint 2
		
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
		
		// ================ set initial data =======================
		project = this.CP.getProjectList().get(0);
		uID = "2";
		
		uName = this.prefix + "name";
		sprintID = "1";
		uHandler = "";
		uPartners = this.testPartner + uID;
		uEstimation = this.testEstimation;
		uNotes = this.testNote;
		uSpecificTime = "";		
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", uName);
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("Estimate", uEstimation);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime",uSpecificTime);		
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
    	String[] b = { sprintID, uID, uName, uHandler, uPartners, uEstimation, uNotes};    			
    	expected = genXML(b[0], b[1], b[2], b[3], b[4], b[5], b[6]);
		assertEquals(expected, response.getWriterBuffer().toString());	    	
	}	
	
	private String genXML(String sprintID, String issueID, String name, String handler, String partners , String estimation, String notes) {
 		StringBuilder result = new StringBuilder("");
		
		result.append("<AddUnplannedItem>");
		result.append("<Result>success</Result>");	
		//
		result.append("<UnplannedItem>");		
		result.append("<Id>" + issueID + "</Id>");
		result.append("<Link>" + "/ezScrum/showIssueInformation.do?issueID=" + issueID + "</Link>");
		result.append("<Name>" + name + "</Name>");
		result.append("<SprintID>" + sprintID + "</SprintID>");
		result.append("<Estimate>" + estimation + "</Estimate>");
		result.append("<Status>" + "new" + "</Status>");
		result.append("<ActualHour>" + "0" + "</ActualHour>");
		result.append("<Handler>" + handler + "</Handler>");
		result.append("<Partners>" + partners + "</Partners>");
		result.append("<Notes>" + notes + "</Notes>");
		result.append("</UnplannedItem>");
		//
		result.append("</AddUnplannedItem>");	
		
		return result.toString();
	}
}
