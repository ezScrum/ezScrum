package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddSprintToRelease;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxAddSprintTaskActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateRelease CR;
	private Configuration configuration;
	private final String ACTION_PATH = "/ajaxAddSprintTask";
	private IProject project;
	
	public AjaxAddSprintTaskActionTest(String testName) {
		super(testName);
	}
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		
		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案

		this.CR = new CreateRelease(1, this.CP);
		this.CR.exe(); // 新增一筆Release Plan
		
		this.project = this.CP.getProjectList().get(0);
		
		super.setUp();
		
		// ================ set action info ========================
		setContextDirectory(new File(configuration.getBaseDirPath().concat("/WebContent")));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(this.ACTION_PATH);
		
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getTestDataPath());
		
		configuration.setTestMode(false);
		configuration.store();

		super.tearDown();
		
		ini = null;
		projectManager = null;
		this.CP = null;
		this.CR = null;
		configuration = null;
	}

	/**
	 * 測試全部欄位都填寫的情況
	 */
	public void testAddSprintTask_1() throws Exception {
		// 在Release中加入1個Sprint
		AddSprintToRelease addSprint = new AddSprintToRelease(1, CR, CP);
		addSprint.exe();
		
		// Sprint加入1個Story
		AddStoryToSprint addStory_Sprint = new AddStoryToSprint(1, 1, 1, CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStory_Sprint.exe();
		
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);	
		// 設定新增Task所需的資訊
		String expectedTaskName = "UT for Add New Task for Name";
		String expectedStoryID = "1";
		String expectedTaskEstimation= "1";
		String expectedSpecificTime= "2013-07-02";
		String expectedSprintID = "1";
		String expectedTaskNote = "UT for Add New Task for Notes";
		addRequestParameter("Name", expectedTaskName);
		addRequestParameter("Estimate", expectedTaskEstimation);
		addRequestParameter("Notes", expectedTaskNote);
		addRequestParameter("SpecificTime", expectedSpecificTime);
		addRequestParameter("sprintId", expectedSprintID);
		addRequestParameter("issueID", expectedStoryID);

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("<AddNewTask><Result>true</Result><Task><Id>").append(2)	// task = story id + 1
							.append("</Id><Link>/ezScrum/showIssueInformation.do?issueID=").append(2)	// task = story id + 1
							.append("</Link><Name>").append(expectedTaskName)
							.append("</Name><Estimate>").append(expectedTaskEstimation)
							.append("</Estimate><Actual>").append(0)
							.append("</Actual><Notes>").append(expectedTaskNote)
							.append("</Notes></Task></AddNewTask>");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
	
	/**
	 * 測試只填寫名字其他都不填的情況
	 */
	public void testAddSprintTask_2() throws Exception {
		// 在Release中加入1個Sprint
		AddSprintToRelease addSprint = new AddSprintToRelease(1, CR, CP);
		addSprint.exe();
		
		// Sprint加入1個Story
		AddStoryToSprint addStory_Sprint = new AddStoryToSprint(1, 1, 1, CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStory_Sprint.exe();
		
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);	
		// 設定新增Task所需的資訊
		String expectedTaskName = "UT for Add New Task for Name";
		String expectedStoryID = "1";
		String expectedTaskEstimation= "";
		String expectedSpecificTime= "";
		String expectedSprintID = "1";
		String expectedTaskNote = "";
		addRequestParameter("Name", expectedTaskName);
		addRequestParameter("Estimate", expectedTaskEstimation);
		addRequestParameter("Notes", expectedTaskNote);
		addRequestParameter("SpecificTime", expectedSpecificTime);
		addRequestParameter("sprintId", expectedSprintID);
		addRequestParameter("issueID", expectedStoryID);

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("<AddNewTask><Result>true</Result><Task><Id>").append(2)	// task = story id + 1
							.append("</Id><Link>/ezScrum/showIssueInformation.do?issueID=").append(2)	// task = story id + 1
							.append("</Link><Name>").append(expectedTaskName)
							.append("</Name><Estimate>").append(0)
							.append("</Estimate><Actual>").append("0")
							.append("</Actual><Notes>").append(expectedTaskNote)
							.append("</Notes></Task></AddNewTask>");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}