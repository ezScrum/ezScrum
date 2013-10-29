package ntut.csie.ezScrum.iteration.support.filter;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.IStory;

public class ProductBacklogFilterFactoryTest extends TestCase {
	
	private StoryDataForFilter data = null;
	
	public ProductBacklogFilterFactoryTest(String testMethod) {
        super(testMethod);
    }

	protected void setUp() {
		this.data = new StoryDataForFilter();
	}
	
	protected void tearDown() {
		this.data = null;
	}
	
	public void testgetPBFilterFilter_BACKLOG() {
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter("BACKLOG", this.data.getStorirs());
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
	
	public void testgetPBFilterFilter_DETAIL() {
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter("DETAIL", this.data.getStorirs());
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
	
	public void testgetPBFilterFilter_DONE() {
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter("DONE", this.data.getStorirs());
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
	
	public void testgetPBFilterFilter_NULL_1() {
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter("", this.data.getStorirs());
		IStory[] filterStories = filter.getStories();

		// 藉由驗證是否被此 Factory 建構出的物件給過濾 Story 
		// 來判斷採用的過濾方法的物件是否為預期的
		assertEquals(10, filterStories.length);
		// 驗證取出為空狀態的 story
		assertNotNull(filterStories);
	}
	
	public void testgetPBFilterFilter_NULL_2() {
		AProductBacklogFilter filter = ProductBacklogFilterFactory.getInstance().getPBFilterFilter(null, this.data.getStorirs());
		IStory[] filterStories = filter.getStories();

		// 藉由驗證是否被此 Factory 建構出的物件給過濾 Story 
		// 來判斷採用的過濾方法的物件是否為預期的
		assertEquals(10, filterStories.length);
		// 驗證取出為空狀態的 story
		assertNotNull(filterStories);
	}
}
