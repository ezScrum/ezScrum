package ntut.csie.ezScrum.web.action.plan;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class RemoveReleasePlanActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateRelease CR;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	
	public RemoveReleasePlanActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe();											// 初始化 SQL
		
    	this.CP = new CreateProject(1);
    	this.CP.exeCreate();								// 新增一測試專案
    	
    	this.CR = new CreateRelease(1, this.CP);
    	this.CR.exe();										// 新增一筆Release Plan
    	
    	super.setUp();
    	
    	setContextDirectory(new File(config.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	setRequestPathInfo("/removeReleasePlan");
    	
    	
    	// ============= release ==============
    	ini = null;
    }
	
    protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe();											// 初始化 SQL
		
    	CopyProject copyProject = new CopyProject(this.CP);
    	copyProject.exeDelete_Project();					// 刪除測試檔案
    	
    	super.tearDown();
    	
    	
    	// ============= release ==============
    	ini = null;
    	copyProject = null;
    	this.CP = null;
    	this.CR = null;
    }
    
    // 正常執行
    public void testexecute() throws Exception {
    	// ================ set initial data =======================
    	IProject project = this.CP.getProjectList().get(0);
    	// ================ set initial data =======================
    	
    	
    	// ================== set parameter info ====================
    	addRequestParameter("releaseID", Integer.toString(this.CR.getReleaseCount()));		// 刪除第一筆
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
    	assertNull(ReleasePlan);
    	
    	
    	// ============= release ==============
    	project = null;
    	helper = null;
    	ReleasePlan = null;
    }

/*
     * 待修正: RemoveReleasePlanAction.java #50 沒有對錯誤的releaseID作妥善處理    
    // 代入錯誤的 ReleaseID 參數
    public void testexecuteWrongParameter() throws Exception {
    	// ================ set initial data =======================
    	IProject project = this.CP.getProjectList().get(0);
    	// ================ set initial data =======================
    	
    	
    	// ================== set parameter info ====================
    	addRequestParameter("releaseID", "X");		// 參數沒有給正確的 ID
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
    	assertNotNull(ReleasePlan);		// 沒有刪除掉第一筆 Release Plan
    	
    	// ============= release ==============
    	project = null;
    	helper = null;
    	ReleasePlan = null;
    }
*/
    
	// get user session -> 統一由 ezScrumInfoConfig.getUserSession()建立
//	private IUserSession CreateUserSession() throws LogonException {
//		IAccount theAccount = null;
//		theAccount = new Account(config.USER_ID);
//		IUserSession theUserSession = new UserSession(theAccount);
//		
//		return theUserSession;
//	}
    
}