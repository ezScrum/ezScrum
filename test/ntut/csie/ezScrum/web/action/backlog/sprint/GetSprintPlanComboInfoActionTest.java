package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import servletunit.struts.MockStrutsTestCase;

public class GetSprintPlanComboInfoActionTest extends MockStrutsTestCase {

	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private IProject mIProject;
	private final String mActionPath = "/GetSprintsComboInfo";

	public GetSprintPlanComboInfoActionTest(String testName) {
		super(testName);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// create project
		mCP = new CreateProject(1);
		mCP.exeCreate();

		// create sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		mIProject = mCP.getProjectList().get(0);
		super.setUp();

		// ================ set action info ========================
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);

		ini = null;
	}

	protected void tearDown() throws Exception {
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		ini = null;
		projectManager = null;
		mCP = null;
		mCS = null;
		mConfig = null;
		mIProject = null;
	}

	public void testGetSprintPlanComboInfo() throws JSONException{
		ArrayList<Long> idList = mCS.getSprintsId();
		
		// ================ set request info ========================
		String projectName = mIProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("SprintID", String.valueOf(idList.get(0)));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();

		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject actualResponse = new JSONObject(actualResponseText);
		JSONArray sprints = actualResponse.getJSONArray("Sprints");
		for (int i = 0; i < 2; i++) {
			JSONObject sprint = sprints.getJSONObject(i);
			assertEquals(String.valueOf(idList.get(i)), sprint.getString("Id"));
			assertEquals("Sprint #" + idList.get(i), sprint.getString("Info"));
			assertEquals("true", sprint.getString("Edit"));
		}
		
		JSONObject currentSprint = actualResponse.getJSONObject("CurrentSprint");
		assertEquals(String.valueOf(idList.get(0)), currentSprint.getString("Id"));
		assertEquals("Sprint #" + idList.get(0), currentSprint.getString("Info"));
		assertEquals("true", currentSprint.getString("Edit"));
	}
}
