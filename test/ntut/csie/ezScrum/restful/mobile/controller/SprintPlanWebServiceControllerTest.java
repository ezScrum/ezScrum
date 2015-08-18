package ntut.csie.ezScrum.restful.mobile.controller;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.restful.mobile.support.ConvertSprintBacklog;
import ntut.csie.ezScrum.restful.mobile.util.SprintUtil;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.databasEnum.SprintEnum;
import ntut.csie.ezScrum.web.databasEnum.StoryEnum;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

public class SprintPlanWebServiceControllerTest {
	private static String SERVER_URL = "http://127.0.0.1:8080/ezScrum/web-service";
	private static String API_URL = "http://127.0.0.1:8080/ezScrum/web-service/%s/sprint/%s?username=%s&password=%s";
	private String mUsername = "admin";
	private String mPassword = "admin";
	private int mProjectCount = 1;
	private int mSprintCount = 1;
	private int mStoryCount = 3;
	
	private static HttpServer mServer;
	private HttpClient mHttpClient;
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
		mASTS = new AddStoryToSprint(mStoryCount, 1, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mASTS.exe();
		
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
	public void testCreateSprint() throws Exception {
		// get existed sprints
		ArrayList<SprintObject> sprints = mProject.getSprints();
		// assert
		assertEquals(mSprintCount, sprints.size());
		
		// Test Data
		int interval = 2;
		int members = 4;
		int availableHours = 100;
		int focusFactor = 50;
		String sprintGoal = "TEST_SPRINT_GOAL";
		String startDate = "2015/07/01";
		String demoDate = "2015/07/15";
		String dueDate = "2015/07/15";
		String demoPlace = "Lab1321";
		String dailyInfo = "TEST_DAILY_INFO";
		
		// prepare request data
		JSONObject sprintJson = new JSONObject();
		sprintJson.put(SprintEnum.INTERVAL, interval)
		          .put(SprintEnum.MEMBERS, members)
		          .put(SprintEnum.AVAILABLE_HOURS, availableHours)
		          .put(SprintEnum.FOCUS_FACTOR, focusFactor)
		          .put(SprintEnum.GOAL, sprintGoal)
		          .put(SprintEnum.START_DATE, startDate)
		          .put(SprintEnum.DEMO_DATE, demoDate)
		          .put(SprintEnum.DEMO_PLACE, demoPlace)
		          .put(SprintEnum.DAILY_INFO, dailyInfo)
		          .put(SprintEnum.DUE_DATE, dueDate);
		
		// Assemble URL
		String URL = String.format(API_URL, mProjectName, "create", mUsername, mPassword);
		
		// Send Http Request
		BasicHttpEntity entity = new BasicHttpEntity();
		entity.setContent(new ByteArrayInputStream(sprintJson.toString().getBytes()));
		entity.setContentEncoding(StandardCharsets.UTF_8.name());
		HttpPost httpPost = new HttpPost(URL);
		httpPost.setEntity(entity);
		String result = EntityUtils.toString(mHttpClient.execute(httpPost).getEntity(), StandardCharsets.UTF_8);
		String expectedJsonString = ConvertSprintBacklog.getSprintBacklogJsonString(mProject.getCurrentSprint());
		// assert
		assertEquals(expectedJsonString, result);
	}
	
	@Test
	public void testDeleteSprint() throws Exception {
		// get existed sprints
		ArrayList<SprintObject> sprints = mProject.getSprints();
		// assert
		assertEquals(mSprintCount, sprints.size());
		
		// Assemble URL
		String URL = String.format(API_URL, mProjectName, "delete/" + sprints.get(0).getId(), mUsername, mPassword);

		// Send Http Request
		HttpDelete httpDelete = new HttpDelete(URL);
		HttpResponse httpResponse = mHttpClient.execute(httpDelete);
		String result = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
		
		// assert
		assertEquals("", result);
	}
	
	@Test
	public void testUpdateSprint() throws Exception {
		// Test Data
		int interval = 2;
		int members = 4;
		int availableHours = 80;
		int focusFactor = 90;
		String sprintGoal = "TEST_SPRINT_GOAL_NEW";
		String startDate = "2013/07/01";
		String demoDate = "2013/07/22";
		String demoPlace = "TEST_DEMO_PLACE";
		String dailyInfo = "TEST_DAILY_INFO_NEW";
		String dueDate = "2013/07/22";

		SprintObject updateSpint = mCS.getSprints().get(0);
		// prepare request data
		JSONObject sprintJson = new JSONObject();
		sprintJson.put(SprintEnum.ID, updateSpint.getId())
		        .put(SprintEnum.INTERVAL, interval)
		        .put(SprintEnum.MEMBERS, members)
		        .put(SprintEnum.AVAILABLE_HOURS, availableHours)
		        .put(SprintEnum.FOCUS_FACTOR, focusFactor)
		        .put(SprintEnum.GOAL, sprintGoal)
		        .put(SprintEnum.START_DATE, startDate)
		        .put(SprintEnum.DEMO_DATE, demoDate)
		        .put(SprintEnum.DEMO_PLACE, demoPlace)
		        .put(SprintEnum.DAILY_INFO, dailyInfo)
		        .put(SprintEnum.DUE_DATE, dueDate);

		// Assemble URL
		String URL = String.format(API_URL, mProjectName, "update", mUsername, mPassword);

		// Send Http Request
		BasicHttpEntity entity = new BasicHttpEntity();
		entity.setContent(new ByteArrayInputStream(sprintJson.toString().getBytes()));
		entity.setContentEncoding(StandardCharsets.UTF_8.name());
		HttpPut httpPut = new HttpPut(URL);
		httpPut.setEntity(entity);
		EntityUtils.toString(mHttpClient.execute(httpPut).getEntity(), StandardCharsets.UTF_8);

		// get Sprint
		SprintObject sprint = SprintObject.get(updateSpint.getId());

		// assert
		assertEquals(interval, sprint.getInterval());
		assertEquals(members, sprint.getMembersAmount());
		assertEquals(availableHours, sprint.getHoursCanCommit());
		assertEquals(focusFactor, sprint.getFocusFactor());
		assertEquals(sprintGoal, sprint.getSprintGoal());
		assertEquals(startDate, sprint.getStartDateString());
		assertEquals(demoDate, sprint.getDemoDateString());
		assertEquals(demoPlace, sprint.getDemoPlace());
		assertEquals(dailyInfo, sprint.getDailyInfo());
	}
	
	@Test
	public void testGetAllSprints() throws Exception {
		// Assemble URL
		String URL = String.format(API_URL, mProjectName, "all", mUsername, mPassword);

		// Send Http Request
		HttpGet httpGet = new HttpGet(URL);
		String result = EntityUtils.toString(mHttpClient.execute(httpGet).getEntity(), StandardCharsets.UTF_8);
		JSONArray response = new JSONArray(result);

		// get Sprints
		ArrayList<SprintObject> sprints = mProject.getSprints();

		// assert
		assertEquals(sprints.size(), response.length());
		
		for (int i = 0; i < response.length(); i++) {
			assertEquals(sprints.get(i).getId(), response.getJSONObject(i).getLong(SprintUtil.TAG_ID));
			assertEquals(sprints.get(i).getInterval(), response.getJSONObject(i).getInt(SprintUtil.TAG_INTERVAL));
			assertEquals(sprints.get(i).getMembersAmount(), response.getJSONObject(i).getInt(SprintUtil.TAG_MEMBERS));
			assertEquals(sprints.get(i).getHoursCanCommit(), response.getJSONObject(i).getInt(SprintUtil.TAG_HOURS_CAN_COMMIT));
			assertEquals(sprints.get(i).getFocusFactor(), response.getJSONObject(i).getInt(SprintUtil.TAG_FOCUS_FACTOR));
			assertEquals(sprints.get(i).getSprintGoal(), response.getJSONObject(i).getString(SprintUtil.TAG_SPRINT_GOAL));
			assertEquals(sprints.get(i).getStartDateString(), response.getJSONObject(i).getString(SprintUtil.TAG_START_DATE));
			assertEquals(sprints.get(i).getDemoDateString(), response.getJSONObject(i).getString(SprintUtil.TAG_DEMO_DATE));
			assertEquals(sprints.get(i).getDemoPlace(), response.getJSONObject(i).getString(SprintUtil.TAG_DEMO_PLACE));
			assertEquals(sprints.get(i).getDailyInfo(), response.getJSONObject(i).getString(SprintUtil.TAG_DAILY_MEETING));
		}
	}
	
	@Test
	public void testGetSprintWithStories() throws Exception {
		// get sprint
		SprintObject sprint = SprintObject.get(mCS.getSprintsId().get(0));
		
		// Assemble URL
		String URL = String.format(API_URL, mProjectName, sprint.getId() + "/all", mUsername, mPassword);

		// Send Http Request
		HttpGet httpGet = new HttpGet(URL);
		String result = EntityUtils.toString(mHttpClient.execute(httpGet).getEntity(), StandardCharsets.UTF_8);
		JSONObject response = new JSONObject(result);
		

		// assert
		assertEquals(sprint.getId(), response.getLong(SprintUtil.TAG_ID));
		assertEquals(sprint.getInterval(), response.getInt(SprintUtil.TAG_INTERVAL));
		assertEquals(sprint.getMembersAmount(), response.getInt(SprintUtil.TAG_MEMBERS));
		assertEquals(sprint.getHoursCanCommit(), response.getInt(SprintUtil.TAG_HOURS_CAN_COMMIT));
		assertEquals(sprint.getFocusFactor(), response.getInt(SprintUtil.TAG_FOCUS_FACTOR));
		assertEquals(sprint.getSprintGoal(), response.getString(SprintUtil.TAG_SPRINT_GOAL));
		assertEquals(sprint.getStartDateString(), response.getString(SprintUtil.TAG_START_DATE));
		assertEquals(sprint.getDemoDateString(), response.getString(SprintUtil.TAG_DEMO_DATE));
		assertEquals(sprint.getDemoPlace(), response.getString(SprintUtil.TAG_DEMO_PLACE));
		assertEquals(sprint.getDailyInfo(), response.getString(SprintUtil.TAG_DAILY_MEETING));
		
		JSONArray storiesJSONArray = response.getJSONArray(SprintUtil.TAG_STORIES);

		for (int i = 0; i < storiesJSONArray.length(); i++) {
			assertEquals(sprint.getStories().get(i).getId(), storiesJSONArray.getJSONObject(i).getLong(StoryEnum.ID));
			assertEquals(sprint.getStories().get(i).getName(), storiesJSONArray.getJSONObject(i).getString(StoryEnum.NAME));
			assertEquals(sprint.getStories().get(i).getNotes(), storiesJSONArray.getJSONObject(i).getString(StoryEnum.NOTES));
			assertEquals(sprint.getStories().get(i).getHowToDemo(), storiesJSONArray.getJSONObject(i).getString(StoryEnum.HOW_TO_DEMO));
			assertEquals(sprint.getStories().get(i).getImportance(), storiesJSONArray.getJSONObject(i).getInt(StoryEnum.IMPORTANCE));
			assertEquals(sprint.getStories().get(i).getValue(), storiesJSONArray.getJSONObject(i).getInt(StoryEnum.VALUE));
			assertEquals(sprint.getStories().get(i).getEstimate(), storiesJSONArray.getJSONObject(i).getInt(StoryEnum.ESTIMATE));
			assertEquals(sprint.getStories().get(i).getStatus(), storiesJSONArray.getJSONObject(i).getInt(StoryEnum.STATUS));
			assertEquals(sprint.getStories().get(i).getSprintId(), storiesJSONArray.getJSONObject(i).getLong(StoryEnum.SPRINT_ID));
		}
	}
}
