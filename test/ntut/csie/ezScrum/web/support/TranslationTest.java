package ntut.csie.ezScrum.web.support;

import java.util.ArrayList;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CheckOutIssue;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.jcis.resource.core.IProject;

public class TranslationTest extends TestCase {
	private CreateProject mCreateProject;
	private CreateSprint mCreateSprint;
	private CreateProductBacklog mCreateProjectBacklog;
	private int mProjectCount = 1;
	private int mStoryCount = 10;
	private Configuration mConfig = null;
	private IProject mProject;

	public TranslationTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.store();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增 Project
		mCreateProject = new CreateProject(mProjectCount);
		mCreateProject.exeCreate();

		// 新增 Sprint
		mCreateSprint = new CreateSprint(1, mCreateProject);
		mCreateSprint.exe();

		// 新增 Story
		mCreateProjectBacklog = new CreateProductBacklog(mStoryCount, mCreateProject);
		mCreateProjectBacklog.exe();
		
		mProject = mCreateProject.getProjectList().get(0);

		// 為了不讓 SQL 跑太快而沒有正確更新值進去
		Thread.sleep(1000);

		super.setUp();
		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(mConfig.getDataPath());

		mConfig.setTestMode(false);
		mConfig.store();

		// ============= release ==============
		ini = null;
		mCreateProject = null;
		mCreateSprint = null;
		mCreateProjectBacklog = null;
		mConfig = null;

		super.tearDown();
	}

	// // 測試 translateStoryToJson(IIssue[] stories)
	// public void testtranslateStoryToJson1() throws LogonException {
	// // ================ set initial data =======================
	// String file_path = this.config.getInitialSQLPath();
	//
	// IProject project = this.CP.getProjectList().get(0);
	// ProductBacklog backlog = new ProductBacklog(project, config.getUserSession());
	// long issueID = this.CPB.getIssueList().get(0).getIssueID();
	//
	// backlog.addAttachFile(issueID, file_path); // 將 TestData/MyWorkspace/initial_bk.sql 上傳測試
	// // ================ set initial data =======================
	//
	// IIssue[] stories = new IIssue[1];
	// stories[0] = backlog.getIssue(issueID);
	//
	// StringBuilder ExpectedSB = new StringBuilder();
	//
	// // 尚未寫
	// ExpectedSB.append("????????????");
	//
	// StringBuilder ActualSB = new StringBuilder();
	// ActualSB.append(new Translation().translateStoryToJson(stories));
	//
	// // 先測試檔案上傳部份，所以測試是否有此檔案名稱
	// assertEquals(true, ActualSB.toString().contains("initial_bk.sql"));
	// assertEquals(true, ActualSB.toString().contains("fileDownload"));
	//
	// /**
	// * !! 注意 !! 正確測試要測試為 透過 JasonObject 所合成的 Jason 字串
	// * 但是這個 story 只是先測試檔案名稱是否有正確顯示
	// */
	//
	//
	// // ============= release ==============
	// backlog = null;
	// stories = null;
	// ExpectedSB = null;
	// ActualSB = null;
	// }

	// 測試是否有將 FilterType 加入 Story 的屬性之一
	public void testtranslateStoryToJson2() throws Exception {
		ProductBacklogHelper PBHelper = new ProductBacklogHelper(mConfig.getUserSession(), mProject);
		IIssue[] stories = new IIssue[1];

		// initial data
		for (int i = 0; i < 10; i++) {
			PBHelper.editStory(mCreateProjectBacklog.getIssueList().get(i).getIssueID(), "0", "0", "0", "0", "0", "0", true);
			IIssue issue = PBHelper.getIssue(mCreateProjectBacklog.getIssueList().get(i).getIssueID());

			assertEquals("0", issue.getEstimated());
			assertEquals("0", issue.getImportance());
			assertEquals("0", issue.getValue());
		}

		StringBuilder ActualSB = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			stories[0] = PBHelper.getIssue(mCreateProjectBacklog.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories));
			assertFalse(ActualSB.toString().contains("DONE"));
			assertFalse(ActualSB.toString().contains("DETAIL"));
			assertTrue(ActualSB.toString().contains("BACKLOG"));
		}

		ArrayList<IIssue> issueList = new ArrayList<IIssue>();
		issueList.add(mCreateProjectBacklog.getIssueList().get(0));
		issueList.add(mCreateProjectBacklog.getIssueList().get(1));
		issueList.add(mCreateProjectBacklog.getIssueList().get(2));
		issueList.add(mCreateProjectBacklog.getIssueList().get(3));
		CheckOutIssue coi = new CheckOutIssue(issueList, mCreateProject);
		// 將前四筆狀態 done
		coi.exeDone_Issues();

		stories = new IIssue[10];

		// 驗證 done 狀態
		for (int i = 0; i < 4; i++) {
			stories[i] = PBHelper.getIssue(mCreateProjectBacklog.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));
			assertTrue(ActualSB.toString().contains("DONE"));
			assertFalse(ActualSB.toString().contains("DETAIL"));
			assertFalse(ActualSB.toString().contains("BACKLOG"));
		}

		// 驗證 backlog 狀態
		for (int i = 4; i < 9; i++) {
			stories[i] = PBHelper.getIssue(mCreateProjectBacklog.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));
			assertFalse(ActualSB.toString().contains("DONE"));
			assertFalse(ActualSB.toString().contains("DETAIL"));
			assertTrue(ActualSB.toString().contains("BACKLOG"));
		}

		// 驗證 detail 狀態
		for (int i = 0; i < 10; i++) {
			stories[i] = PBHelper.getIssue(mCreateProjectBacklog.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));
			assertFalse(ActualSB.toString().contains("DETAIL"));
		}

		// 將 4 - 5 改成 detail (目前判斷是 value / estimation / importance 這三者皆要有值才算是)
		PBHelper.editStory(mCreateProjectBacklog.getIssueList().get(4).getIssueID(), "", "1", "1", "1", "", "", true);
		Thread.sleep(1000);

		PBHelper.editStory(mCreateProjectBacklog.getIssueList().get(5).getIssueID(), "", "1", "1", "1", "", "", true);
		Thread.sleep(1000);
		
		// 驗證 done 狀態
		for (int i = 0; i < 4; i++) {
			stories[i] = PBHelper.getIssue(mCreateProjectBacklog.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));
			assertTrue(ActualSB.toString().contains("DONE"));
			assertFalse(ActualSB.toString().contains("DETAIL"));
			assertFalse(ActualSB.toString().contains("BACKLOG"));
		}

		// 驗證 detail 狀態
		for (int i = 4; i < 6; i++) {
			stories[i] = PBHelper.getIssue(mCreateProjectBacklog.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));
			assertFalse(ActualSB.toString().contains("DONE"));
			assertTrue(ActualSB.toString().contains("DETAIL"));
			assertFalse(ActualSB.toString().contains("BACKLOG"));
		}

		// 驗證 backlog 狀態
		for (int i = 7; i < 10; i++) {
			stories[i] = PBHelper.getIssue(mCreateProjectBacklog.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));
			assertFalse(ActualSB.toString().contains("DONE"));
			assertFalse(ActualSB.toString().contains("DETAIL"));
			assertTrue(ActualSB.toString().contains("BACKLOG"));
		}
	}
}