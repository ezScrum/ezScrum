package ntut.csie.ezScrum.iteration.support.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.IStory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProductBacklogFilterFactoryTest {
	
	private StoryDataForFilter mData = null;
	
	@Before
	public void setUp() {
		mData = new StoryDataForFilter();
	}
	
	@After
	public void tearDown() {
		mData = null;
	}
	
	@Test
	public void testgetPBFilterFilter_BACKLOG() {
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter("BACKLOG", mData.getStorirs());
		IStory[] filterStories = filter.getStories();

		// 藉由驗證是否被此 Factory 建構出的物件給過濾 Story 
		// 來判斷採用的過濾方法的物件是否為預期的
		assertEquals(5, filterStories.length);
		// 驗證取出條件為 business value = 0 or null
		for (IStory s : filterStories) {
			assertTrue( Integer.parseInt(s.getEstimated()) == 0 ||
					    Integer.parseInt(s.getValue()) == 0     ||
					    Integer.parseInt(s.getImportance()) == 0 );
		}
	}
	
	@Test
	public void testgetPBFilterFilter_DETAIL() {
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter("DETAIL", mData.getStorirs());
		IStory[] filterStories = filter.getStories();

		// 藉由驗證是否被此 Factory 建構出的物件給過濾 Story 
		// 來判斷採用的過濾方法的物件是否為預期的
		assertEquals(2, filterStories.length);
		// 驗證取出條件為 business value = 0 or null
		for (IStory s : filterStories) {
			assertTrue(Integer.parseInt(s.getValue()) > 0);
			assertTrue(Integer.parseInt(s.getImportance()) > 0);
			assertTrue(Integer.parseInt(s.getEstimated()) > 0);
			assertEquals(ITSEnum.S_NEW_STATUS, s.getStatus());
		}
	}
	
	@Test
	public void testgetPBFilterFilter_DONE() {
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter("DONE", mData.getStorirs());
		IStory[] filterStories = filter.getStories();

		// 藉由驗證是否被此 Factory 建構出的物件給過濾 Story 
		// 來判斷採用的過濾方法的物件是否為預期的
		assertEquals(3, filterStories.length);
		// 驗證取出條件為狀態 closed
		for (IStory s : filterStories) {
			assertEquals("100", s.getValue());
			assertEquals("100", s.getImportance());
			assertEquals("5", s.getEstimated());
			assertEquals(ITSEnum.S_CLOSED_STATUS, s.getStatus());
		}
	}
	
	@Test
	public void testgetPBFilterFilter_NULL_1() {
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter("", mData.getStorirs());
		IStory[] filterStories = filter.getStories();

		// 藉由驗證是否被此 Factory 建構出的物件給過濾 Story 
		// 來判斷採用的過濾方法的物件是否為預期的
		assertEquals(10, filterStories.length);
		// 驗證取出為空狀態的 story
		assertNotNull(filterStories);
	}
	
	@Test
	public void testgetPBFilterFilter_NULL_2() {
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter(null, mData.getStorirs());
		IStory[] filterStories = filter.getStories();

		// 藉由驗證是否被此 Factory 建構出的物件給過濾 Story 
		// 來判斷採用的過濾方法的物件是否為預期的
		assertEquals(10, filterStories.length);
		// 驗證取出為空狀態的 story
		assertNotNull(filterStories);
	}
}
