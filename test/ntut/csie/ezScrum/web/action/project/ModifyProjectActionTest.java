package ntut.csie.ezScrum.web.action.project;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.project.core.IProjectDescription;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class ModifyProjectActionTest extends MockStrutsTestCase{
	private CreateProject CP;
	private Configuration configuration;
	
	public ModifyProjectActionTest(String testMethod){
		super(testMethod);
	}

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		
		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案
		
		super.setUp();

		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());
		
		configuration.setTestMode(false);
		configuration.save();

		super.tearDown();
		
		ini = null;
		projectManager = null;
		configuration = null;
	}
	
	public void testModifyProjectInformation(){
		// ================ set action info ========================
		setContextDirectory( new File(configuration.getBaseDirPath()+ "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/ModifyProjectDescription");
		
		// ================ set request info ========================
		String expectProjectDisplayName = "update project display name";
		String expectProjectAttachFileSize = "5";
		String expectProjectComment = "update project comment";
		String expectProjectManager = "update project manager";
		addRequestParameter("ProjectDisplayName", expectProjectDisplayName);
		addRequestParameter("AttachFileSize", expectProjectAttachFileSize);
		addRequestParameter("Commnet", expectProjectComment);
		addRequestParameter("ProjectManager", expectProjectManager);
		
		List<IProject> testProjectList = this.CP.getProjectList();
		IProject testProject = testProjectList.get(0);
		String projectName = testProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		
		// ================ set session info ========================
		request.getSession().setAttribute( projectName, testProject );
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		//	assert response text
		String expectResponseText = "success";
		String acutalResponseText = response.getWriterBuffer().toString();
		assertEquals(expectResponseText, acutalResponseText);
		
		//	assert database information
		ProjectMapper projectMapper = new ProjectMapper();
		IProject actualProject = projectMapper.getProjectByID(projectName);
		IProjectDescription actualProjectDesc = actualProject.getProjectDesc();
		assertEquals(projectName, actualProjectDesc.getName() );
		assertEquals(expectProjectDisplayName, actualProjectDesc.getDisplayName() );
		assertEquals(expectProjectAttachFileSize, actualProjectDesc.getAttachFileSize() );
		assertEquals(expectProjectComment, actualProjectDesc.getComment() );
		assertEquals(expectProjectManager, actualProjectDesc.getProjectManager() );
	}
}