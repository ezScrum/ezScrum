package ntut.csie.ezScrum.issue.mail.service.core;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

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

		mASTS = new AddStoryToSprint(STORY_COUNT, STORY_ESTIMATE, mCS, mCP, CREATE_PRODUCTBACKLOG_TYPE);
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

	@Test
	public void testGetStoryInfo_withImportance() {
		String ans = "	TEST_STORY_3(5)\n	TEST_STORY_2(5)\n	TEST_STORY_1(5)\n";
		ans = ans + "Estimated velocity : 15.0 story points";
		ArrayList<StoryObject> stories = mASTS.getStories();
		int importance = 80;
		for (StoryObject story : stories) {
			story.setImportance(importance);
			story.save();
			importance += 5;
		}

		SprintInfoContent sprintInfoContent = new SprintInfoContent();
		ArrayList<SprintObject> sprints = mCS.getSprints();
		SprintObject sprint = sprints.get(0);
		ArrayList<ProjectObject> projects = mCP.getAllProjects();
		String storiesInfo = sprintInfoContent.getStoryInfo(sprint, projects.get(0));
		assertEquals(ans, storiesInfo);
	}

	@Test
	public void testGetStoryInfo_WithoutImportance() {
		String ans = "	TEST_STORY_1(5)\n	TEST_STORY_2(5)\n	TEST_STORY_3(5)\n";
		ans = ans + "Estimated velocity : 15.0 story points";
		ArrayList<StoryObject> stories = mASTS.getStories();

		SprintInfoContent sprintInfoContent = new SprintInfoContent();
		ArrayList<SprintObject> sprints = mCS.getSprints();
		SprintObject sprint = sprints.get(0);
		ArrayList<ProjectObject> projects = mCP.getAllProjects();
		String storiesInfo = sprintInfoContent.getStoryInfo(sprint, projects.get(0));
		assertEquals(ans, storiesInfo);
	}

	@Test
	public void testGetSchedule() {
		String ans = "	 Sprint period :";
		Calendar cal = Calendar.getInstance();
		Date mToday = cal.getTime();
		String startDate = "";
		String endDate = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		startDate = format.format(mToday);
		cal.add(Calendar.DAY_OF_YEAR, 13);
		endDate = format.format(cal.getTime());
		ans = ans + startDate + " to " + endDate + "\n";
		ans = ans + "	 Daily Scrum : TEST_SPRINTDAILYINFO_1\n";
		ans = ans + "	 Sprint demo : " + endDate + " Lab1321";

		SprintInfoContent sprintInfoContent = new SprintInfoContent();
		ArrayList<SprintObject> sprints = mCS.getSprints();
		SprintObject sprint = sprints.get(0);
		String com = sprintInfoContent.getSchedule(sprint);
		assertEquals(ans, com);
	}
}
