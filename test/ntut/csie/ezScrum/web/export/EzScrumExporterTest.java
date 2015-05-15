package ntut.csie.ezScrum.web.export;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EzScrumExporterTest {
	private CreateProject CP;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();

	@Before
	public void setUp() throws Exception {
		config.DATABASE_NAME = "ezscrum_export_test";
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(config);
		ini.exe();

		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案

		ini = null;
	}

	@After
	public void tearDown() throws Exception {
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(config);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(this.config.getTestDataPath());

		ini = null;
		projectManager = null;
		this.CP = null;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetJsonFile() throws IOException {
		ArrayList<String> projectsName = new ArrayList<String>();
		projectsName.add(this.CP.getProjectList().get(0).getName());
		EzScrumExporter exporter = new EzScrumExporter(projectsName);
		File file = exporter.getJsonFile();
		JSONParser parser = new JSONParser();
		FileReader reader = new FileReader(file.getAbsoluteFile());
		System.out.println("file path: "+file.getAbsolutePath());
		try {
			JSONObject jsonObject = (JSONObject) parser.parse(reader);
			JSONArray projectsJsonArray = (JSONArray) jsonObject
					.get("projects");
			JSONObject projectJson = (JSONObject) projectsJsonArray.get(0);
			Assert.assertEquals("TEST_PROJECT_1", projectJson.get("name"));
			Assert.assertEquals("TEST_PROJECT_1",
					projectJson.get("displayName"));
			Assert.assertEquals("This is Test Project - 1",
					projectJson.get("comment"));
			Assert.assertEquals("Project_Manager_1",
					projectJson.get("productOwner"));
			Assert.assertEquals("2", projectJson.get("attachMaxSize"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			reader.close();
			Assert.assertTrue(file.delete());
		}
	}
}
