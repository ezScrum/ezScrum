package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.ArrayList;
import ntut.csie.ezScrum.dao.AttachFileDAO;
import ntut.csie.ezScrum.dao.StoryDAO;
import ntut.csie.ezScrum.dao.TagDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author samhuang 2015/03/12
 * 
 */
public class StoryObjectTest {
	private Configuration mConfig = null;
	private CreateProject mCP = null;
	private final static int mPROJECT_COUNT = 1;
	private long mProjectId = -1;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCP = new CreateProject(mPROJECT_COUNT);
		mCP.exeCreate();

		mProjectId = mCP.getAllProjects().get(0).getId();
	}

	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		mConfig = null;
		mCP = null;
	}

	@Test
	public void testSave_Create_New_Story() {
		StoryObject story = createStory();

		story = StoryDAO.getInstance().get(story.getId());
		assertEquals(1, story.getId());
		assertEquals("TEST_NAME", story.getName());
		assertEquals("TEST_NOTE", story.getNotes());
		assertEquals("TEST_HOW_TO_DEMO", story.getHowToDemo());
		assertEquals(1, story.getImportance());
		assertEquals(2, story.getValue());
		assertEquals(3, story.getEstimate());
		assertEquals(StoryObject.STATUS_DONE, story.getStatus());
		assertEquals(1, story.getSprintId());
	}

	@Test
	public void testSave_Update_Story() {
		StoryObject story = createStory();

		story.setName("TEST_NAME2").setNotes("TEST_NOTE2")
				.setHowToDemo("TEST_HOW_TO_DEMO2").setImportance(2).setValue(3)
				.setEstimate(4).setStatus(StoryObject.STATUS_UNCHECK)
				.setSprintId(2).save();

		story = StoryObject.get(story.getId());

		assertEquals(1, story.getId());
		assertEquals("TEST_NAME2", story.getName());
		assertEquals("TEST_NOTE2", story.getNotes());
		assertEquals("TEST_HOW_TO_DEMO2", story.getHowToDemo());
		assertEquals(2, story.getImportance());
		assertEquals(3, story.getValue());
		assertEquals(4, story.getEstimate());
		assertEquals(StoryObject.STATUS_UNCHECK, story.getStatus());
		assertEquals(2, story.getSprintId());
	}

	@Test
	public void testGetHistories() {
		StoryObject story = createStory();

		story.setName("TEST_NAME2").setNotes("TEST_NOTE2")
				.setHowToDemo("TEST_HOW_TO_DEMO2").setImportance(2).setValue(3)
				.setEstimate(4).setStatus(StoryObject.STATUS_UNCHECK)
				.setSprintId(2).save();

		assertEquals(1, story.getId());
		assertEquals("TEST_NAME2", story.getName());
		assertEquals("TEST_NOTE2", story.getNotes());
		assertEquals("TEST_HOW_TO_DEMO2", story.getHowToDemo());
		assertEquals(2, story.getImportance());
		assertEquals(3, story.getValue());
		assertEquals(4, story.getEstimate());
		assertEquals(StoryObject.STATUS_UNCHECK, story.getStatus());
		assertEquals(2, story.getSprintId());

		ArrayList<HistoryObject> histories = story.getHistories();
		assertEquals(11, histories.size());

		HistoryObject history = histories.get(0);
		assertEquals(HistoryObject.TYPE_CREATE, history.getHistoryType());
		assertEquals("", history.getNewValue());

		history = histories.get(1);
		assertEquals(HistoryObject.TYPE_APPEND, history.getHistoryType());
		assertEquals("1", history.getNewValue());

		history = histories.get(2);
		assertEquals(HistoryObject.TYPE_NAME, history.getHistoryType());
		assertEquals("TEST_NAME", history.getOldValue());
		assertEquals("TEST_NAME2", history.getNewValue());

		history = histories.get(3);
		assertEquals(HistoryObject.TYPE_NOTE, history.getHistoryType());
		assertEquals("TEST_NOTE", history.getOldValue());
		assertEquals("TEST_NOTE2", history.getNewValue());

		history = histories.get(4);
		assertEquals(HistoryObject.TYPE_HOWTODEMO, history.getHistoryType());
		assertEquals("TEST_HOW_TO_DEMO", history.getOldValue());
		assertEquals("TEST_HOW_TO_DEMO2", history.getNewValue());

		history = histories.get(5);
		assertEquals(HistoryObject.TYPE_IMPORTANCE, history.getHistoryType());
		assertEquals("1", history.getOldValue());
		assertEquals("2", history.getNewValue());

		history = histories.get(6);
		assertEquals(HistoryObject.TYPE_VALUE, history.getHistoryType());
		assertEquals("2", history.getOldValue());
		assertEquals("3", history.getNewValue());

		history = histories.get(7);
		assertEquals(HistoryObject.TYPE_ESTIMATE, history.getHistoryType());
		assertEquals("3", history.getOldValue());
		assertEquals("4", history.getNewValue());

		history = histories.get(8);
		assertEquals(HistoryObject.TYPE_STATUS, history.getHistoryType());
		assertEquals("1", history.getOldValue());
		assertEquals("0", history.getNewValue());

		history = histories.get(9);
		assertEquals(HistoryObject.TYPE_REMOVE, history.getHistoryType());
		assertEquals("1", history.getNewValue());

		history = histories.get(10);
		assertEquals(HistoryObject.TYPE_APPEND, history.getHistoryType());
		assertEquals("2", history.getNewValue());
	}

	@Test
	public void testGetTasks() {
		StoryObject story = createStory();

		assertEquals(0, story.getTasks().size());

		TaskObject task1 = new TaskObject(mProjectId);
		task1.setName("TASK_NAME_1").setNotes("TASK_NOTE_1").setEstimate(2)
				.setStoryId(story.getId());
		task1.save();

		TaskObject task2 = new TaskObject(mProjectId);
		task2.setName("TASK_NAME_2").setNotes("TASK_NOTE_2").setEstimate(3)
				.setStoryId(story.getId());
		task2.save();

		ArrayList<TaskObject> tasks = story.getTasks();
		assertEquals(2, story.getTasks().size());

		TaskObject task = tasks.get(0);
		assertEquals("TASK_NAME_1", task.getName());
		assertEquals("TASK_NOTE_1", task.getNotes());
		assertEquals(2, task.getEstimate());

		task = tasks.get(1);
		assertEquals("TASK_NAME_2", task.getName());
		assertEquals("TASK_NOTE_2", task.getNotes());
		assertEquals(3, task.getEstimate());
	}
	
	@Test
	public void testAddTag() {
		StoryObject story = createStory();
		
		TagObject tag1 = new TagObject("TAG_1", mProjectId);
		tag1.save();
		
		TagObject tag2 = new TagObject("TAG_2", mProjectId);
		tag2.save();
		
		story.addTag(tag1.getId());
		story.save();
		assertEquals(1, story.getTags().size());
		
		story.addTag(tag2.getId());
		story.save();
		assertEquals(2, story.getTags().size());
		
		// 不存在的 tag id, 不會加入成功
		story.addTag(5);
		story.save();
		assertEquals(2, story.getTags().size());
	}
	
	@Test
	public void testRemoveTag() {
		StoryObject story = createStory();
		
		TagObject tag1 = new TagObject("TAG_1", mProjectId);
		tag1.save();
		
		TagObject tag2 = new TagObject("TAG_2", mProjectId);
		tag2.save();
		
		TagDAO.getInstance().addTagToStory(story.getId(), tag1.getId());
		TagDAO.getInstance().addTagToStory(story.getId(), tag2.getId());		
		
		// test non-exist tag id
		story.removeTag(10);
		story.save();
		assertEquals(2, story.getTags().size());
		
		story.removeTag(tag1.getId());
		story.save();
		assertEquals(1, story.getTags().size());
		
		story.removeTag(tag2.getId());
		story.save();
		assertEquals(0, story.getTags().size());
	}

	@Test
	public void testGetTags() {	
		// test data
		String storyTagName = "TEST_TAG_NAME_";
		// create story
		StoryObject story = createStory();
		// add 3 tags
		for (int i = 0; i < 3; i++) {
			TagObject tag = new TagObject(storyTagName + (i + 1), mProjectId);
			tag.save();
			// add tag to story
			story.addTag(tag.getId());
			story.save();
		}
		
		// getTags
		ArrayList<TagObject> tags = story.getTags();
		
		// assert
		assertEquals(3, tags.size());
		for (int i = 0; i < 3; i++) {
			assertEquals(storyTagName + (i + 1), tags.get(i).getName());
		}
	}
	
	@Test
	public void testSetTags() {
		// test data
		String storyTagName = "TEST_TAG_NAME_";
		// create story
		StoryObject story = createStory();
		// List
		ArrayList<Long> tagIds = new ArrayList<Long>();
		// add 3 tags
		for (int i = 0; i < 3; i++) {
			TagObject tag = new TagObject(storyTagName + (i + 1), mProjectId);
			tag.save();
			// add tag to List
			tagIds.add(tag.getId());
		}
		
		// SetTags
		story.setTags(tagIds);
		story.save();
		
		// getTags
		ArrayList<TagObject> tags = story.getTags();
		// assert
		assertEquals(3, tags.size());
		for (int i = 0; i < 3; i++) {
			assertEquals(storyTagName + (i + 1), tags.get(i).getName());
		}
	}
	
	@Test
	public void testSetTags_MultipleSetTags() {
		// test data
		String storyTagName = "TEST_TAG_NAME_";
		// create story
		StoryObject story = createStory();
		// List
		ArrayList<Long> tagIds1 = new ArrayList<Long>();
		ArrayList<Long> tagIds2 = new ArrayList<Long>();
		// create 3 tags
		TagObject tag1 = new TagObject(storyTagName + 1, mProjectId);
		tag1.save();
		TagObject tag2 = new TagObject(storyTagName + 2, mProjectId);
		tag2.save();
		TagObject tag3 = new TagObject(storyTagName + 3, mProjectId);
		tag3.save();
		
		// add tag1 & tag2 to List1
		tagIds1.add(tag1.getId());
		tagIds1.add(tag2.getId());
		
		// add tag2 & tag3 to List2
		tagIds2.add(tag2.getId());
		tagIds2.add(tag3.getId());
		
		// SetTags 1
		story.setTags(tagIds1);
		story.save();
		// getTags
		ArrayList<TagObject> tags = story.getTags();
		// assert
		assertEquals(2, tags.size());
		assertEquals(storyTagName + 1, tags.get(0).getName());
		assertEquals(storyTagName + 2, tags.get(1).getName());
		
		// SetTags 2
		story.setTags(tagIds2);
		story.save();
		// getTags
		tags = story.getTags();
		// assert
		assertEquals(2, tags.size());
		assertEquals(storyTagName + 2, tags.get(0).getName());
		assertEquals(storyTagName + 3, tags.get(1).getName());
	}

	@Test
	public void testGetAttachFiles() {
		StoryObject story = createStory();

		AttachFileObject.Builder fileBuilder = new AttachFileObject.Builder();
		fileBuilder.setContentType("jpg").setIssueId(story.getId())
				.setIssueType(IssueTypeEnum.TYPE_STORY).setName("FILE_1").setPath("/TEST_PATH");
		AttachFileObject attachFile = fileBuilder.build();
		AttachFileDAO.getInstance().create(attachFile);
		
		attachFile = story.getAttachFiles().get(0);
		assertEquals(1, story.getAttachFiles().size());
		
		assertEquals("jpg", attachFile.getContentType());
		assertEquals("FILE_1", attachFile.getName());
		assertEquals("/TEST_PATH", attachFile.getPath());
		assertEquals(IssueTypeEnum.TYPE_STORY, attachFile.getIssueType());
	}
	
	private StoryObject createStory() {
		StoryObject story = StoryObject.get(1);

		assertNull(story);

		story = new StoryObject(mProjectId);
		story.setName("TEST_NAME").setNotes("TEST_NOTE")
				.setHowToDemo("TEST_HOW_TO_DEMO").setImportance(1).setValue(2)
				.setEstimate(3).setStatus(StoryObject.STATUS_DONE)
				.setSprintId(1).save();
		story = StoryObject.get(story.getId());
		assertEquals(1, story.getId());
		assertEquals("TEST_NAME", story.getName());
		assertEquals("TEST_NOTE", story.getNotes());
		assertEquals("TEST_HOW_TO_DEMO", story.getHowToDemo());
		assertEquals(1, story.getImportance());
		assertEquals(2, story.getValue());
		assertEquals(3, story.getEstimate());
		assertEquals(StoryObject.STATUS_DONE, story.getStatus());
		assertEquals(1, story.getSprintId());
		
		return story;
	}
}
