package ntut.csie.ezScrum.web.action.report;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class ShowCheckOutIssueTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private AddStoryToSprint ASS;
	private AddTaskToStory ATS;
	private Configuration configuration;
	private final String ACTION_PATH = "/showCheckOutIssue";

	public ShowCheckOutIssueTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案

		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe(); // 新增1個Sprint到專案內

		this.ASS = new AddStoryToSprint(1, 1, this.CS, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		this.ASS.exe(); // 新增1筆Story到Sprint內

		this.ATS = new AddTaskToStory(1, 1, this.ASS, this.CP);
		this.ATS.exe(); // 新增1筆Task到Story內

		super.setUp();
		// ================ set action info ========================
		setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(this.ACTION_PATH);

		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		CopyProject copyProject = new CopyProject(this.CP);
		copyProject.exeDelete_Project(); // 刪除測試檔案
		
		configuration.setTestMode(false);
		configuration.save();

		super.tearDown();

		// ============= release ==============
		ini = null;
		copyProject = null;
		this.CP = null;
		this.CS = null;
		this.ASS = null;
		this.ATS = null;
		configuration = null;
	}

	// 測試Issue為Task的CheckOut
	public void testshowCheckOutIssue_Task() throws Exception {
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		long issueID = ATS.getTaskIDList().get(0);
		ProductBacklogMapper productBacklogMapper = new ProductBacklogMapper(project, configuration.getUserSession());
		IIssue item = productBacklogMapper.getIssue(issueID);

		// ================ set request info ========================
		String projectName = project.getName();
		request.setHeader("Referer", "?PID=" + projectName);// SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);

		addRequestParameter("issueID", String.valueOf(issueID));

		// ================ 執行 action ==============================
		actionPerform();

		// ================ assert =============================
		verifyNoActionErrors();
		verifyNoActionMessages();

		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("{\"Task\":{")
							.append("\"Id\":\"").append(item.getIssueID()).append("\",")
							.append("\"Name\":\"").append(item.getSummary()).append("\",")
							.append("\"Partners\":\"").append(item.getPartners()).append("\",")
							.append("\"Notes\":\"").append(item.getNotes()).append("\",")
							.append("\"Handler\":\"").append(configuration.USER_ID).append("\"")
							.append("},\"success\":true,\"Total\":1}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseTest.toString(), actualResponseText);
	}

	// 測試Issue為Story的CheckOut
	public void testshowCheckOutIssue_Story() throws Exception {
		// ================ set initial data =======================
		IProject project = this.CP.getProjectList().get(0);
		long issueID = ASS.getStories().get(0).getIssueID();
		ProductBacklogMapper productBacklogMapper = new ProductBacklogMapper(project, configuration.getUserSession());
		IIssue item = productBacklogMapper.getIssue(issueID);

		// ================ set request info ========================
		String projectName = project.getName();
		request.setHeader("Referer", "?PID=" + projectName);// SessionManager會對URL的參數作分析 ,未帶入此參數無法存入session
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);

		addRequestParameter("issueID", String.valueOf(issueID));

		// ================ 執行 action ==============================
		actionPerform();

		// ================ assert =============================
		verifyNoActionErrors();
		verifyNoActionMessages();

		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("{\"Task\":{")
							.append("\"Id\":\"").append(item.getIssueID()).append("\",")
							.append("\"Name\":\"").append(item.getSummary()).append("\",")
							.append("\"Partners\":\"").append(item.getPartners()).append("\",")
							.append("\"Notes\":\"").append(item.getNotes()).append("\",")
							.append("\"Handler\":\"").append(configuration.USER_ID).append("\"")
							.append("},\"success\":true,\"Total\":1}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseTest.toString(), actualResponseText);
	}
}
