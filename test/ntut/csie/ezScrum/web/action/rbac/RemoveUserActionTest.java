package ntut.csie.ezScrum.web.action.rbac;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.form.LogonForm;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.LogonException;
import servletunit.struts.MockStrutsTestCase;

public class RemoveUserActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateAccount CA;
	private int ProjectCount = 1;
	private int AccountCount = 2;
	private String ActionPath_RemoveUser = "/removeUser";	// defined in "struts-config.xml"
	private AddUserToRole AUTR;

	private Configuration configuration;
	private AccountMapper accountMapper;

	public RemoveUserActionTest(String testMethod) {
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

		// 新增使用者
		this.CA = new CreateAccount(this.AccountCount);
		this.CA.exe();

		// 用來指派Scrum角色
		this.AUTR = new AddUserToRole(this.CP, this.CA);

		this.accountMapper = new AccountMapper();

		super.setUp();

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL

		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());
		
		configuration.setTestMode(false);
		configuration.save();

		super.tearDown();

		// ============= release ==============
		//    	AccountFactory.releaseManager();
		this.accountMapper.releaseManager();
		ini = null;
		projectManager = null;
		this.CP = null;
		this.CA = null;
		this.config = null;
		this.accountMapper = null;
		configuration = null;
	}

	// 測試正常執行
	public void testexecute() throws LogonException {
		setRequestPathInformation(this.ActionPath_RemoveUser);

		// 先加入 PO 角色
		this.AUTR.exe_PO();

		AccountObject acc = this.AUTR.getNowAccount();
		AccountObject account = this.accountMapper.getAccount(acc.getUsername());
		HashMap<String, ProjectRole> roleMap = account.getRoles();

		assertEquals(1, roleMap.size());

		// ================ set initial data =======================
		long id = acc.getId();
		long res = this.AUTR.getNowProjectObject().getId();
		String op = ScrumEnum.SCRUMROLE_PRODUCTOWNER;
		// ================ set initial data =======================

		// 測試是否正確加入此 PO 角色
		//		assertEquals(roles[0].getRoleId(), getRole(res, op));

		boolean isExisted = false;
		for (Entry<String, ProjectRole> role : roleMap.entrySet()) {
			if (op.equals(role.getValue().getScrumRole().getRoleName())) {
				isExisted = true;
				break;
			}
		}
		assertTrue(isExisted);

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(id));
		addRequestParameter("resource", String.valueOf(res));
		addRequestParameter("operation", op);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		// ================ set session info ========================

		actionPerform();		// 執行 action

		account = this.accountMapper.getAccount(id);
		assertNotNull(account);
		assertEquals(this.CA.getAccount_ID(1), account.getUsername());
		assertEquals(this.CA.getAccount_RealName(1), account.getNickName());
		assertEquals("true", account.getEnable());
		assertEquals("5e6698ee13f3ef999374751897721cb6", account.getPassword());		// 密碼要經過 MD5 編碼
		assertEquals(this.CA.getAccount_Mail(1), account.getEmail());

		// 測試 Role 是否正確被移除
		roleMap = account.getRoles();
		assertEquals(0, roleMap.size());
	}

	// 測試 Admin 正常移除 System 權限 
	public void testexecuteAdmin_Remove1() throws LogonException {
		setRequestPathInformation(this.ActionPath_RemoveUser);

		// ================ set initial data =======================
		String id = "1";
		String accountId = "admin";
		String res = ScrumEnum.SYSTEM;
		String op = ScrumEnum.SCRUMROLE_ADMIN;
		// ================ set initial data =======================    	

		// ================== set parameter info ====================
		addRequestParameter("id", id);
		addRequestParameter("resource", res);
		addRequestParameter("operation", op);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		// ================ set session info ========================

		actionPerform();		// 執行 action

		AccountObject account = this.accountMapper.getAccount(accountId);
		HashMap<String, ProjectRole> roleMap = account.getRoles();

		// 測試是否正確移除 System 角色
		assertEquals(0, roleMap.size());
//		assertEquals(roleMap.length, 1);
//		assertEquals(roleMap[0].getRoleId(), "user");
	}

	// 測試將 Admin 指派到某個專案的角色，再移除此專案的角色
	public void testexecuteAdmin_Remove2() throws LogonException {
		setRequestPathInformation(this.ActionPath_RemoveUser);

		// 將 Admin 加入測試專案一的 PO
		this.AUTR.setNowAccountIsSystem();
		this.AUTR.exe_PO();

		// ================ set initial data =======================
		long id = 1;	 			// admin
		String accountId = "admin"; // admin
		long res = this.AUTR.getNowProjectObject().getId();
		String op = ScrumEnum.SCRUMROLE_PRODUCTOWNER;
		// ================ set initial data =======================    	

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(id));
		addRequestParameter("resource", String.valueOf(res));
		addRequestParameter("operation", op);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		// ================ set session info ========================

		actionPerform();		// 執行 action

		// 測試是否正確移除此角色，但是沒有移除掉 Admin 權限
		AccountObject account = this.accountMapper.getAccount(id);
		HashMap<String, ProjectRole> roleMap = account.getRoles();

		// 測試是否正確移除 System 角色
		assertEquals(1, roleMap.size());
		// 測試專案的 Role 是否正確加入，並且沒有移除 admin 權限
		assertEquals(accountId, roleMap.get("system").getScrumRole().getRoleName());
		
//		String[] Role_ID = new String[roleMap.length];
//		for (int i = 0; i < roleMap.length; i++) {
//			Role_ID[i] = roleMap[i].getRoleId();
//		}
//		// 利用 Rold ID 抽出來後排序，再做比對
//		Arrays.sort(Role_ID);
//
//		assertEquals(Role_ID[0], "admin");
//		assertEquals(Role_ID[1], "user");
	}

	// 測試將 Admin 指派到某個專案的角色，再移除 Admin 權限，該專案的角色不會移除
	public void testexecuteAdmin_Remove3() throws LogonException {
		setRequestPathInformation(this.ActionPath_RemoveUser);

		// 將 Admin 加入測試專案一的 PO
		this.AUTR.setNowAccountIsSystem();
		this.AUTR.exe_PO();

		// ================ set initial data =======================
		long id = 1; 			// admin
		long res = this.AUTR.getNowProjectObject().getId();
		String pid = this.AUTR.getNowProjectObject().getName();
		String op = ScrumEnum.SCRUMROLE_ADMIN;
		// ================ set initial data =======================    	

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(id));
		addRequestParameter("resource", String.valueOf(res));
		addRequestParameter("operation", op);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		// ================ set session info ========================

		actionPerform();		// 執行 action

		// 測試是否正確移除此角色，但是沒有移除掉 Admin 權限
		AccountObject account = this.accountMapper.getAccount(id);
		HashMap<String, ProjectRole> roleMap = account.getRoles();

		// 測試是否正確移除 System 角色
		assertEquals(1, roleMap.size());
		assertEquals(ScrumEnum.SCRUMROLE_PRODUCTOWNER, roleMap.get(pid).getScrumRole().getRoleName());
	}

	/**
	 * 1. admin 建立專案 (setup done)
	 * 2. admin 建立角色(setup done)
	 * 3. admin 指定 user 到專案中擔任 PO
	 * 4. admin 移除 user 在專案中的角色
	 * 5. user 登入ezScrum
	 * 6. 測試user不會看到專案資訊
	 * @throws LogonException
	 */
	public void testexecuteAdmin_Remove_IntegrationTest() throws LogonException {
		//	=============== common data ============================
		AccountObject account = this.CA.getAccountList().get(0);
		IUserSession userSession = getUserSession(account);
		long userId = account.getId();			// 取得第一筆  ID
		String userAccount = account.getUsername();	// 取得第一筆 Account ID
		long projectID = this.CP.getAllProjects().get(0).getId();

		/**
		 * 3. admin 指定 user 到專案中擔任 PO (將 user 加入測試專案一的 PO)
		 */
		this.AUTR.setAccountIndex(0);
		this.AUTR.exe_PO();

		/**
		 * 4. admin 移除 user 在專案中的角色
		 */
		// ================ set initial data =======================
		setRequestPathInformation(this.ActionPath_RemoveUser);
		long res = projectID;
		String op = ScrumEnum.SCRUMROLE_PRODUCTOWNER;

		// ================== set parameter info ====================
		addRequestParameter("id", String.valueOf(userId));
		addRequestParameter("resource", String.valueOf(res));
		addRequestParameter("operation", op);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());

		actionPerform();		// 執行 action

		// 測試是否正確移除此角色，但是沒有移除掉 Admin 權限
		AccountObject actualAccount = this.accountMapper.getAccount(userAccount);
		HashMap<String, ProjectRole> roleMap = actualAccount.getRoles();

		// 測試是否正確移除 System 角色
		assertEquals(0, roleMap.size());
//		// 測試專案的 Role 是否正確加入，並且沒有移除 admin 權限
//		String[] Role_ID = new String[roles.length];
//		for (int i = 0; i < roles.length; i++) {
//			Role_ID[i] = roles[i].getRoleId();
//		}
//		// 利用 Rold ID 抽出來後排序，再做比對
//		Arrays.sort(Role_ID);
//
//		assertEquals(Role_ID[0], "user");

		/**
		 * 5. user 登入 ezScrum
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();

		// ================ set action info ========================
		String actionPath_logonSubmit = "/logonSubmit";
		setRequestPathInformation(actionPath_logonSubmit);

		// ================== set parameter info ====================
		String loginUserID = userAccount;
		String loginUserPassword = this.CA.getAccount_PWD(1);
		;
		LogonForm logonForm = new LogonForm();
		logonForm.setUserId(loginUserID);
		logonForm.setPassword(loginUserPassword);
		setActionForm(logonForm);

		// ================ 執行 login action ======================
		actionPerform();

		// ================ assert ======================
		verifyForward("success");

		/**
		 * 6. view project list (測試user不會看到專案資訊)
		 */
		// ================ clean previous action info ========================
		cleanActionInformation();

		// ================ set action info ========================
		String actionPath_viewProjectList = "/viewProjectList";
		setRequestPathInformation(actionPath_viewProjectList);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", userSession);

		// ================ 執行 view project list action ======================
		actionPerform();

		// ================ assert ========================
		//	assert response text
		String viewProjectListExpectedResponseText = "<Projects></Projects>";
		String viewProjectListActualResponseText = this.response.getWriterBuffer().toString();
		assertEquals(viewProjectListExpectedResponseText, viewProjectListActualResponseText);

	}

	private String getRole(String res, String op) {
		return res + "_" + op;
	}

	private IUserSession getUserSession(AccountObject account) {
		IUserSession userSession = new UserSession(account);
		return userSession;
	}
}
