package ntut.csie.ezScrum.web.action.report;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.jcis.core.util.DateUtil;
import servletunit.struts.MockStrutsTestCase;

public class GetPastSprintsInfoActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private Configuration mConfig;
	private ProjectObject mProject;
	
	public GetPastSprintsInfoActionTest(String testMethod) {
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

		mProject = mCP.getAllProjects().get(0);
		
		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent")); // 設定讀取的
		// struts-config
		// 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/getPastSprintInfo");

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
		mConfig = null;
		mProject = null;
	}
	
	public void testPastRetrospectiveInfo_NoSprint(){
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
	
	public void testPastRetrospectiveInfo() {
		// Get Project
		ProjectObject project = mCP.getAllProjects().get(0);

		// Create 3 Sprints
		SprintObject sprint1 = new SprintObject(mProject.getId());
		sprint1.setStartDate("2015/8/1")
		       .setDueDate("2015/8/14")
		       .setGoal("Sprint# 1")
		       .save();
		
		Calendar calendar = Calendar.getInstance();
		SprintObject sprint2 = new SprintObject(mProject.getId());
		sprint2.setStartDate(DateUtil.formatBySlashForm(calendar.getTime()));
		calendar.add(Calendar.DAY_OF_YEAR, 14);
		sprint2.setDueDate(DateUtil.formatBySlashForm(calendar.getTime()))
			   .setGoal("Sprint# 2")       
		       .save();
		
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		SprintObject sprint3 = new SprintObject(mProject.getId());
		sprint3.setStartDate(DateUtil.formatBySlashForm(calendar.getTime()));
		calendar.add(Calendar.DAY_OF_YEAR, 14);
		sprint3.setDueDate(DateUtil.formatBySlashForm(calendar.getTime()))
	           .setGoal("Sprint# 3")   
	           .save();
		
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
		
		expectedStringBuilder.append("<Sprints>");
		expectedStringBuilder.append("<Sprint>");
		expectedStringBuilder.append("<Id>").append(sprint2.getId()).append("</Id>");
		expectedStringBuilder.append("<Name>Sprint #").append(sprint2.getId()).append("</Name>");
		expectedStringBuilder.append("<Start>").append("false").append("</Start>");
		expectedStringBuilder.append("<Edit>").append("1").append("</Edit>");
		expectedStringBuilder.append("<Goal>").append(sprint2.getGoal()).append("</Goal>");
		expectedStringBuilder.append("</Sprint>");
		expectedStringBuilder.append("<Sprint>");
		expectedStringBuilder.append("<Id>").append(sprint1.getId()).append("</Id>");
		expectedStringBuilder.append("<Name>Sprint #").append(sprint1.getId()).append("</Name>");
		expectedStringBuilder.append("<Start>").append("false").append("</Start>");
		expectedStringBuilder.append("<Edit>").append("0").append("</Edit>");
		expectedStringBuilder.append("<Goal>").append(sprint1.getGoal()).append("</Goal>");
		expectedStringBuilder.append("</Sprint>");
		expectedStringBuilder.append("</Sprints>");

		// assert
		assertEquals(expectedStringBuilder.toString(), response.getWriterBuffer().toString());
	}
}
