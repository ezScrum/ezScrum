package ntut.csie.ezScrum.web.action.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.jcis.core.util.FileUtil;
import ntut.csie.jcis.resource.core.IPath;
import ntut.csie.jcis.resource.core.IProject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import servletunit.struts.MockStrutsTestCase;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedHashTreeMap;

public class GetTaskBoardStoryTaskListTest extends MockStrutsTestCase {
	
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig;
	private ProjectObject mProject;
	private Gson mGson;
	
	public GetTaskBoardStoryTaskListTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增一測試專案
		mCP = new CreateProject(1);
		mCP.exeCreate();
		mProject = mCP.getAllProjects().get(0);

		// 新增1筆 Sprint Plan
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		mGson = new Gson();
		super.setUp();

		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));	// 設定讀取的struts-config檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo("/getTaskBoardStoryTaskList");

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
	 	// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mGson = null;
		mConfig = null;
		
		super.tearDown();
	}

	/**
	 * 1個 story，沒有 task
	 * filter 條件
	 * - 第1個 sprint
	 * - 所有人 ALL
	 */
	public void testGetTaskBoardStoryTaskList_1() throws Exception {
		// Sprint 加入1個 Story
		int storyCount = 1;
		AddStoryToSprint ASTS = new AddStoryToSprint(storyCount, 1, mCS, mCP, "EST");
		ASTS.exe();
		StoryObject story = ASTS.getStories().get(0);
		long stroyId = story.getId();

		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("UserID", "ALL");
		addRequestParameter("sprintID", "1");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		String result = response.getWriterBuffer().toString();
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText
		        .append("{")
		        .append("\"Stories\":[{")
		        .append("\"Id\":\"").append(stroyId).append("\",")
		        .append("\"Name\":\"").append(story.getName()).append("\",")
		        .append("\"Value\":\"").append(story.getValue()).append("\",")
		        .append("\"Estimate\":\"").append(story.getEstimate()).append("\",")
		        .append("\"Importance\":\"").append(story.getImportance()).append("\",")
		        .append("\"Tag\":\"\",")
		        .append("\"Status\":\"").append(story.getStatusString()).append("\",")
		        .append("\"Notes\":\"").append(story.getNotes()).append("\",")
		        .append("\"HowToDemo\":\"").append(story.getHowToDemo()).append("\",")
		        .append("\"Link\":\"\",")
		        .append("\"Release\":\"\",")
		        .append("\"Sprint\":\"").append(mCS.getSprintsId().get(0)).append("\",")
		        .append("\"Attach\":false,")
		        .append("\"AttachFileList\":[],")
		        .append("\"Tasks\":[]}],")
		        .append("\"success\":true,\"Total\":1}");
		assertEquals(expectedResponseText.toString(), result);
	}

	/**
	 * 10個 story，沒有 task
	 * filter 條件
	 * - 第1個 sprint
	 * - 所有人 ALL
	 */
	public void testGetTaskBoardStoryTaskList_2() throws Exception {
		// Sprint 加入10個 Story
		int storyCount = 10;
		AddStoryToSprint ASTS = new AddStoryToSprint(storyCount, 1, mCS, mCP, "EST");
		ASTS.exe();

		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("UserID", "ALL");
		addRequestParameter("sprintID", "1");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		String result = response.getWriterBuffer().toString();
		HashMap<String, Object> resultMap = mGson.fromJson(result, HashMap.class);
		ArrayList<LinkedHashTreeMap<String, Object>> storyList = (ArrayList<LinkedHashTreeMap<String, Object>>) resultMap.get("Stories");
		Number total = (Number) resultMap.get("Total");
		ArrayList<StoryObject> expectedStories = ASTS.getStories();
		

		assertEquals(true, resultMap.get("success"));
		assertEquals(storyCount, total.intValue());
		for (int i = 0; i < storyCount; i++) {
			StoryObject story;
			story = expectedStories.get(i);
			assertEquals(String.valueOf(story.getId()), storyList.get(i).get("Id"));
			assertEquals(story.getName(), storyList.get(i).get("Name"));
			assertEquals(String.valueOf(story.getValue()), storyList.get(i).get("Value"));
			assertEquals(String.valueOf(story.getEstimate()), storyList.get(i).get("Estimate"));
			assertEquals(String.valueOf(story.getImportance()), storyList.get(i).get("Importance"));
			assertEquals("", storyList.get(i).get("Tag"));
			assertEquals(story.getStatusString(), storyList.get(i).get("Status"));
			assertEquals(story.getNotes(), storyList.get(i).get("Notes"));
			assertEquals(story.getHowToDemo(), storyList.get(i).get("HowToDemo"));
			assertEquals(String.valueOf(mCS.getSprintsId().get(0)), storyList.get(i).get("Sprint"));
			assertEquals(false, storyList.get(i).get("Attach"));
			assertEquals(story.getAttachFiles(), storyList.get(i).get("AttachFileList"));
			assertEquals(new ArrayList<LinkedHashTreeMap>(), storyList.get(i).get("Tasks"));
		}
	}

	/**
	 * 1個 story，1個 task
	 * filter 條件
	 * - 第1個 sprint
	 * - 所有人 ALL
	 */
	public void testGetTaskBoardStoryTaskList_3() throws Exception {
		int storyCount = 1;
		int taskCount = 1;
		// Sprint 加入1個 Story
		AddStoryToSprint ASTS = new AddStoryToSprint(storyCount, 1, mCS, mCP, "EST");
		ASTS.exe();
		
		StoryObject story = ASTS.getStories().get(0);
		long stroyId = story.getId();

		// 每個 Story 加入1個 task
		AddTaskToStory addTaskToStory = new AddTaskToStory(taskCount, 1, ASTS, mCP);
		addTaskToStory.exe();
		
		TaskObject task = addTaskToStory.getTasks().get(0);
		long taskId = task.getId();

		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("UserID", "ALL");
		addRequestParameter("sprintID", "");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		String result = response.getWriterBuffer().toString();
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText
		        .append("{")
		        .append("\"Stories\":[{")
		        .append("\"Id\":\"").append(stroyId).append("\",")
		        .append("\"Name\":\"").append(story.getName()).append("\",")
		        .append("\"Value\":\"").append(story.getValue()).append("\",")
		        .append("\"Estimate\":\"").append(story.getEstimate()).append("\",")
		        .append("\"Importance\":\"").append(story.getImportance()).append("\",")
		        .append("\"Tag\":\"\",")
		        .append("\"Status\":\"").append(story.getStatusString()).append("\",")
		        .append("\"Notes\":\"").append(story.getNotes()).append("\",")
		        .append("\"HowToDemo\":\"").append(story.getHowToDemo()).append("\",")
		        .append("\"Link\":\"\",")
		        .append("\"Release\":\"\",")
		        .append("\"Sprint\":\"").append(mCS.getSprintsId().get(0)).append("\",")
		        .append("\"Attach\":false,")
		        .append("\"AttachFileList\":[],")
		        .append("\"Tasks\":[{")
		        .append("\"Id\":\"").append(taskId).append("\",")
		        .append("\"Name\":\"").append(task.getName()).append("\",")
		        .append("\"Estimate\":\"").append(task.getEstimate()).append("\",")
		        .append("\"RemainHours\":\"").append(task.getRemains()).append("\",")
		        .append("\"HandlerUserName\":\"\",")
		        .append("\"Notes\":\"").append(task.getNotes()).append("\",")
		        .append("\"AttachFileList\":[],")
		        .append("\"Attach\":false,")
		        .append("\"Status\":\"").append(task.getStatusString()).append("\",")
		        .append("\"Partners\":\"\",")
		        .append("\"Link\":\"").append("\",")
		        .append("\"Actual\":\"").append(task.getActual()).append("\"")
		        .append("}]}],")
		        .append("\"success\":true,\"Total\":1}");
		assertEquals(expectedResponseText.toString(), result);
	}

	/**
	 * 5個 story，1個 task
	 * filter 條件
	 * - 第1個 sprint
	 * - TEST_ACCOUNT_ID_1
	 */
	public void testGetTaskBoardStoryTaskList_4() throws Exception {
		int storyCount = 5;
		int taskCount = 1;
		// Sprint 加入5個 Story
		AddStoryToSprint ASTS = new AddStoryToSprint(storyCount, 1, mCS, mCP, "EST");
		ASTS.exe();

		// 每個 Story 加入1個 task
		AddTaskToStory ATTS = new AddTaskToStory(taskCount, 1, ASTS, mCP);
		ATTS.exe();

		// 新建一個帳號並給於專案權限
		CreateAccount CA = new CreateAccount(1);
		CA.exe();
		
		AddUserToRole AUTR = new AddUserToRole(mCP, CA);
		AUTR.exe_ST();

		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(mProject, -1);
		// 將第一個 story 跟 task 全都拉到 done, 用 TEST_ACCOUNT_ID_1 checkout task
		ArrayList<TaskObject> tasks = ATTS.getTasks();
		ArrayList<StoryObject> stories = ASTS.getStories();
		sprintBacklogLogic.checkOutTask(tasks.get(0).getId(), tasks.get(0).getName(), CA.getAccount_ID(1), "", tasks.get(0).getNotes(), "");
		Thread.sleep(1000);
		sprintBacklogLogic.closeTask(tasks.get(0).getId(), tasks.get(0).getName(), tasks.get(0).getNotes(), 0, "");
		sprintBacklogLogic.closeStory(stories.get(0).getId(), stories.get(0).getName(), stories.get(0).getNotes(), "");
		// 將第三個 task check out，用 TEST_ACCOUNT_ID_1 checkout task
		sprintBacklogLogic.checkOutTask(tasks.get(2).getId(), tasks.get(2).getName(), CA.getAccount_ID(1), "", tasks.get(2).getNotes(), "");

		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("UserID", CA.getAccount_ID(1));
		addRequestParameter("sprintID", "1");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		String result = response.getWriterBuffer().toString();
		HashMap<String, Object> resultMap = mGson.fromJson(result, HashMap.class);
		ArrayList<LinkedHashTreeMap<String, Object>> storyList = (ArrayList<LinkedHashTreeMap<String, Object>>) resultMap.get("Stories");
		ArrayList<LinkedHashTreeMap<String, Object>> taskList;
		Number total = (Number) resultMap.get("Total");
		ArrayList<StoryObject> expectedStories = ASTS.getStories();
		ArrayList<TaskObject> expectedTasks = ATTS.getTasks();

		/**
		 * 一個 Story and task 被 TEST_ACCOUNT_ID_1 拉到 Done
		 * 一個 task 被 TEST_ACCOUNT_ID_1 拉到 Done
		 * 所以有兩個 Story 會被 Filter 出來(與其底下的 task)
		 */
		assertEquals(true, resultMap.get("success"));
		assertEquals(2, total.intValue());
		
		for (int i = 0; i < storyList.size(); i++) {
			StoryObject story;
			TaskObject task;
			story = expectedStories.get(i * 2);
			task = expectedTasks.get(i * 2);
			taskList = (ArrayList<LinkedHashTreeMap<String, Object>>) storyList.get(i).get("Tasks");
			assertEquals(String.valueOf(story.getId()), storyList.get(i).get("Id"));
			assertEquals(story.getName(), storyList.get(i).get("Name"));
			assertEquals(String.valueOf(story.getValue()), storyList.get(i).get("Value"));
			assertEquals(String.valueOf(story.getEstimate()), storyList.get(i).get("Estimate"));
			assertEquals(String.valueOf(story.getImportance()), storyList.get(i).get("Importance"));
			assertEquals("", storyList.get(i).get("Tag"));
			if (i == 0) {
				assertEquals(ITSEnum.CLOSED, storyList.get(i).get("Status"));
			} else {
				assertEquals(ITSEnum.S_NEW_STATUS, storyList.get(i).get("Status"));
			}
			assertEquals(story.getNotes(), storyList.get(i).get("Notes"));
			assertEquals(story.getHowToDemo(), storyList.get(i).get("HowToDemo"));
			assertEquals(String.valueOf(mCS.getSprintsId().get(0)), storyList.get(i).get("Sprint"));
			assertEquals(false, storyList.get(i).get("Attach"));
			assertEquals(story.getAttachFiles(), storyList.get(i).get("AttachFileList"));
			for (int j = 0; j < taskList.size(); j++) {
				assertEquals(String.valueOf(task.getId()), taskList.get(j).get("Id"));
				assertEquals(task.getName(), taskList.get(j).get("Name"));
				assertEquals(String.valueOf(task.getEstimate()), taskList.get(j).get("Estimate"));
				assertEquals(CA.getAccount_ID(1), taskList.get(j).get("HandlerUserName"));
				assertEquals(task.getNotes(), taskList.get(j).get("Notes"));
				assertEquals(task.getAttachFiles(), taskList.get(j).get("AttachFileList"));
				assertEquals(false, taskList.get(j).get("Attach"));
				if (i == 0) {
					assertEquals(ITSEnum.CLOSED, taskList.get(j).get("Status"));
					assertEquals("0", taskList.get(j).get("RemainHours"));
				} else {
					assertEquals(ITSEnum.ASSIGNED, taskList.get(j).get("Status"));
					assertEquals(String.valueOf(task.getRemains()), taskList.get(j).get("RemainHours"));
				}
				assertEquals(task.getPartnersUsername(), taskList.get(j).get("Partners"));
				assertEquals("", taskList.get(j).get("Link"));
				assertEquals(String.valueOf(task.getActual()), taskList.get(j).get("Actual"));
			}
		}
	}
	
	/**
	 * 1個 story attach 1個 file
	 * filter 條件
	 * - 第1個 sprint
	 * - TEST_ACCOUNT_ID_1
	 */
	public void testGetTaskBoardStoryTaskList_AttachFile_Story() throws Exception {
		// Sprint 加入1個 Story
		int storyCount = 1;
		AddStoryToSprint ASTS = new AddStoryToSprint(storyCount, 1, mCS, mCP, "EST");
		ASTS.exe();

		StoryObject story = ASTS.getStories().get(0);
		long storyId = story.getId();

		// attach file
		final String FILE_NAME = "ezScrumTestFile";
		IProject iProject = mCP.getProjectList().get(0);
		IPath fullPath = iProject.getFullPath();
		String targetPath = fullPath.getPathString() + File.separator + FILE_NAME;
		File file = new File(targetPath);
        BufferedWriter output = new BufferedWriter(new FileWriter(file));
        output.write("hello i am test");
        output.close();
        
        AttachFileInfo attachFileInfo = new AttachFileInfo();
        attachFileInfo.issueId = storyId;
        attachFileInfo.issueType = IssueTypeEnum.TYPE_STORY;
        attachFileInfo.name = FILE_NAME;
        attachFileInfo.contentType = "text/plain";
        attachFileInfo.projectName = mCP.getProjectList().get(0).getName();
        
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(mProject);
		long attachFileId = productBacklogHelper.addAttachFile(attachFileInfo, file);
		StoryObject expectedStory = productBacklogHelper.getStory(storyId);

		try {
			FileUtil.delete(targetPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("UserID", "ALL");
		addRequestParameter("sprintID", "1");

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		String result = response.getWriterBuffer().toString();
		LinkedHashMap<String, Object> resultMap = mGson.fromJson(result, LinkedHashMap.class);
		ArrayList<LinkedHashTreeMap<String, Object>> storyList = (ArrayList<LinkedHashTreeMap<String, Object>>) resultMap.get("Stories");
		assertEquals(String.valueOf(expectedStory.getId()), storyList.get(0).get("Id"));
		assertEquals(expectedStory.getName(), storyList.get(0).get("Name"));
		assertEquals(String.valueOf(expectedStory.getValue()), storyList.get(0).get("Value"));
		assertEquals(String.valueOf(expectedStory.getEstimate()), storyList.get(0).get("Estimate"));
		assertEquals(String.valueOf(expectedStory.getImportance()), storyList.get(0).get("Importance"));
		assertEquals("", storyList.get(0).get("Tag"));
		assertEquals(expectedStory.getStatusString(), storyList.get(0).get("Status"));
		assertEquals(expectedStory.getNotes(), storyList.get(0).get("Notes"));
		assertEquals(expectedStory.getHowToDemo(), storyList.get(0).get("HowToDemo"));
		assertEquals(String.valueOf(mCS.getSprintsId().get(0)), storyList.get(0).get("Sprint"));
		assertEquals(!expectedStory.getAttachFiles().isEmpty(), storyList.get(0).get("Attach"));
		LinkedHashTreeMap attachFile = ((List<LinkedHashTreeMap>) storyList.get(0).get("AttachFileList")).get(0);
		assertEquals(expectedStory.getAttachFiles().get(0).getId(), ((Double) attachFile.get("FileId")).longValue());
		assertEquals(expectedStory.getAttachFiles().get(0).getName(), attachFile.get("FileName"));
	}
	
	/**
	 * 1個task attach 1個file
	 * filter條件
	 * - 第1個sprint
	 * - TEST_ACCOUNT_ID_1
	 */
	public void testGetTaskBoardStoryTaskList_AttachFile_Task() throws Exception {
		// Sprint 加入1個 Story
		int storyCount = 1;
		int taskCount = 1;
		
		AddStoryToSprint ASTS = new AddStoryToSprint(storyCount, 1, mCS, mCP, "EST");
		ASTS.exe();
		StoryObject story = ASTS.getStories().get(0);
		long stroyId = story.getId();
		
		// 每個 Story 加入1個 task
		AddTaskToStory ATTS = new AddTaskToStory(taskCount, 1, ASTS, mCP);
		ATTS.exe();
		
		TaskObject task = ATTS.getTasks().get(0);
		long taskId = task.getId();
		
		// attach file
		final String FILE_NAME = "ezScrumTestFile";
		IProject iProject = mCP.getProjectList().get(0);
		IPath fullPath = iProject.getFullPath();
		String targetPath = fullPath.getPathString() + File.separator + "ezScrumTestFile";
		File file = new File(targetPath);
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		output.write("hello i am test");
		output.close();
		
		AttachFileInfo attachFileInfo = new AttachFileInfo();
        attachFileInfo.issueId = taskId;
        attachFileInfo.issueType = IssueTypeEnum.TYPE_TASK;
        attachFileInfo.name = FILE_NAME;
        attachFileInfo.contentType = "text/plain";
        attachFileInfo.projectName = mCP.getProjectList().get(0).getName();
		
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(mProject);
		productBacklogHelper.addAttachFile(attachFileInfo, file);
		
		StoryObject expectedStory = productBacklogHelper.getStory(stroyId);
		task.reload();
		
		try {
			FileUtil.delete(targetPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?PID=" + projectName);
		addRequestParameter("UserID", "ALL");
		addRequestParameter("sprintID", "1");
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ======================
		verifyNoActionErrors();
		verifyNoActionMessages();
		String result = response.getWriterBuffer().toString();
		System.out.println(result);
		
		JSONObject json = new JSONObject(result);
		
		JSONArray stories = json.getJSONArray("Stories");
		assertEquals(1, stories.length());
		
		JSONObject actualStory = stories.getJSONObject(0);
		assertEquals(expectedStory.getName(), actualStory.getString("Name"));
		assertEquals(expectedStory.getId() + "", actualStory.getString("Id"));
		assertEquals(String.valueOf(expectedStory.getValue()), actualStory.getString("Value"));
		assertEquals(String.valueOf(expectedStory.getEstimate()), actualStory.getString("Estimate"));
		assertEquals(String.valueOf(expectedStory.getImportance()), actualStory.getString("Importance"));
		assertEquals(expectedStory.getStatusString(), actualStory.getString("Status"));
		assertEquals(expectedStory.getNotes(), actualStory.getString("Notes"));
		assertEquals(expectedStory.getHowToDemo(), actualStory.getString("HowToDemo"));
		
		JSONArray actualAttachFiles = actualStory.getJSONArray("AttachFileList");
		assertEquals(0, actualAttachFiles.length());
		
		JSONArray actualTasks = actualStory.getJSONArray("Tasks");
		assertEquals(1, actualTasks.length());
		
		JSONObject actualTask = actualTasks.getJSONObject(0);
		assertEquals(task.getId(), actualTask.getLong("Id"));
		assertEquals(task.getName(), actualTask.getString("Name"));
		assertEquals(task.getEstimate(), actualTask.getInt("Estimate"));
		assertEquals(task.getRemains(), actualTask.getInt("RemainHours"));
		assertEquals(task.getActual(), actualTask.getInt("Actual"));
		assertEquals(task.getNotes(), actualTask.getString("Notes"));
		assertEquals(task.getStatusString(), actualTask.getString("Status"));
	}
}
