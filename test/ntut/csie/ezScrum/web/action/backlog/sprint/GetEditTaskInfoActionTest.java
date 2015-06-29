package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class GetEditTaskInfoActionTest extends MockStrutsTestCase {

	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private final String mActionPath = "/getEditTaskInfo";
	private IProject mIProject;

	public GetEditTaskInfoActionTest(String testName) {
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
		mIProject = null;
	}

	public void testGetEditTaskInfo() throws Exception {
		ArrayList<Long> sprintIds = mCS.getSprintsId();
		long sprintId = sprintIds.get(0);
		int storyCount = 1;
		int storyEst = 2;
		AddStoryToSprint ASS = new AddStoryToSprint(storyCount, storyEst,
				sprintIds.size(), mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASS.exe();

		int taskCount = 1;
		int taskEst = 2;
		AddTaskToStory ATS = new AddTaskToStory(taskCount, taskEst, ASS, mCP);
		ATS.exe();

		CreateProductBacklog CPB = new CreateProductBacklog(storyCount, mCP);
		CPB.exe();
		String taskId = String.valueOf(ATS.getTasksId().get(0));

		// ================ set request info ========================
		String projectName = mIProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", String.valueOf(sprintIds.get(0)));
		addRequestParameter("issueID", taskId);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession",
				mConfig.getUserSession());

		// ================ 執行 action ==============================
		actionPerform();

		// ================ assert =============================
		verifyNoActionErrors();
		verifyNoActionMessages();

		TaskObject task = ATS.getTasks().get(0);
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText
			.append("<EditTask>")
				.append("<Task>")
					.append("<Id>").append(taskId).append("</Id>")
					.append("<Name>").append(task.getName()).append("</Name>")
					.append("<Estimate>").append(task.getEstimate()).append("</Estimate>")
					.append("<Actual>").append(task.getActual()).append("</Actual>")
					.append("<Handler></Handler>")
					.append("<Remains>").append(task.getRemains()).append("</Remains>")
					.append("<Partners></Partners>")
					.append("<Notes>").append(task.getNotes()).append("</Notes>")
				.append("</Task>")
			.append("</EditTask>");

		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
