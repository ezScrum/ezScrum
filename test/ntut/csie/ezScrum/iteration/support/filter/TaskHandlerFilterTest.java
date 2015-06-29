package ntut.csie.ezScrum.iteration.support.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaskHandlerFilterTest {
	private MySQLControl mControl = null;
	private Configuration mConfig = null;
	private StoryDataForFilter mData = null;
	private String mCompareInfo = "TaskHandlerTest";
	private CreateProject mCP;

	@Before
	public void setUp() {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mControl = new MySQLControl(mConfig);
		mControl.connect();
		
		mCP = new CreateProject(1);
		mCP.exeCreate();
		
		mData = new StoryDataForFilter(mCompareInfo);
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
		
		projectManager = null;
		mControl = null;
		mConfig = null;
		mData = null;
		mCP = null;
	}
	
	@Test
	public void testFilterStories() {
		AProductBacklogFilter filter = new TaskHandlerFilter(mData.getTasksByInfo(), mCompareInfo);
		assertEquals(0, filter.getStories().size());
	}
	
	@Test
	public void testFilterTasks_1() {
		AProductBacklogFilter filter = new TaskHandlerFilter(mData.getTasksByInfo(), mCompareInfo);
		ArrayList<TaskObject> filterTasks = filter.getTasks();

		assertEquals(5, filterTasks.size());
		int id = 4;
		for (TaskObject task : filterTasks) {
			assertEquals(task.getId(), id++);
		}
		
		assertEquals(0, filter.getStories().size());
	}
	
	@Test
	public void testFilterTasks_2() {
		AProductBacklogFilter filter = new TaskHandlerFilter(mData.getTasksByInfo(), "ALL");
		ArrayList<TaskObject> filterTasks = filter.getTasks();

		assertEquals(10, filterTasks.size());
		// 驗證取出條件為 Handler 內容有 "TaskHandlerTest"
		int id = 1;
		for (TaskObject task : filterTasks) {
			assertEquals(task.getId(), id++);
		}
		
		assertEquals(0, filter.getStories().size());
	}
}
