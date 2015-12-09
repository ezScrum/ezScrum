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
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

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

		// Create Account
		mCA = new CreateAccount(2);
		mCA.exe();

		// Create Project
		mCP = new CreateProject(1);
		mCP.exeCreate();

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
		mHttpServer = null;
		mClient = null;
	}

	@Test
	public void testCreateDroppedStory() {
		// TODO
		assertTrue("todo", false);
	}

	@Test
	public void testCreateTagInStory() {
		// TODO
		assertTrue("todo", false);
	}

	@Test
	public void testCreateHistoryInStory() {
		// TODO
		assertTrue("todo", false);
	}
	
	@Test
	public void testDeleteHistoryInStory() {
		// TODO
		assertTrue("todo", false);
	}

	@Test
	public void testCreateAttachFile() {
		// TODO
		assertTrue("todo", false);
	}

	@Test
	public void testCreateTaskInDroppedStory() {
		// TODO
		assertTrue("todo", false);
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
	public void testCreateAttachFileInTask() {
		// TODO
		assertTrue("todo", false);
	}
}
