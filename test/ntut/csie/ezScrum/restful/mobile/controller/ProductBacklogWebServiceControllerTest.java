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

import ntut.csie.ezScrum.dao.StoryDAO;
import ntut.csie.ezScrum.dao.TagDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;

public class ProductBacklogWebServiceControllerTest extends JerseyTest {
	private ResourceConfig mResourceConfig;
	private static String BASE_URL = "http://localhost:9527/ezScrum/web-service";
	private URI mBaseUri = URI.create(BASE_URL);
	private Client mClient;
	private HttpServer mHttpServer;
	private String mUsername = "admin";
	private String mPassword = "admin";
	private int mProjectCount = 1;
	private int mStoryCount = 3;
	private int mEstimate = 90;
	private CreateProject mCP;
	private CreateProductBacklog mCPB;
	private ProjectObject mProject;
	private Configuration mConfig;
	private String mProjectName;

	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(ProductBacklogWebServiceController.class);
		return mResourceConfig;
	}
	
	@Before
	public void setUp() {
		// change to test mode
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// initial SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// create a new project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreateForDb();
		
		mCPB = new CreateProductBacklog(mStoryCount, mEstimate, mCP, "EST");
		mCPB.exe();

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
		mCPB = null;
		mProject = null;
		mConfig = null;
	}

	@Test
	public void testGetProductBacklogList() throws JSONException {
		Response response = mClient.target(BASE_URL)
                .path("/" + mProjectName + "/product-backlog/storylist")
                .queryParam("username", mUsername)
                .queryParam("password", mPassword)
                .request()
                .get();
		
		ArrayList<StoryObject> stories = mCPB.getStories();
		
		JSONObject storiesJson = new JSONObject(response.readEntity(String.class));
		for (int i = 0; i < stories.size(); i++) {
			StoryObject story = stories.get(i);
			JSONObject storyJson = storiesJson.getJSONArray("stories").getJSONObject(i);
			assertEquals(story.getId(), storyJson.getLong("id"));
			assertEquals(story.getName(), storyJson.getString("name"));
			assertEquals(story.getNotes(), storyJson.getString("notes"));
			assertEquals(story.getHowToDemo(), storyJson.getString("how_to_demo"));
			assertEquals(story.getImportance(), storyJson.getInt("importance"));
			assertEquals(story.getValue(), storyJson.getInt("value"));
			assertEquals(story.getEstimate(), storyJson.getInt("estimate"));
			assertEquals(story.getStatus(), storyJson.getInt("status"));
			assertEquals(story.getSprintId(), storyJson.getLong("sprint_id"));
		}
	}
	
	@Test
	public void testGetStory() throws JSONException {
		StoryObject story = mCPB.getStories().get(0);
		
		Response response = mClient.target(BASE_URL)
                .path("/" + mProjectName + "/product-backlog/storylist/" + story.getId())
                .queryParam("username", mUsername)
                .queryParam("password", mPassword)
                .request()
                .get();
		
		JSONObject storyJson = new JSONObject(response.readEntity(String.class));
		assertEquals(story.getId(), storyJson.getLong("id"));
		assertEquals(story.getName(), storyJson.getString("name"));
		assertEquals(story.getNotes(), storyJson.getString("notes"));
		assertEquals(story.getHowToDemo(), storyJson.getString("how_to_demo"));
		assertEquals(story.getImportance(), storyJson.getInt("importance"));
		assertEquals(story.getValue(), storyJson.getInt("value"));
		assertEquals(story.getEstimate(), storyJson.getInt("estimate"));
		assertEquals(story.getStatus(), storyJson.getInt("status"));
		assertEquals(story.getSprintId(), storyJson.getLong("sprint_id"));
	}
	
	@Test
	public void testCreateStory() throws JSONException {
		JSONObject storyJson = new JSONObject();
		storyJson
			.put("name", "TEST_STORY")
			.put("importance", 100)
			.put("estimate", 2)
			.put("value", 50)
			.put("how_to_demo", "TEST_STORY_DEMO")
			.put("notes", "TEST_STORY_NOTE")
			.put("status", 1)
			.put("sprint_id", -1)
			.put("tags", "");
		
		Response response = mClient.target(BASE_URL)
                .path("/" + mProjectName + "/product-backlog/create")
                .queryParam("username", mUsername)
                .queryParam("password", mPassword)
                .request()
                .post(Entity.text(storyJson.toString()));
		
		// assert result
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(mProject);
		ArrayList<StoryObject> stories = productBacklogHelper.getStories();
		assertEquals(4, stories.size());
		
		JSONObject responseJson = new JSONObject(response.readEntity(String.class));
		assertEquals("SUCCESS", responseJson.getString("status"));
		assertEquals(stories.get(stories.size() - 1).getId(), responseJson.getLong("storyId"));
	}
	
	@Test
	public void testUpdateStory() throws JSONException {
		JSONObject storyJson = new JSONObject();
		storyJson
			.put("id", 1)
			.put("name", "思都瑞名字")
			.put("importance", 100)
			.put("estimate", 2)
			.put("value", 50)
			.put("how_to_demo", "思都瑞怎麼爹麼")
			.put("notes", "思都瑞備註")
			.put("status", 0)
			.put("sprint_id", -1)
			.put("tags", "QOQ,QAQ");
		
		// create tags
		TagObject tag1 = new TagObject("QOQ", mProject.getId());
		tag1.save();
		TagObject tag2 = new TagObject("QAQ", mProject.getId());
		tag2.save();
		TagObject tag3 = new TagObject("QWQ", mProject.getId());
		tag3.save();
		
		Response response = mClient.target(BASE_URL)
                .path("/" + mProjectName + "/product-backlog/update")
                .queryParam("username", mUsername)
                .queryParam("password", mPassword)
                .request()
                .post(Entity.text(storyJson.toString()));
		
		// assert first time
		JSONObject responseJson = new JSONObject(response.readEntity(String.class));
		assertEquals("SUCCESS", responseJson.getString("status"));
		
		StoryObject story = StoryDAO.getInstance().get(1);
		assertEquals(story.getId(), storyJson.getLong("id"));
		assertEquals(story.getName(), storyJson.getString("name"));
		assertEquals(story.getNotes(), storyJson.getString("notes"));
		assertEquals(story.getHowToDemo(), storyJson.getString("how_to_demo"));
		assertEquals(story.getImportance(), storyJson.getInt("importance"));
		assertEquals(story.getValue(), storyJson.getInt("value"));
		assertEquals(story.getEstimate(), storyJson.getInt("estimate"));
		assertEquals(story.getStatus(), storyJson.getInt("status"));
		assertEquals(story.getSprintId(), storyJson.getLong("sprint_id"));
		
		// reset story again, and then update it again
		storyJson
			.put("id", 1)
			.put("name", "QQ")
			.put("importance", 90)
			.put("estimate", 8)
			.put("value", 100)
			.put("how_to_demo", "要死啦")
			.put("notes", "改不完拉")
			.put("status", 1)
			.put("sprint_id", 1)
			.put("tags", "QAQ,QWQ");
		
		mClient.target(BASE_URL)
        	   .path("/" + mProjectName + "/product-backlog/update")
        	   .queryParam("username", mUsername)
        	   .queryParam("password", mPassword)
               .request()
               .post(Entity.text(storyJson.toString()));
		
		// assert second time, make sure update successfully
		story = StoryDAO.getInstance().get(1);
		assertEquals(story.getId(), storyJson.getLong("id"));
		assertEquals(story.getName(), storyJson.getString("name"));
		assertEquals(story.getNotes(), storyJson.getString("notes"));
		assertEquals(story.getHowToDemo(), storyJson.getString("how_to_demo"));
		assertEquals(story.getImportance(), storyJson.getInt("importance"));
		assertEquals(story.getValue(), storyJson.getInt("value"));
		assertEquals(story.getEstimate(), storyJson.getInt("estimate"));
		assertEquals(story.getStatus(), storyJson.getInt("status"));
		assertEquals(story.getSprintId(), storyJson.getLong("sprint_id"));
	}
	
	@Test
	public void testDeleteStory() throws JSONException {
		StoryObject story = mCPB.getStories().get(0);
		
		Response response = mClient.target(BASE_URL)
                .path("/" + mProjectName + "/product-backlog/storylist/" + story.getId())
                .queryParam("username", mUsername)
                .queryParam("password", mPassword)
                .request()
                .delete();
		
		JSONObject responseJson = new JSONObject(response.readEntity(String.class));
		assertEquals("SUCCESS", responseJson.getString("status"));
		
		story = StoryDAO.getInstance().get(story.getId());
		assertEquals(null, story);
	}
	
	@Test
	public void testGetTagList() throws JSONException {
		// create tags
		TagObject tag1 = new TagObject("QOQ", mProject.getId());
		tag1.save();
		TagObject tag2 = new TagObject("QAQ", mProject.getId());
		tag2.save();
		TagObject tag3 = new TagObject("QWQ", mProject.getId());
		tag3.save();
		
		Response response = mClient.target(BASE_URL)
                .path("/" + mProjectName + "/product-backlog/taglist")
                .queryParam("username", mUsername)
                .queryParam("password", mPassword)
                .request()
                .get();
		
		ArrayList<TagObject> tags = TagDAO.getInstance().getTagsByProjectId(mProject.getId());
		
		JSONObject responseJson = new JSONObject(response.readEntity(String.class));
		JSONArray tagsJson = responseJson.getJSONArray("tags");
		for (int i = 0; i < tagsJson.length(); i++) {
			TagObject tag = tags.get(i);
			JSONObject tagJson = tagsJson.getJSONObject(i);
			assertEquals(tag.getId(), tagJson.getLong("id"));
			assertEquals(tag.getName(), tagJson.getString("name"));
			assertEquals(mProject.getId(), tagJson.getLong("project_id"));
		}
	}
	
	@Test
	public void testGetStoryHistory() throws JSONException {
		StoryObject story = mCPB.getStories().get(0);
		
		Response response = mClient.target(BASE_URL)
                .path("/" + mProjectName + "/product-backlog/" + story.getId() + "/history")
                .queryParam("username", mUsername)
                .queryParam("password", mPassword)
                .request()
                .get();
		
		JSONObject responseJson = new JSONObject(response.readEntity(String.class));
		JSONArray historiesJson = responseJson.getJSONArray("histories");
		assertEquals(1, historiesJson.length());
		
		JSONObject historyJson = historiesJson.getJSONObject(0);
		assertEquals("Create Story #1", historyJson.getString("description"));
		assertEquals(story.getId(), historyJson.getLong("issue_id"));
		assertEquals(IssueTypeEnum.TYPE_STORY, historyJson.getInt("issue_type"));
		assertEquals(HistoryObject.TYPE_CREATE, historyJson.getInt("type"));
	}
}
