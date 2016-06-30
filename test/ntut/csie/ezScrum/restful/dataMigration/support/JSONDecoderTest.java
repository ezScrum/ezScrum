package ntut.csie.ezScrum.restful.dataMigration.support;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.AccountJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.AttachFileJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.HistoryJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ProjectJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ReleaseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.RetrospectiveJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ScrumRoleJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.SprintJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.StoryJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TagJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TaskJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.UnplanJSONEnum;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;

public class JSONDecoderTest {
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

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		mConfig = null;
	}
	
	@Test
	public void testToProject() throws JSONException {
		JSONObject projectJSON = new JSONObject();
		projectJSON.put(ProjectJSONEnum.NAME, "name");
		projectJSON.put(ProjectJSONEnum.DISPLAY_NAME, "display name");
		projectJSON.put(ProjectJSONEnum.COMMENT, "comment");
		projectJSON.put(ProjectJSONEnum.PRODUCT_OWNER, "PO");
		projectJSON.put(ProjectJSONEnum.CREATE_TIME, System.currentTimeMillis());
		ProjectObject project = JSONDecoder.toProject(projectJSON.toString());
		assertEquals(projectJSON.getString(ProjectJSONEnum.NAME), project.getName());
		assertEquals(projectJSON.getString(ProjectJSONEnum.DISPLAY_NAME), project.getDisplayName());
		assertEquals(projectJSON.getString(ProjectJSONEnum.COMMENT), project.getComment());
		assertEquals(projectJSON.getString(ProjectJSONEnum.PRODUCT_OWNER), project.getManager());
		assertEquals(projectJSON.getLong(ProjectJSONEnum.CREATE_TIME), project.getCreateTime());
	}
	
	@Test
	public void testToRelease() throws JSONException {
		long projectId = 1;
		JSONObject releaseJSON = new JSONObject();
		releaseJSON.put(ReleaseJSONEnum.NAME, "name");
		releaseJSON.put(ReleaseJSONEnum.DESCRIPTION, "description");
		releaseJSON.put(ReleaseJSONEnum.START_DATE, "2015/12/06");
		releaseJSON.put(ReleaseJSONEnum.END_DATE, "2015/12/31");
		ReleaseObject release = JSONDecoder.toRelease(projectId, releaseJSON.toString());
		assertEquals(releaseJSON.getString(ReleaseJSONEnum.NAME), release.getName());
		assertEquals(releaseJSON.getString(ReleaseJSONEnum.DESCRIPTION), release.getDescription());
		assertEquals(releaseJSON.getString(ReleaseJSONEnum.START_DATE), release.getStartDateString());
		assertEquals(releaseJSON.getString(ReleaseJSONEnum.END_DATE), release.getEndDateString());
		assertEquals(projectId, release.getProjectId());
	}
	
	@Test
	public void testToScrumRoles() throws JSONException {
		String projectName = "project";
		JSONObject scrumRolesJSON = new JSONObject();
		JSONObject productOwnerJSON = new JSONObject();
		JSONObject scrumMasterJSON = new JSONObject();
		JSONObject scrumTeamJSON = new JSONObject();
		JSONObject stakeholderJSON = new JSONObject();
		JSONObject guestJSON = new JSONObject();
		scrumRolesJSON.put(ScrumRoleJSONEnum.PRODUCT_OWNER, productOwnerJSON);
		scrumRolesJSON.put(ScrumRoleJSONEnum.SCRUM_MASTER, scrumMasterJSON);
		scrumRolesJSON.put(ScrumRoleJSONEnum.SCRUM_TEAM, scrumTeamJSON);
		scrumRolesJSON.put(ScrumRoleJSONEnum.STAKEHOLDER, stakeholderJSON);
		scrumRolesJSON.put(ScrumRoleJSONEnum.GUEST, guestJSON);
		Iterator<?> iterator = scrumRolesJSON.keys();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			JSONObject roleJSON = scrumRolesJSON.getJSONObject(key);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG, true);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN, true);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE, true);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG, true);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN, true);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_TASKBOARD, true);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_UNPLAN, true);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT, true);
			roleJSON.put(ScrumRoleJSONEnum.ACCESS_REPORT, true);
		}
		ArrayList<ScrumRole> scrumRoles = JSONDecoder.toScrumRoles(projectName, scrumRolesJSON.toString());
		ScrumRole productOwner = scrumRoles.get(0);
		ScrumRole scrumMaster = scrumRoles.get(1);
		ScrumRole scrumTeam = scrumRoles.get(2);
		ScrumRole stakeholder = scrumRoles.get(3);
		ScrumRole guest = scrumRoles.get(4);
		
		// Assert Product Owner permission
		assertEquals(productOwner.getAccessProductBacklog(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG));
		assertEquals(productOwner.getAccessSprintPlan(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN));
		assertEquals(productOwner.getAccessTaskBoard(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD));
		assertEquals(productOwner.getAccessSprintBacklog(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG));
		assertEquals(productOwner.getAccessReleasePlan(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN));
		assertEquals(productOwner.getAccessRetrospective(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE));
		assertEquals(productOwner.getAccessUnplanItem(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLAN));
		assertEquals(productOwner.getAccessReport(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT));
		assertEquals(productOwner.getAccessEditProject(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT));
		assertEquals(projectName, productOwner.getProjectName());
		
		// Assert Scrum Master permission
		assertEquals(scrumMaster.getAccessProductBacklog(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG));
		assertEquals(scrumMaster.getAccessSprintPlan(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN));
		assertEquals(scrumMaster.getAccessTaskBoard(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD));
		assertEquals(scrumMaster.getAccessSprintBacklog(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG));
		assertEquals(scrumMaster.getAccessReleasePlan(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN));
		assertEquals(scrumMaster.getAccessRetrospective(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE));
		assertEquals(scrumMaster.getAccessUnplanItem(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLAN));
		assertEquals(scrumMaster.getAccessReport(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT));
		assertEquals(scrumMaster.getAccessEditProject(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT));
		assertEquals(projectName, scrumMaster.getProjectName());
		
		// Assert Scrum Team permission
		assertEquals(scrumTeam.getAccessProductBacklog(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG));
		assertEquals(scrumTeam.getAccessSprintPlan(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN));
		assertEquals(scrumTeam.getAccessTaskBoard(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD));
		assertEquals(scrumTeam.getAccessSprintBacklog(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG));
		assertEquals(scrumTeam.getAccessReleasePlan(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN));
		assertEquals(scrumTeam.getAccessRetrospective(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE));
		assertEquals(scrumTeam.getAccessUnplanItem(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLAN));
		assertEquals(scrumTeam.getAccessReport(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT));
		assertEquals(scrumTeam.getAccessEditProject(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT));
		assertEquals(projectName, scrumTeam.getProjectName());
		
		// Assert Stakeholder permission
		assertEquals(stakeholder.getAccessProductBacklog(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG));
		assertEquals(stakeholder.getAccessSprintPlan(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN));
		assertEquals(stakeholder.getAccessTaskBoard(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD));
		assertEquals(stakeholder.getAccessSprintBacklog(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG));
		assertEquals(stakeholder.getAccessReleasePlan(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN));
		assertEquals(stakeholder.getAccessRetrospective(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE));
		assertEquals(stakeholder.getAccessUnplanItem(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLAN));
		assertEquals(stakeholder.getAccessReport(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT));
		assertEquals(stakeholder.getAccessEditProject(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT));
		assertEquals(projectName, stakeholder.getProjectName());
		
		// Assert Guest permission
		assertEquals(guest.getAccessProductBacklog(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG));
		assertEquals(guest.getAccessSprintPlan(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN));
		assertEquals(guest.getAccessTaskBoard(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD));
		assertEquals(guest.getAccessSprintBacklog(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG));
		assertEquals(guest.getAccessReleasePlan(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN));
		assertEquals(guest.getAccessRetrospective(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE));
		assertEquals(guest.getAccessUnplanItem(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLAN));
		assertEquals(guest.getAccessReport(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT));
		assertEquals(guest.getAccessEditProject(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT));
		assertEquals(projectName, guest.getProjectName());
	}
	
	@Test
	public void testToTag() throws JSONException {
		long projectId = 1;
		JSONObject tagJSON = new JSONObject();
		tagJSON.put(TagJSONEnum.NAME, "tag01");
		TagObject tag = JSONDecoder.toTag(projectId, tagJSON.toString());
		assertEquals(tagJSON.getString(TagJSONEnum.NAME), tag.getName());
		assertEquals(projectId, tag.getProjectId());
	}
	
	@Test
	public void testToTagInStory() throws JSONException {
		String name = "testProject";
		String displayName = "testDisplayName";
		String comment = "comment";
		String productOwner = "PO";
		long attachFileSize = 2;
		String tagName = "Data Migration";

		ProjectObject project = new ProjectObject(name);
		project.setDisplayName(displayName).setComment(comment)
				.setManager(productOwner).setAttachFileSize(attachFileSize)
				.save();
		TagObject tag = new TagObject(tagName, project.getId()); 
		tag.save();
		JSONObject tagJSON = new JSONObject();
		tagJSON.put(TagJSONEnum.NAME, tagName);
		TagObject tagResult = JSONDecoder.toTagInStory(tagJSON.toString());
		assertEquals(tagJSON.getString(TagJSONEnum.NAME), tagResult.getName());
		assertEquals(tag.getId(), tagResult.getId());
	}
	
	@Test
	public void testToAccount() throws JSONException {
		JSONObject accountJSON = new JSONObject();
		accountJSON.put(AccountJSONEnum.USERNAME, "account");
		accountJSON.put(AccountJSONEnum.NICK_NAME, "account");
		accountJSON.put(AccountJSONEnum.PASSWORD, "account");
		accountJSON.put(AccountJSONEnum.EMAIL, "account@gamil.com");
		accountJSON.put(AccountJSONEnum.ENABLE, true);
		AccountObject account = JSONDecoder.toAccount(accountJSON.toString());
		assertEquals(accountJSON.getString(AccountJSONEnum.USERNAME), account.getUsername());
		assertEquals(accountJSON.getString(AccountJSONEnum.NICK_NAME), account.getNickName());
		assertEquals(accountJSON.getString(AccountJSONEnum.PASSWORD), account.getPassword());
		assertEquals(accountJSON.getString(AccountJSONEnum.EMAIL), account.getEmail());
		assertEquals(accountJSON.getBoolean(AccountJSONEnum.ENABLE), account.getEnable());
	}
	
	@Test
	public void testToSprint() throws JSONException {
		long projectId = 1;
		JSONObject sprintJSON = new JSONObject();
		sprintJSON.put(SprintJSONEnum.GOAL, "sprint goal");
		sprintJSON.put(SprintJSONEnum.INTERVAL, 2);
		sprintJSON.put(SprintJSONEnum.TEAM_SIZE, 5);
		sprintJSON.put(SprintJSONEnum.AVAILABLE_HOURS, 100);
		sprintJSON.put(SprintJSONEnum.FOCUS_FACTOR, 70);
		sprintJSON.put(SprintJSONEnum.START_DATE, "2015/11/27");
		sprintJSON.put(SprintJSONEnum.END_DATE, "2015/12/10");
		sprintJSON.put(SprintJSONEnum.DEMO_DATE, "2015/12/10");
		sprintJSON.put(SprintJSONEnum.DEMO_PLACE, "Lab1321");
		sprintJSON.put(SprintJSONEnum.DAILY_INFO, "12:50@Lab1321");
		SprintObject sprint = JSONDecoder.toSprint(projectId, sprintJSON.toString());
		assertEquals(sprintJSON.getString(SprintJSONEnum.GOAL), sprint.getGoal());
		assertEquals(sprintJSON.getInt(SprintJSONEnum.INTERVAL), sprint.getInterval());
		assertEquals(sprintJSON.getInt(SprintJSONEnum.TEAM_SIZE), sprint.getTeamSize());
		assertEquals(sprintJSON.getInt(SprintJSONEnum.AVAILABLE_HOURS), sprint.getAvailableHours());
		assertEquals(sprintJSON.getInt(SprintJSONEnum.FOCUS_FACTOR), sprint.getFocusFactor());
		assertEquals(sprintJSON.getString(SprintJSONEnum.START_DATE), sprint.getStartDateString());
		assertEquals(sprintJSON.getString(SprintJSONEnum.END_DATE), sprint.getEndDateString());
		assertEquals(sprintJSON.getString(SprintJSONEnum.DEMO_DATE), sprint.getDemoDateString());
		assertEquals(sprintJSON.getString(SprintJSONEnum.DEMO_PLACE), sprint.getDemoPlace());
		assertEquals(sprintJSON.getString(SprintJSONEnum.DAILY_INFO), sprint.getDailyInfo());
		assertEquals(projectId, sprint.getProjectId());
	}
	
	@Test
	public void testToStory() throws JSONException {
		long projectId = 1;
		long sprintId = 2;
		JSONObject storyJSON = new JSONObject();
		storyJSON.put(StoryJSONEnum.NAME, "name");
		storyJSON.put(StoryJSONEnum.STATUS, "new");
		storyJSON.put(StoryJSONEnum.ESTIMATE, 1);
		storyJSON.put(StoryJSONEnum.IMPORTANCE, 2);
		storyJSON.put(StoryJSONEnum.VALUE, 3);
		storyJSON.put(StoryJSONEnum.NOTES, "notes");
		storyJSON.put(StoryJSONEnum.HOW_TO_DEMO, "how to demo");
		StoryObject story = JSONDecoder.toStory(projectId, sprintId, storyJSON.toString());
		assertEquals(storyJSON.getString(StoryJSONEnum.NAME), story.getName());
		assertEquals(StoryObject.STATUS_UNCHECK, story.getStatus());
		assertEquals(storyJSON.getInt(StoryJSONEnum.ESTIMATE), story.getEstimate());
		assertEquals(storyJSON.getInt(StoryJSONEnum.IMPORTANCE), story.getImportance());
		assertEquals(storyJSON.getInt(StoryJSONEnum.VALUE), story.getValue());
		assertEquals(storyJSON.getString(StoryJSONEnum.NOTES), story.getNotes());
		assertEquals(storyJSON.getString(StoryJSONEnum.HOW_TO_DEMO), story.getHowToDemo());
		assertEquals(projectId, story.getProjectId());
		assertEquals(sprintId, story.getSprintId());
	}
	
	@Test
	public void testToTask() throws JSONException {
		long projectId = 1;
		long storyId = 2;
		
		AccountObject account1 = new AccountObject("account1");
		account1.setNickName("account1");
		account1.setPassword("account1");
		account1.setEmail("account1@gmail.com");
		account1.setEnable(true);
		account1.save();
		
		AccountObject account2 = new AccountObject("account2");
		account2.setNickName("account2");
		account2.setPassword("account2");
		account2.setEmail("account2@gmail.com");
		account2.setEnable(true);
		account2.save();
		
		JSONObject taskJSON = new JSONObject();
		taskJSON.put(TaskJSONEnum.NAME, "name");
		taskJSON.put(TaskJSONEnum.HANDLER, "account1");
		taskJSON.put(TaskJSONEnum.ESTIMATE, 1);
		taskJSON.put(TaskJSONEnum.REMAIN, 2);
		taskJSON.put(TaskJSONEnum.ACTUAL, 3);
		taskJSON.put(TaskJSONEnum.NOTES, "notes");
		taskJSON.put(TaskJSONEnum.STATUS, "assigned");
		JSONArray partnerJSONArray = new JSONArray();
		taskJSON.put(TaskJSONEnum.PARTNERS, partnerJSONArray);
		JSONObject partnerJSON = new JSONObject();
		partnerJSON.put(AccountJSONEnum.USERNAME, "account2");
		partnerJSONArray.put(partnerJSON);
		
		TaskObject task = JSONDecoder.toTask(projectId, storyId, taskJSON.toString());
		assertEquals(taskJSON.getString(TaskJSONEnum.NAME), task.getName());
		assertEquals(taskJSON.getString(TaskJSONEnum.HANDLER), task.getHandler().getUsername());
		assertEquals(taskJSON.getInt(TaskJSONEnum.ESTIMATE), task.getEstimate());
		assertEquals(taskJSON.getInt(TaskJSONEnum.REMAIN), task.getRemains());
		assertEquals(taskJSON.getInt(TaskJSONEnum.ACTUAL), task.getActual());
		assertEquals(taskJSON.getString(TaskJSONEnum.NOTES), task.getNotes());
		assertEquals(TaskObject.STATUS_CHECK, task.getStatus());
		assertEquals(projectId, task.getProjectId());
		assertEquals(storyId, task.getStoryId());
	}
	
	@Test
	public void testToUnplan() throws JSONException {
		long projectId = 1;
		long sprintId = 2;
		
		AccountObject account1 = new AccountObject("account1");
		account1.setNickName("account1");
		account1.setPassword("account1");
		account1.setEmail("account1@gmail.com");
		account1.setEnable(true);
		account1.save();
		
		AccountObject account2 = new AccountObject("account2");
		account2.setNickName("account2");
		account2.setPassword("account2");
		account2.setEmail("account2@gmail.com");
		account2.setEnable(true);
		account2.save();
		
		JSONObject unplanJSON = new JSONObject();
		unplanJSON.put(UnplanJSONEnum.NAME, "name");
		unplanJSON.put(UnplanJSONEnum.HANDLER, "account1");
		unplanJSON.put(UnplanJSONEnum.ESTIMATE, 1);
		unplanJSON.put(UnplanJSONEnum.ACTUAL, 3);
		unplanJSON.put(UnplanJSONEnum.NOTES, "notes");
		unplanJSON.put(UnplanJSONEnum.STATUS, "assigned");
		JSONArray partnerJSONArray = new JSONArray();
		unplanJSON.put(TaskJSONEnum.PARTNERS, partnerJSONArray);
		JSONObject partnerJSON = new JSONObject();
		partnerJSON.put(AccountJSONEnum.USERNAME, "account2");
		partnerJSONArray.put(partnerJSON);
		
		UnplanObject unplan = JSONDecoder.toUnplan(projectId, sprintId, unplanJSON.toString());
		assertEquals(unplanJSON.getString(UnplanJSONEnum.NAME), unplan.getName());
		assertEquals(unplanJSON.getString(UnplanJSONEnum.HANDLER), unplan.getHandler().getUsername());
		assertEquals(unplanJSON.getInt(UnplanJSONEnum.ESTIMATE), unplan.getEstimate());
		assertEquals(unplanJSON.getInt(UnplanJSONEnum.ACTUAL), unplan.getActual());
		assertEquals(unplanJSON.getString(UnplanJSONEnum.NOTES), unplan.getNotes());
		assertEquals(UnplanObject.STATUS_CHECK, unplan.getStatus());
		assertEquals(projectId, unplan.getProjectId());
		assertEquals(sprintId, unplan.getSprintId());
	}
	
	@Test
	public void testToHistory_StoryStatus_UncheckToDone() throws JSONException {
		long issueId = 1;
		int issueType = IssueTypeEnum.TYPE_STORY;
		JSONObject historyJSON = new JSONObject();
		historyJSON.put(HistoryJSONEnum.HISTORY_TYPE, "STATUS");
		historyJSON.put(HistoryJSONEnum.OLD_VALUE, "new");
		historyJSON.put(HistoryJSONEnum.NEW_VALUE, "closed");
		historyJSON.put(HistoryJSONEnum.CREATE_TIME, System.currentTimeMillis());
		HistoryObject history = JSONDecoder.toHistory(issueId, issueType, historyJSON.toString());
		assertEquals(HistoryTypeTranslator.getHistoryType(historyJSON.getString(HistoryJSONEnum.HISTORY_TYPE)), history.getHistoryType());
		assertEquals(String.valueOf(StoryObject.STATUS_UNCHECK), history.getOldValue());
		assertEquals(String.valueOf(StoryObject.STATUS_DONE), history.getNewValue());
		assertEquals(historyJSON.getLong(HistoryJSONEnum.CREATE_TIME), history.getCreateTime());
		assertEquals(issueId, history.getIssueId());
		assertEquals(issueType, history.getIssueType());
	}
	
	@Test
	public void testToHistory_TaskStatus_UncheckToCheck() throws JSONException {
		long issueId = 1;
		int issueType = IssueTypeEnum.TYPE_TASK;
		JSONObject historyJSON = new JSONObject();
		historyJSON.put(HistoryJSONEnum.HISTORY_TYPE, "STATUS");
		historyJSON.put(HistoryJSONEnum.OLD_VALUE, "new");
		historyJSON.put(HistoryJSONEnum.NEW_VALUE, "assigned");
		historyJSON.put(HistoryJSONEnum.CREATE_TIME, System.currentTimeMillis());
		HistoryObject history = JSONDecoder.toHistory(issueId, issueType, historyJSON.toString());
		assertEquals(HistoryTypeTranslator.getHistoryType(historyJSON.getString(HistoryJSONEnum.HISTORY_TYPE)), history.getHistoryType());
		assertEquals(String.valueOf(TaskObject.STATUS_UNCHECK), history.getOldValue());
		assertEquals(String.valueOf(TaskObject.STATUS_CHECK), history.getNewValue());
		assertEquals(historyJSON.getLong(HistoryJSONEnum.CREATE_TIME), history.getCreateTime());
		assertEquals(issueId, history.getIssueId());
		assertEquals(issueType, history.getIssueType());
	}
	
	@Test
	public void testToHistory_TaskStatus_CheckToDone() throws JSONException {
		long issueId = 1;
		int issueType = IssueTypeEnum.TYPE_TASK;
		JSONObject historyJSON = new JSONObject();
		historyJSON.put(HistoryJSONEnum.HISTORY_TYPE, "STATUS");
		historyJSON.put(HistoryJSONEnum.OLD_VALUE, "assigned");
		historyJSON.put(HistoryJSONEnum.NEW_VALUE, "closed");
		historyJSON.put(HistoryJSONEnum.CREATE_TIME, System.currentTimeMillis());
		HistoryObject history = JSONDecoder.toHistory(issueId, issueType, historyJSON.toString());
		assertEquals(HistoryTypeTranslator.getHistoryType(historyJSON.getString(HistoryJSONEnum.HISTORY_TYPE)), history.getHistoryType());
		assertEquals(String.valueOf(TaskObject.STATUS_CHECK), history.getOldValue());
		assertEquals(String.valueOf(TaskObject.STATUS_DONE), history.getNewValue());
		assertEquals(historyJSON.getLong(HistoryJSONEnum.CREATE_TIME), history.getCreateTime());
		assertEquals(issueId, history.getIssueId());
		assertEquals(issueType, history.getIssueType());
	}
	
	@Test
	public void testToHistory_UnplanStatus_UncheckToCheck() throws JSONException {
		long issueId = 1;
		int issueType = IssueTypeEnum.TYPE_UNPLAN;
		JSONObject historyJSON = new JSONObject();
		historyJSON.put(HistoryJSONEnum.HISTORY_TYPE, "STATUS");
		historyJSON.put(HistoryJSONEnum.OLD_VALUE, "new");
		historyJSON.put(HistoryJSONEnum.NEW_VALUE, "assigned");
		historyJSON.put(HistoryJSONEnum.CREATE_TIME, System.currentTimeMillis());
		HistoryObject history = JSONDecoder.toHistory(issueId, issueType, historyJSON.toString());
		assertEquals(HistoryTypeTranslator.getHistoryType(historyJSON.getString(HistoryJSONEnum.HISTORY_TYPE)), history.getHistoryType());
		assertEquals(String.valueOf(UnplanObject.STATUS_UNCHECK), history.getOldValue());
		assertEquals(String.valueOf(UnplanObject.STATUS_CHECK), history.getNewValue());
		assertEquals(historyJSON.getLong(HistoryJSONEnum.CREATE_TIME), history.getCreateTime());
		assertEquals(issueId, history.getIssueId());
		assertEquals(issueType, history.getIssueType());
	}
	
	@Test
	public void testToHistory_UnplanStatus_CheckToDone() throws JSONException {
		long issueId = 1;
		int issueType = IssueTypeEnum.TYPE_UNPLAN;
		JSONObject historyJSON = new JSONObject();
		historyJSON.put(HistoryJSONEnum.HISTORY_TYPE, "STATUS");
		historyJSON.put(HistoryJSONEnum.OLD_VALUE, "assigned");
		historyJSON.put(HistoryJSONEnum.NEW_VALUE, "closed");
		historyJSON.put(HistoryJSONEnum.CREATE_TIME, System.currentTimeMillis());
		HistoryObject history = JSONDecoder.toHistory(issueId, issueType, historyJSON.toString());
		assertEquals(HistoryTypeTranslator.getHistoryType(historyJSON.getString(HistoryJSONEnum.HISTORY_TYPE)), history.getHistoryType());
		assertEquals(String.valueOf(UnplanObject.STATUS_CHECK), history.getOldValue());
		assertEquals(String.valueOf(UnplanObject.STATUS_DONE), history.getNewValue());
		assertEquals(historyJSON.getLong(HistoryJSONEnum.CREATE_TIME), history.getCreateTime());
		assertEquals(issueId, history.getIssueId());
		assertEquals(issueType, history.getIssueType());
	}
	
	@Test
	public void testToRetrospective_GoodWithStatusNew() throws JSONException {
		long projectId = 1;
		long sprintId = 2;
		JSONObject retrospectiveJSON = new JSONObject();
		retrospectiveJSON.put(RetrospectiveJSONEnum.NAME, "name");
		retrospectiveJSON.put(RetrospectiveJSONEnum.DESCRIPTION, "description");
		retrospectiveJSON.put(RetrospectiveJSONEnum.TYPE, RetrospectiveObject.TYPE_GOOD);
		retrospectiveJSON.put(RetrospectiveJSONEnum.STATUS, RetrospectiveObject.STATUS_NEW);
		RetrospectiveObject retrospective = JSONDecoder.toRetrospective(projectId, sprintId, retrospectiveJSON.toString());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.NAME), retrospective.getName());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.DESCRIPTION), retrospective.getDescription());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.TYPE), retrospective.getType());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.STATUS), retrospective.getStatus());
		assertEquals(projectId, retrospective.getProjectId());
		assertEquals(sprintId, retrospective.getSprintId());
	}
	
	@Test
	public void testToRetrospective_GoodWithStatusAssigned() throws JSONException {
		long projectId = 1;
		long sprintId = 2;
		JSONObject retrospectiveJSON = new JSONObject();
		retrospectiveJSON.put(RetrospectiveJSONEnum.NAME, "name");
		retrospectiveJSON.put(RetrospectiveJSONEnum.DESCRIPTION, "description");
		retrospectiveJSON.put(RetrospectiveJSONEnum.TYPE, RetrospectiveObject.TYPE_GOOD);
		retrospectiveJSON.put(RetrospectiveJSONEnum.STATUS, RetrospectiveObject.STATUS_ASSIGNED);
		RetrospectiveObject retrospective = JSONDecoder.toRetrospective(projectId, sprintId, retrospectiveJSON.toString());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.NAME), retrospective.getName());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.DESCRIPTION), retrospective.getDescription());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.TYPE), retrospective.getType());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.STATUS), retrospective.getStatus());
		assertEquals(projectId, retrospective.getProjectId());
		assertEquals(sprintId, retrospective.getSprintId());
	}
	
	@Test
	public void testToRetrospective_GoodWithStatusClosed() throws JSONException {
		long projectId = 1;
		long sprintId = 2;
		JSONObject retrospectiveJSON = new JSONObject();
		retrospectiveJSON.put(RetrospectiveJSONEnum.NAME, "name");
		retrospectiveJSON.put(RetrospectiveJSONEnum.DESCRIPTION, "description");
		retrospectiveJSON.put(RetrospectiveJSONEnum.TYPE, RetrospectiveObject.TYPE_GOOD);
		retrospectiveJSON.put(RetrospectiveJSONEnum.STATUS, RetrospectiveObject.STATUS_CLOSED);
		RetrospectiveObject retrospective = JSONDecoder.toRetrospective(projectId, sprintId, retrospectiveJSON.toString());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.NAME), retrospective.getName());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.DESCRIPTION), retrospective.getDescription());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.TYPE), retrospective.getType());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.STATUS), retrospective.getStatus());
		assertEquals(projectId, retrospective.getProjectId());
		assertEquals(sprintId, retrospective.getSprintId());
	}
	
	@Test
	public void testToRetrospective_GoodWithStatusResolved() throws JSONException {
		long projectId = 1;
		long sprintId = 2;
		JSONObject retrospectiveJSON = new JSONObject();
		retrospectiveJSON.put(RetrospectiveJSONEnum.NAME, "name");
		retrospectiveJSON.put(RetrospectiveJSONEnum.DESCRIPTION, "description");
		retrospectiveJSON.put(RetrospectiveJSONEnum.TYPE, RetrospectiveObject.TYPE_GOOD);
		retrospectiveJSON.put(RetrospectiveJSONEnum.STATUS, RetrospectiveObject.STATUS_RESOLVED);
		RetrospectiveObject retrospective = JSONDecoder.toRetrospective(projectId, sprintId, retrospectiveJSON.toString());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.NAME), retrospective.getName());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.DESCRIPTION), retrospective.getDescription());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.TYPE), retrospective.getType());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.STATUS), retrospective.getStatus());
		assertEquals(projectId, retrospective.getProjectId());
		assertEquals(sprintId, retrospective.getSprintId());
	}
	
	@Test
	public void testToRetrospective_ImprovementWithStatusNew() throws JSONException {
		long projectId = 1;
		long sprintId = 2;
		JSONObject retrospectiveJSON = new JSONObject();
		retrospectiveJSON.put(RetrospectiveJSONEnum.NAME, "name");
		retrospectiveJSON.put(RetrospectiveJSONEnum.DESCRIPTION, "description");
		retrospectiveJSON.put(RetrospectiveJSONEnum.TYPE, RetrospectiveObject.TYPE_IMPROVEMENT);
		retrospectiveJSON.put(RetrospectiveJSONEnum.STATUS, RetrospectiveObject.STATUS_NEW);
		RetrospectiveObject retrospective = JSONDecoder.toRetrospective(projectId, sprintId, retrospectiveJSON.toString());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.NAME), retrospective.getName());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.DESCRIPTION), retrospective.getDescription());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.TYPE), retrospective.getType());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.STATUS), retrospective.getStatus());
		assertEquals(projectId, retrospective.getProjectId());
		assertEquals(sprintId, retrospective.getSprintId());
	}
	
	@Test
	public void testToRetrospective_ImprovementWithStatusAssigned() throws JSONException {
		long projectId = 1;
		long sprintId = 2;
		JSONObject retrospectiveJSON = new JSONObject();
		retrospectiveJSON.put(RetrospectiveJSONEnum.NAME, "name");
		retrospectiveJSON.put(RetrospectiveJSONEnum.DESCRIPTION, "description");
		retrospectiveJSON.put(RetrospectiveJSONEnum.TYPE, RetrospectiveObject.TYPE_IMPROVEMENT);
		retrospectiveJSON.put(RetrospectiveJSONEnum.STATUS, RetrospectiveObject.STATUS_ASSIGNED);
		RetrospectiveObject retrospective = JSONDecoder.toRetrospective(projectId, sprintId, retrospectiveJSON.toString());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.NAME), retrospective.getName());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.DESCRIPTION), retrospective.getDescription());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.TYPE), retrospective.getType());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.STATUS), retrospective.getStatus());
		assertEquals(projectId, retrospective.getProjectId());
		assertEquals(sprintId, retrospective.getSprintId());
	}
	
	@Test
	public void testToRetrospective_ImprovementWithStatusClosed() throws JSONException {
		long projectId = 1;
		long sprintId = 2;
		JSONObject retrospectiveJSON = new JSONObject();
		retrospectiveJSON.put(RetrospectiveJSONEnum.NAME, "name");
		retrospectiveJSON.put(RetrospectiveJSONEnum.DESCRIPTION, "description");
		retrospectiveJSON.put(RetrospectiveJSONEnum.TYPE, RetrospectiveObject.TYPE_IMPROVEMENT);
		retrospectiveJSON.put(RetrospectiveJSONEnum.STATUS, RetrospectiveObject.STATUS_CLOSED);
		RetrospectiveObject retrospective = JSONDecoder.toRetrospective(projectId, sprintId, retrospectiveJSON.toString());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.NAME), retrospective.getName());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.DESCRIPTION), retrospective.getDescription());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.TYPE), retrospective.getType());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.STATUS), retrospective.getStatus());
		assertEquals(projectId, retrospective.getProjectId());
		assertEquals(sprintId, retrospective.getSprintId());
	}
	
	@Test
	public void testToRetrospective_ImprovementWithStatusResolved() throws JSONException {
		long projectId = 1;
		long sprintId = 2;
		JSONObject retrospectiveJSON = new JSONObject();
		retrospectiveJSON.put(RetrospectiveJSONEnum.NAME, "name");
		retrospectiveJSON.put(RetrospectiveJSONEnum.DESCRIPTION, "description");
		retrospectiveJSON.put(RetrospectiveJSONEnum.TYPE, RetrospectiveObject.TYPE_IMPROVEMENT);
		retrospectiveJSON.put(RetrospectiveJSONEnum.STATUS, RetrospectiveObject.STATUS_RESOLVED);
		RetrospectiveObject retrospective = JSONDecoder.toRetrospective(projectId, sprintId, retrospectiveJSON.toString());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.NAME), retrospective.getName());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.DESCRIPTION), retrospective.getDescription());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.TYPE), retrospective.getType());
		assertEquals(retrospectiveJSON.getString(RetrospectiveJSONEnum.STATUS), retrospective.getStatus());
		assertEquals(projectId, retrospective.getProjectId());
		assertEquals(sprintId, retrospective.getSprintId());
	}
	
	@Test
	public void testToAttachFileInfo() throws JSONException {
		String projectName = "project01";
		long issueId = 1;
		int issueType = IssueTypeEnum.TYPE_STORY;
		JSONObject attachFileInfoJSON = new JSONObject();
		attachFileInfoJSON.put(AttachFileJSONEnum.NAME, "Story01.txt");
		attachFileInfoJSON.put(AttachFileJSONEnum.CONTENT_TYPE, "application/octet-stream");
		AttachFileInfo attachFileInfo = JSONDecoder.toAttachFileInfo(projectName, issueId, issueType, attachFileInfoJSON.toString());
		assertEquals(attachFileInfoJSON.getString(AttachFileJSONEnum.NAME), attachFileInfo.name);
		assertEquals(attachFileInfoJSON.getString(AttachFileJSONEnum.CONTENT_TYPE), attachFileInfo.contentType);
		assertEquals(projectName, attachFileInfo.projectName);
		assertEquals(issueId, attachFileInfo.issueId);
		assertEquals(issueType, attachFileInfo.issueType);
	}
	
	@Test
	public void testToBase64BinaryString() throws JSONException {
		String base64StringData = "U3RvcnkwMQ==";
		JSONObject attachFileJSON = new JSONObject();
		attachFileJSON.put(AttachFileJSONEnum.NAME, "Story01.txt");
		attachFileJSON.put(AttachFileJSONEnum.CONTENT_TYPE, "application/octet-stream");
		attachFileJSON.put(AttachFileJSONEnum.BINARY, base64StringData);
		String base64String = JSONDecoder.toBase64BinaryString(attachFileJSON.toString());
		assertEquals(base64StringData, base64String);
	}
}
