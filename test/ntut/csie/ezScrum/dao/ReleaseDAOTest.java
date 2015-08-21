package ntut.csie.ezScrum.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.databasEnum.ReleaseEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReleaseDAOTest {
	private MySQLControl mControl = null;
	private Configuration mConfig;
	private CreateProject mCP;
	private static long sProjectId;
	private long mReleaseId;
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

		sProjectId = mCP.getAllProjects().get(0).getId();

		// create a release
		mReleaseId = createRelease();
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
		mConfig = null;
		mControl = null;
	}

	@Test
	public void testCreate() throws SQLException {
		// Test data
		String name = "TEST_RELEASE_NAME";
		String description = "TEST_RELEASE_DESCRIPTION";
		String releaseStartDate = "2015/05/28";
		String releaseDueDate = "2015/06/11";

		// Create Release
		ReleaseObject release = new ReleaseObject(sProjectId);
		release.setName(name).setDescription(description)
				.setStartDate(releaseStartDate).setDueDate(releaseDueDate);

		// Test create method
		long releaseId = ReleaseDAO.getInstance().create(release);

		// Get Data From DB
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ReleaseEnum.TABLE_NAME);
		valueSet.addEqualCondition(ReleaseEnum.ID, releaseId);

		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);

		ReleaseObject releaseFromDB = null;

		if (result.next()) {
			releaseFromDB = ReleaseDAO.convert(result);
		}
		closeResultSet(result);

		// Verify Data
		assertEquals(name, releaseFromDB.getName());
		assertEquals(description, releaseFromDB.getDescription());
		assertEquals(releaseStartDate, releaseFromDB.getStartDateString());
		assertEquals(releaseDueDate, releaseFromDB.getDueDateString());
	}

	@Test
	public void testGet() {
		// get release
		ReleaseObject release = ReleaseDAO.getInstance().get(mReleaseId);

		// assert
		assertNotNull(release);
		assertEquals("TEST_RELEASE_NAME", release.getName());
		assertEquals("TEST_RELEASE_DESCRIPTION", release.getDescription());
		assertEquals("2015/05/28", release.getStartDateString());
		assertEquals("2015/06/11", release.getDueDateString());
	}

	@Test
	public void testUpdate() {
		// Test data
		String name = "TEST_RELEASE_NAME_UPDATE";
		String description = "TEST_RELEASE_DESCRIPTION_UPDATE";
		String releaseStartDate = "2015/06/11";
		String releaseDueDate = "2015/06/24";
		
		// Get release
		ReleaseObject release = ReleaseDAO.getInstance().get(mReleaseId);
		release.setName(name)
		       .setDescription(description)
		       .setStartDate(releaseStartDate)
		       .setDueDate(releaseDueDate);
		
		// Test update method
		boolean isUpdateSuccess = ReleaseDAO.getInstance().update(release);
		assertTrue(isUpdateSuccess);
		
		ReleaseObject releaseFromDB = ReleaseDAO.getInstance().get(mReleaseId);
		// Verify
		assertEquals(name, releaseFromDB.getName());
		assertEquals(description, releaseFromDB.getDescription());
		assertEquals(releaseStartDate, releaseFromDB.getStartDateString());
		assertEquals(releaseDueDate, releaseFromDB.getDueDateString());
	}

	@Test
	public void testDelete() throws SQLException {
		// Call DAO delete
		ReleaseDAO.getInstance().delete(mReleaseId);
		
		// Fetch Release from DB
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ReleaseEnum.TABLE_NAME);
		valueSet.addEqualCondition(ReleaseEnum.ID, mReleaseId);
		
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		ReleaseObject releaseFromDB = null;
		
		if (result.next()) {
			releaseFromDB = ReleaseDAO.convert(result);
		}
		closeResultSet(result);
		
		// assert
		assertNull(releaseFromDB);
	}
	
	@Test
	public void testGetReleasesByProjectId() {
		// TODO
	}
	
	private long createRelease() {
		// Test data
		String name = "TEST_RELEASE_NAME";
		String description = "TEST_RELEASE_DESCRIPTION";
		String releaseStartDate = "2015/05/28";
		String releaseDueDate = "2015/06/11";

		// Create Release
		ReleaseObject release = new ReleaseObject(sProjectId);
		release.setName(name).setDescription(description)
				.setStartDate(releaseStartDate).setDueDate(releaseDueDate);

		// Call DAO
		long releaseId = ReleaseDAO.getInstance().create(release);
		assertTrue(releaseId > 0);

		return releaseId;
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
