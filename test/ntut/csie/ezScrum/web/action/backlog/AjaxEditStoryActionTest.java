package ntut.csie.ezScrum.web.action.backlog;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import servletunit.struts.MockStrutsTestCase;

public class AjaxEditStoryActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateProductBacklog mCPB;
	private Configuration mConfig;
	private ProjectObject mProject;
	private int mStoriesCount = 3;

	public AjaxEditStoryActionTest(String testMethod) {
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
		mCPB = new CreateProductBacklog(mStoriesCount, mCP);
		mCPB.exe();

		// get project
		mProject = mCP.getAllProjects().get(0);

		super.setUp();

		// 設定讀取的 struts-config 檔案路徑
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/ajaxEditStory");
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
		mCPB = null;
		mConfig = null;
		projectManager = null;

		super.tearDown();
	}
	
	@Test
	public void testEditStory(){
		// test data
		String storyName = "TEST_STORY_NEW";
		int importance = 13;
		int estimate = 13;
		int value = 13;
		String howToDemo = "TEST_STORY_HOWTODEMO_NEW";
		String notes = "TEST_STORY_NOTES_NEW";

		// Story
		StoryObject story = mCPB.getStories().get(0);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(story.getId()));
		addRequestParameter("Name", storyName);
		addRequestParameter("Importance", String.valueOf(importance));
		addRequestParameter("Estimate", String.valueOf(estimate));
		addRequestParameter("Value", String.valueOf(value));
		addRequestParameter("HowToDemo", howToDemo);
		addRequestParameter("Notes", notes);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// ================ set session info ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + mProject.getName());

		// 執行 action
		actionPerform();
		// 驗證回傳
		verifyNoActionErrors();

		// reload story
		story.reload();

		// assert
		assertEquals(storyName, story.getName());
		assertEquals(importance, story.getImportance());
		assertEquals(estimate, story.getEstimate());
		assertEquals(value, story.getValue());
		assertEquals(howToDemo, story.getHowToDemo());
		assertEquals(notes, story.getNotes());
	}
}
