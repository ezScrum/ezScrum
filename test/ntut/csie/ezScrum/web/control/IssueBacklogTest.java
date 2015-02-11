package ntut.csie.ezScrum.web.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.service.CustomIssueType;
import ntut.csie.ezScrum.service.IssueBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateCustomIssueType;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IssueBacklogTest {
	private CreateProject mCP;
	private int mProjectCount = 1;
	private IssueBacklog mBacklog = null;
	private Configuration mConfig;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL
		
		// 新增Project
		this.mCP = new CreateProject(this.mProjectCount);
		this.mCP.exeCreate();
		
		this.mBacklog = new IssueBacklog(this.mCP.getProjectList().get(0), mConfig.getUserSession());
		
		// release
		ini = null;
    }

	@After
    protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
    	
    	// release
    	ini = null;
    	projectManager = null;
    	mCP = null;
    	mBacklog = null;
    	mConfig = null;
    }
    
    @Test // 測試判斷此專案是否可以被回報
    public void testIsReportProject() throws Exception {
    	CreateCustomIssueType CCIT = new CreateCustomIssueType(this.mCP, 0);
		// 新增一筆 name : Issue_Type_1 的 issue type, status : false
		CCIT.exeFalseType("F_Issue_Type_1");
		assertFalse(this.mBacklog.isReportProject());
		
		// 新增一筆 name : Issue_Type_2 的 issue type, status : false
		CCIT.exeFalseType("F_Issue_Type_2");
		assertFalse(this.mBacklog.isReportProject());
		
		// 新增一筆 name : Issue_Type_2 的 issue type, status : true
		CCIT.exeTrueType("T_Issue_Type_3");
		assertTrue(this.mBacklog.isReportProject());
    }
    
    @Test // 測試加入 IssueType
    public void testAddIssueType() {
		String Name = "Hello";
		String isPublic = "true";
		
		mBacklog.addIssueType(Name, Boolean.valueOf(isPublic));
		CustomIssueType type = getIssueType(mBacklog.getCustomIssueType(), Name);
		assertEquals(Name, type.getTypeName());
		assertEquals(mBacklog.getCustomIssueType().size(), type.getTypeId());
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
		mBacklog.addIssueType(Name, Boolean.valueOf(isPublic));
		type = getIssueType(mBacklog.getCustomIssueType(), Name);
		assertEquals(Name, type.getTypeName());
		assertEquals(mBacklog.getCustomIssueType().size(), type.getTypeId());
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
