package ntut.csie.ezScrum.web.action.plan.sprint;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxShowStoryFromSprintActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateRelease mCR;
	private CreateSprint mCS;
	private Configuration mConfig;
	private final String mActionPath = "/AjaxShowStoryfromSprint";
	private IProject mProject;
	
	public AjaxShowStoryFromSprintActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 新增一測試專案
    	mCP = new CreateProject(1);
    	mCP.exeCreate();
    	mProject = mCP.getProjectList().get(0);
    	
		// 新增一筆Release Plan
    	mCR = new CreateRelease(1, mCP);
    	mCR.exe();
    	
		// 新增二筆Sprint Plan
    	mCS = new CreateSprint(2, mCP);
    	mCS.exe();
    	
    	super.setUp();
    	
		// 設定讀取的 struts-config 檔案路徑
    	setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	setRequestPathInfo( mActionPath );
    	
    	// ============= release ==============
    	ini = null;
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
    	
    	// ============= release ==============
    	ini = null;
    	mCP = null;
    	mCR = null;
    	mCS = null;
    	mConfig = null;
    	
    	super.tearDown();
    }
    
	/**
	 * no story
	 */
	public void testShowStoryFromSprint_1(){
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("Sid", String.valueOf(mCS.getSprintsId().get(0)));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		String expectedResponseText = "<ExistingStories></ExistingStories>";
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
	}
	
	public void testShowStoryFromSprint_2() throws Exception{
		int storycount = 2;
		
		AddStoryToSprint ASTS = new AddStoryToSprint(storycount, 2, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		ASTS.exe();
		
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("Sid", String.valueOf(mCS.getSprintsId().get(0)));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText
					.append("<ExistingStories>")
						.append("<Story>")
							.append("<Id>1</Id>")
							.append("<Link></Link>")
							.append("<Name>TEST_STORY_1</Name>")
							.append("<Value>50</Value>")
							.append("<Importance>100</Importance>")
							.append("<Estimate>2</Estimate>")
							.append("<Status>new</Status>")
							.append("<Notes>TEST_STORY_NOTE_1</Notes>")
							.append("<HowToDemo>TEST_STORY_DEMO_1</HowToDemo>")
							.append("<Release></Release>")
							.append("<Sprint>1</Sprint>")
							.append("<Tag></Tag>")
						.append("</Story>")
						.append("<Story>")
							.append("<Id>2</Id>")
							.append("<Link></Link>")
							.append("<Name>TEST_STORY_2</Name>")
							.append("<Value>50</Value>")
							.append("<Importance>100</Importance>")
							.append("<Estimate>2</Estimate>")
							.append("<Status>new</Status>")
							.append("<Notes>TEST_STORY_NOTE_2</Notes>")
							.append("<HowToDemo>TEST_STORY_DEMO_2</HowToDemo>")
							.append("<Release></Release>")
							.append("<Sprint>1</Sprint>")
							.append("<Tag></Tag>")
						.append("</Story>")
					.append("</ExistingStories>");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
