package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxRemoveSprintTaskTest extends MockStrutsTestCase {
	
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private ProjectObject mProject;
	private final String mActionPath = "/ajaxRemoveSprintTask";

	public AjaxRemoveSprintTaskTest(String testName) {
		super(testName);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// create project
		mCP = new CreateProject(1);
		mCP.exeCreate();

		// create sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		mProject = mCP.getAllProjects().get(0);

		super.setUp();
		// ================ set action info ========================
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);

		ini = null;
	}

	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		ini = null;
		projectManager = null;
		mCP = null;
		mConfig = null;
	}

	public void testRemoveSprintTask_1() throws Exception {
		ArrayList<Long> sprintIdList = mCS.getSprintsId();

		int sprintCount = 1;
		int storyCount = 1;
		int storyEst = 2;
		AddStoryToSprint ASS = new AddStoryToSprint(storyCount, storyEst,
				sprintCount, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASS.exe();

		int taskCount = 1;
		int taskEst = 2;
		AddTaskToStory ATS = new AddTaskToStory(taskCount, taskEst, ASS, mCP);
		ATS.exe();

		String issueId = String.valueOf(ATS.getTasksId().get(0));
		String parentId = String.valueOf(ASS.getStories().get(0).getId());

		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", String.valueOf(sprintIdList.get(0)));
		addRequestParameter("issueID", issueId);
		addRequestParameter("parentID", parentId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ 執行 action ==============================
		actionPerform();

		// ================ assert =============================
		verifyNoActionErrors();
		verifyNoActionMessages();

		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText
			.append("<DropTask>")
				.append("<Result>true</Result>")
				.append("<Task>")
					.append("<Id>").append(issueId).append("</Id>")
				.append("</Task>")
			.append("</DropTask>");
				
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
	
	public void testRemoveSprintTask_2() throws Exception {
		ArrayList<Long> sprintIdList = mCS.getSprintsId();
		long sprintId = sprintIdList.get(0);
		int storyCount = 1;
		int storyEst = 2;
		AddStoryToSprint ASS = new AddStoryToSprint(storyCount, storyEst, (int) sprintId, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASS.exe();

		int taskCount = 1;
		int taskEst = 2;
		AddTaskToStory ATS = new AddTaskToStory(taskCount, taskEst, ASS, mCP);
		ATS.exe();

		TaskObject task = ATS.getTasks().get(0);

		long expectedSprintId = sprintIdList.get(0);
		String issueId = String.valueOf(ATS.getTasksId().get(0));
		String expectedStoryId = String.valueOf(ASS.getStories().get(0).getId());

		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", String.valueOf(expectedSprintId));
		addRequestParameter("issueID", issueId);
		addRequestParameter("parentID", expectedStoryId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ 執行 action ==============================
		actionPerform();

		// ================ assert =============================
		verifyNoActionErrors();
		verifyNoActionMessages();

		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText
			.append("<DropTask>")
				.append("<Result>true</Result>")
				.append("<Task>")
					.append("<Id>").append(issueId).append("</Id>")
				.append("</Task>")
			.append("</DropTask>");
				
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
		
		vaildateShowExistedTasks(String.valueOf(expectedSprintId), expectedStoryId, task);
	}
	
	private void vaildateShowExistedTasks(String expectedSprintId, String expectedStoryId, TaskObject task){
		// clear response information and request parameter
		response.reset();
		clearRequestParameters();
		
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);	
		
		addRequestParameter("sprintID", expectedSprintId);
		addRequestParameter("issueID", expectedStoryId);

		// ================ 執行 action ======================
		setRequestPathInfo( "/showAddExistedTask2" );
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText
			.append("<Tasks>")
				.append("<Task>")
					.append("<Id>").append(task.getId()).append("</Id>")
					.append("<Link>/ezScrum/showIssueInformation.do?issueID=").append(task.getId()).append("</Link>")
					.append("<Name>").append(task.getName()).append("</Name>")
					.append("<Status>").append("new").append("</Status>")
					.append("<Estimate>").append(task.getEstimate()).append("</Estimate>")
					.append("<Actual>").append(task.getActual()).append("</Actual>")
					.append("<Handler></Handler>")
					.append("<Partners></Partners>")
					.append("<Notes>").append(task.getNotes()).append("</Notes>")
				.append("</Task>")
			.append("</Tasks>");
		
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
