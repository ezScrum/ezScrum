package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.TestTool;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.LogonException;
import servletunit.struts.MockStrutsTestCase;

public class GetAccountListActionTest extends MockStrutsTestCase {

	private CreateProject mCP;
	private CreateAccount mCA;
	private int mProjectCount = 1;
	private int mAccountCount = 5;
	private String mActionPath = "/getAccountList";
	private Configuration mConfig;
	private AccountMapper mAccountMapper;

	public GetAccountListActionTest(String testMethod) {
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
		 * GetAccountListAction
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
		mAccountMapper = null;
		projectManager = null;
		mConfig = null;
	}

	// One
	public void testGetAccountListAction() {
		mCA = new CreateAccount(1);
		mCA.exe();

		// ================ set initial data =======================
		String projectName = mCP.getProjectList().get(0).getName();

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ set URL parameter ========================
		// SessionManager 會對 URL 的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + projectName);
		
		// 執行 action
		actionPerform();

		/**
		 * Verify:
		 */
		ArrayList<AccountObject> accounts = mAccountMapper.getAccounts();
		assertEquals(2, accounts.size()); // 包含 admin
		
		AccountObject account = mAccountMapper.getAccount(mCA
				.getAccount_ID(1));
		assertNotNull(account);
		assertEquals(account.getUsername(), mCA.getAccount_ID(1));
		assertEquals(account.getPassword(),
				(new TestTool()).getMd5(mCA.getAccount_PWD(1)));
		assertEquals(account.getEmail(), mCA.getAccount_Mail(1));
		assertEquals(account.getNickName(), mCA.getAccount_RealName(1));
		assertEquals(account.getEnable(), true);
	}

	// multiple
	public void testGetAccountListActionX() throws LogonException {
		mCA = new CreateAccount(mAccountCount);
		mCA.exe();

		// ================ set initial data =======================
		String projectName = mCP.getProjectList().get(0).getName();

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ set URL parameter ========================
		// SessionManager 會對 URL 的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + projectName);
		
		// 執行 action
		actionPerform();

		/**
		 * Verify:
		 */
		ArrayList<AccountObject> accounts = mAccountMapper.getAccounts();

		assertEquals(mAccountCount + 1, accounts.size()); // 包含 admin

		for (int i = 1; i < mAccountCount + 1; i++) {
			AccountObject account = mAccountMapper.getAccount(mCA
					.getAccount_ID(i));
			assertNotNull(account);
			assertEquals(account.getUsername(), mCA.getAccount_ID(i));
			assertEquals(account.getPassword(),
					(new TestTool()).getMd5(mCA.getAccount_PWD(i)));
			assertEquals(account.getEmail(), mCA.getAccount_Mail(i));
			assertEquals(account.getNickName(), mCA.getAccount_RealName(i));
			assertEquals(account.getEnable(), true);
		}

	}

}
