package ntut.csie.ezScrum.web.action.unplanned;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplannedItem;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.UnplannedInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import servletunit.struts.MockStrutsTestCase;

public class EditUnplannedItemActionTest extends MockStrutsTestCase {
	
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateAccount mCA;
	private CreateUnplannedItem mCUI;
	private Configuration mConfig;
	private ProjectObject mProject;
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

		mProject = mCP.getAllProjects().get(0);

		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
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
		projectManager = null;
	}

	/**
	 * case 1: One sprint(s) with One Unplanned item(s)
	 * 將 name, partners, status, estimate, actual, notes 更新
	 */
	public void testEditOneSprintWithOneUnplanned() {
		// create one sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();
		
		// create one unplanned
		mCUI = new CreateUnplannedItem(1, mCP, mCS);
		mCUI.exe();
		
		// create one account
		mCA = new CreateAccount(1);
		mCA.exe();

		// ================ set initial data =======================
		String handlerUsername = "";
		String partnersUsername = mCA.getAccountList().get(0).getUsername();
		String specificTime = "";
		UnplannedInfo unplannedInfo = new UnplannedInfo();
		unplannedInfo.id = 1;
		unplannedInfo.sprintId = 1;
		unplannedInfo.name = "NEW_UNPLANNED_NAME_" + 1;
		unplannedInfo.notes = "NEW_UNPLANNED_NOTES_" + 1;
		unplannedInfo.statusString = "new";
		unplannedInfo.estimate = 99;
		unplannedInfo.actual = 9;

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplannedInfo.sprintId);
		addRequestParameter("Name", unplannedInfo.name);
		addRequestParameter("Notes", unplannedInfo.notes);
		addRequestParameter("Status", unplannedInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplannedInfo.estimate));
		addRequestParameter("ActualHour", String.valueOf(unplannedInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?PID=" + mProject.getName());
		
		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(handlerUsername, partnersUsername, unplannedInfo);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	/**
	 * case 2: One sprint(s) with Two Unplanned item(s)
	 * 測試先修改 UI2 再修改 UI1
	 */
	public void testEditOneSprintWithTwoUnplanned() {
		// create one sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();
		
		// create two unplanned
		mCUI = new CreateUnplannedItem(2, mCP, mCS);
		mCUI.exe();
		
		// create two account
		mCA = new CreateAccount(2);
		mCA.exe();

		// (I) test update unplanned #2

		// ================ set initial data =======================
		String handlerUsername = "";
		String partnersUsername = mCA.getAccountList().get(0).getUsername();
		String specificTime = "";
		UnplannedInfo unplannedInfo = new UnplannedInfo();
		unplannedInfo.id = 2;
		unplannedInfo.sprintId = 1;
		unplannedInfo.name = "NEW_UNPLANNED_NAME_" + unplannedInfo.id;
		unplannedInfo.notes = "NEW_UNPLANNED_NOTES_" + unplannedInfo.id;
		unplannedInfo.statusString = "new";
		unplannedInfo.estimate = 99;
		unplannedInfo.actual = 9;

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplannedInfo.sprintId);
		addRequestParameter("Name", unplannedInfo.name);
		addRequestParameter("Notes", unplannedInfo.notes);
		addRequestParameter("Status", unplannedInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplannedInfo.estimate));
		addRequestParameter("ActualHour", String.valueOf(unplannedInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?PID=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		
		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(handlerUsername, partnersUsername, unplannedInfo);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (II) test update unplanned #1

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		handlerUsername = mCA.getAccountList().get(0).getUsername();;
		partnersUsername = mCA.getAccountList().get(1).getUsername();
		specificTime = "";
		unplannedInfo = new UnplannedInfo();
		unplannedInfo.id = 1;
		unplannedInfo.sprintId = 1;
		unplannedInfo.name = "NEW_UNPLANNED_NAME_" + unplannedInfo.id;
		unplannedInfo.notes = "NEW_UNPLANNED_NOTES_" + unplannedInfo.id;
		unplannedInfo.statusString = "assigned";
		unplannedInfo.estimate = 99;
		unplannedInfo.actual = 9;
		
		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplannedInfo.sprintId);
		addRequestParameter("Name", unplannedInfo.name);
		addRequestParameter("Notes", unplannedInfo.notes);
		addRequestParameter("Status", unplannedInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplannedInfo.estimate));
		addRequestParameter("ActualHour", String.valueOf(unplannedInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?PID=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		
		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(handlerUsername, partnersUsername, unplannedInfo);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	/**
	 * case 3: Two sprint(s) with One Unplanned item(s)
	 * 測試先修改 sprint2 UI1 再修改 sprint1 UI1
	 */
	public void testEditTwoSprintWithOneUnplanned() {
		// create two sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();
		
		// create one unplanned
		mCUI = new CreateUnplannedItem(1, mCP, mCS);
		mCUI.exe();
		
		// create two account
		mCA = new CreateAccount(2);
		mCA.exe();

		// (I) test update sprint2 unplanned 1

		// ================ set initial data =======================
		String handlerUsername = "";
		String partnersUsername = mCA.getAccountList().get(0).getUsername();
		String specificTime = "";
		UnplannedInfo unplannedInfo = new UnplannedInfo();
		unplannedInfo.id = 2;
		unplannedInfo.sprintId = 2;
		unplannedInfo.name = "NEW_UNPLANNED_NAME_" + unplannedInfo.id;
		unplannedInfo.notes = "NEW_UNPLANNED_NOTES_" + unplannedInfo.id;
		unplannedInfo.statusString = "new";
		unplannedInfo.estimate = 99;
		unplannedInfo.actual = 9;

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplannedInfo.sprintId);
		addRequestParameter("Name", unplannedInfo.name);
		addRequestParameter("Notes", unplannedInfo.notes);
		addRequestParameter("Status", unplannedInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplannedInfo.estimate));
		addRequestParameter("ActualHour", String.valueOf(unplannedInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?PID=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		
		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(handlerUsername, partnersUsername, unplannedInfo);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (II) test update sprint1 unplanned

		// 執行下一次的 action 必須做此動作，否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		handlerUsername = mCA.getAccountList().get(0).getUsername();
		partnersUsername = mCA.getAccountList().get(1).getUsername();
		specificTime = "";
		unplannedInfo = new UnplannedInfo();
		unplannedInfo.id = 1;
		unplannedInfo.sprintId = 1;
		unplannedInfo.name = "NEW_UNPLANNED_NAME_" + unplannedInfo.id;
		unplannedInfo.notes = "NEW_UNPLANNED_NOTES_" + unplannedInfo.id;
		unplannedInfo.statusString = "assigned";
		unplannedInfo.estimate = 99;
		unplannedInfo.actual = 9;

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplannedInfo.sprintId);
		addRequestParameter("Name", unplannedInfo.name);
		addRequestParameter("Notes", unplannedInfo.notes);
		addRequestParameter("Status", unplannedInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplannedInfo.estimate));
		addRequestParameter("ActualHour", String.valueOf(unplannedInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?PID=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		
		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(handlerUsername, partnersUsername, unplannedInfo);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	/**
	 * case 4: Two sprint(s) with Two Unplanned item(s)
	 * 測試先修改 sprint1 UI1.UI2 再修改 sprint2 UI2.UI1
	 */
	public void testEditTwoSprintWithTwoUnplanned() {
		// create two sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();
		
		// create two unplanned
		mCUI = new CreateUnplannedItem(2, mCP, mCS);
		mCUI.exe();
		
		// create two account
		mCA = new CreateAccount(2);
		mCA.exe();

		// (I) test update sprint1 unplanned #1

		// ================ set initial data =======================
		String handlerUsername = "";
		String partnersUsername = mCA.getAccountList().get(0).getUsername();
		String specificTime = "";
		UnplannedInfo unplannedInfo = new UnplannedInfo();
		unplannedInfo.id = 1;
		unplannedInfo.sprintId = 1;
		unplannedInfo.name = "NEW_UNPLANNED_NAME_" + unplannedInfo.id;
		unplannedInfo.notes = "NEW_UNPLANNED_NOTES_" + unplannedInfo.id;
		unplannedInfo.statusString = "new";
		unplannedInfo.estimate = 99;
		unplannedInfo.actual = 9;

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplannedInfo.sprintId);
		addRequestParameter("Name", unplannedInfo.name);
		addRequestParameter("Notes", unplannedInfo.notes);
		addRequestParameter("Status", unplannedInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplannedInfo.estimate));
		addRequestParameter("ActualHour", String.valueOf(unplannedInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?PID=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		
		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(handlerUsername, partnersUsername, unplannedInfo);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (II) test update sprint1 unplanned #2

		// 執行下一次的 action 必須做此動作，否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		handlerUsername = mCA.getAccountList().get(0).getUsername();
		partnersUsername = mCA.getAccountList().get(1).getUsername();
		specificTime = "";
		unplannedInfo = new UnplannedInfo();
		unplannedInfo.id = 2;
		unplannedInfo.sprintId = 1;
		unplannedInfo.name = "NEW_UNPLANNED_NAME_" + unplannedInfo.id;
		unplannedInfo.notes = "NEW_UNPLANNED_NOTES_" + unplannedInfo.id;
		unplannedInfo.statusString = "assigned";
		unplannedInfo.estimate = 99;
		unplannedInfo.actual = 9;

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplannedInfo.sprintId);
		addRequestParameter("Name", unplannedInfo.name);
		addRequestParameter("Notes", unplannedInfo.notes);
		addRequestParameter("Status", unplannedInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplannedInfo.estimate));
		addRequestParameter("ActualHour", String.valueOf(unplannedInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?PID=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		
		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(handlerUsername, partnersUsername, unplannedInfo);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (III) test update sprint2 unplanned #2

		// 執行下一次的 action 必須做此動作，否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		handlerUsername = mCA.getAccountList().get(0).getUsername();
		partnersUsername = mCA.getAccountList().get(1).getUsername();
		specificTime = "";
		unplannedInfo = new UnplannedInfo();
		unplannedInfo.id = 4;
		unplannedInfo.sprintId = 2;
		unplannedInfo.name = "NEW_UNPLANNED_NAME_" + unplannedInfo.id;
		unplannedInfo.notes = "NEW_UNPLANNED_NOTES_" + unplannedInfo.id;
		unplannedInfo.statusString = "assigned";
		unplannedInfo.estimate = 99;
		unplannedInfo.actual = 9;

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplannedInfo.sprintId);
		addRequestParameter("Name", unplannedInfo.name);
		addRequestParameter("Notes", unplannedInfo.notes);
		addRequestParameter("Status", unplannedInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplannedInfo.estimate));
		addRequestParameter("ActualHour", String.valueOf(unplannedInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?PID=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		
		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(handlerUsername, partnersUsername, unplannedInfo);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (IV) test update sprint2 unplanned #1

		// 執行下一次的 action 必須做此動作，否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		handlerUsername = mCA.getAccountList().get(0).getUsername();
		partnersUsername = mCA.getAccountList().get(1).getUsername();
		specificTime = "";
		unplannedInfo = new UnplannedInfo();
		unplannedInfo.id = 3;
		unplannedInfo.sprintId = 2;
		unplannedInfo.name = "NEW_UNPLANNED_NAME_" + unplannedInfo.id;
		unplannedInfo.notes = "NEW_UNPLANNED_NOTES_" + unplannedInfo.id;
		unplannedInfo.statusString = "assigned";
		unplannedInfo.estimate = 99;
		unplannedInfo.actual = 9;

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplannedInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplannedInfo.sprintId);
		addRequestParameter("Name", unplannedInfo.name);
		addRequestParameter("Notes", unplannedInfo.notes);
		addRequestParameter("Status", unplannedInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplannedInfo.estimate));
		addRequestParameter("ActualHour", String.valueOf(unplannedInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?PID=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		
		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(handlerUsername, partnersUsername, unplannedInfo);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	private String genXML(String handlerUsername, String partnerUsername,
			UnplannedInfo unplannedInfo) {
		StringBuilder result = new StringBuilder();
		result.append("<EditUnplannedItem>");
		result.append("<Result>success</Result>");
		result.append("<UnplannedItem>");
		result.append("<Id>").append(unplannedInfo.id).append("</Id>");
		result.append("<Link></Link>");
		result.append("<Name>").append(unplannedInfo.name).append("</Name>");
		result.append("<SprintID>").append(unplannedInfo.sprintId).append("</SprintID>");
		result.append("<Estimate>").append(unplannedInfo.estimate).append("</Estimate>");
		result.append("<Status>").append(unplannedInfo.statusString).append("</Status>");
		result.append("<ActualHour>").append(unplannedInfo.actual).append("</ActualHour>");
		result.append("<Handler>").append(handlerUsername).append("</Handler>");
		result.append("<Partners>").append(partnerUsername).append("</Partners>");
		result.append("<Notes>").append(unplannedInfo.notes).append("</Notes>");
		result.append("</UnplannedItem>");
		result.append("</EditUnplannedItem>");

		return result.toString();
	}
}
