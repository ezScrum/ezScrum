package ntut.csie.ezScrum.web.action.unplanned;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import servletunit.struts.MockStrutsTestCase;

public class AddNewUnplannedItemActionTest extends MockStrutsTestCase {
	
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateAccount mCA;
	private Configuration mConfig;
	private String mActionPath = "/addNewUnplannedItem";

	private String mTestName = "TEST_UNPLANNED_NAME";
	private String mTestNote = "TEST_NOTE";
	private int mTestEstimate = 99;

	public AddNewUnplannedItemActionTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增一測試專案
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

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mConfig = null;
		projectManager = null;
	}

	// case 1: One sprint with adding 1 unplanned
	public void testOneSprint_addOneUnplanned() {
		// 新增一個 Sprint	
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long id = 1;
		long sprintId = mCS.getSprintsId().get(0);
		String name = mTestName;
		String notes = mTestNote;
		String handlerUsername = "";
		String partnersUsername = "";
		int estimate = 0;
		String specificTimeString = "";

		// ================== set parameter info ====================
		addRequestParameter("Name", name);
		addRequestParameter("Notes", notes);
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("Estimate", String.valueOf(estimate));
		addRequestParameter("SprintID", "Sprint #" + sprintId);
		addRequestParameter("SpecificTime", specificTimeString);

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
		String expected = genXML(sprintId, id, name, handlerUsername,
				partnersUsername, estimate, notes);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 2: One sprint with adding 2 unplanned
	public void testOneSprint_addTwoUnplanneds() {
		// 新增一個 Sprint	
		mCS = new CreateSprint(1, mCP);
		mCS.exe();
		// 新增二個 Account
		mCA = new CreateAccount(2);
		mCA.exe();

		// (I) test Sprint 1

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long id = 1;
		long sprintId = mCS.getSprintsId().get(0);
		String name = mTestName;
		String notes = mTestNote;
		String handlerUsername = "";
		String partnersUsername = mCA.getAccountList().get(0).getUsername();
		int estimate = mTestEstimate;
		String specificTimeString = "";

		// ================== set parameter info ====================
		addRequestParameter("Name", name);
		addRequestParameter("SprintID", "Sprint #" + sprintId);
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("Estimate", String.valueOf(estimate));
		addRequestParameter("Notes", notes);
		addRequestParameter("SpecificTime", specificTimeString);

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
		String expected = genXML(sprintId, id, name, handlerUsername,
				partnersUsername, estimate, notes);
		assertEquals(expected, response.getWriterBuffer().toString());

		// (II) test Sprint 2

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		project = mCP.getAllProjects().get(0);
		id = 2;
		sprintId = mCS.getSprintsId().get(0);
		name = mTestName;
		notes = mTestNote;
		handlerUsername = "";
		partnersUsername = mCA.getAccountList().get(1).getUsername();
		estimate = mTestEstimate;
		specificTimeString = "";

		// ================== set parameter info ====================
		addRequestParameter("Name", name);
		addRequestParameter("SprintID", "Sprint #" + sprintId);
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("Estimate", String.valueOf(estimate));
		addRequestParameter("Notes", notes);
		addRequestParameter("SpecificTime", specificTimeString);

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
		expected = genXML(sprintId, id, name, handlerUsername,
				partnersUsername, estimate, notes);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 3: Two sprint with adding 1 unplanned
	public void testTwoSprint_addOneUnplanned() {
		// 新增一個 Sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();
		
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long id = 1;
		long sprintId = mCS.getSprintsId().get(1);
		String name = mTestName;
		String notes = "";
		String handlerUsername = "";
		String partnersUsername = "";
		int estimate = 0;
		String specificTimeString = "";

		// ================== set parameter info ====================
		addRequestParameter("Name", name);
		addRequestParameter("SprintID", "Sprint #" + sprintId);
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("Estimate", String.valueOf(estimate));
		addRequestParameter("Notes", notes);
		addRequestParameter("SpecificTime", specificTimeString);

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
		String expected = genXML(sprintId, id, name, handlerUsername,
				partnersUsername, estimate, notes);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 4: Two sprint with adding 2 unplanned
	public void testTwoSprint_addTwoUnplanneds() {
		// 新增一個 Sprint	
		mCS = new CreateSprint(2, mCP);
		mCS.exe();
		// 新增二個 Account
		mCA = new CreateAccount(2);
		mCA.exe();

		// (I) test Sprint 1

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long id = 1;
		long sprintId = mCS.getSprintsId().get(0);
		String name = mTestName;
		String notes = mTestNote;
		String handlerUsername = "";
		String partnersUsername = mCA.getAccountList().get(0).getUsername();
		int estimate = mTestEstimate;
		String specificTimeString = "";

		// ================== set parameter info ====================
		addRequestParameter("Name", name);
		addRequestParameter("SprintID", "Sprint #" + sprintId);
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("Estimate", String.valueOf(estimate));
		addRequestParameter("Notes", notes);
		addRequestParameter("SpecificTime", specificTimeString);

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
		String expected = genXML(sprintId, id, name, handlerUsername,
				partnersUsername, estimate, notes);
		assertEquals(expected, response.getWriterBuffer().toString());

		// (II) test Sprint 2

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		project = mCP.getAllProjects().get(0);
		id = 2;
		sprintId = mCS.getSprintsId().get(0);
		name = mTestName;
		notes = mTestNote;
		handlerUsername = "";
		partnersUsername = mCA.getAccountList().get(1).getUsername();
		estimate = mTestEstimate;
		specificTimeString = "";

		// ================== set parameter info ====================
		addRequestParameter("Name", name);
		addRequestParameter("SprintID", "Sprint #" + sprintId);
		addRequestParameter("Handler", handlerUsername);
		addRequestParameter("Partners", partnersUsername);
		addRequestParameter("Estimate", String.valueOf(estimate));
		addRequestParameter("Notes", notes);
		addRequestParameter("SpecificTime", specificTimeString);

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
		expected = genXML(sprintId, id, name, handlerUsername,
				partnersUsername, estimate, notes);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	private String genXML(long sprintId, long unplannedId, String name,
			String handlerUsername, String partnersUsername, int estimate,
			String notes) {
		StringBuilder result = new StringBuilder();

		result.append("<AddUnplannedItem>");
		result.append("<Result>success</Result>");
		//
		result.append("<UnplannedItem>");
		result.append("<Id>").append(unplannedId).append("</Id>");
		result.append("<Link></Link>");
		result.append("<Name>").append(name).append("</Name>");
		result.append("<SprintID>").append(sprintId).append("</SprintID>");
		result.append("<Estimate>").append(estimate).append("</Estimate>");
		result.append("<Status>new</Status>");
		result.append("<ActualHour>0</ActualHour>");
		result.append("<Handler>").append(handlerUsername).append("</Handler>");
		result.append("<Partners>").append(partnersUsername).append("</Partners>");
		result.append("<Notes>").append(notes).append("</Notes>");
		result.append("</UnplannedItem>");
		//
		result.append("</AddUnplannedItem>");

		return result.toString();
	}
}
