package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import servletunit.struts.MockStrutsTestCase;

public class GetUserDataActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateAccount mCA;
	private int mProjectCount = 1;
	private int mAccountCount = 1;
	private String mActionPath = "/getUserData";
	private Configuration mConfig;
	private AccountMapper mAccountMapper;
	private IUserSession mUserSession;

	public GetUserDataActionTest(String testMethod) {
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

		mAccountMapper = new AccountMapper();

		super.setUp();

		/**
		 * 設定讀取的 struts-config 檔案路徑
		 * GetUserDataAction
		 */
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);

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

	public void testGetUserDataAction() {
		mCA = new CreateAccount(mAccountCount);
		mCA.exe();

		// ================ set initial data =======================
		String projectName = mCP.getProjectList().get(0).getName();
		String username = mCA.getAccount_ID(1);
		mUserSession = new UserSession(mCA.getAccountList().get(0));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mUserSession);

		// ================ set URL parameter ========================
		// SessionManager 會對 URL 的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + projectName);

		// 執行 action
		actionPerform();

		/**
		 * Verify:
		 */
		AccountObject actualAccount = mAccountMapper.getAccount(username);
		AccountObject expectAccount = mUserSession.getAccount();

		assertNotNull(actualAccount);
		assertEquals(actualAccount.getUsername(), expectAccount.getUsername());
		assertEquals(actualAccount.getPassword(), expectAccount.getPassword());
		assertEquals(actualAccount.getEmail(), expectAccount.getEmail());
		assertEquals(actualAccount.getNickName(), expectAccount.getNickName());
		assertEquals(actualAccount.getEnable(), expectAccount.getEnable());
	}

}
