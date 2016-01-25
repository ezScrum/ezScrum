package ntut.csie.ezScrum.web.action.retrospective;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import servletunit.struts.MockStrutsTestCase;

public class ShowRetrospectiveActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private ArrayList<RetrospectiveObject> mGoodRetrospectives;
	private ArrayList<RetrospectiveObject> mImprovementRetrospectives;
	
	public ShowRetrospectiveActionTest(String testMethod) {
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
		
		// Initial ArrayList
		mGoodRetrospectives = new ArrayList<RetrospectiveObject>();
		mImprovementRetrospectives = new ArrayList<RetrospectiveObject>();

		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent")); // 設定讀取的
		// struts-config
		// 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/showRetrospective2");

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
		
		mGoodRetrospectives.clear();
		mImprovementRetrospectives.clear();

		super.tearDown();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mConfig = null;
		mGoodRetrospectives = null;
		mImprovementRetrospectives = null;
	}

	// case 1: no sprint
	public void testNoSprint() throws Exception {
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("sprintID", String.valueOf(-1));
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
  
    	// 比對資料是否正確(sprintID = -1)
    	String expected = "<Retrospectives><Sprint><Id>-1</Id><Name>Sprint #-1</Name></Sprint></Retrospectives>";
    	assertEquals(expected, response.getWriterBuffer().toString());   	    	
	}	
	
	// case 2: One sprint with no retrospective
	public void testOneSprint() throws Exception {	
		mCS = new CreateSprint(1, mCP);
		mCS.exe(); // 新增一個 Sprint		
		
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		// 測試不代入 sprint ID
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
  
    	// 比對資料是否正確(sprintID = -1)
    	String expected = "<Retrospectives><Sprint><Id>null</Id><Name>Sprint #null</Name></Sprint></Retrospectives>";
    	assertEquals(expected, response.getWriterBuffer().toString());   	    	
	}		
	
	// case 3: One sprint with 1 Good
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
		
		// Add Retrospective to List
		mGoodRetrospectives.add(goodRetrospective);
		
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("sprintID", String.valueOf(sprintId)); // 取得第一筆 SprintPlan
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
    	String expected = genXML(sprintId);
    	assertEquals(expected, response.getWriterBuffer().toString());	   	    	
	}			
	
	// case 4: One sprint with 1 Improvement
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

		// Add Retrospective to List
		mImprovementRetrospectives.add(improvementRetrospective);
		
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("sprintID", String.valueOf(sprintId)); // 取得第一筆 SprintPlan
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
    	String expected = genXML(sprintId);
     	assertEquals(expected, response.getWriterBuffer().toString());	   	    	
	}	
	
	// case 5: One sprint with 1 Good + 1 Improvement
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
		
		// Add Retrospective to List
		mGoodRetrospectives.add(goodRetrospective);
		mImprovementRetrospectives.add(improvementRetrospective);
		
		// ================ set initial data =======================
		ProjectObject project = mCP.getAllProjects().get(0);
		// ================ set initial data =======================

		// ================== set parameter info ====================
		addRequestParameter("sprintID", String.valueOf(sprintId)); // 取得第一筆 SprintPlan
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
    	String expected = genXML(sprintId);
    	assertEquals(expected, response.getWriterBuffer().toString());	   	    	
    }
	
	private String genXML(long sprintId) {			
		StringBuilder sb = new StringBuilder();
		sb.append("<Retrospectives><Sprint><Id>" + sprintId + "</Id><Name>Sprint #" + sprintId + "</Name></Sprint>");
		
		// Good
		for(RetrospectiveObject goodRetrospective : mGoodRetrospectives){
			if (goodRetrospective.getSprintId() == sprintId) {
				sb.append("<Retrospective>");
				sb.append("<Id>" + goodRetrospective.getId() + "</Id>");
				sb.append("<Link></Link>");
				sb.append("<SprintID>" + goodRetrospective.getSprintId() + "</SprintID>");
				sb.append("<Name>" + TranslateSpecialChar.TranslateXMLChar(goodRetrospective.getName()) + "</Name>");
				sb.append("<Type>" + goodRetrospective.getType() + "</Type>");
				sb.append("<Description>" + TranslateSpecialChar.TranslateXMLChar(goodRetrospective.getDescription()) + "</Description>");
				sb.append("<Status>" + goodRetrospective.getStatus() + "</Status>");
				sb.append("</Retrospective>");
			}
		}
		
		// Improvement
		for (RetrospectiveObject improvementRetrospective : mImprovementRetrospectives) {
			if (improvementRetrospective.getSprintId() == sprintId) {
				sb.append("<Retrospective>");
				sb.append("<Id>" + improvementRetrospective.getId() + "</Id>");
				sb.append("<Link></Link>");
				sb.append("<SprintID>" + improvementRetrospective.getSprintId() + "</SprintID>");
				sb.append("<Name>" + TranslateSpecialChar.TranslateXMLChar(improvementRetrospective.getName()) + "</Name>");
				sb.append("<Type>" + improvementRetrospective.getType() + "</Type>");
				sb.append("<Description>" + TranslateSpecialChar.TranslateXMLChar(improvementRetrospective.getDescription()) + "</Description>");
				sb.append("<Status>" + improvementRetrospective.getStatus() + "</Status>");
				sb.append("</Retrospective>");
			}
		}
		sb.append("</Retrospectives>");
		return sb.toString();
	}
}
