package ntut.csie.ezScrum.web.action.report;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class CheckOutTaskActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
	private Configuration mConfig;

	public CheckOutTaskActionTest(String testMethod) {
		super(testMethod);
	}

	// 目前 setUp 設定的情境為︰產生一個Project、產生一個Sprint、Sprint產生五個Story、每個Story設定點數兩點
	// 將Story加入到Sprint內、每個 Story 產生兩個一點的 Tasks 並且正確加入
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL

		mCP = new CreateProject(1);
		mCP.exeCreate();								// 新增一測試專案

		mCS = new CreateSprint(1, mCP);
		mCS.exe();										// 新增一個 Sprint

		int Story_Count = 5;
		int Story_Estimation = 2;
		mASTS = new AddStoryToSprint(Story_Count, Story_Estimation, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mASTS.exe();		// 新增五筆 Stories 到 Sprints 內，並設計 Sprint 的 Story 點數總和為 10

		int Task_Count = 2;
		mATTS = new AddTaskToStory(Task_Count, 1, mASTS, mCP);
		mATTS.exe();		// 新增兩筆 Task 到各個 Stories 內

		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));	// 設定讀取的 struts-config 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/checkOutTask");

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL

		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mASTS = null;
		mATTS = null;
		mConfig = null;
	}

	/**
	 *  正常執行Task的CheckOut
	 */
	public void testexecute() throws Exception {
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		String partners = "";
		TaskObject task = mATTS.getTasks().get(0); // 取得Task資訊
		Long taskId = task.getId();
		
		// ================== set parameter info ====================
		addRequestParameter("Id", String.valueOf(taskId)); // 取得第一筆 Task ID
		addRequestParameter("Name", task.getName());
		addRequestParameter("Handler", mConfig.USER_ID);
		addRequestParameter("Partners", "");
		addRequestParameter("Notes", task.getNotes());
		addRequestParameter("ChangeDate", "");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 action ==============================
		actionPerform();
		verifyNoActionErrors();

		// 驗證是否正確存入資料
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, -1);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		task = sprintBacklogMapper.getTask(taskId); // 重新取得Task資訊
		task.getHistories();
		
		assertEquals(String.valueOf(taskId), Long.toString(task.getId()));
		assertEquals(mConfig.USER_ID, task.getHandler().getUsername());
		assertEquals(partners, task.getPartnersUsername());
		assertEquals("TEST_TASK_NOTES_1", task.getNotes());
		assertEquals(0, task.getDoneTime());

		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{")
		        			.append("\"success\":true,")
		        			.append("\"Issue\":{")
		        			.append("\"Id\":").append(String.valueOf(taskId)).append(",")
		        			.append("\"Link\":\"\",")
		        			.append("\"Name\":\"").append(task.getName()).append("\",")
		        			.append("\"Handler\":\"").append(task.getHandler().getUsername()).append("\",")
		        			.append("\"Partners\":\"").append(partners).append("\"}")
		        			.append("}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
		assertEquals(TaskObject.STATUS_CHECK, task.getStatus()); // 判斷Task是不是已經assigned了

		// ============= release ==============
		project = null;
		sprintBacklogMapper = null;
		task = null;
	}

	
	/**
	 *  代入錯誤不存在的handler 執行Task的CheckOut
	 */
	public void testWrongParameter1() throws Exception {
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		String partners = "";
		TaskObject task = mATTS.getTasks().get(0); // 取得Task資訊
		Long taskId = task.getId();
		
		// ================== set parameter info ====================
		addRequestParameter("Id", String.valueOf(taskId)); // 取得第一筆 Task ID
		addRequestParameter("Name", task.getName());
		addRequestParameter("Handler", "XXX");
		addRequestParameter("Partners", partners);
		addRequestParameter("Notes", task.getNotes());
		addRequestParameter("ChangeDate", "");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 action ==============================
		actionPerform();
		verifyNoActionErrors();

		// 驗證是否正確存入資料
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, -1);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		task = sprintBacklogMapper.getTask(taskId); // 重新取得Task資訊
		task.getHistories(); // 重新取得 History
		
		assertEquals(String.valueOf(taskId), Long.toString(task.getId()));
		assertEquals("TEST_TASK_1", task.getName());
		assertEquals(null, task.getHandler());					// 因為 Handler 參數有誤，不應該寫入資訊
		assertEquals(partners, task.getPartnersUsername());
		assertEquals("TEST_TASK_NOTES_1", task.getNotes());	// ============= 無法更正 note ============
		assertEquals(0, task.getDoneTime());
		
		// ============= release ==============
		project = null;
		sprintBacklogMapper = null;
		task = null;
	}

	/**
	 *  代入非正確日期的參數	執行Task的CheckOut
	 */
	public void testWrongParameter2() throws Exception {
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		String partners = "py2k; oph; taoyu;";
		TaskObject task = mATTS.getTasks().get(0); // 取得Task資訊
		Long taskId = task.getId();

		// ================== set parameter info ====================
		addRequestParameter("Id", String.valueOf(taskId)); // 取得第一筆 Task ID
		addRequestParameter("Name", task.getName());  
		addRequestParameter("Handler", mConfig.USER_ID);
		addRequestParameter("Partners", partners);
		addRequestParameter("Notes", task.getNotes());
		addRequestParameter("ChangeDate", "XXXX"); // Unparsed date "XXXX"

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 action ==============================
		actionPerform();
		verifyNoActionErrors();

		// 驗證是否正確存入資料
		String expectedResponseText = "fail...非正確日期的參數";
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
		
		// ============= release ==============
		project = null;
		task = null;
	}

	/**
	 *  代入超出範圍的 Task ID
	 */
	public void testWrongParameter3() throws Exception {
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		String partners = "py2k; oph; taoyu;";
		
		// ================== set parameter info ====================
		addRequestParameter("Id", "1000"); // 超出範圍的 Task
		addRequestParameter("Handler", mConfig.USER_ID);
		addRequestParameter("Partners", partners);
		addRequestParameter("Notes", "Nothing");
		addRequestParameter("ChangeDate", "");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 action ==============================
		actionPerform();
		verifyNoActionErrors();

		// 驗證是否正確存入資料
		String expectedResponseText = "fail...issue不存在";
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
		
		// ============= release ==============
		project = null;
	}
}
