package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AddExistedStoryActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private final String ACTION_PATH = "/addExistedStory";
	private IProject project;
	
	public AddExistedStoryActionTest(String testName) {
		super(testName);
	}
	
	protected void setUp() throws Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案
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

	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(this.config.getTestDataPath());

		super.tearDown();
		
		ini = null;
		projectManager = null;
		this.CP = null;
	}
	
	/**
	 * no story
	 */
	public void testAddExistedStory_1(){
		String sprintID = this.CS.getSprintIDList().get(0);
		String releaseID = "-1";
		String[] selects = {};
		
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("selects", selects);
		addRequestParameter("sprintID", sprintID);
		addRequestParameter("releaseID", releaseID);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		String expectedResponseText = "";
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
	}
	
	/**
	 * two stories
	 */
	public void testAddExistedStory_2(){
		int storycount = 2;
		CreateProductBacklog CPB = new CreateProductBacklog(storycount, this.CP);
		CPB.exe();
		
		String sprintID = this.CS.getSprintIDList().get(0);
		String releaseID = "-1";
		String[] selects = {"1","2"};
		
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("selects", selects);
		addRequestParameter("sprintID", sprintID);
		addRequestParameter("releaseID", releaseID);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		String actualResponseText = response.getWriterBuffer().toString();
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("");
		assertEquals(expectedResponseText.toString(), actualResponseText);
		
		
		//	驗證是否確實有被加入sprint中
		String showSprintBacklog_ActionPath = "/showSprintBacklog2";
		setRequestPathInfo( showSprintBacklog_ActionPath );
		
		clearRequestParameters();
		this.response.reset();
		
		// ================ set request info ========================
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", sprintID);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		String expectedStoryEstimation= "2";
		String expectedSprintGoal = "TEST_SPRINTGOAL_1";
		String expectedSprintHoursToCommit = "10.0";
		for (int i = 0; i < this.CS.getSprintCount() - 1; i++) {
			expectedResponseText.append("{\"success\":true,\"Total\":2,")
								.append("\"Sprint\":{")
								.append("\"Id\":").append(sprintID).append(",")
								.append("\"Name\":\"Sprint #").append(sprintID).append("\",")
								.append("\"CurrentPoint\":\"").append(Integer.parseInt(expectedStoryEstimation)*2).append(".0\",")
								.append("\"LimitedPoint\":\"").append(expectedSprintHoursToCommit).append("\",")
								.append("\"TaskPoint\":\"0.0\",")
								.append("\"ReleaseID\":\"Release #None\",")
								.append("\"SprintGoal\":\"").append(expectedSprintGoal).append("\"},")
								.append("\"Stories\":[");
			
			for (IIssue issue : CPB.getIssueList()) {
				expectedResponseText.append("{\"Id\":").append(issue.getIssueID()).append(",")
									.append("\"Link\":\"/ezScrum/showIssueInformation.do?issueID=").append(issue.getIssueID()).append("\",")
									.append("\"Name\":\"").append(issue.getSummary()).append("\",")							
									.append("\"Value\":\"").append(issue.getValue()).append("\",")
									.append("\"Importance\":\"").append(issue.getImportance()).append("\",")
									.append("\"Estimate\":\"").append(issue.getEstimated()).append("\",")
									.append("\"Status\":\"new\",")
									.append("\"Notes\":\"").append(issue.getNotes()).append("\",")
									.append("\"Tag\":\"\",")
									.append("\"HowToDemo\":\"").append(issue.getHowToDemo()).append("\",")
									.append("\"Release\":\"None\",")
									.append("\"Sprint\":\"").append(sprintID).append("\",")
									.append("\"Attach\":\"false\",")
									.append("\"AttachFileList\":[]},");
			}
			expectedResponseText.deleteCharAt(expectedResponseText.length() - 1);
			expectedResponseText.append("]}");
		}
		actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}

}
