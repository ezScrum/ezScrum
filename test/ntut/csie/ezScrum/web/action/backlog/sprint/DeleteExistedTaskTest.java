package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.DropTask;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class DeleteExistedTaskTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private AddStoryToSprint addStoryToSprint;
	private Configuration configuration;
	private final String ACTION_PATH = "/deleteExistedTask";
	private IProject project;
	private String sprintID;
	private String storyID;
	
	public DeleteExistedTaskTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		
		//	新增一個測試專案
    	this.CP = new CreateProject(1);
    	this.CP.exeCreate();
    	this.project = this.CP.getProjectList().get(0);
    	
    	//	新增一個Sprint
    	int sprintCount = 1;
		this.CS = new CreateSprint(sprintCount , this.CP);
    	this.CS.exe();
    	this.sprintID = this.CS.getSprintIDList().get(0);
    	
		//	Sprint加入1個Story
		int storyCount = 1;
		this.addStoryToSprint = new AddStoryToSprint(storyCount, 1, Integer.valueOf(sprintID), CP, CreateProductBacklog.TYPE_ESTIMATION);
		this.addStoryToSprint.exe();
		this.storyID = String.valueOf(addStoryToSprint.getIssueList().get(storyCount-1).getIssueID());
    	
    	super.setUp();
    	
    	// ============= release ==============
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
		configuration.store();
    	
    	// ============= release ==============
    	ini = null;
    	this.CP = null;
    	configuration = null;
    	
    	super.tearDown();
    }
    
    /**
     * 刪除一個Task
     * @throws Exception 
     */
    public void testDeleteExistedTask_1() throws Exception{
		int taskCount = 1;
		String[] taskIDs = this.createTasks(taskCount);
		// drop Task from story
		DropTask dropTask_1 = new DropTask(this.CP, Integer.valueOf(sprintID), Integer.valueOf(storyID), Integer.valueOf(taskIDs[0]));
		dropTask_1.exe();
    	
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", sprintID);
		addRequestParameter("selected", taskIDs);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
		// ================ 執行 action ======================
    	setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	setRequestPathInfo( this.ACTION_PATH );
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		String actualResponseText = response.getWriterBuffer().toString();
		StringBuilder expectedResponseText = new StringBuilder("");
		assertEquals(expectedResponseText.toString(), actualResponseText);
		
		this.verifyExistedTasks();
    }
    
    /**
     * 刪除兩個Task
     * @throws Exception 
     */
    public void testDeleteExistedTask_2() throws Exception{
		int taskCount = 2;
		String[] taskIDs = this.createTasks(taskCount);
		// drop Task from story
		DropTask dropTask_1 = new DropTask(this.CP, Integer.valueOf(sprintID), Integer.valueOf(storyID), Integer.valueOf(taskIDs[0]));
		dropTask_1.exe();
		DropTask dropTask_2 = new DropTask(this.CP, Integer.valueOf(sprintID), Integer.valueOf(storyID), Integer.valueOf(taskIDs[1]));
		dropTask_2.exe();
    	
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", sprintID);
		addRequestParameter("selected", taskIDs);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
		// ================ 執行 action ======================
    	setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	setRequestPathInfo( this.ACTION_PATH );
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		String actualResponseText = response.getWriterBuffer().toString();
		StringBuilder expectedResponseText = new StringBuilder("");
		assertEquals(expectedResponseText.toString(), actualResponseText);
		
		this.verifyExistedTasks();
    }
    
    private String[] createTasks(int taskCount) throws NumberFormatException, Exception{
		// Story加入1個Task
		AddTaskToStory addTaskToStory = new AddTaskToStory(taskCount, 1, this.addStoryToSprint, CP);
		addTaskToStory.exe();
		String[] taskIDs = new String[addTaskToStory.getTaskIDList().size()];
		for(int i = 0; i < addTaskToStory.getTaskIDList().size(); i++ ){
			taskIDs[i] = String.valueOf(addTaskToStory.getTaskIDList().get(i));
		}
		return taskIDs;
    }
    
    /**
     * 驗證delete tasks後，該Tasks是確實被刪除的。
     * @throws Exception
     */
    private void verifyExistedTasks() throws Exception{
		//	clear request and response
		clearRequestParameters();
		this.response.reset();
    	
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		request.getSession().setAttribute("Project", project);	
		// 設定新增Task所需的資訊
		String expectedStoryID = "1";
		String expectedSprintID = "1";
		
		addRequestParameter("sprintID", expectedSprintID);
		addRequestParameter("issueID", expectedStoryID);

		// ================ 執行 action ======================
		String path = "/showAddExistedTask2";
    	setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	setRequestPathInfo( path );
		
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		StringBuilder expectedResponseText = new StringBuilder("<Tasks></Tasks>");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
    }
}
