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
	private CreateProject CP;
	private CreateRelease CR;
	private CreateSprint CS;
	private Configuration configuration;
	private final String ACTION_PATH = "/AjaxShowStoryfromSprint";
	private IProject project;
	
	public AjaxShowStoryFromSprintActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
    	this.CP = new CreateProject(1);
    	this.CP.exeCreate();								// 新增一測試專案
    	this.project = this.CP.getProjectList().get(0);
    	
    	this.CR = new CreateRelease(1, this.CP);
    	this.CR.exe();										// 新增一筆Release Plan
    	
    	this.CS = new CreateSprint(2, this.CP);
    	this.CS.exe();										// 新增二筆Sprint Plan
    	
    	super.setUp();
    	
    	setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
    	setServletConfigFile("/WEB-INF/struts-config.xml");
    	setRequestPathInfo( this.ACTION_PATH );
    	
    	// ============= release ==============
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
		configuration.store();
    	
    	// ============= release ==============
    	ini = null;
    	this.CP = null;
    	this.CR = null;
    	this.CS = null;
    	configuration = null;
    	
    	super.tearDown();
    }
    
	/**
	 * no story
	 */
	public void testShowStoryFromSprint_1(){
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("Sid", this.CS.getSprintIDList().get(0));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
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
		
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(storycount, 2, this.CS, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStoryToSprint.exe();
		
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("Sid", this.CS.getSprintIDList().get(0));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
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
							.append("<Link>/ezScrum/showIssueInformation.do?issueID=1</Link>")
							.append("<Name>TEST_STORY_1</Name>")
							.append("<Value>50</Value>")
							.append("<Importance>100</Importance>")
							.append("<Estimate>2</Estimate>")
							.append("<Status>new</Status>")
							.append("<Notes>TEST_STORY_NOTE_1</Notes>")
							.append("<HowToDemo>TEST_STORY_DEMO_1</HowToDemo>")
							.append("<Release>None</Release>")
							.append("<Sprint>1</Sprint>")
							.append("<Tag></Tag>")
						.append("</Story>")
						.append("<Story>")
							.append("<Id>2</Id>")
							.append("<Link>/ezScrum/showIssueInformation.do?issueID=2</Link>")
							.append("<Name>TEST_STORY_2</Name>")
							.append("<Value>50</Value>")
							.append("<Importance>100</Importance>")
							.append("<Estimate>2</Estimate>")
							.append("<Status>new</Status>")
							.append("<Notes>TEST_STORY_NOTE_2</Notes>")
							.append("<HowToDemo>TEST_STORY_DEMO_2</HowToDemo>")
							.append("<Release>None</Release>")
							.append("<Sprint>1</Sprint>")
							.append("<Tag></Tag>")
						.append("</Story>")
					.append("</ExistingStories>");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
