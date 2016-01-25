package ntut.csie.ezScrum.restful.dataMigration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

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

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.AttachFileJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.HistoryJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.StoryJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TagJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TaskJSONEnum;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.databaseEnum.RoleEnum;
import ntut.csie.ezScrum.web.databaseEnum.StoryEnum;

public class DroppedStoryRESTfulApiTest extends JerseyTest {
	private Configuration mConfig;
	private CreateAccount mCA;
	private CreateProject mCP;
	private ResourceConfig mResourceConfig;
	private Client mClient;
	private HttpServer mHttpServer;
	private static String BASE_URL = "http://localhost:8080/ezScrum/resource/";
	private URI mBaseUri = URI.create(BASE_URL);

	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(DroppedStoryRESTfulApi.class);
		return mResourceConfig;
	}

	@Before
	public void setUp() throws Exception {
		// Set Test Mode
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// Create Account
		mCA = new CreateAccount(2);
		mCA.exe();

		// Create Project
		mCP = new CreateProject(1);
		mCP.exeCreateForDb();

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

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		// Stop Server
		mHttpServer.stop(0);

		// ============= release ==============
		ini = null;
		mCP = null;
		mHttpServer = null;
		mClient = null;
	}

	@Test
	public void testCreateDroppedStory() throws JSONException {
		// Test Data
		String name = "TEST_STORY_NAME";
		String status = "new";
		int estimate = 3;
		int importance = 98;
		int value = 20;
		String notes = "TEST_STORY_VALUE";
		String howToDemo = "TEST_STORY_HOWTODEMO";
		ProjectObject project = mCP.getAllProjects().get(0);

		JSONObject storyJSON = new JSONObject();
		storyJSON.put(StoryJSONEnum.NAME, name);
		storyJSON.put(StoryJSONEnum.STATUS, status);
		storyJSON.put(StoryJSONEnum.ESTIMATE, estimate);
		storyJSON.put(StoryJSONEnum.IMPORTANCE, importance);
		storyJSON.put(StoryJSONEnum.VALUE, value);
		storyJSON.put(StoryJSONEnum.NOTES, notes);
		storyJSON.put(StoryJSONEnum.HOW_TO_DEMO, howToDemo);
		
		// Call '/projects/{projectId}/stories' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		              "/stories")
		        .request()
		        .post(Entity.text(storyJSON.toString()));

		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		String responseMessage = responseJSON.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);
		JSONObject responseContent = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
		
		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(ResponseJSONEnum.SUCCESS_MEESSAGE, responseMessage);
		long storyId = responseContent.getLong(StoryEnum.ID);
		assertTrue(storyId > 0);
		StoryObject story = StoryObject.get(storyId);
		assertEquals(story.toString(), responseContent.toString());
	}

	@Test
	public void testCreateTagInDroppedStory() throws JSONException {
		// Test Data
		String tagName = "TEST_TAG_NAME";
		ProjectObject project = mCP.getAllProjects().get(0);
		StoryObject story = new StoryObject(project.getId());
		story.save();
		
		// Create Tag
		TagObject tag = new TagObject(tagName, project.getId());
		tag.save();
		
		JSONObject tagJSON = new JSONObject();
		tagJSON.put(TagJSONEnum.NAME, tagName);

		// Call '/projects/{projectId}/stories/{storyId}/tags' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		              "/stories/" + story.getId() + 
		              "/tags")
		        .request()
		        .post(Entity.text(tagJSON.toString()));

		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		String responseMessage = responseJSON.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);
		String responseContent = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT).toString();
		
		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(ResponseJSONEnum.SUCCESS_MEESSAGE, responseMessage);
		assertEquals(story.getTags().get(0).toString(), responseContent);
	}

	@Test
	public void testCreateHistoryInDroppedStory() throws JSONException {
		ProjectObject project = mCP.getAllProjects().get(0);
		StoryObject story = new StoryObject(project.getId());
		story.save();
		
		// Check story's histories before create history
		assertEquals(1, story.getHistories().size());
		assertEquals(HistoryObject.TYPE_CREATE, story.getHistories().get(0).getHistoryType());
				
		long createTime = System.currentTimeMillis();
		JSONObject historyJSON = new JSONObject();
		historyJSON.put(HistoryJSONEnum.HISTORY_TYPE, "STATUS")
		           .put(HistoryJSONEnum.OLD_VALUE, "new")
		           .put(HistoryJSONEnum.NEW_VALUE, "closed")
		           .put(HistoryJSONEnum.CREATE_TIME, createTime);

		// Call '/projects/{projectId}/stories/{storyId}/histories' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		              "/stories/" + story.getId() + 
		              "/histories")
		        .request()
		        .post(Entity.text(historyJSON.toString()));

		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		String responseMessage = responseJSON.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);
		String responseContent = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT).toString();
		
		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(ResponseJSONEnum.SUCCESS_MEESSAGE, responseMessage);
		assertEquals(story.getHistories().get(1).toString(), responseContent);
	}
	
	@Test
	public void testDeleteHistoryInDroppedStory() throws JSONException {
		ProjectObject project = mCP.getAllProjects().get(0);
		StoryObject story = new StoryObject(project.getId());
		story.save();
		
		// Check story's histories before create history
		assertEquals(1, story.getHistories().size());
		assertEquals(HistoryObject.TYPE_CREATE, story.getHistories().get(0).getHistoryType());
		
		// Call '/projects/{projectId}/stories/{storyId}/histories' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		              "/stories/" + story.getId() + 
		              "/histories")
		        .request()
		        .delete();

		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		String responseMessage = responseJSON.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);
		String responseContent = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT).toString();
		
		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(ResponseJSONEnum.SUCCESS_MEESSAGE, responseMessage);
		assertEquals(new JSONObject().toString(), responseContent);
		assertEquals(0, story.getHistories().size());
	}

	@Test
	public void testCreateAttachFileInDroppedStory() throws JSONException {
		ProjectObject project = mCP.getAllProjects().get(0);
		StoryObject story = new StoryObject(project.getId());
		story.save();
		
		// Check story attach files before create attach file
		assertEquals(0, story.getAttachFiles().size());

		JSONObject attachFileJSON = new JSONObject();
		attachFileJSON.put(AttachFileJSONEnum.NAME, "Story01.txt");
		attachFileJSON.put(AttachFileJSONEnum.CONTENT_TYPE, "application/octet-stream");
		attachFileJSON.put(AttachFileJSONEnum.BINARY, "U3RvcnkwMQ==");

		// Call '/projects/{projectId}/stories/{storyId}/attachfiles' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		              "/stories/" + story.getId() + 
		              "/attachfiles")
		        .request()
		        .post(Entity.text(attachFileJSON.toString()));

		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		String responseMessage = responseJSON.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);
		String responseContent = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT).toString();
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(ResponseJSONEnum.SUCCESS_MEESSAGE, responseMessage);
		assertEquals(new JSONObject().toString(), responseContent);
		assertEquals(1, story.getAttachFiles().size());
		assertEquals(attachFileJSON.getString(AttachFileJSONEnum.NAME), story.getAttachFiles().get(0).getName());
		assertEquals(attachFileJSON.getString(AttachFileJSONEnum.CONTENT_TYPE), story.getAttachFiles().get(0).getContentType());
		assertEquals(story.getId(), story.getAttachFiles().get(0).getIssueId());
		assertEquals(IssueTypeEnum.TYPE_STORY, story.getAttachFiles().get(0).getIssueType());
		
		// clean test data
		File file = new File(story.getAttachFiles().get(0).getPath());
		file.delete();
	}

	@Test
	public void testCreateTaskInDroppedStory() throws JSONException {
		// Test Data
		AccountObject account = mCA.getAccountList().get(0);
		String name = "TEST_TASK_NAME";
		String handler = account.getUsername();
		int estimate = 3;
		int remain = 2;
		int actual = 3;
		String notes = "TEST_CREATE_TASK";
		String status = "assigned";
		ProjectObject project = mCP.getAllProjects().get(0);
		StoryObject story = new StoryObject(project.getId());
		story.save();
		
		// Add Account to Project
		account.joinProjectWithScrumRole(project.getId(), RoleEnum.ScrumTeam);

		JSONObject taskJSON = new JSONObject();
		taskJSON.put(TaskJSONEnum.NAME, name);
		taskJSON.put(TaskJSONEnum.HANDLER, handler);
		taskJSON.put(TaskJSONEnum.ESTIMATE, estimate);
		taskJSON.put(TaskJSONEnum.REMAIN, remain);
		taskJSON.put(TaskJSONEnum.ACTUAL, actual);
		taskJSON.put(TaskJSONEnum.NOTES, notes);
		taskJSON.put(TaskJSONEnum.STATUS, status);
		JSONArray partnersIdJSONArray = new JSONArray();
		taskJSON.put(TaskJSONEnum.PARTNERS, partnersIdJSONArray);
		
		// Call '/projects/{projectId}/stories/{storyId}/tasks' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		              "/stories/" + story.getId() + 
		              "/tasks")
		        .request()
		        .post(Entity.text(taskJSON.toString()));
		
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		String responseMessage = responseJSON.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);
		JSONObject responseContent = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
		
		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(ResponseJSONEnum.SUCCESS_MEESSAGE, responseMessage);
		long taskId = responseContent.getLong(StoryEnum.ID);
		assertTrue(taskId > 0);
		TaskObject task = TaskObject.get(taskId);
		assertEquals(task.toString(), responseContent.toString());
	}

	@Test
	public void testCreateHistoryInTask() throws JSONException {
		ProjectObject project = mCP.getAllProjects().get(0);
		// Create Test Data - Dropped Story
		StoryObject story = new StoryObject(project.getId());
		story.save(); // save without setting sprint id
		// Create Test Data -Task
		TaskObject task = new TaskObject(project.getId());
		task.setStoryId(story.getId()).setName("TEST_NAME").save();
		
		// Check task histories before create history
		assertEquals(2, task.getHistories().size());
		assertEquals(HistoryObject.TYPE_CREATE, task.getHistories().get(0).getHistoryType());
		assertEquals(HistoryObject.TYPE_APPEND, task.getHistories().get(1).getHistoryType());

		long createTime = System.currentTimeMillis();
		JSONObject historyJSON = new JSONObject();
		historyJSON.put(HistoryJSONEnum.HISTORY_TYPE, "STATUS");
		historyJSON.put(HistoryJSONEnum.OLD_VALUE, "new");
		historyJSON.put(HistoryJSONEnum.NEW_VALUE, "assigned");
		historyJSON.put(HistoryJSONEnum.CREATE_TIME, createTime);
		
		// Call '/projects/{projectId}/tasks/{taskId}/histories' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		        		"/stories/" + story.getId() + 
		                "/tasks/" + task.getId() + 
		                "/histories")
		        .request()
		        .post(Entity.text(historyJSON.toString()));
		
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		String responseMessage = responseJSON.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);
		String responseContent = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT).toString();
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(ResponseJSONEnum.SUCCESS_MEESSAGE, responseMessage);
		assertEquals(task.getHistories().get(2).toString(), responseContent);
	}
	
	@Test
	public void testDeleteHistoryInTask() throws JSONException {
		ProjectObject project = mCP.getAllProjects().get(0);
		// Create Test Data - Dropped Story
		StoryObject story = new StoryObject(project.getId());
		story.save(); // save without setting sprint id
		// Create Test Data -Task
		TaskObject task = new TaskObject(project.getId());
		task.setStoryId(story.getId()).setName("TEST_NAME").save();
		
		// Check task histories before create history
		assertEquals(2, task.getHistories().size());
		assertEquals(HistoryObject.TYPE_CREATE, task.getHistories().get(0).getHistoryType());
		assertEquals(HistoryObject.TYPE_APPEND, task.getHistories().get(1).getHistoryType());
		
		// Call '/projects/{projectId}/tasks/{taskId}/histories' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		        		"/stories/" + story.getId() + 
		                "/tasks/" + task.getId() + 
		                "/histories")
		        .request()
		        .delete();
		
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		String responseMessage = responseJSON.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);
		String responseContent = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT).toString();
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(ResponseJSONEnum.SUCCESS_MEESSAGE, responseMessage);
		assertEquals(new JSONObject().toString(), responseContent);
		assertEquals(0, task.getHistories().size());
	}

	@Test
	public void testCreateAttachFileInTask() throws JSONException {
		ProjectObject project = mCP.getAllProjects().get(0);
		// Create Test Data - Dropped Story
		StoryObject story = new StoryObject(project.getId());
		story.save(); // save without setting sprint id
		// Create Test Data -Task
		TaskObject task = new TaskObject(project.getId());
		task.setStoryId(story.getId()).setName("TEST_NAME").save();
		
		// Check task attach files before create attach file
		assertEquals(0, task.getAttachFiles().size());

		JSONObject attachFileJSON = new JSONObject();
		attachFileJSON.put(AttachFileJSONEnum.NAME, "Task01.txt");
		attachFileJSON.put(AttachFileJSONEnum.CONTENT_TYPE, "application/octet-stream");
		attachFileJSON.put(AttachFileJSONEnum.BINARY, "VGFzazAx");
		
		// Call '/projects/{projectId}/tasks/{taskId}/attachfiles' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		        		"/stories/" + story.getId() + 
		                "/tasks/" + task.getId() + 
		                "/attachfiles")
		        .request()
		        .post(Entity.text(attachFileJSON.toString()));
		
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		String responseMessage = responseJSON.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);
		String responseContent = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT).toString();
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(ResponseJSONEnum.SUCCESS_MEESSAGE, responseMessage);
		assertEquals(new JSONObject().toString(), responseContent);
		assertEquals(1, task.getAttachFiles().size());
		assertEquals(attachFileJSON.getString(AttachFileJSONEnum.NAME), task.getAttachFiles().get(0).getName());
		assertEquals(attachFileJSON.getString(AttachFileJSONEnum.CONTENT_TYPE), task.getAttachFiles().get(0).getContentType());
		assertEquals(task.getId(), task.getAttachFiles().get(0).getIssueId());
		assertEquals(IssueTypeEnum.TYPE_TASK, task.getAttachFiles().get(0).getIssueType());
		
		// clean test data
		File file = new File(task.getAttachFiles().get(0).getPath());
		file.delete();
	}
}
