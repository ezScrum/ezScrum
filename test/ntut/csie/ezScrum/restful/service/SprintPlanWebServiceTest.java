package ntut.csie.ezScrum.restful.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.restful.mobile.service.SprintPlanWebService;
import ntut.csie.ezScrum.restful.mobile.service.StoryWebService;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateTask;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.jcis.resource.core.IProject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

public class SprintPlanWebServiceTest extends TestCase {
	private int ProjectCount = 1;
	private int ReleaseCount = 1;
	private int SprintCount = 3;
	private int StoryCount = 3;
	private int TaskCount = 3;
	private int StoryEstimation = 2;
	private CreateProject CP;
	private CreateRelease CR;
	private CreateSprint CS;
	private CreateTask CT;
	private IProject project;
	private IReleasePlanDesc Realease;
	private SprintPlanHelper SPhelper;
	private SprintBacklogHelper SPBhelper;
	private Configuration configuration;
	private AddStoryToSprint ASS;

	public SprintPlanWebServiceTest(String testMethod) {
		super(testMethod);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		CP = new CreateProject(ProjectCount);
		CP.exeCreate(); // 新增一測試專案

		CR = new CreateRelease(ReleaseCount, CP);
		CR.exe();

		Realease = CR.getReleaseList().get(0);
		project = CP.getProjectList().get(0);
		SPhelper = new SprintPlanHelper(project);
		SPBhelper = new SprintBacklogHelper(project, configuration.getUserSession());

		ini = null;
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL

		CopyProject copyProject = new CopyProject(CP);
		copyProject.exeDelete_Project();					// 刪除測試檔案
		
		configuration.setTestMode(false);
		configuration.save();

		copyProject = null;
		CP = null;
		CR = null;
		ini = null;
		configuration = null;
	}

	public void testgetAllSprint() throws Exception {
		AccountObject account = new AccountObject("TEST_ACCOUNT");
		account.setPassword("TEST_ACCOUNT").setEmail("ezscrum@gmail.com").setEnable(true).setNickName("FUCKING_NICKNAME");
		account.save();
		
		String projectID = project.getName();

		// 沒有Sprint的時候
		SprintPlanWebService mSprintPlanWebService = new SprintPlanWebService(account, projectID);
		assertEquals(mSprintPlanWebService.getAllSprint(), "[]");

		// 有Sprint的時候
		// create sprint
		CS = new CreateSprint(SprintCount, CP);
		CS.exe();	    // 新增 Sprint

		mSprintPlanWebService = new SprintPlanWebService(account, projectID);

		List<SprintObject> sprintlist = SPhelper.getAllSprint();
		JSONArray sprintJSONArray = new JSONArray(mSprintPlanWebService.getAllSprint()); // 從WebService取得Json

		for (int i = 0; i < SprintCount; i++) {
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
    public void testgetSprintWithAllItem() throws Exception {
		// User Object
		AccountObject account = new AccountObject("TEST_ACCOUNT");
		account.setPassword("TEST_ACCOUNT").setEmail("ezscrum@gmail.com").setEnable(true).setNickName("FUCKING_NICKNAME");
		account.save();

		String projectID = project.getName();

		// create sprint
		CS = new CreateSprint(SprintCount, CP);
		CS.exe();	    // 新增 Sprint

		ASS = new AddStoryToSprint(StoryCount, StoryEstimation, CS, CP, CreateProductBacklog.TYPE_ESTIMATION); // 新增 Story
		ASS.exe();

		CT = new CreateTask(TaskCount, CP);
		CT.exe(); 

		StoryWebService mStoryWebService = new StoryWebService(account, projectID);

		List<IStory> storyList = SPBhelper.getExistingStories(Realease.getID());

		for (int i = 0; i < storyList.size(); i++) {
			JSONArray taskJSONArray = new JSONArray(mStoryWebService.getTaskInStory(String.valueOf(storyList.get(i).getStoryId()))); // 從WebService取得Json
			ArrayList<TaskObject> tasksList = CT.getTaskList();
			
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
