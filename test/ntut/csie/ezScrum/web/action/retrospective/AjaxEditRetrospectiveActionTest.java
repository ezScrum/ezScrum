package ntut.csie.ezScrum.web.action.retrospective;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRetrospective;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxEditRetrospectiveActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateRetrospective mCR;

	private Configuration mConfig;

	private String mPrefix = "TEST_RETROSPECTIVE_EDIT_";
	private String[] mStatus = {"new", "closed", "resolved", "assigned"};
	private String actionPath = "/ajaxEditRetrospective";

	public AjaxEditRetrospectiveActionTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		mCP = new CreateProject(1);
		mCP.exeCreate(); // 新增一測試專案

		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent")); // 設定讀取的
		// struts-config
		// 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(actionPath);

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
		mCP = null;
		mCS = null;
		mCR = null;
		mConfig = null;
	}

	// case 1: One sprint with editing Good retrospective
	// 將  name, type, description, status 更新
	public void testEditGood() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint				
		mCR = new CreateRetrospective(1, 0, mCP, mCS);
		mCR.exe();

		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		String sprintID = "1";
		String issueID = "1";
		String rName = mPrefix + "updateName";
		String rID = "1";
		String rType = ScrumEnum.IMPROVEMENTS_ISSUE_TYPE;
		String rDesc = mPrefix + "updateDescription";
		String rStatus = mStatus[1];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);
		addRequestParameter("Status", rStatus);
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
		String[] a = {sprintID, rID, rName, rType, rDesc, rStatus};
		String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5]);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 2: One sprint with adding Improvement retrospective
	// 將  name, type, description, status 更新	
	public void testEditImprovement() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint		
		mCR = new CreateRetrospective(0, 1, mCP, mCS);
		mCR.exe();

		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		String sprintID = "1";
		String issueID = "1";
		String rName = mPrefix + "updateName";
		String rID = "1";
		String rType = ScrumEnum.GOOD_ISSUE_TYPE;
		String rDesc = mPrefix + "updateDescription";
		String rStatus = mStatus[1];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);
		addRequestParameter("Status", rStatus);
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
		String[] a = {sprintID, rID, rName, rType, rDesc, rStatus};
		String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5]);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 3: One sprint with editing Good retrospective twice
	// 將  name, type, description, status 更新後再更新回原資料		
	public void testEditGood2() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint				
		mCR = new CreateRetrospective(1, 0, mCP, mCS);
		mCR.exe();

		/*
		 * (I)
		 */
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		String sprintID = "1";
		String issueID = "1";
		String rName = mPrefix + "updateName";
		String rID = "1";
		String rType = ScrumEnum.IMPROVEMENTS_ISSUE_TYPE;
		String rDesc = mPrefix + "updateDescription";
		String rStatus = mStatus[2];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);
		addRequestParameter("Status", rStatus);
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

		/*
		 * (II)
		 */
		// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		rName = mPrefix + "name";
		rID = "1";
		rType = ScrumEnum.GOOD_ISSUE_TYPE;
		rDesc = mPrefix + "description";
		rStatus = mStatus[0];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);
		addRequestParameter("Status", rStatus);
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
		String[] a = {sprintID, rID, rName, rType, rDesc, rStatus};
		String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5]);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 4: One sprint with editing Improvement retrospective twice
	// 將  name, type, description, status 更新後再更新回原資料	
	public void testEditImporvement2() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint		
		mCR = new CreateRetrospective(0, 1, mCP, mCS);
		mCR.exe();

		/*
		 * (I)
		 */
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);
		String sprintID = "1";
		String issueID = "1";
		String rName = mPrefix + "updateName";
		String rID = "1";
		String rType = ScrumEnum.GOOD_ISSUE_TYPE;
		String rDesc = mPrefix + "updateDescription";
		String rStatus = mStatus[3];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);
		addRequestParameter("Status", rStatus);
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

		/*
		 * (II)
		 */
		// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();

		// ================ set initial data =======================
		rName = mPrefix + "name";
		rID = "1";
		rType = ScrumEnum.IMPROVEMENTS_ISSUE_TYPE;
		rDesc = mPrefix + "description";
		rStatus = mStatus[0];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", issueID);
		addRequestParameter("Name", rName);
		addRequestParameter("SprintID", "#" + sprintID);
		addRequestParameter("Type", rType);
		addRequestParameter("Description", rDesc);
		addRequestParameter("Status", rStatus);
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
		String[] a = {sprintID, rID, rName, rType, rDesc, rStatus};
		String expected = genXML(a[0], a[1], a[2], a[3], a[4], a[5]);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	private String genXML(String sprintID, String issueID, String name, String type, String desc, String status) {
		StringBuilder result = new StringBuilder("");

		result.append("<EditRetrospective><Result>true</Result><Retrospective>");
		result.append("<Id>" + issueID + "</Id>");
		result.append("<Link>" + "/ezScrum/showIssueInformation.do?issueID=" + issueID + "</Link>");
		result.append("<SprintID>" + sprintID + "</SprintID>");
		result.append("<Name>" + name + "</Name>");
		result.append("<Type>" + type + "</Type>");
		result.append("<Description>" + desc + "</Description>");
		result.append("<Status>" + status + "</Status>");
		result.append("</Retrospective></EditRetrospective>");

		return result.toString();
	}
}
