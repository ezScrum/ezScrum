package ntut.csie.ezScrum.issue.mail.service.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;

public class SprintInfoContentTest {
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private AddStoryToSprint mASTS;

	@Before
	public void setUp() throws Exception {
		// initialize database
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// initialize SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		ini = null;
		// create test data
		int PROJECT_COUNT = 1;
		int SPRINT_COUNT = 1;
		int STORY_COUNT = 3;
		int STORY_ESTIMATE = 5;
		String CREATE_PRODUCTBACKLOG_TYPE = "EST";

		mCP = new CreateProject(PROJECT_COUNT);
		mCP.exeCreateForDb();

		mCS = new CreateSprint(SPRINT_COUNT, mCP);
		mCS.exe();
		
		mASTS = new AddStoryToSprint(STORY_COUNT, STORY_ESTIMATE, mCS, mCP,
				CREATE_PRODUCTBACKLOG_TYPE);
		mASTS.exe();
		
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
		mConfig = null;
	}

	@Test
	public void testGetResult() {
		// TODO
	}
}
