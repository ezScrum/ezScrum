package ntut.csie.ezScrum.restful.mobile.controller;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
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
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class StoryWebServiceControllerTest extends JerseyTest {
	private ResourceConfig mResourceConfig;
	private static String BASE_URL = "http://localhost:9527/ezScrum/web-service";
	private URI mBaseUri = URI.create(BASE_URL);
	private Client mClient;
	private HttpServer mHttpServer;
	private String mUsername = "admin";
	private String mPassword = "admin";
	private int mProjectCount = 1;
	private int mSprintCount = 1;
	private int mStoryCount = 5;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private Configuration mConfig;
	private ProjectObject mProject;
	private String mProjectName;
	
	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(StoryWebServiceController.class);
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

		// create project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreateForDb();
		
		// create sprint
		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();
		
		// create story
		mASTS = new AddStoryToSprint(mStoryCount, 1, mCS, mCP,
				CreateProductBacklog.COLUMN_TYPE_EST);
		mASTS.exe();
		
		// create account
		String TEST_ACCOUNT_NAME = "TEST_ACCOUNT_1";
		String TEST_ACCOUNT_NICKNAME = "TEST_ACCOUNT_NICKNAME_1";
		String TEST_ACCOUNT_PASSWORD = "TEST_ACCOUNT_PASSWORD_1";
		String TEST_ACCOUNT_EMAIL = "TEST_ACCOUNT_EMAIL_1";

		AccountObject account = new AccountObject(TEST_ACCOUNT_NAME);
		account.setNickName(TEST_ACCOUNT_NICKNAME);
		account.setPassword(TEST_ACCOUNT_PASSWORD);
		account.setEmail(TEST_ACCOUNT_EMAIL);
		account.setEnable(true);
		account.save();
		account.reload();

		mProject = mCP.getAllProjects().get(0);
		mProjectName = mProject.getName();
		
		
		mUsername = new String(Base64.encodeBase64(mUsername.getBytes()));
		mPassword = new String(Base64.encodeBase64(mPassword.getBytes()));
		
		// start server
		mHttpServer = JdkHttpServerFactory.createHttpServer(mBaseUri, mResourceConfig, true);

		// Create Client
		mClient = ClientBuilder.newClient();
	}
	
	@After
	public void tearDown() {
		// stop server
		mHttpServer.stop(0);
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mConfig.setTestMode(false);
		mConfig.save();

		// release
		mCP = null;
		mCS = null;
		mASTS = null;
		mProject = null;
		mConfig = null;
	}
	
	@Test
	public void testCreateStory() throws JSONException {
		// 預設已經新增五個 stories
		ArrayList<StoryObject> stories = mProject.getStories();
		assertEquals(mStoryCount, stories.size());
		
		// initial request data
		JSONObject storyJson = new JSONObject();
		storyJson.put("name", "TEST_NAME").put("notes", "TEST_NOTES")
				.put("how_to_demo", "TEST_HOW_TO_DEMO").put("importance", 99)
				.put("value", 15).put("estimate", 21).put("status", 0)
				.put("sprint_id", -1).put("tags", "");
		
		Response response = mClient.target(BASE_URL)
                .path("/" + mProjectName + "/story/create")
                .queryParam("username", mUsername)
                .queryParam("password", mPassword)
                .request()
                .post(Entity.text(storyJson.toString()));
		
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		
		// 新增一個 story，project 內的 story 要有六個
		stories = mProject.getStories();
		assertEquals(mStoryCount + 1, stories.size());
		// 對回傳的 JSON 做 assert
		assertEquals("SUCCESS", responseJSON.getString("status"));
		assertEquals(stories.get(stories.size()-1).getId(), responseJSON.getLong("storyId"));
	}
	
	@Test
	public void testUpdateStory() throws JSONException {
		StoryObject story = mASTS.getStories().get(0);
		// initial request data
		JSONObject storyJson = new JSONObject();
		storyJson.put("id", story.getId()).put("name", "顆顆").put("notes", "崩潰")
				.put("how_to_demo", "做不完").put("importance", 99)
				.put("value", 15).put("estimate", 21).put("status", StoryObject.STATUS_DONE)
				.put("sprint_id", -1).put("tags", "").put("serial_id", story.getSerialId());
		
		Response response = mClient.target(BASE_URL)
                .path("/" + mProjectName + "/story/update")
                .queryParam("username", mUsername)
                .queryParam("password", mPassword)
                .request()
                .put(Entity.text(storyJson.toString()));
		
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		
		assertEquals(storyJson.getLong("id"), responseJSON.getLong("id"));
		assertEquals(storyJson.getString("name"), responseJSON.getString("name"));
		assertEquals(storyJson.getString("notes"), responseJSON.getString("notes"));
		assertEquals(storyJson.getString("how_to_demo"), responseJSON.getString("how_to_demo"));
		assertEquals(storyJson.getInt("importance"), responseJSON.getInt("importance"));
		assertEquals(storyJson.getInt("value"), responseJSON.getInt("value"));
		assertEquals(storyJson.getInt("estimate"), responseJSON.getInt("estimate"));
		assertEquals("closed", responseJSON.getString("status"));
		assertEquals(storyJson.getLong("sprint_id"), responseJSON.getLong("sprint_id"));
		assertEquals(10, responseJSON.getJSONArray("histories").length());
		assertEquals(0, responseJSON.getJSONArray("tags").length());
	}
	
	@Test
	public void testGetTasksInStory() throws Exception {
		// create 3 tasks to story #1
		int taskCount = 3;
		AddTaskToStory ATTS = new AddTaskToStory(taskCount, 5, mASTS, mCP);
		ATTS.exe();
		StoryObject story = mASTS.getStories().get(0);
		
		Response response = mClient.target(BASE_URL)
                .path("/" + mProjectName + "/story/" + story.getId() + "/tasks")
                .queryParam("username", mUsername)
                .queryParam("password", mPassword)
                .request()
                .get();
		
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		
		assertEquals(taskCount, responseJSON.getJSONArray("tasks").length());
	}
	
	@Test
	public void testAddExistedTask() {
		// create 3 tasks in project
		TaskObject task1 = new TaskObject(mProject.getId());
		task1.setName("TASK_NAME1").save();
		
		TaskObject task2 = new TaskObject(mProject.getId());
		task2.setName("TASK_NAME2").save();
		
		TaskObject task3 = new TaskObject(mProject.getId());
		task3.setName("TASK_NAME3").save();
		
		// initial request data，add task#1 & #3 to story#1
		StoryObject story = mASTS.getStories().get(0);
		String taskIdJsonString = String.format("[%s, %s]", task1.getId(), task2.getId());

		mClient.target(BASE_URL)
                .path("/" + mProjectName + "/story/" + story.getId() + "/add-existed-task")
                .queryParam("username", mUsername)
                .queryParam("password", mPassword)
                .request()
                .post(Entity.text(taskIdJsonString));
		story.reload();
		
		assertEquals(2, story.getTasks().size());
	}
}
