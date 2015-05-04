package ntut.csie.ezScrum.restful.mobile.controller.v2;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import ntut.csie.ezScrum.dao.StoryDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.repackaged.org.apache.http.protocol.HTTP;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

public class StoryApiTest extends ApiTest {
	private static String SERVER_URL = "http://127.0.0.1:8080/ezScrum/api";
	private static String API_URL = "http://127.0.0.1:8080/ezScrum/api/stories";
	private static HttpServer mServer;
	private HttpClient mClient;
	private long mAccountId;

	private int mProjectCount = 1;
	private int mSprintCount = 1;
	private int mStoryCount = 5;
	private CreateProject mCP;
	private CreateProductBacklog mCPB;
	private CreateSprint mCS;
	private CreateAccount mCA;
	private AddStoryToSprint mASTS;
	private Configuration mConfig;
	private ProjectObject mProject;

	@Before
	public void setUp() throws Exception {
		// start server
		mServer = HttpServerFactory.create(SERVER_URL);
		mServer.start();

		mClient = HttpClientBuilder.create().build();
		;

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
		mAccountId = mCA.getAccountList().get(0).getId();

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
	 public void testPost() throws Exception {
	 // 預設已經新增五個 stories
	 ArrayList<StoryObject> stories = mProject.getStories();
	 assertEquals(10, stories.size());
	
	 // initial request data
	 JSONObject storyJson = new JSONObject();
	 storyJson.put("name", "TEST_NAME").put("notes", "TEST_NOTES")
	 .put("how_to_demo", "TEST_HOW_TO_DEMO").put("importance", 99)
	 .put("value", 15).put("estimate", 21).put("status", 0)
	 .put("sprint_id", -1).put("tags", "")
	 .put("project_name", mProject.getName());
	
	 BasicHttpEntity entity = new BasicHttpEntity();
	 entity.setContent(new ByteArrayInputStream(storyJson.toString()
	 .getBytes()));
	 entity.setContentEncoding("utf-8");
	 HttpPost httpPost = new HttpPost(API_URL);
	 httpPost.setEntity(entity);
	 setHeaders(httpPost, mAccountId);
	 String result = EntityUtils.toString(mClient.execute(httpPost)
	 .getEntity());
	 System.out.println(result);
	 JSONObject response = new JSONObject(result);
	
	 // 新增一個 story，project 內的 story 要有六個
	 stories = mProject.getStories();
	 assertEquals(11, stories.size());
	 // 對回傳的 JSON 做 assert
	 assertEquals("SUCCESS", response.getString("status"));
	 assertEquals(stories.get(stories.size() - 1).getId(),
	 response.getLong("storyId"));
	 }
	
	 @Test
	 public void testPut() throws JSONException, ParseException,
	 ClientProtocolException, IOException {
	 StoryObject story = mASTS.getStories().get(0);
	 // initial request data
	 JSONObject storyJson = new JSONObject();
	 storyJson.put("id", story.getId()).put("name", "顆顆").put("notes", "崩潰")
	 .put("how_to_demo", "做不完").put("importance", 99)
	 .put("value", 15).put("estimate", 21).put("status", 0)
	 .put("sprint_id", -1).put("tags", "")
	 .put("project_name", mProject.getName());
	
	 BasicHttpEntity entity = new BasicHttpEntity();
	 entity.setContent(new ByteArrayInputStream(storyJson.toString()
	 .getBytes()));
	 entity.setContentEncoding("utf-8");
	 HttpPut httpPut = new HttpPut(API_URL + "/" + story.getId());
	 httpPut.setEntity(entity);
	 setHeaders(httpPut, mAccountId);
	 String result = EntityUtils.toString(mClient.execute(httpPut)
	 .getEntity(), HTTP.UTF_8);
	 JSONObject response = new JSONObject(result);
	
	 assertEquals(storyJson.getLong("id"), response.getLong("id"));
	 assertEquals(storyJson.getString("name"), response.getString("name"));
	 assertEquals(storyJson.getString("notes"), response.getString("notes"));
	 assertEquals(storyJson.getString("how_to_demo"),
	 response.getString("how_to_demo"));
	 assertEquals(storyJson.getInt("importance"),
	 response.getInt("importance"));
	 assertEquals(storyJson.getInt("value"), response.getInt("value"));
	 assertEquals(storyJson.getInt("estimate"), response.getInt("estimate"));
	 assertEquals(storyJson.getInt("status"), response.getInt("status"));
	 assertEquals(storyJson.getLong("sprint_id"),
	 response.getLong("sprint_id"));
	 assertEquals(9, response.getJSONArray("histories").length());
	 assertEquals(0, response.getJSONArray("tags").length());
	 }

	@Test
	public void testGet() throws ParseException, ClientProtocolException,
			IOException, JSONException {
		StoryObject story = mASTS.getStories().get(0);

		HttpGet httpGet = new HttpGet(API_URL + "/" + story.getId()
				+ "?project_name=abcd");
		setHeaders(httpGet, mAccountId);
		HttpResponse httpResponse = mClient.execute(httpGet);
		String response = EntityUtils.toString(httpResponse.getEntity(),
				"utf-8");

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
	public void testGetList() throws ParseException, ClientProtocolException,
			IOException, JSONException {
		HttpGet httpGet = new HttpGet(API_URL + "?project_name="
				+ mProject.getName());
		setHeaders(httpGet, mAccountId);
		HttpResponse httpResponse = mClient.execute(httpGet);
		String response = EntityUtils.toString(httpResponse.getEntity(),
				HTTP.UTF_8);

		ArrayList<StoryObject> stories = mCPB.getStories();

		JSONObject storiesJson = new JSONObject(response);
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
	public void testDelete() throws ParseException, ClientProtocolException,
			IOException, JSONException {
		StoryObject story = mCPB.getStories().get(0);
		HttpDelete httpDelete = new HttpDelete(API_URL + "/" + story.getId()
				+ "?project_name=" + mProject.getName());
		setHeaders(httpDelete, mAccountId);
		HttpResponse httpResponse = mClient.execute(httpDelete);
		String response = EntityUtils.toString(httpResponse.getEntity(),
				"utf-8");

		System.out.println(response);
		JSONObject responseJson = new JSONObject(response);
		assertEquals("SUCCESS", responseJson.getString("status"));

		story = StoryDAO.getInstance().get(story.getId());
		assertEquals(null, story);
	}
}
