package ntut.csie.ezScrum.web.action.report;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;
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
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL

		mCP = new CreateProject(1);
		mCP.exeCreate();								// 新增一測試專案

		mCS = new CreateSprint(1, mCP);
		mCS.exe();										// 新增一個 Sprint

		int Story_Count = 5;
		int Story_Estimation = 2;
		mASTS = new AddStoryToSprint(Story_Count, Story_Estimation, mCS, mCP, CreateProductBacklog.TYPE_ESTIMATION);
		mASTS.exe();		// 新增五筆 Stories 到 Sprints 內，並設計 Sprint 的 Story 點數總和為 10

		int Task_Count = 2;
		mATTS = new AddTaskToStory(Task_Count, 1, mASTS, mCP);
		mATTS.exe();		// 新增兩筆 Task 到各個 Stories 內

		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/doneIssue");

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL

		CopyProject copyProject = new CopyProject(mCP);
		copyProject.exeDelete_Project();					// 刪除測試檔案
		
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		copyProject = null;
		mCP = null;
		mCS = null;
		mASTS = null;
		mATTS = null;
		mConfig = null;
	}

	// 測試Task拉到Done時的狀況
	public void testDoneIssue_Task() {
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		TaskObject task = mATTS.getTasks().get(0); // 取得Task資訊
		Long taskId = task.getId();

		// ================== set parameter info ====================
		addRequestParameter("Id", String.valueOf(taskId)); // 取得第一筆 Task ID
		addRequestParameter("Name", task.getName());
		addRequestParameter("Notes", task.getNotes());
		addRequestParameter("ChangeDate", "2015/02/06-12:00:00");
		addRequestParameter("Actualhour", String.valueOf(task.getActual()));
		addRequestParameter("IssueType", "Task");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 action ==============================
		actionPerform();
		verifyNoActionErrors();

		// 驗證是否正確存入資料
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, mConfig.getUserSession(), null);
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

	// 測試Story拉到Done時的狀況
	public void testDoneIssue_Story() {
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		IIssue story = mASTS.getStories().get(0);		// 取得Story資訊
		Long storyId = story.getIssueID();
		
		// ================== set parameter info ====================
		addRequestParameter("Id", String.valueOf(storyId));
		addRequestParameter("Name", story.getSummary());
		addRequestParameter("Notes", story.getNotes());
		addRequestParameter("ChangeDate", "2015/02/06-12:00:00");
		addRequestParameter("Actualhour", story.getActualHour());
		addRequestParameter("IssueType", "Story");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 action ==============================
		actionPerform();
		verifyNoActionErrors();

		// 驗證是否正確存入資料
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, mConfig.getUserSession(), null);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		story = sprintBacklogMapper.getStory(storyId); // 重新取得Story資訊
		
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{")
							.append("\"success\":true,")
							.append("\"Issue\":{")
							.append("\"Id\":").append(String.valueOf(storyId)).append(",")
							.append("\"Link\":\"/ezScrum/showIssueInformation.do?issueID=").append(String.valueOf(storyId)).append("\",")
							.append("\"Name\":\"").append(story.getSummary()).append("\",")
							.append("\"Handler\":\"").append(story.getAssignto()).append("\",")
							.append("\"Partners\":\"").append(story.getPartners()).append("\"}")
							.append("}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
		assertEquals("closed", story.getStatus()); // 判斷Story是不是已經closed了

		// ============= release ==============
		project = null;
		sprintBacklogLogic = null;
		sprintBacklogMapper = null;
		story = null;
	}
}
