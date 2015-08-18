package ntut.csie.ezScrum.restful.mobile.controller.v2;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TokenObject;

import org.apache.http.HttpResponse;
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

public class ProjectApiTest extends ApiTestBaseClass {
	private static String SERVER_URL = "http://127.0.0.1:8080/ezScrum/api";
	private static String API_URL = "http://127.0.0.1:8080/ezScrum/api/projects";
	private static HttpServer mServer;
	private HttpClient mClient;
	private long mAccountId;
	private String mPlatformType;

	private CreateProject mCP;
//	private CreateAccount mCA;
	private Configuration mConfig;
	
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
		
//		mCA = new CreateAccount(1);
//		mCA.exe();
//		mAccountId = mCA.getAccountList().get(0).getId();
		mAccountId = 1; // get admin
		mPlatformType = "windows";
		
		TokenObject token = new TokenObject(mAccountId, mPlatformType);
		token.save();
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
		mConfig = null;
	}
	
	@Test
	public void testGet() throws ClientProtocolException, IOException,
			JSONException {
		// create project
		mCP = new CreateProject(1);
		mCP.exeCreate();
		
		// get data
		ProjectObject project = mCP.getAllProjects().get(0);
		HttpGet httpGet = new HttpGet(API_URL + "/" + project.getId());
		setHeaders(httpGet, mAccountId, mPlatformType);
		HttpResponse httpResponse = mClient.execute(httpGet);
		String response = EntityUtils.toString(httpResponse.getEntity(),
				"utf-8");
		
		// assert data
		JSONObject projectJson = new JSONObject(response);
		assertEquals(project.getId(), projectJson.getLong("id"));
		assertEquals(project.getName(), projectJson.getString("name"));
		assertEquals(project.getDisplayName(), projectJson.getString("display_name"));
		assertEquals(project.getComment(), projectJson.getString("comment"));
		assertEquals(project.getManager(), projectJson.getString("product_owner"));
		assertEquals(project.getAttachFileSize(), projectJson.getLong("attach_max_size"));
	}
	
	@Test
	public void testGetList() throws ClientProtocolException, IOException,
			JSONException {
		// create project
		mCP = new CreateProject(3);
		mCP.exeCreate();
		
		// get data
		ArrayList<ProjectObject> projects = mCP.getAllProjects();
		HttpGet httpGet = new HttpGet(API_URL + "/");
		setHeaders(httpGet, mAccountId, mPlatformType);
		HttpResponse httpResponse = mClient.execute(httpGet);
		String response = EntityUtils.toString(httpResponse.getEntity(),
				"utf-8");
		
		// assert data
		JSONObject projectsJson = new JSONObject(response);
		for (int i = 0; i < 3; i++) {
			ProjectObject project = projects.get(i);
			JSONObject projectJson = projectsJson.getJSONArray("projects")
					.getJSONObject(i);
			assertEquals(project.getId(), projectJson.getLong("id"));
			assertEquals(project.getName(), projectJson.getString("name"));
			assertEquals(project.getDisplayName(), projectJson.getString("display_name"));
			assertEquals(project.getComment(), projectJson.getString("comment"));
			assertEquals(project.getManager(), projectJson.getString("product_owner"));
			assertEquals(project.getAttachFileSize(), projectJson.getLong("attach_max_size"));
		}
	}
	
	@Test
	public void testPost() throws Exception {
		JSONObject projectJson = new JSONObject();
		projectJson.put("name", "TEST_NAME")
				.put("display_name", "TEST_DISPLAYNAME")
				.put("comment", "TEST_COMMENT")
				.put("product_owner", "TEST_PO")
				.put("attach_max_size", 2);
		
		BasicHttpEntity entity = new BasicHttpEntity();
		entity.setContent(new ByteArrayInputStream(projectJson.toString()
				.getBytes()));
		entity.setContentEncoding("utf-8");
		HttpPost httpPost = new HttpPost(API_URL);
		httpPost.setEntity(entity);
		setHeaders(httpPost, mAccountId, mPlatformType);
		HttpResponse httpResponse = mClient.execute(httpPost);
		String result = EntityUtils.toString(httpResponse.getEntity());
		JSONObject response = new JSONObject(result);
		
		// check one project in database
		ArrayList<ProjectObject> projects = ProjectObject.getAllProjects();
		assertEquals(1, projects.size());
		// assert response JSON string
		assertEquals(200, httpResponse.getStatusLine().getStatusCode());
		assertEquals("ok", response.getString("msg"));
	}
	
	@Test
	public void testPut() throws Exception {
		// create project
		mCP = new CreateProject(1);
		mCP.exeCreate();
		ProjectObject project = mCP.getAllProjects().get(0);
		
		JSONObject projectJson = new JSONObject();
		projectJson
				.put("id", project.getId())
				.put("name", project.getName())
				.put("display_name", "TEST_DISPLAYNAME")
				.put("comment", "TEST_COMMENT")
				.put("product_owner", "TEST_PO")
				.put("attach_max_size", 2);
		
		BasicHttpEntity entity = new BasicHttpEntity();
		entity.setContent(new ByteArrayInputStream(projectJson.toString()
				.getBytes()));
		entity.setContentEncoding("utf-8");
		HttpPut httpPut = new HttpPut(API_URL + "/" + project.getId());
		httpPut.setEntity(entity);
		setHeaders(httpPut, mAccountId, mPlatformType);
		HttpResponse httpResponse = mClient.execute(httpPut);
		String result = EntityUtils.toString(httpResponse.getEntity(),
				"utf-8");
		
		// assert response msg
		JSONObject response = new JSONObject(result);
		assertEquals(200, httpResponse.getStatusLine().getStatusCode());
		assertEquals("ok", response.getString("msg"));
	}
	
	@Test
	public void testDelete() throws ClientProtocolException, IOException {
		mCP = new CreateProject(1);
		mCP.exeCreateForDb();
		
		ArrayList<ProjectObject> projects = ProjectObject.getAllProjects();
		assertEquals(1, projects.size());
		long projectId = mCP.getAllProjects().get(0).getId();
		
		HttpDelete httpDelete = new HttpDelete(API_URL + "/" + projectId);
		HttpResponse httpResponse = mClient.execute(httpDelete);
		
		assertEquals(200, httpResponse.getStatusLine().getStatusCode());
		
		projects = ProjectObject.getAllProjects();
		assertEquals(0, projects.size());
	}
}
