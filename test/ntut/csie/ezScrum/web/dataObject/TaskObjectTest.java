package ntut.csie.ezScrum.web.dataObject;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;

public class TaskObjectTest extends TestCase{
	
	private Configuration mConfig = null;
	private CreateProject CP = null;
	
	private final static int PROJECT_COUNT = 1;
	
	@Override
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.store();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		CP = new CreateProject(PROJECT_COUNT);
		CP.exeCreate();
		
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		mConfig = null;
		super.tearDown();
	}

	public void testSave() {
		TaskObject task = new TaskObject(1);
		task.setName("TEST_NAME")
			.setNotes("TEST_NOTES")
			.setEstimate(10)
			.setRemains(8)
			.setActual(0);
		
		task.save();
		
		assertEquals(1, task.getId());
		assertEquals(1, task.getSerialId());
		assertEquals("TEST_NAME", task.getName());
		assertEquals("TEST_NOTES", task.getNotes());
		assertEquals(10, task.getEstimate());
		assertEquals(8, task.getRemains());
		assertEquals(0, task.getActual());
	}
}
