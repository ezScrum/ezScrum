package ntut.csie.ezScrum.SaaS.IntegationTest;

import javax.jdo.PersistenceManager;

import ntut.csie.ezScrum.SaaS.PMF;
import ntut.csie.ezScrum.SaaS.database.ProjectDataStore;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class ModifyProjectActionGAETest extends MockStrutsTestCase {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private ezScrumGAEConfig config = new ezScrumGAEConfig();
	private final String expectedProjectID = "test";
	private final String expectedProjectDisplayName = "Project for Test Create Project";
	private final String expectedProjectComment = "";
	private final String expectedProjectManager = "ezScrum tester";
	private final String attachFileSize = "";
	
	public ModifyProjectActionGAETest(String method) {
		super(method);
	}

	protected void setUp() throws Exception {
		super.setUp();
		helper.setUp();
    	setContextDirectory(this.config.getWebContentFile());		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile(this.config.getServletConfigFilePath());
	}

	protected void tearDown() throws Exception {
		helper.tearDown();
		super.tearDown();
	}

	
	public void testModifyProjectInformation(){
		IProject project = this.createProjectByAction();
		
		// ================ set action info ========================
		this.cleanActionInformation();
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
		
		String projectName = project.getName();
		request.setHeader("Referer", "?PID=" + project.getName());
		
		// ================ set session info ========================
		request.getSession().setAttribute( projectName, project );
		request.getSession().setAttribute("UserSession", this.config.getAdminSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		//	assert response text
		String expectResponseText = "success";
		String acutalResponseText = response.getWriterBuffer().toString();
		assertEquals(expectResponseText, acutalResponseText);
		
		//	assert database information
		ProjectDataStore projectDataStore = this.getProjectDS(projectName);
		assertEquals(projectName, projectDataStore.getName() );
		assertEquals(expectProjectDisplayName, projectDataStore.getDisplayName() );
		assertEquals(expectProjectComment, projectDataStore.getComment() );
		assertEquals(expectProjectManager, projectDataStore.getManager() );
	}
	
	private IProject createProjectByAction(){
		/**
		 * 1. admin 建立專案
		 */
		// ================ set action info ========================
		setRequestPathInfo("/AjaxCreateProject");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", this.config.getAdminSession());
		
		// ================ set request info ========================
		//	設定專案資訊
		addRequestParameter("Name", this.expectedProjectID);
		addRequestParameter("DisplayName", this.expectedProjectDisplayName);
		addRequestParameter("Comment", this.expectedProjectComment);
		addRequestParameter("ProjectManager", this.expectedProjectManager);
		addRequestParameter("AttachFileSize", this.attachFileSize);
		addRequestParameter("from", "createProject");
		
		//	設定ITS參數資料
		addRequestParameter("ServerUrl", this.config.SERVER_URL);
		addRequestParameter("ServicePath", this.config.SERVER_PATH);
		addRequestParameter("DBAccount", this.config.SERVER_ACCOUNT);
		addRequestParameter("DBPassword", this.config.SERVER_PASSWORD);
		addRequestParameter("SQLType", this.config.DATABASE_TYPE);
		addRequestParameter("DBName", this.config.DATABASE_NAME);
		
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
		
		IProject project = (new ProjectMapper()).getProjectByID( expectedProjectID );
		return project;
	}
	
	/**
	 * clean previous action info
	 */
	private void cleanActionInformation(){
		clearRequestParameters();
		this.response.reset();
	}
	
	private ProjectDataStore getProjectDS(String projectId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Key projectKey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), projectId);
		ProjectDataStore projectDS;
		
		try {
			projectDS = pm.getObjectById(ProjectDataStore.class, projectKey);

		} finally {
			pm.close();
		}
		
		return projectDS;
	}
}
