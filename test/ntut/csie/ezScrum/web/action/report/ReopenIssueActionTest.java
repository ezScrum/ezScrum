package ntut.csie.ezScrum.web.action.report;

import java.io.File;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class ReopenIssueActionTest extends MockStrutsTestCase {
	
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
	private Configuration mConfig;

	public ReopenIssueActionTest(String testMethod) {
		super(testMethod);
	}

	// 目前 setUp 設定的情境為︰產生1個Project、產生1個Sprint、Sprint產生1個Story、每個Story設定點數1點
	// 將Story加入到Sprint內、每個 Story 產生1個1點的 Tasks 並且正確加入
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
		mASTS = new AddStoryToSprint(1, 1, mCS, mCP, CreateProductBacklog.TYPE_ESTIMATION);
		mASTS.exe();

		// 新增兩筆 Task 到各個 Stories 內
		mATTS = new AddTaskToStory(1, 1, mASTS, mCP);
		mATTS.exe();

		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/reopenIssue");

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

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

	// 測試Task ReOpen時的狀況
	public void testReopenIssue_Task() {
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		TaskObject task = mATTS.getTasks().get(0);
		long taskId = task.getId();
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, mConfig.getUserSession(), "-1");
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();

		// ================== set parameter info ====================
		addRequestParameter("Id", String.valueOf(taskId));
		addRequestParameter("Name", task.getName());
		addRequestParameter("Notes", task.getNotes());
		addRequestParameter("IssueType", "Task");
		addRequestParameter("ChangeDate", "");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 action ==============================
		sprintBacklogMapper.closeStory(task.getId(), task.getNotes(), ""); // 先設定Task為closed的狀態 在測試
		actionPerform();
		// 驗證回傳 path
		verifyNoActionErrors();
		// 驗證是否正確存入資料
		task = TaskObject.get(taskId); // 重新取得Task資訊
		StringBuilder expectedResponseText = new StringBuilder();
		String handlerUsername = "";
		AccountObject handler = task.getHandler();
		if (handler != null) {
			handlerUsername = handler.getUsername();
		}
		expectedResponseText.append("{")
							.append("\"success\":true,")
							.append("\"Issue\":{")
							.append("\"Id\":").append(String.valueOf(taskId)).append(",")
							.append("\"Link\":\"").append("\",")
							.append("\"Name\":\"").append(task.getName()).append("\",")
							.append("\"Handler\":\"").append(handlerUsername).append("\",")
							.append("\"Partners\":\"").append(task.getPartnersUsername()).append("\"}")
							.append("}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
		assertEquals(TaskObject.STATUS_CHECK, task.getStatus()); // 判斷Task狀態是不是回到assigned了

		// ============= release ==============
		project = null;
		sprintBacklogLogic = null;
		sprintBacklogMapper = null;
		task = null;
	}

	// 測試Story ReOpen時的狀況
	public void testReopenIssue_Story() {
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		StoryObject story = mASTS.getStories().get(0);
		Long storyId = story.getId();
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, mConfig.getUserSession(), "-1");
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();

		// ================== set parameter info ====================
		addRequestParameter("Id", String.valueOf(storyId));
		addRequestParameter("Name", story.getName());
		addRequestParameter("Notes", story.getNotes());
		addRequestParameter("IssueType", "Story");
		addRequestParameter("ChangeDate", "");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 action ==============================
		sprintBacklogMapper.closeStory(story.getId(), story.getNotes(), ""); // 先設定Story為closed的狀態 在測試
		actionPerform();
		// 驗證回傳 path
		verifyNoActionErrors();
		// 驗證是否正確存入資料，重新取得 Story 資訊
		story = sprintBacklogMapper.getStory(storyId);
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{")
							.append("\"success\":true,")
							.append("\"Issue\":{")
							.append("\"Id\":").append(String.valueOf(storyId)).append(",")
							.append("\"Link\":\"/ezScrum/showIssueInformation.do?issueID=").append(String.valueOf(storyId)).append("\",")
							.append("\"Name\":\"").append(story.getName()).append("\",")
							.append("\"Handler\":\"").append(story.getAssignto()).append("\",")
							.append("\"Partners\":\"").append(story.getPartners()).append("\"}")
							.append("}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
		assertEquals(ITSEnum.S_NEW_STATUS, story.getStatus()); // 判斷Story狀態是不是回到new了

		// ============= release ==============
		project = null;
		sprintBacklogLogic = null;
		sprintBacklogMapper = null;
		story = null;
	}
}
