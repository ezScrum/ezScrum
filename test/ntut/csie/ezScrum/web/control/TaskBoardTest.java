package ntut.csie.ezScrum.web.control;

import java.io.IOException;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import servletunit.struts.MockStrutsTestCase;

public class TaskBoardTest extends MockStrutsTestCase {
	private TaskBoard mTB;
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
	private Configuration mConfiguration = null;

	public TaskBoardTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfiguration = new Configuration();
		mConfiguration.setTestMode(true);
		mConfiguration.save();
		
		InitialSQL ini = new InitialSQL(mConfiguration);
		ini.exe(); // 初始化 SQL

		// 新增Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		// 新增Sprint
		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();

		// 新增Story
		mASTS = new AddStoryToSprint(mStoryCount, 1, mCS, mCP, CreateProductBacklog.TYPE_ESTIMATION);
		mASTS.exe();
		
		// 新增 Task
		mATTS = new AddTaskToStory(mTaskCount, mTaskEstimate, mASTS, mCP);
		mATTS.exe();

		mSprintBacklogLogic = new SprintBacklogLogic(mCP.getProjectList().get(0), mConfiguration.getUserSession(), mCS.getSprintIDList().get(0));
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
		
		mTB = new TaskBoard(mSprintBacklogLogic, mSprintBacklogMapper);

		// 為了使 Story 建立時間與修改時間分開而停下
		Thread.sleep(1000);
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfiguration);
		ini.exe(); // 初始化 SQL

		CopyProject copyProject = new CopyProject(mCP);
		copyProject.exeDelete_Project(); // 刪除測試檔案
		
		mConfiguration.setTestMode(false);
		mConfiguration.save();
		
		// release
		mTB = null;
		copyProject = null;
		mConfiguration = null;
		mSprintBacklogLogic = null;
	}

	// TaskBoard getStories 照 Importance 排序測試1
	public void testGetStrories_SortByImportance1() throws Exception {
		// Story 建立時即為遞減排序測試
		List<IIssue> stories = mTB.getStories();
		// 驗證 Story 數量
		assertEquals(mStoryCount, stories.size());
		// 驗證 Story 是否依 Importance 排列
		int impA = Integer.valueOf(stories.get(0).getImportance());
		int impB = Integer.valueOf(stories.get(1).getImportance());
		assertEquals(true, (impA > impB));
		impA = Integer.valueOf(stories.get(1).getImportance());
		impB = Integer.valueOf(stories.get(2).getImportance());
		assertEquals(true, (impA > impB));
		impA = Integer.valueOf(stories.get(2).getImportance());
		impB = Integer.valueOf(stories.get(3).getImportance());
		assertEquals(true, (impA > impB));
		impA = Integer.valueOf(stories.get(3).getImportance());
		impB = Integer.valueOf(stories.get(4).getImportance());
		assertEquals(true, (impA > impB));
	}

	// TaskBoard getStories 照 Importance 排序測試2
	public void testGetStrories_SortByImportance2() throws Exception {
		// Story 建立時即為遞增排序測試
		List<IIssue> storyList = this.mTB.getStories();
		IIssue[] stories = storyList.toArray(new IIssue[storyList.size()]);
		// 驗證 Story 數量
		assertEquals(this.mStoryCount, stories.length);
		
		ProductBacklogHelper helper = new ProductBacklogHelper(mConfiguration.getUserSession(), this.mCP.getProjectList().get(0));
		IIssue issue = null;
		issue = helper.editStory(stories[0].getIssueID(), stories[0].getSummary(), "10", "10", stories[0].getEstimated(),
				stories[0].getHowToDemo(), stories[0].getNotes(), true);
		assertNotNull(issue);
		issue = helper.editStory(stories[1].getIssueID(), stories[1]
				.getSummary(), "10", "20", stories[1].getEstimated(), stories[1].getHowToDemo(), stories[1].getNotes(), true);
		assertNotNull(issue);
		issue = helper.editStory(stories[2].getIssueID(), stories[2]
				.getSummary(), "10", "30", stories[2].getEstimated(),
				stories[2].getHowToDemo(), stories[2].getNotes(), true);
		assertNotNull(issue);
		issue = helper.editStory(stories[3].getIssueID(), stories[3]
				.getSummary(), "10", "40", stories[3].getEstimated(),
				stories[3].getHowToDemo(), stories[3].getNotes(), true);
		assertNotNull(issue);
		issue = helper.editStory(stories[4].getIssueID(), stories[4]
				.getSummary(), "10", "50", stories[4].getEstimated(),
				stories[4].getHowToDemo(), stories[4].getNotes(), true);
		assertNotNull(issue);
		this.mSprintBacklogMapper.forceRefresh();

		this.mTB = new TaskBoard(this.mSprintBacklogLogic, this.mSprintBacklogMapper);
		storyList = this.mTB.getStories();
		stories = storyList.toArray(new IIssue[storyList.size()]);
		// 驗證 Story 是否依 Importance 排列
		int impA = Integer.valueOf(stories[0].getImportance());
		int impB = Integer.valueOf(stories[1].getImportance());
		assertEquals(true, (impA > impB));
		impA = Integer.valueOf(stories[1].getImportance());
		impB = Integer.valueOf(stories[2].getImportance());
		assertEquals(true, (impA > impB));
		impA = Integer.valueOf(stories[2].getImportance());
		impB = Integer.valueOf(stories[3].getImportance());
		assertEquals(true, (impA > impB));
		impA = Integer.valueOf(stories[3].getImportance());
		impB = Integer.valueOf(stories[4].getImportance());
		assertEquals(true, (impA > impB));
	}

	// TaskBoard getStories 照 Importance 排序測試3
	public void testGetStrories_SortByImportance() throws Exception {
		// Story 建立時即為任意順序測試
		List<IIssue> storyList = this.mTB.getStories();
		IIssue[] stories = storyList.toArray(new IIssue[storyList.size()]);
		// 驗證 Story 數量
		assertEquals(this.mStoryCount, stories.length);

		ProductBacklogHelper helper = new ProductBacklogHelper(mConfiguration.getUserSession(), this.mCP.getProjectList().get(0));
		IIssue issue = null;
		issue = helper.editStory(stories[0].getIssueID(), stories[0]
				.getSummary(), "10", "40", stories[0].getEstimated(),
				stories[0].getHowToDemo(), stories[0].getNotes(), true);
		assertNotNull(issue);
		this.mSprintBacklogMapper.forceRefresh();
		issue = helper.editStory(stories[1].getIssueID(), stories[1]
				.getSummary(), "10", "30", stories[1].getEstimated(),
				stories[1].getHowToDemo(), stories[1].getNotes(), true);
		assertNotNull(issue);
		this.mSprintBacklogMapper.forceRefresh();
		issue = helper.editStory(stories[2].getIssueID(), stories[2]
				.getSummary(), "10", "10", stories[2].getEstimated(),
				stories[2].getHowToDemo(), stories[2].getNotes(), true);
		assertNotNull(issue);
		this.mSprintBacklogMapper.forceRefresh();
		issue = helper.editStory(stories[3].getIssueID(), stories[3]
				.getSummary(), "10", "30", stories[3].getEstimated(),
				stories[3].getHowToDemo(), stories[3].getNotes(), true);
		assertNotNull(issue);
		this.mSprintBacklogMapper.forceRefresh();
		issue = helper.editStory(stories[4].getIssueID(), stories[4]
				.getSummary(), "10", "40", stories[4].getEstimated(),
				stories[4].getHowToDemo(), stories[4].getNotes(), true);
		assertNotNull(issue);
		this.mSprintBacklogMapper.forceRefresh();

		this.mTB = new TaskBoard(this.mSprintBacklogLogic, this.mSprintBacklogMapper);
		storyList = this.mTB.getStories();
		stories = storyList.toArray(new IIssue[storyList.size()]);
		// 驗證 Story 是否依 Importance 排列
		int impA = Integer.valueOf(stories[0].getImportance());
		int impB = Integer.valueOf(stories[1].getImportance());
		assertEquals(true, (impA == impB));
		impA = Integer.valueOf(stories[1].getImportance());
		impB = Integer.valueOf(stories[2].getImportance());
		assertEquals(true, (impA > impB));
		impA = Integer.valueOf(stories[2].getImportance());
		impB = Integer.valueOf(stories[3].getImportance());
		assertEquals(true, (impA == impB));
		impA = Integer.valueOf(stories[3].getImportance());
		impB = Integer.valueOf(stories[4].getImportance());
		assertEquals(true, (impA > impB));
	}
	
	public void testGetTaskPoint(){
		// 初始 Task Point String = 120 / 120
		String actualTaskPointString = mTB.getTaskPoint();
		String expectedTaskPointString = String.valueOf(mSprintBacklogLogic.getTaskCurrnetRemainsPoint()) + " / " 
		                               + String.valueOf(mSprintBacklogLogic.getTaskCurrentEstimatePoint());
		assertEquals(expectedTaskPointString, actualTaskPointString);
		
		// 一個Task Done
		TaskObject task1 = mATTS.getTasks().get(0);
		String DONE_TIME = "2015/02/02-12:00:00";
		mSprintBacklogLogic.closeTask(task1.getId(), task1.getName(), task1.getNotes(), task1.getActual(), DONE_TIME);
		// assert
		actualTaskPointString = mTB.getTaskPoint();
		expectedTaskPointString = String.valueOf(mSprintBacklogLogic.getTaskCurrnetRemainsPoint()) + " / " 
		                        + String.valueOf(mSprintBacklogLogic.getTaskCurrentEstimatePoint());
		assertEquals(expectedTaskPointString, actualTaskPointString);
		
		// 兩個 Task Done
		TaskObject task2 = mATTS.getTasks().get(1);
		mSprintBacklogLogic.closeTask(task2.getId(), task2.getName(), task2.getNotes(), task2.getActual(), DONE_TIME);
		// assert
		actualTaskPointString = mTB.getTaskPoint();
		expectedTaskPointString = String.valueOf(mSprintBacklogLogic.getTaskCurrnetRemainsPoint()) + " / "
		                        + String.valueOf(mSprintBacklogLogic.getTaskCurrentEstimatePoint());
		assertEquals(expectedTaskPointString, actualTaskPointString);
		
		// 全部Task Done
		for(TaskObject task : mATTS.getTasks()){
			mSprintBacklogLogic.closeTask(task.getId(), task.getName(), task.getNotes(), task.getActual(), DONE_TIME);
		}
		// assert
		actualTaskPointString = mTB.getTaskPoint();
		expectedTaskPointString = String.valueOf(mSprintBacklogLogic.getTaskCurrnetRemainsPoint()) + " / "
		                        + String.valueOf(mSprintBacklogLogic.getTaskCurrentEstimatePoint());
		assertEquals(expectedTaskPointString, actualTaskPointString);
	}
}
