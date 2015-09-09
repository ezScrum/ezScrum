package ntut.csie.ezScrum.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
import ntut.csie.ezScrum.web.databaseEnum.RetrospectiveEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RetrospectiveDAOTest {
	private MySQLControl mControl = null;
	private Configuration mConfig;
	private CreateProject mCP;
	private ProjectObject mProject = null;
	private long mRtrospectiveId;
	private int mProjectCount = 1;

	@Before
	public void setUp() {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		mControl = new MySQLControl(mConfig);
		mControl.connect();

		mProject = mCP.getAllProjects().get(0);

		// create a retrospective
		mRtrospectiveId = createRetrospective();
	}

	@After
	public void tearDown() {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		mConfig.setTestMode(false);
		mConfig.save();

		// release
		ini = null;
		mCP = null;
		mProject = null;
		mConfig = null;
		mControl = null;
	}

	@Test
	public void testCreate() throws SQLException {
		// Test data
		String name = "TEST_RETROSPECTIVE_NAME";
		String description = "TEST_RETROSPECTIVE_DESCRIPTION";
		String type = RetrospectiveObject.TYPE_GOOD;
		String status = RetrospectiveObject.STATUS_NEW;
		long sprintId = 1;

		// Create Retrospective
		RetrospectiveObject retrospective = new RetrospectiveObject(mProject.getId());
		retrospective.setName(name).setDescription(description)
				     .setType(type).setStatus(status).setSprintId(sprintId);

		// Test create method
		long retrospectiveId = RetrospectiveDAO.getInstance().create(retrospective);
		assertTrue(retrospectiveId > 0);
		
		// Get Data From DB
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(RetrospectiveEnum.TABLE_NAME);
		valueSet.addEqualCondition(RetrospectiveEnum.ID, retrospectiveId);

		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);

		RetrospectiveObject retrospectiveFromDB = null;

		if (result.next()) {
			retrospectiveFromDB = RetrospectiveDAO.convert(result);
		}
		closeResultSet(result);

		// Verify Data
		assertEquals(name, retrospectiveFromDB.getName());
		assertEquals(description, retrospectiveFromDB.getDescription());
		assertEquals(type, retrospectiveFromDB.getType());
		assertEquals(status, retrospectiveFromDB.getStatus());
		assertEquals(sprintId, retrospectiveFromDB.getSprintId());
	}

	@Test
	public void testGet() {
		// get retrospective
		RetrospectiveObject retrospective = RetrospectiveDAO.getInstance().get(mRtrospectiveId);

		// assert
		assertNotNull(retrospective);
		assertEquals("TEST_RETROSPECTIVE_NAME", retrospective.getName());
		assertEquals("TEST_RETROSPECTIVE_DESCRIPTION", retrospective.getDescription());
		assertEquals(RetrospectiveObject.TYPE_GOOD, retrospective.getType());
		assertEquals(RetrospectiveObject.STATUS_NEW, retrospective.getStatus());
	}

	@Test
	public void testUpdate() {
		// Test data
		String name = "TEST_RETROSPECTIVE_NAME_UPDATE";
		String description = "TEST_RETROSPECTIVE_DESCRIPTION_UPDATE";
		String type = RetrospectiveObject.TYPE_IMPROVEMENT;
		String status = RetrospectiveObject.STATUS_CLOSED;
		
		// Get retrospective
		RetrospectiveObject retrospective = RetrospectiveDAO.getInstance().get(mRtrospectiveId);
		retrospective.setName(name).setDescription(description)
				.setType(type).setStatus(status);
		
		// Test update method
		boolean isUpdateSuccess = RetrospectiveDAO.getInstance().update(retrospective);
		assertTrue(isUpdateSuccess);
		
		RetrospectiveObject retrospectiveFromDB = RetrospectiveDAO.getInstance().get(mRtrospectiveId);
		// Verify
		assertEquals(name, retrospectiveFromDB.getName());
		assertEquals(description, retrospectiveFromDB.getDescription());
		assertEquals(type, retrospectiveFromDB.getType());
		assertEquals(status, retrospectiveFromDB.getStatus());
	}

	@Test
	public void testDelete() throws SQLException {
		// Call DAO delete
		boolean isDeleteSuccess = RetrospectiveDAO.getInstance().delete(mRtrospectiveId);
		assertTrue(isDeleteSuccess);
		
		// Fetch Retrospective from DB
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(RetrospectiveEnum.TABLE_NAME);
		valueSet.addEqualCondition(RetrospectiveEnum.ID, mRtrospectiveId);
		
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		RetrospectiveObject retrospectiveFromDB = null;
		
		if (result.next()) {
			retrospectiveFromDB = RetrospectiveDAO.convert(result);
		}
		closeResultSet(result);
		
		// assert
		assertNull(retrospectiveFromDB);
	}
	
	@Test
	public void testGetRetrospectivesBySprintId() {
		long sprintId = 2;
		ArrayList<RetrospectiveObject> retrospectives = RetrospectiveDAO.getInstance().getRetrospectivesBySprintId(sprintId);
		assertEquals(0, retrospectives.size());
		RetrospectiveObject retrospective1 = new RetrospectiveObject(mProject.getId());
		retrospective1.setSprintId(sprintId).save();
		RetrospectiveObject retrospective2 = new RetrospectiveObject(mProject.getId());
		retrospective2.setSprintId(sprintId).save();
		RetrospectiveObject retrospective3 = new RetrospectiveObject(mProject.getId());
		retrospective3.setSprintId(sprintId).save();
		retrospectives = RetrospectiveDAO.getInstance().getRetrospectivesBySprintId(sprintId);
		assertEquals(3, retrospectives.size());
		assertEquals(retrospectives.get(0).getId(), retrospective1.getId());
		assertEquals(retrospectives.get(1).getId(), retrospective2.getId());
		assertEquals(retrospectives.get(2).getId(), retrospective3.getId());
	}
	
	private long createRetrospective() {
		// Test data
		String name = "TEST_RETROSPECTIVE_NAME";
		String description = "TEST_RETROSPECTIVE_DESCRIPTION";
		String type = RetrospectiveObject.TYPE_GOOD;
		String status = RetrospectiveObject.STATUS_NEW;
		long sprintId = 1;

		// Create Retrospective
		RetrospectiveObject retrospective = new RetrospectiveObject(mProject.getId());
		retrospective.setName(name).setDescription(description)
				.setType(type).setStatus(status).setSprintId(sprintId);

		// Test create method
		long retrospectiveId = RetrospectiveDAO.getInstance().create(retrospective);
		assertTrue(retrospectiveId > 0);

		return retrospectiveId;
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
