package ntut.csie.ezScrum.pic.core;

import static org.junit.Assert.assertEquals;

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
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;

public class MakePDFServiceTest {
	private SprintBacklogHelper mSprintBacklogHelper;

	private Configuration mConfig;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;

	private int mStoriesCount = 3;
	MakePDFService makePDFService;
	TaskObject task;
	@Before
	public void setUp() throws Exception {
		makePDFService = new MakePDFService();
		
		// initialize database
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		ini = null;

		// create test data
		int PROJECT_COUNT = 1;
		int SPRINT_COUNT = 1;
		int STORY_COUNT = 3;
		int STORY_ESTIMATE = 5;
		int TASK_COUNT = 3;
		int TASK_ESTIMATE = 8;
		String CREATE_PRODUCTBACKLOG_TYPE = "EST";

		mCP = new CreateProject(PROJECT_COUNT);
		mCP.exeCreateForDb();

		mCS = new CreateSprint(SPRINT_COUNT, mCP);
		mCS.exe();

		mASTS = new AddStoryToSprint(STORY_COUNT, STORY_ESTIMATE, mCS, mCP,
				CREATE_PRODUCTBACKLOG_TYPE);
		mASTS.exe();

		mATTS = new AddTaskToStory(TASK_COUNT, TASK_ESTIMATE, mASTS, mCP);
		mATTS.exe();

		ProjectObject project = mCP.getAllProjects().get(0);
		long sprintId = 1;
		mSprintBacklogHelper = new SprintBacklogHelper(project, sprintId);
	}
	@After
	public void teardown() {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		ini = null;
		mCP = null;
		mCS = null;
		mASTS = null;
		mATTS = null;
		mSprintBacklogHelper = null;
	}
	@Test
	public void testGetTaskPDFRow() {
		int taskSize = 6;
		
		assertEquals(3, makePDFService.getTaskPDFRow(taskSize));
	
		taskSize = 15;
		assertEquals(8, makePDFService.getTaskPDFRow(taskSize));

	}
	@Test
	public void testAtLeastHigh() {
		long taskId = 1;

		// get task one
		TaskObject task = TaskObject.get(taskId);
		assertEquals(1, task.getId());
		assertEquals("TEST_TASK_1", task.getName());
		assertEquals(1, task.getSerialId());
		
		
		String ans = "Task Id # 1" + "\n" + "TEST_TASK_1" + "\n\n\n\n" + "                                           Estimate : 8hr";
		/*String name = task.getName();
		int nameSize = name.length();
		if (nameSize < 175) {
			int addEndOfLineNum = nameSize / 35;
			for (int i = 0; i < (4 - addEndOfLineNum); i++) {
				ans += "\n";
			}
		}*/
		//ans += "                                           Estimate : " + task.getEstimate() + "hr";
		assertEquals(ans, makePDFService.atLeastHigh(task));
	}
}
