package ntut.csie.ezScrum.web.action.export;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxGetReleasePlanActionTest extends MockStrutsTestCase{
	private CreateProject CP;
	private CreateRelease CR;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private final String ACTION_PATH = "/ajaxGetReleasePlan";
	private IProject project;
	
	public AjaxGetReleasePlanActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe();											// 初始化 SQL
		
    	this.CP = new CreateProject(1);
    	this.CP.exeCreate();								// 新增一測試專案
		this.project = this.CP.getProjectList().get(0);
		
    	super.setUp();
    	
    	setContextDirectory(new File(config.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(this.ACTION_PATH);
    	
    	// ============= release ==============
    	ini = null;
    }
	
    protected void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe();											// 初始化 SQL
		
    	CopyProject copyProject = new CopyProject(this.CP);
    	copyProject.exeDelete_Project();					// 刪除測試檔案
    	
    	// ============= release ==============
    	ini = null;
    	copyProject = null;
    	this.CP = null;
    	this.CR = null;
    	
    	super.tearDown();
    }
    
	/**
	 * project中沒有任何release plan的時候
	 */
	public void testAjaxGetReleasePlanAction_1() {
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());

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
		this.CR = new CreateRelease(1, this.CP);
		this.CR.exe();										// 新增一個release plan
		IProject project = this.CP.getProjectList().get(0);

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
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
    	this.CR = new CreateRelease(5, this.CP);
    	this.CR.exe();										// 新增5個release plan
		IProject project = this.CP.getProjectList().get(0);
//		Thread.sleep(5000);
    	
    	// ================ set session info ========================
    	request.getSession().setAttribute("UserSession", config.getUserSession());
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
