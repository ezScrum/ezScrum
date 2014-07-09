package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class ShowExistedStoryActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private Configuration configuration;
	private final String ACTION_PATH = "/showExistedStory";
	private IProject project;
	
	public ShowExistedStoryActionTest(String testName) {
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
		
		this.CS = new CreateSprint(2, this.CP);
		this.CS.exe();
		
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
	
	/**
	 * no story
	 */
	public void testShowExistedStory_1(){
		String sprintID = this.CS.getSprintIDList().get(0);
		String releaseID = "-1";
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", sprintID);
		addRequestParameter("releaseID", releaseID);
		
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
	
	/**
	 * 至少存在兩個story以上
	 */
	public void testShowExistedStory_2(){
		int storycount = 2;
		CreateProductBacklog CPB = new CreateProductBacklog(storycount, this.CP);
		CPB.exe();
		
		String sprintID = this.CS.getSprintIDList().get(0);
		String releaseID = "-1";
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", sprintID);
		addRequestParameter("releaseID", releaseID);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		String releaseIDAndSprintID = "None";
		String storyStatus = "new";
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("<ExistingStories>")
							.append("<Story>")
							.append("<Id>" + 1 + "</Id>")
							.append("<Link>/ezScrum/showIssueInformation.do?issueID=" + 1 + "</Link>")
							.append("<Name>" + CPB.TEST_STORY_NAME + 1 + "</Name>")
							.append("<Value>" + CPB.TEST_STORY_VALUE + "</Value>")
							.append("<Importance>" + CPB.TEST_STORY_IMP + "</Importance>")
							.append("<Estimate>" + CPB.TEST_STORY_EST + "</Estimate>")
							.append("<Status>" + storyStatus + "</Status>")
							.append("<Notes>" + CPB.TEST_STORY_NOTES + 1 + "</Notes>")
							.append("<HowToDemo>" + CPB.TEST_STORY_HOW_TO_DEMO + 1 + "</HowToDemo>")
							.append("<Release>" + releaseIDAndSprintID + "</Release>")
							.append("<Sprint>" + releaseIDAndSprintID + "</Sprint>")
							.append("<Tag>" + "" + "</Tag>")
							.append("</Story>")
							.append("<Story>")
							.append("<Id>" + 2 + "</Id>")
							.append("<Link>/ezScrum/showIssueInformation.do?issueID=" + 2 + "</Link>")
							.append("<Name>" + CPB.TEST_STORY_NAME + 2 + "</Name>")
							.append("<Value>" + CPB.TEST_STORY_VALUE + "</Value>")
							.append("<Importance>" + CPB.TEST_STORY_IMP + "</Importance>")
							.append("<Estimate>" + CPB.TEST_STORY_EST + "</Estimate>")
							.append("<Status>" + storyStatus + "</Status>")
							.append("<Notes>" + CPB.TEST_STORY_NOTES + 2 + "</Notes>")
							.append("<HowToDemo>" + CPB.TEST_STORY_HOW_TO_DEMO + 2 + "</HowToDemo>")
							.append("<Release>" + releaseIDAndSprintID + "</Release>")
							.append("<Sprint>" + releaseIDAndSprintID + "</Sprint>")
							.append("<Tag>" + "" + "</Tag>")
							.append("</Story>")
							.append("</ExistingStories>");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
	
	/**
	 * test sprintID NumberFormatException exception 
	 */
	public void testShowExistedStory_3(){
		String sprintID = "SprintIDNumberFormatException";
		String releaseID = "-1";
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", sprintID);
		addRequestParameter("releaseID", releaseID);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		String expectedResponseText = "";
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
	}
	
	/**
	 * test releaseID NumberFormatException exception 
	 */
	public void testShowExistedStory_4(){
		String sprintID = "";
		String releaseID = "ReleaseNumberFormatException";
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", sprintID);
		addRequestParameter("releaseID", releaseID);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		String expectedResponseText = "";
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
	}
	
	/**
	 * test releaseID NumberFormatException exception 
	 */
	public void testShowExistedStory_5(){
		String sprintID = "";
		String releaseID = "";
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", sprintID);
		addRequestParameter("releaseID", releaseID);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		String expectedResponseText = "";
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
	}
}
