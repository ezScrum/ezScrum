package ntut.csie.ezScrum.restful.mobilel.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.restful.mobile.service.SprintPlanWebService;
import ntut.csie.ezScrum.restful.mobile.util.SprintUtil;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.databasEnum.StoryEnum;
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
	public void testGetAllSprints() throws Exception {
		AccountObject account = new AccountObject("TEST_ACCOUNT");
		account.setPassword("TEST_ACCOUNT").setEmail("ezscrum@gmail.com").setEnable(true).setNickName("FUCKING_NICKNAME");
		account.save();
		
		String projectName = mProject.getName();

		// 沒有Sprint的時候
		SprintPlanWebService sprintPlanWebService = new SprintPlanWebService(account, projectName);
		assertEquals(sprintPlanWebService.getAllSprints(), "[]");

		// 有Sprint的時候
		// create sprint
		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();

		sprintPlanWebService = new SprintPlanWebService(account, projectName);

		ArrayList<SprintObject> sprints = mSprintPlanHelper.getAllSprints();
		JSONArray sprintJSONArray = new JSONArray(sprintPlanWebService.getAllSprints()); // 從WebService取得Json

		for (int i = 0; i < mSprintCount; i++) {
			JSONObject sprintJSONObject = (JSONObject) sprintJSONArray.get(i);
			assertEquals(sprints.get(i).getId(), sprintJSONObject.get(SprintUtil.TAG_ID));
			assertEquals(sprints.get(i).getGoal(), sprintJSONObject.get(SprintUtil.TAG_SPRINT_GOAL));
			assertEquals(sprints.get(i).getStartDateString(), sprintJSONObject.get(SprintUtil.TAG_START_DATE));
			assertEquals(sprints.get(i).getDemoDateString(), sprintJSONObject.get(SprintUtil.TAG_DEMO_DATE));
			assertEquals(sprints.get(i).getInterval(), sprintJSONObject.get(SprintUtil.TAG_INTERVAL));
			assertEquals(sprints.get(i).getFocusFactor(), sprintJSONObject.get(SprintUtil.TAG_FOCUS_FACTOR));
			assertEquals(sprints.get(i).getMembers(), sprintJSONObject.get(SprintUtil.TAG_MEMBERS));
			assertEquals(sprints.get(i).getAvailableHours(), sprintJSONObject.get(SprintUtil.TAG_HOURS_CAN_COMMIT));
			assertEquals(sprints.get(i).getDemoPlace(), sprintJSONObject.get(SprintUtil.TAG_DEMO_PLACE));
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
		SprintPlanWebService sprintPlanWebService = new SprintPlanWebService(account, mProject.getName());
		assertEquals(sprintPlanWebService.getCurrentSprintJsonString(), "");
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
		SprintPlanWebService sprintPlanWebService = new SprintPlanWebService(account, mProject.getName());
		// getCurrentSprint JSON String
		String currentSprintJSONString = sprintPlanWebService.getCurrentSprintJsonString();
		// create CurrentSprint JSON Object
		JSONObject sprintJSONObject = new JSONObject(currentSprintJSONString);
		
		// assert
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getId(), sprintJSONObject.get(SprintUtil.TAG_ID));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getGoal(), sprintJSONObject.get(SprintUtil.TAG_SPRINT_GOAL));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getStartDateString(), sprintJSONObject.get(SprintUtil.TAG_START_DATE));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getDemoDateString(), sprintJSONObject.get(SprintUtil.TAG_DEMO_DATE));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getInterval(), sprintJSONObject.get(SprintUtil.TAG_INTERVAL));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getFocusFactor(), sprintJSONObject.get(SprintUtil.TAG_FOCUS_FACTOR));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getMembers(), sprintJSONObject.get(SprintUtil.TAG_MEMBERS));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getAvailableHours(), sprintJSONObject.get(SprintUtil.TAG_HOURS_CAN_COMMIT));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getDemoPlace(), sprintJSONObject.get(SprintUtil.TAG_DEMO_PLACE));
	}
	
	@Test
	public void testGetSprintWithStories() throws Exception {
		// Add account
		AccountObject account = new AccountObject("TEST_ACCOUNT");
		account.setPassword("TEST_ACCOUNT")
		       .setEmail("ezscrum@gmail.com")
		       .setEnable(true)
		       .setNickName("TEST_NICK_NAME")
		       .save();
		// create sprint
		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();
		// create story
		mASTS = new AddStoryToSprint(mStoryCount, mStoryEstimation, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mASTS.exe();
		
		// create SprintPlanWebService
		SprintPlanWebService sprintPlanWebService = new SprintPlanWebService(account, mProject.getName());
		// getSprintWithStories
		String sprintJSONString = sprintPlanWebService.getSprintWithStories(mCS.getSprintsId().get(0));
		// create sprint JSON Object
		JSONObject sprintJSONObject = new JSONObject(sprintJSONString);
		// get stories JSONArray
		JSONArray storiesJSONArray = sprintJSONObject.getJSONArray(SprintUtil.TAG_STORIES);
		
		// assert
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getId(), sprintJSONObject.get(SprintUtil.TAG_ID));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getGoal(), sprintJSONObject.get(SprintUtil.TAG_SPRINT_GOAL));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getStartDateString(), sprintJSONObject.get(SprintUtil.TAG_START_DATE));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getDemoDateString(), sprintJSONObject.get(SprintUtil.TAG_DEMO_DATE));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getInterval(), sprintJSONObject.get(SprintUtil.TAG_INTERVAL));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getFocusFactor(), sprintJSONObject.get(SprintUtil.TAG_FOCUS_FACTOR));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getMembers(), sprintJSONObject.get(SprintUtil.TAG_MEMBERS));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getAvailableHours(), sprintJSONObject.get(SprintUtil.TAG_HOURS_CAN_COMMIT));
		assertEquals(SprintObject.get(mCS.getSprintsId().get(0)).getDemoPlace(), sprintJSONObject.get(SprintUtil.TAG_DEMO_PLACE));
		
		for (int i = 0; i < storiesJSONArray.length(); i++) {
			StoryObject story = mASTS.getStories().get(i);
			JSONObject storyJSONObject = storiesJSONArray.getJSONObject(i);
			assertEquals(story.getId(), storyJSONObject.get(StoryEnum.ID));
			assertEquals(story.getName(), storyJSONObject.get(StoryEnum.NAME));
			assertEquals(story.getSprintId(), storyJSONObject.get(StoryEnum.SPRINT_ID));
			assertEquals(story.getEstimate(), storyJSONObject.get(StoryEnum.ESTIMATE));
			assertEquals(story.getImportance(), storyJSONObject.get(StoryEnum.IMPORTANCE));
			assertEquals(story.getValue(), storyJSONObject.get(StoryEnum.VALUE));
			assertEquals(story.getHowToDemo(), storyJSONObject.get(StoryEnum.HOW_TO_DEMO));
			assertEquals(story.getNotes(), storyJSONObject.get(StoryEnum.NOTES));
			assertEquals(story.getStatus(), storyJSONObject.get(StoryEnum.STATUS));
		}
		
	}
}
