package ntut.csie.ezScrum.web.action;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.dataObject.UserInformation;
import ntut.csie.ezScrum.web.helper.AccountHelper;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class GetProjectMembersActionTest extends MockStrutsTestCase {
	
	private CreateProject CP;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private final String ACTION_PATH = "/getProjectMembers";
	private IProject project;
	
	public GetProjectMembersActionTest(String testName) {
		super(testName);
	}
	
	protected void setUp() throws Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案
		this.project = this.CP.getProjectList().get(0);
		
		super.setUp();
		
		// ================ set action info ========================
		setContextDirectory( new File(config.getBaseDirPath()+ "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo( this.ACTION_PATH );
		
		ini = null;
	}
	
	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(this.config.getTestDataPath());
		
		super.tearDown();
		
		ini = null;
		projectManager = null;
		this.CP = null;
	}
	
	/**
	 * 正常狀態，新增一名帳號至專案
	 * @throws InterruptedException 
	 */
	public void testGetProjcetMembers1() throws InterruptedException {
		CreateAccount CA = new CreateAccount(1);
		CA.exe();

		AddUserToRole addUserToRole  = new AddUserToRole(CP, CA);
		addUserToRole.exe_SM();

		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("_dc", String.valueOf(System.currentTimeMillis()));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		// assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText
			.append("<Members>")
				.append("<Member>")
					.append("<ID>TEST_ACCOUNT_ID_1</ID>")
					.append("<Name>TEST_ACCOUNT_REALNAME_1</Name>")
					.append("<Role>ScrumMaster</Role>")
					.append("<Enable>true</Enable>")
				.append("</Member>")
			.append("</Members>");
		String actualResponseText = response.getWriterBuffer().toString();
		
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
	
	/**
	 * 修改顯示名稱
	 */
	public void testGetProjcetMembers2() {
		CreateAccount CA = new CreateAccount(1);
		CA.exe();
		CA.setAccount_RealName(1);
		
		AddUserToRole addUserToRole  = new AddUserToRole(CP, CA);
		addUserToRole.exe_SM();
		
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("_dc", String.valueOf(System.currentTimeMillis()));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		// assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText
			.append("<Members>")
				.append("<Member>")
					.append("<ID>TEST_ACCOUNT_ID_1</ID>")
					.append("<Name>TEST_ACCOUNT_REALNAME_NEW_1</Name>")
					.append("<Role>ScrumMaster</Role>")
					.append("<Enable>true</Enable>")
				.append("</Member>")
			.append("</Members>");
		String actualResponseText = response.getWriterBuffer().toString();
		
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
	
	/**
	 * 修改Role
	 * @throws Exception 
	 */
	public void testGetProjcetMembers3() throws Exception {
		CreateAccount CA = new CreateAccount(1);
		CA.exe();
		
		Thread.sleep(200);
		AddUserToRole addUserToRole  = new AddUserToRole(CP, CA);
		addUserToRole.exe_PO();
		
		AccountHelper ah = new AccountHelper(config.getUserSession());
		ah.assignRole_remove(CA.getAccountList().get(0).getID(), CP.getProjectList().get(0).getName(), ScrumEnum.SCRUMROLE_PRODUCTOWNER);
		
		Thread.sleep(200);
		addUserToRole.exe_SM();
		
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("_dc", String.valueOf(System.currentTimeMillis()));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		// assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText
			.append("<Members>")
				.append("<Member>")
					.append("<ID>TEST_ACCOUNT_ID_1</ID>")
					.append("<Name>TEST_ACCOUNT_REALNAME_1</Name>")
					.append("<Role>ScrumMaster</Role>")
					.append("<Enable>true</Enable>")
				.append("</Member>")
			.append("</Members>");

		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
	
	/**
	 * disable account
	 */
	public void testGetProjcetMembers4() throws InterruptedException {
		CreateAccount CA = new CreateAccount(1);
		CA.exe();
		
		AddUserToRole addUserToRole  = new AddUserToRole(CP, CA);
		addUserToRole.exe_SM();
		
		Thread.sleep(1000);
		addUserToRole.setEnable(CA, 0, false);
		
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("_dc", String.valueOf(System.currentTimeMillis()));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		// assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText
			.append("<Members>")
			.append("</Members>");
		String actualResponseText = response.getWriterBuffer().toString();
		
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
