package ntut.csie.ezScrum.web.logic;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProductBacklogLogicTest {
	private ProductBacklogLogic mProductBacklogLogic = null;
	private Configuration mConfig = null;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
	
    @Before
	public void setUp() throws Exception {
		// initialize database
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();// 初始化 SQL

		// create test data
		int PROJECT_COUNT = 1;
		int SPRINT_COUNT = 1;
		int STORY_COUNT = 3;
		int STORY_ESTIMATE = 5;
		int TASK_COUNT = 3;
		int TASK_ESTIMATE = 8;
		String CREATE_PRODUCTBACKLOG_TYPE = "EST";

		mCP = new CreateProject(PROJECT_COUNT);
		mCP.exeCreate();

		mCS = new CreateSprint(SPRINT_COUNT, mCP);
		mCS.exe();

		mASTS = new AddStoryToSprint(STORY_COUNT, STORY_ESTIMATE, mCS, mCP, CREATE_PRODUCTBACKLOG_TYPE);
		mASTS.exe();

		mATTS = new AddTaskToStory(TASK_COUNT, TASK_ESTIMATE, mASTS, mCP);
		mATTS.exe();

		IProject project = mCP.getProjectList().get(0);
		
		mProductBacklogLogic = new ProductBacklogLogic(project);
	}
	
	@After
	public void tearDown() {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
		
		mProductBacklogLogic = null;
		mConfig = null;
		ini = null;
		mCP = null;
		mCS = null;
		mASTS = null;
		mATTS = null;
	}
	
	@Test
	public void testAddStoriesToSprint() {
		
	}
	
	@Test
	public void testGetAddableStories() {
		
	}
	
	@Test
	public void testGetAddableStories_WithSprintId() {
		
	}
}
