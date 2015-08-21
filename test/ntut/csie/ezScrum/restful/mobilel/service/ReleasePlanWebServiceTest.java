package ntut.csie.ezScrum.restful.mobilel.service;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ReleaseObject;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.restful.mobile.service.ReleasePlanWebService;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReleasePlanWebServiceTest {
	private int mProjectCount = 1;
	private int mReleaseCount = 3;
	private int mSprintCount = 3;
	private CreateProject mCP;
	private CreateRelease mCR;
	private ProjectObject mProject;
	private ReleasePlanHelper mReleasePlanHelper;
	private ReleasePlanWebService mReleasePlanWebService;
	private Configuration mConfig;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增一個 Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		mCR = new CreateRelease(mReleaseCount, mCP);
		mCR.exe();
		
		mProject = mCP.getAllProjects().get(0);
		mReleasePlanHelper = new ReleasePlanHelper(mProject);
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
		mProject = null;
		mReleasePlanHelper = null;
		mReleasePlanWebService = null;
		mConfig = null;
	}

	@Test
	public void testGetAllReleasePlan() throws LogonException, JSONException {
		String username = "admin";
		String userpwd = "admin";
		String projectID = mProject.getName();
		mReleasePlanWebService = new ReleasePlanWebService(username, userpwd, projectID);

		// create sprint
		CreateSprint CS = new CreateSprint(mSprintCount, mCP);
		CS.exe();

		// 從ReleasePlanHelper拿出release做assert
		List<ReleaseObject> releasePlanDescs = mReleasePlanHelper.loadReleasePlansList();
		JSONArray releasesJSONArray = new JSONArray(mReleasePlanWebService.getAllReleasePlan());
		
		for (int i = 0; i < mReleaseCount; i++) {
			JSONObject releaseJSONObject = (JSONObject) releasesJSONArray.get(i);
			assertEquals(releasePlanDescs.get(i).getID(), releaseJSONObject.get("ID"));
			assertEquals(releasePlanDescs.get(i).getName(), releaseJSONObject.get("Name"));
			assertEquals(releasePlanDescs.get(i).getDescription(), releaseJSONObject.get("Description"));
			JSONArray sprintsJSONArray = new JSONArray(releaseJSONObject.get("SprintList").toString());
			ArrayList<SprintObject> sprints = releasePlanDescs.get(i).getSprints();
			// assert ReleasePlan中的SprintPlan
			for(int j = 0; j < sprintsJSONArray.length(); j++) {
				JSONObject sprintJSONObject = (JSONObject) sprintsJSONArray.get(j);
				assertEquals(sprints.get(j).getId(), sprintJSONObject.getLong("mId"));
				assertEquals(sprints.get(j).getSprintGoal(), sprintJSONObject.get("mSprintGoal"));
				assertEquals(sprints.get(j).getInterval(), sprintJSONObject.get("mInterval"));
				assertEquals(sprints.get(j).getMembersAmount(), sprintJSONObject.get("mMembersAmount"));
				assertEquals(sprints.get(j).getFocusFactor(), sprintJSONObject.get("mFocusFactor"));
				assertEquals(sprints.get(j).getHoursCanCommit(), sprintJSONObject.get("mHoursCanCommit"));
				assertEquals(sprints.get(j).getDemoPlace(), sprintJSONObject.get("mDemoPlace"));
				assertEquals(sprints.get(j).getDailyInfo(), sprintJSONObject.get("mDailyInfo"));
			}
		}
	}
	
	@Test
	public void testgetReleasePlan() throws LogonException, JSONException, SQLException {
		String username = "admin";
		String userpwd = "admin";
		String projectID = mProject.getName();
		mReleasePlanWebService = new ReleasePlanWebService(username, userpwd, projectID);

		// create sprint
		CreateSprint CS = new CreateSprint(mSprintCount, mCP);
		CS.exe();

		// 從ReleasePlanHelper拿出release做assert
		List<ReleaseObject> releaselist = mReleasePlanHelper.loadReleasePlansList();
		
		for(int i = 0; i < mReleaseCount ; i++){
			JSONObject releaseJSONObject = new JSONObject(mReleasePlanWebService.getReleasePlan(releaselist.get(i).getID()));
			JSONObject releasePlanDescJSONObject = new JSONObject(releaseJSONObject.get("releasePlanDesc").toString());
			assertEquals(releaselist.get(i).getID(), releasePlanDescJSONObject.get("id"));
			assertEquals(releaselist.get(i).getName(), releasePlanDescJSONObject.get("name"));
			assertEquals(releaselist.get(i).getStartDate(), releasePlanDescJSONObject.get("startDate"));
			assertEquals(releaselist.get(i).getEndDate(), releasePlanDescJSONObject.get("endDate"));
			assertEquals(releaselist.get(i).getDescription(), releasePlanDescJSONObject.get("description"));
		}
	}
}
