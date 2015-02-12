package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import servletunit.struts.MockStrutsTestCase;

// 一般使用者更新資料
public class ViewProjectManagementActionTest extends MockStrutsTestCase {

	private CreateAccount mCA;
	private int mAccountCount = 1;
	private String mActionPath = "/viewManagement";
	private IUserSession mUserSession;
	private Configuration mConfig;
	
	public ViewProjectManagementActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		super.setUp();
		
		/**
		 * 設定讀取的 struts-config 檔案路徑
		 * ViewProjectManagementAction
		 */
    	setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	setRequestPathInfo(this.mActionPath);
    	
    	// ============= release ==============
    	ini = null;
    }

    protected void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();
		
    	super.tearDown();    	
    	
    	// ============= release ==============
    	ini = null;
    	mCA = null;
    	mUserSession = null;
    	projectManager = null;
    	mConfig = null;
    }
    
    // 
    public void testViewProjectManagementAction_admin() { 	    			
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// 執行 action
    	actionPerform();
    	
    	// Verify
    	verifyForward("Admin_ManagementView");    	
    }		
    
    public void testViewProjectManagementAction_user() { 	    			
		// create account
		mCA = new CreateAccount(mAccountCount);
		mCA.exe();
		
    	// ================ set initial data =======================
    	mUserSession = new UserSession(mCA.getAccountList().get(0));    	
    	    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", mUserSession);

		// 執行 action
    	actionPerform();
    	
    	// Verify
    	verifyForward("User_ManagementView");    	
    }	    
}
