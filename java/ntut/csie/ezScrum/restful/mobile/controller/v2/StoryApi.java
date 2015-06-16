package ntut.csie.ezScrum.restful.mobile.controller.v2;

import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.restful.mobile.service.ProductBacklogWebService;
import ntut.csie.ezScrum.restful.mobile.service.StoryWebService;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONObject;

@Path("/stories")
public class StoryApi extends BaseAuthApi {
	
	@Override
	protected Response get(long resourceId, UriInfo uriInfo) throws LogonException {
		MultivaluedMap<String, String> queries = uriInfo.getQueryParameters();
		String projectName = queries.get("project_name").get(0);
		ProductBacklogWebService service = new ProductBacklogWebService(getUser(), projectName);
		service.getStory(resourceId);
		return response(200, service.getRESTFulResponseString());
	}
	
	@Override
	protected Response getList(UriInfo uriInfo) throws Exception {
		MultivaluedMap<String, String> queries = uriInfo.getQueryParameters();
		String projectName = queries.get("project_name").get(0);
		ProductBacklogWebService service = new ProductBacklogWebService(getUser(), projectName);
		service.getStories();
		return response(200, service.getRESTFulResponseString());
	}

	@Override
	protected Response post(String entity) throws Exception {
		JSONObject jsonEntity = new JSONObject(entity);
		StoryWebService service = new StoryWebService(getUser(), jsonEntity.getString("project_name"));
		String response = service.createStory(entity);
		return response(200, response);
	}

	@Override
	protected Response put(long resourceId, String entity) throws Exception {
		JSONObject jsonEntity = new JSONObject(entity);
		StoryWebService service = new StoryWebService(getUser(), jsonEntity.getString("project_name"));
		String response = service.updateStory(entity);
		return response(200, response);
	}

	@Override
	protected Response delete(long resourceId, UriInfo uriInfo) throws Exception {
		MultivaluedMap<String, String> queries = uriInfo.getQueryParameters();
		String projectName = queries.get("project_name").get(0);
		System.out.println(projectName);
		ProductBacklogWebService service = new ProductBacklogWebService(getUser(), projectName);
		service.deleteStory(resourceId);
		return response(200, service.getRESTFulResponseString());
	}

	@Override
	protected boolean permissionCheck(AccountObject user, UriInfo uriInfo) {
		try {
			MultivaluedMap<String, String> queries = uriInfo.getQueryParameters();
			String projectName = queries.get("project_name").get(0);
			ScrumRole scrumRole = user.getProjectRoleMap().get(projectName).getScrumRole();
			boolean productBacklog = scrumRole.getAccessProductBacklog();
			boolean sprintBacklog = scrumRole.getAccessSprintBacklog();
			return productBacklog || sprintBacklog;			
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	protected boolean ownerCheck(AccountObject user, UriInfo uriInfo) {
		return true;
	}
}
