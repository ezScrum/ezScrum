package ntut.csie.ezScrum.web.action.backlog;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxGetSprintBacklogDateInfoActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	
	public AjaxGetSprintBacklogDateInfoActionTest(String testMethod) {
        super(testMethod);
    }

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		mCP = new CreateProject(1);
		mCP.exeCreate(); // 新增一測試專案

		mCS = new CreateSprint(3, mCP);
		mCS.exe(); // 新增三筆Sprint Plan

		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));	// 設定讀取的struts-config檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/AjaxGetSprintBacklogDateInfo");

		// ============= release ==============
		ini = null;
	}
	
	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); 	// 初始化 SQL

		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mConfig = null;

		super.tearDown();
	}
	
	public void testExecute() throws Exception {
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		// ================ set initial data =======================
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
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