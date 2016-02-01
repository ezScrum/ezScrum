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
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ReleaseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.security.SecurityModule;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.databaseEnum.ReleaseEnum;

public class ReleaseRESTfulApiTest extends JerseyTest {
	private Configuration mConfig;
	private CreateProject mCP;
	private ResourceConfig mResourceConfig;
	private Client mClient;
	private HttpServer mHttpServer;
	private static String BASE_URL = "http://localhost:8080/ezScrum/resource/";
	private URI mBaseUri = URI.create(BASE_URL);

	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(ReleaseRESTfulApi.class);
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
	public void testCreateRelease() throws JSONException {
		// Test Data
		String name = "TEST_RELEASE_NAME";
		String description = "TEST_RELEASE_DESCRIPTION";
		String startDate = "2015/11/24";
		String dueDate = "2015/12/21";
		ProjectObject project = mCP.getAllProjects().get(0);

		JSONObject releaseJSON = new JSONObject();
		releaseJSON.put(ReleaseJSONEnum.NAME, name);
		releaseJSON.put(ReleaseJSONEnum.DESCRIPTION, description);
		releaseJSON.put(ReleaseJSONEnum.START_DATE, startDate);
		releaseJSON.put(ReleaseJSONEnum.DUE_DATE, dueDate);

		// Call '/projects/{projectId}/releases' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		              "/releases")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, SecurityModule.ADMIN_MD5_USERNAME)
		        .header(SecurityModule.PASSWORD_HEADER, SecurityModule.ADMIN_MD5_PASSWORD)
		        .post(Entity.text(releaseJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = jsonResponse.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertTrue(contentJSON.getLong(ReleaseEnum.ID) != -1);
		ReleaseObject release = ReleaseObject.get(contentJSON.getLong(ReleaseEnum.ID));
		assertEquals(name, release.getName());
		assertEquals(description, release.getDescription());
		assertEquals(startDate, release.getStartDateString());
		assertEquals(dueDate, release.getDueDateString());
	}
	
	@Test
	public void testCreateRelease_AccountIsInvalid() throws JSONException {
		// Test Data
		String wrongAdminUsername = "wrongAdminUsername";
		String wrongAdminPassword = "wrongAdminPassword";
		
		String name = "TEST_RELEASE_NAME";
		String description = "TEST_RELEASE_DESCRIPTION";
		String startDate = "2015/11/24";
		String dueDate = "2015/12/21";
		ProjectObject project = mCP.getAllProjects().get(0);

		JSONObject releaseJSON = new JSONObject();
		releaseJSON.put(ReleaseJSONEnum.NAME, name);
		releaseJSON.put(ReleaseJSONEnum.DESCRIPTION, description);
		releaseJSON.put(ReleaseJSONEnum.START_DATE, startDate);
		releaseJSON.put(ReleaseJSONEnum.DUE_DATE, dueDate);

		// Call '/projects/{projectId}/releases' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		              "/releases")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, wrongAdminUsername)
		        .header(SecurityModule.PASSWORD_HEADER, wrongAdminPassword)
		        .post(Entity.text(releaseJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = jsonResponse.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertTrue(contentJSON.getLong(ReleaseEnum.ID) != -1);
		ReleaseObject release = ReleaseObject.get(contentJSON.getLong(ReleaseEnum.ID));
		assertEquals(name, release.getName());
		assertEquals(description, release.getDescription());
		assertEquals(startDate, release.getStartDateString());
		assertEquals(dueDate, release.getDueDateString());
	}
	
	@Test
	public void testCreateRelease_AccountIsEmpty() throws JSONException {
		// Test Data
		String wrongAdminUsername = "";
		String wrongAdminPassword = "";
		
		String name = "TEST_RELEASE_NAME";
		String description = "TEST_RELEASE_DESCRIPTION";
		String startDate = "2015/11/24";
		String dueDate = "2015/12/21";
		ProjectObject project = mCP.getAllProjects().get(0);

		JSONObject releaseJSON = new JSONObject();
		releaseJSON.put(ReleaseJSONEnum.NAME, name);
		releaseJSON.put(ReleaseJSONEnum.DESCRIPTION, description);
		releaseJSON.put(ReleaseJSONEnum.START_DATE, startDate);
		releaseJSON.put(ReleaseJSONEnum.DUE_DATE, dueDate);

		// Call '/projects/{projectId}/releases' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		              "/releases")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, wrongAdminUsername)
		        .header(SecurityModule.PASSWORD_HEADER, wrongAdminPassword)
		        .post(Entity.text(releaseJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = jsonResponse.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertTrue(contentJSON.getLong(ReleaseEnum.ID) != -1);
		ReleaseObject release = ReleaseObject.get(contentJSON.getLong(ReleaseEnum.ID));
		assertEquals(name, release.getName());
		assertEquals(description, release.getDescription());
		assertEquals(startDate, release.getStartDateString());
		assertEquals(dueDate, release.getDueDateString());
	}
	
	@Test
	public void testCreateRelease_AccountIsNull() throws JSONException {
		// Test Data
		String wrongAdminUsername = null;
		String wrongAdminPassword = null;
		
		String name = "TEST_RELEASE_NAME";
		String description = "TEST_RELEASE_DESCRIPTION";
		String startDate = "2015/11/24";
		String dueDate = "2015/12/21";
		ProjectObject project = mCP.getAllProjects().get(0);

		JSONObject releaseJSON = new JSONObject();
		releaseJSON.put(ReleaseJSONEnum.NAME, name);
		releaseJSON.put(ReleaseJSONEnum.DESCRIPTION, description);
		releaseJSON.put(ReleaseJSONEnum.START_DATE, startDate);
		releaseJSON.put(ReleaseJSONEnum.DUE_DATE, dueDate);

		// Call '/projects/{projectId}/releases' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		              "/releases")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, wrongAdminUsername)
		        .header(SecurityModule.PASSWORD_HEADER, wrongAdminPassword)
		        .post(Entity.text(releaseJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = jsonResponse.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertTrue(contentJSON.getLong(ReleaseEnum.ID) != -1);
		ReleaseObject release = ReleaseObject.get(contentJSON.getLong(ReleaseEnum.ID));
		assertEquals(name, release.getName());
		assertEquals(description, release.getDescription());
		assertEquals(startDate, release.getStartDateString());
		assertEquals(dueDate, release.getDueDateString());
	}
}
