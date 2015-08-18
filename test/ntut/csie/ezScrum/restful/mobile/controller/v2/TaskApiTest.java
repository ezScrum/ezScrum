package ntut.csie.ezScrum.restful.mobile.controller.v2;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import ntut.csie.ezScrum.dao.TaskDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.restful.mobile.util.SprintBacklogUtil;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.dataObject.TokenObject;
import ntut.csie.ezScrum.web.databasEnum.TaskEnum;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;

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

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

public class TaskApiTest extends TestableApi {
	private static String SERVER_URL = "http://127.0.0.1:8080/ezScrum/api";
	private static String API_URL = "http://127.0.0.1:8080/ezScrum/api/tasks";
	private static HttpServer mServer;
	private HttpClient mClient;
	private long mAccountId;
	private String mPlatformType;

	private int mProjectCount = 1;
	private int mSprintCount = 1;
	private int mStoryCount = 3;
	private int mTaskCount = 3;
	private CreateProject mCP;
	private CreateProductBacklog mCPB;
	private CreateSprint mCS;
	private CreateAccount mCA;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
	private Configuration mConfig;
	private ProjectObject mProject;
	@Before
	public void setUp() throws Exception {
		// start server
		mServer = HttpServerFactory.create(SERVER_URL);
		mServer.start();

		mClient = HttpClientBuilder.create().build();

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
		mPlatformType = "windows";

		// create story
		mASTS = new AddStoryToSprint(mStoryCount, 1, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mASTS.exe();
		
		// create tasks
		mATTS = new AddTaskToStory(mTaskCount, 5, mASTS, mCP);
		mATTS.exe();
		
		TokenObject token = new TokenObject(mAccountId, mPlatformType);
		token.save();

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
	public void testPost() throws JSONException, ParseException, ClientProtocolException, IOException {
		// Assert all tasks size
		ProductBacklogMapper productBacklogMapper = new ProductBacklogMapper(mProject);
		ArrayList<StoryObject> stories = productBacklogMapper.getStories();
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		
		for (StoryObject story : stories) {
			tasks.addAll(story.getTasks());
		}
		
		assertEquals(mStoryCount * mTaskCount, tasks.size());
		
		// test data
		String TEST_TASK_NAME = "TEST_TASK_NAME";
		String TEST_TASK_NOTES = "TEST_TASK_NOTES";
		long TEST_TASK_STORY_ID = 1;
		int TEST_TASK_ESTIMATE = 13;
		int TEST_TASK_REMAIN = 13;

		// initial request data
		JSONObject taskJson = new JSONObject();
		taskJson.put(TaskEnum.NAME, TEST_TASK_NAME)
		        .put(TaskEnum.NOTES, TEST_TASK_NOTES)
		        .put(TaskEnum.ESTIMATE, TEST_TASK_ESTIMATE)
		        .put(TaskEnum.STORY_ID, TEST_TASK_STORY_ID)
		        .put(TaskEnum.REMAIN, TEST_TASK_REMAIN)
		        .put(TaskEnum.HANDLER_ID, mAccountId)
		        .put("project_name", mProject.getName());

		BasicHttpEntity entity = new BasicHttpEntity();
		entity.setContent(new ByteArrayInputStream(taskJson.toString().getBytes()));
		entity.setContentEncoding(StandardCharsets.UTF_8.name());
		HttpPost httpPost = new HttpPost(API_URL);
		httpPost.setEntity(entity);
		setHeaders(httpPost, mAccountId, mPlatformType);
		String result = EntityUtils.toString(mClient.execute(httpPost).getEntity());
		System.out.println(result);
		
		tasks = new ArrayList<TaskObject>();

		for (StoryObject story : stories) {
			tasks.addAll(story.getTasks());
		}
		
		// assert
		assertEquals(mStoryCount * mTaskCount + 1, tasks.size());
	}
	
	@Test
	public void testGet() throws ParseException, IOException {
		TaskObject task = mATTS.getTasks().get(0);

		HttpGet httpGet = new HttpGet(API_URL + "/" + task.getId());
		setHeaders(httpGet, mAccountId, mPlatformType);
		HttpResponse httpResponse = mClient.execute(httpGet);
		String response = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);

		assertEquals(task.toString(), response);
	}

	@Test
	public void testGetList() throws ClientProtocolException, IOException, JSONException {
		HttpGet httpGet = new HttpGet(API_URL + "?project_name=" + mProject.getName());
		setHeaders(httpGet, mAccountId, mPlatformType);
		HttpResponse httpResponse = mClient.execute(httpGet);
		String response = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);

		ArrayList<TaskObject> tasks = mATTS.getTasks();

		JSONObject tasksJson = new JSONObject(response);
		for (int i = 0; i < tasks.size(); i++) {
			TaskObject task = tasks.get(i);
			JSONObject taskJson = tasksJson.getJSONArray(SprintBacklogUtil.TAG_TASKS).getJSONObject(i);
			assertEquals(task.getId(), taskJson.getLong(TaskEnum.ID));
			assertEquals(task.getName(), taskJson.getString(TaskEnum.NAME));
			assertEquals(task.getNotes(), taskJson.getString(TaskEnum.NOTES));
			assertEquals(task.getEstimate(), taskJson.getInt(TaskEnum.ESTIMATE));
			assertEquals(task.getRemains(), taskJson.getInt(TaskEnum.REMAIN));
		}
	}
	
	@Test
	public void testPut() throws JSONException, ParseException, ClientProtocolException, IOException {
		TaskObject task = mATTS.getTasks().get(0);
		
		// test data
		String TEST_TASK_NAME = "TEST_TASK_NAME_NEW";
		String TEST_TASK_NOTES = "TEST_TASK_NOTES_NEW";
		long TEST_TASK_STORY_ID = -1;
		int TEST_TASK_ESTIMATE = 8;
		int TEST_TASK_REMAIN = 8;

		// initial request data
		JSONObject taskJson = new JSONObject();
		taskJson.put(TaskEnum.ID, task.getId())
		        .put(TaskEnum.NAME, TEST_TASK_NAME)
		        .put(TaskEnum.NOTES, TEST_TASK_NOTES)
		        .put(TaskEnum.ESTIMATE, TEST_TASK_ESTIMATE)
		        .put(TaskEnum.STORY_ID, TEST_TASK_STORY_ID)
		        .put(TaskEnum.REMAIN, TEST_TASK_REMAIN)
		        .put(TaskEnum.HANDLER_ID, mAccountId)
		        .put("project_name", mProject.getName());
		
		BasicHttpEntity entity = new BasicHttpEntity();
		entity.setContent(new ByteArrayInputStream(taskJson.toString().getBytes()));
		entity.setContentEncoding(StandardCharsets.UTF_8.name());
		HttpPut httpPut = new HttpPut(API_URL + "/" + task.getId());
		httpPut.setEntity(entity);
		setHeaders(httpPut, mAccountId, mPlatformType);
		String result = EntityUtils.toString(mClient.execute(httpPut).getEntity(), StandardCharsets.UTF_8);
		JSONObject response = new JSONObject(result);
		
		// assert
		assertEquals(TEST_TASK_NAME, response.getString(TaskEnum.NAME));
		assertEquals(TEST_TASK_NOTES, response.getString(TaskEnum.NOTES));
		assertEquals(TEST_TASK_ESTIMATE, response.getInt(TaskEnum.ESTIMATE));
		assertEquals(TEST_TASK_REMAIN, response.getInt(TaskEnum.REMAIN));
	}

	@Test
	public void testDelete() throws JSONException, ParseException, IOException {
		TaskObject task = mATTS.getTasks().get(0);
		HttpDelete httpDelete = new HttpDelete(API_URL + "/" + task.getId() + "?project_name=" + mProject.getName());
		setHeaders(httpDelete, mAccountId, mPlatformType);
		HttpResponse httpResponse = mClient.execute(httpDelete);
		String response = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);

		System.out.println(response);
		
		JSONObject responseJson = new JSONObject(response);
		assertEquals("ok", responseJson.getString("msg"));

		task = TaskDAO.getInstance().get(task.getId());
		assertEquals(null, task);
	}
}
