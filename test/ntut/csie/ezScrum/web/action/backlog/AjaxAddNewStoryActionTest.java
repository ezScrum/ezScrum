package ntut.csie.ezScrum.web.action.backlog;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.test.CreateData.AddSprintToRelease;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxAddNewStoryActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateRelease CR;
	private Configuration configuration;
	
	public AjaxAddNewStoryActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案

		this.CR = new CreateRelease(1, this.CP);
		this.CR.exe(); // 新增一筆Release Plan

		super.setUp();

		setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent")); // 設定讀取的struts-config檔案路徑
		
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/ajaxAddNewStory");

		// ============= release ==============
		ini = null;
	}
	
	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		CopyProject copyProject = new CopyProject(this.CP);
		copyProject.exeDelete_Project(); // 刪除測試檔案
		
		configuration.setTestMode(false);
		configuration.save();

		// ============= release ==============
		ini = null;
		copyProject = null;
		this.CP = null;
		this.CR = null;
		configuration = null;
		
		super.tearDown();
	}
	
	public void testexecute() throws Exception
	{
		IProject project = this.CP.getProjectList().get(0);
		
		//在Release中加入一個Sprint
		AddSprintToRelease addSprint = new AddSprintToRelease(1,CR,CP);
		addSprint.exe();
		
		//設定參數
		
		addRequestParameter("Name", "Test Add New Story Name");
		addRequestParameter("Description", "Test Add New Story Description");
		addRequestParameter("Importance", "100");
		addRequestParameter("Estimation", "1");
		addRequestParameter("HowToDemo", "Test Add New Story HowToDemo");
		addRequestParameter("Notes", "Test Add New Story Notes");
		addRequestParameter("sprintId","1");
		addRequestParameter("TagIDs","");
		
		
		//設定Session資訊
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		
		actionPerform(); // 執行 action
		verifyNoActionErrors();
		
		/*-----------------------------------------------------------
		*	驗證Story是否有被加入Sprint 1
		-------------------------------------------------------------*/
//		ProductBacklogHelper helper = new ProductBacklogHelper(project, config.getUserSession());
//		IStory[] stories = helper.getStories();
		IStory[] stories = (new ProductBacklogLogic(configuration.getUserSession(), project)).getStories();
		
		assertEquals(1, stories.length);
		
		IStory story = stories[0];
		
		assertEquals("1", story.getSprintID());
		
		/*-----------------------------------------------------------
		*	驗證Story是否有被加入Release 1
		-------------------------------------------------------------*/
		
		assertEquals("1", story.getReleaseID());
		
	}
}
