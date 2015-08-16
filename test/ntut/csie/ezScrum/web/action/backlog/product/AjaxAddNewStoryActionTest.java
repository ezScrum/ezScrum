package ntut.csie.ezScrum.web.action.backlog.product;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateTag;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxAddNewStoryActionTest extends MockStrutsTestCase {

	private CreateProject mCP;
	private Configuration mConfig;
	private final String mActionPath = "/ajaxAddNewStory";
	private IProject mProject;
	
	public AjaxAddNewStoryActionTest(String testName) {
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
	
	/**
	 * 新增的Story
	 * 沒有加上 Tag = "" and sprintID = null
	 */
	public void testAddNewStory_1(){
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		
		String expectedStoryName = "UT for Add New Story for Name";
		String expectedStoryImportance = "0";
		String expectedStoryEstimation= "0";
		String expectedStoryValue= "0";
		String expectedSprintId= null;
		String expectedTagNames = "";
		String expectedStoryHoewToDemo = "UT for Add New Story for How to Demo";
		String expectedStoryNote = "UT for Add New Story for Notes";
		addRequestParameter("Name", expectedStoryName);
		addRequestParameter("Importance", expectedStoryImportance);
		addRequestParameter("Estimate", expectedStoryEstimation);
		addRequestParameter("Value", expectedStoryValue);
		addRequestParameter("HowToDemo", expectedStoryHoewToDemo);
		addRequestParameter("Notes", expectedStoryNote);
		addRequestParameter("SprintId", expectedSprintId);
		addRequestParameter("Tags", expectedTagNames);
		
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
							.append("\"Id\":1,")
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
							.append("\"FilterType\":\"BACKLOG\",")
							.append("\"Attach\":false,")
							.append("\"AttachFileList\":[]")
							.append("}]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
	
	/**
	 * 新增的Story
	 * 沒有加上 Tag = null and sprintID = ""
	 */
	public void testAddNewStory_2(){
		// ================ set request info ========================
		
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		String expectedStoryName = "UT for Add New Story for Name";
		String expectedStoryImportance = "0";
		String expectedStoryEstimation= "0";
		String expectedStoryValue= "0";
		String expectedSprintId= "";
		String expectedTagNames = null;
		String expectedStoryHoewToDemo = "UT for Add New Story for How to Demo";
		String expectedStoryNote = "UT for Add New Story for Notes";
		addRequestParameter("Name", expectedStoryName);
		addRequestParameter("Importance", expectedStoryImportance);
		addRequestParameter("Estimate", expectedStoryEstimation);
		addRequestParameter("Value", expectedStoryValue);
		addRequestParameter("HowToDemo", expectedStoryHoewToDemo);
		addRequestParameter("Notes", expectedStoryNote);
		addRequestParameter("SprintId", expectedSprintId);
		addRequestParameter("Tags", expectedTagNames);
		
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
							.append("\"Id\":1,")
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
							.append("\"FilterType\":\"BACKLOG\",")
							.append("\"Attach\":false,")
							.append("\"AttachFileList\":[]")
							.append("}]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
	
	/**
	 * 新增的Story加上 TagIDs and sprintID = ""
	 */
	public void testAddNewStory_3(){
		int tagCount = 2;
		CreateTag CT = new CreateTag(tagCount, mCP);
		CT.exe();
		List<TagObject> tagList = CT.getTagList();
		
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		String expectedStoryName = "UT for Add New Story for Name";
		String expectedStoryImportance = "0";
		String expectedStoryEstimation = "0";
		String expectedStoryValue = "0";
		String expectedSprintId = "";
		String expectedTagNames = tagList.get(0).getName() + "," + tagList.get(1).getName();
		String expectedStoryHoewToDemo = "UT for Add New Story for How to Demo";
		String expectedStoryNote = "UT for Add New Story for Notes";
		addRequestParameter("Name", expectedStoryName);
		addRequestParameter("Importance", expectedStoryImportance);
		addRequestParameter("Estimate", expectedStoryEstimation);
		addRequestParameter("Value", expectedStoryValue);
		addRequestParameter("HowToDemo", expectedStoryHoewToDemo);
		addRequestParameter("Notes", expectedStoryNote);
		addRequestParameter("SprintId", expectedSprintId);
		addRequestParameter("Tags", expectedTagNames);
		
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
							.append("\"Id\":1,")
							.append("\"Type\":\"Story\",")
							.append("\"Name\":\"").append(expectedStoryName).append("\",")
							.append("\"Value\":").append(expectedStoryValue).append(",")
							.append("\"Estimate\":").append(expectedStoryEstimation).append(",")
							.append("\"Importance\":").append(expectedStoryImportance).append(",")
							.append("\"Tag\":\"").append(expectedTagNames).append("\",")			
							.append("\"Status\":\"new\",")
							.append("\"Notes\":\"").append(expectedStoryNote).append("\",")
							.append("\"HowToDemo\":\"").append(expectedStoryHoewToDemo).append("\",")
							.append("\"Link\":\"\",")
							.append("\"Release\":\"\",")
							.append("\"Sprint\":\"None\",")
							.append("\"FilterType\":\"BACKLOG\",")
							.append("\"Attach\":false,")
							.append("\"AttachFileList\":[]")
							.append("}]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
	
	/**
	 * 新增的Story加上 TagIDs and sprintID = "1"
	 */
	public void testAddNewStory_4(){
		int tagCount = 2;
		CreateTag CT = new CreateTag(tagCount, mCP);
		CT.exe();
		List<TagObject> tagList = CT.getTagList();
		
		CreateSprint CS = new CreateSprint(1, mCP);
		CS.exe();
		List<Long> sprintIDList = CS.getSprintsId();
		
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		String expectedStoryName = "UT for Add New Story for Name";
		String expectedStoryImportance = "0";
		String expectedStoryEstimation = "0";
		String expectedStoryValue = "0";
		String expectedSprintId = String.valueOf(sprintIDList.get(0));
		String expectedTagNames = tagList.get(0).getName() + "," + tagList.get(1).getName();
		String expectedStoryHoewToDemo = "UT for Add New Story for How to Demo";
		String expectedStoryNote = "UT for Add New Story for Notes";
		addRequestParameter("Name", expectedStoryName);
		addRequestParameter("Importance", expectedStoryImportance);
		addRequestParameter("Estimate", expectedStoryEstimation);
		addRequestParameter("Value", expectedStoryValue);
		addRequestParameter("HowToDemo", expectedStoryHoewToDemo);
		addRequestParameter("Notes", expectedStoryNote);
		addRequestParameter("SprintId", expectedSprintId);
		addRequestParameter("Tags", expectedTagNames);
		
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
							.append("\"Id\":1,")
							.append("\"Type\":\"Story\",")
							.append("\"Name\":\"").append(expectedStoryName).append("\",")
							.append("\"Value\":").append(expectedStoryValue).append(",")		
							.append("\"Estimate\":").append(expectedStoryEstimation).append(",")
							.append("\"Importance\":").append(expectedStoryImportance).append(",")
							.append("\"Tag\":\"").append(expectedTagNames).append("\",")	
							.append("\"Status\":\"new\",")
							.append("\"Notes\":\"").append(expectedStoryNote).append("\",")
							.append("\"HowToDemo\":\"").append(expectedStoryHoewToDemo).append("\",")
							.append("\"Link\":\"\",")
							.append("\"Release\":\"\",")
							.append("\"Sprint\":").append(expectedSprintId).append(",")
							.append("\"FilterType\":\"BACKLOG\",")
							.append("\"Attach\":false,")
							.append("\"AttachFileList\":[]")
							.append("}]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
