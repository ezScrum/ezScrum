package ntut.csie.ezScrum.web.support;

import java.io.File;

import ntut.csie.ezScrum.dao.ProjectDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;

import org.junit.Test;

import servletunit.struts.MockStrutsTestCase;

public class SessionManagerTest extends MockStrutsTestCase{
	private ProjectObject mProject = null;
	private Configuration mConfig = null;
    private SessionManager mSessionManager = null;
	private final String mACTION_PATH = "/getProjectMembers";
	
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
		setRequestPathInfo(mACTION_PATH);
	}
	
	@Override
	protected void tearDown() throws Exception{
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
		
		ini = null;
		mConfig = null;
		mProject = null;
		mSessionManager = null;
		super.tearDown();
	}
	
	@Test
	public void testGetProjectObject() {
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("_dc", String.valueOf(System.currentTimeMillis()));
		
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		request.getSession().setAttribute(projectName, mProject);
		mSessionManager = new SessionManager(request);

		ProjectObject projectObject =  SessionManager.getProjectObject(request);
		assertNotNull(projectObject);
	}
	
	@Test
	public void testGetScrumRole() throws Exception {
		// ================ set request info ========================
		String projectName = mProject.getName();
		long projectId = mProject.getId();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("_dc", String.valueOf(System.currentTimeMillis()));
		
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		request.getSession().setAttribute(projectName, mProject);
		
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
		ScrumRole role = SessionManager.getScrumRole(request, mProject, account);
		
		assertNotNull(role);
		assertTrue(role.getEditProject());
	}
	
	@Test
	public void testSetProjectObject(){
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("_dc", String.valueOf(System.currentTimeMillis()));

		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		request.getSession().setAttribute(projectName, mProject);

		mSessionManager = new SessionManager(request);
		mSessionManager.setProjectObject(request, mProject);
		
		// assert
		ProjectObject projectObject = (ProjectObject) request.getSession().getAttribute(mProject.getName());
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
		
		mProject = new ProjectObject(projectName);
		mProject.setDisplayName(displayName);
		mProject.setManager(manager);
		mProject.setCreateTime(System.currentTimeMillis());
		mProject.setAttachFileSize(attachFileMaxSize);
		mProject.save();
		mProject.reload();
	}
	
}
