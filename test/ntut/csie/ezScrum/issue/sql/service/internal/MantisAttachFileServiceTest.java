package ntut.csie.ezScrum.issue.sql.service.internal;

import java.io.File;

import junit.framework.TestCase;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;

public class MantisAttachFileServiceTest extends TestCase {
	private CreateProject CP;
	private CreateProductBacklog CPB;
	private int ProjectCount = 1;
	private int StoryCount = 1;
	private Configuration configuration;
	
	private MantisAttachFileService MAFSservice;
	private long FileID = 0;
	
	private ISQLControl control;
	
	public MantisAttachFileServiceTest(String testMethod) {
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
		
		// 新增Story	
		this.CPB = new CreateProductBacklog(this.StoryCount, this.CP);
		this.CPB.exe();
		
		IProject project = this.CP.getProjectList().get(0);
		
		MantisService mantisService = new MantisService(configuration);
		this.control = mantisService.getControl();
		this.control.setUser(configuration.getDBAccount());
		this.control.setPassword(configuration.getDBPassword());
		this.control.connection();
		
		this.MAFSservice = new MantisAttachFileService(control, configuration);
		
		// ================ set initial data =======================
		// 新增一筆檔案在一筆 issue - id(1) 
//		ProductBacklog backlog = new ProductBacklog(project, config.getUserSession());
		ProductBacklogMapper productBacklogMapper = new ProductBacklogMapper(project, configuration.getUserSession());
		productBacklogMapper.addAttachFile(this.CPB.getIssueList().get(0).getIssueID(), configuration.getInitialSQLPath());
		IIssue issue = productBacklogMapper.getIssue(this.CPB.getIssueList().get(0).getIssueID());
		this.FileID = issue.getAttachFile().get(0).getAttachFileId();
		// ================ set initial data =======================
		
		super.setUp();
		
		// ============= release ==============
		ini = null;
		project = null;
		productBacklogMapper = null;
	}
	
	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
		if (this.control != null) {
			try {
				this.control.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());
    	
		configuration.setTestMode(false);
		configuration.store();
		
    	// ============= release ==============
    	ini = null;
    	this.CP = null;
    	this.CPB = null;
    	this.MAFSservice = null;
    	projectManager = null;
    	configuration = null;
    	
    	super.tearDown();
	}

	/*
	 * 測試從 file ID 取得此檔案
	 */
	public void testgetAttachFile_1() {
		File ActualFile = this.MAFSservice.getAttachFile(Long.toString(this.FileID));
		File ExpectedFile = new File(configuration.getInitialSQLPath());
		
		assertEquals(ExpectedFile.length(), ActualFile.length());
		assertEquals(ExpectedFile.isFile(), ActualFile.isFile());
		assertEquals(ExpectedFile.isAbsolute(), ActualFile.isAbsolute());
		
		// release
		ActualFile.delete();
		ActualFile = null;
		ExpectedFile = null;
	}
}
