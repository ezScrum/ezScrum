package ntut.csie.ezScrum.restful.mobile.controller.v2;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MultivaluedMap;
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
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TokenObject;

public class ProjectApiTest extends JerseyTest {
	private Configuration mConfig;
	private CreateProject mCP;
	private ResourceConfig mResourceConfig;
	private Client mClient;
	private HttpServer mHttpServer;
	private static String BASE_URL = "http://127.0.0.1:8080/ezScrum/api";
	private URI mBaseUri = URI.create(BASE_URL);
	private long mAccountId;
	private String mPlatformType;

	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(ProjectApi.class);
		return mResourceConfig;
	}
	
	@Before
	public void setUp() throws Exception {
		// change to test mode
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// initial SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mAccountId = 1; // get admin
		mPlatformType = "windows";
		
		TokenObject token = new TokenObject(mAccountId, mPlatformType);
		token.save();
		
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

		mConfig.setTestMode(false);
		mConfig.save();
		
		// stop server
		mHttpServer.stop(0);

		// release
		mCP = null;
		mConfig = null;
	}
	
	@Test
	public void testGet() throws JSONException {
		// create project
		mCP = new CreateProject(1);
		mCP.exeCreateForDb();
		
		// get data
		ProjectObject project = mCP.getAllProjects().get(0);
		MultivaluedMap<String, Object> headersMap = TestableApi.getHeaders(mAccountId, mPlatformType);
		Response response = mClient.target(BASE_URL)
                .path("projects/" + project.getId())
                .request()
                .headers(headersMap)
                .get();
		
		// assert data
		JSONObject projectJson = new JSONObject(response.readEntity(String.class));
		assertEquals(project.getId(), projectJson.getLong("id"));
		assertEquals(project.getName(), projectJson.getString("name"));
		assertEquals(project.getDisplayName(), projectJson.getString("display_name"));
		assertEquals(project.getComment(), projectJson.getString("comment"));
		assertEquals(project.getManager(), projectJson.getString("product_owner"));
		assertEquals(project.getAttachFileSize(), projectJson.getLong("attach_max_size"));
	}
	
	@Test
	public void testGetList() throws JSONException {
		// create project
		mCP = new CreateProject(3);
		mCP.exeCreateForDb();
		
		// get data
		ArrayList<ProjectObject> projects = mCP.getAllProjects();
		MultivaluedMap<String, Object> headersMap = TestableApi.getHeaders(mAccountId, mPlatformType);
		Response response = mClient.target(BASE_URL)
                .path("projects")
                .request()
                .headers(headersMap)
                .get();
		
		// assert data
		JSONObject projectsJson = new JSONObject(response.readEntity(String.class));
		for (int i = 0; i < 3; i++) {
			ProjectObject project = projects.get(i);
			JSONObject projectJson = projectsJson.getJSONArray("projects").getJSONObject(i);
			assertEquals(project.getId(), projectJson.getLong("id"));
			assertEquals(project.getName(), projectJson.getString("name"));
			assertEquals(project.getDisplayName(), projectJson.getString("display_name"));
			assertEquals(project.getComment(), projectJson.getString("comment"));
			assertEquals(project.getManager(), projectJson.getString("product_owner"));
			assertEquals(project.getAttachFileSize(), projectJson.getLong("attach_max_size"));
		}
	}
	
	@Test
	public void testPost() throws JSONException {
		JSONObject projectJson = new JSONObject();
		projectJson.put("name", "TEST_NAME")
				.put("display_name", "TEST_DISPLAYNAME")
				.put("comment", "TEST_COMMENT")
				.put("product_owner", "TEST_PO")
				.put("attach_max_size", 2);
		
		MultivaluedMap<String, Object> headersMap = TestableApi.getHeaders(mAccountId, mPlatformType);
		Response response = mClient.target(BASE_URL)
                .path("projects")
                .request()
                .headers(headersMap)
                .post(Entity.text(projectJson.toString()));
		
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		
		// check one project in database
		ArrayList<ProjectObject> projects = ProjectObject.getAllProjects();
		assertEquals(1, projects.size());
		// assert response JSON string
		assertEquals(200, response.getStatus());
		assertEquals("ok", responseJSON.getString("msg"));
	}
	
	@Test
	public void testPut() throws JSONException {
		// create project
		mCP = new CreateProject(1);
		mCP.exeCreateForDb();
		ProjectObject project = mCP.getAllProjects().get(0);
		
		JSONObject projectJson = new JSONObject();
		projectJson
				.put("id", project.getId())
				.put("name", project.getName())
				.put("display_name", "TEST_DISPLAYNAME")
				.put("comment", "TEST_COMMENT")
				.put("product_owner", "TEST_PO")
				.put("attach_max_size", 2);
		
		MultivaluedMap<String, Object> headersMap = TestableApi.getHeaders(mAccountId, mPlatformType);
		
		Response response = mClient.target(BASE_URL)
                .path("projects/" + project.getId())
                .request()
                .headers(headersMap)
                .put(Entity.text(projectJson.toString()));
		
		// assert response msg
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		assertEquals(200, response.getStatus());
		assertEquals("ok", responseJSON.getString("msg"));
	}
	
	@Test
	public void testDelete() {
		mCP = new CreateProject(1);
		mCP.exeCreateForDb();
		
		ArrayList<ProjectObject> projects = ProjectObject.getAllProjects();
		assertEquals(1, projects.size());
		long projectId = mCP.getAllProjects().get(0).getId();
		
		MultivaluedMap<String, Object> headersMap = TestableApi.getHeaders(mAccountId, mPlatformType);

		Response response = mClient.target(BASE_URL)
		        .path("projects/" + projectId)
		        .request()
		        .headers(headersMap)
		        .delete();
		
		assertEquals(200, response.getStatus());
		
		projects = ProjectObject.getAllProjects();
		assertEquals(0, projects.size());
	}
}
