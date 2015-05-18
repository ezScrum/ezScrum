package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import servletunit.struts.MockStrutsTestCase;

public class AddExistedStoryActionTest extends MockStrutsTestCase {

	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private ProjectObject mProject;
	private final String mActionPath = "/addExistedStory";

	public AddExistedStoryActionTest(String testName) {
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
		
		// create sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		mProject = mCP.getAllProjects().get(0);
		super.setUp();

		// ================ set action info ========================
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
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
		mCS = null;
		mConfig = null;
		mProject = null;
	}

	/**
	 * no story
	 */
	public void testAddExistedStory_1() {
		long sprintId = mCS.getSprintsId().get(0);
		String releaseId = "-1";
		String[] selects = {};

		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("selects", selects);
		addRequestParameter("sprintID", String.valueOf(sprintId));
		addRequestParameter("releaseID", releaseId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		// assert response text
		String expectedResponseText = "";
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
	}

	/**
	 * two stories
	 */
	public void testAddExistedStory_2() {
		int storyCount = 2;
		CreateProductBacklog CPB = new CreateProductBacklog(storyCount, mCP);
		CPB.exe();

		long sprintId = mCS.getSprintsId().get(0);
		String releaseID = "-1";
		String[] selects = { "1", "2" };

		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("selects", selects);
		addRequestParameter("sprintID", String.valueOf(sprintId));
		addRequestParameter("releaseID", releaseID);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		// assert response text
		String actualResponseText = response.getWriterBuffer().toString();
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("");
		assertEquals(expectedResponseText.toString(), actualResponseText);

		// 驗證是否確實有被加入sprint中
		String showSprintBacklog_ActionPath = "/showSprintBacklog2";
		setRequestPathInfo(showSprintBacklog_ActionPath);

		clearRequestParameters();
		response.reset();

		// ================ set request info ========================
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", String.valueOf(sprintId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		// assert response text
		String expectedStoryEstimation = "2";
		String expectedSprintGoal = "TEST_SPRINTGOAL_1";
		String expectedSprintHoursToCommit = "10";
		for (int i = 0; i < mCS.getSprintCount() - 1; i++) {
			expectedResponseText.append("{\"success\":true,\"Total\":2,")
					.append("\"Sprint\":{").append("\"Id\":").append(sprintId)
					.append(",").append("\"Name\":\"Sprint #").append(sprintId)
					.append("\",").append("\"CurrentPoint\":").append(Integer.parseInt(expectedStoryEstimation) * 2).append(",")
					.append("\"LimitedPoint\":").append(Integer.parseInt(expectedSprintHoursToCommit)).append(",")
					.append("\"TaskPoint\":0,")
					.append("\"ReleaseID\":\"Release #None\",")
					.append("\"SprintGoal\":\"").append(expectedSprintGoal)
					.append("\"},").append("\"Stories\":[");

			for (StoryObject story : CPB.getStories()) {
				expectedResponseText
						.append("{\"Id\":").append(story.getId()).append(",")
						.append("\"Link\":\"").append("\",")
						.append("\"Name\":\"").append(story.getName()).append("\",")
						.append("\"Value\":").append(story.getValue()).append(",")
						.append("\"Importance\":").append(story.getImportance()).append(",")
						.append("\"Estimate\":").append(story.getEstimate()).append(",")
						.append("\"Status\":\"new\",")
						.append("\"Notes\":\"").append(story.getNotes()).append("\",")
						.append("\"Tag\":\"\",")
						.append("\"HowToDemo\":\"").append(story.getHowToDemo()).append("\",")
						.append("\"Release\":\"\",")
						.append("\"Sprint\":").append(sprintId).append(",")
						.append("\"Attach\":false,")
						.append("\"AttachFileList\":[]},");
			}
			expectedResponseText.deleteCharAt(expectedResponseText.length() - 1);
			expectedResponseText.append("]}");
		}
		actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
