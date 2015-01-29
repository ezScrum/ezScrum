package ntut.csie.ezScrum.web.support;

import java.io.File;

import ntut.csie.ezScrum.dao.ProjectDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;
import servletunit.struts.MockStrutsTestCase;

public class SessionManagerTest extends MockStrutsTestCase{
	private ProjectObject project = null;
	private Configuration mConfig = null;
	@SuppressWarnings("unused")
    private SessionManager mSessionManager = null;
	private final String ACTION_PATH = "/getProjectMembers";
	
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 建立測試資料
		createTestData();

		// ================ set action info ========================
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(ACTION_PATH);
	}
	
	@Override
	protected void tearDown() throws Exception{
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mConfig.setTestMode(false);
		mConfig.save();
		
		ini = null;
		mConfig = null;
		mSessionManager = null;
		super.tearDown();
	}
	
	public void testGetProjectObject() {
		// ================ set request info ========================
		String projectName = project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("_dc", String.valueOf(System.currentTimeMillis()));
		
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		request.getSession().setAttribute(projectName, project);
		mSessionManager = new SessionManager(request);

		ProjectObject projectObject =  SessionManager.getProjectObject(request);
		assertNotNull(projectObject);
	}
	
	public void testGetScrumRole() throws Exception {
		// ================ set request info ========================
		String projectName = project.getName();
		long projectId = project.getId();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("_dc", String.valueOf(System.currentTimeMillis()));
		
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		request.getSession().setAttribute(projectName, project);
		
		mSessionManager = new SessionManager(request);
		
		// create account
		String accountName = "TEST_ACCOUNT_1";
		String nickname = "TEST_NICKNAME_1";
		String password = "TEST_PASSWORD_1";
		String email = "TEST_EMAIL_1";
		AccountObject account = new AccountObject(accountName);
		account.setNickName(nickname);
		account.setPassword(password);
		account.setEmail(email);
		account.save();
		account.reload();
		// create project role
		boolean createRoleResult = account.createProjectRole(projectId, RoleEnum.ProductOwner);
		assertTrue(createRoleResult);
		
		// create Scrum role
		ScrumRole scrumRole = new ScrumRole(RoleEnum.ProductOwner);
		ProjectDAO.getInstance().createScrumRole(projectId, RoleEnum.ProductOwner, scrumRole);
		
		// getScrumRole
		ScrumRole role = SessionManager.getScrumRole(request, project, account);
		
		assertNotNull(role);
		assertTrue(role.getEditProject());
	}
	
	public void testSetProjectObject(){
		// ================ set request info ========================
		String projectName = project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("_dc", String.valueOf(System.currentTimeMillis()));

		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		request.getSession().setAttribute(projectName, project);

		mSessionManager = new SessionManager(request);
		SessionManager.setProjectObject(request, project);
		
		// assert
		ProjectObject projectObject = (ProjectObject) request.getSession().getAttribute(project.getName());
		assertNotNull(projectObject);
		assertEquals(projectName, projectObject.getName());
	}
	
	/**
	 * 建立測試用的專案資料
	 */
	private void createTestData(){
		String projectName = "TEST_PROJECT_1";
		String displayName = "TEST_DISPLAYNAME";
		String manager = "TEST_PROJECT_MANAGER";
		long attachFileMaxSize = 1024L;
		
		project = new ProjectObject(projectName);
		project.setDisplayName(displayName);
		project.setManager(manager);
		project.setCreateTime(System.currentTimeMillis());
		project.setAttachFileSize(attachFileMaxSize);
		project.save();
		project.reload();
	}
	
}
