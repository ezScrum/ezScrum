package ntut.csie.ezScrum.robust.aspectj.tool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import ntut.csie.ezScrum.restful.dataMigration.security.SecurityModule;

import ntut.csie.ezScrum.robust.aspectj.tool.AspectJSwitch;
import ntut.csie.ezScrum.robust.aspectj.tool.AspectJSwitchApi;

public class AspectJSwitchApiTest extends JerseyTest {
	private Configuration mConfig;
	private ResourceConfig mResourceConfig;
	private Client mClient;
	private HttpServer mHttpServer;
	private static String BASE_URL = "http://localhost:9527/ezScrum/aspectj/";
	private URI mBaseUri = URI.create(BASE_URL);

	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(AspectJSwitchApi.class);
		return mResourceConfig;
	}

	@Before
	public void setUp() {
		// Turn AspectJ Switch Off
		AspectJSwitch.getInstance().turnOff();
		
		// Set Test Mode
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// Start Server
		mHttpServer = JdkHttpServerFactory.createHttpServer(mBaseUri, mResourceConfig, true);

		// Create Client
		mClient = ClientBuilder.newClient();
	}

	@After
	public void tearDown() {
		// Turn AspectJ Switch Off
		AspectJSwitch.getInstance().turnOff();
		
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
		mHttpServer = null;
		mClient = null;
	}
	
	@Test
	public void turnAspectJSwitchOnByActionName() {
		String actionName = "ShowEditUnplanItemAction";
		assertFalse(AspectJSwitch.getInstance().isSwitchOn(actionName));
		// Call '/aspectj/aspectj' API
		Response response = mClient.target(BASE_URL)
		        .path("switch/on")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, SecurityModule.ADMIN_MD5_USERNAME)
		        .header(SecurityModule.PASSWORD_HEADER, SecurityModule.ADMIN_MD5_PASSWORD)
		        .post(Entity.text(actionName));

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertTrue(AspectJSwitch.getInstance().isSwitchOn(actionName));
	}
	
	@Test
	public void turnAspectJSwitchOnByActionName_WithInvalidUsernameAndInvalidPassword() {
		String actionName = "ShowEditUnplanItemAction";
		assertFalse(AspectJSwitch.getInstance().isSwitchOn(actionName));
		// Call '/aspectj/aspectj' API
		Response response = mClient.target(BASE_URL)
		        .path("switch/on")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, "InvalidUsername")
		        .header(SecurityModule.PASSWORD_HEADER, "InvalidPassword")
		        .post(Entity.text(actionName));

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertFalse(AspectJSwitch.getInstance().isSwitchOn(actionName));
	}

	@Test
	public void turnAspectJSwitchOnByActionName_WithNullActionName() {
		String actionName = null;
		assertFalse(AspectJSwitch.getInstance().isSwitchOn(actionName));
		// Call '/aspectj/aspectj' API
		Response response = mClient.target(BASE_URL)
		        .path("switch/on")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, SecurityModule.ADMIN_MD5_USERNAME)
		        .header(SecurityModule.PASSWORD_HEADER, SecurityModule.ADMIN_MD5_PASSWORD)
		        .post(Entity.text(actionName));

		// Assert
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		assertFalse(AspectJSwitch.getInstance().isSwitchOn(actionName));
	}
	
	@Test
	public void turnAspectJSwitchOnByActionName_WithEmptyActionName() {
		String actionName = "";
		assertFalse(AspectJSwitch.getInstance().isSwitchOn(actionName));
		// Call '/aspectj/aspectj' API
		Response response = mClient.target(BASE_URL)
		        .path("switch/on")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, SecurityModule.ADMIN_MD5_USERNAME)
		        .header(SecurityModule.PASSWORD_HEADER, SecurityModule.ADMIN_MD5_PASSWORD)
		        .post(Entity.text(actionName));

		// Assert
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		assertFalse(AspectJSwitch.getInstance().isSwitchOn(actionName));
	}
	
	@Test
	public void turnAspectJSwitchOff() {
		String actionName = "ShowEditUnplanItemAction";
		AspectJSwitch.getInstance().turnOnByActionName(actionName);
		assertTrue(AspectJSwitch.getInstance().isSwitchOn(actionName));
		
		// Call '/aspectj/aspectj' API
		Response response = mClient.target(BASE_URL)
		        .path("switch/off")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, SecurityModule.ADMIN_MD5_USERNAME)
		        .header(SecurityModule.PASSWORD_HEADER, SecurityModule.ADMIN_MD5_USERNAME)
		        .post(Entity.text(""));

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertFalse(AspectJSwitch.getInstance().isSwitchOn(actionName));
	}
	
	@Test
	public void turnAspectJSwitchOff_WithInvalidUsernameAndInvalidPassword() {
		// Call '/aspectj/aspectj' API
		Response response = mClient.target(BASE_URL)
		        .path("switch/off")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, "InvalidUsername")
		        .header(SecurityModule.PASSWORD_HEADER, "InvalidPassword")
		        .post(Entity.text(""));

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
}