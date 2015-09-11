package ntut.csie.ezScrum.restful.mobile.controller;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.restful.mobile.support.ConvertSprintBacklog;
import ntut.csie.ezScrum.restful.mobile.util.SprintBacklogUtil;
import ntut.csie.ezScrum.restful.mobile.util.SprintUtil;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.databaseEnum.StoryEnum;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

public class SprintBacklogWebServiceControllerTest {
	private static String SERVER_URL = "http://127.0.0.1:8080/ezScrum/web-service";
	private static String API_URL = "http://127.0.0.1:8080/ezScrum/web-service/%s/sprint-backlog/%s?username=%s&password=%s";
	private static HttpServer mServer;
	private HttpClient mHttpClient;
	private String mUsername = "admin";
	private String mPassword = "admin";

	private int mProjectCount = 1;
	private int mStoryCount = 3;
	private int mSprintCount = 2;
	private int mEstimate = 90;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
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

		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();

		mASTS = new AddStoryToSprint(mStoryCount, mEstimate, mCS, mCP,
				CreateProductBacklog.COLUMN_TYPE_EST);
		mASTS.exe();

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
	public void testGetSprintInfoList() throws ParseException, ClientProtocolException, IOException, JSONException {
		// Get Sprint
		SprintObject sprint = mProject.getCurrentSprint();
		// Get JSON
		String expectedJSONString = ConvertSprintBacklog.getSprintBacklogJsonString(sprint);
		
		// Assemble URL
		String URL = String.format(API_URL, mProjectName, "sprintlist", mUsername, mPassword);

		// Send Http Request
		HttpGet httpGet = new HttpGet(URL);
		String result = EntityUtils.toString(mHttpClient.execute(httpGet).getEntity(), StandardCharsets.UTF_8);
		JSONObject response = new JSONObject(result);
		
		// Assert
		assertEquals(expectedJSONString, response.toString());
	}

	@Test
	public void testGetSprintBacklog() throws ParseException, ClientProtocolException, IOException, JSONException {
		// Get Sprint
		SprintObject sprint = mCS.getSprints().get(0);
		// Get JSON
		String expectedJSONString = ConvertSprintBacklog.getSprintBacklogJsonString(sprint);
		
		// Assemble URL
		String URL = String.format(API_URL, mProjectName, sprint.getId() + "/sprintbacklog", mUsername, mPassword);

		// Send Http Request
		HttpGet httpGet = new HttpGet(URL);
		String result = EntityUtils.toString(mHttpClient.execute(httpGet).getEntity(), StandardCharsets.UTF_8);
		JSONObject response = new JSONObject(result);
		
		// Assert
		assertEquals(expectedJSONString, response.toString());
	}

	@Test
	public void testGetCurrentSprintBacklog() throws Exception {
		// create test data
		SprintObject currentSprint = mCS.getSprints().get(0);
		StoryObject story1 = currentSprint.getStories().get(0);
		TaskObject task1 = new TaskObject(mProject.getId());
		task1.setStoryId(story1.getId()).setName("Test_Task_Name1")
				.setStatus(TaskObject.STATUS_UNCHECK).setEstimate(13)
				.setRemains(8).setActual(10).setNotes("Test_Task_Notes");
		task1.save();
		TaskObject task2 = new TaskObject(mProject.getId());
		task2.setStoryId(story1.getId()).setName("Test_Task_Name2")
				.setStatus(TaskObject.STATUS_UNCHECK).setEstimate(13)
				.setRemains(8).setActual(10).setNotes("Test_Task_Notes");
		task2.save();
		
		// Send Http Request
		String URL = String.format(API_URL, mProjectName, "current-sprint", mUsername, mPassword);
		HttpGet httpGet = new HttpGet(URL);
		String result = EntityUtils.toString(mHttpClient.execute(httpGet)
				.getEntity(), StandardCharsets.UTF_8);
		JSONObject wholeJson = new JSONObject(result);
		JSONArray storyArray = wholeJson.getJSONArray("stories");
		// assert basic sprint info
		assertEquals(currentSprint.getId(), wholeJson.getLong(SprintUtil.TAG_ID));
		assertEquals(currentSprint.getProjectId(), wholeJson.getLong(SprintUtil.TAG_PROJECT_ID));
		assertEquals(currentSprint.getStartDateString(), wholeJson.getString(SprintUtil.TAG_START_DATE));
		assertEquals(currentSprint.getInterval(), wholeJson.getLong(SprintUtil.TAG_INTERVAL));
		assertEquals(currentSprint.getTeamSize(), wholeJson.getLong(SprintUtil.TAG_MEMBERS));
		assertEquals(currentSprint.getSerialId(), wholeJson.getLong(SprintUtil.TAG_SERIAL_ID));
		assertEquals(currentSprint.getGoal(), wholeJson.getString(SprintUtil.TAG_SPRINT_GOAL));
		assertEquals(currentSprint.getAvailableHours(), wholeJson.getLong(SprintUtil.TAG_HOURS_CAN_COMMIT));
		assertEquals(currentSprint.getFocusFactor(), wholeJson.getLong(SprintUtil.TAG_FOCUS_FACTOR));
		assertEquals(currentSprint.getDemoDateString(), wholeJson.getString(SprintUtil.TAG_DEMO_DATE));
		assertEquals(currentSprint.getDemoPlace(), wholeJson.getString(SprintUtil.TAG_DEMO_PLACE));
		assertEquals(currentSprint.getDailyInfo(), wholeJson.getString(SprintUtil.TAG_DAILY_MEETING));
		assertEquals(currentSprint.getDueDateString(), wholeJson.getString(SprintUtil.TAG_DUE_DATE));
		assertEquals(3, storyArray.length());
		// assert story1 info
		JSONObject story1Json = storyArray.getJSONObject(0);
		assertEquals(story1.getId(), story1Json.getLong(StoryEnum.ID));
		assertEquals(story1.getName(), story1Json.getString(StoryEnum.NAME));
		assertEquals(story1.getNotes(), story1Json.getString(StoryEnum.NOTES));
		assertEquals(story1.getHowToDemo(), story1Json.getString(StoryEnum.HOW_TO_DEMO));
		assertEquals(story1.getImportance(), story1Json.getInt(StoryEnum.IMPORTANCE));
		assertEquals(story1.getValue(), story1Json.getInt(StoryEnum.VALUE));
		assertEquals(story1.getEstimate(), story1Json.getInt(StoryEnum.ESTIMATE));
		assertEquals(story1.getStatus(), story1Json.getInt(StoryEnum.STATUS));
		assertEquals(story1.getSprintId(), story1Json.getLong(StoryEnum.SPRINT_ID));
		JSONArray taskArray1 = story1Json.getJSONArray("tasks");
		JSONArray historyArray1 = story1Json.getJSONArray("histories");
		JSONArray tagArray1 = story1Json.getJSONArray("tags");
		assertEquals(2, taskArray1.length());
		assertEquals(4, historyArray1.length());
		assertEquals(0, tagArray1.length());
		// assert story2 info
		StoryObject story2 = currentSprint.getStories().get(1);
		JSONObject story2Json = storyArray.getJSONObject(1);
		assertEquals(story2.getId(), story2Json.getLong(StoryEnum.ID));
		assertEquals(story2.getName(), story2Json.getString(StoryEnum.NAME));
		assertEquals(story2.getNotes(), story2Json.getString(StoryEnum.NOTES));
		assertEquals(story2.getHowToDemo(), story2Json.getString(StoryEnum.HOW_TO_DEMO));
		assertEquals(story2.getImportance(), story2Json.getInt(StoryEnum.IMPORTANCE));
		assertEquals(story2.getValue(), story2Json.getInt(StoryEnum.VALUE));
		assertEquals(story2.getEstimate(), story2Json.getInt(StoryEnum.ESTIMATE));
		assertEquals(story2.getStatus(), story2Json.getInt(StoryEnum.STATUS));
		assertEquals(story2.getSprintId(), story2Json.getLong(StoryEnum.SPRINT_ID));
		JSONArray taskArray2 = story2Json.getJSONArray("tasks");
		JSONArray historyArray2 = story2Json.getJSONArray("histories");
		JSONArray tagArray2 = story2Json.getJSONArray("tags");
		assertEquals(0, taskArray2.length());
		assertEquals(2, historyArray2.length());
		assertEquals(0, tagArray2.length());
		// assert story3 info
		StoryObject story3 = currentSprint.getStories().get(1);
		JSONObject story3Json = storyArray.getJSONObject(1);
		assertEquals(story3.getId(), story3Json.getLong(StoryEnum.ID));
		assertEquals(story3.getName(), story3Json.getString(StoryEnum.NAME));
		assertEquals(story3.getNotes(), story3Json.getString(StoryEnum.NOTES));
		assertEquals(story3.getHowToDemo(), story3Json.getString(StoryEnum.HOW_TO_DEMO));
		assertEquals(story3.getImportance(), story3Json.getInt(StoryEnum.IMPORTANCE));
		assertEquals(story3.getValue(), story3Json.getInt(StoryEnum.VALUE));
		assertEquals(story3.getEstimate(), story3Json.getInt(StoryEnum.ESTIMATE));
		assertEquals(story3.getStatus(), story3Json.getInt(StoryEnum.STATUS));
		assertEquals(story3.getSprintId(), story3Json.getLong(StoryEnum.SPRINT_ID));
		JSONArray taskArray3 = story3Json.getJSONArray("tasks");
		JSONArray historyArray3 = story3Json.getJSONArray("histories");
		JSONArray tagArray3 = story3Json.getJSONArray("tags");
		assertEquals(0, taskArray3.length());
		assertEquals(2, historyArray3.length());
		assertEquals(0, tagArray3.length());
	}

	@Test
	public void testGetStoriesId() throws Exception {
		long sprintId = mCS.getSprintsId().get(1);
		
		//Assemble URL
		String URL = String.format(API_URL, mProjectName, sprintId + "/storylist", mUsername, mPassword);
		
		// Send Http Request
		HttpGet httpGet = new HttpGet(URL);
		String result = EntityUtils.toString(mHttpClient.execute(httpGet).getEntity(),StandardCharsets.UTF_8);
		
		// get Expected JSON String
		String expectedJSONString = ConvertSprintBacklog.getStoriesIdJsonStringInSprint(SprintObject.get(sprintId).getStories());
		
		// Assert
		assertEquals(expectedJSONString, result);
	}
	
	@Test
	public void testGetStoriesId_WithCurrentSprint() throws Exception {
		String sprintId = "current";
		
		//Assemble URL
		String URL = String.format(API_URL, mProjectName, sprintId + "/storylist", mUsername, mPassword);
		
		// Send Http Request
		HttpGet httpGet = new HttpGet(URL);
		String result = EntityUtils.toString(mHttpClient.execute(httpGet).getEntity(),StandardCharsets.UTF_8);
		
		SprintObject currentSprint = mCS.getSprints().get(0);
		// get Expected JSON String
		String expectedJSONString = ConvertSprintBacklog.getStoriesIdJsonStringInSprint(currentSprint.getStories());
		
		// Assert
		assertEquals(expectedJSONString, result);
	}

	@Test
	public void testGetTasksId() throws Exception {
		SprintObject sprint = new SprintObject(mProject.getId());
		sprint.save();
		StoryObject story = new StoryObject(mProject.getId());
		story.setSprintId(sprint.getId());
		story.save();
		TaskObject task1 = new TaskObject(mProject.getId());
		task1.setStoryId(story.getId()).setName("Test_Task_Name1")
				.setStatus(TaskObject.STATUS_UNCHECK).setEstimate(13)
				.setRemains(8).setActual(10).setNotes("Test_Task_Notes");
		task1.save();
		TaskObject task2 = new TaskObject(mProject.getId());
		task2.setStoryId(story.getId()).setName("Test_Task_Name2")
				.setStatus(TaskObject.STATUS_UNCHECK).setEstimate(13)
				.setRemains(8).setActual(10).setNotes("Test_Task_Notes");
		task2.save();
		TaskObject task3 = new TaskObject(mProject.getId());
		task3.setStoryId(story.getId()).setName("Test_Task_Name3")
				.setStatus(TaskObject.STATUS_UNCHECK).setEstimate(13)
				.setRemains(8).setActual(10).setNotes("Test_Task_Notes");
		task3.save();
		String sprintId = String.valueOf(sprint.getId());
		String storyId = String.valueOf(story.getId());
		// Assemble URL
		String URL = String.format(API_URL, mProjectName, sprintId + "/" + storyId + "/task-id-list", mUsername, mPassword);

		// Send Http Request
		HttpGet httpGet = new HttpGet(URL);
		String result = EntityUtils.toString(mHttpClient.execute(httpGet)
				.getEntity(), StandardCharsets.UTF_8);
		JSONObject wholeJson = new JSONObject(result);
		JSONObject storyJson = wholeJson
				.getJSONObject(SprintBacklogUtil.TAG_STORY);
		long storyIdFromResponse = storyJson.getLong(SprintBacklogUtil.TAG_ID);
		JSONArray taskIdArray = storyJson
				.getJSONArray(SprintBacklogUtil.TAG_TASKSIDL);
		assertEquals(story.getId(), storyIdFromResponse);
		assertEquals(task1.getId(), taskIdArray.get(0));
		assertEquals(task2.getId(), taskIdArray.get(1));
		assertEquals(task3.getId(), taskIdArray.get(2));
	}

	@Test
	public void testGetTasksId_WithInvalidSprintId() throws Exception {
		SprintObject sprint = new SprintObject(mProject.getId());
		sprint.save();
		StoryObject story = new StoryObject(mProject.getId());
		story.setSprintId(sprint.getId());
		story.save();
		ArrayList<Long> tasksId = new ArrayList<>();
		TaskObject task1 = new TaskObject(mProject.getId());
		task1.setStoryId(story.getId()).setName("Test_Task_Name1")
				.setStatus(TaskObject.STATUS_UNCHECK).setEstimate(13)
				.setRemains(8).setActual(10).setNotes("Test_Task_Notes");
		task1.save();
		TaskObject task2 = new TaskObject(mProject.getId());
		task2.setStoryId(story.getId()).setName("Test_Task_Name2")
				.setStatus(TaskObject.STATUS_UNCHECK).setEstimate(13)
				.setRemains(8).setActual(10).setNotes("Test_Task_Notes");
		task2.save();
		TaskObject task3 = new TaskObject(mProject.getId());
		task3.setStoryId(story.getId()).setName("Test_Task_Name3")
				.setStatus(TaskObject.STATUS_UNCHECK).setEstimate(13)
				.setRemains(8).setActual(10).setNotes("Test_Task_Notes");
		task3.save();
		tasksId.add(task1.getId());
		tasksId.add(task2.getId());
		tasksId.add(task3.getId());
		String storyId = String.valueOf(story.getId());
		// create invalid sprint
		SprintObject invalidSprint = new SprintObject(mProject.getId());
		// Assemble URL
		String URL = String.format(API_URL, mProjectName, invalidSprint.getId() + "/" + storyId + "/task-id-list",
				mUsername, mPassword);

		// Send Http Request
		HttpGet httpGet = new HttpGet(URL);
		String result = EntityUtils.toString(mHttpClient.execute(httpGet)
				.getEntity(), StandardCharsets.UTF_8);
		// assert result
		assertEquals("", result);
	}

	@Test
	public void testGetTaskHistory() throws Exception {
		SprintObject sprint = new SprintObject(mProject.getId());
		sprint.save();
		StoryObject story = new StoryObject(mProject.getId());
		story.setSprintId(sprint.getId());
		story.save();
		TaskObject task = new TaskObject(mProject.getId());
		task.setStoryId(story.getId()).setName("Test_Task_Name")
				.setStatus(TaskObject.STATUS_UNCHECK).setEstimate(13)
				.setRemains(8).setActual(10).setNotes("Test_Task_Notes");
		task.save();
		String sprintId = String.valueOf(sprint.getId());
		String taskId = String.valueOf(task.getId());
		// Assemble URL
		String URL = String.format(API_URL, mProjectName, sprintId + "/" + taskId + "/history",
				mUsername, mPassword);

		// Send Http Request
		HttpGet httpGet = new HttpGet(URL);
		String result = EntityUtils.toString(mHttpClient.execute(httpGet)
				.getEntity(), StandardCharsets.UTF_8);
		JSONObject wholeJson = new JSONObject(result);
		JSONArray historyJsonArray = wholeJson
				.getJSONArray(SprintBacklogUtil.TAG_TASKHISTORIES);
		assertEquals(2, historyJsonArray.length());
		// assert first history
		assertEquals(
				task.getHistories().get(0).getHistoryType(),
				historyJsonArray.getJSONObject(0).getInt(
						SprintBacklogUtil.TAG_HISTORYTYPE));
		assertEquals(
				parseDate(task.getHistories().get(0).getCreateTime()),
				historyJsonArray.getJSONObject(0).getString(
						SprintBacklogUtil.TAG_MODIFYDATE));
		assertEquals(
				task.getHistories().get(0).getDescription(),
				historyJsonArray.getJSONObject(0).getString(
						SprintBacklogUtil.TAG_DESCRIPTION));
		// assert second history
		assertEquals(
				task.getHistories().get(1).getHistoryType(),
				historyJsonArray.getJSONObject(1).getInt(
						SprintBacklogUtil.TAG_HISTORYTYPE));
		assertEquals(
				parseDate(task.getHistories().get(1).getCreateTime()),
				historyJsonArray.getJSONObject(1).getString(
						SprintBacklogUtil.TAG_MODIFYDATE));
		assertEquals(
				task.getHistories().get(1).getDescription(),
				historyJsonArray.getJSONObject(1).getString(
						SprintBacklogUtil.TAG_DESCRIPTION));
	}

	@Test
	public void testGetTaskHistory_WithInvalidSprint() throws Exception {
		SprintObject sprint = new SprintObject(mProject.getId());
		sprint.save();
		StoryObject story = new StoryObject(mProject.getId());
		story.setSprintId(sprint.getId());
		story.save();
		TaskObject task = new TaskObject(mProject.getId());
		task.setStoryId(story.getId()).setName("Test_Task_Name")
				.setStatus(TaskObject.STATUS_UNCHECK).setEstimate(13)
				.setRemains(8).setActual(10).setNotes("Test_Task_Notes");
		task.save();
		SprintObject invalidSprint = new SprintObject(mProject.getId());
		invalidSprint.save();
		String invalidSprintId = String.valueOf(invalidSprint.getId());
		String taskId = String.valueOf(task.getId());
		// Assemble URL
		String URL = String.format(API_URL, mProjectName, invalidSprintId + "/" + taskId + "/history", mUsername, mPassword);

		// Send Http Request
		HttpGet httpGet = new HttpGet(URL);
		String result = EntityUtils.toString(mHttpClient.execute(httpGet)
				.getEntity(), StandardCharsets.UTF_8);
		// assert result
		assertEquals("", result);
	}

	@Test
	public void testGetTaskInformation() throws Exception {
		SprintObject sprint = new SprintObject(mProject.getId());
		sprint.save();
		StoryObject story = new StoryObject(mProject.getId());
		story.setSprintId(sprint.getId());
		story.save();
		TaskObject task = new TaskObject(mProject.getId());
		task.setStoryId(story.getId()).setName("Test_Task_Name")
				.setStatus(TaskObject.STATUS_UNCHECK).setEstimate(13)
				.setRemains(8).setActual(10).setNotes("Test_Task_Notes");
		task.save();
		String sprintId = String.valueOf(sprint.getId());
		String taskId = String.valueOf(task.getId());
		// Assemble URL
		String URL = String.format(API_URL, mProjectName, sprintId + "/" + taskId,
				mUsername, mPassword);

		// Send Http Request
		HttpGet httpGet = new HttpGet(URL);
		String result = EntityUtils.toString(mHttpClient.execute(httpGet)
				.getEntity(), StandardCharsets.UTF_8);
		JSONObject wholeJson = new JSONObject(result);
		assertEquals(task.getId(), wholeJson.getLong(SprintBacklogUtil.TAG_ID));
		assertEquals(task.getName(),
				wholeJson.getString(SprintBacklogUtil.TAG_NAME));
		assertEquals("", wholeJson.getString(SprintBacklogUtil.TAG_HANDLER));
		assertEquals(0, wholeJson.getJSONArray(SprintBacklogUtil.TAG_PARTNERS)
				.length());
		assertEquals(task.getEstimate(),
				wholeJson.getLong(SprintBacklogUtil.TAG_ESTIMATE));
		assertEquals(task.getRemains(),
				wholeJson.getLong(SprintBacklogUtil.TAG_REMAINS));
		assertEquals(task.getActual(),
				wholeJson.getLong(SprintBacklogUtil.TAG_ACTUAL));
		assertEquals(task.getNotes(),
				wholeJson.getString(SprintBacklogUtil.TAG_NOTES));
	}

	@Test
	public void testGetTaskInformation_WithInvalidTask() throws Exception {
		SprintObject sprint = new SprintObject(mProject.getId());
		sprint.save();
		StoryObject story = new StoryObject(mProject.getId());
		story.save();
		TaskObject task = new TaskObject(mProject.getId());
		task.setName("Test_Task_Name").setStatus(TaskObject.STATUS_UNCHECK)
				.setEstimate(13).setRemains(8).setActual(10)
				.setNotes("Test_Task_Notes");
		task.save();
		String sprintId = String.valueOf(sprint.getId());
		String taskId = String.valueOf(task.getId());
		// Assemble URL
		String URL = String.format(API_URL, mProjectName, sprintId + "/" + taskId,
				mUsername, mPassword);

		// Send Http Request
		HttpGet httpGet = new HttpGet(URL);
		String result = EntityUtils.toString(mHttpClient.execute(httpGet)
				.getEntity(), StandardCharsets.UTF_8);
		assertEquals("", result);
	}

	private static String parseDate(long date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
		Date d = new Date(date);

		String modifiedDate = sdf.format(d);
		return modifiedDate;
	}
}
