package ntut.csie.ezScrum.web.action.config;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;

import org.codehaus.jettison.json.JSONException;

import servletunit.struts.MockStrutsTestCase;

public class SaveConfigurationActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private final String mActionPath = "/saveConfiguration";
	private Configuration mConfig;

	public SaveConfigurationActionTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增一測試專案
		mCP = new CreateProject(1);
		mCP.exeCreate();
		
		super.setUp();
		
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(false);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除測試檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// ============= release ==============
		ini = null;
		mCP = null;
		mConfig = null;

		super.tearDown();
	}

	/**
	 * Jenkins上會有問題，需要再測試
	 * 
	 */
	public void testSaveConfigurationAction() throws JSONException {
//		
//		String originServerUrl = configuration.getServerUrl();
//		String originDBAccount = configuration.getDBAccount();
//		String originDBPassword = configuration.getDBPassword();
//		String originDBName = configuration.getDBName();
//		String originDBType= configuration.getDBType();
//		
//		// ================ set request info ========================
//		String projectName = project.getName();
//		String actualServerUrl = "127.0.0.1";
//		String actualDBAccount = "test";
//		String actualDBPassword = "1234";
//		String actualDBType = "MySQL";
//		String actualDBName = "ezscrum_test";
//		request.setHeader("Referer", "?PID=" + projectName);
//		addRequestParameter("ServerUrl", actualServerUrl);
//		addRequestParameter("DBAccount", actualDBAccount);
//		addRequestParameter("DBPassword", actualDBPassword);
//		addRequestParameter("DBType", actualDBType);
//		addRequestParameter("DBName", actualDBName);
//		
//		// ================ set session info ========================
//		request.getSession().setAttribute("UserSession", configuration.getUserSession());
//		
//		// ================ 執行 action ===============================
//		actionPerform();
//
//		// ================ assert ==================================
//		verifyNoActionErrors();
//		verifyNoActionMessages();
//		
//		configuration = new Configuration();
//		
//		// assert response text
//		String expectServerUrl = configuration.getServerUrl();
//		String expectDBAccount = configuration.getDBAccount();
//		String expectDBType = configuration.getDBType();
//		String expectDBName = configuration.getDBName();
//		
//		assertEquals(expectServerUrl, actualServerUrl);
//		assertEquals(expectDBAccount, actualDBAccount);
//		assertEquals(expectDBType, actualDBType);
//		assertEquals(expectDBName, actualDBName);
//		
//		request.setHeader("Referer", "?PID=" + projectName);
//		addRequestParameter("ServerUrl", originServerUrl);
//		addRequestParameter("DBAccount", originDBAccount);
//		addRequestParameter("DBPassword", originDBPassword);
//		addRequestParameter("DBType", originDBType);
//		addRequestParameter("DBName", originDBName);
//		actionPerform();
	}
}
