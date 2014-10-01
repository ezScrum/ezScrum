package ntut.csie.ezScrum.web.helper;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.jcis.resource.core.IProject;

public class ProductBacklogHelperTest extends TestCase {
	private CreateProject CP;
	private CreateProductBacklog CPB;
	
	private int ProjectCount = 1;
	private int StoryCount = 1;
	
	private ProductBacklogHelper productBacklogHelper = null;
	private Configuration configuration = null;
	
	public ProductBacklogHelperTest(String testMethod) {
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
		productBacklogHelper = new ProductBacklogHelper(project, configuration.getUserSession());
		
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
    	projectManager = null;
    	configuration = null;
    	
    	super.tearDown();
	}
	
	public void testAddAttachFile() {
		AttachFileInfo attachFileInfo = new AttachFileInfo();
		attachFileInfo.name = "initial_bk.sql";
		attachFileInfo.issueId = CPB.getIssueList().get(0).getIssueID();
		attachFileInfo.issueType = AttachFileObject.TYPE_STORY;
		attachFileInfo.projectName = this.CP.getProjectList().get(0).getName();
		
		File sqlFile = new File(configuration.getInitialSQLPath());
		
		try {
			long id = productBacklogHelper.addAttachFile(attachFileInfo, sqlFile);
			AttachFileObject attachFile = productBacklogHelper.getAttachFile(id);
			File actualFile = new File(attachFile.getPath());
			assertEquals(sqlFile.length(), actualFile.length());
			assertEquals(attachFileInfo.name, attachFile.getName());
			assertEquals(attachFileInfo.issueType, attachFile.getIssueType());
		} catch (IOException e) {
			System.out.println(e);
			assertTrue(false);
		}
	}
}
