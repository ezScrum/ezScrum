package ntut.csie.ezScrum.web.action.plan;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.test.CreateData.AddSprintToRelease;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxMoveStorySprintActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateRelease CR;
	private Configuration configuration;

	public AjaxMoveStorySprintActionTest(String testMethod) {
		super(testMethod);
	}
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案

		this.CR = new CreateRelease(1, this.CP);
		this.CR.exe(); // 新增一筆Release Plan

		super.setUp();

		setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent")); // 設定讀取的struts-config檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/moveStorySprint");

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

		// ============= release ==============
		ini = null;
		copyProject = null;
		this.CP = null;
		this.CR = null;
		configuration = null;

		super.tearDown();
	}

	public void testexecute() throws Exception {
		IProject project = CP.getProjectList().get(0);

		// 在Release中加入3個Sprint
		AddSprintToRelease addSprint = new AddSprintToRelease(3, CR, CP);
		addSprint.exe();

		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		int SprintCount = sprintPlanHelper.getLastSprintId();

		// 3個Sprint，每個Sprint加入個2Story
		AddStoryToSprint addStory_Sprint = new AddStoryToSprint(1, 1,
				SprintCount, CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStory_Sprint.exe();
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(configuration.getUserSession(), project);

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
				request.getSession().setAttribute("UserSession", configuration.getUserSession());
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
				request.getSession().setAttribute("UserSession", configuration.getUserSession());
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
