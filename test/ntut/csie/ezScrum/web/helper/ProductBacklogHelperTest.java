package ntut.csie.ezScrum.web.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddSprintToRelease;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataInfo.StoryInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProductBacklogHelperTest {
	private ProductBacklogHelper mProductBacklogHelper1;
	private ProductBacklogHelper mProductBacklogHelper2;
	private ProductBacklogLogic mProductBacklogLogic1;
	private ProductBacklogLogic mProductBacklogLogic2;
	private ProductBacklogMapper mProductBacklogMapper1;
	private ProductBacklogMapper mProductBacklogMapper2;
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateProductBacklog mCPB;
	private int ProjectCount = 2;
	private Configuration mConfig;

	@Before
	public void setUp() {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增 Project
		mCP = new CreateProject(ProjectCount);
		mCP.exeCreate();

		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		ProjectObject project1 = mCP.getAllProjects().get(0);
		ProjectObject project2 = mCP.getAllProjects().get(1);

		mProductBacklogHelper1 = new ProductBacklogHelper(project1);
		mProductBacklogHelper2 = new ProductBacklogHelper(project2);

		mProductBacklogLogic1 = new ProductBacklogLogic(project1);
		mProductBacklogLogic2 = new ProductBacklogLogic(project2);

		mProductBacklogMapper1 = new ProductBacklogMapper(project1);
		mProductBacklogMapper2 = new ProductBacklogMapper(project2);

		// release
		ini = null;
	}

	@After
	public void tearDown() {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		mConfig.setTestMode(false);
		mConfig.save();

		// release
		ini = null;
		mProductBacklogHelper1 = null;
		mProductBacklogHelper2 = null;
		mProductBacklogLogic1 = null;
		mProductBacklogLogic2 = null;
		mProductBacklogMapper1 = null;
		mProductBacklogMapper2 = null;
		mCP = null;
		mCPB = null;
		mConfig = null;
	}

	@Test
	public void testAddNewTag() {
		// 新增Tag
		mProductBacklogHelper1.addNewTag("Tag");
		mProductBacklogHelper1.addNewTag("Project1_Tag1");

		mProductBacklogHelper2.addNewTag("Tag");
		mProductBacklogHelper2.addNewTag("Project2_Tag1");
		mProductBacklogHelper2.addNewTag("Project2_Tag2");

		// 確認數量
		assertEquals(2, mProductBacklogHelper1.getTagList().size());
		assertEquals(3, mProductBacklogHelper2.getTagList().size());

		// 確認名稱
		assertEquals("Tag", mProductBacklogHelper1.getTagList().get(0)
				.getName());
		assertEquals("Project1_Tag1", mProductBacklogHelper1.getTagList()
				.get(1).getName());

		assertEquals("Tag", mProductBacklogHelper2.getTagList().get(0)
				.getName());
		assertEquals("Project2_Tag1", mProductBacklogHelper2.getTagList()
				.get(1).getName());
		assertEquals("Project2_Tag2", mProductBacklogHelper2.getTagList()
				.get(2).getName());
	}

	@Test
	public void testIsTagExist() {
		// 新增 Tag
		mProductBacklogHelper1.addNewTag("Tag");
		assertTrue(mProductBacklogHelper1.isTagExist("Tag"));
		assertFalse(mProductBacklogHelper1.isTagExist("TagNoExist"));
	}

	@Test
	public void testGetTagByName() {
		// 新增Tag
		mProductBacklogHelper1.addNewTag("Tag");
		mProductBacklogHelper1.addNewTag("Project1_Tag1");

		TagObject tag1 = mProductBacklogHelper1.getTagList().get(0);
		TagObject tag2 = mProductBacklogHelper1.getTagList().get(1);

		// 確認Tag
		assertTrue(mProductBacklogHelper1.isTagExist("Tag"));
		assertTrue(mProductBacklogHelper1.isTagExist("Project1_Tag1"));

		assertEquals(tag1.getName(), mProductBacklogHelper1.getTagByName("Tag")
				.getName());
		assertEquals(tag1.getId(), mProductBacklogHelper1.getTagByName("Tag")
				.getId());

		assertEquals(tag2.getName(),
				mProductBacklogHelper1.getTagByName("Project1_Tag1").getName());
		assertEquals(tag2.getId(),
				mProductBacklogHelper1.getTagByName("Project1_Tag1").getId());
	}

	@Test
	public void testDeleteTag() {
		// 新增 Tag
		mProductBacklogHelper1.addNewTag("Tag");
		mProductBacklogHelper1.addNewTag("Project1_Tag1");

		// 確認存在
		assertTrue(mProductBacklogHelper1.isTagExist("Tag"));
		assertTrue(mProductBacklogHelper1.isTagExist("Project1_Tag1"));

		// 刪除 Tag
		mProductBacklogHelper1.deleteTag(mProductBacklogHelper1.getTagByName(
				"Tag").getId());
		mProductBacklogHelper1.deleteTag(mProductBacklogHelper1.getTagByName(
				"Project1_Tag1").getId());

		// 確認不存在
		assertFalse(mProductBacklogHelper1.isTagExist("Tag"));
		assertFalse(mProductBacklogHelper1.isTagExist("Project1_Tag1"));
	}

	@Test
	public void testGetTagList() {
		// 新增 Tag
		mProductBacklogHelper1.addNewTag("Tag");
		mProductBacklogHelper1.addNewTag("Project1_Tag1");

		// 確認數量
		assertEquals(2, mProductBacklogHelper1.getTagList().size());

		// 確認名稱
		assertEquals("Tag", mProductBacklogHelper1.getTagList().get(0)
				.getName());
		assertEquals("Project1_Tag1", mProductBacklogHelper1.getTagList()
				.get(1).getName());
	}

	@Test
	public void testUpdateTag() {
		// 新增 Tag
		mProductBacklogHelper1.addNewTag("Tag");
		mProductBacklogHelper1.addNewTag("Project1_Tag1");
		mProductBacklogHelper2.addNewTag("Project2_Tag1");

		// 將 Project1_Tag1 修改為新名稱
		long project1Tag1Id = mProductBacklogHelper1.getTagByName(
				"Project1_Tag1").getId();
		mProductBacklogMapper1.updateTag(project1Tag1Id, "Project1_Tag");

		// 判斷原本的 Tag 不存在 修改後的 Tag 存在
		assertNull(mProductBacklogHelper1.getTagByName("Project1_Tag1"));
		assertNotNull(mProductBacklogHelper1.getTagByName("Project1_Tag"));

		// 將 Project2 中的 Project2_Tag1 修改為與 Project1 中的 Tag 名稱一樣
		// 確認兩個名稱相同的 Tag id 一樣
		long project2Tag1Id = mProductBacklogHelper2.getTagByName(
				"Project2_Tag1").getId();
		mProductBacklogMapper2.updateTag(project2Tag1Id, "Tag");

		// 判斷原本的 Tag 不存在 修改後的 Tag 存在
		assertNull(mProductBacklogHelper2.getTagByName("Project2_Tag1"));
		assertNotNull(mProductBacklogHelper2.getTagByName("Tag"));
	}

	@Test
	public void testAddStoryTag() {
		// create 1 story in each project
		mCPB = new CreateProductBacklog(1, mCP);
		mCPB.exe();

		// 新增 Tag
		mProductBacklogHelper1.addNewTag("Tag");
		mProductBacklogHelper1.addNewTag("Project1_Tag1");
		mProductBacklogHelper2.addNewTag("Project2_Tag1");

		// 將 Story 加上 Tag
		long tag1P1Id = mProductBacklogHelper1.getTagByName("Tag").getId();
		long tag2P1Id = mProductBacklogHelper1.getTagByName("Project1_Tag1")
				.getId();
		long tag1P2Id = mProductBacklogHelper2.getTagByName("Project2_Tag1")
				.getId();

		long story1Id = mProductBacklogLogic1.getStories().get(0).getId();
		long story2Id = mProductBacklogLogic2.getStories().get(0).getId();

		mProductBacklogHelper1.addStoryTag(story1Id, tag1P1Id);
		mProductBacklogHelper1.addStoryTag(story1Id, tag2P1Id);
		mProductBacklogHelper2.addStoryTag(story2Id, tag1P2Id);

		// 取得 Story 的 Tag List
		ArrayList<TagObject> story1Tags = mProductBacklogLogic1.getStories()
				.get(0).getTags();
		ArrayList<TagObject> story2Tags = mProductBacklogLogic2.getStories()
				.get(0).getTags();

		// 確認Tag
		assertEquals(2, story1Tags.size());
		assertEquals(1, story2Tags.size());

		assertEquals(mProductBacklogHelper1.getTagByName("Tag").getId(),
				story1Tags.get(0).getId());
		assertEquals(mProductBacklogHelper1.getTagByName("Tag").getName(),
				story1Tags.get(0).getName());
		assertEquals(mProductBacklogHelper1.getTagByName("Project1_Tag1")
				.getId(), story1Tags.get(1).getId());
		assertEquals(mProductBacklogHelper1.getTagByName("Project1_Tag1")
				.getName(), story1Tags.get(1).getName());

		assertEquals(mProductBacklogHelper2.getTagByName("Project2_Tag1")
				.getId(), story2Tags.get(0).getId());
		assertEquals(mProductBacklogHelper2.getTagByName("Project2_Tag1")
				.getName(), story2Tags.get(0).getName());
	}

	@Test
	public void testRemoveStoryTag() {
		// create 1 story in each project
		mCPB = new CreateProductBacklog(1, mCP);
		mCPB.exe();

		// 新增 Tag
		mProductBacklogHelper1.addNewTag("Tag");
		mProductBacklogHelper1.addNewTag("Project1_Tag1");
		mProductBacklogHelper2.addNewTag("Project2_Tag1");

		// 將 Story 加上 Tag
		long tag1P1Id = mProductBacklogHelper1.getTagByName("Tag").getId();
		long tag2P1Id = mProductBacklogHelper1.getTagByName("Project1_Tag1")
				.getId();
		long tag1P2Id = mProductBacklogHelper2.getTagByName("Project2_Tag1")
				.getId();

		long story1Id = mProductBacklogLogic1.getStories().get(0).getId();
		long story2Id = mProductBacklogLogic2.getStories().get(0).getId();

		mProductBacklogHelper1.addStoryTag(story1Id, tag1P1Id);
		mProductBacklogHelper1.addStoryTag(story1Id, tag2P1Id);
		mProductBacklogHelper1.addStoryTag(story2Id, tag1P2Id);

		// 取得 Story 的 Tag List
		ArrayList<TagObject> story1Tags = mProductBacklogLogic1.getStories()
				.get(0).getTags();
		ArrayList<TagObject> story2Tags = mProductBacklogLogic2.getStories()
				.get(0).getTags();

		// 確認 Tag
		assertEquals(2, story1Tags.size());
		assertEquals(1, story2Tags.size());

		assertEquals(mProductBacklogHelper1.getTagByName("Tag").getId(),
				story1Tags.get(0).getId());
		assertEquals(mProductBacklogHelper1.getTagByName("Tag").getName(),
				story1Tags.get(0).getName());
		assertEquals(mProductBacklogHelper1.getTagByName("Project1_Tag1")
				.getId(), story1Tags.get(1).getId());
		assertEquals(mProductBacklogHelper1.getTagByName("Project1_Tag1")
				.getName(), story1Tags.get(1).getName());

		assertEquals(mProductBacklogHelper2.getTagByName("Project2_Tag1")
				.getId(), story2Tags.get(0).getId());
		assertEquals(mProductBacklogHelper2.getTagByName("Project2_Tag1")
				.getName(), story2Tags.get(0).getName());

		// 移除 Story 的 Tag
		mProductBacklogHelper1.removeStoryTag(story1Id, tag2P1Id);
		mProductBacklogHelper1.removeStoryTag(story2Id, tag1P2Id);

		// 取得 Story 的 Tag List
		story1Tags.clear();
		story1Tags = mProductBacklogLogic1.getStories().get(0).getTags();
		story2Tags.clear();
		story2Tags = mProductBacklogLogic2.getStories().get(0).getTags();

		// 確認 Tag
		assertEquals(1, story1Tags.size());
		assertEquals(0, story2Tags.size());

		assertEquals(mProductBacklogHelper1.getTagByName("Tag").getId(),
				story1Tags.get(0).getId());
		assertEquals(mProductBacklogHelper1.getTagByName("Tag").getName(),
				story1Tags.get(0).getName());
	}

	// 兩個專案各有相同名稱之 Tag，並將 Story 都標記此 Tag
	// 在 Project1 修改 Tag 名稱後，確認 Proejct1 的 Story Tag 名稱變更
	// 並確認 Project2 的 Tag 存在且與 Story 的 Tag 不會被變更
	@Test
	public void testTagScenario1() {
		// create 1 story in each project
		mCPB = new CreateProductBacklog(1, mCP);
		mCPB.exe();

		mProductBacklogHelper1.addNewTag("Tag");
		mProductBacklogHelper2.addNewTag("Tag");

		// 將 Story 加上 Tag
		long tagId = mProductBacklogHelper1.getTagByName("Tag").getId();
		long story1Id = mProductBacklogLogic1.getStories().get(0).getId();
		long story2Id = mProductBacklogLogic2.getStories().get(0).getId();

		mProductBacklogHelper1.addStoryTag(story1Id, tagId);
		mProductBacklogHelper2.addStoryTag(story2Id, tagId);

		// 修改 Project1 的 Tag
		mProductBacklogMapper1.updateTag(tagId, "ModifyTag");

		// 確認 Proejct1 的 Story Tag 名稱變更
		TagObject modifyTag = mProductBacklogHelper1.getTagByName("ModifyTag");
		TagObject story1Tag = mProductBacklogLogic1.getStories().get(0)
				.getTags().get(0);

		assertFalse(mProductBacklogHelper1.isTagExist("Tag"));
		assertTrue(mProductBacklogHelper1.isTagExist("ModifyTag"));

		assertEquals(modifyTag.getId(), story1Tag.getId());
		assertEquals(modifyTag.getName(), story1Tag.getName());

		// 確認 Project2 的 Tag 存在且與 Story 的 Tag 不會被變更
		TagObject story2Tag = mProductBacklogLogic2.getStories().get(0)
				.getTags().get(0);

		assertTrue(mProductBacklogHelper2.isTagExist("Tag"));

		assertEquals(story2Tag.getId(), story2Tag.getId());
		assertEquals(story2Tag.getName(), story2Tag.getName());
	}

	// 兩個專案各有相同名稱之 Tag，並將 Story 都標記此 Tag
	// 在 Project1 刪除 Tag 名稱後，確認 Proejct1 的 Story Tag 被移除
	// 並確認 Project2 的 Tag 存在且與 Story 的 Tag 不會被變更
	@Test
	public void testTagScenario2() {
		// create 1 story in each project
		mCPB = new CreateProductBacklog(1, mCP);
		mCPB.exe();

		mProductBacklogHelper1.addNewTag("Tag");
		mProductBacklogHelper2.addNewTag("Tag");

		// 將 Story 加上 Tag
		long tagId1 = mProductBacklogHelper1.getTagByName("Tag").getId();
		long tagId2 = mProductBacklogHelper2.getTagByName("Tag").getId();

		StoryObject story1 = mProductBacklogLogic1.getStories().get(0);
		StoryObject story2 = mProductBacklogLogic2.getStories().get(0);

		long story1Id = story1.getId();
		long story2Id = story2.getId();

		mProductBacklogHelper1.addStoryTag(story1Id, tagId1);
		mProductBacklogHelper2.addStoryTag(story2Id, tagId2);

		// 刪除 Project1 的 Tag
		mProductBacklogHelper1.deleteTag(tagId1);

		// 確認 Proejct1 的 Tag 被移除 Project2 的存在
		assertFalse(mProductBacklogHelper1.isTagExist("Tag"));
		assertTrue(mProductBacklogHelper2.isTagExist("Tag"));

		// 確認 Project1 的 Story's Tag 被移除
		ArrayList<TagObject> story1Tags = mProductBacklogLogic1.getStories()
				.get(0).getTags();
		assertEquals(0, story1Tags.size());

		// 確認 Project2 的 Story's Tag 不會被變更
		ArrayList<TagObject> story2Tags = mProductBacklogLogic2.getStories()
				.get(0).getTags();
		TagObject tag = mProductBacklogHelper2.getTagByName("Tag");

		assertEquals(1, story2Tags.size());
		assertEquals(tag.getId(), story2Tags.get(0).getId());
		assertEquals(tag.getName(), story2Tags.get(0).getName());
	}

	// 驗證 Story 狀態為 Done 時，不顯示
	@Test
	public void testGetUnclosedStories1() {
		mCPB = new CreateProductBacklog(10, mCP);
		mCPB.exe(); // 新增十筆 Story

		CreateSprint CS = new CreateSprint(1, mCP);
		CS.exe(); // 新增一筆 Sprint

		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(mCP.getAllProjects().get(0), -1);

		// 將第一筆 Story Done
		//String changeDate = "2015/03/30-11:35:27";
		sprintBacklogLogic.getSprintBacklogMapper().closeStory(
				mCPB.getStories().get(0).getId(), "TEST_STORY_NOTE_" + "1",
				mCPB.getStories().get(0).getNotes(), new Date());

		ArrayList<StoryObject> unclosedStories = mProductBacklogLogic1
				.getUnclosedStories();
		assertEquals(9, unclosedStories.size());

		// 從 ID 第二筆開始驗證
		for (int i = 0; i < unclosedStories.size(); i++) {
			assertEquals((i + 2), unclosedStories.get(i).getId());
			assertEquals("TEST_STORY_DEMO_" + Long.toString(i + 2),
					unclosedStories.get(i).getHowToDemo());
			assertEquals("TEST_STORY_" + Long.toString(i + 2), unclosedStories
					.get(i).getName());
			assertEquals("TEST_STORY_NOTE_" + Long.toString(i + 2),
					unclosedStories.get(i).getNotes());
		}

		// 將第十筆 Story Done
		//changeDate = "2015/03/30-11:45:27";
		sprintBacklogLogic.getSprintBacklogMapper().closeStory(
				mCPB.getStories().get(9).getId(), "TEST_STORY_NOTE_" + "10",
				mCPB.getStories().get(9).getNotes(), new Date());

		unclosedStories = mProductBacklogLogic1.getUnclosedStories();
		assertEquals(8, unclosedStories.size());

		// 從 ID 第二筆開始驗證到第九筆
		for (int i = 0; i < unclosedStories.size(); i++) {
			assertEquals((i + 2), unclosedStories.get(i).getId());
			assertEquals("TEST_STORY_DEMO_" + Long.toString(i + 2),
					unclosedStories.get(i).getHowToDemo());
			assertEquals("TEST_STORY_" + Long.toString(i + 2), unclosedStories
					.get(i).getName());
			assertEquals("TEST_STORY_NOTE_" + Long.toString(i + 2),
					unclosedStories.get(i).getNotes());
		}
	}

	// 驗證 Story 存在於 Sprint 內，被查詢時不能出現
	@Test
	public void testGetAddableStories1() throws Exception {
		mCPB = new CreateProductBacklog(10, mCP);
		mCPB.exe(); // 新增十筆 Story

		CreateSprint CS = new CreateSprint(1, mCP);
		CS.exe(); // 新增一筆 SprintPlan

		// 將一筆 Story 加入 Sprint
		ArrayList<Long> storyIds = new ArrayList<Long>();
		storyIds.add(mCPB.getStories().get(0).getId());
		mProductBacklogLogic1.addStoriesToSprint(storyIds, 1);

		ArrayList<StoryObject> addableStories = mProductBacklogLogic1
				.getAddableStories();
		assertEquals(9, addableStories.size());

		// 從 ID 第二筆開始驗證
		for (int i = 0; i < addableStories.size(); i++) {
			assertEquals((i + 2), addableStories.get(i).getId());
			assertEquals("TEST_STORY_DEMO_" + Long.toString(i + 2),
					addableStories.get(i).getHowToDemo());
			assertEquals("TEST_STORY_" + Long.toString(i + 2), addableStories
					.get(i).getName());
			assertEquals("TEST_STORY_NOTE_" + Long.toString(i + 2),
					addableStories.get(i).getNotes());
		}

		// 再將三筆 Story 加入 Sprint 1 內
		storyIds.add(mCPB.getStories().get(1).getId());
		storyIds.add(mCPB.getStories().get(2).getId());
		storyIds.add(mCPB.getStories().get(3).getId());
		mProductBacklogLogic1.addStoriesToSprint(storyIds, 1);

		addableStories = mProductBacklogLogic1.getAddableStories();
		assertEquals(6, addableStories.size());

		// 從 ID 第二筆開始驗證
		for (int i = 0; i < addableStories.size(); i++) {
			assertEquals((i + 5), addableStories.get(i).getId());
			assertEquals("TEST_STORY_DEMO_" + Long.toString(i + 5),
					addableStories.get(i).getHowToDemo());
			assertEquals("TEST_STORY_" + Long.toString(i + 5), addableStories
					.get(i).getName());
			assertEquals("TEST_STORY_NOTE_" + Long.toString(i + 5),
					addableStories.get(i).getNotes());
		}
	}

	// 驗證 Story 存在於 Sprint 而 Sprint 存在於 Release 內，被查詢時不能出現
	@Test
	public void testGetAddableStories2() throws Exception {
		mCPB = new CreateProductBacklog(10, mCP);
		mCPB.exe(); // 新增十筆 Story

		CreateRelease CR = new CreateRelease(2, mCP);
		CR.exe(); // 新增一筆 ReleasePlan

		AddSprintToRelease ASTR = new AddSprintToRelease(2, CR, mCP);
		ASTR.exe(); // 將兩筆 Sprint 加入 Release 內

		// 將五筆 Story 加入 Sprint 1
		ArrayList<Long> storyIds = new ArrayList<Long>();
		storyIds.add(mCPB.getStories().get(0).getId());
		storyIds.add(mCPB.getStories().get(1).getId());
		storyIds.add(mCPB.getStories().get(2).getId());
		storyIds.add(mCPB.getStories().get(3).getId());
		storyIds.add(mCPB.getStories().get(4).getId());
		Thread.sleep(1000); // 速度太快，暫停一下，避免影響資料存的時間一樣
		mProductBacklogLogic1.addStoriesToSprint(storyIds, 1);

		// 驗證顯示剩下的 6-10 筆 StoryID
		ArrayList<StoryObject> addableStories = mProductBacklogLogic1
				.getAddableStories();
		assertEquals(5, addableStories.size());

		// 從 ID 第六筆開始驗證
		for (int i = 0; i < addableStories.size(); i++) {
			assertEquals((i + 6), addableStories.get(i).getId());
			assertEquals("TEST_STORY_DEMO_" + Long.toString(i + 6),
					addableStories.get(i).getHowToDemo());
			assertEquals("TEST_STORY_" + Long.toString(i + 6), addableStories
					.get(i).getName());
			assertEquals("TEST_STORY_NOTE_" + Long.toString(i + 6),
					addableStories.get(i).getNotes());
		}

		// 將三筆 Story (ID = 6, 7, 8) 加入 Sprint 2
		storyIds.clear();
		storyIds.add(mCPB.getStories().get(5).getId());
		storyIds.add(mCPB.getStories().get(6).getId());
		storyIds.add(mCPB.getStories().get(7).getId());
		Thread.sleep(1000); // 速度太快，暫停一下，避免影響資料存的時間一樣
		mProductBacklogLogic1.addStoriesToSprint(storyIds, 2);

		// 驗證顯示剩下的 9, 10 筆 StoryID
		addableStories = mProductBacklogLogic1.getAddableStories();
		assertEquals(2, addableStories.size());

		// 從 ID 第九筆開始驗證
		for (int i = 0; i < addableStories.size(); i++) {
			assertEquals((i + 9), addableStories.get(i).getId());
			assertEquals("TEST_STORY_DEMO_" + Long.toString(i + 9),
					addableStories.get(i).getHowToDemo());
			assertEquals("TEST_STORY_" + Long.toString(i + 9), addableStories
					.get(i).getName());
			assertEquals("TEST_STORY_NOTE_" + Long.toString(i + 9),
					addableStories.get(i).getNotes());
		}

		// 將兩筆 Story (ID = 9, 10) 加入 Sprint 3 (Sprint 3 不存在)
		storyIds.clear();
		storyIds.add((long) 9);
		storyIds.add((long) 10);
		Thread.sleep(1000); // 速度太快，暫停一下，避免影響資料存的時間一樣
		mProductBacklogLogic1.addStoriesToSprint(storyIds, 3);

		// 驗證顯示 0 筆資料
		addableStories = mProductBacklogLogic1.getAddableStories();
		assertEquals(0, addableStories.size());

		// 將兩筆 Story (ID = 7, 8) 移除 Sprint 2
		storyIds.clear();
		storyIds.add((long) 7);
		storyIds.add((long) 8);
		Thread.sleep(1000); // 速度太快，暫停一下，避免影響資料存的時間一樣
		mProductBacklogLogic1.dropStoryFromSprint(7);
		mProductBacklogLogic1.dropStoryFromSprint(8);

		// 驗證顯示 2 筆資料
		addableStories = mProductBacklogLogic1.getAddableStories();
		assertEquals(2, addableStories.size());

		// 從 ID 第七筆開始驗證
		for (int i = 0; i < addableStories.size(); i++) {
			assertEquals((i + 7), addableStories.get(i).getId());
			assertEquals("TEST_STORY_DEMO_" + Long.toString(i + 7),
					addableStories.get(i).getHowToDemo());
			assertEquals("TEST_STORY_" + Long.toString(i + 7), addableStories
					.get(i).getName());
			assertEquals("TEST_STORY_NOTE_" + Long.toString(i + 7),
					addableStories.get(i).getNotes());
		}
	}

	@Test
	public void testAddAttachFile() {
		// create 1 story
		mCPB = new CreateProductBacklog(1, mCP);
		mCPB.exe();

		AttachFileInfo attachFileInfo = new AttachFileInfo();
		attachFileInfo.name = "initial_bk.sql";
		attachFileInfo.issueId = mCPB.getStories().get(0).getId();
		attachFileInfo.issueType = IssueTypeEnum.TYPE_STORY;
		attachFileInfo.projectName = mCP.getProjectList().get(0).getName();

		File sqlFile = new File(mConfig.getInitialSQLPath());

		try {
			long id = mProductBacklogHelper1.addAttachFile(attachFileInfo,
					sqlFile);
			AttachFileObject attachFile = mProductBacklogHelper1
					.getAttachFile(id);
			File actualFile = new File(attachFile.getPath());
			assertEquals(sqlFile.length(), actualFile.length());
			assertEquals(attachFileInfo.name, attachFile.getName());
			assertEquals(attachFileInfo.issueType, attachFile.getIssueType());
		} catch (IOException e) {
			System.out.println(e);
			assertTrue(false);
		}
	}

	@Test
	public void testDeleteAttachFile() {
		// create 1 story
		mCPB = new CreateProductBacklog(1, mCP);
		mCPB.exe();

		AttachFileInfo attachFileInfo = new AttachFileInfo();
		attachFileInfo.name = "initial_bk.sql";
		attachFileInfo.issueId = mCPB.getStories().get(0).getId();
		attachFileInfo.issueType = IssueTypeEnum.TYPE_STORY;
		attachFileInfo.projectName = mCP.getProjectList().get(0).getName();

		File sqlFile = new File(mConfig.getInitialSQLPath());

		try {
			long id = mProductBacklogHelper1.addAttachFile(attachFileInfo,
					sqlFile);
			AttachFileObject attachFile = mProductBacklogHelper1
					.getAttachFile(id);
			File actualFile = new File(attachFile.getPath());
			assertEquals(sqlFile.length(), actualFile.length());
			assertEquals(attachFileInfo.name, attachFile.getName());
			assertEquals(attachFileInfo.issueType, attachFile.getIssueType());

			mProductBacklogHelper1.deleteAttachFile(attachFile.getId());

			try {
				mProductBacklogHelper1.getAttachFile(id);
				assertTrue(false);
			} catch (Exception e) {
				assertTrue(true);

				File deletedFile = new File(attachFile.getPath());
				assertEquals(false, deletedFile.exists());
			}
		} catch (IOException e) {
			System.out.println(e);
			assertTrue(false);
		}
	}

	@Test
	public void testEditStoryHistory() {
		// create 1 story
		mCPB = new CreateProductBacklog(1, mCP);
		mCPB.exe();

		StoryInfo storyInfo = new StoryInfo();
		storyInfo.id = mCPB.getStoryIds().get(0);
		storyInfo.name = "快接 task 啦";
		storyInfo.value = 6;
		storyInfo.importance = 6;
		storyInfo.estimate = 6;
		storyInfo.howToDemo = "QAQ";
		storyInfo.notes = "已哭";

		StoryObject story = mProductBacklogHelper1.updateStory(storyInfo.id, storyInfo);
		// assert issue info
		assertEquals(storyInfo.id, story.getId());
		assertEquals(storyInfo.name, story.getName());
		assertEquals(storyInfo.value, story.getValue());
		assertEquals(storyInfo.importance, story.getImportance());
		assertEquals(storyInfo.estimate, story.getEstimate());
		assertEquals(storyInfo.howToDemo, story.getHowToDemo());
		assertEquals(storyInfo.notes, story.getNotes());
		// assert issue history
		ArrayList<HistoryObject> histories = story.getHistories();
		assertEquals(7, histories.size());
	}

	@Test
	public void testDeleteStory() {
		long projectId = mCP.getAllProjects().get(0).getId();
		for (int i = 0; i < 3; i++) {
			StoryObject story = new StoryObject(projectId);
			story.setName("Story_" + i).setNotes("Story_" + i);
			story.save();
		}

		// 在刪除前 project1 應要有三筆 story
		ArrayList<StoryObject> stories = ProjectObject.get(projectId)
				.getStories();
		assertEquals(3, stories.size());

		// 刪除第三筆 story
		long deletedStoryId = stories.get(2).getId();
		StringBuilder response = mProductBacklogHelper1
				.deleteStory(deletedStoryId);
		String expectedResponse = "{\"success\":true, \"Total\":1, \"Stories\":[{\"Id\":"
				+ deletedStoryId + "}]}";
		stories.clear();
		stories = ProjectObject.get(projectId).getStories();

		assertEquals(2, stories.size());
		assertEquals(response.toString(), expectedResponse);
	}

	@Test
	public void testGetSprintHashMap() {
		// create 1 story
		mCPB = new CreateProductBacklog(1, mCP);
		mCPB.exe();

		ArrayList<StoryObject> stories = mCPB.getStories();
		for (StoryObject story : stories) {
			story.setSprintId(1).save();
		}

		Map<Long, ArrayList<StoryObject>> sprintMap = mProductBacklogHelper1
				.getSprintHashMap();
		assertEquals(1, sprintMap.get(1L).size());
		assertEquals(mCPB.getStoryIds().get(0), sprintMap.get(1L).get(0)
				.getId());
	}

	@Test
	public void testMoveStory() {
		// create 1 story
		mCPB = new CreateProductBacklog(1, mCP);
		mCPB.exe();

		StoryObject story = mCPB.getStories().get(0);
		assertEquals(-1, story.getSprintId());

		long sprintId = mCS.getSprintsId().get(0);
		mProductBacklogHelper1.moveStory(story.getId(), sprintId);

		story.reload();
		assertEquals(sprintId, story.getSprintId());
	}

	@Test
	public void testCheckAccountInProject() {
		CreateAccount CA = new CreateAccount(1);
		CA.exe();

		AccountObject account = CA.getAccountList().get(0);
		account.createProjectRole(mCP.getAllProjects().get(0).getId(),
				RoleEnum.ScrumTeam);

		boolean inProject = mProductBacklogHelper1.checkAccountInProject(
				new ArrayList<AccountObject>(), account);
		assertEquals(false, inProject);

		ArrayList<AccountObject> members = new ArrayList<AccountObject>();
		members.add(account);

		inProject = mProductBacklogHelper1.checkAccountInProject(members,
				account);
		assertEquals(true, inProject);
	}

	@Test
	public void testGetTagListResponseText() {
		// 新增Tag
		mProductBacklogHelper1.addNewTag("Tag");
		mProductBacklogHelper1.addNewTag("Project1_Tag1");

		// 確認數量
		assertEquals(2, mProductBacklogHelper1.getTagList().size());

		// 確認名稱
		assertEquals("Tag", mProductBacklogHelper1.getTagList().get(0)
				.getName());
		assertEquals("Project1_Tag1", mProductBacklogHelper1.getTagList()
				.get(1).getName());

		String response = mProductBacklogHelper1.getTagListResponseText()
				.toString();
		String expectString = "<IssueTag><Id>%s</Id><Name>%s</Name></IssueTag>";
		assertEquals(true,
				response.contains(String.format(expectString, "1", "Tag")));
		assertEquals(true, response.contains(String.format(expectString, "2",
				"Project1_Tag1")));
	}

	@Test
	public void testGetAddNewTagResponsetext() {
		// Test Tag Name
		String TEST_TAG_NAME = "TEST_TAG_NAME";

		// assemble test Tag Response text
		StringBuilder expectResponse = new StringBuilder();
		expectResponse.append("<Tags><Result>true</Result>");
		expectResponse.append("<IssueTag>");
		expectResponse.append("<Id>" + 1 + "</Id>");
		expectResponse.append("<Name>"
				+ new TranslateSpecialChar().TranslateXMLChar(TEST_TAG_NAME)
				+ "</Name>");
		expectResponse.append("</IssueTag>");
		expectResponse.append("</Tags>");

		// call GetAddNewTagResponsetext
		StringBuilder actualResponse = mProductBacklogHelper1
				.getAddNewTagResponsetext(TEST_TAG_NAME);

		// assert
		assertEquals(expectResponse.toString(), actualResponse.toString());
	}

	@Test
	public void testGetAddNewTagResponsetext_TagExist() {
		// Test Tag Name
		String TEST_TAG_NAME = "TEST_TAG_NAME";
		// Create Tag
		TagObject newTag = new TagObject(TEST_TAG_NAME, mCP.getAllProjects()
				.get(0).getId());
		newTag.save();

		// assemble test Tag Response text
		StringBuilder expectResponse = new StringBuilder();
		expectResponse.append("<Tags><Result>false</Result>");
		expectResponse.append("<Message>Tag Name : " + TEST_TAG_NAME
				+ " already exist</Message>");
		expectResponse.append("</Tags>");

		// call GetAddNewTagResponsetext
		StringBuilder actualResponse = mProductBacklogHelper1
				.getAddNewTagResponsetext(TEST_TAG_NAME);

		// assert
		assertEquals(expectResponse.toString(), actualResponse.toString());
	}

	@Test
	public void testGetAddNewTagResponsetext_ExistComma() {
		// Test Tag Name
		String TEST_TAG_NAME = "TEST_TAG_NAME,";
		// Create Tag
		TagObject newTag = new TagObject(TEST_TAG_NAME, mCP.getAllProjects()
				.get(0).getId());
		newTag.save();

		// assemble test Tag Response text
		StringBuilder expectResponse = new StringBuilder();
		expectResponse.append("<Tags><Result>false</Result>");
		expectResponse
				.append("<Message>TagName: \",\" is not allowed</Message>");
		expectResponse.append("</Tags>");

		// call GetAddNewTagResponsetext
		StringBuilder actualResponse = mProductBacklogHelper1
				.getAddNewTagResponsetext(TEST_TAG_NAME);

		// assert
		assertEquals(expectResponse.toString(), actualResponse.toString());
	}

	@Test
	public void testGetDeleteTagReponseText() {
		// Test Tag Name
		String TEST_TAG_NAME = "TEST_TAG_NAME";
		// Create Tag
		TagObject newTag = new TagObject(TEST_TAG_NAME, mCP.getAllProjects()
				.get(0).getId());
		newTag.save();

		// assemble test Tag Response text
		StringBuilder expectResponse = new StringBuilder();
		expectResponse.append("<TagList><Result>success</Result>");
		expectResponse.append("<IssueTag>");
		expectResponse.append("<Id>" + newTag.getId() + "</Id>");
		expectResponse.append("</IssueTag>");
		expectResponse.append("</TagList>");

		// call GetAddNewTagResponsetext
		StringBuilder actualResponse = mProductBacklogHelper1
				.getDeleteTagReponseText(newTag.getId());

		// assert
		assertEquals(expectResponse.toString(), actualResponse.toString());
	}

	@Test
	public void testGetAddStoryTagResponseText() throws JSONException {
		// create 1 story
		mCPB = new CreateProductBacklog(1, mCP);
		mCPB.exe();

		// Test Tag Name
		String TEST_TAG_NAME = "TEST_TAG_NAME";
		// Create Tag
		TagObject newTag = new TagObject(TEST_TAG_NAME, mCP.getAllProjects().get(0).getId());
		newTag.save();
		// Get Story
		StoryObject story = mCPB.getStories().get(0);

		// assemble test json data
		JSONObject expectResponse = new JSONObject();
		expectResponse.put("success", true);
		expectResponse.put("Total", 1);

		JSONArray jsonStroies = new JSONArray();

		JSONObject jsonStory = new JSONObject();
		jsonStory.put("Id", story.getId());
		jsonStory.put("Type", "Story");
		jsonStory.put("Name", story.getName());
		jsonStory.put("Value", story.getValue());
		jsonStory.put("Estimate", story.getEstimate());
		jsonStory.put("Importance", story.getImportance());
		jsonStory.put("Tag", TEST_TAG_NAME);
		jsonStory.put("Status", story.getStatusString());
		jsonStory.put("Notes", story.getNotes());
		jsonStory.put("HowToDemo", story.getHowToDemo());
		jsonStory.put("Link", "");
		jsonStory.put("Release", "");
		jsonStory.put("Sprint", story.getSprintId() == StoryObject.NO_PARENT ? "None" : story.getSprintId());
		jsonStory.put("FilterType", "DETAIL");
		jsonStory.put("Attach", false);

		JSONArray jsonFiles = new JSONArray();
		jsonStory.put("AttachFileList", jsonFiles);
		jsonStroies.put(jsonStory);

		expectResponse.put("Stories", jsonStroies);

		// getAddStoryTagResponseText
		StringBuilder actualResponse = mProductBacklogHelper1.getAddStoryTagResponseText(story.getId(), newTag.getId());
		// assert
		assertEquals(expectResponse.toString(), actualResponse.toString());
	}

	@Test
	public void testGetRemoveStoryTagResponseText() throws JSONException {
		// create 1 story
		mCPB = new CreateProductBacklog(1, mCP);
		mCPB.exe();
		// Test Tag Name
		String TEST_TAG_NAME = "TEST_TAG_NAME";
		// Create Tag
		TagObject newTag = new TagObject(TEST_TAG_NAME, mCP.getAllProjects()
				.get(0).getId());
		newTag.save();
		// Get Story
		StoryObject story = mCPB.getStories().get(0);

		// assemble test json data
		JSONObject expectResponse = new JSONObject();
		expectResponse.put("success", true);
		expectResponse.put("Total", 1);

		JSONArray jsonStroies = new JSONArray();

		JSONObject jsonStory = new JSONObject();
		jsonStory.put("Id", story.getId());
		jsonStory.put("Type", "Story");
		jsonStory.put("Name", story.getName());
		jsonStory.put("Value", story.getValue());
		jsonStory.put("Estimate", story.getEstimate());
		jsonStory.put("Importance", story.getImportance());
		jsonStory.put("Tag", "");
		jsonStory.put("Status", story.getStatusString());
		jsonStory.put("Notes", story.getNotes());
		jsonStory.put("HowToDemo", story.getHowToDemo());
		jsonStory.put("Link", "");
		jsonStory.put("Release", "");
		jsonStory.put("Sprint", story.getSprintId() == StoryObject.NO_PARENT ? "None" : story.getSprintId());
		jsonStory.put("FilterType", "DETAIL");
		jsonStory.put("Attach", false);

		JSONArray jsonFiles = new JSONArray();
		jsonStory.put("AttachFileList", jsonFiles);
		jsonStroies.put(jsonStory);

		expectResponse.put("Stories", jsonStroies);

		// getAddStoryTagResponseText
		StringBuilder actualResponse = mProductBacklogHelper1.getRemoveStoryTagResponseText(story.getId(), newTag.getId());
		// assert
		assertEquals(expectResponse.toString(), actualResponse.toString());
	}

	@Test
	public void testGetShowProductBacklogResponseText() throws JSONException {
		// create 1 story
		mCPB = new CreateProductBacklog(1, mCP);
		mCPB.exe();
		// get Story
		StoryObject story = mCPB.getStories().get(0);
		// Test Tag Name
		String TEST_TAG_NAME = "TEST_TAG_NAME";
		// Test filter type
		String TEST_FILETER_TYPE = "DETAIL";
		// Create Tag
		TagObject newTag = new TagObject(TEST_TAG_NAME, mCP.getAllProjects().get(0).getId());
		newTag.save();

		// assemble test json data
		JSONObject expectResponse = new JSONObject();
		expectResponse.put("success", true);
		expectResponse.put("Total", 1);

		JSONArray jsonStroies = new JSONArray();

		JSONObject jsonStory = new JSONObject();
		jsonStory.put("Id", story.getId());
		jsonStory.put("Type", "Story");
		jsonStory.put("Name", story.getName());
		jsonStory.put("Value", story.getValue());
		jsonStory.put("Estimate", story.getEstimate());
		jsonStory.put("Importance", story.getImportance());
		jsonStory.put("Tag", "");
		jsonStory.put("Status", story.getStatusString());
		jsonStory.put("Notes", story.getNotes());
		jsonStory.put("HowToDemo", story.getHowToDemo());
		jsonStory.put("Link", "");
		jsonStory.put("Release", "");
		jsonStory.put("Sprint", story.getSprintId() == StoryObject.NO_PARENT ? "None" : story.getSprintId());
		jsonStory.put("FilterType", TEST_FILETER_TYPE);
		jsonStory.put("Attach", false);

		JSONArray jsonFiles = new JSONArray();
		jsonStory.put("AttachFileList", jsonFiles);
		jsonStroies.put(jsonStory);

		expectResponse.put("Stories", jsonStroies);

		// getAddStoryTagResponseText
		StringBuilder actualResponse = mProductBacklogHelper1.getShowProductBacklogResponseText("DETAIL");
		// assert
		assertEquals(expectResponse.toString(), actualResponse.toString());
	}
}