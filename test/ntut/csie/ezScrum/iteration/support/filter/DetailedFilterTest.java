package ntut.csie.ezScrum.iteration.support.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

public class DetailedFilterTest {
	private StoryDataForFilter mData = null;
	private CreateProject mCP;
	private Configuration mConfig;
	
	@Before
	public void setUp() {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mCP = new CreateProject(1);
		mCP.exeCreateForDb();
		
		mData = new StoryDataForFilter();
	}
	
	@After
	public void tearDown() {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
		
		mCP = null;
		mData = null;
	}
	
	@Test
	public void testFilterStories() {
		AProductBacklogFilter filter = new DetailedFilter(mData.getStorirs());
		ArrayList<StoryObject> filterStories = filter.getStories();

		assertEquals(2, filterStories.size());
		for (StoryObject s : filterStories) {
			assertTrue(s.getValue() > 0);
			assertTrue(s.getImportance() > 0);
			assertTrue(s.getEstimate() > 0);
			assertEquals(StoryObject.STATUS_UNCHECK, s.getStatus());
		}
	}
}
