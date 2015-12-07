package ntut.csie.ezScrum.restful.dataMigration.support;

import java.util.ArrayList;
import java.util.Iterator;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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

public class JSONDecoder {
	// Translate JSON String to ProjectObject
	public static ProjectObject toProject(String projectJSONString) {
		ProjectObject project = null;
		try {
			JSONObject projectJSON = new JSONObject(projectJSONString);
			// Get All Project Information
			String projectName = projectJSON.getString(ProjectJSONEnum.NAME);
			String projectDisplayName = projectJSON.getString(ProjectJSONEnum.DISPLAY_NAME);
			String projectComment = projectJSON.getString(ProjectJSONEnum.COMMENT);
			String projectProductOwner = projectJSON.getString(ProjectJSONEnum.PRODUCT_OWNER);
			long createTime = projectJSON.getLong(ProjectJSONEnum.CREATE_TIME);

			// Create ProjectObject
			project = new ProjectObject(projectName);
			project.setDisplayName(projectDisplayName);
			project.setComment(projectComment);
			project.setManager(projectProductOwner);
			project.setCreateTime(createTime);
		} catch (JSONException e) {
			project = null;
		}
		return project;
	}
	
	// Translate JSON String to ReleaseObject
	public static ReleaseObject toRelease(long projectId, String releaseJSONString) {
		ReleaseObject release = null;
		try {
			JSONObject releaseJSON = new JSONObject(releaseJSONString);
			String releaseName = releaseJSON.getString(ReleaseJSONEnum.NAME);
			String releaseDescription = releaseJSON.getString(ReleaseJSONEnum.DESCRIPTION);
			String releaseStartDate = releaseJSON.getString(ReleaseJSONEnum.START_DATE);
			String releaseDueDate = releaseJSON.getString(ReleaseJSONEnum.DUE_DATE);

			// Create ReleaseObject
			release = new ReleaseObject(projectId);
			release.setName(releaseName)
			        .setDescription(releaseDescription)
			        .setStartDate(releaseStartDate)
			        .setDueDate(releaseDueDate);
		} catch (JSONException e) {
			release = null;
		}
		return release;
	}

	// Translate JSON String to Scrum Roles
	public static ArrayList<ScrumRole> toScrumRoles(String projectName, String scrumRolesJSONString) {
		ArrayList<ScrumRole> scrumRoles = new ArrayList<>();
		JSONObject scrumRolesJSON;
		try {
			scrumRolesJSON = new JSONObject(scrumRolesJSONString);
			Iterator<?> iterator = scrumRolesJSON.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				JSONObject roleJSON;
				roleJSON = scrumRolesJSON.getJSONObject(key);
				ScrumRole scrumRole = new ScrumRole(projectName, key);
				scrumRole.setAccessProductBacklog(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG));
				scrumRole.setAccessReleasePlan(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN));
				scrumRole.setAccessRetrospective(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE));
				scrumRole.setAccessSprintBacklog(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG));
				scrumRole.setAccessSprintPlan(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN));
				scrumRole.setAccessTaskBoard(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD));
				scrumRole.setAccessUnplanItem(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLAN));
				scrumRole.setAccessEditProject(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT));
				scrumRole.setAccessReport(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT));
				scrumRoles.add(scrumRole);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return scrumRoles;
	}

	// Translate JSON String to Tag
	public static TagObject toTag(long projectId, String tagJSONString) {
		TagObject tag = null;
		try {
			JSONObject tagJSON = new JSONObject(tagJSONString);
			String tagName = tagJSON.getString(TagJSONEnum.NAME);
			tag = new TagObject(tagName, projectId);
		} catch (JSONException e) {
			tag = null;
		}
		return tag;
	}
	
	// Translate JSON String to Tag in Story
	public static TagObject toTagInStory(String tagJSONString) {
		TagObject tag = null;
		try {
			JSONObject tagJSON = new JSONObject(tagJSONString);
			String tagName = tagJSON.getString(TagJSONEnum.NAME);
			tag = TagObject.get(tagName);
		} catch (JSONException e) {
			tag = null;
		}
		return tag;
	}
	
	// Translate JSON String to AccountObject
	public static AccountObject toAccount(String accountJSONString) {
		AccountObject account = null;
		try {
			JSONObject accountJSON = new JSONObject(accountJSONString);

			// Get Account Information
			String userName = accountJSON.getString(AccountJSONEnum.USERNAME);
			String userNickName = accountJSON.getString(AccountJSONEnum.NICK_NAME);
			String userPassword = accountJSON.getString(AccountJSONEnum.PASSWORD);
			String userEmail = accountJSON.getString(AccountJSONEnum.EMAIL);
			int userEnable = accountJSON.getInt(AccountJSONEnum.ENABLE);

			// Create AccountObject
			account = new AccountObject(userName);
			account.setNickName(userNickName)
			       .setPassword(userPassword)
			       .setEmail(userEmail)
			       .setEnable(userEnable == 1 ? true : false);
		} catch (JSONException e) {
			account = null;
		}
		return account;
	}
	
	// Translate JSON String to SprintObject
	public static SprintObject toSprint(long projectId, String sprintJSONString) {
		SprintObject sprint = null;
		try {
			JSONObject sprintJSON = new JSONObject(sprintJSONString);

			// Get Sprint Information
			String goal = sprintJSON.getString(SprintJSONEnum.GOAL);
			int interval = sprintJSON.getInt(SprintJSONEnum.INTERVAL);
			int teamSize = sprintJSON.getInt(SprintJSONEnum.TEAM_SIZE);
			int availableHours = sprintJSON.getInt(SprintJSONEnum.AVAILABLE_HOURS);
			int focusFactor = sprintJSON.getInt(SprintJSONEnum.FOCUS_FACTOR);
			String startDate = sprintJSON.getString(SprintJSONEnum.START_DATE);
			String dueDate = sprintJSON.getString(SprintJSONEnum.DUE_DATE);
			String demoDate = sprintJSON.getString(SprintJSONEnum.DEMO_DATE);
			String demoPlace = sprintJSON.getString(SprintJSONEnum.DEMO_PLACE);
			String dailyInfo = sprintJSON.getString(SprintJSONEnum.DAILY_INFO);

			// Create SprintObject
			sprint = new SprintObject(projectId);
			sprint.setGoal(goal)
			      .setInterval(interval)
			      .setTeamSize(teamSize)
			      .setAvailableHours(availableHours)
			      .setFocusFactor(focusFactor)
			      .setStartDate(startDate)
			      .setDueDate(dueDate)
			      .setDemoDate(demoDate)
			      .setDemoPlace(demoPlace)
			      .setDailyInfo(dailyInfo);
		} catch (JSONException e) {
			sprint = null;
		}
		return sprint;
	}
	
	// Translate JSON String to SprintObject
	public static StoryObject toStory(long projectId, long sprintId, String storyJSONString) {
		StoryObject story = null;
		try {
			JSONObject storyJSON = new JSONObject(storyJSONString);

			// Get Story Information
			String name = storyJSON.getString(StoryJSONEnum.NAME);
			String status = storyJSON.getString(StoryJSONEnum.STATUS);
			int estimate = storyJSON.getInt(StoryJSONEnum.ESTIMATE);
			int importance = storyJSON.getInt(StoryJSONEnum.IMPORTANCE);
			int value = storyJSON.getInt(StoryJSONEnum.VALUE);
			String notes = storyJSON.getString(StoryJSONEnum.NOTES);
			String howToDemo = storyJSON.getString(StoryJSONEnum.HOW_TO_DEMO);

			// Create StoryObject
			story = new StoryObject(projectId);
			story.setName(name)
			     .setStatus(StatusTranslator.getStoryStatus(status))
			     .setEstimate(estimate)
			     .setImportance(importance)
			     .setValue(value)
			     .setNotes(notes)
			     .setHowToDemo(howToDemo)
			     .setSprintId(sprintId);
		} catch (JSONException e) {
			story = null;
		}
		return story;
	}
	
	// Translate JSON String to TaskObject
	public static TaskObject toTask(long projectId, long storyId, String taskJSONString) {
		TaskObject task = null;
		try {
			JSONObject taskJSON = new JSONObject(taskJSONString);
			JSONArray partnerJSONArray = taskJSON.getJSONArray(TaskJSONEnum.PARTNERS);

			// Get Task Information
			String name = taskJSON.getString(TaskJSONEnum.NAME);
			String handler = taskJSON.getString(TaskJSONEnum.HANDLER);
			int estimate = taskJSON.getInt(TaskJSONEnum.ESTIMATE);
			int remain = taskJSON.getInt(TaskJSONEnum.REMAIN);
			int actual = taskJSON.getInt(TaskJSONEnum.ACTUAL);
			String notes = taskJSON.getString(TaskJSONEnum.NOTES);
			String status = taskJSON.getString(TaskJSONEnum.STATUS);
			ArrayList<Long> partnersId = new ArrayList<Long>();
			for (int i = 0; i < partnerJSONArray.length(); i++) {
				JSONObject partnerJSON = partnerJSONArray.getJSONObject(i);
				String partnerName = partnerJSON.getString(AccountJSONEnum.USERNAME);
				long partnerId = AccountObject.get(partnerName).getId();
				partnersId.add(partnerId);
			}

			// Create StoryObject
			task = new TaskObject(projectId);
			task.setName(name)
			    .setEstimate(estimate)
			    .setRemains(remain)
			    .setActual(actual)
			    .setNotes(notes)
			    .setStatus(StatusTranslator.getTaskStatus(status))
			    .setStoryId(storyId);
			if (!handler.isEmpty()) {
				long handlerId = AccountObject.get(handler).getId();
				task.setHandlerId(handlerId);
			}
			if (!partnersId.isEmpty()) {
				task.setPartnersId(partnersId);
			}
		} catch (JSONException e) {
			task = null;
		}
		return task;
	}
	
	// Translate JSON String to UnplanObject
	public static UnplanObject toUnplan(long projectId, long sprintId, String unplanJSONString) {
		UnplanObject unplan = null;
		try {
			JSONObject unplanJSON = new JSONObject(unplanJSONString);
			JSONArray partnerJSONArray = unplanJSON.getJSONArray(UnplanJSONEnum.PARTNERS);

			// Get Unplan Information
			String name = unplanJSON.getString(UnplanJSONEnum.NAME);
			String handler = unplanJSON.getString(UnplanJSONEnum.HANDLER);
			int estimate = unplanJSON.getInt(UnplanJSONEnum.ESTIMATE);
			int actual = unplanJSON.getInt(UnplanJSONEnum.ACTUAL);
			String notes = unplanJSON.getString(UnplanJSONEnum.NOTES);
			String status = unplanJSON.getString(UnplanJSONEnum.STATUS);
			ArrayList<Long> partnersId = new ArrayList<Long>();
			for (int i = 0; i < partnerJSONArray.length(); i++) {
				JSONObject partnerJSON = partnerJSONArray.getJSONObject(i);
				String partnerName = partnerJSON.getString(AccountJSONEnum.USERNAME);
				long partnerId = AccountObject.get(partnerName).getId();
				partnersId.add(partnerId);
			}

			// Create UnplanObject
			unplan = new UnplanObject(sprintId, projectId);
			unplan.setName(name)
			        .setEstimate(estimate)
			        .setActual(actual)
			        .setNotes(notes)
			        .setStatus(StatusTranslator.getUnplanStatus(status));
			if (!handler.isEmpty()) {
				long handlerId = AccountObject.get(handler).getId();
				unplan.setHandlerId(handlerId);
			}
			if (!partnersId.isEmpty()) {
				unplan.setPartnersId(partnersId);
			}
		} catch (JSONException e) {
			unplan = null;
		}
		return unplan;
	}
	
	// Translate JSON String to HistoryObject
	public static HistoryObject toHistory(long issueId, int issueType, String historyJSONString) {
		HistoryObject history = null;
		try {
			JSONObject historyJSON = new JSONObject(historyJSONString);

			// Get Story Information
			String type = historyJSON.getString(HistoryJSONEnum.HISTORY_TYPE);
			String oldValue = historyJSON.getString(HistoryJSONEnum.OLD_VALUE);
			String newValue = historyJSON.getString(HistoryJSONEnum.NEW_VALUE);
			long createTime = historyJSON.getLong(HistoryJSONEnum.CREATE_TIME);

			// Create HistoryObject
			history = new HistoryObject();
			history.setIssueId(issueId)
			       .setIssueType(issueType)
			       .setHistoryType(HistoryTypeTranslator.getHistoryType(type))
			       .setCreateTime(createTime);
			if (HistoryTypeTranslator.getHistoryType(type) == HistoryObject.TYPE_STATUS) {
				history.setOldValue(String.valueOf(StatusTranslator.getUnplanStatus(oldValue)))
				       .setNewValue(String.valueOf(StatusTranslator.getUnplanStatus(newValue)));
			} else {
				history.setOldValue(oldValue)
			           .setNewValue(newValue);
			}
		} catch (JSONException e) {
			history = null;
		}
		return history;
	}
	
	// Translate JSON String to Retro
	public static RetrospectiveObject toRetrospective(long projectId, long sprintId, String retrospectiveJSONString) {
		RetrospectiveObject retrospective = null;
		try {
			JSONObject retrospectiveJSON = new JSONObject(retrospectiveJSONString);

			// Get Retrospective Information
			String name = retrospectiveJSON.getString(RetrospectiveJSONEnum.NAME);
			String description = retrospectiveJSON.getString(RetrospectiveJSONEnum.DESCRIPTION);
			String type = retrospectiveJSON.getString(RetrospectiveJSONEnum.TYPE);
			String status = retrospectiveJSON.getString(RetrospectiveJSONEnum.STATUS);

			// Create RetrospectiveObject
			retrospective = new RetrospectiveObject(projectId);
			retrospective.setSprintId(sprintId)
			        .setName(name)
			        .setDescription(description)
			        .setType(type)
			        .setStatus(status);
		} catch (JSONException e) {
			retrospective = null;
		}
		return retrospective;
	}
	
	// Translate JSON String to AttachFileInfo
	public static AttachFileInfo toAttachFileInfo(String projectName, long issueId, int issueType, String attachFileJSONString) {
		AttachFileInfo attachFileInfo = new AttachFileInfo();
		try {
			JSONObject attachFileJSON = new JSONObject(attachFileJSONString);
			attachFileInfo.issueId = issueId;
	        attachFileInfo.issueType = issueType;
	        attachFileInfo.name = attachFileJSON.getString(AttachFileJSONEnum.NAME);
	        attachFileInfo.contentType = attachFileJSON.getString(AttachFileJSONEnum.CONTENT_TYPE);
	        attachFileInfo.projectName = projectName;
		} catch (JSONException e) {
			attachFileInfo = null;
		}
		return attachFileInfo;
	}
	
	// Translate AttachFile JSON String to base64 binary String
	public static String toBase64BinaryString(String attachFileJSONString) {
		String base64BinaryString = "";
		try {
			JSONObject attachFileJSON = new JSONObject(attachFileJSONString);
			base64BinaryString = attachFileJSON.getString(AttachFileJSONEnum.BINARY);
		} catch (JSONException e) {
			base64BinaryString = "";
		}
		return base64BinaryString;
	}
}
