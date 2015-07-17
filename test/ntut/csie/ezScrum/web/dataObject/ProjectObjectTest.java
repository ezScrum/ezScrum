package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProjectObjectTest {
	private Configuration mConfig;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
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
	}

	@Test
	public void testCreateProject() {
		String name = "testProject";
		String displayName = "testDisplayName";
		String comment = "comment";
		String productOwner = "PO";
		long attachFileSize = 2;

		ProjectObject project = new ProjectObject(name);
		project.setDisplayName(displayName).setComment(comment)
				.setManager(productOwner).setAttachFileSize(attachFileSize)
				.save();

		assertEquals(1, project.getId());
	}

	@Test
	public void testDeleteProject() {
		String name = "testProject";
		String displayName = "testDisplayName";
		String comment = "comment";
		String productOwner = "PO";
		long attachFileSize = 2;

		ProjectObject project = new ProjectObject(name);
		project.setDisplayName(displayName).setComment(comment)
				.setManager(productOwner).setAttachFileSize(attachFileSize)
				.save();
		project.reload();

		assertNotSame(-1, project.getId());
	}

	@Test
	public void testUpdateProject() {
		String name = "testProject";
		String displayName = "testDisplayName";
		String comment = "comment";
		String updateComment = "update comment";
		String productOwner = "PO";
		long attachFileSize = 2;

		ProjectObject project = new ProjectObject(name);
		project.setDisplayName(displayName).setComment(comment)
				.setManager(productOwner).setAttachFileSize(attachFileSize)
				.save();

		project.setComment(updateComment).save();
		project.reload();

		assertNotSame(-1, project.getId());
		assertEquals(updateComment, project.getComment());
	}

	@Test
	public void testGetProjectList() {
		String name = "testProject";
		String displayName = "testDisplayName";
		String comment = "comment";
		String updateComment = "update comment";
		String productOwner = "PO";
		long attachFileSize = 2;

		ProjectObject project = new ProjectObject(name);
		project.setDisplayName(displayName).setComment(comment)
				.setManager(productOwner).setAttachFileSize(attachFileSize)
				.save();

		project.setComment(updateComment).save();
		project.reload();

		ArrayList<ProjectObject> projects = ProjectObject.getAllProjects();

		assertEquals(1, projects.size());
	}

	@Test
	public void testGetProject() {
		String name = "testProject";
		String displayName = "testDisplayName";
		String comment = "comment";
		String productOwner = "PO";
		long attachFileSize = 2;

		ProjectObject project = new ProjectObject(name);
		project.setDisplayName(displayName).setComment(comment)
				.setManager(productOwner).setAttachFileSize(attachFileSize)
				.save();
		project.reload();

		ProjectObject theProject = ProjectObject.get(project.getId());

		assertEquals(name, theProject.getName());
	}

	@Test
	public void testGetProjectByName() {
		String name = "testProject";
		String displayName = "testDisplayName";
		String comment = "comment";
		String productOwner = "PO";
		long attachFileSize = 2;

		ProjectObject project = new ProjectObject(name);
		project.setDisplayName(displayName).setComment(comment)
				.setManager(productOwner).setAttachFileSize(attachFileSize)
				.save();

		ProjectObject theProject = ProjectObject.get(name);

		assertEquals(name, theProject.getName());
		assertEquals(displayName, theProject.getDisplayName());
		assertEquals(comment, theProject.getComment());
		assertEquals(productOwner, theProject.getManager());
		assertEquals(attachFileSize, theProject.getAttachFileSize());
	}

	@Test
	public void testGetProjectMemberList() throws Exception {
		/**
		 * set up a project and a user
		 */
		ProjectObject project = new ProjectObject("name");
		project.setDisplayName("name").setComment("comment")
				.setManager("PO_YC").setAttachFileSize(2).save();
		project.reload();
		String userName = "account";
		String nickName = "user name";
		String password = "password";
		String email = "email";
		boolean enable = true;
		// create account
		AccountObject account = new AccountObject(userName);
		account.setNickName(nickName);
		account.setPassword(password);
		account.setEmail(email);
		account.setEnable(enable);
		account.save();
		account.reload();

		// create project role
		boolean result = account.createProjectRole(project.getId(),
				RoleEnum.ProductOwner);
		assertTrue(result);

		// GetProjectMemberList
		List<AccountObject> userList = project.getProjectMembers();

		assertEquals(1, userList.size());
	}

	@Test
	public void testGetCurrentSprint_WithNoSprint() {
		ProjectObject project = new ProjectObject(
				"testGetCurrentSprint_WithNoSprint");
		project.setDisplayName("testGetCurrentSprint_WithNoSprint")
				.setAttachFileSize(2).save();

		// 取得目前時間所在的 sprint
		SprintObject sprint = project.getCurrentSprint();
		assertNull(sprint);
	}

	@Test
	public void testGetCurrentSprint_WithThreeSprint() {
		// create project
		ProjectObject project = new ProjectObject(
				"testGetCurrentSprint_WithThreeSprint");
		project.setDisplayName("testGetCurrentSprint_WithThreeSprint")
				.setAttachFileSize(2).save();

		// sprint info
		String sprintGoal = "TEST_SPRINT_GOAL";
		String sprintDailyInfo = "TEST_SPRINT_DAILY_INFO";
		String sprintDemoPlace = "TEST_SPRINT_DEMO_PLACE";
		int sprintInterval = 2;
		int membersNumbre = 4;
		int hoursCanCommit = 150;
		int focusFactor = 80;
		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();

		// create sprint 1
		SprintObject sprint1 = new SprintObject(project.getId());
		sprint1.setInterval(sprintInterval).setMembers(membersNumbre)
				.setHoursCanCommit(hoursCanCommit).setFocusFactor(focusFactor)
				.setSprintGoal(sprintGoal).setStartDate(getDate(today, -1))
				.setDueDate(getDate(today, 12)).setDailyInfo(sprintDailyInfo)
				.setDemoDate(getDate(today, 12)).setDemoPlace(sprintDemoPlace)
				.save();
		// create sprint 2
		SprintObject sprint2 = new SprintObject(project.getId());
		sprint2.setInterval(sprintInterval).setMembers(membersNumbre)
				.setHoursCanCommit(hoursCanCommit).setFocusFactor(focusFactor)
				.setSprintGoal(sprintGoal).setStartDate(getDate(today, -15))
				.setDueDate(getDate(today, -2)).setDailyInfo(sprintDailyInfo)
				.setDemoDate(getDate(today, -2)).setDemoPlace(sprintDemoPlace)
				.save();
		// create sprint 3
		SprintObject sprint3 = new SprintObject(project.getId());
		sprint3.setInterval(sprintInterval).setMembers(membersNumbre)
				.setHoursCanCommit(hoursCanCommit).setFocusFactor(focusFactor)
				.setSprintGoal(sprintGoal).setStartDate(getDate(today, -29))
				.setDueDate(getDate(today, -16)).setDailyInfo(sprintDailyInfo)
				.setDemoDate(getDate(today, -16)).setDemoPlace(sprintDemoPlace)
				.save();
		// 取得目前時間所在的 sprint
		assertTrue(sprint1.contains(today));
		assertFalse(sprint2.contains(today));
		assertFalse(sprint3.contains(today));
		SprintObject sprint = project.getCurrentSprint();
		assertEquals(getDate(today, -1), sprint.getStartDateString());
		assertEquals(getDate(today, 12), sprint.getDueDateString());
		assertEquals(getDate(today, 12), sprint.getDemoDateString());
		assertTrue(sprint.contains(today));
	}

	@Test
	public void testGetCurrentSprint_NotInThreeSprints() {
		// create project
		ProjectObject project = new ProjectObject(
				"testGetCurrentSprint_NotInThreeSprints");
		project.setDisplayName("testGetCurrentSprint_NotInThreeSprints")
				.setAttachFileSize(2).save();

		// sprint info
		String sprintGoal = "TEST_SPRINT_GOAL";
		String sprintDailyInfo = "TEST_SPRINT_DAILY_INFO";
		String sprintDemoPlace = "TEST_SPRINT_DEMO_PLACE";
		int sprintInterval = 2;
		int membersNumbre = 4;
		int hoursCanCommit = 150;
		int focusFactor = 80;
		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();

		// create sprint 1
		SprintObject sprint1 = new SprintObject(project.getId());
		sprint1.setInterval(sprintInterval).setMembers(membersNumbre)
				.setHoursCanCommit(hoursCanCommit).setFocusFactor(focusFactor)
				.setSprintGoal(sprintGoal).setStartDate(getDate(today, 1))
				.setDueDate(getDate(today, 14)).setDailyInfo(sprintDailyInfo)
				.setDemoDate(getDate(today, 14)).setDemoPlace(sprintDemoPlace)
				.save();
		// create sprint 2
		SprintObject sprint2 = new SprintObject(project.getId());
		sprint2.setInterval(sprintInterval).setMembers(membersNumbre)
				.setHoursCanCommit(hoursCanCommit).setFocusFactor(focusFactor)
				.setSprintGoal(sprintGoal).setStartDate(getDate(today, 15))
				.setDueDate(getDate(today, 28)).setDailyInfo(sprintDailyInfo)
				.setDemoDate(getDate(today, 28)).setDemoPlace(sprintDemoPlace)
				.save();
		// create sprint 3
		SprintObject sprint3 = new SprintObject(project.getId());
		sprint3.setInterval(sprintInterval).setMembers(membersNumbre)
				.setHoursCanCommit(hoursCanCommit).setFocusFactor(focusFactor)
				.setSprintGoal(sprintGoal).setStartDate(getDate(today, 29))
				.setDueDate(getDate(today, 42)).setDailyInfo(sprintDailyInfo)
				.setDemoDate(getDate(today, 42)).setDemoPlace(sprintDemoPlace)
				.save();
		// 取得目前時間所在的 sprint
		assertFalse(sprint1.contains(today));
		assertFalse(sprint2.contains(today));
		assertFalse(sprint3.contains(today));
		SprintObject sprint = project.getCurrentSprint();
		assertEquals(getDate(today, 29), sprint.getStartDateString());
		assertEquals(getDate(today, 42), sprint.getDueDateString());
		assertEquals(getDate(today, 42), sprint.getDemoDateString());
	}

	@Test
	public void testGetLatestSprint_WithNoSprint() {
		ProjectObject project = new ProjectObject(
				"testGetLatestSprint_WithNoSprint");
		project.setDisplayName("testGetLatestSprint_WithNoSprint")
				.setAttachFileSize(2).save();

		// 取得目前時間所在的 sprint
		SprintObject sprint = project.getLatestSprint();
		assertNull(sprint);
	}

	@Test
	public void testGetLatestSprint_WithThreeSprints() {
		// create project
		ProjectObject project = new ProjectObject(
				"testGetCurrentSprint_WithThreeSprint");
		project.setDisplayName("testGetCurrentSprint_WithThreeSprint")
				.setAttachFileSize(2).save();

		// sprint info
		String sprintGoal = "TEST_SPRINT_GOAL";
		String sprintDailyInfo = "TEST_SPRINT_DAILY_INFO";
		String sprintDemoPlace = "TEST_SPRINT_DEMO_PLACE";
		int sprintInterval = 2;
		int membersNumbre = 4;
		int hoursCanCommit = 150;
		int focusFactor = 80;
		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();

		// create sprint 1
		SprintObject sprint1 = new SprintObject(project.getId());
		sprint1.setInterval(sprintInterval).setMembers(membersNumbre)
				.setHoursCanCommit(hoursCanCommit).setFocusFactor(focusFactor)
				.setSprintGoal(sprintGoal).setStartDate(getDate(today, -1))
				.setDueDate(getDate(today, 12)).setDailyInfo(sprintDailyInfo)
				.setDemoDate(getDate(today, 12)).setDemoPlace(sprintDemoPlace)
				.save();
		// create sprint 2
		SprintObject sprint2 = new SprintObject(project.getId());
		sprint2.setInterval(sprintInterval).setMembers(membersNumbre)
				.setHoursCanCommit(hoursCanCommit).setFocusFactor(focusFactor)
				.setSprintGoal(sprintGoal).setStartDate(getDate(today, -15))
				.setDueDate(getDate(today, -2)).setDailyInfo(sprintDailyInfo)
				.setDemoDate(getDate(today, -2)).setDemoPlace(sprintDemoPlace)
				.save();
		// create sprint 3
		SprintObject sprint3 = new SprintObject(project.getId());
		sprint3.setInterval(sprintInterval).setMembers(membersNumbre)
				.setHoursCanCommit(hoursCanCommit).setFocusFactor(focusFactor)
				.setSprintGoal(sprintGoal).setStartDate(getDate(today, -29))
				.setDueDate(getDate(today, -16)).setDailyInfo(sprintDailyInfo)
				.setDemoDate(getDate(today, -16)).setDemoPlace(sprintDemoPlace)
				.save();
		// 取得目前時間所在的 sprint
		SprintObject sprint = project.getLatestSprint();
		assertEquals(sprint3.getStartDateString(), sprint.getStartDateString());
		assertEquals(sprint3.getDueDateString(), sprint.getDueDateString());
		assertEquals(sprint3.getDemoDateString(), sprint.getDemoDateString());
		assertEquals(sprint3.getId(), sprint.getId());
	}

	private String getDate(Date date, int duration) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTime(date);
		calendarEnd.add(Calendar.DAY_OF_YEAR, duration);
		return format.format(calendarEnd.getTime());
	}
}
