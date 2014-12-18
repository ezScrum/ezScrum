package ntut.csie.ezScrum.web.action.backlog;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.test.CreateData.AddSprintToRelease;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AddExistedStoryActionTest extends MockStrutsTestCase {
	private CreateProject mCreateProject;
	private CreateRelease mCreateRelease;
	private CreateProductBacklog mCreateProductBacklog;
	private Configuration mConfig;
	private IProject mProject;

	public AddExistedStoryActionTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.store();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增一測試專案
		mCreateProject = new CreateProject(1);
		mCreateProject.exeCreate();

		// 新增一筆Release Plan
		mCreateRelease = new CreateRelease(1, mCreateProject);
		mCreateRelease.exe();

		mCreateProductBacklog = new CreateProductBacklog(5, mCreateProject);
		mCreateProductBacklog.exe();

		mProject = mCreateProject.getProjectList().get(0);

		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent")); // 設定讀取的 struts-config檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/addExistedStory");

		// ============= release ==============
		ini = null;
	}

	public String getBaseDir() {
		String basedir = System.getProperty("ntut.csie.jcis.resource.BaseDir");

		if (basedir == null) {
			basedir = System.getProperty("user.dir").replace('\\', '/');
		}

		return basedir;
	}

	protected void tearDown() throws IOException, Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除測試檔案
		CopyProject copyProject = new CopyProject(mCreateProject);
		copyProject.exeDelete_Project();

		mConfig.setTestMode(false);
		mConfig.store();

		// ============= release ==============
		ini = null;
		copyProject = null;
		mCreateProject = null;
		mCreateRelease = null;
		mCreateProductBacklog = null;
		mConfig = null;

		super.tearDown();
	}

	public void testExecute() throws Exception {
		// ================== set parameter info ====================
		// 第一筆 Release
		addRequestParameter("releaseID", Integer.toString(mCreateRelease.getReleaseCount()));
		// Sprint 空資料
		addRequestParameter("sprintID", "");

		String[] storyIds = new String[mCreateProductBacklog.getIssueList().size()];
		int count = 0;
		for (IIssue issue : mCreateProductBacklog.getIssueList()) {
			storyIds[count++] = Long.toString(issue.getIssueID());
		}

		addRequestParameter("selects", storyIds);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);

		// ================ set session info ========================
		// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + mProject.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 驗證 ReleasePlan 資料
		ReleasePlanHelper RPHelper = new ReleasePlanHelper(mProject);
		IReleasePlanDesc releasePlan = RPHelper.getReleasePlan(Integer
		        .toString(mCreateRelease.getReleaseCount()));
		assertNotNull(releasePlan);
		assertEquals(mCreateRelease.getDefault_RELEASE_NAME(mCreateRelease.getReleaseCount()),
		        releasePlan.getName());
		assertEquals(
		        mCreateRelease.getDefault_RELEASE_DESC(mCreateRelease.getReleaseCount()),
		        releasePlan.getDescription());

		// 驗證 Story 有被加入 ReleasePlan ID
		ProductBacklogHelper PBHelper = new ProductBacklogHelper(mConfig.getUserSession(), mProject);

		for (IIssue issue : mCreateProductBacklog.getIssueList()) {
			String releaseID = PBHelper.getIssue(issue.getIssueID()).getReleaseID();
			assertEquals(Integer.toString(mCreateRelease.getReleaseCount()), releaseID);
		}

		// ============= release ==============
		mProject = null;
		storyIds = null;
		RPHelper = null;
		PBHelper = null;
	}

	/*
	 * 待修正: AddExistedStoryAction.java #67 沒有對錯誤 ReleaseID作妥善處理 // 代入錯誤的 ReleaseID 參數 public void testexecuteNoReleaseParameter() throws Exception { // ================ set initial data
	 * ======================= IProject project = this.CP.getProjectList().get(0); // ================ set initial data =======================
	 * 
	 * // ================== set parameter info ==================== addRequestParameter("releaseID", "XX"); // 錯誤 ReleaseID addRequestParameter("sprintID", ""); // Sprint 空資料
	 * 
	 * String[] StoryIDs = new String[this.CPB.getIssueList().size()]; int count = 0; for (IIssue issue : this.CPB.getIssueList()) { StoryIDs[count++] = Long.toString(issue.getIssueID()); }
	 * 
	 * addRequestParameter("selects", StoryIDs); // ================== set parameter info ====================
	 * 
	 * // ================ set session info ======================== request.getSession().setAttribute("UserSession", config.getUserSession()); request.getSession().setAttribute("Project", project);
	 * // ================ set session info ======================== request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
	 * 
	 * actionPerform(); // 執行 action verifyNoActionErrors();
	 * 
	 * // 驗證回傳 path verifyForwardPath(null); verifyForward(null);
	 * 
	 * // 驗證 ReleasePlan 資料 ReleasePlanHelper helper = new ReleasePlanHelper((IProject) request .getSession().getAttribute("Project")); IReleasePlanDesc ReleasePlan = helper.getReleasePlan(Integer
	 * .toString(this.CR.getReleaseCount())); assertNotNull(ReleasePlan); assertEquals( this.CR.getDefault_RELEASE_NAME(this.CR.getReleaseCount()), ReleasePlan.getName()); assertEquals(
	 * this.CR.getDefault_RELEASE_DESC(this.CR.getReleaseCount()), ReleasePlan.getDescription());
	 * 
	 * // 驗證 Story 沒有被加入 ReleasePlan ID ProductBacklogHelper PBhelper = new ProductBacklogHelper(project, config.getUserSession());
	 * 
	 * for (IIssue issue : this.CPB.getIssueList()) { String releaseID = PBhelper.getIssue(issue.getIssueID()).getReleaseID(); assertEquals(Integer.toString(-1), releaseID); }
	 * 
	 * // ============= release ============== project = null; StoryIDs = null; helper = null; ReleasePlan = null; PBhelper = null; }
	 */

	/*
	 * 待修正: AddExistedStoryAction.java #48 沒有對空資料sprintID作妥善處理 // 代入錯誤的 Story 參數 public void testexecuteNoStoryParameter() throws Exception { // ================ set initial data
	 * ======================= IProject project = this.CP.getProjectList().get(0); // ================ set initial data =======================
	 * 
	 * // ================== set parameter info ==================== addRequestParameter("releaseID", ""); // Release 空資料 addRequestParameter("sprintID", ""); // Sprint 空資料
	 * 
	 * String[] StoryIDs = null; addRequestParameter("selects", StoryIDs); // ================== set parameter info ====================
	 * 
	 * // ================ set session info ======================== request.getSession().setAttribute("UserSession", config.getUserSession()); request.getSession().setAttribute("Project", project);
	 * // ================ set session info ======================== request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
	 * 
	 * actionPerform(); // 執行 action verifyNoActionErrors();
	 * 
	 * // 驗證回傳 path verifyForwardPath(null); verifyForward(null);
	 * 
	 * // 驗證 ReleasePlan 資料 ReleasePlanHelper helper = new ReleasePlanHelper((IProject) request .getSession().getAttribute("Project")); IReleasePlanDesc ReleasePlan = helper.getReleasePlan(Integer
	 * .toString(this.CR.getReleaseCount())); assertNotNull(ReleasePlan); assertEquals( this.CR.getDefault_RELEASE_NAME(this.CR.getReleaseCount()), ReleasePlan.getName()); assertEquals(
	 * this.CR.getDefault_RELEASE_DESC(this.CR.getReleaseCount()), ReleasePlan.getDescription());
	 * 
	 * // 驗證 Story 沒有被加入 ReleasePlan ID ProductBacklogHelper PBhelper = new ProductBacklogHelper(project, config.getUserSession());
	 * 
	 * for (IIssue issue : this.CPB.getIssueList()) { String releaseID = PBhelper.getIssue(issue.getIssueID()).getReleaseID(); assertEquals(Integer.toString(-1), releaseID); }
	 * 
	 * // ============= release ============== project = null; StoryIDs = null; helper = null; ReleasePlan = null; PBhelper = null; }
	 */

	// -----------------------------------------------------------
	// * 測試當 Story 加入一個有屬於某個 Release 的 Sprint 時，
	// * Story 是否也有自動加入此 Release
	// -------------------------------------------------------------
	public void testAdd_Story_And_Release_Relation() throws Exception {
		// 在每個Release中加入一個Sprint
		AddSprintToRelease addSprint = new AddSprintToRelease(1, mCreateRelease, mCreateProject);
		addSprint.exe();

		// ================== set parameter info ====================
		addRequestParameter("releaseID", ""); // Release 為""代表這是將 Story 加入 Sprint 的動作
		addRequestParameter("sprintID", "1"); // Sprint 空資料

		String[] storyIds = new String[mCreateProductBacklog.getIssueList().size()];
		int count = 0;
		for (IIssue issue : mCreateProductBacklog.getIssueList()) {
			storyIds[count++] = Long.toString(issue.getIssueID());
		}

		addRequestParameter("selects", storyIds);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		// SessionManager 會對 URL 的參數作分析，未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + mProject.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 驗證 ReleasePlan 資料，因為 Sprint 1在 Release 之中，所以 Story 應該也要被加入 Release 中
		ReleasePlanHelper RPHelper = new ReleasePlanHelper(mProject);
		IReleasePlanDesc releasePlan = RPHelper.getReleasePlan(Integer
		        .toString(mCreateRelease.getReleaseCount()));
		assertNotNull(releasePlan);
		assertEquals(
		        mCreateRelease.getDefault_RELEASE_NAME(mCreateRelease.getReleaseCount()),
		        releasePlan.getName());
		assertEquals(
		        mCreateRelease.getDefault_RELEASE_DESC(mCreateRelease.getReleaseCount()),
		        releasePlan.getDescription());

		// 驗證 Story 有被加入 ReleasePlan ID
		ProductBacklogHelper PBhelper = new ProductBacklogHelper(mConfig.getUserSession(), mProject);

		for (IIssue issue : mCreateProductBacklog.getIssueList()) {
			String releaseId = PBhelper.getIssue(issue.getIssueID()).getReleaseID();
			assertEquals(Integer.toString(mCreateRelease.getReleaseCount()), releaseId);
		}

		// ============= release ==============
		mProject = null;
		storyIds = null;
		RPHelper = null;
		PBhelper = null;
	}
}
