package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;

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
	private IProject mIProject;
	private final String mActionPath = "/showExistedStory";

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

		// create Project
		mCP = new CreateProject(1);
		mCP.exeCreate();

		// create Sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();
		
		mIProject = mCP.getProjectList().get(0);

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
		mIProject = null;
		mConfig = null;
	}

	/**
	 * no story
	 */
	public void testShowExistedStory_1() {
		long sprintId = mCS.getSprintsId().get(0);
		String releaseId = "-1";
		
		// ================ set request info ========================
		String projectName = mIProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", String.valueOf(sprintId));
		addRequestParameter("releaseID", releaseId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();

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

		long sprintId = mCS.getSprintsId().get(0);
		String releaseId = "-1";
		// ================ set request info ========================
		String projectName = mIProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", String.valueOf(sprintId));
		addRequestParameter("releaseID", releaseId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		// assert response text
		long releaseIdAndSprintId = -1;
		String storyStatus = "new";
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText
			.append("<ExistingStories>")
				.append("<Story>")
					.append("<Id>1</Id>")
					.append("<Link></Link>")
					.append("<Name>").append("TEST_STORY_" + 1).append("</Name>")
					.append("<Value>").append(50).append("</Value>")
					.append("<Importance>").append(100).append("</Importance>")
					.append("<Estimate>").append(2).append("</Estimate>")
					.append("<Status>").append(storyStatus).append("</Status>")
					.append("<Notes>").append("TEST_STORY_NOTE_" + 1).append("</Notes>")
					.append("<HowToDemo>").append("TEST_STORY_DEMO_" + 1).append("</HowToDemo>")
					.append("<Release></Release>")
					.append("<Sprint>").append(releaseIdAndSprintId).append("</Sprint>")
					.append("<Tag></Tag>")
				.append("</Story>")
				.append("<Story>")
					.append("<Id>2</Id>")
					.append("<Link></Link>")
					.append("<Name>").append("TEST_STORY_" + 2).append("</Name>")
					.append("<Value>").append(50).append("</Value>")
					.append("<Importance>").append(100).append("</Importance>")
					.append("<Estimate>").append(2).append("</Estimate>")
					.append("<Status>").append(storyStatus).append("</Status>")
					.append("<Notes>").append("TEST_STORY_NOTE_" + 2).append("</Notes>")
					.append("<HowToDemo>").append("TEST_STORY_DEMO_" + 2).append("</HowToDemo>")
					.append("<Release></Release>")
					.append("<Sprint>").append(releaseIdAndSprintId).append("</Sprint>")
					.append("<Tag></Tag>")
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
		String projectName = mIProject.getName();
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

		String expectedResponseText = "<ExistingStories></ExistingStories>";
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
		String projectName = mIProject.getName();
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

		String expectedResponseText = "<ExistingStories></ExistingStories>";
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
		String projectName = this.mIProject.getName();
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

		String expectedResponseText = "<ExistingStories></ExistingStories>";
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
	}
}
