package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.DropTask;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxRemoveSprintTaskTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private final String ACTION_PATH = "/ajaxRemoveSprintTask";
	private IProject project;
	
	public AjaxRemoveSprintTaskTest(String testName) {
		super(testName);
	}
	
	protected void setUp() throws Exception {
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		this.CP = new CreateProject(1);
		this.CP.exeCreate();// 新增一測試專案
		this.project = this.CP.getProjectList().get(0);
		
		this.CS = new CreateSprint(2, this.CP);
		this.CS.exe();
		
		super.setUp();
		// ================ set action info ========================
		setContextDirectory( new File(config.getBaseDirPath()+ "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo( this.ACTION_PATH );
		
		ini = null;
	}
	
	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(this.config.getTestDataPath());
		
		super.tearDown();
		
		ini = null;
		projectManager = null;
		this.CP = null;
	}
	
	public void testRemoveSprintTask_1() throws Exception {
		List<String> idList = this.CS.getSprintIDList();
		int sprintID = Integer.parseInt(idList.get(0));
		int storyCount = 1;
		int storyEst = 2;
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(storyCount, storyEst, sprintID, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStoryToSprint.exe();
		
		int taskCount = 1;
		int taskEst = 2;
		AddTaskToStory addTaskToStory = new AddTaskToStory(taskCount, taskEst, addStoryToSprint, this.CP);
		addTaskToStory.exe();
		
		String issueID = String.valueOf(addTaskToStory.getTaskIDList().get(0));		
		String parentID = String.valueOf(addStoryToSprint.getIssueList().get(0).getIssueID());
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", idList.get(0));
		addRequestParameter("issueID", issueID);
		addRequestParameter("parentID", parentID);
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		// ================  執行 action ==============================
		actionPerform();
		// ================    assert    =============================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("<DropTask><Result>true</Result><Task><Id>");
		expectedResponseTest.append(issueID + "</Id></Task></DropTask>");
				
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseTest.toString(), actualResponseText);
	}
	
	public void testRemoveSprintTask_2() throws Exception {
		List<String> idList = this.CS.getSprintIDList();
		int sprintID = Integer.parseInt(idList.get(0));
		int storyCount = 1;
		int storyEst = 2;
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(storyCount, storyEst, sprintID, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStoryToSprint.exe();
		
		int taskCount = 1;
		int taskEst = 2;
		AddTaskToStory addTaskToStory = new AddTaskToStory(taskCount, taskEst, addStoryToSprint, this.CP);
		addTaskToStory.exe();
		IIssue task = addTaskToStory.getTaskList().get(0);
		
		String expectedSprintID = idList.get(0);
		String issueID = String.valueOf(addTaskToStory.getTaskIDList().get(0));		
		String expectedStoryID = String.valueOf(addStoryToSprint.getIssueList().get(0).getIssueID());
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", expectedSprintID);
		addRequestParameter("issueID", issueID);
		addRequestParameter("parentID", expectedStoryID);
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		// ================  執行 action ==============================
		actionPerform();
		// ================    assert    =============================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("<DropTask><Result>true</Result><Task><Id>");
		expectedResponseTest.append(issueID + "</Id></Task></DropTask>");
				
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseTest.toString(), actualResponseText);
		
		this.vaildateShowExistedTasks(expectedSprintID, expectedStoryID, task);
	}
	
	private void vaildateShowExistedTasks(String expectedSprintID, String expectedStoryID, IIssue task){
		// clear response information and request parameter
		this.response.reset();
		clearRequestParameters();
		
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", config.getUserSession());
		request.getSession().setAttribute("Project", project);	
		
		addRequestParameter("sprintID", expectedSprintID);
		addRequestParameter("issueID", expectedStoryID);

		// ================ 執行 action ======================
		setRequestPathInfo( "/showAddExistedTask2" );
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("<Tasks><Task><Id>").append(task.getIssueID()).append("</Id>")
							.append("<Link>/ezScrum/showIssueInformation.do?issueID=").append(task.getIssueID()).append("</Link>")
							.append("<Name>").append(task.getSummary()).append("</Name>")
							.append("<Status>").append("new").append("</Status>")
							.append("<Estimate>").append(task.getEstimated()).append("</Estimate>")
							.append("<Actual>").append(task.getActualHour()).append("</Actual>")
							.append("<Handler></Handler>")
							.append("<Partners></Partners>")
							.append("<Notes>").append(task.getNotes()).append("</Notes>")
							.append("</Task></Tasks>");
		
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
