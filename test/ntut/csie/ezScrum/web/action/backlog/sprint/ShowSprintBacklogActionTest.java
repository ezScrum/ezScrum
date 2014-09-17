package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class ShowSprintBacklogActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private Configuration configuration;
	private final String ACTION_PATH = "/showSprintBacklog2";
	private IProject project;
	
	public ShowSprintBacklogActionTest(String testName) {
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
		this.project = this.CP.getProjectList().get(0);
		
		this.CS = new CreateSprint(2, this.CP);
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
		configuration = null;
	}
	
	/**
	 * 沒有Sprint
	 */
	public void testShowSprintBacklog_1(){
		List<String> idList = this.CS.getSprintIDList();
		
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", idList.get(0));
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		String expectedSprintId= idList.get(0);
		String expectedSprintGoal = "TEST_SPRINTGOAL_1";
		String expectedSprintHoursToCommit = "10.0";
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"success\":true,\"Total\":0,")
							.append("\"Sprint\":{")
							.append("\"Id\":").append(expectedSprintId).append(",")
							.append("\"Name\":\"Sprint #").append(expectedSprintId).append("\",")
							.append("\"CurrentPoint\":\"0.0\",")
							.append("\"LimitedPoint\":\"").append(expectedSprintHoursToCommit).append("\",")
							.append("\"TaskPoint\":\"0.0\",")
							.append("\"ReleaseID\":\"Release #None\",")
							.append("\"SprintGoal\":\"").append(expectedSprintGoal).append("\"},")
							.append("\"Stories\":[]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
	
	/**
	 * 存在兩個Sprint
	 * @throws Exception 
	 */
	public void testShowSprintBacklog_2() throws Exception{
		List<String> idList = this.CS.getSprintIDList();
		int sprintID = Integer.parseInt(idList.get(0));
		int storyCount = 1;
		int storyEst = 5;
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(storyCount, storyEst, sprintID, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStoryToSprint.exe();
		
		int taskCount = 1;
		int taskEstValue = 2;
		AddTaskToStory addTaskToStory = new AddTaskToStory(taskCount, taskEstValue, addStoryToSprint, this.CP);
		addTaskToStory.exe();
		
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", idList.get(0));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		String expectedStoryName = addStoryToSprint.getIssueList().get(0).getSummary();
		String expectedStoryImportance = addStoryToSprint.getIssueList().get(0).getImportance();
		String expectedStoryEstimation = String.valueOf(storyEst);
		String expectedStoryValue = addStoryToSprint.getIssueList().get(0).getValue();
		String expectedStoryHoewToDemo = addStoryToSprint.getIssueList().get(0).getHowToDemo();
		String expectedStoryNote = addStoryToSprint.getIssueList().get(0).getNotes();
		String expectedSprintId= idList.get(0);
		String expectedSprintGoal = "TEST_SPRINTGOAL_1";
		String expectedSprintHoursToCommit = "10.0";
		String issueID = String.valueOf(addStoryToSprint.getIssueList().get(0).getIssueID());
		
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"success\":true,")
							.append("\"Total\":1,")
							.append("\"Sprint\":{")
							.append("\"Id\":").append(expectedSprintId).append(",")
							.append("\"Name\":\"Sprint #").append(expectedSprintId).append("\",")
							.append("\"CurrentPoint\":\"").append(expectedStoryEstimation).append(".0\",")
							.append("\"LimitedPoint\":\"").append(expectedSprintHoursToCommit).append("\",")
							.append("\"TaskPoint\":\"").append(taskEstValue).append(".0\",")
							.append("\"ReleaseID\":\"Release #None\",")
							.append("\"SprintGoal\":\"").append(expectedSprintGoal).append("\"},")
							.append("\"Stories\":[{")
							.append("\"Id\":").append(issueID).append(",")
							.append("\"Link\":\"/ezScrum/showIssueInformation.do?issueID=").append(issueID).append("\",")
							.append("\"Name\":\"").append(expectedStoryName).append("\",")
							.append("\"Value\":\"").append(expectedStoryValue).append("\",")
							.append("\"Importance\":\"").append(expectedStoryImportance).append("\",")			
							.append("\"Estimate\":\"").append(expectedStoryEstimation).append("\",")
							.append("\"Status\":\"new\",")
							.append("\"Notes\":\"").append(expectedStoryNote).append("\",")
							.append("\"Tag\":\"\",")
							.append("\"HowToDemo\":\"").append(expectedStoryHoewToDemo).append("\",")
							.append("\"Release\":\"None\",")
							.append("\"Sprint\":\"").append(expectedSprintId).append("\",")
							.append("\"Attach\":\"false\",")
							.append("\"AttachFileList\":[]")
							.append("}]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
