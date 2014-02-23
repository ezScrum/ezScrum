package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.io.IOException;

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
import ntut.csie.jcis.account.core.LogonException;
import servletunit.struts.MockStrutsTestCase;

// 系統管理員更新資料
public class ModifyAccountActionTest extends MockStrutsTestCase {

	private CreateProject CP;
	private CreateAccount CA;
	private int ProjectCount = 1;
	private int AccountCount = 1;
	private final String ActionPath_ModifyAccount = "/modifyAccount";	// defined in "struts-config.xml"

	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private AccountMapper accountMapper;

	public ModifyAccountActionTest(String testMethod) {
		super(testMethod);
	}

	private void setRequestPathInformation(String actionPath) {
		setContextDirectory(new File(config.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
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
		InitialSQL ini = new InitialSQL(config);
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
		InitialSQL ini = new InitialSQL(config);
		ini.exe();											// 初始化 SQL

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(this.config.getTestDataPath());

		super.tearDown();

		// ============= release ==============
		// AccountFactory.releaseManager();
		this.accountMapper.releaseManager();
		ini = null;
		projectManager = null;
		this.CP = null;
		this.CA = null;
		this.config = null;
		this.accountMapper = null;
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
		String userId = "TEST_ACCOUNT_ID";		// 取得第一筆 Account ID
		String userPw = "TEST_ACCOUNT_PW";
		String userMail = "TEST_ACCOUNT_MAIL";
		String userName = "TEST_ACCOUNT_REALNAME";
		String userEnable = "true";	// default is true
		String userIsEdit = "false";	// false 代表是新增帳號
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("id", userId);
		addRequestParameter("passwd", userPw);
		addRequestParameter("mail", userMail);
		addRequestParameter("name", userName);
		addRequestParameter("enable", userEnable);
		addRequestParameter("isEdit", userIsEdit);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform();		// 執行 action

		// ================ assert ========================
		IAccount account = this.accountMapper.getAccount(userId);

		assertNotNull(account);
		assertEquals(account.getID(), userId);
		assertEquals(account.getPassword(), (new TestTool()).getMd5(userPw));
		assertEquals(account.getEmail(), userMail);
		assertEquals(account.getName(), userName);
		assertEquals(account.getEnable(), userEnable);
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

		String userId = this.CA.getAccount_ID(1);		// 取得第一筆 Account ID
		String userPw = this.CA.getAccount_PWD(1) + postfix;
		String userMail = "modify@test.com";
		String userName = this.CA.getAccount_RealName(1) + postfix;
		String userEnable = "false";	// default is true
		String userIsEdit = "true";	// false 代表是新增帳號

		// ================== set parameter info ====================
		addRequestParameter("id", userId);
		addRequestParameter("passwd", userPw);
		addRequestParameter("mail", userMail);
		addRequestParameter("name", userName);
		addRequestParameter("enable", userEnable);
		addRequestParameter("isEdit", userIsEdit);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// 執行 action
		actionPerform();

		// ================ assert ========================
		IAccount account = this.accountMapper.getAccount(userId);

		assertNotNull(account);
		assertEquals(account.getID(), userId);
		assertEquals(account.getPassword(), (new TestTool()).getMd5(userPw));
		assertEquals(account.getEmail(), userMail);
		assertEquals(account.getName(), userName);
		assertEquals(account.getEnable(), userEnable);
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

		String userId = this.CA.getAccount_ID(1);		// 取得第一筆 Account ID
		String userMail = "modify@test.com";
		String userName = this.CA.getAccount_RealName(1) + postfix;
		String userEnable = "false";	// default is true
		String userIsEdit = "true";		// false 代表是新增帳號

		// ================== set parameter info ====================
		addRequestParameter("id", userId);
		addRequestParameter("mail", userMail);
		addRequestParameter("name", userName);
		addRequestParameter("enable", userEnable);
		addRequestParameter("isEdit", userIsEdit);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		// assert response text
		String userScrumRole = "user";
		String expectResponseText = "<Accounts>" +
		        "<Account>" +
		        "<ID>" + userId + "</ID>" +
		        "<Name>" + userName + "</Name>" +
		        "<Mail>" + userMail + "</Mail>" +
		        "<Roles>" + userScrumRole + "</Roles>" +
		        "<Enable>" + userEnable + "</Enable>" +
		        "</Account>" +
		        "</Accounts>";
		String acutalResponseText = response.getWriterBuffer().toString();
		assertEquals(expectResponseText, acutalResponseText);

		// assert database information
		IAccount account = this.accountMapper.getAccount(userId);
		String expectUserPassword = this.CA.getAccount_PWD(1);

		assertNotNull(account);
		assertEquals(account.getID(), userId);
		assertEquals(account.getPassword(), (new TestTool()).getMd5(expectUserPassword));
		assertEquals(account.getEmail(), userMail);
		assertEquals(account.getName(), userName);
		assertEquals(account.getEnable(), userEnable);
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

		String userId = this.CA.getAccount_ID(1);		// 取得第一筆 Account ID
		String userPw = this.CA.getAccount_PWD(1) + postfix;
		String userMail = this.CA.getAccount_Mail(1);
		String userName = this.CA.getAccount_RealName(1);
		String userEnable = "true";		// default is true
		String userIsEdit = "true";		// false 代表是新增帳號

		// ================== set parameter info ====================
		addRequestParameter("id", userId);
		addRequestParameter("passwd", userPw);
		addRequestParameter("mail", userMail);
		addRequestParameter("name", userName);
		addRequestParameter("enable", userEnable);
		addRequestParameter("isEdit", userIsEdit);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		// assert response text
		String userScrumRole = "user";
		String expectResponseText = "<Accounts>" +
		        "<Account>" +
		        "<ID>" + userId + "</ID>" +
		        "<Name>" + userName + "</Name>" +
		        "<Mail>" + userMail + "</Mail>" +
		        "<Roles>" + userScrumRole + "</Roles>" +
		        "<Enable>" + userEnable + "</Enable>" +
		        "</Account>" +
		        "</Accounts>";
		String acutalResponseText = response.getWriterBuffer().toString();
		assertEquals(expectResponseText, acutalResponseText);

		// assert database information
		IAccount account = this.accountMapper.getAccount(userId);

		assertNotNull(account);
		assertEquals(account.getID(), userId);
		assertEquals(account.getPassword(), (new TestTool()).getMd5(userPw));
		assertEquals(account.getEmail(), userMail);
		assertEquals(account.getName(), userName);
		assertEquals(account.getEnable(), userEnable);
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
		String userId = "tester";
		String userName = "tester";
		String userPwd = "tester";
		String userEmail = "tester@mail.com";
		String userEnable = "true";	// default is true
		String userIsEdit = "false";		// false 代表是新增帳號

		// ================== set parameter info ====================
		addRequestParameter("id", userId);
		addRequestParameter("name", userName);
		addRequestParameter("passwd", userPwd);
		addRequestParameter("mail", userEmail);
		addRequestParameter("enable", userEnable);
		addRequestParameter("isEdit", userIsEdit);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 create account action ======================
		actionPerform();

		// ================ assert ========================
		// assert response text
		String userRole = "user";
		String expectResponseText =
		        "<Accounts>" +
		                "<Account>" +
		                "<ID>" + userId + "</ID>" +
		                "<Name>" + userName + "</Name>" +
		                "<Mail>" + userEmail + "</Mail>" +
		                "<Roles>" + userRole + "</Roles>" +
		                "<Enable>" + userEnable + "</Enable>" +
		                "</Account>" +
		                "</Accounts>";
		String acutalResponseText = response.getWriterBuffer().toString();
		assertEquals(expectResponseText, acutalResponseText);

		// assert database information
		IAccount account = this.accountMapper.getAccount(userId);

		assertNotNull(account);
		assertEquals(account.getID(), userId);
		assertEquals(account.getName(), userName);
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
		String updateUserName = "uptate_name_tester";
		String updateUserPwd = "update_pwd_tester";
		String updateUserEmail = "update_mail_tester@mail.com";
		String updateUserEnable = "true";	// default is true
		String updateUserIsEdit = "true";		// false 代表是新增帳號

		// ================== set parameter info ====================
		addRequestParameter("id", userId);
		addRequestParameter("name", updateUserName);
		addRequestParameter("passwd", updateUserPwd);
		addRequestParameter("mail", updateUserEmail);
		addRequestParameter("enable", updateUserEnable);
		addRequestParameter("isEdit", updateUserIsEdit);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 update account action ======================
		actionPerform();

		// ================ assert ========================
		// assert response text
		String updateExpectResponseText =
		        "<Accounts>" +
		                "<Account>" +
		                "<ID>" + userId + "</ID>" +
		                "<Name>" + updateUserName + "</Name>" +
		                "<Mail>" + updateUserEmail + "</Mail>" +
		                "<Roles>" + userRole + "</Roles>" +
		                "<Enable>" + updateUserEnable + "</Enable>" +
		                "</Account>" +
		                "</Accounts>";
		String updateAcutalResponseText = response.getWriterBuffer().toString();
		assertEquals(updateExpectResponseText, updateAcutalResponseText);

		// assert database information
		IAccount updateAccount = this.accountMapper.getAccount(userId);

		assertNotNull(updateAccount);
		assertEquals(updateAccount.getID(), userId);
		assertEquals(updateAccount.getName(), updateUserName);
		assertEquals(updateAccount.getPassword(), (new TestTool()).getMd5(updateUserPwd));
		assertEquals(updateAccount.getEmail(), updateUserEmail);
		assertEquals(updateAccount.getEnable(), updateUserEnable);

		/**
		 * 3. 登入
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();

		// ================ set action info ========================
		String ActionPath_LogonSubmit = "/logonSubmit";
		setRequestPathInformation(ActionPath_LogonSubmit);

		// ================== set parameter info ====================
		String loginUserID = userId;
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
		String userId = "tester";
		String userName = "tester";
		String userPwd = "tester";
		String userEmail = "tester@mail.com";
		String userEnable = "true";			// default is true
		String userIsEdit = "false";		// false 代表是新增帳號

		// ================== set parameter info ====================
		addRequestParameter("id", userId);
		addRequestParameter("name", userName);
		addRequestParameter("passwd", userPwd);
		addRequestParameter("mail", userEmail);
		addRequestParameter("enable", userEnable);
		addRequestParameter("isEdit", userIsEdit);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 create account action ======================
		actionPerform();

		// ================ assert ========================
		// assert response text
		String userRole = "user";
		String expectResponseText =
		        "<Accounts>" +
		                "<Account>" +
		                "<ID>" + userId + "</ID>" +
		                "<Name>" + userName + "</Name>" +
		                "<Mail>" + userEmail + "</Mail>" +
		                "<Roles>" + userRole + "</Roles>" +
		                "<Enable>" + userEnable + "</Enable>" +
		                "</Account>" +
		                "</Accounts>";
		String acutalResponseText = response.getWriterBuffer().toString();
		assertEquals(expectResponseText, acutalResponseText);

		// assert database information
		IAccount account = this.accountMapper.getAccount(userId);

		assertNotNull(account);
		assertEquals(account.getID(), userId);
		assertEquals(account.getName(), userName);
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
		addRequestParameter("id", userId);
		addRequestParameter("name", userName);
		addRequestParameter("passwd", userPwd);
		addRequestParameter("mail", userEmail);
		addRequestParameter("enable", updateUserEnable);
		addRequestParameter("isEdit", updateUserIsEdit);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 update account action ======================
		actionPerform();

		// ================ assert ========================
		// assert response text
		String updateExpectResponseText =
		        "<Accounts>" +
		                "<Account>" +
		                "<ID>" + userId + "</ID>" +
		                "<Name>" + userName + "</Name>" +
		                "<Mail>" + userEmail + "</Mail>" +
		                "<Roles>" + userRole + "</Roles>" +
		                "<Enable>" + updateUserEnable + "</Enable>" +
		                "</Account>" +
		                "</Accounts>";
		String updateAcutalResponseText = response.getWriterBuffer().toString();
		assertEquals(updateExpectResponseText, updateAcutalResponseText);

		// assert database information
		IAccount updateAccount = this.accountMapper.getAccount(userId);

		assertNotNull(updateAccount);
		assertEquals(updateAccount.getID(), userId);
		assertEquals(updateAccount.getName(), userName);
		assertEquals(updateAccount.getPassword(), (new TestTool()).getMd5(userPwd));
		assertEquals(updateAccount.getEmail(), userEmail);
		assertEquals(updateAccount.getEnable(), updateUserEnable);

		/**
		 * 3. 登入
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();

		// ================ set action info ========================
		String ActionPath_LogonSubmit = "/logonSubmit";
		setRequestPathInformation(ActionPath_LogonSubmit);

		// ================== set parameter info ====================
		String loginUserID = userId;
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
