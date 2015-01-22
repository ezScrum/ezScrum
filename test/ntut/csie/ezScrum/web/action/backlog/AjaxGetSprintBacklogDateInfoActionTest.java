package ntut.csie.ezScrum.web.action.backlog;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxGetSprintBacklogDateInfoActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private Configuration configuration;
	
	public AjaxGetSprintBacklogDateInfoActionTest(String testMethod) {
        super(testMethod);
    }

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案

		this.CS = new CreateSprint(3, this.CP);
		this.CS.exe(); // 新增三筆Sprint Plan

		super.setUp();

		setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent"));	// 設定讀取的struts-config檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/AjaxGetSprintBacklogDateInfo");

		// ============= release ==============
		ini = null;
	}
	
	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); 	// 初始化 SQL

		CopyProject copyProject = new CopyProject(this.CP);
		copyProject.exeDelete_Project(); // 刪除測試檔案
		
		configuration.setTestMode(false);
		configuration.save();

		// ============= release ==============
		ini = null;
		copyProject = null;
		this.CP = null;
		this.CS = null;
		configuration = null;

		super.tearDown();
	}
	
	public void testexecute() throws Exception {
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		// ================ set initial data =======================
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================== set parameter info ====================
		addRequestParameter("sprintID", "1"); // 第一筆 sprint
		// ================== set parameter info ====================
		
		actionPerform(); // 執行 action
		verifyNoActionErrors();
	}
}