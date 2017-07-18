package ntut.csie.ezScrum.web.action;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import servletunit.struts.MockStrutsTestCase;

public class NotifyNotificationLogoutTest extends MockStrutsTestCase{
private Configuration mConfig;
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();	
			
		super.setUp();
		
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/notifyLogout");
		// ============= release ==============
		ini = null;
	}
	
	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();
		
		// ============= release ==============
		ini = null;
		mConfig = null;
	}
	
	public void testexecute() throws Exception {
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ==============================
		actionPerform();
		verifyNoActionErrors();
		String actualResponseText = response.getWriterBuffer().toString();
		assertNotSame("Subscription",actualResponseText);
		assertNotSame("No-Subscription",actualResponseText);
	}
}
