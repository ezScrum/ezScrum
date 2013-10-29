package ntut.csie.ezScrum.iteration.support.filter;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.IStory;

public class DoneFilterTest extends TestCase {
	
	private StoryDataForFilter data = null;
	
	public DoneFilterTest(String testMethod) {
        super(testMethod);
    }

	protected void setUp() {
		this.data = new StoryDataForFilter();
	}
	
	protected void tearDown() {
		this.data = null;
	}
	
	public void testFilterStories() {
		AProductBacklogFilter filter = new DoneFilter(this.data.getStorirs());
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
