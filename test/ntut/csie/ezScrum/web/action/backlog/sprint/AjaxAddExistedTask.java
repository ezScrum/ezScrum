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
import ntut.csie.ezScrum.test.CreateData.DropTask;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import servletunit.struts.MockStrutsTestCase;

public class AjaxAddExistedTask extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private ProjectObject mProject;
	private final String mActionPath = "/addExistedTask";

	public AjaxAddExistedTask(String testName) {
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
		mCS = new CreateSprint(1, mCP);
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
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		ini = null;
		projectManager = null;
		mCP = null;
		mCS = null;
		mConfig = null;
		mProject = null;
	}

	/**
	 * 測試有一個 Droped Task 的情況
	 */
	public void testAddExistedTask() throws Exception {
		long sprintId = Long.valueOf(mCS.getSprintsId().get(0));
		AddStoryToSprint ASS = new AddStoryToSprint(1, 1, (int) sprintId, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASS.exe();

		long storyId = ASS.getStories().get(0).getId();
		AddTaskToStory ATS = new AddTaskToStory(1, 1, ASS, mCP);
		ATS.exe();

		int taskId = ATS.getTasksId().get(0).intValue();
		DropTask dropTask = new DropTask(mCP, sprintId, storyId, taskId);
		dropTask.exe();

		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);

		// 設定 Session 資訊
		request.getSession().setAttribute("UserSession",mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// 設定新增 Task 所需的資訊
		String expectedStoryId = String.valueOf(storyId);
		String expectedSprintId = String.valueOf(sprintId);
		String expectedTaskId = String.valueOf(taskId);

		addRequestParameter("sprintID", expectedSprintId);
		addRequestParameter("issueID", expectedStoryId); // story
		addRequestParameter("selected", expectedTaskId); // task

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();

		SprintBacklogMapper sprintBacklogMapper = new SprintBacklogMapper(mProject, sprintId);
		ArrayList<TaskObject> tasks = sprintBacklogMapper.getTasksByStoryId(storyId);
		assertEquals(expectedTaskId, String.valueOf(tasks.get(0).getId()));
	}
}
