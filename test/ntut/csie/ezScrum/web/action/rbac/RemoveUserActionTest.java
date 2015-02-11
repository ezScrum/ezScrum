package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.TestTool;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.form.LogonForm;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import servletunit.struts.MockStrutsTestCase;

public class RemoveUserActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateAccount mCA;
	private int mProjectCount = 1;
	private int mAccountCount = 2;
	private String mActionPath = "/removeUser";
	private AddUserToRole mAUTR;
	private Configuration mConfig;
	private AccountMapper mAccountMapper;

	public RemoveUserActionTest(String testMethod) {
		super(testMethod);
	}

	/**
	 * 設定讀取的 struts-config 檔案路徑
	 * RemoveUserAction
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

		// 新增使用者
		mCA = new CreateAccount(mAccountCount);
		mCA.exe();

		// 指派 Scrum 角色
		mAUTR = new AddUserToRole(mCP, mCA);

		mAccountMapper = new AccountMapper();

		super.setUp();

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		//	刪除外部檔案
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

	// 測試正常執行
	public void testExecute() {
		setRequestPathInformation(mActionPath);

		// 加入 PO 角色
		mAUTR.exe_PO();

		AccountObject account = mCA.getAccountList().get(0);
		HashMap<String, ProjectRole> roleMap = account.getRoles();

		assertEquals(1, roleMap.size());

		// ================ set initial data =======================
		long accountId = account.getId();
		long projectId = mAUTR.getNowProjectObject().getId();
		String scrumRole = ScrumEnum.SCRUMROLE_PRODUCTOWNER;

		boolean isExisted = false;
		for (Entry<String, ProjectRole> role : roleMap.entrySet()) {
			if (scrumRole.equals(role.getValue().getScrumRole().getRoleName())) {
				isExisted = true;
				break;
			}
		}
		assertTrue(isExisted);

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(accountId));
		addRequestParameter("resource", String.valueOf(projectId));
		addRequestParameter("operation", scrumRole);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// 執行 action
		actionPerform();

		account = mAccountMapper.getAccount(accountId);
		assertNotNull(account);
		assertEquals(mCA.getAccount_ID(1), account.getUsername());
		assertEquals(new TestTool().getMd5(mCA.getAccount_PWD(1)), account.getPassword());
		assertEquals(mCA.getAccount_RealName(1), account.getNickName());
		assertEquals(true, account.getEnable());
		assertEquals(mCA.getAccount_Mail(1), account.getEmail());

		// 測試 Role 是否正確被移除
		roleMap = account.getRoles();
		assertEquals(0, roleMap.size());
	}

	// 測試 Admin 正常移除 System 權限 
	public void testExecuteAdmin_Remove1() {
		setRequestPathInformation(mActionPath);

		// ================ set initial data =======================
		long accountId = 1;
		String projectName = ScrumEnum.SYSTEM;
		String scrumRole = ScrumEnum.SCRUMROLE_ADMIN;

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(accountId));
		addRequestParameter("resource", projectName);
		addRequestParameter("operation", scrumRole);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// 執行 action
		actionPerform();

		AccountObject account = mAccountMapper.getAccount(accountId);
		HashMap<String, ProjectRole> roleMap = account.getRoles();

		// 測試是否正確移除 System 角色
		assertEquals(0, roleMap.size());
	}

	// 測試將 Admin 指派到某個專案的角色，再移除此專案的角色
	public void testExecuteAdmin_Remove2() {
		setRequestPathInformation(mActionPath);

		// 將 Admin 加入測試專案一的 PO
		mAUTR.setNowAccountIsSystem();
		mAUTR.exe_PO();

		// ================ set initial data =======================
		long id = 1;	 			// admin
		String username = "admin"; 	// admin
		long projectId = mAUTR.getNowProjectObject().getId();
		String scrumRole = ScrumEnum.SCRUMROLE_PRODUCTOWNER;

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(id));
		addRequestParameter("resource", String.valueOf(projectId));
		addRequestParameter("operation", scrumRole);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// 執行 action
		actionPerform();

		// 測試是否正確移除此角色，但是沒有移除掉 Admin 權限
		AccountObject account = mAccountMapper.getAccount(id);
		HashMap<String, ProjectRole> roleMap = account.getRoles();

		// 測試是否正確移除 System 角色
		assertEquals(1, roleMap.size());
		// 測試專案的 Role 是否正確加入，並且沒有移除 admin 權限
		assertEquals(username, roleMap.get("system").getScrumRole().getRoleName());
	}

	// 測試將 Admin 指派到某個專案的角色，再移除 Admin 權限，該專案的角色不會移除
	public void testExecuteAdmin_Remove3() {
		setRequestPathInformation(mActionPath);

		// 將 Admin 加入測試專案一的 PO
		mAUTR.setNowAccountIsSystem();
		mAUTR.exe_PO();

		// ================ set initial data =======================
		long accountId = 1;
		long projectId = mAUTR.getNowProjectObject().getId();
		String projectName = mAUTR.getNowProjectObject().getName();
		String scrumRole = ScrumEnum.SCRUMROLE_ADMIN;

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(accountId));
		addRequestParameter("resource", String.valueOf(projectId));
		addRequestParameter("operation", scrumRole);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// 執行 action
		actionPerform();

		// 測試是否正確移除此角色，但是沒有移除掉 Admin 權限
		AccountObject account = mAccountMapper.getAccount(accountId);
		HashMap<String, ProjectRole> roleMap = account.getRoles();

		// 測試是否正確移除 System 角色
		assertEquals(1, roleMap.size());
		assertEquals(ScrumEnum.SCRUMROLE_PRODUCTOWNER, roleMap.get(projectName).getScrumRole().getRoleName());
	}

	/**
	 * 1. admin 建立專案 (setup done)
	 * 2. admin 建立角色(setup done)
	 * 3. admin 指定 user 到專案中擔任 PO
	 * 4. admin 移除 user 在專案中的角色
	 * 5. user 登入ezScrum
	 * 6. 測試user不會看到專案資訊
	 */
	public void testExecuteAdmin_Remove_IntegrationTest() {
		//	=============== common data ============================
		AccountObject account = mCA.getAccountList().get(0);
		IUserSession userSession = getUserSession(account);
		long accountId = account.getId();
		String username = account.getUsername();
		long projectId = mCP.getAllProjects().get(0).getId();

		/**
		 * 3. admin 指定 user 到專案中擔任 PO (將 user 加入測試專案一的 PO)
		 */
		mAUTR.setAccountIndex(0);
		mAUTR.exe_PO();

		/**
		 * 4. admin 移除 user 在專案中的角色
		 */
		// ================ set initial data =======================
		setRequestPathInformation(mActionPath);
		String scrumRole = ScrumEnum.SCRUMROLE_PRODUCTOWNER;

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(accountId));
		addRequestParameter("resource", String.valueOf(projectId));
		addRequestParameter("operation", scrumRole);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// 執行 action
		actionPerform();

		// 測試是否正確移除此角色，但是沒有移除掉 Admin 權限
		AccountObject actualAccount = mAccountMapper.getAccount(username);
		HashMap<String, ProjectRole> roleMap = actualAccount.getRoles();

		// 測試是否正確移除 System 角色
		assertEquals(0, roleMap.size());

		/**
		 * 5. user 登入 ezScrum
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();

		// ================ set action info ========================
		String actionPath_logonSubmit = "/logonSubmit";
		setRequestPathInformation(actionPath_logonSubmit);

		// ================== set parameter info ====================
		LogonForm logonForm = new LogonForm();
		logonForm.setUserId(username);
		logonForm.setPassword(mCA.getAccount_PWD(1));
		setActionForm(logonForm);

		// 執行 login action
		actionPerform();

		// ================ assert ======================
		verifyForward("success");

		/**
		 * 6. view project list (測試 user 不會看到專案資訊)
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();

		// ================ set action info ========================
		String actionPath_viewProjectList = "/viewProjectList";
		setRequestPathInformation(actionPath_viewProjectList);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", userSession);

		// 執行 view project list action
		actionPerform();

		// ================ assert ========================
		//	assert response text
		String viewProjectListExpected = "<Projects></Projects>";
		String viewProjectListActual = response.getWriterBuffer().toString();
		assertEquals(viewProjectListExpected, viewProjectListActual);
	}

	private IUserSession getUserSession(AccountObject account) {
		IUserSession userSession = new UserSession(account);
		return userSession;
	}
}
