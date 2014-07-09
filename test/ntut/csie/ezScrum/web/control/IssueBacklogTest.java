package ntut.csie.ezScrum.web.control;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.service.CustomIssueType;
import ntut.csie.ezScrum.service.IssueBacklog;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateCustomIssueType;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;

public class IssueBacklogTest extends TestCase {
	private CreateProject CP;
	private int ProjectCount = 1;
	private IssueBacklog backlog = null;
	
	private Configuration configuration;
	
	public IssueBacklogTest(String testMethod) {
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
		
		this.backlog = new IssueBacklog(this.CP.getProjectList().get(0), configuration.getUserSession());
		
		super.setUp();
		
		// release
		ini = null;
    }

    protected void tearDown() throws IOException, Exception {
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
    	this.backlog = null;
    	configuration = null;
    }
    
    // 測試判斷此專案是否可以被回報
    public void testisReportProject() throws Exception {
    	CreateCustomIssueType CCIT = new CreateCustomIssueType(this.CP, 0);
		// 新增一筆 name : Issue_Type_1 的 issue type, status : false
		CCIT.exeFalseType("F_Issue_Type_1");
		assertFalse(this.backlog.isReportProject());
		
		// 新增一筆 name : Issue_Type_2 的 issue type, status : false
		CCIT.exeFalseType("F_Issue_Type_2");
		assertFalse(this.backlog.isReportProject());
		
		// 新增一筆 name : Issue_Type_2 的 issue type, status : true
		CCIT.exeTrueType("T_Issue_Type_3");
		assertTrue(this.backlog.isReportProject());
    }
    
    // 測試回傳的CustomIssueType 是否正確
/*    public void testgetCustomIssueType() throws Exception {
    	int type_size = this.backlog.getCustomIssueType().size();
    	
    	CreateCustomIssueType CCIT = new CreateCustomIssueType(this.CP, 0);
		// 新增一筆 name : Issue_Type_1 的 issue type, status : false
		CCIT.exeFalseType("F_Issue_Type_1");

		// 因為阿春仔的關係，所以要自動 +2 （就目前而言是+2，他說可能改天會變成+3 XD ）
		int new_type_size = this.backlog.getCustomIssueType().size();
		assertEquals(1, (new_type_size-type_size) );
		// 因為阿春仔的關係，所以要自動 +2 （就目前而言是+2，他說可能改天會變成+3 XD ）	
		
		assertEquals("F_Issue_Type_1", this.backlog.getCustomIssueType().get(0).getTypeName().toString());
		assertFalse(this.backlog.getCustomIssueType().get(0).ispublic());
		
		CCIT.exeTrueType("T_Issue_Type_2");
		
		// 因為阿春仔的關係，所以要自動 +2 （就目前而言是+2，他說可能改天會變成+3 XD ）
		new_type_size = this.backlog.getCustomIssueType().size();
		assertEquals(2, (new_type_size-type_size) );
		// 因為阿春仔的關係，所以要自動 +2 （就目前而言是+2，他說可能改天會變成+3 XD ）
		
		assertEquals("F_Issue_Type_1", this.backlog.getCustomIssueType().get(0).getTypeName().toString());
		assertEquals("T_Issue_Type_2", this.backlog.getCustomIssueType().get(1).getTypeName().toString());
		assertFalse(this.backlog.getCustomIssueType().get(0).ispublic());
		assertTrue(this.backlog.getCustomIssueType().get(1).ispublic());
    }*/
    
    // 測試加入 IssueType
    public void testaddIssueType() {
		String Name = "Hello";
		String isPublic = "true";
		
		backlog.addIssueType(Name, Boolean.valueOf(isPublic));
		CustomIssueType type = getIssueType(backlog.getCustomIssueType(), Name);
		assertEquals(Name, type.getTypeName());
		assertEquals(backlog.getCustomIssueType().size(), type.getTypeId());
		assertEquals(true, type.ispublic());
		
		// ====================================================================
		// 測試重複加入同一筆不會出錯，這個測試是對的，不過原本程式碼尚未打算處理此部份
		// ====================================================================
//		backlog.addIssueType(Name, Boolean.valueOf(isPublic));
//		type = getIssueType(backlog.getCustomIssueType(), Name);
//		assertEquals(Name, type.getTypeName());
//		assertEquals(backlog.getCustomIssueType().size(), type.getTypeId());
//		assertEquals(true, type.ispublic());
		
		// 測試加入第二筆 IssueType
		Name = "Hello2";
		isPublic = "false";
		backlog.addIssueType(Name, Boolean.valueOf(isPublic));
		type = getIssueType(backlog.getCustomIssueType(), Name);
		assertEquals(Name, type.getTypeName());
		assertEquals(backlog.getCustomIssueType().size(), type.getTypeId());
		assertEquals(false, type.ispublic());
    }
    
    /**
     * 從 CustomIssueType List 取得一筆 CustomIssueType
     */
    private CustomIssueType getIssueType(List<CustomIssueType> types, String Name) {
    	for (CustomIssueType type : types) {
    		if (type.getTypeName().equals(Name)) {
    			return type;
    		}
    	}
    	
    	return null;
    }
}
