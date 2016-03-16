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
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.restful.mobile.support.ConvertSprintBacklog;
import ntut.csie.ezScrum.restful.mobile.util.SprintUtil;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.databaseEnum.SprintEnum;
import ntut.csie.ezScrum.web.databaseEnum.StoryEnum;

public class SprintPlanWebServiceControllerTest extends JerseyTest {
	private ResourceConfig mResourceConfig;
	private static String BASE_URL = "http://localhost:9527/ezScrum/web-service";
	private URI mBaseUri = URI.create(BASE_URL);
	private String mUsername = "admin";
	private String mPassword = "admin";
	private int mProjectCount = 1;
	private int mSprintCount = 1;
	private int mStoryCount = 3;
	private Client mClient;
	private HttpServer mHttpServer;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private Configuration mConfig;
	private ProjectObject mProject;
	private String mProjectName;
	
	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(SprintPlanWebServiceController.class);
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
		mASTS = new AddStoryToSprint(mStoryCount, 1, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mASTS.exe();
		
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
	public void testCreateSprint() throws JSONException {
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
		sprintJson.put(SprintEnum.SERIAL_ID, 1)
				  .put(SprintEnum.INTERVAL, interval)
		          .put(SprintEnum.TEAM_SIZE, members)
		          .put(SprintEnum.AVAILABLE_HOURS, availableHours)
		          .put(SprintEnum.FOCUS_FACTOR, focusFactor)
		          .put(SprintEnum.GOAL, sprintGoal)
		          .put(SprintEnum.START_DATE, startDate)
		          .put(SprintEnum.DEMO_DATE, demoDate)
		          .put(SprintEnum.DEMO_PLACE, demoPlace)
		          .put(SprintEnum.DAILY_INFO, dailyInfo)
		          .put(SprintEnum.DUE_DATE, dueDate);
		
		Response response = mClient.target(BASE_URL)
                .path("/" + mProjectName + "/sprint/create")
                .queryParam("username", mUsername)
                .queryParam("password", mPassword)
                .request()
                .post(Entity.text(sprintJson.toString()));
		String expectedJsonString = ConvertSprintBacklog.getSprintBacklogJsonString(mProject.getCurrentSprint());
		// assert
		assertEquals(expectedJsonString, response.readEntity(String.class));
	}
	
	@Test
	public void testDeleteSprint() {
		// get existed sprints
		ArrayList<SprintObject> sprints = mProject.getSprints();
		// assert
		assertEquals(mSprintCount, sprints.size());
		
		Response response = mClient.target(BASE_URL)
                .path("/" + mProjectName + "/sprint/delete/" + sprints.get(0).getId())
                .queryParam("username", mUsername)
                .queryParam("password", mPassword)
                .request()
                .delete();
		
		// assert
		assertEquals("", response.readEntity(String.class));
	}
	
	@Test
	public void testUpdateSprint() throws JSONException {
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
		SprintObject temp = SprintObject.get(updateSpint.getId());
		// prepare request data
		JSONObject sprintJson = new JSONObject();
		sprintJson.put(SprintEnum.ID, updateSpint.getId())
				.put(SprintEnum.SERIAL_ID, temp.getSerialId())
		        .put(SprintEnum.INTERVAL, interval)
		        .put(SprintEnum.TEAM_SIZE, members)
		        .put(SprintEnum.AVAILABLE_HOURS, availableHours)
		        .put(SprintEnum.FOCUS_FACTOR, focusFactor)
		        .put(SprintEnum.GOAL, sprintGoal)
		        .put(SprintEnum.START_DATE, startDate)
		        .put(SprintEnum.DEMO_DATE, demoDate)
		        .put(SprintEnum.DEMO_PLACE, demoPlace)
		        .put(SprintEnum.DAILY_INFO, dailyInfo)
		        .put(SprintEnum.DUE_DATE, dueDate);

		mClient.target(BASE_URL)
               .path("/" + mProjectName + "/sprint/update")
               .queryParam("username", mUsername)
               .queryParam("password", mPassword)
               .request()
               .put(Entity.text(sprintJson.toString()));
		
		// get Sprint
		SprintObject sprint = SprintObject.get(updateSpint.getId());
		
		// assert
		assertEquals(interval, sprint.getInterval());
		assertEquals(members, sprint.getTeamSize());
		assertEquals(availableHours, sprint.getAvailableHours());
		assertEquals(focusFactor, sprint.getFocusFactor());
		assertEquals(sprintGoal, sprint.getGoal());
		assertEquals(startDate, sprint.getStartDateString());
		assertEquals(demoDate, sprint.getDemoDateString());
		assertEquals(demoPlace, sprint.getDemoPlace());
		assertEquals(dailyInfo, sprint.getDailyInfo());
	}
	
	@Test
	public void testGetAllSprints() throws JSONException {
		Response response = mClient.target(BASE_URL)
                .path("/" + mProjectName + "/sprint/all")
                .queryParam("username", mUsername)
                .queryParam("password", mPassword)
                .request()
                .get();
		
		JSONArray responseJSONArray = new JSONArray(response.readEntity(String.class));
		// get Sprints
		ArrayList<SprintObject> sprints = mProject.getSprints();

		// assert
		assertEquals(sprints.size(), responseJSONArray.length());
		
		for (int i = 0; i < responseJSONArray.length(); i++) {
			assertEquals(sprints.get(i).getId(), responseJSONArray.getJSONObject(i).getLong(SprintUtil.TAG_ID));
			assertEquals(sprints.get(i).getInterval(), responseJSONArray.getJSONObject(i).getInt(SprintUtil.TAG_INTERVAL));
			assertEquals(sprints.get(i).getTeamSize(), responseJSONArray.getJSONObject(i).getInt(SprintUtil.TAG_TEAM_SIZE));
			assertEquals(sprints.get(i).getAvailableHours(), responseJSONArray.getJSONObject(i).getInt(SprintUtil.TAG_HOURS_CAN_COMMIT));
			assertEquals(sprints.get(i).getFocusFactor(), responseJSONArray.getJSONObject(i).getInt(SprintUtil.TAG_FOCUS_FACTOR));
			assertEquals(sprints.get(i).getGoal(), responseJSONArray.getJSONObject(i).getString(SprintUtil.TAG_SPRINT_GOAL));
			assertEquals(sprints.get(i).getStartDateString(), responseJSONArray.getJSONObject(i).getString(SprintUtil.TAG_START_DATE));
			assertEquals(sprints.get(i).getDemoDateString(), responseJSONArray.getJSONObject(i).getString(SprintUtil.TAG_DEMO_DATE));
			assertEquals(sprints.get(i).getDemoPlace(), responseJSONArray.getJSONObject(i).getString(SprintUtil.TAG_DEMO_PLACE));
			assertEquals(sprints.get(i).getDailyInfo(), responseJSONArray.getJSONObject(i).getString(SprintUtil.TAG_DAILY_MEETING));
		}
	}
	
	@Test
	public void testGetSprintWithStories() throws JSONException {
		// get sprint
		SprintObject sprint = SprintObject.get(mCS.getSprintsId().get(0));
		
		Response response = mClient.target(BASE_URL)
                .path("/" + mProjectName + "/sprint/" + sprint.getId() + "/all")
                .queryParam("username", mUsername)
                .queryParam("password", mPassword)
                .request()
                .get();
		
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		

		// assert
		assertEquals(sprint.getId(), responseJSON.getLong(SprintUtil.TAG_ID));
		assertEquals(sprint.getInterval(), responseJSON.getInt(SprintUtil.TAG_INTERVAL));
		assertEquals(sprint.getTeamSize(), responseJSON.getInt(SprintUtil.TAG_TEAM_SIZE));
		assertEquals(sprint.getAvailableHours(), responseJSON.getInt(SprintUtil.TAG_HOURS_CAN_COMMIT));
		assertEquals(sprint.getFocusFactor(), responseJSON.getInt(SprintUtil.TAG_FOCUS_FACTOR));
		assertEquals(sprint.getGoal(), responseJSON.getString(SprintUtil.TAG_SPRINT_GOAL));
		assertEquals(sprint.getStartDateString(), responseJSON.getString(SprintUtil.TAG_START_DATE));
		assertEquals(sprint.getDemoDateString(), responseJSON.getString(SprintUtil.TAG_DEMO_DATE));
		assertEquals(sprint.getDemoPlace(), responseJSON.getString(SprintUtil.TAG_DEMO_PLACE));
		assertEquals(sprint.getDailyInfo(), responseJSON.getString(SprintUtil.TAG_DAILY_MEETING));
		
		JSONArray storiesJSONArray = responseJSON.getJSONArray(SprintUtil.TAG_STORIES);

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
