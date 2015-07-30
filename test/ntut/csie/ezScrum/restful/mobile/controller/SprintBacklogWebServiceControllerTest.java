package ntut.csie.ezScrum.restful.mobile.controller;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
		
		mASTS = new AddStoryToSprint(mStoryCount, mEstimate, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
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
	public void testGetTaskInformation() {
		
	}
}
