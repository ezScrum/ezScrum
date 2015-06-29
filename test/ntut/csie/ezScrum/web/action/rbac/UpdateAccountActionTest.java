package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.TestTool;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.form.LogonForm;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import servletunit.struts.MockStrutsTestCase;

public class UpdateAccountActionTest extends MockStrutsTestCase {

	private CreateProject mCP;
	private CreateAccount mCA;
	private int mProjectCount = 1;
	private int mAccountCount = 1;
	private String mActionPath = "/updateAccount";
	private Configuration mConfig;
	private AccountMapper mAccountMapper;

	public UpdateAccountActionTest(String testMethod) {
		super(testMethod);
	}
	
	/**
	 * 設定讀取的 struts-config 檔案路徑
	 * UpdateAccountAction
	 */
	private void setRequestPathInformation(String actionPath) {
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
		mConfig = null;
		mAccountMapper = null;
	}

	/**
	 * update User Information
	 */
	public void testUpdateAccountAction_1() {
		setRequestPathInformation(mActionPath);
		
		mCA = new CreateAccount(mAccountCount);
		mCA.exe();

		// ================ set initial data =======================
		String projectName = mCP.getProjectList().get(0).getName();
		String postfix = "_update";
		AccountObject account = mCA.getAccountList().get(0);
		long accountId = account.getId();
		String username = account.getUsername();
		String password = mCA.getAccount_PWD(1) + postfix;
		String email = "modify@test.com";
		String nickName = account.getNickName() + postfix;
		boolean enable = false;		// default is true
		boolean isEdit = true;		// false 代表是新增帳號

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(accountId));
		addRequestParameter("account", username);
		addRequestParameter("passwd", password);
		addRequestParameter("mail", email);
		addRequestParameter("name", nickName);
		addRequestParameter("enable", String.valueOf(enable));
		addRequestParameter("isEdit", String.valueOf(isEdit));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ set URL parameter ========================
		// SessionManager 會對 URL 的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + projectName);

		// 執行 action
		actionPerform();

		/**
		 * Verify:
		 */
		account = (new AccountMapper()).getAccount(accountId);
		assertNotNull(account);
		assertEquals(accountId, account.getId());
		assertEquals(username, account.getUsername());
		assertEquals((new TestTool()).getMd5(password), account.getPassword());
		assertEquals(nickName, account.getNickName());
		assertEquals(email, account.getEmail());
		assertEquals(enable, account.getEnable());
	}
	
	/**
	 * update Password
	 */
	public void testUpdateAccountAction_2() {
		setRequestPathInformation(mActionPath);
		
		mCA = new CreateAccount(mAccountCount);
		mCA.exe();

		// ================ set initial data =======================
		String projectName = mCP.getProjectList().get(0).getName();
		String postfix = "_update";
		AccountObject account = mCA.getAccountList().get(0);
		long accountId = account.getId();
		String username = account.getUsername();
		String password = mCA.getAccount_PWD(1) + postfix;
		String email = account.getEmail();
		String nickName = account.getNickName();
		boolean enable = account.getEnable();	// default is true
		boolean isEdit = true;					// false 代表是新增帳號

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(accountId));
		addRequestParameter("account", username);
		addRequestParameter("passwd", password);
		addRequestParameter("mail", email);
		addRequestParameter("name", nickName);
		addRequestParameter("enable", String.valueOf(enable));
		addRequestParameter("isEdit", String.valueOf(isEdit));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ set URL parameter ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		request.setHeader("Referer", "?PID=" + projectName);

		// 執行 action
		actionPerform();

		// ================ assert ========================
		StringBuilder expectResponse = new StringBuilder();
		expectResponse
			.append("<Accounts>")
				.append("<AccountInfo>")
					.append("<ID>").append(accountId).append("</ID>")
					.append("<Account>").append(username).append("</Account>")
					.append("<Name>").append(nickName).append("</Name>")
					.append("<Mail>").append(email).append("</Mail>")
					.append("<Roles></Roles>")
					.append("<Enable>").append(enable).append("</Enable>")
				.append("</AccountInfo>")
			.append("</Accounts>");
		String acutalResponse = response.getWriterBuffer().toString();
		assertEquals(expectResponse.toString(), acutalResponse);

		// assert database information
		AccountObject newAccount = mAccountMapper.getAccount(username);
		assertNotNull(newAccount);
		assertEquals(username, newAccount.getUsername());
		assertEquals((new TestTool()).getMd5(password), newAccount.getPassword());
		assertEquals(nickName, newAccount.getNickName());
		assertEquals(email, newAccount.getEmail());
		assertEquals(enable, newAccount.getEnable());
	}
	
	/**
	 * Integration Test
	 * 更新帳號資訊後，測試使用者是否可以登入 ezScrum.
	 * Steps:
	 * 1. admin 更新所有資訊( name, password, email )
	 * 2. user 登入
	 */
	public void testUpdateAccountAction_3() {
		// create account
		mCA = new CreateAccount(mAccountCount);
		mCA.exe();

		/**
		 * 1. 更新帳號資訊(name, email, password)
		 */
		// ================ initial action info ========================
		cleanActionInformation();
		setRequestPathInformation(mActionPath);

		// ================== set initial info ====================
		String projectName = mCP.getProjectList().get(0).getName();
		AccountObject account = mCA.getAccountList().get(0);
		long accountId = account.getId();
		String username = account.getUsername();
		String updatePassword = "update_pwd_tester";
		String updateNickName = "uptate_nickName_tester";
		String updateEmail = "update_mail_tester@mail.com";
		boolean updateEnable = true;		// default is true
		boolean updateIsEdit = true;		// false 代表是新增帳號

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(accountId));
		addRequestParameter("account", account.getUsername());
		addRequestParameter("passwd", updatePassword);
		addRequestParameter("name", updateNickName);
		addRequestParameter("mail", updateEmail);
		addRequestParameter("enable", String.valueOf(updateEnable));
		addRequestParameter("isEdit", String.valueOf(updateIsEdit));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ set URL parameter ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		request.setHeader("Referer", "?PID=" + projectName);

		// 執行 update account action
		actionPerform();

		// ================ assert ========================
		StringBuilder updateExpectResponse = new StringBuilder();
		updateExpectResponse
			.append("<Accounts>")
				.append("<AccountInfo>")
					.append("<ID>").append(accountId).append("</ID>")
					.append("<Account>").append(username).append("</Account>")
					.append("<Name>").append(updateNickName).append("</Name>")
					.append("<Mail>").append(updateEmail).append("</Mail>")
					.append("<Roles></Roles>")
					.append("<Enable>").append(updateEnable).append("</Enable>")
				.append("</AccountInfo>")
			.append("</Accounts>");
		String updateAcutalResponse = response.getWriterBuffer().toString();
		assertEquals(updateExpectResponse.toString(), updateAcutalResponse);
		
		// assert database information
		AccountObject updateAccount = mAccountMapper.getAccount(username);
		assertNotNull(updateAccount);
		assertEquals(username, updateAccount.getUsername());
		assertEquals((new TestTool()).getMd5(updatePassword), updateAccount.getPassword());
		assertEquals(updateNickName, updateAccount.getNickName());
		assertEquals(updateEmail, updateAccount.getEmail());
		assertEquals(updateEnable, updateAccount.getEnable());

		/**
		 * 2. 登入
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();

		// ================ set action info ========================
		String actionPath_LogonSubmit = "/logonSubmit";
		setRequestPathInformation(actionPath_LogonSubmit);

		// ================== set parameter info ====================
		LogonForm logonForm = new LogonForm();
		logonForm.setUserId(username);
		logonForm.setPassword(updatePassword);
		setActionForm(logonForm);

		// 執行 login action
		actionPerform();

		// ================ assert ======================
		verifyForward("success");
	}
}
