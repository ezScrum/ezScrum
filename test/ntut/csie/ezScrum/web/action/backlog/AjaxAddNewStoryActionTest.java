package ntut.csie.ezScrum.web.action.backlog;

import java.io.File;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddSprintToRelease;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import servletunit.struts.MockStrutsTestCase;

public class AjaxAddNewStoryActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateRelease mCR;
	private Configuration mConfig;
	
	public AjaxAddNewStoryActionTest(String testMethod) {
        super(testMethod);
    }
	
	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		mCP = new CreateProject(1);
		mCP.exeCreate(); // 新增一測試專案

		mCR = new CreateRelease(1, mCP);
		mCR.exe(); // 新增一筆Release Plan

		super.setUp();

		// 設定讀取的struts-config檔案路徑
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/ajaxAddNewStory");

		// ============= release ==============
		ini = null;
	}
	
	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCR = null;
		mConfig = null;
		
		super.tearDown();
	}
	
	@Test
	public void testExecute() throws Exception {
		ProjectObject project = mCP.getAllProjects().get(0);

		// 在 Release 中加入一個 Sprint
		AddSprintToRelease ASTR = new AddSprintToRelease(1, mCR, mCP);
		ASTR.exe();

		// 設定參數
		addRequestParameter("Name", "Test Add New Story Name");
		addRequestParameter("Description", "Test Add New Story Description");
		addRequestParameter("Importance", "100");
		addRequestParameter("Estimate", "1");
		addRequestParameter("Value", "50");
		addRequestParameter("HowToDemo", "Test Add New Story HowToDemo");
		addRequestParameter("Notes", "Test Add New Story Notes");
		addRequestParameter("SprintId", "1");
		addRequestParameter("TagIDs", "");

		// 設定 Session 資訊
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// SessionManager 會對 URL 的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + project.getName());
		
		actionPerform(); // 執行 action
		verifyNoActionErrors();
		
		/*-----------------------------------------------------------
		*	驗證Story是否有被加入Sprint 1
		-------------------------------------------------------------*/
		ArrayList<StoryObject> stories = (new ProductBacklogLogic(project)).getStories();
		assertEquals(1, stories.size());
		StoryObject story = stories.get(0);
		assertEquals(1, story.getSprintId());
	}
}
