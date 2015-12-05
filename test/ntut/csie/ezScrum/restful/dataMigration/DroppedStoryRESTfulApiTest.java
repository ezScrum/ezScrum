package ntut.csie.ezScrum.restful.dataMigration;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.HttpServer;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;

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
	}

	@Test
	public void testCreateTagInStory() {
		// TODO
	}

	@Test
	public void testCreateHistoryInStory() {
		// TODO
	}

	@Test
	public void testCreateAttachfile() {
		// TODO
	}

	@Test
	public void testCreateTaskInDroppedStory() {
		// TODO
	}

	@Test
	public void testCreateHistoryInTask() {
		// TODO
	}

	@Test
	public void testCreateAttachfileInTask() {
		// TODO
	}
}
