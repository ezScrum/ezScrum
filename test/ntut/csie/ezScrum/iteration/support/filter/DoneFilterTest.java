package ntut.csie.ezScrum.iteration.support.filter;

import static org.junit.Assert.assertEquals;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.IStory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DoneFilterTest {

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
		AProductBacklogFilter filter = new DoneFilter(mData.getStorirs());
		IStory[] filterStories = filter.getStories();

		assertEquals(3, filterStories.length);
		for (IStory s : filterStories) {
			assertEquals("100", s.getValue());
			assertEquals("100", s.getImportance());
			assertEquals("5", s.getEstimated());
			assertEquals(ITSEnum.S_CLOSED_STATUS, s.getStatus());
		}
	}
}
