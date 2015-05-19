package ntut.csie.ezScrum.restful.mobile.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConvertProjectTest {
	private CreateProject mCP;
	private Configuration mConfig = null;
	private int mProjectCount = 3;
	private ArrayList<ProjectObject> mProjects;
	private ConvertProject mConvertProject;

	@Before
	public void setUp() throws JSONException {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增 Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		mProjects = mCP.getAllProjects();
		mConvertProject = new ConvertProject();

		// ============= release ==============
		ini = null;
	}

	@After
	public void tearDown() {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mCP = null;
		projectManager = null;
		mConfig = null;
	}

	@Test
	public void testConvertProject() throws JSONException {
		for (ProjectObject project : mProjects) {
			mConvertProject.addProject(project);
		}
		String response = mConvertProject.getJSONString();

		JSONObject actualJson = new JSONObject(response);
		JSONArray projects = actualJson.getJSONArray("Projects");
		for (int i = 0; i < mProjectCount; i++) {
			JSONObject project = projects.getJSONObject(i).getJSONObject(
					"Project");
			ProjectObject projectObject = mProjects.get(i);
			assertEquals(projectObject.getName(), project.get("id"));
			assertEquals(projectObject.getDisplayName(), project.get("Name"));
			assertEquals(projectObject.getComment(), project.get("Comment"));
			assertEquals(projectObject.getManager(),
					project.get("ProjectManager"));
			assertNotNull(project.get("CreateDate"));
			assertEquals("No Plan!", project.get("DemoDate"));
		}
	}
}
