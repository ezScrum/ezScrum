package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class ShowExistedStoryActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private final String mACTION_PATH = "/showExistedStory";
	private IProject mProject;

	public ShowExistedStoryActionTest(String testName) {
		super(testName);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增 Project
		mCP = new CreateProject(1);
		mCP.exeCreate();

		// 新增 Sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();
		
		mProject = mCP.getProjectList().get(0);

		super.setUp();

		// ================ set action info ========================
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mACTION_PATH);

		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
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
	}

	/**
	 * no story
	 */
	public void testShowExistedStory_1() {
		String sprintId = mCS.getSprintIDList().get(0);
		String releaseId = "-1";
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", sprintId);
		addRequestParameter("releaseID", releaseId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		// assert response text
		String expectedResponseText = "<ExistingStories></ExistingStories>";
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
	}

	/**
	 * 至少存在兩個 story 以上
	 */
	public void testShowExistedStory_2() {
		int storycount = 2;
		CreateProductBacklog CPB = new CreateProductBacklog(storycount, mCP);
		CPB.exe();

		String sprintId = mCS.getSprintIDList().get(0);
		String releaseId = "-1";
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", sprintId);
		addRequestParameter("releaseID", releaseId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		// assert response text
		String releaseIdAndSprintId = "None";
		String storyStatus = "new";
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("<ExistingStories>")
		        .append("<Story>")
		        .append("<Id>" + 1 + "</Id>")
		        .append("<Link>/ezScrum/showIssueInformation.do?issueID=" + 1 + "</Link>")
		        .append("<Name>" + CPB.TEST_STORY_NAME + 1 + "</Name>")
		        .append("<Value>" + CPB.TEST_STORY_VALUE + "</Value>")
		        .append("<Importance>" + CPB.TEST_STORY_IMP + "</Importance>")
		        .append("<Estimate>" + CPB.TEST_STORY_EST + "</Estimate>")
		        .append("<Status>" + storyStatus + "</Status>")
		        .append("<Notes>" + CPB.TEST_STORY_NOTES + 1 + "</Notes>")
		        .append("<HowToDemo>" + CPB.TEST_STORY_HOW_TO_DEMO + 1 + "</HowToDemo>")
		        .append("<Release>" + releaseIdAndSprintId + "</Release>")
		        .append("<Sprint>" + releaseIdAndSprintId + "</Sprint>")
		        .append("<Tag>" + "" + "</Tag>")
		        .append("</Story>")
		        .append("<Story>")
		        .append("<Id>" + 2 + "</Id>")
		        .append("<Link>/ezScrum/showIssueInformation.do?issueID=" + 2 + "</Link>")
		        .append("<Name>" + CPB.TEST_STORY_NAME + 2 + "</Name>")
		        .append("<Value>" + CPB.TEST_STORY_VALUE + "</Value>")
		        .append("<Importance>" + CPB.TEST_STORY_IMP + "</Importance>")
		        .append("<Estimate>" + CPB.TEST_STORY_EST + "</Estimate>")
		        .append("<Status>" + storyStatus + "</Status>")
		        .append("<Notes>" + CPB.TEST_STORY_NOTES + 2 + "</Notes>")
		        .append("<HowToDemo>" + CPB.TEST_STORY_HOW_TO_DEMO + 2 + "</HowToDemo>")
		        .append("<Release>" + releaseIdAndSprintId + "</Release>")
		        .append("<Sprint>" + releaseIdAndSprintId + "</Sprint>")
		        .append("<Tag>" + "" + "</Tag>")
		        .append("</Story>")
		        .append("</ExistingStories>");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}

	/**
	 * test sprintId NumberFormatException exception
	 */
	public void testShowExistedStory_3() {
		String sprintId = "SprintIDNumberFormatException";
		String releaseId = "-1";
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", sprintId);
		addRequestParameter("releaseID", releaseId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

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
	 * test releaseID NumberFormatException exception
	 */
	public void testShowExistedStory_4() {
		String sprintId = "";
		String releaseId = "ReleaseNumberFormatException";
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", sprintId);
		addRequestParameter("releaseID", releaseId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

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
	 * test releaseId NumberFormatException exception
	 */
	public void testShowExistedStory_5() {
		String sprintId = "";
		String releaseId = "";
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", sprintId);
		addRequestParameter("releaseID", releaseId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

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
}
