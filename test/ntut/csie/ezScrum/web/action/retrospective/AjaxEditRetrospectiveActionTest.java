package ntut.csie.ezScrum.web.action.retrospective;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxEditRetrospectiveActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
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
		mCP.exeCreateForDb(); // 新增一測試專案

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
		ini.exe();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mConfig = null;
	}

	// case 1: One sprint with editing Good retrospective
	// 將  name, type, description, status 更新
	public void testEditGood() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint			
		
		long projectId = mCP.getAllProjects().get(0).getId();
		long sprintId = mCS.getSprintsId().get(0);

		RetrospectiveObject goodRetrospective = new RetrospectiveObject(projectId);
		goodRetrospective.setName("TEST_RETROSPECTIVE_NAME")
		                 .setDescription("TEST_RETROSPECTIVE_DESCRIPTION")
		                 .setType(RetrospectiveObject.TYPE_GOOD)
		                 .setSprintId(sprintId)
		                 .save();
		
		long goodRetrospectiveId = goodRetrospective.getId();

		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		String retrospectiveName = mPrefix + "updateName";
		String retrospectiveType = ScrumEnum.IMPROVEMENTS_ISSUE_TYPE;
		String retrospectiveDescription = mPrefix + "updateDescription";
		String retrospectiveStatus = mStatus[1];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(goodRetrospectiveId));
		addRequestParameter("Name", retrospectiveName);
		addRequestParameter("SprintID", "#" +  String.valueOf(sprintId));
		addRequestParameter("Type", retrospectiveType);
		addRequestParameter("Description", retrospectiveDescription);
		addRequestParameter("Status", retrospectiveStatus);
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
		String expected = genXML(sprintId, goodRetrospectiveId, retrospectiveName, retrospectiveType, retrospectiveDescription, retrospectiveStatus);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 2: One sprint with adding Improvement retrospective
	// 將  name, type, description, status 更新	
	public void testEditImprovement() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint		
		
		long projectId = mCP.getAllProjects().get(0).getId();
		long sprintId = mCS.getSprintsId().get(0);
		
		RetrospectiveObject improvementRetrospective = new RetrospectiveObject(projectId);
		improvementRetrospective.setName("TEST_RETROSPECTIVE_NAME")
		                        .setDescription("TEST_RETROSPECTIVE_DESCRIPTION")
		                        .setType(RetrospectiveObject.TYPE_IMPROVEMENT)
		                        .setSprintId(sprintId)
		                        .save();
		
		long improvementRetrospectiveId = improvementRetrospective.getId();
		                        
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		String retrospectiveName = mPrefix + "updateName";
		String retrospectiveType = ScrumEnum.GOOD_ISSUE_TYPE;
		String retrospectiveDescription = mPrefix + "updateDescription";
		String retrospectiveStatus = mStatus[1];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(improvementRetrospectiveId));
		addRequestParameter("Name", retrospectiveName);
		addRequestParameter("SprintID", "#" + sprintId);
		addRequestParameter("Type", retrospectiveType);
		addRequestParameter("Description", retrospectiveDescription);
		addRequestParameter("Status", retrospectiveStatus);
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
		String expected = genXML(sprintId, improvementRetrospectiveId, retrospectiveName, retrospectiveType, retrospectiveDescription, retrospectiveStatus);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 3: One sprint with editing Good retrospective twice
	// 將  name, type, description, status 更新後再更新回原資料		
	public void testEditGood2() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint				
		
		long projectId = mCP.getAllProjects().get(0).getId();
		long sprintId = mCS.getSprintsId().get(0);
		
		RetrospectiveObject goodRetrospective = new RetrospectiveObject(projectId);
		goodRetrospective.setName("TEST_RETROSPECTIVE_NAME")
		                 .setDescription("TEST_RETROSPECTIVE_DESCRIPTION")
		                 .setType(RetrospectiveObject.TYPE_GOOD)
		                 .setSprintId(sprintId)
		                 .save();
		
		long goodRetrospectiveId = goodRetrospective.getId();

		/*
		 * (I)
		 */
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		String retrospectiveName = mPrefix + "updateName";
		String retrospectiveType = ScrumEnum.IMPROVEMENTS_ISSUE_TYPE;
		String retrospectiveDescription = mPrefix + "updateDescription";
		String retrospectiveStatus = mStatus[2];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(goodRetrospectiveId));
		addRequestParameter("Name", retrospectiveName);
		addRequestParameter("SprintID", "#" + sprintId);
		addRequestParameter("Type", retrospectiveType);
		addRequestParameter("Description", retrospectiveDescription);
		addRequestParameter("Status", retrospectiveStatus);
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
		retrospectiveName = mPrefix + "name";
		retrospectiveType = ScrumEnum.GOOD_ISSUE_TYPE;
		retrospectiveDescription = mPrefix + "description";
		retrospectiveStatus = mStatus[0];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(goodRetrospectiveId));
		addRequestParameter("Name", retrospectiveName);
		addRequestParameter("SprintID", "#" + sprintId);
		addRequestParameter("Type", retrospectiveType);
		addRequestParameter("Description", retrospectiveDescription);
		addRequestParameter("Status", retrospectiveStatus);
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
		String expected = genXML(sprintId, goodRetrospectiveId, retrospectiveName, retrospectiveType, retrospectiveDescription, retrospectiveStatus);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	// case 4: One sprint with editing Improvement retrospective twice
	// 將  name, type, description, status 更新後再更新回原資料	
	public void testEditImporvement2() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint		
		
		long projectId = mCP.getAllProjects().get(0).getId();
		long sprintId = mCS.getSprintsId().get(0);
		
		RetrospectiveObject improvementRetrospective = new RetrospectiveObject(projectId);
		improvementRetrospective.setName("TEST_RETROSPECTIVE_NAME")
		                        .setDescription("TEST_RETROSPECTIVE_DESCRIPTION")
		                        .setType(RetrospectiveObject.TYPE_IMPROVEMENT)
		                        .setSprintId(sprintId)
		                        .save();

		long improvementRetrospectiveId = improvementRetrospective.getId();
		
		/*
		 * (I)
		 */
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		String retrospectiveName = mPrefix + "updateName";
		String retrospectiveType = ScrumEnum.GOOD_ISSUE_TYPE;
		String retrospectiveDescription = mPrefix + "updateDescription";
		String retrospectiveStatus = mStatus[3];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(improvementRetrospectiveId));
		addRequestParameter("Name", retrospectiveName);
		addRequestParameter("SprintID", "#" + sprintId);
		addRequestParameter("Type", retrospectiveType);
		addRequestParameter("Description", retrospectiveDescription);
		addRequestParameter("Status", retrospectiveStatus);
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
		retrospectiveName = mPrefix + "name";
		retrospectiveType = ScrumEnum.IMPROVEMENTS_ISSUE_TYPE;
		retrospectiveDescription = mPrefix + "description";
		retrospectiveStatus = mStatus[0];
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(improvementRetrospectiveId));
		addRequestParameter("Name", retrospectiveName);
		addRequestParameter("SprintID", "#" + sprintId);
		addRequestParameter("Type", retrospectiveType);
		addRequestParameter("Description", retrospectiveDescription);
		addRequestParameter("Status", retrospectiveStatus);
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
		String expected = genXML(sprintId, improvementRetrospectiveId, retrospectiveName, retrospectiveType, retrospectiveDescription, retrospectiveStatus);
		assertEquals(expected, response.getWriterBuffer().toString());
	}

	private String genXML(long sprintId, long retrospectiveId, String name, String type, String description, String status) {
		StringBuilder result = new StringBuilder("");
		result.append("<EditRetrospective><Result>true</Result><Retrospective>");
		result.append("<Id>" + retrospectiveId + "</Id>");
		result.append("<Link>" + "/ezScrum/showIssueInformation.do?issueID=" + retrospectiveId + "</Link>");
		result.append("<SprintID>" + sprintId + "</SprintID>");
		result.append("<Name>" + name + "</Name>");
		result.append("<Type>" + type + "</Type>");
		result.append("<Description>" + description + "</Description>");
		result.append("<Status>" + status + "</Status>");
		result.append("</Retrospective></EditRetrospective>");
		return result.toString();
	}
}
