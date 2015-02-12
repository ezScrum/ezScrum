package ntut.csie.ezScrum.web.action.report;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.SprintInfoUIObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

import com.google.gson.Gson;

public class GetSprintInfoForTaskBoardTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private IProject mProject;
	private Gson mGson;
	private Configuration mConfig;

	public GetSprintInfoForTaskBoardTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		// 新增一測試專案
		mCP = new CreateProject(1);
		mCP.exeCreate();
		mProject = mCP.getProjectList().get(0);

		// 新增1筆Sprint Plan
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		mGson = new Gson();
		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));	// 設定讀取的struts-config檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/GetSprintInfoForTaskBoard");

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); 	// 初始化 SQL

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mGson = null;
		mConfig = null;
		super.tearDown();
	}

	/**
	 * 完全沒有動story與task的sprint info
	 */
	public void testGetSprintInfoForTaskBoard_1() throws Exception {
		final int STORY_COUNT = 2, TASK_COUNT = 2, STORY_EST = 5, TASK_EST = 5;
		// Sprint加入2個Story
		AddStoryToSprint ASTS = new AddStoryToSprint(STORY_COUNT, STORY_EST, mCS, mCP, CreateProductBacklog.TYPE_ESTIMATION);
		ASTS.exe();

		// 每個Story加入2個task
		AddTaskToStory ATTS = new AddTaskToStory(TASK_COUNT, TASK_EST, ASTS, mCP);
		ATTS.exe();

		// ================ set request info ========================
		request.setHeader("Referer", "?PID=" + mProject.getName());
		addRequestParameter("sprintID", mCS.getSprintIDList().get(0));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		String result = response.getWriterBuffer().toString();
		SprintInfoUIObject sprintInfo = mGson.fromJson(result, SprintInfoUIObject.class);
		Double storyPoint = sprintInfo.CurrentStoryPoint;
		Double taskPoint = sprintInfo.CurrentTaskPoint;
		assertEquals(mCS.getSprintIDList().get(0), String.valueOf(sprintInfo.ID));
		assertEquals(mCS.TEST_SPRINT_GOAL + mCS.getSprintIDList().get(0), sprintInfo.SprintGoal);
		assertEquals(STORY_COUNT * STORY_EST, storyPoint.intValue());
		assertEquals(STORY_COUNT * TASK_COUNT * TASK_EST, taskPoint.intValue());
		assertEquals("Release #0", sprintInfo.ReleaseID);
		assertEquals(true, sprintInfo.isCurrentSprint);
	}

	/**
	 * 一個story完成與兩個task完成的sprint info
	 * 總共2 story, 4 task
	 */
	public void testGetSprintInfoForTaskBoard_2() throws Exception {
		final int STORY_COUNT = 2, TASK_COUNT = 2, STORY_EST = 5, TASK_EST = 5;
		// Sprint加入2個Story
		AddStoryToSprint ASTS = new AddStoryToSprint(STORY_COUNT, STORY_EST, mCS, mCP, CreateProductBacklog.TYPE_ESTIMATION);
		ASTS.exe();

		// 每個Story加入2個task
		AddTaskToStory ATTS = new AddTaskToStory(TASK_COUNT, TASK_EST, ASTS, mCP);
		ATTS.exe();

		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(mProject, mConfig.getUserSession(), null);
		// 將第1個story跟task全都拉到done
		sprintBacklogLogic.closeTask(ATTS.getTasks().get(0).getId(), ATTS.getTasks().get(0).getName(), "", 0, "");
		sprintBacklogLogic.closeTask(ATTS.getTasks().get(1).getId(), ATTS.getTasks().get(1).getName(), "", 0, "");
		sprintBacklogLogic.closeStory(ASTS.getStories().get(0).getIssueID(), "", "");

		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", "1");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		String result = response.getWriterBuffer().toString();
		SprintInfoUIObject sprintInfo = mGson.fromJson(result, SprintInfoUIObject.class);
		Double storyPoint = sprintInfo.CurrentStoryPoint;
		Double taskPoint = sprintInfo.CurrentTaskPoint;
		assertEquals(mCS.getSprintIDList().get(0), String.valueOf(sprintInfo.ID));
		assertEquals(mCS.TEST_SPRINT_GOAL + mCS.getSprintIDList().get(0), sprintInfo.SprintGoal);
		assertEquals((STORY_COUNT - 1) * STORY_EST, storyPoint.intValue());				// done 1個story
		assertEquals((STORY_COUNT * TASK_COUNT - 2) * TASK_EST, taskPoint.intValue());	// done 2個task
		assertEquals("Release #0", sprintInfo.ReleaseID);
		assertEquals(true, sprintInfo.isCurrentSprint);
	}
}
