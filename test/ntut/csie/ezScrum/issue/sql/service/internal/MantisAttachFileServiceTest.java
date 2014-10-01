package ntut.csie.ezScrum.issue.sql.service.internal;

import java.util.ArrayList;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.jcis.resource.core.IProject;

public class MantisAttachFileServiceTest extends TestCase {
	private CreateProject CP;
	private int ProjectCount = 1;
	private CreateSprint CS;
	private int SprintCount = 1;
	private AddStoryToSprint ASTS;
	private int StoryCount = 1;
	
	private Configuration configuration;
	private MantisService mantisService;
	private MantisAttachFileService MAFSservice;
	private IProject project;

	public MantisAttachFileServiceTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();

		// 新增Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();

		// 新增Sprint
		this.CS = new CreateSprint(SprintCount, CP);
		this.CS.exe();

		// 新增 Story 至 Sprint
		this.ASTS = new AddStoryToSprint(StoryCount, 3, this.CS, this.CP, "EST");
		this.ASTS.exe();
		
		project = this.CP.getProjectList().get(0);
		mantisService = new MantisService(configuration);
		MAFSservice = new MantisAttachFileService(mantisService.getControl(), configuration);
		mantisService.openConnect();

		super.setUp();
		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws Exception {
		mantisService.closeConnect();
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());

		configuration.setTestMode(false);
		configuration.store();

		// ============= release ==============
		ini = null;
		this.CP = null;
		this.CS = null;
		this.MAFSservice = null;
		projectManager = null;
		configuration = null;

		super.tearDown();
	}

	public void testAddAttachFile() throws Exception {
		AddStoryToSprint ASS = new AddStoryToSprint(1, 5, CS, CP, "EST");
		ASS.exe();

		AttachFileInfo attachFileInfo = new AttachFileInfo();
		attachFileInfo.name = "TEST.txt";
		attachFileInfo.path = "/abc/def/TEST.txt";
		attachFileInfo.issueId = ASS.getIssueList().get(0).getIssueID();
		attachFileInfo.issueType = AttachFileObject.TYPE_STORY;
		
		long id = MAFSservice.addAttachFile(attachFileInfo);
		AttachFileObject attachFile = MAFSservice.getAttachFile(id);
		
		assertEquals(attachFile.getName(), attachFileInfo.name);
		assertEquals(attachFile.getPath(), attachFileInfo.path);
		assertEquals(attachFile.getIssueId(), attachFileInfo.issueId);
		assertEquals(attachFile.getIssueType(), attachFileInfo.issueType);
	}
	
	// 透過FileName取得AttachFileObject
	public void testGetAttachFileByName() throws Exception{
		AddStoryToSprint ASS = new AddStoryToSprint(1, 5, CS, CP, "EST");
		ASS.exe();

		AttachFileInfo attachFileInfo = new AttachFileInfo();
		attachFileInfo.id = 1;
		attachFileInfo.name = "TEST.txt";
		attachFileInfo.path = "/abc/def/TEST.txt";
		attachFileInfo.issueId = ASS.getIssueList().get(0).getIssueID();
		attachFileInfo.issueType = AttachFileObject.TYPE_STORY;

		// 加入AttachFile
		MAFSservice.addAttachFile(attachFileInfo);
		// getAttachFile
		AttachFileObject attachFile = MAFSservice.getAttachFile(attachFileInfo.id);

		assertEquals(attachFileInfo.id, attachFile.getId());
		assertEquals(attachFileInfo.name, attachFile.getName());
		assertEquals(attachFileInfo.path, attachFile.getPath());
		assertEquals(attachFileInfo.issueId, attachFile.getIssueId());
	}

	// 透過StoryId取得多筆AttachFile
	public void testGetAttachFileByStoryId() throws Exception{
		AddStoryToSprint ASS = new AddStoryToSprint(1, 5, CS, CP, "EST");
		ASS.exe();

		AttachFileInfo attachFileInfo1 = new AttachFileInfo();
		attachFileInfo1.id = 1;
		attachFileInfo1.name = "TEST.txt";
		attachFileInfo1.path = "/abc/def/TEST.txt";
		attachFileInfo1.issueId = ASS.getIssueList().get(0).getIssueID();
		attachFileInfo1.issueType = AttachFileObject.TYPE_STORY;

		AttachFileInfo attachFileInfo2 = new AttachFileInfo();
		attachFileInfo2.id = 2;
		attachFileInfo2.name = "TEST2.txt";
		attachFileInfo2.path = "/abc/def/TEST2.txt";
		attachFileInfo2.issueId = ASS.getIssueList().get(0).getIssueID();
		attachFileInfo2.issueType = AttachFileObject.TYPE_STORY;

		// 加入AttachFile
		MAFSservice.addAttachFile(attachFileInfo1);
		MAFSservice.addAttachFile(attachFileInfo2);

		// getAttachFile
		ArrayList<AttachFileObject> attachFileList = MAFSservice.getAttachFilesByStoryId(ASS.getIssueList().get(0).getIssueID());
		ArrayList<AttachFileInfo> attachFileArray = new ArrayList<AttachFileInfo>();
		attachFileArray.add(attachFileInfo1);
		attachFileArray.add(attachFileInfo2);

		for (int i = 0; i < attachFileList.size(); i++) {
			assertEquals(attachFileArray.get(i).id, attachFileList.get(i).getId());
			assertEquals(attachFileArray.get(i).name, attachFileList.get(i).getName());
			assertEquals(attachFileArray.get(i).path, attachFileList.get(i).getPath());
			assertEquals(attachFileArray.get(i).issueId, attachFileList.get(i).getIssueId());
		}
	}
	
	public void testGetAttachFileByTaskId() throws Exception{
		AddStoryToSprint ASS = new AddStoryToSprint(1, 5, CS, CP, "EST");
		ASS.exe();

		AddTaskToStory addTaskToStory = new AddTaskToStory(1, 1, ASS, CP);
		addTaskToStory.exe();

		AttachFileInfo attachFileInfo1 = new AttachFileInfo();
		attachFileInfo1.id = 1;
		attachFileInfo1.name = "TEST.txt";
		attachFileInfo1.path = "/abc/def/TEST.txt";
		attachFileInfo1.issueId = addTaskToStory.getTaskIDList().get(0);
		attachFileInfo1.issueType = AttachFileObject.TYPE_TASK;

		AttachFileInfo attachFileInfo2 = new AttachFileInfo();
		attachFileInfo2.id = 2;
		attachFileInfo2.name = "TEST2.txt";
		attachFileInfo2.path = "/abc/def/TEST2.txt";
		attachFileInfo2.issueId = addTaskToStory.getTaskIDList().get(0);
		attachFileInfo2.issueType = AttachFileObject.TYPE_TASK;

		// 加入AttachFile
		MAFSservice.addAttachFile(attachFileInfo1);
		MAFSservice.addAttachFile(attachFileInfo2);

		// getAttachFile
		ArrayList<AttachFileObject> attachFileList = MAFSservice.getAttachFilesByTaskId(addTaskToStory.getTaskIDList().get(0));

		ArrayList<AttachFileInfo> attachFileArray = new ArrayList<AttachFileInfo>();
		attachFileArray.add(attachFileInfo1);
		attachFileArray.add(attachFileInfo2);

		for (int i = 0; i < attachFileList.size(); i++) {
			assertEquals(attachFileArray.get(i).id, attachFileList.get(i).getId());
			assertEquals(attachFileArray.get(i).name, attachFileList.get(i).getName());
			assertEquals(attachFileArray.get(i).path, attachFileList.get(i).getPath());
			assertEquals(attachFileArray.get(i).issueId, attachFileList.get(i).getIssueId());
		}
	}

	// 從story get attach file
	public void testDeleteAttachFile_1() {
		ArrayList<AttachFileInfo> infoList = new ArrayList<AttachFileInfo>();
		ArrayList<AttachFileObject> actualAttachFileList;
		long storyId = 1;
		// 新增三筆假資料
		for (int i = 0; i < 3; i++) {
			AttachFileInfo attachFileInfo = new AttachFileInfo();
			attachFileInfo.issueId = storyId;
			attachFileInfo.name = "TEST_FILE_NAME_" + i;
			attachFileInfo.path = "./" + attachFileInfo.name;
			attachFileInfo.issueType = AttachFileObject.TYPE_STORY;
			attachFileInfo.projectName = project.getName();
			attachFileInfo.id = MAFSservice.addAttachFile(attachFileInfo);
			infoList.add(attachFileInfo);
		}

		actualAttachFileList = MAFSservice.getAttachFilesByStoryId(storyId);
		assertEquals(3, actualAttachFileList.size());
		// delete one data
		MAFSservice.deleteAttachFile(infoList.get(0).id);
		actualAttachFileList.clear();
		actualAttachFileList = MAFSservice.getAttachFilesByStoryId(storyId);
		assertEquals(2, actualAttachFileList.size());
	}

	// 從task get attach file
	public void testDeleteAttachFile_2() {
		ArrayList<AttachFileInfo> infoList = new ArrayList<AttachFileInfo>();
		ArrayList<AttachFileObject> actualAttachFileList;
		long taskId = 1;
		// 新增三筆假資料
		for (int i = 0; i < 3; i++) {
			AttachFileInfo attachFileInfo = new AttachFileInfo();
			attachFileInfo.issueId = taskId;
			attachFileInfo.name = "TEST_FILE_NAME_" + i;
			attachFileInfo.path = "./" + attachFileInfo.name;
			attachFileInfo.issueType = AttachFileObject.TYPE_TASK;
			attachFileInfo.projectName = project.getName();
			attachFileInfo.id = MAFSservice.addAttachFile(attachFileInfo);
			infoList.add(attachFileInfo);
		}

		actualAttachFileList = MAFSservice.getAttachFilesByTaskId(taskId);
		assertEquals(3, actualAttachFileList.size());
		// delete one data
		MAFSservice.deleteAttachFile(infoList.get(0).id);
		actualAttachFileList.clear();
		actualAttachFileList = MAFSservice.getAttachFilesByTaskId(taskId);
		assertEquals(2, actualAttachFileList.size());
	}
}
