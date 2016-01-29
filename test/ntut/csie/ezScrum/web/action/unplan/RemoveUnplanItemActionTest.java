package ntut.csie.ezScrum.web.action.unplan;

import java.io.File;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplanItem;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;
import servletunit.struts.MockStrutsTestCase;

public class RemoveUnplanItemActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateUnplanItem mCUI;
	private Configuration mConfig;
	private String mActionPath = "/removeUnplanItem";

	public RemoveUnplanItemActionTest(String testMethod) {
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
		mCP.exeCreateForDb();

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
	}

	// case 1: One sprint with 1 Unplan item
	public void testOneSprintWithOneUnplan() {
		// create one sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// create one unplan
		mCUI = new CreateUnplanItem(1, mCP, mCS);
		mCUI.exe();

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long unplanId = mCUI.getUnplansId().get(0);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?projectName=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(unplanId);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplanId,
		        IssueTypeEnum.TYPE_UNPLAN).size());
	}

	// case 2: One sprint with 2 Unplan item
	public void testOneSprintWithTwoUnplan() {
		// create one sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// create two unplan
		mCUI = new CreateUnplanItem(2, mCP, mCS);
		mCUI.exe();

		// (I) unplan 1

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long unplanId = mCUI.getUnplansId().get(0);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?projectName=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(unplanId);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplanId,
		        IssueTypeEnum.TYPE_UNPLAN).size());

		// (II) unplan 2

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		unplanId = mCUI.getUnplansId().get(1);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?projectName=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(unplanId);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplanId,
		        IssueTypeEnum.TYPE_UNPLAN).size());
	}

	// case 3: Two sprint with 1 Unplan item
	public void testTwoSprintWithOneUnplan() {
		// create two sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		// create one unplan
		mCUI = new CreateUnplanItem(1, mCP, mCS);
		mCUI.exe();

		// (I) Sprint 1 unplan 1

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long unplanId = mCUI.getUnplansId().get(0);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?projectName=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(unplanId);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplanId,
		        IssueTypeEnum.TYPE_UNPLAN).size());

		// (II) sprint 2 unplan 1

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		unplanId = mCUI.getUnplansId().get(1);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?projectName=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(unplanId);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplanId,
		        IssueTypeEnum.TYPE_UNPLAN).size());
	}

	// case 4: Two sprint with 2 Unplan item
	public void testTwoSprintWithTwoUnplan() {
		// create two sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		// create two unplan
		mCUI = new CreateUnplanItem(2, mCP, mCS);
		mCUI.exe();

		// (I) sprint 1, unplan 1

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long unplanId = mCUI.getUnplansId().get(0);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?projectName=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(unplanId);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplanId,
		        IssueTypeEnum.TYPE_UNPLAN).size());

		// (II) sprint 1, unplan 2

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		unplanId = mCUI.getUnplansId().get(1);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?projectName=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(unplanId);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplanId,
		        IssueTypeEnum.TYPE_UNPLAN).size());

		// (III) sprint 2, unplan 1

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		unplanId = mCUI.getUnplansId().get(2);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?projectName=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(unplanId);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplanId,
		        IssueTypeEnum.TYPE_UNPLAN).size());

		// (IV) sprint 2, unplan 2

		// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		unplanId = mCUI.getUnplansId().get(3);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?projectName=" + project.getName());

		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(unplanId);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(unplanId,
		        IssueTypeEnum.TYPE_UNPLAN).size());
	}

	private String genXML(long unplanId) {
		StringBuilder result = new StringBuilder();

		result.append("<DeleteUnplannedItem>");
		result.append("<Result>true</Result>");
		//
		result.append("<UnplannedItem>");
		result.append("<Id>").append(unplanId).append("</Id>");
		result.append("</UnplannedItem>");
		//
		result.append("</DeleteUnplannedItem>");

		return result.toString();
	}
}
