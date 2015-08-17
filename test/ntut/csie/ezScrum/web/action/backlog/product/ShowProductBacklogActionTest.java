package ntut.csie.ezScrum.web.action.backlog.product;

import java.io.File;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.ChangeIssueStatus;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import servletunit.struts.MockStrutsTestCase;

public class ShowProductBacklogActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private Configuration mConfig;
	private final String mActionPath = "/showProductBacklog2";
	private ProjectObject mProject;

	public ShowProductBacklogActionTest(String testName) {
		super(testName);
	}

	protected void setUp() throws Exception {
		super.setUp();
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增一測試專案
		mCP = new CreateProject(1);
		mCP.exeCreate();
		mProject = mCP.getAllProjects().get(0);

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
		mConfig = null;
	}

	public void testShowProductBacklogAction_NoStory() {
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);

		addRequestParameter("FilterType", "");

		// ================ set session info ========================
		request.getSession().setAttribute(projectName, mProject);
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		// assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{").append("\"success\":true,")
				.append("\"Total\":0,").append("\"Stories\":[]").append("}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}

	public void testShowProductBacklogAction_Stories() {
		int storyCount = 2;
		CreateProductBacklog CPB = new CreateProductBacklog(storyCount, mCP);
		CPB.exe();
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);

		addRequestParameter("FilterType", "");

		// ================ set session info ========================
		request.getSession().setAttribute(projectName, mProject);
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		// assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"success\":true,\"Total\":2,").append(
				"\"Stories\":[");
		// 取2次Story資料
		for (int i = 0; i < storyCount; i++) {
			expectedResponseText
					.append("{\"Id\":").append(CPB.getStories().get(i).getId()).append(",")
					.append("\"Type\":\"Story\",")
					.append("\"Name\":\"").append(CPB.getStories().get(i).getName()).append("\",")
					.append("\"Value\":").append(CPB.getStories().get(i).getValue()).append(",")
					.append("\"Estimate\":").append(CPB.getStories().get(i).getEstimate()).append(",")
					.append("\"Importance\":").append(CPB.getStories().get(i).getImportance()).append(",")
					.append("\"Tag\":\"\",")
					.append("\"Status\":\"new\",")
					.append("\"Notes\":\"").append(CPB.getStories().get(i).getNotes()).append("\",")
					.append("\"HowToDemo\":\"").append(CPB.getStories().get(i).getHowToDemo()).append("\",")
					.append("\"Link\":\"\",")
					.append("\"Release\":\"\",")
					.append("\"Sprint\":\"None\",")
					.append("\"FilterType\":\"DETAIL\",")
					.append("\"Attach\":false,")
					.append("\"AttachFileList\":[]},");
		}
		expectedResponseText.deleteCharAt(expectedResponseText.length() - 1);
		expectedResponseText.append("]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}

	/**
	 * filter type = BACKLOG
	 */
	public void testShowProductBacklog_Backlog() {
		CreateProductBacklog CPB = new CreateProductBacklog();
		CPB.createBacklogStory(mProject, 0, 0, 0); // backlog
		CPB.createBacklogStory(mProject, 0, 1, 0); // backlog
		CPB.createBacklogStory(mProject, 1, 2, 3); // detail
		ArrayList<StoryObject> stories = CPB.getStories();
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);

		String filterType = "BACKLOG";
		addRequestParameter("FilterType", filterType);

		// ================ set session info ========================
		request.getSession().setAttribute(projectName, mProject);
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		// assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"success\":true,\"Total\":2,").append(
				"\"Stories\":[");
		// 取2次Story資料
		for (int i = 1; i < 3; i++) {
			StoryObject story = stories.get(i - 1);
			expectedResponseText
					.append("{\"Id\":").append(story.getId()).append(",")
					.append("\"Type\":\"Story\",")
					.append("\"Name\":\"").append(story.getName()).append("\",")
					.append("\"Value\":").append(story.getValue()).append(",")
					.append("\"Estimate\":").append(story.getEstimate()).append(",")
					.append("\"Importance\":").append(story.getImportance()).append(",")
					.append("\"Tag\":\"\",")
					.append("\"Status\":\"new\",")
					.append("\"Notes\":\"").append(story.getNotes()).append("\",")
					.append("\"HowToDemo\":\"").append(story.getHowToDemo()).append("\",")
					.append("\"Link\":\"\",")
					.append("\"Release\":\"\",")
					.append("\"Sprint\":\"None\",")
					.append("\"FilterType\":\"BACKLOG\",")
					.append("\"Attach\":false,")
					.append("\"AttachFileList\":[]},");
		}
		expectedResponseText.deleteCharAt(expectedResponseText.length() - 1);
		expectedResponseText.append("]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}

	/**
	 * filter type = DONE
	 * 
	 * @throws Exception
	 */
	public void testShowProductBacklog_Done() throws Exception {
		CreateProductBacklog CPB = new CreateProductBacklog();
		CPB.createBacklogStory(mProject, 0, 0, 0); // backlog
		CPB.createBacklogStory(mProject, 0, 1, 0); // backlog
		CPB.createBacklogStory(mProject, 1, 2, 3); // detail

		int sprintCount = 1;
		int storyCount = 1;
		int storyEstValue = 8;
		CreateSprint CS = new CreateSprint(sprintCount, mCP);
		CS.exe();
		AddStoryToSprint ASTS = new AddStoryToSprint(storyCount, storyEstValue,
				CS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASTS.exe();

		ArrayList<StoryObject> stories = ASTS.getStories();
		ChangeIssueStatus CIS = new ChangeIssueStatus(stories, mCP);
		CIS.exeCloseStories();

		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);

		String filterType = "DONE";
		addRequestParameter("FilterType", filterType);
		String expectedStoryName = ASTS.getStories().get(0).getName();
		int expectedStoryImportance = ASTS.getStories().get(0).getImportance();
		int expectedStoryEstimate = ASTS.getStories().get(0).getEstimate();
		int expectedStoryValue = ASTS.getStories().get(0).getValue();
		String expectedStoryHoewToDemo = ASTS.getStories().get(0)
				.getHowToDemo();
		String expectedStoryNote = ASTS.getStories().get(0).getNotes();
		long storyId = ASTS.getStories().get(0).getId();
		long SprintId = CS.getSprintsId().get(0);
		// ================ set session info ========================
		request.getSession().setAttribute(projectName, mProject);
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		// assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"success\":true,")
				.append("\"Total\":1,").append("\"Stories\":[{")
				.append("\"Id\":").append(storyId).append(",")
				.append("\"Type\":\"Story\",")
				.append("\"Name\":\"").append(expectedStoryName).append("\",")
				.append("\"Value\":").append(expectedStoryValue)
				.append(",").append("\"Estimate\":")
				.append(expectedStoryEstimate).append(",")
				.append("\"Importance\":").append(expectedStoryImportance)
				.append(",").append("\"Tag\":\"\",")
				.append("\"Status\":\"closed\",").append("\"Notes\":\"")
				.append(expectedStoryNote).append("\",")
				.append("\"HowToDemo\":\"").append(expectedStoryHoewToDemo)
				.append("\",")
				.append("\"Link\":\"\",").append("\"Release\":\"\",")
				.append("\"Sprint\":").append(SprintId).append(",")
				.append("\"FilterType\":\"DONE\",").append("\"Attach\":false,")
				.append("\"AttachFileList\":[]").append("}]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}

	/**
	 * Filter Type = DETAIL
	 */

	public void testShowProductBacklog_Detail() throws Exception {
		CreateProductBacklog CPB = new CreateProductBacklog();
		CPB.createBacklogStory(mProject, 0, 0, 0); // backlog
		CPB.createBacklogStory(mProject, 0, 1, 0); // backlog
		CPB.createBacklogStory(mProject, 1, 2, 3); // detail

		int sprintCount = 1;
		int storyCount = 1;
		int storyEstValue = 8;
		CreateSprint CS = new CreateSprint(sprintCount, mCP);
		CS.exe();
		AddStoryToSprint ASTS = new AddStoryToSprint(storyCount, storyEstValue,
				CS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASTS.exe();

		ArrayList<StoryObject> stories = ASTS.getStories();
		ChangeIssueStatus CIS = new ChangeIssueStatus(stories, mCP);
		CIS.exeCloseStories();
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);

		String filterType = "DETAIL";
		addRequestParameter("FilterType", filterType);
		String expectedStoryName = CPB.getStories().get(2).getName();
		int expectedStoryImportance = CPB.getStories().get(2).getImportance();
		int expectedStoryEstimation = CPB.getStories().get(2).getEstimate();
		int expectedStoryValue = CPB.getStories().get(2).getValue();
		String expectedStoryHoewToDemo = CPB.getStories().get(2).getHowToDemo();
		String expectedStoryNote = CPB.getStories().get(2).getNotes();
		long storyId = CPB.getStories().get(2).getId();
		// ================ set session info ========================
		request.getSession().setAttribute(projectName, mProject);
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		// assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"success\":true,")
				.append("\"Total\":1,").append("\"Stories\":[{")
				.append("\"Id\":").append(storyId).append(",")
				.append("\"Type\":\"Story\",")
				.append("\"Name\":\"").append(expectedStoryName).append("\",")
				.append("\"Value\":").append(expectedStoryValue)
				.append(",").append("\"Estimate\":")
				.append(expectedStoryEstimation).append(",")
				.append("\"Importance\":").append(expectedStoryImportance)
				.append(",").append("\"Tag\":\"\",")
				.append("\"Status\":\"new\",").append("\"Notes\":\"")
				.append(expectedStoryNote).append("\",")
				.append("\"HowToDemo\":\"").append(expectedStoryHoewToDemo)
				.append("\",")
				.append("\"Link\":\"\",").append("\"Release\":\"\",")
				.append("\"Sprint\":\"None\",")
				.append("\"FilterType\":\"DETAIL\",")
				.append("\"Attach\":false,").append("\"AttachFileList\":[]")
				.append("}]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
 	}
}
