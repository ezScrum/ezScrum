package ntut.csie.ezScrum.iteration.support.filter;

import junit.framework.TestCase;
import ntut.csie.ezScrum.iteration.core.IStory;

public class NullFilterTest extends TestCase {
	
	private StoryDataForFilter data = null;
	
	public NullFilterTest(String testMethod) {
        super(testMethod);
    }

	protected void setUp() {
		this.data = new StoryDataForFilter();
	}
	
	protected void tearDown() {
		this.data = null;
	}
	
	public void testFilterStories() {
		AProductBacklogFilter filter = new NullFilter(this.data.getStorirs());
		IStory[] filterStories = filter.getStories();

		assertEquals(10, filterStories.length);
		assertNotNull(filterStories);
	}
}
