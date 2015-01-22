package ntut.csie.ezScrum.web.action.export;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxGetVelocityActionTest extends MockStrutsTestCase {
	private Configuration configuration;
	private String actionPath = "/ajaxGetVelocity";
	private IUserSession userSession = null;
	private CreateProject CP;
	private CreateRelease CR;
	private CreateSprint CS;
	private IProject project;

	public AjaxGetVelocityActionTest(String testMethod) {
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
		this.project = this.CP.getProjectList().get(0);

		userSession = configuration.getUserSession();

		super.setUp();

		setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent"));
		// 設定讀取的
		// struts-config
		// 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(actionPath);

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		CopyProject copyProject = new CopyProject(this.CP);
		copyProject.exeDelete_Project(); // 刪除測試檔案
		
		configuration.setTestMode(false);
		configuration.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		copyProject = null;
		this.CP = null;
		this.CR = null;
		this.CS = null;
		userSession = null;
		configuration = null;

		super.tearDown();
	}

	/**
	 * project中沒有任何release plan的時候
	 */
	public void testAjaxGetVelocityAction_1() {
		// ================ set initial data =======================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		addRequestParameter("releases", "");

		// ================ 執行 action ===============================
		actionPerform();

		// ================ assert ==================================
		verifyNoActionErrors();
		verifyNoActionMessages();

		/**
		 * {"Sprints":[],"Average":""}
		 */
		// assert response text
		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("{")
							.append("\"Sprints\":[],")
							.append("\"Average\":\"\"")
							.append("}");
		String actualResponseTest = response.getWriterBuffer().toString();
		assertEquals(expectedResponseTest.toString(), actualResponseTest);
	}

	/**
	 * project中有1個release plan的時候, 但releaseID不存在
	 */
	public void testAjaxGetVelocityAction_2() {
		// ================ set initial data =======================
		this.CR = new CreateRelease(1, this.CP);
		this.CR.exe();
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		addRequestParameter("releases", "3");

		// ================ 執行 action ===============================
		actionPerform();

		// ================ assert ==================================
		verifyNoActionErrors();
		verifyNoActionMessages();

		/**
		 * {"Sprints":[],"Average":""}
		 */
		// assert response text
		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("{")
							.append("\"Sprints\":[],")
							.append("\"Average\":\"\"")
							.append("}");
		String actualResponseTest = response.getWriterBuffer().toString();
		assertEquals(expectedResponseTest.toString(), actualResponseTest);
	}

	/**
	 * project中有1個release plan的時候
	 */
	public void testAjaxGetVelocityAction_3() throws Exception {
		// ================ set initial data =======================
		this.CR = new CreateRelease(1, this.CP);
		this.CR.exe();
		CS = new CreateSprint(2, CP); 				// 新增2筆 sprints
		CS.exe();
		AddStoryToSprint ASS = new AddStoryToSprint(2, 1, CS, CP, CreateProductBacklog.TYPE_ESTIMATION);
		ASS.exe(); // 每個Sprint中新增2筆Story
		// 讓所有story都done
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, userSession, CS.getSprintIDList().get(0));
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(userSession, project);
		IStory[] stories = productBacklogLogic.getStories();
		for (int i = 0; i < stories.length; i++) {
			sprintBacklogLogic.doneIssue(stories[i].getStoryId(), stories[i].getSummary(), stories[i].getNotes(), "", stories[i].getActualHour());
		}

		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		addRequestParameter("releases", "1");

		// ================ 執行 action ===============================
		actionPerform();

		// ================ assert ==================================
		verifyNoActionErrors();
		verifyNoActionMessages();

		/**
		 * {"Sprints":[
		 * 		{"ID":"1","Name":"Sprint1","Velocity":2},
		 * 		{"ID":"2","Name":"Sprint2","Velocity":2}],
		 *  "Average":2}
		 */
		// assert response text
		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("{")
							.append("\"Sprints\":[{")
								.append("\"ID\":\"1\",")
								.append("\"Name\":\"Sprint1\",")
								.append("\"Velocity\":2},")
								.append("{\"ID\":\"2\",")
								.append("\"Name\":\"Sprint2\",")
								.append("\"Velocity\":2}],")
							.append("\"Average\":2}");
		String actualResponseTest = response.getWriterBuffer().toString();
		assertEquals(expectedResponseTest.toString(), actualResponseTest);
	}
}
