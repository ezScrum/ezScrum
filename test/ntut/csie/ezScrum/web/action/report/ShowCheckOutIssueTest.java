package ntut.csie.ezScrum.web.action.report;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class ShowCheckOutIssueTest extends MockStrutsTestCase {
	
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
	private Configuration mConfig;
	private final String ACTION_PATH = "/showCheckOutIssue";

	public ShowCheckOutIssueTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增一測試專案
		mCP = new CreateProject(1);
		mCP.exeCreate();

		// 新增1個Sprint到專案內
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// 新增1筆Story到Sprint內
		mASTS = new AddStoryToSprint(1, 1, mCS, mCP,
				CreateProductBacklog.COLUMN_TYPE_EST);
		mASTS.exe();

		// 新增1筆Task到Story內
		mATTS = new AddTaskToStory(1, 1, mASTS, mCP);
		mATTS.exe();

		super.setUp();
		// ================ set action info ========================
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(ACTION_PATH);

		ini = null;
	}

	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mASTS = null;
		mATTS = null;
		mConfig = null;
	}

	// 測試Issue為Task的CheckOut
	public void testShowCheckOutIssue_Task() throws Exception {
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		long taskId = mATTS.getTasksId().get(0);
		TaskObject task = TaskObject.get(taskId);

		// ================ set request info ========================
		String projectName = project.getName();
		// SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session
		request.setHeader("Referer", "?PID=" + projectName);
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);

		addRequestParameter("issueID", String.valueOf(taskId));
		addRequestParameter("issueType", "Task");

		// ================ 執行 action ==============================
		actionPerform();

		// ================ assert =============================
		verifyNoActionErrors();
		verifyNoActionMessages();

		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("{\"Task\":{")
							.append("\"Id\":\"").append(task.getId()).append("\",")
							.append("\"Name\":\"").append(task.getName()).append("\",")
							.append("\"Partners\":\"").append("\",")
							.append("\"Notes\":\"").append(task.getNotes()).append("\",")
							.append("\"Handler\":\"").append("admin").append("\",")
							.append("\"IssueType\":\"").append("Task").append("\"")
							.append("},\"success\":true,\"Total\":1}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseTest.toString(), actualResponseText);
	}

	// 測試Issue為Story的CheckOut
	public void testShowCheckOutIssue_Story() throws Exception {
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long storyId = mASTS.getStories().get(0).getId();
		ProductBacklogMapper productBacklogMapper = new ProductBacklogMapper(project);
		StoryObject story = productBacklogMapper.getStory(storyId);

		// ================ set request info ========================
		String projectName = project.getName();
		request.setHeader("Referer", "?PID=" + projectName);// SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);

		addRequestParameter("issueID", String.valueOf(storyId));
		addRequestParameter("issueType", "Story");

		// ================ 執行 action ==============================
		actionPerform();

		// ================ assert =============================
		verifyNoActionErrors();
		verifyNoActionMessages();

		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("{\"Story\":{")
							.append("\"Id\":\"").append(story.getId()).append("\",")
							.append("\"Name\":\"").append(story.getName()).append("\",")
							.append("\"Partners\":\"").append("\",")
							.append("\"Notes\":\"").append(story.getNotes()).append("\",")
							.append("\"Handler\":\"").append("\",")
							.append("\"IssueType\":\"").append("Story").append("\",")
							.append("},\"success\":true,\"Total\":1}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseTest.toString(), actualResponseText);
	}
}
