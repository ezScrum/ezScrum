package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.LogonException;
import servletunit.struts.MockStrutsTestCase;

// 一般使用者更新資料
public class ViewProjectManagementActionTest extends MockStrutsTestCase {

	private CreateAccount CA;
	private int AccountCount = 1;
	private String actionPath = "/viewManagement";	// defined in "struts-config.xml"
	private IUserSession userSession;
	
	private Configuration configuration;
	
	public ViewProjectManagementActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
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
		
		configuration.setTestMode(false);
		configuration.save();
		
    	super.tearDown();    	
    	
    	// ============= release ==============
    	ini = null;
    	this.CA = null;
    	this.config = null;
    	this.userSession = null;
    	configuration = null;
    }
    
    // 
    public void testViewProjectManagementAction_admin() throws LogonException { 	    			
		
    	// ================ set initial data =======================

    	// ================ set initial data =======================    	
    	
    	// ================== set parameter info ==================== 	    
   	
    	// ================== set parameter info ====================
    	    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", configuration.getUserSession());
    	// ================ set session info ========================
    	
    	// ================ set URL parameter ========================    	
    	// ================ set URL parameter ========================

    	actionPerform();		// 執行 action
    	
    	/*
    	 * Verify:
    	 */
    	verifyForward("Admin_ManagementView");    	
    }		
    
    public void testViewProjectManagementAction_user() throws LogonException { 	    			
		// 新增使用者
		this.CA = new CreateAccount(this.AccountCount);
		this.CA.exe();
		
    	// ================ set initial data =======================
    	this.userSession = new UserSession(this.CA.getAccountList().get(0));    	
    	// ================ set initial data =======================

    	// ================ set initial data =======================    	
    	
    	// ================== set parameter info ==================== 	    
   	
    	// ================== set parameter info ====================
    	    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", this.userSession);
    	// ================ set session info ========================
    	
    	// ================ set URL parameter ========================    	
    	// ================ set URL parameter ========================

    	actionPerform();		// 執行 action
    	
    	/*
    	 * Verify:
    	 */
    	verifyForward("User_ManagementView");    	
    }	    
    
}
