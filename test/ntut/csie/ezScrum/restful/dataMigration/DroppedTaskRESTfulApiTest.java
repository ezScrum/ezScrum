package ntut.csie.ezScrum.restful.dataMigration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.HttpServer;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.AccountJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TaskJSONEnum;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.databaseEnum.RoleEnum;
import ntut.csie.ezScrum.web.databaseEnum.TaskEnum;

public class DroppedTaskRESTfulApiTest extends JerseyTest {
	private Configuration mConfig;
	private CreateAccount mCA;
	private CreateProject mCP;
	private ResourceConfig mResourceConfig;
	private Client mClient;
	private HttpServer mHttpServer;
	private static String BASE_URL = "http://localhost:8080/ezScrum/resource/";
	private URI mBaseUri = URI.create(BASE_URL);

	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(DroppedTaskRESTfulApi.class);
		return mResourceConfig;
	}

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() throws Exception {
		// Set Test Mode
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// Create Account
		mCA = new CreateAccount(2);
		mCA.exe();

		// Create Project
		mCP = new CreateProject(1);
		mCP.exeCreate();
		
		// Start Server
		mHttpServer = JdkHttpServerFactory.createHttpServer(mBaseUri, mResourceConfig, true);

		// Create Client
		mClient = ClientBuilder.newClient();
	}

	@After
	public void tearDown() {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除測試檔案
		CopyProject copyProject = new CopyProject(mCP);
		copyProject.exeDelete_Project();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		// Stop Server
		mHttpServer.stop(0);

		// ============= release ==============
		ini = null;
		copyProject = null;
		mCP = null;
		mHttpServer = null;
		mClient = null;
	}

	@Test
	public void testCreateDroppedTask() throws JSONException {
		// Test Data
		AccountObject handlerAccount = mCA.getAccountList().get(0);
		AccountObject partnerAccount = mCA.getAccountList().get(1);
		String name = "TEST_TASK_NAME";
		String handler = handlerAccount.getUsername();
		String partner = partnerAccount.getUsername();
		int estimate = 3;
		int remain = 2;
		int actual = 3;
		String notes = "TEST_CREATE_DROPPED_TASK";
		String status = "assigned";
		ProjectObject project = mCP.getAllProjects().get(0);

		// Add Account to Project
		handlerAccount.joinProjectWithScrumRole(project.getId(), RoleEnum.ScrumTeam);
		partnerAccount.joinProjectWithScrumRole(project.getId(), RoleEnum.ScrumTeam);

		JSONObject taskJSON = new JSONObject();
		taskJSON.put(TaskJSONEnum.NAME, name);
		taskJSON.put(TaskJSONEnum.HANDLER, handler);
		taskJSON.put(TaskJSONEnum.ESTIMATE, estimate);
		taskJSON.put(TaskJSONEnum.REMAIN, remain);
		taskJSON.put(TaskJSONEnum.ACTUAL, actual);
		taskJSON.put(TaskJSONEnum.NOTES, notes);
		taskJSON.put(TaskJSONEnum.STATUS, status);
		JSONArray partnersIdJSONArray = new JSONArray();
		JSONObject partnerJSON = new JSONObject();
		partnerJSON.put(AccountJSONEnum.USERNAME, partner);
		partnersIdJSONArray.put(partnerJSON);
		taskJSON.put(TaskJSONEnum.PARTNERS, partnersIdJSONArray);

		// Call '/projects/{projectId}/tasks' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		               "/tasks")
		        .request()
		        .post(Entity.text(taskJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = jsonResponse.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertTrue(contentJSON.getLong(TaskEnum.ID) != -1);
		TaskObject task = TaskObject.get(contentJSON.getLong(TaskEnum.ID));
		assertEquals(name, task.getName());
		assertEquals(handler, task.getHandler().getUsername());
		assertEquals(estimate, task.getEstimate());
		assertEquals(remain, task.getRemains());
		assertEquals(actual, task.getActual());
		assertEquals(notes, task.getNotes());
		assertEquals(status, task.getStatusString());
		assertEquals(partner, task.getPartnersUsername());
	}

	@Test
	public void createHistoryInDroppedTask() {
		// TODO
	}

	@Test
	public void createAttachfilesInDropped() {
		// TODO
	}
}
