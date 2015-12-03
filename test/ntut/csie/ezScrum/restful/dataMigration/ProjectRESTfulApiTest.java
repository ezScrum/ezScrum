package ntut.csie.ezScrum.restful.dataMigration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ProjectJSONEnum;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.databaseEnum.ProjectEnum;

public class ProjectRESTfulApiTest extends JerseyTest {
	private Configuration mConfig;
	private CreateProject mCP;
	private ResourceConfig mResourceConfig;
	private Client mClient;
	private HttpServer mHttpServer;
	private static String BASE_URL = "http://localhost:8080/ezScrum/resource/";
	private URI mBaseUri = URI.create(BASE_URL);

	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(ProjectRESTfulApi.class);
		return mResourceConfig;
	}

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() {
		// Set Test Mode
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

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
	public void testCreateProject() throws JSONException {
		// Test Data
		String projectName = "TEST_PROJECT_NAME";
		String projectDisplayName = "TEST_PROJECT_DISPLAY_NAME";
		String projectComment = "TEST_PROJECT_COMMENT";
		String projectProductOwner = "TEST_PROJECT_PRODUCT_OWNER";
		int projectMaxAttachFileSize = 2;
		long createTime = System.currentTimeMillis();

		JSONObject projectJSON = new JSONObject();
		projectJSON.put(ProjectJSONEnum.NAME, projectName);
		projectJSON.put(ProjectJSONEnum.DISPLAY_NAME, projectDisplayName);
		projectJSON.put(ProjectJSONEnum.COMMENT, projectComment);
		projectJSON.put(ProjectJSONEnum.PRODUCT_OWNER, projectProductOwner);
		projectJSON.put(ProjectJSONEnum.ATTATCH_MAX_SIZE, projectMaxAttachFileSize);
		projectJSON.put(ProjectJSONEnum.CREATE_TIME, createTime);
		
		JSONObject scrumRoleJSON = new JSONObject();
		projectJSON.put(ProjectJSONEnum.SCRUM_ROLES, scrumRoleJSON);
		JSONArray projectRoleJSONArray = new JSONArray();
		projectJSON.put(ProjectJSONEnum.PROJECT_ROLES, projectRoleJSONArray);
		JSONArray tagJSONArray = new JSONArray();
		projectJSON.put(ProjectJSONEnum.TAGS, tagJSONArray);
		
		// Call '/projects' API
		Response response = mClient.target(BASE_URL)
				                   .path("projects")
				                   .request()
				                   .post(Entity.text(projectJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		
		// Assert
		assertTrue(jsonResponse.getLong(ProjectEnum.ID) > -1);
		assertEquals(projectName, jsonResponse.getString(ProjectEnum.NAME));
		assertEquals(projectDisplayName, jsonResponse.getString(ProjectEnum.DISPLAY_NAME));
		assertEquals(projectComment, jsonResponse.getString(ProjectEnum.COMMENT));
		assertEquals(projectProductOwner, jsonResponse.getString(ProjectEnum.PRODUCT_OWNER));
		assertEquals(projectMaxAttachFileSize, jsonResponse.getInt(ProjectEnum.ATTATCH_MAX_SIZE));
		assertEquals(createTime, jsonResponse.getLong(ProjectEnum.CREATE_TIME));
	}
}
