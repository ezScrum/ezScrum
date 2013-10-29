package ntut.csie.ezScrum.web.action.project;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.project.core.IProjectDescription;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class ViewProjectListActionTest extends MockStrutsTestCase{
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	
	public ViewProjectListActionTest(String testMethod){
		super(testMethod);
	}
	
	protected void setUp() throws Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		CreateProject CP = new CreateProject(2);
		CP.exeCreate(); // 新增一測試專案
		
		super.setUp();

		ini = null;
		CP = null;
	}
	
	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(this.config.getTestDataPath());

		super.tearDown();
		
		ini = null;
		projectManager = null;
	}
	
	public void testViewProjectList(){
		// ================ set action info ========================
		setContextDirectory( new File(config.getBaseDirPath() + "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/viewProjectList");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		//	assert response text
		ProjectMapper projectMapper = new ProjectMapper();
		IProject projectOne = projectMapper.getProjectByID("TEST_PROJECT_1");
		IProject projectTwo = projectMapper.getProjectByID("TEST_PROJECT_2");
		IProjectDescription projectOneDesc = projectOne.getProjectDesc();
		IProjectDescription projectTwoDesc = projectTwo.getProjectDesc();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String expectResponseText = 
			"<Projects>" +
				"<Project>" +
					"<ID>TEST_PROJECT_1</ID>" +
					"<Name>TEST_PROJECT_1</Name>" +
					"<Comment>This is Test Project - 1</Comment>" +
					"<ProjectManager>Project_Manager_1</ProjectManager>" +
					"<CreateDate>" + dateFormat.format( projectOneDesc.getCreateDate() ) + "</CreateDate>" +
					"<DemoDate>No Plan!</DemoDate>" +
				"</Project>" +
				"<Project>" +
					"<ID>TEST_PROJECT_2</ID>" +
					"<Name>TEST_PROJECT_2</Name>" +
					"<Comment>This is Test Project - 2</Comment>" +
					"<ProjectManager>Project_Manager_2</ProjectManager>" +
					"<CreateDate>" + dateFormat.format( projectTwoDesc.getCreateDate() ) + "</CreateDate>" +
					"<DemoDate>No Plan!</DemoDate>" +
				"</Project>" +
			"</Projects>";
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectResponseText, actualResponseText);
	}
}
