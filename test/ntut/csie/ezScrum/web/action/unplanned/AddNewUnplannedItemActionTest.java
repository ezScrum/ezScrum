package ntut.csie.ezScrum.web.action.unplanned;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AddNewUnplannedItemActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private String mActionPath = "/addNewUnplannedItem";

	private String mPrefix = "TEST_UNPLANNED_";
	private String mTestPartner = "TEST_PARTNER_";
	private String mTestEstimation = "99";
	private String mTestNote = "TEST_NOTE_";

	public AddNewUnplannedItemActionTest(String testMethod) {
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

		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent")); // 設定讀取的
		// struts-config 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		projectManager = null;
		this.mCP = null;
		this.mCS = null;
		mConfig = null;
	}

	// case 1: One sprint with adding unplanned item
	public void testOneSprint1ui() throws Exception {
		this.mCS = new CreateSprint(1, this.mCP);
		this.mCS.exe(); // 新增一個 Sprint	

		// ================ set initial data =======================
		IProject project = this.mCP.getProjectList().get(0);
		String sprintID = "1";
		String uName = this.mPrefix + "name";
		String uID = "1";
		String uHandler = "";
		String uPartners = "";
		String uEstimation = "0";
		String uNotes = "";
		String uSpecificTime = "";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", uName);
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("Estimate", uEstimation);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime", uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String[] a = {sprintID, uID, uName, uHandler, uPartners, uEstimation, uNotes};
		String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5], a[6]);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 2: One sprint with adding 2 unplanned items
	public void testOneSprint2ui() throws Exception {
		this.mCS = new CreateSprint(1, this.mCP);
		this.mCS.exe(); // 新增一個 Sprint	

		// (I) test Sprint 1

		// ================ set initial data =======================
		IProject project = this.mCP.getProjectList().get(0);
		String uID = "1";

		String uName = this.mPrefix + "name";
		String sprintID = "1";
		String uHandler = "";
		String uPartners = this.mTestPartner + uID;
		String uEstimation = this.mTestEstimation;
		String uNotes = this.mTestNote;
		String uSpecificTime = "";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", uName);
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("Estimate", uEstimation);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime", uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String[] a = {sprintID, uID, uName, uHandler, uPartners, uEstimation, uNotes};
		String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5], a[6]);
		assertEquals(expected, response.getWriterBuffer().toString());

		// (II) test Sprint 2

		// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		project = this.mCP.getProjectList().get(0);
		uID = "2";

		uName = this.mPrefix + "name";
		sprintID = "1";
		uHandler = "";
		uPartners = this.mTestPartner + uID;
		uEstimation = this.mTestEstimation;
		uNotes = this.mTestNote;
		uSpecificTime = "";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", uName);
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("Estimate", uEstimation);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime", uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String[] b = {sprintID, uID, uName, uHandler, uPartners, uEstimation, uNotes};
		expected = genXML(b[0], b[1], b[2], b[3], b[4], b[5], b[6]);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 3: Two sprint with adding unplanned item
	public void testTwoSprint1ui() throws Exception {
		this.mCS = new CreateSprint(2, this.mCP);
		this.mCS.exe();

		// ================ set initial data =======================
		IProject project = this.mCP.getProjectList().get(0);
		String sprintID = "2";
		String uName = this.mPrefix + "name";
		String uID = "1";
		String uHandler = "";
		String uPartners = "";
		String uEstimation = "0";
		String uNotes = "";
		String uSpecificTime = "";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", uName);
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("Estimate", uEstimation);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime", uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String[] a = {sprintID, uID, uName, uHandler, uPartners, uEstimation, uNotes};
		String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5], a[6]);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 4: Two sprint with adding 2 unplanned items
	public void testTwoSprint2ui() throws Exception {
		this.mCS = new CreateSprint(2, this.mCP);
		this.mCS.exe();

		// (I) test Sprint 1

		// ================ set initial data =======================
		IProject project = this.mCP.getProjectList().get(0);
		String uID = "1";

		String uName = this.mPrefix + "name";
		String sprintID = "1";
		String uHandler = "";
		String uPartners = this.mTestPartner + uID;
		String uEstimation = this.mTestEstimation;
		String uNotes = this.mTestNote;
		String uSpecificTime = "";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", uName);
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("Estimate", uEstimation);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime", uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String[] a = {sprintID, uID, uName, uHandler, uPartners, uEstimation, uNotes};
		String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5], a[6]);
		assertEquals(expected, response.getWriterBuffer().toString());

		// (II) test Sprint 2

		// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		project = this.mCP.getProjectList().get(0);
		uID = "2";

		uName = this.mPrefix + "name";
		sprintID = "1";
		uHandler = "";
		uPartners = this.mTestPartner + uID;
		uEstimation = this.mTestEstimation;
		uNotes = this.mTestNote;
		uSpecificTime = "";
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("Name", uName);
		addRequestParameter("SprintID", "Sprint #" + sprintID);
		addRequestParameter("Handler", uHandler);
		addRequestParameter("Partners", uPartners);
		addRequestParameter("Estimate", uEstimation);
		addRequestParameter("Notes", uNotes);
		addRequestParameter("SpecificTime", uSpecificTime);
		// ================== set parameter info ====================

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName()); // SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String[] b = {sprintID, uID, uName, uHandler, uPartners, uEstimation, uNotes};
		expected = genXML(b[0], b[1], b[2], b[3], b[4], b[5], b[6]);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	private String genXML(String sprintID, String issueID, String name, String handler, String partners, String estimation, String notes) {
		StringBuilder result = new StringBuilder("");

		result.append("<AddUnplannedItem>");
		result.append("<Result>success</Result>");
		//
		result.append("<UnplannedItem>");
		result.append("<Id>" + issueID + "</Id>");
		result.append("<Link>" + "/ezScrum/showIssueInformation.do?issueID=" + issueID + "</Link>");
		result.append("<Name>" + name + "</Name>");
		result.append("<SprintID>" + sprintID + "</SprintID>");
		result.append("<Estimate>" + estimation + "</Estimate>");
		result.append("<Status>" + "new" + "</Status>");
		result.append("<ActualHour>" + "0" + "</ActualHour>");
		result.append("<Handler>" + handler + "</Handler>");
		result.append("<Partners>" + partners + "</Partners>");
		result.append("<Notes>" + notes + "</Notes>");
		result.append("</UnplannedItem>");
		//
		result.append("</AddUnplannedItem>");

		return result.toString();
	}
}
