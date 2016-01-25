package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import servletunit.struts.MockStrutsTestCase;

public class GetSprintPlanComboInfoActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private ProjectObject mProject;
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
		mCP.exeCreateForDb();

		// create sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		mProject = mCP.getAllProjects().get(0);
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

		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		ini = null;
		mCP = null;
		mCS = null;
		mConfig = null;
		mProject = null;
	}

	public void testGetSprintPlanComboInfo() throws JSONException {
		ArrayList<Long> idList = mCS.getSprintsId();

		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("SprintID", String.valueOf(idList.get(0)));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();

		String actualResponseText = response.getWriterBuffer().toString();
		JSONObject actualResponse = new JSONObject(actualResponseText);
		JSONArray sprints = actualResponse.getJSONArray("Sprints");
		
		// order by start date (decreasing)
		JSONObject sprint1 = sprints.getJSONObject(0);
		assertEquals(String.valueOf(idList.get(1)), sprint1.getString("Id"));
		assertEquals("Sprint #" + idList.get(1), sprint1.getString("Info"));
		assertEquals("true", sprint1.getString("Edit"));
		
		JSONObject sprint2 = sprints.getJSONObject(1);
		assertEquals(String.valueOf(idList.get(0)), sprint2.getString("Id"));
		assertEquals("Sprint #" + idList.get(0), sprint2.getString("Info"));
		assertEquals("true", sprint2.getString("Edit"));

		JSONObject currentSprint = actualResponse
				.getJSONObject("CurrentSprint");
		assertEquals(String.valueOf(idList.get(0)),
				currentSprint.getString("Id"));
		assertEquals("Sprint #" + idList.get(0),
				currentSprint.getString("Info"));
		assertEquals("true", currentSprint.getString("Edit"));
	}
}
