package ntut.csie.ezScrum.web.action.backlog;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import servletunit.struts.MockStrutsTestCase;

public class AddExistedStoryActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateProductBacklog mCPB;
	private Configuration mConfig;
	private ProjectObject mProject;
	private int mStoriesCount = 5;

	public AddExistedStoryActionTest(String testMethod) {
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
		mCP.exeCreateForDb();

		// create sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// create story
		mCPB = new CreateProductBacklog(mStoriesCount, mCP);
		mCPB.exe();

		mProject = mCP.getAllProjects().get(0);

		super.setUp();

		// 設定讀取的 struts-config 檔案路徑
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/addExistedStory");

		// ============= release ==============
		ini = null;
	}

	@After
	public void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCPB = null;
		mConfig = null;
		super.tearDown();
	}

	/**
	 * test add 5 stories to sprint#1
	 */
	@Test
	public void testAddExistedStory1() {
		long sprintId = mCS.getSprintsId().get(0);
		String[] storiesId = new String[mStoriesCount];

		for (int i = 0; i < mStoriesCount; i++) {
			StoryObject story = mCPB.getStories().get(i);
			storiesId[i] = Long.toString(story.getId());
		}

		// ================== set parameter info ====================
		addRequestParameter("releaseID", "");
		addRequestParameter("sprintID", String.valueOf(sprintId));
		addRequestParameter("selects", storiesId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// ================ set session info ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?projectName=" + mProject.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		for (StoryObject story : mCPB.getStories()) {
			story.reload();
			assertEquals(sprintId, story.getSprintId());
		}
	}

	/**
	 * Add not existed story to sprint#1
	 */
	@Test
	public void testAddExistedStory2() {
		String[] storiesId = {"10"};
		SprintObject sprint = mCS.getSprints().get(0);
		// ================== set parameter info ====================
		addRequestParameter("releaseID", "");
		addRequestParameter("sprintID", String.valueOf(sprint.getId()));
		addRequestParameter("selects", storiesId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// ================ set session info ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?projectName=" + mProject.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		StoryObject story = StoryObject.get(10);
		assertNull(story);
		
		assertEquals(0, sprint.getStories().size());
	 }
	
	/**
	 * Add existed stories to not existed sprint
	 */
	@Test
	public void testAddExistedStory3() {
		long sprintId = 2;
		String[] storiesId = new String[mStoriesCount];

		for (int i = 0; i < mStoriesCount; i++) {
			StoryObject story = mCPB.getStories().get(i);
			storiesId[i] = Long.toString(story.getId());
		}

		// ================== set parameter info ====================
		addRequestParameter("releaseID", "");
		addRequestParameter("sprintID", String.valueOf(sprintId));
		addRequestParameter("selects", storiesId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// ================ set session info ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?projectName=" + mProject.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(mProject, sprintId);
		assertNull(sprintBacklogLogic.getSprintBacklogMapper());
		for (StoryObject story : mCPB.getStories()) {
			assertEquals(-1, story.getSprintId());
		}
	 }
	
	/**
	 * Add existed stories to not existed sprint
	 */
	@Test
	public void testAddExistedStory4() {
		String[] storiesId = new String[mStoriesCount];

		for (int i = 0; i < mStoriesCount; i++) {
			StoryObject story = mCPB.getStories().get(i);
			storiesId[i] = Long.toString(story.getId());
		}

		// ================== set parameter info ====================
		addRequestParameter("releaseID", "");
		addRequestParameter("sprintID", "fsdfholsdhfsl");
		addRequestParameter("selects", storiesId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// ================ set session info ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?projectName=" + mProject.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		for (StoryObject story : mCPB.getStories()) {
			assertEquals(-1, story.getSprintId());
		}
	 }
}
