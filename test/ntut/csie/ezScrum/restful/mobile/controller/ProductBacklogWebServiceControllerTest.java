package ntut.csie.ezScrum.restful.mobile.controller;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import ntut.csie.ezScrum.dao.StoryDAO;
import ntut.csie.ezScrum.dao.TagDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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

public class ProductBacklogWebServiceControllerTest {
	private static String SERVER_URL = "http://127.0.0.1:8080/ezScrum/web-service";
	private static String API_URL = "http://127.0.0.1:8080/ezScrum/web-service/%s/product-backlog/%s?username=%s&password=%s";
	private static HttpServer mServer;
	private HttpClient mHttpClient;
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

	@Before
	public void setUp() throws Exception {
		// start server
		mServer = HttpServerFactory.create(SERVER_URL);
		mServer.start();
		
		mHttpClient = HttpClientBuilder.create().build();

		// change to test mode
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// initial SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// create a new project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();
		
		mCPB = new CreateProductBacklog(mStoryCount, mEstimate, mCP, "EST");
		mCPB.exe();

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
		mCPB = null;
		mProject = null;
		mConfig = null;
	}

	@Test
	public void testGetProductBacklogList() throws Exception {
		String URL = String.format(API_URL, mProjectName, "storylist", mUsername, mPassword);
		HttpGet httpGet = new HttpGet(URL);
		HttpResponse httpResponse = mHttpClient.execute(httpGet);
		String response = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
		
		ArrayList<StoryObject> stories = mCPB.getStories();
		
		JSONObject storiesJson = new JSONObject(response);
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
	public void testGetStory() throws Exception {
		StoryObject story = mCPB.getStories().get(0);
		
		String URL = String.format(API_URL, mProjectName, "storylist/" + story.getId(), mUsername, mPassword);
		HttpGet httpGet = new HttpGet(URL);
		HttpResponse httpResponse = mHttpClient.execute(httpGet);
		String response = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
		
		JSONObject storyJson = new JSONObject(response);
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
	public void testCreateStory() throws Exception {
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
		
		// send request to create a story
		String URL = String.format(API_URL, mProjectName, "create", mUsername, mPassword);
		HttpPost httpPost = new HttpPost(URL);
		BasicHttpEntity entity = new BasicHttpEntity();
		entity.setContent(new ByteArrayInputStream(storyJson.toString().getBytes(StandardCharsets.UTF_8)));
		entity.setContentEncoding("utf-8");
		httpPost.setEntity(entity);
		HttpResponse httpResponse = mHttpClient.execute(httpPost);
		String response = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
		
		// assert result
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(mProject);
		ArrayList<StoryObject> stories = productBacklogHelper.getStories();
		assertEquals(4, stories.size());
		
		JSONObject responseJson = new JSONObject(response);
		assertEquals("SUCCESS", responseJson.getString("status"));
		assertEquals(stories.get(stories.size() - 1).getId(), responseJson.getLong("storyId"));
	}
	
	@Test
	public void testUpdateStory() throws Exception {
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
		
		String URL = String.format(API_URL, mProjectName, "update", mUsername, mPassword);
		HttpPost httpPost = new HttpPost(URL);
		BasicHttpEntity entity = new BasicHttpEntity();
		entity.setContent(new ByteArrayInputStream(storyJson.toString().getBytes(StandardCharsets.UTF_8)));
		entity.setContentEncoding("utf-8");
		httpPost.setEntity(entity);
		HttpResponse httpResponse = mHttpClient.execute(httpPost);
		String response = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
		
		// assert first time
		JSONObject responseJson = new JSONObject(response);
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
		
		entity = new BasicHttpEntity();
		entity.setContent(new ByteArrayInputStream(storyJson.toString().getBytes(StandardCharsets.UTF_8)));
		entity.setContentEncoding("utf-8");
		httpPost.setEntity(entity);
		httpResponse = mHttpClient.execute(httpPost);
		response = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
		
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
	public void testDeleteStory() throws Exception {
		StoryObject story = mCPB.getStories().get(0);
		String URL = String.format(API_URL, mProjectName, "storylist/" + story.getId(), mUsername, mPassword);
		HttpDelete httpDelete = new HttpDelete(URL);
		HttpResponse httpResponse = mHttpClient.execute(httpDelete);
		String response = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
		
		JSONObject responseJson = new JSONObject(response);
		assertEquals("SUCCESS", responseJson.getString("status"));
		
		story = StoryDAO.getInstance().get(story.getId());
		assertEquals(null, story);
	}
	
	@Test
	public void testGetTagList() throws Exception {
		// create tags
		TagObject tag1 = new TagObject("QOQ", mProject.getId());
		tag1.save();
		TagObject tag2 = new TagObject("QAQ", mProject.getId());
		tag2.save();
		TagObject tag3 = new TagObject("QWQ", mProject.getId());
		tag3.save();
		
		String URL = String.format(API_URL, mProjectName, "taglist", mUsername, mPassword);
		HttpGet httpGet = new HttpGet(URL);
		HttpResponse httpResponse = mHttpClient.execute(httpGet);
		String response = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
		
		ArrayList<TagObject> tags = TagDAO.getInstance().getTagsByProjectId(mProject.getId());
		
		JSONObject responseJson = new JSONObject(response);
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
	public void testGetStoryHistory() throws Exception {
		StoryObject story = mCPB.getStories().get(0);
		
		String URL = String.format(API_URL, mProjectName, story.getId() + "/history", mUsername, mPassword);
		HttpGet httpGet = new HttpGet(URL);
		HttpResponse httpResponse = mHttpClient.execute(httpGet);
		String response = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
		
		JSONObject responseJson = new JSONObject(response);
		JSONArray historiesJson = responseJson.getJSONArray("histories");
		assertEquals(1, historiesJson.length());
		
		JSONObject historyJson = historiesJson.getJSONObject(0);
		assertEquals("Create Story #1", historyJson.getString("description"));
		assertEquals(story.getId(), historyJson.getLong("issue_id"));
		assertEquals(IssueTypeEnum.TYPE_STORY, historyJson.getInt("issue_type"));
		assertEquals(HistoryObject.TYPE_CREATE, historyJson.getInt("type"));
	}
}
