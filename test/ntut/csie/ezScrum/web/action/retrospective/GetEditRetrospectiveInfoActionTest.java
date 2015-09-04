package ntut.csie.ezScrum.web.action.retrospective;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import servletunit.struts.MockStrutsTestCase;

public class GetEditRetrospectiveInfoActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	
	private Configuration mConfig;
	
	private String mActionPath = "/getEditRetrospectiveInfo";
	
	public GetEditRetrospectiveInfoActionTest(String testMethod) {
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
	
	// case 1: One sprint with 1 Good
	public void testOneSprint1g() throws Exception {	
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
		long retrospectiveId = goodRetrospectiveId;
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(retrospectiveId));		
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
    	String expected = genXML(goodRetrospective);
    	assertEquals(expected, response.getWriterBuffer().toString());	   	    	
	}			
	
	// case 2: One sprint with 1 Improvement
	public void testOneSprint1i() throws Exception {	
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
		long retrospectiveId = improvementRetrospectiveId;
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(retrospectiveId));		
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
    	String expected = genXML(improvementRetrospective);
     	assertEquals(expected, response.getWriterBuffer().toString());	   	    	
	}	
	
	// case 3: One sprint with 1 Good + 1 Improvement
	public void testOneSprint1g1i() throws Exception {	
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
		
		RetrospectiveObject improvementRetrospective = new RetrospectiveObject(projectId);
		improvementRetrospective.setName("TEST_RETROSPECTIVE_NAME")
		                        .setDescription("TEST_RETROSPECTIVE_DESCRIPTION")
		                        .setType(RetrospectiveObject.TYPE_IMPROVEMENT)
		                        .setSprintId(sprintId)
		                        .save();
		
		long goodRetrospectiveId = goodRetrospective.getId();
		long improvementRetrospectiveId = improvementRetrospective.getId();
		
		// (I) 先取得good
		
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long retrospectiveId = goodRetrospectiveId;
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(retrospectiveId));		
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
    	String expected = genXML(goodRetrospective);
    	assertEquals(expected, response.getWriterBuffer().toString());	
    	
		// (II) 再取得improvement

    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
		
		// ================ set initial data =======================
		retrospectiveId = improvementRetrospectiveId;
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(retrospectiveId));		
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
    	expected = genXML(improvementRetrospective);
    	assertEquals(expected, response.getWriterBuffer().toString());	    	
	}	

	// case 4: One sprint with 1 Improvement + 1 Good
	public void testOneSprint1i1g() throws Exception {	
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
		
		RetrospectiveObject improvementRetrospective = new RetrospectiveObject(projectId);
		improvementRetrospective.setName("TEST_RETROSPECTIVE_NAME")
		                        .setDescription("TEST_RETROSPECTIVE_DESCRIPTION")
		                        .setType(RetrospectiveObject.TYPE_IMPROVEMENT)
		                        .setSprintId(sprintId)
		                        .save();
		
		long goodRetrospectiveId = goodRetrospective.getId();
		long improvementRetrospectiveId = improvementRetrospective.getId();
		
		// (I) 先取得improve
		
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		long retrospectiveId = improvementRetrospectiveId;
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(retrospectiveId));		
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
    	String expected = genXML(improvementRetrospective);
    	assertEquals(expected, response.getWriterBuffer().toString());	
    	
		// (II) 再取得 Good
    	
    	// 執行下一次的action必須做此動作,否則response內容不會更新!
		clearRequestParameters();
		response.reset();
		
		// ================ set initial data =======================
		retrospectiveId = goodRetrospectiveId;
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("issueID", String.valueOf(retrospectiveId));		
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
    	expected = genXML(goodRetrospective);
    	assertEquals(expected, response.getWriterBuffer().toString());	    	
	}	
	
	private String genXML(RetrospectiveObject retrospective) {
		StringBuilder result = new StringBuilder("");
		TranslateSpecialChar tsc = new TranslateSpecialChar();

		result.append("<EditRetrospective><Result>true</Result><Retrospective>");
		result.append("<Id>" + retrospective.getId() + "</Id>");
		result.append("<Link>" + "/ezScrum/showIssueInformation.do?issueID=" + retrospective.getId() + "</Link>");
		result.append("<SprintID>" + retrospective.getSprintId() + "</SprintID>");
		result.append("<Name>" + tsc.TranslateXMLChar(retrospective.getName()) + "</Name>");
		result.append("<Type>" + retrospective.getTypeString() + "</Type>");
		result.append("<Description>" + tsc.TranslateXMLChar(retrospective.getDescription()) + "</Description>");
		result.append("<Status>" + retrospective.getStatusString() + "</Status>");
		result.append("</Retrospective></EditRetrospective>");

		return result.toString();
	}
}
