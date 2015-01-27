package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ntut.csie.ezScrum.dao.AccountDAO;
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
		long TEST_TASK_ID = 1;
		// create a task
		TaskObject task = new TaskObject(TEST_TASK_ID);
		task.setName("TEST_NAME").setEstimate(10)
				.setRemains(8).setActual(0);
		task.save();
		// before testSetPartnersId
		List<Long> partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(0, partnersId.size());
		// set one partner
		ArrayList<Long> testPartnersId = new ArrayList<Long>();
		testPartnersId.add(1L);
		task.setPartnersId(testPartnersId);
		// testSetPartnersId_withOnePartner
		partnersId.clear();
		partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
	}
	
	@Test
	public void testSetPartnersId_withTwoPartners() {
		long TEST_TASK_ID = 1;
		// create a task
		TaskObject task = new TaskObject(TEST_TASK_ID);
		task.setName("TEST_NAME").setEstimate(10)
				.setRemains(8).setActual(0);
		task.save();
		// before testSetPartnersId
		List<Long> partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(0, partnersId.size());
		// set two partners
		ArrayList<Long> testPartnersId = new ArrayList<Long>();
		testPartnersId.add(1L);
		testPartnersId.add(2L);
		task.setPartnersId(testPartnersId);
		// testSetPartnersId_withTwoPartners
		partnersId.clear();
		partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(2, partnersId.size());
		assertEquals(1L, partnersId.get(0));
		assertEquals(2L, partnersId.get(1));
	}
	
	@Test
	public void testSetPartnersId_withTwoPartnersAddOneAndRemoveOnePartner() {
		long TEST_TASK_ID = 1;
		// create a task
		TaskObject task = new TaskObject(TEST_TASK_ID);
		task.setName("TEST_NAME").setEstimate(10)
				.setRemains(8).setActual(0);
		task.save();
		// set two partners
		ArrayList<Long> oldPartnersId = new ArrayList<Long>();
		oldPartnersId.add(1L);
		oldPartnersId.add(2L);
		task.setPartnersId(oldPartnersId);
		// testSetPartnersId_withTwoPartnersAddOneAndRemoveOnePartner
		ArrayList<Long> newPartnersId = new ArrayList<Long>();
		newPartnersId.add(2L);
		newPartnersId.add(3L);
		task.setPartnersId(newPartnersId);
		List<Long> partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		partnersId.clear();
		partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(2, partnersId.size());
		assertEquals(2L, partnersId.get(0));
		assertEquals(3L, partnersId.get(1));
	}
	
	@Test
	public void testAddPartner() {
		long TEST_TASK_ID = 1;
		// create a task
		TaskObject task = new TaskObject(TEST_TASK_ID);
		task.setName("TEST_NAME").setEstimate(10)
				.setRemains(8).setActual(0);
		task.save();
		// check status before add partner
		List<Long> partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(0, partnersId.size());
		// testAddPartner
		task.addPartner(1);
		partnersId.clear();
		partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
	}
	
	@Test
	public void testAddPartner_withExistPartner() {
		long TEST_TASK_ID = 1;
		// create a task
		TaskObject task = new TaskObject(TEST_TASK_ID);
		task.setName("TEST_NAME").setEstimate(10)
				.setRemains(8).setActual(0);
		task.save();
		task.addPartner(1);
		// check status before test
		List<Long> partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
		// testAddPartner_withExistPartner
		task.addPartner(1);
		partnersId.clear();
		partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
	}
	
	@Test
	public void testAddAttachFile() {
	}
	
	@Test
	public void testGetHandler() {
	}
	
	@Test
	public void testGetPartnersId() {
		long TEST_TASK_ID = 1;
		// create a task
		TaskObject task = new TaskObject(TEST_TASK_ID);
		task.setName("TEST_NAME").setEstimate(10)
				.setRemains(8).setActual(0);
		task.save();
		// check status before get partners id
		List<Long> partnersId = task.getPartnersId();
		assertEquals(0, partnersId.size());
		// add a partner
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, 1);
		// testGetPartnersId
		partnersId.clear();
		partnersId = task.getPartnersId();
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
	}
	
	@Test
	public void testGetPartnersId_withTwoPartners() {
		long TEST_TASK_ID = 1;
		// create a task
		TaskObject task = new TaskObject(TEST_TASK_ID);
		task.setName("TEST_NAME").setEstimate(10)
				.setRemains(8).setActual(0);
		task.save();
		// check status before get partners id
		List<Long> partnersId = task.getPartnersId();
		assertEquals(0, partnersId.size());
		// add two partners
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, 1);
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, 2);
		// testGetPartnersId_withTwoPartners
		partnersId.clear();
		partnersId = task.getPartnersId();
		assertEquals(2, partnersId.size());
		assertEquals(1L, partnersId.get(0));
		assertEquals(2L, partnersId.get(1));
	}
	
	@Test
	public void testGetPartners() {
		long TEST_TASK_ID = 1;
		String USERNAME = "test_username";
		String PASSWORD = "test_password";
		boolean ENABLE = true;
		// create a task
		TaskObject task = new TaskObject(TEST_TASK_ID);
		task.setName("TEST_NAME").setEstimate(10)
				.setRemains(8).setActual(0);
		task.save();
		// before add a partner
		assertEquals(0, task.getPartners().size());
		// create a account
		AccountObject account = new AccountObject(USERNAME);
		account.setPassword(PASSWORD).setEnable(ENABLE);
		account.save();
		// add partner
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, account.getId());
		// testGetPartners
		assertEquals(1, task.getPartners().size());
		assertEquals(account.getId(), task.getPartners().get(0).getId());
	}
	
	@Test
	public void testGetPartners_withTwoPartners() {
		long TEST_TASK_ID = 1;
		String FIRST_USERNAME = "test_first_username";
		String SECOND_USERNAME = "test_second_username";
		String PASSWORD = "test_password";
		boolean ENABLE = true;
		// create a task
		TaskObject task = new TaskObject(TEST_TASK_ID);
		task.setName("TEST_NAME").setEstimate(10)
				.setRemains(8).setActual(0);
		task.save();
		// before add partners
		assertEquals(0, task.getPartners().size());
		// create first account
		AccountObject firstAccount = new AccountObject(FIRST_USERNAME);
		firstAccount.setPassword(PASSWORD).setEnable(ENABLE);
		firstAccount.save();
		// create first account
		AccountObject secondAccount = new AccountObject(SECOND_USERNAME);
		secondAccount.setPassword(PASSWORD).setEnable(ENABLE);
		secondAccount.save();
		// add two partners
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, firstAccount.getId());
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, secondAccount.getId());
		// testGetPartners
		assertEquals(2, task.getPartners().size());
		assertEquals(firstAccount.getId(), task.getPartners().get(0).getId());
		assertEquals(secondAccount.getId(), task.getPartners().get(1).getId());
	}
	
	@Test
	public void testGetPartnersUsername() {
		long TEST_TASK_ID = 1;
		String USERNAME = "test_username";
		String PASSWORD = "test_password";
		boolean ENABLE = true;
		// create a task
		TaskObject task = new TaskObject(TEST_TASK_ID);
		task.setName("TEST_NAME").setEstimate(10)
				.setRemains(8).setActual(0);
		task.save();
		// before add a partner get partners username
		assertEquals("", task.getPartnersUsername());
		// create a account
		AccountObject account = new AccountObject(USERNAME);
		account.setPassword(PASSWORD).setEnable(ENABLE);
		account.save();
		// add partner
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, account.getId());
		// testGetPartnersUsername
		assertEquals("test_username", task.getPartnersUsername());
	}
	
	@Test
	public void testGetPartnersUsername_withTwoPartners() {
		long TEST_TASK_ID = 1;
		String FIRST_USERNAME = "test_first_username";
		String SECOND_USERNAME = "test_second_username";
		String PASSWORD = "test_password";
		boolean ENABLE = true;
		// create a task
		TaskObject task = new TaskObject(TEST_TASK_ID);
		task.setName("TEST_NAME").setEstimate(10)
				.setRemains(8).setActual(0);
		task.save();
		// before add partners
		assertEquals("", task.getPartnersUsername());
		// create first account
		AccountObject firstAccount = new AccountObject(FIRST_USERNAME);
		firstAccount.setPassword(PASSWORD).setEnable(ENABLE);
		firstAccount.save();
		// create first account
		AccountObject secondAccount = new AccountObject(SECOND_USERNAME);
		secondAccount.setPassword(PASSWORD).setEnable(ENABLE);
		secondAccount.save();
		// add two partners
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, firstAccount.getId());
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, secondAccount.getId());
		// testGetPartnersUsername_withTwoPartners
		assertEquals("test_first_username;test_second_username", task.getPartnersUsername());
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
