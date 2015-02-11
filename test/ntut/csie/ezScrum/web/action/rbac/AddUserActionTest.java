package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map.Entry;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.TestTool;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;
import ntut.csie.ezScrum.web.form.LogonForm;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.LogonException;
import servletunit.struts.MockStrutsTestCase;

public class AddUserActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateAccount mCA;
	private int mProjectCount = 1;
	private int mAccountCount = 1;
	private String mActionPath = "/addUser";
	private Configuration mConfig;
	private AccountMapper mAccountMapper;

	public AddUserActionTest(String testMethod) {
		super(testMethod);
	}

	private void setRequestPathInformation(String actionPath) {
		/**
		 * 設定讀取的 struts-config 檔案路徑
		 * GetUserDataAction
		 */
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(actionPath);
	}

	/**
	 * clean previous action info
	 */
	private void cleanActionInformation() {
		clearRequestParameters();
		response.reset();
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增 Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		// 新增使用者
		mCA = new CreateAccount(mAccountCount);
		mCA.exe();

		mAccountMapper = new AccountMapper();

		super.setUp();
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
		projectManager = null;
		mCP = null;
		mCA = null;
		mAccountMapper = null;
		mConfig = null;
	}

	// 以一般使用者身分執行
	public void testAddUserAction_user() {
		setRequestPathInformation(mActionPath);

		// ================ set initial data =======================
		long accountId = mCA.getAccountList().get(0).getId();
		long projectId = mCP.getAllProjects().get(0).getId();
		String pid = mCP.getAllProjects().get(0).getName();
		String roleName = RoleEnum.ProductOwner.name();

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(accountId));
		addRequestParameter("resource", String.valueOf(projectId));
		addRequestParameter("operation", roleName);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ set URL parameter ========================
		// SessionManager 會對 URL 的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + pid);

		// 執行 action
		actionPerform();

		AccountObject account = mAccountMapper.getAccount(accountId);
		assertNotNull(account);
		assertEquals(mCA.getAccount_ID(1), account.getUsername());
		assertEquals(mCA.getAccount_RealName(1), account.getNickName());
		assertEquals(true, account.getEnable());

		assertEquals((new TestTool()).getMd5(mCA.getAccount_PWD(1)),
				account.getPassword());
		assertEquals(mCA.getAccount_Mail(1), account.getEmail());

		// 測試 Role 是否正確
		HashMap<String, ProjectRole> roleMap = account.getRoles();
		boolean isExisted = false;
		for (Entry<String, ProjectRole> role : roleMap.entrySet()) {
			if (roleName.equals(role.getValue().getScrumRole().getRoleName())) {
				isExisted = true;
				break;
			}
		}
		assertTrue(isExisted);
		assertEquals(roleMap.size(), 1);// ProductOwner
	}

	// 以系統管理員身分執行
	public void testAddUserAction_admin() {
		setRequestPathInformation(mActionPath);

		// ================ set initial data =======================
		String accountId = "1";
		String username = "admin";
		long projectId = mCP.getAllProjects().get(0).getId();
		String projectName = mCP.getAllProjects().get(0).getName();
		String Actor = "ProductOwner"; // ?

		// ================== set parameter info ====================
		addRequestParameter("id", accountId);
		addRequestParameter("resource", String.valueOf(projectId));
		addRequestParameter("operation", Actor);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ set URL parameter ========================
		// SessionManager 會對 URL 的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + projectName);

		// 執行 action
		actionPerform();

		AccountObject account = mAccountMapper.getAccount(username);
		assertNotNull(account);
		assertEquals(username, account.getUsername());
		assertEquals("admin", account.getNickName());
		assertEquals(true, account.getEnable());

		// 更新囉 ilove306 -> admin
		assertEquals(account.getPassword(), (new TestTool()).getMd5("admin"));
		// 更新囉 null -> example@ezScrum.tw
		assertEquals(account.getEmail(), "example@ezScrum.tw");

		// Role verify
		HashMap<String, ProjectRole> roleMap = account.getRoles();

		assertEquals(roleMap.size(), 2); // PO and admin

		assertEquals(Actor, roleMap.get(projectName).getScrumRole()
				.getRoleName());
		assertEquals(username, roleMap.get("system").getScrumRole()
				.getRoleName());
	}

	/**
	 * Integration Test Steps 1. admin 新增專案 (setup done) 2. admin 新增帳號 (setup
	 * done) 3. admin assign this account to the project 4. user login ezScrum
	 * 5. user view project list 6. user select project
	 * 
	 * @throws InterruptedException
	 */
	public void testAddUserAction_IntegrationTest() {
		// =============== common data ============================
		AccountObject account = mCA.getAccountList().get(0);
		IUserSession userSession = getUserSession(account);
		long accountId = account.getId();
		long projectId = mCP.getAllProjects().get(0).getId();
		String projectName = mCP.getAllProjects().get(0).getName();

		/**
		 * 3. admin assign this account to the project
		 */
		// ================ set action info ========================
		setRequestPathInformation(mActionPath);

		// ================ set initial data =======================
		String scrumRole = "ProductOwner";

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(accountId));
		addRequestParameter("resource", String.valueOf(projectId));
		addRequestParameter("operation", scrumRole);

		// ================ set session info with admin ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ set URL parameter ========================
		// SessionManager 會對 URL 的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + projectName);

		// ================ 執行 add user action ======================
		actionPerform();

		// ================ assert ========================
		// assert response text
		String expectedUserRole_PO = (new TestTool()).getRole(projectName,
				scrumRole);
		long expectedUserId = account.getId();
		String expectedUserAccount = account.getUsername();
		String expectedUserName = account.getNickName();
		String expectedUserPassword = (new TestTool()).getMd5(mCA
				.getAccount_PWD(1));
		String expectedUserMail = account.getEmail();
		boolean expectedUserEnable = account.getEnable();
		StringBuilder addUserExpectedResponseText = new StringBuilder();
		addUserExpectedResponseText.append("<Accounts>")
				.append("<AccountInfo>").append("<ID>").append(expectedUserId)
				.append("</ID>").append("<Account>")
				.append(expectedUserAccount).append("</Account>")
				.append("<Name>").append(expectedUserName).append("</Name>")
				.append("<Mail>").append(expectedUserMail).append("</Mail>")
				.append("<Roles>").append(expectedUserRole_PO)
				.append("</Roles>").append("<Enable>")
				.append(expectedUserEnable).append("</Enable>")
				.append("</AccountInfo>").append("</Accounts>");
		String addUserActualResponseText = response.getWriterBuffer()
				.toString();
		assertEquals(addUserExpectedResponseText.toString(),
				addUserActualResponseText);

		// assert database information
		AccountObject actualAccount = mAccountMapper.getAccount(accountId);
		HashMap<String, ProjectRole> roleMap = actualAccount.getRoles();
		assertNotNull(account);
		assertEquals(expectedUserId, actualAccount.getId());
		assertEquals(expectedUserAccount, actualAccount.getUsername());
		assertEquals(expectedUserName, actualAccount.getNickName());
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
		assertEquals(roleMap.size(), 1); // ProductOwner
		assertTrue(isExisted); // ProductOwner

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
		logonForm.setUserId(account.getUsername());
		logonForm.setPassword(mCA.getAccount_PWD(1));
		setActionForm(logonForm);

		// ================ 執行 login action ======================
		actionPerform();

		// ================ assert ======================
		verifyForward("success");

		/**
		 * 5. view project list
		 */
		account = mAccountMapper.getAccount(accountId);
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
		ProjectObject expectedProject = mCP.getAllProjects().get(0);
		String expectedProjectDemoDate = "No Plan!";
		// assert response text
		StringBuilder viewProjectListExpectedResponseText = new StringBuilder();
		viewProjectListExpectedResponseText.append("<Projects>")
				.append("<Project>").append("<ID>")
				.append(expectedProject.getName()).append("</ID>")
				.append("<Name>").append(expectedProject.getDisplayName())
				.append("</Name>").append("<Comment>")
				.append(expectedProject.getComment()).append("</Comment>")
				.append("<ProjectManager>")
				.append(expectedProject.getManager())
				.append("</ProjectManager>").append("<CreateDate>")
				.append(dateFormat.format(expectedProject.getCreateTime()))
				.append("</CreateDate>").append("<DemoDate>")
				.append(expectedProjectDemoDate).append("</DemoDate>")
				.append("</Project>").append("</Projects>");
		String viewProjectListActualResponseText = response.getWriterBuffer()
				.toString();
		assertEquals(viewProjectListExpectedResponseText.toString(),
				viewProjectListActualResponseText);

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
	 * 當request parameter 不完整無法將該使用者加入到特定專案中。 request parameter包含 1. id (X) 2.
	 * resource (O) 3. operation (O)
	 */
	public void testAddUserAction_NullID_RequestParameter() {
		// =============== common data ============================
		AccountObject account = mCA.getAccountList().get(0);
		long projectId = mCP.getAllProjects().get(0).getId();
		String projectName = mCP.getAllProjects().get(0).getName();
		String scrumRole = "ProductOwner";

		/**
		 * 3. admin assign this account to the project
		 */

		// ================ set action info ========================
		setRequestPathInformation(mActionPath);

		// ================== set parameter info ====================
		addRequestParameter("resource", String.valueOf(projectId));
		addRequestParameter("operation", scrumRole);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ set URL parameter ========================
		// SessionManager 會對 URL 的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + projectName);

		// ================ 執行 add user action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionMessages();

		HashMap<String, ProjectRole> roles = account.getRoles();
		assertEquals(roles.size(), 0);
	}

	/**
	 * 當 request parameter 不完整無法將該使用者加入到特定專案中。 request parameter 包含 1. id (O) 2.
	 * resource (X) 3. operation (O)
	 */
	public void testAddUserAction_NullResource_RequestParameter() {
		// =============== common data ============================
		AccountObject account = mCA.getAccountList().get(0);
		long accountId = account.getId();
		String scrumRole = "ProductOwner";

		/**
		 * 3. admin assign this account to the project
		 */

		// ================ set action info ========================
		setRequestPathInformation(mActionPath);

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(accountId));
		addRequestParameter("operation", scrumRole);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ 執行 add user action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionMessages();

		// assert database information
		// 測試 Role 是否正確
		HashMap<String, ProjectRole> roles = account.getRoles();
		assertEquals(roles.size(), 0);
	}

	/**
	 * 當request parameter 不完整無法將該使用者加入到特定專案中。 request parameter 包含 1. id (O) 2.
	 * resource (O) 3. operation (X)
	 */
	public void testAddUserAction_NullOperation_RequestParameter() {
		// =============== common data ============================
		AccountObject account = mCA.getAccountList().get(0);
		long accountId = account.getId();
		long projectId = mCP.getAllProjects().get(0).getId();
		String pid = mCP.getAllProjects().get(0).getName();

		/**
		 * 3. admin assign this account to the project
		 */

		// ================ set action info ========================
		setRequestPathInformation(mActionPath);

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(accountId));
		addRequestParameter("resource", String.valueOf(projectId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ set URL parameter ========================
		// SessionManager 會對 URL 的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + pid);

		// ================ 執行 add user action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionMessages();

		// assert database information
		// 測試 Role 是否正確
		HashMap<String, ProjectRole> roles = account.getRoles();
		assertEquals(roles.size(), 0);
	}

	private IUserSession getUserSession(AccountObject account) {
		IUserSession userSession = new UserSession(account);
		return userSession;
	}

	// 測試錯誤不存在的 id
	public void testExecute_Wrong1() throws LogonException {
		// ================ set initial data =======================
		long accountId = 8591;
		long projectId = mCP.getAllProjects().get(0).getId();
		String scrumRole = "ProductOwner";

		// ================ set action info ========================
		setRequestPathInformation(mActionPath);

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(accountId));
		addRequestParameter("resource", String.valueOf(projectId));
		addRequestParameter("operation", scrumRole);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// 執行 action
		actionPerform();

		String actualResponse = response.getWriterBuffer().toString();
		String expectResponse = "<Accounts>Account not found.</Accounts>";
		assertEquals(expectResponse, actualResponse);
	}

	// 測試錯誤不存在的專案名稱
	public void testExecute_Wrong2() throws LogonException {
		// ================ set initial data =======================
		AccountObject account = mCA.getAccountList().get(0);
		long accountId = account.getId();
		long projectId = 9527;
		String scrumRole = "ProductOwner";

		// ================ set action info ========================
		setRequestPathInformation(mActionPath);

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(accountId));
		addRequestParameter("resource", String.valueOf(projectId));
		addRequestParameter("operation", scrumRole);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// 執行 action
		actionPerform();

		// 測試沒有正確寫入
		HashMap<String, ProjectRole> roles = account.getRoles();
		assertEquals(roles.size(), 0);
	}

	// 測試錯誤不存在的角色權限
	public void testExecute_Wrong3() throws LogonException {
		// ================ set initial data =======================
		AccountObject account = mCA.getAccountList().get(0);
		long accountId = account.getId();
		long projectId = mCP.getAllProjects().get(0).getId();
		String scrumRole = "????????";

		// ================ set action info ========================
		setRequestPathInformation(mActionPath);

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(accountId));
		addRequestParameter("resource", String.valueOf(projectId));
		addRequestParameter("operation", scrumRole);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// 執行 action
		actionPerform();

		// 測試沒有正確寫入
		HashMap<String, ProjectRole> roles = account.getRoles();
		assertEquals(roles.size(), 0);
	}
}