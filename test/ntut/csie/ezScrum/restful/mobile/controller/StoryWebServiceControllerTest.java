package ntut.csie.ezScrum.restful.mobile.controller;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

public class StoryWebServiceControllerTest {
	private static String SERVER_URL = "http://127.0.0.1:8080/ezScrum/web-service";
	private static String API_URL = "http://127.0.0.1:8080/ezScrum/web-service/%s/story/%s?username=%s&password=%s";
	private static HttpServer mServer;
	private HttpClient mHttpClient;
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
	
	@Before
	public void setUp() throws Exception {
		// start server
		mServer = HttpServerFactory.create(SERVER_URL);
		mServer.start();
		// get http client
		mHttpClient = HttpClientBuilder.create().build();
		
		// change to test mode
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// initial SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// create project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();
		
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
	}
	
	@After
	public void tearDown() {
		// stop server
		mServer.stop(0);
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
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
	public void testCreateStory() throws Exception {
		// 預設已經新增五個 stories
		ArrayList<StoryObject> stories = mProject.getStories();
		assertEquals(mStoryCount, stories.size());
		
		// initial request data
		JSONObject storyJson = new JSONObject();
		storyJson.put("name", "TEST_NAME").put("notes", "TEST_NOTES")
				.put("how_to_demo", "TEST_HOW_TO_DEMO").put("importance", 99)
				.put("value", 15).put("estimate", 21).put("status", 0)
				.put("sprint_id", -1).put("tags", "");
		String URL = String.format(API_URL, mProjectName, "create", mUsername, mPassword);
		BasicHttpEntity entity = new BasicHttpEntity();
		entity.setContent(new ByteArrayInputStream(storyJson.toString().getBytes()));
		entity.setContentEncoding("utf-8");
		HttpPost httpPost = new HttpPost(URL);
		httpPost.setEntity(entity);
		String result = EntityUtils.toString(mHttpClient.execute(httpPost).getEntity(), StandardCharsets.UTF_8);
		JSONObject response = new JSONObject(result);
		
		// 新增一個 story，project 內的 story 要有六個
		stories = mProject.getStories();
		assertEquals(mStoryCount + 1, stories.size());
		// 對回傳的 JSON 做 assert
		assertEquals("SUCCESS", response.getString("status"));
		assertEquals(stories.get(stories.size()-1).getId(), response.getLong("storyId"));
	}
	
	@Test
	public void testUpdateStory() throws Exception {
		StoryObject story = mASTS.getStories().get(0);
		// initial request data
		JSONObject storyJson = new JSONObject();
		storyJson.put("id", story.getId()).put("name", "顆顆").put("notes", "崩潰")
				.put("how_to_demo", "做不完").put("importance", 99)
				.put("value", 15).put("estimate", 21).put("status", 0)
				.put("sprint_id", -1).put("tags", "");
		String URL = String.format(API_URL, mProjectName, "update", mUsername, mPassword);
		BasicHttpEntity entity = new BasicHttpEntity();
		entity.setContent(new ByteArrayInputStream(storyJson.toString().getBytes()));
		entity.setContentEncoding("utf-8");
		HttpPut httpPut = new HttpPut(URL);
		httpPut.setEntity(entity);
		String result = EntityUtils.toString(mHttpClient.execute(httpPut).getEntity(), StandardCharsets.UTF_8);
		JSONObject response = new JSONObject(result);
		
		assertEquals(storyJson.getLong("id"), response.getLong("id"));
		assertEquals(storyJson.getString("name"), response.getString("name"));
		assertEquals(storyJson.getString("notes"), response.getString("notes"));
		assertEquals(storyJson.getString("how_to_demo"), response.getString("how_to_demo"));
		assertEquals(storyJson.getInt("importance"), response.getInt("importance"));
		assertEquals(storyJson.getInt("value"), response.getInt("value"));
		assertEquals(storyJson.getInt("estimate"), response.getInt("estimate"));
		assertEquals(storyJson.getInt("status"), response.getInt("status"));
		assertEquals(storyJson.getLong("sprint_id"), response.getLong("sprint_id"));
		assertEquals(9, response.getJSONArray("histories").length());
		assertEquals(0, response.getJSONArray("tags").length());
	}
	
	@Test
	public void testGetTasksInStory() throws Exception {
		// create 3 tasks to story #1
		int taskCount = 3;
		AddTaskToStory ATTS = new AddTaskToStory(taskCount, 5, mASTS, mCP);
		ATTS.exe();
		StoryObject story = mASTS.getStories().get(0);
		// initial request data
		String URL = String.format(API_URL, mProjectName, story.getId() + "/tasks", mUsername, mPassword);
		HttpGet httpGet = new HttpGet(URL);
		String result = EntityUtils.toString(mHttpClient.execute(httpGet).getEntity(), StandardCharsets.UTF_8);
		JSONObject response = new JSONObject(result);
		
		assertEquals(taskCount, response.getJSONArray("tasks").length());
	}
	
	@Test
	public void testAddExistedTask() throws Exception {
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
		String URL = String.format(API_URL, mProjectName, story.getId() + "/add-existed-task", mUsername, mPassword);
		BasicHttpEntity entity = new BasicHttpEntity();
		entity.setContent(new ByteArrayInputStream(taskIdJsonString.getBytes()));
		entity.setContentEncoding("utf-8");
		HttpPost httpPost = new HttpPost(URL);
		httpPost.setEntity(entity);
		mHttpClient.execute(httpPost);
		
		story.reload();
		assertEquals(2, story.getTasks().size());
	}
}
