package ntut.csie.ezScrum.restful.service;

import static org.junit.Assert.*;
import java.util.List;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.restful.mobile.service.ProjectWebService;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.account.core.LogonException;
import ntut.csie.jcis.resource.core.IProject;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProjectWebServiceTest {
	private Configuration mConfig;
	private ProjectWebService mProjectWebService;
	private CreateProject mCP;
	
	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
	}
	
	@After
	public void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mConfig.setTestMode(false);
		mConfig.save();
		
		// release
		mConfig = null;
		mProjectWebService = null;
		mCP = null;
	}
	
	@Test
	public void testgetProjectList() throws LogonException {
		String username = "admin";
		String userpwd = "admin";
		// 新增Project
		int ProjectCount = 5;
		mCP = new CreateProject(ProjectCount);
		mCP.exeCreate();
		mProjectWebService = new ProjectWebService(username, userpwd);
		assertEquals(ProjectCount, mProjectWebService.getProjectList().size());
	}
	
	@Test
	public void testgetRESTFulResponseString() throws LogonException, JSONException {
		String username = "admin";
		String userpwd = "admin";
		String demodate = "No Plan!";
		// 新增Project
		int ProjectCount = 5;
		mCP = new CreateProject(ProjectCount);
		mCP.exeCreate();
		mProjectWebService = new ProjectWebService(username, userpwd);
		JSONObject object=  new JSONObject(mProjectWebService.getRESTFulResponseString());
		JSONArray array = object.getJSONArray("Projects");
		List<IProject> projectlist = mProjectWebService.getProjectList();
		
		for(int i = 0; i < ProjectCount; i++) {
			JSONObject project = array.getJSONObject(i).getJSONObject("Project");
			assertEquals(projectlist.get(i).getProjectDesc().getName(), project.getString("id"));
			assertEquals(projectlist.get(i).getProjectDesc().getDisplayName(), project.getString("Name"));
			assertEquals(projectlist.get(i).getProjectDesc().getComment(), project.getString("Comment"));
			assertEquals(projectlist.get(i).getProjectDesc().getProjectManager(), project.getString("ProjectManager"));
			assertEquals(demodate, project.getString("DemoDate"));
		}
	}
}
