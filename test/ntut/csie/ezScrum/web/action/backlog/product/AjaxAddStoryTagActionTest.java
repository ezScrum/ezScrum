package ntut.csie.ezScrum.web.action.backlog.product;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateTag;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import servletunit.struts.MockStrutsTestCase;

public class AjaxAddStoryTagActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateTag CT;
	private CreateProductBacklog CPB;
	private int ProjectCount = 1;
	private Configuration configuration;
	private final String ACTION_PATH = "/AjaxAddStoryTag";
	
	public AjaxAddStoryTagActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
		// 新增Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();
		
		this.CT = new CreateTag(2, this.CP);
		this.CT.exe();
		
		this.CPB = new CreateProductBacklog(2, this.CP);
		this.CPB.exe();
		
		super.setUp();
		
		// 設定讀取的struts-config檔案路徑
		setContextDirectory(new File(configuration.getBaseDirPath() + "/WebContent")); 
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(this.ACTION_PATH);
		
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
		projectManager.initialRoleBase(configuration.getTestDataPath());
		
		configuration.setTestMode(false);
		configuration.store();

		super.tearDown();
		
		ini = null;
		projectManager = null;
		this.CP = null;
		this.CT = null;
		this.CPB = null;
		configuration = null;
	}
	
	public void testAddTagToStory(){
		// ================ set request info ========================
		ArrayList<Long> storyIDList = this.CPB.getIssueIDList();
		ArrayList<IIssueTag> tags = this.CT.getTagList();
		
		request.setHeader("Referer", "?PID=" + this.CP.getProjectList().get(0).getName());
		addRequestParameter("tagId", String.valueOf(tags.get(0).getTagId()));
		addRequestParameter("storyId", String.valueOf(storyIDList.get(0)));
		String expectedStoryId = String.valueOf(this.CPB.getIssueIDList().get(0));
		String expectedStoryName = this.CPB.getIssueList().get(0).getSummary();
		String expectedStoryValue = this.CPB.getIssueList().get(0).getValue();
		String expectedStoryImportance = this.CPB.getIssueList().get(0).getImportance();
		String expectedStoryEstimation = this.CPB.getIssueList().get(0).getEstimated();
		String expectedStoryNote = this.CPB.getIssueList().get(0).getNotes();
		String expectedStoryHoewToDemo = this.CPB.getIssueList().get(0).getHowToDemo();
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", configuration.getUserSession());
		// ================ 執行 action ======================
		actionPerform();
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"success\":true,")
							.append("\"Total\":1,")
							.append("\"Stories\":[{")
							.append("\"Id\":").append(expectedStoryId).append(",")
							.append("\"Name\":\"").append(expectedStoryName).append("\",")
							.append("\"Value\":\"").append(expectedStoryValue).append("\",")			
							.append("\"Estimate\":\"").append(expectedStoryEstimation).append("\",")
							.append("\"Importance\":\"").append(expectedStoryImportance).append("\",")
							.append("\"Tag\":\"").append(tags.get(0).getTagName()).append("\",")
							.append("\"Status\":\"new\",")
							.append("\"Notes\":\"").append(expectedStoryNote).append("\",")
							.append("\"HowToDemo\":\"").append(expectedStoryHoewToDemo).append("\",")
							.append("\"Link\":\"/ezScrum/showIssueInformation.do?issueID=").append(expectedStoryId).append("\",")
							.append("\"Release\":\"None\",")
							.append("\"Sprint\":\"None\",")
							.append("\"FilterType\":\"DETAIL\",")
							.append("\"Attach\":false,")
							.append("\"AttachFileList\":[]")
							.append("}]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
