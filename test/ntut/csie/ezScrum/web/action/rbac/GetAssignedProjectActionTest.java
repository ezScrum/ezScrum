package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.util.HashMap;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import servletunit.struts.MockStrutsTestCase;

public class GetAssignedProjectActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateAccount mCA;
	private AddUserToRole mAUTR;
	private int mProjectCount = 1;
	private int mAccountCount = 1;
	private String mActionPath = "/getAssignedProject";
	private Configuration mConfig;
	private AccountMapper mAccountMapper;

	public GetAssignedProjectActionTest(String testMethod) {
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

		// 新增 User
		mCA = new CreateAccount(mAccountCount);
		mCA.exe();

		// 用來指派 Scrum 角色
		mAUTR = new AddUserToRole(mCP, mCA);

		mAccountMapper = new AccountMapper();

		super.setUp();

		/**
		 * 設定讀取的 struts-config 檔案路徑
		 * GetAssignedProjectAction
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

	//
	public void testGetAssignedProjectAction() {
		// 先加入 PO 角色
		mAUTR.exe_PO();

		// ================ set initial data =======================
		String projectName = mCP.getProjectList().get(0).getName();
		long accountId = mCA.getAccountList().get(0).getId();

		// ================== set parameter info ====================
		addRequestParameter("accountID", String.valueOf(accountId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ set URL parameter ========================
		// SessionManager 會對 URL 的參數作分析,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + projectName);
		
		// 執行 action
		actionPerform();

		/**
		 * Verify:
		 */
		AccountObject account = mAccountMapper.getAccount(accountId);

		assertNotNull(account);

		// role
		HashMap<String, ProjectRole> roleMap = account.getRoles();

		assertEquals(1, roleMap.size()); // PO
		assertEquals(ScrumEnum.SCRUMROLE_PRODUCTOWNER, roleMap.get(projectName)
				.getScrumRole().getRoleName());
	}
}
