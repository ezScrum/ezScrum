package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.dataObject.UserInformation;
import ntut.csie.ezScrum.web.form.LogonForm;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IRole;
import ntut.csie.jcis.account.core.LogonException;
import servletunit.struts.MockStrutsTestCase;

public class RemoveUserActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateAccount CA;
	private int ProjectCount = 1;
	private int AccountCount = 2;
	private String ActionPath_RemoveUser = "/removeUser";	// defined in "struts-config.xml"
	private AddUserToRole AUTR;
	
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private AccountMapper accountMapper;
	
	public RemoveUserActionTest(String testMethod) {
        super(testMethod);
    }
	
	private void setRequestPathInformation( String actionPath ){
    	setContextDirectory(new File(config.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	setRequestPathInfo( actionPath );
	}
	
	/**
	 * clean previous action info
	 */
	private void cleanActionInformation(){
		clearRequestParameters();
		this.response.reset();
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
		
    	// ============= release ==============
    	ini = null;		
    }

    protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe();											// 初始化 SQL
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(this.config.getTestDataPath());
    	
    	super.tearDown();
    	
    	// ============= release ==============
//    	AccountFactory.releaseManager();
    	this.accountMapper.releaseManager();
    	ini = null;
    	projectManager = null;
    	this.CP = null;
    	this.CA = null;
    	this.config = null;
    	this.accountMapper = null;
    }
    
    // 測試正常執行
    public void testexecute() throws LogonException {
    	setRequestPathInformation( this.ActionPath_RemoveUser );
    	
    	// 先加入 PO 角色
    	this.AUTR.exe_PO();
    	
    	IAccount acc = this.AUTR.getNoeAccount();
    	IAccount account = this.accountMapper.getAccount(acc.getID());
		IRole[] roles = account.getRoles();
    	
		assertEquals(roles.length, 2);
		
    	// ================ set initial data =======================
		String id = acc.getID();
		String res = this.AUTR.getNowProject().getName();
		String op = ScrumEnum.SCRUMROLE_PRODUCTOWNER;
    	// ================ set initial data =======================
		
		// 測試是否正確加入此 PO 角色
//		assertEquals(roles[0].getRoleId(), getRole(res, op));
		String expectUserRole = getRole(res, op);
		boolean isExisted = false;
		for(IRole role:roles){
			if(expectUserRole.equals(role.getRoleId())){
				isExisted = true;
				break;
			}
		}
		assertEquals(true, isExisted);
		
    	// ================== set parameter info ====================
    	addRequestParameter("id", id);
    	addRequestParameter("resource", res);
    	addRequestParameter("operation", op);
    	// ================== set parameter info ====================
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	// ================ set session info ========================
    	
    	actionPerform();		// 執行 action

    	account = this.accountMapper.getAccount(id);
		assertNotNull(account);
		assertEquals(account.getID(), this.CA.getAccount_ID(1));
		assertEquals(account.getName(), this.CA.getAccount_RealName(1));
		assertEquals(account.getEnable(), "true");
		assertEquals(account.getPassword(), "5e6698ee13f3ef999374751897721cb6");		// 密碼要經過 MD5 編碼
		assertEquals(account.getEmail(), this.CA.getAccount_Mail(1));
		
		// 測試 Role 是否正確被移除
		roles = account.getRoles();
		assertEquals(roles.length, 1);
    }
    
    // 測試 Admin 正常移除 System 權限 
    public void testexecuteAdmin_Remove1() throws LogonException {
    	setRequestPathInformation( this.ActionPath_RemoveUser );
    	
    	// ================ set initial data =======================
		String id = "admin";
		String res = ScrumEnum.SYSTEM;
		String op = ScrumEnum.SCRUMROLE_ADMIN;
    	// ================ set initial data =======================    	
		
    	// ================== set parameter info ====================
    	addRequestParameter("id", id);
    	addRequestParameter("resource", res);
    	addRequestParameter("operation", op);
    	// ================== set parameter info ====================
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	// ================ set session info ========================
    	
    	actionPerform();		// 執行 action

    	IAccount account = this.accountMapper.getAccount(id);
    	IRole[] roles = account.getRoles();
    	
    	// 測試是否正確移除 System 角色
    	assertEquals(roles.length, 1);
    	assertEquals(roles[0].getRoleId(), "user");
    }
    
    // 測試將 Admin 指派到某個專案的角色，再移除此專案的角色
    public void testexecuteAdmin_Remove2() throws LogonException {
    	setRequestPathInformation( this.ActionPath_RemoveUser );
    	
    	// 將 Admin 加入測試專案一的 PO
    	this.AUTR.setNowAccountIsSystem();
    	this.AUTR.exe_PO();
    	
    	// ================ set initial data =======================
		String id = "admin";
		String res = this.AUTR.getNowProject().getName();
		String op = ScrumEnum.SCRUMROLE_PRODUCTOWNER;
    	// ================ set initial data =======================    	
		
    	// ================== set parameter info ====================
    	addRequestParameter("id", id);
    	addRequestParameter("resource", res);
    	addRequestParameter("operation", op);
    	// ================== set parameter info ====================
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	// ================ set session info ========================

    	actionPerform();		// 執行 action

    	// 測試是否正確移除此角色，但是沒有移除掉 Admin 權限
    	IAccount account = this.accountMapper.getAccount(id);
    	IRole[] roles = account.getRoles();
    	
    	// 測試是否正確移除 System 角色
    	assertEquals(roles.length, 2);
		// 測試專案的 Role 是否正確加入，並且沒有移除 admin 權限
		String []Role_ID = new String[roles.length];
		for (int i=0 ; i<roles.length ; i++) {
			Role_ID[i] = roles[i].getRoleId();
		}
		// 利用 Rold ID 抽出來後排序，再做比對
		Arrays.sort(Role_ID);
		
    	assertEquals(Role_ID[0], "admin");
    	assertEquals(Role_ID[1], "user");
    }
    
    // 測試將 Admin 指派到某個專案的角色，再移除 Admin 權限，該專案的角色不會移除
    public void testexecuteAdmin_Remove3() throws LogonException {
    	setRequestPathInformation( this.ActionPath_RemoveUser );
    	
    	// 將 Admin 加入測試專案一的 PO
    	this.AUTR.setNowAccountIsSystem();
    	this.AUTR.exe_PO();
    	
    	// ================ set initial data =======================
		String id = "admin";
		String res = ScrumEnum.SYSTEM;
		String op = ScrumEnum.SCRUMROLE_ADMIN;
    	// ================ set initial data =======================    	
		
    	// ================== set parameter info ====================
    	addRequestParameter("id", id);
    	addRequestParameter("resource", res);
    	addRequestParameter("operation", op);
    	// ================== set parameter info ====================
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	// ================ set session info ========================

    	actionPerform();		// 執行 action

    	// 測試是否正確移除此角色，但是沒有移除掉 Admin 權限
    	IAccount account = this.accountMapper.getAccount(id);
    	IRole[] roles = account.getRoles();
    	
    	// 測試是否正確移除 System 角色
    	assertEquals(roles.length, 2);
		// 測試專案的 Role 是否正確加入，並且沒有移除 admin 權限
		String []Role_ID = new String[roles.length];
		for (int i=0 ; i<roles.length ; i++) {
			Role_ID[i] = roles[i].getRoleId();
		}
		// 利用 Rold ID 抽出來後排序，再做比對
		Arrays.sort(Role_ID);
		
    	assertEquals(Role_ID[0], getRole(this.AUTR.getNowProject().getName(), ScrumEnum.SCRUMROLE_PRODUCTOWNER));
    	assertEquals(Role_ID[1], "user");
    }
    
    /**
     * 1. admin 建立專案 (setup done)
     * 2. admin 建立角色(setup done)
     * 3. admin 指定 user 到專案中擔任 PO
     * 4. admin 移除 user 在專案中的角色
     * 5. user 登入ezScrum
     * 6. 測試user不會看到專案資訊
     * @throws LogonException
     */
    public void testexecuteAdmin_Remove_IntegrationTest() throws LogonException {
    	//	=============== common data ============================
    	IAccount account = this.CA.getAccountList().get(0);
    	IUserSession userSession = getUserSession(account);
    	String userId = this.CA.getAccount_ID(1);		// 取得第一筆 Account ID
    	String projectID = this.CP.getProjectList().get(0).getName();
    	
    	
    	/**
    	 * 3. admin 指定 user 到專案中擔任 PO (將 user 加入測試專案一的 PO)
    	 */
    	this.AUTR.setAccountIndex(0);
    	this.AUTR.exe_PO();
    	
    	/**
    	 * 4. admin 移除 user 在專案中的角色
    	 */
    	// ================ set initial data =======================
    	setRequestPathInformation( this.ActionPath_RemoveUser );
		String res = projectID;
		String op = ScrumEnum.SCRUMROLE_PRODUCTOWNER;
		
    	// ================== set parameter info ====================
    	addRequestParameter("id", userId);
    	addRequestParameter("resource", res);
    	addRequestParameter("operation", op);
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());

    	actionPerform();		// 執行 action

    	// 測試是否正確移除此角色，但是沒有移除掉 Admin 權限
    	IAccount actualAccount = this.accountMapper.getAccount(userId);
    	IRole[] roles = actualAccount.getRoles();
    	
    	// 測試是否正確移除 System 角色
    	assertEquals(roles.length, 1);
		// 測試專案的 Role 是否正確加入，並且沒有移除 admin 權限
		String []Role_ID = new String[roles.length];
		for (int i=0 ; i<roles.length ; i++) {
			Role_ID[i] = roles[i].getRoleId();
		}
		// 利用 Rold ID 抽出來後排序，再做比對
		Arrays.sort(Role_ID);
		
    	assertEquals(Role_ID[0], "user");
    	
		/**
		 * 5. user 登入 ezScrum
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();
		
		// ================ set action info ========================
    	String actionPath_logonSubmit = "/logonSubmit";
    	setRequestPathInformation( actionPath_logonSubmit );
		
    	// ================== set parameter info ====================
		String loginUserID = userId;
		String loginUserPassword = this.CA.getAccount_PWD(1);;
    	LogonForm logonForm = new LogonForm();
    	logonForm.setUserId(loginUserID);
    	logonForm.setPassword(loginUserPassword);
    	setActionForm(logonForm);
    	
    	// ================ 執行 login action ======================
    	actionPerform();
    	
    	// ================ assert ======================
    	verifyForward("success"); 
		
    	/**
    	 * 6. view project list (測試user不會看到專案資訊)
    	 */
		// ================ clean previous action info ========================
		cleanActionInformation();
		
		// ================ set action info ========================
    	String actionPath_viewProjectList = "/viewProjectList";
    	setRequestPathInformation( actionPath_viewProjectList );
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute( "UserSession", userSession );
    	
		// ================ 執行 view project list action ======================
    	actionPerform();
    	
    	// ================ assert ========================
    	//	assert response text
    	String viewProjectListExpectedResponseText = "<Projects></Projects>";
    	String viewProjectListActualResponseText = this.response.getWriterBuffer().toString();
    	assertEquals(viewProjectListExpectedResponseText, viewProjectListActualResponseText);
    	
    }
    
    private String getRole(String res, String op) {
    	return res + "_" + op;
    }
    
    private IUserSession getUserSession(IAccount account){
    	IUserSession userSession = new UserSession(account);
    	return userSession;
    }
}
