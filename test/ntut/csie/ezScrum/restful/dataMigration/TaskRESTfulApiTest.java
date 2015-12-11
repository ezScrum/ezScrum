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
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TaskJSONEnum;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateTask;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.databaseEnum.RoleEnum;
import ntut.csie.ezScrum.web.databaseEnum.TaskEnum;

public class TaskRESTfulApiTest extends JerseyTest {
	private Configuration mConfig;
	private CreateAccount mCA;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private CreateTask mCT;
	private ResourceConfig mResourceConfig;
	private Client mClient;
	private HttpServer mHttpServer;
	private static String BASE_URL = "http://localhost:8080/ezScrum/resource/";
	private URI mBaseUri = URI.create(BASE_URL);

	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(TaskRESTfulApi.class);
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
		mCA = new CreateAccount(1);
		mCA.exe();

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
		mCT = null;
		mHttpServer = null;
		mClient = null;
	}
	
	@Test
	public void testCreateTask() throws JSONException {
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
		StoryObject story = mASTS.getStories().get(0);
		SprintObject sprint = mCS.getSprints().get(0);
		
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

		// Call '/projects/{projectId}/sprints/{sprintId}/stories/{storyId}/tasks' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/stories/" + story.getId() + 
		                "/tasks")
		        .request()
		        .post(Entity.text(taskJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = jsonResponse.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertTrue(contentJSON.getLong(TaskEnum.ID) != -1);
		TaskObject task = TaskObject.get(contentJSON.getLong(TaskEnum.ID));
		assertEquals(name, task.getName());
		assertEquals(handler, task.getHandler().getUsername());
		assertEquals(estimate, task.getEstimate());
		assertEquals(remain, task.getRemains());
		assertEquals(actual, task.getActual());
		assertEquals(notes, task.getNotes());
		assertEquals(status, task.getStatusString());
	}
	
	@Test
	public void testCreateHistoryInTask() throws JSONException {
		ProjectObject project = mCP.getAllProjects().get(0);
		SprintObject sprint = mCS.getSprints().get(0);
		StoryObject story = mASTS.getStories().get(0);
		
		TaskObject task = new TaskObject(project.getId());
		task.setName("TEST_NAME").setStoryId(story.getId()).save();
		
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
		
		// Call '/projects/{projectId}/sprints/{sprintId}/stories/{storyId}/tasks/{taskId}/histories' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
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
	public void testDeleteHistoriesInTask() throws Exception {
		ProjectObject project = mCP.getAllProjects().get(0);
		SprintObject sprint = mCS.getSprints().get(0);
		StoryObject story = mASTS.getStories().get(0);
		
		// Create Task
		mCT = new CreateTask(1, 8, story.getId(), mCP);
		mCT.exe();
		
		// Call '/projects/{projectId}/sprints/{sprintId}/stories/{storyId}/tasks/{taskId}/histories' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/stories/" + story.getId() + 
		                "/tasks/" + mCT.getTaskIDList().get(0) + 
		                "/histories")
		        .request()
		        .delete();
		TaskObject task = TaskObject.get(mCT.getTaskIDList().get(0));
		
		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(0, task.getHistories().size());
	}

	@Test
	public void testCreateAttachFileInTask() throws JSONException {
		ProjectObject project = mCP.getAllProjects().get(0);
		SprintObject sprint = mCS.getSprints().get(0);
		StoryObject story = mASTS.getStories().get(0);
		
		TaskObject task = new TaskObject(project.getId());
		task.setName("TEST_NAME").setStoryId(story.getId()).save();
		
		// Check task attach files before create attach file
		assertEquals(0, task.getAttachFiles().size());
		
		JSONObject attachFileJSON = new JSONObject();
		attachFileJSON.put(AttachFileJSONEnum.NAME, "Task01.txt");
		attachFileJSON.put(AttachFileJSONEnum.CONTENT_TYPE, "application/octet-stream");
		attachFileJSON.put(AttachFileJSONEnum.BINARY, "VGFzazAx");
		
		// Call '/projects/{projectId}/sprints/{sprintId}/stories/{storyId}/tasks/{taskId}/attachfiles' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
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
