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
		mConfig.setTestMode(true);
		mConfig.save();
		
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
		// 初始化 SQL
		mConfig = new Configuration(); // reload config info
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

	/**
	 * Jenkins上會有問題，需要再測試
	 * 
	 */
	public void testSaveConfigurationAction() throws JSONException {
		String originServerUrl = mConfig.getServerUrl();
		String originDBAccount = mConfig.getDBAccount();
		String originDBPassword = mConfig.getDBPassword();
		String originDBName = mConfig.getDBName();
		String originDBType= mConfig.getDBType();
		
		// ================ set request info ========================
		String actualServerUrl = "127.0.0.1";
		String actualDBAccount = "test";
		String actualDBPassword = "1234";
		String actualDBType = "MySQL";
		String actualDBName = "ezscrum_test";
		addRequestParameter("ServerUrl", actualServerUrl);
		addRequestParameter("DBAccount", actualDBAccount);
		addRequestParameter("DBPassword", actualDBPassword);
		addRequestParameter("DBType", actualDBType);
		addRequestParameter("DBName", actualDBName);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ===============================
		actionPerform();

		// ================ assert ==================================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		mConfig = new Configuration();
		
		// assert response text
		String expectServerUrl = mConfig.getServerUrl();
		String expectDBAccount = mConfig.getDBAccount();
		String expectDBType = mConfig.getDBType();
		String expectDBName = mConfig.getDBName();
		
		assertEquals(expectServerUrl, actualServerUrl);
		assertEquals(expectDBAccount, actualDBAccount);
		assertEquals(expectDBType, actualDBType);
		assertEquals(expectDBName, actualDBName);
		
		// 將 ezScrum.ini 改回原來的值
		addRequestParameter("ServerUrl", originServerUrl);
		addRequestParameter("DBAccount", originDBAccount);
		addRequestParameter("DBPassword", originDBPassword);
		addRequestParameter("DBType", originDBType);
		addRequestParameter("DBName", originDBName);
		
		// ================ 執行 action ===============================
		actionPerform();
	}
}
