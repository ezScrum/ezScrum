package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;

import ntut.csie.ezScrum.dao.SprintDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.databasEnum.SprintEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
		mCP.exeCreate();
		
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

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

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
		String sprintDueDate = "2015/06/11";

		// create sprint object
		SprintObject sprint = new SprintObject(mProjectId);
		sprint.setInterval(sprintInterval)
		        .setMembers(membersNumbre)
		        .setHoursCanCommit(hoursCanCommit)
		        .setFocusFactor(focusFactor)
		        .setSprintGoal(sprintGoal)
		        .setStartDate(sprintStartDate)
		        .setDueDate(sprintDueDate)
		        .setDailyInfo(sprintDailyInfo)
		        .setDemoDate(sprintDemoDate)
		        .setDemoPlace(sprintDemoPlace)
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
		assertEquals(membersNumbre, sprintFromDB.getMembersNumber());
		assertEquals(hoursCanCommit, sprintFromDB.getHoursCanCommit());
		assertEquals(focusFactor, sprintFromDB.getFocusFactor());
		assertEquals(sprintGoal, sprintFromDB.getSprintGoal());
		assertEquals(sprintDailyInfo, sprintFromDB.getDailyInfo());
		assertEquals(sprintDemoPlace, sprintFromDB.getDemoPlace());
		assertEquals(sprintStartDate, sprintFromDB.getStartDate());
		assertEquals(sprintDemoDate, sprintFromDB.getDemoDate());
		assertEquals(sprintDueDate, sprintFromDB.getDueDate());
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
		String sprintDueDate = "2015/06/19";
		
		sprint.setInterval(sprintInterval)
		        .setMembers(membersNumbre)
		        .setHoursCanCommit(hoursCanCommit)
		        .setFocusFactor(focusFactor)
		        .setSprintGoal(sprintGoal)
		        .setStartDate(sprintStartDate)
		        .setDueDate(sprintDueDate)
		        .setDailyInfo(sprintDailyInfo)
		        .setDemoPlace(sprintDemoPlace)
		        .setDemoDate(sprintDemoDate)
		        .save();
		
		sprint = SprintObject.get(sprint.getId());
		
		// assert
		assertEquals(sprintInterval, sprint.getInterval());
		assertEquals(membersNumbre, sprint.getMembersNumber());
		assertEquals(hoursCanCommit, sprint.getHoursCanCommit());
		assertEquals(focusFactor, sprint.getFocusFactor());
		assertEquals(sprintGoal, sprint.getSprintGoal());
		assertEquals(sprintDailyInfo, sprint.getDailyInfo());
		assertEquals(sprintDemoPlace, sprint.getDemoPlace());
		assertEquals(sprintStartDate, sprint.getStartDate());
		assertEquals(sprintDemoDate, sprint.getDemoDate());
		assertEquals(sprintDueDate, sprint.getDueDate());
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
		String sprintDueDate = "2015/06/11";

		// create sprint object
		SprintObject sprint = new SprintObject(mProjectId);
		sprint.setInterval(sprintInterval)
		        .setMembers(membersNumbre)
		        .setHoursCanCommit(hoursCanCommit)
		        .setFocusFactor(focusFactor)
		        .setSprintGoal(sprintGoal)
		        .setStartDate(sprintStartDate)
		        .setDueDate(sprintDueDate)
		        .setDailyInfo(sprintDailyInfo)
		        .setDemoDate(sprintDemoDate)
		        .setDemoPlace(sprintDemoPlace)
		        .save();

		assertNotSame(-1, sprint.getId());
		assertEquals(sprintInterval, sprint.getInterval());
		assertEquals(membersNumbre, sprint.getMembersNumber());
		assertEquals(hoursCanCommit, sprint.getHoursCanCommit());
		assertEquals(focusFactor, sprint.getFocusFactor());
		assertEquals(sprintGoal, sprint.getSprintGoal());
		assertEquals(sprintDailyInfo, sprint.getDailyInfo());
		assertEquals(sprintDemoPlace, sprint.getDemoPlace());
		assertEquals(sprintStartDate, sprint.getStartDate());
		assertEquals(sprintStartDate, sprint.getStartDate());
		assertEquals(sprintDueDate, sprint.getDueDate());
		
		return sprint;
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
