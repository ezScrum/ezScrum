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
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.SprintJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.security.SecurityModule;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.databaseEnum.SprintEnum;

public class SprintRESTfulApiTest extends JerseyTest {
	private Configuration mConfig;
	private CreateProject mCP;
	private ResourceConfig mResourceConfig;
	private Client mClient;
	private HttpServer mHttpServer;
	private static String BASE_URL = "http://localhost:8080/ezScrum/resource/";
	private URI mBaseUri = URI.create(BASE_URL);

	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(SprintRESTfulApi.class);
		return mResourceConfig;
	}

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() {
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
	public void testCreateSprint() throws JSONException {
		// Test Data
		String goal = "TEST_SPRINT_GOAL";
		int interval = 2;
		int teamSize = 4;
		int availableHours = 200;
		int focusFactor = 80;
		String startDate = "2015/11/24";
		String dueDate = "2015/12/07";
		String demoDate = "2015/12/07";
		String demoPlace = "Lab1321";
		String dailyInfo = "Lab1321@13:30";
		ProjectObject project = mCP.getAllProjects().get(0);
		
		JSONObject sprintJSON = new JSONObject();
		sprintJSON.put(SprintJSONEnum.GOAL, goal);
		sprintJSON.put(SprintJSONEnum.INTERVAL, interval);
		sprintJSON.put(SprintJSONEnum.TEAM_SIZE, teamSize);
		sprintJSON.put(SprintJSONEnum.AVAILABLE_HOURS, availableHours);
		sprintJSON.put(SprintJSONEnum.FOCUS_FACTOR, focusFactor);
		sprintJSON.put(SprintJSONEnum.START_DATE, startDate);
		sprintJSON.put(SprintJSONEnum.DUE_DATE, dueDate);
		sprintJSON.put(SprintJSONEnum.DEMO_DATE, demoDate);
		sprintJSON.put(SprintJSONEnum.DEMO_PLACE, demoPlace);
		sprintJSON.put(SprintJSONEnum.DAILY_INFO, dailyInfo);
		
		// Call '/projects/{projectId}/sprints' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		              "/sprints")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, SecurityModule.ADMIN_MD5_USERNAME)
		        .header(SecurityModule.PASSWORD_HEADER, SecurityModule.ADMIN_MD5_PASSWORD)
		        .post(Entity.text(sprintJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertTrue(contentJSON.getLong(SprintEnum.ID) != -1);
		assertEquals(goal, contentJSON.getString(SprintJSONEnum.GOAL));
		assertEquals(interval, contentJSON.getInt(SprintJSONEnum.INTERVAL));
		assertEquals(teamSize, contentJSON.getInt(SprintJSONEnum.TEAM_SIZE));
		assertEquals(availableHours, contentJSON.getInt(SprintJSONEnum.AVAILABLE_HOURS));
		assertEquals(focusFactor, contentJSON.getInt(SprintJSONEnum.FOCUS_FACTOR));
		assertEquals(startDate, contentJSON.getString(SprintJSONEnum.START_DATE));
		assertEquals(dueDate, contentJSON.getString(SprintJSONEnum.DUE_DATE));
		assertEquals(demoDate, contentJSON.getString(SprintJSONEnum.DEMO_DATE));
		assertEquals(demoPlace, contentJSON.getString(SprintJSONEnum.DEMO_PLACE));
		assertEquals(dailyInfo, contentJSON.getString(SprintJSONEnum.DAILY_INFO));
	}
	
	@Test
	public void testCreateSprint_AccountIsInvalid() throws JSONException {
		String wrongAdminUsername = "wrongAdminUsername";
		String wrongAdminPassword = "wrongAdminPassword";
		
		// Test Data
		String goal = "TEST_SPRINT_GOAL";
		int interval = 2;
		int teamSize = 4;
		int availableHours = 200;
		int focusFactor = 80;
		String startDate = "2015/11/24";
		String dueDate = "2015/12/07";
		String demoDate = "2015/12/07";
		String demoPlace = "Lab1321";
		String dailyInfo = "Lab1321@13:30";
		ProjectObject project = mCP.getAllProjects().get(0);
		
		JSONObject sprintJSON = new JSONObject();
		sprintJSON.put(SprintJSONEnum.GOAL, goal);
		sprintJSON.put(SprintJSONEnum.INTERVAL, interval);
		sprintJSON.put(SprintJSONEnum.TEAM_SIZE, teamSize);
		sprintJSON.put(SprintJSONEnum.AVAILABLE_HOURS, availableHours);
		sprintJSON.put(SprintJSONEnum.FOCUS_FACTOR, focusFactor);
		sprintJSON.put(SprintJSONEnum.START_DATE, startDate);
		sprintJSON.put(SprintJSONEnum.DUE_DATE, dueDate);
		sprintJSON.put(SprintJSONEnum.DEMO_DATE, demoDate);
		sprintJSON.put(SprintJSONEnum.DEMO_PLACE, demoPlace);
		sprintJSON.put(SprintJSONEnum.DAILY_INFO, dailyInfo);
		
		// Call '/projects/{projectId}/sprints' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		              "/sprints")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, wrongAdminUsername)
		        .header(SecurityModule.PASSWORD_HEADER, wrongAdminPassword)
		        .post(Entity.text(sprintJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
	
	@Test
	public void testCreateSprint_AccountIsEmpty() throws JSONException {
		String wrongAdminUsername = "";
		String wrongAdminPassword = "";
		
		// Test Data
		String goal = "TEST_SPRINT_GOAL";
		int interval = 2;
		int teamSize = 4;
		int availableHours = 200;
		int focusFactor = 80;
		String startDate = "2015/11/24";
		String dueDate = "2015/12/07";
		String demoDate = "2015/12/07";
		String demoPlace = "Lab1321";
		String dailyInfo = "Lab1321@13:30";
		ProjectObject project = mCP.getAllProjects().get(0);
		
		JSONObject sprintJSON = new JSONObject();
		sprintJSON.put(SprintJSONEnum.GOAL, goal);
		sprintJSON.put(SprintJSONEnum.INTERVAL, interval);
		sprintJSON.put(SprintJSONEnum.TEAM_SIZE, teamSize);
		sprintJSON.put(SprintJSONEnum.AVAILABLE_HOURS, availableHours);
		sprintJSON.put(SprintJSONEnum.FOCUS_FACTOR, focusFactor);
		sprintJSON.put(SprintJSONEnum.START_DATE, startDate);
		sprintJSON.put(SprintJSONEnum.DUE_DATE, dueDate);
		sprintJSON.put(SprintJSONEnum.DEMO_DATE, demoDate);
		sprintJSON.put(SprintJSONEnum.DEMO_PLACE, demoPlace);
		sprintJSON.put(SprintJSONEnum.DAILY_INFO, dailyInfo);
		
		// Call '/projects/{projectId}/sprints' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		              "/sprints")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, wrongAdminUsername)
		        .header(SecurityModule.PASSWORD_HEADER, wrongAdminPassword)
		        .post(Entity.text(sprintJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
	
	@Test
	public void testCreateSprint_AccountIsNull() throws JSONException {
		String wrongAdminUsername = null;
		String wrongAdminPassword = null;
		
		// Test Data
		String goal = "TEST_SPRINT_GOAL";
		int interval = 2;
		int teamSize = 4;
		int availableHours = 200;
		int focusFactor = 80;
		String startDate = "2015/11/24";
		String dueDate = "2015/12/07";
		String demoDate = "2015/12/07";
		String demoPlace = "Lab1321";
		String dailyInfo = "Lab1321@13:30";
		ProjectObject project = mCP.getAllProjects().get(0);
		
		JSONObject sprintJSON = new JSONObject();
		sprintJSON.put(SprintJSONEnum.GOAL, goal);
		sprintJSON.put(SprintJSONEnum.INTERVAL, interval);
		sprintJSON.put(SprintJSONEnum.TEAM_SIZE, teamSize);
		sprintJSON.put(SprintJSONEnum.AVAILABLE_HOURS, availableHours);
		sprintJSON.put(SprintJSONEnum.FOCUS_FACTOR, focusFactor);
		sprintJSON.put(SprintJSONEnum.START_DATE, startDate);
		sprintJSON.put(SprintJSONEnum.DUE_DATE, dueDate);
		sprintJSON.put(SprintJSONEnum.DEMO_DATE, demoDate);
		sprintJSON.put(SprintJSONEnum.DEMO_PLACE, demoPlace);
		sprintJSON.put(SprintJSONEnum.DAILY_INFO, dailyInfo);
		
		// Call '/projects/{projectId}/sprints' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		              "/sprints")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, wrongAdminUsername)
		        .header(SecurityModule.PASSWORD_HEADER, wrongAdminPassword)
		        .post(Entity.text(sprintJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
}
