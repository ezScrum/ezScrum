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

public class GetEditTaskInfoActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private final String ACTION_PATH = "/getEditTaskInfo";
	private IProject mProject;
	
	public GetEditTaskInfoActionTest(String testName) {
		super(testName);
	}
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		this.mCP = new CreateProject(1);
		this.mCP.exeCreate();// 新增一測試專案
		this.mProject = this.mCP.getProjectList().get(0);
		
		this.mCS = new CreateSprint(2, this.mCP);
		this.mCS.exe();
		
		super.setUp();
		// ================ set action info ========================
		setContextDirectory( new File(mConfig.getBaseDirPath()+ "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo( this.ACTION_PATH );
		
		ini = null;
	}
	
	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(mConfig.getDataPath());
		
		mConfig.setTestMode(false);
		mConfig.save();
		
		super.tearDown();
		
		ini = null;
		projectManager = null;
		this.mCP = null;
		this.mCS = null;
		mConfig = null;
	}
	
	public void testGetEditTaskInfo() throws Exception {
		List<String> sprintIds = this.mCS.getSprintIDList();
		int sprintID = Integer.parseInt(sprintIds.get(0));
		int storyCount = 1;
		int storyEst = 2;
		AddStoryToSprint ASTS = new AddStoryToSprint(storyCount, storyEst, sprintID, this.mCP, CreateProductBacklog.TYPE_ESTIMATION);
		ASTS.exe();
		
		int taskCount = 1;
		int taskEst = 2;
		AddTaskToStory ATTS = new AddTaskToStory(taskCount, taskEst, ASTS, this.mCP);
		ATTS.exe();
		
		CreateProductBacklog CPB = new CreateProductBacklog(storyCount, this.mCP);
		CPB.exe();
		String taskId = String.valueOf(ATTS.getTasksId().get(0));
		// ================ set request info ========================
		String projectName = this.mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", sprintIds.get(0));
		addRequestParameter("issueID", taskId);
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		// ================  執行 action ==============================
		actionPerform();
		// ================    assert    =============================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		String expectedTaskName = ATTS.getTasks().get(0).getName();
		String expectedTaskEstimation= ATTS.getTasks().get(0).getEstimate() + "";
		String expectedTaskActualHour = ATTS.getTasks().get(0).getActual() + "";
		String expectedTaskRemains = ATTS.getTasks().get(0).getRemains() + "";
		String expectedTaskNote = ATTS.getTasks().get(0).getNotes();
		
		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("<EditTask><Task><Id>" + taskId + "</Id>");
		expectedResponseTest.append("<Name>" + expectedTaskName	+ "</Name>");
		expectedResponseTest.append("<Estimate>" + expectedTaskEstimation + "</Estimate>");
		expectedResponseTest.append("<Actual>" + expectedTaskActualHour + "</Actual><Handler></Handler>");
		expectedResponseTest.append("<Remains>" + expectedTaskRemains + "</Remains><Partners></Partners>");
		expectedResponseTest.append("<Notes>" + expectedTaskNote + "</Notes></Task></EditTask>");
		
		String actualResponseText = response.getWriterBuffer().toString();
		System.out.println("result = " + actualResponseText);
		assertEquals(expectedResponseTest.toString(), actualResponseText);
		
	}
}
