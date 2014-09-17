package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map.Entry;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.TestTool;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.dataObject.RoleEnum;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.form.LogonForm;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.LogonException;
import servletunit.struts.MockStrutsTestCase;

public class AddUserActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateAccount CA;
	private int ProjectCount = 1;
	private int AccountCount = 1;
	private String ActionPath_AddUser = "/addUser";	// defined in "struts-config.xml"

	private Configuration configuration;
	private AccountMapper accountMapper;

	public AddUserActionTest(String testMethod) {
		super(testMethod);
	}

	private void setRequestPathInformation(String actionPath) {
		setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(actionPath);
	}

	/**
	 * clean previous action info
	 */
	private void cleanActionInformation() {
		clearRequestParameters();
		this.response.reset();
	}

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		InitialSQL ini = new InitialSQL(configuration);
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
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL

		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());
		
		configuration.setTestMode(false);
		configuration.store();

		super.tearDown();

		// ============= release ==============
		this.accountMapper.releaseManager();
		ini = null;
		projectManager = null;
		this.CP = null;
		this.CA = null;
		this.config = null;
		this.accountMapper = null;
		configuration = null;
	}

	// 以一般使用者身分執行
	public void testAddUserAction_user() throws LogonException {
		setRequestPathInformation(this.ActionPath_AddUser);

		// ================ set initial data =======================
		String id = this.CA.getAccountList().get(0).getId();		// 取得第一筆 Account ID
		String Project_ID = this.CP.getProjectObjectList().get(0).getId();
		String pid = this.CP.getProjectObjectList().get(0).getName();
		String Actor = RoleEnum.ProductOwner.name();	// ?

		// ================== set parameter info ====================
		addRequestParameter("id", id);
		addRequestParameter("resource", Project_ID);
		addRequestParameter("operation", Actor);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		// ================ set URL parameter ========================    	
		request.setHeader("Referer", "?PID=" + pid);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// 執行 action
		actionPerform();

		/*
		 * Verify:
		 */

		UserObject account = this.accountMapper.getAccountById(id);
		assertNotNull(account);
		assertEquals(this.CA.getAccount_ID(1), account.getAccount());
		assertEquals(this.CA.getAccount_RealName(1), account.getName());
		assertEquals("true", account.getEnable());

		assertEquals((new TestTool()).getMd5(this.CA.getAccount_PWD(1)), account.getPassword());
		assertEquals(this.CA.getAccount_Mail(1), account.getEmail());

		// 測試 Role 是否正確
		//		String expectedUserRole_PO = (new TestTool()).getRole(Project_ID, Actor);
		//		String expectedUserRole_USER = "user";
		HashMap<String, ProjectRole> roleMap = account.getRoles();
		//		String[] userRole = {expectedUserRole_USER, expectedUserRole_PO};
		//		for (String roleID : userRole) {
		boolean isExisted = false;
		for (Entry<String, ProjectRole> role : roleMap.entrySet()) {
			if (Actor.equals(role.getValue().getScrumRole().getRoleName())) {
				isExisted = true;
				break;
			}
		}
		assertTrue(isExisted);
		//		}
		assertEquals(roleMap.size(), 1);		// ProductOwner
	}

	// 以系統管理員身分執行
	public void testAddUserAction_admin() throws LogonException {
		setRequestPathInformation(this.ActionPath_AddUser);

		// ================ set initial data =======================
		String id = "1";			// 取得第一筆 Account ID
		String accountId = "admin";	// 取得第一筆 Account ID
		String Project_ID = this.CP.getProjectObjectList().get(0).getId();
		String projectName = this.CP.getProjectObjectList().get(0).getName();
		String Actor = "ProductOwner";	// ?
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("id", id);
		addRequestParameter("resource", Project_ID);
		addRequestParameter("operation", Actor);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		// ================ set session info ========================

		// ================ set URL parameter ========================    	
		request.setHeader("Referer", "?PID=" + projectName);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		// ================ set URL parameter ========================

		actionPerform();		// 執行 action

		/*
		 * Verify:
		 */
		UserObject account = this.accountMapper.getAccount(accountId);

		assertNotNull(account);
		assertEquals(accountId, account.getAccount());
		assertEquals(ScrumEnum.SCRUMROLE_ADMIN, account.getName());
		assertEquals("true", account.getEnable());

		// 更新囉 ilove306 -> admin	
		assertEquals(account.getPassword(), (new TestTool()).getMd5("admin"));
		// 更新囉 null -> example@ezScrum.tw
		assertEquals(account.getEmail(), "example@ezScrum.tw");

		// Role verify
		HashMap<String, ProjectRole> roleMap = account.getRoles();

		assertEquals(roleMap.size(), 2);	// PO and admin

		assertEquals(Actor, roleMap.get(projectName).getScrumRole().getRoleName());
		assertEquals(accountId, roleMap.get("system").getScrumRole().getRoleName());
		// 測試專案的 Role 是否正確加入，並且沒有移除 admin 權限
		//		String[] Role_ID = new String[roleMap.length];
		//		for (int i = 0; i < roleMap.length; i++) {
		//			Role_ID[i] = roleMap[i].getRoleId();
		//		}
		// 利用 Rold ID 抽出來後排序，再做比對
		//		Arrays.sort(Role_ID);

		//		assertEquals(Role_ID[0], (new TestTool()).getRole(Project_ID, Actor));
		//		assertEquals(Role_ID[1], ScrumEnum.SCRUMROLE_ADMIN);
		//		assertEquals(Role_ID[2], "user");
	}

	/**
	 * Integration Test Steps 
	 * 1. admin 新增專案 (setup done) 
	 * 2. admin 新增帳號 (setup done) 
	 * 3. admin assign this account to the project 
	 * 4. user login ezScrum 
	 * 5. user view project list 
	 * 6. user select project
	 * 
	 * @throws InterruptedException
	 */
	public void testAddUserAction_IntegrationTest() throws InterruptedException {
		//	=============== common data ============================
		UserObject account = this.CA.getAccountList().get(0);
		IUserSession userSession = getUserSession(account);
		String userId = account.getId();		// 取得第一筆 Account ID
		String projectID = this.CP.getProjectObjectList().get(0).getId();
		String projectName = this.CP.getProjectObjectList().get(0).getName();

		/**
		 * 3. admin assign this account to the project
		 */

		// ================ set action info ========================
		setRequestPathInformation(this.ActionPath_AddUser);

		// ================ set initial data =======================
		String scrumRole = "ProductOwner";

		// ================== set parameter info ====================
		addRequestParameter("id", userId);
		addRequestParameter("resource", projectID);
		addRequestParameter("operation", scrumRole);

		// ================ set session info with admin ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		// ================ set URL parameter ========================    	
		request.setHeader("Referer", "?PID=" + projectName);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 add user action ======================
		actionPerform();

		// ================ assert ========================
		//	assert response text
		String expectedUserRole_PO = (new TestTool()).getRole(projectName, scrumRole);
		//		String expectedUserRole_USER = "user";
		String expectedUserId = account.getId();
		String expectedUserAccount = account.getAccount();
		String expectedUserName = account.getName();
		String expectedUserPassword = (new TestTool()).getMd5(this.CA.getAccount_PWD(1));
		String expectedUserMail = account.getEmail();
		String expectedUserEnable = account.getEnable();
		//		account.addRole(new Role(expectedUserRole_PO, expectedUserRole_PO));
		//		String expectedUserRole = roleArr[0].getRoleId() + ", " + roleArr[1].getRoleId();
		StringBuilder addUserExpectedResponseText = new StringBuilder();
		addUserExpectedResponseText.append("<Accounts>")
		        .append("<AccountInfo>")
		        .append("<ID>").append(expectedUserId).append("</ID>")
		        .append("<Account>").append(expectedUserAccount).append("</Account>")
		        .append("<Name>").append(expectedUserName).append("</Name>")
		        .append("<Mail>").append(expectedUserMail).append("</Mail>")
		        .append("<Roles>").append(expectedUserRole_PO).append("</Roles>")
		        .append("<Enable>").append(expectedUserEnable).append("</Enable>")
		        .append("</AccountInfo>")
		        .append("</Accounts>");
		String addUserActualResponseText = this.response.getWriterBuffer().toString();
		assertEquals(addUserExpectedResponseText.toString(), addUserActualResponseText);

		//	assert database information
		UserObject actualAccount = this.accountMapper.getAccountById(userId);
		HashMap<String, ProjectRole> roleMap = actualAccount.getRoles();
		assertNotNull(account);
		assertEquals(expectedUserId, actualAccount.getId());
		assertEquals(expectedUserAccount, actualAccount.getAccount());
		assertEquals(expectedUserName, actualAccount.getName());
		assertEquals(expectedUserPassword, actualAccount.getPassword());
		assertEquals(expectedUserMail, actualAccount.getEmail());
		assertEquals(expectedUserEnable, actualAccount.getEnable());

		// 測試 Role 是否正確
		boolean isExisted = false;
		for (Entry<String, ProjectRole> role : roleMap.entrySet()) {
			if (scrumRole.equals(role.getValue().getScrumRole().getRoleName())) {
				isExisted = true;
				break;
			}
		}
		assertEquals(roleMap.size(), 1);	// ProductOwner
		assertTrue(isExisted);				// ProductOwner

		/**
		 * 4. user login ezScrum
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();

		// ================ set action info ========================
		String actionPath_logonSubmit = "/logonSubmit";
		setRequestPathInformation(actionPath_logonSubmit);

		// ================== set parameter info ====================
		LogonForm logonForm = new LogonForm();
		logonForm.setUserId(account.getAccount());
		logonForm.setPassword(this.CA.getAccount_PWD(1));
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
		setRequestPathInformation(actionPath_viewProjectList);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", userSession);

		// ================ 執行 view project list action ======================
		actionPerform();

		// ================ assert ========================
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		ProjectObject expectedProject = this.CP.getProjectObjectList().get(0);
		String expectedProjectDemoDate = "No Plan!";
		//	assert response text
		StringBuilder viewProjectListExpectedResponseText = new StringBuilder();
		viewProjectListExpectedResponseText.append("<Projects>")
		        .append("<Project>")
		        .append("<ID>").append(expectedProject.getName()).append("</ID>")
		        .append("<Name>").append(expectedProject.getDisplayName()).append("</Name>")
		        .append("<Comment>").append(expectedProject.getComment()).append("</Comment>")
		        .append("<ProjectManager>").append(expectedProject.getManager()).append("</ProjectManager>")
		        .append("<CreateDate>").append(dateFormat.format(expectedProject.getCreateDate())).append("</CreateDate>")
		        .append("<DemoDate>").append(expectedProjectDemoDate).append("</DemoDate>")
		        .append("</Project>")
		        .append("</Projects>");
		String viewProjectListActualResponseText = this.response.getWriterBuffer().toString();
		assertEquals(viewProjectListExpectedResponseText.toString(), viewProjectListActualResponseText);

		/**
		 * 6. user select project
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();

		// ================ set action info ========================
		String actionPath_viewProject = "/viewProject";
		setRequestPathInformation(actionPath_viewProject);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", userSession);

		// ================== set parameter info ====================
		addRequestParameter("PID", projectName);

		// ================ 執行 view project action ======================
		actionPerform();

		// ================ assert ======================
		verifyForward("SummaryView");
	}

	/**
	 * 當request parameter 不完整無法將該使用者加入到特定專案中。 request parameter包含 
	 * 1. id (X) 
	 * 2. resource (O) 
	 * 3. operation (O)
	 */
	public void testAddUserAction_NullID_RequestParameter() {
		//	=============== common data ============================
		UserObject account = this.CA.getAccountList().get(0);
		String projectID = this.CP.getProjectObjectList().get(0).getId();
		String pid = this.CP.getProjectObjectList().get(0).getName();
		String scrumRole = "ProductOwner";

		/**
		 * 3. admin assign this account to the project
		 */

		// ================ set action info ========================
		setRequestPathInformation(this.ActionPath_AddUser);

		// ================== set parameter info ====================
		//    	addRequestParameter("id", userId);
		addRequestParameter("resource", projectID);
		addRequestParameter("operation", scrumRole);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		// ================ set URL parameter ========================    	
		request.setHeader("Referer", "?PID=" + pid);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 add user action ======================
		actionPerform();

		// ================ assert ========================
		//	assert response text
		verifyNoActionMessages();

		//	assert database information
		// 	測試 Role 是否正確
		HashMap<String, ProjectRole> roles = account.getRoles();
		assertEquals(roles.size(), 0);
		//		assertEquals("user", roles[0].getRoleId());
	}

	/**
	 * 當request parameter 不完整無法將該使用者加入到特定專案中。 request parameter包含 
	 * 1. id (O) 
	 * 2. resource (X) 
	 * 3. operation (O)
	 */
	public void testAddUserAction_NullResource_RequestParameter() {
		//	=============== common data ============================
		UserObject account = this.CA.getAccountList().get(0);
		String userId = this.CA.getAccount_ID(1);
		String projectID = this.CP.getProjectList().get(0).getName();
		String scrumRole = "ProductOwner";

		/**
		 * 3. admin assign this account to the project
		 */

		// ================ set action info ========================
		setRequestPathInformation(this.ActionPath_AddUser);

		// ================== set parameter info ====================
		addRequestParameter("id", userId);
		//    	addRequestParameter("resource", projectID);
		addRequestParameter("operation", scrumRole);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		// ================ set URL parameter ========================    	
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 add user action ======================
		actionPerform();

		// ================ assert ========================
		//	assert response text
		verifyNoActionMessages();

		//	assert database information
		// 	測試 Role 是否正確
		HashMap<String, ProjectRole> roles = account.getRoles();
		assertEquals(roles.size(), 0);
		//		assertEquals("user", roles[0].getRoleId());
	}

	/**
	 * 當request parameter 不完整無法將該使用者加入到特定專案中。 request parameter包含 
	 * 1. id (O) 
	 * 2. resource (O) 
	 * 3. operation (X)
	 */
	public void testAddUserAction_NullOperation_RequestParameter() {
		//	=============== common data ============================
		UserObject account = this.CA.getAccountList().get(0);
		String userId = this.CA.getAccount_ID(1);
		String projectID = this.CP.getProjectObjectList().get(0).getId();
		String pid = this.CP.getProjectObjectList().get(0).getName();

		/**
		 * 3. admin assign this account to the project
		 */

		// ================ set action info ========================
		setRequestPathInformation(this.ActionPath_AddUser);

		// ================== set parameter info ====================
		addRequestParameter("id", userId);
		addRequestParameter("resource", projectID);
		//    	addRequestParameter("operation", scrumRole);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		// ================ set URL parameter ========================    	
		request.setHeader("Referer", "?PID=" + pid);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 add user action ======================
		actionPerform();

		// ================ assert ========================
		//	assert response text
		verifyNoActionMessages();

		//	assert database information
		// 	測試 Role 是否正確
		HashMap<String, ProjectRole> roles = account.getRoles();
		assertEquals(roles.size(), 0);
		//		assertEquals("user", roles[0].getRoleId());
	}

	private IUserSession getUserSession(UserObject account) {
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