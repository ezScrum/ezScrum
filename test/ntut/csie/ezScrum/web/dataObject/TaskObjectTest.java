package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.dao.TaskDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author samhuang 2014/12/23
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
		mConfig.save();

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
	
	@Test
	public void testSetPartnersId() {
	}
	
	@Test
	public void testAddPartner() {
	}
	
	@Test
	public void testGetHandler() {
	}
	
	@Test
	public void testGetPartnersId() {
	}
	
	@Test
	public void testGetPartners() {
	}
	
	@Test
	public void testGetPartnersName() {
	}
	
	@Test
	public void testGetHistories() {
	}
	
	@Test
	public void testGetAttachFiles() {
	}
	
	@Test
	public void testToJSON() {
	}

	/**
	 * 測試新增一個 task
	 */
	@Test
	public void testSave_createANewTask() {
		TaskObject task = new TaskObject(1);
		task.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
				.setRemains(8).setActual(0);

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
		task.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
				.setRemains(8).setActual(0);

		task.save();

		assertEquals(1, task.getId());
		assertEquals(1, task.getSerialId());
		assertEquals("TEST_NAME", task.getName());
		assertEquals("TEST_NOTES", task.getNotes());
		assertEquals(10, task.getEstimate());
		assertEquals(8, task.getRemains());
		assertEquals(0, task.getActual());

		task.setName("TEST_NAME2").setNotes("TEST_NOTES2").setEstimate(3)
				.setRemains(5).setActual(1);

		task.save();

		assertEquals(1, task.getId());
		assertEquals(1, task.getSerialId());
		assertEquals("TEST_NAME2", task.getName());
		assertEquals("TEST_NOTES2", task.getNotes());
		assertEquals(3, task.getEstimate());
		assertEquals(5, task.getRemains());
		assertEquals(1, task.getActual());
	}

	@Test
	public void testDelete() throws SQLException {
		TaskObject task = new TaskObject(1);
		task.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
				.setRemains(8).setActual(0);

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
		assertEquals(null, TaskDAO.getInstance().get(1));
	}

	@Test
	public void testReload() {
		TaskObject task = new TaskObject(1);
		task.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
				.setRemains(8).setActual(0);

		task.save();

		assertEquals(1, task.getId());
		assertEquals(1, task.getSerialId());
		assertEquals("TEST_NAME", task.getName());
		assertEquals("TEST_NOTES", task.getNotes());
		assertEquals(10, task.getEstimate());
		assertEquals(8, task.getRemains());
		assertEquals(0, task.getActual());

		task.setName("TEST_NAME2").setNotes("TEST_NOTES2").setEstimate(5)
				.setRemains(3).setActual(1);

		assertEquals(1, task.getId());
		assertEquals(1, task.getSerialId());
		assertEquals("TEST_NAME2", task.getName());
		assertEquals("TEST_NOTES2", task.getNotes());
		assertEquals(5, task.getEstimate());
		assertEquals(3, task.getRemains());
		assertEquals(1, task.getActual());

		try {
			task.reload();

			assertEquals(1, task.getId());
			assertEquals(1, task.getSerialId());
			assertEquals("TEST_NAME", task.getName());
			assertEquals("TEST_NOTES", task.getNotes());
			assertEquals(10, task.getEstimate());
			assertEquals(8, task.getRemains());
			assertEquals(0, task.getActual());
		} catch (Exception e) {
		}
	}

	@Test
	public void testGetTasksByStory() throws SQLException {
		long storyId = 1;
		// 新增三筆 task 但有兩筆在 story 下
		for (int i = 1; i <= 3; i++) {
			TaskObject task = new TaskObject(1);
			task.setName("TEST_NAME_" + i).setNotes("TEST_NOTES_" + i)
					.setEstimate(10).setRemains(8).setActual(0);
			if (i != 2) {
				task.setStoryId(storyId);
			}
			task.save();
		}

		ArrayList<TaskObject> tasks = TaskObject.getTasksByStory(storyId);
		assertEquals(2, tasks.size());

		assertEquals("TEST_NAME_1", tasks.get(0).getName());
		assertEquals("TEST_NOTES_1", tasks.get(0).getNotes());
		assertEquals(10, tasks.get(0).getEstimate());
		assertEquals(8, tasks.get(0).getRemains());
		assertEquals(0, tasks.get(0).getActual());

		assertEquals("TEST_NAME_3", tasks.get(1).getName());
		assertEquals("TEST_NOTES_3", tasks.get(1).getNotes());
		assertEquals(10, tasks.get(1).getEstimate());
		assertEquals(8, tasks.get(1).getRemains());
		assertEquals(0, tasks.get(1).getActual());
	}
}
