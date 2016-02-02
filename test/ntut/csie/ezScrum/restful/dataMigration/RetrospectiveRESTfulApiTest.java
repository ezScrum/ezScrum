package ntut.csie.ezScrum.restful.dataMigration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

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
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.RetrospectiveJSONEnum;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.databaseEnum.RetrospectiveEnum;

public class RetrospectiveRESTfulApiTest extends JerseyTest {
	private Configuration mConfig;
	private CreateProject mCP;
	private CreateSprint mCS;
	private ResourceConfig mResourceConfig;
	private Client mClient;
	private HttpServer mHttpServer;
	private static String BASE_URL = "http://localhost:8080/ezScrum/resource/";
	private URI mBaseUri = URI.create(BASE_URL);

	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(RetrospectiveRESTfulApi.class);
		return mResourceConfig;
	}

	@Before
	public void setUp() throws Exception {
		// Set Test Mode
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// Create Project
		mCP = new CreateProject(1);
		mCP.exeCreateForDb();

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

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		// Stop Server
		mHttpServer.stop(0);

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mHttpServer = null;
		mClient = null;
	}
	
	@Test
	public void testCreateRetrospective_GoodWithStatusNew() throws JSONException {
		// Test Data
		String name = "TEST_RETROSPECTIVE_NAME";
		String description = "TEST_RETROSPECTIVE_DESCRIPTION";
		String type = "Good";
		String status = "new";
		ProjectObject project = mCP.getAllProjects().get(0);
		SprintObject sprint = mCS.getSprints().get(0);

		JSONObject retrospectiveJSON = new JSONObject();
		retrospectiveJSON.put(RetrospectiveJSONEnum.NAME, name);
		retrospectiveJSON.put(RetrospectiveJSONEnum.DESCRIPTION, description);
		retrospectiveJSON.put(RetrospectiveJSONEnum.TYPE, type);
		retrospectiveJSON.put(RetrospectiveJSONEnum.STATUS, status);

		// Call '/projects/{projectId}/sprints/{sprintId}/retrospectives' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/retrospectives")
		        .request()
		        .post(Entity.text(retrospectiveJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = jsonResponse.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertTrue(contentJSON.getLong(RetrospectiveEnum.ID) != -1);
		RetrospectiveObject retrospective = RetrospectiveObject.get(contentJSON.getLong(RetrospectiveEnum.ID));
		assertEquals(name, retrospective.getName());
		assertEquals(description, retrospective.getDescription());
		assertEquals(type, retrospective.getType());
		assertEquals(status, retrospective.getStatus());
	}
	
	@Test
	public void testCreateRetrospective_ImprovementWithStatusNew() throws JSONException {
		// Test Data
		String name = "TEST_RETROSPECTIVE_NAME";
		String description = "TEST_RETROSPECTIVE_DESCRIPTION";
		String type = "Improvement";
		String status = "new";
		ProjectObject project = mCP.getAllProjects().get(0);
		SprintObject sprint = mCS.getSprints().get(0);

		JSONObject retrospectiveJSON = new JSONObject();
		retrospectiveJSON.put(RetrospectiveJSONEnum.NAME, name);
		retrospectiveJSON.put(RetrospectiveJSONEnum.DESCRIPTION, description);
		retrospectiveJSON.put(RetrospectiveJSONEnum.TYPE, type);
		retrospectiveJSON.put(RetrospectiveJSONEnum.STATUS, status);

		// Call '/projects/{projectId}/sprints/{sprintId}/retrospectives' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/retrospectives")
		        .request()
		        .post(Entity.text(retrospectiveJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = jsonResponse.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertTrue(contentJSON.getLong(RetrospectiveEnum.ID) != -1);
		RetrospectiveObject retrospective = RetrospectiveObject.get(contentJSON.getLong(RetrospectiveEnum.ID));
		assertEquals(name, retrospective.getName());
		assertEquals(description, retrospective.getDescription());
		assertEquals(type, retrospective.getType());
		assertEquals(status, retrospective.getStatus());
	}
	
	@Test
	public void testCreateRetrospective_ImprovementWithStatusClosed() throws JSONException {
		// Test Data
		String name = "TEST_RETROSPECTIVE_NAME";
		String description = "TEST_RETROSPECTIVE_DESCRIPTION";
		String type = "Improvement";
		String status = "closed";
		ProjectObject project = mCP.getAllProjects().get(0);
		SprintObject sprint = mCS.getSprints().get(0);

		JSONObject retrospectiveJSON = new JSONObject();
		retrospectiveJSON.put(RetrospectiveJSONEnum.NAME, name);
		retrospectiveJSON.put(RetrospectiveJSONEnum.DESCRIPTION, description);
		retrospectiveJSON.put(RetrospectiveJSONEnum.TYPE, type);
		retrospectiveJSON.put(RetrospectiveJSONEnum.STATUS, status);

		// Call '/projects/{projectId}/sprints/{sprintId}/retrospectives' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/retrospectives")
		        .request()
		        .post(Entity.text(retrospectiveJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = jsonResponse.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertTrue(contentJSON.getLong(RetrospectiveEnum.ID) != -1);
		RetrospectiveObject retrospective = RetrospectiveObject.get(contentJSON.getLong(RetrospectiveEnum.ID));
		assertEquals(name, retrospective.getName());
		assertEquals(description, retrospective.getDescription());
		assertEquals(type, retrospective.getType());
		assertEquals(status, retrospective.getStatus());
	}
}
