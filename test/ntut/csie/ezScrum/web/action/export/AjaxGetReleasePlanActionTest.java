package ntut.csie.ezScrum.web.action.export;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.jcis.resource.core.IProject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import servletunit.struts.MockStrutsTestCase;

public class AjaxGetReleasePlanActionTest extends MockStrutsTestCase{
	private CreateProject mCP;
	private CreateRelease mCR;
	private Configuration mConfig;
	private final String mActionPath = "/ajaxGetReleasePlan";
	private IProject mProject;
	
	public AjaxGetReleasePlanActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL
		
    	mCP = new CreateProject(1);
    	mCP.exeCreate();								// 新增一測試專案
		mProject = mCP.getProjectList().get(0);
		
    	super.setUp();
    	
    	setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);
    	
    	// ============= release ==============
    	ini = null;
    }
	
    protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL
		
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
    	
    	mConfig.setTestMode(false);
		mConfig.save();
    	
    	// ============= release ==============
    	ini = null;
    	mCP = null;
    	mCR = null;
    	mConfig = null;
    	
    	super.tearDown();
    }
    
	/**
	 * project中沒有任何release plan的時候
	 */
	public void testAjaxGetReleasePlanAction_1() {
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ===============================
		actionPerform();

		// ================ assert ==================================
		verifyNoActionErrors();
		verifyNoActionMessages();
		// assert response text
		/**
		 * {"Releases":[]}
		 */
		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("{")
		        .append("\"Releases\":[]")
		        .append("}");
		String actualResponseTest = response.getWriterBuffer().toString();
		assertEquals(expectedResponseTest.toString(), actualResponseTest);
	}

	/**
	 * project中有1個release plan的時候
	 */
	public void testAjaxGetReleasePlanAction_2() {
		// ================ set initial data =======================
		mCR = new CreateRelease(1, mCP);
		mCR.exe();										// 新增一個release plan
		ProjectObject project = mCP.getAllProjects().get(0);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName());

		// ================ 執行 action ===============================
		actionPerform();

		// ================ assert ==================================
		verifyNoActionErrors();
		verifyNoActionMessages();
		// assert response text
		/**
		 * {"Releases":[{"ID":"1","Name":"TEST_RELEASE_1"}]}
		 */
		ReleasePlanHelper RPhelper = new ReleasePlanHelper(project);
		List<IReleasePlanDesc> ReleasePlans = RPhelper.loadReleasePlansList();
		JSONObject releaseObject = new JSONObject();
		JSONArray releaseplanlist = new JSONArray();
		int releasecount = 0;
		try {
			for (IReleasePlanDesc plan : ReleasePlans) {
				JSONObject releaseplan = new JSONObject();
				releaseplan.put("ID", plan.getID());
				releaseplan.put("Name", plan.getName());
				releaseplanlist.put(releaseplan);
			}
			releaseObject.put("Releases", releaseplanlist);
			releasecount++;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		assertEquals(1, releasecount);
		String expectedResponseTest = releaseObject.toString();
		String actualResponseTest = response.getWriterBuffer().toString();
		assertEquals(expectedResponseTest, actualResponseTest);
	}
    
    /**
     * project中有5個release plan的時候
     */
    public void testAjaxGetReleasePlanAction_3() throws InterruptedException {
    	// ================ set initial data =======================
    	mCR = new CreateRelease(5, mCP);
    	mCR.exe();										// 新增5個release plan
    	ProjectObject project = mCP.getAllProjects().get(0);
		Thread.sleep(1000);
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", mConfig.getUserSession());
    	request.getSession().setAttribute("Project", project);
    	request.setHeader("Referer", "?PID=" + project.getName());
    	
    	// ================ 執行 action ===============================
    	actionPerform();
    	
    	// ================ assert ==================================
    	verifyNoActionErrors();
    	verifyNoActionMessages();
    	// assert response text
    	/**
    	 * {"Releases":[
    	 * 		{"ID":"1","Name":"TEST_RELEASE_1"},
    	 * 		{"ID":"2","Name":"TEST_RELEASE_2"},
    	 * 		{"ID":"3","Name":"TEST_RELEASE_3"},
    	 * 		{"ID":"4","Name":"TEST_RELEASE_4"},
    	 * 		{"ID":"5","Name":"TEST_RELEASE_5"}
    	 * ]}
    	 */
    	ReleasePlanHelper RPhelper = new ReleasePlanHelper(project);
    	List<IReleasePlanDesc> ReleasePlans = RPhelper.loadReleasePlansList();
		JSONObject releaseObject = new JSONObject();
    	JSONArray releaseplanlist = new JSONArray();
    	int releasecount = 0;
    	try {
			for (IReleasePlanDesc plan : ReleasePlans) {
				JSONObject releaseplan = new JSONObject();
				releaseplan.put("ID", plan.getID());
		        releaseplan.put("Name", plan.getName());
				releaseplanlist.put(releaseplan);
		        releasecount++;
			}
			releaseObject.put("Releases", releaseplanlist);
    	} catch (JSONException e) {
            e.printStackTrace();
        }
    	assertEquals(5, releasecount);
    	String expectedResponseTest = releaseObject.toString();
    	String actualResponseTest = response.getWriterBuffer().toString();
    	assertEquals(expectedResponseTest, actualResponseTest);
    }
}
