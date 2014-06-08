package ntut.csie.ezScrum.web.action.config;

import java.io.File;

import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class ShowConfigurationActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private final String actionPath = "/showConfiguration";
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private IProject project;

	public ShowConfigurationActionTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(config);
		ini.exe();

		// 新增一測試專案
		CP = new CreateProject(1);
		CP.exeCreate();
		project = this.CP.getProjectList().get(0);

		super.setUp();

		setContextDirectory(new File(config.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(actionPath);

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(config);
		ini.exe();

		// 刪除測試檔案
		CopyProject copyProject = new CopyProject(CP);
		copyProject.exeDelete_Project();

		// ============= release ==============
		ini = null;
		copyProject = null;
		CP = null;

		super.tearDown();
	}

	public void testShowConfigurationAction() {
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		
		// ================ 執行 action ===============================
		actionPerform();

		// ================ assert ==================================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		// assert response text
		String actualResponse = response.getWriterBuffer().toString();
		StringBuilder expectedResponse = new StringBuilder();
		System.out.println("actualResponse = " + actualResponse);
		assertEquals(expectedResponse.toString(), actualResponse);
	}
}
