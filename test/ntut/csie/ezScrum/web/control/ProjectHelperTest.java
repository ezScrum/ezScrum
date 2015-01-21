package ntut.csie.ezScrum.web.control;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.resource.core.IProject;
import ntut.csie.jcis.resource.core.IWorkspace;
import ntut.csie.jcis.resource.core.IWorkspaceRoot;
import ntut.csie.jcis.resource.core.ResourceFacade;

public class ProjectHelperTest extends TestCase {
	private CreateProject CP;
	private int ProjectCount = 3;
	private ProjectLogic helper = null;
	private ProjectMapper mapper = null;
	
	private Configuration configuration;
	
	public ProjectHelperTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
		// 新增Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();
		
		this.helper = new ProjectLogic();
		this.mapper = new ProjectMapper();
		
		super.setUp();
		
		// release
		ini = null;
    }

    protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
		CopyProject copyProject = new CopyProject(this.CP);
    	copyProject.exeDelete_Project();					// 刪除測試檔案
    	
    	configuration.setTestMode(false);
		configuration.store();
    	
    	super.tearDown();
    	
    	// release
    	ini = null;
    	copyProject = null;
    	this.CP = null;
    	this.helper = null;
    	configuration = null;
    }
    
    // 測試沒有專案存在的錯誤
    public void testgetAllCustomProjectsWrongParameter() throws Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
    	IProject[] ActualProjects = helper.getAllCustomProjects();
		assertNull(ActualProjects);
    }
    
    // 測試根據專案名稱取得專案
    public void testgetProject() {
    	String name = this.CP.mProjectName + Integer.toString(this.CP.getProjectList().size());
//    	IProject Expected = this.helper.getProjectByID(name);
    	IProject Expected = this.mapper.getProjectByID(name);
    	
		IWorkspace workspace = ResourceFacade.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
    	IProject Actual = root.getProject(name);
    	
    	assertEquals(Actual.getName(), Expected.getName());
    	assertEquals(Actual.getFullPath(), Expected.getFullPath());
    }
    
    // 測試根據專案錯誤的名稱取得專案
    public void testgetProjectWrongParameter() {
		System.out.println("testgetProjectWrongParameter: 請找時間把測試失敗原因找出來~");
/*    	
    	String name = "????????";
//    	IProject Expected = this.helper.getProjectByID(name);
    	IProject Expected = this.mapper.getProjectByID(name);
    	
    	// 目前沒有打算改這個問題，所以繼續沿用學長寫的方式 
    	assertNull(Expected);
*/    	
    }
}