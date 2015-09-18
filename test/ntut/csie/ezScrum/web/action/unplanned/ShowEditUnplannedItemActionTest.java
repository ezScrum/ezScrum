package ntut.csie.ezScrum.web.action.unplanned;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplannedItem;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import servletunit.struts.MockStrutsTestCase;

public class ShowEditUnplannedItemActionTest extends MockStrutsTestCase {

	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateUnplannedItem mCUI;
	private Configuration mConfig;
	private String mActionPath = "/showEditUnplannedItem";

	public ShowEditUnplannedItemActionTest(String testMethod) {
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

	protected void tearDown() throws IOException, Exception {
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

	// case 1: One sprint with 1 Unplanned
	public void testOneSprintWithOneUnplanned() {
		int index = 1;
		// 新增一個 Sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// 新增一個 Unplanned
		mCUI = new CreateUnplannedItem(1, mCP, mCS);
		mCUI.exe();

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long unplannedId = mCUI.getUnplannedsId().get(0);
		long sprintId = mCUI.getUnplanneds().get(0).getSprintId();

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
		String expected = genXML(sprintId, unplannedId, index);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	// case 2: One sprint with 2 Unplanned
	public void testOneSprintWithTwoUnplanneds() {
		int index = 0;
		// 新增一個 Sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// 新增兩個 Unplanned
		mCUI = new CreateUnplannedItem(2, mCP, mCS);
		mCUI.exe();

		// (I) Unplanned 1

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long unplannedId = mCUI.getUnplannedsId().get(index);
		long sprintId = mCUI.getUnplanneds().get(index).getSprintId();

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
		String expected = genXML(sprintId, unplannedId, index + 1);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (II) Unplanned 2

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		index = 1;
		unplannedId = mCUI.getUnplannedsId().get(index);

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
		expected = genXML(sprintId, unplannedId, index + 1);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	// case 3: Two sprint with 1 Unplanned
	public void testTwoSprintWithOneUnplanned() {
		int index = 0;
		// 新增二個 Sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		// 新增一個 Unplanned
		mCUI = new CreateUnplannedItem(1, mCP, mCS);
		mCUI.exe();

		// (I) sprint 1

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long unplannedId = mCUI.getUnplannedsId().get(index);
		long sprintId = mCUI.getUnplanneds().get(index).getSprintId();

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
		String expected = genXML(sprintId, unplannedId, index + 1);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (II) sprint 2

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		index = 1;
		unplannedId = mCUI.getUnplannedsId().get(index);
		sprintId = mCUI.getUnplanneds().get(index).getSprintId();

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
		expected = genXML(sprintId, unplannedId, index + 1);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	// case 4: Two sprint with 2 Unplanned
	public void testTwoSprintWithTwoUnplanneds() {
		int index = 0;
		// 新增二個 Sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		// 新增二個 Unplanned
		mCUI = new CreateUnplannedItem(2, mCP, mCS);
		mCUI.exe();

		// (I) sprint1, unplanned1

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long unplannedId = mCUI.getUnplannedsId().get(index);
		long sprintId = mCUI.getUnplanneds().get(index).getSprintId();

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
		String expected = genXML(sprintId, unplannedId, index + 1);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (II) sprint1, unplanned2

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		index = 1;
		unplannedId = mCUI.getUnplannedsId().get(index);
		sprintId = mCUI.getUnplanneds().get(index).getSprintId();

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
		expected = genXML(sprintId, unplannedId, index + 1);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (III) sprint2, unplanned1

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		index = 2;
		unplannedId = mCUI.getUnplannedsId().get(index);
		sprintId = mCUI.getUnplanneds().get(index).getSprintId();

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
		expected = genXML(sprintId, unplannedId, index + 1);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (IV) sprint2, unplanned2

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		index = 3;
		unplannedId = mCUI.getUnplannedsId().get(index);
		sprintId = mCUI.getUnplanneds().get(index).getSprintId();

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
		expected = genXML(sprintId, unplannedId, index + 1);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	private String genXML(long sprintId, long unplannedId, int index) {
		StringBuilder result = new StringBuilder();
		String name = "TEST_UNPLANNED_" + index;
		String notes = "TEST_UNPLANNED_NOTES_" + index;
		int estimate = 2;
		String handlerUsername = "";
		String partnersUsername = "";

		result.append("<EditUnplannedItem>");
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
		result.append("</EditUnplannedItem>");

		return result.toString();
	}

}
