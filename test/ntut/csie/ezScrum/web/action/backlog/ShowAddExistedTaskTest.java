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
	private CreateProject CP;
	private CreateSprint CS;
	private Configuration configuration;
	private final String ACTION_PATH = "/showAddExistedTask2";
	private IProject project;
	
	public ShowAddExistedTaskTest(String testName) {
		super(testName);
	}
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		
		//	新增一個測試專案
		this.CP = new CreateProject(1);
		this.CP.exeCreate();
		this.project = this.CP.getProjectList().get(0);
		
		//	新增一個Sprint
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe();
		
		super.setUp();
		
		// ================ set action info ========================
		setContextDirectory( new File(configuration.getBaseDirPath()+ "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo( this.ACTION_PATH );
		
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());
		
		configuration.setTestMode(false);
		configuration.store();

		super.tearDown();
		
		ini = null;
		projectManager = null;
		this.CP = null;
		this.CS = null;
		configuration = null;
	}
	
	/**
	 * 測試沒有Droped Task的情況
	 */
	public void testShowAddExistTask_1() throws Exception {
		// 加入1個Sprint
		int sprintID = Integer.valueOf(this.CS.getSprintIDList().get(0));
		// Sprint加入1個Story
		AddStoryToSprint addStory_Sprint = new AddStoryToSprint(1, 1, sprintID, CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStory_Sprint.exe();
		// Story加入1個Task
		AddTaskToStory addTask_Story = new AddTaskToStory(1, 1, addStory_Sprint, CP);
		addTask_Story.exe();

		
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);	
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
		int sprintID = Integer.valueOf(this.CS.getSprintIDList().get(0));
		// Sprint加入1個Story
		AddStoryToSprint addStory_Sprint = new AddStoryToSprint(1, 1, sprintID, CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStory_Sprint.exe();
		// Story加入1個Task
		AddTaskToStory addTask_Story = new AddTaskToStory(1, 1, addStory_Sprint, CP);
		addTask_Story.exe();
		// drop Task from story
		DropTask dropTask = new DropTask(CP, 1, 1, 2);
		dropTask.exe();
		
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);	
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
		expectedResponseText.append("<Tasks><Task><Id>").append(2)	
							.append("</Id><Link>/ezScrum/showIssueInformation.do?issueID=").append(2)
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
