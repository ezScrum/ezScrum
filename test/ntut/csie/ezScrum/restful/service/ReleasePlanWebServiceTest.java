package ntut.csie.ezScrum.restful.service;

import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.restful.mobile.service.ReleasePlanWebService;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.jcis.account.core.LogonException;
import ntut.csie.jcis.resource.core.IProject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ReleasePlanWebServiceTest extends TestCase {
	private CreateProject CP;
	private CreateRelease CR;
	private int ProjectCount = 1;
	private int ReleaseCount = 3;
	private int SprintCount = 3;
	private IProject project;
	private ReleasePlanHelper RPhelper;
	private ReleasePlanWebService rService;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();

	public ReleasePlanWebServiceTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(config);
		ini.exe();

		// 新增一個 Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();

		this.CR = new CreateRelease(this.ReleaseCount, this.CP);
		this.CR.exe();
		
		project = this.CP.getProjectList().get(0);
		RPhelper = new ReleasePlanHelper(project);

		super.setUp();

		// release
		ini = null;
	}

	protected void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(config);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(this.config.getTestDataPath());

		super.setUp();

		// release
		ini = null;
		this.CP = null;
		this.CR = null;
		this.config = null;
	}

	public void testgetAllReleasePlan() throws LogonException, JSONException {
		String username = "admin";
		String userpwd = "admin";
		String projectID = project.getName();
		rService = new ReleasePlanWebService(username, userpwd, projectID);

		// create sprint
		CreateSprint CS = new CreateSprint(SprintCount, CP);
		CS.exe();

		// 從ReleasePlanHelper拿出release做assert
		List<IReleasePlanDesc> releaselist = RPhelper.loadReleasePlansList();
		JSONArray releasesJSONArray = new JSONArray(rService.getAllReleasePlan());
		
		for (int i = 0; i < ReleaseCount; i++) {
			JSONObject releaseJSONObject = (JSONObject) releasesJSONArray.get(i);
			assertEquals(releaselist.get(i).getID(), releaseJSONObject.get("ID"));
			assertEquals(releaselist.get(i).getName(), releaseJSONObject.get("Name"));
			assertEquals(releaselist.get(i).getDescription(), releaseJSONObject.get("Description"));
			JSONArray sprintsJSONArray = new JSONArray(releaseJSONObject.get("SprintList").toString());
			List<ISprintPlanDesc> sprintlist = releaselist.get(i).getSprintDescList();
			// assert ReleasePlan中的SprintPlan
			for(int j = 0; j < sprintsJSONArray.length(); j++) {
				JSONObject sprintJSONObject = (JSONObject) sprintsJSONArray.get(j);
				assertEquals(sprintlist.get(j).getID(), sprintJSONObject.get("m_id").toString());
				assertEquals(sprintlist.get(j).getGoal(), sprintJSONObject.get("m_goal"));
				assertEquals(sprintlist.get(j).getInterval(), sprintJSONObject.get("m_interval").toString());
				assertEquals(sprintlist.get(j).getMemberNumber(), sprintJSONObject.get("m_memberNumber").toString());
				assertEquals(sprintlist.get(j).getFocusFactor(), sprintJSONObject.get("m_factor").toString());
				assertEquals(sprintlist.get(j).getAvailableDays(), sprintJSONObject.get("m_availableDays").toString());
				assertEquals(sprintlist.get(j).getDemoPlace(), sprintJSONObject.get("m_demoPlace"));
				assertEquals(sprintlist.get(j).getNotes(), sprintJSONObject.get("m_notes"));
				assertEquals((int)Double.parseDouble(sprintlist.get(j).getActualCost()), sprintJSONObject.get("m_actualCost"));
			}
		}
	}
	
	public void testgetReleasePlan() throws LogonException, JSONException {
		String username = "admin";
		String userpwd = "admin";
		String projectID = project.getName();
		rService = new ReleasePlanWebService(username, userpwd, projectID);

		// create sprint
		CreateSprint CS = new CreateSprint(SprintCount, CP);
		CS.exe();

		// 從ReleasePlanHelper拿出release做assert
		List<IReleasePlanDesc> releaselist = RPhelper.loadReleasePlansList();
		
		for(int i = 0; i < ReleaseCount ; i++){
			JSONObject releaseJSONObject = new JSONObject(rService.getReleasePlan(releaselist.get(i).getID()));
			JSONObject releasePlanDescJSONObject = new JSONObject(releaseJSONObject.get("releasePlanDesc").toString());
			assertEquals(releaselist.get(i).getID(), releasePlanDescJSONObject.get("id"));
			assertEquals(releaselist.get(i).getName(), releasePlanDescJSONObject.get("name"));
			assertEquals(releaselist.get(i).getStartDate(), releasePlanDescJSONObject.get("startDate"));
			assertEquals(releaselist.get(i).getEndDate(), releasePlanDescJSONObject.get("endDate"));
			assertEquals(releaselist.get(i).getDescription(), releasePlanDescJSONObject.get("description"));
			
			JSONArray sprintsJSONArray = new JSONArray(releaseJSONObject.get("sprintDescList").toString());
			for(int j = 0 ; j < sprintsJSONArray.length() ; j++){
				JSONObject sprintDescListJSONObject = (JSONObject) sprintsJSONArray.get(j);
				assertEquals(releaselist.get(i).getSprintDescList().get(j), sprintDescListJSONObject.get("sprintDescList"));
			}
		}
	}
}
