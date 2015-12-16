package ntut.csie.ezScrum.web.action.backlog;

import java.io.File;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.databaseEnum.RoleEnum;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import servletunit.struts.MockStrutsTestCase;

public class GetAddSprintTaskInfoActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	
	public GetAddSprintTaskInfoActionTest(String testMethod) {
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
		
		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent")); // 設定讀取的
		// struts-config
		// 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/getAddSprintTaskInfo");

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws Exception{
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		projectManager = null;
		mCP = null;
		mCS = null;
		mConfig = null;
	}
	
	public void testNullSprint() {

		ProjectObject project = mCP.getAllProjects().get(0);

		// ================== set parameter info ====================
		addRequestParameter("sprintId", "0");

		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager
																	// 會對URL的參數作分析
																	// ,未帶入此參數無法存入session
		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		assertEquals("<Handlers><Partner></Partner><Handler></Handler></Handlers>", response.getWriterBuffer().toString());
	}

	public void testNotNullSprint() {
		ProjectObject project = mCP.getAllProjects().get(0);
		
		// Create Sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一測試專案
		
		SprintObject sprint = mCS.getSprints().get(0);

		AccountObject account = new AccountObject("test");
		account.setEnable(true)
		       .setNickName("TEST_NICK_NAME")
		       .setPassword("TEST_PASSWORD")
		       .setEmail("TEST_EMAIL")
		       .save();
		
		AccountObject account2 = new AccountObject("test2");
		account2.setEnable(true)
		       .setNickName("TEST_NICK_NAME2")
		       .setPassword("TEST_PASSWORD2")
		       .setEmail("TEST_EMAIL2")
		       .save();
		
		//所有 Sprint 封裝成 XML 給 Ext(ComboBox) 使用
		StringBuilder mStringBuilder = new StringBuilder();
		// set account into project
		account.joinProjectWithScrumRole(project.getId(), RoleEnum.ScrumTeam);
		account2.joinProjectWithScrumRole(project.getId(), RoleEnum.ScrumTeam);
		
		// ================== set parameter info ====================
		addRequestParameter("sprintId", String.valueOf(sprint.getId()));
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager
																	// 會對URL的參數作分析
																	// ,未帶入此參數無法存入session
		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();	
		
		// Get Expected String
		ArrayList<AccountObject> projectWorkers = ProjectMapper.getProjectWorkers(project.getId());
		ArrayList<String> actors = new ArrayList<>();
		actors.add("");
		for (AccountObject worker : projectWorkers) {
			actors.add(worker.getUsername());
		}
		String defaultActor = "";
		if (actors != null) {
			for (int i = 0; i < actors.size(); i++) {
				//預設角色會有一個為null
				if (i > 1) {
					defaultActor += "; ";
				}
				defaultActor += actors.get(i);
			}
		}
		
		mStringBuilder.append("<Handlers><Partner><Name>" + defaultActor + "</Name></Partner>");
		for (String handler : actors) {
			mStringBuilder.append("<Handler>");
			mStringBuilder.append("<Name>" + handler + "</Name>");
			mStringBuilder.append("</Handler>");
		}
		mStringBuilder.append("</Handlers>");
		
		assertEquals("<Handlers><Partner><Name>test; test2</Name></Partner><Handler><Name></Name></Handler><Handler><Name>test</Name></Handler><Handler><Name>test2</Name></Handler></Handlers>", response.getWriterBuffer().toString());
	}
}
