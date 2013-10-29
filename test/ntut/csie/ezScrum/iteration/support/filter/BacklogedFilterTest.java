package ntut.csie.ezScrum.iteration.support.filter;

import junit.framework.TestCase;
import ntut.csie.ezScrum.iteration.core.IStory;

public class BacklogedFilterTest extends TestCase {
	
	private StoryDataForFilter data = null;
	
	public BacklogedFilterTest(String testMethod) {
        super(testMethod);
    }

	protected void setUp() {
		this.data = new StoryDataForFilter();
	}
	
	protected void tearDown() {
		this.data = null;
	}
	
	public void testFilterStories() {
		AProductBacklogFilter filter = new BacklogedFilter(this.data.getStorirs());
		IStory[] filterStories = filter.getStories();

		assertEquals(5, filterStories.length);
		for (IStory s : filterStories) {
			assertTrue( Integer.parseInt(s.getEstimated()) == 0 ||
					    Integer.parseInt(s.getValue()) == 0     ||
					    Integer.parseInt(s.getImportance()) == 0 );
		}
	}
}
