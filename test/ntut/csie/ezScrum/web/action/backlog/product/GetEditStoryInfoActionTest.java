package ntut.csie.ezScrum.web.action.backlog.product;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class GetEditStoryInfoActionTest extends MockStrutsTestCase{
	
	private CreateProject CP;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private final String ACTION_PATH = "/getEditStoryInfo";
	private IProject project;
	
	public GetEditStoryInfoActionTest(String testName) {
		super(testName);
	}
	
	protected void setUp() throws Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案
		this.project = this.CP.getProjectList().get(0);
		
		super.setUp();
		
		// ================ set action info ========================
		setContextDirectory( new File(config.getBaseDirPath()+ "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo( this.ACTION_PATH );
		
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(this.config.getTestDataPath());

		super.tearDown();
		
		ini = null;
		projectManager = null;
		this.CP = null;
	}
	
	public void testGetEditStoryInformation_Stories(){
		int storyCount = 2;
		CreateProductBacklog createProductBacklog = new CreateProductBacklog(storyCount, this.CP);
		createProductBacklog.exe();
		
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("issueID", String.valueOf(createProductBacklog.getIssueIDList().get(1)));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		String expectedResponseText =
			"<ProductBacklog>" +
				"<Total>1</Total>" +
				"<Story>" +
					"<Id>2</Id>" +
					"<Link>/ezScrum/showIssueInformation.do?issueID=2</Link>" +
					"<Name>TEST_STORY_2</Name>" +
					"<Value>50</Value>" +
					"<Importance>100</Importance>" +
					"<Estimation>2</Estimation>" +
					"<Status>new</Status>" +
					"<Notes>TEST_STORY_NOTE_2</Notes>" +
					"<HowToDemo>TEST_STORY_DEMO_2</HowToDemo>" +
					"<Release>None</Release>" +
					"<Sprint>None</Sprint>" +
					"<Tag></Tag>" +
					"<Attach>false</Attach>" +
				"</Story>" +
			"</ProductBacklog>";
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
	}
}
