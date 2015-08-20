package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;

import ntut.csie.ezScrum.dao.ReleaseDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.databasEnum.ReleaseEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReleaseObjectTest {
	private MySQLControl mControl = null;
	private Configuration mConfig = null;
	private CreateProject mCP = null;
	private final int mPROJECT_COUNT = 1;
	private long mProjectId = -1;
	private ReleaseObject mRelease = null;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCP = new CreateProject(mPROJECT_COUNT);
		mCP.exeCreate();

		mControl = new MySQLControl(mConfig);
		mControl.connect();

		mProjectId = mCP.getAllProjects().get(0).getId();
		mRelease = createRelease();
	}

	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		mConfig = null;
		mControl = null;
		mCP = null;
	}
	
	@Test
	public void testSaveCreateNewRelease() throws SQLException {
		// Test Data
		String releaseName = "TEST_RELEASE_NAME";
		String releaseDescription = "TEST_RELEASE_DESCRIPTION";
		String releaseStartDate = "2015/08/03";
		String releaseDueDate = "2015/10/31";
		
		// Create release object
		ReleaseObject release = new ReleaseObject(mProjectId);
		release.setName(releaseName)
		       .setDescription(releaseDescription)
		       .setStartDate(releaseStartDate)
		       .setDueDate(releaseDueDate)
		       .save();
		
		// 從資料庫撈出  Release
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ReleaseEnum.TABLE_NAME);
		valueSet.addEqualCondition(ReleaseEnum.ID, release.getId());

		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		ReleaseObject releaseCreated = null;
		if (result.next()) {
			releaseCreated = ReleaseDAO.convert(result);
		}
		// Close result set
		closeResultSet(result);

		// assert
		assertEquals(release.getId(), releaseCreated.getId());
		assertEquals(release.getProjectId(), releaseCreated.getProjectId());
		assertEquals(releaseName, releaseCreated.getName());
		assertEquals(releaseDescription, releaseCreated.getDescription());
		assertEquals(releaseStartDate, releaseCreated.getStartDateString());
		assertEquals(releaseDueDate, releaseCreated.getDueDateString());
	}

	@Test
	public void testSaveUpdateRelease() {
		// Test Data
		String releaseName = "TEST_RELEASE_NAME_NEW";
		String releaseDescription = "TEST_RELEASE_DESCRIPTION_NEW";
		String releaseStartDate = "2015/06/03";
		String releaseDueDate = "2015/08/31";
		
		// Update Release
		mRelease.setName(releaseName)
		        .setDescription(releaseDescription)
		        .setStartDate(releaseStartDate)
		        .setDueDate(releaseDueDate)
		        .save();
		
		ReleaseObject release = ReleaseObject.get(mRelease.getId());
		
		// assert
		assertEquals(releaseName, release.getName());
		assertEquals(releaseDescription, release.getDescription());
		assertEquals(releaseStartDate, release.getStartDateString());
		assertEquals(releaseDueDate, release.getDueDateString());
	}
	
	@Test
	public void testDelete() {
		// Get releaseId
		long releaseId = mRelease.getId();
		// Assert release exist
		assertNotNull(mRelease);
		// Delete release
		boolean deleteStatus = mRelease.delete();
		// Assert Delete Status
		assertTrue(deleteStatus);
		
		// Reload release object
		ReleaseObject release = ReleaseObject.get(releaseId);
		// Assert release object is null
		assertNull(release);
	}
	
	private ReleaseObject createRelease() {
		// Test Data
		String releaseName = "TEST_RELEASE_NAME";
		String releaseDescription = "TEST_RELEASE_DESCRIPTION";
		String releaseStartDate = "2015/08/03";
		String releaseDueDate = "2015/10/31";

		// Create release object
		ReleaseObject release = new ReleaseObject(mProjectId);
		release.setName(releaseName)
		        .setDescription(releaseDescription)
		        .setStartDate(releaseStartDate)
		        .setDueDate(releaseDueDate)
		        .save();

		// assert
		assertNotSame(-1, release.getId());
		assertEquals(mProjectId, release.getProjectId());
		assertEquals(releaseName, release.getName());
		assertEquals(releaseDescription, release.getDescription());
		assertEquals(releaseStartDate, release.getStartDateString());
		assertEquals(releaseDueDate, release.getDueDateString());
		return release;
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
