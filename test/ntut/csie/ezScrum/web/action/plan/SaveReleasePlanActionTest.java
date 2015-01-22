package ntut.csie.ezScrum.web.action.plan;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class SaveReleasePlanActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateRelease CR;
	private Configuration configuration;
	
	public SaveReleasePlanActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
    	this.CP = new CreateProject(1);
    	this.CP.exeCreate();										// 新增一測試專案
    	
    	this.CR = new CreateRelease(1, this.CP);
    	this.CR.exe();										// 新增一筆Release Plan
    	
    	super.setUp();
    	
    	setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	setRequestPathInfo("/saveReleasePlan");
    	
    	
    	// ============= release ==============
    	ini = null;
    }

    protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
    	CopyProject copyProject = new CopyProject(this.CP);
    	copyProject.exeDelete_Project();					// 刪除測試檔案
    	
    	configuration.setTestMode(false);
		configuration.save();
    	
    	super.tearDown();
    	
    	// ============= release ==============
    	ini = null;
    	copyProject = null;
    	this.CP = null;
    	this.CR = null;
    	configuration = null;
    }

   
    // 測試 Save 路徑
    public void testexecuteSave() throws Exception {
    	// ================ set initial data =======================
    	IProject project = this.CP.getProjectList().get(0);
    	Calendar cal = Calendar.getInstance();
    	Date StartDate = cal.getTime();
    	cal.add(Calendar.DAY_OF_YEAR, 10);
    	Date EndDate = cal.getTime();
    	SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    	
    	String ReleaseName = "Release Test Name";
    	String ReleaseDesc = "Release Test Desc";
    	String action = "save";
    	// ================ set initial data =======================
    	
    	
    	// ================== set parameter info ====================    	
    	addRequestParameter("Id", Integer.toString(this.CR.getReleaseCount() + 1));	//  新增第二筆
    	addRequestParameter("Name", ReleaseName);
    	addRequestParameter("StartDate", format.format(StartDate));
    	addRequestParameter("EndDate", format.format(EndDate));
    	addRequestParameter("Description", ReleaseDesc);
    	addRequestParameter("action", action);
    	// ================== set parameter info ====================
    	
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", configuration.getUserSession());    	
    	request.getSession().setAttribute("Project", project);
    	// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
    	
    	actionPerform();		// 執行 action
    	
    	// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();
    	assertEquals("true", response.getWriterBuffer().toString());
    	
    	// 驗證 ReleasePlan 資料
    	ReleasePlanHelper helper = new ReleasePlanHelper((IProject) request.getSession().getAttribute("Project"));
    	IReleasePlanDesc ReleasePlan = helper.getReleasePlan(Integer.toString(this.CR.getReleaseCount() + 1));
    	assertEquals(Integer.toString(this.CR.getReleaseCount() + 1), ReleasePlan.getID());
    	assertEquals(ReleaseName, ReleasePlan.getName());
    	assertEquals(format.format(StartDate), ReleasePlan.getStartDate());
    	assertEquals(format.format(EndDate), ReleasePlan.getEndDate());
    	assertEquals(ReleaseDesc, ReleasePlan.getDescription());
//    	assertEquals(null, ReleasePlan.getSprintDescList());
    	assertEquals(0, ReleasePlan.getSprintDescList().size());
    	
    	// ============= release ==============
    	project = null;
    	format = null;
    	helper = null;
    	ReleasePlan = null;
    }
    
    // 測試 Edit 路徑
    public void testexecuteEdit() throws Exception {
    	// ================ set initial data =======================
    	IProject project = this.CP.getProjectList().get(0);
    	Calendar cal = Calendar.getInstance();
    	Date StartDate = cal.getTime();
    	cal.add(Calendar.DAY_OF_YEAR, 10);
    	Date EndDate = cal.getTime();
    	SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    	
    	String ReleaseName = "Release New Name";
    	String ReleaseDesc = "Release New Desc";
    	String action = "edit";
    	// ================ set initial data =======================

    	
    	// ================== set parameter info ====================
    	addRequestParameter("Id", Integer.toString(this.CR.getReleaseCount()));		// 編輯第一筆
    	addRequestParameter("Name", ReleaseName);
    	addRequestParameter("StartDate", format.format(StartDate));
    	addRequestParameter("EndDate", format.format(EndDate));
    	addRequestParameter("Description", ReleaseDesc);
    	addRequestParameter("action", action);
    	// ================== set parameter info ====================
   
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", configuration.getUserSession());    	
    	request.getSession().setAttribute("Project", project);
    	// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
    	
    	actionPerform();		// 執行 action
    	
    	// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();
    	
    	// 驗證ReleasePlan資料
    	ReleasePlanHelper helper = new ReleasePlanHelper((IProject) request.getSession().getAttribute("Project"));
    	IReleasePlanDesc ReleasePlan = helper.getReleasePlan(Integer.toString(this.CR.getReleaseCount()));
    	assertEquals(Integer.toString(this.CR.getReleaseCount()), ReleasePlan.getID());
    	assertEquals(ReleaseName, ReleasePlan.getName());
    	assertEquals(format.format(StartDate), ReleasePlan.getStartDate());
    	assertEquals(format.format(EndDate), ReleasePlan.getEndDate());
    	assertEquals(ReleaseDesc, ReleasePlan.getDescription());
//    	assertEquals(null, ReleasePlan.getSprintDescList());
    	assertEquals(0, ReleasePlan.getSprintDescList().size());
    	
    	// ============= release ==============
    	project = null;
    	format = null;
    	helper = null;
    	ReleasePlan = null;
    }
       
    
/*    
 * 待修正: PermissionAction.java 之  getResponse()帶入null參數造成錯誤
    // 測試沒有 action 參數的路徑
    public void testexecuteNullParameter() throws Exception {
    	// ================ set initial data =======================
    	IProject project = this.CP.getProjectList().get(0);
    	Calendar cal = Calendar.getInstance();
    	Date StartDate = cal.getTime();
    	cal.add(Calendar.DAY_OF_YEAR, 10);
    	Date EndDate = cal.getTime();
    	SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    	
    	String ReleaseName = "Release New Name";
    	String ReleaseDesc = "Release New Desc";
    	String action = null;
    	// ================ set initial data =======================
    	
    	
    	// ================== set parameter info ====================
    	addRequestParameter("Id", Integer.toString(this.CR.getReleaseCount()));		// 編輯第一筆
    	addRequestParameter("Name", ReleaseName);
    	addRequestParameter("StartDate", format.format(StartDate));
    	addRequestParameter("EndDate", format.format(EndDate));
    	addRequestParameter("Description", ReleaseDesc);
    	addRequestParameter("action", action);
    	// ================== set parameter info ====================
    
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());    	
    	request.getSession().setAttribute("Project", project);
    	// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform();		// 執行 action
    	
    	// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();
    	
    	// 驗證 ReleasePlan 資料
    	ReleasePlanHelper helper = new ReleasePlanHelper((IProject) request.getSession().getAttribute("Project"));
    	IReleasePlanDesc ReleasePlan = helper.getReleasePlan(Integer.toString(this.CR.getReleaseCount()));
    	assertNotNull(ReleasePlan);
    	
    	
    	// ============= release ==============
    	project = null;
    	helper = null;
    	ReleasePlan = null;
    }
*/    
    
    
    // 測試參數沒有給正確數值，如，（日期格式錯誤）
    public void testexecuteWrongParameter1() throws Exception {
    	// ================ set initial data =======================
    	IProject project = this.CP.getProjectList().get(0);
    	Calendar cal = Calendar.getInstance();
    	Date StartDate = cal.getTime();
    	cal.add(Calendar.DAY_OF_YEAR, 10);
    	Date EndDate = cal.getTime();
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	
    	String ReleaseName = "Release New Name";
    	String ReleaseDesc = "Release New Desc";
    	String action = "save";
    	// ================ set initial data =======================
    	
    	
    	// ================== set parameter info ====================
    	addRequestParameter("Id", Integer.toString(this.CR.getReleaseCount()));		// 編輯第一筆
    	addRequestParameter("Name", ReleaseName);
    	addRequestParameter("StartDate", format.format(StartDate));
    	addRequestParameter("EndDate", format.format(EndDate));
    	addRequestParameter("Description", ReleaseDesc);
    	addRequestParameter("action", action);
    	// ================== set parameter info ====================
    
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", configuration.getUserSession());    	
    	request.getSession().setAttribute("Project", project);
    	// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform();		// 執行 action
    	
    	// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyNoActionErrors();
    	
    	// 驗證 ReleasePlan 資料
    	ReleasePlanHelper helper = new ReleasePlanHelper((IProject) request.getSession().getAttribute("Project"));
    	IReleasePlanDesc ReleasePlan = helper.getReleasePlan(Integer.toString(this.CR.getReleaseCount()));
    	assertNotNull(ReleasePlan);
    	
    	
    	// ============= release ==============
    	project = null;
    	format = null;
    	helper = null;
    	ReleasePlan = null;
    }
    
    // 測試參數沒有給正確數值，如，（Name 為 null）
    public void testexecuteWrongParameter2() throws Exception {
    	// ================ set initial data =======================
    	IProject project = this.CP.getProjectList().get(0);
    	Calendar cal = Calendar.getInstance();
    	Date StartDate = cal.getTime();
    	cal.add(Calendar.DAY_OF_YEAR, 10);
    	Date EndDate = cal.getTime();
    	SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    	
    	String ReleaseName = null;
    	String ReleaseDesc = null;
    	String action = "save";
    	// ================ set initial data =======================
    	
    	
    	// ================== set parameter info ====================
    	addRequestParameter("Id", Integer.toString(this.CR.getReleaseCount()));		// 編輯第一筆
    	addRequestParameter("Name", ReleaseName);
    	addRequestParameter("StartDate", format.format(StartDate));
    	addRequestParameter("EndDate", format.format(EndDate));
    	addRequestParameter("Description", ReleaseDesc);
    	addRequestParameter("action", action);
    	// ================== set parameter info ====================
    
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", configuration.getUserSession());    	
    	request.getSession().setAttribute("Project", project);
    	// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
    	actionPerform();		// 執行 action
    	
    	// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyNoActionErrors();
    	
    	// 驗證 ReleasePlan 資料
    	ReleasePlanHelper helper = new ReleasePlanHelper((IProject) request.getSession().getAttribute("Project"));
    	IReleasePlanDesc ReleasePlan = helper.getReleasePlan(Integer.toString(this.CR.getReleaseCount()));
    	assertNotNull(ReleasePlan);
    	
    	// ============= release ==============
    	project = null;
    	helper = null;
    	ReleasePlan = null;
    }

    
	// get user session -> 統一由 ezScrumInfoConfig.getUserSession()建立
//	private IUserSession CreateUserSession() throws LogonException {
//		IAccount theAccount = null;
//		theAccount = new Account(config.USER_ID);
//		IUserSession theUserSession = new UserSession(theAccount);
//		
//		return theUserSession;
//	}
}
