package ntut.csie.ezScrum.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.databaseEnum.IssuePartnerRelationEnum;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.databaseEnum.TaskEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaskDAOTest {
	private MySQLControl mControl = null;
	private Configuration mConfig;
	private CreateProject mCP;
	private int mProjectCount = 2;
	private static long sProjectId;


	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		mControl = new MySQLControl(mConfig);
		mControl.connect();

		sProjectId = mCP.getAllProjects().get(0).getId();
	}

	@After
	public void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mCP = null;
		mConfig = null;
		mControl = null;
	}

	@Test
	public void testCreate() throws SQLException {
		// create three test data
		for (int i = 0; i < 3; i++) {
			TaskObject task = new TaskObject(sProjectId);
			task.setName("TEST_TASK_" + i + 1)
			        .setNotes("TEST_NOTE_" + i + 1)
			        .setEstimate(i * 2)
			        .setRemains(i * 2)
			        .setActual(i * 2);
			long taskId = TaskDAO.getInstance().create(task);
			assertNotSame(-1, taskId);
		}

		// 從 DB 裡取出 task 資料
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TaskEnum.TABLE_NAME);
		valueSet.addEqualCondition(TaskEnum.PROJECT_ID, sProjectId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		while (result.next()) {
			tasks.add(TaskDAO.convert(result));
		}
		closeResultSet(result);

		assertEquals(3, tasks.size());
		for (int i = 0; i < 3; i++) {
			assertEquals(i + 1, tasks.get(i).getId());
			assertEquals(i + 1, tasks.get(i).getSerialId());
			assertEquals("TEST_TASK_" + i + 1, tasks.get(i).getName());
			assertEquals("TEST_NOTE_" + i + 1, tasks.get(i).getNotes());
			assertEquals(sProjectId, tasks.get(i).getProjectId());
			assertEquals(-1, tasks.get(i).getStoryId());
			assertEquals(i * 2, tasks.get(i).getEstimate());
			assertEquals(i * 2, tasks.get(i).getRemains());
			assertEquals(i * 2, tasks.get(i).getActual());
			assertNotNull(tasks.get(i).getCreateTime());
			assertNotNull(tasks.get(i).getUpdateTime());
		}
	}

	@Test
	public void testGet() throws SQLException {
		// create three test data
		for (int i = 0; i < 3; i++) {
			TaskObject task = new TaskObject(sProjectId);
			task.setName("TEST_TASK_" + i + 1)
			        .setNotes("TEST_NOTE_" + i + 1)
			        .setEstimate(i * 2)
			        .setRemains(i * 2)
			        .setActual(i * 2);
			long taskId = TaskDAO.getInstance().create(task);
			assertNotSame(-1, taskId);
		}

		// get task
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		for (int i = 0; i < 3; i++) {
			tasks.add(TaskDAO.getInstance().get(i + 1));
		}
		assertEquals(3, tasks.size());

		for (int i = 0; i < 3; i++) {
			assertEquals(i + 1, tasks.get(i).getId());
			assertEquals(i + 1, tasks.get(i).getSerialId());
			assertEquals("TEST_TASK_" + i + 1, tasks.get(i).getName());
			assertEquals("TEST_NOTE_" + i + 1, tasks.get(i).getNotes());
			assertEquals(sProjectId, tasks.get(i).getProjectId());
			assertEquals(-1, tasks.get(i).getStoryId());
			assertEquals(i * 2, tasks.get(i).getEstimate());
			assertEquals(i * 2, tasks.get(i).getRemains());
			assertEquals(i * 2, tasks.get(i).getActual());
			assertNotNull(tasks.get(i).getCreateTime());
			assertNotNull(tasks.get(i).getUpdateTime());
		}
	}

	@Test
	public void testUpdate() throws SQLException {
		TaskObject task = new TaskObject(sProjectId);
		task.setName("TEST_TASK_1")
		        .setNotes("TEST_NOTE_1")
		        .setEstimate(1)
		        .setRemains(2)
		        .setActual(3);
		long taskId = TaskDAO.getInstance().create(task);
		assertNotSame(-1, taskId);

		task = TaskDAO.getInstance().get(taskId);
		task.setName("崩潰惹")
		        .setNotes("含淚寫測試")
		        .setEstimate(8)
		        .setRemains(8)
		        .setActual(8);
		boolean result = TaskDAO.getInstance().update(task);
		assertEquals(true, result);

		TaskObject theTask = TaskDAO.getInstance().get(taskId);
		assertEquals(theTask.getId(), task.getId());
		assertEquals(theTask.getSerialId(), task.getSerialId());
		assertEquals(theTask.getName(), task.getName());
		assertEquals(theTask.getNotes(), task.getNotes());
		assertEquals(theTask.getProjectId(), task.getProjectId());
		assertEquals(theTask.getStoryId(), task.getStoryId());
		assertEquals(theTask.getEstimate(), task.getEstimate());
		assertEquals(theTask.getRemains(), task.getRemains());
		assertEquals(theTask.getActual(), task.getActual());
	}

	@Test
	public void testDelete() throws SQLException {
		// create three test data
		for (int i = 0; i < 3; i++) {
			TaskObject task = new TaskObject(sProjectId);
			task.setName("TEST_TASK_" + i + 1)
		        .setNotes("TEST_NOTE_" + i + 1)
		        .setEstimate(i * 2)
		        .setRemains(i * 2)
		        .setActual(i * 2);
			long taskId = TaskDAO.getInstance().create(task);
			assertNotSame(-1, taskId);
		}

		// get tasks
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		for (int i = 0; i < 3; i++) {
			tasks.add(TaskDAO.getInstance().get(i + 1));
		}
		assertEquals(3, tasks.size());

		// delete task #2
		boolean result = TaskDAO.getInstance().delete(tasks.get(1).getId());
		assertEquals(true, result);

		// reload tasks
		tasks.clear();
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TaskEnum.TABLE_NAME);
		valueSet.addEqualCondition(TaskEnum.PROJECT_ID, sProjectId);
		String query = valueSet.getSelectQuery();
		ResultSet resultSet = mControl.executeQuery(query);
		while (resultSet.next()) {
			tasks.add(TaskDAO.convert(resultSet));
		}
		assertEquals(2, tasks.size());
		closeResultSet(resultSet);
	}

	@Test
	public void testGetTasksByStory() throws SQLException {
		// create three test data
		for (int i = 0; i < 3; i++) {
			TaskObject task = new TaskObject(sProjectId);
			task.setName("TEST_TASK_" + i + 1)
		        .setNotes("TEST_NOTE_" + i + 1)
		        .setEstimate(i * 2)
		        .setRemains(i * 2)
		        .setActual(i * 2);
			if (i == 1) {
				task.setStoryId(2);
			} else {
				task.setStoryId(1);
			}
			long taskId = TaskDAO.getInstance().create(task);
			assertNotSame(-1, taskId);
		}
		
		// get tasks by story id
		ArrayList<TaskObject> tasks = TaskDAO.getInstance().getTasksByStoryId(1);
		assertEquals(2, tasks.size());
	}
	
	@Test
	public void testGetTasksWithNoParent() throws SQLException {
		// create three test data
		for (int i = 0; i < 3; i++) {
			TaskObject task = new TaskObject(sProjectId);
			task.setName("TEST_TASK_" + i + 1)
		        .setNotes("TEST_NOTE_" + i + 1)
		        .setEstimate(i * 2)
		        .setRemains(i * 2)
		        .setActual(i * 2);
			if (i == 1) {
				task.setStoryId(2);
			}
			long taskId = TaskDAO.getInstance().create(task);
			assertNotSame(-1, taskId);
		}
		
		// get wild tasks
		ArrayList<TaskObject> tasks = TaskDAO.getInstance().getTasksWithNoParent(sProjectId);
		assertEquals(2, tasks.size());
	}
	
	@Test
	public void testGetPartnersId_withOnePartner() {
		long TEST_TASK_ID = 3;
		long TEST_PARTNER_ID = 5;
		// before add partner testGetPartnersId
		ArrayList<Long> partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(0, partnersId.size());
		// create add partner query
		IQueryValueSet addPartnerValueSet = new MySQLQuerySet();
		addPartnerValueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		addPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_ID, TEST_TASK_ID);
		addPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ACCOUNT_ID,
				TEST_PARTNER_ID);
		addPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_TASK);
		String addPartnerQuery = addPartnerValueSet.getInsertQuery();
		// execute add partner query
		mControl.executeInsert(addPartnerQuery);
		// after add partner testGetPartnersId
		partnersId.clear();
		partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(1, partnersId.size());
		assertEquals(5, partnersId.get(0).longValue());
	}
	
	@Test
	public void testGetPartnersId_withTwoPartners() {
		long TEST_TASK_ID = 3;
		long TEST_FIRST_PARTNER_ID = 5;
		long TEST_SECOND_PARTNER_ID = 7;
		// before add partner testGetPartnersId
		ArrayList<Long> partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(0, partnersId.size());
		// create add first partner query
		IQueryValueSet addFirstPartnerValueSet = new MySQLQuerySet();
		addFirstPartnerValueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		addFirstPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_ID, TEST_TASK_ID);
		addFirstPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ACCOUNT_ID,
				TEST_FIRST_PARTNER_ID);
		addFirstPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_TASK);
		String addFirstPartnerQuery = addFirstPartnerValueSet.getInsertQuery();
		// create add first partner query
		IQueryValueSet addSecondPartnerValueSet = new MySQLQuerySet();
		addSecondPartnerValueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		addSecondPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_ID, TEST_TASK_ID);
		addSecondPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ACCOUNT_ID,
				TEST_SECOND_PARTNER_ID);
		addSecondPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_TASK);
		String addSecondPartnerQuery = addSecondPartnerValueSet.getInsertQuery();
		// execute add partner query
		mControl.executeInsert(addFirstPartnerQuery);
		mControl.executeInsert(addSecondPartnerQuery);
		// after add partner testGetPartnersId
		partnersId.clear();
		partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(2, partnersId.size());
		assertEquals(5, partnersId.get(0).longValue());
		assertEquals(7, partnersId.get(1).longValue());
	}
	
	@Test
	public void testAddPartner() throws SQLException {
		long TEST_TASK_ID = 3;
		long TEST_PARTNER_ID = 5;
		// create get partners id query
		IQueryValueSet getPartnersIdValueSet = new MySQLQuerySet();
		getPartnersIdValueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		getPartnersIdValueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_ID,
				Long.toString(TEST_TASK_ID));
		getPartnersIdValueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_TASK);
		String getPartnersIdQuery = getPartnersIdValueSet.getSelectQuery();
		ArrayList<Long> partnerIdList = new ArrayList<Long>();
		// execute get partners id query
		ResultSet result = mControl.executeQuery(getPartnersIdQuery);
		while (result.next()) {
			partnerIdList.add(result.getLong(IssuePartnerRelationEnum.ACCOUNT_ID));
		}
		closeResultSet(result);
		
		// before add partner
		assertEquals(0, partnerIdList.size());
		// add partner
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, TEST_PARTNER_ID);
		// execute get partners id query
		result = mControl.executeQuery(getPartnersIdQuery);
		try {
			while (result.next()) {
				partnerIdList.add(result
						.getLong(IssuePartnerRelationEnum.ACCOUNT_ID));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		// after add partner
		assertEquals(1, partnerIdList.size());
		assertEquals(5, partnerIdList.get(0).longValue());
	}
	
	@Test
	public void testRemovePartner() throws SQLException {
		long TEST_TASK_ID = 1;
		long TEST_PARTNER_ID = 2;
		
		// add a new issue partner relationship
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		valueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_ID, TEST_TASK_ID);
		valueSet.addInsertValue(IssuePartnerRelationEnum.ACCOUNT_ID,
				TEST_PARTNER_ID);
		valueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_TASK);
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);
		
		// assert the record is inserted correctly
		valueSet.clear();
		valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ID, id);
		query = valueSet.getSelectQuery();
		
		ResultSet result = mControl.executeQuery(query);
		int size = 0;
		while (result.next()) {
			size++;
		}
		closeResultSet(result);
		assertEquals(1, size);
		
		// remove partner from relations
		TaskDAO.getInstance().removePartner(TEST_TASK_ID, TEST_PARTNER_ID);
		
		// assert again, the record should be removed 
		valueSet.clear();
		valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ID, id);
		query = valueSet.getSelectQuery();
		
		result = mControl.executeQuery(query);
		size = 0;
		while (result.next()) {
			size++;
		}
		closeResultSet(result);
		assertEquals(0, size);
	}

	@Test
	public void testPartnerExists() {
		long TEST_TASK_ID = 1;
		long TEST_PARTNER_ID = 2;
		
		// assert does relation exist, should be FALSE
		boolean exists = TaskDAO.getInstance().partnerExists(TEST_TASK_ID, TEST_PARTNER_ID);
		assertFalse(exists);
		
		// add a new issue partner relationship
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		valueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_ID, TEST_TASK_ID);
		valueSet.addInsertValue(IssuePartnerRelationEnum.ACCOUNT_ID,
				TEST_PARTNER_ID);
		valueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_TASK);
		String query = valueSet.getInsertQuery();
		mControl.executeInsert(query);
		
		// assert again, should be TURE
		exists = TaskDAO.getInstance().partnerExists(TEST_TASK_ID, TEST_PARTNER_ID);
		assertTrue(exists);
	}
	
	@Test
	public void testConvert() throws SQLException {
		String TEST_NAME = "TEST_NAME";
		String TEST_NOTES = "TEST_NOTES";
		long TEST_SERIAL_NUMBER = 99;
		long TEST_ESTIMATE = 0;
		long TEST_REMAINS = 1;
		long TEST_ACTUAL = 3;
		long TEST_STATUS = 1;
		long TEST_HANDLER = 5;
		long TEST_PROJECT_ID = 4;
		long TEST_STORY_ID = 5;
		long TEST_CREATE_TIME = System.currentTimeMillis();
		
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TaskEnum.TABLE_NAME);
		valueSet.addInsertValue(TaskEnum.SERIAL_ID, TEST_SERIAL_NUMBER);
		valueSet.addInsertValue(TaskEnum.NAME, TEST_NAME);
		valueSet.addInsertValue(TaskEnum.HANDLER_ID, TEST_HANDLER);
		valueSet.addInsertValue(TaskEnum.ESTIMATE, TEST_ESTIMATE);
		valueSet.addInsertValue(TaskEnum.REMAIN, TEST_REMAINS);
		valueSet.addInsertValue(TaskEnum.ACTUAL, TEST_ACTUAL);
		valueSet.addInsertValue(TaskEnum.NOTES, TEST_NOTES);
		valueSet.addInsertValue(TaskEnum.STATUS, TEST_STATUS);
		valueSet.addInsertValue(TaskEnum.PROJECT_ID, TEST_PROJECT_ID);
		valueSet.addInsertValue(TaskEnum.STORY_ID, TEST_STORY_ID);
		valueSet.addInsertValue(TaskEnum.CREATE_TIME, TEST_CREATE_TIME);
		valueSet.addInsertValue(TaskEnum.UPDATE_TIME, TEST_CREATE_TIME);
		String query = valueSet.getInsertQuery();
		
		long id = mControl.executeInsert(query);
		
		valueSet.clear();
		valueSet.addTableName(TaskEnum.TABLE_NAME);
		valueSet.addEqualCondition(TaskEnum.ID, id);
		query = valueSet.getSelectQuery();
		
		ResultSet result= mControl.executeQuery(query);
		result.next();
		TaskObject actual = TaskDAO.convert(result);
		closeResultSet(result);
		
		assertEquals(id, actual.getId());
		assertEquals(TEST_SERIAL_NUMBER, actual.getSerialId());
		assertEquals(TEST_NAME, actual.getName());
		assertEquals(TEST_NOTES, actual.getNotes());
		assertEquals(TEST_PROJECT_ID, actual.getProjectId());
		assertEquals(TEST_STORY_ID, actual.getStoryId());
		assertEquals(TEST_ESTIMATE, actual.getEstimate());
		assertEquals(TEST_REMAINS, actual.getRemains());
		assertEquals(TEST_ACTUAL, actual.getActual());
		assertEquals(TEST_CREATE_TIME, actual.getCreateTime());
		assertEquals(TEST_CREATE_TIME, actual.getUpdateTime());
	}

	private void closeResultSet(ResultSet result) {
		if (result != null) {
			try {
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}			
		}
	}
}
