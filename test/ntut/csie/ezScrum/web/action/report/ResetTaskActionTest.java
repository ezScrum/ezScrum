package ntut.csie.ezScrum.web.action.report;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class ResetTaskActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private AddStoryToSprint ASS;
	private AddTaskToStory ATS;
	private Configuration configuration;

	public ResetTaskActionTest(String testMethod) {
		super(testMethod);
	}

	// 目前 setUp 設定的情境為︰產生1個Project、產生1個Sprint、Sprint產生1個Story、每個Story設定點數1點
	// 將Story加入到Sprint內、每個 Story 產生1個1點的 Tasks 並且正確加入
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案

		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe(); // 新增一個 Sprint

		this.ASS = new AddStoryToSprint(1, 1, this.CS, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		this.ASS.exe(); // 新增五筆 Stories 到 Sprints 內，並設計 Sprint 的 Story 點數總和為 10

		this.ATS = new AddTaskToStory(1, 1, this.ASS, this.CP);
		this.ATS.exe(); // 新增兩筆 Task 到各個 Stories 內

		super.setUp();

		setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent"));	// 設定讀取的 struts-config 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/resetTask");

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		CopyProject copyProject = new CopyProject(this.CP);
		copyProject.exeDelete_Project(); // 刪除測試檔案
		
		configuration.setTestMode(false);
		configuration.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		copyProject = null;
		this.CP = null;
		this.CS = null;
		this.ASS = null;
		this.ATS = null;
		configuration = null;
	}

	// 測試Task ReOpen時的狀況
	public void testResetTask() {
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		TaskObject task = this.ATS.getTasks().get(0); // 取得Task資訊
		Long TaskID = task.getId();
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, configuration.getUserSession(), CS.getSprintIDList().get(0));
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();

		// ================== set parameter info ====================
		addRequestParameter("Id", String.valueOf(TaskID)); // 取得第一筆 Task ID
		addRequestParameter("Name", task.getName());
		addRequestParameter("Notes", task.getNotes());
		addRequestParameter("ChangeDate", "");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 action ==============================
		// 先設定Task為assigned的狀態 在測試
		sprintBacklogLogic.checkOutTask(task.getId(), task.getName(), configuration.USER_ID, "", task.getNotes(), "");
		actionPerform();
		// 驗證回傳 path
		verifyNoActionErrors();
		// 驗證是否正確存入資料
		task = TaskObject.get(TaskID); // 重新取得Task資訊
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{")
							.append("\"success\":true,")
							.append("\"Issue\":{")
							.append("\"Id\":").append(String.valueOf(TaskID)).append(",")
							.append("\"Link\":\"").append("\",")
							.append("\"Name\":\"").append(task.getName()).append("\",")
							.append("\"Handler\":\"").append("").append("\",")
							.append("\"Partners\":\"").append(task.getPartnersUsername()).append("\"}")
							.append("}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
		assertEquals(TaskObject.STATUS_UNCHECK, task.getStatus()); // 判斷Task狀態是不是回到Uncheck了

		// ============= release ==============
		project = null;
		sprintBacklogLogic = null;
		sprintBacklogMapper = null;
		task = null;
	}
}