package ntut.csie.ezScrum.issue.sql.service.internal;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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

public class MantisAttachFileServiceTest {
	private int mProjectCount = 1;
	private int mSprintCount = 1;
	private int mStoryCount = 1;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private Configuration mConfig;
	private MantisService mMantisService;
	private MantisAttachFileService mMantisAttachFileService;
	private IProject mProject;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		// 新增Sprint
		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();

		// 新增 Story 至 Sprint
		mASTS = new AddStoryToSprint(mStoryCount, 3, mCS, mCP, "EST");
		mASTS.exe();
		
		mProject = mCP.getProjectList().get(0);
		mMantisService = new MantisService(mConfig);
		mMantisAttachFileService = new MantisAttachFileService(mMantisService.getControl(), mConfig);
		mMantisService.openConnect();
	}

	@After
	public void tearDown() throws Exception {
		mMantisService.closeConnect();
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		mConfig.setTestMode(false);
		mConfig.save();

		// release resource
		mCP = null;
		mCS = null;
		mASTS = null;
		mConfig = null;
		mMantisService = null;
		mMantisAttachFileService = null;
		mProject = null;
	}

	@Test
	public void testAddAttachFile() throws Exception{
		AddStoryToSprint ASTS = new AddStoryToSprint(1, 5, mCS, mCP, "EST");
		ASTS.exe();
		
		AttachFileInfo attachFileInfo = new AttachFileInfo();
		attachFileInfo.name = "TEST.txt";
		attachFileInfo.path = "/abc/def/TEST.txt";
		attachFileInfo.issueId = ASTS.getStories().get(0).getIssueID();
		attachFileInfo.issueType = AttachFileObject.TYPE_STORY;
		
		long id = mMantisAttachFileService.addAttachFile(attachFileInfo);
		assertEquals(1, id);
		AttachFileObject attachFile = mMantisAttachFileService.getAttachFile(id);
		
		assertEquals(attachFile.getName(), attachFileInfo.name);
		assertEquals(attachFile.getPath(), attachFileInfo.path);
		assertEquals(attachFile.getIssueId(), attachFileInfo.issueId);
		assertEquals(attachFile.getIssueType(), attachFileInfo.issueType);
	}
	
	@Test
	// 透過FileName取得AttachFileObject
	public void testGetAttachFileByName() throws Exception{
		AddStoryToSprint ASTS = new AddStoryToSprint(1, 5, mCS, mCP, "EST");
		ASTS.exe();

		AttachFileInfo attachFileInfo = new AttachFileInfo();
		attachFileInfo.name = "TEST.txt";
		attachFileInfo.path = "/abc/def/TEST.txt";
		attachFileInfo.issueId = ASTS.getStories().get(0).getIssueID();
		attachFileInfo.issueType = AttachFileObject.TYPE_STORY;

		// 加入AttachFile
		long fileId = mMantisAttachFileService.addAttachFile(attachFileInfo);
		// getAttachFile
		AttachFileObject attachFile = mMantisAttachFileService.getAttachFile(fileId);

		assertEquals(fileId, attachFile.getId());
		assertEquals(attachFileInfo.name, attachFile.getName());
		assertEquals(attachFileInfo.path, attachFile.getPath());
		assertEquals(attachFileInfo.issueId, attachFile.getIssueId());
	}

	@Test
	// 透過StoryId取得多筆AttachFile
	public void testGetAttachFileByStoryId() throws Exception{
		AddStoryToSprint ASTS = new AddStoryToSprint(1, 5, mCS, mCP, "EST");
		ASTS.exe();
		ArrayList<Long> fileIdList = new ArrayList<Long>();

		AttachFileInfo attachFileInfo1 = new AttachFileInfo();
		attachFileInfo1.name = "TEST.txt";
		attachFileInfo1.path = "/abc/def/TEST.txt";
		attachFileInfo1.issueId = ASTS.getStories().get(0).getIssueID();
		attachFileInfo1.issueType = AttachFileObject.TYPE_STORY;

		AttachFileInfo attachFileInfo2 = new AttachFileInfo();
		attachFileInfo2.name = "TEST2.txt";
		attachFileInfo2.path = "/abc/def/TEST2.txt";
		attachFileInfo2.issueId = ASTS.getStories().get(0).getIssueID();
		attachFileInfo2.issueType = AttachFileObject.TYPE_STORY;

		// 加入AttachFile
		fileIdList.add(mMantisAttachFileService.addAttachFile(attachFileInfo1));
		fileIdList.add(mMantisAttachFileService.addAttachFile(attachFileInfo2));

		// getAttachFile
		ArrayList<AttachFileObject> attachFileList = mMantisAttachFileService.getAttachFilesByStoryId(ASTS.getStories().get(0).getIssueID());
		ArrayList<AttachFileInfo> attachFileArray = new ArrayList<AttachFileInfo>();
		attachFileArray.add(attachFileInfo1);
		attachFileArray.add(attachFileInfo2);

		for (int i = 0; i < attachFileList.size(); i++) {
			assertEquals((long)fileIdList.get(i), attachFileList.get(i).getId());
			assertEquals(attachFileArray.get(i).name, attachFileList.get(i).getName());
			assertEquals(attachFileArray.get(i).path, attachFileList.get(i).getPath());
			assertEquals(attachFileArray.get(i).issueId, attachFileList.get(i).getIssueId());
		}
	}
	
	@Test
	public void testGetAttachFileByTaskId() throws Exception{
		AddStoryToSprint ASTS = new AddStoryToSprint(1, 5, mCS, mCP, "EST");
		ASTS.exe();
		AddTaskToStory ATTS = new AddTaskToStory(1, 1, ASTS, mCP);
		ATTS.exe();
		ArrayList<Long> fileIdList = new ArrayList<Long>();

		AttachFileInfo attachFileInfo1 = new AttachFileInfo();
		attachFileInfo1.name = "TEST.txt";
		attachFileInfo1.path = "/abc/def/TEST.txt";
		attachFileInfo1.issueId = ATTS.getTasksId().get(0);
		attachFileInfo1.issueType = AttachFileObject.TYPE_TASK;

		AttachFileInfo attachFileInfo2 = new AttachFileInfo();
		attachFileInfo2.name = "TEST2.txt";
		attachFileInfo2.path = "/abc/def/TEST2.txt";
		attachFileInfo2.issueId = ATTS.getTasksId().get(0);
		attachFileInfo2.issueType = AttachFileObject.TYPE_TASK;

		// 加入AttachFile
		fileIdList.add(mMantisAttachFileService.addAttachFile(attachFileInfo1));
		fileIdList.add(mMantisAttachFileService.addAttachFile(attachFileInfo2));

		// getAttachFile
		ArrayList<AttachFileObject> attachFileList = mMantisAttachFileService.getAttachFilesByTaskId(ATTS.getTasksId().get(0));
		ArrayList<AttachFileInfo> attachFileArray = new ArrayList<AttachFileInfo>();
		attachFileArray.add(attachFileInfo1);
		attachFileArray.add(attachFileInfo2);

		for (int i = 0; i < attachFileList.size(); i++) {
			assertEquals((long)fileIdList.get(i), attachFileList.get(i).getId());
			assertEquals(attachFileArray.get(i).name, attachFileList.get(i).getName());
			assertEquals(attachFileArray.get(i).path, attachFileList.get(i).getPath());
			assertEquals(attachFileArray.get(i).issueId, attachFileList.get(i).getIssueId());
		}
	}

	@Test
	// 從story get attach file
	public void testDeleteAttachFile_1() {
		ArrayList<AttachFileInfo> infoList = new ArrayList<AttachFileInfo>();
		ArrayList<AttachFileObject> actualAttachFileList;
		ArrayList<Long> fileIdList = new ArrayList<Long>();
		long storyId = 1;
		// 新增三筆假資料
		for (int i = 0; i < 3; i++) {
			AttachFileInfo attachFileInfo = new AttachFileInfo();
			attachFileInfo.issueId = storyId;
			attachFileInfo.name = "TEST_FILE_NAME_" + i;
			attachFileInfo.path = "./" + attachFileInfo.name;
			attachFileInfo.issueType = AttachFileObject.TYPE_STORY;
			attachFileInfo.projectName = mProject.getName();
			fileIdList.add(mMantisAttachFileService.addAttachFile(attachFileInfo));
			infoList.add(attachFileInfo);
		}

		actualAttachFileList = mMantisAttachFileService.getAttachFilesByStoryId(storyId);
		assertEquals(3, actualAttachFileList.size());
		// delete one data
		mMantisAttachFileService.deleteAttachFile(fileIdList.get(0));
		actualAttachFileList.clear();
		actualAttachFileList = mMantisAttachFileService.getAttachFilesByStoryId(storyId);
		assertEquals(2, actualAttachFileList.size());
	}

	@Test
	// 從task get attach file
	public void testDeleteAttachFile_2() {
		ArrayList<AttachFileInfo> infoList = new ArrayList<AttachFileInfo>();
		ArrayList<AttachFileObject> actualAttachFileList;
		ArrayList<Long> fileIdList = new ArrayList<Long>();
		long taskId = 1;
		// 新增三筆假資料
		for (int i = 0; i < 3; i++) {
			AttachFileInfo attachFileInfo = new AttachFileInfo();
			attachFileInfo.issueId = taskId;
			attachFileInfo.name = "TEST_FILE_NAME_" + i;
			attachFileInfo.path = "./" + attachFileInfo.name;
			attachFileInfo.issueType = AttachFileObject.TYPE_TASK;
			attachFileInfo.projectName = mProject.getName();
			fileIdList.add(mMantisAttachFileService.addAttachFile(attachFileInfo));
			infoList.add(attachFileInfo);
		}

		actualAttachFileList = mMantisAttachFileService.getAttachFilesByTaskId(taskId);
		assertEquals(3, actualAttachFileList.size());
		// delete one data
		mMantisAttachFileService.deleteAttachFile(fileIdList.get(0));
		actualAttachFileList.clear();
		actualAttachFileList = mMantisAttachFileService.getAttachFilesByTaskId(taskId);
		assertEquals(2, actualAttachFileList.size());
	}
}
