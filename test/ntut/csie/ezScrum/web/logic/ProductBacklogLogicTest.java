package ntut.csie.ezScrum.web.logic;

import java.util.ArrayList;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.jcis.resource.core.IProject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProductBacklogLogicTest {
	private ProductBacklogLogic mProductBacklogLogic = null;
	private Configuration mConfig = null;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
	
    @Before
	public void setUp() throws Exception {
		// initialize database
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();// 初始化 SQL

		// create test data
		int PROJECT_COUNT = 1;
		int SPRINT_COUNT = 2;
		int STORY_COUNT = 3;
		int STORY_ESTIMATE = 5;
		int TASK_COUNT = 3;
		int TASK_ESTIMATE = 8;
		String CREATE_PRODUCTBACKLOG_TYPE = "EST";

		mCP = new CreateProject(PROJECT_COUNT);
		mCP.exeCreate();

		mCS = new CreateSprint(SPRINT_COUNT, mCP);
		mCS.exe();

		mASTS = new AddStoryToSprint(STORY_COUNT, STORY_ESTIMATE, mCS, mCP, CREATE_PRODUCTBACKLOG_TYPE);
		mASTS.exe();

		mATTS = new AddTaskToStory(TASK_COUNT, TASK_ESTIMATE, mASTS, mCP);
		mATTS.exe();

		IProject project = mCP.getProjectList().get(0);
		
		mProductBacklogLogic = new ProductBacklogLogic(project);
	}
	
	@After
	public void tearDown() {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
		
		mProductBacklogLogic = null;
		mConfig = null;
		ini = null;
		mCP = null;
		mCS = null;
		mASTS = null;
		mATTS = null;
	}
	
	@Test
	public void testAddStoriesToSprint() {
		Assert.assertEquals(3, StoryObject.getStoriesBySprintId(Integer.valueOf(mCS.getSprintIDList().get(0))).size());
		Assert.assertEquals(3, StoryObject.getStoriesBySprintId(Integer.valueOf(mCS.getSprintIDList().get(1))).size());
		Assert.assertEquals(1, mASTS.getStories().get(0).getId());
		Assert.assertEquals(1, mASTS.getStories().get(0).getSprintId());
		Assert.assertEquals(2, mASTS.getStories().get(1).getId());
		Assert.assertEquals(1, mASTS.getStories().get(1).getSprintId());
		Assert.assertEquals(3, mASTS.getStories().get(2).getId());
		Assert.assertEquals(1, mASTS.getStories().get(2).getSprintId());
		Assert.assertEquals(4, mASTS.getStories().get(3).getId());
		Assert.assertEquals(2, mASTS.getStories().get(3).getSprintId());
		Assert.assertEquals(5, mASTS.getStories().get(4).getId());
		Assert.assertEquals(2, mASTS.getStories().get(4).getSprintId());
		Assert.assertEquals(6, mASTS.getStories().get(5).getId());
		Assert.assertEquals(2, mASTS.getStories().get(5).getSprintId());
		ArrayList<Long> storiesId = new ArrayList<Long>();
		storiesId.add(1l);
		storiesId.add(2l);
		storiesId.add(3l);
		mProductBacklogLogic.addStoriesToSprint(storiesId, Integer.valueOf(mCS.getSprintIDList().get(1)));
		Assert.assertEquals(0, StoryObject.getStoriesBySprintId(Integer.valueOf(mCS.getSprintIDList().get(0))).size());
		Assert.assertEquals(6, StoryObject.getStoriesBySprintId(Integer.valueOf(mCS.getSprintIDList().get(1))).size());
		Assert.assertEquals(1, StoryObject.get(mASTS.getStories().get(0).getId()).getId());
		Assert.assertEquals(2, StoryObject.get(mASTS.getStories().get(0).getId()).getSprintId());
		Assert.assertEquals(2, StoryObject.get(mASTS.getStories().get(1).getId()).getId());
		Assert.assertEquals(2, StoryObject.get(mASTS.getStories().get(1).getId()).getSprintId());
		Assert.assertEquals(3, StoryObject.get(mASTS.getStories().get(2).getId()).getId());
		Assert.assertEquals(2, StoryObject.get(mASTS.getStories().get(2).getId()).getSprintId());
		Assert.assertEquals(4, StoryObject.get(mASTS.getStories().get(3).getId()).getId());
		Assert.assertEquals(2, StoryObject.get(mASTS.getStories().get(3).getId()).getSprintId());
		Assert.assertEquals(5, StoryObject.get(mASTS.getStories().get(4).getId()).getId());
		Assert.assertEquals(2, StoryObject.get(mASTS.getStories().get(4).getId()).getSprintId());
		Assert.assertEquals(6, StoryObject.get(mASTS.getStories().get(5).getId()).getId());
		Assert.assertEquals(2, StoryObject.get(mASTS.getStories().get(5).getId()).getSprintId());
		ArrayList<Long> storiesId2 = new ArrayList<Long>();
		storiesId2.add(4l);
		storiesId2.add(5l);
		storiesId2.add(6l);
		mProductBacklogLogic.addStoriesToSprint(storiesId2, Integer.valueOf(mCS.getSprintIDList().get(0)));
		Assert.assertEquals(3, StoryObject.getStoriesBySprintId(Integer.valueOf(mCS.getSprintIDList().get(0))).size());
		Assert.assertEquals(3, StoryObject.getStoriesBySprintId(Integer.valueOf(mCS.getSprintIDList().get(1))).size());
		Assert.assertEquals(1, StoryObject.get(mASTS.getStories().get(0).getId()).getId());
		Assert.assertEquals(2, StoryObject.get(mASTS.getStories().get(0).getId()).getSprintId());
		Assert.assertEquals(2, StoryObject.get(mASTS.getStories().get(1).getId()).getId());
		Assert.assertEquals(2, StoryObject.get(mASTS.getStories().get(1).getId()).getSprintId());
		Assert.assertEquals(3, StoryObject.get(mASTS.getStories().get(2).getId()).getId());
		Assert.assertEquals(2, StoryObject.get(mASTS.getStories().get(2).getId()).getSprintId());
		Assert.assertEquals(4, StoryObject.get(mASTS.getStories().get(3).getId()).getId());
		Assert.assertEquals(1, StoryObject.get(mASTS.getStories().get(3).getId()).getSprintId());
		Assert.assertEquals(5, StoryObject.get(mASTS.getStories().get(4).getId()).getId());
		Assert.assertEquals(1, StoryObject.get(mASTS.getStories().get(4).getId()).getSprintId());
		Assert.assertEquals(6, StoryObject.get(mASTS.getStories().get(5).getId()).getId());
		Assert.assertEquals(1, StoryObject.get(mASTS.getStories().get(5).getId()).getSprintId());
	}
	
	@Test
	public void testAddStoriesToSprint_WithInvalidSprintId() {
		Assert.assertEquals(3, StoryObject.getStoriesBySprintId(Integer.valueOf(mCS.getSprintIDList().get(0))).size());
		Assert.assertEquals(3, StoryObject.getStoriesBySprintId(Integer.valueOf(mCS.getSprintIDList().get(1))).size());
		Assert.assertEquals(1, mASTS.getStories().get(0).getId());
		Assert.assertEquals(1, mASTS.getStories().get(0).getSprintId());
		Assert.assertEquals(2, mASTS.getStories().get(1).getId());
		Assert.assertEquals(1, mASTS.getStories().get(1).getSprintId());
		Assert.assertEquals(3, mASTS.getStories().get(2).getId());
		Assert.assertEquals(1, mASTS.getStories().get(2).getSprintId());
		Assert.assertEquals(4, mASTS.getStories().get(3).getId());
		Assert.assertEquals(2, mASTS.getStories().get(3).getSprintId());
		Assert.assertEquals(5, mASTS.getStories().get(4).getId());
		Assert.assertEquals(2, mASTS.getStories().get(4).getSprintId());
		Assert.assertEquals(6, mASTS.getStories().get(5).getId());
		Assert.assertEquals(2, mASTS.getStories().get(5).getSprintId());
		ArrayList<Long> storiesId = new ArrayList<Long>();
		storiesId.add(1l);
		storiesId.add(2l);
		storiesId.add(3l);
		storiesId.add(4l);
		storiesId.add(5l);
		storiesId.add(6l);
		mProductBacklogLogic.addStoriesToSprint(storiesId, -1);
		Assert.assertEquals(3, StoryObject.getStoriesBySprintId(Integer.valueOf(mCS.getSprintIDList().get(0))).size());
		Assert.assertEquals(3, StoryObject.getStoriesBySprintId(Integer.valueOf(mCS.getSprintIDList().get(1))).size());
		Assert.assertEquals(1, StoryObject.get(mASTS.getStories().get(0).getId()).getId());
		Assert.assertEquals(1, StoryObject.get(mASTS.getStories().get(0).getId()).getSprintId());
		Assert.assertEquals(2, StoryObject.get(mASTS.getStories().get(1).getId()).getId());
		Assert.assertEquals(1, StoryObject.get(mASTS.getStories().get(1).getId()).getSprintId());
		Assert.assertEquals(3, StoryObject.get(mASTS.getStories().get(2).getId()).getId());
		Assert.assertEquals(1, StoryObject.get(mASTS.getStories().get(2).getId()).getSprintId());
		Assert.assertEquals(4, StoryObject.get(mASTS.getStories().get(3).getId()).getId());
		Assert.assertEquals(2, StoryObject.get(mASTS.getStories().get(3).getId()).getSprintId());
		Assert.assertEquals(5, StoryObject.get(mASTS.getStories().get(4).getId()).getId());
		Assert.assertEquals(2, StoryObject.get(mASTS.getStories().get(4).getId()).getSprintId());
		Assert.assertEquals(6, StoryObject.get(mASTS.getStories().get(5).getId()).getId());
		Assert.assertEquals(2, StoryObject.get(mASTS.getStories().get(5).getId()).getSprintId());
	}
	
	@Test
	public void testGetAddableStories() {
		Assert.assertEquals(0, mProductBacklogLogic.getAddableStories().size());
		ArrayList<StoryObject> stories = mASTS.getStories();
		stories.get(1).setSprintId(-1).save();
		Assert.assertEquals(1, mProductBacklogLogic.getAddableStories().size());
		StoryObject addableStory = mProductBacklogLogic.getAddableStories().get(0);
		Assert.assertEquals(stories.get(1).getId(), addableStory.getId());
		Assert.assertEquals(-1, addableStory.getSprintId());
	}
}
