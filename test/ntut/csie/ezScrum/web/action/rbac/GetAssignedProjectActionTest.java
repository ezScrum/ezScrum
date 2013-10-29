package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.dataObject.UserInformation;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IPermission;
import ntut.csie.jcis.account.core.IRole;
import ntut.csie.jcis.account.core.LogonException;
import servletunit.struts.MockStrutsTestCase;

// 一般使用者更新資料
public class GetAssignedProjectActionTest extends MockStrutsTestCase {

	private CreateProject CP;
	private CreateAccount CA;
	private AddUserToRole AUTR;
	
	private int ProjectCount = 1;
	private int AccountCount = 1;
	
	private String actionPath = "/getAssignedProject";	// defined in "struts-config.xml"
	
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private AccountMapper accountMapper;
	
	public GetAssignedProjectActionTest(String testMethod) {
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
		
		// 用來指派Scrum角色
		this.AUTR = new AddUserToRole(this.CP, this.CA);		
		
		this.accountMapper = new AccountMapper();
		
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
    	this.accountMapper.releaseManager();
    	ini = null;
    	copyProject = null;
    	this.CP = null;
    	this.CA = null;
    	this.config = null;
    	this.accountMapper = null;
    }
    
    // 
    public void testGetAssignedProjectAction() throws LogonException {		
    	// 先加入 PO 角色
    	this.AUTR.exe_PO();
    	
    	// ================ set initial data =======================
    	String projectId = this.CP.getProjectList().get(0).getName();
    	// User Information
    	
    	String userId = this.CA.getAccount_ID(1);
    	// ================ set initial data =======================    	
    	
    	// ================== set parameter info ==================== 	    
    	addRequestParameter("accountID", userId);   	
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
    	IAccount account = this.accountMapper.getAccountById(userId);
		
		assertNotNull(account);
		
		// role
		IRole[] roles = account.getRoles();
    	
		assertEquals(roles.length, 2);		
		IPermission[] permissons = roles[0].getPermisions();
		String roleA = permissons[0].getPermissionName();	// actual role
		permissons = roles[1].getPermisions();
		String roleB = permissons[0].getPermissionName();	// actual role
		
		String res = this.AUTR.getNowProject().getName();	// project
		String op = ScrumEnum.SCRUMROLE_PRODUCTOWNER;	// scrum role
		String roleE = res + "_" + op;	// expected role		
		
		assertEquals("system_read", roleA);	
		assertEquals(roleE, roleB);	
    }		
}
