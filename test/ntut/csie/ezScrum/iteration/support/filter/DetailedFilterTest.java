package ntut.csie.ezScrum.iteration.support.filter;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.IStory;

public class DetailedFilterTest extends TestCase {
	
	private StoryDataForFilter data = null;
	
	public DetailedFilterTest(String testMethod) {
        super(testMethod);
    }

	protected void setUp() {
		this.data = new StoryDataForFilter();
	}
	
	protected void tearDown() {
		this.data = null;
	}
	
	public void testFilterStories() {
		AProductBacklogFilter filter = new DetailedFilter(this.data.getStorirs());
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
