package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.dao.SerialNumberDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.databaseEnum.SerialNumberEnum;

public class SerialNumberObjectTest {
	private MySQLControl mControl = null;
	private Configuration mConfig;

	@Before
	public void setUp() {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mControl = new MySQLControl(mConfig);
		mControl.connect();
	}

	@After
	public void tearDown() {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		ini = null;
		mControl = null;
		mConfig = null;
	}

	@Test
	public void testToJSON() throws JSONException {
		long projectId = 1;
		long releaseId = 2;
		long sprintId = 3;
		long storyId = 4;
		long taskId = 5;
		long unplannedId = 6;
		long retrospectiveId = 7;
		
		SerialNumberObject serialNumber = new SerialNumberObject(projectId);
		serialNumber.setReleaseId(releaseId)
		            .setSprintId(sprintId)
		            .setStoryId(storyId)
		            .setTaskId(taskId)
		            .setUnplannedId(unplannedId)
		            .setRetrospectiveId(retrospectiveId)
		            .save();
		
		JSONObject serialNumberJson = serialNumber.toJSON();
		assertEquals(serialNumber.getId(), serialNumberJson.getLong(SerialNumberEnum.ID));
		assertEquals(serialNumber.getProjectId(), serialNumberJson.getLong(SerialNumberEnum.PROJECT_ID));
		assertEquals(serialNumber.getReleaseId(), serialNumberJson.getLong(SerialNumberEnum.RELEASE));
		assertEquals(serialNumber.getSprintId(), serialNumberJson.getLong(SerialNumberEnum.SPRINT));
		assertEquals(serialNumber.getStoryId(), serialNumberJson.getLong(SerialNumberEnum.STORY));
		assertEquals(serialNumber.getTaskId(), serialNumberJson.getLong(SerialNumberEnum.TASK));
		assertEquals(serialNumber.getUnplannedId(), serialNumberJson.getLong(SerialNumberEnum.UNPLANNED));
		assertEquals(serialNumber.getRetrospectiveId(), serialNumberJson.getLong(SerialNumberEnum.RETROSPECTIVE));
	}

	@Test
	public void testSave_CreateANewSerialNumber() {
		// Test Data
		long projectId = 1;
		long releaseId = 2;
		long sprintId = 3;
		long storyId = 4;
		long taskId = 5;
		long unplannedId = 6;
		long retrospectiveId = 7;

		// Build SerialNumberObject
		SerialNumberObject serialNumber = new SerialNumberObject(projectId);
		serialNumber.setReleaseId(releaseId)
		            .setRetrospectiveId(retrospectiveId)
		            .setSprintId(sprintId)
		            .setStoryId(storyId)
		            .setTaskId(taskId)
		            .setUnplannedId(unplannedId)
		            .save();

		long id = serialNumber.getId();
		assertTrue(id > 0);

		SerialNumberObject serialNumberFromDB = SerialNumberDAO.getInstance().get(id);

		assertEquals(id, serialNumberFromDB.getId());
		assertEquals(projectId, serialNumberFromDB.getProjectId());
		assertEquals(releaseId, serialNumberFromDB.getReleaseId());
		assertEquals(sprintId, serialNumberFromDB.getSprintId());
		assertEquals(storyId, serialNumberFromDB.getStoryId());
		assertEquals(taskId, serialNumberFromDB.getTaskId());
		assertEquals(unplannedId, serialNumberFromDB.getUnplannedId());
		assertEquals(retrospectiveId, serialNumberFromDB.getRetrospectiveId());
	}
	
	@Test
	public void testSave_UpdateSerialNumber() {
		// Test Data
		long projectId = 1;
		long releaseId = 12;
		long sprintId = 13;
		long storyId = 14;
		long taskId = 15;
		long unplannedId = 16;
		long retrospectiveId = 17;

		// Build SerialNumberObject
		SerialNumberObject serialNumber = new SerialNumberObject(projectId);
		serialNumber.setReleaseId(2)
		            .setSprintId(3)
		            .setStoryId(4)
		            .setTaskId(5)
		            .setUnplannedId(6)
		            .setRetrospectiveId(7)
		            .save();

		long id = serialNumber.getId();
		assertTrue(id > 0);
		assertEquals(id, serialNumber.getId());
		assertEquals(1, serialNumber.getProjectId());
		assertEquals(2, serialNumber.getReleaseId());
		assertEquals(3, serialNumber.getSprintId());
		assertEquals(4, serialNumber.getStoryId());
		assertEquals(5, serialNumber.getTaskId());
		assertEquals(6, serialNumber.getUnplannedId());
		assertEquals(7, serialNumber.getRetrospectiveId());
		
		// Update serialNumber
		serialNumber.setReleaseId(releaseId)
		            .setRetrospectiveId(retrospectiveId)
		            .setSprintId(sprintId)
		            .setStoryId(storyId)
		            .setTaskId(taskId)
		            .setUnplannedId(unplannedId)
		            .save();

		SerialNumberObject serialNumberFromDB = SerialNumberDAO.getInstance().get(id);
		
		// Assert
		assertEquals(id, serialNumberFromDB.getId());
		assertEquals(projectId, serialNumberFromDB.getProjectId());
		assertEquals(releaseId, serialNumberFromDB.getReleaseId());
		assertEquals(sprintId, serialNumberFromDB.getSprintId());
		assertEquals(storyId, serialNumberFromDB.getStoryId());
		assertEquals(taskId, serialNumberFromDB.getTaskId());
		assertEquals(unplannedId, serialNumberFromDB.getUnplannedId());
		assertEquals(retrospectiveId, serialNumberFromDB.getRetrospectiveId());
	}

	@Test
	public void testReload() {

	}

	@Test
	public void testDelete() {

	}

	@Test
	public void testExists() {

	}
}
