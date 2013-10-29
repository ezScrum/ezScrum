package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.TestTool;
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
import ntut.csie.jcis.account.core.internal.Role;
import servletunit.struts.MockStrutsTestCase;

public class AddUserActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateAccount CA;
	private int ProjectCount = 1;
	private int AccountCount = 1;
	private String ActionPath_AddUser = "/addUser";	// defined in "struts-config.xml"
	
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private AccountMapper accountMapper;
	
	public AddUserActionTest(String testMethod) {
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
    	this.accountMapper.releaseManager();
    	ini = null;
    	projectManager = null;
    	this.CP = null;
    	this.CA = null;
    	this.config = null;
    	this.accountMapper = null;
    }
    
    // 以一般使用者身分執行
    public void testAddUserAction_user() throws LogonException { 	
    	setRequestPathInformation( this.ActionPath_AddUser );
    	
    	// ================ set initial data =======================
    	String id = this.CA.getAccount_ID(1);		// 取得第一筆 Account ID
    	String Project_ID = this.CP.getProjectList().get(0).getName();
    	String Actor = "ProductOwner";	// ?
    	
    	// ================== set parameter info ====================
    	addRequestParameter("id", id);
    	addRequestParameter("resource", Project_ID);
    	addRequestParameter("operation", Actor);
    	    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	
    	// ================ set URL parameter ========================    	
		request.setHeader("Referer", "?PID=" + Project_ID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// 執行 action
    	actionPerform();
    	
    	/*
    	 * Verify:
    	 */

    	IAccount account = this.accountMapper.getAccountById(id);
		assertNotNull(account);
		assertEquals(account.getID(), this.CA.getAccount_ID(1));
		assertEquals(account.getName(), this.CA.getAccount_RealName(1));
		assertEquals(account.getEnable(), "true");
		
		assertEquals(account.getPassword(), (new TestTool()).getMd5(this.CA.getAccount_PWD(1)));
		assertEquals(account.getEmail(), this.CA.getAccount_Mail(1));
		
		// 測試 Role 是否正確
		String expectedUserRole_PO = (new TestTool()).getRole(Project_ID, Actor);
		String expectedUserRole_USER = "user";
		IRole[] roleArr = account.getRoles();
		String[] userRole = {expectedUserRole_USER, expectedUserRole_PO};
		for( String roleID:userRole ){
			boolean isExisted = false;
			for(IRole role:roleArr){
				if(roleID.equals(role.getRoleId())){
					isExisted = true;
					break;
				}
			}
			assertEquals(true, isExisted);
		}
		IRole[] roles = account.getRoles();
		assertEquals(roles.length, 2);		//	include user and ProductOwner
    }

    // 以系統管理員身分執行
    public void testAddUserAction_admin() throws LogonException {
    	setRequestPathInformation( this.ActionPath_AddUser );
    	
    	// ================ set initial data =======================
    	String id = "admin";		// 取得第一筆 Account ID
    	String Project_ID = this.CP.getProjectList().get(0).getName();
    	String Actor = "ProductOwner";	// ?
    	// ================ set initial data =======================
    	    	
    	// ================== set parameter info ====================
    	addRequestParameter("id", id);
    	addRequestParameter("resource", Project_ID);
    	addRequestParameter("operation", Actor);
    	// ================== set parameter info ====================
    	    
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	// ================ set session info ========================

    	// ================ set URL parameter ========================    	
		request.setHeader("Referer", "?PID=" + Project_ID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
    	// ================ set URL parameter ========================

    	actionPerform();		// 執行 action
    	
    	/*
    	 * Verify:
    	 */
		IAccount account = this.accountMapper.getAccountById(id);
		
		assertNotNull(account);
		assertEquals(account.getID(), id);
		assertEquals(account.getName(), ScrumEnum.SCRUMROLE_ADMIN);
		assertEquals(account.getEnable(), "true");

		// 更新囉 ilove306 -> admin	
		assertEquals(account.getPassword(), (new TestTool()).getMd5("admin"));		
		// 更新囉 null -> example@ezScrum.tw
		assertEquals(account.getEmail(), "example@ezScrum.tw");
		
		// Role verify
		IRole[] roles = account.getRoles();
		
		assertEquals(roles.length, 3);
		
		// 測試專案的 Role 是否正確加入，並且沒有移除 admin 權限
		String []Role_ID = new String[roles.length];
		for (int i=0 ; i<roles.length ; i++) {
			Role_ID[i] = roles[i].getRoleId();
		}
		// 利用 Rold ID 抽出來後排序，再做比對
		Arrays.sort(Role_ID);
		
		assertEquals(Role_ID[0], (new TestTool()).getRole(Project_ID, Actor));
		assertEquals(Role_ID[1], ScrumEnum.SCRUMROLE_ADMIN);
		assertEquals(Role_ID[2], "user");
    }
   
	/**
	 * Integration Test
	 * Steps
	 * 	1. admin 新增專案 (setup done) 
	 * 	2. admin 新增帳號 (setup done)
	 * 	3. admin assign this account to the project
	 * 	4. user login ezScrum
	 * 	5. user view project list
	 * 	6. user select project
	 * @throws InterruptedException 
	 */
    public void testAddUserAction_IntegrationTest() throws InterruptedException {
    	//	=============== common data ============================
    	IAccount account = this.CA.getAccountList().get(0);
    	IUserSession userSession = getUserSession(account);
    	String userId = this.CA.getAccount_ID(1);		// 取得第一筆 Account ID
    	String projectID = this.CP.getProjectList().get(0).getName();

    	/**
    	 * 3. admin assign this account to the project
    	 */
    	
    	// ================ set action info ========================
    	setRequestPathInformation( this.ActionPath_AddUser );
    	
    	// ================ set initial data =======================
    	String scrumRole = "ProductOwner";
    	
    	// ================== set parameter info ====================
    	addRequestParameter("id", userId);
    	addRequestParameter("resource", projectID);
    	addRequestParameter("operation", scrumRole);
    	    	
    	// ================ set session info with admin ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	
    	// ================ set URL parameter ========================    	
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ 執行 add user action ======================
    	actionPerform();
    	
    	// ================ assert ========================
    	//	assert response text
		String expectedUserRole_PO = (new TestTool()).getRole(projectID, scrumRole);
		String expectedUserRole_USER = "user";
    	String expectedUserId = this.CA.getAccount_ID(1);
    	String expectedUserName = this.CA.getAccount_RealName(1);
    	String expectedUserPassword = (new TestTool()).getMd5(this.CA.getAccount_PWD(1));
    	String expectedUserMail = this.CA.getAccount_Mail(1);
    	String expectedUserEnable = "true";
    	account.addRole(new Role(expectedUserRole_PO, expectedUserRole_PO));
    	IRole[] roleArr = account.getRoles();
    	String expectedUserRole = roleArr[0].getRoleId() + ", " + roleArr[1].getRoleId();
    	String addUserExpectedResponseText = 
    		"<Accounts>" +
	    		"<Account>" +
		    		"<ID>" + expectedUserId + "</ID>" +
		    		"<Name>" + expectedUserName + "</Name>" +
		    		"<Mail>" + expectedUserMail + "</Mail>" +
		    		"<Roles>" + expectedUserRole + "</Roles>" +
		    		"<Enable>" + expectedUserEnable + "</Enable>" +
	    		"</Account>" +
    		"</Accounts>";
    	String addUserActualResponseText = this.response.getWriterBuffer().toString();
    	assertEquals(addUserExpectedResponseText, addUserActualResponseText);
    	
    	//	assert database information
    	IAccount expectedAccount = this.accountMapper.getAccountById(userId);
		assertNotNull(account);
		assertEquals(expectedUserId, expectedAccount.getID());
		assertEquals(expectedUserName, expectedAccount.getName());
		assertEquals(expectedUserPassword, expectedAccount.getPassword());
		assertEquals(expectedUserMail, expectedAccount.getEmail());
		assertEquals(expectedUserEnable, expectedAccount.getEnable());
		
		// 測試 Role 是否正確
		String[] userRole = {expectedUserRole_USER, expectedUserRole_PO};
		for( String roleID:userRole ){
			boolean isExisted = false;
			for(IRole role:roleArr){
				if(roleID.equals(role.getRoleId())){
					isExisted = true;
					break;
				}
			}
			assertEquals(true, isExisted);
		}
		IRole[] roles = account.getRoles();
		assertEquals(roles.length, 2);		//	include user and ProductOwner
		
		/**
		 * 4. user login ezScrum
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();
		
		// ================ set action info ========================
    	String actionPath_logonSubmit = "/logonSubmit";
    	setRequestPathInformation( actionPath_logonSubmit );
		
    	// ================== set parameter info ====================
		String loginUserID = userId;
		String loginUserPassword = this.CA.getAccount_PWD(1);
    	LogonForm logonForm = new LogonForm();
    	logonForm.setUserId(loginUserID);
    	logonForm.setPassword(loginUserPassword);
    	setActionForm(logonForm);
    	
    	// ================ 執行 login action ======================
    	actionPerform();
    	
    	// ================ assert ======================
    	verifyForward("success"); 
		
    	/**
    	 * 5. view project list
    	 */
    	account = accountMapper.getAccountById(userId);
    	userSession = getUserSession(account);
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
    	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	String expectedProjectID = "TEST_PROJECT_1";
    	String expectedProjectDisplayName = "TEST_PROJECT_1";
    	String expectedProjectComment = "This is Test Project - 1";
    	String expectedProjectManager = "Project_Manager_1";
    	String expectedProjectCreateDate = dateFormat.format( this.CP.getProjectList().get(0).getProjectDesc().getCreateDate() );
    	String expectedProjectDemoDate = "No Plan!";
    	//	assert response text
    	String viewProjectListExpectedResponseText = 
			"<Projects>" +
				"<Project>" +
					"<ID>" + expectedProjectID + "</ID>" +
					"<Name>" + expectedProjectDisplayName + "</Name>" +
					"<Comment>" + expectedProjectComment + "</Comment>" +
					"<ProjectManager>" + expectedProjectManager + "</ProjectManager>" +
					"<CreateDate>" + expectedProjectCreateDate + "</CreateDate>" +
					"<DemoDate>" + expectedProjectDemoDate + "</DemoDate>" +
				"</Project>" +
			"</Projects>";
    	String viewProjectListActualResponseText = this.response.getWriterBuffer().toString();
    	assertEquals(viewProjectListExpectedResponseText, viewProjectListActualResponseText);
		
    	/**
		 * 6. user select project
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();
		
		// ================ set action info ========================
    	String actionPath_viewProject = "/viewProject";
    	setRequestPathInformation( actionPath_viewProject );
		
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", userSession );
    	
    	// ================== set parameter info ====================
    	addRequestParameter("PID", projectID);
    	
    	// ================ 執行 view project action ======================
    	actionPerform();
    	
    	// ================ assert ======================
    	verifyForward("SummaryView"); 
    }
    
    /**
     * 當request parameter 不完整無法將該使用者加入到特定專案中。
     * request parameter包含
     * 1. id (X)
     * 2. resource (O)
     * 3. operation (O)
     */
    public void testAddUserAction_NullID_RequestParameter(){
    	//	=============== common data ============================
    	IAccount account = this.CA.getAccountList().get(0);
    	String projectID = this.CP.getProjectList().get(0).getName();
    	String scrumRole = "ProductOwner";
    	
    	/**
    	 * 3. admin assign this account to the project
    	 */
    	
    	// ================ set action info ========================
    	setRequestPathInformation( this.ActionPath_AddUser );
    	
    	// ================== set parameter info ====================
//    	addRequestParameter("id", userId);
    	addRequestParameter("resource", projectID);
    	addRequestParameter("operation", scrumRole);
    	    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	
    	// ================ set URL parameter ========================    	
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ 執行 add user action ======================
    	actionPerform();
    	
    	// ================ assert ========================
    	//	assert response text
    	verifyNoActionMessages();
    	
    	//	assert database information
		// 	測試 Role 是否正確
		IRole[] roles = account.getRoles();
		assertEquals(roles.length, 1);
		assertEquals("user", roles[0].getRoleId());
    }
    
    /**
     * 當request parameter 不完整無法將該使用者加入到特定專案中。
     * request parameter包含
     * 1. id (O)
     * 2. resource (X)
     * 3. operation (O)
     */
    public void testAddUserAction_NullResource_RequestParameter(){
    	//	=============== common data ============================
    	IAccount account = this.CA.getAccountList().get(0);
    	String userId = this.CA.getAccount_ID(1);
    	String projectID = this.CP.getProjectList().get(0).getName();
    	String scrumRole = "ProductOwner";
    	
    	/**
    	 * 3. admin assign this account to the project
    	 */
    	
    	// ================ set action info ========================
    	setRequestPathInformation( this.ActionPath_AddUser );
    	
    	// ================== set parameter info ====================
    	addRequestParameter("id", userId);
//    	addRequestParameter("resource", projectID);
    	addRequestParameter("operation", scrumRole);
    	    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	
    	// ================ set URL parameter ========================    	
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ 執行 add user action ======================
    	actionPerform();
    	
    	// ================ assert ========================
    	//	assert response text
    	verifyNoActionMessages();
    	
    	//	assert database information
		// 	測試 Role 是否正確
		IRole[] roles = account.getRoles();
		assertEquals(roles.length, 1);
		assertEquals("user", roles[0].getRoleId());
    }
    
    /**
     * 當request parameter 不完整無法將該使用者加入到特定專案中。
     * request parameter包含
     * 1. id (O)
     * 2. resource (O)
     * 3. operation (X)
     */
    public void testAddUserAction_NullOperation_RequestParameter(){
    	//	=============== common data ============================
    	IAccount account = this.CA.getAccountList().get(0);
    	String userId = this.CA.getAccount_ID(1);
    	String projectID = this.CP.getProjectList().get(0).getName();
    	
    	/**
    	 * 3. admin assign this account to the project
    	 */
    	
    	// ================ set action info ========================
    	setRequestPathInformation( this.ActionPath_AddUser );
    	
    	// ================== set parameter info ====================
    	addRequestParameter("id", userId);
    	addRequestParameter("resource", projectID);
//    	addRequestParameter("operation", scrumRole);
    	    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	
    	// ================ set URL parameter ========================    	
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ 執行 add user action ======================
    	actionPerform();
    	
    	// ================ assert ========================
    	//	assert response text
    	verifyNoActionMessages();
    	
    	//	assert database information
		// 	測試 Role 是否正確
		IRole[] roles = account.getRoles();
		assertEquals(roles.length, 1);
		assertEquals("user", roles[0].getRoleId());
    }
    
    private IUserSession getUserSession(IAccount account){
    	IUserSession userSession = new UserSession(account);
    	return userSession;
    }
/*    
 * 待修正: AddUserAction.java #68 account = null 未作妥善處理
    // 測試錯誤不存在的 ID
    public void testexecute_Wrong1() throws LogonException {
    	// ================ set initial data =======================
    	String id = "????";
    	String Project_ID = this.CP.getProjectList().get(0).getName();
    	String Actor = "ProductOwner";
    	// ================ set initial data =======================
    	
    	
    	// ================== set parameter info ====================
    	addRequestParameter("id", id);
    	addRequestParameter("resource", Project_ID);
    	addRequestParameter("operation", Actor);
    	// ================== set parameter info ====================
    	
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	// ================ set session info ========================
    	

		actionPerform();		// 執行 action
    	
    	IAccountManager am = AccountFactory.getManager();
		IAccount account = am.getAccount(id);
		assertNull(account);
    }
*/ 
    
/*  
 * 待修正: AddUserAction.java #40 updateAccount() 未作妥善處理導致存取資料庫錯誤
    // 測試錯誤不存在的專案名稱
    public void testexecute_Wrong2() throws LogonException {
    	// ================ set initial data =======================
    	String id = this.CA.getAccount_ID(1);		// 取得第一筆 Account ID
    	String Project_ID = "???";
    	String Actor = "ProductOwner";
    	// ================ set initial data =======================
    	
    	
    	// ================== set parameter info ====================
    	addRequestParameter("id", id);
    	addRequestParameter("resource", Project_ID);
    	addRequestParameter("operation", Actor);
    	// ================== set parameter info ====================
    	
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	// ================ set session info ========================
    	
    	actionPerform();		// 執行 action
    	
    	IAccountManager am = AccountFactory.getManager();
		IAccount account = am.getAccount(id);
		IRole[] roles = account.getRoles();
		
		// 測試沒有正確寫入
		assertEquals(roles.length, 0);
    }

    
/* 
 * 待修正: AddUserAction.java #40 updateAccount() 未作妥善處理導致存取資料庫錯誤  
    // 測試錯誤不存在的角色權限
    public void testexecute_Wrong3() throws LogonException {
    	// ================ set initial data =======================
    	String id = this.CA.getAccount_ID(1);		// 取得第一筆 Account ID
    	String Project_ID = this.CP.getProjectList().get(0).getName();
    	String Actor = "????????";
    	// ================ set initial data =======================
    	
    	
    	// ================== set parameter info ====================
    	addRequestParameter("id", id);
    	addRequestParameter("resource", Project_ID);
    	addRequestParameter("operation", Actor);
    	// ================== set parameter info ====================
    	
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
    	// ================ set session info ========================
    	
    	actionPerform();		// 執行 action
    	
    	IAccountManager am = AccountFactory.getManager();
		IAccount account = am.getAccount(id);
		IRole[] roles = account.getRoles();
		
		// 測試沒有正確寫入
		assertEquals(roles.length, 0);
    }

*/

}