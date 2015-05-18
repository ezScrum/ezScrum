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

public class AjaxDeleteStoryActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateProductBacklog mCPB;
	private Configuration mConfig;
	private ProjectObject mProject;
	private int mStoriesCount = 3;

	public AjaxDeleteStoryActionTest(String testMethod) {
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
		setRequestPathInfo("/ajaxDeleteStory");
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
	public void testDeleteStory() {
		// Story
		StoryObject story = mCPB.getStories().get(0);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(story.getId()));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		// 驗證回傳
		verifyNoActionErrors();
		
		// assert
		assertEquals((mStoriesCount - 1), mProject.getStories().size());
	}
	
	@Test
	public void testDeleteStoryWithInvalidStoryId() {
		// ================== set parameter info ====================
		addRequestParameter("issueID", "C");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		// 驗證回傳
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();
		
		// assert
		assertEquals(mStoriesCount, mProject.getStories().size());
	}
	
	@Test
	public void testDeleteStoryWithNotExistStory() {
		// ================== set parameter info ====================
		addRequestParameter("issueID", "100");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		// 驗證回傳
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();
		
		// assert
		assertEquals(mStoriesCount, mProject.getStories().size());
	}
}
