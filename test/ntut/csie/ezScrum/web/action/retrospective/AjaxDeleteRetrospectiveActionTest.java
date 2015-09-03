package ntut.csie.ezScrum.web.action.retrospective;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.dao.RetrospectiveDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxDeleteRetrospectiveActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	
	private Configuration mConfig;
	
	private String mActionPath = "/ajaxDeleteRetrospective";
	
	public AjaxDeleteRetrospectiveActionTest(String testMethod) {
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
		mCP = null;
		mCS = null;
		mConfig = null;
	}
	
	// case1: delete One good retrospective
	public void testDeleteRetrospectiveWith1g() throws Exception {
		
		mCS = new CreateSprint(1, mCP);
		mCS.exe();
		
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
		IProject project = mCP.getProjectList().get(0);		
		String sprintID = "1";
		long issueID = goodRetrospectiveId;
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(issueID));		
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
    	String expected = genXML(sprintID, String.valueOf(issueID));
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
    	// 從DB裡面抓回剛剛的資料, 應該為NULL
    	RetrospectiveObject retrospectiveFromDB = RetrospectiveDAO.getInstance().get(goodRetrospectiveId);
    	assertNull(retrospectiveFromDB);
	}
	
	// case2: delete One improvement retrospective
	public void testDeleteRetrospectiveWith1i() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe();
		
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
		IProject project = mCP.getProjectList().get(0);		
		String sprintID = "1";
		long issueID = improvementRetrospectiveId;
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(issueID));		
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
    	String expected = genXML(sprintID, String.valueOf(issueID));
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
    	// 從DB裡面抓回剛剛的資料, 應該為NULL
    	RetrospectiveObject retrospectiveFromDB = RetrospectiveDAO.getInstance().get(improvementRetrospectiveId);
    	assertNull(retrospectiveFromDB);
    }	

	// case3: delete One good & One improvement retrospective
	public void testDeleteRetrospectiveWith1g1i() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe();	

		long projectId = mCP.getAllProjects().get(0).getId();
		long sprintId = mCS.getSprintsId().get(0);
		
		RetrospectiveObject goodRetrospective = new RetrospectiveObject(projectId);
		goodRetrospective.setName("TEST_RETROSPECTIVE_NAME")
		                 .setDescription("TEST_RETROSPECTIVE_DESCRIPTION")
		                 .setType(RetrospectiveObject.TYPE_GOOD)
		                 .setSprintId(sprintId)
		                 .save();
		
		RetrospectiveObject improvementRetrospective = new RetrospectiveObject(projectId);
		improvementRetrospective.setName("TEST_RETROSPECTIVE_NAME")
		                        .setDescription("TEST_RETROSPECTIVE_DESCRIPTION")
		                        .setType(RetrospectiveObject.TYPE_IMPROVEMENT)
		                        .setSprintId(sprintId)
		                        .save();
		long goodRetrospectiveId = goodRetrospective.getId();
		long improvementRetrospectiveId = improvementRetrospective.getId();
		
		// (I) 先刪除good 
		
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);		
		String sprintID = "1";
		long issueID = goodRetrospectiveId;
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(issueID));		
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
    	String expected = genXML(sprintID, String.valueOf(issueID));
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
    	// 從DB裡面抓回剛剛的資料, 應該為NULL
    	RetrospectiveObject retrospectiveFromDB = RetrospectiveDAO.getInstance().get(goodRetrospectiveId);
    	assertNull(retrospectiveFromDB);
		// (II) 再刪除improvement
    	
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
		
		// ================ set initial data =======================
		issueID = improvementRetrospectiveId;
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(issueID));		
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
    	expected = genXML(sprintID, String.valueOf(issueID));
    	assertEquals(expected, response.getWriterBuffer().toString());
    	
    	// 從DB裡面抓回剛剛的資料, 應該為NULL
    	retrospectiveFromDB = RetrospectiveDAO.getInstance().get(improvementRetrospectiveId);
    	assertNull(retrospectiveFromDB);
    }	
	
	// case4: delete One improvement & One good retrospective
	public void testDeleteRetrospectiveWith1i1g() throws Exception {
		mCS = new CreateSprint(1, mCP);
		mCS.exe();	
		
		long projectId = mCP.getAllProjects().get(0).getId();
		long sprintId = mCS.getSprintsId().get(0);
		
		RetrospectiveObject improvementRetrospective = new RetrospectiveObject(projectId);
		improvementRetrospective.setName("TEST_RETROSPECTIVE_NAME")
		                        .setDescription("TEST_RETROSPECTIVE_DESCRIPTION")
		                        .setType(RetrospectiveObject.TYPE_IMPROVEMENT)
		                        .setSprintId(sprintId)
		                        .save();
		
		RetrospectiveObject goodRetrospective = new RetrospectiveObject(projectId);
		goodRetrospective.setName("TEST_RETROSPECTIVE_NAME")
		                 .setDescription("TEST_RETROSPECTIVE_DESCRIPTION")
		                 .setType(RetrospectiveObject.TYPE_GOOD)
		                 .setSprintId(sprintId)
		                 .save();
		
		
		long goodRetrospectiveId = goodRetrospective.getId();
		long improvementRetrospectiveId = improvementRetrospective.getId();
		// (I) 先刪除improve
		
		// ================ set initial data =======================
		IProject project = mCP.getProjectList().get(0);		
		String sprintID = "1";
		long issueID = improvementRetrospectiveId;
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(issueID));		
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
    	String expected = genXML(sprintID, String.valueOf(issueID));
    	assertEquals(expected, response.getWriterBuffer().toString());
    	assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(issueID, IssueTypeEnum.TYPE_RETROSPECTIVE).size());
    	
    	// 從DB裡面抓回剛剛的資料, 應該為NULL
    	RetrospectiveObject retrospectiveFromDB = RetrospectiveDAO.getInstance().get(improvementRetrospectiveId);
    	assertNull(retrospectiveFromDB);
    	
		// (II) 再刪除good
    	
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
		
		// ================ set initial data =======================
		issueID = goodRetrospectiveId;
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(issueID));		
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
    	expected = genXML(sprintID, String.valueOf(issueID));
    	assertEquals(expected, response.getWriterBuffer().toString());
    	assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(issueID, IssueTypeEnum.TYPE_RETROSPECTIVE).size());
    	
    	// 從DB裡面抓回剛剛的資料, 應該為NULL
    	retrospectiveFromDB = RetrospectiveDAO.getInstance().get(goodRetrospectiveId);
    	assertNull(retrospectiveFromDB); 	
	}
	
	private String genXML(String sprintID, String issueID) {
 		StringBuilder result = new StringBuilder("");
		
		result.append("<DeleteRetrospective><Result>true</Result><Retrospective>");
		result.append("<Id>" + issueID + "</Id>");
		result.append("<SprintID>" + sprintID + "</SprintID>");
		result.append("</Retrospective></DeleteRetrospective>");	
		
		return result.toString();
	}
}
