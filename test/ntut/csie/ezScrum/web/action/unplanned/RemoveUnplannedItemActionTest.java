package ntut.csie.ezScrum.web.action.unplanned;

import java.io.File;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplannedItem;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import servletunit.struts.MockStrutsTestCase;

public class RemoveUnplannedItemActionTest extends MockStrutsTestCase {

	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateUnplannedItem mCUI;
	private Configuration mConfig;
	private String mActionPath = "/removeUnplannedItem";

	public RemoveUnplannedItemActionTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// create one project
		mCP = new CreateProject(1);
		mCP.exeCreate();

		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent")); // 設定讀取的
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mCUI = null;
		mConfig = null;
		projectManager = null;
	}

	// case 1: One sprint with 1 Unplanned item
	public void testOneSprintWithOneUnplanned() {
		// create one sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// create one unplanned
		mCUI = new CreateUnplannedItem(1, mCP, mCS);
		mCUI.exe();

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long unplannedId = mCUI.getUnplannedsId().get(0);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(unplannedId);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplannedId,
		        IssueTypeEnum.TYPE_UNPLANNED).size());
	}

	// case 2: One sprint with 2 Unplanned item
	public void testOneSprintWithTwoUnplanned() {
		// create one sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// create two unplanned
		mCUI = new CreateUnplannedItem(2, mCP, mCS);
		mCUI.exe();

		// (I) unplanned 1

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long unplannedId = mCUI.getUnplannedsId().get(0);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(unplannedId);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplannedId,
		        IssueTypeEnum.TYPE_UNPLANNED).size());

		// (II) unplanned 2

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		unplannedId = mCUI.getUnplannedsId().get(1);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(unplannedId);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplannedId,
		        IssueTypeEnum.TYPE_UNPLANNED).size());
	}

	// case 3: Two sprint with 1 Unplanned item
	public void testTwoSprintWithOneUnplanned() {
		// create two sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		// create one unplanned
		mCUI = new CreateUnplannedItem(1, mCP, mCS);
		mCUI.exe();

		// (I) Sprint 1 unplanned 1

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long unplannedId = mCUI.getUnplannedsId().get(0);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(unplannedId);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplannedId,
		        IssueTypeEnum.TYPE_UNPLANNED).size());

		// (II) sprint 2 unplanned 1

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		unplannedId = mCUI.getUnplannedsId().get(1);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(unplannedId);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplannedId,
		        IssueTypeEnum.TYPE_UNPLANNED).size());
	}

	// case 4: Two sprint with 2 Unplanned item
	public void testTwoSprintWithTwoUnplanned() {
		// create two sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		// create two unplanned
		mCUI = new CreateUnplannedItem(2, mCP, mCS);
		mCUI.exe();

		// (I) sprint 1, unplanned 1

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long unplannedId = mCUI.getUnplannedsId().get(0);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(unplannedId);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplannedId,
		        IssueTypeEnum.TYPE_UNPLANNED).size());

		// (II) sprint 1, unplanned 2

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		unplannedId = mCUI.getUnplannedsId().get(1);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(unplannedId);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplannedId,
		        IssueTypeEnum.TYPE_UNPLANNED).size());

		// (III) sprint 2, unplanned 1

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		unplannedId = mCUI.getUnplannedsId().get(2);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(unplannedId);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplannedId,
		        IssueTypeEnum.TYPE_UNPLANNED).size());

		// (IV) sprint 2, unplanned 2

		// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		unplannedId = mCUI.getUnplannedsId().get(3);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(unplannedId);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplannedId,
		        IssueTypeEnum.TYPE_UNPLANNED).size());
	}

	private String genXML(long unplannedId) {
		StringBuilder result = new StringBuilder();

		result.append("<DeleteUnplannedItem>");
		result.append("<Result>true</Result>");
		//
		result.append("<UnplannedItem>");
		result.append("<Id>").append(unplannedId).append("</Id>");
		result.append("</UnplannedItem>");
		//
		result.append("</DeleteUnplannedItem>");

		return result.toString();
	}
}
