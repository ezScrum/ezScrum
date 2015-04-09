package ntut.csie.ezScrum.restful.mobilel.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.restful.mobile.service.SprintPlanWebService;
import ntut.csie.ezScrum.restful.mobile.service.StoryWebService;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateTask;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.jcis.resource.core.IProject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SprintPlanWebServiceTest {
	private int mProjectCount = 1;
	private int mReleaseCount = 1;
	private int mSprintCount = 3;
	private int mStoryCount = 3;
	private int mTaskCount = 3;
	private int mStoryEstimation = 2;
	private CreateProject mCP;
	private CreateRelease mCR;
	private CreateSprint mCS;
	private CreateTask mCT;
	private AddStoryToSprint mASTS;
	private IProject mIProject;
	private ProjectObject mProject;
	private IReleasePlanDesc mIReleasePlanDesc;
	private SprintPlanHelper mSprintPlanHelper;
	private SprintBacklogHelper mSprintBacklogHelper;
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

		mIReleasePlanDesc = mCR.getReleaseList().get(0);
		mProject = mCP.getAllProjects().get(0);
		mIProject = mCP.getProjectList().get(0);
		mSprintPlanHelper = new SprintPlanHelper(mIProject);
		mSprintBacklogHelper = new SprintBacklogHelper(mProject);
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
		mCT = null;
		mASTS = null;
		mIProject = null;
		mIReleasePlanDesc = null;
		mSprintPlanHelper = null;
		mSprintBacklogHelper = null;
		mConfig = null;
	}

	@Test
	public void testgetAllSprint() throws Exception {
		AccountObject account = new AccountObject("TEST_ACCOUNT");
		account.setPassword("TEST_ACCOUNT").setEmail("ezscrum@gmail.com").setEnable(true).setNickName("FUCKING_NICKNAME");
		account.save();
		
		String projectID = mIProject.getName();

		// 沒有Sprint的時候
		SprintPlanWebService mSprintPlanWebService = new SprintPlanWebService(account, projectID);
		assertEquals(mSprintPlanWebService.getAllSprint(), "[]");

		// 有Sprint的時候
		// create sprint
		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();	    // 新增 Sprint

		mSprintPlanWebService = new SprintPlanWebService(account, projectID);

		List<SprintObject> sprintlist = mSprintPlanHelper.getAllSprint();
		JSONArray sprintJSONArray = new JSONArray(mSprintPlanWebService.getAllSprint()); // 從WebService取得Json

		for (int i = 0; i < mSprintCount; i++) {
			JSONObject sprintJSONObject = (JSONObject) sprintJSONArray.get(i);
			assertEquals(sprintlist.get(i).id, sprintJSONObject.get("id"));
			assertEquals(sprintlist.get(i).sprintGoal, sprintJSONObject.get("sprintGoal"));
			assertEquals(sprintlist.get(i).startDate, sprintJSONObject.get("startDate"));
			assertEquals(sprintlist.get(i).demoDate, sprintJSONObject.get("demoDate"));
			assertEquals(sprintlist.get(i).interval, sprintJSONObject.get("interval"));
			assertEquals(sprintlist.get(i).focusFactor, sprintJSONObject.get("focusFactor"));
			assertEquals(sprintlist.get(i).members, sprintJSONObject.get("members"));
			assertEquals(sprintlist.get(i).hoursCanCommit, sprintJSONObject.get("hoursCanCommit"));
			assertEquals(sprintlist.get(i).demoPlace, sprintJSONObject.get("demoPlace"));
		}

	}

	@SuppressWarnings("deprecation")
	@Test
    public void testgetSprintWithAllItem() throws Exception {
		// User Object
		AccountObject account = new AccountObject("TEST_ACCOUNT");
		account.setPassword("TEST_ACCOUNT").setEmail("ezscrum@gmail.com").setEnable(true).setNickName("FUCKING_NICKNAME");
		account.save();

		String projectID = mIProject.getName();

		// create sprint
		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();	    // 新增 Sprint

		mASTS = new AddStoryToSprint(mStoryCount, mStoryEstimation, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST); // 新增 Story
		mASTS.exe();

		mCT = new CreateTask(mTaskCount, mCP);
		mCT.exe(); 

		StoryWebService mStoryWebService = new StoryWebService(account, projectID);

		ArrayList<StoryObject> storyList = mSprintBacklogHelper.getExistingStories();

		for (int i = 0; i < storyList.size(); i++) {
			JSONArray taskJSONArray = new JSONArray(mStoryWebService.getTasksInStory(storyList.get(i).getId())); // 從WebService取得Json
			ArrayList<TaskObject> tasksList = mCT.getTaskList();
			
			for (int j = 0; j < taskJSONArray.length(); j++) {
				JSONObject storyJSONObject = (JSONObject) taskJSONArray.get(j);
				assertEquals(String.valueOf(tasksList.get(j).getId()), storyJSONObject.get("id"));
				assertEquals(tasksList.get(j).getEstimate(), storyJSONObject.get("estimation"));
				assertEquals(tasksList.get(j).getStatus(), storyJSONObject.get("status"));
				assertEquals(tasksList.get(j).getPartners(), storyJSONObject.get("partners"));
				assertEquals(tasksList.get(j).getRemains(), storyJSONObject.get("remains"));
				assertEquals(tasksList.get(j).getActual(), storyJSONObject.get("actual"));
				assertEquals(tasksList.get(j).getNotes(), storyJSONObject.get("notes"));
			}
		}
	}
}
