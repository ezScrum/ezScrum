package ntut.csie.ezScrum.web.action.plan;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import servletunit.struts.MockStrutsTestCase;

public class AddStorytoReleaseActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateRelease mCR;
	private CreateSprint mCS;
	private Configuration mConfig;
	
	public AddStorytoReleaseActionTest(String testMethod) {
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
    	
    	mCR = new CreateRelease(1, mCP);
    	mCR.exe();										// 新增一筆Release Plan
    	
    	mCS = new CreateSprint(2, mCP);
    	mCS.exe();										// 新增二筆Sprint Plan
    	
    	super.setUp();
    	
    	setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	
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
    	mCS = null;
    	mConfig = null;
    	
    	super.tearDown();
    }

    public void testExecute() throws Exception {
    	// ===================== 此 action 似乎尚未被使用 ===========================    	
    }
}
