package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddSprintToRelease;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxAddSprintTaskActionTest extends MockStrutsTestCase {

	private CreateProject mCP;
	private CreateRelease mCR;
	private Configuration mConfig;
	private IProject mIProject;
	private final String mActionPath = "/ajaxAddSprintTask";

	public AjaxAddSprintTaskActionTest(String testName) {
		super(testName);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// create project
		mCP = new CreateProject(1);
		mCP.exeCreate();

		// create release Plan
		mCR = new CreateRelease(1, mCP);
		mCR.exe();

		mIProject = mCP.getProjectList().get(0);

		super.setUp();

		// ================ set action info ========================
		setContextDirectory(new File(mConfig.getBaseDirPath().concat(
				"/WebContent")));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);

		ini = null;
	}

	protected void tearDown() throws Exception {
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		ini = null;
		projectManager = null;
		mCP = null;
		mCR = null;
		mConfig = null;
		mIProject = null;
	}

	/**
	 * 測試全部欄位都填寫的情況
	 */
	public void testAddSprintTask_1() throws Exception {
		AddSprintToRelease ASR = new AddSprintToRelease(1, mCR, mCP);
		ASR.exe();

		AddStoryToSprint ASS = new AddStoryToSprint(1, 1, 1, mCP,
				CreateProductBacklog.COLUMN_TYPE_EST);
		ASS.exe();

		// ================ set request info ========================
		String projectName = mIProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);

		// 設定 Session 資訊
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());
		request.getSession().setAttribute("Project", mIProject);

		// 設定新增 Task 所需的資訊
		String expectedTaskName = "UT for Add New Task for Name";
		String expectedStoryId = "1";
		String expectedTaskEstimation = "1";
		String expectedSpecificTime = "2013-07-02";
		String expectedSprintId = "1";
		String expectedTaskNote = "UT for Add New Task for Notes";

		addRequestParameter("Name", expectedTaskName);
		addRequestParameter("Estimate", expectedTaskEstimation);
		addRequestParameter("Notes", expectedTaskNote);
		addRequestParameter("SpecificTime", expectedSpecificTime);
		addRequestParameter("sprintId", expectedSprintId);
		addRequestParameter("issueID", expectedStoryId);

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();

		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText
			.append("<AddNewTask>")
				.append("<Result>true</Result>")
				.append("<Task>")
					.append("<Id>1</Id>") // task = story id + 1
					.append("<Link>/ezScrum/showIssueInformation.do?issueID=1</Link>")
					.append("<Name>").append(expectedTaskName).append("</Name>")
					.append("<Estimate>").append(expectedTaskEstimation).append("</Estimate>")
					.append("<Actual>0</Actual>")
					.append("<Notes>").append(expectedTaskNote).append("</Notes>")
				.append("</Task>")
			.append("</AddNewTask>");
		
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}

	/**
	 * 測試只填寫名字其他都不填的情況
	 */
	public void testAddSprintTask_2() throws Exception {
		AddSprintToRelease ASR = new AddSprintToRelease(1, mCR, mCP);
		ASR.exe();

		AddStoryToSprint ASS = new AddStoryToSprint(1, 1, 1, mCP,
				CreateProductBacklog.COLUMN_TYPE_EST);
		ASS.exe();

		// ================ set request info ========================
		String projectName = mIProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		
		// 設定 Session 資訊
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());
		request.getSession().setAttribute("Project", mIProject);
		
		// 設定新增 Task 所需的資訊
		String expectedTaskName = "UT for Add New Task for Name";
		String expectedStoryId = "1";
		String expectedTaskEstimation = "";
		String expectedSpecificTime = "";
		String expectedSprintId = "1";
		String expectedTaskNote = "";
		
		addRequestParameter("Name", expectedTaskName);
		addRequestParameter("Estimate", expectedTaskEstimation);
		addRequestParameter("Notes", expectedTaskNote);
		addRequestParameter("SpecificTime", expectedSpecificTime);
		addRequestParameter("sprintId", expectedSprintId);
		addRequestParameter("issueID", expectedStoryId);

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText
			.append("<AddNewTask>")
				.append("<Result>true</Result>")
				.append("<Task>")
					.append("<Id>1</Id>")
					.append("<Link>/ezScrum/showIssueInformation.do?issueID=1</Link>")
					.append("<Name>").append(expectedTaskName).append("</Name>")
					.append("<Estimate>0</Estimate>")
					.append("<Actual>0</Actual>")
					.append("<Notes>").append(expectedTaskNote).append("</Notes>")
				.append("</Task>")
			.append("</AddNewTask>");
		
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}