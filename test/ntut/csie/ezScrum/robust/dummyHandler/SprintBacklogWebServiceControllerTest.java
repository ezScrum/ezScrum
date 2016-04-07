package ntut.csie.ezScrum.robust.dummyHandler;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.HttpServer;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.restful.mobile.controller.SprintBacklogWebServiceController;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

public class SprintBacklogWebServiceControllerTest extends JerseyTest {
	private Configuration mConfig;
	private ProjectObject mProject;
	private ResourceConfig mResourceConfig;
	private Client mClient;
	private HttpServer mHttpServer;
	private static String BASE_URL = "http://localhost:9527/ezScrum/web-service";
	private URI mBaseUri = URI.create(BASE_URL);
	private String mUsername = "admin";
	private String mPassword = "admin";

	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(SprintBacklogWebServiceController.class);
		return mResourceConfig;
	}

	@Before
	public void setUp() {
		// Set Test Mode
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// Encoding usename and password to base64
		mUsername = new String(Base64.encodeBase64(mUsername.getBytes()));
		mPassword = new String(Base64.encodeBase64(mPassword.getBytes()));
		// Create Project
		String name = "testProject";
		String displayName = "testDisplayName";
		String comment = "comment";
		String productOwner = "PO";
		long attachFileSize = 2;

		mProject = new ProjectObject(name);
		mProject.setDisplayName(displayName).setComment(comment)
				.setManager(productOwner).setAttachFileSize(attachFileSize)
				.save();
		
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
		mProject = null;
		mHttpServer = null;
		mClient = null;
	}

	@Test
	public void testGetTasksId_WithInvalidUsernameAndPassword() {
		// Create Sprint
		int sprintInterval = 2;
		int membersNumbre = 4;
		int hoursCanCommit = 150;
		int focusFactor = 80;

		String sprintGoal = "TEST_SPRINT_GOAL";
		String sprintDailyInfo = "TEST_SPRINT_DAILY_INFO";
		String sprintDemoPlace = "TEST_SPRINT_DEMO_PLACE";
		String sprintStartDate = "2015/05/28";
		String sprintDemoDate = "2015/06/11";
		String sprintEndDate = "2015/06/11";

		// create sprint object
		SprintObject sprint = new SprintObject(mProject.getId());
		sprint.setInterval(sprintInterval).setTeamSize(membersNumbre)
				.setAvailableHours(hoursCanCommit).setFocusFactor(focusFactor)
				.setGoal(sprintGoal).setStartDate(sprintStartDate)
				.setEndDate(sprintEndDate).setDailyInfo(sprintDailyInfo)
				.setDemoDate(sprintDemoDate).setDemoPlace(sprintDemoPlace)
				.save();
		
		// Create Story
		StoryObject story = new StoryObject(mProject.getId());
		story.setName("TEST_NAME").setNotes("TEST_NOTE")
				.setHowToDemo("TEST_HOW_TO_DEMO").setImportance(1).setValue(2)
				.setEstimate(3).setStatus(StoryObject.STATUS_DONE)
				.setSprintId(sprint.getId()).save();
		
		Response response = mClient.target(BASE_URL)
                .path("/" + mProject.getName() + "/sprint-backlog/" + sprint.getId() + "/" + story.getId() + "/task-id-list")
                .queryParam("username", "invalidUsername")
                .queryParam("password", "invalidPassword")
                .request()
                .get();
		
		String responseString = response.readEntity(String.class);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals("Invalid username or password", responseString);
	}
	
	@Test
	public void testGetTasksId_WithInvalidSprintId() {
		// Create Sprint
		int sprintInterval = 2;
		int membersNumbre = 4;
		int hoursCanCommit = 150;
		int focusFactor = 80;

		String sprintGoal = "TEST_SPRINT_GOAL";
		String sprintDailyInfo = "TEST_SPRINT_DAILY_INFO";
		String sprintDemoPlace = "TEST_SPRINT_DEMO_PLACE";
		String sprintStartDate = "2015/05/28";
		String sprintDemoDate = "2015/06/11";
		String sprintEndDate = "2015/06/11";

		// create sprint object
		SprintObject sprint = new SprintObject(mProject.getId());
		sprint.setInterval(sprintInterval).setTeamSize(membersNumbre)
				.setAvailableHours(hoursCanCommit).setFocusFactor(focusFactor)
				.setGoal(sprintGoal).setStartDate(sprintStartDate)
				.setEndDate(sprintEndDate).setDailyInfo(sprintDailyInfo)
				.setDemoDate(sprintDemoDate).setDemoPlace(sprintDemoPlace)
				.save();
		
		// Create Story
		StoryObject story = new StoryObject(mProject.getId());
		story.setName("TEST_NAME").setNotes("TEST_NOTE")
				.setHowToDemo("TEST_HOW_TO_DEMO").setImportance(1).setValue(2)
				.setEstimate(3).setStatus(StoryObject.STATUS_DONE)
				.setSprintId(sprint.getId()).save();
		
		// set invalid sprint id
		long invalidSprintId = -99;
		Response response = mClient.target(BASE_URL)
                .path("/" + mProject.getName() + "/sprint-backlog/" + invalidSprintId + "/" + story.getId() + "/task-id-list")
                .queryParam("username", mUsername)
                .queryParam("password", mPassword)
                .request()
                .get();
		
		String responseString = response.readEntity(String.class);
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
		assertEquals("Invalid path parameter", responseString);
	}
	
	@Test
	public void testGetTasksId_WithInvalidStoryId() {
		// Create Sprint
		int sprintInterval = 2;
		int membersNumbre = 4;
		int hoursCanCommit = 150;
		int focusFactor = 80;

		String sprintGoal = "TEST_SPRINT_GOAL";
		String sprintDailyInfo = "TEST_SPRINT_DAILY_INFO";
		String sprintDemoPlace = "TEST_SPRINT_DEMO_PLACE";
		String sprintStartDate = "2015/05/28";
		String sprintDemoDate = "2015/06/11";
		String sprintEndDate = "2015/06/11";

		// create sprint object
		SprintObject sprint = new SprintObject(mProject.getId());
		sprint.setInterval(sprintInterval).setTeamSize(membersNumbre)
				.setAvailableHours(hoursCanCommit).setFocusFactor(focusFactor)
				.setGoal(sprintGoal).setStartDate(sprintStartDate)
				.setEndDate(sprintEndDate).setDailyInfo(sprintDailyInfo)
				.setDemoDate(sprintDemoDate).setDemoPlace(sprintDemoPlace)
				.save();
		
		// Create Story
		StoryObject story = new StoryObject(mProject.getId());
		story.setName("TEST_NAME").setNotes("TEST_NOTE")
				.setHowToDemo("TEST_HOW_TO_DEMO").setImportance(1).setValue(2)
				.setEstimate(3).setStatus(StoryObject.STATUS_DONE)
				.setSprintId(sprint.getId()).save();
		
		// set invalid story id
		long invalidStoryId = -99;
		Response response = mClient.target(BASE_URL)
                .path("/" + mProject.getName() + "/sprint-backlog/" + sprint.getId() + "/" + invalidStoryId + "/task-id-list")
                .queryParam("username", mUsername)
                .queryParam("password", mPassword)
                .request()
                .get();
		
		String responseString = response.readEntity(String.class);
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
		assertEquals("Invalid path parameter", responseString);
	}
}

