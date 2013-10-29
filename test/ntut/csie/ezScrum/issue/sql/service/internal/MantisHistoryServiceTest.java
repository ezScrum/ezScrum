package ntut.csie.ezScrum.issue.sql.service.internal;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueHistory;
import ntut.csie.ezScrum.issue.sql.service.core.ITSPrefsStorage;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.jcis.resource.core.IProject;

public class MantisHistoryServiceTest extends TestCase {
	private CreateProject CP;
	private int ProjectCount = 1;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private MantisHistoryService MHSservice;
	private MantisService MService;
	
	public MantisHistoryServiceTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe();											// 初始化 SQL
		
		// 新增Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();
		
		IProject project = this.CP.getProjectList().get(0);
		ITSPrefsStorage itsPrefs = new ITSPrefsStorage(project, config.getUserSession());
		this.MService = new MantisService(itsPrefs);
		this.MHSservice = new MantisHistoryService(this.MService.getControl(), itsPrefs);
		
		super.setUp();
		
		// ============= release ==============
		ini = null;
		project = null;
		itsPrefs = null;
	}
	
	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe();											// 初始化 SQL
		
		//再一次關閉SQL連線，以免導致Project無法刪除
		MService.closeConnect();
		
		CopyProject copyProject = new CopyProject(this.CP);
    	copyProject.exeDelete_Project();					// 刪除測試檔案
    	
    	// ============= release ==============
    	ini = null;
    	copyProject = null;
    	this.CP = null;
    	this.config = null;
    	this.MHSservice = null;
    	this.MService = null;
    	
    	super.tearDown();
	}
	
	public void testaddMantisActionHistory_nullaccount() {
		this.MService.openConnect();
		// test method
		this.MHSservice.addMantisActionHistory((long)100, "XXX", "10", "50", 18, new Date());
		// test method
		// f1 沒有任何事情發生
		
		// close connection
		this.MService.closeConnect();
	}
	
	public void testaddMantisActionHistory() {
		// add 5 issues
		CreateProductBacklog cpb = new CreateProductBacklog(5, this.CP);
		cpb.exe();
		
		try {
			new Thread().sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.MService.openConnect();
		// test method
		this.MHSservice.addMantisActionHistory(1, "status", "0", "50", 0, new Date());
		// test method
		IIssue issue_1 = this.MService.getIssue(1);
		List<IIssueHistory> histories = this.MHSservice.getIssueHistory(issue_1);
		assertEquals(histories.get(0).getFieldName(), "New Issue");
		assertEquals(histories.get(0).getNewValue(), "0");
		assertEquals(histories.get(0).getOldValue(), "0");
		assertEquals(histories.get(0).getType(), 1);
		assertEquals(histories.get(1).getFieldName(), "status");
		assertEquals(histories.get(1).getNewValue(), "50");
		assertEquals(histories.get(1).getOldValue(), "0");
		assertEquals(histories.get(1).getType(), 0);
		
		Date d = new Date();
		d.setTime(d.getTime() + 1000);
		// test method
		this.MHSservice.addMantisActionHistory(2, "handler_id", "0", "1", 0, d);
		// test method
		IIssue issue_2 = this.MService.getIssue(2);
		histories = this.MHSservice.getIssueHistory(issue_2);
		assertEquals(histories.get(0).getFieldName(), "New Issue");
		assertEquals(histories.get(0).getNewValue(), "0");
		assertEquals(histories.get(0).getOldValue(), "0");
		assertEquals(histories.get(0).getType(), 1);
		assertEquals(histories.get(1).getFieldName(), "handler_id");
		assertEquals(histories.get(1).getNewValue(), "1");
		assertEquals(histories.get(1).getOldValue(), "0");
		assertEquals(histories.get(1).getType(), 0);
		
		d.setTime(d.getTime() + 1000);
		// test method
		this.MHSservice.addMantisActionHistory(3, "status", 10, 50, 0, d);
		// test method
		IIssue issue_3 = this.MService.getIssue(3);
		histories = this.MHSservice.getIssueHistory(issue_3);
		assertEquals(histories.get(0).getFieldName(), "New Issue");
		assertEquals(histories.get(0).getNewValue(), "0");
		assertEquals(histories.get(0).getOldValue(), "0");
		assertEquals(histories.get(0).getType(), 1);
		assertEquals(histories.get(1).getFieldName(), "status");
		assertEquals(histories.get(1).getNewValue(), "50");
		assertEquals(histories.get(1).getOldValue(), "10");
		assertEquals(histories.get(1).getType(), 0);
		
		d.setTime(d.getTime() + 1000);
		// test method
		this.MHSservice.addMantisActionHistory(4, null, 0, (long)50, 1, d);
		// test method
		IIssue issue_4 = this.MService.getIssue(4);
		histories = this.MHSservice.getIssueHistory(issue_4);
		assertEquals(histories.get(0).getFieldName(), "New Issue");
		assertEquals(histories.get(0).getNewValue(), "0");
		assertEquals(histories.get(0).getOldValue(), "0");
		assertEquals(histories.get(0).getType(), 1);
		assertEquals(histories.get(1).getFieldName(), "New Issue");
		assertEquals(histories.get(1).getNewValue(), "50");
		assertEquals(histories.get(1).getOldValue(), "0");
		assertEquals(histories.get(1).getType(), 1);
		
		d.setTime(d.getTime() + 1000);
		// test method
		this.MHSservice.addMantisActionHistory(5, "Sprint", "-1", "1", 0, d);
		// test method
		IIssue issue_5 = this.MService.getIssue(5);
		histories = this.MHSservice.getIssueHistory(issue_5);
		assertEquals(histories.get(0).getFieldName(), "New Issue");
		assertEquals(histories.get(0).getNewValue(), "0");
		assertEquals(histories.get(0).getOldValue(), "0");
		assertEquals(histories.get(0).getType(), 1);
		assertEquals(histories.get(1).getFieldName(), "Sprint");
		assertEquals(histories.get(1).getNewValue(), "1");
		assertEquals(histories.get(1).getOldValue(), "-1");
		assertEquals(histories.get(1).getType(), 0);
		
		// close connection
		this.MService.closeConnect();
	}
	
	public void testupdateHistoryModifiedDate() {
		CreateProductBacklog cpb = new CreateProductBacklog(1, this.CP);
		cpb.exe();
		
		Date today = new Date();
		
		this.MService.openConnect();
		Date d = new Date();
		d.setTime(d.getTime() + 1000);
		this.MHSservice.addMantisActionHistory(1, "status", "0", "50", 0, d);
		IIssue issue_1 = this.MService.getIssue(1);
		List<IIssueHistory> histories = this.MHSservice.getIssueHistory(issue_1);
		assertEquals(histories.get(0).getFieldName(), "New Issue");
		assertEquals(histories.get(0).getNewValue(), "0");
		assertEquals(histories.get(0).getOldValue(), "0");
		assertEquals(histories.get(0).getType(), 1);
		assertEquals(histories.get(1).getFieldName(), "status");
		assertEquals(histories.get(1).getNewValue(), "50");
		assertEquals(histories.get(1).getOldValue(), "0");
		assertEquals(histories.get(1).getType(), 0);
		
		// 因為update不能設定 date，所以休息一下進廣告
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// test method
		this.MHSservice.updateHistoryModifiedDate(1, today);
		// test method
		issue_1 = this.MService.getIssue(1);
		histories = this.MHSservice.getIssueHistory(issue_1);
		assertEquals(histories.get(0).getFieldName(), "New Issue");
		assertEquals(histories.get(0).getNewValue(), "0");
		assertEquals(histories.get(0).getOldValue(), "0");
		assertEquals(histories.get(0).getType(), 1);
		assertEquals(histories.get(1).getFieldName(), "status");
		assertEquals(histories.get(1).getNewValue(), "50");
		assertEquals(histories.get(1).getOldValue(), "0");
		assertEquals(histories.get(1).getType(), 0);
//		assertEquals(histories.get(1).getModifyDate(), today.getTime());
		
		// close connection
		this.MService.closeConnect();
	}
	
	public void testgetIssueHistory() {
		CreateProductBacklog cpb = new CreateProductBacklog(5, this.CP);
		cpb.exe();
		
		this.MService.openConnect();
		Date d = new Date();
		d.setTime(d.getTime() + 1000);
		this.MHSservice.addMantisActionHistory(1, "status", "0", "50", 0, d);
		IIssue issue_1 = this.MService.getIssue(1);
		// test method
		List<IIssueHistory> histories = this.MHSservice.getIssueHistory(issue_1);
		// test method
		assertEquals(histories.get(0).getFieldName(), "New Issue");
		assertEquals(histories.get(0).getNewValue(), "0");
		assertEquals(histories.get(0).getOldValue(), "0");
		assertEquals(histories.get(0).getType(), 1);
		assertEquals(histories.get(1).getFieldName(), "status");
		assertEquals(histories.get(1).getNewValue(), "50");
		assertEquals(histories.get(1).getOldValue(), "0");
		assertEquals(histories.get(1).getType(), 0);
		
		d.setTime(d.getTime() + 1000);
		this.MHSservice.addMantisActionHistory(1, "Sprint", "-1", "1", 0, d);
		// test method
		histories = this.MHSservice.getIssueHistory(issue_1);
		// test method
		assertEquals(histories.get(0).getFieldName(), "New Issue");
		assertEquals(histories.get(0).getNewValue(), "0");
		assertEquals(histories.get(0).getOldValue(), "0");
		assertEquals(histories.get(0).getType(), 1);
		assertEquals(histories.get(1).getFieldName(), "status");
		assertEquals(histories.get(1).getNewValue(), "50");
		assertEquals(histories.get(1).getOldValue(), "0");
		assertEquals(histories.get(1).getType(), 0);
		assertEquals(histories.get(2).getFieldName(), "Sprint");
		assertEquals(histories.get(2).getNewValue(), "1");
		assertEquals(histories.get(2).getOldValue(), "-1");
		assertEquals(histories.get(2).getType(), 0);
		
		d.setTime(d.getTime() + 1000);
		this.MHSservice.addMantisActionHistory(1, "status", 10, (long)50, 0, d);
		// test method
		histories = this.MHSservice.getIssueHistory(issue_1);
		// test method
		assertEquals(histories.get(0).getFieldName(), "New Issue");
		assertEquals(histories.get(0).getNewValue(), "0");
		assertEquals(histories.get(0).getOldValue(), "0");
		assertEquals(histories.get(0).getType(), 1);
		assertEquals(histories.get(1).getFieldName(), "status");
		assertEquals(histories.get(1).getNewValue(), "50");
		assertEquals(histories.get(1).getOldValue(), "0");
		assertEquals(histories.get(1).getType(), 0);
		assertEquals(histories.get(2).getFieldName(), "Sprint");
		assertEquals(histories.get(2).getNewValue(), "1");
		assertEquals(histories.get(2).getOldValue(), "-1");
		assertEquals(histories.get(2).getType(), 0);
		assertEquals(histories.get(3).getFieldName(), "status");
		assertEquals(histories.get(3).getNewValue(), "50");
		assertEquals(histories.get(3).getOldValue(), "10");
		assertEquals(histories.get(3).getType(), 0);
		
		// close connection
		this.MService.closeConnect();
	}
	
	public void testinitHistory() {
		CreateProductBacklog cpb = new CreateProductBacklog(1, this.CP);
		cpb.exe();
		
		this.MService.openConnect();
		Date d = new Date();
		d.setTime(d.getTime() + 1000);
		this.MHSservice.addMantisActionHistory(1, "status", "0", "50", 0, d);
		d.setTime(d.getTime() + 1000);
		this.MHSservice.addMantisActionHistory(1, "Sprint", "-1", "1", 0, d);
		d.setTime(d.getTime() + 1000);
		this.MHSservice.addMantisActionHistory(1, "status", 10, (long)50, 0, d);
		
		IIssue issue_1 = this.MService.getIssue(1);
		this.MHSservice.initHistory(issue_1);
		
		// test method
		List<IIssueHistory> histories = issue_1.getHistory();
		// test method
		assertEquals(histories.get(0).getFieldName(), "New Issue");
		assertEquals(histories.get(0).getNewValue(), "0");
		assertEquals(histories.get(0).getOldValue(), "0");
		assertEquals(histories.get(0).getType(), 1);
		assertEquals(histories.get(1).getFieldName(), "status");
		assertEquals(histories.get(1).getNewValue(), "50");
		assertEquals(histories.get(1).getOldValue(), "0");
		assertEquals(histories.get(1).getType(), 0);
		assertEquals(histories.get(2).getFieldName(), "Sprint");
		assertEquals(histories.get(2).getNewValue(), "1");
		assertEquals(histories.get(2).getOldValue(), "-1");
		assertEquals(histories.get(2).getType(), 0);
		assertEquals(histories.get(3).getFieldName(), "status");
		assertEquals(histories.get(3).getNewValue(), "50");
		assertEquals(histories.get(3).getOldValue(), "10");
		assertEquals(histories.get(3).getType(), 0);
		
		// close connection
		this.MService.closeConnect();
	}
	
	public void testremoveHistory() {
		CreateProductBacklog cpb = new CreateProductBacklog(1, this.CP);
		cpb.exe();
		
		this.MService.openConnect();
		Date d = new Date();
		d.setTime(d.getTime() + 1000);
		this.MHSservice.addMantisActionHistory(1, "status", "0", "50", 0, d);
		d.setTime(d.getTime() + 1000);
		this.MHSservice.addMantisActionHistory(1, "Sprint", "-1", "1", 0, d);
		d.setTime(d.getTime() + 1000);
		this.MHSservice.addMantisActionHistory(1, "status", 10, (long)50, 0, d);
		
		IIssue issue_1 = this.MService.getIssue(1);
		
		this.MHSservice.initHistory(issue_1);
		
		List<IIssueHistory> histories = issue_1.getHistory();
		assertEquals(histories.get(0).getFieldName(), "New Issue");
		assertEquals(histories.get(0).getNewValue(), "0");
		assertEquals(histories.get(0).getOldValue(), "0");
		assertEquals(histories.get(0).getType(), 1);
		assertEquals(histories.get(1).getFieldName(), "status");
		assertEquals(histories.get(1).getNewValue(), "50");
		assertEquals(histories.get(1).getOldValue(), "0");
		assertEquals(histories.get(1).getType(), 0);
		assertEquals(histories.get(2).getFieldName(), "Sprint");
		assertEquals(histories.get(2).getNewValue(), "1");
		assertEquals(histories.get(2).getOldValue(), "-1");
		assertEquals(histories.get(2).getType(), 0);
		assertEquals(histories.get(3).getFieldName(), "status");
		assertEquals(histories.get(3).getNewValue(), "50");
		assertEquals(histories.get(3).getOldValue(), "10");
		assertEquals(histories.get(3).getType(), 0);	
		
		
		// test method
		this.MHSservice.removeHistory("1");
		// test method
		issue_1 = this.MService.getIssue(1);
		histories = issue_1.getHistory();
		assertEquals(0, histories.size());
		
		// close connection
		this.MService.closeConnect();
	}
}