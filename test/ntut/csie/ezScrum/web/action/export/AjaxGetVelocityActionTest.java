package ntut.csie.ezScrum.web.action.export;

import java.io.File;

import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxGetVelocityActionTest extends MockStrutsTestCase {
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private String actionPath = "/ajaxGetVelocity";
	private CreateProject CP;
	private IProject project;
	
	public AjaxGetVelocityActionTest(String testMethod) {
		super(testMethod);
	}
	
	protected void setUp() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe(); // 初始化 SQL
		
		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案
		this.project = this.CP.getProjectList().get(0);
		
		super.setUp();

		setContextDirectory(new File(config.getBaseDirPath() + "/WebContent"));
		// 設定讀取的
		// struts-config
		// 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(actionPath);

		// ============= release ==============
		ini = null;
	}
	
	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe(); // 初始化 SQL

		CopyProject copyProject = new CopyProject(this.CP);
		copyProject.exeDelete_Project(); // 刪除測試檔案

		super.tearDown();

		// ============= release ==============
		ini = null;
		copyProject = null;
		this.CP = null;
	}
	
	public void testAjaxGetVelocityAction_1() {
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName + "&releases=");
		
		request.getSession().setAttribute("UserSession", config.getUserSession());
		
		actionPerform();
		
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		StringBuilder expectedResponseTest = new StringBuilder();
		String actualResponseTest = response.getWriterBuffer().toString();
		assertEquals(expectedResponseTest.toString(), actualResponseTest);
	}
}
