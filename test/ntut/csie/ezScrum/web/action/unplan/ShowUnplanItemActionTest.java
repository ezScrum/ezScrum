package ntut.csie.ezScrum.web.action.unplan;

import java.io.File;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplanItem;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import servletunit.struts.MockStrutsTestCase;

public class ShowUnplanItemActionTest extends MockStrutsTestCase {

	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateUnplanItem mCUI;
	private Configuration mConfig;
	private String mActionPath = "/GetUnplanItems";

	public ShowUnplanItemActionTest(String testMethod) {
		super(testMethod);
	}

	@Before
	public void setUp() throws Exception {
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

	@After
	public void tearDown() throws Exception {
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

	// case 1: No sprint
	@Test
	public void testNoSprint() {
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		String selectedSprint = "-1";

		// ================== set parameter info ====================
		addRequestParameter("SprintID", selectedSprint);

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

		// 比對資料是否正確 (sprintID = -1)
		ArrayList<UnplanObject> unplans = new ArrayList<UnplanObject>();
		String expected = genXML(selectedSprint, unplans);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}
	
	@Test
	public void testSelectDefaultSprint() {
		// create one sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// create two unplan
		mCUI = new CreateUnplanItem(2, mCP, mCS);
		mCUI.exe();
		
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		String selectedSprint = "";
		long sprintId = mCS.getSprintsId().get(0);
		
		// ================== set parameter info ====================
		addRequestParameter("SprintID", selectedSprint);
		
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

		// 比對資料是否正確 (default sprintId = 1)
		ArrayList<UnplanObject> unplans = SprintObject.get(sprintId).getUnplans();
		String expected = genXML(String.valueOf(sprintId), unplans);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	// case 2: One sprint with No UnplanItem
	@Test
	public void testOneSprint() {
		// create one sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long sprintId = mCS.getSprintsId().get(0);
		String selectedSprint = Long.toString(sprintId);
		
		// ================== set parameter info ====================
		addRequestParameter("SprintID", selectedSprint);

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

		// 比對資料是否正確 (sprintID = 1)
		ArrayList<UnplanObject> unplans = SprintObject.get(sprintId).getUnplans();
		String expected = genXML(selectedSprint, unplans);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	// case 3: One sprint with One UnplanItem
	@Test
	public void testOneSprintWithOneUnplan() {
		// create one sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// creare one unplan
		mCUI = new CreateUnplanItem(1, mCP, mCS);
		mCUI.exe();
		
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long sprintId = mCS.getSprintsId().get(0);
		String selectedSprint = Long.toString(sprintId);

		// ================== set parameter info ====================
		addRequestParameter("SprintID", selectedSprint);

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
		ArrayList<UnplanObject> unplans = SprintObject.get(sprintId).getUnplans();
		String expected = genXML(selectedSprint, unplans);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	// case 4: One sprint with Two UnplanItem
	@Test
	public void testOneSprintWithTwoUnplan() {
		// create one sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// create two unplan
		mCUI = new CreateUnplanItem(2, mCP, mCS);
		mCUI.exe();

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long sprintId = mCS.getSprintsId().get(0);
		String selectedSprint = Long.toString(sprintId);
		
		// ================== set parameter info ====================
		addRequestParameter("SprintID", selectedSprint);

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
		ArrayList<UnplanObject> unplans = SprintObject.get(sprintId).getUnplans();
		String expected = genXML(selectedSprint, unplans);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	// case 5: Two sprint with One UnplanItem
	@Test
	public void testTwoSprintWithOneUnplan() {
		// create two sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();
		
		// create one unplan
		mCUI = new CreateUnplanItem(1, mCP, mCS);
		mCUI.exe();

		// (I) test Sprint 1

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long sprintId = mCS.getSprintsId().get(0);
		String selectedSprint = Long.toString(sprintId);

		// ================== set parameter info ====================
		addRequestParameter("SprintID", selectedSprint);

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
		ArrayList<UnplanObject> unplans = SprintObject.get(sprintId).getUnplans();
		String expected = genXML(selectedSprint, unplans);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (II) test Sprint 2

		// 執行下一次的 action 必須做此動作,否則 response 內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		project = mCP.getAllProjects().get(0);
		sprintId = mCS.getSprintsId().get(1);
		selectedSprint = Long.toString(sprintId);

		// ================== set parameter info ====================
		addRequestParameter("SprintID", selectedSprint);

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
		unplans = SprintObject.get(sprintId).getUnplans();
		expected = genXML(selectedSprint, unplans);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	// case 6: Two sprint with Two Unplan
	public void testTwoSprintWithTwoUnplan() {
		// create two sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		//create two unplan
		mCUI = new CreateUnplanItem(2, mCP, mCS);
		mCUI.exe();

		// (I) test Sprint 1

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long sprintId = mCS.getSprintsId().get(0);
		String selectedSprint = Long.toString(sprintId);

		// ================== set parameter info ====================
		addRequestParameter("SprintID", selectedSprint);

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
		ArrayList<UnplanObject> unplans = SprintObject.get(sprintId).getUnplans();
		String expected = genXML(selectedSprint, unplans);
		String actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);

		// (II) test Sprint 2

		// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		project = mCP.getAllProjects().get(0);
		sprintId = mCS.getSprintsId().get(1);
		selectedSprint = Long.toString(sprintId);

		// ================== set parameter info ====================
		addRequestParameter("SprintID", selectedSprint);

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
		unplans = SprintObject.get(sprintId).getUnplans();
		expected = genXML(selectedSprint, unplans);
		actualed = response.getWriterBuffer().toString();
		assertEquals(expected, actualed);
	}

	// 產生某一個 sprint 下的所有 unplan item(s)
	private String genXML(String selectedSprint, ArrayList<UnplanObject> unplans) {
		StringBuilder result = new StringBuilder();

		result.append("<UnplannedItems>");
		// sprint
		result.append("<Sprint>");
		result.append("<Id>").append(selectedSprint).append("</Id>");
		result.append("<Name>Sprint ").append(selectedSprint).append("</Name>");
		result.append("</Sprint>");
		// unplan item
		for (UnplanObject unplan : unplans) {
			result.append("<UnplannedItem>");
			result.append("<Id>").append(unplan.getId()).append("</Id>");
			result.append("<Link></Link>");
			result.append("<Name>").append(unplan.getName()).append("</Name>");
			result.append("<SprintID>").append(unplan.getSprintId()).append("</SprintID>");
			result.append("<Estimate>").append(unplan.getEstimate()).append("</Estimate>");
			result.append("<Status>").append(unplan.getStatusString()).append("</Status>");
			result.append("<ActualHour>").append(unplan.getActual()).append("</ActualHour>");
			result.append("<Handler>").append(unplan.getHandlerName()).append("</Handler>");
			result.append("<Partners>").append(unplan.getPartnersUsername()).append("</Partners>");
			result.append("<Notes>").append(unplan.getNotes()).append("</Notes>");
			result.append("</UnplannedItem>");
		}
		//
		result.append("</UnplannedItems>");
		return result.toString();
	}

}
