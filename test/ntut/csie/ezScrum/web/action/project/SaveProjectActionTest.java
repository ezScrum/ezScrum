package ntut.csie.ezScrum.web.action.project;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
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
		
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();
		
		// release
		mConfig = null;
	}
	
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
		ProjectObject project = projectMapper.getProject(projectName);
		assertEquals(projectName, project.getName());
		assertEquals(projectDisplayName, project.getDisplayName());
		assertEquals(2L, project.getAttachFileSize());
		assertEquals(comment, project.getComment());
		assertEquals(projectManager, project.getManager());
	}
}
