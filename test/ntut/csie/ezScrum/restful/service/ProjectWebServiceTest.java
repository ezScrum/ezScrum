package ntut.csie.ezScrum.restful.service;

import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.restful.mobile.service.ProjectWebService;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.jcis.account.core.LogonException;
import ntut.csie.jcis.resource.core.IProject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ProjectWebServiceTest extends TestCase {
	private CreateProject CP;
	private ProjectWebService pService = null;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	
	public ProjectWebServiceTest(String testMethod) {
		super(testMethod);
	}
	
	protected void setUp() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		super.setUp();
		
		// release
		ini = null;
	}
	
	protected void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		super.setUp();
		
		// release
		ini = null;
	}
	
	public void testgetProjectList() throws LogonException {
		String username = "admin";
		String userpwd = "admin";
		// 新增Project
		int ProjectCount = 5;
		CP = new CreateProject(ProjectCount);
		CP.exeCreate();
		pService = new ProjectWebService(username, userpwd);
		assertEquals(ProjectCount, pService.getProjectList().size());
	}
	
	public void testgetRESTFulResponseString() throws LogonException, JSONException {
		String username = "admin";
		String userpwd = "admin";
		String demodate = "No Plan!";
		// 新增Project
		int ProjectCount = 5;
		CP = new CreateProject(ProjectCount);
		CP.exeCreate();
		pService = new ProjectWebService(username, userpwd);
		JSONObject object=  new JSONObject(pService.getRESTFulResponseString());
		JSONArray array = object.getJSONArray("Projects");
		List<IProject> projectlist = pService.getProjectList();
		
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
