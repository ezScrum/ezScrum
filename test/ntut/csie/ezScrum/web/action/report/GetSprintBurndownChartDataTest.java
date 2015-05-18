package ntut.csie.ezScrum.web.action.report;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.control.TaskBoard;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import servletunit.struts.MockStrutsTestCase;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedHashTreeMap;

public class GetSprintBurndownChartDataTest extends MockStrutsTestCase {
	
	private CreateProject mCP;
	private CreateSprint mCS;
	private ProjectObject mProject;
	private Gson mGson;
	private Configuration mConfig;

	public GetSprintBurndownChartDataTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增一測試專案
		mCP = new CreateProject(1);
		mCP.exeCreate();
		mProject = mCP.getAllProjects().get(0);

		// 新增1筆 Sprint Plan
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		mGson = new Gson();
		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));	// 設定讀取的struts-config檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/getSprintBurndownChartData");

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws Exception {
	 	// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mGson = null;
		mConfig = null;
		super.tearDown();
	}

	/**
	 * 測試 Story 的 burndown chart 在都沒有 done 的情況下的圖資料是否正確
	 */
	public void testGetSprintBurndownChartData_Story_1() throws Exception {
		final int STORY_COUNT = 2, STORY_EST = 5;
		// Sprint加入2個Story
		AddStoryToSprint ASTS = new AddStoryToSprint(STORY_COUNT, STORY_EST, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASTS.exe();

		// 拿出 sprint 的每一天日期放在 idealPointArray 當 expecte 天數
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(mProject, mCS.getSprintsId().get(0));
		SprintBacklogMapper SprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		TaskBoard taskBoard = new TaskBoard(sprintBacklogLogic, SprintBacklogMapper);
		LinkedHashMap<Date, Double> ideal = taskBoard.getStoryIdealPointMap();
		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Object[] idealPointArray = ideal.keySet().toArray();

		// ================ set request info ========================
		request.setHeader("Referer", "?PID=" + mProject.getName());
		addRequestParameter("SprintID", String.valueOf(mCS.getSprintsId().get(0)));
		addRequestParameter("Type", "story");

		// ================ set session info ========================s
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		String result = response.getWriterBuffer().toString();
		HashMap resultMap = mGson.fromJson(result, HashMap.class);
		ArrayList<LinkedHashTreeMap<String, Object>> points = (ArrayList<LinkedHashTreeMap<String, Object>>) resultMap.get("Points");
		Double totalStoryPoint = new Integer(STORY_COUNT * STORY_EST).doubleValue();
		// 算出每一天ideal point的遞減值
		Double periodPoint = totalStoryPoint / (points.size() - 1);
		assertEquals(true, resultMap.get("success"));
		for (int i = 0; i < points.size(); i++) {
			assertEquals(formatter.format(idealPointArray[i]), points.get(i).get("Date"));
			assertEquals(totalStoryPoint - periodPoint * i, points.get(i).get("IdealPoint"));
			// 只有第一天有real point其他因為時間尚未到，所以不會有real point
			if (i == 0) {
				assertEquals(totalStoryPoint, points.get(i).get("RealPoint"));
			} else {
				assertEquals("null", points.get(i).get("RealPoint"));
			}
		}
	}

	/**
	 * 測試Story的burndown chart當有story拉到done時，資料是否正確
	 */
	public void testGetSprintBurndownChartData_Story_2() throws Exception {
		final int STORY_COUNT = 2, STORY_EST = 5;
		// Sprint加入2個Story
		AddStoryToSprint ASTS = new AddStoryToSprint(STORY_COUNT, STORY_EST, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASTS.exe();

		// 拿出sprint的每一天日期
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(mProject, mCS.getSprintsId().get(0));
		SprintBacklogMapper SprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		TaskBoard taskBoard = new TaskBoard(sprintBacklogLogic, SprintBacklogMapper);
		LinkedHashMap<Date, Double> ideal = taskBoard.getStoryIdealPointMap();
		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Object[] idealPointArray = ideal.keySet().toArray();
		// 將story移到done
		sprintBacklogLogic.closeStory(ASTS.getStories().get(0).getId(), ASTS.getStories().get(0).getName(), "", "");

		// ================ set request info ========================
		request.setHeader("Referer", "?PID=" + mProject.getName());
		addRequestParameter("SprintID",  String.valueOf(mCS.getSprintsId().get(0)));
		addRequestParameter("Type", "story");

		// ================ set session info ========================s
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		String result = response.getWriterBuffer().toString();
		HashMap resultMap = mGson.fromJson(result, HashMap.class);
		ArrayList<LinkedHashTreeMap<String, Object>> points = (ArrayList<LinkedHashTreeMap<String, Object>>) resultMap.get("Points");
		// 由於done了一個story因此減掉一個story的est
		Double totalStoryPoint = new Integer(STORY_COUNT * STORY_EST).doubleValue() - STORY_EST;
		Double periodPoint = totalStoryPoint / (points.size() - 1);
		assertEquals(true, resultMap.get("success"));
		for (int i = 0; i < points.size(); i++) {
			assertEquals(formatter.format(idealPointArray[i]), points.get(i).get("Date"));
			assertEquals(totalStoryPoint - periodPoint * i, points.get(i).get("IdealPoint"));
			if (i == 0) {
				assertEquals(totalStoryPoint, points.get(i).get("RealPoint"));
			} else {
				assertEquals("null", points.get(i).get("RealPoint"));
			}
		}
	}

	/**
	 * 測試Task的burndown chart在都沒有done的情況下的圖資料是否正確
	 */
	public void testGetSprintBurndownChartData_Task_1() throws Exception {
		final int STORY_COUNT = 2, TASK_COUNT = 2, STORY_EST = 5, TASK_EST = 5;
		// Sprint加入2個Story
		AddStoryToSprint ASTS = new AddStoryToSprint(STORY_COUNT, STORY_EST, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASTS.exe();

		// 每個Story加入2個task
		AddTaskToStory ATTS = new AddTaskToStory(TASK_COUNT, TASK_EST, ASTS, mCP);
		ATTS.exe();

		// ================ set request info ========================
		request.setHeader("Referer", "?PID=" + mProject.getName());
		addRequestParameter("SprintID", String.valueOf(mCS.getSprintsId().get(0)));
		addRequestParameter("Type", "task");

		// ================ set session info ========================s
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// 拿出sprint的每一天日期放在idealPointArray當expecte 天數
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(mProject, mCS.getSprintsId().get(0));
		SprintBacklogMapper SprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		TaskBoard taskBoard = new TaskBoard(sprintBacklogLogic, SprintBacklogMapper);
		LinkedHashMap<Date, Double> ideal = taskBoard.getStoryIdealPointMap();
		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Object[] idealPointArray = ideal.keySet().toArray();

		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		String result = response.getWriterBuffer().toString();
		HashMap resultMap = mGson.fromJson(result, HashMap.class);
		ArrayList<LinkedHashTreeMap<String, Object>> points = (ArrayList<LinkedHashTreeMap<String, Object>>) resultMap.get("Points");
		Double totalTaskPoint = new Integer(STORY_COUNT * TASK_COUNT * TASK_EST).doubleValue();
		// 算出每一天ideal point的遞減值
		Double periodPoint = totalTaskPoint / (points.size() - 1);
		assertEquals(true, resultMap.get("success"));
		for (int i = 0; i < points.size(); i++) {
			assertEquals(formatter.format(idealPointArray[i]), points.get(i).get("Date"));
			assertEquals(totalTaskPoint - periodPoint * i, points.get(i).get("IdealPoint"));
			// 只有第一天有real point其他因為時間尚未到，所以不會有real point
			if (i == 0) {
				assertEquals(totalTaskPoint, points.get(i).get("RealPoint"));
			} else {
				assertEquals("null", points.get(i).get("RealPoint"));
			}
		}
	}

	/**
	 * 測試Story的burndown chart當有story拉到done時，資料是否正確
	 */
	public void testGetSprintBurndownChartData_Task_2() throws Exception {
		final int STORY_COUNT = 2, TASK_COUNT = 2, STORY_EST = 5, TASK_EST = 5;
		// Sprint加入2個Story
		AddStoryToSprint ASTS = new AddStoryToSprint(STORY_COUNT, STORY_EST, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASTS.exe();

		// 每個Story加入2個task
		AddTaskToStory ATTS = new AddTaskToStory(TASK_COUNT, TASK_EST, ASTS, mCP);
		ATTS.exe();

		// 拿出sprint的每一天日期
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(mProject, mCS.getSprintsId().get(0));
		SprintBacklogMapper SprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		TaskBoard taskBoard = new TaskBoard(sprintBacklogLogic, SprintBacklogMapper);
		LinkedHashMap<Date, Double> ideal = taskBoard.getStoryIdealPointMap();
		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Object[] idealPointArray = ideal.keySet().toArray();
		// 將task拉到done
		sprintBacklogLogic.closeTask(ATTS.getTasksId().get(0), ATTS.getTasks().get(0).getName(), "", 0, "");

		// ================ set request info ========================
		request.setHeader("Referer", "?PID=" + mProject.getName());
		addRequestParameter("SprintID", String.valueOf(mCS.getSprintsId().get(0)));
		addRequestParameter("Type", "task");

		// ================ set session info ========================s
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		String result = response.getWriterBuffer().toString();
		HashMap resultMap = mGson.fromJson(result, HashMap.class);
		ArrayList<LinkedHashTreeMap<String, Object>> points = (ArrayList<LinkedHashTreeMap<String, Object>>) resultMap.get("Points");
		assertEquals(true, resultMap.get("success"));
		// 由於done了一個task因此減掉一個task的est
		Double totalTaskPoint = new Integer(STORY_COUNT * TASK_COUNT * TASK_EST).doubleValue() - TASK_EST;
		Double periodPoint = totalTaskPoint / (points.size() - 1);
		for (int i = 0; i < points.size(); i++) {
			assertEquals(formatter.format(idealPointArray[i]), points.get(i).get("Date"));
			assertEquals(totalTaskPoint - periodPoint * i, points.get(i).get("IdealPoint"));
			if (i == 0) {
				assertEquals(totalTaskPoint, points.get(i).get("RealPoint"));
			} else {
				assertEquals("null", points.get(i).get("RealPoint"));
			}
		}
	}
}
