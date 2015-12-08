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

import ntut.csie.ezScrum.dao.StoryDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TokenObject;

public class StoryApiTest extends JerseyTest {
	private Configuration mConfig;
	private CreateProject mCP;
	private ResourceConfig mResourceConfig;
	private Client mClient;
	private HttpServer mHttpServer;
	private static String BASE_URL = "http://127.0.0.1:8080/ezScrum/api";
	private URI mBaseUri = URI.create(BASE_URL);
	private long mAccountId;
	private String mPlatformType;

	private int mProjectCount = 1;
	private int mSprintCount = 1;
	private int mStoryCount = 5;
	private CreateProductBacklog mCPB;
	private CreateSprint mCS;
	private CreateAccount mCA;
	private ProjectObject mProject;
	
	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(StoryApi.class);
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
		mCP.exeCreate();

		// create sprint
		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();

		mCPB = new CreateProductBacklog(mStoryCount, 5, mCP, "EST");
		mCPB.exe();

		mCA = new CreateAccount(1);
		mCA.exe();
		
		AddUserToRole AUTR = new AddUserToRole(mCP, mCA);
		AUTR.exe_PO();
		
		mAccountId = mCA.getAccountList().get(0).getId();
		mPlatformType = "windows";
		
		TokenObject token = new TokenObject(mAccountId, mPlatformType);
		token.save();

		mProject = mCP.getAllProjects().get(0);
		
		// start server
		mHttpServer = JdkHttpServerFactory.createHttpServer(mBaseUri, mResourceConfig, true);

		// Create Client
		mClient = ClientBuilder.newClient();
	}

	@After
	public void tearDown() {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		mConfig.setTestMode(false);
		mConfig.save();
		
		// stop server
		mHttpServer.stop(0);

		// release
		mCP = null;
		mCS = null;
		mProject = null;
		mConfig = null;
	}

	@Test
	public void testPost() throws JSONException {
		// 預設已經新增五個 stories
		ArrayList<StoryObject> stories = mProject.getStories();
		assertEquals(5, stories.size());
		// initial request data
		JSONObject storyJson = new JSONObject();
		storyJson.put("name", "TEST_NAME").put("notes", "TEST_NOTES")
				.put("how_to_demo", "TEST_HOW_TO_DEMO").put("importance", 99)
				.put("value", 15).put("estimate", 21).put("status", 0)
				.put("sprint_id", -1).put("tags", "")
				.put("project_name", mProject.getName());
		MultivaluedMap<String, Object> headersMap = TestableApi.getHeaders(mAccountId, mPlatformType);
		Response response = mClient.target(BASE_URL)
                .path("stories")
                .request()
                .headers(headersMap)
                .post(Entity.text(storyJson.toString()));
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		// 新增一個 story，project 內的 story 要有六個
		stories = mProject.getStories();
		assertEquals(6, stories.size());
		// 對回傳的 JSON 做 assert
		assertEquals("SUCCESS", responseJSON.getString("status"));
		assertEquals(stories.get(stories.size() - 1).getId(),
				responseJSON.getLong("storyId"));
	}

	@Test
	public void testPut() throws JSONException {
		StoryObject story = mCPB.getStories().get(0);
		// initial request data
		JSONObject storyJson = new JSONObject();
		storyJson.put("id", story.getId()).put("name", "顆顆").put("notes", "崩潰")
				.put("how_to_demo", "做不完").put("importance", 99)
				.put("value", 15).put("estimate", 21).put("status", 0)
				.put("sprint_id", -1).put("tags", "")
				.put("project_name", mProject.getName());
		MultivaluedMap<String, Object> headersMap = TestableApi.getHeaders(mAccountId, mPlatformType);
		Response response = mClient.target(BASE_URL)
                .path("stories/" + story.getId())
                .request()
                .headers(headersMap)
                .put(Entity.text(storyJson.toString()));
		// assert response msg
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		assertEquals(storyJson.getLong("id"), responseJSON.getLong("id"));
		assertEquals(storyJson.getString("name"), responseJSON.getString("name"));
		assertEquals(storyJson.getString("notes"), responseJSON.getString("notes"));
		assertEquals(storyJson.getString("how_to_demo"),
				responseJSON.getString("how_to_demo"));
		assertEquals(storyJson.getInt("importance"),
				responseJSON.getInt("importance"));
		assertEquals(storyJson.getInt("value"), responseJSON.getInt("value"));
		assertEquals(storyJson.getInt("estimate"), responseJSON.getInt("estimate"));
		assertEquals(storyJson.getInt("status"), responseJSON.getInt("status"));
		assertEquals(storyJson.getLong("sprint_id"),
				responseJSON.getLong("sprint_id"));
		assertEquals(8, responseJSON.getJSONArray("histories").length());
		assertEquals(0, responseJSON.getJSONArray("tags").length());
	}

	@Test
	public void testGet() throws JSONException {
		StoryObject story = mCPB.getStories().get(0);
		MultivaluedMap<String, Object> headersMap = TestableApi.getHeaders(mAccountId, mPlatformType);
		Response response = mClient.target(BASE_URL)
                .path("stories/" + story.getId())
                .queryParam("project_name", mProject.getName())
                .request()
                .headers(headersMap)
                .get();
		
		// assert data
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		assertEquals(story.getId(), responseJSON.getLong("id"));
		assertEquals(story.getName(), responseJSON.getString("name"));
		assertEquals(story.getNotes(), responseJSON.getString("notes"));
		assertEquals(story.getHowToDemo(), responseJSON.getString("how_to_demo"));
		assertEquals(story.getImportance(), responseJSON.getInt("importance"));
		assertEquals(story.getValue(), responseJSON.getInt("value"));
		assertEquals(story.getEstimate(), responseJSON.getInt("estimate"));
		assertEquals(story.getStatus(), responseJSON.getInt("status"));
		assertEquals(story.getSprintId(), responseJSON.getLong("sprint_id"));
	}

	@Test
	public void testGetList() throws JSONException {
		MultivaluedMap<String, Object> headersMap = TestableApi.getHeaders(mAccountId, mPlatformType);
		Response response = mClient.target(BASE_URL)
                .path("stories")
                .queryParam("project_name", mProject.getName())
                .request()
                .headers(headersMap)
                .get();
		ArrayList<StoryObject> stories = mCPB.getStories();
		JSONObject storiesJson = new JSONObject(response.readEntity(String.class));
		for (int i = 0; i < stories.size(); i++) {
			StoryObject story = stories.get(i);
			JSONObject storyJson = storiesJson.getJSONArray("stories")
					.getJSONObject(i);
			assertEquals(story.getId(), storyJson.getLong("id"));
			assertEquals(story.getName(), storyJson.getString("name"));
			assertEquals(story.getNotes(), storyJson.getString("notes"));
			assertEquals(story.getHowToDemo(),
					storyJson.getString("how_to_demo"));
			assertEquals(story.getImportance(), storyJson.getInt("importance"));
			assertEquals(story.getValue(), storyJson.getInt("value"));
			assertEquals(story.getEstimate(), storyJson.getInt("estimate"));
			assertEquals(story.getStatus(), storyJson.getInt("status"));
			assertEquals(story.getSprintId(), storyJson.getLong("sprint_id"));
		}
	}

	@Test
	public void testDelete() throws JSONException {
		StoryObject story = mCPB.getStories().get(0);
		MultivaluedMap<String, Object> headersMap = TestableApi.getHeaders(mAccountId, mPlatformType);
		Response response = mClient.target(BASE_URL)
		        .path("stories/" + story.getId())
		        .queryParam("project_name", mProject.getName())
		        .request()
		        .headers(headersMap)
		        .delete();
		assertEquals(200, response.getStatus());

		story = StoryDAO.getInstance().get(story.getId());
		assertEquals(null, story);
	}
}
