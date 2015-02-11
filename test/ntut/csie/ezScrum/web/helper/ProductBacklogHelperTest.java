package ntut.csie.ezScrum.web.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.jcis.resource.core.IProject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProductBacklogHelperTest{
	private CreateProject mCP;
	private CreateProductBacklog mCreateProductBacklog = null;
	private ProductBacklogHelper mProductBacklogHelper = null;
	private Configuration mConfig = null;
	private IProject mProject;
	private int mProjectCount = 1;
	private int mStoryCount = 1;
	
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
		
		// 新增Story	
		mCreateProductBacklog = new CreateProductBacklog(mStoryCount, mCP);
		mCreateProductBacklog.exe();
		
		// 建立 productbacklog 物件
		mProject = mCP.getProjectList().get(0);
		mProductBacklogHelper = new ProductBacklogHelper(mConfig.getUserSession(), mProject);
		
		// 為了使 Story 建立時間與修改時間分開而停下
		Thread.sleep(1000);
		// ============= release ==============
		ini = null;
		mProject = null;
	}
	
	@After
	public void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
    	
    	// ============= release ==============
    	ini = null;
    	mCP = null;
    	
    	mCreateProductBacklog = null;
    	mProductBacklogHelper = null;
    	projectManager = null;
    	mConfig = null;
    	mProject = null;
	}
	
	@Test
	public void testAddAttachFile() {
		AttachFileInfo attachFileInfo = new AttachFileInfo();
		attachFileInfo.name = "initial_bk.sql";
		attachFileInfo.issueId = mCreateProductBacklog.getIssueList().get(0).getIssueID();
		attachFileInfo.issueType = AttachFileObject.TYPE_STORY;
		attachFileInfo.projectName = mCP.getProjectList().get(0).getName();
		
		File sqlFile = new File(mConfig.getInitialSQLPath());
		
		try {
			long id = mProductBacklogHelper.addAttachFile(attachFileInfo, sqlFile);
			AttachFileObject attachFile = mProductBacklogHelper.getAttachFile(id);
			File actualFile = new File(attachFile.getPath());
			assertEquals(sqlFile.length(), actualFile.length());
			assertEquals(attachFileInfo.name, attachFile.getName());
			assertEquals(attachFileInfo.issueType, attachFile.getIssueType());
		} catch (IOException e) {
			System.out.println(e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testDeleteAttachFile() {
		AttachFileInfo attachFileInfo = new AttachFileInfo();
		attachFileInfo.name = "initial_bk.sql";
		attachFileInfo.issueId = mCreateProductBacklog.getIssueList().get(0).getIssueID();
		attachFileInfo.issueType = AttachFileObject.TYPE_STORY;
		attachFileInfo.projectName = mCP.getProjectList().get(0).getName();
		
		File sqlFile = new File(mConfig.getInitialSQLPath());
		
		try {
			long id = mProductBacklogHelper.addAttachFile(attachFileInfo, sqlFile);
			AttachFileObject attachFile = mProductBacklogHelper.getAttachFile(id);
			File actualFile = new File(attachFile.getPath());
			assertEquals(sqlFile.length(), actualFile.length());
			assertEquals(attachFileInfo.name, attachFile.getName());
			assertEquals(attachFileInfo.issueType, attachFile.getIssueType());
			
			mProductBacklogHelper.deleteAttachFile(attachFile.getId());
			
			try {
				mProductBacklogHelper.getAttachFile(id);
				assertTrue(false);
			} catch (Exception e) {
				assertTrue(true);
				
				File deletedFile = new File(attachFile.getPath());
				assertEquals(false, deletedFile.exists());
			}
		} catch (IOException e) {
			System.out.println(e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testEditStoryHistory() throws SQLException {
		long issueId = mCreateProductBacklog.getIssueIDList().get(0);
		String name = "快接 task 啦";
		String value = "6";
		String importance = "6";
		String estimate = "6";
		String howToDemo = "QAQ";
		String notes = "已哭";
		
		IIssue issue = mProductBacklogHelper.editStory(issueId, name, value, importance, estimate, howToDemo, notes, true);
		// assert issue info
		assertEquals(issueId, issue.getIssueID());
		assertEquals(name, issue.getSummary());
		assertEquals(value, issue.getValue());
		assertEquals(importance, issue.getImportance());
		assertEquals(estimate, issue.getEstimated());
		assertEquals(howToDemo, issue.getHowToDemo());
		assertEquals(notes, issue.getNotes());
		// assert issue history
		ArrayList<HistoryObject> histories = issue.getHistories();
		assertEquals(7, histories.size());
	}
}
