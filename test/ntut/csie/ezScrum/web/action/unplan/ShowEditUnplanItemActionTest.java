package ntut.csie.ezScrum.web.action.unplan;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplanItem;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import servletunit.struts.MockStrutsTestCase;

public class ShowEditUnplanItemActionTest extends MockStrutsTestCase {

	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateUnplanItem mCUI;
	private Configuration mConfig;
	private String mActionPath = "/showEditUnplanItem";

	public ShowEditUnplanItemActionTest(String testMethod) {
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

	// case 1: One sprint with 1 Unplan
	public void testOneSprintWithOneUnplan() {
		int index = 1;
		// 新增一個 Sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// 新增一個 Unplan
		mCUI = new CreateUnplanItem(1, mCP, mCS);
		mCUI.exe();

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long unplanId = mCUI.getUnplansId().get(0);
		long sprintId = mCUI.getUnplans().get(0).getSprintId();

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

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
		String expected = genXML(sprintId, unplanId, index);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	// case 2: One sprint with 2 Unplan
	public void testOneSprintWithTwoUnplans() {
		int index = 0;
		// 新增一個 Sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// 新增兩個 Unplan
		mCUI = new CreateUnplanItem(2, mCP, mCS);
		mCUI.exe();

		// (I) Unplan 1

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long unplanId = mCUI.getUnplansId().get(index);
		long sprintId = mCUI.getUnplans().get(index).getSprintId();

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

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
		String expected = genXML(sprintId, unplanId, index + 1);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (II) Unplan 2

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		index = 1;
		unplanId = mCUI.getUnplansId().get(index);

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

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
		expected = genXML(sprintId, unplanId, index + 1);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	// case 3: Two sprint with 1 Unplan
	public void testTwoSprintWithOneUnplan() {
		int index = 0;
		// 新增二個 Sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		// 新增一個 Unplan
		mCUI = new CreateUnplanItem(1, mCP, mCS);
		mCUI.exe();

		// (I) sprint 1

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long unplanId = mCUI.getUnplansId().get(index);
		long sprintId = mCUI.getUnplans().get(index).getSprintId();

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

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
		String expected = genXML(sprintId, unplanId, index + 1);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (II) sprint 2

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		index = 1;
		unplanId = mCUI.getUnplansId().get(index);
		sprintId = mCUI.getUnplans().get(index).getSprintId();

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

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
		expected = genXML(sprintId, unplanId, index + 1);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	// case 4: Two sprint with 2 Unplan
	public void testTwoSprintWithTwoUnplans() {
		int index = 0;
		// 新增二個 Sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		// 新增二個 Unplan
		mCUI = new CreateUnplanItem(2, mCP, mCS);
		mCUI.exe();

		// (I) sprint1, unplan1

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long unplanId = mCUI.getUnplansId().get(index);
		long sprintId = mCUI.getUnplans().get(index).getSprintId();

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

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
		String expected = genXML(sprintId, unplanId, index + 1);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (II) sprint1, unplan2

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		index = 1;
		unplanId = mCUI.getUnplansId().get(index);
		sprintId = mCUI.getUnplans().get(index).getSprintId();

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

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
		expected = genXML(sprintId, unplanId, index + 1);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (III) sprint2, unplan1

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		index = 2;
		unplanId = mCUI.getUnplansId().get(index);
		sprintId = mCUI.getUnplans().get(index).getSprintId();

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

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
		expected = genXML(sprintId, unplanId, index + 1);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (IV) sprint2, unplan2

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		index = 3;
		unplanId = mCUI.getUnplansId().get(index);
		sprintId = mCUI.getUnplans().get(index).getSprintId();

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(unplanId));

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
		expected = genXML(sprintId, unplanId, index + 1);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	private String genXML(long sprintId, long unplanId, int index) {
		StringBuilder result = new StringBuilder();
		String name = "TEST_UNPLAN_" + index;
		String notes = "TEST_UNPLAN_NOTES_" + index;
		int estimate = 2;
		String handlerUsername = "";
		String partnersUsername = "";

		result.append("<EditUnplannedItem>");
		//
		result.append("<UnplannedItem>");
		result.append("<Id>").append(unplanId).append("</Id>");
		result.append("<Link></Link>");
		result.append("<Name>").append(name).append("</Name>");
		result.append("<SprintID>").append(sprintId).append("</SprintID>");
		result.append("<Estimate>").append(estimate).append("</Estimate>");
		result.append("<Status>new</Status>");
		result.append("<ActualHour>").append(estimate).append("</ActualHour>");
		result.append("<Handler>").append(handlerUsername).append("</Handler>");
		result.append("<Partners>").append(partnersUsername).append("</Partners>");
		result.append("<Notes>").append(notes).append("</Notes>");
		result.append("</UnplannedItem>");
		//
		result.append("</EditUnplannedItem>");

		return result.toString();
	}

}
