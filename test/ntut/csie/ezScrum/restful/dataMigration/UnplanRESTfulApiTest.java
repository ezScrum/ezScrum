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
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.UnplanJSONEnum;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;
import ntut.csie.ezScrum.web.databaseEnum.RoleEnum;
import ntut.csie.ezScrum.web.databaseEnum.UnplanEnum;

public class UnplanRESTfulApiTest extends JerseyTest {
	private Configuration mConfig;
	private CreateAccount mCA;
	private CreateProject mCP;
	private CreateSprint mCS;
	private ResourceConfig mResourceConfig;
	private Client mClient;
	private HttpServer mHttpServer;
	private static String BASE_URL = "http://localhost:8080/ezScrum/resource/";
	private URI mBaseUri = URI.create(BASE_URL);

	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(UnplanRESTfulApi.class);
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

		// Create Sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

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
		mCS = null;
		mHttpServer = null;
		mClient = null;
	}
	
	@Test
	public void testCreateUnplan() throws JSONException {
		// Test Data
		AccountObject handlerAccount = mCA.getAccountList().get(0);
		AccountObject partnerAccount = mCA.getAccountList().get(0);
		String name = "TEST_UNPLAN_NAME";
		String handler = handlerAccount.getUsername();
		String partner = partnerAccount.getUsername();
		int estimate = 3;
		int actual = 2;
		String notes = "TEST_CREATE_UNPLAN";
		String status = "assigned";
		ProjectObject project = mCP.getAllProjects().get(0);
		SprintObject sprint = mCS.getSprints().get(0);

		// Add Account to Project
		handlerAccount.joinProjectWithScrumRole(project.getId(), RoleEnum.ScrumTeam);
		partnerAccount.joinProjectWithScrumRole(project.getId(), RoleEnum.ScrumTeam);

		JSONObject unplanJSON = new JSONObject();
		unplanJSON.put(UnplanJSONEnum.NAME, name);
		unplanJSON.put(UnplanJSONEnum.HANDLER, handler);
		unplanJSON.put(UnplanJSONEnum.ESTIMATE, estimate);
		unplanJSON.put(UnplanJSONEnum.ACTUAL, actual);
		unplanJSON.put(UnplanJSONEnum.NOTES, notes);
		unplanJSON.put(UnplanJSONEnum.STATUS, status);
		JSONArray partnersIdJSONArray = new JSONArray();
		JSONObject partnerJSON = new JSONObject();
		partnerJSON.put(AccountJSONEnum.USERNAME, partner);
		partnersIdJSONArray.put(partnerJSON);
		unplanJSON.put(UnplanJSONEnum.PARTNERS, partnersIdJSONArray);

		// Call '/projects/{projectId}/sprints/{sprintId}/unplans' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/unplans")
		        .request()
		        .post(Entity.text(unplanJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = jsonResponse.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertTrue(contentJSON.getLong(UnplanEnum.ID) != -1);
		UnplanObject unplan = UnplanObject.get(contentJSON.getLong(UnplanEnum.ID));
		assertEquals(name, unplan.getName());
		assertEquals(handler, unplan.getHandler().getUsername());
		assertEquals(estimate, unplan.getEstimate());
		assertEquals(actual, unplan.getActual());
		assertEquals(notes, unplan.getNotes());
		assertEquals(status, unplan.getStatusString());
	}
	
	@Test
	public void testCreateHistoryInUnplan() {
		// TODO
	}

}
