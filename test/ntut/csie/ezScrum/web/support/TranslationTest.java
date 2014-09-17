package ntut.csie.ezScrum.web.support;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.CheckOutIssue;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.jcis.resource.core.IProject;

public class TranslationTest extends TestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private CreateProductBacklog CPB;
	private int ProjectCount = 1;
	private int StoryCount = 10;
	private Configuration configuration = null;
	
	public TranslationTest(String testMethod) {
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
		
		// 新增 sprint
		this.CS = new CreateSprint(1, this.CP);
		this.CS.exe();
		
		// 新增Story	
		this.CPB = new CreateProductBacklog(this.StoryCount, this.CP);
		this.CPB.exe();
		
		// 為了不讓 SQL 跑太快而沒有正確更新值進去
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.setUp();
		
		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
		CopyProject copyProject = new CopyProject(this.CP);
    	copyProject.exeDelete_Project();					// 刪除測試檔案
    	
    	configuration.setTestMode(false);
		configuration.store();
		
    	// ============= release ==============
    	ini = null;
    	this.CP = null;
    	this.CS = null;
    	this.CPB = null;
    	copyProject = null;
    	configuration = null;
    	
    	super.tearDown();
	}
	
//	// 測試 translateStoryToJson(IIssue[] stories)
//	public void testtranslateStoryToJson1() throws LogonException {
//		// ================ set initial data =======================
//		String file_path = this.config.getInitialSQLPath();
//		
//		IProject project = this.CP.getProjectList().get(0);
//		ProductBacklog backlog = new ProductBacklog(project, config.getUserSession());
//		long issueID = this.CPB.getIssueList().get(0).getIssueID();
//		
//		backlog.addAttachFile(issueID, file_path);		// 將 TestData/MyWorkspace/initial_bk.sql 上傳測試
//		// ================ set initial data =======================
//		
//		IIssue[] stories = new IIssue[1];
//		stories[0] = backlog.getIssue(issueID);
//		
//		StringBuilder ExpectedSB = new StringBuilder();
//		
//		// 尚未寫
//		ExpectedSB.append("????????????");
//		
//		StringBuilder ActualSB = new StringBuilder();
//		ActualSB.append(new Translation().translateStoryToJson(stories));
//		
//		// 先測試檔案上傳部份，所以測試是否有此檔案名稱
//		assertEquals(true, ActualSB.toString().contains("initial_bk.sql"));
//		assertEquals(true, ActualSB.toString().contains("fileDownload"));
//		
//		/**
//		 * !!  注意  !!    正確測試要測試為 透過 JasonObject 所合成的 Jason 字串
//		 *                但是這個 story 只是先測試檔案名稱是否有正確顯示
//		 */
//		
//		
//		// ============= release ==============
//		backlog = null;
//		stories = null;
//		ExpectedSB = null;
//		ActualSB = null;
//	}
	
	// 測試是否有將 FilterType 加入 Story 的屬性之一
	public void testtranslateStoryToJson2() throws Exception {
		IProject project = this.CP.getProjectList().get(0);
		ProductBacklogHelper helper = new ProductBacklogHelper(project, configuration.getUserSession());
		ntut.csie.ezScrum.web.helper.ProductBacklogHelper productBacklogHelper = new ntut.csie.ezScrum.web.helper.ProductBacklogHelper(configuration.getUserSession(), project);
		IIssue[] stories = new IIssue[1];
		
		// initial data
		for (int i=0 ; i<10 ; i++) {
			
//			helper.edit(this.CPB.getIssueList().get(i).getIssueID(), "0", "0", "0", "0", "0", "0");
			productBacklogHelper.editStory(this.CPB.getIssueList().get(i).getIssueID(), "0", "0", "0", "0", "0", "0");
			
			IIssue issue = helper.getIssue(this.CPB.getIssueList().get(i).getIssueID());
			assertEquals("0", issue.getEstimated());
			assertEquals("0", issue.getImportance());
			assertEquals("0", issue.getValue());
		}
		
		StringBuilder ActualSB = new StringBuilder();
		for (int i=0 ; i<10 ; i++) {
			stories[0] = helper.getIssue(this.CPB.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories));
			assertFalse(ActualSB.toString().contains("DONE"));
			assertFalse(ActualSB.toString().contains("DETAIL"));
			assertTrue(ActualSB.toString().contains("BACKLOG"));
		}
		
//		ArrayList<Long> IssueID = new ArrayList<Long>();
//		IssueID.add((long)this.CPB.getIssueList().get(0).getIssueID());
//		IssueID.add((long)this.CPB.getIssueList().get(1).getIssueID());
//		IssueID.add((long)this.CPB.getIssueList().get(2).getIssueID());
//		IssueID.add((long)this.CPB.getIssueList().get(3).getIssueID());
//		CheckOutIssue coi =  new CheckOutIssue(IssueID, this.CP);
		
		List<IIssue> issueList = new ArrayList<IIssue>();
		issueList.add(this.CPB.getIssueList().get(0));
		issueList.add(this.CPB.getIssueList().get(1));
		issueList.add(this.CPB.getIssueList().get(2));
		issueList.add(this.CPB.getIssueList().get(3));
		CheckOutIssue coi =  new CheckOutIssue( issueList, this.CP);
		
		// 將前四筆狀態 done
		coi.exeDone_Issues();
		
		stories = new IIssue[10];
		
		// 驗證 done 狀態
		for (int i=0 ; i<4 ; i++) {
			stories[i] = helper.getIssue(this.CPB.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));
			
			assertTrue(ActualSB.toString().contains("DONE"));
			assertFalse(ActualSB.toString().contains("DETAIL"));
			assertFalse(ActualSB.toString().contains("BACKLOG"));
		}

		// 驗證 backlog 狀態
		for (int i=4 ; i<9 ; i++) {
			stories[i] = helper.getIssue(this.CPB.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));
			assertFalse(ActualSB.toString().contains("DONE"));
			assertFalse(ActualSB.toString().contains("DETAIL"));
			assertTrue(ActualSB.toString().contains("BACKLOG"));
		}
		
		// 驗證 detail 狀態
		for (int i=0 ; i<10 ; i++) {
			stories[i] = helper.getIssue(this.CPB.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));
			assertFalse(ActualSB.toString().contains("DETAIL"));
		}
		
		
		// 將 4 - 5 改成 detail (目前判斷是value/estimation/importance這三者皆要有值才算是)
//		helper.edit(this.CPB.getIssueList().get(4).getIssueID(), "", "1", "1", "1", "", "");
		productBacklogHelper.editStory(this.CPB.getIssueList().get(4).getIssueID(), "", "1", "1", "1", "", "");
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		helper.edit(this.CPB.getIssueList().get(5).getIssueID(), "", "1", "1", "1", "", "");
		productBacklogHelper.editStory(this.CPB.getIssueList().get(5).getIssueID(), "", "1", "1", "1", "", "");
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		// 驗證  done 狀態
		for (int i=0 ; i<4 ; i++) {
			stories[i] = helper.getIssue(this.CPB.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));
			assertTrue(ActualSB.toString().contains("DONE"));
			assertFalse(ActualSB.toString().contains("DETAIL"));
			assertFalse(ActualSB.toString().contains("BACKLOG"));
		}		
		
		// 驗證  detail 狀態
		for (int i=4 ; i<6 ; i++) {
			stories[i] = helper.getIssue(this.CPB.getIssueList().get(i).getIssueID());						
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));									
			assertFalse(ActualSB.toString().contains("DONE"));
			assertTrue(ActualSB.toString().contains("DETAIL"));
			assertFalse(ActualSB.toString().contains("BACKLOG"));		
		}

		// 驗證  backlog 狀態
		for (int i=7 ; i<10 ; i++) {
			stories[i] = helper.getIssue(this.CPB.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));
			assertFalse(ActualSB.toString().contains("DONE"));
			assertFalse(ActualSB.toString().contains("DETAIL"));
			assertTrue(ActualSB.toString().contains("BACKLOG"));	
		}
	}
}