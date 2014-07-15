package ntut.csie.ezScrum.web.action.config;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import servletunit.struts.MockStrutsTestCase;

public class ShowConfigurationActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private final String actionPath = "/showConfiguration";
	private Configuration configuration;
	private IProject project;

	public ShowConfigurationActionTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
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
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();

		// 刪除測試檔案
		CopyProject copyProject = new CopyProject(CP);
		copyProject.exeDelete_Project();
		
		configuration.setTestMode(false);
		configuration.store();

		// ============= release ==============
		ini = null;
		copyProject = null;
		CP = null;
		configuration = null;

		super.tearDown();
	}

	public void testShowConfigurationAction() throws JSONException {
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		configuration = new Configuration(configuration.getUserSession());
		// ================ 執行 action ===============================
		actionPerform();

		// ================ assert ==================================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		// assert response text
		String ServerUrl = configuration.getServerUrl();
		String DBAccount = configuration.getDBAccount();
		String DBType = configuration.getDBType();
		String DBName = configuration.getDBName();
		JSONObject actualResponse = new JSONObject(response.getWriterBuffer().toString());
		
		assertEquals(ServerUrl, actualResponse.get("ServerUrl"));
		assertEquals(DBAccount, actualResponse.get("DBAccount"));
		assertEquals(DBType, actualResponse.get("DBType"));
		assertEquals(DBName, actualResponse.get("DBName"));
	}
}
