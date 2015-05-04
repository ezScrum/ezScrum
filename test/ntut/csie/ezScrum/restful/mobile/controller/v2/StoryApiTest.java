package ntut.csie.ezScrum.restful.mobile.controller.v2;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

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

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

		mClient = HttpClientBuilder.create().build();;

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
		assertEquals(mStoryCount, stories.size());

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
		assertEquals(mStoryCount + 1, stories.size());
		// 對回傳的 JSON 做 assert
		assertEquals("SUCCESS", response.getString("status"));
		assertEquals(stories.get(stories.size() - 1).getId(),
				response.getLong("storyId"));
	}
}
