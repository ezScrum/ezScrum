package ntut.csie.ezScrum.web.action.project;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import servletunit.struts.MockStrutsTestCase;

public class ViewProjectListActionTest extends MockStrutsTestCase{
	private Configuration mConfig;
	
	public ViewProjectListActionTest(String testMethod){
		super(testMethod);
	}
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.store();
		
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 新增一測試專案
		CreateProject CP = new CreateProject(2);
		CP.exeCreate();
		
		super.setUp();

		ini = null;
		CP = null;
	}
	
	protected void tearDown() throws IOException, Exception {
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(mConfig.getDataPath());
		
		mConfig.setTestMode(false);
		mConfig.store();

		super.tearDown();
		
		ini = null;
		projectManager = null;
		mConfig = null;
	}
	
	public void testViewProjectList(){
		// ================ set action info ========================
		setContextDirectory( new File(mConfig.getBaseDirPath() + "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/viewProjectList");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		//	assert response text
		ProjectMapper projectMapper = new ProjectMapper();
		ProjectObject projectOne = projectMapper.getProjectByPidForDb("TEST_PROJECT_1");
		ProjectObject projectTwo = projectMapper.getProjectByPidForDb("TEST_PROJECT_2");
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String expectResponseText = 
			"<Projects>" +
				"<Project>" +
					"<ID>TEST_PROJECT_1</ID>" +
					"<Name>TEST_PROJECT_1</Name>" +
					"<Comment>This is Test Project - 1</Comment>" +
					"<ProjectManager>Project_Manager_1</ProjectManager>" +
					"<CreateDate>" + dateFormat.format(projectOne.getCreateDate()) + "</CreateDate>" +
					"<DemoDate>No Plan!</DemoDate>" +
				"</Project>" +
				"<Project>" +
					"<ID>TEST_PROJECT_2</ID>" +
					"<Name>TEST_PROJECT_2</Name>" +
					"<Comment>This is Test Project - 2</Comment>" +
					"<ProjectManager>Project_Manager_2</ProjectManager>" +
					"<CreateDate>" + dateFormat.format(projectTwo.getCreateDate()) + "</CreateDate>" +
					"<DemoDate>No Plan!</DemoDate>" +
				"</Project>" +
			"</Projects>";
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectResponseText, actualResponseText);
	}
}
