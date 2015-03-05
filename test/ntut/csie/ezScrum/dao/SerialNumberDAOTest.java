package ntut.csie.ezScrum.dao;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.SerialNumberObject;
import ntut.csie.ezScrum.web.databasEnum.SerialNumberEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

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
		long id = mSerialNumberDao.create(new SerialNumberObject(projectId,
		        1, 1, 1, 1, 1, 1));
		assertNotSame(-1, id);
	}

	@Test
	public void testGet() throws SQLException {
		long projectId = 1;

		// 新增三筆測試資料
		for (int i = 1; i <= 3; i++) {
			mSerialNumberDao.create(new SerialNumberObject(i, i, i, i, i, i, i));
		}

		// get
		SerialNumberObject serialNumber = mSerialNumberDao.get(projectId);
		assertEquals(projectId, serialNumber.getProjectId());
		assertEquals(1, serialNumber.getReleaseId());
		assertEquals(1, serialNumber.getSprintId());
		assertEquals(1, serialNumber.getStoryId());
		assertEquals(1, serialNumber.getTaskId());
		assertEquals(1, serialNumber.getRetrospectiveId());
		assertEquals(1, serialNumber.getUnplannedId());
	}

	@Test
	public void testUpdate() throws SQLException {
		// create 3筆 SerialNumberObject
		long projectId = 2;
		for (int i = 1; i <= 3; i++) {
			mSerialNumberDao.create(new SerialNumberObject(i, i, i, i, i, i, i));
		}
		// 修改第二筆 SerialNumberObject data 再存回 DB
		SerialNumberObject serialNumber = mSerialNumberDao.get(projectId);
		serialNumber.setReleaseId(3);
		serialNumber.setSprintId(4);
		serialNumber.setStoryId(5);
		serialNumber.setTaskId(6);
		serialNumber.setUnplannedId(7);
		serialNumber.setRetrospectiveId(8);

		boolean result = mSerialNumberDao.update(serialNumber);
		assertEquals(true, result);

		// 取出第二筆資料並 assert data
		SerialNumberObject editSerialNumber = mSerialNumberDao.get(projectId);
		assertEquals(editSerialNumber.getReleaseId(), serialNumber.getReleaseId());
		assertEquals(editSerialNumber.getSprintId(), serialNumber.getSprintId());
		assertEquals(editSerialNumber.getStoryId(), serialNumber.getStoryId());
		assertEquals(editSerialNumber.getTaskId(), serialNumber.getTaskId());
		assertEquals(editSerialNumber.getUnplannedId(), serialNumber.getUnplannedId());
		assertEquals(editSerialNumber.getRetrospectiveId(), serialNumber.getRetrospectiveId());
	}

	@Test
	public void testUpdateByColumn() throws SQLException {
		// create 3筆 SerialNumberObject
		long projectId = 2;
		for (int i = 1; i <= 3; i++) {
			mSerialNumberDao.create(new SerialNumberObject(i, i, i, i, i, i, i));
		}
		// 修改第二筆 SerialNumberObject data 再存回 DB
		SerialNumberObject serialNumber = mSerialNumberDao.get(projectId);
		serialNumber.setReleaseId(3);
		serialNumber.setSprintId(4);
		serialNumber.setStoryId(5);
		serialNumber.setTaskId(6);
		serialNumber.setUnplannedId(7);
		serialNumber.setRetrospectiveId(8);
		
		// update
		mSerialNumberDao.updateByColumn(SerialNumberEnum.RELEASE, serialNumber);
		mSerialNumberDao.updateByColumn(SerialNumberEnum.SPRINT, serialNumber);
		mSerialNumberDao.updateByColumn(SerialNumberEnum.STORY, serialNumber);
		mSerialNumberDao.updateByColumn(SerialNumberEnum.TASK, serialNumber);
		mSerialNumberDao.updateByColumn(SerialNumberEnum.UNPLANNED, serialNumber);
		mSerialNumberDao.updateByColumn(SerialNumberEnum.RETROSPECTIVE, serialNumber);
		
		// 取出第二筆資料並 assert data
		SerialNumberObject editSerialNumber = mSerialNumberDao.get(projectId);
		assertEquals(editSerialNumber.getReleaseId(), serialNumber.getReleaseId());
		assertEquals(editSerialNumber.getSprintId(), serialNumber.getSprintId());
		assertEquals(editSerialNumber.getStoryId(), serialNumber.getStoryId());
		assertEquals(editSerialNumber.getTaskId(), serialNumber.getTaskId());
		assertEquals(editSerialNumber.getUnplannedId(), serialNumber.getUnplannedId());
		assertEquals(editSerialNumber.getRetrospectiveId(), serialNumber.getRetrospectiveId());
	}
	
	@Test
	public void testDelete() throws SQLException {
		int projectCount = 3;
		long projectId = 3;
		for(int i = 1; i <= projectCount; i++) {
			mSerialNumberDao.create(new SerialNumberObject(i, i, i, i, i, i, i));
		}
		
		// assert before delete one serial number have three data
		ArrayList<SerialNumberObject> serialnumberList = new ArrayList<SerialNumberObject>();
		for(int i = 1; i <= projectCount; i++) {
			serialnumberList.add(mSerialNumberDao.get(i));
		}
		assertEquals(projectCount, serialnumberList.size());
		
		// clear serialnumberList data
		serialnumberList = null;
		serialnumberList = new ArrayList<SerialNumberObject>();
		
		// delete one serial number and assert list remain two data
		boolean result = mSerialNumberDao.delete(projectId);
		assertEquals(true, result);
		
		for(int i = 1; i <= projectCount; i++) {
			if(i == projectId) {
				assertNull(mSerialNumberDao.get(i));
			} else {
				serialnumberList.add(mSerialNumberDao.get(i));
				assertEquals(i, serialnumberList.get(i-1).getProjectId());
			}
		}
		assertEquals(projectCount-1, serialnumberList.size());
	}
}
