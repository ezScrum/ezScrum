package ntut.csie.ezScrum.web.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ntut.csie.ezScrum.dao.ProjectDAO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EzScrumImporterTest {
	private static String FILE_NAME = "test/ntut/csie/ezScrum/web/importer/ezScrum_backup.json";
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws IOException {
		FileWriter file = new FileWriter(FILE_NAME);
		JSONObject wholeJson = new JSONObject();
        JSONArray projects = new JSONArray();
        JSONObject project = new JSONObject();
        project.put(JsonParser.PROJECT_CREATE_TIME, 1432109256540L);
        project.put(JsonParser.PROJECT_ATTACH_MAX_SIZE, 2L);
        project.put(JsonParser.PROJECT_PRODUCT_OWNER, "Jay");
        project.put(JsonParser.PROJECT_NAME, "Project1");
        project.put(JsonParser.PROJECT_COMMENT, "The first project");
        project.put(JsonParser.PROJECT_DISPLAY_NAME, "Project1 Display");
        projects.add(project);
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
		assertEquals(1, ProjectDAO.getInstance().getAllProjects().size());
	}
}
