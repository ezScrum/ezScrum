package ntut.csie.ezScrum.web.action.retrospective;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.core.util.DateUtil;
import servletunit.struts.MockStrutsTestCase;

public class GetAddNewRetrospectiveInfoActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	
	public GetAddNewRetrospectiveInfoActionTest(String testMethod) {
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
		setRequestPathInfo("/getAddNewRetrospectiveInfo");

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
	
	public void testRetrospectiveInfo_NoSprint(){
		// Get Project
		ProjectObject project = mCP.getAllProjects().get(0);
		// ================ set session info ========================
		request.getSession().setAttribute("Project", project);
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();
		
		// Expected String
		StringBuilder expectedStringBuilder = new StringBuilder();
		expectedStringBuilder.append("<Sprints>")
		                     .append("<Sprint>")
		                     .append("<Id>0</Id>")
		                     .append("<Name>No sprint</Name>")
		                     .append("<Start>false</Start>")
		                     .append("<Edit>\"0\"</Edit>")
		                     .append("<Goal></Goal>")
		                     .append("</Sprint>")
		                     .append("</Sprints>");
		// assert
		assertEquals(expectedStringBuilder.toString(), response.getWriterBuffer().toString());
	}
	
	public void testRetrospectiveInfo(){
		// Get Project
		ProjectObject project = mCP.getAllProjects().get(0);
		
		// Create 3 Sprints
		mCS = new CreateSprint(3, mCP);
		mCS.exe();
		
		// ================ set session info ========================
		request.getSession().setAttribute("Project", project);
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		// ================ set session info ========================
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		actionPerform(); // 執行 action

		// 驗證回傳 path
		verifyForwardPath(null);
		verifyForward(null);
		verifyNoActionErrors();
		
		// Expected String
		StringBuilder expectedStringBuilder = new StringBuilder();
		
		// Get Sprints
		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		ArrayList<SprintObject> sprints = sprintPlanHelper.getSprints();
		
		Date today = new Date();
		TranslateSpecialChar translateSpecialChar = new TranslateSpecialChar();
		
		// 所有 Sprint 封裝成 XML 給 Ext(ComboBox) 使用
		expectedStringBuilder.append("<Sprints>");
		
		for (SprintObject sprint : sprints) {
			expectedStringBuilder.append("<Sprint>");
			expectedStringBuilder.append("<Id>" + String.valueOf(sprint.getId()) + "</Id>");
			expectedStringBuilder.append("<Name>Sprint #" + String.valueOf(sprint.getId()) + "</Name>");

			/*-----------------------------------------------------------
			 *	判斷此Sprint是否已經開始了
			-------------------------------------------------------------*/
			String startDateString = sprint.getStartDateString();
			Date startDate = DateUtil.dayFilter(startDateString);
			expectedStringBuilder.append("<Start>");
			boolean start = startDate.after(today);
			expectedStringBuilder.append((start ? "true" : "false"));
			expectedStringBuilder.append("</Start>");

			/*------------------------------------------------------------
			 *  判斷此Sprint是否已過期，是否可以編輯
			 -------------------------------------------------------------*/
			String dueDateString = sprint.getDueDateString();
			
			Date endDate = DateUtil.dayFilter(dueDateString);
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			String transToday_String = format.format(today);
			Date transToday_Date = DateUtil.dayFilter(transToday_String); 
			
			expectedStringBuilder.append("<Edit>");
			boolean end = transToday_Date.after(endDate);
			
			expectedStringBuilder.append((!end ? "1" : "0"));
			expectedStringBuilder.append("</Edit>");

			/*-----------------------------------------------------------
			 *	加入Sprint Goal
			-------------------------------------------------------------*/
			expectedStringBuilder.append("<Goal>");
			expectedStringBuilder.append(translateSpecialChar.TranslateXMLChar(sprint.getGoal()));
			expectedStringBuilder.append("</Goal>");

			expectedStringBuilder.append("</Sprint>");
		}
		expectedStringBuilder.append("</Sprints>");
		
		// assert
		assertEquals(expectedStringBuilder.toString(), response.getWriterBuffer().toString());
	}
}
