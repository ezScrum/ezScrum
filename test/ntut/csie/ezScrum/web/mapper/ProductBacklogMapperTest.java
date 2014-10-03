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
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.jcis.resource.core.IProject;

public class ProductBacklogMapperTest extends TestCase {
	private CreateProject CP;
	private CreateProductBacklog CPB;
	
	private int ProjectCount = 1;
	private int StoryCount = 1;
	
	private ProductBacklogMapper productBacklogMapper = null;
	private Configuration configuration = null;
	
	private final String FILE_NAME = "Initial.sql";
	private final String FILE_TYPE = "sql/plain";
	
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
		IIssue issue = this.CPB.getIssueList().get(0);
		
		addAttachFile(productBacklogMapper, issue.getIssueID());
		
		issue = this.productBacklogMapper.getIssue(issue.getIssueID());
		AttachFileObject ActualFile = issue.getAttachFile().get(0);
		
		assertEquals(1, issue.getAttachFile().size());
		assertEquals(FILE_NAME, ActualFile.getName());
		assertEquals(FILE_TYPE, ActualFile.getContentType());
		assertEquals(issue.getIssueID(), ActualFile.getId());
		
		// ============= release ==============
		issue = null;
		ActualFile = null;
	}
	
	// 測試刪除一筆 Issue 的檔案
	public void testdeleteAttachFile() {
		IIssue issue = this.CPB.getIssueList().get(0);		
		
		addAttachFile(productBacklogMapper, issue.getIssueID());
		
		issue = this.productBacklogMapper.getIssue(issue.getIssueID());
		AttachFileObject ActualFile = issue.getAttachFile().get(0);
		
		assertEquals(1, issue.getAttachFile().size());
		assertEquals(FILE_NAME, ActualFile.getName());
		assertEquals(FILE_TYPE, ActualFile.getContentType());
		assertEquals(issue.getIssueID(), ActualFile.getId());
		
		// 刪除此 issue 的檔案
		this.productBacklogMapper.deleteAttachFile(ActualFile.getId());
		issue = this.productBacklogMapper.getIssue(issue.getIssueID());
		assertEquals(0, issue.getAttachFile().size());
		
		// ============= release ==============
		issue = null;
		ActualFile = null;		
	}
	
	// 測試不用透過 mantis 直接取得檔案的方法
	public void testgetAttachfile() {
		IIssue issue = this.CPB.getIssueList().get(0);
		
		addAttachFile(productBacklogMapper, issue.getIssueID());
		
		issue = this.productBacklogMapper.getIssue(issue.getIssueID());
		AttachFileObject IssueFile = issue.getAttachFile().get(0);
		
		assertEquals(1, issue.getAttachFile().size());
		assertEquals(FILE_NAME, IssueFile.getName());
		assertEquals(FILE_TYPE, IssueFile.getContentType());
		assertEquals(issue.getIssueID(), IssueFile.getId());

		// ============= release ==============
		issue = null;
	}
	
	private void addAttachFile(ProductBacklogMapper mapper, long issueId) {
		AttachFileInfo attachFileInfo = new AttachFileInfo();
        attachFileInfo.issueId = issueId;
        attachFileInfo.issueType = AttachFileObject.TYPE_TASK;
        attachFileInfo.name = FILE_NAME;
        attachFileInfo.contentType = FILE_TYPE;
        attachFileInfo.projectName = CP.getProjectList().get(0).getName();
        mapper.addAttachFile(attachFileInfo);
	}
}