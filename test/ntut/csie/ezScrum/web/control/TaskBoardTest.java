package ntut.csie.ezScrum.web.control;

import java.io.IOException;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import servletunit.struts.MockStrutsTestCase;

public class TaskBoardTest extends MockStrutsTestCase {
	private TaskBoard TB;
	private SprintBacklogMapper SB;
	private SprintBacklogLogic sprintBacklogLogic;
	private CreateProject CP;
	private CreateSprint CS;
	private AddStoryToSprint ASS;
	private int ProjectCount = 1;
	private int SprintCount = 1;
	private int StoryCount = 5;
	private Configuration configuration = null;

	public TaskBoardTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		// 新增Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();

		// 新增Sprint
		this.CS = new CreateSprint(this.SprintCount, this.CP);
		this.CS.exe();

		// 新增Story
		this.ASS = new AddStoryToSprint(this.StoryCount, 1, this.CS, this.CP,
				CreateProductBacklog.TYPE_ESTIMATION);
		this.ASS.exe();

		this.sprintBacklogLogic = new SprintBacklogLogic(this.CP.getProjectList().get(0), configuration.getUserSession(), null);
		this.SB = sprintBacklogLogic.getSprintBacklogMapper();
		
		this.TB = new TaskBoard(this.sprintBacklogLogic, this.SB);
		
//		this.SB = new SprintBacklogMapper(this.CP.getProjectList().get(0), this.config.getUserSession());
//		this.TB = new TaskBoard(this.SB);

		// 為了使 Story 建立時間與修改時間分開而停下
		Thread.sleep(1000);
	}

	protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe(); // 初始化 SQL

		CopyProject copyProject = new CopyProject(this.CP);
		copyProject.exeDelete_Project(); // 刪除測試檔案
		
		configuration.setTestMode(false);
		configuration.save();
		
		// release
		copyProject = null;
		configuration = null;
	}

	// TaskBoard getStories 照 Importance 排序測試1
	public void testgetStrories1() throws Exception {
		// Story 建立時即為遞減排序測試
		List<IIssue> stories = this.TB.getStories();
		// 驗證 Story 數量
		assertEquals(this.StoryCount, stories.size());
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
	public void testgetStrories2() throws Exception {
		// Story 建立時即為遞增排序測試
		List<IIssue> storyList = this.TB.getStories();
		IIssue[] stories = storyList.toArray(new IIssue[storyList.size()]);
		// 驗證 Story 數量
		assertEquals(this.StoryCount, stories.length);
		// 修改 Story 的 Importance 順序
//		ProductBacklogHelper helper = new ProductBacklogHelper(this.CP.getProjectList().get(0), this.config.getUserSession());
//		boolean isSuccess = false;
//		isSuccess = helper.edit(stories[0].getIssueID(), stories[0]
//				.getSummary(), "10", "10", stories[0].getEstimated(),
//				stories[0].getHowToDemo(), stories[0].getNotes());
//		assertEquals(true, isSuccess);
//		isSuccess = helper.edit(stories[1].getIssueID(), stories[1]
//				.getSummary(), "10", "20", stories[1].getEstimated(),
//				stories[1].getHowToDemo(), stories[1].getNotes());
//		assertEquals(true, isSuccess);
//		isSuccess = helper.edit(stories[2].getIssueID(), stories[2]
//				.getSummary(), "10", "30", stories[2].getEstimated(),
//				stories[2].getHowToDemo(), stories[2].getNotes());
//		assertEquals(true, isSuccess);
//		isSuccess = helper.edit(stories[3].getIssueID(), stories[3]
//				.getSummary(), "10", "40", stories[3].getEstimated(),
//				stories[3].getHowToDemo(), stories[3].getNotes());
//		assertEquals(true, isSuccess);
//		isSuccess = helper.edit(stories[4].getIssueID(), stories[4]
//				.getSummary(), "10", "50", stories[4].getEstimated(),
//				stories[4].getHowToDemo(), stories[4].getNotes());
		ProductBacklogHelper helper = new ProductBacklogHelper(configuration.getUserSession(), this.CP.getProjectList().get(0));
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
		this.SB.forceRefresh();

		this.TB = new TaskBoard(this.sprintBacklogLogic, this.SB);
		storyList = this.TB.getStories();
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
	public void testgetStrories3() throws Exception {
		// Story 建立時即為任意順序測試
		List<IIssue> storyList = this.TB.getStories();
		IIssue[] stories = storyList.toArray(new IIssue[storyList.size()]);
		// 驗證 Story 數量
		assertEquals(this.StoryCount, stories.length);

		// 修改 Story 的 Importance 順序
//		ProductBacklogHelper helper = new ProductBacklogHelper(this.CP.getProjectList().get(0), this.config.getUserSession());
//		boolean isSuccess = false;
//		isSuccess = helper.edit(stories[0].getIssueID(), stories[0]
//				.getSummary(), "10", "40", stories[0].getEstimated(),
//				stories[0].getHowToDemo(), stories[0].getNotes());
//		assertEquals(true, isSuccess);
//		this.SB.forceRefresh();
//		isSuccess = helper.edit(stories[1].getIssueID(), stories[1]
//				.getSummary(), "10", "30", stories[1].getEstimated(),
//				stories[1].getHowToDemo(), stories[1].getNotes());
//		assertEquals(true, isSuccess);
//		this.SB.forceRefresh();
//		isSuccess = helper.edit(stories[2].getIssueID(), stories[2]
//				.getSummary(), "10", "10", stories[2].getEstimated(),
//				stories[2].getHowToDemo(), stories[2].getNotes());
//		assertEquals(true, isSuccess);
//		this.SB.forceRefresh();
//		isSuccess = helper.edit(stories[3].getIssueID(), stories[3]
//				.getSummary(), "10", "30", stories[3].getEstimated(),
//				stories[3].getHowToDemo(), stories[3].getNotes());
//		assertEquals(true, isSuccess);
//		this.SB.forceRefresh();
//		isSuccess = helper.edit(stories[4].getIssueID(), stories[4]
//				.getSummary(), "10", "40", stories[4].getEstimated(),
//				stories[4].getHowToDemo(), stories[4].getNotes());
		ProductBacklogHelper helper = new ProductBacklogHelper(configuration.getUserSession(), this.CP.getProjectList().get(0));
		IIssue issue = null;
		issue = helper.editStory(stories[0].getIssueID(), stories[0]
				.getSummary(), "10", "40", stories[0].getEstimated(),
				stories[0].getHowToDemo(), stories[0].getNotes(), true);
		assertNotNull(issue);
		this.SB.forceRefresh();
		issue = helper.editStory(stories[1].getIssueID(), stories[1]
				.getSummary(), "10", "30", stories[1].getEstimated(),
				stories[1].getHowToDemo(), stories[1].getNotes(), true);
		assertNotNull(issue);
		this.SB.forceRefresh();
		issue = helper.editStory(stories[2].getIssueID(), stories[2]
				.getSummary(), "10", "10", stories[2].getEstimated(),
				stories[2].getHowToDemo(), stories[2].getNotes(), true);
		assertNotNull(issue);
		this.SB.forceRefresh();
		issue = helper.editStory(stories[3].getIssueID(), stories[3]
				.getSummary(), "10", "30", stories[3].getEstimated(),
				stories[3].getHowToDemo(), stories[3].getNotes(), true);
		assertNotNull(issue);
		this.SB.forceRefresh();
		issue = helper.editStory(stories[4].getIssueID(), stories[4]
				.getSummary(), "10", "40", stories[4].getEstimated(),
				stories[4].getHowToDemo(), stories[4].getNotes(), true);
		assertNotNull(issue);
		this.SB.forceRefresh();

		this.TB = new TaskBoard(this.sprintBacklogLogic, this.SB);
		storyList = this.TB.getStories();
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
}
