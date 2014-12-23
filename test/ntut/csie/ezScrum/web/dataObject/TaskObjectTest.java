package ntut.csie.ezScrum.web.dataObject;

import java.sql.SQLException;

import junit.framework.TestCase;
import ntut.csie.ezScrum.dao.TaskDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;

/**
 * 
 * @author samhuang
 * 2014/12/23
 *
 */
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

	/**
	 * 測試新增一個 task
	 */
	public void testSave_newTask() {
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
	
	/**
	 * 測試一個已存在的 task
	 */
	public void testSave_updateTask() {
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
		
		task.setName("TEST_NAME2")
			.setNotes("TEST_NOTES2")
			.setEstimate(3)
			.setRemains(5)
			.setActual(1);
		
		task.save();
		
		assertEquals(1, task.getId());
		assertEquals(1, task.getSerialId());
		assertEquals("TEST_NAME2", task.getName());
		assertEquals("TEST_NOTES2", task.getNotes());
		assertEquals(3, task.getEstimate());
		assertEquals(5, task.getRemains());
		assertEquals(1, task.getActual());
	}
	
	public void testDelete() {
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
		
		boolean deleteSuccess = task.delete();
		
		assertTrue(deleteSuccess);
		assertEquals(-1, task.getId());
		assertEquals(-1, task.getSerialId());
		try {
			TaskDAO.getInstance().get(1);
			assertTrue(false);
		} catch (SQLException e) {
			assertTrue(true);
		}
	}
}
