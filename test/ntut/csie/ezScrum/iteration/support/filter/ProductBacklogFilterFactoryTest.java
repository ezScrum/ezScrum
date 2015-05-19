package ntut.csie.ezScrum.iteration.support.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProductBacklogFilterFactoryTest {
	private MySQLControl mControl = null;
	private Configuration mConfig = null;
	private StoryDataForFilter mData = null;
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
		
		mData = new StoryDataForFilter();
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
	public void testgetPBFilterFilter_BACKLOG() {
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter("BACKLOG", mData.getStorirs());
		ArrayList<StoryObject> filterStories = filter.getStories();

		// 藉由驗證是否被此 Factory 建構出的物件給過濾 Story 
		// 來判斷採用的過濾方法的物件是否為預期的
		assertEquals(5, filterStories.size());
		// 驗證取出條件為 business value = 0 or null
		for (StoryObject story : filterStories) {
			assertTrue(story.getEstimate() == 0 ||
					   story.getValue() == 0     ||
					   story.getImportance() == 0 );
		}
	}
	
	@Test
	public void testgetPBFilterFilter_DETAIL() {
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter("DETAIL", mData.getStorirs());
		ArrayList<StoryObject> filterStories = filter.getStories();

		// 藉由驗證是否被此 Factory 建構出的物件給過濾 Story 
		// 來判斷採用的過濾方法的物件是否為預期的
		assertEquals(2, filterStories.size());
		// 驗證取出條件為 business value = 0 or null
		for (StoryObject story : filterStories) {
			assertTrue(story.getValue() > 0);
			assertTrue(story.getImportance() > 0);
			assertTrue(story.getEstimate() > 0);
			assertEquals(StoryObject.STATUS_UNCHECK, story.getStatus());
		}
	}
	
	@Test
	public void testgetPBFilterFilter_DONE() {
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter("DONE", mData.getStorirs());
		ArrayList<StoryObject> filterStories = filter.getStories();

		// 藉由驗證是否被此 Factory 建構出的物件給過濾 Story 
		// 來判斷採用的過濾方法的物件是否為預期的
		assertEquals(3, filterStories.size());
		// 驗證取出條件為狀態 closed
		for (StoryObject story : filterStories) {
			assertEquals(100, story.getValue());
			assertEquals(100, story.getImportance());
			assertEquals(5, story.getEstimate());
			assertEquals(StoryObject.STATUS_DONE, story.getStatus());
		}
	}
	
	@Test
	public void testgetPBFilterFilter_NULL_1() {
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter("", mData.getStorirs());
		ArrayList<StoryObject> filterStories = filter.getStories();

		// 藉由驗證是否被此 Factory 建構出的物件給過濾 Story 
		// 來判斷採用的過濾方法的物件是否為預期的
		assertEquals(10, filterStories.size());
		// 驗證取出為空狀態的 story
		assertNotNull(filterStories);
	}
	
	@Test
	public void testgetPBFilterFilter_NULL_2() {
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter(null, mData.getStorirs());
		ArrayList<StoryObject> filterStories = filter.getStories();

		// 藉由驗證是否被此 Factory 建構出的物件給過濾 Story 
		// 來判斷採用的過濾方法的物件是否為預期的
		assertEquals(10, filterStories.size());
		// 驗證取出為空狀態的 story
		assertNotNull(filterStories);
	}
}
