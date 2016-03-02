package ntut.csie.ezScrum.web.action.backlog.product;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import servletunit.struts.MockStrutsTestCase;

public class GetEditStoryInfoActionTest extends MockStrutsTestCase{
	
	private CreateProject mCP;
	private Configuration mConfig;
	private final String mActionPath = "/getEditStoryInfo";
	private ProjectObject mProject;
	
	public GetEditStoryInfoActionTest(String testName) {
		super(testName);
	}
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mCP = new CreateProject(1);
		mCP.exeCreateForDb(); // 新增一測試專案
		mProject = mCP.getAllProjects().get(0);
		
		super.setUp();
		
		// ================ set action info ========================
		setContextDirectory( new File(mConfig.getBaseDirPath()+ "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo( mActionPath );
		
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();
		
		ini = null;
		mCP = null;
		mConfig = null;
	}
	
	public void testGetEditStoryInformation_Stories(){
		int storyCount = 2;
		CreateProductBacklog CPB = new CreateProductBacklog(storyCount, mCP);
		CPB.exe();
		
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?projectName=" + projectName);
		addRequestParameter("issueID", String.valueOf(CPB.getStoryIds().get(1)));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
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
					.append("<SerialId>2</SerialId>")
					.append("<Link></Link>")
					.append("<Name>TEST_STORY_2</Name>")
					.append("<Value>50</Value>")
					.append("<Importance>100</Importance>")
					.append("<Estimate>2</Estimate>")
					.append("<Status>new</Status>")
					.append("<Notes>TEST_STORY_NOTE_2</Notes>")
					.append("<HowToDemo>TEST_STORY_DEMO_2</HowToDemo>")
					.append("<Release></Release>")
					.append("<Sprint>None</Sprint>")
					.append("<Tag></Tag>")
					.append("<Attach>false</Attach>")
				.append("</Story>")
			.append("</ProductBacklog>");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
