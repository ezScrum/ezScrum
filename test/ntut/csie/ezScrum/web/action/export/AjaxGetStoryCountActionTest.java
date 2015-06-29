package ntut.csie.ezScrum.web.action.export;

import java.io.File;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import servletunit.struts.MockStrutsTestCase;

public class AjaxGetStoryCountActionTest extends MockStrutsTestCase {
	private Configuration mConfig;
	private String mActionPath = "/ajaxGetStoryCount";
	private CreateProject mCP;
	private CreateRelease mCR;
	private CreateSprint mCS;
	private ProjectObject mProject;

	public AjaxGetStoryCountActionTest(String testMethod) {
		super(testMethod);
	}

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		super.setUp();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		mCP = new CreateProject(1);
		mCP.exeCreate(); // 新增一測試專案
		mProject = mCP.getAllProjects().get(0);

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));

		// 設定讀取的
		// struts-config
		// 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
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
		mCR = null;
		mCS = null;
		mConfig = null;
	}

	/**
	 * project中沒有任何release plan的時候
	 */
	@Test
	public void testAjaxGetStoryCountAction_1() {
		// ================ set initial data =======================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		addRequestParameter("releases", "");

		// ================ 執行 action ===============================
		actionPerform();

		// ================ assert ==================================
		verifyNoActionErrors();
		verifyNoActionMessages();

		/**
		 * {"Sprints":[],"TotalSprintCount":0,"TotalStoryCount":0}
		 */
		// assert response text
		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("{")
		        .append("\"Sprints\":[],")
		        .append("\"TotalSprintCount\":0,")
		        .append("\"TotalStoryCount\":0")
		        .append("}");
		String actualResponseTest = response.getWriterBuffer().toString();
		assertEquals(expectedResponseTest.toString(), actualResponseTest);
	}

	/**
	 * project中有1個release plan的時候, 但releaseID不存在
	 */
	@Test
	public void testAjaxGetStoryCountAction_2() {
		// ================ set initial data =======================
		mCR = new CreateRelease(1, mCP);
		mCR.exe();

		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		addRequestParameter("releases", "2");

		// ================ 執行 action ===============================
		actionPerform();

		// ================ assert ==================================
		verifyNoActionErrors();
		verifyNoActionMessages();

		/**
		 * {"Sprints":[],"TotalSprintCount":0,"TotalStoryCount":0}
		 */
		// assert response text
		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("{")
		        .append("\"Sprints\":[],")
		        .append("\"TotalSprintCount\":0,")
		        .append("\"TotalStoryCount\":0")
		        .append("}");
		String actualResponseTest = response.getWriterBuffer().toString();
		assertEquals(expectedResponseTest.toString(), actualResponseTest);
	}

	/**
	 * project中有1個release plan的時候
	 */
	@Test
	public void testAjaxGetStoryCountAction_3() throws Exception {
		// ================ set initial data =======================
		mCR = new CreateRelease(1, this.mCP);
		mCR.exe();
		
		mCS = new CreateSprint(2, mCP); // 新增2筆 sprints
		mCS.exe();

		AddStoryToSprint ASS = new AddStoryToSprint(2, 1, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASS.exe(); // 每個Sprint中新增2筆Story

		// 讓所有story都done
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(mProject, mCS.getSprintsId().get(0));
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(mProject);

		ArrayList<StoryObject> stories = productBacklogLogic.getStories();
		for (int i = 0; i < stories.size(); i++) {
			sprintBacklogLogic.closeStory(stories.get(i).getId(), stories.get(i).getName(), stories.get(i).getNotes(), "2015/05/13-12:00:00");
		}

		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("releases", "1");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// ================ 執行 action ===============================
		actionPerform();

		// ================ assert ==================================
		verifyNoActionErrors();
		verifyNoActionMessages();

		// assert response text
		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("{")
		        .append("\"Sprints\":[{")
		        .append("\"ID\":\"1\",")
		        .append("\"Name\":\"Sprint1\",")
		        .append("\"StoryDoneCount\":2,\"StoryRemainingCount\":2,\"StoryIdealCount\":2},")
		        .append("{\"ID\":\"2\",")
		        .append("\"Name\":\"Sprint2\",")
		        .append("\"StoryDoneCount\":2,\"StoryRemainingCount\":0,\"StoryIdealCount\":0}")
		        .append("],\"TotalSprintCount\":2,")
		        .append("\"TotalStoryCount\":4")
		        .append("}");
		String actualResponseTest = response.getWriterBuffer().toString();
		assertEquals(expectedResponseTest.toString(), actualResponseTest);
	}
}
