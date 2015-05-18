package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddSprintToRelease;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxRemoveStoryActionTest extends MockStrutsTestCase {

	private CreateProject mCP;
	private CreateRelease mCR;
	private Configuration mConfig;

	public AjaxRemoveStoryActionTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// create project
		mCP = new CreateProject(1);
		mCP.exeCreate();

		// create release plan
		mCR = new CreateRelease(1, mCP);
		mCR.exe();

		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/ajaxRemoveSprintBacklog");

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除測試檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		projectManager = null;
		mCP = null;
		mCR = null;
		mConfig = null;
		super.tearDown();
	}

	/**
	 * 測試 drop story 後，story 要從 release 跟 sprint 的關係中切除
	 */
	public void testRemoveStory() throws Exception {
		IProject iProject = mCP.getProjectList().get(0);
		ProjectObject project = new ProjectObject(iProject.getName());
		AddSprintToRelease ASR = new AddSprintToRelease(5, mCR, mCP);
		ASR.exe();

		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(iProject);
		int SprintCount = sprintPlanHelper.getLastSprintId();

		// 5個 Sprint，每個 Sprint 加入1個 Story
		AddStoryToSprint ASS = new AddStoryToSprint(1, 1, SprintCount, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASS.exe();

		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(project);

		// 取出所有 Story
		ArrayList<StoryObject> stories = productBacklogLogic.getStories();
		ArrayList<Long> storyList = new ArrayList<Long>();
		for (StoryObject story : stories) {
			storyList.add(story.getId());
		}
		// 將所有 Story 加入最後一個 Release
		// productBacklogLogic.addReleaseTagToIssue(storyList, lastReleaseId);

		// 增加 delay 時間，因為新增 release 與刪除 release 的秒速過近，會造成系統抓錯資料導致 assert 錯誤
		// (系統抓資料抓到秒，但是新增到 actionPerfome 回來刪掉 release 可能只花費毫秒，因此秒的單位會一樣)
		Thread.sleep(1000);

		// ================ set request info ========================
		addRequestParameter("issueID", "1");
		addRequestParameter("sprintID", "1");

		// ================ set session info ========================
		// 設定 Session 資訊
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", iProject);
		request.setHeader("Referer", "?PID=" + iProject.getName());

		// ================ 執行 action ==============================
		actionPerform();

		// ================ assert =============================
		// Story 自 Sprint 移除時，應該自 Release 中移除
		stories = productBacklogLogic.getStories();
		for (StoryObject story : stories) {
			if (story.getId() == 1) {
				assertEquals(0, story.getSprintId());
			} else {
				// 因為1個 sprint 有1個 story 所以 id 是對應的，因此拿來確認 story 所對應 sprint 有無錯誤
				assertEquals(story.getId(), story.getSprintId());
			}
		}
	}
}
