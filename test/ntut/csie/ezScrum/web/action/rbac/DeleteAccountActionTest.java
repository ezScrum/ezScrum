package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.LogonException;
import servletunit.struts.MockStrutsTestCase;

public class DeleteAccountActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateAccount CA;
	private int ProjectCount = 1;
	private int AccountCount = 2;
	private String actionPath = "/deleteAccount";	// defined in "struts-config.xml"
	
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private AccountMapper accountMapper;
	
	public DeleteAccountActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe();											// 初始化 SQL
		
		// 新增Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();

		// 新增使用者
		this.CA = new CreateAccount(this.AccountCount);
		this.CA.exe();
		
		this.accountMapper = new AccountMapper();
		
		super.setUp();
		
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
    	this.accountMapper.releaseManager();
    	ini = null;
    	copyProject = null;
    	this.CP = null;
    	this.CA = null;
    	this.config = null;
    	this.accountMapper = null;
    }
    
    // 測試正常執行
    public void testDeleteAccountAction() throws LogonException {
		
    	// ================ set initial data =======================
		String userId = this.CA.getAccountList().get(0).getId();
		
    	// ================== set parameter info ====================
    	addRequestParameter("id", userId);
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	
    	// 執行 action
    	actionPerform();
    	UserObject account = this.accountMapper.getAccount(userId);
		
		assertNull(account);
    }
        
}
