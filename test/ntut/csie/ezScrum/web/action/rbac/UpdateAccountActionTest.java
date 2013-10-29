package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.test.TestTool;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.dataObject.UserInformation;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.AccountFactory;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IAccountManager;
import ntut.csie.jcis.account.core.LogonException;
import servletunit.struts.MockStrutsTestCase;

// 一般使用者更新資料
public class UpdateAccountActionTest extends MockStrutsTestCase {

	private CreateProject CP;
	private CreateAccount CA;
	private int ProjectCount = 1;
	private int AccountCount = 1;
	private String actionPath = "/updateAccount";	// defined in "struts-config.xml"
	
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	
	public UpdateAccountActionTest(String testMethod) {
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
    	(new AccountMapper()).releaseManager();
    	ini = null;
    	copyProject = null;
    	this.CP = null;
    	this.CA = null;
    	this.config = null;
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
    	
    	String userId = this.CA.getAccount_ID(1);		// 取得第一筆 Account ID
    	String userPw = this.CA.getAccount_PWD(1) + postfix;
    	String userMail = "modify@test.com";
    	String userName = this.CA.getAccount_RealName(1) + postfix;
    	String userEnable = "false";	// default is true
    	String userIsEdit = "true";	// false 代表是新增帳號
    	// ================ set initial data =======================    	
    	
    	// ================== set parameter info ==================== 	    
    	addRequestParameter("id", userId);
    	addRequestParameter("passwd", userPw);
    	addRequestParameter("mail", userMail);
    	addRequestParameter("name", userName);
    	addRequestParameter("enable", userEnable);
    	addRequestParameter("isEdit", userIsEdit);    	
    	// ================== set parameter info ====================
    	    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	// ================ set session info ========================
    	
    	// ================ set URL parameter ========================    	
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
    	// ================ set URL parameter ========================

    	actionPerform();		// 執行 action
    	
    	/*
    	 * Verify:
    	 */
    	IAccount account = (new AccountMapper()).getAccountById(userId);
		
		assertNotNull(account);
		assertEquals(account.getID(), userId);
		assertEquals(account.getPassword(), (new TestTool()).getMd5(userPw));	
		assertEquals(account.getEmail(), userMail);		
		assertEquals(account.getName(), userName);
		assertEquals(account.getEnable(), userEnable);		
    }		
    
}
