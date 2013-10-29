package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.util.List;

import servletunit.struts.MockStrutsTestCase;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.jcis.resource.core.IProject;

public class AjaxGetPartnerListTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateSprint CS;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private final String ACTION_PATH = "/AjaxGetPartnerList";
	private IProject project;
	private int sprintID;
	
	public AjaxGetPartnerListTest(String testName) {
		super(testName);
	}

	protected void setUp() throws Exception {
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		this.CP = new CreateProject(1);
		this.CP.exeCreate();// 新增一測試專案
		this.project = this.CP.getProjectList().get(0);
		
		this.CS = new CreateSprint(2, this.CP);
		this.CS.exe();
		this.sprintID = 1;
		
		super.setUp();
		// ================ set action info ========================
		setContextDirectory( new File(config.getBaseDirPath()+ "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo( this.ACTION_PATH );
		
		ini = null;
	}
	
	protected void tearDoen() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(this.config.getTestDataPath());
		
		super.tearDown();
		
		ini = null;
		projectManager = null;
		this.CP = null;
	}
	
	public void testGetPartnerList() throws Exception {
		List<String> idList = this.CS.getSprintIDList();
		int storyCount = 1;
		int storyEst = 2;
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(storyCount, storyEst, sprintID, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		addStoryToSprint.exe();
		
		int taskCount = 1;
		int taskEst = 2;
		AddTaskToStory addTaskToStory = new AddTaskToStory(taskCount, taskEst, addStoryToSprint, this.CP);
		addTaskToStory.exe();
		
		CreateProductBacklog createProductBacklog = new CreateProductBacklog(storyCount, this.CP);
		createProductBacklog.exe();
		String issueID = String.valueOf(createProductBacklog.getIssueIDList().get(0));
		// ================ set request info ========================
		String projectName = this.project.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", idList.get(0));
		addRequestParameter("issueID", issueID);
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		// ================  執行 action ==============================
		actionPerform();
		// ================    assert    =============================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		
		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("<Partners><Result>success</Result></Partners>");
		
		String actualResponseText = response.getWriterBuffer().toString();
		System.out.println("result = " + actualResponseText);
		assertEquals(expectedResponseTest.toString(), actualResponseText);
	}
}
