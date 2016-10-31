package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.dao.SprintDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.web.databaseEnum.SprintEnum;
import ntut.csie.jcis.core.util.DateUtil;

public class SprintObjectTest {
	private MySQLControl mControl = null;
	private Configuration mConfig = null;
	private CreateProject mCP = null;
	private final static int mPROJECT_COUNT = 1;
	private long mProjectId = -1;
	private long mSprintId = -1;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCP = new CreateProject(mPROJECT_COUNT);
		mCP.exeCreateForDb();

		mControl = new MySQLControl(mConfig);
		mControl.connect();

		mProjectId = mCP.getAllProjects().get(0).getId();

		// create Sprint
		SprintObject sprint = createSprint();
		mSprintId = sprint.getId();
	}

	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		mConfig = null;
		mControl = null;
		mCP = null;
	}

	@Test
	public void testSaveCreateNewSprint() throws SQLException {
		// Test Data
		int sprintInterval = 2;
		int membersNumbre = 4;
		int hoursCanCommit = 150;
		int focusFactor = 80;

		String sprintGoal = "TEST_SPRINT_GOAL";
		String sprintDailyInfo = "TEST_SPRINT_DAILY_INFO";
		String sprintDemoPlace = "TEST_SPRINT_DEMO_PLACE";
		String sprintStartDate = "2015/05/28";
		String sprintDemoDate = "2015/06/11";
		String sprintEndDate = "2015/06/11";

		// create sprint object
		SprintObject sprint = new SprintObject(mProjectId);
		sprint.setInterval(sprintInterval).setTeamSize(membersNumbre)
				.setAvailableHours(hoursCanCommit).setFocusFactor(focusFactor)
				.setGoal(sprintGoal).setStartDate(sprintStartDate)
				.setEndDate(sprintEndDate).setDailyInfo(sprintDailyInfo)
				.setDemoDate(sprintDemoDate).setDemoPlace(sprintDemoPlace)
				.save();

		// 從資料庫撈出 Sprint
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SprintEnum.TABLE_NAME);
		valueSet.addEqualCondition(SprintEnum.ID, sprint.getId());

		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		SprintObject sprintFromDB = null;
		if (result.next()) {
			sprintFromDB = SprintDAO.convert(result);
		}
		closeResultSet(result);

		// assert
		assertEquals(sprintInterval, sprintFromDB.getInterval());
		assertEquals(membersNumbre, sprintFromDB.getTeamSize());
		assertEquals(hoursCanCommit, sprintFromDB.getAvailableHours());
		assertEquals(focusFactor, sprintFromDB.getFocusFactor());
		assertEquals(sprintGoal, sprintFromDB.getGoal());
		assertEquals(sprintDailyInfo, sprintFromDB.getDailyInfo());
		assertEquals(sprintDemoPlace, sprintFromDB.getDemoPlace());
		assertEquals(sprintStartDate, sprintFromDB.getStartDateString());
		assertEquals(sprintDemoDate, sprintFromDB.getDemoDateString());
		assertEquals(sprintEndDate, sprintFromDB.getEndDateString());
	}

	@Test
	public void testSaveUpdateSprint() {
		SprintObject sprint = createSprint();

		// Test Data
		int sprintInterval = 3;
		int membersNumbre = 6;
		int hoursCanCommit = 100;
		int focusFactor = 49;

		String sprintGoal = "TEST_SPRINT_GOAL_NEW";
		String sprintDailyInfo = "TEST_SPRINT_DAILY_INFO_NEW";
		String sprintDemoPlace = "TEST_SPRINT_DEMO_PLACE_NEW";
		String sprintStartDate = "2015/05/29";
		String sprintDemoDate = "2015/06/19";
		String sprintEndDate = "2015/06/19";

		sprint.setInterval(sprintInterval).setTeamSize(membersNumbre)
				.setAvailableHours(hoursCanCommit).setFocusFactor(focusFactor)
				.setGoal(sprintGoal).setStartDate(sprintStartDate)
				.setEndDate(sprintEndDate).setDailyInfo(sprintDailyInfo)
				.setDemoPlace(sprintDemoPlace).setDemoDate(sprintDemoDate)
				.save();

		sprint = SprintObject.get(sprint.getId());

		// assert
		assertEquals(sprintInterval, sprint.getInterval());
		assertEquals(membersNumbre, sprint.getTeamSize());
		assertEquals(hoursCanCommit, sprint.getAvailableHours());
		assertEquals(focusFactor, sprint.getFocusFactor());
		assertEquals(sprintGoal, sprint.getGoal());
		assertEquals(sprintDailyInfo, sprint.getDailyInfo());
		assertEquals(sprintDemoPlace, sprint.getDemoPlace());
		assertEquals(sprintStartDate, sprint.getStartDateString());
		assertEquals(sprintDemoDate, sprint.getDemoDateString());
		assertEquals(sprintEndDate, sprint.getEndDateString());
	}
	
	@Test
	public void testDelete() {
		SprintObject sprint = SprintObject.get(mSprintId);
		assertNotNull(sprint);

		// Delete
		boolean deleteStatus = sprint.delete();
		// assert
		assertTrue(deleteStatus);

		sprint = SprintObject.get(mSprintId);
		assertNull(sprint);
	}

	@Test
	public void testContainsTask() {
		SprintObject sprint = SprintObject.get(mSprintId);
		StoryObject story = new StoryObject(mProjectId);
		story.setSprintId(sprint.getId());
		story.save();
		TaskObject task = new TaskObject(mProjectId);
		task.save();
		assertFalse(sprint.containsTask(task));
		task.setStoryId(story.getId());
		task.save();
		assertTrue(sprint.containsTask(task));
	}

	private SprintObject createSprint() {
		// Test Data
		int sprintInterval = 2;
		int membersNumbre = 4;
		int hoursCanCommit = 150;
		int focusFactor = 80;

		String sprintGoal = "TEST_SPRINT_GOAL";
		String sprintDailyInfo = "TEST_SPRINT_DAILY_INFO";
		String sprintDemoPlace = "TEST_SPRINT_DEMO_PLACE";
		String sprintStartDate = "2015/05/28";
		String sprintDemoDate = "2015/06/11";
		String sprintEndDate = "2015/06/11";

		// create sprint object
		SprintObject sprint = new SprintObject(mProjectId);
		sprint.setInterval(sprintInterval).setTeamSize(membersNumbre)
				.setAvailableHours(hoursCanCommit).setFocusFactor(focusFactor)
				.setGoal(sprintGoal).setStartDate(sprintStartDate)
				.setEndDate(sprintEndDate).setDailyInfo(sprintDailyInfo)
				.setDemoDate(sprintDemoDate).setDemoPlace(sprintDemoPlace)
				.save();

		assertNotSame(-1, sprint.getId());
		assertEquals(sprintInterval, sprint.getInterval());
		assertEquals(membersNumbre, sprint.getTeamSize());
		assertEquals(hoursCanCommit, sprint.getAvailableHours());
		assertEquals(focusFactor, sprint.getFocusFactor());
		assertEquals(sprintGoal, sprint.getGoal());
		assertEquals(sprintDailyInfo, sprint.getDailyInfo());
		assertEquals(sprintDemoPlace, sprint.getDemoPlace());
		assertEquals(sprintStartDate, sprint.getStartDateString());
		assertEquals(sprintStartDate, sprint.getStartDateString());
		assertEquals(sprintEndDate, sprint.getEndDateString());

		return sprint;
	}
	
	@Test
	public void testContains() {
		SprintObject sprint = new SprintObject(mProjectId);
		sprint.setStartDate("2015/08/24");
		sprint.setEndDate("2015/08/31");
		sprint.save();
		assertFalse(sprint.contains(DateUtil.dayFilter("2015/08/23")));
		assertTrue(sprint.contains(DateUtil.dayFilter("2015/08/24")));
		assertTrue(sprint.contains(DateUtil.dayFilter("2015/08/25")));
		assertTrue(sprint.contains(DateUtil.dayFilter("2015/08/26")));
		assertTrue(sprint.contains(DateUtil.dayFilter("2015/08/27")));
		assertTrue(sprint.contains(DateUtil.dayFilter("2015/08/28")));
		assertTrue(sprint.contains(DateUtil.dayFilter("2015/08/29")));
		assertTrue(sprint.contains(DateUtil.dayFilter("2015/08/30")));
		assertTrue(sprint.contains(DateUtil.dayFilter("2015/08/31")));
		assertFalse(sprint.contains(DateUtil.dayFilter("2015/09/01")));
	}
	
	@Test
	public void TestDateCheck(){
		SprintObject sprint = new SprintObject(mProjectId);
		sprint.setStartDate("2015/08/24");
		sprint.setEndDate("2015/09/06");
		sprint.save();
		
		assertFalse(sprint.dateCheck(DateUtil.dayFilter("2015/08/17"), DateUtil.dayFilter("2015/08/23")));
		assertTrue(sprint.dateCheck(DateUtil.dayFilter("2015/08/18"), DateUtil.dayFilter("2015/08/24")));
		assertTrue(sprint.dateCheck(DateUtil.dayFilter("2015/08/19"), DateUtil.dayFilter("2015/08/25")));
		assertTrue(sprint.dateCheck(DateUtil.dayFilter("2015/08/23"), DateUtil.dayFilter("2015/08/29")));
		assertTrue(sprint.dateCheck(DateUtil.dayFilter("2015/08/24"), DateUtil.dayFilter("2015/08/30")));
		assertTrue(sprint.dateCheck(DateUtil.dayFilter("2015/08/25"), DateUtil.dayFilter("2015/08/31")));
		assertTrue(sprint.dateCheck(DateUtil.dayFilter("2015/08/30"), DateUtil.dayFilter("2015/09/05")));
		assertTrue(sprint.dateCheck(DateUtil.dayFilter("2015/08/31"), DateUtil.dayFilter("2015/09/06")));
		assertTrue(sprint.dateCheck(DateUtil.dayFilter("2015/09/01"), DateUtil.dayFilter("2015/09/07")));
		assertTrue(sprint.dateCheck(DateUtil.dayFilter("2015/09/05"), DateUtil.dayFilter("2015/09/11")));
		assertTrue(sprint.dateCheck(DateUtil.dayFilter("2015/09/06"), DateUtil.dayFilter("2015/09/12")));
		assertFalse(sprint.dateCheck(DateUtil.dayFilter("2015/09/07"), DateUtil.dayFilter("2015/09/13")));
	}
	
	@Test
	public void testGetTotalStoryPoints() {
		SprintObject sprint = new SprintObject(mProjectId);
		sprint.setStartDate("2015/08/24");
		sprint.setEndDate("2015/08/31");
		sprint.save();
		
		StoryObject story1 = new StoryObject(mProjectId);
		story1.setSprintId(sprint.getId())
			  .setEstimate(1)
			  .save();
		
		StoryObject story2 = new StoryObject(mProjectId);
		story2.setSprintId(sprint.getId())
			  .setEstimate(2)
			  .save();
		
		StoryObject story3 = new StoryObject(mProjectId);
		story3.setSprintId(sprint.getId())
			  .setEstimate(3)
			  .save();
		assertEquals(6, sprint.getTotalStoryPoints());
	}
	
	@Test
	public void testGetStoryUnclosedPoints() {
		SprintObject sprint = new SprintObject(mProjectId);
		sprint.setStartDate("2015/08/24");
		sprint.setEndDate("2015/08/31");
		sprint.save();
		
		StoryObject story1 = new StoryObject(mProjectId);
		story1.setSprintId(sprint.getId())
			  .setEstimate(1)
			  .save();
		
		StoryObject story2 = new StoryObject(mProjectId);
		story2.setSprintId(sprint.getId())
			  .setEstimate(2)
			  .setStatus(StoryObject.STATUS_DONE)
			  .save();
		
		StoryObject story3 = new StoryObject(mProjectId);
		story3.setSprintId(sprint.getId())
			  .setEstimate(3)
			  .save();
		assertEquals(4.0, sprint.getStoryUnclosedPoints());
	}
	
	@Test
	public void testGetTotalTaskPoints() {
		SprintObject sprint = new SprintObject(mProjectId);
		sprint.setStartDate("2015/08/24");
		sprint.setEndDate("2015/08/31");
		sprint.save();
		
		StoryObject story1 = new StoryObject(mProjectId);
		story1.setSprintId(sprint.getId())
			  .setEstimate(1)
			  .save();
		
		StoryObject story2 = new StoryObject(mProjectId);
		story2.setSprintId(sprint.getId())
			  .setEstimate(2)
			  .setStatus(StoryObject.STATUS_DONE)
			  .save();
		
		StoryObject story3 = new StoryObject(mProjectId);
		story3.setSprintId(sprint.getId())
			  .setEstimate(3)
			  .save();
		
		TaskObject task1 = new TaskObject(mProjectId);
		task1.setStoryId(story1.getId())
		     .setEstimate(4)
		     .save();
		
		TaskObject task2 = new TaskObject(mProjectId);
		task2.setStoryId(story2.getId())
		     .setEstimate(5)
		     .save();
		
		TaskObject task3 = new TaskObject(mProjectId);
		task3.setStoryId(story3.getId())
		     .setEstimate(6)
		     .save();
		assertEquals(15.0, sprint.getTotalTaskPoints());
	}
	
	@Test
	public void testGetTaskRemainsPoints() {
		SprintObject sprint = new SprintObject(mProjectId);
		sprint.setStartDate("2015/08/24");
		sprint.setEndDate("2015/08/31");
		sprint.save();
		
		StoryObject story1 = new StoryObject(mProjectId);
		story1.setSprintId(sprint.getId())
			  .setEstimate(1)
			  .save();
		
		StoryObject story2 = new StoryObject(mProjectId);
		story2.setSprintId(sprint.getId())
			  .setEstimate(2)
			  .setStatus(StoryObject.STATUS_DONE)
			  .save();
		
		StoryObject story3 = new StoryObject(mProjectId);
		story3.setSprintId(sprint.getId())
			  .setEstimate(3)
			  .save();
		
		TaskObject task1 = new TaskObject(mProjectId);
		task1.setStoryId(story1.getId())
		     .setEstimate(4)
		     .save();
		
		TaskObject task2 = new TaskObject(mProjectId);
		task2.setStoryId(story2.getId())
		     .setEstimate(5)
		     .save();
		
		TaskObject task3 = new TaskObject(mProjectId);
		task3.setStoryId(story3.getId())
		     .setEstimate(6)
		     .save();
		assertEquals(15.0, sprint.getTaskRemainsPoints());
	}
	
	@Test
	public void testGetLimitedPoint() {
		
	}

	@Test
	public void testContainsStory() {
		// Test Data
		int sprintInterval = 2;
		int membersNumbre = 4;
		int hoursCanCommit = 150;
		int focusFactor = 80;

		String sprintGoal = "TEST_SPRINT_GOAL";
		String sprintDailyInfo = "TEST_SPRINT_DAILY_INFO";
		String sprintDemoPlace = "TEST_SPRINT_DEMO_PLACE";
		String sprintStartDate = "2015/05/28";
		String sprintDemoDate = "2015/06/11";
		String sprintEndDate = "2015/06/11";

		// create sprint object
		SprintObject sprint = new SprintObject(mProjectId);
		sprint.setInterval(sprintInterval).setTeamSize(membersNumbre)
				.setAvailableHours(hoursCanCommit).setFocusFactor(focusFactor)
				.setGoal(sprintGoal).setStartDate(sprintStartDate)
				.setEndDate(sprintEndDate).setDailyInfo(sprintDailyInfo)
				.setDemoDate(sprintDemoDate).setDemoPlace(sprintDemoPlace)
				.save();
		
		// create story
		StoryObject story = new StoryObject(mProjectId);
		story.setSprintId(sprint.getId())
			.setName("Test_Story")
			.setImportance(99)
			.setValue(98)
			.setEstimate(20)
			.setStatus(StoryObject.STATUS_UNCHECK)
			.save();
		
		assertTrue(sprint.containsStory(story));
	}

	private void closeResultSet(ResultSet result) {
		if (result != null) {
			try {
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
