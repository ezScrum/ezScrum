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
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import servletunit.struts.MockStrutsTestCase;

public class ShowSprintBacklogActionTest extends MockStrutsTestCase {
	
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private final String mACTION_PATH = "/showSprintBacklog2";
	private ProjectObject mProject;
	
	public ShowSprintBacklogActionTest(String testName) {
		super(testName);
	}
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mCP = new CreateProject(1);
		mCP.exeCreate(); // 新增一測試專案
		mProject = mCP.getAllProjects().get(0);
		
		mCS = new CreateSprint(2, mCP);
		mCS.exe();
		
		super.setUp();
		
		// ================ set action info ========================
		setContextDirectory( new File(mConfig.getBaseDirPath()+ "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo( mACTION_PATH );
		
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		// 讓 config 回到  Production 模式
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
	 * 沒有Sprint
	 */
	public void testShowSprintBacklog_1(){
		List<String> idList = mCS.getSprintIDList();
		
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", idList.get(0));
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
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
		List<String> idList = mCS.getSprintIDList();
		int sprintID = Integer.parseInt(idList.get(0));
		int storyCount = 1;
		int storyEst = 5;
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(storyCount, storyEst, sprintID, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		addStoryToSprint.exe();
		
		int taskCount = 1;
		int taskEstValue = 2;
		AddTaskToStory addTaskToStory = new AddTaskToStory(taskCount, taskEstValue, addStoryToSprint, mCP);
		addTaskToStory.exe();
		
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", idList.get(0));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		String expectedStoryName = addStoryToSprint.getStories().get(0).getName();
		int expectedStoryImportance = addStoryToSprint.getStories().get(0).getImportance();
		int expectedStoryEstimate = storyEst;
		int expectedStoryValue = addStoryToSprint.getStories().get(0).getValue();
		String expectedStoryHoewToDemo = addStoryToSprint.getStories().get(0).getHowToDemo();
		String expectedStoryNote = addStoryToSprint.getStories().get(0).getNotes();
		String expectedSprintId= idList.get(0);
		String expectedSprintGoal = "TEST_SPRINTGOAL_1";
		String expectedSprintHoursToCommit = "10.0";
		long storyId = addStoryToSprint.getStories().get(0).getId();
		
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"success\":true,")
							.append("\"Total\":1,")
							.append("\"Sprint\":{")
							.append("\"Id\":").append(expectedSprintId).append(",")
							.append("\"Name\":\"Sprint #").append(expectedSprintId).append("\",")
							.append("\"CurrentPoint\":\"").append(expectedStoryEstimate).append(".0\",")
							.append("\"LimitedPoint\":\"").append(expectedSprintHoursToCommit).append("\",")
							.append("\"TaskPoint\":\"").append(taskEstValue).append(".0\",")
							.append("\"ReleaseID\":\"Release #None\",")
							.append("\"SprintGoal\":\"").append(expectedSprintGoal).append("\"},")
							.append("\"Stories\":[{")
							.append("\"Id\":").append(storyId).append(",")
							.append("\"Link\":\"/ezScrum/showIssueInformation.do?issueID=").append(storyId).append("\",")
							.append("\"Name\":\"").append(expectedStoryName).append("\",")
							.append("\"Value\":\"").append(expectedStoryValue).append("\",")
							.append("\"Importance\":\"").append(expectedStoryImportance).append("\",")			
							.append("\"Estimate\":\"").append(expectedStoryEstimate).append("\",")
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
