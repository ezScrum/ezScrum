package ntut.csie.ezScrum.web.logic;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SprintPlanLogicTest {
	private SprintPlanLogic mSprintPlanLogic = null;
	private Configuration mConfig = null;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;

	@Before
	public void setUp() throws Exception {
		// initialize database
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// create test data
		int PROJECT_COUNT = 1;
		int SPRINT_COUNT = 3;
		int STORY_COUNT = 3;
		int STORY_ESTIMATE = 5;
		String STORY_COLUMNBESET = "EST";

		mCP = new CreateProject(PROJECT_COUNT);
		mCP.exeCreate();

		mCS = new CreateSprint(SPRINT_COUNT, mCP);
		mCS.exe();

		mASTS = new AddStoryToSprint(STORY_COUNT, STORY_ESTIMATE, mCS, mCP, STORY_COLUMNBESET);
		mASTS.exe();

		ProjectObject project = mCP.getAllProjects().get(0);
		
		// create SprintPlanLogic 
		mSprintPlanLogic = new SprintPlanLogic(project);
	}

	@After
	public void tearDown() {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		mSprintPlanLogic = null;
		mConfig = null;
		ini = null;
		mCP = null;
		mCS = null;
		mASTS = null;
	}
	
	@Test
	public void testGetSprintsSortedById() {
		// GetSprintsSortById
		ArrayList<SprintObject> sprints = mSprintPlanLogic.getSprintsSortedById();
		// assert
		assertEquals(3, sprints.get(0).getId());
		assertEquals(2, sprints.get(1).getId());
		assertEquals(1, sprints.get(2).getId());
	}
	
	@Test
	public void testGetSprintsSortedByStartDate() {
		// Get Sprints
		SprintObject sprint1 = SprintObject.get(mCS.getSprintsId().get(0));
		SprintObject sprint2 = SprintObject.get(mCS.getSprintsId().get(1));
		SprintObject sprint3 = SprintObject.get(mCS.getSprintsId().get(2));
		// Update StartDate
		sprint1.setStartDate("2015/06/22")
		       .setDemoDate("2015/07/06")
		       .save();
		sprint2.setStartDate("2015/07/07")
		       .setDemoDate("2015/07/21")
		       .save();
		sprint3.setStartDate("2015/07/22")
		       .setDemoDate("2015/08/05")
		       .save();
		// GetSprintsSortByStartDate
		ArrayList<SprintObject> sprints = mSprintPlanLogic.getSprintsSortedByStartDate();
		// assert
		assertEquals(sprint3.getId(), sprints.get(0).getId());
		assertEquals(sprint2.getId(), sprints.get(1).getId());
		assertEquals(sprint1.getId(), sprints.get(2).getId());
	}
}
