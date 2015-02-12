package ntut.csie.ezScrum.iteration.support.filter;

import junit.framework.TestCase;
import ntut.csie.ezScrum.iteration.core.ITask;

public class TaskHandlerFilterTest extends TestCase {
	private StoryDataForFilter data = null;
	private String compareInfo = "TaskHandlerTest";
	
	public TaskHandlerFilterTest(String testMethod) {
        super(testMethod);
    }

	protected void setUp() {
		this.data = new StoryDataForFilter(this.compareInfo);
	}
	
	protected void tearDown() {
		this.data = null;
	}
	
	public void testFilterStories() {
		AProductBacklogFilter filter = new TaskHandlerFilter(this.data.getTasksByInfo(), this.compareInfo);
		assertNull(filter.getStories());
	}
	
	public void testFilterTasks_1() {
		AProductBacklogFilter filter = new TaskHandlerFilter(this.data.getTasksByInfo(), this.compareInfo);
		ITask[] filterTasks = filter.getTasks();

		assertEquals(5, filterTasks.length);
		// 驗證取出條件為 Handler 內容有 "TaskHandlerTest"
		int id = 4;
		for (ITask t : filterTasks) {
			assertTrue(t.getAssignto().equals(this.compareInfo));
			assertEquals(t.getIssueID(), id++);
		}
		
		assertNull(filter.getStories());
	}
	
	public void testFilterTasks_2() {
		AProductBacklogFilter filter = new TaskHandlerFilter(this.data.getTasksByInfo(), "ALL");
		ITask[] filterTasks = filter.getTasks();

		assertEquals(10, filterTasks.length);
		// 驗證取出條件為 Handler 內容有 "TaskHandlerTest"
		int id = 1;
		for (ITask t : filterTasks) {
			assertEquals(t.getIssueID(), id++);
		}
		
		assertNull(filter.getStories());
	}
}
