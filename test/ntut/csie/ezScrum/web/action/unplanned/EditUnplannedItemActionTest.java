package ntut.csie.ezScrum.web.action.unplanned;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplannedItem;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class EditUnplannedItemActionTest extends MockStrutsTestCase{
	private CreateProject CP;
	private CreateSprint CS;
	private CreateUnplannedItem CU;
	
	private Configuration configuration;
	
	private String PREFIX = "TEST_UNPLANNED_EDIT_";
	private String UPDATE_NAME = "NAME_";
	private String UPDATE_ESTIMATION = "99";
	private String UPDATE_HOUR = "9";
	private String UPDATE_PARTNER = "PARTNER_";
	private String UPDATE_NOTE = "NOTE_";
	private int UPDATE_WAITTIME_AFTER_ADD = 1;
	private String[] UPDATE_STATUS = { "new", "assigned", "closed" };
	
	private String actionPath = "/editUnplannedItem";
	
	public EditUnplannedItemActionTest(String testMethod) {
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
	
	// case 1: One sprint(s) with One Unplanned item(s)
	// 將  name, partners, status, estimation, actual hour, notes 更新
	public void testEditOneSprint1ui() throws Exception {	
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe(); // 新增一個 Sprint
		this.CU = new CreateUnplannedItem(1, CP, CS);
		this.CU.exe();

		// 若新增跟編輯unplanned item的秒數一致,會造成從history抓取最新值estimation與note錯誤
        try  {
            TimeUnit.SECONDS.sleep( this.UPDATE_WAITTIME_AFTER_ADD );
        }  catch  (InterruptedException e) {
            e.printStackTrace();
        }		
		
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		String sprintID = "1";
		String issueID = "1";
		String uName = this.PREFIX + this.UPDATE_NAME + issueID;
		String uHandler = "";	// 必須該專案有使用者才能測試
		String uPartners = this.PREFIX + this.UPDATE_PARTNER + issueID;
		String uEstimation = this.UPDATE_ESTIMATION;
		String uActualHour = this.UPDATE_HOUR;
		String uNotes = this.PREFIX + this.UPDATE_NOTE + issueID;
		String uSpecificTime = "";
		String uStatus = this.UPDATE_STATUS[1];
		// ================ set initial data =======================

		// ================== set parameter info ====================		
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", uName);
		addRequestParameter("Status", uStatus);	
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Estimate", uEstimation);		
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("ActualHour", uActualHour);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime", uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String[] a = { sprintID, issueID, uName, uHandler, uPartners, uEstimation, uNotes, uActualHour, uStatus };
		String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]);
		assertEquals(expected, response.getWriterBuffer().toString());
	}
		
	// case 2: One sprint(s) with Two Unplanned item(s)
	//  測試先修改UI2再修改UI1
	public void testEditOneSprint2ui() throws Exception {		
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe(); // 新增一個 Sprint
		this.CU = new CreateUnplannedItem(2, CP, CS);
		this.CU.exe();

		// 若新增跟編輯unplanned item的秒數一致,會造成從history抓取最新值estimation與note錯誤
        try  {
            TimeUnit.SECONDS.sleep( this.UPDATE_WAITTIME_AFTER_ADD );
        }  catch  (InterruptedException e) {
            e.printStackTrace();
        }		
		
        // (I) test update UI 2
        
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		String sprintID = "1";
		String issueID = "2";
		String uName = this.PREFIX + this.UPDATE_NAME + issueID;
		String uHandler = "";	// 必須該專案有使用者才能測試
		String uPartners = this.PREFIX + this.UPDATE_PARTNER + issueID;
		String uEstimation = this.UPDATE_ESTIMATION;
		String uActualHour = this.UPDATE_HOUR;
		String uNotes = this.PREFIX + this.UPDATE_NOTE + issueID;
		String uSpecificTime = "";
		String uStatus = this.UPDATE_STATUS[1];
		// ================ set initial data =======================

		// ================== set parameter info ====================		
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", uName);
		addRequestParameter("Status", uStatus);	
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Estimate", uEstimation);		
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("ActualHour", uActualHour);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime", uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String[] a = { sprintID, issueID, uName, uHandler, uPartners, uEstimation, uNotes, uActualHour, uStatus };
		String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]);
		assertEquals(expected, response.getWriterBuffer().toString());
		
        // (II) test update UI 1
		
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
        
		// ================ set initial data =======================
		sprintID = "1";
		issueID = "2";
		uName = this.PREFIX + this.UPDATE_NAME + issueID;
		uHandler = "";	// 必須該專案有使用者才能測試
		uPartners = this.PREFIX + this.UPDATE_PARTNER + issueID;
		uEstimation = this.UPDATE_ESTIMATION;
		uActualHour = this.UPDATE_HOUR;
		uNotes = this.PREFIX + this.UPDATE_NOTE + issueID;
		uSpecificTime = "";
		uStatus = this.UPDATE_STATUS[2];
		// ================ set initial data =======================

		// ================== set parameter info ====================		
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", uName);
		addRequestParameter("Status", uStatus);	
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Estimate", uEstimation);		
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("ActualHour", uActualHour);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime", uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String[] b = { sprintID, issueID, uName, uHandler, uPartners, uEstimation, uNotes, uActualHour, uStatus };
		expected = genXML(b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8]);
		assertEquals(expected, response.getWriterBuffer().toString());		
	}	
	
	// case 3: Two sprint(s) with One Unplanned item(s)
	//  測試先修改sprint2 再修改sprint1
	public void testEditTwoSprint1ui() throws Exception {		
		this.CS = new CreateSprint(2, this.CP);
		this.CS.exe(); // 新增一個 Sprint
		this.CU = new CreateUnplannedItem(1, CP, CS);
		this.CU.exe();

		// 若新增跟編輯unplanned item的秒數一致,會造成從history抓取最新值estimation與note錯誤
        try  {
            TimeUnit.SECONDS.sleep( this.UPDATE_WAITTIME_AFTER_ADD );
        }  catch  (InterruptedException e) {
            e.printStackTrace();
        }		
		
        // (I) test update sprint2 UI 
        
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		String sprintID = "2";
		String issueID = "2";
		String uName = this.PREFIX + this.UPDATE_NAME + issueID;
		String uHandler = "";	// 必須該專案有使用者才能測試
		String uPartners = this.PREFIX + this.UPDATE_PARTNER + issueID;
		String uEstimation = this.UPDATE_ESTIMATION;
		String uActualHour = this.UPDATE_HOUR;
		String uNotes = this.PREFIX + this.UPDATE_NOTE + issueID;
		String uSpecificTime = "";
		String uStatus = this.UPDATE_STATUS[1];
		// ================ set initial data =======================

		// ================== set parameter info ====================		
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", uName);
		addRequestParameter("Status", uStatus);	
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Estimate", uEstimation);		
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("ActualHour", uActualHour);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime", uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String[] a = { sprintID, issueID, uName, uHandler, uPartners, uEstimation, uNotes, uActualHour, uStatus };
		String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]);
		assertEquals(expected, response.getWriterBuffer().toString());
		
        // (II) test update sprint1 UI
		
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
        
		// ================ set initial data =======================
		sprintID = "1";
		issueID = "1";
		uName = this.PREFIX + this.UPDATE_NAME + issueID;
		uHandler = "";	// 必須該專案有使用者才能測試
		uPartners = this.PREFIX + this.UPDATE_PARTNER + issueID;
		uEstimation = this.UPDATE_ESTIMATION;
		uActualHour = this.UPDATE_HOUR;
		uNotes = this.PREFIX + this.UPDATE_NOTE + issueID;
		uSpecificTime = "";
		uStatus = this.UPDATE_STATUS[2];
		// ================ set initial data =======================

		// ================== set parameter info ====================		
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", uName);
		addRequestParameter("Status", uStatus);	
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Estimate", uEstimation);		
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("ActualHour", uActualHour);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime", uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String[] b = { sprintID, issueID, uName, uHandler, uPartners, uEstimation, uNotes, uActualHour, uStatus };
		expected = genXML(b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8]);
		assertEquals(expected, response.getWriterBuffer().toString());		
	}		
	
	// case 4: Two sprint(s) with Two Unplanned item(s)
	//  測試先修改sprint1 UI1.UI2 再修改sprint2 UI2.UI1
	public void testEditTwoSprint2ui() throws Exception {		
		this.CS = new CreateSprint(2, this.CP);
		this.CS.exe(); // 新增一個 Sprint
		this.CU = new CreateUnplannedItem(2, CP, CS);
		this.CU.exe();

		// 若新增跟編輯unplanned item的秒數一致,會造成從history抓取最新值estimation與note錯誤
        try  {
            TimeUnit.SECONDS.sleep( this.UPDATE_WAITTIME_AFTER_ADD );
        }  catch  (InterruptedException e) {
            e.printStackTrace();
        }		
		
        // (I) test update sprint1 UI1 
        
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		String sprintID = "1";
		String issueID = "1";
		String uName = this.PREFIX + this.UPDATE_NAME + issueID;
		String uHandler = "";	// 必須該專案有使用者才能測試
		String uPartners = this.PREFIX + this.UPDATE_PARTNER + issueID;
		String uEstimation = this.UPDATE_ESTIMATION;
		String uActualHour = this.UPDATE_HOUR;
		String uNotes = this.PREFIX + this.UPDATE_NOTE + issueID;
		String uSpecificTime = "";
		String uStatus = this.UPDATE_STATUS[1];
		// ================ set initial data =======================

		// ================== set parameter info ====================		
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", uName);
		addRequestParameter("Status", uStatus);	
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Estimate", uEstimation);		
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("ActualHour", uActualHour);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime", uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String[] a = { sprintID, issueID, uName, uHandler, uPartners, uEstimation, uNotes, uActualHour, uStatus };
		String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]);
		assertEquals(expected, response.getWriterBuffer().toString());
		
        // (II) test update sprint1 UI2
		
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
        
		// ================ set initial data =======================
		sprintID = "1";
		issueID = "2";
		uName = this.PREFIX + this.UPDATE_NAME + issueID;
		uHandler = "";	// 必須該專案有使用者才能測試
		uPartners = this.PREFIX + this.UPDATE_PARTNER + issueID;
		uEstimation = this.UPDATE_ESTIMATION;
		uActualHour = this.UPDATE_HOUR;
		uNotes = this.PREFIX + this.UPDATE_NOTE + issueID;
		uSpecificTime = "";
		uStatus = this.UPDATE_STATUS[2];
		// ================ set initial data =======================

		// ================== set parameter info ====================		
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", uName);
		addRequestParameter("Status", uStatus);	
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Estimate", uEstimation);		
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("ActualHour", uActualHour);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime", uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String[] b = { sprintID, issueID, uName, uHandler, uPartners, uEstimation, uNotes, uActualHour, uStatus };
		expected = genXML(b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8]);
		assertEquals(expected, response.getWriterBuffer().toString());	
		
        // (III) test update sprint2 UI2
		
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
        
		// ================ set initial data =======================
		sprintID = "2";
		issueID = "4";
		uName = this.PREFIX + this.UPDATE_NAME + issueID;
		uHandler = "";	// 必須該專案有使用者才能測試
		uPartners = this.PREFIX + this.UPDATE_PARTNER + issueID;
		uEstimation = this.UPDATE_ESTIMATION;
		uActualHour = this.UPDATE_HOUR;
		uNotes = this.PREFIX + this.UPDATE_NOTE + issueID;
		uSpecificTime = "";
		uStatus = this.UPDATE_STATUS[2];
		// ================ set initial data =======================

		// ================== set parameter info ====================		
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", uName);
		addRequestParameter("Status", uStatus);	
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Estimate", uEstimation);		
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("ActualHour", uActualHour);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime", uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String[] c = { sprintID, issueID, uName, uHandler, uPartners, uEstimation, uNotes, uActualHour, uStatus };
		expected = genXML(c[0], c[1], c[2], c[3], c[4], c[5], c[6], c[7], c[8]);
		assertEquals(expected, response.getWriterBuffer().toString());

        // (IV) test update sprint2 UI1
		
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
        
		// ================ set initial data =======================
		sprintID = "2";
		issueID = "3";
		uName = this.PREFIX + this.UPDATE_NAME + issueID;
		uHandler = "";	// 必須該專案有使用者才能測試
		uPartners = this.PREFIX + this.UPDATE_PARTNER + issueID;
		uEstimation = this.UPDATE_ESTIMATION;
		uActualHour = this.UPDATE_HOUR;
		uNotes = this.PREFIX + this.UPDATE_NOTE + issueID;
		uSpecificTime = "";
		uStatus = this.UPDATE_STATUS[2];
		// ================ set initial data =======================

		// ================== set parameter info ====================		
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", uName);
		addRequestParameter("Status", uStatus);	
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Estimate", uEstimation);		
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("ActualHour", uActualHour);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime", uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String[] d = { sprintID, issueID, uName, uHandler, uPartners, uEstimation, uNotes, uActualHour, uStatus };
		expected = genXML(d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7], d[8]);
		assertEquals(expected, response.getWriterBuffer().toString());			
	}		
	
	private String genXML(String sprintID, String issueID, String name, String handler, String partners , String estimation, String notes, String actualhour, String status ) {
	 	StringBuilder result = new StringBuilder("");
			
		result.append("<EditUnplannedItem>");
		result.append("<Result>success</Result>");
		//
		result.append("<UnplannedItem>");
		result.append("<Id>" + issueID + "</Id>");
		result.append("<Link>" + "/ezScrum/showIssueInformation.do?issueID=" + issueID + "</Link>");
		result.append("<Name>" + name + "</Name>");
		result.append("<SprintID>" + sprintID + "</SprintID>");
		result.append("<Estimate>" + estimation + "</Estimate>");
		result.append("<Status>" + status + "</Status>");
		result.append("<ActualHour>" + actualhour + "</ActualHour>");
		result.append("<Handler>" + handler + "</Handler>");
		result.append("<Partners>" + partners + "</Partners>");
		result.append("<Notes>" + notes + "</Notes>");
		result.append("</UnplannedItem>");
		//
		result.append("</EditUnplannedItem>");	
			
		return result.toString();
	}
	
}
