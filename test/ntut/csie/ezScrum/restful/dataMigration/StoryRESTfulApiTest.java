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
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.HistoryJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.StoryJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TagJSONEnum;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.databaseEnum.HistoryEnum;
import ntut.csie.ezScrum.web.databaseEnum.StoryEnum;
import ntut.csie.ezScrum.web.databaseEnum.TagEnum;

public class StoryRESTfulApiTest extends JerseyTest {
	private Configuration mConfig;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private ResourceConfig mResourceConfig;
	private Client mClient;
	private HttpServer mHttpServer;
	private static String BASE_URL = "http://localhost:8080/ezScrum/resource/";
	private URI mBaseUri = URI.create(BASE_URL);

	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(StoryRESTfulApi.class);
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

		// Create Sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();
		
		// Add Story To Sprint
		mASTS = new AddStoryToSprint(1, 8, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mASTS.exe();

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
		mCS = null;
		mHttpServer = null;
		mClient = null;
	}

	@Test
	public void testCreateStory() throws JSONException {
		// Test Data
		String name = "TEST_STORY_NAME";
		String status = "new";
		int estimate = 3;
		int importance = 98;
		int value = 20;
		String notes = "TEST_STORY_VALUE";
		String howToDemo = "TEST_STORY_HOWTODEMO";
		ProjectObject project = mCP.getAllProjects().get(0);
		SprintObject sprint = mCS.getSprints().get(0);

		JSONObject storyJSON = new JSONObject();
		storyJSON.put(StoryJSONEnum.NAME, name);
		storyJSON.put(StoryJSONEnum.STATUS, status);
		storyJSON.put(StoryJSONEnum.ESTIMATE, estimate);
		storyJSON.put(StoryJSONEnum.IMPORTANCE, importance);
		storyJSON.put(StoryJSONEnum.VALUE, value);
		storyJSON.put(StoryJSONEnum.NOTES, notes);
		storyJSON.put(StoryJSONEnum.HOW_TO_DEMO, howToDemo);

		// Call '/projects/{projectId}/sprints/{sprintId}/stories' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		              "/sprints/" + sprint.getId() +
		              "/stories")
		        .request()
		        .post(Entity.text(storyJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = jsonResponse.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertTrue(contentJSON.getLong(StoryEnum.ID) != -1);
		assertEquals(name, contentJSON.getString(StoryEnum.NAME));
		assertEquals(StoryObject.STATUS_UNCHECK, contentJSON.getInt(StoryEnum.STATUS));
		assertEquals(estimate, contentJSON.getInt(StoryEnum.ESTIMATE));
		assertEquals(importance, contentJSON.getInt(StoryEnum.IMPORTANCE));
		assertEquals(value, contentJSON.getInt(StoryEnum.VALUE));
		assertEquals(notes, contentJSON.getString(StoryEnum.NOTES));
		assertEquals(howToDemo, contentJSON.getString(StoryEnum.HOW_TO_DEMO));
	}
	
	@Test
	public void testCreateTagInStory() throws JSONException {
		// Test Data
		String tagName = "TEST_TAG_NAME";
		ProjectObject project = mCP.getAllProjects().get(0);
		SprintObject sprint = mCS.getSprints().get(0);
		StoryObject story = mASTS.getStories().get(0);
		
		// Create Tag
		TagObject tag = new TagObject(tagName, project.getId());
		tag.save();
		assertTrue(tag.getId() > -1);
		
		JSONObject tagJSON = new JSONObject();
		tagJSON.put(TagJSONEnum.NAME, tagName);

		// Call '/projects/{projectId}/sprints/{sprintId}/stories/{storyId}/tags' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		              "/sprints/" + sprint.getId() +
		              "/stories/" + story.getId() + 
		              "/tags")
		        .request()
		        .post(Entity.text(tagJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = jsonResponse.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
		
		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertTrue(contentJSON.getLong(TagEnum.ID) != -1);
		assertEquals(1, story.getTags().size());
		assertEquals(tagName, story.getTags().get(0).getName());
	}
	
	@Test
	public void testCreateHistoryInStory() throws JSONException {
		// Test Data
		String type = "CREATE";
		String oldValue = "";
		String newValue = "";
		long createTime = System.currentTimeMillis();
		
		ProjectObject project = mCP.getAllProjects().get(0);
		SprintObject sprint = mCS.getSprints().get(0);
		StoryObject story = mASTS.getStories().get(0);
		
		JSONObject historyJSON = new JSONObject();
		historyJSON.put(HistoryJSONEnum.HISTORY_TYPE, type)
		           .put(HistoryJSONEnum.OLD_VALUE, oldValue)
		           .put(HistoryJSONEnum.NEW_VALUE, newValue)
		           .put(HistoryJSONEnum.CREATE_TIME, createTime);

		// Call '/projects/{projectId}/sprints/{sprintId}/stories/{storyId}/histories' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		              "/sprints/" + sprint.getId() +
		              "/stories/" + story.getId() + 
		              "/histories")
		        .request()
		        .post(Entity.text(historyJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = jsonResponse.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
		
		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertTrue(contentJSON.getLong(HistoryEnum.ID) != -1);
		HistoryObject historyInStory = story.getHistories().get(story.getHistories().size() - 1);
		assertEquals(HistoryObject.TYPE_CREATE, historyInStory.getHistoryType());
		assertEquals(oldValue, historyInStory.getOldValue());
		assertEquals(newValue, historyInStory.getNewValue());
		assertEquals(createTime, historyInStory.getCreateTime());
	}
	
	@Test
	public void testDeleteHistoriesInStory() throws JSONException {
		ProjectObject project = mCP.getAllProjects().get(0);
		SprintObject sprint = mCS.getSprints().get(0);
		StoryObject story = mASTS.getStories().get(0);
		
		// Call '/projects/{projectId}/sprints/{sprintId}/stories/{storyId}/histories' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/stories/" + story.getId() + 
		                "/histories")
		        .request()
		        .delete();
		
		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(0, story.getHistories().size());
	}
	
	@Test
	public void testCreateAttachFileInStory() {
		// TODO
		assertTrue("todo", false);
	}
}
