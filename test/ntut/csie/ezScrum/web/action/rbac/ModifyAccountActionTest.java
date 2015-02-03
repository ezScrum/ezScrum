package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.TestTool;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.form.LogonForm;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.LogonException;
import servletunit.struts.MockStrutsTestCase;

// 系統管理員更新資料
public class ModifyAccountActionTest extends MockStrutsTestCase {

	private CreateProject CP;
	private CreateAccount CA;
	private int ProjectCount = 1;
	private int AccountCount = 1;
	private final String ActionPath_ModifyAccount = "/modifyAccount";	// defined in "struts-config.xml"

	private Configuration configuration;
	private AccountMapper accountMapper;

	public ModifyAccountActionTest(String testMethod) {
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
		configuration.save();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL

		// 新增Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();

		this.accountMapper = new AccountMapper();

		super.setUp();

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());

		configuration.setTestMode(false);
		configuration.save();
		
		super.tearDown();

		// ============= release ==============
		ini = null;
		projectManager = null;
		this.CP = null;
		this.CA = null;
		this.config = null;
		this.accountMapper = null;
		configuration = null;
	}

	/**
	 * admin 新增帳號
	 * 
	 * @throws LogonException
	 */
	public void testModifyAccountAction_create() throws LogonException {
		setRequestPathInformation(this.ActionPath_ModifyAccount);

		// ================ set initial data =======================
		String projectId = this.CP.getProjectList().get(0).getName();
		// User Information
		String userAccount = "TEST_ACCOUNT_ID";		// 取得第一筆 Account ID
		String userPw = "TEST_ACCOUNT_PW";
		String userMail = "TEST_ACCOUNT_MAIL";
		String userName = "TEST_ACCOUNT_REALNAME";
		String userEnable = "true";	// default is true
		String userIsEdit = "false";	// false 代表是新增帳號
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("account", userAccount);
		addRequestParameter("passwd", userPw);
		addRequestParameter("mail", userMail);
		addRequestParameter("name", userName);
		addRequestParameter("enable", userEnable);
		addRequestParameter("isEdit", userIsEdit);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform();		// 執行 action

		// ================ assert ========================
		AccountObject account = this.accountMapper.getAccount(userAccount);

		assertNotNull(account);
		assertEquals(userAccount, account.getUsername());
		assertEquals((new TestTool()).getMd5(userPw), account.getPassword());
		assertEquals(userMail, account.getEmail());
		assertEquals(userName, account.getNickName());
		assertEquals(userEnable, account.getEnable());
	}

	public void testModifyAccountAction_update() throws LogonException {
		setRequestPathInformation(this.ActionPath_ModifyAccount);
		// 新增使用者
		this.CA = new CreateAccount(this.AccountCount);
		this.CA.exe();

		// ================ set initial data =======================
		String projectId = this.CP.getProjectList().get(0).getName();
		// User Information
		String postfix = "_update";
		AccountObject user = CA.getAccountList().get(0);
		String userId = user.getId() + "";			// 取得第一筆 ID
		String userAccount = user.getUsername();	// 取得第一筆 Account ID
		String userPw = user.getPassword() + postfix;
		String userMail = "modify@test.com";
		String userName = user.getNickName() + postfix;
		String userEnable = "false";			// default is true
		String userIsEdit = "true";	// false 代表是新增帳號

		// ================== set parameter info ====================
		addRequestParameter("id", userId);
		addRequestParameter("account", userAccount);
		addRequestParameter("passwd", userPw);
		addRequestParameter("mail", userMail);
		addRequestParameter("name", userName);
		addRequestParameter("enable", userEnable);
		addRequestParameter("isEdit", userIsEdit);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// 執行 action
		actionPerform();

		// ================ assert ========================
		AccountObject account = this.accountMapper.getAccount(userAccount);

		assertNotNull(account);
		assertEquals(userAccount, account.getUsername());
		assertEquals((new TestTool()).getMd5(userPw), account.getPassword());
		assertEquals(userMail, account.getEmail());
		assertEquals(userName, account.getNickName());
		assertEquals(userEnable, account.getEnable());
	}

	/**
	 * admin更新使用者資訊
	 */
	public void testModifyAccountAction_updateUserInformation() {
		setRequestPathInformation(this.ActionPath_ModifyAccount);
		// 新增使用者
		this.CA = new CreateAccount(this.AccountCount);
		this.CA.exe();

		// ================ set initial data =======================
		String projectId = this.CP.getProjectList().get(0).getName();
		// User Information
		String postfix = "_update";

		AccountObject user = CA.getAccountList().get(0);
		String userId = user.getId() + "";			// 取得第一筆 ID
		String userAccount = user.getUsername();	// 取得第一筆 Account ID
		String userMail = "modify@test.com";
		String userName = user.getNickName() + postfix;
		String userEnable = "false";	// default is true
		String userIsEdit = "true";		// false 代表是新增帳號

		// ================== set parameter info ====================
		addRequestParameter("id", userId);
		addRequestParameter("account", userAccount);
		addRequestParameter("mail", userMail);
		addRequestParameter("name", userName);
		addRequestParameter("enable", userEnable);
		addRequestParameter("isEdit", userIsEdit);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		// assert response text
		//		String userScrumRole = "user";
		String expectResponseText = "<Accounts>" +
		        "<AccountInfo>" +
		        "<ID>" + userId + "</ID>" +
		        "<Account>" + userAccount + "</Account>" +
		        "<Name>" + userName + "</Name>" +
		        "<Mail>" + userMail + "</Mail>" +
		        //		        "<Roles>" + userScrumRole + "</Roles>" +
		        "<Roles></Roles>" +
		        "<Enable>" + userEnable + "</Enable>" +
		        "</AccountInfo>" +
		        "</Accounts>";
		String acutalResponseText = response.getWriterBuffer().toString();
		assertEquals(expectResponseText, acutalResponseText);

		// assert database information
		AccountObject account = this.accountMapper.getAccount(userAccount);
		String expectUserPassword = this.CA.getAccount_PWD(1);

		assertNotNull(account);
		assertEquals(userAccount, account.getUsername());
		assertEquals((new TestTool()).getMd5(expectUserPassword), account.getPassword());
		assertEquals(userMail, account.getEmail());
		assertEquals(userName, account.getNickName());
		assertEquals(userEnable, account.getEnable());
	}

	/**
	 * admin更新使用者密碼
	 */
	public void testModifyAccountAction_updateUserPassword() {
		setRequestPathInformation(this.ActionPath_ModifyAccount);
		// 新增使用者
		this.CA = new CreateAccount(this.AccountCount);
		this.CA.exe();

		// ================ set initial data =======================
		String projectId = this.CP.getProjectList().get(0).getName();
		// User Information
		String postfix = "_update";

		AccountObject user = CA.getAccountList().get(0);
		String userId = user.getId() + "";			// 取得第一筆 ID
		String userAccount = user.getUsername();	// 取得第一筆 Account ID
		String userPw = user.getPassword() + postfix;
		String userMail = user.getEmail();
		String userName = user.getNickName();
		String userEnable = user.getEnable() + "";	// default is true
		String userIsEdit = "true";				// false 代表是新增帳號

		// ================== set parameter info ====================
		addRequestParameter("id", userId);
		addRequestParameter("account", userAccount);
		addRequestParameter("passwd", userPw);
		addRequestParameter("mail", userMail);
		addRequestParameter("name", userName);
		addRequestParameter("enable", userEnable);
		addRequestParameter("isEdit", userIsEdit);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		// assert response text
		//		String userScrumRole = "user";
		String expectResponseText = "<Accounts>" +
		        "<AccountInfo>" +
		        "<ID>" + userId + "</ID>" +
		        "<Account>" + userAccount + "</Account>" +
		        "<Name>" + userName + "</Name>" +
		        "<Mail>" + userMail + "</Mail>" +
		        //		        "<Roles>" + userScrumRole + "</Roles>" +
		        "<Roles></Roles>" +
		        "<Enable>" + userEnable + "</Enable>" +
		        "</AccountInfo>" +
		        "</Accounts>";
		String acutalResponseText = response.getWriterBuffer().toString();
		assertEquals(expectResponseText, acutalResponseText);

		// assert database information
		AccountObject account = this.accountMapper.getAccount(userAccount);

		assertNotNull(account);
		assertEquals(userAccount, account.getUsername());
		assertEquals((new TestTool()).getMd5(userPw), account.getPassword());
		assertEquals(userMail, account.getEmail());
		assertEquals(userName, account.getNickName());
		assertEquals(userEnable, account.getEnable());
	}

	/**
	 * Integration Test
	 * 更新帳號資訊後，測試使用者是否可以登入ezScrum.
	 * Steps:
	 * 1. admin 新增帳號
	 * 2. admin 更新所有資訊( name, password, email )
	 * 3. user登入
	 */
	public void testModifyAccountAction_updateAllInformation_IntegrationTest() {
		/**
		 * 1. 新增帳號
		 */
		// ================ set action info ========================
		setRequestPathInformation(this.ActionPath_ModifyAccount);

		// ================== set initial info ====================
		String projectId = this.CP.getProjectList().get(0).getName();
		String userAccount = "tester";
		String userName = "tester";
		String userPwd = "tester";
		String userEmail = "tester@mail.com";
		String userEnable = "true";	// default is true
		String userIsEdit = "false";		// false 代表是新增帳號

		// ================== set parameter info ====================
		addRequestParameter("account", userAccount);
		addRequestParameter("name", userName);
		addRequestParameter("passwd", userPwd);
		addRequestParameter("mail", userEmail);
		addRequestParameter("enable", userEnable);
		addRequestParameter("isEdit", userIsEdit);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 create account action ======================
		actionPerform();

		// ================ assert ========================
		AccountObject account = this.accountMapper.getAccount(userAccount);
		// assert response text
		//		String userRole = "user";
		String expectResponseText = "<Accounts>" +
		        "<AccountInfo>" +
		        "<ID>" + account.getId() + "</ID>" +
		        "<Account>" + userAccount + "</Account>" +
		        "<Name>" + userName + "</Name>" +
		        "<Mail>" + userEmail + "</Mail>" +
		        "<Roles></Roles>" +
		        "<Enable>" + userEnable + "</Enable>" +
		        "</AccountInfo>" +
		        "</Accounts>";
		String acutalResponseText = response.getWriterBuffer().toString();
		assertEquals(expectResponseText, acutalResponseText);

		// assert database information

		assertNotNull(account);
		assertEquals(userAccount, account.getUsername());
		assertEquals((new TestTool()).getMd5(userPwd), account.getPassword());
		assertEquals(userEmail, account.getEmail());
		assertEquals(userName, account.getNickName());
		assertEquals(userEnable, account.getEnable());

		/**
		 * 2. 更新帳號資訊(name, email, password)
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();

		// ================ set action info ========================
		setRequestPathInformation(this.ActionPath_ModifyAccount);

		// ================== set initial info ====================
		String updateUserName = "uptate_name_tester";
		String updateUserPwd = "update_pwd_tester";
		String updateUserEmail = "update_mail_tester@mail.com";
		String updateUserEnable = "true";	// default is true
		String updateUserIsEdit = "true";		// false 代表是新增帳號

		// ================== set parameter info ====================
		addRequestParameter("id", account.getId() + "");
		addRequestParameter("account", account.getUsername());
		addRequestParameter("name", updateUserName);
		addRequestParameter("passwd", updateUserPwd);
		addRequestParameter("mail", updateUserEmail);
		addRequestParameter("enable", updateUserEnable);
		addRequestParameter("isEdit", updateUserIsEdit);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 update account action ======================
		actionPerform();

		// ================ assert ========================
		// assert response text
		String updateExpectResponseText = "<Accounts>" +
		        "<AccountInfo>" +
		        "<ID>" + account.getId() + "</ID>" +
		        "<Account>" + userAccount + "</Account>" +
		        "<Name>" + updateUserName + "</Name>" +
		        "<Mail>" + updateUserEmail + "</Mail>" +
		        "<Roles></Roles>" +
		        "<Enable>" + updateUserEnable + "</Enable>" +
		        "</AccountInfo>" +
		        "</Accounts>";
		String updateAcutalResponseText = response.getWriterBuffer().toString();
		assertEquals(updateExpectResponseText, updateAcutalResponseText);

		// assert database information
		AccountObject updateAccount = this.accountMapper.getAccount(userAccount);

		assertNotNull(updateAccount);
		assertEquals(userAccount, updateAccount.getUsername());
		assertEquals(updateUserName, updateAccount.getNickName());
		assertEquals((new TestTool()).getMd5(updateUserPwd), updateAccount.getPassword());
		assertEquals(updateUserEmail, updateAccount.getEmail());
		assertEquals(updateUserEnable, updateAccount.getEnable());

		/**
		 * 3. 登入
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();

		// ================ set action info ========================
		String ActionPath_LogonSubmit = "/logonSubmit";
		setRequestPathInformation(ActionPath_LogonSubmit);

		// ================== set parameter info ====================
		String loginUserID = userAccount;
		String loginUserPassword = updateUserPwd;
		LogonForm logonForm = new LogonForm();
		logonForm.setUserId(loginUserID);
		logonForm.setPassword(loginUserPassword);
		setActionForm(logonForm);

		// ================ 執行 login action ======================
		actionPerform();

		// ================ assert ======================
		verifyForward("success");
	}

	/**
	 * Integration Test
	 * 更新帳號資訊(enable:true->false)，測試使用者無法登入ezScrum.
	 * Steps
	 * 1. admin 新增帳號
	 * 2. admin 更新所有資訊( enable:true->false)
	 * 3. user登入
	 */
	public void testModifyAccountAction_updateEnableInformation_IntegrationTest() {
		/**
		 * 1. 新增帳號
		 */
		// ================ set action info ========================
		setRequestPathInformation(this.ActionPath_ModifyAccount);

		// ================== set initial info ====================
		String projectId = this.CP.getProjectList().get(0).getName();
		String userAccount = "tester";
		String userName = "tester";
		String userPwd = "tester";
		String userEmail = "tester@mail.com";
		String userEnable = "true";			// default is true
		String userIsEdit = "false";		// false 代表是新增帳號

		// ================== set parameter info ====================
		addRequestParameter("account", userAccount);
		addRequestParameter("name", userName);
		addRequestParameter("passwd", userPwd);
		addRequestParameter("mail", userEmail);
		addRequestParameter("enable", userEnable);
		addRequestParameter("isEdit", userIsEdit);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 create account action ======================
		actionPerform();

		// ================ assert ========================
		AccountObject account = this.accountMapper.getAccount(userAccount);
		// assert response text
//		String userRole = "user";
		String expectResponseText = "<Accounts>" +
		        "<AccountInfo>" +
		        "<ID>" + account.getId() + "</ID>" +
		        "<Account>" + userAccount + "</Account>" +
		        "<Name>" + userName + "</Name>" +
		        "<Mail>" + userEmail + "</Mail>" +
		        "<Roles></Roles>" +
		        "<Enable>" + userEnable + "</Enable>" +
		        "</AccountInfo>" +
		        "</Accounts>";
		String acutalResponseText = response.getWriterBuffer().toString();
		assertEquals(expectResponseText, acutalResponseText);

		// assert database information

		assertNotNull(account);
		assertEquals(account.getUsername(), userAccount);
		assertEquals(account.getNickName(), userName);
		assertEquals(account.getPassword(), (new TestTool()).getMd5(userPwd));
		assertEquals(account.getEmail(), userEmail);
		assertEquals(account.getEnable(), userEnable);

		/**
		 * 2. 更新帳號資訊(name, email, password)
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();

		// ================ set action info ========================
		setRequestPathInformation(this.ActionPath_ModifyAccount);

		// ================== set initial info ====================
		String updateUserEnable = "false";		// default is true
		String updateUserIsEdit = "true";		// false 代表是新增帳號

		// ================== set parameter info ====================
		addRequestParameter("id", account.getId() + "");
		addRequestParameter("account", userAccount);
		addRequestParameter("name", userName);
		addRequestParameter("passwd", userPwd);
		addRequestParameter("mail", userEmail);
		addRequestParameter("enable", updateUserEnable);
		addRequestParameter("isEdit", updateUserIsEdit);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 update account action ======================
		actionPerform();

		// ================ assert ========================
		// assert response text
		String updateExpectResponseText = "<Accounts>" +
		        "<AccountInfo>" +
		        "<ID>" + account.getId() + "</ID>" +
		        "<Account>" + userAccount + "</Account>" +
		        "<Name>" + userName + "</Name>" +
		        "<Mail>" + userEmail + "</Mail>" +
		        "<Roles></Roles>" +
		        "<Enable>" + updateUserEnable + "</Enable>" +
		        "</AccountInfo>" +
		        "</Accounts>";
		String updateAcutalResponseText = response.getWriterBuffer().toString();
		assertEquals(updateExpectResponseText, updateAcutalResponseText);

		// assert database information
		AccountObject updateAccount = this.accountMapper.getAccount(userAccount);

		assertNotNull(updateAccount);
		assertEquals(userAccount, updateAccount.getUsername());
		assertEquals((new TestTool()).getMd5(userPwd), updateAccount.getPassword());
		assertEquals(userEmail, updateAccount.getEmail());
		assertEquals(userName, updateAccount.getNickName());
		assertEquals(updateUserEnable, updateAccount.getEnable());

		/**
		 * 3. 登入
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();

		// ================ set action info ========================
		String ActionPath_LogonSubmit = "/logonSubmit";
		setRequestPathInformation(ActionPath_LogonSubmit);

		// ================== set parameter info ====================
		String loginUserID = userAccount;
		String loginUserPassword = userPwd;
		LogonForm logonForm = new LogonForm();
		logonForm.setUserId(loginUserID);
		logonForm.setPassword(loginUserPassword);
		setActionForm(logonForm);

		// ================ 執行 login action ======================
		actionPerform();

		// ================ assert ======================
		verifyForwardPath("/logon.do");
	}
}
