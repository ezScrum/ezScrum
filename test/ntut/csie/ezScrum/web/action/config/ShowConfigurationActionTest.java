package ntut.csie.ezScrum.web.action.config;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import servletunit.struts.MockStrutsTestCase;

public class ShowConfigurationActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private final String mActionPath = "/showConfiguration";
	private Configuration mConfig;
	private IProject mProject;

	public ShowConfigurationActionTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增一測試專案
		mCP = new CreateProject(1);
		mCP.exeCreate();
		mProject = mCP.getProjectList().get(0);
		
		super.setUp();

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

		// 刪除測試檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mCP = null;
		mConfig = null;

		super.tearDown();
	}

	public void testShowConfigurationAction() throws JSONException {
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		mConfig = new Configuration(mConfig.getUserSession());
		// ================ 執行 action ===============================
		actionPerform();

		// ================ assert ==================================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		// assert response text
		String serverUrl = mConfig.getServerUrl();
		String DBAccount = mConfig.getDBAccount();
		String DBType = mConfig.getDBType();
		String DBName = mConfig.getDBName();
		JSONObject actualResponse = new JSONObject(response.getWriterBuffer().toString());
		
		assertEquals(serverUrl, actualResponse.get("ServerUrl"));
		assertEquals(DBAccount, actualResponse.get("DBAccount"));
		assertEquals(DBType, actualResponse.get("DBType"));
		assertEquals(DBName, actualResponse.get("DBName"));
	}
}
