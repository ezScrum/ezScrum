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
	private CreateProject mCreateProject;
	private CreateSprint mCreateSprint;
	private AddStoryToSprint mAddStoryToSprint;
	private Configuration mConfig;
	private final String ACTION_PATH = "/deleteExistedTask";
	private IProject mProject;
	private long mSprintId;
	private long mStoryId;
	
	public DeleteExistedTaskTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 新增一個測試專案
    	mCreateProject = new CreateProject(1);
    	mCreateProject.exeCreate();
    	mProject = mCreateProject.getProjectList().get(0);
    	
    	// 新增一個 Sprint
    	int sprintCount = 1;
		mCreateSprint = new CreateSprint(sprintCount , mCreateProject);
    	mCreateSprint.exe();
    	mSprintId = Long.parseLong(mCreateSprint.getSprintIDList().get(0));
    	
		// Sprint 加入1個 Story
		int storyCount = 1;
		mAddStoryToSprint = new AddStoryToSprint(storyCount, 1, (int)mSprintId, mCreateProject, CreateProductBacklog.TYPE_ESTIMATION);
		mAddStoryToSprint.exe();
		mStoryId = mAddStoryToSprint.getIssueList().get(storyCount-1).getIssueID();
    	
    	super.setUp();
    	
    	// ============= release ==============
    	ini = null;
    }
	
    protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(mConfig.getDataPath());
    	
		mConfig.setTestMode(false);
		mConfig.save();
    	
    	// ============= release ==============
    	ini = null;
    	mCreateProject = null;
    	mConfig = null;
    	
    	super.tearDown();
    }
    
    /**
     * 刪除一個Task
     * @throws Exception 
     */
    public void testDeleteExistedTask_1() throws Exception{
		int taskCount = 1;
		String[] taskIDs = createTasks(taskCount);
		// drop Task from story
		DropTask dropTaskOne = new DropTask(mCreateProject, mSprintId, mStoryId, Long.valueOf(taskIDs[0]));
		dropTaskOne.exe();
    	
		// ================ set request info ========================
		String projectName = this.mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", String.valueOf(mSprintId));
		addRequestParameter("selected", taskIDs);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
    	setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
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
		DropTask dropTask_1 = new DropTask(this.mCreateProject, mSprintId, mStoryId, Long.valueOf(taskIDs[0]));
		dropTask_1.exe();
		DropTask dropTask_2 = new DropTask(this.mCreateProject, mSprintId, mStoryId, Long.valueOf(taskIDs[1]));
		dropTask_2.exe();
    	
		// ================ set request info ========================
		String projectName = this.mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", String.valueOf(mSprintId));
		addRequestParameter("selected", taskIDs);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
    	setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
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
		AddTaskToStory addTaskToStory = new AddTaskToStory(taskCount, 1, this.mAddStoryToSprint, mCreateProject);
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
		String projectName = this.mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		// 設定Session資訊
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", mProject);	
		// 設定新增Task所需的資訊
		String expectedStoryID = "1";
		String expectedSprintID = "1";
		
		addRequestParameter("sprintID", expectedSprintID);
		addRequestParameter("issueID", expectedStoryID);

		// ================ 執行 action ======================
		String path = "/showAddExistedTask2";
    	setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
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
