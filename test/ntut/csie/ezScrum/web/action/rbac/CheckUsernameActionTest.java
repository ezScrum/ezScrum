package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.account.core.LogonException;
import servletunit.struts.MockStrutsTestCase;

// admin 新增使用者之前會先做此檢查
public class CheckUsernameActionTest extends MockStrutsTestCase {

	private CreateProject mCP;
	private CreateAccount mCA;
	private int mProjectCount = 1;
	private int mAccountCount = 1;
	private String mActionPath = "/checkAccountID";
	private Configuration mConfig;

	public CheckUsernameActionTest(String testMethod) {
		super(testMethod);
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

		super.setUp();

		/**
		 * 設定讀取的 struts-config 檔案路徑
		 * CheckUsernameAction
		 */
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
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
		mCP = null;
		mCA = null;
		mConfig = null;
		projectManager = null;
	}

	// 測試欲新增加的帳號已重複
	public void testCheckUsernameAction_existed() {
		// 新增使用者
		mCA = new CreateAccount(mAccountCount);
		mCA.exe();

		// ================ set initial data =======================
		String projectName = mCP.getProjectList().get(0).getName();
		String username = mCA.getAccount_ID(1);

		// ================== set parameter info ====================
		addRequestParameter("id", username);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ set URL parameter ========================
		// SessionManager 會對 URL 的參數作分析，未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + projectName);
		
		// ================ set URL parameter ========================

		// 執行 action
		actionPerform();

		/**
		 * Verify:
		 */
		verifyNoActionErrors();
		String result = response.getWriterBuffer().toString();

		assertEquals("false", result); // 帳號已存在
	}

	// 測試新增加的帳號
	public void testCheckUsernameAction_New() throws LogonException {

		// ================ set initial data =======================
		String projectName = this.mCP.getProjectList().get(0).getName();
		String username = "testNewID";

		// ================== set parameter info ====================
		addRequestParameter("id", username);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ set URL parameter ========================
		// SessionManager 會對 URL 的參數作分析，未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + projectName);

		// 執行 action
		actionPerform();

		/**
		 * Verify:
		 */
		verifyNoActionErrors();
		String result = response.getWriterBuffer().toString();

		assertEquals("true", result); // 帳號未存在
	}

	// 測試欲新增加的帳號為 empty
	public void testCheckUsernameAction_New1() {

		// ================ set initial data =======================
		String projectName = mCP.getProjectList().get(0).getName();
		String username = "";

		// ================== set parameter info ====================
		addRequestParameter("id", username);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ set URL parameter ========================
		// SessionManager 會對 URL 的參數作分析，未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + projectName);

		// 執行 action
		actionPerform();

		/**
		 * Verify:
		 */
		verifyNoActionErrors();
		String result = response.getWriterBuffer().toString();

		assertEquals("false", result); // 帳號 NG
	}
}
