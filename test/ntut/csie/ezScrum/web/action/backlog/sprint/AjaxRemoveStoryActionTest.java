package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.test.CreateData.AddSprintToRelease;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxRemoveStoryActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateRelease CR;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();

	public AjaxRemoveStoryActionTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe(); // 初始化 SQL

		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案

		this.CR = new CreateRelease(1, this.CP);
		this.CR.exe(); // 新增一筆Release Plan

		super.setUp();

		setContextDirectory(new File(config.getBaseDirPath() + "/WebContent")); // 設定讀取的
		// struts-config
		// 檔案路徑

		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/ajaxRemoveSprintBacklog");

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe(); // 初始化 SQL

		CopyProject copyProject = new CopyProject(this.CP);
		copyProject.exeDelete_Project(); // 刪除測試檔案

		// ============= release ==============
		ini = null;
		copyProject = null;
		this.CP = null;
		this.CR = null;

		super.tearDown();
	}

	/**
	 * 測試drop story後，story要從release跟sprint的關係中切除
	 */
	public void testRemoveStory() throws Exception {
		IProject project = CP.getProjectList().get(0);
		String lastReleaseID = String.valueOf(CR.getReleaseCount());

		// 在Release中加入5個Sprint
		AddSprintToRelease addSprint = new AddSprintToRelease(5, CR, CP);
		addSprint.exe();

		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		int SprintCount = sprintPlanHelper.getLastSprintId();

		// 5個Sprint，每個Sprint加入1個Story
		AddStoryToSprint addStory_Sprint = new AddStoryToSprint(1, 1, SprintCount, CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStory_Sprint.exe();

		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(config.getUserSession(), project);

		// 取出所有Story
		IStory[] stories = productBacklogLogic.getStories();
		ArrayList<Long> storyList = new ArrayList<Long>();
		for (IStory story : stories) {
			storyList.add(story.getIssueID());
		}
		// 將所有Story加入最後一個Release
		productBacklogLogic.addReleaseTagToIssue(storyList, lastReleaseID);
		
		// 增加delay時間，因為新增release與刪除release的秒速過近，會造成系統抓錯資料導致assert錯誤
		// (系統抓資料抓到秒，但是新增到actionPerfome回來刪掉release可能只花費毫秒，因此秒的單位會一樣)
		Thread.sleep(1000);		
		// ================ set request info ========================
		addRequestParameter("issueID", "1");
		addRequestParameter("sprintID", "1");
		
		// ================ set session info ========================
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", config.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		// ================ 執行 action ==============================
		actionPerform();
		
		// ================ assert =============================
		// Story自Sprint移除時，應該自Release中移除
		stories = productBacklogLogic.getStories();
		for (IStory story : stories) {
			if (story.getIssueID() == 1) {
				assertEquals("0", story.getSprintID());
				assertEquals("0", story.getReleaseID());
			} else {
				// 因為1個sprint有1個story所以ID是對應的，因此拿來確認story所對應srpint有無錯誤
				assertEquals(String.valueOf(story.getIssueID()), story.getSprintID());
				assertEquals(lastReleaseID, story.getReleaseID());
			}
		}
	}
}
