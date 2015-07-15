package ntut.csie.ezScrum.restful.mobilel.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.restful.mobile.service.SprintPlanWebService;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.databasEnum.SprintEnum;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SprintPlanWebServiceTest {
	private int mProjectCount = 1;
	private int mReleaseCount = 1;
	private int mSprintCount = 3;
	private int mStoryCount = 3;
	private int mStoryEstimation = 2;
	private CreateProject mCP;
	private CreateRelease mCR;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private ProjectObject mProject;
	private SprintPlanHelper mSprintPlanHelper;
	private Configuration mConfig;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		mConfig = new Configuration(new UserSession(AccountObject.get("admin")));
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate(); // 新增一測試專案

		mCR = new CreateRelease(mReleaseCount, mCP);
		mCR.exe();

		mProject = mCP.getAllProjects().get(0);
		mSprintPlanHelper = new SprintPlanHelper(mProject);
	}

	@After
	public void tearDown() throws Exception {
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
		mCR = null;
		mCS = null;
		mASTS = null;
		mSprintPlanHelper = null;
		mConfig = null;
	}

	@Test
	public void testGetAllSprint() throws Exception {
		AccountObject account = new AccountObject("TEST_ACCOUNT");
		account.setPassword("TEST_ACCOUNT").setEmail("ezscrum@gmail.com").setEnable(true).setNickName("FUCKING_NICKNAME");
		account.save();
		
		String projectName = mProject.getName();

		// 沒有Sprint的時候
		SprintPlanWebService mSprintPlanWebService = new SprintPlanWebService(account, projectName);
		assertEquals(mSprintPlanWebService.getAllSprints(), "[]");

		// 有Sprint的時候
		// create sprint
		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();

		mSprintPlanWebService = new SprintPlanWebService(account, projectName);

		ArrayList<SprintObject> sprints = mSprintPlanHelper.getAllSprints();
		JSONArray sprintJSONArray = new JSONArray(mSprintPlanWebService.getAllSprints()); // 從WebService取得Json

		for (int i = 0; i < mSprintCount; i++) {
			JSONObject sprintJSONObject = (JSONObject) sprintJSONArray.get(i);
			assertEquals(sprints.get(i).getId(), sprintJSONObject.get(SprintEnum.ID));
			assertEquals(sprints.get(i).getSprintGoal(), sprintJSONObject.get(SprintEnum.GOAL));
			assertEquals(sprints.get(i).getStartDate(), sprintJSONObject.get(SprintEnum.START_DATE));
			assertEquals(sprints.get(i).getDemoDate(), sprintJSONObject.get(SprintEnum.DEMO_DATE));
			assertEquals(sprints.get(i).getInterval(), sprintJSONObject.get(SprintEnum.INTERVAL));
			assertEquals(sprints.get(i).getFocusFactor(), sprintJSONObject.get(SprintEnum.FOCUS_FACTOR));
			assertEquals(sprints.get(i).getMembersNumber(), sprintJSONObject.get(SprintEnum.MEMBERS));
			assertEquals(sprints.get(i).getHoursCanCommit(), sprintJSONObject.get(SprintEnum.AVAILABLE_HOURS));
			assertEquals(sprints.get(i).getDemoPlace(), sprintJSONObject.get(SprintEnum.DEMO_PLACE));
		}

	}
	
	@Test
	public void testGetCurrentSprint_noSprint() throws LogonException, JSONException {
		AccountObject account = new AccountObject("TEST_ACCOUNT");
		account.setPassword("TEST_ACCOUNT")
		       .setEmail("ezscrum@gmail.com")
		       .setEnable(true)
		       .setNickName("TEST_NICK_NAME")
		       .save();
		
		// 沒有Sprint的時候
		SprintPlanWebService mSprintPlanWebService = new SprintPlanWebService(account, mProject.getName());
		assertEquals(mSprintPlanWebService.getCurrentSprint(), "");
	}
	
	@Test
	public void testGetCurrentSprint() throws LogonException, JSONException {
		AccountObject account = new AccountObject("TEST_ACCOUNT");
		account.setPassword("TEST_ACCOUNT")
		       .setEmail("ezscrum@gmail.com")
		       .setEnable(true)
		       .setNickName("TEST_NICK_NAME")
		       .save();
		
		// create sprint
		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();
		
		// create SprintPlanWebService
		SprintPlanWebService mSprintPlanWebService = new SprintPlanWebService(account, mProject.getName());
		// getCurrentSprint JSON String
		String currentSprintJSONString = mSprintPlanWebService.getCurrentSprint();
		// create CurrentSprint JSON Object
		JSONObject sprintJSONObject = new JSONObject(currentSprintJSONString);
		
		// assert
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getId(), sprintJSONObject.get(SprintEnum.ID));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getSprintGoal(), sprintJSONObject.get(SprintEnum.GOAL));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getStartDate(), sprintJSONObject.get(SprintEnum.START_DATE));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getDemoDate(), sprintJSONObject.get(SprintEnum.DEMO_DATE));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getInterval(), sprintJSONObject.get(SprintEnum.INTERVAL));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getFocusFactor(), sprintJSONObject.get(SprintEnum.FOCUS_FACTOR));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getMembersNumber(), sprintJSONObject.get(SprintEnum.MEMBERS));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getHoursCanCommit(), sprintJSONObject.get(SprintEnum.AVAILABLE_HOURS));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getDemoPlace(), sprintJSONObject.get(SprintEnum.DEMO_PLACE));
	}
}
