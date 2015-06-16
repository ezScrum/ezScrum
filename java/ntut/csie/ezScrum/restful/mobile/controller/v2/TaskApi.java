package ntut.csie.ezScrum.restful.mobile.controller.v2;

import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.restful.mobile.service.TaskWebService;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

@Path("/tasks")
public class TaskApi extends BaseAuthApi {

	@Override
	protected Response get(long resourceId, UriInfo uriInfo) throws Exception {
		TaskObject task = TaskObject.get(resourceId);
		return response(200, task.toString());
	}

	@Override
	protected Response getList(UriInfo uriInfo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
 
	@Override
	protected Response post(String entity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Response put(long resourceId, String entity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Response delete(long resourceId, UriInfo uriInfo)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean permissionCheck(AccountObject user, UriInfo uriInfo) {
		try {
			MultivaluedMap<String, String> queries = uriInfo.getQueryParameters();
			String projectName = queries.get("project_name").get(0);
			ScrumRole scrumRole = user.getProjectRoleMap().get(projectName).getScrumRole();
			boolean sprintBacklog = scrumRole.getAccessSprintBacklog();
			return sprintBacklog;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	protected boolean ownerCheck(AccountObject user, UriInfo uriInfo) {
		return true;
	}

}
