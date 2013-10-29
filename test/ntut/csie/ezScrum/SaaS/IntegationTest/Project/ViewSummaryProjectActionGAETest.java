package ntut.csie.ezScrum.SaaS.IntegationTest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.jdo.PersistenceManager;

import ntut.csie.ezScrum.SaaS.PMF;
import ntut.csie.ezScrum.SaaS.IntegationTest.ezScrumGAEConfig;
import ntut.csie.ezScrum.SaaS.database.ProjectDataStore;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.control.TaskBoard;
import ntut.csie.ezScrum.web.form.LogonForm;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class ViewSummaryProjectActionGAETest extends MockStrutsTestCase{
	// GAE DB
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private ezScrumGAEConfig config = new ezScrumGAEConfig();
	
	private final String expectedProjectID = "test";
	private final String expectedProjectDisplayName = "Project for Test Create Project";
	private final String expectedProjectComment = "";
	private final String expectedProjectManager = "ezScrum tester";
	private final String attachFileSize = "";
	public ViewSummaryProjectActionGAETest(String method) {
		super(method);
	}

	protected void setUp() throws Exception {
		super.setUp();
		helper.setUp();
    	setContextDirectory(this.config.getWebContentFile());		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile(this.config.getServletConfigFilePath());
	}

	protected void tearDown() throws Exception {
		helper.tearDown();
		super.tearDown();
	}
	/**
	 * 1. admin 建立專案
	 * 2. admin 瀏覽專案
	 */
	public void testAdminViewProjectSummary_Test(){
		/**
		 * 1. admin 建立專案
		 */
		IProject project = this.createProjectByAction();
		
    	/**
    	 * 2. admin 瀏覽專案
    	 */
    	this.cleanActionInformation();
		setRequestPathInfo("/viewProject");
    	
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", this.config.getAdminSession());
		
		// ================ set request info ========================
		addRequestParameter("PID", this.expectedProjectID);
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionMessages();
		verifyNoActionErrors();
		
		verifyForward("SummaryView");					//	define in ViewProjectSummaryAction.java
		verifyForwardPath("/Pages/ezScrumContent.jsp");	//	define in tiles-defs.xml
		
		String expectIsGuest = "false";
		String actualIsGuest = (String) request.getSession().getAttribute("isGuest");
		assertEquals(expectIsGuest, actualIsGuest);
		
    	String expectedSprintID = "null";
		TaskBoard taskBoard = (TaskBoard) request.getAttribute("TaskBoard");
		String actualSprintID = (String) request.getAttribute("SprintID");
		assertNull(taskBoard);
		assertEquals(expectedSprintID, actualSprintID);
		
//		IProject project = (new ProjectMapper()).getProjectByID( expectedProjectID );
		this.assertProjectSummaryInformation(project);
	}
	
	/**
	 * Integration Test
	 * Steps
	 * 	1. admin 新增專案
	 * 	2. admin view project Summary
	 * 		-	getSprintBurndownChartData (story)
	 * 		-	getSprintBurndownChartData (task)
	 * 		-	GetTaskBoardDescription
	 * 		-	GetProjectDescription
	 * 		-	AjaxGetHandlerList
	 * 		-	AjaxGetSprintBacklogDateInfo
	 * 		-	GetProjectLeftTreeItem
	 * 		-	AjaxGetTagList
	 * 		-	AjaxGetCustomIssueType
	 * 		-	GetTopTitleInfo
	 * 3. admin logout ezScrum
	 * 4. admin login ezScrum
	 * 5. admin view project list
	 * 6. admin view project Summary
	 * @throws Exception 
	 */
    public void testAdminViewProjectSummary_IntegrationTest() throws Exception {
    	//	=============== common data ============================
    	String adminForwardName = "Tenant_ManagementView";
    	String adminForwardPath = "/Pages/ezScrumTenantManagement.jsp";
    	String userId = this.config.getAdminId();
    	String userPassword = this.config.getAdminPassword();
    	IUserSession adminSession = this.config.getAdminSession();
    	String actualResponseText = "";
    	String expectedResponseText = "";
    	
		/**
		 * 1. admin 建立專案
		 */
    	IProject project = this.createProjectByAction();
    	
    	/**
		 * 2 admin select project
		 */
		// ================ set action info ========================    	
		this.cleanActionInformation();
		setRequestPathInfo( "/viewProject" );
		
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", adminSession );
    	
    	// ================== set parameter info ====================
    	addRequestParameter("PID", expectedProjectID);
    	
    	// ================ 執行 view project action ======================
    	actionPerform();
    	
    	// ================ assert ======================
		verifyNoActionMessages();
		verifyNoActionErrors();
		
		verifyForward("SummaryView");					//	define in ViewProjectSummaryAction.java
		verifyForwardPath("/Pages/ezScrumContent.jsp");	//	define in tiles-defs.xml
		String expectIsGuest = "false";
		
		String actualIsGuest = (String) request.getSession().getAttribute("isGuest");
		assertEquals(expectIsGuest, actualIsGuest);
		
    	String expectedSprintID = "null";
		TaskBoard taskBoard = (TaskBoard) request.getAttribute("TaskBoard");
		String actualSprintID = (String) request.getAttribute("SprintID");
		assertNull(taskBoard);
		assertEquals(expectedSprintID, actualSprintID);
		
		this.assertProjectSummaryInformation(project);
		
    	/**
	   	 * 3. admin logout ezScrum
	   	 */
		// ================ set action info ========================
		this.cleanActionInformation();
		setRequestPathInfo("/logout");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", adminSession);
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionMessages();
		verifyNoActionErrors();
		verifyForward("home");
		verifyForwardPath("/logon.do");
		
		/**
		 * 4. admin login ezScrum
		 */
		// ================ set action info ========================
		this.cleanActionInformation();
	   	setRequestPathInfo( "/logonSubmit" );
			
	   	// ================== set parameter info ====================
		String loginUserID = userId;
		String loginUserPassword = userPassword;
		LogonForm logonForm = new LogonForm();
	   	logonForm.setUserId(loginUserID);
	   	logonForm.setPassword(loginUserPassword);
	   	setActionForm(logonForm);
	   	
	   	// ================ 執行 login action ======================
	   	actionPerform();
	   	
	   	// ================ assert ======================
//	   	verifyForward("success");
//	   	verifyForwardPath("/resetProjectSession.do");
	   	verifyForward(adminForwardName);
	   	verifyForwardPath(adminForwardPath);
			
	   	/**
	   	 * 5. view project list
	   	 */
		// ================ set action info ========================
	   	this.cleanActionInformation();
	   	setRequestPathInfo( "/viewProjectList" );
	   	
	   	// ================ set session info ========================
	   	request.getSession().setAttribute( "UserSession", adminSession );
	   	
		// ================ 執行 view project list action ======================
	   	actionPerform();
	   	
	   	// ================ assert ========================
	   	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	   	String expectedProjectCreateDate = dateFormat.format( project.getProjectDesc().getCreateDate() );
	   	String expectedProjectDemoDate = "No Plan!";
	   	//	assert response text
	   	expectedResponseText = 
				"<Projects>" +
					"<Project>" +
						"<ID>" + expectedProjectID + "</ID>" +
						"<Name>" + expectedProjectDisplayName + "</Name>" +
						"<Comment>" + expectedProjectComment + "</Comment>" +
						"<ProjectManager>" + expectedProjectManager + "</ProjectManager>" +
						"<CreateDate>" + expectedProjectCreateDate + "</CreateDate>" +
						"<DemoDate>" + expectedProjectDemoDate + "</DemoDate>" +
					"</Project>" +
				"</Projects>";
	   	actualResponseText = this.response.getWriterBuffer().toString();
	   	assertEquals(expectedResponseText, actualResponseText);
	   	
    	/**
		 * 5 admin select project
		 */
		// ================ set action info ========================    	
		this.cleanActionInformation();
		setRequestPathInfo( "/viewProject" );
		
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", adminSession );
    	
    	// ================== set parameter info ====================
    	addRequestParameter("PID", expectedProjectID);
    	
    	// ================ 執行 view project action ======================
    	actionPerform();
    	
    	// ================ assert ======================
		verifyNoActionMessages();
		verifyNoActionErrors();
		
		verifyForward("SummaryView");					//	define in ViewProjectSummaryAction.java
		verifyForwardPath("/Pages/ezScrumContent.jsp");	//	define in tiles-defs.xml
		expectIsGuest = "false";
		
		actualIsGuest = (String) request.getSession().getAttribute("isGuest");
		assertEquals(expectIsGuest, actualIsGuest);
		
    	expectedSprintID = "null";
		taskBoard = (TaskBoard) request.getAttribute("TaskBoard");
		actualSprintID = (String) request.getAttribute("SprintID");
		assertNull(taskBoard);
		assertEquals(expectedSprintID, actualSprintID);
		
		/**
		 * 6. admin view project Summary
		 */
		this.assertProjectSummaryInformation(project);
    }
	
    /**
     * 比對資料庫中是否存在此專案的PID
     * 1. assert 不存在
     * 2. assert 存在
     */
    public void testPIDIsExisted(){
    	IUserSession adminSession = this.config.getAdminSession();
    	
		/**
		 * 1. admin 建立專案
		 */
    	IProject project = this.createProjectByAction();
    	
    	/**
    	 * project ID does not existed 
    	 */
    	String notexistedProjectID = "testNotExisted";
		setRequestPathInfo("/viewProject");
    	
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", adminSession);
		
		// ================ set request info ========================
		addRequestParameter("PID", notexistedProjectID);
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionMessages();
		verifyNoActionErrors();
		verifyForward("error");					//	define in ViewProjectSummaryAction.java
		verifyForwardPath("/Error.jsp");	//	define in tiles-defs.xml
		
		/**
		 * project ID existed
		 */
		this.cleanActionInformation();
    	String existedProjectID = this.expectedProjectID;
		setRequestPathInfo("/viewProject");
    	
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", adminSession);
		
		// ================ set request info ========================
		addRequestParameter("PID", existedProjectID);
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionMessages();
		verifyNoActionErrors();
		verifyForward("SummaryView");					//	define in ViewProjectSummaryAction.java
		verifyForwardPath("/Pages/ezScrumContent.jsp");	//	define in tiles-defs.xml
		
		String expectIsGuest = "false";
		String actualIsGuest = (String) request.getSession().getAttribute("isGuest");
		assertEquals(expectIsGuest, actualIsGuest);
    }
    
	private IProject createProjectByAction(){
		/**
		 * 1. admin 建立專案
		 */
		// ================ set action info ========================
		setRequestPathInfo("/AjaxCreateProject");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", this.config.getAdminSession());
		
		// ================ set request info ========================
		//	設定專案資訊
		addRequestParameter("Name", this.expectedProjectID);
		addRequestParameter("DisplayName", this.expectedProjectDisplayName);
		addRequestParameter("Comment", this.expectedProjectComment);
		addRequestParameter("ProjectManager", this.expectedProjectManager);
		addRequestParameter("AttachFileSize", this.attachFileSize);
		addRequestParameter("from", "createProject");
		
		//	設定ITS參數資料
		addRequestParameter("ServerUrl", this.config.SERVER_URL);
		addRequestParameter("ServicePath", this.config.SERVER_PATH);
		addRequestParameter("DBAccount", this.config.SERVER_ACCOUNT);
		addRequestParameter("DBPassword", this.config.SERVER_PASSWORD);
		addRequestParameter("SQLType", this.config.DATABASE_TYPE);
		addRequestParameter("DBName", this.config.DATABASE_NAME);
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		//	assert response text
		String actualResponseText = response.getWriterBuffer().toString();
		String expectResponseText = 
			"<Root>" +
				"<CreateProjectResult>" +
				"<Result>Success</Result>" +
				"<ID>test</ID>" +
				"</CreateProjectResult>" +
			"</Root>";
		assertEquals(expectResponseText, actualResponseText);
		
		IProject project = (new ProjectMapper()).getProjectByID( expectedProjectID );
		return project;
	}
    
    private void assertProjectSummaryInformation(IProject project){
       	//	=============== common data ============================
    	String userId = this.config.getAdminId();
    	String userName = this.config.getAdminId();
    	IUserSession adminSession = this.config.getAdminSession();
    	String actualResponseText = "";
    	String expectedResponseText = "";
    	String actionPath = "";
    	
       	/**
		 * 6.1 user select project - get Project Description
		 */
		// ================ set action info ========================
		this.cleanActionInformation();
		setRequestPathInfo("/GetProjectDescription");
		
    	// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + expectedProjectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", adminSession);
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		//	assert response text
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
		String expectAttachFileSize = "2";
		actualResponseText = response.getWriterBuffer().toString();
		String expectProjectCreateDate = dateFormat.format( project.getProjectDesc().getCreateDate() );
		expectedResponseText = 
		"{" +
			"\"ID\":\"0\"," +
			"\"ProjectName\":\"" + this.expectedProjectID + "\"," +
			"\"ProjectDisplayName\":\"" + this.expectedProjectDisplayName + "\"," +
			"\"Commnet\":\"" + this.expectedProjectComment + "\"," +
			"\"ProjectManager\":\"" + this.expectedProjectManager + "\"," +
			"\"AttachFileSize\":\"" + expectAttachFileSize + "\"," +
			"\"ProjectCreateDate\":\"" + expectProjectCreateDate + "\"" +
		"}";
		assertEquals(expectedResponseText, actualResponseText);
		
    	/**
		 * 6.2 user select project - get Sprint Description
		 */
		// ================ set action info ========================
		this.cleanActionInformation();
		setRequestPathInfo("/GetTaskBoardDescription");
		
    	// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + this.expectedProjectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", adminSession);
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		//	assert response text
		actualResponseText = response.getWriterBuffer().toString();
		expectedResponseText = 
			"{" +
				"\"ID\":\"0\"," +
				"\"SprintGoal\":\"\"," +
				"\"Current_Story_Undone_Total_Point\":\"\"," +
				"\"Current_Task_Undone_Total_Point\":\"\"" +
			"}"; 
		assertEquals(expectedResponseText, actualResponseText);
		
    	/**
		 * 6.3 user select project - get Story Burndown Chat
		 */

		// ================ clean previous action info ========================
		this.cleanActionInformation();
		actionPath = "/getSprintBurndownChartData";
		setRequestPathInfo(actionPath);
		
    	// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + this.expectedProjectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ set request info ========================
		addRequestParameter("SprintID", "-1");	//	-1:代表離現在時間最近的Sprint
		addRequestParameter("Type", "story");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", adminSession);
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		expectedResponseText = 
			"{" +
				"\"Points\":[]," +
				"\"success\":true" +
			"}";
		actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
		
    	/**
		 * 6.4 user select project - get Task Burndown Chat
		 */
		// ================ clean previous action info ========================
		this.cleanActionInformation();
		setRequestPathInfo("/getSprintBurndownChartData");
		
    	// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + this.expectedProjectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ set request info ========================
		addRequestParameter("SprintID", "-1");	//	-1:代表離現在時間最近的Sprint
		addRequestParameter("Type", "task");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", adminSession);
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		expectedResponseText = 
			"{" +
				"\"Points\":[]," +
				"\"success\":true" +
			"}";
		
		actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
		
		
		/**
		 * AjaxGetHandlerList
		 */
		// ================ clean previous action info ========================
		this.cleanActionInformation();
		setRequestPathInfo("/AjaxGetHandlerList");
		
    	// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + expectedProjectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", adminSession);
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		expectedResponseText = 
			"<Handlers>" +
				"<Result>success</Result>" +
				"<Handler>" +
					"<Name></Name>" +
				"</Handler>" +
			"</Handlers>";
		
		actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
		
//		 * 		-	AjaxGetSprintBacklogDateInfo
		// ================ clean previous action info ========================
		this.cleanActionInformation();
		setRequestPathInfo("/AjaxGetSprintBacklogDateInfo");
		
    	// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + expectedProjectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ set request parameter ========================
		addRequestParameter("sprintID", "");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", adminSession);
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		expectedResponseText = "";
		actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
		
//		 * 		-	AjaxGetTagList
		// ================ clean previous action info ========================
		this.cleanActionInformation();
		setRequestPathInfo("/AjaxGetTagList");
		
    	// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + expectedProjectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", adminSession);
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		expectedResponseText = "<TagList><Result>success</Result></TagList>";
		actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
		
//		 * 		-	AjaxGetCustomIssueType
		// ================ clean previous action info ========================
		this.cleanActionInformation();
		setRequestPathInfo("/AjaxGetCustomIssueType");
		
    	// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + expectedProjectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", adminSession);
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		expectedResponseText = 
			"<Root>" +
				"<Result>Success</Result>" +
				"<IssueType>" +
					"<TypeId></TypeId>" +
					"<TypeName></TypeName>" +
					"<IsPublic></IsPublic>" +
				"</IssueType>" +
			"</Root>";
		actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
		
//		 * 		-	GetTopTitleInfo
		// ================ clean previous action info ========================
		this.cleanActionInformation();
		setRequestPathInfo("/GetTopTitleInfo");
		
    	// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + expectedProjectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", adminSession);
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		expectedResponseText = 
			"{\"UserName\":\"" + userId + "(" + userName + ")" + "\"," +
			"\"ProjectName\":\"" + this.expectedProjectID + "\"}";
		actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
		
//		 * 		-	GetProjectLeftTreeItem
    }
    
	private ProjectDataStore getProjectDS(String projectId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Key projectKey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), projectId);
		ProjectDataStore projectDS;
		
		try {
			projectDS = pm.getObjectById(ProjectDataStore.class, projectKey);

		} finally {
			pm.close();
		}
		
		return projectDS;
	}
	
	/**
	 * clean previous action info
	 */
	private void cleanActionInformation(){
		clearRequestParameters();
		this.response.reset();
	}
}
