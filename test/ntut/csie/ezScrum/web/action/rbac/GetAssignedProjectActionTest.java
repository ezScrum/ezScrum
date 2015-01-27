package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
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
	
	private Configuration configuration;
	private AccountMapper accountMapper;
	
	public GetAssignedProjectActionTest(String testMethod) {
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
		
		// 新增使用者
		this.CA = new CreateAccount(this.AccountCount);
		this.CA.exe();
		
		// 用來指派Scrum角色
		this.AUTR = new AddUserToRole(this.CP, this.CA);		
		
		this.accountMapper = new AccountMapper();
		
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
//    	AccountFactory.releaseManager();
    	this.accountMapper.releaseManager();
    	ini = null;
    	copyProject = null;
    	this.CP = null;
    	this.CA = null;
    	this.config = null;
    	this.accountMapper = null;
    	configuration = null;
    }
    
    // 
    public void testGetAssignedProjectAction() throws LogonException {		
    	// 先加入 PO 角色
    	this.AUTR.exe_PO();
    	
    	// ================ set initial data =======================
    	String projectId = this.CP.getProjectList().get(0).getName();
    	// User Information
    	
    	String userId = this.CA.getAccountList().get(0).getId();
    	// ================ set initial data =======================    	
    	
    	// ================== set parameter info ==================== 	    
    	addRequestParameter("accountID", userId);   	
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
    	AccountObject account = this.accountMapper.getAccount(userId);
		
		assertNotNull(account);
		
		// role
		HashMap<String, ProjectRole> roleMap = account.getRoles();
    	
		assertEquals(1, roleMap.size());	// PO
		assertEquals(ScrumEnum.SCRUMROLE_PRODUCTOWNER, roleMap.get(projectId).getScrumRole().getRoleName());	
//		IPermission[] permissons = roles[0].getPermisions();
//		String roleA = permissons[0].getPermissionName();	// actual role
//		permissons = roles[1].getPermisions();
//		String roleB = permissons[0].getPermissionName();	// actual role
//		
//		String res = this.AUTR.getNowProject().getName();	// project
//		String op = ScrumEnum.SCRUMROLE_PRODUCTOWNER;	// scrum role
//		String roleE = res + "_" + op;	// expected role		
//		
//		assertEquals("system_read", roleA);	
//		assertEquals(roleE, roleB);	
    }		
}
