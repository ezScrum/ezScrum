package ntut.csie.ezScrum.restful.dataMigration;

import static org.junit.Assert.assertEquals;

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

import ntut.csie.ezScrum.dao.AccountDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.AccountJSONEnum;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class AccountRESTfulApiTest extends JerseyTest {
	private Configuration mConfig;
	private CreateProject mCP;
	private ResourceConfig mResourceConfig;
	private Client mClient;
	private HttpServer mHttpServer;
	private static String BASE_URL = "http://localhost:8080/ezScrum/resource/";
	private URI mBaseUri = URI.create(BASE_URL);

	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(AccountRESTfulApi.class);
		return mResourceConfig;
	}

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
	public void testCreateAccount() throws JSONException {
		// Test Data
		String userName = "TEST_USER_NAME";
		String userNickName = "TEST_USER_NICK_NAME";
		String userPassword = "TEST_USER_PASSWORD";
		String userEmail = "TEST_USER_EMAIL";
		boolean enbale = true;

		JSONObject accountJSON = new JSONObject();
		accountJSON.put(AccountJSONEnum.USERNAME, userName);
		accountJSON.put(AccountJSONEnum.NICK_NAME, userNickName);
		accountJSON.put(AccountJSONEnum.PASSWORD, userPassword);
		accountJSON.put(AccountJSONEnum.EMAIL, userEmail);
		accountJSON.put(AccountJSONEnum.ENABLE, enbale);

		// Call '/accounts' API
		Response response = mClient.target(BASE_URL)
		        .path("accounts")
		        .request()
		        .post(Entity.text(accountJSON.toString()));

		AccountObject account = AccountObject.get(userName);

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(userName, account.getUsername());
		assertEquals(userNickName, account.getNickName());
		assertEquals(AccountDAO.getMd5(userPassword), account.getPassword());
		assertEquals(userEmail, account.getEmail());
		assertEquals(true, account.getEnable());
	}
	
	@Test
	public void testCreateAccount_WithMD5Password() throws JSONException {
		// Test Data
		String userName = "TEST_USER_NAME";
		String userNickName = "TEST_USER_NICK_NAME";
		String userPassword = "93189e2c4c7b1a2c7b16a24d5daa98a9";
		String userEmail = "TEST_USER_EMAIL";
		boolean enbale = true;

		JSONObject accountJSON = new JSONObject();
		accountJSON.put(AccountJSONEnum.USERNAME, userName);
		accountJSON.put(AccountJSONEnum.NICK_NAME, userNickName);
		accountJSON.put(AccountJSONEnum.PASSWORD, userPassword);
		accountJSON.put(AccountJSONEnum.EMAIL, userEmail);
		accountJSON.put(AccountJSONEnum.ENABLE, enbale);

		// Call '/accounts' API
		Response response = mClient.target(BASE_URL)
		        .path("accounts")
		        .request()
		        .post(Entity.text(accountJSON.toString()));

		AccountObject account = AccountObject.get(userName);

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(userName, account.getUsername());
		assertEquals(userNickName, account.getNickName());
		assertEquals(userPassword, account.getPassword());
		assertEquals(userEmail, account.getEmail());
		assertEquals(true, account.getEnable());
	}
	
	@Test
	public void testCreateExistingAccount() throws JSONException {
		// Test Data
		String userName = "TEST_USER_NAME";
		String userNickName = "TEST_USER_NICK_NAME";
		String userPassword = "93189e2c4c7b1a2c7b16a24d5daa98a9";
		String userEmail = "TEST_USER_EMAIL";
		boolean enbale = true;

		JSONObject accountJSON = new JSONObject();
		accountJSON.put(AccountJSONEnum.USERNAME, userName);
		accountJSON.put(AccountJSONEnum.NICK_NAME, userNickName);
		accountJSON.put(AccountJSONEnum.PASSWORD, userPassword);
		accountJSON.put(AccountJSONEnum.EMAIL, userEmail);
		accountJSON.put(AccountJSONEnum.ENABLE, enbale);

		// Call '/accounts' API
		Response response = mClient.target(BASE_URL)
		        .path("accounts")
		        .request()
		        .post(Entity.text(accountJSON.toString()));

		AccountObject account = AccountObject.get(userName);

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(userName, account.getUsername());
		assertEquals(userNickName, account.getNickName());
		assertEquals(userPassword, account.getPassword());
		assertEquals(userEmail, account.getEmail());
		assertEquals(true, account.getEnable());
		
		// Call '/accounts' API again
		response = mClient.target(BASE_URL)
		        .path("accounts")
		        .request()
		        .post(Entity.text(accountJSON.toString()));
		
		// Assert
		assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
	}
}
