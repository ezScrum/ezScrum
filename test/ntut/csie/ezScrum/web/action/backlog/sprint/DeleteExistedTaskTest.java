package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.DropTask;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import servletunit.struts.MockStrutsTestCase;

public class DeleteExistedTaskTest extends MockStrutsTestCase {

	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASS;
	private Configuration mConfig;
	private final String mActionPath = "/deleteExistedTask";
	private ProjectObject mProject;
	private long mSprintId;
	private long mStoryId;

	public DeleteExistedTaskTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// create project
		mCP = new CreateProject(1);
		mCP.exeCreate();
		mProject = mCP.getAllProjects().get(0);

		// create sprint
		int sprintCount = 1;
		mCS = new CreateSprint(sprintCount, mCP);
		mCS.exe();
		mSprintId = mCS.getSprintsId().get(0);

		// create story to sprint
		int storyCount = 1;
		mASS = new AddStoryToSprint(storyCount, 1, (int) mSprintId, mCP,
				CreateProductBacklog.COLUMN_TYPE_EST);
		mASS.exe();
		mStoryId = mASS.getStories().get(storyCount - 1).getId();

		super.setUp();

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws Exception {
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mASS = null;
		mConfig = null;
		mProject = null;
		super.tearDown();
	}

	/**
	 * 刪除一個 Task
	 */
	public void testDeleteExistedTask_1() throws Exception {
		int taskCount = 1;
		String[] tasksId = createTasks(taskCount);
		DropTask DT = new DropTask(mCP, mSprintId, mStoryId,
				Long.valueOf(tasksId[0]));
		DT.exe();

		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", String.valueOf(mSprintId));
		addRequestParameter("selected", tasksId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ 執行 action ======================
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		String actualResponseText = response.getWriterBuffer().toString();
		StringBuilder expectedResponseText = new StringBuilder("");
		assertEquals(expectedResponseText.toString(), actualResponseText);

		verifyExistedTasks();
	}

	/**
	 * 刪除兩個 Task
	 */
	public void testDeleteExistedTask_2() throws Exception {
		int taskCount = 2;
		String[] tasksId = createTasks(taskCount);
		DropTask DT1 = new DropTask(mCP, mSprintId, mStoryId,
				Long.valueOf(tasksId[0]));
		DT1.exe();
		DropTask DT2 = new DropTask(mCP, mSprintId, mStoryId,
				Long.valueOf(tasksId[1]));
		DT2.exe();

		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", String.valueOf(mSprintId));
		addRequestParameter("selected", tasksId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ 執行 action ======================
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		String actualResponseText = response.getWriterBuffer().toString();
		StringBuilder expectedResponseText = new StringBuilder("");
		assertEquals(expectedResponseText.toString(), actualResponseText);

		verifyExistedTasks();
	}

	private String[] createTasks(int taskCount) throws Exception {
		// Story 加入1個 Task
		AddTaskToStory ATS = new AddTaskToStory(taskCount, 1, mASS, mCP);
		ATS.exe();
		
		String[] tasksId = new String[ATS.getTasksId().size()];
		for (int i = 0; i < ATS.getTasksId().size(); i++) {
			tasksId[i] = String.valueOf(ATS.getTasksId().get(i));
		}
		return tasksId;
	}

	/**
	 * 驗證 delete tasks 後，該 Tasks 是確實被刪除的。
	 */
	private void verifyExistedTasks() {
		// clear request and response
		clearRequestParameters();
		response.reset();

		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		// 設定Session資訊
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		// 設定新增Task所需的資訊
		String expectedStoryId = "1";
		String expectedSprintId = "1";

		addRequestParameter("sprintID", expectedSprintId);
		addRequestParameter("issueID", expectedStoryId);

		// ================ 執行 action ======================
		String path = "/showAddExistedTask2";
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(path);

		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		StringBuilder expectedResponseText = new StringBuilder(
				"<Tasks></Tasks>");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
