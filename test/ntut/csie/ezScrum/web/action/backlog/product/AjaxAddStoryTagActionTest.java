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

public class AjaxAddStoryTagActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateTag mCT;
	private CreateProductBacklog mCPB;
	private Configuration mConfig;
	private int mProjectCount = 1;
	private final String mActionPath = "/AjaxAddStoryTag";
	
	public AjaxAddStoryTagActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL
		
		// 新增Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();
		
		mCT = new CreateTag(2, mCP);
		mCT.exe();
		
		mCPB = new CreateProductBacklog(2, mCP);
		mCPB.exe();
		
		super.setUp();
		
		// 設定讀取的struts-config檔案路徑
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent")); 
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);
		
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

		super.tearDown();
		
		ini = null;
		projectManager = null;
		mCP = null;
		mCT = null;
		mCPB = null;
		mConfig = null;
	}
	
	public void testAddTagToStory(){
		// ================ set request info ========================
		ArrayList<Long> storyIDList = mCPB.getIssueIDList();
		ArrayList<TagObject> tags = mCT.getTagList();
		
		request.setHeader("Referer", "?PID=" + mCP.getProjectList().get(0).getName());
		addRequestParameter("tagId", String.valueOf(tags.get(0).getId()));
		addRequestParameter("storyId", String.valueOf(storyIDList.get(0)));
		String expectedStoryId = String.valueOf(mCPB.getIssueIDList().get(0));
		String expectedStoryName = mCPB.getIssueList().get(0).getSummary();
		String expectedStoryValue = mCPB.getIssueList().get(0).getValue();
		String expectedStoryImportance = mCPB.getIssueList().get(0).getImportance();
		String expectedStoryEstimation = mCPB.getIssueList().get(0).getEstimated();
		String expectedStoryNote = mCPB.getIssueList().get(0).getNotes();
		String expectedStoryHoewToDemo = mCPB.getIssueList().get(0).getHowToDemo();
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
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
							.append("\"Tag\":\"").append(tags.get(0).getName()).append("\",")
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
