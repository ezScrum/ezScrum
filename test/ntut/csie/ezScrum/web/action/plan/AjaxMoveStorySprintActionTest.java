package ntut.csie.ezScrum.web.action.plan;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddSprintToRelease;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxMoveStorySprintActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateRelease mCR;
	private Configuration mConfig;

	public AjaxMoveStorySprintActionTest(String testMethod) {
		super(testMethod);
	}
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		this.mCP = new CreateProject(1);
		this.mCP.exeCreate(); // 新增一測試專案

		this.mCR = new CreateRelease(1, this.mCP);
		this.mCR.exe(); // 新增一筆Release Plan

		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent")); // 設定讀取的struts-config檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/moveStorySprint");

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		this.mCP = null;
		this.mCR = null;
		mConfig = null;

		super.tearDown();
	}

	public void testExecute() throws Exception {
		IProject project = mCP.getProjectList().get(0);

		// 在Release中加入3個Sprint
		AddSprintToRelease ASTR = new AddSprintToRelease(3, mCR, mCP);
		ASTR.exe();

		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		int SprintCount = sprintPlanHelper.getLastSprintId();

		// 3個Sprint，每個Sprint加入個2Story
		AddStoryToSprint ASTS = new AddStoryToSprint(1, 1,
				SprintCount, mCP, CreateProductBacklog.TYPE_ESTIMATION);
		ASTS.exe();
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(mConfig.getUserSession(), project);

		// 取出所有Story
		IStory[] stories = productBacklogLogic.getStories();
		ArrayList<Long> storyList = new ArrayList<Long>();
		for (IStory story : stories) {
			storyList.add(story.getIssueID());
		}
		
		/*-----------------------------------------------------------
		 *  從release移到sprint
		 *  "issueID"
		 *  "sprintID"
		-------------------------------------------------------------*/
		for (IStory story:stories) {
			/*-----------------------------------------------------------
			*	把所有Story加入Sprint 1
			-------------------------------------------------------------*/
			if (!(story.getSprintID().equals("1"))) {
				clearRequestParameters();
				request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
				addRequestParameter("issueID", String.valueOf(story.getIssueID()));
				addRequestParameter("moveID", "1");
				addRequestParameter("type", "sprint");
				// 設定Session資訊
				request.getSession().setAttribute("UserSession", mConfig.getUserSession());
				request.getSession().setAttribute("Project", project);
				actionPerform();
			}
		}
		
		/*-----------------------------------------------------------
		*	殘酷的驗收時間
		-------------------------------------------------------------*/
		stories = productBacklogLogic.getStories();
		for (IStory story:stories) {
			assertEquals("1", story.getSprintID());
		}
		
		/*-----------------------------------------------------------
		 * 從sprint移到release
		 *  "issueID"
		 *  "releaseID"
		-------------------------------------------------------------*/
		for (IStory story:stories) {
			/*-----------------------------------------------------------
			*	把所有Story加入Sprint 1
			-------------------------------------------------------------*/
			if (!(story.getReleaseID().equals("1"))) {
				clearRequestParameters();
				request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
				addRequestParameter("issueID", String.valueOf(story.getIssueID()));
				addRequestParameter("moveID", "1");
				addRequestParameter("type", "release");
				// 設定Session資訊
				request.getSession().setAttribute("UserSession", mConfig.getUserSession());
				request.getSession().setAttribute("Project", project);
				actionPerform();
			}
		}
		
		/*-----------------------------------------------------------
		*	殘酷的驗收時間
		-------------------------------------------------------------*/
		stories = productBacklogLogic.getStories();
		for (IStory story:stories) {
			assertEquals("1", story.getReleaseID());
		}
	}
}
