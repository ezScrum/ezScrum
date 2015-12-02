package ntut.csie.ezScrum.restful.dataMigration.support;

import java.util.Iterator;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.AccountJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ProjectJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ScrumRoleJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TagJSONEnum;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.databaseEnum.RoleEnum;

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

			// Get Project
			project = ProjectObject.get(projectName);

			// Set Project Information
			if (project == null) {
				project = new ProjectObject(projectName);
			}
			project.setDisplayName(projectDisplayName);
			project.setComment(projectComment);
			project.setManager(projectProductOwner);
			project.save(createTime);

			// Set ScrumRole
			JSONObject scrumRoleJSON = new JSONObject(projectJSON.getJSONObject(ProjectJSONEnum.SCRUM_ROLES).toString());
			Iterator<?> iterator = scrumRoleJSON.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				JSONObject roleJSON = scrumRoleJSON.getJSONObject(key);
				ScrumRole scrumRole = new ScrumRole(project.getName(), key);
				scrumRole.setAccessProductBacklog(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG));
				scrumRole.setAccessReleasePlan(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN));
				scrumRole.setAccessRetrospective(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE));
				scrumRole.setAccessSprintBacklog(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG));
				scrumRole.setAccessSprintPlan(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN));
				scrumRole.setAccessTaskBoard(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD));
				scrumRole.setAccessUnplanItem(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLANNED));
				scrumRole.setEditProject(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT));
				scrumRole.setReadReport(roleJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT));
				project.updateScrumRole(scrumRole);
			}

			// Set ProjectRole
			JSONArray projectRoleJSONArray = projectJSON.getJSONArray(ProjectJSONEnum.PROJECT_ROLES);
			for (int i = 0; i < projectRoleJSONArray.length(); i++) {
				JSONObject accountJSON = projectRoleJSONArray.getJSONObject(i);
				AccountObject account = AccountObject.get(accountJSON.getString(AccountJSONEnum.USERNAME));
				account.createProjectRole(project.getId(), RoleEnum.valueOf(accountJSON.getString(ScrumRoleJSONEnum.ROLE)));
			}

			// Add tag
			JSONArray tagJSONArray = projectJSON.getJSONArray(ProjectJSONEnum.TAGS);
			for (int i = 0; i < tagJSONArray.length(); i++) {
				JSONObject tagJSON = tagJSONArray.getJSONObject(i);
				TagObject tag = new TagObject(tagJSON.getString(TagJSONEnum.NAME), project.getId());
				tag.save();
			}
		} catch (JSONException e) {
			if (project != null && project.getId() > -1) {
				project.delete();
			}
			project = null;
		}
		return project;
	}

}
