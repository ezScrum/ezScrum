package ntut.csie.ezScrum.iteration.support.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.IStory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DetailedFilterTest {
	
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
		AProductBacklogFilter filter = new DetailedFilter(mData.getStorirs());
		IStory[] filterStories = filter.getStories();

		assertEquals(2, filterStories.length);
		for (IStory s : filterStories) {
			assertTrue(Integer.parseInt(s.getValue()) > 0);
			assertTrue(Integer.parseInt(s.getImportance()) > 0);
			assertTrue(Integer.parseInt(s.getEstimated()) > 0);
			assertEquals(ITSEnum.S_NEW_STATUS, s.getStatus());
		}
	}
}
