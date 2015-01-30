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
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class CheckOutTaskActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private AddStoryToSprint ASS;
	private AddTaskToStory ATS;
	private Configuration configuration;

	public CheckOutTaskActionTest(String testMethod) {
		super(testMethod);
	}

	// 目前 setUp 設定的情境為︰產生一個Project、產生一個Sprint、Sprint產生五個Story、每個Story設定點數兩點
	// 將Story加入到Sprint內、每個 Story 產生兩個一點的 Tasks 並且正確加入
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL

		this.CP = new CreateProject(1);
		this.CP.exeCreate();								// 新增一測試專案

		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe();										// 新增一個 Sprint

		int Story_Count = 5;
		int Story_Estimation = 2;
		this.ASS = new AddStoryToSprint(Story_Count, Story_Estimation, this.CS, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		this.ASS.exe();		// 新增五筆 Stories 到 Sprints 內，並設計 Sprint 的 Story 點數總和為 10

		int Task_Count = 2;
		this.ATS = new AddTaskToStory(Task_Count, 1, this.ASS, this.CP);
		this.ATS.exe();		// 新增兩筆 Task 到各個 Stories 內

		super.setUp();

		setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent"));	// 設定讀取的 struts-config 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/checkOutTask");

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL

		CopyProject copyProject = new CopyProject(this.CP);
		copyProject.exeDelete_Project();					// 刪除測試檔案
		
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

	/**
	 *  正常執行Task的CheckOut
	 */
	public void testexecute() throws Exception {
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		String partners = "py2k; oph; taoyu;";
		IIssue issue = this.ATS.getTasks().get(0); // 取得Task資訊
		Long TaskID = issue.getIssueID();
		
		// ================== set parameter info ====================
		addRequestParameter("Id", String.valueOf(TaskID)); // 取得第一筆 Task ID
		addRequestParameter("Name", issue.getSummary());
		addRequestParameter("Handler", configuration.USER_ID);
		addRequestParameter("Partners", partners);
		addRequestParameter("Notes", issue.getNotes());
		addRequestParameter("ChangeDate", "");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 action ==============================
		actionPerform();
		verifyNoActionErrors();

		// 驗證是否正確存入資料
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, configuration.getUserSession(), null);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		issue = sprintBacklogMapper.getStory(TaskID); // 重新取得Task資訊
		
		assertEquals(String.valueOf(TaskID), Long.toString(issue.getIssueID()));
		assertEquals(configuration.USER_ID, issue.getAssignto());
		assertEquals(partners, issue.getPartners());
		assertEquals("TEST_TASK_NOTES_1", issue.getNotes());
		assertEquals(0, issue.getDoneDate());

		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{")
		        			.append("\"success\":true,")
		        			.append("\"Issue\":{")
		        			.append("\"Id\":").append(String.valueOf(TaskID)).append(",")
		        			.append("\"Link\":\"/ezScrum/showIssueInformation.do?issueID=").append(String.valueOf(TaskID)).append("\",")
		        			.append("\"Name\":\"").append(issue.getSummary()).append("\",")
		        			.append("\"Handler\":\"").append(issue.getAssignto()).append("\",")
		        			.append("\"Partners\":\"").append(partners).append("\"}")
		        			.append("}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
		assertEquals("assigned", issue.getStatus()); // 判斷Task是不是已經assigned了

		// ============= release ==============
		project = null;
		sprintBacklogMapper = null;
		issue = null;
	}

	
	/**
	 *  代入錯誤不存在的handler 執行Task的CheckOut
	 */
	public void testWrongParameter1() throws Exception {
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		String partners = "py2k; oph; taoyu;";
		IIssue issue = this.ATS.getTasks().get(0); // 取得Task資訊
		Long TaskID = issue.getIssueID();
		
		// ================== set parameter info ====================
		addRequestParameter("Id", String.valueOf(TaskID)); // 取得第一筆 Task ID
		addRequestParameter("Name", issue.getSummary());
		addRequestParameter("Handler", "XXX");
		addRequestParameter("Partners", partners);
		addRequestParameter("Notes", issue.getNotes());
		addRequestParameter("ChangeDate", "");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ 執行 action ==============================
		actionPerform();
		verifyNoActionErrors();

		// 驗證是否正確存入資料
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, configuration.getUserSession(), null);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		issue = sprintBacklogMapper.getStory(TaskID); // 重新取得Task資訊
		
		assertEquals(String.valueOf(TaskID), Long.toString(issue.getIssueID()));
		assertEquals("TEST_TASK_1", issue.getSummary());
		assertEquals("", issue.getAssignto());					// 因為 Handler 參數有誤，不應該寫入資訊
		assertEquals(partners, issue.getPartners());
		assertEquals("TEST_TASK_NOTES_1", issue.getNotes());	// ============= 無法更正 note ============
		assertEquals(0, issue.getDoneDate());
		
		// ============= release ==============
		project = null;
		sprintBacklogMapper = null;
		issue = null;
	}

	/**
	 *  代入非正確日期的參數	執行Task的CheckOut
	 */
	public void testWrongParameter2() throws Exception {
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		String partners = "py2k; oph; taoyu;";
		IIssue issue = this.ATS.getTasks().get(0); // 取得Task資訊
		Long TaskID = issue.getIssueID();

		// ================== set parameter info ====================
		addRequestParameter("Id", String.valueOf(TaskID)); // 取得第一筆 Task ID
		addRequestParameter("Name", issue.getSummary());
		addRequestParameter("Handler", configuration.USER_ID);
		addRequestParameter("Partners", partners);
		addRequestParameter("Notes", issue.getNotes());
		addRequestParameter("ChangeDate", "XXXX"); // Unparsed date "XXXX"

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
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
		issue = null;
	}

	/**
	 *  代入超出範圍的 Task ID
	 */
	public void testWrongParameter3() throws Exception {
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		String partners = "py2k; oph; taoyu;";
		
		// ================== set parameter info ====================
		addRequestParameter("Id", "1000"); // 超出範圍的 Task
		addRequestParameter("Handler", configuration.USER_ID);
		addRequestParameter("Partners", partners);
		addRequestParameter("Notes", "Nothing");
		addRequestParameter("ChangeDate", "");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
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
