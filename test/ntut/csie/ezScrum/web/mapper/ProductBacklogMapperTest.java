package ntut.csie.ezScrum.web.mapper;

import java.io.File;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.internal.IssueAttachFile;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;

public class ProductBacklogMapperTest extends TestCase {
	private CreateProject CP;
	private CreateProductBacklog CPB;
	
	private int ProjectCount = 1;
	private int StoryCount = 1;
	
	private ProductBacklogMapper productBacklogMapper = null;
	private Configuration configuration = null;
	
	public ProductBacklogMapperTest(String testMethod) {
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
		
		super.setUp();
		
		// 建立 productbacklog 物件
		IProject project = this.CP.getProjectList().get(0);
		this.productBacklogMapper = new ProductBacklogMapper(project, configuration.getUserSession());
		
		
		// ============= release ==============
		ini = null;
		project = null;
	}
	
	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
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
    	this.productBacklogMapper = null;
    	projectManager = null;
    	configuration = null;
    	
    	super.tearDown();
	}
	
	// 測試上傳檔案到一筆 issue 是否成功
	public void testaddAttachFile() {
		String Test_File_Path = configuration.getInitialSQLPath();
		
		IIssue issue = this.CPB.getIssueList().get(0);
		this.productBacklogMapper.addAttachFile(issue.getIssueID(), Test_File_Path);		// 將 InitialData/initial_bk.sql 上傳測試		
		
		issue = this.productBacklogMapper.getIssue(issue.getIssueID());
		IssueAttachFile ActualFile = issue.getAttachFile().get(0);
		File ExpectedFile = new File(Test_File_Path);
		
		assertEquals(1, issue.getAttachFile().size());
		assertEquals(ExpectedFile.getName(), ActualFile.getFilename());
		assertEquals(ExpectedFile.length(), ActualFile.getFilesize());
		
		// ============= release ==============
		issue = null;
		ActualFile = null;
		ExpectedFile = null;
	}
	
	// 測試刪除一筆 Issue 的檔案
	public void testdeleteAttachFile() {
		String Test_File_Path = configuration.getInitialSQLPath();
		IIssue issue = this.CPB.getIssueList().get(0);		
		this.productBacklogMapper.addAttachFile(issue.getIssueID(), Test_File_Path);		// 將 InitialData/initial_bk.sql 上傳測試
		
		issue = this.productBacklogMapper.getIssue(issue.getIssueID());
		IssueAttachFile ActualFile = issue.getAttachFile().get(0);
		File ExpectedFile = new File(Test_File_Path);
		
		assertEquals(1, issue.getAttachFile().size());
		assertEquals(ExpectedFile.getName(), ActualFile.getFilename());
		assertEquals(ExpectedFile.length(), ActualFile.getFilesize());
		
		// 刪除此 issue 的檔案
		this.productBacklogMapper.deleteAttachFile(ActualFile.getAttachFileId());
		issue = this.productBacklogMapper.getIssue(issue.getIssueID());
		assertEquals(0, issue.getAttachFile().size());
		
		// ============= release ==============
		issue = null;
		ActualFile = null;
		ExpectedFile = null;		
	}
	
	// 測試不用透過 mantis 直接取得檔案的方法
	public void testgetAttachfile() {
		String Test_File_Path = configuration.getInitialSQLPath();
		IIssue issue = this.CPB.getIssueList().get(0);
		
		this.productBacklogMapper.addAttachFile(issue.getIssueID(), Test_File_Path);		// 將 InitialData/initial_bk.sql 上傳測試		
		
		issue = this.productBacklogMapper.getIssue(issue.getIssueID());
		IssueAttachFile IssueFile = issue.getAttachFile().get(0);
		File ExpectedFile = new File(Test_File_Path);
		
		assertEquals(1, issue.getAttachFile().size());
		assertEquals(ExpectedFile.getName(), IssueFile.getFilename());
		assertEquals(ExpectedFile.length(), IssueFile.getFilesize());
		
		File ActualFile = this.productBacklogMapper.getAttachfile(Long.toString(IssueFile.getIssueID()));
		
		assertEquals(ExpectedFile.length(), ActualFile.length());
		assertEquals(ExpectedFile.isFile(), ActualFile.isFile());
		assertEquals(ExpectedFile.isAbsolute(), ActualFile.isAbsolute());
		
		// ============= release ==============
		issue = null;
		ActualFile = null;
		ExpectedFile = null;		
	}
}