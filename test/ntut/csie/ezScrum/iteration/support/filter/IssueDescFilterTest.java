package ntut.csie.ezScrum.iteration.support.filter;

import junit.framework.TestCase;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;

public class IssueDescFilterTest extends TestCase {
	
	private StoryDataForFilter data = null;
	private String compareInfo = "IssueDescTest";
	
	public IssueDescFilterTest(String testMethod) {
        super(testMethod);
    }

	protected void setUp() {
		this.data = new StoryDataForFilter(this.compareInfo);
	}
	
	protected void tearDown() {
		this.data = null;
	}
	
	public void testFilterStories() {
		AProductBacklogFilter filter = new IssueDescFilter(this.data.getStorirs_byInfo(), this.compareInfo);
		IStory[] filterStories = filter.getStories();

		assertEquals(5, filterStories.length);
		int id = 6;
		for (IStory s : filterStories) {
			assertTrue(s.getDescription().contains(this.compareInfo));
		}
		
		assertNull(filter.getTasks());
	}
	
	public void testFilterTasks() {
		AProductBacklogFilter filter = new IssueDescFilter(this.data.getTasks_byInfo(), this.compareInfo);
		ITask[] filterTasks = filter.getTasks();

		assertEquals(5, filterTasks.length);
		for (ITask t : filterTasks) {
			assertTrue(t.getDescription().contains(this.compareInfo));
		}
		
		assertNull(filter.getStories());
	}
}
