package ntut.csie.ezScrum.web.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.SprintInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SprintPlanMapperTest {
	private static Log mlog = LogFactory.getLog(SprintPlanMapperTest.class);
	private CreateProject mCP;
	private CreateProductBacklog mCPB;

	private int mProjectCount = 1;
	private int mStoryCount = 2;

	private SprintPlanMapper mSprintPlanMapper = null;
	private Configuration mConfig = null;
	private SprintObject mSprint = null;

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
		mCP.exeCreate();

		// 新增 Story
		mCPB = new CreateProductBacklog(mStoryCount, mCP);
		mCPB.exe();

		// 建立  SprintPlanMapper 物件
		ProjectObject project = mCP.getAllProjects().get(0);
		mSprintPlanMapper = new SprintPlanMapper(project);

		// 建立 Sprint
		mSprint = createSprint();

		// ============= release ==============
		ini = null;
		project = null;
	}

	@After
	public void tearDown() {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCPB = null;
		mSprintPlanMapper = null;
		projectManager = null;
		mConfig = null;
	}

	@Test
	public void testAddSprint() {
		// create Sprint
		SprintInfo sprintInfo = new SprintInfo();
		sprintInfo.interval = 2;
		sprintInfo.members = 4;
		sprintInfo.hoursCanCommit = 100;
		sprintInfo.focusFactor = 80;
		sprintInfo.sprintGoal = "TEST_SPRINT_GOAL";
		sprintInfo.startDate = "2015/06/10";
		sprintInfo.demoDate = "2015/06/24";
		sprintInfo.demoPlace = "Lab1321";
		sprintInfo.dailyInfo = "11:10@Lab1321";

		// call SprintPlanMapper addSprintPlan
		SprintObject sprint = mSprintPlanMapper.addSprint(sprintInfo);

		// assert
		assertEquals(sprintInfo.interval, sprint.getInterval());
		assertEquals(sprintInfo.members, sprint.getMembersNumber());
		assertEquals(sprintInfo.hoursCanCommit, sprint.getHoursCanCommit());
		assertEquals(sprintInfo.focusFactor, sprint.getFocusFactor());
		assertEquals(sprintInfo.sprintGoal, sprint.getSprintGoal());
		assertEquals(sprintInfo.startDate, sprint.getStartDate());
		assertEquals(sprintInfo.demoDate, sprint.getDemoDate());
		assertEquals(sprintInfo.demoPlace, sprint.getDemoPlace());
		assertEquals(sprintInfo.dailyInfo, sprint.getDailyInfo());
	}

	@Test
	public void testGetSprint() {
		// get Sprint
		SprintObject sprint = mSprintPlanMapper.getSprint(mSprint.getId());
		// assert
		assertEquals(2, sprint.getInterval());
		assertEquals(4, sprint.getMembersNumber());
		assertEquals(100, sprint.getHoursCanCommit());
		assertEquals(80, sprint.getFocusFactor());
		assertEquals("TEST_SPRINT_GOAL", sprint.getSprintGoal());
		assertEquals("2015/06/10", sprint.getStartDate());
		assertEquals("2015/06/24", sprint.getDemoDate());
		assertEquals("Lab1321", sprint.getDemoPlace());
		assertEquals("11:10@Lab1321", sprint.getDailyInfo());
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
		sprintInfo.id = mSprint.getId();
		sprintInfo.interval = 2;
		sprintInfo.members = 2;
		sprintInfo.hoursCanCommit = 120;
		sprintInfo.focusFactor = 80;
		sprintInfo.sprintGoal = "TEST_SPRINT_GOAL_NEW";
		sprintInfo.startDate = "2015/06/11";
		sprintInfo.demoDate = "2015/06/25";
		sprintInfo.demoPlace = "Lab1321_NEW";
		sprintInfo.dailyInfo = "12:10@Lab1321";

		// call updateSprintPlan
		mSprintPlanMapper.updateSprint(sprintInfo);
		mSprint = SprintObject.get(mSprint.getId());

		// assert
		assertEquals(sprintInfo.interval, mSprint.getInterval());
		assertEquals(sprintInfo.members, mSprint.getMembersNumber());
		assertEquals(sprintInfo.hoursCanCommit, mSprint.getHoursCanCommit());
		assertEquals(sprintInfo.focusFactor, mSprint.getFocusFactor());
		assertEquals(sprintInfo.sprintGoal, mSprint.getSprintGoal());
		assertEquals(sprintInfo.startDate, mSprint.getStartDate());
		assertEquals(sprintInfo.demoDate, mSprint.getDemoDate());
		assertEquals(sprintInfo.demoPlace, mSprint.getDemoPlace());
		assertEquals(sprintInfo.dailyInfo, mSprint.getDailyInfo());
	}

	@Test
	public void testDeleteSprint() {
		// Delete Sprint
		mSprintPlanMapper.deleteSprint(mSprint.getId());
		// Get Sprint
		SprintObject sprint = SprintObject.get(mSprint.getId());
		// assert
		assertNull(sprint);
	}

	@Test
	public void testMoveSprint() {
		// create Sprint 2
		SprintInfo sprintInfo = new SprintInfo();
		sprintInfo.interval = 3;
		sprintInfo.members = 2;
		sprintInfo.hoursCanCommit = 80;
		sprintInfo.focusFactor = 70;
		sprintInfo.sprintGoal = "TEST_SPRINT_GOAL_2";
		sprintInfo.startDate = "2015/06/10";
		sprintInfo.demoDate = "2015/07/01";
		sprintInfo.demoPlace = "Lab1324";
		sprintInfo.dailyInfo = "17:10@Lab1324";
		SprintObject sprint = mSprintPlanMapper.addSprint(sprintInfo);

		// record serialId
		long sprintSerialId1 = mSprint.getSerialId();
		long sprintSerialId2 = sprint.getSerialId();

		// move sprint
		mSprintPlanMapper.moveSprint(sprint.getId(), mSprint.getId());
		mSprint.reload();
		sprint.reload();

		// assert
		assertEquals(sprintSerialId2, mSprint.getSerialId());
		assertEquals(sprintSerialId1, sprint.getSerialId());
	}

	private SprintObject createSprint() {
		// create Sprint
		SprintInfo sprintInfo = new SprintInfo();
		sprintInfo.interval = 2;
		sprintInfo.members = 4;
		sprintInfo.hoursCanCommit = 100;
		sprintInfo.focusFactor = 80;
		sprintInfo.sprintGoal = "TEST_SPRINT_GOAL";
		sprintInfo.startDate = "2015/06/10";
		sprintInfo.demoDate = "2015/06/24";
		sprintInfo.demoPlace = "Lab1321";
		sprintInfo.dailyInfo = "11:10@Lab1321";
		// addSprint
		SprintObject sprint = mSprintPlanMapper.addSprint(sprintInfo);
		// echo
		mlog.info("Create 1 test Sprint success.");
		return sprint;
	}
}
