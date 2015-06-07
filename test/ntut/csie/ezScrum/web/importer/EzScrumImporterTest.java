package ntut.csie.ezScrum.web.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import ntut.csie.ezScrum.dao.ProjectDAO;
import ntut.csie.ezScrum.dao.SerialNumberDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SerialNumberObject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EzScrumImporterTest {
	private static String FILE_NAME = "test/ntut/csie/ezScrum/web/importer/ezScrum_backup.json";
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
		
		// create test data
		FileWriter file = new FileWriter(FILE_NAME);
		JSONObject wholeJson = new JSONObject();
        JSONArray projects = new JSONArray();
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
        projects.add(project1);
        projects.add(project2);
        wholeJson.put("projects", projects);
        try {
            file.write(wholeJson.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            file.flush();
            file.close();
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

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		mConfig = null;
		
		// clear test data
		File file = new File(FILE_NAME);
		if (file.exists()) {
			file.delete();
		}
	}
	
	@Test
	public void testImport() {
		assertEquals(0, ProjectDAO.getInstance().getAllProjects().size());
		File file = new File(FILE_NAME);
		EzScrumImporter ezScrumImporter = new EzScrumImporter(file);
		assertTrue(ezScrumImporter.doImport());
		ArrayList<ProjectObject> projects = ProjectDAO.getInstance().getAllProjects();
		assertEquals(2, projects.size());
		ProjectObject project1 = projects.get(0);
		SerialNumberObject serialNumberObject1 = SerialNumberDAO.getInstance().get(project1.getId());
		assertEquals(1, serialNumberObject1.getId());
		assertEquals(project1.getId(), serialNumberObject1.getProjectId());
		assertEquals(0, serialNumberObject1.getReleaseId());
		assertEquals(0, serialNumberObject1.getSprintId());
		assertEquals(0, serialNumberObject1.getStoryId());
		assertEquals(0, serialNumberObject1.getTaskId());
		assertEquals(0, serialNumberObject1.getUnplannedId());
		assertEquals(0, serialNumberObject1.getRetrospectiveId());
		
		ProjectObject project2 = projects.get(1);
		SerialNumberObject serialNumberObject2 = SerialNumberDAO.getInstance().get(project2.getId());
		assertEquals(2, serialNumberObject2.getId());
		assertEquals(project2.getId(), serialNumberObject2.getProjectId());
		assertEquals(0, serialNumberObject2.getReleaseId());
		assertEquals(0, serialNumberObject2.getSprintId());
		assertEquals(0, serialNumberObject2.getStoryId());
		assertEquals(0, serialNumberObject2.getTaskId());
		assertEquals(0, serialNumberObject2.getUnplannedId());
		assertEquals(0, serialNumberObject2.getRetrospectiveId());
	}
}
