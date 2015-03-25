package ntut.csie.ezScrum.iteration.support.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IssueDescFilterTest {
	
	private StoryDataForFilter mData = null;
	private String mCompareInfo = "IssueDescTest";
	
	@Before
	public void setUp() {
		mData = new StoryDataForFilter(mCompareInfo);
	}
	
	@After
	public void tearDown() {
		mData = null;
	}
	
	@Test
	public void testFilterStories() {
//		AProductBacklogFilter filter = new IssueDescFilter(mData.getStorirsByInfo(), this.mCompareInfo);
//		ArrayList<StoryObject> filterStories = filter.getStories();
//
//		assertEquals(5, filterStories.size());
//		for (StoryObject s : filterStories) {
//			assertTrue(s.getDescription().contains(mCompareInfo));
//		}
//		assertNull(filter.getTasks());
	}
	
	public void testFilterTasks() {
		AProductBacklogFilter filter = new IssueDescFilter(mData.getTasksByInfo(), mCompareInfo);
		ITask[] filterTasks = filter.getTasks();

		assertEquals(5, filterTasks.length);
		for (ITask t : filterTasks) {
			assertTrue(t.getDescription().contains(mCompareInfo));
		}
		assertNull(filter.getStories());
	}
}
