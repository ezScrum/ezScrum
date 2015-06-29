package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.LogonException;
import servletunit.struts.MockStrutsTestCase;

public class DeleteAccountActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateAccount mCA;
	private int mProjectCount = 1;
	private int mAccountCount = 2;
	private String mActionPath = "/deleteAccount";
	private Configuration mConfig;
	private AccountMapper mAccountMapper;

	public DeleteAccountActionTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		// 新增使用者
		mCA = new CreateAccount(mAccountCount);
		mCA.exe();

		mAccountMapper = new AccountMapper();

		super.setUp();

		/**
		 * 設定讀取的 struts-config 檔案路徑
		 * DeleteAccountAction
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
		mCP = null;
		mCA = null;
		projectManager = null;
		mAccountMapper = null;
		mConfig = null;
	}

	// test happy path
	public void testDeleteAccountAction_1() throws LogonException {
		// ================ set initial data =======================
		long accountId = mCA.getAccountList().get(0).getId();

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(accountId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// 執行 action
		actionPerform();

		// assert account does exist
		AccountObject account = mAccountMapper.getAccount(accountId);
		assertNull(account);

		// assert response
		String result = response.getWriterBuffer().toString();
		assertEquals("true", result);
	}

	// test delete not exist account
	public void testDeleteAccountAction_2() throws LogonException {
		// ================ set initial data =======================
		long accountId = 9527;

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(accountId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// 執行 action
		actionPerform();

		// assert response
		String result = response.getWriterBuffer().toString();
		assertEquals("false", result);
	}

	// test delete wrong type account id
	public void testDeleteAccountAction_3() throws LogonException {
		// ================ set initial data =======================
		String accountId = "?????";

		// ================== set parameter info ====================
		addRequestParameter("id", accountId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// 執行 action
		actionPerform();

		// assert response
		String result = response.getWriterBuffer().toString();
		assertEquals("false", result);
	}
}
