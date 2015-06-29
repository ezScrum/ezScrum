package ntut.csie.ezScrum.web.action.project;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import servletunit.struts.MockStrutsTestCase;

public class ModifyProjectActionTest extends MockStrutsTestCase{
	private CreateProject mCP;
	private Configuration mConfig;
	
	public ModifyProjectActionTest(String testMethod){
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mCP = new CreateProject(1);
		mCP.exeCreate(); // 新增一測試專案
		
		super.setUp();
	}

	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();
		
		// release
		mCP = null;
		mConfig = null;
	}
	
	public void testModifyProjectInformation(){
		// ================ set action info ========================
		setContextDirectory( new File(mConfig.getBaseDirPath()+ "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/ModifyProjectDescription");
		
		// ================ set request info ========================
		String expectProjectDisplayName = "update project display name";
		long expectProjectAttachFileSize = 5;
		String expectProjectComment = "update project comment";
		String expectProjectManager = "update project manager";
		addRequestParameter("ProjectDisplayName", expectProjectDisplayName);
		addRequestParameter("AttachFileSize", String.valueOf(expectProjectAttachFileSize));
		addRequestParameter("Commnet", expectProjectComment);
		addRequestParameter("ProjectManager", expectProjectManager);
		
		ProjectObject testProject = mCP.getAllProjects().get(0);
		String projectName = testProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		
		// ================ set session info ========================
		request.getSession().setAttribute(projectName, testProject);
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		//	assert response text
		String expectResponseText = "success";
		String acutalResponseText = response.getWriterBuffer().toString();
		assertEquals(expectResponseText, acutalResponseText);
		
		//	assert database information
		ProjectMapper projectMapper = new ProjectMapper();
		ProjectObject actualProject = projectMapper.getProject(projectName);
		assertEquals(projectName, actualProject.getName());
		assertEquals(expectProjectDisplayName, actualProject.getDisplayName());
		assertEquals(expectProjectAttachFileSize, actualProject.getAttachFileSize());
		assertEquals(expectProjectComment, actualProject.getComment());
		assertEquals(expectProjectManager, actualProject.getManager());
	}
}