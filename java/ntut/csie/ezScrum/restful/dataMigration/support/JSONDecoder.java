package ntut.csie.ezScrum.restful.dataMigration.support;

import java.util.ArrayList;
import java.util.Iterator;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.AccountJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ProjectJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ScrumRoleJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.SprintJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TagJSONEnum;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;

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
				scrumRole.setEditProject(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT));
				scrumRole.setReadReport(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT));
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
}
