package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.dao.ReleaseDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.databaseEnum.ReleaseEnum;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.jcis.core.util.DateUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReleaseObjectTest {
	private MySQLControl mControl = null;
	private Configuration mConfig = null;
	private CreateProject mCP = null;
	private CreateSprint mCS = null;
	private final int mPROJECT_COUNT = 1;
	private ReleaseObject mRelease = null;
	private ProjectObject mProject = null;

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

		mProject = mCP.getAllProjects().get(0);
		mRelease = createRelease();
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
	public void testSaveCreateNewRelease() throws SQLException {
		// Test Data
		String releaseName = "TEST_RELEASE_NAME";
		String releaseDescription = "TEST_RELEASE_DESCRIPTION";
		String releaseStartDate = "2015/08/03";
		String releaseDueDate = "2015/10/31";

		// Create release object
		ReleaseObject release = new ReleaseObject(mProject.getId());
		release.setName(releaseName).setDescription(releaseDescription)
				.setStartDate(releaseStartDate).setDueDate(releaseDueDate)
				.save();

		// 從資料庫撈出 Release
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ReleaseEnum.TABLE_NAME);
		valueSet.addEqualCondition(ReleaseEnum.ID, release.getId());

		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		ReleaseObject releaseCreated = null;
		if (result.next()) {
			releaseCreated = ReleaseDAO.convert(result);
		}
		// Close result set
		closeResultSet(result);

		// assert
		assertEquals(release.getId(), releaseCreated.getId());
		assertEquals(release.getProjectId(), releaseCreated.getProjectId());
		assertEquals(releaseName, releaseCreated.getName());
		assertEquals(releaseDescription, releaseCreated.getDescription());
		assertEquals(releaseStartDate, releaseCreated.getStartDateString());
		assertEquals(releaseDueDate, releaseCreated.getDueDateString());
	}

	@Test
	public void testSaveUpdateRelease() {
		// Test Data
		String releaseName = "TEST_RELEASE_NAME_NEW";
		String releaseDescription = "TEST_RELEASE_DESCRIPTION_NEW";
		String releaseStartDate = "2015/06/03";
		String releaseDueDate = "2015/08/31";

		// Update Release
		mRelease.setName(releaseName).setDescription(releaseDescription)
				.setStartDate(releaseStartDate).setDueDate(releaseDueDate)
				.save();

		ReleaseObject release = ReleaseObject.get(mRelease.getId());

		// assert
		assertEquals(releaseName, release.getName());
		assertEquals(releaseDescription, release.getDescription());
		assertEquals(releaseStartDate, release.getStartDateString());
		assertEquals(releaseDueDate, release.getDueDateString());
	}

	@Test
	public void testDelete() {
		// Get releaseId
		long releaseId = mRelease.getId();
		// Assert release exist
		assertNotNull(mRelease);
		// Delete release
		boolean deleteStatus = mRelease.delete();
		// Assert Delete Status
		assertTrue(deleteStatus);

		// Reload release object
		ReleaseObject release = ReleaseObject.get(releaseId);
		// Assert release object is null
		assertNull(release);
	}

	@Test
	public void testContainsSprint() {
		ReleaseObject release = new ReleaseObject(mProject.getId());
		release.setName("TEST_RELEASE").setStartDate("2015/08/01")
				.setDueDate("2015/08/31").save();
		SprintObject sprint1 = new SprintObject(mProject.getId());
		sprint1.setGoal("TEST_SPRINT_GOAL_1").setStartDate("2015/07/24")
				.setDueDate("2015/08/01").save();
		SprintObject sprint2 = new SprintObject(mProject.getId());
		sprint2.setGoal("TEST_SPRINT_GOAL_2").setStartDate("2015/08/31")
				.setDueDate("2015/09/06").save();
		SprintObject sprint3 = new SprintObject(mProject.getId());
		sprint3.setGoal("TEST_SPRINT_GOAL_3").setStartDate("2015/08/15")
				.setDueDate("2015/08/21").save();
		// assert
		assertFalse(release.containsSprint(sprint1));
		assertFalse(release.containsSprint(sprint2));
		assertTrue(release.containsSprint(sprint3));
	}

	@Test
	public void testGetSprints() {
		ReleaseObject release = new ReleaseObject(mProject.getId());
		release.setName("TEST_RELEASE").setStartDate("2015/08/01")
				.setDueDate("2015/08/31").save();
		SprintObject sprint1 = new SprintObject(mProject.getId());
		sprint1.setGoal("TEST_SPRINT_GOAL_1").setStartDate("2015/08/01")
				.setDueDate("2015/08/07").save();
		SprintObject sprint2 = new SprintObject(mProject.getId());
		sprint2.setGoal("TEST_SPRINT_GOAL_2").setStartDate("2015/08/08")
				.setDueDate("2015/08/14").save();
		SprintObject sprint3 = new SprintObject(mProject.getId());
		sprint3.setGoal("TEST_SPRINT_GOAL_3").setStartDate("2015/08/15")
				.setDueDate("2015/08/21").save();
		// assert sprint count
		assertEquals(3, release.getSprints().size());
		// assert sprint 1
		assertEquals(sprint1.getId(), release.getSprints().get(0).getId());
		assertEquals(sprint1.getGoal(), release.getSprints().get(0)
				.getGoal());
		assertEquals(sprint1.getStartDateString(), release.getSprints().get(0)
				.getStartDateString());
		assertEquals(sprint1.getDueDateString(), release.getSprints().get(0)
				.getDueDateString());
		// assert sprint 2
		assertEquals(sprint2.getId(), release.getSprints().get(1).getId());
		assertEquals(sprint2.getGoal(), release.getSprints().get(1)
				.getGoal());
		assertEquals(sprint2.getStartDateString(), release.getSprints().get(1)
				.getStartDateString());
		assertEquals(sprint2.getDueDateString(), release.getSprints().get(1)
				.getDueDateString());
		// assert sprint 3
		assertEquals(sprint3.getId(), release.getSprints().get(2).getId());
		assertEquals(sprint3.getGoal(), release.getSprints().get(2)
				.getGoal());
		assertEquals(sprint3.getStartDateString(), release.getSprints().get(2)
				.getStartDateString());
		assertEquals(sprint3.getDueDateString(), release.getSprints().get(2)
				.getDueDateString());
	}

	@Test
	public void testGetStories() {
		// Test Data
		String storyName = "TEST_STORY_NAME_";
		String storyNotes = "TEST_STORY_NOTES_";
		String storyHowtodemo = "TEST_STORY_HOW_TO_DEMO_";
		int storyEstimate = 8;
		int storyImportance = 96;

		// Create Sprint
		SprintObject sprint = new SprintObject(mProject.getId());
		sprint.setInterval(2).setAvailableHours(100).setTeamSize(4)
				.setGoal("TEST_SPRINT_GOAL")
				.setDailyInfo("TEST_SPRINT_DAILY_INFO")
				.setStartDate("2015/08/03").setDemoDate("2015/08/17")
				.setDueDate("2015/08/17").save();

		// Create Story 1
		StoryObject story1 = new StoryObject(mProject.getId());
		story1.setSprintId(sprint.getId()).setName(storyName + 1)
				.setEstimate(storyEstimate)
				.setStatus(StoryObject.STATUS_UNCHECK).setNotes(storyNotes + 1)
				.setImportance(storyImportance)
				.setHowToDemo(storyHowtodemo + 1).save();

		// Create Story 2
		StoryObject story2 = new StoryObject(mProject.getId());
		story2.setSprintId(sprint.getId()).setName(storyName + 2)
				.setEstimate(storyEstimate)
				.setStatus(StoryObject.STATUS_UNCHECK).setNotes(storyNotes + 2)
				.setImportance(storyImportance)
				.setHowToDemo(storyHowtodemo + 2).save();

		// Create Story 3
		StoryObject story3 = new StoryObject(mProject.getId());
		story3.setSprintId(sprint.getId()).setName(storyName + 3)
				.setEstimate(storyEstimate)
				.setStatus(StoryObject.STATUS_UNCHECK).setNotes(storyNotes + 3)
				.setImportance(storyImportance)
				.setHowToDemo(storyHowtodemo + 3).save();

		// GetStories
		ArrayList<StoryObject> stories = mRelease.getStories();

		// Assert
		assertEquals(3, stories.size());

		for (int i = 0; i < stories.size(); i++) {
			assertEquals(storyName + (i + 1), stories.get(i).getName());
			assertEquals(storyNotes + (i + 1), stories.get(i).getNotes());
			assertEquals(storyHowtodemo + (i + 1), stories.get(i)
					.getHowToDemo());
			assertEquals(storyEstimate, stories.get(i).getEstimate());
			assertEquals(storyImportance, stories.get(i).getImportance());
			assertEquals(StoryObject.STATUS_UNCHECK, stories.get(i).getStatus());
			assertEquals(sprint.getId(), stories.get(i).getSprintId());
		}
	}

	@Test
	public void testGetDoneStoryByDate() throws Exception {
		// 新增三筆 sprints
		mCS = new CreateSprint(3, mCP);
		mCS.exe();
		// 每個Sprint中新增2筆Story
		AddStoryToSprint ASS = new AddStoryToSprint(2, 1, mCS, mCP, "EST");
		ASS.exe();

		long releaseId = 1;
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(
				mProject);
		ReleaseObject release = ReleaseObject.get(releaseId);

		ArrayList<StoryObject> stories = productBacklogLogic.getStories();
		ArrayList<Long> storyIdList = new ArrayList<Long>();
		for (StoryObject story : stories) {
			storyIdList.add(story.getId());
		}
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(
				mProject, mCS.getSprintsId().get(0));
		for (int i = 0; i < stories.size(); i++) {
			// 把除了最後一筆 story 以外的 story 都設成 done
			if (stories.get(i).getId() != stories.size()) {
				sprintBacklogLogic.closeStory(stories.get(i).getId(), stories
						.get(i).getName(), stories.get(i).getNotes(),
						"2015/02/03-16:00:00");
			}
		}
		assertEquals(1.0, release.getReleaseAllStoryDone());
	}
	
	@Test
	public void testContains() {
		ReleaseObject release = new ReleaseObject(mProject.getId());
		release.setStartDate("2015/08/24");
		release.setDueDate("2015/08/31");
		release.save();
		assertFalse(release.contains(DateUtil.dayFilter("2015/08/23")));
		assertTrue(release.contains(DateUtil.dayFilter("2015/08/24")));
		assertTrue(release.contains(DateUtil.dayFilter("2015/08/25")));
		assertTrue(release.contains(DateUtil.dayFilter("2015/08/26")));
		assertTrue(release.contains(DateUtil.dayFilter("2015/08/27")));
		assertTrue(release.contains(DateUtil.dayFilter("2015/08/28")));
		assertTrue(release.contains(DateUtil.dayFilter("2015/08/29")));
		assertTrue(release.contains(DateUtil.dayFilter("2015/08/30")));
		assertTrue(release.contains(DateUtil.dayFilter("2015/08/31")));
		assertFalse(release.contains(DateUtil.dayFilter("2015/09/01")));
	}

	private ReleaseObject createRelease() {
		// Test Data
		String releaseName = "TEST_RELEASE_NAME";
		String releaseDescription = "TEST_RELEASE_DESCRIPTION";
		String releaseStartDate = "2015/08/03";
		String releaseDueDate = "2015/10/31";

		// Create release object
		ReleaseObject release = new ReleaseObject(mProject.getId());
		release.setName(releaseName).setDescription(releaseDescription)
				.setStartDate(releaseStartDate).setDueDate(releaseDueDate)
				.save();

		// assert
		assertNotSame(-1, release.getId());
		assertEquals(mProject.getId(), release.getProjectId());
		assertEquals(releaseName, release.getName());
		assertEquals(releaseDescription, release.getDescription());
		assertEquals(releaseStartDate, release.getStartDateString());
		assertEquals(releaseDueDate, release.getDueDateString());
		return release;
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
