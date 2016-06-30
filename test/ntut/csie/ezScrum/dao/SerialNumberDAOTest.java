package ntut.csie.ezScrum.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.web.dataObject.SerialNumberObject;

public class SerialNumberDAOTest {
	private Configuration mConfig;
	private SerialNumberDAO mSerialNumberDao = null;
	private MySQLControl mControl = null;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mSerialNumberDao = SerialNumberDAO.getInstance();
		mControl = new MySQLControl(mConfig);
		mControl.connect();
	}

	@After
	public void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mConfig = null;
		mSerialNumberDao = null;
		mControl = null;
	}

	@Test
	public void testCreate() {
		long projectId = 1;
		SerialNumberObject serialNumber = new SerialNumberObject(projectId);
		serialNumber.setReleaseId(1)
        		    .setRetrospectiveId(1)
        		    .setSprintId(1)
        		    .setStoryId(1)
        		    .setTaskId(1)
        		    .setUnplanId(1);
		long id = mSerialNumberDao.create(serialNumber);
		assertTrue(id > 0);
	}

	@Test
	public void testGet() {
		long projectId = 1;
		SerialNumberObject serialNumber = new SerialNumberObject(projectId);
		serialNumber.setReleaseId(1)
        		    .setRetrospectiveId(1)
        		    .setSprintId(1)
        		    .setStoryId(1)
        		    .setTaskId(1)
        		    .setUnplanId(1);
		long id = mSerialNumberDao.create(serialNumber);
		assertTrue(id > 0);
		assertNotNull(SerialNumberDAO.getInstance().get(id));
	}
	
	@Test
	public void testGetByProjectId() {
		long projectId = 1;
		SerialNumberObject serialNumber = new SerialNumberObject(projectId);
		serialNumber.setReleaseId(1)
        		    .setRetrospectiveId(1)
        		    .setSprintId(1)
        		    .setStoryId(1)
        		    .setTaskId(1)
        		    .setUnplanId(1);
		long id = mSerialNumberDao.create(serialNumber);
		assertTrue(id > 0);
		assertNotNull(SerialNumberDAO.getInstance().getByProjectId(projectId));
	}

	@Test
	public void testUpdate() throws SQLException {
		// create 3筆 SerialNumberObject
		long projectId = 2;
		for (int i = 1; i <= 3; i++) {
			SerialNumberObject serialNumber = new SerialNumberObject(projectId);
			serialNumber.setReleaseId(i)
	        		    .setRetrospectiveId(i)
	        		    .setSprintId(i)
	        		    .setStoryId(i)
	        		    .setTaskId(i)
	        		    .setUnplanId(i);
			mSerialNumberDao.create(serialNumber);
		}
		// 修改第二筆 SerialNumberObject data 再存回 DB
		SerialNumberObject serialNumber = mSerialNumberDao.get(projectId);
		serialNumber.setReleaseId(3);
		serialNumber.setSprintId(4);
		serialNumber.setStoryId(5);
		serialNumber.setTaskId(6);
		serialNumber.setUnplanId(7);
		serialNumber.setRetrospectiveId(8);

		boolean result = mSerialNumberDao.update(serialNumber);
		assertEquals(true, result);

		// 取出第二筆資料並 assert data
		SerialNumberObject editSerialNumber = mSerialNumberDao.get(projectId);
		assertEquals(editSerialNumber.getReleaseId(), serialNumber.getReleaseId());
		assertEquals(editSerialNumber.getSprintId(), serialNumber.getSprintId());
		assertEquals(editSerialNumber.getStoryId(), serialNumber.getStoryId());
		assertEquals(editSerialNumber.getTaskId(), serialNumber.getTaskId());
		assertEquals(editSerialNumber.getUnplanId(), serialNumber.getUnplanId());
		assertEquals(editSerialNumber.getRetrospectiveId(), serialNumber.getRetrospectiveId());
	}
	
	@Test
	public void testDelete() {
		long projectId = 1;
		SerialNumberObject serialNumber = new SerialNumberObject(projectId);
		serialNumber.setReleaseId(1)
        		    .setRetrospectiveId(1)
        		    .setSprintId(1)
        		    .setStoryId(1)
        		    .setTaskId(1)
        		    .setUnplanId(1);
		long id = mSerialNumberDao.create(serialNumber);
		assertTrue(id > 0);
		assertNotNull(SerialNumberDAO.getInstance().get(id));
		SerialNumberDAO.getInstance().delete(id);
		assertNull(SerialNumberDAO.getInstance().get(id));
	}
	
	@Test
	public void testDeleteByProjectId() {
		long projectId = 1;
		SerialNumberObject serialNumber = new SerialNumberObject(projectId);
		serialNumber.setReleaseId(1)
        		    .setRetrospectiveId(1)
        		    .setSprintId(1)
        		    .setStoryId(1)
        		    .setTaskId(1)
        		    .setUnplanId(1);
		long id = mSerialNumberDao.create(serialNumber);
		assertTrue(id > 0);
		assertNotNull(SerialNumberDAO.getInstance().get(id));
		SerialNumberDAO.getInstance().deleteByProjectId(projectId);
		assertNull(SerialNumberDAO.getInstance().get(id));
	}
}
