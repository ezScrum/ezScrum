package ntut.csie.ezScrum.iteration.support.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IssueNameFilterTest {
	
	private StoryDataForFilter mData = null;
	private CreateProject mCP;
	private Configuration mConfig;
	private String mCompareInfo = "IssueNameTest";
	
	@Before
	public void setUp() {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
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
		
		mCP = null;
		mData = null;
	}
	
	@Test
	public void testFilterStories() {
		AProductBacklogFilter filter = new IssueNameFilter(mData.getStorirsByInfo(), mCompareInfo);
		ArrayList<StoryObject> filterStories = filter.getStories();

		assertEquals(5, filterStories.size());
		for (StoryObject s : filterStories) {
			assertTrue(s.getName().contains(mCompareInfo));
		}
		assertEquals(0, filter.getTasks().size());
	}
	
	public void testFilterTasks() {
		AProductBacklogFilter filter = new IssueNameFilter(mData.getTasksByInfo(), mCompareInfo);
		ArrayList<TaskObject> filterTasks = filter.getTasks();

		assertEquals(5, filterTasks.size());
		for (TaskObject t : filterTasks) {
			assertTrue(t.getName().contains(mCompareInfo));
		}
		assertEquals(0, filter.getStories().size());
	}
}
