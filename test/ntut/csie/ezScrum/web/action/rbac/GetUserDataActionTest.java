package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.LogonException;
import servletunit.struts.MockStrutsTestCase;

// 使用者更新資料前會先執行此Action
public class GetUserDataActionTest extends MockStrutsTestCase {

	private CreateProject CP;
	private CreateAccount CA;
	private int ProjectCount = 1;
	private int AccountCount = 1;
	private String actionPath = "/getUserData";	// defined in "struts-config.xml"

	private Configuration configuration;
	private AccountMapper accountMapper;
	private IUserSession userSession;

	public GetUserDataActionTest(String testMethod) {
		super(testMethod);
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

		this.accountMapper = new AccountMapper();

		super.setUp();

		// 固定行為可抽離
		setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(this.actionPath);

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL

		CopyProject copyProject = new CopyProject(this.CP);
		copyProject.exeDelete_Project();					// 刪除測試檔案
		
		configuration.setTestMode(false);
		configuration.store();

		super.tearDown();

		// ============= release ==============
		// AccountFactory.releaseManager();
		this.accountMapper.releaseManager();
		ini = null;
		copyProject = null;
		this.CP = null;
		this.CA = null;
		this.config = null;
		this.accountMapper = null;
		configuration = null;
	}

	//
	public void testGetUserDataAction() throws LogonException {
		// 新增使用者
		this.CA = new CreateAccount(this.AccountCount);
		this.CA.exe();

		// ================ set initial data =======================
		String projectId = this.CP.getProjectList().get(0).getName();
		// User Information
		String userId = this.CA.getAccount_ID(1);		// 取得第一筆 Account ID
		this.userSession = new UserSession(this.CA.getAccountList().get(0));
		// ================ set initial data =======================

		// ================== set parameter info ====================
		// ================== set parameter info ====================

		// ================ set session info ========================
		// config session為 admin
		// request.getSession().setAttribute("UserSession", config.getUserSession());
		request.getSession().setAttribute("UserSession", this.userSession);
		// ================ set session info ========================

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectId);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		// ================ set URL parameter ========================

		actionPerform();		// 執行 action

		/*
		 * Verify:
		 */
		AccountObject account = this.accountMapper.getAccount(userId);		// actual
		AccountObject accountE = this.userSession.getAccount();				// excepted

		assertNotNull(account);
		assertEquals(account.getAccount(), accountE.getAccount());
		assertEquals(account.getPassword(), accountE.getPassword());
		assertEquals(account.getEmail(), accountE.getEmail());
		assertEquals(account.getName(), accountE.getName());
		assertEquals(account.getEnable(), accountE.getEnable());
	}

}
