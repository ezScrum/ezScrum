package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.web.databaseEnum.AttachFileEnum;

public class AttachFileObjectTest {
	private MySQLControl mControl = null;
	private Configuration mConfig;

	@Before
	public void setUp(){
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mControl = new MySQLControl(mConfig);
		mControl.connect();
	}
	
	@After
	public void tearDown(){
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
	public void testSave_CreateANewAttachFile() {
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
		                 .setPath(filePath)
		                 .save();
		
		long id = attachFile.getId();
		assertTrue(id > 0);
		
		AttachFileObject attachFileFromDB = AttachFileObject.get(id);

		assertEquals(attachFile.getId(), attachFileFromDB.getId());
		assertEquals(contentType, attachFileFromDB.getContentType());
		assertEquals(fileName, attachFileFromDB.getName());
		assertEquals(filePath, attachFileFromDB.getPath());
		assertEquals(issueId, attachFileFromDB.getIssueId());
		assertEquals(issueType, attachFileFromDB.getIssueType());
	}
	
	@Test
	public void testGetFullPath() {
		// Test Data
		String contentType = "text/plain";
		String fileName = "file.txt";
		String projectName = "project01";
		long issueId = 10;
		int issueType = 1;
		
		Configuration configuration = new Configuration();
		// Build AttachFileObject
		AttachFileObject attachFile = new AttachFileObject();
		attachFile.setContentType(contentType)
		                 .setName(fileName)
		                 .setIssueId(issueId)
		                 .setIssueType(issueType)
		                 .setPath(File.separator + "AttachFile" + File.separator + projectName + File.separator + fileName)
		                 .save();
		
		long id = attachFile.getId();
		assertTrue(id > 0);
		
		AttachFileObject attachFileFromDB = AttachFileObject.get(id);
		assertEquals(configuration.getWorkspacePath() + File.separator + "AttachFile" + File.separator + projectName + File.separator + fileName, attachFileFromDB.getFullPath());
	}
	
	@Test
	public void testReload() {
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
		                 .setPath(filePath)
		                 .save();

		long id = attachFile.getId();
		assertTrue(id > 0);
		assertEquals(contentType, attachFile.getContentType());
		assertEquals(fileName, attachFile.getName());
		assertEquals(filePath, attachFile.getPath());
		assertEquals(issueId, attachFile.getIssueId());
		assertEquals(issueType, attachFile.getIssueType());

		attachFile.setContentType(contentType + "CuteCoolIsDevil")
			      .setName(fileName + "CuteCoolSoBad")
			      .setIssueId(issueId + 1)
			      .setIssueType(issueType + 99)
			      .setPath(filePath + "HenryIsGood");
		
		assertEquals(contentType + "CuteCoolIsDevil", attachFile.getContentType());
		assertEquals(fileName + "CuteCoolSoBad", attachFile.getName());
		assertEquals(filePath + "HenryIsGood", attachFile.getPath());
		assertEquals(issueId + 1, attachFile.getIssueId());
		assertEquals(issueType + 99, attachFile.getIssueType());

		attachFile.reload();
		assertEquals(contentType, attachFile.getContentType());
		assertEquals(fileName, attachFile.getName());
		assertEquals(filePath, attachFile.getPath());
		assertEquals(issueId, attachFile.getIssueId());
		assertEquals(issueType, attachFile.getIssueType());
	}
	
	@Test
	public void testDelete() {
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
		                 .setPath(filePath)
		                 .save();

		long id = attachFile.getId();
		assertTrue(id > 0);
		assertEquals(contentType, attachFile.getContentType());
		assertEquals(fileName, attachFile.getName());
		assertEquals(filePath, attachFile.getPath());
		assertEquals(issueId, attachFile.getIssueId());
		assertEquals(issueType, attachFile.getIssueType());

		boolean deleteSuccess = attachFile.delete();

		assertTrue(deleteSuccess);
		assertEquals(-1, attachFile.getId());
		assertEquals(-1, attachFile.getIssueId());
		assertEquals(-1, attachFile.getIssueType());
		assertEquals(null, AttachFileObject.get(id));
	}
	
	@Test
	public void testToString() {
		// Test Data
		long id = 1;
		long issueId = 5;
		int issueType = 10;
		String name = "TEST_ATTACHFILE_NAME";
		String path = "TEST_ATTACHFILE_PATH";
		String contentType = "TEST_ATTACHFILE_CONTENT_TYPE";
		long createTime = System.currentTimeMillis();
		
		AttachFileObject attachFile = new AttachFileObject();
		attachFile.setId(id)
		          .setName(name)
		          .setContentType(contentType)
		          .setIssueId(issueId)
		          .setIssueType(issueType)
		          .setPath(path)
		          .setCreateTime(createTime);
		
		// Call toString
		String actualString = attachFile.toString();
		
		String expectedString = AttachFileEnum.ID + "=" + id + "\n" +
				AttachFileEnum.NAME + "=" + name + "\n" +
				AttachFileEnum.PATH + "=" + path + "\n" +
				AttachFileEnum.ISSUE_ID + "=" + issueId + "\n" +
				AttachFileEnum.ISSUE_TYPE + "=" + issueType + "\n" +
				AttachFileEnum.CREATE_TIME + "=" + createTime;
		
		// Assert
		assertEquals(expectedString, actualString);
	}

	
}
