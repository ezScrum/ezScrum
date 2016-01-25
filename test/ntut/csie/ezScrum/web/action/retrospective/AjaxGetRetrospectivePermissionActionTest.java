package ntut.csie.ezScrum.web.action.retrospective;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.databaseEnum.RoleEnum;
import servletunit.struts.MockStrutsTestCase;

public class AjaxGetRetrospectivePermissionActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	
	public AjaxGetRetrospectivePermissionActionTest(String testMethod) {
		super(testMethod);
	}
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		mCP = new CreateProject(1);
		mCP.exeCreateForDb(); // 新增一測試專案
		
		mCS = new CreateSprint(1, mCP);
		mCS.exe();
		
		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent")); // 設定讀取的
		// struts-config
		// 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/AjaxGetRETPermission");

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mConfig = null;
	}
	
	public void testScrumTeamGetRetrospectivePermission() {
		// Get Project
		ProjectObject project = mCP.getAllProjects().get(0);
		// Create Account
		AccountObject account = new AccountObject("TEST_ACCOUNT_NAME");
		account.setEnable(true)
		       .setNickName("TEST_ACCOUNT_NICK_NAME")
		       .setPassword("TEST_ACCOUNT_PASSWORD")
		       .save();
		// Add Project Role
		account.joinProjectWithScrumRole(project.getId(), RoleEnum.ScrumTeam);
		// Get Roles
		HashMap<String, ProjectRole> roles = account.getProjectRoleMap();
		HashMap<String, ScrumRole> scrumRoles = new HashMap<String, ScrumRole>();
		
		for(int i =0; i < roles.size(); i++){
			scrumRoles.put(project.getName(), roles.get(project.getName()).getScrumRole());
		}
		
		// ================ set session info ========================
		request.getSession().setAttribute("Project", project);
		request.getSession().setAttribute("ScrumRoles", scrumRoles);
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action
		
		// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();
		
		// Expected String
		StringBuilder expectedStringBuilder = new StringBuilder();
		expectedStringBuilder.append("<Permission>");
		expectedStringBuilder.append("<Function name =\"Restrospective\">");
		expectedStringBuilder.append("<AddRetrospective>true</AddRetrospective>");
		expectedStringBuilder.append("<EditRetrospective>true</EditRetrospective>");
		expectedStringBuilder.append("<DeleteRetrospective>true</DeleteRetrospective>");
		expectedStringBuilder.append("</Function>");
		expectedStringBuilder.append("</Permission>");
		
		// assert
		assertEquals(expectedStringBuilder.toString(), response.getWriterBuffer().toString());
	}
	
	public void testGuestGetRetrospectivePermission() {
		// Get Project
		ProjectObject project = mCP.getAllProjects().get(0);
		// Create Account
		AccountObject account = new AccountObject("TEST_ACCOUNT_NAME");
		account.setEnable(true)
		       .setNickName("TEST_ACCOUNT_NICK_NAME")
		       .setPassword("TEST_ACCOUNT_PASSWORD")
		       .save();
		// Add Project Role
		account.joinProjectWithScrumRole(project.getId(), RoleEnum.Guest);
		// Get Roles
		HashMap<String, ProjectRole> roles = account.getProjectRoleMap();
		HashMap<String, ScrumRole> scrumRoles = new HashMap<String, ScrumRole>();
		
		for(int i =0; i < roles.size(); i++){
			scrumRoles.put(project.getName(), roles.get(project.getName()).getScrumRole());
		}
		
		// ================ set session info ========================
		request.getSession().setAttribute("Project", project);
		request.getSession().setAttribute("ScrumRoles", scrumRoles);
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action
		
		// 驗證回傳 path
    	verifyForwardPath(null);
    	verifyForward(null);
    	verifyNoActionErrors();
		
		// Expected String
		StringBuilder expectedStringBuilder = new StringBuilder();
		expectedStringBuilder.append("<Permission>");
		expectedStringBuilder.append("<Function name =\"Restrospective\">");
		expectedStringBuilder.append("<AddRetrospective>false</AddRetrospective>");
		expectedStringBuilder.append("<EditRetrospective>false</EditRetrospective>");
		expectedStringBuilder.append("<DeleteRetrospective>false</DeleteRetrospective>");
		expectedStringBuilder.append("</Function>");
		expectedStringBuilder.append("</Permission>");
		
		// assert
		assertEquals(expectedStringBuilder.toString(), response.getWriterBuffer().toString());
	}

}
