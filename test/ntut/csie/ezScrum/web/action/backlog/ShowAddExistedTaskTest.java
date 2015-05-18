package ntut.csie.ezScrum.web.action.backlog;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.DropTask;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class ShowAddExistedTaskTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private final String ACTION_PATH = "/showAddExistedTask2";
	private IProject mProject;
	
	public ShowAddExistedTaskTest(String testName) {
		super(testName);
	}
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		//	新增一個測試專案
		mCP = new CreateProject(1);
		mCP.exeCreate();
		mProject = mCP.getProjectList().get(0);
		
		//	新增一個Sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();
		
		super.setUp();
		
		// ================ set action info ========================
		setContextDirectory( new File(mConfig.getBaseDirPath()+ "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo( ACTION_PATH );
		
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();
		
		ini = null;
		projectManager = null;
		mCP = null;
		mCS = null;
		mConfig = null;
	}
	
	/**
	 * 測試沒有Droped Task的情況
	 */
	public void testShowAddExistTask_1() throws Exception {
		// 加入1個Sprint
		long sprintID = mCS.getSprintsId().get(0);
		// Sprint加入1個Story
		AddStoryToSprint ASTS = new AddStoryToSprint(1, 1, (int) sprintID, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASTS.exe();
		// Story加入1個Task
		AddTaskToStory ATTS = new AddTaskToStory(1, 1, ASTS, mCP);
		ATTS.exe();

		
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);	
		// 設定新增Task所需的資訊
		String expectedStoryID = "1";
		String expectedSprintID = "1";
		
		addRequestParameter("sprintID", expectedSprintID);
		addRequestParameter("issueID", expectedStoryID);

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("<Tasks></Tasks>");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
	
	/**
	 * 測試有一個Droped Task的情況
	 */
	public void testShowAddExistTask_2() throws Exception {
		// 加入1個Sprint
		long sprintID = mCS.getSprintsId().get(0);
		// Sprint加入1個Story
		AddStoryToSprint ASTS = new AddStoryToSprint(1, 1, (int) sprintID, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASTS.exe();
		// Story加入1個Task
		AddTaskToStory ATTS = new AddTaskToStory(1, 1, ASTS, mCP);
		ATTS.exe();
		// drop Task from story
		DropTask DT = new DropTask(mCP, 1, 1, 1);
		DT.exe();
		
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);	
		// 設定新增Task所需的資訊
		String expectedTaskName = "TEST_TASK_1";
		String expectedStoryID = "1";
		String expectedSprintID = "1";
		String expectedTaskEstimation= "1";
		String expectedTaskNote = "TEST_TASK_NOTES_1";
		
		addRequestParameter("sprintID", expectedSprintID);
		addRequestParameter("issueID", expectedStoryID);

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("<Tasks><Task><Id>").append(1)	
							.append("</Id><Link>/ezScrum/showIssueInformation.do?issueID=").append(1)
							.append("</Link><Name>").append(expectedTaskName)
							.append("</Name><Status>").append("new")
							.append("</Status><Estimate>").append(expectedTaskEstimation)
							.append("</Estimate><Actual>").append(0)
							.append("</Actual><Handler></Handler><Partners></Partners><Notes>").append(expectedTaskNote)
							.append("</Notes></Task></Tasks>");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
