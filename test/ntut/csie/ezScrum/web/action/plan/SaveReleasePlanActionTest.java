package ntut.csie.ezScrum.web.action.plan;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import servletunit.struts.MockStrutsTestCase;

public class SaveReleasePlanActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateRelease mCR;
	private Configuration mConfig;
	
	public SaveReleasePlanActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL
		
    	mCP = new CreateProject(1);
    	mCP.exeCreate();										// 新增一測試專案
    	
    	mCR = new CreateRelease(1, mCP);
    	mCR.exe();										// 新增一筆Release Plan
    	
    	super.setUp();
    	
    	setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	setRequestPathInfo("/saveReleasePlan");
    	
    	
    	// ============= release ==============
    	ini = null;
    }

    protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL
		
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
    	
    	mConfig.setTestMode(false);
		mConfig.save();
    	
    	super.tearDown();
    	
    	// ============= release ==============
    	ini = null;
    	mCP = null;
    	mCR = null;
    	mConfig = null;
    }

   
    // 測試 Save 路徑
    public void testExecuteSave() throws Exception {
    	// ================ set initial data =======================
    	ProjectObject project = mCP.getAllProjects().get(0);
    	Calendar cal = Calendar.getInstance();
    	Date StartDate = cal.getTime();
    	cal.add(Calendar.DAY_OF_YEAR, 10);
    	Date EndDate = cal.getTime();
    	SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    	
    	String releaseName = "Release Test Name";
    	String releaseDesc = "Release Test Desc";
    	String action = "save";
    	// ================ set initial data =======================
    	
    	
    	// ================== set parameter info ====================    	
    	addRequestParameter("Id", Integer.toString(mCR.getReleaseCount() + 1));	//  新增第二筆
    	addRequestParameter("Name", releaseName);
    	addRequestParameter("StartDate", format.format(StartDate));
    	addRequestParameter("EndDate", format.format(EndDate));
    	addRequestParameter("Description", releaseDesc);
    	addRequestParameter("action", action);
    	// ================== set parameter info ====================
    	
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", mConfig.getUserSession());    	
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
    	ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper((ProjectObject) request.getSession().getAttribute("Project"));
    	IReleasePlanDesc releasePlan = releasePlanHelper.getReleasePlan(Integer.toString(mCR.getReleaseCount() + 1));
    	assertEquals(Integer.toString(mCR.getReleaseCount() + 1), releasePlan.getID());
    	assertEquals(releaseName, releasePlan.getName());
    	assertEquals(format.format(StartDate), releasePlan.getStartDate());
    	assertEquals(format.format(EndDate), releasePlan.getEndDate());
    	assertEquals(releaseDesc, releasePlan.getDescription());
    	assertEquals(0, releasePlan.getSprintDescList().size());
    	
    	// ============= release ==============
    	project = null;
    	format = null;
    	releasePlanHelper = null;
    	releasePlan = null;
    }
    
    // 測試 Edit 路徑
    public void testExecuteEdit() throws Exception {
    	// ================ set initial data =======================
    	ProjectObject project = mCP.getAllProjects().get(0);
    	Calendar cal = Calendar.getInstance();
    	Date StartDate = cal.getTime();
    	cal.add(Calendar.DAY_OF_YEAR, 10);
    	Date EndDate = cal.getTime();
    	SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    	
    	String releaseName = "Release New Name";
    	String releaseDesc = "Release New Desc";
    	String action = "edit";
    	// ================ set initial data =======================

    	
    	// ================== set parameter info ====================
    	addRequestParameter("Id", Integer.toString(mCR.getReleaseCount()));		// 編輯第一筆
    	addRequestParameter("Name", releaseName);
    	addRequestParameter("StartDate", format.format(StartDate));
    	addRequestParameter("EndDate", format.format(EndDate));
    	addRequestParameter("Description", releaseDesc);
    	addRequestParameter("action", action);
    	// ================== set parameter info ====================
   
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", mConfig.getUserSession());    	
    	request.getSession().setAttribute("Project", project);
    	// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
    	
    	actionPerform();		// 執行 action
    	
    	// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();
    	
    	// 驗證ReleasePlan資料
    	ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper((ProjectObject) request.getSession().getAttribute("Project"));
    	IReleasePlanDesc releasePlan = releasePlanHelper.getReleasePlan(Integer.toString(mCR.getReleaseCount()));
    	assertEquals(Integer.toString(mCR.getReleaseCount()), releasePlan.getID());
    	assertEquals(releaseName, releasePlan.getName());
    	assertEquals(format.format(StartDate), releasePlan.getStartDate());
    	assertEquals(format.format(EndDate), releasePlan.getEndDate());
    	assertEquals(releaseDesc, releasePlan.getDescription());
    	assertEquals(0, releasePlan.getSprintDescList().size());
    	
    	// ============= release ==============
    	project = null;
    	format = null;
    	releasePlanHelper = null;
    	releasePlan = null;
    }
    
//    // 待修正: PermissionAction.java 之  getResponse()帶入null參數造成錯誤
//    // 測試沒有 action 參數的路徑
//    public void testExecuteNullParameter() throws Exception {
//    	// ================ set initial data =======================
//    	IProject project = mCP.getProjectList().get(0);
//    	Calendar cal = Calendar.getInstance();
//    	Date StartDate = cal.getTime();
//    	cal.add(Calendar.DAY_OF_YEAR, 10);
//    	Date EndDate = cal.getTime();
//    	SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
//    	
//    	String ReleaseName = "Release New Name";
//    	String ReleaseDesc = "Release New Desc";
//    	String action = null;
//    	// ================ set initial data =======================
//    	
//    	
//    	// ================== set parameter info ====================
//    	addRequestParameter("Id", Integer.toString(mCR.getReleaseCount()));		// 編輯第一筆
//    	addRequestParameter("Name", ReleaseName);
//    	addRequestParameter("StartDate", format.format(StartDate));
//    	addRequestParameter("EndDate", format.format(EndDate));
//    	addRequestParameter("Description", ReleaseDesc);
//    	addRequestParameter("action", action);
//    	// ================== set parameter info ====================
//    
//    	
//    	// ================ set session info ========================
//    	request.getSession().setAttribute("UserSession", mConfig.getUserSession());    	
//    	request.getSession().setAttribute("Project", project);
//    	// ================ set session info ========================
//		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
//
//		actionPerform();		// 執行 action
//    	
//    	// 驗證回傳 path
//    	verifyForwardPath(null);
//    	verifyForward(null);
//    	verifyNoActionErrors();
//    	
//    	// 驗證 ReleasePlan 資料
//    	ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper((IProject) request.getSession().getAttribute("Project"));
//    	IReleasePlanDesc releasePlan = releasePlanHelper.getReleasePlan(Integer.toString(mCR.getReleaseCount()));
//    	assertNotNull(releasePlan);
//    	
//    	
//    	// ============= release ==============
//    	project = null;
//    	releasePlanHelper = null;
//    	releasePlan = null;
//    }
    
    // 測試參數沒有給正確數值，如，（日期格式錯誤）
    public void testExecuteWrongParameter1() throws Exception {
    	// ================ set initial data =======================
    	ProjectObject project = mCP.getAllProjects().get(0);
    	Calendar cal = Calendar.getInstance();
    	Date StartDate = cal.getTime();
    	cal.add(Calendar.DAY_OF_YEAR, 10);
    	Date EndDate = cal.getTime();
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	
    	String releaseName = "Release New Name";
    	String releaseDesc = "Release New Desc";
    	String action = "save";
    	// ================ set initial data =======================
    	
    	
    	// ================== set parameter info ====================
    	addRequestParameter("Id", Integer.toString(mCR.getReleaseCount()));		// 編輯第一筆
    	addRequestParameter("Name", releaseName);
    	addRequestParameter("StartDate", format.format(StartDate));
    	addRequestParameter("EndDate", format.format(EndDate));
    	addRequestParameter("Description", releaseDesc);
    	addRequestParameter("action", action);
    	// ================== set parameter info ====================
    
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", mConfig.getUserSession());    	
    	request.getSession().setAttribute("Project", project);
    	// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform();		// 執行 action
    	
    	// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyNoActionErrors();
    	
    	// 驗證 ReleasePlan 資料
    	ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper((ProjectObject) request.getSession().getAttribute("Project"));
    	IReleasePlanDesc releasePlan = releasePlanHelper.getReleasePlan(Integer.toString(mCR.getReleaseCount()));
    	assertNotNull(releasePlan);
    	
    	
    	// ============= release ==============
    	project = null;
    	format = null;
    	releasePlanHelper = null;
    	releasePlan = null;
    }
    
    // 測試參數沒有給正確數值，如，（Name 為 null）
    public void testExecuteWrongParameter2() throws Exception {
    	// ================ set initial data =======================
    	ProjectObject project = mCP.getAllProjects().get(0);
    	Calendar cal = Calendar.getInstance();
    	Date StartDate = cal.getTime();
    	cal.add(Calendar.DAY_OF_YEAR, 10);
    	Date EndDate = cal.getTime();
    	SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    	
    	String releaseName = null;
    	String releaseDesc = null;
    	String action = "save";
    	// ================ set initial data =======================
    	
    	
    	// ================== set parameter info ====================
    	addRequestParameter("Id", Integer.toString(mCR.getReleaseCount()));		// 編輯第一筆
    	addRequestParameter("Name", releaseName);
    	addRequestParameter("StartDate", format.format(StartDate));
    	addRequestParameter("EndDate", format.format(EndDate));
    	addRequestParameter("Description", releaseDesc);
    	addRequestParameter("action", action);
    	// ================== set parameter info ====================
    
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", mConfig.getUserSession());    	
    	request.getSession().setAttribute("Project", project);
    	// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
    	actionPerform();		// 執行 action
    	
    	// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyNoActionErrors();
    	
    	// 驗證 ReleasePlan 資料
    	ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper((ProjectObject) request.getSession().getAttribute("Project"));
    	IReleasePlanDesc releasePlan = releasePlanHelper.getReleasePlan(Integer.toString(mCR.getReleaseCount()));
    	assertNotNull(releasePlan);
    	
    	// ============= release ==============
    	project = null;
    	releasePlanHelper = null;
    	releasePlan = null;
    }
}
