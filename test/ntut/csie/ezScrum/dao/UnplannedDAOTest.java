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
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.UnplannedObject;
import ntut.csie.ezScrum.web.databasEnum.UnplannedEnum;
import ntut.csie.ezScrum.web.databaseEnum.IssuePartnerRelationEnum;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author AllenHuang 2015/08/21
 */

public class UnplannedDAOTest {
	private MySQLControl mControl = null;
	private Configuration mConfig;
	private CreateProject mCP;
	private CreateSprint mCS;
	private static int sProjectCount = 2;
	private static long sProjectId;
	private static long sSprintId;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCP = new CreateProject(sProjectCount);
		mCP.exeCreate();
		
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		mControl = new MySQLControl(mConfig);
		mControl.connect();

		sProjectId = mCP.getAllProjects().get(0).getId();
		sSprintId = mCS.getSprints().get(0).getId();
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
		mCS = null;
		mConfig = null;
		mControl = null;
	}

	@Test
	public void testCreate() throws SQLException {
		// create three test data
		for (int i = 0; i < 3; i++) {
			UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
			unplanned.setName("TEST_UNPLANNED_" + i + 1)
			        .setNotes("TEST_NOTE_" + i + 1)
			        .setEstimate(i * 2)
			        .setActual(i * 2);
			long unplannedId = UnplannedDAO.getInstance().create(unplanned);
			assertNotSame(-1, unplannedId);
		}

		// 從 DB 裡取出 unplanned 資料
		ArrayList<UnplannedObject> unplanneds = new ArrayList<UnplannedObject>();
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(UnplannedEnum.TABLE_NAME);
		valueSet.addEqualCondition(UnplannedEnum.PROJECT_ID, sProjectId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		while (result.next()) {
			unplanneds.add(UnplannedDAO.convert(result));
		}
		closeResultSet(result);

		assertEquals(3, unplanneds.size());
		for (int i = 0; i < 3; i++) {
			assertEquals(i + 1, unplanneds.get(i).getId());
			assertEquals(i + 1, unplanneds.get(i).getSerialId());
			assertEquals("TEST_UNPLANNED_" + i + 1, unplanneds.get(i).getName());
			assertEquals("TEST_NOTE_" + i + 1, unplanneds.get(i).getNotes());
			assertEquals(sProjectId, unplanneds.get(i).getProjectId());
			assertEquals(sSprintId, unplanneds.get(i).getSprintId());
			assertEquals(i * 2, unplanneds.get(i).getEstimate());
			assertEquals(i * 2, unplanneds.get(i).getActual());
			assertNotNull(unplanneds.get(i).getCreateTime());
			assertNotNull(unplanneds.get(i).getUpdateTime());
		}
	}

	@Test
	public void testGet() throws SQLException {
		// create three test data
		for (int i = 0; i < 3; i++) {
			UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
			unplanned.setName("TEST_UNPLANNED_" + i + 1)
			        .setNotes("TEST_NOTE_" + i + 1)
			        .setEstimate(i * 2)
			        .setActual(i * 2);
			long unplannedId = UnplannedDAO.getInstance().create(unplanned);
			assertNotSame(-1, unplannedId);
		}

		// get task
		ArrayList<UnplannedObject> unplanneds = new ArrayList<UnplannedObject>();
		for (int i = 0; i < 3; i++) {
			unplanneds.add(UnplannedDAO.getInstance().get(i + 1));
		}
		assertEquals(3, unplanneds.size());

		for (int i = 0; i < 3; i++) {
			assertEquals(i + 1, unplanneds.get(i).getId());
			assertEquals(i + 1, unplanneds.get(i).getSerialId());
			assertEquals("TEST_UNPLANNED_" + i + 1, unplanneds.get(i).getName());
			assertEquals("TEST_NOTE_" + i + 1, unplanneds.get(i).getNotes());
			assertEquals(sProjectId, unplanneds.get(i).getProjectId());
			assertEquals(sSprintId, unplanneds.get(i).getSprintId());
			assertEquals(i * 2, unplanneds.get(i).getEstimate());
			assertEquals(i * 2, unplanneds.get(i).getActual());
			assertNotNull(unplanneds.get(i).getCreateTime());
			assertNotNull(unplanneds.get(i).getUpdateTime());
		}
	}

	@Test
	public void testUpdate() throws SQLException {
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_UNPLANNED_1")
		        .setNotes("TEST_NOTE_1")
		        .setEstimate(1)
		        .setActual(3);
		long unplannedId = UnplannedDAO.getInstance().create(unplanned);
		assertNotSame(-1, unplannedId);

		unplanned = UnplannedDAO.getInstance().get(unplannedId);
		unplanned.setName("崩潰惹")
		        .setNotes("含淚寫測試")
		        .setEstimate(8)
		        .setActual(8);
		boolean result = UnplannedDAO.getInstance().update(unplanned);
		assertEquals(true, result);

		UnplannedObject theUnplanned = UnplannedDAO.getInstance().get(unplannedId);
		assertEquals(theUnplanned.getId(), unplanned.getId());
		assertEquals(theUnplanned.getSerialId(), unplanned.getSerialId());
		assertEquals(theUnplanned.getName(), unplanned.getName());
		assertEquals(theUnplanned.getNotes(), unplanned.getNotes());
		assertEquals(theUnplanned.getProjectId(), unplanned.getProjectId());
		assertEquals(theUnplanned.getSprintId(), unplanned.getSprintId());
		assertEquals(theUnplanned.getEstimate(), unplanned.getEstimate());
		assertEquals(theUnplanned.getActual(), unplanned.getActual());
	}

	@Test
	public void testDelete() throws SQLException {
		// create three test data
		for (int i = 0; i < 3; i++) {
			UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
			unplanned.setName("TEST_UNPLANNED_" + i + 1)
		        .setNotes("TEST_NOTE_" + i + 1)
		        .setEstimate(i * 2)
		        .setActual(i * 2);
			long unplannedId = UnplannedDAO.getInstance().create(unplanned);
			assertNotSame(-1, unplannedId);
		}

		// get unplanneds
		ArrayList<UnplannedObject> unplanneds = new ArrayList<UnplannedObject>();
		for (int i = 0; i < 3; i++) {
			unplanneds.add(UnplannedDAO.getInstance().get(i + 1));
		}
		assertEquals(3, unplanneds.size());

		// delete unplanned #2
		boolean result = UnplannedDAO.getInstance().delete(unplanneds.get(1).getId());
		assertEquals(true, result);

		// reload unplanneds
		unplanneds.clear();
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(UnplannedEnum.TABLE_NAME);
		valueSet.addEqualCondition(UnplannedEnum.PROJECT_ID, sProjectId);
		String query = valueSet.getSelectQuery();
		ResultSet resultSet = mControl.executeQuery(query);
		while (resultSet.next()) {
			unplanneds.add(UnplannedDAO.convert(resultSet));
		}
		assertEquals(2, unplanneds.size());
		closeResultSet(resultSet);
	}

	@Test
	public void testGetUnplannedBySprintId() throws SQLException {
		// create three test data
		for (int i = 0; i < 3; i++) {
			UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
			unplanned.setName("TEST_UNPLANNED_" + i + 1)
		        .setNotes("TEST_NOTE_" + i + 1)
		        .setEstimate(i * 2)
		        .setActual(i * 2);
			if (i == 1) {
				unplanned.setSprintId(2);
			}
			long unplannedId = UnplannedDAO.getInstance().create(unplanned);
			assertNotSame(-1, unplannedId);
		}
		
		// get unplanneds by sprint id
		ArrayList<UnplannedObject> unplanneds = UnplannedDAO.getInstance()
				.getUnplannedBySprintId(1);
		assertEquals(2, unplanneds.size());
	}
	
	@Test
	public void testGetUnplannedsByProjectId() throws SQLException {
		// create three test data
		for (int i = 0; i < 3; i++) {
			UnplannedObject unplanned = null;
			if (i == 1) {
				unplanned = new UnplannedObject(sSprintId, 2);
			} else {
				unplanned = new UnplannedObject(sSprintId, sProjectId);
			}
			unplanned.setName("TEST_UNPLANNED_" + i + 1)
		        .setNotes("TEST_NOTE_" + i + 1)
		        .setEstimate(i * 2)
		        .setActual(i * 2);
			long unplannedId = UnplannedDAO.getInstance().create(unplanned);
			assertNotSame(-1, unplannedId);
		}
		
		// get project #1 unplanneds
		ArrayList<UnplannedObject> tasks = UnplannedDAO.getInstance().getUnplannedsByProjectId(sProjectId);
		assertEquals(2, tasks.size());
	}
	
	@Test
	public void testGetPartnersId_withOnePartner() {
		long TEST_UNPLANNED_ID = 3;
		long TEST_PARTNER_ID = 5;
		// before add partner testGetPartnersId
		ArrayList<Long> partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		assertEquals(0, partnersId.size());
		// create add partner query
		IQueryValueSet addPartnerValueSet = new MySQLQuerySet();
		addPartnerValueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		addPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_ID, TEST_UNPLANNED_ID);
		addPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ACCOUNT_ID,
				TEST_PARTNER_ID);
		addPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_UNPLANNED);
		String addPartnerQuery = addPartnerValueSet.getInsertQuery();
		// execute add partner query
		mControl.executeInsert(addPartnerQuery);
		// after add partner testGetPartnersId
		partnersId.clear();
		partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		assertEquals(1, partnersId.size());
		assertEquals(5, partnersId.get(0).longValue());
	}
	
	@Test
	public void testGetPartnersId_withTwoPartners() {
		long TEST_UNPLANNED_ID = 3;
		long TEST_FIRST_PARTNER_ID = 5;
		long TEST_SECOND_PARTNER_ID = 7;
		// before add partner testGetPartnersId
		ArrayList<Long> partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		assertEquals(0, partnersId.size());
		// create add first partner query
		IQueryValueSet addFirstPartnerValueSet = new MySQLQuerySet();
		addFirstPartnerValueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		addFirstPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_ID, TEST_UNPLANNED_ID);
		addFirstPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ACCOUNT_ID,
				TEST_FIRST_PARTNER_ID);
		addFirstPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_UNPLANNED);
		String addFirstPartnerQuery = addFirstPartnerValueSet.getInsertQuery();
		// create add first partner query
		IQueryValueSet addSecondPartnerValueSet = new MySQLQuerySet();
		addSecondPartnerValueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		addSecondPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_ID, TEST_UNPLANNED_ID);
		addSecondPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ACCOUNT_ID,
				TEST_SECOND_PARTNER_ID);
		addSecondPartnerValueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_UNPLANNED);
		String addSecondPartnerQuery = addSecondPartnerValueSet.getInsertQuery();
		// execute add partner query
		mControl.executeInsert(addFirstPartnerQuery);
		mControl.executeInsert(addSecondPartnerQuery);
		// after add partner testGetPartnersId
		partnersId.clear();
		partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		assertEquals(2, partnersId.size());
		assertEquals(5, partnersId.get(0).longValue());
		assertEquals(7, partnersId.get(1).longValue());
	}
	
	@Test
	public void testAddPartner() throws SQLException {
		long TEST_UNPLANNED_ID = 3;
		long TEST_PARTNER_ID = 5;
		// create get partners id query
		IQueryValueSet getPartnersIdValueSet = new MySQLQuerySet();
		getPartnersIdValueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		getPartnersIdValueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_ID,
				Long.toString(TEST_UNPLANNED_ID));
		getPartnersIdValueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_UNPLANNED);
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
		UnplannedDAO.getInstance().addPartner(TEST_UNPLANNED_ID, TEST_PARTNER_ID);
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
		long TEST_UNPLANNED_ID = 1;
		long TEST_PARTNER_ID = 2;
		
		// add a new issue partner relationship
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		valueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_ID, TEST_UNPLANNED_ID);
		valueSet.addInsertValue(IssuePartnerRelationEnum.ACCOUNT_ID,
				TEST_PARTNER_ID);
		valueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_UNPLANNED);
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
		UnplannedDAO.getInstance().removePartner(TEST_UNPLANNED_ID, TEST_PARTNER_ID);
		
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
		long TEST_UNPLANNED_ID = 1;
		long TEST_PARTNER_ID = 2;
		
		// assert does relation exist, should be FALSE
		boolean exists = UnplannedDAO.getInstance().partnerExists(TEST_UNPLANNED_ID, TEST_PARTNER_ID);
		assertFalse(exists);
		
		// add a new issue partner relationship
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		valueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_ID, TEST_UNPLANNED_ID);
		valueSet.addInsertValue(IssuePartnerRelationEnum.ACCOUNT_ID,
				TEST_PARTNER_ID);
		valueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_UNPLANNED);
		String query = valueSet.getInsertQuery();
		mControl.executeInsert(query);
		
		// assert again, should be TURE
		exists = UnplannedDAO.getInstance().partnerExists(TEST_UNPLANNED_ID, TEST_PARTNER_ID);
		assertTrue(exists);
	}
	
	@Test
	public void testConvert() throws SQLException {
		String TEST_NAME = "TEST_NAME";
		String TEST_NOTES = "TEST_NOTES";
		long TEST_SERIAL_NUMBER = 99;
		long TEST_ESTIMATE = 0;
		long TEST_ACTUAL = 3;
		long TEST_STATUS = 1;
		long TEST_HANDLER = 5;
		long TEST_PROJECT_ID = 4;
		long TEST_SPRINT_ID= 5;
		long TEST_CREATE_TIME = System.currentTimeMillis();
		
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(UnplannedEnum.TABLE_NAME);
		valueSet.addInsertValue(UnplannedEnum.SERIAL_ID, TEST_SERIAL_NUMBER);
		valueSet.addInsertValue(UnplannedEnum.NAME, TEST_NAME);
		valueSet.addInsertValue(UnplannedEnum.HANDLER_ID, TEST_HANDLER);
		valueSet.addInsertValue(UnplannedEnum.ESTIMATE, TEST_ESTIMATE);
		valueSet.addInsertValue(UnplannedEnum.ACTUAL, TEST_ACTUAL);
		valueSet.addInsertValue(UnplannedEnum.NOTES, TEST_NOTES);
		valueSet.addInsertValue(UnplannedEnum.STATUS, TEST_STATUS);
		valueSet.addInsertValue(UnplannedEnum.PROJECT_ID, TEST_PROJECT_ID);
		valueSet.addInsertValue(UnplannedEnum.SPRINT_ID, TEST_SPRINT_ID);
		valueSet.addInsertValue(UnplannedEnum.CREATE_TIME, TEST_CREATE_TIME);
		valueSet.addInsertValue(UnplannedEnum.UPDATE_TIME, TEST_CREATE_TIME);
		String query = valueSet.getInsertQuery();
		
		long id = mControl.executeInsert(query);
		
		valueSet.clear();
		valueSet.addTableName(UnplannedEnum.TABLE_NAME);
		valueSet.addEqualCondition(UnplannedEnum.ID, id);
		query = valueSet.getSelectQuery();
		
		ResultSet result= mControl.executeQuery(query);
		result.next();
		UnplannedObject actual = UnplannedDAO.convert(result);
		closeResultSet(result);
		
		assertEquals(id, actual.getId());
		assertEquals(TEST_SERIAL_NUMBER, actual.getSerialId());
		assertEquals(TEST_NAME, actual.getName());
		assertEquals(TEST_NOTES, actual.getNotes());
		assertEquals(TEST_PROJECT_ID, actual.getProjectId());
		assertEquals(TEST_SPRINT_ID, actual.getSprintId());
		assertEquals(TEST_ESTIMATE, actual.getEstimate());
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
