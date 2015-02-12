package ntut.csie.ezScrum.web.action.unplanned;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplannedItem;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class EditUnplannedItemActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateUnplannedItem mCUI;
	private Configuration mConfig;
	private IProject mProject;

	private String mPREFIX = "TEST_UNPLANNED_EDIT_";
	private String mUPDATE_NAME = "NAME_";
	private String mUPDATE_ESTIMATION = "99";
	private String mUPDATE_HOUR = "9";
	private String mUPDATE_PARTNER = "PARTNER_";
	private String mUPDATE_NOTE = "NOTE_";
	private String mUPDATE_STATUS[] = {"new", "assigned", "closed"};

	private String mActionPath = "/editUnplannedItem";

	public EditUnplannedItemActionTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增 Project
		mCP = new CreateProject(1);
		mCP.exeCreate();

		mProject = mCP.getProjectList().get(0);

		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mCUI = null;
		mConfig = null;
		mProject = null;
	}

	// case 1: One sprint(s) with One Unplanned item(s)
	// 將 name, partners, status, estimation, actual hour, notes 更新
	public void testEditOneSprint1ui() throws Exception {
		// 新增一個 Sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();
		mCUI = new CreateUnplannedItem(1, mCP, mCS);
		mCUI.exe();

		// 若新增跟編輯 unplanned item 的秒數一致，會造成從 history 抓取最新值 estimation 與 note 錯誤
		Thread.sleep(1000);

		// ================ set initial data =======================
		String sprintId = "1";
		String issueId = "1";
		String name = mPREFIX + mUPDATE_NAME + issueId;
		String handler = "";
		String partners = mPREFIX + mUPDATE_PARTNER + issueId;
		String estimate = mUPDATE_ESTIMATION;
		String actualHour = mUPDATE_HOUR;
		String notes = mPREFIX + mUPDATE_NOTE + issueId;
		String specificTime = "";
		String status = mUPDATE_STATUS[1];

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueId);
		addRequestParameter("Name", name);
		addRequestParameter("Status", status);
		addRequestParameter("SprintID", "Sprint #" + sprintId);
		addRequestParameter("Estimate", estimate);
		addRequestParameter("Handler", handler);
		addRequestParameter("Partners", partners);
		addRequestParameter("ActualHour", actualHour);
		addRequestParameter("Notes", notes);
		addRequestParameter("SpecificTime", specificTime);

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

		// 比對資料是否正確
		String[] a = {sprintId, issueId, name, handler, partners, estimate, notes, actualHour, status};
		String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 2: One sprint(s) with Two Unplanned item(s)
	// 測試先修改 UI2 再修改 UI1
	public void testEditOneSprint2ui() throws Exception {
		// 新增一個 Sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();
		mCUI = new CreateUnplannedItem(2, mCP, mCS);
		mCUI.exe();

		// 若新增跟編輯 unplanned item 的秒數一致，會造成從 history 抓取最新值 estimation 與 note 錯誤
		Thread.sleep(1000);

		// (I) test update UI 2

		// ================ set initial data =======================
		String sprintId = "1";
		String issueId = "2";
		String name = mPREFIX + mUPDATE_NAME + issueId;
		String handler = "";
		String partners = mPREFIX + mUPDATE_PARTNER + issueId;
		String estimate = mUPDATE_ESTIMATION;
		String actualHour = mUPDATE_HOUR;
		String notes = mPREFIX + mUPDATE_NOTE + issueId;
		String specificTime = "";
		String status = mUPDATE_STATUS[1];

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueId);
		addRequestParameter("Name", name);
		addRequestParameter("Status", status);
		addRequestParameter("SprintID", "Sprint #" + sprintId);
		addRequestParameter("Estimate", estimate);
		addRequestParameter("Handler", handler);
		addRequestParameter("Partners", partners);
		addRequestParameter("ActualHour", actualHour);
		addRequestParameter("Notes", notes);
		addRequestParameter("SpecificTime", specificTime);

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

		// 比對資料是否正確
		String[] a = {sprintId, issueId, name, handler, partners, estimate, notes, actualHour, status};
		String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]);
		assertEquals(expected, response.getWriterBuffer().toString());

		// (II) test update UI 1

		// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		sprintId = "1";
		issueId = "2";
		name = mPREFIX + mUPDATE_NAME + issueId;
		handler = "";
		partners = mPREFIX + mUPDATE_PARTNER + issueId;
		estimate = mUPDATE_ESTIMATION;
		actualHour = mUPDATE_HOUR;
		notes = mPREFIX + mUPDATE_NOTE + issueId;
		specificTime = "";
		status = mUPDATE_STATUS[2];

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueId);
		addRequestParameter("Name", name);
		addRequestParameter("Status", status);
		addRequestParameter("SprintID", "Sprint #" + sprintId);
		addRequestParameter("Estimate", estimate);
		addRequestParameter("Handler", handler);
		addRequestParameter("Partners", partners);
		addRequestParameter("ActualHour", actualHour);
		addRequestParameter("Notes", notes);
		addRequestParameter("SpecificTime", specificTime);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		// ================ set session info ========================
		// SessionManager 會對 URL 的參數作分析，未帶入此參數無法存入 session
		request.setHeader("Referer", "?PID=" + mProject.getName());
		// 執行 action
		actionPerform();
		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String[] b = {sprintId, issueId, name, handler, partners, estimate, notes, actualHour, status};
		expected = genXML(b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8]);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 3: Two sprint(s) with One Unplanned item(s)
	// 測試先修改 sprint2 再修改 sprint1
	public void testEditTwoSprint1ui() throws Exception {
		// 新增一個 Sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();
		mCUI = new CreateUnplannedItem(1, mCP, mCS);
		mCUI.exe();

		// 若新增跟編輯 unplanned item 的秒數一致，會造成從 history 抓取最新值 estimation 與 note 錯誤
		Thread.sleep(1000);

		// (I) test update sprint2 UI

		// ================ set initial data =======================
		String sprintId = "2";
		String issueId = "2";
		String name = mPREFIX + mUPDATE_NAME + issueId;
		String handler = "";
		String partners = mPREFIX + mUPDATE_PARTNER + issueId;
		String estimate = mUPDATE_ESTIMATION;
		String actualHour = mUPDATE_HOUR;
		String notes = mPREFIX + mUPDATE_NOTE + issueId;
		String specificTime = "";
		String status = mUPDATE_STATUS[1];

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueId);
		addRequestParameter("Name", name);
		addRequestParameter("Status", status);
		addRequestParameter("SprintID", "Sprint #" + sprintId);
		addRequestParameter("Estimate", estimate);
		addRequestParameter("Handler", handler);
		addRequestParameter("Partners", partners);
		addRequestParameter("ActualHour", actualHour);
		addRequestParameter("Notes", notes);
		addRequestParameter("SpecificTime", specificTime);

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

		// 比對資料是否正確
		String[] a = {sprintId, issueId, name, handler, partners, estimate, notes, actualHour, status};
		String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]);
		assertEquals(expected, response.getWriterBuffer().toString());

		// (II) test update sprint1 UI

		// 執行下一次的 action 必須做此動作，否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		sprintId = "1";
		issueId = "1";
		name = mPREFIX + mUPDATE_NAME + issueId;
		handler = "";
		partners = mPREFIX + mUPDATE_PARTNER + issueId;
		estimate = mUPDATE_ESTIMATION;
		actualHour = mUPDATE_HOUR;
		notes = mPREFIX + mUPDATE_NOTE + issueId;
		specificTime = "";
		status = mUPDATE_STATUS[2];

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueId);
		addRequestParameter("Name", name);
		addRequestParameter("Status", status);
		addRequestParameter("SprintID", "Sprint #" + sprintId);
		addRequestParameter("Estimate", estimate);
		addRequestParameter("Handler", handler);
		addRequestParameter("Partners", partners);
		addRequestParameter("ActualHour", actualHour);
		addRequestParameter("Notes", notes);
		addRequestParameter("SpecificTime", specificTime);

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

		// 比對資料是否正確
		String[] b = {sprintId, issueId, name, handler, partners, estimate, notes, actualHour, status};
		expected = genXML(b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8]);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 4: Two sprint(s) with Two Unplanned item(s)
	// 測試先修改 sprint1 UI1.UI2 再修改 sprint2 UI2.UI1
	public void testEditTwoSprint2ui() throws Exception {
		// 新增一個 Sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();
		mCUI = new CreateUnplannedItem(2, mCP, mCS);
		mCUI.exe();

		// 若新增跟編輯 unplanned item 的秒數一致，會造成從 history 抓取最新值 estimation 與 note 錯誤
		Thread.sleep(1000);

		// (I) test update sprint1 UI1

		// ================ set initial data =======================
		String sprintId = "1";
		String issueId = "1";
		String name = mPREFIX + mUPDATE_NAME + issueId;
		String handler = "";
		String partners = mPREFIX + mUPDATE_PARTNER + issueId;
		String estimate = mUPDATE_ESTIMATION;
		String actualHour = mUPDATE_HOUR;
		String notes = mPREFIX + mUPDATE_NOTE + issueId;
		String specificTime = "";
		String status = mUPDATE_STATUS[1];

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueId);
		addRequestParameter("Name", name);
		addRequestParameter("Status", status);
		addRequestParameter("SprintID", "Sprint #" + sprintId);
		addRequestParameter("Estimate", estimate);
		addRequestParameter("Handler", handler);
		addRequestParameter("Partners", partners);
		addRequestParameter("ActualHour", actualHour);
		addRequestParameter("Notes", notes);
		addRequestParameter("SpecificTime", specificTime);

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

		// 比對資料是否正確
		String[] a = {sprintId, issueId, name, handler, partners, estimate, notes, actualHour, status};
		String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]);
		assertEquals(expected, response.getWriterBuffer().toString());

		// (II) test update sprint1 UI2

		// 執行下一次的 action 必須做此動作，否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		sprintId = "1";
		issueId = "2";
		name = mPREFIX + mUPDATE_NAME + issueId;
		handler = "";
		partners = mPREFIX + mUPDATE_PARTNER + issueId;
		estimate = mUPDATE_ESTIMATION;
		actualHour = mUPDATE_HOUR;
		notes = mPREFIX + mUPDATE_NOTE + issueId;
		specificTime = "";
		status = mUPDATE_STATUS[2];

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueId);
		addRequestParameter("Name", name);
		addRequestParameter("Status", status);
		addRequestParameter("SprintID", "Sprint #" + sprintId);
		addRequestParameter("Estimate", estimate);
		addRequestParameter("Handler", handler);
		addRequestParameter("Partners", partners);
		addRequestParameter("ActualHour", actualHour);
		addRequestParameter("Notes", notes);
		addRequestParameter("SpecificTime", specificTime);

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

		// 比對資料是否正確
		String[] b = {sprintId, issueId, name, handler, partners, estimate, notes, actualHour, status};
		expected = genXML(b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8]);
		assertEquals(expected, response.getWriterBuffer().toString());

		// (III) test update sprint2 UI2

		// 執行下一次的 action 必須做此動作，否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		sprintId = "2";
		issueId = "4";
		name = mPREFIX + mUPDATE_NAME + issueId;
		handler = "";
		partners = mPREFIX + mUPDATE_PARTNER + issueId;
		estimate = mUPDATE_ESTIMATION;
		actualHour = mUPDATE_HOUR;
		notes = mPREFIX + mUPDATE_NOTE + issueId;
		specificTime = "";
		status = mUPDATE_STATUS[2];

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueId);
		addRequestParameter("Name", name);
		addRequestParameter("Status", status);
		addRequestParameter("SprintID", "Sprint #" + sprintId);
		addRequestParameter("Estimate", estimate);
		addRequestParameter("Handler", handler);
		addRequestParameter("Partners", partners);
		addRequestParameter("ActualHour", actualHour);
		addRequestParameter("Notes", notes);
		addRequestParameter("SpecificTime", specificTime);

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

		// 比對資料是否正確
		String[] c = {sprintId, issueId, name, handler, partners, estimate, notes, actualHour, status};
		expected = genXML(c[0], c[1], c[2], c[3], c[4], c[5], c[6], c[7], c[8]);
		assertEquals(expected, response.getWriterBuffer().toString());

		// (IV) test update sprint2 UI1

		// 執行下一次的 action 必須做此動作，否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		sprintId = "2";
		issueId = "3";
		name = mPREFIX + mUPDATE_NAME + issueId;
		handler = "";
		partners = mPREFIX + mUPDATE_PARTNER + issueId;
		estimate = mUPDATE_ESTIMATION;
		actualHour = mUPDATE_HOUR;
		notes = mPREFIX + mUPDATE_NOTE + issueId;
		specificTime = "";
		status = mUPDATE_STATUS[2];

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueId);
		addRequestParameter("Name", name);
		addRequestParameter("Status", status);
		addRequestParameter("SprintID", "Sprint #" + sprintId);
		addRequestParameter("Estimate", estimate);
		addRequestParameter("Handler", handler);
		addRequestParameter("Partners", partners);
		addRequestParameter("ActualHour", actualHour);
		addRequestParameter("Notes", notes);
		addRequestParameter("SpecificTime", specificTime);

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

		// 比對資料是否正確
		String[] d = {sprintId, issueId, name, handler, partners, estimate, notes, actualHour, status};
		expected = genXML(d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7], d[8]);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	private String genXML(String sprintId, String issueId, String name, String handler, String partners, String estimation, String notes, String actualhour, String status) {
		StringBuilder result = new StringBuilder("");

		result.append("<EditUnplannedItem>");
		result.append("<Result>success</Result>");
		result.append("<UnplannedItem>");
		result.append("<Id>" + issueId + "</Id>");
		result.append("<Link>" + "/ezScrum/showIssueInformation.do?issueID=" + issueId + "</Link>");
		result.append("<Name>" + name + "</Name>");
		result.append("<SprintID>" + sprintId + "</SprintID>");
		result.append("<Estimate>" + estimation + "</Estimate>");
		result.append("<Status>" + status + "</Status>");
		result.append("<ActualHour>" + actualhour + "</ActualHour>");
		result.append("<Handler>" + handler + "</Handler>");
		result.append("<Partners>" + partners + "</Partners>");
		result.append("<Notes>" + notes + "</Notes>");
		result.append("</UnplannedItem>");
		result.append("</EditUnplannedItem>");

		return result.toString();
	}
}
