package ntut.csie.ezScrum.iteration.support.filter;

import junit.framework.TestCase;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;

public class IssueNameFilterTest extends TestCase {
	
	private StoryDataForFilter data = null;
	private String compareInfo = "IssueNameTest";
	
	public IssueNameFilterTest(String testMethod) {
        super(testMethod);
    }

	protected void setUp() {
		this.data = new StoryDataForFilter(this.compareInfo);
	}
	
	protected void tearDown() {
		this.data = null;
	}
	
	public void testFilterStories() {
		AProductBacklogFilter filter = new IssueNameFilter(this.data.getStorirs_byInfo(), this.compareInfo);
		IStory[] filterStories = filter.getStories();

		assertEquals(5, filterStories.length);
		for (IStory s : filterStories) {
			assertTrue(s.getName().contains(this.compareInfo));
		}
		
		assertNull(filter.getTasks());
	}
	
	public void testFilterTasks() {
		AProductBacklogFilter filter = new IssueNameFilter(this.data.getTasks_byInfo(), this.compareInfo);
		ITask[] filterTasks = filter.getTasks();

		assertEquals(5, filterTasks.length);
		for (ITask t : filterTasks) {
			assertTrue(t.getSummary().contains(this.compareInfo));
		}
		
		assertNull(filter.getStories());
	}
}
