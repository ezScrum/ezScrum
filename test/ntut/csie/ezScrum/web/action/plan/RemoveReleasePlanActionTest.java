package ntut.csie.ezScrum.web.action.plan;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import servletunit.struts.MockStrutsTestCase;

public class RemoveReleasePlanActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateRelease mCR;
	private Configuration mConfig;
	
	public RemoveReleasePlanActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL
		
    	mCP = new CreateProject(1);
    	mCP.exeCreate();								// 新增一測試專案
    	
    	mCR = new CreateRelease(1, mCP);
    	mCR.exe();										// 新增一筆Release Plan
    	
    	super.setUp();
    	
    	setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	setRequestPathInfo("/removeReleasePlan");
    	
    	
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
    
    // 正常執行
    public void testExecute() throws Exception {
    	// ================ set initial data =======================
    	ProjectObject project = mCP.getAllProjects().get(0);
    	// ================ set initial data =======================
    	
    	
    	// ================== set parameter info ====================
    	addRequestParameter("releaseID", Integer.toString(mCR.getReleaseCount()));		// 刪除第一筆
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
    	
		// 驗證 ReleasePlan 資料
		ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper((ProjectObject) request.getSession().getAttribute("Project"));
		IReleasePlanDesc relasePlan = releasePlanHelper.getReleasePlan(Integer.toString(mCR.getReleaseCount()));
		assertNull(relasePlan);
    	
    	
    	// ============= release ==============
    	project = null;
    	releasePlanHelper = null;
    	relasePlan = null;
    }

    // 代入錯誤的 ReleaseID 參數
    public void testExecuteWrongParameter() throws Exception {
    	// ================ set initial data =======================
    	ProjectObject project = mCP.getAllProjects().get(0);
    	// ================ set initial data =======================
    	
    	
    	// ================== set parameter info ====================
    	addRequestParameter("releaseID", "X");		// 參數沒有給正確的 ID
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
    	
    	// 驗證 ReleasePlan 資料
    	ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper((ProjectObject) request.getSession().getAttribute("Project"));
    	IReleasePlanDesc relasePlan = releasePlanHelper.getReleasePlan(Integer.toString(mCR.getReleaseCount()));
    	assertNotNull(relasePlan);		// 沒有刪除掉第一筆 Release Plan
    	
    	// ============= release ==============
    	project = null;
    	releasePlanHelper = null;
    	relasePlan = null;
    }
}