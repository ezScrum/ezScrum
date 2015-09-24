package ntut.csie.ezScrum.dao;

import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.databaseEnum.AttachFileEnum;

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
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// ============= release ==============
		ini = null;
		mConfig = null;
		mControl = null;
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
		AttachFileObject.Builder attachFileBuilder = new AttachFileObject.Builder();
		attachFileBuilder.setContentType(contentType)
		                 .setName(fileName)
		                 .setIssueId(issueId)
		                 .setIssueType(issueType)
		                 .setPath(filePath);
		AttachFileObject attachFile = attachFileBuilder.build();
		
		// Call DAO
		long attachFileId = AttachFileDAO.getInstance().create(attachFile);
		
		Assert.assertNotSame(-1, attachFileId);
		
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
		
	}
	
	@Test
	public void testGetAttachFilesByStoryId() {
		
	}
	
	@Test
	public void testGetAttachFilesByTaskId() {
		
	}
	
	@Test
	public void testDelete() {
		
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
