package ntut.csie.ezScrum.restful.dataMigration;

import static org.junit.Assert.assertTrue;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.HttpServer;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplanItem;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;

public class RESTfulApiPathConflictTest extends JerseyTest {
	private Configuration mConfig;
	private CreateProject mCP;
	private CreateRelease mCR;
	private CreateSprint mCS;
	private CreateUnplanItem mCU;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;

	private Client mClient;
	private HttpServer mHttpServer;
	private ResourceConfig mResourceConfig;
	private static String BASE_URL = "http://localhost:8080/ezScrum/resource";
	private URI mBaseUri = URI.create(BASE_URL);

	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(AccountRESTfulApi.class, ProjectRESTfulApi.class, SprintRESTfulApi.class, StoryRESTfulApi.class,
		        TaskRESTfulApi.class, DroppedStoryRESTfulApi.class, DroppedTaskRESTfulApi.class, ReleaseRESTfulApi.class,
		        RetrospectiveRESTfulApi.class, UnplanRESTfulApi.class);
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

		// Create Project
		mCP = new CreateProject(1);
		mCP.exeCreateForDb();

		// Create Release
		mCR = new CreateRelease(2, mCP);
		mCR.exe();

		// Create Sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		// Add Story to sprint
		mASTS = new AddStoryToSprint(2, 8, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mASTS.exe();

		// Add Task to Story
		mATTS = new AddTaskToStory(2, 13, mASTS, mCP);
		mATTS.exe();

		// Create Unplan
		mCU = new CreateUnplanItem(2, mCP, mCS);
		mCU.exe();

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

		ini = null;
		mCP = null;
		mCR = null;
		mCS = null;
		mASTS = null;
		mATTS = null;
		mCU = null;
	}

	@Test
	public void testIsPathConflict() {
		ProjectObject project = mCP.getAllProjects().get(0);
		SprintObject sprint = mCS.getSprints().get(0);
		StoryObject story = mASTS.getStories().get(0);
		StoryObject droppedStory = mASTS.getStories().get(1);
		TaskObject task = mATTS.getTasks().get(0);
		TaskObject droppedTask = mATTS.getTasks().get(1);
		UnplanObject unplan = mCU.getUnplans().get(0);
		
		// Drop Story
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(project);
		productBacklogHelper.dropStoryFromSprint(droppedStory.getId());
		droppedStory = StoryObject.get(droppedStory.getId());

		// Create Retrospective
		RetrospectiveObject retrospective = new RetrospectiveObject(project.getId());
		retrospective.setName("retrospective1");
		retrospective.setDescription("retrospective");
		retrospective.setStatus(RetrospectiveObject.STATUS_NEW);
		retrospective.setType(RetrospectiveObject.TYPE_GOOD);
		retrospective.setSprintId(sprint.getId());
		retrospective.save();

		// Api Test
		// Account
		// Call '/accounts' API
		Response response = mClient.target(mBaseUri)
		        .path("accounts")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Project
		// Call '/projects' API
		response = mClient.target(mBaseUri)
		        .path("projects")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Call '/projects/{projectId/scrumroles' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/scrumroles")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Call '/projects/{projectId/projectroles' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/projectroles")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Call '/projects/{projectId/tags' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/tags")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Sprint
		// Call '/projects/{projectId/sprints' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/sprints")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Story
		// Call '/projects/{projectId/sprints/{sprintId}/stories' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/stories")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Call '/projects/{projectId/sprints/{sprintId}/stories/{storyId}/tags' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/stories/" + story.getId() +
		                "/tags")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Call '/projects/{projectId/sprints/{sprintId}/stories/{storyId}/histories' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/stories/" + story.getId() +
		                "/histories")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Call '/projects/{projectId/sprints/{sprintId}/stories/{storyId}/histories' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/stories/" + story.getId() +
		                "/histories")
		        .request()
		        .delete();
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Call '/projects/{projectId/sprints/{sprintId}/stories/{storyId}/attachfiles' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/stories/" + story.getId() +
		                "/attachfiles")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Task
		// Call '/projects/{projectId/sprints/{sprintId}/stories/{storyId}/tasks' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/stories/" + story.getId() +
		                "/tasks")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Call '/projects/{projectId/sprints/{sprintId}/stories/{storyId}/tasks/{taskId}/histories' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/stories/" + story.getId() +
		                "/tasks/" + task.getId() +
		                "/histories")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Call '/projects/{projectId/sprints/{sprintId}/stories/{storyId}/tasks/{taskId}/histories' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/stories/" + story.getId() +
		                "/tasks/" + task.getId() +
		                "/histories")
		        .request()
		        .delete();
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Call '/projects/{projectId/sprints/{sprintId}/stories/{storyId}/tasks/{taskId}/attachfiles' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/stories/" + story.getId() +
		                "/tasks/" + task.getId() +
		                "/attachfiles")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Release
		// Call '/projects/{projectId/releases' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/releases")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Retrospective
		// Call '/projects/{projectId/sprints/{sprintId}/stories/{storyId}/retrospectives' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/retrospectives")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Unplan
		// Call '/projects/{projectId/sprints/{sprintId}/stories/{storyId}/unplans' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/unplans")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Call '/projects/{projectId/sprints/{sprintId}/stories/{storyId}/unplans/{unplanId}/histories' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/unplans/" + unplan.getId() +
		                "/histories")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());
		
		// Call '/projects/{projectId/sprints/{sprintId}/stories/{storyId}/unplans/{unplanId}/histories' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/sprints/" + sprint.getId() +
		                "/unplans/" + unplan.getId() +
		                "/histories")
		        .request()
		        .delete();
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());
		
		// Dropped Story
		// Call '/projects/{projectId/stories/' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/stories")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());
		
		// Call '/projects/{projectId/stories/{storyId}/tags' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/stories/" + droppedStory.getId() +
		                "/tags")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());
		
		// Call '/projects/{projectId/stories/{storyId}/histories' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/stories/" + droppedStory.getId() +
		                "/histories")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());
		
		// Call '/projects/{projectId/stories/{storyId}/histories' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/stories/" + droppedStory.getId() +
		                "/histories")
		        .request()
		        .delete();
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());
		
		// Call '/projects/{projectId/stories/{storyId}/attachfiles' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/stories/" + droppedStory.getId() +
		                "/attachfiles")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());
		
		// Call '/projects/{projectId/stories/{storyId}/tasks' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/stories/" + droppedStory.getId() +
		                "/tasks")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());
		
		// Call '/projects/{projectId/stories/{storyId}/tasks/{taskId}/histories' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/stories/" + droppedStory.getId() +
		                "/tasks/" + droppedStory.getTasks().get(0).getId() +
		                "/histories")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());
		
		// Call '/projects/{projectId/stories/{storyId}/tasks/{taskId}/histories' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/stories/" + droppedStory.getId() +
		                "/tasks/" + droppedStory.getTasks().get(0).getId() +
		                "/histories")
		        .request()
		        .delete();
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());
		
		// Call '/projects/{projectId/stories/{storyId}/tasks/{taskId}/attachfiles' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/stories/" + droppedStory.getId() +
		                "/tasks/" + droppedStory.getTasks().get(0).getId() +
		                "/attachfiles")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());
		
		// Dropped Task
		// Call '/projects/{projectId/tasks' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/tasks")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());
		
		// Call '/projects/{projectId/tasks/{taskId}/histories' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/tasks/" + droppedTask.getId() +
		                "/histories")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());

		// Call '/projects/{projectId/tasks/{taskId}/histories' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/tasks/" + droppedTask.getId() +
		                "/histories")
		        .request()
		        .delete();
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());
		
		// Call '/projects/{projectId/tasks/{taskId}/attachfiles' API
		response = mClient.target(mBaseUri)
		        .path("projects/" + project.getId() +
		                "/tasks/" + droppedTask.getId() +
		                "/attachfiles")
		        .request()
		        .post(Entity.text(""));
		assertTrue(response.getStatus() != Response.Status.NOT_FOUND.getStatusCode());
	}
}
