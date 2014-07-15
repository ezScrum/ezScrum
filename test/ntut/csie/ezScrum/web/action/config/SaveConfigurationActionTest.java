package ntut.csie.ezScrum.web.action.config;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;

import org.codehaus.jettison.json.JSONException;

import servletunit.struts.MockStrutsTestCase;

public class SaveConfigurationActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private final String actionPath = "/saveConfiguration";
	private Configuration configuration;
	private IProject project;

	public SaveConfigurationActionTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		configuration = new Configuration();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();

		// 新增一測試專案
		CP = new CreateProject(1);
		CP.exeCreate();
		project = this.CP.getProjectList().get(0);
		
		super.setUp();
		
		setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(actionPath);

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(false);
		configuration.store();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();

		// 刪除測試檔案
		CopyProject copyProject = new CopyProject(CP);
		copyProject.exeDelete_Project();

		// ============= release ==============
		ini = null;
		copyProject = null;
		CP = null;
		configuration = null;

		super.tearDown();
	}

	public void testSaveConfigurationAction() throws JSONException {
		
		String originServerUrl = configuration.getServerUrl();
		String originDBAccount = configuration.getDBAccount();
		String originDBPassword = configuration.getDBPassword();
		String originDBName = configuration.getDBName();
		String originDBType= configuration.getDBType();
		
		// ================ set request info ========================
		String projectName = this.project.getName();
		String actualServerUrl = "127.0.0.1";
		String actualDBAccount = "test";
		String actualDBPassword = "1234";
		String actualDBType = "MySQL";
		String actualDBName = "ezscrum_test";
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("ServerUrl", actualServerUrl);
		addRequestParameter("DBAccount", actualDBAccount);
		addRequestParameter("DBPassword", actualDBPassword);
		addRequestParameter("DBType", actualDBType);
		addRequestParameter("DBName", actualDBName);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
		// ================ 執行 action ===============================
		actionPerform();

		// ================ assert ==================================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		configuration = new Configuration();
		
		// assert response text
		String expectServerUrl = configuration.getServerUrl();
		String expectDBAccount = configuration.getDBAccount();
		String expectDBType = configuration.getDBType();
		String expectDBName = configuration.getDBName();
		
		assertEquals(expectServerUrl, actualServerUrl);
		assertEquals(expectDBAccount, actualDBAccount);
		assertEquals(expectDBType, actualDBType);
		assertEquals(expectDBName, actualDBName);
		
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("ServerUrl", originServerUrl);
		addRequestParameter("DBAccount", originDBAccount);
		addRequestParameter("DBPassword", originDBPassword);
		addRequestParameter("DBType", originDBType);
		addRequestParameter("DBName", originDBName);
		actionPerform();
	}
}
