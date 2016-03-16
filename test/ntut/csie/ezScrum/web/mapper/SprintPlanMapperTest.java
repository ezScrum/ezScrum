package ntut.csie.ezScrum.web.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataInfo.SprintInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;

public class SprintPlanMapperTest {
	private static Log mlog = LogFactory.getLog(SprintPlanMapperTest.class);
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateProductBacklog mCPB;
	private int mProjectCount = 1;
	private int mSprintCount = 1;
	private int mStoryCount = 2;
	private SprintPlanMapper mSprintPlanMapper = null;
	private Configuration mConfig = null;

	@Before
	public void setUp() {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增 Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreateForDb();
		
		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();

		// 新增 Story
		mCPB = new CreateProductBacklog(mStoryCount, mCP);
		mCPB.exe();

		// 建立  SprintPlanMapper 物件
		ProjectObject project = mCP.getAllProjects().get(0);
		mSprintPlanMapper = new SprintPlanMapper(project);

		// ============= release ==============
		ini = null;
		project = null;
	}

	@After
	public void tearDown() {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mCPB = null;
		mSprintPlanMapper = null;
		mConfig = null;
	}

	@Test
	public void testAddSprint() {
		// create Sprint
		SprintInfo sprintInfo = new SprintInfo();
		sprintInfo.interval = 2;
		sprintInfo.teamSize = 4;
		sprintInfo.hoursCanCommit = 100;
		sprintInfo.focusFactor = 80;
		sprintInfo.sprintGoal = "TEST_SPRINT_GOAL";
		sprintInfo.startDate = "2015/06/10";
		sprintInfo.demoDate = "2015/06/24";
		sprintInfo.demoPlace = "Lab1321";
		sprintInfo.dailyInfo = "11:10@Lab1321";
		sprintInfo.dueDate = "2015/06/24";

		// call SprintPlanMapper addSprintPlan
		long sprintId = mSprintPlanMapper.addSprint(sprintInfo);
		SprintObject sprint = SprintObject.get(sprintId);

		// assert
		assertEquals(sprintInfo.interval, sprint.getInterval());
		assertEquals(sprintInfo.teamSize, sprint.getTeamSize());
		assertEquals(sprintInfo.hoursCanCommit, sprint.getAvailableHours());
		assertEquals(sprintInfo.focusFactor, sprint.getFocusFactor());
		assertEquals(sprintInfo.sprintGoal, sprint.getGoal());
		assertEquals(sprintInfo.startDate, sprint.getStartDateString());
		assertEquals(sprintInfo.demoDate, sprint.getDemoDateString());
		assertEquals(sprintInfo.demoPlace, sprint.getDemoPlace());
		assertEquals(sprintInfo.dailyInfo, sprint.getDailyInfo());
	}

	@Test
	public void testGetSprint() {
		// get Sprint
		SprintObject sprint = mSprintPlanMapper.getSprint(mCS.getSprintsId().get(0));
		// assert
		assertEquals(2, sprint.getInterval());
		assertEquals(4, sprint.getTeamSize());
		assertEquals(120, sprint.getAvailableHours());
		assertEquals(80, sprint.getFocusFactor());
		assertEquals(mCS.TEST_SPRINT_GOAL + 1, sprint.getGoal());
		assertEquals(CreateSprint.SPRINT_DEMOPLACE, sprint.getDemoPlace());
		assertEquals(mCS.TEST_SPRINT_DAILY_INFO + 1, sprint.getDailyInfo());
		
		// get startDate
		Date currentDate = mCS.mToday;
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Calendar calendarStart = Calendar.getInstance();
		Calendar calendarEnd = Calendar.getInstance();
		calendarStart.setTime(currentDate);
		// get endDate
		calendarEnd.add(Calendar.DAY_OF_YEAR, CreateSprint.SPRINT_INTERVAL * 7 - 1);
		
		// assert
		assertEquals(format.format(calendarStart.getTime()), sprint.getStartDateString());
		assertEquals(format.format(calendarEnd.getTime()), sprint.getDemoDateString());
	}

	@Test
	public void testGetSprints() {
		// create multiple sprints
		createSprint();
		createSprint();

		// get Sprints
		ArrayList<SprintObject> sprints = mSprintPlanMapper.getSprints();

		// assert
		assertEquals(3, sprints.size());
	}

	@Test
	public void testUpdateSprint() {
		SprintInfo sprintInfo = new SprintInfo();
		sprintInfo.id = mCS.getSprintsId().get(0);
		SprintObject tempSprint = SprintObject.get(sprintInfo.id);
		sprintInfo.serialId = tempSprint.getSerialId();
		sprintInfo.interval = 2;
		sprintInfo.teamSize = 2;
		sprintInfo.hoursCanCommit = 120;
		sprintInfo.focusFactor = 80;
		sprintInfo.sprintGoal = "TEST_SPRINT_GOAL_NEW";
		sprintInfo.startDate = "2015/06/11";
		sprintInfo.demoDate = "2015/06/25";
		sprintInfo.demoPlace = "Lab1321_NEW";
		sprintInfo.dailyInfo = "12:10@Lab1321";
		sprintInfo.dueDate = "2015/06/25";
		
		// call updateSprintPlan
		mSprintPlanMapper.updateSprint(sprintInfo);
		SprintObject sprint = SprintObject.get(mCS.getSprintsId().get(0));

		// assert
		assertEquals(sprintInfo.interval, sprint.getInterval());
		assertEquals(sprintInfo.teamSize, sprint.getTeamSize());
		assertEquals(sprintInfo.hoursCanCommit, sprint.getAvailableHours());
		assertEquals(sprintInfo.focusFactor, sprint.getFocusFactor());
		assertEquals(sprintInfo.sprintGoal, sprint.getGoal());
		assertEquals(sprintInfo.startDate, sprint.getStartDateString());
		assertEquals(sprintInfo.demoDate, sprint.getDemoDateString());
		assertEquals(sprintInfo.demoPlace, sprint.getDemoPlace());
		assertEquals(sprintInfo.dailyInfo, sprint.getDailyInfo());
	}

	@Test
	public void testDeleteSprint() {
		// Delete Sprint
		mSprintPlanMapper.deleteSprint(mCS.getSprintsId().get(0));
		// Get Sprint
		SprintObject sprint = SprintObject.get(mCS.getSprintsId().get(0));
		// assert
		assertNull(sprint);
	}

	@Test
	public void testMoveSprint() {
		// create Sprint 2
		SprintInfo sprintInfo = new SprintInfo();
		sprintInfo.interval = 3;
		sprintInfo.teamSize = 2;
		sprintInfo.hoursCanCommit = 80;
		sprintInfo.focusFactor = 70;
		sprintInfo.sprintGoal = "TEST_SPRINT_GOAL_2";
		sprintInfo.startDate = "2015/06/10";
		sprintInfo.demoDate = "2015/07/01";
		sprintInfo.demoPlace = "Lab1324";
		sprintInfo.dailyInfo = "17:10@Lab1324";
		sprintInfo.dueDate = "2015/07/01";
		long sprintId = mSprintPlanMapper.addSprint(sprintInfo);
		SprintObject sprint = SprintObject.get(sprintId);
		
		// Get first default Sprint
		SprintObject defaultSprint = SprintObject.get(mCS.getSprintsId().get(0));

		// record serialId
		long sprintSerialId1 = defaultSprint.getSerialId();
		long sprintSerialId2 = sprint.getSerialId();

		// move sprint
		mSprintPlanMapper.moveSprint(sprint.getId(), defaultSprint.getId());
		defaultSprint.reload();
		sprint.reload();

		// assert
		assertEquals(sprintSerialId2, defaultSprint.getSerialId());
		assertEquals(sprintSerialId1, sprint.getSerialId());
	}

	private SprintObject createSprint() {
		// create Sprint
		SprintInfo sprintInfo = new SprintInfo();
		sprintInfo.interval = 2;
		sprintInfo.teamSize = 4;
		sprintInfo.hoursCanCommit = 100;
		sprintInfo.focusFactor = 80;
		sprintInfo.sprintGoal = "TEST_SPRINT_GOAL";
		sprintInfo.startDate = "2015/06/10";
		sprintInfo.demoDate = "2015/06/24";
		sprintInfo.demoPlace = "Lab1321";
		sprintInfo.dailyInfo = "11:10@Lab1321";
		sprintInfo.dueDate = "2015/06/24";
		// addSprint
		long sprintId = mSprintPlanMapper.addSprint(sprintInfo);
		SprintObject sprint = SprintObject.get(sprintId);
		// echo
		mlog.info("Create 1 test Sprint success.");
		return sprint;
	}
}
