package ntut.csie.ezScrum.web.action.backlog;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.test.CreateData.AddSprintToRelease;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AddExistedStoryActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateRelease CR;
	private CreateProductBacklog CPB;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	
	public AddExistedStoryActionTest(String testMethod) {
        super(testMethod);
    }

	protected void setUp() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe(); // 初始化 SQL

		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案

		this.CR = new CreateRelease(1, this.CP);
		this.CR.exe(); // 新增一筆Release Plan

		this.CPB = new CreateProductBacklog(5, this.CP);
		this.CPB.exe();

		super.setUp();

		setContextDirectory(new File(config.getBaseDirPath() + "/WebContent")); // 設定讀取的 struts-config檔案路徑
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
		InitialSQL ini = new InitialSQL(config);
		ini.exe(); // 初始化 SQL

		CopyProject copyProject = new CopyProject(this.CP);
		copyProject.exeDelete_Project(); // 刪除測試檔案

		// ============= release ==============
		ini = null;
		copyProject = null;
		this.CP = null;
		this.CR = null;
		this.CPB = null;

		super.tearDown();
	}


	// 正常執行
	public void testexecute() throws Exception {
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("releaseID", Integer.toString(this.CR
				.getReleaseCount())); // 第一筆 Release
		addRequestParameter("sprintID", ""); // Sprint 空資料
		
		String[] StoryIDs = new String[this.CPB.getIssueList().size()];
		int count = 0;
		for (IIssue issue : this.CPB.getIssueList()) {
			StoryIDs[count++] = Long.toString(issue.getIssueID());
		}

		addRequestParameter("selects", StoryIDs);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());		
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================    	    	
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 驗證 ReleasePlan 資料
		ReleasePlanHelper helper = new ReleasePlanHelper((IProject) request
				.getSession().getAttribute("Project"));
		IReleasePlanDesc ReleasePlan = helper.getReleasePlan(Integer
				.toString(this.CR.getReleaseCount()));
		assertNotNull(ReleasePlan);
		assertEquals(this.CR.getDefault_RELEASE_NAME(this.CR.getReleaseCount()),
				ReleasePlan.getName());
		assertEquals(
				this.CR.getDefault_RELEASE_DESC(this.CR.getReleaseCount()),
				ReleasePlan.getDescription());

		// 驗證 Story 有被加入 ReleasePlan ID
		ProductBacklogHelper PBhelper = new ProductBacklogHelper(project,
				config.getUserSession());		
		
		for (IIssue issue : this.CPB.getIssueList()) {
			String releaseID = PBhelper.getIssue(issue.getIssueID()).getReleaseID();
			assertEquals(Integer.toString(this.CR.getReleaseCount()), releaseID);
		}

		// ============= release ==============
		project = null;
		StoryIDs = null;
		helper = null;
		PBhelper = null;
	}


/*	
 * 待修正: AddExistedStoryAction.java #67 沒有對錯誤 ReleaseID作妥善處理
	// 代入錯誤的 ReleaseID 參數
	public void testexecuteNoReleaseParameter() throws Exception {
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("releaseID", "XX"); // 錯誤 ReleaseID
		addRequestParameter("sprintID", ""); // Sprint 空資料
		
		String[] StoryIDs = new String[this.CPB.getIssueList().size()];
		int count = 0;
		for (IIssue issue : this.CPB.getIssueList()) {
			StoryIDs[count++] = Long.toString(issue.getIssueID());
		}

		addRequestParameter("selects", StoryIDs);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());	
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		actionPerform(); // 執行 action
		verifyNoActionErrors();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);

		// 驗證 ReleasePlan 資料
		ReleasePlanHelper helper = new ReleasePlanHelper((IProject) request
				.getSession().getAttribute("Project"));
		IReleasePlanDesc ReleasePlan = helper.getReleasePlan(Integer
				.toString(this.CR.getReleaseCount()));
		assertNotNull(ReleasePlan);
		assertEquals(
				this.CR.getDefault_RELEASE_NAME(this.CR.getReleaseCount()),
				ReleasePlan.getName());
		assertEquals(
				this.CR.getDefault_RELEASE_DESC(this.CR.getReleaseCount()),
				ReleasePlan.getDescription());

		// 驗證 Story 沒有被加入 ReleasePlan ID
		ProductBacklogHelper PBhelper = new ProductBacklogHelper(project,
				config.getUserSession());		
		
		for (IIssue issue : this.CPB.getIssueList()) {
			String releaseID = PBhelper.getIssue(issue.getIssueID()).getReleaseID();
			assertEquals(Integer.toString(-1), releaseID);
		}

		// ============= release ==============
		project = null;
		StoryIDs = null;
		helper = null;
		ReleasePlan = null;
		PBhelper = null;
	}

*/
	
	
/*
 * 待修正: AddExistedStoryAction.java #48 沒有對空資料sprintID作妥善處理
	// 代入錯誤的 Story 參數
	public void testexecuteNoStoryParameter() throws Exception {
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("releaseID", ""); // Release 空資料
		addRequestParameter("sprintID", ""); // Sprint 空資料

		String[] StoryIDs = null;
		addRequestParameter("selects", StoryIDs);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());		
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		actionPerform(); // 執行 action
		verifyNoActionErrors();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);

		// 驗證 ReleasePlan 資料
		ReleasePlanHelper helper = new ReleasePlanHelper((IProject) request
				.getSession().getAttribute("Project"));
		IReleasePlanDesc ReleasePlan = helper.getReleasePlan(Integer
				.toString(this.CR.getReleaseCount()));
		assertNotNull(ReleasePlan);
		assertEquals(
				this.CR.getDefault_RELEASE_NAME(this.CR.getReleaseCount()),
				ReleasePlan.getName());
		assertEquals(
				this.CR.getDefault_RELEASE_DESC(this.CR.getReleaseCount()),
				ReleasePlan.getDescription());

		// 驗證 Story 沒有被加入 ReleasePlan ID
		ProductBacklogHelper PBhelper = new ProductBacklogHelper(project,
				config.getUserSession());		
		
		for (IIssue issue : this.CPB.getIssueList()) {
			String releaseID = PBhelper.getIssue(issue.getIssueID()).getReleaseID();
			assertEquals(Integer.toString(-1), releaseID);
		}

		// ============= release ==============
		project = null;
		StoryIDs = null;
		helper = null;
		ReleasePlan = null;
		PBhelper = null;
	}
*/
	
	
//	-----------------------------------------------------------
//	 *	測試當Story加入一個有屬於某個Release的Sprint時，
//	 *	Story是否也有自動加入此Release
//	-------------------------------------------------------------
	public void testAdd_Story_And_Release_Relation() throws Exception {
		// 在每個Release中加入一個Sprint
		AddSprintToRelease addSprint = new AddSprintToRelease(1, CR, CP);
		addSprint.exe();

		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("releaseID",""); //Release 為""代表這是將Story加入Sprint的動作
		addRequestParameter("sprintID", "1"); // Sprint 空資料

		String[] StoryIDs = new String[this.CPB.getIssueList().size()];
		int count = 0;
		for (IIssue issue : this.CPB.getIssueList()) {
			StoryIDs[count++] = Long.toString(issue.getIssueID());
		}

		addRequestParameter("selects", StoryIDs);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());		
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 驗證 ReleasePlan 資料，因為Sprint 1在Release之中，所以Story應該也要被加入Release中
		ReleasePlanHelper helper = new ReleasePlanHelper((IProject) request
				.getSession().getAttribute("Project"));
		IReleasePlanDesc ReleasePlan = helper.getReleasePlan(Integer
				.toString(this.CR.getReleaseCount()));
		assertNotNull(ReleasePlan);
		assertEquals(
				this.CR.getDefault_RELEASE_NAME(this.CR.getReleaseCount()),
				ReleasePlan.getName());
		assertEquals(
				this.CR.getDefault_RELEASE_DESC(this.CR.getReleaseCount()),
				ReleasePlan.getDescription());

		// 驗證 Story 有被加入 ReleasePlan ID
		ProductBacklogHelper PBhelper = new ProductBacklogHelper(project,
				config.getUserSession());		
		
		for (IIssue issue : this.CPB.getIssueList()) {
			String releaseID = PBhelper.getIssue(issue.getIssueID()).getReleaseID();
			assertEquals(Integer.toString(this.CR.getReleaseCount()), releaseID);
		}

		// ============= release ==============
		project = null;
		StoryIDs = null;
		helper = null;
		PBhelper = null;
	}
	

	// get user session -> 統一由 ezScrumInfoConfig.getUserSession()建立
//	private IUserSession CreateUserSession() throws LogonException {
//		
//		IAccount theAccount = null;
//		theAccount = new Account(config.USER_ID);
//		
//		IUserSession theUserSession = new UserSession(theAccount);
//
//		return theUserSession;
//	}
}
