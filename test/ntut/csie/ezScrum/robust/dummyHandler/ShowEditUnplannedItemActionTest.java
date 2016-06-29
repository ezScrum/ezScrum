package ntut.csie.ezScrum.robust.dummyHandler;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.robust.aspectj.tool.AspectJSwitch;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplanItem;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import servletunit.struts.ExceptionDuringTestError;
import servletunit.struts.MockStrutsTestCase;

public class ShowEditUnplannedItemActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateUnplanItem mCUI;
	private Configuration mConfig;
	private String mActionPath = "/showEditUnplannedItem";
	private String mActionName = "ShowEditUnplannedItemAction";
	
	public ShowEditUnplannedItemActionTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		// Turn AspectJ Switch on
		AspectJSwitch.getInstance().turnOnByActionName(mActionName);
		
		// Turn test mode on
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// Initialize database
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// Create Project
		mCP = new CreateProject(1);
		mCP.exeCreateForDb();
		
		// Create Sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// Create Unplan
		mCUI = new CreateUnplanItem(1, mCP, mCS);
		mCUI.exe();

		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent")); // 設定讀取的
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		// Turn AspectJ Switch off
		AspectJSwitch.getInstance().turnOff();
		
		// Clean database
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// Turn test mode off
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mCUI = null;
		mConfig = null;
	}

	public void testShowEditUnplanItemAction_WithIOExceptionWhenGetWriter() {
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long unplanId = mCUI.getUnplansId().get(0);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?projectName=" + project.getName());

		// Execute ShowEditUnplanItemAction
		String exceptionMessage = "";
		try {
			actionPerform();
		} catch (ExceptionDuringTestError e) {
			exceptionMessage = e.getMessage();
		}
		assertEquals("An uncaught exception was thrown during actionExecute()", exceptionMessage);
	}
}
