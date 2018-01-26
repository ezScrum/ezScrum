package ntut.csie.ezScrum.web.action.unplan;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplanItem;
import ntut.csie.ezScrum.web.dataInfo.UnplanInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import servletunit.struts.MockStrutsTestCase;

public class EditUnplanItemActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateAccount mCA;
	private CreateUnplanItem mCUI;
	private Configuration mConfig;
	private ProjectObject mProject;
	private String mActionPath = "/editUnplanItem";

	public EditUnplanItemActionTest(String testMethod) {
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
		mCP.exeCreateForDb();

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

	/**
	 * case 1: One sprint(s) with One Unplan item(s)
	 * 將 name, partners, status, estimate, actual, notes 更新
	 */
	public void testEditOneSprintWithOneUnplan() {
		// create one sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();
		
		// create one unplan
		mCUI = new CreateUnplanItem(1, mCP, mCS);
		mCUI.exe();
		
		// create one account
		mCA = new CreateAccount(1);
		mCA.exe();

		// ================ set initial data =======================
		String handlerUsername = "";
		String partnersUsername = mCA.getAccountList().get(0).getUsername();
		String specificTime = "";
		UnplanInfo unplanInfo = new UnplanInfo();
		unplanInfo.id = 1;
		unplanInfo.sprintId = 1;
		unplanInfo.name = "NEW_UNPLAN_NAME_" + 1;
		unplanInfo.notes = "NEW_UNPLAN_NOTES_" + 1;
		unplanInfo.statusString = "new";
		unplanInfo.estimate = 99;
		//unplanInfo.actual = 9;

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplanInfo.sprintId);
		addRequestParameter("Name", unplanInfo.name);
		addRequestParameter("Notes", unplanInfo.notes);
		addRequestParameter("Status", unplanInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplanInfo.estimate));
		//addRequestParameter("ActualHour", String.valueOf(unplanInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?projectName=" + mProject.getName());
		
		// 執行 action
		actionPerform();

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(handlerUsername, partnersUsername, unplanInfo);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	/**
	 * case 2: One sprint(s) with Two Unplan item(s)
	 * 測試先修改 UI2 再修改 UI1
	 */
	public void testEditOneSprintWithTwoUnplan() {
		// create one sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();
		
		// create two unplan
		mCUI = new CreateUnplanItem(2, mCP, mCS);
		mCUI.exe();
		
		// create two account
		mCA = new CreateAccount(2);
		mCA.exe();

		// (I) test update unplan #2

		// ================ set initial data =======================
		String handlerUsername = "";
		String partnersUsername = mCA.getAccountList().get(0).getUsername();
		String specificTime = "";
		UnplanInfo unplanInfo = new UnplanInfo();
		unplanInfo.id = 2;
		unplanInfo.sprintId = 1;
		unplanInfo.name = "NEW_UNPLAN_NAME_" + unplanInfo.id;
		unplanInfo.notes = "NEW_UNPLAN_NOTES_" + unplanInfo.id;
		unplanInfo.statusString = "new";
		unplanInfo.estimate = 99;
		//unplanInfo.actual = 9;

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplanInfo.sprintId);
		addRequestParameter("Name", unplanInfo.name);
		addRequestParameter("Notes", unplanInfo.notes);
		addRequestParameter("Status", unplanInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplanInfo.estimate));
		//addRequestParameter("ActualHour", String.valueOf(unplanInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?projectName=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		
		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(handlerUsername, partnersUsername, unplanInfo);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (II) test update unplan #1

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		handlerUsername = mCA.getAccountList().get(0).getUsername();;
		partnersUsername = mCA.getAccountList().get(1).getUsername();
		specificTime = "";
		unplanInfo = new UnplanInfo();
		unplanInfo.id = 1;
		unplanInfo.sprintId = 1;
		unplanInfo.name = "NEW_UNPLAN_NAME_" + unplanInfo.id;
		unplanInfo.notes = "NEW_UNPLAN_NOTES_" + unplanInfo.id;
		unplanInfo.statusString = "assigned";
		unplanInfo.estimate = 99;
		//unplanInfo.actual = 9;
		
		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplanInfo.sprintId);
		addRequestParameter("Name", unplanInfo.name);
		addRequestParameter("Notes", unplanInfo.notes);
		addRequestParameter("Status", unplanInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplanInfo.estimate));
		//addRequestParameter("ActualHour", String.valueOf(unplanInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?projectName=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		
		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(handlerUsername, partnersUsername, unplanInfo);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	/**
	 * case 3: Two sprint(s) with One Unplan item(s)
	 * 測試先修改 sprint2 UI1 再修改 sprint1 UI1
	 */
	public void testEditTwoSprintWithOneUnplan() {
		// create two sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();
		
		// create one unplan
		mCUI = new CreateUnplanItem(1, mCP, mCS);
		mCUI.exe();
		
		// create two account
		mCA = new CreateAccount(2);
		mCA.exe();

		// (I) test update sprint2 unplan 1

		// ================ set initial data =======================
		String handlerUsername = "";
		String partnersUsername = mCA.getAccountList().get(0).getUsername();
		String specificTime = "";
		UnplanInfo unplanInfo = new UnplanInfo();
		unplanInfo.id = 2;
		unplanInfo.sprintId = 2;
		unplanInfo.name = "NEW_UNPLAN_NAME_" + unplanInfo.id;
		unplanInfo.notes = "NEW_UNPLAN_NOTES_" + unplanInfo.id;
		unplanInfo.statusString = "new";
		unplanInfo.estimate = 99;
		//unplanInfo.actual = 9;

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplanInfo.sprintId);
		addRequestParameter("Name", unplanInfo.name);
		addRequestParameter("Notes", unplanInfo.notes);
		addRequestParameter("Status", unplanInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplanInfo.estimate));
		//addRequestParameter("ActualHour", String.valueOf(unplanInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?projectName=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		
		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(handlerUsername, partnersUsername, unplanInfo);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (II) test update sprint1 unplan

		// 執行下一次的 action 必須做此動作，否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		handlerUsername = mCA.getAccountList().get(0).getUsername();
		partnersUsername = mCA.getAccountList().get(1).getUsername();
		specificTime = "";
		unplanInfo = new UnplanInfo();
		unplanInfo.id = 1;
		unplanInfo.sprintId = 1;
		unplanInfo.name = "NEW_UNPLAN_NAME_" + unplanInfo.id;
		unplanInfo.notes = "NEW_UNPLAN_NOTES_" + unplanInfo.id;
		unplanInfo.statusString = "assigned";
		unplanInfo.estimate = 99;
		//unplanInfo.actual = 9;

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplanInfo.sprintId);
		addRequestParameter("Name", unplanInfo.name);
		addRequestParameter("Notes", unplanInfo.notes);
		addRequestParameter("Status", unplanInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplanInfo.estimate));
		//addRequestParameter("ActualHour", String.valueOf(unplanInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?projectName=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		
		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(handlerUsername, partnersUsername, unplanInfo);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	/**
	 * case 4: Two sprint(s) with Two Unplan item(s)
	 * 測試先修改 sprint1 UI1.UI2 再修改 sprint2 UI2.UI1
	 */
	public void testEditTwoSprintWithTwoUnplan() {
		// create two sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();
		
		// create two unplan
		mCUI = new CreateUnplanItem(2, mCP, mCS);
		mCUI.exe();
		
		// create two account
		mCA = new CreateAccount(2);
		mCA.exe();

		// (I) test update sprint1 unplan #1

		// ================ set initial data =======================
		String handlerUsername = "";
		String partnersUsername = mCA.getAccountList().get(0).getUsername();
		String specificTime = "";
		UnplanInfo unplanInfo = new UnplanInfo();
		unplanInfo.id = 1;
		unplanInfo.sprintId = 1;
		unplanInfo.name = "NEW_UNPLAN_NAME_" + unplanInfo.id;
		unplanInfo.notes = "NEW_UNPLAN_NOTES_" + unplanInfo.id;
		unplanInfo.statusString = "new";
		unplanInfo.estimate = 99;
		//unplanInfo.actual = 9;

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplanInfo.sprintId);
		addRequestParameter("Name", unplanInfo.name);
		addRequestParameter("Notes", unplanInfo.notes);
		addRequestParameter("Status", unplanInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplanInfo.estimate));
		//addRequestParameter("ActualHour", String.valueOf(unplanInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?projectName=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		
		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		String expected = genXML(handlerUsername, partnersUsername, unplanInfo);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (II) test update sprint1 unplan #2

		// 執行下一次的 action 必須做此動作，否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		handlerUsername = mCA.getAccountList().get(0).getUsername();
		partnersUsername = mCA.getAccountList().get(1).getUsername();
		specificTime = "";
		unplanInfo = new UnplanInfo();
		unplanInfo.id = 2;
		unplanInfo.sprintId = 1;
		unplanInfo.name = "NEW_UNPLAN_NAME_" + unplanInfo.id;
		unplanInfo.notes = "NEW_UNPLAN_NOTES_" + unplanInfo.id;
		unplanInfo.statusString = "assigned";
		unplanInfo.estimate = 99;
		//unplanInfo.actual = 9;

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplanInfo.sprintId);
		addRequestParameter("Name", unplanInfo.name);
		addRequestParameter("Notes", unplanInfo.notes);
		addRequestParameter("Status", unplanInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplanInfo.estimate));
		//addRequestParameter("ActualHour", String.valueOf(unplanInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?projectName=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		
		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(handlerUsername, partnersUsername, unplanInfo);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (III) test update sprint2 unplan #2

		// 執行下一次的 action 必須做此動作，否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		handlerUsername = mCA.getAccountList().get(0).getUsername();
		partnersUsername = mCA.getAccountList().get(1).getUsername();
		specificTime = "";
		unplanInfo = new UnplanInfo();
		unplanInfo.id = 4;
		unplanInfo.sprintId = 2;
		unplanInfo.name = "NEW_UNPLAN_NAME_" + unplanInfo.id;
		unplanInfo.notes = "NEW_UNPLAN_NOTES_" + unplanInfo.id;
		unplanInfo.statusString = "assigned";
		unplanInfo.estimate = 99;
		//unplanInfo.actual = 9;

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplanInfo.sprintId);
		addRequestParameter("Name", unplanInfo.name);
		addRequestParameter("Notes", unplanInfo.notes);
		addRequestParameter("Status", unplanInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplanInfo.estimate));
		//addRequestParameter("ActualHour", String.valueOf(unplanInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?projectName=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		
		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(handlerUsername, partnersUsername, unplanInfo);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (IV) test update sprint2 unplan #1

		// 執行下一次的 action 必須做此動作，否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		handlerUsername = mCA.getAccountList().get(0).getUsername();
		partnersUsername = mCA.getAccountList().get(1).getUsername();
		specificTime = "";
		unplanInfo = new UnplanInfo();
		unplanInfo.id = 3;
		unplanInfo.sprintId = 2;
		unplanInfo.name = "NEW_UNPLAN_NAME_" + unplanInfo.id;
		unplanInfo.notes = "NEW_UNPLAN_NOTES_" + unplanInfo.id;
		unplanInfo.statusString = "assigned";
		unplanInfo.estimate = 99;
		//unplanInfo.actual = 9;

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanInfo.id));
		addRequestParameter("SprintID", "Sprint #" + unplanInfo.sprintId);
		addRequestParameter("Name", unplanInfo.name);
		addRequestParameter("Notes", unplanInfo.notes);
		addRequestParameter("Status", unplanInfo.statusString);
		addRequestParameter("Estimate", String.valueOf(unplanInfo.estimate));
		//addRequestParameter("ActualHour", String.valueOf(unplanInfo.actual));
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("SpecificTime", specificTime);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);
		request.setHeader("Referer", "?projectName=" + mProject.getName());
		
		// 執行 action
		actionPerform();
		
		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();

		// 比對資料是否正確
		expected = genXML(handlerUsername, partnersUsername, unplanInfo);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	private String genXML(String handlerUsername, String partnerUsername,
			UnplanInfo unplanInfo) {
		StringBuilder result = new StringBuilder();
		result.append("<EditUnplannedItem>");
		result.append("<Result>success</Result>");
		result.append("<UnplannedItem>");
		result.append("<Id>").append(unplanInfo.id).append("</Id>");
		result.append("<Link></Link>");
		result.append("<Name>").append(unplanInfo.name).append("</Name>");
		result.append("<SprintID>").append(unplanInfo.sprintId).append("</SprintID>");
		result.append("<Estimate>").append(unplanInfo.estimate).append("</Estimate>");
		result.append("<Status>").append(unplanInfo.statusString).append("</Status>");
		//result.append("<ActualHour>").append(unplanInfo.actual).append("</ActualHour>");
		result.append("<Handler>").append(handlerUsername).append("</Handler>");
		result.append("<Partners>").append(partnerUsername).append("</Partners>");
		result.append("<Notes>").append(unplanInfo.notes).append("</Notes>");
		result.append("</UnplannedItem>");
		result.append("</EditUnplannedItem>");

		return result.toString();
	}
}
