package ntut.csie.ezScrum.web.action.backlog.product;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CheckOutIssue;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class ShowProductBacklogActionTest extends MockStrutsTestCase {
	
	private CreateProject CP;
	private Configuration configuration;
	private final String ACTION_PATH = "/showProductBacklog2";
	private IProject project;
	
	public ShowProductBacklogActionTest(String testName) {
		super(testName);
	}
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		
		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案
		this.project = this.CP.getProjectList().get(0);
		
		super.setUp();
		
		// ================ set action info ========================
		setContextDirectory( new File(configuration.getBaseDirPath()+ "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo( this.ACTION_PATH );
		
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());
		
		configuration.setTestMode(false);
		configuration.save();

		super.tearDown();
		
		ini = null;
		projectManager = null;
		this.CP = null;
		configuration = null;
	}
	
	public void testShowProductBacklogAction_NoStory(){
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		
		addRequestParameter("FilterType", "");
		
		// ================ set session info ========================
		request.getSession().setAttribute( projectName, this.project );
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{")
							.append("\"success\":true,")
							.append("\"Total\":0,")
							.append("\"Stories\":[]")
							.append("}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
	
	public void testShowProductBacklogAction_Stories(){
		int storyCount = 2;
		CreateProductBacklog CPB = new CreateProductBacklog(storyCount, this.CP);
		CPB.exe();
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		
		addRequestParameter("FilterType", "");
		
		// ================ set session info ========================
		request.getSession().setAttribute( projectName, this.project );
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"success\":true,\"Total\":2,")
							.append("\"Stories\":[");
		//  取2次Story資料
		for (int i = 0; i < storyCount; i++) {
			expectedResponseText.append("{\"Id\":").append(CPB.getIssueList().get(i).getIssueID()).append(",")
								.append("\"Name\":\"").append(CPB.getIssueList().get(i).getSummary()).append("\",")							
								.append("\"Value\":\"").append(CPB.getIssueList().get(i).getValue()).append("\",")
								.append("\"Estimate\":\"").append(CPB.getIssueList().get(i).getEstimated()).append("\",")
								.append("\"Importance\":\"").append(CPB.getIssueList().get(i).getImportance()).append("\",")
								.append("\"Tag\":\"\",")
								.append("\"Status\":\"new\",")
								.append("\"Notes\":\"").append(CPB.getIssueList().get(i).getNotes()).append("\",")
								.append("\"HowToDemo\":\"").append(CPB.getIssueList().get(i).getHowToDemo()).append("\",")
								.append("\"Link\":\"/ezScrum/showIssueInformation.do?issueID=").append(CPB.getIssueList().get(i).getIssueID()).append("\",")
								.append("\"Release\":\"None\",")
								.append("\"Sprint\":\"None\",")
								.append("\"FilterType\":\"DETAIL\",")
								.append("\"Attach\":false,")
								.append("\"AttachFileList\":[]},");
		}
		expectedResponseText.deleteCharAt(expectedResponseText.length() - 1);
		expectedResponseText.append("]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
	
	/**
	 * filter type = BACKLOG
	 */
	public void testShowProductBacklog_Backlog(){
		CreateProductBacklog CPB = new CreateProductBacklog();
//		createProductBacklog.createBacklogStory(project, value, importance, estimation);
		CPB.createBacklogStory(project, "0", "0", "0");	//	backlog
		CPB.createBacklogStory(project, "0", "1", "0");	//	backlog
		CPB.createBacklogStory(project, "1", "2", "3");	//	detail
		List<IIssue> issueList = CPB.getIssueList();
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		
		String filterType = "BACKLOG";
		addRequestParameter("FilterType", filterType);
		
		// ================ set session info ========================
		request.getSession().setAttribute( projectName, this.project );
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"success\":true,\"Total\":2,")
							.append("\"Stories\":[");
		//  取2次Story資料
		for (int i = 1; i >= 0; i--) {
			expectedResponseText.append("{\"Id\":").append(issueList.get(i).getIssueID()).append(",")
								.append("\"Name\":\"").append(issueList.get(i).getSummary()).append("\",")							
								.append("\"Value\":\"").append(issueList.get(i).getValue()).append("\",")
								.append("\"Estimate\":\"").append(issueList.get(i).getEstimated()).append("\",")
								.append("\"Importance\":\"").append(issueList.get(i).getImportance()).append("\",")
								.append("\"Tag\":\"\",")
								.append("\"Status\":\"new\",")
								.append("\"Notes\":\"").append(issueList.get(i).getNotes()).append("\",")
								.append("\"HowToDemo\":\"").append(issueList.get(i).getHowToDemo()).append("\",")
								.append("\"Link\":\"/ezScrum/showIssueInformation.do?issueID=").append(issueList.get(i).getIssueID()).append("\",")
								.append("\"Release\":\"None\",")
								.append("\"Sprint\":\"None\",")
								.append("\"FilterType\":\"BACKLOG\",")
								.append("\"Attach\":false,")
								.append("\"AttachFileList\":[]},");
		}
		expectedResponseText.deleteCharAt(expectedResponseText.length() - 1);
		expectedResponseText.append("]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
	/**
	 * filter type = DONE
	 * @throws Exception 
	 */
	public void testShowProductBacklog_Done() throws Exception{
		CreateProductBacklog CPB = new CreateProductBacklog();
//		createProductBacklog.createBacklogStory(project, value, importance, estimation);
		CPB.createBacklogStory(project, "0", "0", "0");	//	backlog
		CPB.createBacklogStory(project, "0", "1", "0");	//	backlog
		CPB.createBacklogStory(project, "1", "2", "3");	//	detail
		
		int sprintCount = 1;
		int storyCount = 1;
		int storyEstValue = 8;
		CreateSprint createSprint = new CreateSprint(sprintCount, this.CP);
		createSprint.exe();
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(storyCount, storyEstValue, createSprint, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStoryToSprint.exe();
		
		List<IIssue> issueList = addStoryToSprint.getIssueList();
		CheckOutIssue checkOutIssue = new CheckOutIssue(issueList, this.CP);
		checkOutIssue.exeDone_Issues();
		
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		
		String filterType = "DONE";
		addRequestParameter("FilterType", filterType);
		String expectedStoryName = addStoryToSprint.getIssueList().get(0).getSummary();
		String expectedStoryImportance = addStoryToSprint.getIssueList().get(0).getImportance();
		String expectedStoryEstimation = addStoryToSprint.getIssueList().get(0).getEstimated();
		String expectedStoryValue = addStoryToSprint.getIssueList().get(0).getValue();
		String expectedStoryHoewToDemo = addStoryToSprint.getIssueList().get(0).getHowToDemo();
		String expectedStoryNote = addStoryToSprint.getIssueList().get(0).getNotes();
		String issueID = String.valueOf(addStoryToSprint.getIssueList().get(0).getIssueID());
		String SprintID = createSprint.getSprintIDList().get(0);
		// ================ set session info ========================
		request.getSession().setAttribute( projectName, this.project );
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"success\":true,")
							.append("\"Total\":1,")
							.append("\"Stories\":[{")
							.append("\"Id\":").append(issueID).append(",")
							.append("\"Name\":\"").append(expectedStoryName).append("\",")
							.append("\"Value\":\"").append(expectedStoryValue).append("\",")			
							.append("\"Estimate\":\"").append(expectedStoryEstimation).append("\",")
							.append("\"Importance\":\"").append(expectedStoryImportance).append("\",")
							.append("\"Tag\":\"\",")
							.append("\"Status\":\"closed\",")
							.append("\"Notes\":\"").append(expectedStoryNote).append("\",")
							.append("\"HowToDemo\":\"").append(expectedStoryHoewToDemo).append("\",")
							.append("\"Link\":\"/ezScrum/showIssueInformation.do?issueID=").append(issueID).append("\",")
							.append("\"Release\":\"None\",")
							.append("\"Sprint\":\"").append(SprintID).append("\",")
							.append("\"FilterType\":\"DONE\",")
							.append("\"Attach\":false,")
							.append("\"AttachFileList\":[]")
							.append("}]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
	/**
	 * Filter Type = DETAIL
	 */
	
	public void testShowProductBacklog_Detail() throws Exception{
		CreateProductBacklog CPB = new CreateProductBacklog();
//		createProductBacklog.createBacklogStory(project, value, importance, estimation);
		CPB.createBacklogStory(project, "0", "0", "0");	//	backlog
		CPB.createBacklogStory(project, "0", "1", "0");	//	backlog
		CPB.createBacklogStory(project, "1", "2", "3");	//	detail
		
		int sprintCount = 1;
		int storyCount = 1;
		int storyEstValue = 8;
		CreateSprint createSprint = new CreateSprint(sprintCount, this.CP);
		createSprint.exe();
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(storyCount, storyEstValue, createSprint, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStoryToSprint.exe();
		
		List<IIssue> issueList = addStoryToSprint.getIssueList();
		CheckOutIssue checkOutIssue = new CheckOutIssue(issueList, this.CP);
		checkOutIssue.exeDone_Issues();
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		
		String filterType = "DETAIL";
		addRequestParameter("FilterType", filterType);
		String expectedStoryName = CPB.getIssueList().get(2).getSummary();
		String expectedStoryImportance = CPB.getIssueList().get(2).getImportance();
		String expectedStoryEstimation = CPB.getIssueList().get(2).getEstimated();
		String expectedStoryValue = CPB.getIssueList().get(2).getValue();
		String expectedStoryHoewToDemo = CPB.getIssueList().get(2).getHowToDemo();
		String expectedStoryNote = CPB.getIssueList().get(2).getNotes();
		String issueID = String.valueOf(CPB.getIssueList().get(2).getIssueID());
		// ================ set session info ========================
		request.getSession().setAttribute( projectName, this.project );
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"success\":true,")
							.append("\"Total\":1,")
							.append("\"Stories\":[{")
							.append("\"Id\":").append(issueID).append(",")
							.append("\"Name\":\"").append(expectedStoryName).append("\",")
							.append("\"Value\":\"").append(expectedStoryValue).append("\",")	
							.append("\"Estimate\":\"").append(expectedStoryEstimation).append("\",")
							.append("\"Importance\":\"").append(expectedStoryImportance).append("\",")		
							.append("\"Tag\":\"\",")
							.append("\"Status\":\"new\",")
							.append("\"Notes\":\"").append(expectedStoryNote).append("\",")
							.append("\"HowToDemo\":\"").append(expectedStoryHoewToDemo).append("\",")
							.append("\"Link\":\"/ezScrum/showIssueInformation.do?issueID=").append(issueID).append("\",")
							.append("\"Release\":\"None\",")
							.append("\"Sprint\":\"None\",")
							.append("\"FilterType\":\"DETAIL\",")
							.append("\"Attach\":false,")
							.append("\"AttachFileList\":[]")
							.append("}]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
