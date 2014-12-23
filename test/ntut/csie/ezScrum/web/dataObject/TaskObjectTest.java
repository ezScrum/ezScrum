package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import ntut.csie.ezScrum.dao.TaskDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author samhuang
 * 2014/12/23
 *
 */
public class TaskObjectTest {
	
	private Configuration mConfig = null;
	private CreateProject CP = null;
	
	private final static int PROJECT_COUNT = 1;
	
	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.store();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		CP = new CreateProject(PROJECT_COUNT);
		CP.exeCreate();
	}
	
	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		mConfig = null;
	}

	/**
	 * 測試新增一個 task
	 */
	@Test
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
	@Test
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
	
	@Test(expected=SQLException.class)
	public void testDelete() throws SQLException {
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
		TaskDAO.getInstance().get(1);
	}
}
