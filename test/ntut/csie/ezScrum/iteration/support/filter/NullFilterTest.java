package ntut.csie.ezScrum.iteration.support.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import ntut.csie.ezScrum.iteration.core.IStory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NullFilterTest {
	
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
	public void testFilterStories() {
		AProductBacklogFilter filter = new NullFilter(mData.getStorirs());
		IStory[] filterStories = filter.getStories();
		assertEquals(10, filterStories.length);
		assertNotNull(filterStories);
	}
}
