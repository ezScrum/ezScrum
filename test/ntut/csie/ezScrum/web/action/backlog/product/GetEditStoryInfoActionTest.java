package ntut.csie.ezScrum.web.action.backlog.product;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class GetEditStoryInfoActionTest extends MockStrutsTestCase{
	
	private CreateProject CP;
	private Configuration configuration;
	private final String ACTION_PATH = "/getEditStoryInfo";
	private IProject project;
	
	public GetEditStoryInfoActionTest(String testName) {
		super(testName);
	}
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		
		this.CP = new CreateProject(1);
		this.CP.exeCreate(); // 新增一測試專案
		this.project = this.CP.getProjectList().get(0);
		
		super.setUp();
		
		// ================ set action info ========================
		setContextDirectory( new File(configuration.getBaseDirPath()+ "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo( this.ACTION_PATH );
		
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getTestDataPath());
		
		configuration.setTestMode(false);
		configuration.store();

		super.tearDown();
		
		ini = null;
		projectManager = null;
		this.CP = null;
		configuration = null;
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
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("<ProductBacklog>")
				.append("<Total>1</Total>")
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
					.append("<Sprint>None</Sprint>")
					.append("<Tag></Tag>")
					.append("<Attach>false</Attach>")
				.append("</Story>")
			.append("</ProductBacklog>");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
