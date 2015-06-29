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
import ntut.csie.ezScrum.web.dataObject.TagObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import servletunit.struts.MockStrutsTestCase;

public class AjaxAddStoryTagActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateProductBacklog mCPB;
	private Configuration mConfig;
	private ProjectObject mProject;
	private int mStoriesCount = 3;

	public AjaxAddStoryTagActionTest(String testMethod) {
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
		setRequestPathInfo("/AjaxAddStoryTag");
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
	public void testAddStoryTag() {
		// test data
		String newTagName = "TEST_TAG_NAME";
		// create new tag
		TagObject tag = new TagObject(newTagName, mProject.getId());
		tag.save();
		// Story
		StoryObject story = mCPB.getStories().get(0);
		
		// ================== set parameter info ====================
		addRequestParameter("storyId", String.valueOf(story.getId()));
		addRequestParameter("tagId", String.valueOf(tag.getId()));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// ================ set session info ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + mProject.getName());
		
		actionPerform(); // 執行 action
		verifyNoActionErrors();
		
		// assert
		assertEquals(1, story.getTags().size());
		assertEquals(tag.getId(), story.getTags().get(0).getId());
	}

	@Test
	public void testAddStoryTagWithNotExistTag() {
		// Story
		StoryObject story = mCPB.getStories().get(0);

		// ================== set parameter info ====================
		addRequestParameter("storyId", String.valueOf(story.getId()));
		addRequestParameter("tagId", "2");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// ================ set session info ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + mProject.getName());

		// 執行 action
		actionPerform();
		// 驗證回傳
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// assert
		assertEquals(0, story.getTags().size());
	}
	
}
