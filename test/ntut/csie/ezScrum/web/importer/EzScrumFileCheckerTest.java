package ntut.csie.ezScrum.web.importer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class EzScrumFileCheckerTest {
	private static String FILE_NAME_VALID = "test/ntut/csie/ezScrum/web/importer/ezScrum_backup_valid.json";
	private static String FILE_NAME_INVALID_KEY = "test/ntut/csie/ezScrum/web/importer/ezScrum_backup_invalid_key.json";
	private static String FILE_NAME_INVALID_TYPE = "test/ntut/csie/ezScrum/web/importer/ezScrum_backup_invalid_type.json";
	private static String FILE_NAME_INVALID_FILE_FORMAT = "test/ntut/csie/ezScrum/web/importer/ezScrum_backup_invalid_format.json";
	private Configuration mConfig;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws IOException {
		// set up database
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// create test data for valid file
		FileWriter file1 = new FileWriter(FILE_NAME_VALID);
		JSONObject wholeJson1 = new JSONObject();
		JSONArray projects_1 = new JSONArray();
		JSONObject project1 = new JSONObject();
		project1.put(JsonParser.PROJECT_CREATE_TIME, 1432109256540L);
		project1.put(JsonParser.PROJECT_ATTACH_MAX_SIZE, 2L);
		project1.put(JsonParser.PROJECT_PRODUCT_OWNER, "Jay");
		project1.put(JsonParser.PROJECT_NAME, "Project1");
		project1.put(JsonParser.PROJECT_COMMENT, "The first project");
		project1.put(JsonParser.PROJECT_DISPLAY_NAME, "Project1 Display");

		JSONObject project2 = new JSONObject();
		project2.put(JsonParser.PROJECT_CREATE_TIME, 1432109256540L);
		project2.put(JsonParser.PROJECT_ATTACH_MAX_SIZE, 2L);
		project2.put(JsonParser.PROJECT_PRODUCT_OWNER, "Sam");
		project2.put(JsonParser.PROJECT_NAME, "Project2");
		project2.put(JsonParser.PROJECT_COMMENT, "The second project");
		project2.put(JsonParser.PROJECT_DISPLAY_NAME, "Project2 Display");
		projects_1.add(project1);
		projects_1.add(project2);
		wholeJson1.put("projects", projects_1);

		// create test data for invalid key file
		FileWriter file2 = new FileWriter(FILE_NAME_INVALID_KEY);
		JSONObject wholeJson2 = new JSONObject();
		JSONArray projects_2 = new JSONArray();
		JSONObject project3 = new JSONObject();
		project3.put(JsonParser.PROJECT_NAME, "Project1");
		project3.put(JsonParser.PROJECT_COMMENT, "The first project");
		project3.put(JsonParser.PROJECT_DISPLAY_NAME, "Project1 Display");

		JSONObject project4 = new JSONObject();
		project4.put(JsonParser.PROJECT_CREATE_TIME, "1432109256540");
		project4.put(JsonParser.PROJECT_ATTACH_MAX_SIZE, 2L);
		project4.put(JsonParser.PROJECT_PRODUCT_OWNER, "Sam");
		projects_2.add(project3);
		projects_2.add(project4);
		wholeJson2.put("projects", projects_2);

		// create test data for invalid type file
		FileWriter file3 = new FileWriter(FILE_NAME_INVALID_KEY);
		JSONObject wholeJson3 = new JSONObject();
		JSONArray projects_3 = new JSONArray();
		JSONObject project5 = new JSONObject();
		project5.put(JsonParser.PROJECT_CREATE_TIME, "1432109256540");
		project5.put(JsonParser.PROJECT_ATTACH_MAX_SIZE, "2");
		project5.put(JsonParser.PROJECT_PRODUCT_OWNER, 123);
		project5.put(JsonParser.PROJECT_NAME, "Project1");
		project5.put(JsonParser.PROJECT_COMMENT, "The first project");
		project5.put(JsonParser.PROJECT_DISPLAY_NAME, "Project1 Display");

		JSONObject project6 = new JSONObject();
		project6.put(JsonParser.PROJECT_CREATE_TIME, 1432109256540L);
		project6.put(JsonParser.PROJECT_ATTACH_MAX_SIZE, 2L);
		project6.put(JsonParser.PROJECT_PRODUCT_OWNER, "Sam");
		project6.put(JsonParser.PROJECT_NAME, 1);
		project6.put(JsonParser.PROJECT_COMMENT, 2);
		project6.put(JsonParser.PROJECT_DISPLAY_NAME, 3);
		projects_3.add(project5);
		projects_3.add(project6);
		wholeJson3.put("projects", projects_3);

		// create test data for invalid file format
		FileWriter file4 = new FileWriter(FILE_NAME_INVALID_FILE_FORMAT);
		try {
			file1.write(wholeJson1.toJSONString());
			file2.write(wholeJson2.toJSONString());
			file3.write(wholeJson3.toJSONString());
			file4.write("gi");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			file1.flush();
			file1.close();
			file2.flush();
			file2.close();
			file3.flush();
			file3.close();
			file4.flush();
			file4.close();
		}
	}

	@After
	public void tearDown() {
		// clear database
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		mConfig = null;

		// clear test data
		File file1 = new File(FILE_NAME_VALID);
		if (file1.exists()) {
			file1.delete();
		}
		File file2 = new File(FILE_NAME_INVALID_KEY);
		if (file2.exists()) {
			file2.delete();
		}
		File file3 = new File(FILE_NAME_INVALID_TYPE);
		if (file3.exists()) {
			file3.delete();
		}
		File file4 = new File(FILE_NAME_INVALID_FILE_FORMAT);
		if (file4.exists()) {
			file4.delete();
		}
	}

	 @Test
	 public void testIsValid_valid() {
	 EzScrumFileChecker ezScrumFileChecker = new EzScrumFileChecker(
	 new File(FILE_NAME_VALID));
	 assertTrue(ezScrumFileChecker.isValid());
	 }
	
	 @Test
	 public void testIsValid_invalidKey() {
	 EzScrumFileChecker ezScrumFileChecker = new EzScrumFileChecker(
	 new File(FILE_NAME_INVALID_KEY));
	 assertFalse(ezScrumFileChecker.isValid());
	 }
	
	 @Test
	 public void testIsValid_invalidType() {
	 EzScrumFileChecker ezScrumFileChecker = new EzScrumFileChecker(
	 new File(FILE_NAME_INVALID_TYPE));
	 assertFalse(ezScrumFileChecker.isValid());
	 }
	
	 @Test
	 public void testGetReport_valid() {
	 EzScrumFileChecker ezScrumFileChecker = new EzScrumFileChecker(
	 new File(FILE_NAME_VALID));
	 assertEquals("", ezScrumFileChecker.getReport());
	 }
	
	 @Test
	 public void testGetReport_invalidKey() {
	 EzScrumFileChecker ezScrumFileChecker = new EzScrumFileChecker(
	 new File(FILE_NAME_INVALID_KEY));
	 assertEquals(
	 "Missing key: createTime, attachMaxSize, productOwner, name, comment, displayName",
	 ezScrumFileChecker.getReport());
	 }
	
	 @Test
	 public void testGetReport_invalidType() {
	 EzScrumFileChecker ezScrumFileChecker = new EzScrumFileChecker(
	 new File(FILE_NAME_INVALID_TYPE));
	 assertEquals(
	 "Invalid type of key: createTime, attachMaxSize, productOwner, name, comment, displayName",
	 ezScrumFileChecker.getReport());
	 }
	
	@Test
	public void testGetReport_invalidFormat() throws IOException {
		EzScrumFileChecker ezScrumFileChecker = new EzScrumFileChecker(
				new File(FILE_NAME_INVALID_FILE_FORMAT));
		assertEquals("File format error", ezScrumFileChecker.getReport());
	}
}
