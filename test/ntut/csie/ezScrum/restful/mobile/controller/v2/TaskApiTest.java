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

import ntut.csie.ezScrum.dao.TaskDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.restful.mobile.util.SprintBacklogUtil;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.dataObject.TokenObject;
import ntut.csie.ezScrum.web.databaseEnum.AccountEnum;
import ntut.csie.ezScrum.web.databaseEnum.TaskEnum;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;

public class TaskApiTest extends JerseyTest {
	private Configuration mConfig;
	private CreateProject mCP;
	private CreateProductBacklog mCPB;
	private CreateSprint mCS;
	private CreateAccount mCA;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
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
	private int mTaskCount = 3;
	private ProjectObject mProject;
	
	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(TaskApi.class);
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

		mConfig.setTestMode(false);
		mConfig.save();

		// release
		mCP = null;
		mCS = null;
		mASTS = null;
		mProject = null;
		mConfig = null;
		
		// stop server
		mHttpServer.stop(0);
	}
	
	@Test
	public void testPost() throws JSONException {
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
		
		MultivaluedMap<String, Object> headersMap = TestableApi.getHeaders(mAccountId, mPlatformType);
		Response response = mClient.target(BASE_URL)
                .path("tasks")
                .request()
                .headers(headersMap)
                .post(Entity.text(taskJson.toString()));
		
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		assertEquals(taskJson.getString(TaskEnum.NAME), responseJSON.getString(TaskEnum.NAME));
		assertEquals(taskJson.getString(TaskEnum.NOTES), responseJSON.getString(TaskEnum.NOTES));
		assertEquals(taskJson.getInt(TaskEnum.ESTIMATE), responseJSON.getInt(TaskEnum.ESTIMATE));
		assertEquals(taskJson.getLong(TaskEnum.STORY_ID), responseJSON.getLong(TaskEnum.STORY_ID));
		assertEquals(taskJson.getInt(TaskEnum.REMAIN), responseJSON.getInt(TaskEnum.REMAIN));
		assertEquals(taskJson.getLong(TaskEnum.HANDLER_ID), responseJSON.getJSONObject(TaskEnum.HANDLER).getLong(AccountEnum.ID));
		assertEquals(taskJson.getString("project_name"), ProjectObject.get(responseJSON.getInt(TaskEnum.PROJECT_ID)).getName());
	}
	
	@Test
	public void testGet() {
		TaskObject task = mATTS.getTasks().get(0);
		MultivaluedMap<String, Object> headersMap = TestableApi.getHeaders(mAccountId, mPlatformType);
		Response response = mClient.target(BASE_URL)
                .path("tasks/" + task.getId())
                .queryParam("project_name", mProject.getName())
                .request()
                .headers(headersMap)
                .get();
		
		assertEquals(task.toString(), response.readEntity(String.class));
	}

	@Test
	public void testGetList() throws JSONException {
		MultivaluedMap<String, Object> headersMap = TestableApi.getHeaders(mAccountId, mPlatformType);
		Response response = mClient.target(BASE_URL)
                .path("tasks")
                .queryParam("project_name", mProject.getName())
                .request()
                .headers(headersMap)
                .get();

		ArrayList<TaskObject> tasks = mATTS.getTasks();

		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		for (int i = 0; i < tasks.size(); i++) {
			TaskObject task = tasks.get(i);
			JSONObject taskJson = responseJSON.getJSONArray(SprintBacklogUtil.TAG_TASKS).getJSONObject(i);
			assertEquals(task.getId(), taskJson.getLong(TaskEnum.ID));
			assertEquals(task.getName(), taskJson.getString(TaskEnum.NAME));
			assertEquals(task.getNotes(), taskJson.getString(TaskEnum.NOTES));
			assertEquals(task.getEstimate(), taskJson.getInt(TaskEnum.ESTIMATE));
			assertEquals(task.getRemains(), taskJson.getInt(TaskEnum.REMAIN));
		}
	}
	
	@Test
	public void testPut() throws JSONException {
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
		
		MultivaluedMap<String, Object> headersMap = TestableApi.getHeaders(mAccountId, mPlatformType);
		Response response = mClient.target(BASE_URL)
                .path("tasks/" + task.getId())
                .request()
                .headers(headersMap)
                .put(Entity.text(taskJson.toString()));
		
		// assert response msg
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		
		// assert
		assertEquals(TEST_TASK_NAME, responseJSON.getString(TaskEnum.NAME));
		assertEquals(TEST_TASK_NOTES, responseJSON.getString(TaskEnum.NOTES));
		assertEquals(TEST_TASK_ESTIMATE, responseJSON.getInt(TaskEnum.ESTIMATE));
		assertEquals(TEST_TASK_REMAIN, responseJSON.getInt(TaskEnum.REMAIN));
	}

	@Test
	public void testDelete() throws JSONException {
		TaskObject task = mATTS.getTasks().get(0);
		MultivaluedMap<String, Object> headersMap = TestableApi.getHeaders(mAccountId, mPlatformType);
		Response response = mClient.target(BASE_URL)
		        .path("tasks/" + task.getId())
		        .queryParam("project_name", mProject.getName())
		        .request()
		        .headers(headersMap)
		        .delete();
		
		JSONObject responseJson = new JSONObject(response.readEntity(String.class));
		assertEquals("ok", responseJson.getString("msg"));

		task = TaskDAO.getInstance().get(task.getId());
		assertEquals(null, task);
	}
}
