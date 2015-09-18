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

public class AjaxEditStoryActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private Configuration mConfig;
	private final String mActionPath = "/ajaxEditStory";
	private IProject mProject;
	
	public AjaxEditStoryActionTest(String testName) {
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
		mCP.exeCreate(); // 新增一測試專案
		mProject = mCP.getProjectList().get(0);
		
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
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();
		
		ini = null;
		projectManager = null;
		mCP = null;
		mConfig = null;
	}
	
	public void testEditStory() throws InterruptedException{
		int storyCount = 2;
		CreateProductBacklog CPB = new CreateProductBacklog(storyCount, mCP);
		CPB.exe();
		
		// ================ set request info ========================
		/**
		 *	Q: 由於在story資料庫的紀錄方式為XML並且XML有一個欄位是記錄更改時間，
		 *	        在撰寫測試案例時，如果做新增完馬上做編輯的動作，
		 *	        由於時間太快可能導致此爛位的時間一模一樣，會讓取讀錯誤的資料。
		 *	Sol: 使用sleep確保時間有差距。
		 */
		Thread.sleep(1000);	
		
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		String expectedStoryName = "UT for Update Story for Name";
		String expectedStoryImportance = "5";
		String expectedStoryEstimation = "5";
		String expectedStoryValue = "5";
		String expectedStoryHoewToDemo = "UT for Update Story for How to Demo";
		String expectedStoryNote = "UT for Update Story for Notes";
		String storyId = String.valueOf(CPB.getStoryIds().get(0));
		addRequestParameter("issueID", storyId);
		addRequestParameter("Name", expectedStoryName);
		addRequestParameter("Importance", expectedStoryImportance);
		addRequestParameter("Estimate", expectedStoryEstimation);
		addRequestParameter("Value", expectedStoryValue);
		addRequestParameter("HowToDemo", expectedStoryHoewToDemo);
		addRequestParameter("Notes", expectedStoryNote);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"success\":true,")
							.append("\"Total\":1,")
							.append("\"Stories\":[{")
							.append("\"Id\":").append(storyId).append(",")
							.append("\"Type\":\"Story\",")
							.append("\"Name\":\"").append(expectedStoryName).append("\",")
							.append("\"Value\":").append(expectedStoryValue).append(",")			
							.append("\"Estimate\":").append(expectedStoryEstimation).append(",")
							.append("\"Importance\":").append(expectedStoryImportance).append(",")
							.append("\"Tag\":\"\",")
							.append("\"Status\":\"new\",")
							.append("\"Notes\":\"").append(expectedStoryNote).append("\",")
							.append("\"HowToDemo\":\"").append(expectedStoryHoewToDemo).append("\",")
							.append("\"Link\":\"\",")
							.append("\"Release\":\"\",")
							.append("\"Sprint\":\"None\",")
							.append("\"FilterType\":\"DETAIL\",")
							.append("\"Attach\":false,")
							.append("\"AttachFileList\":[]")
							.append("}]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
