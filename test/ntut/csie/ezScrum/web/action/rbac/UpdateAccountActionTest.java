package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.TestTool;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.LogonException;
import servletunit.struts.MockStrutsTestCase;

// 一般使用者更新資料
public class UpdateAccountActionTest extends MockStrutsTestCase {

	private CreateProject CP;
	private CreateAccount CA;
	private int ProjectCount = 1;
	private int AccountCount = 1;
	private String actionPath = "/updateAccount";	// defined in "struts-config.xml"
	
	private Configuration configuration;
	
	public UpdateAccountActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
		// 新增Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();
		
		super.setUp();
		
		// 固定行為可抽離
    	setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	setRequestPathInfo(this.actionPath);
    	
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
    	(new AccountMapper()).releaseManager();
    	ini = null;
    	copyProject = null;
    	this.CP = null;
    	this.CA = null;
    	this.config = null;
    	configuration = null;
    }
    
    // 
    public void testUpdateAccountAction() throws LogonException { 	    			
		// 新增使用者
		this.CA = new CreateAccount(this.AccountCount);
		this.CA.exe();
		
    	// ================ set initial data =======================
    	String projectId = this.CP.getProjectList().get(0).getName();
    	// User Information
    	String postfix = "_update";
    	AccountObject account = CA.getAccountList().get(0);
    	String userId = account.getId();		// 取得第一筆 Account ID
    	String userAccount = account.getUsername();		// 取得第一筆 Account ID
    	String userPw = account.getPassword() + postfix;
    	String userMail = "modify@test.com";
    	String userName = account.getName() + postfix;
    	String userEnable = "false";	// default is true
    	String userIsEdit = "true";	// false 代表是新增帳號
    	// ================ set initial data =======================    	
    	
    	// ================== set parameter info ==================== 	    
    	addRequestParameter("id", userId);
    	addRequestParameter("account", userAccount);
    	addRequestParameter("passwd", userPw);
    	addRequestParameter("mail", userMail);
    	addRequestParameter("name", userName);
    	addRequestParameter("enable", userEnable);
    	addRequestParameter("isEdit", userIsEdit);    	
    	// ================== set parameter info ====================
    	    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", configuration.getUserSession());
    	// ================ set session info ========================
    	
    	// ================ set URL parameter ========================    	
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
    	// ================ set URL parameter ========================

    	actionPerform();		// 執行 action
    	
    	/*
    	 * Verify:
    	 */
    	account = (new AccountMapper()).getAccount(userAccount);
		
		assertNotNull(account);
		assertEquals(userAccount, account.getUsername());
		assertEquals((new TestTool()).getMd5(userPw), account.getPassword());	
		assertEquals(userMail, account.getEmail());		
		assertEquals(userName, account.getName());
		assertEquals(userEnable, account.getEnable());		
    }		
    
}
