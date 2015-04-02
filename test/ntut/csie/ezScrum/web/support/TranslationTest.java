package ntut.csie.ezScrum.web.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CheckOutIssue;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.StoryInfo;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.jcis.resource.core.IProject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TranslationTest {

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

		// 讓 config 回到 Production 模式
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
	
	@Test
	public void testTranslateCustomIssueToJson() {
		
	}

	@Test
	public void testTranslateStoryToXML() {
		
	}
	
	@Test
	public void testTranslateStoryToJson() {
		
	}
	
	@Test
	public void testTranslateStoriesToJson() {
		
	}
	
	@Test
	public void testTranslateTaskToJson() {
		
	}
	
	@Test
	public void testTranslateTaskboardIssueToJson() {
		
	}
	
	@Test
	public void testTranslateTaskboardTaskToJson() {
		
	}
	
	@Test
	public void testTranslateSprintInfoToJson() {
		
	}
	
	@Test
	public void testTranslateSprintBacklogToJson() {
		
	}
	
	@Test
	public void testTranslateWorkitemToJson() {
		
	}
	
	@Test
	public void testTranslateStatusToJson() {
		
	}
	
	@Test
	public void testTranslateBurndownChartDataToJson() {
		
	}
	
	@Test
	public void testTranslateCPI_SPI_DataToJson() {
		
	}
	
	@Test
	public void testTranslateEV_PV_TAC_DataToJson() {
		
	}
	
	@Test
	public void testJoin() {
		
	}
	
	// 測試是否有將 FilterType 加入 Story 的屬性之一
	@Test
	public void testTranslateStoryToJson2() throws Exception {
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(
				mProject);
		ArrayList<StoryObject> stories = new ArrayList<StoryObject>();

		// initial data
		for (int i = 0; i < 10; i++) {
			StoryInfo storyInfo = new StoryInfo();
			storyInfo.id = mCPB.getStories().get(i).getId();
			storyInfo.name = "0";
			storyInfo.estimate = 0;
			storyInfo.value = 0;
			storyInfo.importance = 0;
			storyInfo.howToDemo = "0";
			storyInfo.sprintId = 0;
			productBacklogHelper.updateStory(storyInfo.id, storyInfo);

			StoryObject story = mCPB.getStories().get(i);
			assertEquals(0, story.getEstimate());
			assertEquals(0, story.getImportance());
			assertEquals(0, story.getValue());
			Thread.sleep(500);
		}

		StringBuilder actualSB = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			stories.add(mCPB.getStories().get(i));
			actualSB = new StringBuilder();
			actualSB.append(new Translation().translateStoryToJson(stories
					.get(i)));
			assertFalse(actualSB.toString().contains("DONE"));
			assertFalse(actualSB.toString().contains("DETAIL"));
			assertTrue(actualSB.toString().contains("BACKLOG"));
		}

		CheckOutIssue coi = new CheckOutIssue(stories, mCP);
		// 將前四筆狀態 done
		coi.exeDone_Issues();

		stories.clear();

		// 驗證 done 狀態
		for (int i = 0; i < 4; i++) {
			StoryObject story = mCPB.getStories().get(i);
			actualSB = new StringBuilder();
			actualSB.append(new Translation().translateStoryToJson(story));
			assertTrue(actualSB.toString().contains("DONE"));
			assertFalse(actualSB.toString().contains("DETAIL"));
			assertFalse(actualSB.toString().contains("BACKLOG"));
		}

		// 驗證 backlog 狀態
		for (int i = 4; i < 9; i++) {
			StoryObject story = mCPB.getStories().get(i);
			actualSB = new StringBuilder();
			actualSB.append(new Translation().translateStoryToJson(story));
			assertFalse(actualSB.toString().contains("DONE"));
			assertFalse(actualSB.toString().contains("DETAIL"));
			assertTrue(actualSB.toString().contains("BACKLOG"));
		}

		// 驗證 detail 狀態
		for (int i = 0; i < 10; i++) {
			StoryObject story = mCPB.getStories().get(i);
			actualSB = new StringBuilder();
			actualSB.append(new Translation().translateStoryToJson(story));
			assertFalse(actualSB.toString().contains("DETAIL"));
		}

		// 將 4 - 5 改成 detail (目前判斷是 value / estimation / importance 這三者皆要有值才算是)
		StoryInfo storyInfo = new StoryInfo();
		storyInfo.id = mCPB.getStories().get(4).getId();
		storyInfo.name = "";
		storyInfo.estimate = 1;
		storyInfo.value = 1;
		storyInfo.importance = 1;
		storyInfo.howToDemo = "";
		storyInfo.sprintId = 0;
		productBacklogHelper.updateStory(storyInfo);
		Thread.sleep(1000);

		storyInfo.id = mCPB.getStories().get(5).getId();
		productBacklogHelper.updateStory(storyInfo);
		Thread.sleep(1000);

		// 驗證 done 狀態
		for (int i = 0; i < 4; i++) {
			StoryObject story = mCPB.getStories().get(i);
			actualSB = new StringBuilder();
			actualSB.append(new Translation().translateStoryToJson(story));
			assertTrue(actualSB.toString().contains("DONE"));
			assertFalse(actualSB.toString().contains("DETAIL"));
			assertFalse(actualSB.toString().contains("BACKLOG"));
		}

		// 驗證 detail 狀態
		for (int i = 4; i < 6; i++) {
			StoryObject story = mCPB.getStories().get(i);
			actualSB = new StringBuilder();
			actualSB.append(new Translation().translateStoryToJson(story));
			assertFalse(actualSB.toString().contains("DONE"));
			assertTrue(actualSB.toString().contains("DETAIL"));
			assertFalse(actualSB.toString().contains("BACKLOG"));
		}

		// 驗證 backlog 狀態
		for (int i = 7; i < 10; i++) {
			StoryObject story = mCPB.getStories().get(i);
			actualSB = new StringBuilder();
			actualSB.append(new Translation().translateStoryToJson(story));
			assertFalse(actualSB.toString().contains("DONE"));
			assertFalse(actualSB.toString().contains("DETAIL"));
			assertTrue(actualSB.toString().contains("BACKLOG"));
		}
	}
}