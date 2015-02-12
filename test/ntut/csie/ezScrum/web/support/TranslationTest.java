package ntut.csie.ezScrum.web.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TranslationTest{
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateProductBacklog mCPB;
	private int mProjectCount = 1;
	private int mStoryCount = 10;
	private Configuration mConfig = null;
	private IProject mProject;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增 Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		// 新增 Sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// 新增 Story
		mCPB = new CreateProductBacklog(mStoryCount, mCP);
		mCPB.exe();
		
		mProject = mCP.getProjectList().get(0);

		// 為了不讓 SQL 跑太快而沒有正確更新值進去
		Thread.sleep(500);

		// ============= release ==============
		ini = null;
	}

	@After
	public void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mCPB = null;
		mConfig = null;
		mProject = null;
	}

	// 測試是否有將 FilterType 加入 Story 的屬性之一
	@Test
	public void testTranslateStoryToJson2() throws Exception {
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(mConfig.getUserSession(), mProject);
		IIssue[] stories = new IIssue[1];

		// initial data
		for (int i = 0; i < 10; i++) {
			productBacklogHelper.editStory(mCPB.getIssueList().get(i).getIssueID(), "0", "0", "0", "0", "0", "0", true);
			IIssue issue = productBacklogHelper.getIssue(mCPB.getIssueList().get(i).getIssueID());

			assertEquals("0", issue.getEstimated());
			assertEquals("0", issue.getImportance());
			assertEquals("0", issue.getValue());
			Thread.sleep(500);
		}

		StringBuilder ActualSB = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			stories[0] = productBacklogHelper.getIssue(mCPB.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories));
			assertFalse(ActualSB.toString().contains("DONE"));
			assertFalse(ActualSB.toString().contains("DETAIL"));
			assertTrue(ActualSB.toString().contains("BACKLOG"));
		}

		ArrayList<IIssue> issueList = new ArrayList<IIssue>();
		issueList.add(mCPB.getIssueList().get(0));
		issueList.add(mCPB.getIssueList().get(1));
		issueList.add(mCPB.getIssueList().get(2));
		issueList.add(mCPB.getIssueList().get(3));
		CheckOutIssue coi = new CheckOutIssue(issueList, mCP);
		// 將前四筆狀態 done
		coi.exeDone_Issues();

		stories = new IIssue[10];

		// 驗證 done 狀態
		for (int i = 0; i < 4; i++) {
			stories[i] = productBacklogHelper.getIssue(mCPB.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));
			assertTrue(ActualSB.toString().contains("DONE"));
			assertFalse(ActualSB.toString().contains("DETAIL"));
			assertFalse(ActualSB.toString().contains("BACKLOG"));
		}

		// 驗證 backlog 狀態
		for (int i = 4; i < 9; i++) {
			stories[i] = productBacklogHelper.getIssue(mCPB.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));
			assertFalse(ActualSB.toString().contains("DONE"));
			assertFalse(ActualSB.toString().contains("DETAIL"));
			assertTrue(ActualSB.toString().contains("BACKLOG"));
		}

		// 驗證 detail 狀態
		for (int i = 0; i < 10; i++) {
			stories[i] = productBacklogHelper.getIssue(mCPB.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));
			assertFalse(ActualSB.toString().contains("DETAIL"));
		}

		// 將 4 - 5 改成 detail (目前判斷是 value / estimation / importance 這三者皆要有值才算是)
		productBacklogHelper.editStory(mCPB.getIssueList().get(4).getIssueID(), "", "1", "1", "1", "", "", true);
		Thread.sleep(1000);

		productBacklogHelper.editStory(mCPB.getIssueList().get(5).getIssueID(), "", "1", "1", "1", "", "", true);
		Thread.sleep(1000);
		
		// 驗證 done 狀態
		for (int i = 0; i < 4; i++) {
			stories[i] = productBacklogHelper.getIssue(mCPB.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));
			assertTrue(ActualSB.toString().contains("DONE"));
			assertFalse(ActualSB.toString().contains("DETAIL"));
			assertFalse(ActualSB.toString().contains("BACKLOG"));
		}

		// 驗證 detail 狀態
		for (int i = 4; i < 6; i++) {
			stories[i] = productBacklogHelper.getIssue(mCPB.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));
			assertFalse(ActualSB.toString().contains("DONE"));
			assertTrue(ActualSB.toString().contains("DETAIL"));
			assertFalse(ActualSB.toString().contains("BACKLOG"));
		}

		// 驗證 backlog 狀態
		for (int i = 7; i < 10; i++) {
			stories[i] = productBacklogHelper.getIssue(mCPB.getIssueList().get(i).getIssueID());
			ActualSB = new StringBuilder();
			ActualSB.append(new Translation().translateStoryToJson(stories[i]));
			assertFalse(ActualSB.toString().contains("DONE"));
			assertFalse(ActualSB.toString().contains("DETAIL"));
			assertTrue(ActualSB.toString().contains("BACKLOG"));
		}
	}
}