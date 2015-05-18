package ntut.csie.ezScrum.web.action.report;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import servletunit.struts.MockStrutsTestCase;

public class DoneIssueActionTest extends MockStrutsTestCase {
	
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
	private Configuration mConfig;

	public DoneIssueActionTest(String testMethod) {
		super(testMethod);
	}

	// 目前 setUp 設定的情境為︰產生一個Project、產生一個Sprint、Sprint產生五個Story、每個Story設定點數兩點
	// 將Story加入到Sprint內、每個 Story 產生兩個一點的 Tasks 並且正確加入
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

		// 新增一個 Sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// 新增五筆 Stories 到 Sprints 內，並設計 Sprint 的 Story 點數總和為 10
		int Story_Count = 5;
		int Story_Estimation = 2;
		mASTS = new AddStoryToSprint(Story_Count, Story_Estimation, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mASTS.exe();

		// 新增兩筆 Task 到各個 Stories 內
		int Task_Count = 2;
		mATTS = new AddTaskToStory(Task_Count, 1, mASTS, mCP);
		mATTS.exe();

		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/doneIssue");

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

		super.tearDown();

		// ============= release ==============
		ini = null;
		projectManager = null;
		mCP = null;
		mCS = null;
		mASTS = null;
		mATTS = null;
		mConfig = null;
	}

	// 測試Task拉到Done時的狀況
	public void testDoneIssue_Task() {
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		TaskObject task = mATTS.getTasks().get(0);
		Long taskId = task.getId();

		// ================== set parameter info ====================
		addRequestParameter("Id", String.valueOf(taskId));
		addRequestParameter("Name", task.getName());
		addRequestParameter("Notes", task.getNotes());
		addRequestParameter("ChangeDate", "2015/02/06-12:00:00");
		addRequestParameter("Actualhour", String.valueOf(task.getActual()));
		addRequestParameter("IssueType", "Task");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		request.setHeader("Referer", "?PID=" + project.getName());

		// ================ 執行 action ==============================
		actionPerform();
		verifyNoActionErrors();

		// 驗證是否正確存入資料
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, -1);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		task = sprintBacklogMapper.getTask(taskId); // 重新取得Task資訊

		StringBuilder expectedResponseText = new StringBuilder();
		String handlerUserName = "";
		if(task.getHandler() != null){
			handlerUserName = task.getHandler().getUsername();
		}
		expectedResponseText.append("{")
							.append("\"success\":true,")
							.append("\"Issue\":{")
							.append("\"Id\":").append(String.valueOf(taskId)).append(",")
							.append("\"Link\":\"").append("\",")
							.append("\"Name\":\"").append(task.getName()).append("\",")
							.append("\"Handler\":\"").append(handlerUserName).append("\",")
							.append("\"Partners\":\"").append(task.getPartnersUsername()).append("\"}")
							.append("}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
		assertEquals(TaskObject.STATUS_DONE, task.getStatus()); // 判斷Task是不是已經closed了

		// ============= release ==============
		project = null;
		sprintBacklogLogic = null;
		sprintBacklogMapper = null;
		task = null;
	}

	/**
	 * 測試 Story 拉到 Done 時的狀況
	 * TODO: 此 test case 需重寫!!!
	 */
	public void testDoneIssue_Story() {
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		StoryObject story = mASTS.getStories().get(0);
		long storyId = story.getId();
		
		// ================== set parameter info ====================
		addRequestParameter("Id", String.valueOf(storyId));
		addRequestParameter("Name", story.getName());
		addRequestParameter("Notes", story.getNotes());
		addRequestParameter("ChangeDate", "2015/02/06-12:00:00");
		addRequestParameter("Actualhour", Long.toString(story.getEstimate()));
		addRequestParameter("IssueType", "Story");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		request.setHeader("Referer", "?PID=" + project.getName());

		// ================ 執行 action ==============================
		actionPerform();
		verifyNoActionErrors();

		// 驗證是否正確存入資料
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, -1);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		story = sprintBacklogMapper.getStory(storyId);
		
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{")
							.append("\"success\":true,")
							.append("\"Issue\":{")
							.append("\"Id\":").append(storyId).append(",")
							.append("\"Link\":\"").append("\",")
							.append("\"Name\":\"").append(story.getName()).append("\",")
							.append("\"Estimate\":").append(story.getEstimate()).append(",")
							.append("\"Handler\":\"").append("\",")
							.append("\"Partners\":\"").append("\"}")
							.append("}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
		assertEquals(StoryObject.STATUS_DONE, story.getStatus()); // 判斷Story是不是已經closed了

		// ============= release ==============
		project = null;
		sprintBacklogLogic = null;
		sprintBacklogMapper = null;
		story = null;
	}
}
