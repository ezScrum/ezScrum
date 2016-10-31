package ntut.csie.ezScrum.web.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.jcis.core.util.DateUtil;

public class SprintPlanHelperTest {
	private SprintPlanHelper mSprintPlanHelper;
	private CreateProject mCP;
	private CreateSprint mCS;
	private int mProjectCount = 1;
	private int mSprintCount = 3;
	private Configuration mConfig = null;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCP = new CreateProject(mProjectCount);
		mCP.exeCreateForDb();

		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();

		// create SprintPlanHelper
		mSprintPlanHelper = new SprintPlanHelper(mCP.getAllProjects().get(0));
	}

	@After
	public void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		// release
		mCP = null;
		mCS = null;
		mConfig = null;
	}

	@Test
	public void testGetNextDemoDate_currentSprint() {
		// Get first Sprint
		SprintObject firstSprint = SprintObject.get(mCS.getSprintsId().get(0));
		// Call GetNextDemoDate
		String actualNextDamoDate = mSprintPlanHelper.getNextDemoDate();
		// Assert
		assertEquals(firstSprint.getDemoDateString(), actualNextDamoDate);
	}

	@Test
	public void testGetNextDemoDate_BeforeEverySprint() {
		// Modify first sprint
		SprintObject firstSprint = SprintObject.get(mCS.getSprintsId().get(0));
		firstSprint.setStartDate("2015/06/01").setEndDate("2015/06/15")
				.setDemoDate("2015/06/15").save();
		// Call GetNextDemoDate
		String actualNextDamoDate = mSprintPlanHelper.getNextDemoDate();
		// Get second sprint
		SprintObject secondSprint = SprintObject.get(mCS.getSprintsId().get(1));
		// Assert
		assertEquals(secondSprint.getDemoDateString(), actualNextDamoDate);
	}

	@Test
	public void testGetNextDemoDate_AfterEverySprint() {
		// Modify first sprint
		SprintObject firstSprint = SprintObject.get(mCS.getSprintsId().get(0));
		firstSprint.setStartDate("2015/06/01").setEndDate("2015/06/15")
				.setDemoDate("2015/06/15").save();
		// Modify second sprint
		SprintObject secondSprint = SprintObject.get(mCS.getSprintsId().get(1));
		secondSprint.setStartDate("2015/06/16").setEndDate("2015/06/30")
				.setDemoDate("2015/06/30").save();
		// Modify third sprint
		SprintObject thirdSprint = SprintObject.get(mCS.getSprintsId().get(2));
		thirdSprint.setStartDate("2015/07/01").setEndDate("2015/07/15")
				.setDemoDate("2015/07/15").save();
		// Call GetNextDemoDate
		String actualNextDamoDate = mSprintPlanHelper.getNextDemoDate();
		// Assert
		assertNull(actualNextDamoDate);
	}

	@Test
	public void testGetOneSprintInformation_lastSprint() {
		// Get Last Sprint
		SprintObject thirdSprint = SprintObject.get(mCS.getSprintsId().get(2));
		// getOneSprintInformation - Get Last sprint
		SprintObject lastSprint = mSprintPlanHelper.getOneSprintInformation(
				true, -1);
		// assert
		assertEquals(thirdSprint.getId(), lastSprint.getId());
		assertEquals(thirdSprint.getProjectId(), lastSprint.getProjectId());
		assertEquals(thirdSprint.getTeamSize(),
				lastSprint.getTeamSize());
		assertEquals(thirdSprint.getInterval(), lastSprint.getInterval());
		assertEquals(thirdSprint.getFocusFactor(), lastSprint.getFocusFactor());
		assertEquals(thirdSprint.getAvailableHours(),
				lastSprint.getAvailableHours());
		assertEquals(thirdSprint.getStartDateString(), lastSprint.getStartDateString());
		assertEquals(thirdSprint.getDemoDateString(), lastSprint.getDemoDateString());
		assertEquals(thirdSprint.getGoal(), lastSprint.getGoal());
		assertEquals(thirdSprint.getDailyInfo(), lastSprint.getDailyInfo());
		assertEquals(thirdSprint.getStories().size(), lastSprint.getStories()
				.size());
	}

	@Test
	public void testGetOneSprintInformation_sprintId() {
		// Get Sprint
		SprintObject firstSprint = SprintObject.get(mCS.getSprintsId().get(0));
		// getOneSprintInformation - SprintId
		SprintObject sprint = mSprintPlanHelper.getOneSprintInformation(false,
				firstSprint.getId());
		// assert
		assertEquals(firstSprint.getId(), sprint.getId());
		assertEquals(firstSprint.getProjectId(), sprint.getProjectId());
		assertEquals(firstSprint.getTeamSize(), sprint.getTeamSize());
		assertEquals(firstSprint.getInterval(), sprint.getInterval());
		assertEquals(firstSprint.getFocusFactor(), sprint.getFocusFactor());
		assertEquals(firstSprint.getAvailableHours(),
				sprint.getAvailableHours());
		assertEquals(firstSprint.getStartDateString(), sprint.getStartDateString());
		assertEquals(firstSprint.getDemoDateString(), sprint.getDemoDateString());
		assertEquals(firstSprint.getGoal(), sprint.getGoal());
		assertEquals(firstSprint.getDailyInfo(), sprint.getDailyInfo());
		assertEquals(firstSprint.getStories().size(), sprint.getStories()
				.size());
	}

	@Test
	public void testGetProjectStartDate() {
		// Get Sprint
		SprintObject firstSprint = SprintObject.get(mCS.getSprintsId().get(0));
		// Get ProjectStartDate
		Date projectStartDate = mSprintPlanHelper.getProjectStartDate();
		// First Sprint start date
		Date firstSprintStartDate = DateUtil.dayFilter(firstSprint.getStartDateString());
		// assert
		assertEquals(firstSprintStartDate.getTime(), projectStartDate.getTime());
	}

	@Test
	public void testGetProjectEndDate() {
		// Get last Sprint
		SprintObject lastSprint = SprintObject.get(mCS.getSprintsId().get(
				mCS.getSprintCount() - 1));
		// Get ProjectEndDate
		Date projectEndDate = mSprintPlanHelper.getProjectEndDate();
		// Last Sprint end date
		Date lastSprintEndDate = DateUtil.dayFilter(lastSprint.getDemoDateString());
		// assert
		assertEquals(lastSprintEndDate.getTime(), projectEndDate.getTime());
	}

	@Test
	public void testGetSprintByDate() {
		// Sprint 1
		SprintObject sprint1 = SprintObject.get(mCS.getSprintsId().get(0));
		// Sprint 2
		SprintObject sprint2 = SprintObject.get(mCS.getSprintsId().get(1));
		// Sprint 3
		SprintObject sprint3 = SprintObject.get(mCS.getSprintsId().get(2));

		// get Now Time
		Calendar calendar = Calendar.getInstance();
		Date nowTime = calendar.getTime();

		// GetSprintbyDate - now
		SprintObject sprintNow = mSprintPlanHelper.getSprintByDate(nowTime);
		// assert
		assertEquals(sprint1.getId(), sprintNow.getId());

		// get Date 2 weeks after
		calendar.add(Calendar.DAY_OF_YEAR, 7 * CreateSprint.SPRINT_INTERVAL + 1);
		Date twoWeeksAfterDate = calendar.getTime();
		// GetSprintIdbyDate - 2 weeks after
		sprintNow = mSprintPlanHelper.getSprintByDate(twoWeeksAfterDate);
		// assert
		assertEquals(sprint2.getId(), sprintNow.getId());

		// get Date 4 weeks after
		calendar.add(Calendar.DAY_OF_YEAR, 7 * CreateSprint.SPRINT_INTERVAL + 1);
		Date fourWeeksAfterDate = calendar.getTime();
		// GetSprintIdbyDate - 4 weeks after
		sprintNow = mSprintPlanHelper.getSprintByDate(fourWeeksAfterDate);
		// assert
		assertEquals(sprint3.getId(), sprintNow.getId());

		// get Date 6 weeks after
		calendar.add(Calendar.DAY_OF_YEAR, 7 * CreateSprint.SPRINT_INTERVAL + 1);
		Date sixWeeksAfterDate = calendar.getTime();
		// GetSprintIdbyDate - 6 weeks after
		sprintNow = mSprintPlanHelper.getSprintByDate(sixWeeksAfterDate);
		// assert
		assertNull(sprintNow);
	}
	
	@Test
	public void testCheckSprintDateOverlapping(){
		// Get last Sprint
		SprintObject lastSprint = SprintObject.get(mCS.getSprintsId().get(2));
		StringBuilder checkSprintDate = mSprintPlanHelper.checkSprintDateOverlapping(lastSprint.getId(), lastSprint.getStartDateString(), lastSprint.getEndDateString());
		assertEquals("legal", checkSprintDate.toString());
		
		//Create a sprint which have same startDate and EndDate with lastSprint.
		SprintObject newSprint = new SprintObject(lastSprint.getProjectId());
		newSprint.setTeamSize(1)
		.setAvailableHours(lastSprint.getAvailableHours())
		.setFocusFactor(lastSprint.getFocusFactor())
		.setGoal("Check have same date.")
		.setStartDate(lastSprint.getStartDateString())
		.setEndDate(lastSprint.getEndDateString())
		.setDemoDate(lastSprint.getDemoDateString())
		.save();
		mCS.getSprintsId().add(newSprint.getId());
		mCS.getSprints().add(newSprint);
		checkSprintDate = mSprintPlanHelper.checkSprintDateOverlapping(newSprint.getId(), newSprint.getStartDateString(), newSprint.getEndDateString());
		assertEquals("illegal", checkSprintDate.toString());
	}
}