package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.LogonException;
import servletunit.struts.MockStrutsTestCase;

// admin新增使用者之前會先做此檢查
public class CheckAccountIDActionTest extends MockStrutsTestCase {

	private CreateProject CP;
	private CreateAccount CA;
	private int ProjectCount = 1;
	private int AccountCount = 1;
	private String actionPath = "/checkAccountID";	// defined in "struts-config.xml"
	
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	
	public CheckAccountIDActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe();											// 初始化 SQL
		
		// 新增Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();
		
		super.setUp();
		
		// 固定行為可抽離
    	setContextDirectory(new File(config.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	setRequestPathInfo(this.actionPath);
    	
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
//    	AccountFactory.releaseManager();
    	(new AccountMapper()).releaseManager();
    	ini = null;
    	copyProject = null;
    	this.CP = null;
    	this.CA = null;
    	this.config = null;
    }
    
    // 測試欲新增加的帳號已重複
    public void testCheckAccountIDActionTest_existed() throws LogonException { 	    			
		// 新增使用者
		this.CA = new CreateAccount(this.AccountCount);
		this.CA.exe();
		
    	// ================ set initial data =======================
    	String projectId = this.CP.getProjectList().get(0).getName();
    	// User Information   	
    	String userId = this.CA.getAccount_ID(1);		// 取得第一筆 Account ID
    	
    	// ================== set parameter info ==================== 	    
    	addRequestParameter("id", userId);    	
    	    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	
    	// ================ set URL parameter ========================    	
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
    	// ================ set URL parameter ========================

		// 執行 action
    	actionPerform();
    	
    	/*
    	 * Verify:
    	 */
    	verifyNoActionErrors();
    	String result = response.getWriterBuffer().toString();
 	
    	assertEquals("false", result);	// 帳號已存在    	 
    }		

    // 測試新增加的帳號
    public void testCheckAccountIDActionTest_New() throws LogonException { 	    			
	
    	// ================ set initial data =======================
    	String projectId = this.CP.getProjectList().get(0).getName();
    	// User Information   	
    	String userId = "testNewID";
    	
    	// ================== set parameter info ==================== 	    
    	addRequestParameter("id", userId);    	
    	    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	
    	// ================ set URL parameter ========================    	
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// 執行 action
    	actionPerform();
    	
    	/*
    	 * Verify:
    	 */
    	verifyNoActionErrors();
    	String result = response.getWriterBuffer().toString();
 	
    	assertEquals("true", result);	// 帳號未存在    	 
    }	    
    
    // 測試欲新增加的帳號為 empty
    public void testCheckAccountIDActionTest_New1() throws LogonException { 	    			
	
    	// ================ set initial data =======================
    	String projectId = this.CP.getProjectList().get(0).getName();
    	// User Information   	
    	String userId = "";	// 
    	
    	// ================== set parameter info ==================== 	    
    	addRequestParameter("id", userId);    	
    	    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	
    	// ================ set URL parameter ========================    	
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// 執行 action
    	actionPerform();
    	
    	/*
    	 * Verify:
    	 */
    	verifyNoActionErrors();
    	String result = response.getWriterBuffer().toString();
 	
    	assertEquals("false", result);	// 帳號 NG 
    }    
    
}
