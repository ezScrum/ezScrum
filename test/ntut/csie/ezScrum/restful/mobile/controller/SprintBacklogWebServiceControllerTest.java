package ntut.csie.ezScrum.restful.mobile.controller;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.restful.mobile.util.SprintBacklogUtil;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateTask;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

public class SprintBacklogWebServiceControllerTest {
	private static String SERVER_URL = "http://127.0.0.1:8080/ezScrum/web-service";
	private static String API_URL = "http://127.0.0.1:8080/ezScrum/web-service/%s/sprint-backlog/%s/%s?username=%s&password=%s";
	private static HttpServer mServer;
	private HttpClient mHttpClient;
	private String mUsername = "admin";
	private String mPassword = "admin";

	private int mProjectCount = 1;
	private int mStoryCount = 3;
	private int mSprintCount = 2;
	private int mTaskCount = 3;
	private int mEstimate = 90;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private ProjectObject mProject;
	private CreateTask mCT;
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

		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();

		mASTS = new AddStoryToSprint(mStoryCount, mEstimate, mCS, mCP,
				CreateProductBacklog.COLUMN_TYPE_EST);
		mASTS.exe();

		mCT = new CreateTask(mTaskCount, mCP);
		mCT.exe();

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
	public void testGetSprintInfoList() {

	}

	@Test
	public void testGetSprintBacklog() {

	}

	@Test
	public void testGetCurrentSprintBacklog() {

	}

	@Test
	public void testGetStoryIDList() {

	}

	@Test
	public void testGetTaskIDList() {

	}

	@Test
	public void testGetTaskHistory() {

	}

	@Test
	public void testGetTaskInformation() throws Exception {
		SprintObject sprint = new SprintObject(mProject.getId());
		sprint.save();
		StoryObject story = new StoryObject(mProject.getId());
		story.setSprintId(sprint.getId());
		story.save();
		TaskObject task = new TaskObject(mProject.getId());
		task.setStoryId(story.getId())
			.setName("Test_Task_Name")
			.setStatus(TaskObject.STATUS_UNCHECK)
			.setEstimate(13)
			.setRemains(8)
			.setActual(10)
			.setNotes("Test_Task_Notes");
		task.save();
		String sprintId = String.valueOf(sprint.getId());
		String taskId = String.valueOf(task.getId());
		// Assemble URL
		String URL = String.format(API_URL, mProjectName, sprintId, taskId, mUsername,
				mPassword);

		// Send Http Request
		HttpGet httpGet = new HttpGet(URL);
		String result = EntityUtils.toString(mHttpClient.execute(httpGet).getEntity(), StandardCharsets.UTF_8);
		JSONObject wholeJson = new JSONObject(result);
		assertEquals(task.getId(), wholeJson.getLong(SprintBacklogUtil.TAG_ID));
		assertEquals(task.getName(), wholeJson.getString(SprintBacklogUtil.TAG_NAME));
		assertEquals("", wholeJson.getString(SprintBacklogUtil.TAG_HANDLER));
		assertEquals(0, wholeJson.getJSONArray(SprintBacklogUtil.TAG_PARTNERS).length());
		assertEquals(task.getEstimate(), wholeJson.getLong(SprintBacklogUtil.TAG_ESTIMATE));
		assertEquals(task.getRemains(), wholeJson.getLong(SprintBacklogUtil.TAG_REMAINS));
		assertEquals(task.getActual(), wholeJson.getLong(SprintBacklogUtil.TAG_ACTUAL));
		assertEquals(task.getNotes(), wholeJson.getString(SprintBacklogUtil.TAG_NOTES));
	}
	
	@Test
	public void testGetTaskInformation_WithInvalidTask() throws Exception {
		SprintObject sprint = new SprintObject(mProject.getId());
		sprint.save();
		StoryObject story = new StoryObject(mProject.getId());
		story.save();
		TaskObject task = new TaskObject(mProject.getId());
		task.setName("Test_Task_Name")
			.setStatus(TaskObject.STATUS_UNCHECK)
			.setEstimate(13)
			.setRemains(8)
			.setActual(10)
			.setNotes("Test_Task_Notes");
		task.save();
		String sprintId = String.valueOf(sprint.getId());
		String taskId = String.valueOf(task.getId());
		// Assemble URL
		String URL = String.format(API_URL, mProjectName, sprintId, taskId, mUsername,
				mPassword);

		// Send Http Request
		HttpGet httpGet = new HttpGet(URL);
		String result = EntityUtils.toString(mHttpClient.execute(httpGet).getEntity(), StandardCharsets.UTF_8);
		assertEquals("", result);
	}
}
