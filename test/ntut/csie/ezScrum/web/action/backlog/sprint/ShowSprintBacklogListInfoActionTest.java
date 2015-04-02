package ntut.csie.ezScrum.web.action.backlog.sprint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.TestTool;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import servletunit.struts.MockStrutsTestCase;

public class ShowSprintBacklogListInfoActionTest extends MockStrutsTestCase{
	
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private final String mACTION_PATH = "/showSprintBacklogTreeListInfo";
	private ProjectObject mProject;
	
	public ShowSprintBacklogListInfoActionTest(String testName) {
		super(testName);
	}
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 新增一測試專案
		mCP = new CreateProject(1);
		mCP.exeCreate();
		mProject = mCP.getAllProjects().get(0);
		
		mCS = new CreateSprint(2, mCP);
		mCS.exe();
		
		super.setUp();
		
		// ================ set action info ========================
		setContextDirectory( new File(mConfig.getBaseDirPath()+ "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo( mACTION_PATH );
		
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();
		
		ini = null;
		projectManager = null;
		mCP = null;
		mConfig = null;
	}
	
	/**
	 * 沒有stories and tasks
	 */
	public void testShowSprintBacklogListInfo_1(){
		List<String> idList = mCS.getSprintIdList();
		
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", idList.get(0));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		String expectedResponseText = "[]";
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
		
	}
	
	/**
	 * sprint有stories and tasks
	 * @throws Exception 
	 */
	public void testShowSprintBacklogListInfo_2() throws Exception{
		List<String> idList = mCS.getSprintIdList();
		int sprintID = Integer.parseInt(idList.get(0));
		int storyCount = 1;
		int storyEst = 5;
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(storyCount, storyEst, sprintID, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		addStoryToSprint.exe();
		
		int taskCount = 1;
		int taskEstValue = 2;
		AddTaskToStory addTaskToStory = new AddTaskToStory(taskCount, taskEstValue, addStoryToSprint, mCP);
		addTaskToStory.exe();
		
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", idList.get(0));
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		String actualResponseText = response.getWriterBuffer().toString();
//		System.out.println("actualResponseText = " + actualResponseText);
		//	story
		String storyType = "\"Type\":\"Story\"";
		String storyName = "\"Name\":\"TEST_STORY_1\"";
		String storyImportance = "\"Importance\":\"100\"";
		String storyValue = "\"Value\":\"50\"";
		String storyEstimatation = "\"Estimate\":\"5\"";
		String storyNotes = "\"Notes\":\"TEST_STORY_NOTE_1\"";
		assertTrue(actualResponseText.contains(storyType));
		assertTrue(actualResponseText.contains(storyName));
		assertTrue(actualResponseText.contains(storyImportance));
		assertTrue(actualResponseText.contains(storyValue));
		assertTrue(actualResponseText.contains(storyEstimatation));
		assertTrue(actualResponseText.contains(storyNotes));
		
		//	task
		String taskType = "\"Type\":\"Task\"";
		String taskName = "\"Name\":\"TEST_TASK_1\"";
		String taskValue = "\"Value\":\"\"";
		String taskEstimatation = "\"Estimate\":\"2\"";
		String taskNotes = "\"Notes\":\"TEST_TASK_NOTES_1\"";
		assertTrue(actualResponseText.contains(taskType));
		assertTrue(actualResponseText.contains(taskName));
		assertTrue(actualResponseText.contains(taskValue));
		assertTrue(actualResponseText.contains(taskEstimatation));
		assertTrue(actualResponseText.contains(taskNotes));
	}
	
	public void testShowSprintBacklogListInfo_3() throws Exception {
		List<String> idList = mCS.getSprintIdList();
		int sprintID = Integer.parseInt(idList.get(0));
		int storyCount = 1;
		int storyEst = 2;
		AddStoryToSprint addStoryToSprint = new AddStoryToSprint(storyCount, storyEst, sprintID, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		addStoryToSprint.exe();
		
		int taskCount = 1;
		int taskEst = 2;
		AddTaskToStory addTaskToStory = new AddTaskToStory(taskCount, taskEst, addStoryToSprint, mCP);
		addTaskToStory.exe();
		
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("sprintID", idList.get(0));
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		// ================  執行 action ==============================
		actionPerform();
		// ================    assert    =============================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		//  Story
		String issueType = "Story";
		long storyId = addStoryToSprint.getStories().get(0).getId();
		String storyName = addStoryToSprint.getStories().get(0).getName();
		int storyValue = addStoryToSprint.getStories().get(0).getValue();		
		int storyEstimate = addStoryToSprint.getStories().get(0).getEstimate();
		int storyImportance = addStoryToSprint.getStories().get(0).getImportance();
		int storyStatus = addStoryToSprint.getStories().get(0).getStatus();
		String storyNotes = addStoryToSprint.getStories().get(0).getNotes();
		String storyLink = "\"/ezScrum/showIssueInformation.do?issueID\\u003d1\"";
		
		// 取得 Story 日期
		List<String> storyDate = new ArrayList<String>();
		Date currentDate = new Date();
		TestTool getStoryDate = new TestTool();
		storyDate = getStoryDate.getDateList(currentDate, 10);
		
		// Task
		String taskType = "Task";
		long taskId = addTaskToStory.getTasks().get(0).getId();
		String taskName = addTaskToStory.getTasks().get(0).getName();
		int taskEstimate = addTaskToStory.getTasks().get(0).getEstimate();
		String taskStatus = addTaskToStory.getTasks().get(0).getStatusString();
		String taskNotes = addTaskToStory.getTasks().get(0).getNotes();

		// 取得 Task 日期
		List<String> TaskDate = new ArrayList<String>();
		TaskDate = getStoryDate.getDateList(currentDate, 10);
		
		StringBuilder expectedResponseTest = new StringBuilder();
		expectedResponseTest.append("[{\"Type\":\"" + issueType + "\"")
							.append(",\"ID\":\"" + storyId + "\"")
							.append(",\"Tag\":\"\"")
							.append(",\"Name\":\"" + storyName + "\"")
							.append(",\"Handler\":\" \"")
							.append(",\"Value\":\"" + storyValue + "\"")
							.append(",\"Estimate\":\"" + storyEstimate + "\"")
							.append(",\"Importance\":\"" + storyImportance + "\"")
							.append(",\"Status\":\"" + storyStatus + "\"")
							.append(",\"Notes\":\"" + storyNotes + "\"")
							.append(",\"Link\":" + storyLink)
							.append(",\"SprintID\":\"" + idList.get(0) + "\"")
							.append(",\"ReleaseID\":\"\"")
							.append(",\"dateList\":[");
		for(int i = 0; i < storyDate.size(); i++){
			if(i != storyDate.size() - 1) {
				expectedResponseTest.append("\"" + storyDate.get(i) + " 12:00:00 AM\",");
			}else {
				expectedResponseTest.append("\"" + storyDate.get(i) + " 12:00:00 AM\"]");
			}
		}
		expectedResponseTest.append(",\"leaf\":false")
							.append(",\"expanded\":false")
							.append(",\"id\":\"Story:1\"")
							.append(",\"cls\":\"folder\"")
							.append(",\"children\":[{\"Type\":\"" + taskType + "\"")
							.append(",\"ID\":\"" + taskId + "\"")
							.append(",\"Tag\":\"\"")
							.append(",\"Name\":\"" + taskName + "\"")
							.append(",\"Handler\":\"\"")
							.append(",\"Value\":\"\"")
							.append(",\"Estimate\":\"" + taskEstimate + "\"")
							.append(",\"Importance\":\"\"")
							.append(",\"Status\":\"" + taskStatus + "\"")
							.append(",\"Notes\":\"" + taskNotes + "\"")
							.append(",\"SprintID\":\"\"")
							.append(",\"ReleaseID\":\"\"")
							.append(",\"dateList\":[");
		for(int i = 0; i < TaskDate.size(); i++){
			if(i != TaskDate.size() - 1) {
				expectedResponseTest.append("\"" + TaskDate.get(i) + " 12:00:00 AM\",");
			}else {
				expectedResponseTest.append("\"" + TaskDate.get(i) + " 12:00:00 AM\"]");
			}
		}
		expectedResponseTest.append(",\"Date_9\":\"\"")
							.append(",\"Date_10\":\"\"")
							.append(",\"Date_1\":\"2.0\"")
							.append(",\"Date_2\":\"\"")
							.append(",\"Date_3\":\"\"")
							.append(",\"Date_4\":\"\"")
							.append(",\"Date_5\":\"\"")
							.append(",\"Date_6\":\"\"")
							.append(",\"Date_7\":\"\"")
							.append(",\"Date_8\":\"\"")
							.append(",\"leaf\":true")
							.append(",\"expanded\":false")
							.append(",\"id\":\"Task:1\"")
							.append(",\"cls\":\"file\"}]}]");
		
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseTest.toString(), actualResponseText);
	}
}
