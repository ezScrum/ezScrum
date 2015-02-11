package ntut.csie.ezScrum.web.action.project;

import java.io.File;
import java.io.IOException;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.project.core.IProjectDescription;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class SaveProjectActionTest extends MockStrutsTestCase {
	private Configuration mConfig;
	
	public SaveProjectActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
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
		mConfig = null;
	}
	
//	/**
//	 * 測試錯誤的資料庫帳號密碼
//	 */
//	public void testWrongDBAccountInformation(){
//		assertTrue(false);
//	}
//	
//	/**
//	 * 測試錯誤的資料庫型態
//	 */
//	public void testWrongDBTypeInformation(){
//		assertTrue(false);
//	}
	
	/**
	 * 測試所有資訊皆正確，並建立專案
	 */
	public void testCreateProject(){
		// ================ set action info ========================
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent")); // 設定讀取的struts-config檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/AjaxCreateProject");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ set request info ========================
		//	設定專案資訊
		String projectName = "test";
		String projectDisplayName = "Project for Test Create Project";
		String comment = "";
		String projectManager = "ezScrum tester";
		String attachFileSize = "";
		addRequestParameter("Name", projectName);
		addRequestParameter("DisplayName", projectDisplayName);
		addRequestParameter("Comment", comment);
		addRequestParameter("ProjectManager", projectManager);
		addRequestParameter("AttachFileSize", attachFileSize);
		addRequestParameter("from", "createProject");
		
		//	設定ITS參數資料
		addRequestParameter("ServerUrl", mConfig.getServerUrl());
		addRequestParameter("ServicePath", mConfig.getWebServicePath());
		addRequestParameter("DBAccount", mConfig.getDBAccount());
		addRequestParameter("DBPassword", mConfig.getDBPassword());
		addRequestParameter("SQLType", mConfig.getDBType());
		addRequestParameter("DBName", mConfig.getDBName());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		//	assert response text
		String actualResponseText = response.getWriterBuffer().toString();
		String expectResponseText = 
			"<Root>" +
				"<CreateProjectResult>" +
				"<Result>Success</Result>" +
				"<ID>test</ID>" +
				"</CreateProjectResult>" +
			"</Root>";
		assertEquals(expectResponseText, actualResponseText);
		
		//	assert database information
		ProjectMapper projectMapper = new ProjectMapper();
		IProject project = projectMapper.getProjectByID(projectName);
		IProjectDescription projectDesc = project.getProjectDesc();
		assertEquals(projectName, projectDesc.getName() );
		assertEquals(projectDisplayName, projectDesc.getDisplayName() );
		assertEquals("2", projectDesc.getAttachFileSize() );
		assertEquals(comment, projectDesc.getComment() );
		assertEquals(projectManager, projectDesc.getProjectManager() );
		
		//	assert 外部檔案路徑及檔名，因為外部檔案已經在ProjectMapper.getProjectByID(projectName)集中管理，所以不需再重底層撈資料。
//		IWorkspace workspace = ResourceFacade.getWorkspace();
//		IWorkspaceRoot root = workspace.getRoot();
//    	IProject Actual = root.getProject(projectName);
//    	
//    	assertEquals(Actual.getName(), project.getName());
//    	assertEquals(Actual.getFullPath(), project.getFullPath());
	}
}
