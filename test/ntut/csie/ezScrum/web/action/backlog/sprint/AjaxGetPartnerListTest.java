package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxGetPartnerListTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private ProjectObject mProject;
	private int mSprintId;
	private final String mActionPath = "/AjaxGetPartnerList";

	public AjaxGetPartnerListTest(String testName) {
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
		mCP.exeCreateForDb();

		// create sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		mSprintId = 1;
		mProject = mCP.getAllProjects().get(0);

		super.setUp();
		// ================ set action info ========================
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);

		ini = null;
	}

	protected void tearDoen() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		ini = null;
		mCP = null;
		mCS = null;
		mConfig = null;
		mProject = null;
	}

	public void testGetPartnerList() throws Exception {
		List<Long> sprintsId = mCS.getSprintsId();
		int storyCount = 1;
		int storyEst = 2;
		AddStoryToSprint ASS = new AddStoryToSprint(storyCount, storyEst,
				mSprintId, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASS.exe();

		int taskCount = 1;
		int taskEst = 2;
		AddTaskToStory ATS = new AddTaskToStory(taskCount, taskEst, ASS, mCP);
		ATS.exe();

		CreateProductBacklog CPB = new CreateProductBacklog(storyCount, mCP);
		CPB.exe();

		String issueId = String.valueOf(CPB.getStoryIds().get(0));
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?projectName=" + projectName);
		addRequestParameter("sprintID", String.valueOf(sprintsId.get(0)));
		addRequestParameter("issueID", issueId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ 執行 action ==============================
		actionPerform();

		// ================ assert =============================
		verifyNoActionErrors();
		verifyNoActionMessages();

		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText
				.append("<Partners><Result>success</Result></Partners>");

		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
