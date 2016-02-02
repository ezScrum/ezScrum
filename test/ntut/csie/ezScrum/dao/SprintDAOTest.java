package ntut.csie.ezScrum.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.databaseEnum.SprintEnum;

public class SprintDAOTest {
	private MySQLControl mControl = null;
	private Configuration mConfig;
	private CreateProject mCP;
	private static long sProjectId;
	private long mSprintId;
	private int mProjectCount = 1;

	@Before
	public void setUp() {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCP = new CreateProject(mProjectCount);
		mCP.exeCreateForDb();

		mControl = new MySQLControl(mConfig);
		mControl.connect();
		
		sProjectId = mCP.getAllProjects().get(0).getId();
		
		// create a sprint
		mSprintId = createSprint();
	}

	@After
	public void tearDown() {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mConfig.setTestMode(false);
		mConfig.save();

		// release
		ini = null;
		mCP = null;
		mConfig = null;
		mControl = null;
	}

	@Test
	public void testCreate() throws SQLException {
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

		// create sprint object
		SprintObject sprint = new SprintObject(sProjectId);
		sprint.setInterval(sprintInterval)
		      .setTeamSize(membersNumbre)
		      .setAvailableHours(hoursCanCommit)
		      .setFocusFactor(focusFactor)
		      .setGoal(sprintGoal)
		      .setStartDate(sprintStartDate)
		      .setDailyInfo(sprintDailyInfo)
		      .setDemoDate(sprintDemoDate)
		      .setDemoPlace(sprintDemoPlace);
		
		// call DAO
		long sprintId = SprintDAO.getInstance().create(sprint);
		assertEquals(2, sprintId);
		
		SprintObject sprintFromDB = null;
		
		// 從資料庫撈出 Sprint
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SprintEnum.TABLE_NAME);
		valueSet.addEqualCondition(SprintEnum.ID, sprintId);
		
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
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
	}

	@Test
	public void testGet() {
		// get
		SprintObject sprint = SprintDAO.getInstance().get(mSprintId);
		// assert
		assertNotNull(sprint);
		assertEquals(2, sprint.getInterval());
		assertEquals(4, sprint.getTeamSize());
		assertEquals(150, sprint.getAvailableHours());
		assertEquals(80, sprint.getFocusFactor());
		assertEquals("TEST_SPRINT_GOAL", sprint.getGoal());
		assertEquals("TEST_SPRINT_DAILY_INFO", sprint.getDailyInfo());
		assertEquals("TEST_SPRINT_DEMO_PLACE", sprint.getDemoPlace());
		assertEquals("2015/05/28", sprint.getStartDateString());
		assertEquals("2015/06/11", sprint.getDemoDateString());
	}

	@Test
	public void testUpdate() throws SQLException {
		// Test Data
		int sprintInterval = 3;
		int membersNumbre = 5;
		int hoursCanCommit = 180;
		int focusFactor = 49;

		String sprintGoal = "TEST_SPRINT_GOAL_NEW";
		String sprintDailyInfo = "TEST_SPRINT_DAILY_INFO_NEW";
		String sprintDemoPlace = "TEST_SPRINT_DEMO_PLACE_NEW";
		String sprintStartDate = "2015/05/29";
		String sprintDemoDate = "2015/06/19";
		// get
		SprintObject sprint = SprintDAO.getInstance().get(mSprintId);
		// update data
		sprint.setInterval(sprintInterval)
		        .setTeamSize(membersNumbre)
		        .setAvailableHours(hoursCanCommit)
		        .setFocusFactor(focusFactor)
		        .setGoal(sprintGoal)
		        .setStartDate(sprintStartDate)
		        .setDailyInfo(sprintDailyInfo)
		        .setDemoPlace(sprintDemoPlace)
		        .setDemoDate(sprintDemoDate);
		
		// call DAO update
		boolean updateStatus = SprintDAO.getInstance().update(sprint);
		// assert 
		assertTrue(updateStatus);

		SprintObject sprintFromDB = SprintDAO.getInstance().get(mSprintId);

		assertEquals(sprintInterval, sprintFromDB.getInterval());
		assertEquals(membersNumbre, sprintFromDB.getTeamSize());
		assertEquals(hoursCanCommit, sprintFromDB.getAvailableHours());
		assertEquals(focusFactor, sprintFromDB.getFocusFactor());
		assertEquals(sprintGoal, sprintFromDB.getGoal());
		assertEquals(sprintDailyInfo, sprintFromDB.getDailyInfo());
		assertEquals(sprintDemoPlace, sprintFromDB.getDemoPlace());
		assertEquals(sprintStartDate, sprintFromDB.getStartDateString());
		assertEquals(sprintDemoDate, sprintFromDB.getDemoDateString());
	}

	@Test
	public void testDelete() throws SQLException {
		// call DAO delete
		SprintDAO.getInstance().delete(mSprintId);
		
		// 從資料庫撈出 Sprint
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SprintEnum.TABLE_NAME);
		valueSet.addEqualCondition(SprintEnum.ID, mSprintId);

		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		SprintObject sprintFromDB = null;
		if (result.next()) {
			sprintFromDB = SprintDAO.convert(result);
		}
		closeResultSet(result);
		
		// assert
		assertNull(sprintFromDB);
	}
	
	private long createSprint() {
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

		// create sprint object
		SprintObject sprint = new SprintObject(sProjectId);
		sprint.setInterval(sprintInterval)
		        .setTeamSize(membersNumbre)
		        .setAvailableHours(hoursCanCommit)
		        .setFocusFactor(focusFactor)
		        .setGoal(sprintGoal)
		        .setStartDate(sprintStartDate)
		        .setDailyInfo(sprintDailyInfo)
		        .setDemoDate(sprintDemoDate)
		        .setDemoPlace(sprintDemoPlace);

		// call DAO
		long sprintId = SprintDAO.getInstance().create(sprint);
		assertTrue(sprintId > 0);
		
		return sprintId;
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
