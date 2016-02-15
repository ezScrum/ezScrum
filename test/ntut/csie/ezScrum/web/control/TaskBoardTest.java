package ntut.csie.ezScrum.web.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;

public class TaskBoardTest {
	private TaskBoard mTaskBoard;
	private SprintBacklogMapper mSprintBacklogMapper;
	private SprintBacklogLogic mSprintBacklogLogic;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
	private int mProjectCount = 1;
	private int mSprintCount = 1;
	private int mStoryCount = 5;
	private int mTaskCount = 3;
	private int mTaskEstimate = 8;
	private Configuration mConfig = null;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		// 新增Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreateForDb();

		// 新增Sprint
		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();

		// 新增Story
		mASTS = new AddStoryToSprint(mStoryCount, 1, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mASTS.exe();
		
		// 新增 Task
		mATTS = new AddTaskToStory(mTaskCount, mTaskEstimate, mASTS, mCP);
		mATTS.exe();

		mSprintBacklogLogic = new SprintBacklogLogic(mCP.getAllProjects().get(0), mCS.getSprintsId().get(0));
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
		mTaskBoard = new TaskBoard(mSprintBacklogLogic, mSprintBacklogMapper);
	}

	@After
	public void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
		
		// release
		mCP = null;
		mCS = null;
		mASTS = null;
		mATTS = null;
		mTaskBoard = null;
		mConfig = null;
		mSprintBacklogLogic = null;
		mSprintBacklogMapper = null;
	}

	// TaskBoard getStories 照 Importance 排序測試1
	@Test
	public void testGetStrories_SortByImportance1() throws Exception {
		// Story 建立時即為遞減排序測試
		ArrayList<StoryObject> stories = mTaskBoard.getStories();
		// 驗證 Story 數量
		assertEquals(mStoryCount, stories.size());
		// 驗證 Story 是否依 Importance 排列
		assertTrue(stories.get(0).getImportance() == stories.get(1).getImportance());
		assertTrue(stories.get(1).getImportance() == stories.get(2).getImportance());
		assertTrue(stories.get(2).getImportance() == stories.get(3).getImportance());
		assertTrue(stories.get(3).getImportance() == stories.get(4).getImportance());
	}

	// TaskBoard getStories 照 Importance 排序測試2
	@Test
	public void testGetStrories_SortByImportance2() throws Exception {
		// Story 建立時即為遞增排序測試
		List<StoryObject> stories = mTaskBoard.getStories();
		// 驗證 Story 數量
		assertEquals(mStoryCount, stories.size());
		stories.get(0).setImportance(10).save();
		stories.get(1).setImportance(20).save();
		stories.get(2).setImportance(30).save();
		stories.get(3).setImportance(40).save();
		stories.get(4).setImportance(50).save();

		mTaskBoard = new TaskBoard(mSprintBacklogLogic, mSprintBacklogMapper);
		stories = mTaskBoard.getStories();
		// 驗證 Story 是否依 Importance 排列
		assertTrue(stories.get(0).getImportance() > stories.get(1).getImportance());
		assertTrue(stories.get(1).getImportance() > stories.get(2).getImportance());
		assertTrue(stories.get(2).getImportance() > stories.get(3).getImportance());
		assertTrue(stories.get(3).getImportance() > stories.get(4).getImportance());
	}

	// TaskBoard getStories 照 Importance 排序測試3
	@Test
	public void testGetStrories_SortByImportance() throws Exception {
		// Story 建立時即為任意順序測試
		List<StoryObject> stories = mTaskBoard.getStories();
		// 驗證 Story 數量
		assertEquals(mStoryCount, stories.size());
		stories.get(0).setImportance(40).save();
		stories.get(1).setImportance(30).save();
		stories.get(2).setImportance(10).save();
		stories.get(3).setImportance(30).save();
		stories.get(4).setImportance(40).save();

		mTaskBoard = new TaskBoard(mSprintBacklogLogic, mSprintBacklogMapper);
		stories = mTaskBoard.getStories();
		// 驗證 Story 是否依 Importance 排列
		// 驗證 Story 是否依 Importance 排列
		assertTrue(stories.get(0).getImportance() == stories.get(1).getImportance());
		assertTrue(stories.get(1).getImportance() > stories.get(2).getImportance());
		assertTrue(stories.get(2).getImportance() == stories.get(3).getImportance());
		assertTrue(stories.get(3).getImportance() > stories.get(4).getImportance());
	}
	
	@Test
	public void testGetTaskPoint(){
		// 初始 Task Point String = 120 / 120
		String actualTaskPointString = mTaskBoard.getTaskPoint();
		SprintObject sprint = mSprintBacklogMapper.getSprint();
		String expectedTaskPointString = String.valueOf(sprint.getTaskRemainsPoints()) + " / " 
		                               + String.valueOf(sprint.getTotalTaskPoints());
		assertEquals(expectedTaskPointString, actualTaskPointString);
		
		// 一個Task Done
		TaskObject task1 = mATTS.getTasks().get(0);
		String DONE_TIME = "2015/02/02-12:00:00";
		mSprintBacklogLogic.closeTask(task1.getId(), task1.getName(), task1.getNotes(), task1.getActual(), DONE_TIME);
		// assert
		actualTaskPointString = mTaskBoard.getTaskPoint();
		expectedTaskPointString = String.valueOf(sprint.getTaskRemainsPoints()) + " / " 
		                        + String.valueOf(sprint.getTotalTaskPoints());
		assertEquals(expectedTaskPointString, actualTaskPointString);
		
		// 兩個 Task Done
		TaskObject task2 = mATTS.getTasks().get(1);
		mSprintBacklogLogic.closeTask(task2.getId(), task2.getName(), task2.getNotes(), task2.getActual(), DONE_TIME);
		// assert
		actualTaskPointString = mTaskBoard.getTaskPoint();
		expectedTaskPointString = String.valueOf(sprint.getTaskRemainsPoints()) + " / "
		                        + String.valueOf(sprint.getTotalTaskPoints());
		assertEquals(expectedTaskPointString, actualTaskPointString);
		
		// 全部Task Done
		for(TaskObject task : mATTS.getTasks()){
			mSprintBacklogLogic.closeTask(task.getId(), task.getName(), task.getNotes(), task.getActual(), DONE_TIME);
		}
		// assert
		actualTaskPointString = mTaskBoard.getTaskPoint();
		expectedTaskPointString = String.valueOf(sprint.getTaskRemainsPoints()) + " / "
		                        + String.valueOf(sprint.getTotalTaskPoints());
		assertEquals(expectedTaskPointString, actualTaskPointString);
	}
	
	@Test
	public void testDoneAllTaskAndStory() {
		String projectName = "project01";
		String sprintGoal = "TEST_SPRINT_GOAL";
		int availableHours = 80;
		int focusFactor = 80;
		int interval = 2;
		
		// Story data
		String storyName = "TEST_STORY_NAME";
		int storyEstimate = 10;
		
		// Task data
		String taskName = "TEST_TASK_NAME";
		int taskEstimate = 13;
		
		Calendar calendar = Calendar.getInstance();
		
		// Get End Day
		calendar.add(Calendar.WEEK_OF_YEAR, interval);
		Date endDay = calendar.getTime();
		calendar.add(Calendar.WEEK_OF_YEAR, -interval);
		
		// Create a project
		ProjectObject project = new ProjectObject(projectName);
		project.setDisplayName(projectName)
		       .save();
		
		// Create a sprint
		SprintObject sprint = new SprintObject(project.getId());
		sprint.setStartDate(DateUtil.formatBySlashForm(calendar.getTime()))
		      .setAvailableHours(availableHours)
		      .setInterval(interval)
		      .setFocusFactor(focusFactor)
		      .setGoal(sprintGoal)
		      .setDemoDate(DateUtil.formatBySlashForm(endDay))
		      .setDueDate(DateUtil.formatBySlashForm(endDay))
		      .save();
		
		// Create a story
		StoryObject story = new StoryObject(project.getId());
		story.setName(storyName)
			 .setEstimate(storyEstimate)
			 .setSprintId(sprint.getId())
			 .save();
		
		// Create a task
		TaskObject task = new TaskObject(project.getId());
		task.setName(taskName)
			.setEstimate(taskEstimate)
			.setStoryId(story.getId())
			.save();
		
		// Create TaskBoard
		SprintBacklogMapper sprintBacklogMapper = new SprintBacklogMapper(project, sprint.getId());
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, sprint.getId());
		TaskBoard taskBoard = new TaskBoard(sprintBacklogLogic, sprintBacklogMapper);
		
		////
		// Day 2 - reduce Task Hour
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		// reduce Task remaining hour
		task.setRemains(taskEstimate - 5)
		    .save(calendar.getTimeInMillis());
		
		// Day 3 - Done all Task and Story
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		task.setRemains(0)
		    .setStatus(TaskObject.STATUS_DONE)
	        .save(calendar.getTimeInMillis());
		
		story.setStatus(StoryObject.STATUS_DONE)
		     .save(calendar.getTimeInMillis());
		
		// Assert
		assertEquals(String.valueOf(storyEstimate), taskBoard.getInitialStoryPoint().substring(0, 2));
		assertEquals(String.valueOf(taskEstimate), taskBoard.getInitialTaskPoint().substring(0, 2));
	}
}
