package ntut.csie.ezScrum.restful.mobile.controller.v2;

import java.util.ArrayList;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.databaseEnum.ProjectEnum;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

@Path("/projects")
public class ProjectApi extends BaseAuthApi {

	@Override
	protected Response get(long resourceId, UriInfo uriInfo) throws Exception {
		ProjectObject project = ProjectObject.get(resourceId);
		if (project != null) {
			return response(200, project.toString());
		}
		return responseFail("Project #" + resourceId + " does not exsit");
	}

	@Override
	protected Response getList(UriInfo uriInfo) throws Exception {
		ArrayList<ProjectObject> projects = new ArrayList<ProjectObject>();
		// get user attend project's name
		AccountObject user = getUser();
		if (user.isAdmin()) {
			projects = ProjectObject.getAllProjects();
		} else {
			Set<String> projectsName = user.getProjectRoleMap().keySet();
			for (String projectName : projectsName) {
				ProjectObject project = ProjectObject.get(projectName);
				projects.add(project);
			}
		}
		
		JSONArray projectsArray = new JSONArray();
		JSONObject projectsJson = new JSONObject();
		for (ProjectObject project : projects) {
			projectsArray.put(project.toJSON());
		}
		projectsJson.put("projects", projectsArray);
		
		return response(200, projectsJson.toString());
	}

	@Override
	protected Response post(String entity) throws Exception {
		JSONObject jsonEntity  = new JSONObject(entity);
		ProjectObject project = new ProjectObject(jsonEntity.getString(ProjectEnum.NAME));
		project
			.setDisplayName(jsonEntity.getString(ProjectEnum.DISPLAY_NAME))
			.setComment(jsonEntity.getString(ProjectEnum.COMMENT))
			.setManager(jsonEntity.getString(ProjectEnum.PRODUCT_OWNER))
			.setAttachFileSize(jsonEntity.getInt(ProjectEnum.ATTATCH_MAX_SIZE))
			.save();
		
		if (project.getId() > 0) {
			return responseOK();
		}
		return responseFail("Create Project Fail");		
	}

	@Override
	protected Response put(long resourceId, String entity) throws Exception {
		JSONObject jsonEntity  = new JSONObject(entity);
		ProjectObject project = ProjectObject.get(resourceId);
		project
			.setDisplayName(jsonEntity.getString(ProjectEnum.DISPLAY_NAME))
			.setComment(jsonEntity.getString(ProjectEnum.COMMENT))
			.setManager(jsonEntity.getString(ProjectEnum.PRODUCT_OWNER))
			.setAttachFileSize(jsonEntity.getInt(ProjectEnum.ATTATCH_MAX_SIZE))
			.save();
		
		return responseOK();
	}

	@Override
	protected Response delete(long resourceId, UriInfo uriInfo)
			throws Exception {
		ProjectObject project = ProjectObject.get(resourceId);
		if (project.delete()) {
			return responseOK();
		}
		return responseFail("Delete Project #" + resourceId + " Faid");
	}

	@Override
	protected boolean permissionCheck(AccountObject user, UriInfo uriInfo) {
		return true;
	}

	@Override
	protected boolean ownerCheck(AccountObject user, UriInfo uriInfo) {
		return true;
	}

}
