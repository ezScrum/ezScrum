package ntut.csie.ezScrum.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.databaseEnum.AttachFileEnum;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;

public class AttachFileDAOTest {
	private MySQLControl mControl = null;
	private Configuration mConfig;

	@Before
    public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mControl = new MySQLControl(mConfig);
		mControl.connect();
    }

	@After
    public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
		
		ini = null;
		mControl = null;
		mConfig = null;
    }
	
	@Test
	public void testCreate() throws SQLException {
		// Test Data
		String contentType = "text/plain";
		String fileName = "TEST_ATTACH_FILE_NAME";
		String filePath = "TEST_ATTACH_FILE_PATH";
		long issueId = 10;
		int issueType = 1;
		
		// Build AttachFileObject
		AttachFileObject attachFile = new AttachFileObject();
		attachFile.setContentType(contentType)
		                 .setName(fileName)
		                 .setIssueId(issueId)
		                 .setIssueType(issueType)
		                 .setPath(filePath);
		
		// Call DAO
		long attachFileId = AttachFileDAO.getInstance().create(attachFile);
		
		assertNotSame(-1, attachFileId);
		
		AttachFileObject attachFileFromDB = null;

		// 從 DB 裡取出 AttachFile 資料
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AttachFileEnum.TABLE_NAME);
		valueSet.addEqualCondition(AttachFileEnum.ID, attachFileId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		if (result.next()) {
			attachFileFromDB = AttachFileDAO.convert(result);
		}
		closeResultSet(result);

		// Assert
		assertEquals(attachFileId, attachFileFromDB.getId());
		assertEquals(contentType, attachFileFromDB.getContentType());
		assertEquals(fileName, attachFileFromDB.getName());
		assertEquals(filePath, attachFileFromDB.getPath());
		assertEquals(issueId, attachFileFromDB.getIssueId());
		assertEquals(issueType, attachFileFromDB.getIssueType());
	}
	
	@Test
	public void testGet() {
		// Test Data
		String contentType = "text/plain";
		String fileName = "TEST_ATTACH_FILE_NAME";
		String filePath = "TEST_ATTACH_FILE_PATH";
		long issueId = 10;
		int issueType = 1;

		// Build AttachFileObject
		AttachFileObject attachFile = new AttachFileObject();
		attachFile.setContentType(contentType)
		        .setName(fileName)
		        .setIssueId(issueId)
		        .setIssueType(issueType)
		        .setPath(filePath);
		long attachFileId = AttachFileDAO.getInstance().create(attachFile);
		assertNotSame(-1, attachFileId);
		
		// Call DAO get
		AttachFileObject attachFileFromGet = AttachFileDAO.getInstance().get(attachFileId);
		
		// Assert
		assertNotNull(attachFileFromGet);
		assertEquals(attachFileId, attachFileFromGet.getId());
		assertEquals(contentType, attachFileFromGet.getContentType());
		assertEquals(fileName, attachFileFromGet.getName());
		assertEquals(filePath, attachFileFromGet.getPath());
		assertEquals(issueId, attachFileFromGet.getIssueId());
		assertEquals(issueType, attachFileFromGet.getIssueType());
	}
	
	@Test
	public void testGetAttachFilesByStoryId() {
		// create a attach file
		String TEST_FILE_NAME = "TEST_FILE_NAME";
		String TEST_FILE_PATH = "/TEST_PATH";
		String TEST_FILE_CONTENT_TYPE = "jpg";
		long TEST_CREATE_TIME = System.currentTimeMillis();
		long issueId = 1;
		assertEquals(0, AttachFileDAO.getInstance().getAttachFilesByStoryId(issueId).size());
		
		// create a attach file append to task with id = 1
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AttachFileEnum.TABLE_NAME);
		valueSet.addInsertValue(AttachFileEnum.ISSUE_ID, String.valueOf(issueId));
		valueSet.addInsertValue(AttachFileEnum.ISSUE_TYPE, String.valueOf(IssueTypeEnum.TYPE_TASK));
		valueSet.addInsertValue(AttachFileEnum.NAME, TEST_FILE_NAME);
		valueSet.addInsertValue(AttachFileEnum.PATH, TEST_FILE_PATH);
		valueSet.addInsertValue(AttachFileEnum.CONTENT_TYPE, TEST_FILE_CONTENT_TYPE);
		valueSet.addInsertValue(AttachFileEnum.CREATE_TIME, String.valueOf(TEST_CREATE_TIME));
		String query = valueSet.getInsertQuery();
		mControl.executeInsert(query);
		
		// create a attach file append to story with id = 1
		IQueryValueSet valueSet2 = new MySQLQuerySet();
		valueSet2.addTableName(AttachFileEnum.TABLE_NAME);
		valueSet2.addInsertValue(AttachFileEnum.ISSUE_ID, String.valueOf(issueId));
		valueSet2.addInsertValue(AttachFileEnum.ISSUE_TYPE, String.valueOf(IssueTypeEnum.TYPE_STORY));
		valueSet2.addInsertValue(AttachFileEnum.NAME, TEST_FILE_NAME);
		valueSet2.addInsertValue(AttachFileEnum.PATH, TEST_FILE_PATH);
		valueSet2.addInsertValue(AttachFileEnum.CONTENT_TYPE, TEST_FILE_CONTENT_TYPE);
		valueSet2.addInsertValue(AttachFileEnum.CREATE_TIME, String.valueOf(TEST_CREATE_TIME));
		String query2 = valueSet2.getInsertQuery();
		long id = mControl.executeInsert(query2);
		assertEquals(1, AttachFileDAO.getInstance().getAttachFilesByStoryId(issueId).size());
		assertEquals(id, AttachFileDAO.getInstance().getAttachFilesByStoryId(issueId).get(0).getId());
	}
	
	@Test
	public void testGetAttachFilesByTaskId() {
		// create a attach file
		String TEST_FILE_NAME = "TEST_FILE_NAME";
		String TEST_FILE_PATH = "/TEST_PATH";
		String TEST_FILE_CONTENT_TYPE = "jpg";
		long TEST_CREATE_TIME = System.currentTimeMillis();
		long issueId = 1;
		assertEquals(0, AttachFileDAO.getInstance().getAttachFilesByTaskId(issueId).size());
		
		// create a attach file append to task with id = 1
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AttachFileEnum.TABLE_NAME);
		valueSet.addInsertValue(AttachFileEnum.ISSUE_ID, String.valueOf(issueId));
		valueSet.addInsertValue(AttachFileEnum.ISSUE_TYPE, String.valueOf(IssueTypeEnum.TYPE_TASK));
		valueSet.addInsertValue(AttachFileEnum.NAME, TEST_FILE_NAME);
		valueSet.addInsertValue(AttachFileEnum.PATH, TEST_FILE_PATH);
		valueSet.addInsertValue(AttachFileEnum.CONTENT_TYPE, TEST_FILE_CONTENT_TYPE);
		valueSet.addInsertValue(AttachFileEnum.CREATE_TIME, String.valueOf(TEST_CREATE_TIME));
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);
		
		// create a attach file append to story with id = 1
		IQueryValueSet valueSet2 = new MySQLQuerySet();
		valueSet2.addTableName(AttachFileEnum.TABLE_NAME);
		valueSet2.addInsertValue(AttachFileEnum.ISSUE_ID, String.valueOf(issueId));
		valueSet2.addInsertValue(AttachFileEnum.ISSUE_TYPE, String.valueOf(IssueTypeEnum.TYPE_STORY));
		valueSet2.addInsertValue(AttachFileEnum.NAME, TEST_FILE_NAME);
		valueSet2.addInsertValue(AttachFileEnum.PATH, TEST_FILE_PATH);
		valueSet2.addInsertValue(AttachFileEnum.CONTENT_TYPE, TEST_FILE_CONTENT_TYPE);
		valueSet2.addInsertValue(AttachFileEnum.CREATE_TIME, String.valueOf(TEST_CREATE_TIME));
		String query2 = valueSet2.getInsertQuery();
		mControl.executeInsert(query2);
		assertEquals(1, AttachFileDAO.getInstance().getAttachFilesByTaskId(issueId).size());
		assertEquals(id, AttachFileDAO.getInstance().getAttachFilesByTaskId(issueId).get(0).getId());
	}
	
	@Test
	public void testDelete() {
		// create a attach file
		String TEST_FILE_NAME = "TEST_FILE_NAME";
		String TEST_FILE_PATH = "/TEST_PATH";
		String TEST_FILE_CONTENT_TYPE = "jpg";
		long TEST_CREATE_TIME = System.currentTimeMillis();
		long issueId = 1;
		
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AttachFileEnum.TABLE_NAME);
		valueSet.addInsertValue(AttachFileEnum.ISSUE_ID, String.valueOf(issueId));
		valueSet.addInsertValue(AttachFileEnum.ISSUE_TYPE, String.valueOf(IssueTypeEnum.TYPE_TASK));
		valueSet.addInsertValue(AttachFileEnum.NAME, TEST_FILE_NAME);
		valueSet.addInsertValue(AttachFileEnum.PATH, TEST_FILE_PATH);
		valueSet.addInsertValue(AttachFileEnum.CONTENT_TYPE, TEST_FILE_CONTENT_TYPE);
		valueSet.addInsertValue(AttachFileEnum.CREATE_TIME, String.valueOf(TEST_CREATE_TIME));
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);
		assertNotNull(AttachFileDAO.getInstance().get(id));
		AttachFileDAO.getInstance().delete(id);
		assertNull(AttachFileDAO.getInstance().get(id));
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
