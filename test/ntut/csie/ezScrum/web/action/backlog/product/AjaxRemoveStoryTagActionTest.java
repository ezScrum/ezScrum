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
import ntut.csie.ezScrum.web.dataObject.TagObject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxRemoveStoryTagActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateTag CT;
	private CreateProductBacklog CPB;
	private int ProjectCount = 1;
	private Configuration configuration;
	private final String ACTION_PATH = "/AjaxRemoveStoryTag";
	
	public AjaxRemoveStoryTagActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
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
		projectManager.initialRoleBase(configuration.getDataPath());
		
		configuration.setTestMode(false);
		configuration.save();

		super.tearDown();
		
		ini = null;
		projectManager = null;
		this.CP = null;
		this.CT = null;
		this.CPB = null;
		configuration = null;
	}
	
	/**
	 * setup:
	 * 1. 新增一個專案
	 * 2. 新增兩個Tag
	 * 3. 新增兩個Story
	 * 4. 個別替這兩個Story新增兩個Tags
	 * 
	 * Test:
	 * 1. 移除某一個story的一個Tag
	 * 2. 驗證此Story還存在一個Tag
	 */
	public void testRemoveTagFromStory(){
		this.CT.attachTagToStory(this.CPB);
		
		// ================ set request info ========================
		ArrayList<Long> storyIDList = this.CPB.getIssueIDList();
		ArrayList<TagObject> tags = this.CT.getTagList();
		
		request.setHeader("Referer", "?PID=" + this.CP.getProjectList().get(0).getName());
		addRequestParameter("tagId", String.valueOf(tags.get(0).getId()));
		addRequestParameter("storyId", String.valueOf(storyIDList.get(0)));
		String expectedStoryName = CPB.getIssueList().get(0).getSummary();
		String expectedStoryImportance = CPB.getIssueList().get(0).getImportance();
		String expectedStoryEstimation = CPB.getIssueList().get(0).getEstimated();
		String expectedStoryValue = CPB.getIssueList().get(0).getValue();
		String expectedStoryHoewToDemo = CPB.getIssueList().get(0).getHowToDemo();
		String expectedStoryNote = CPB.getIssueList().get(0).getNotes();
		String issueID = String.valueOf(CPB.getIssueIDList().get(0));
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
							.append("\"Id\":").append(issueID).append(",")
							.append("\"Name\":\"").append(expectedStoryName).append("\",")
							.append("\"Value\":\"").append(expectedStoryValue).append("\",")	
							.append("\"Estimate\":\"").append(expectedStoryEstimation).append("\",")
							.append("\"Importance\":\"").append(expectedStoryImportance).append("\",")		
							.append("\"Tag\":\"").append(tags.get(1).getName()).append("\",")
							.append("\"Status\":\"new\",")
							.append("\"Notes\":\"").append(expectedStoryNote).append("\",")
							.append("\"HowToDemo\":\"").append(expectedStoryHoewToDemo).append("\",")
							.append("\"Link\":\"/ezScrum/showIssueInformation.do?issueID=").append(issueID).append("\",")
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
