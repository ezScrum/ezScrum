package ntut.csie.ezScrum.web.action.backlog;

import java.io.File;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import servletunit.struts.MockStrutsTestCase;

public class AddExistedTaskActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private Configuration mConfig;
	private ProjectObject mProject;
	private int mStoriesCount = 3;
	private int mTasksCount = 3;

	public AddExistedTaskActionTest(String testMethod) {
		super(testMethod);
	}

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// create project
		mCP = new CreateProject(1);
		mCP.exeCreate();

		// create sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// create story
		mASTS = new AddStoryToSprint(mStoriesCount, 5, mCS, mCP, "EST");
		mASTS.exe();
		
		mProject = mCP.getAllProjects().get(0);

		super.setUp();

		// 設定讀取的 struts-config 檔案路徑
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/addExistedTask");

		// ============= release ==============
		ini = null;
	}

	@After
	public void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除測試檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mASTS = null;
		mConfig = null;
		projectManager = null;
		super.tearDown();
	}
	
	/**
	 * test add 5 tasks to story#1
	 */
	@Test
	public void testAddExistedTask1() {
		long sprintId = mCS.getSprintsId().get(0);
		long storyId = mASTS.getStories().get(0).getId();
		String[] tasksId = new String[mTasksCount];
		
		// create 3 task
		for (int i = 0; i < mTasksCount; i++) {
			TaskObject task = new TaskObject(mProject.getId());
			task.setName("TestTask_" + (i+1)).save();
			task.reload();
			tasksId[i] = String.valueOf(task.getId());
		}

		// ================== set parameter info ====================
		addRequestParameter("sprintID", String.valueOf(sprintId));
		addRequestParameter("issueID", String.valueOf(storyId));
		addRequestParameter("selected", tasksId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// ================ set session info ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + mProject.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		StoryObject story = StoryObject.get(storyId);
		ArrayList<TaskObject> tasks = story.getTasks();
		assertEquals(mTasksCount, tasks.size());
		for (TaskObject task : tasks) {
			assertEquals(story.getId(), task.getStoryId());
		}
	}
	
	/**
	 * test add not existed task to story#1
	 */
	@Test
	public void testAddExistedTask2() {
		long sprintId = mCS.getSprintsId().get(0);
		long storyId = mASTS.getStories().get(0).getId();
		String[] tasksId = {"10"};
		
		// ================== set parameter info ====================
		addRequestParameter("sprintID", String.valueOf(sprintId));
		addRequestParameter("issueID", String.valueOf(storyId));
		addRequestParameter("selected", tasksId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// ================ set session info ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + mProject.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		StoryObject story = StoryObject.get(storyId);
		assertEquals(0, story.getTasks().size());
	}
	
	/**
	 * test add task to not existed story
	 */
	@Test
	public void testAddExistedTask3() {
		long sprintId = mCS.getSprintsId().get(0);
		String storyId = "+-*/000";
		String[] tasksId = {"10"};
		
		// ================== set parameter info ====================
		addRequestParameter("sprintID", String.valueOf(sprintId));
		addRequestParameter("issueID", storyId);
		addRequestParameter("selected", tasksId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// ================ set session info ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + mProject.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();
		
		String actualText = response.getWriterBuffer().toString();
		String expectText = "Story#-1 is not existed.";
		assertEquals(expectText, actualText);
	}
}
